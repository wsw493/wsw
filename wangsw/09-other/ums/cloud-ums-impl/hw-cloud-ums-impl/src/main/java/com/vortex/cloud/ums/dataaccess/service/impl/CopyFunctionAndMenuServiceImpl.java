package com.vortex.cloud.ums.dataaccess.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vortex.cloud.ums.dataaccess.dao.ICloudFunctionDao;
import com.vortex.cloud.ums.dataaccess.dao.ICloudFunctionGroupDao;
import com.vortex.cloud.ums.dataaccess.dao.ICloudMenuDao;
import com.vortex.cloud.ums.dataaccess.dao.ICloudSystemDao;
import com.vortex.cloud.ums.dataaccess.service.ICopyFunctionAndMenuService;
import com.vortex.cloud.ums.model.CloudFunction;
import com.vortex.cloud.ums.model.CloudFunctionGroup;
import com.vortex.cloud.ums.model.CloudMenu;
import com.vortex.cloud.ums.model.CloudSystem;
import com.vortex.cloud.ums.support.ManagementConstant;
import com.vortex.cloud.vfs.common.exception.VortexException;
import com.vortex.cloud.vfs.common.uuid.UUIDGenerator;



@Transactional
@Service("copyFunctionAndMenuService")
public class CopyFunctionAndMenuServiceImpl implements ICopyFunctionAndMenuService {
	private static final Logger logger = LoggerFactory.getLogger(CopyFunctionAndMenuServiceImpl.class);

	@Resource
	private ICloudSystemDao cloudSystemDao;
	@Resource
	private ICloudFunctionGroupDao cloudFunctionGroupDao;
	@Resource
	private ICloudFunctionDao cloudFunctionDao;
	@Resource
	private ICloudMenuDao cloudMenuDao;

	@Override
	public void copyFunctionAndMenu(String sourceBsCode, String targetBsCode) throws Exception {
		if (StringUtils.isEmpty(sourceBsCode) || StringUtils.isEmpty(targetBsCode)) {
			logger.error("拷贝业务系统的功能和菜单时，必须传入资源系统code和目标系统code！");
			throw new VortexException("拷贝业务系统的功能和菜单时，必须传入资源系统code和目标系统code！");
		}

		CloudSystem sourceSystem = cloudSystemDao.getByCode(sourceBsCode);
		if (sourceSystem == null) {
			logger.error("根据资源系统code[" + sourceBsCode + "]，未找到系统信息！");
			throw new VortexException("根据资源系统code[" + sourceBsCode + "]，未找到系统信息！");
		}
		if (CloudSystem.SYSTEM_TYPE_CLOUD.equals(sourceSystem.getSystemType())) {
			logger.error("资源系统code[" + sourceBsCode + "]对应的系统类型为云服务系统，无法复制！");
			throw new VortexException("资源系统code[" + sourceBsCode + "]对应的系统类型为云服务系统，无法复制！");
		}

		CloudSystem targetSystem = cloudSystemDao.getByCode(targetBsCode);
		if (targetSystem == null) {
			logger.error("根据目标系统code[" + targetBsCode + "]，未找到系统信息！");
			throw new VortexException("根据目标系统code[" + targetBsCode + "]，未找到系统信息！");
		}
		if (CloudSystem.SYSTEM_TYPE_CLOUD.equals(targetSystem.getSystemType())) {
			logger.error("目标系统code[" + targetBsCode + "]对应的系统类型为云服务系统，无法复制！");
			throw new VortexException("目标系统code[" + targetBsCode + "]对应的系统类型为云服务系统，无法复制！");
		}

		Map<String, String> fgContrast = new HashMap<String, String>(); // 新老functionGroupId的对照表，key为老id，值为新id
		fgContrast.put("-1", "-1");
		Map<String, String> fContrast = new HashMap<String, String>(); // 新老functionId的对照表，key为老id，值为新id，复制菜单时候将会用到
		fContrast.put("-1", "-1");
		Map<String, String> mContrast = new HashMap<String, String>(); // 新老menuId的对照表，key为老id，值为新id，复制菜单时候将会用到
		mContrast.put("-1", "-1");
		Map<String, String> mfContract = new HashMap<String, String>(); // 新老主功能id对照表，key为老id，值为新id，复制功能时将会用到

		// 拷贝功能和功能组
		copyFunctionGroup(sourceSystem.getId(), targetSystem.getId(), targetSystem.getSystemCode(), "-1", fgContrast, fContrast, mfContract);

		// 拷贝菜单
		copyMenu(sourceSystem.getId(), targetSystem.getId(), "-1", fContrast, mContrast);
	}

	private void copyMenu(String sourceSysId, String targetSysId, String parentId, Map<String, String> fContrast, Map<String, String> mContrast) {
		List<CloudMenu> mList = cloudMenuDao.getMenusByParentId(sourceSysId, parentId);

		if (CollectionUtils.isEmpty(mList)) {
			return;
		}

		for (CloudMenu oldMenu : mList) {
			copyMenu(oldMenu, targetSysId, fContrast, mContrast);

			copyMenu(sourceSysId, targetSysId, oldMenu.getId(), fContrast, mContrast);
		}
	}

	private void copyMenu(CloudMenu oldMenu, String targetSysId, Map<String, String> fContrast, Map<String, String> mContrast) {
		CloudMenu newMenu = new CloudMenu();
		BeanUtils.copyProperties(oldMenu, newMenu);
		newMenu.setId(UUIDGenerator.getUUID());
		newMenu.setStatus(0);
		newMenu.setCreateTime(null);
		newMenu.setLastChangeTime(null);
		newMenu.setBeenDeleted(0);
		newMenu.setDeletedTime(null);
		newMenu.setSystemId(targetSysId); // 系统id
		newMenu.setDescription("拷贝功能生成"); // 描述
		newMenu.setFunctionId(fContrast.get(oldMenu.getFunctionId())); // 功能号id
		newMenu.setParentId(mContrast.get(oldMenu.getParentId())); // 父节点id
		newMenu = cloudMenuDao.saveAndFlush(newMenu);

		// 加入对照表
		mContrast.put(oldMenu.getId(), newMenu.getId());
	}

	private void copyFunctionGroup(String sourceSysId, String targetSysId, String targetSysCode, String parentId, Map<String, String> fgContrast, Map<String, String> fContrast,
			Map<String, String> mfContract) {
		List<CloudFunctionGroup> cgList = cloudFunctionGroupDao.getByParentId(sourceSysId, parentId);

		if (CollectionUtils.isEmpty(cgList)) {
			return;
		}

		for (CloudFunctionGroup cg : cgList) {
			// 拷贝功能组
			this.copyFunctionGroup(cg, fgContrast, targetSysId);

			// 拷贝功能组下面的功能，必须让主功能排在前面
			List<CloudFunction> fList = cloudFunctionDao.getByGroupId(cg.getId());
			this.copyFunction(fList, fgContrast, fContrast, targetSysId, targetSysCode, mfContract);

			// 递归
			copyFunctionGroup(sourceSysId, targetSysId, targetSysCode, cg.getId(), fgContrast, fContrast, mfContract);
		}
	}

	/**
	 * 复制功能
	 * 
	 * @param fList
	 * @param fgContrast
	 * @param fContrast
	 * @param targetSysId
	 * @param targetSysCode
	 */
	private void copyFunction(List<CloudFunction> fList, Map<String, String> fgContrast, Map<String, String> fContrast, String targetSysId, String targetSysCode,Map<String, String> mfContract) {
		if (CollectionUtils.isEmpty(fList)) {
			return;
		}

		for (CloudFunction oldFun : fList) {
			CloudFunction newFun = new CloudFunction();
			BeanUtils.copyProperties(oldFun, newFun);
			newFun.setId(null);
			newFun.setStatus(0);
			newFun.setCreateTime(null);
			newFun.setLastChangeTime(null);
			newFun.setBeenDeleted(0);
			newFun.setDeletedTime(null);
			newFun.setDescription("该功能在拷贝时生成");
			newFun.setFunctionType(oldFun.getFunctionType());
			newFun.setMainFunctionId(mfContract.get(oldFun.getMainFunctionId())); // 从对照表中根据旧的主功能id取得新的主功能id
			newFun.setGroupId(fgContrast.get(oldFun.getGroupId())); // 从功能组对照表中取得对照组id
			newFun.setSystemId(targetSysId);
			newFun.setUri(this.replaceParam(newFun.getUri(), ManagementConstant.REQ_PARAM_SYSTEM_CODE, targetSysCode));
			newFun = cloudFunctionDao.saveAndFlush(newFun);
			
			// 如果旧的功能是主功能，则需将主功能新旧对照放进缓存
			if (CloudFunction.FUNCTION_TYPE_MAIN.equals(oldFun.getFunctionType())) {
				mfContract.put(oldFun.getId(), newFun.getId());
			}

			// 加入对照表，复制菜单的时候将会用到
			fContrast.put(oldFun.getId(), newFun.getId());
		}
	}

	/**
	 * 复制功能组
	 * 
	 * @param cg
	 * @param fgContrast
	 * @param targetSysId
	 */
	private void copyFunctionGroup(CloudFunctionGroup cg, Map<String, String> fgContrast, String targetSysId) {
		CloudFunctionGroup ng = new CloudFunctionGroup();
		BeanUtils.copyProperties(cg, ng);
		ng.setId(null);
		ng.setStatus(0);
		ng.setCreateTime(null);
		ng.setLastChangeTime(null);
		ng.setBeenDeleted(0);
		ng.setDeletedTime(null);
		ng.setParentId(fgContrast.get(cg.getParentId())); // 得到对照组ID中的新id
		ng.setSystemId(targetSysId);
		ng = cloudFunctionGroupDao.saveAndFlush(ng);
		// 加入对照表
		fgContrast.put(cg.getId(), ng.getId());
	}

	/**
	 * 替换URL中的某个参数值
	 * 
	 * @param uri
	 *            原始URL
	 * @param paramName
	 *            参数名称
	 * @param newValue
	 *            参数目标值
	 * @return 替换参数值后的URL
	 */
	private String replaceParam(String uri, String paramName, String newValue) {
		if (StringUtils.isEmpty(uri) || StringUtils.isEmpty(paramName)) {
			return uri;
		}

		int indexOfQm = uri.indexOf("?");

		if (indexOfQm == -1 || indexOfQm == uri.length() - 1) { // 未找到参数，直接返回
			return uri;
		}

		String uriPre = uri.substring(0, indexOfQm);

		// 取得问号后面的参数串
		String prams = uri.substring(indexOfQm + 1);
		String[] pms = prams.split("&");
		prams = "";
		for (int i = 0; i < pms.length; i++) {
			if (StringUtils.isNotEmpty(pms[i]) && pms[i].indexOf(paramName + "=") == 0) {
				pms[i] = paramName + "=" + newValue;
			}

			if (i == 0) {
				prams += pms[i];
			} else {
				prams += "&" + pms[i];
			}
		}

		return uriPre + "?" + prams;
	}
}
