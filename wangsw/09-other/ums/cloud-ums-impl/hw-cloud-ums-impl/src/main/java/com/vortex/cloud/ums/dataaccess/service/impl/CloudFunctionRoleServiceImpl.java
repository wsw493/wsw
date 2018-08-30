package com.vortex.cloud.ums.dataaccess.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vortex.cloud.ums.dataaccess.dao.ICloudFunctionRoleDao;
import com.vortex.cloud.ums.dataaccess.service.ICloudFunctionRoleService;
import com.vortex.cloud.ums.dto.CloudFunctionRoleDto;
import com.vortex.cloud.ums.model.CloudFunctionRole;
import com.vortex.cloud.ums.util.orm.Page;
import com.vortex.cloud.vfs.common.exception.ServiceException;
import com.vortex.cloud.vfs.data.hibernate.repository.HibernateRepository;
import com.vortex.cloud.vfs.data.hibernate.service.SimplePagingAndSortingService;





/**
 * 角色功能关系
 * 
 * @author lsm
 * @date 2016年4月7日
 */
@Transactional
@Service("cloudFunctionRoleService")
public class CloudFunctionRoleServiceImpl
	extends SimplePagingAndSortingService<CloudFunctionRole, String>
		implements ICloudFunctionRoleService {

	
	private Logger logger = LoggerFactory.getLogger(CloudFunctionRoleServiceImpl.class);
	
	@Resource
	private ICloudFunctionRoleDao cloudFunctionRoleDao;
	
	@Override
	public HibernateRepository<CloudFunctionRole, String> getDaoImpl() {
		return cloudFunctionRoleDao;
	}
	
	@Override
	public void saveFunctionsForRole(String roleId, String systemId, String[] functionIdArray) {
		if (StringUtils.isBlank(roleId) || StringUtils.isBlank(systemId)) {
			throwExpectionAndLog("saveFunctionsForRole", "入参不能为空");
		}
		
		if(ArrayUtils.isEmpty(functionIdArray)) {
			return;
		}
		
		// 删除现在存在的关系
		List<CloudFunctionRoleDto> dbList = cloudFunctionRoleDao.getListBySystem(roleId, systemId);
		for(CloudFunctionRoleDto functionRoleDto : dbList) {
			cloudFunctionRoleDao.delete(functionRoleDto.getId());
		}
		
		if (ArrayUtils.isNotEmpty(functionIdArray)) { // 数组不为空就保存
			List<CloudFunctionRole> list = new ArrayList<CloudFunctionRole>();

			CloudFunctionRole cloudFunctionRole = null;
			for (String functionId : functionIdArray) {
				cloudFunctionRole = new CloudFunctionRole();
				cloudFunctionRole.setRoleId(roleId);
				cloudFunctionRole.setFunctionId(functionId);
				list.add(cloudFunctionRole);
			}
			
			cloudFunctionRoleDao.save(list);
		}
	}

	@Override
	public void deleteFunctionRole(String id) {
		if (StringUtils.isEmpty(id)) {
			throwExpectionAndLog("deleteFunctionRole", "id不能为空");
		}
		if (canBeDelete(id)) {
			cloudFunctionRoleDao.delete(id);
		}
	}

	/**
	 * 角色与功能之间的关系等否删除
	 * 
	 * @param id 功能id
	 * @return
	 */
	private boolean canBeDelete(String id) {
		return true;
	}

	@Override
	public Page<CloudFunctionRoleDto> getPageBySystem(String roleId, String systemId, Pageable pageable) {
		if (StringUtils.isEmpty(roleId)) {
			throwExpectionAndLog("getPageOfBusinessSystem", "角色id不能为空");
		}

		return cloudFunctionRoleDao.getPageBySystem(roleId, systemId, pageable);
	}

	@Override
	public List<CloudFunctionRoleDto> getListBySystem(String roleId, String systemId) {
		if(StringUtils.isBlank(roleId) || StringUtils.isBlank(systemId)) {
			throwExpectionAndLog("getListOfBusinessSystem", "入参不能为空");
		}
		
		return cloudFunctionRoleDao.getListBySystem(roleId, systemId);
	}

	/**
	 * 记录log和抛异常
	 * 
	 * @param methodName 当前方法名
	 * @param msg 要记录的信息
	 */
	private void throwExpectionAndLog(String methodName, String msg) {
		String message = "CloudFunctionRoleServiceImpl." + msg;
		logger.error(message);
		throw new ServiceException(message);
	}
}
