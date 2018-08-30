package com.vortex.cloud.ums.dataaccess2.service.impl;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.vortex.cloud.ums.dataaccess.dao.ICloudFunctionDao;
import com.vortex.cloud.ums.dataaccess.dao.ICloudFunctionGroupDao;
import com.vortex.cloud.ums.dataaccess.dao.ICloudMenuDao;
import com.vortex.cloud.ums.dataaccess.dao.ICloudSystemDao;
import com.vortex.cloud.ums.dataaccess2.dao.ICloudFunctionDao2;
import com.vortex.cloud.ums.dataaccess2.dao.ICloudFunctionGroupDao2;
import com.vortex.cloud.ums.dataaccess2.dao.ICloudMenuDao2;
import com.vortex.cloud.ums.dataaccess2.dao.ICloudSystemDao2;
import com.vortex.cloud.ums.dataaccess2.service.ICopyFunctionAcrossDatabaseService;
import com.vortex.cloud.ums.dto.CloudFunctionDto;
import com.vortex.cloud.ums.dto.CloudSystemDto;
import com.vortex.cloud.ums.model.CloudFunction;
import com.vortex.cloud.ums.model.CloudFunctionGroup;
import com.vortex.cloud.ums.model.CloudMenu;
import com.vortex.cloud.ums.model.CloudSystem;
import com.vortex.cloud.vfs.common.exception.VortexException;
import com.vortex.cloud.vfs.common.uuid.UUIDGenerator;
import com.vortex.cloud.vfs.data.model.BakDeleteModel;

@Service("copyFunctionAcrossDatabaseService")
@Transactional(value = "transactionManager2", readOnly = true)
public class CopyFunctionAcrossDatabaseServiceImpl implements ICopyFunctionAcrossDatabaseService {
	@Resource
	private ICloudMenuDao cloudMenuDao;
	@Resource
	private ICloudFunctionDao cloudFunctionDao;
	@Resource
	private ICloudFunctionGroupDao cloudFunctionGroupDao;
	@Resource
	private ICloudSystemDao cloudSystemDao;
	@Resource
	private ICloudMenuDao2 cloudMenuDao2;
	@Resource
	private ICloudFunctionDao2 cloudFunctionDao2;
	@Resource
	private ICloudFunctionGroupDao2 cloudFunctionGroupDao2;
	@Resource
	private ICloudSystemDao2 cloudSystemDao2;
	@Resource(name = "sessionFactory2")
	private SessionFactory sessionFactory;

	private static final String ROOT_ID = "-1"; // 树形根节点id
	private static final String CLOUD_FUNCTION_GROUP = "cloud_function_group";
	private static final String CLOUD_MENU = "cloud_menu";
	private static final String SYSTEM_CODE = "systemCode"; // 系统code的key，用于替换老功能中的url中的systemCode参数为新系统的code，否则会导致某些依赖系统code的功能出错

	private static List<String> newFunctionGroupIds = Lists.newArrayList(); // 新增的功能组id列表
	private static List<String> newMenuIds = Lists.newArrayList(); // 新增的菜单id列表
	private static List<String> newFunctionIds = Lists.newArrayList(); // 新增的功能id列表

	@Override
	@Transactional(value = "transactionManager2", readOnly = false)
	public void coyp(String sourceSystemId, String targetSystemId, List<String> smenus) throws Exception {
		if (StringUtils.isEmpty(sourceSystemId) || StringUtils.isEmpty(targetSystemId) || CollectionUtils.isEmpty(smenus)) {
			return;
		}

		List<CloudMenu> oms = cloudMenuDao.findAllByIds(smenus.toArray(new String[smenus.size()]));
		if (CollectionUtils.isNotEmpty(oms)) {
			for (CloudMenu om : oms) {
				this.copyOne(targetSystemId, om);
			}
		}

		newMenuIds.clear();
		newFunctionIds.clear();
		newFunctionGroupIds.clear();
	}

	/**
	 * 复制单个菜单
	 * 
	 * @param targetSystemId
	 * @param om
	 * @throws Exception
	 */
	private void copyOne(String targetSystemId, CloudMenu om) throws Exception {
		CloudMenu nm = cloudMenuDao2.getMenuBySysidAndMcode(targetSystemId, om.getCode());
		if (nm == null) {
			this.addMenu(targetSystemId, om);
		} else {
			this.updateMenu(targetSystemId, om, nm);
		}
	}

	/**
	 * 新增菜单。1.新增菜单基本信息；2.寻找旧菜单的父节点code在目标系统中对应的父菜单，如果未找到此菜单，新建父菜单，然后递归循环，
	 * 直到找到父菜单或者父菜单的父节点为-1为止；3.然后继续调用相应的更新function和functiongroup的算法
	 * 
	 * @param targetSystemId
	 * @param om
	 * @throws Exception
	 */
	private void addMenu(String targetSystemId, CloudMenu om) throws Exception {
		// 处理功能组和功能
		String functionId = this.copyFunction(targetSystemId, om.getFunctionId());

		List<CloudMenu> oldMenus = Lists.newArrayList();

		// 得到待处理的老菜单列表，自下而上
		List<String> parentIds = Lists.newArrayList(); // 待复制的最顶层的菜单的父节点id
		this.getWaitCopyMenus(targetSystemId, om, oldMenus, parentIds);

		String parentId = parentIds.get(0);

		for (int i = oldMenus.size() - 1; i >= 0; i--) {
			sessionFactory.getCurrentSession().clear();
			CloudMenu newMenu = cloudMenuDao2.getMenuBySysidAndMcode(targetSystemId, oldMenus.get(i).getCode());
			if (newMenu == null) {
				newMenu = new CloudMenu();
				newMenu.setId(UUIDGenerator.getUUID());
				newMenu.setCreateTime(new Date());
				newMenu.setLastChangeTime(new Date());
				newMenu.setStatus(0);
				newMenu.setBeenDeleted(BakDeleteModel.NO_DELETED);
				newMenu.setDeletedTime(null);
				newMenu.setSystemId(targetSystemId); // 云系统id
				newMenu.setCode(oldMenus.get(i).getCode()); // 编码
				newMenu.setName(oldMenus.get(i).getName()); // 名称
				newMenu.setOrderIndex(oldMenus.get(i).getOrderIndex()); // 排序号
				newMenu.setDescription(oldMenus.get(i).getDescription()); // 描述
				newMenu.setParentId(parentId); // 父节点id
				newMenu.setPhotoIds(oldMenus.get(i).getPhotoIds()); // json格式的字符串
				newMenu.setIsHidden(oldMenus.get(i).getIsHidden()); // 是否隐藏，默认0显示，1隐藏
				newMenu.setFunctionId(i == 0 ? functionId : null); // 绑定的功能码，只有最底层的才会绑定功能码
				newMenu.setIsControlled(oldMenus.get(i).getIsControlled()); // 是否受控制，默认1-受控，0-不受控；不受控的菜单，所有人都可以访问
				newMenu.setIsWelcomeMenu(oldMenus.get(i).getIsWelcomeMenu()); // 是否欢迎页面，默认0-否，1-是
				newMenu.setNodeCode(this.getNextNodecode(targetSystemId, CLOUD_MENU, parentId)); // 内置编号：用于层级数据结构的构造（如树）
				newMenu.setChildSerialNumer(0); // 子层所有数据记录数，和己编号配置生成子编号
				cloudMenuDao2.save(newMenu);
				newMenuIds.add(newMenu.getId());

				// 父层的ChildSerialNumer+1
				if (!ROOT_ID.equals(parentId)) {
					sessionFactory.getCurrentSession().clear();
					CloudMenu newParentMenu = cloudMenuDao2.getById(parentId);
					newParentMenu.setChildSerialNumer(newParentMenu.getChildSerialNumer() == null ? 1 : newParentMenu.getChildSerialNumer() + 1);
					cloudMenuDao2.update(newParentMenu);
				}
			} else {
				if (!newMenuIds.contains(newMenu.getId())) {
					newMenu.setLastChangeTime(new Date());
					newMenu.setName(oldMenus.get(i).getName()); // 名称
					// newMenu.setOrderIndex(oldMenus.get(i).getOrderIndex());
					// //
					// 排序号
					newMenu.setDescription(oldMenus.get(i).getDescription()); // 描述
					newMenu.setIsHidden(oldMenus.get(i).getIsHidden()); // 是否隐藏，默认0显示，1隐藏
					newMenu.setFunctionId(i == 0 ? functionId : null); // 绑定的功能码，只有最底层的才会绑定功能码
					newMenu.setIsControlled(oldMenus.get(i).getIsControlled()); // 是否受控制，默认1-受控，0-不受控；不受控的菜单，所有人都可以访问
					newMenu.setIsWelcomeMenu(oldMenus.get(i).getIsWelcomeMenu()); // 是否欢迎页面，默认0-否，1-是
					cloudMenuDao2.update(newMenu);
				}
			}

			parentId = newMenu.getId();
		}
	}

	/**
	 * 根据旧菜单，向上追述需要复制的老菜单的列表
	 * 
	 * @param targetSystemId
	 * @param oldMenu
	 * @param rst
	 * @param 返回最上层的菜单的父id
	 * @throws Exception
	 */
	private void getWaitCopyMenus(String targetSystemId, CloudMenu oldMenu, List<CloudMenu> rst, List<String> parentIds) throws Exception {
		rst.add(oldMenu);

		// 找到新库中同code的菜单
		sessionFactory.getCurrentSession().clear();
		CloudMenu newMenu = cloudMenuDao2.getMenuBySysidAndMcode(targetSystemId, oldMenu.getCode());
		String flag = null;
		if (newMenu != null) {
			parentIds.add(newMenu.getParentId());
			flag = ROOT_ID;
		} else if (oldMenu.getParentId().equals(ROOT_ID)) {
			parentIds.add(ROOT_ID);
			flag = ROOT_ID;
		}

		if (StringUtils.isEmpty(flag)) {
			CloudMenu oldParentMenu = cloudMenuDao.findOne(oldMenu.getParentId());

			this.getWaitCopyMenus(targetSystemId, oldParentMenu, rst, parentIds);
		}
	}

	/**
	 * 根据功能号，向上追述需要复制的老功能的列表，底层的排在前面，自下而上的列表
	 * 
	 * @param targetSystemId
	 * @param groupId
	 * @param rst
	 * @return 返回最新的功能组的父id
	 * @throws Exception
	 */
	private void getOldGroups(String targetSystemId, CloudFunctionGroup oldGroup, List<CloudFunctionGroup> rst, List<String> pids) throws Exception {
		rst.add(oldGroup);
		// 找到新库中同code的组
		CloudFunctionGroup newGroup = cloudFunctionGroupDao2.getFunctionGroupBySysidAndFgcode(targetSystemId, oldGroup.getCode());

		if (newGroup != null) { // 如果找到同编码的新组、或者旧组的父节点是根节点则直接返回
			pids.add(newGroup.getParentId());
		} else if (oldGroup.getParentId().equals(ROOT_ID)) {
			pids.add(ROOT_ID);
		}

		if (CollectionUtils.isNotEmpty(pids)) {
			throw new RuntimeException();
		}

		CloudFunctionGroup oldParentGroup = cloudFunctionGroupDao.findOne(oldGroup.getParentId());

		this.getOldGroups(targetSystemId, oldParentGroup, rst, pids);
	}

	/**
	 * 更新菜单，除id和parentId外都更新，然后继续调用相应的更新function和functiongroup的算法。
	 * 
	 * @param targetSystemId
	 * @param om
	 * @param nm
	 * @throws Exception
	 */
	private void updateMenu(String targetSystemId, CloudMenu om, CloudMenu nm) throws Exception {
		// 处理功能组和功能
		String functionId = this.copyFunction(targetSystemId, om.getFunctionId());

		if (!newMenuIds.contains(nm.getId())) {
			// 更新菜单信息
			nm.setLastChangeTime(new Date()); // 最后更新时间
			nm.setName(om.getName()); // 名称
			nm.setDescription(om.getDescription()); // 描述
			nm.setIsHidden(om.getIsHidden()); // 是否隐藏，默认0显示，1隐藏
			nm.setFunctionId(functionId); // 绑定的功能码
			nm.setIsControlled(om.getIsControlled()); // 是否受控制，默认1-受控，0-不受控；不受控的菜单，所有人都可以访问
			nm.setIsWelcomeMenu(om.getIsWelcomeMenu()); // 是否欢迎页面，默认0-否，1-是
			cloudMenuDao2.update(nm);
		}
	}

	/**
	 * 根据目标系统id和资源系统的功能id，复制功能
	 * 
	 * @param targetSystemId
	 * @param functionId
	 * @throws Exception
	 */
	private String copyFunction(String targetSystemId, String functionId) throws Exception {
		CloudFunction of = cloudFunctionDao.findOne(functionId);
		if (of == null) {
			return null;
		}
		CloudFunctionDto nf = cloudFunctionDao2.getFunctionDtoBySysidAndFcode(targetSystemId, of.getCode());

		String rst = null; // 拷贝好功能后，返回给菜单层的功能id。
		if (nf == null) {
			rst = this.addFunction(targetSystemId, of);
		} else {
			rst = this.updateFunction(targetSystemId, of, nf.getId());
		}

		return rst;
	}

	/**
	 * 菜单对应的功能号不存在时，需要新增功能，同时向上追加功能组信息。如果该功能是主功能，则需要将所辖的辅功能一并新增。
	 * 
	 * @param targetSystemId
	 * @param of
	 * @return
	 * @throws Exception
	 */
	private String addFunction(String targetSystemId, CloudFunction of) throws Exception {
		// 处理功能组，返回父节点id
		String groupId = this.addFunctionGroup(targetSystemId, of);

		CloudSystemDto targetSystem = cloudSystemDao2.getById(targetSystemId);

		// 找到目标系统的id
		CloudSystem oldGoalSystem = cloudSystemDao.findOne(of.getGoalSystemId());
		CloudSystem newGoalSystem = cloudSystemDao2.getByCode(oldGoalSystem.getSystemCode());
		if (newGoalSystem == null) {
			throw new VortexException("在目标库中未找到code为[" + oldGoalSystem.getSystemCode() + "]的系统，请先新增该系统后再拷贝！");
		}

		// 如果是辅功能，则找到新库中主功能
		String mainFunctionId = null;
		if (CloudFunction.FUNCTION_TYPE_MINOR.equals(of.getFunctionType()) && StringUtils.isNotEmpty(of.getMainFunctionId())) {
			CloudFunction oldMainFunction = cloudFunctionDao.getByCode(of.getSystemId(), of.getMainFunctionId());
			CloudFunction newMainFunction = cloudFunctionDao2.getFunctionBySysidAndFcode(targetSystemId, oldMainFunction.getCode());
			mainFunctionId = newMainFunction == null ? null : newMainFunction.getId();
		}

		// 新增功能
		CloudFunction nf = new CloudFunction();
		nf.setId(UUIDGenerator.getUUID());
		nf.setCreateTime(new Date());
		nf.setLastChangeTime(new Date());
		nf.setStatus(0);
		nf.setBeenDeleted(BakDeleteModel.NO_DELETED);
		nf.setDeletedTime(null);

		nf.setCode(of.getCode()); // 编码
		nf.setName(of.getName()); // 名称
		nf.setDescription(of.getDescription()); // 描述
		nf.setGroupId(groupId); // 组id
		nf.setOrderIndex(of.getOrderIndex()); // 排序号
		nf.setUri(this.replaceParam(of.getUri(), SYSTEM_CODE, targetSystem.getSystemCode())); // 绑定URI，并将系统uri中的系统code修改为新系统
		nf.setSystemId(targetSystemId); // 所属系统id
		nf.setGoalSystemId(newGoalSystem.getId()); // 指向的系统id
		nf.setFunctionType(of.getFunctionType()); // 功能类型 1-主功能，2-辅功能
		nf.setMainFunctionId(mainFunctionId); // 主功能id
		cloudFunctionDao2.save(nf);
		newFunctionIds.add(nf.getId());

		if (CloudFunction.FUNCTION_TYPE_MAIN.equals(of.getFunctionType())) { // 如果是主功能，则将所属的辅功能一并新增，全部挂接到新增的groupId上面
			// 旧系统的辅功能id
			List<CloudFunctionDto> omList = cloudFunctionDao.listByMainId(of.getId());
			if (CollectionUtils.isNotEmpty(omList)) {
				for (CloudFunctionDto omf : omList) {
					CloudFunction newMF = cloudFunctionDao2.getFunctionBySysidAndFcode(targetSystemId, omf.getCode());

					if (newMF == null) { // 新增辅功能
						newMF = new CloudFunction();
						newMF.setId(UUIDGenerator.getUUID());
						newMF.setCreateTime(new Date());
						newMF.setLastChangeTime(new Date());
						newMF.setStatus(0);
						newMF.setBeenDeleted(BakDeleteModel.NO_DELETED);
						newMF.setDeletedTime(null);

						newMF.setCode(omf.getCode()); // 编码
						newMF.setName(omf.getName()); // 名称
						newMF.setDescription(omf.getDescription()); // 描述
						newMF.setGroupId(groupId); // 组id
						newMF.setOrderIndex(omf.getOrderIndex()); // 排序号
						newMF.setUri(this.replaceParam(omf.getUri(), SYSTEM_CODE, targetSystem.getSystemCode())); // 绑定URI，并将系统uri中的系统code修改为新系统
						newMF.setSystemId(targetSystemId); // 所属系统id
						newMF.setGoalSystemId(newGoalSystem.getId()); // 指向的系统id
						newMF.setFunctionType(omf.getFunctionType()); // 功能类型,1-主功能，2-辅功能
						newMF.setMainFunctionId(nf.getId()); // 主功能id
						cloudFunctionDao2.save(newMF);
						newFunctionIds.add(newMF.getId());
					} else { // 更新辅功能
						if (!newFunctionIds.contains(newMF.getId())) {
							newMF.setLastChangeTime(new Date());
							newMF.setName(omf.getName()); // 名称
							newMF.setDescription(omf.getDescription()); // 描述
							newMF.setGroupId(groupId); // 组id
							newMF.setOrderIndex(omf.getOrderIndex()); // 排序号
							newMF.setUri(this.replaceParam(omf.getUri(), SYSTEM_CODE, targetSystem.getSystemCode())); // 绑定URI，并将系统uri中的系统code修改为新系统
							newMF.setSystemId(targetSystemId); // 所属系统id
							newMF.setGoalSystemId(newGoalSystem.getId()); // 指向的系统id
							newMF.setMainFunctionId(nf.getId()); // 主功能id
							cloudFunctionDao2.update(newMF);
						}
					}
				}
			}
		}

		return nf.getId();
	}

	/**
	 * 菜单对应的功能号存在时，仅更新该功能
	 * 
	 * @param targetSystemId
	 * @param of
	 * @param nf
	 * @return
	 * @throws Exception
	 */
	private String updateFunction(String targetSystemId, CloudFunction of, String nfid) throws Exception {
		CloudSystemDto targetSystem = cloudSystemDao2.getById(targetSystemId);

		// 找到目标系统的id
		CloudSystem oldGoalSystem = cloudSystemDao.findOne(of.getGoalSystemId());
		CloudSystem newGoalSystem = cloudSystemDao2.getByCode(oldGoalSystem.getSystemCode());
		if (newGoalSystem == null) {
			throw new VortexException("在目标库中未找到code为[" + oldGoalSystem.getSystemCode() + "]的系统，请先新增该系统后再拷贝！");
		}

		CloudFunction nf = cloudFunctionDao2.getById(nfid);

		if (!newFunctionIds.contains(nf.getId())) {
			nf.setName(of.getName());
			nf.setDescription(of.getDescription());
			nf.setOrderIndex(of.getOrderIndex());
			nf.setUri(this.replaceParam(of.getUri(), SYSTEM_CODE, targetSystem.getSystemCode())); // 绑定URI，并将系统uri中的系统code修改为新系统
			nf.setLastChangeTime(new Date());
			cloudFunctionDao2.update(nf);
		}

		if (CloudFunction.FUNCTION_TYPE_MAIN.equals(of.getFunctionType())) { // 如果是主功能，则将所属的辅功能一并新增，全部挂接到新增的groupId上面
			// 旧系统的辅功能id
			List<CloudFunctionDto> omList = cloudFunctionDao.listByMainId(of.getId());
			if (CollectionUtils.isNotEmpty(omList)) {
				for (CloudFunctionDto omf : omList) {
					CloudFunction newMF = cloudFunctionDao2.getFunctionBySysidAndFcode(targetSystemId, omf.getCode());

					if (newMF == null) { // 新增辅功能
						newMF = new CloudFunction();
						newMF.setId(UUIDGenerator.getUUID());
						newMF.setCreateTime(new Date());
						newMF.setLastChangeTime(new Date());
						newMF.setStatus(0);
						newMF.setBeenDeleted(BakDeleteModel.NO_DELETED);
						newMF.setDeletedTime(null);

						newMF.setCode(omf.getCode()); // 编码
						newMF.setName(omf.getName()); // 名称
						newMF.setDescription(omf.getDescription()); // 描述
						newMF.setGroupId(nf.getGroupId()); // 组id
						newMF.setOrderIndex(omf.getOrderIndex()); // 排序号
						newMF.setUri(this.replaceParam(omf.getUri(), SYSTEM_CODE, targetSystem.getSystemCode())); // 绑定URI，并将系统uri中的系统code修改为新系统
						newMF.setSystemId(targetSystemId); // 所属系统id
						newMF.setGoalSystemId(newGoalSystem.getId()); // 指向的系统id
						newMF.setFunctionType(omf.getFunctionType()); // 功能类型,1-主功能，2-辅功能
						newMF.setMainFunctionId(nf.getId()); // 主功能id
						cloudFunctionDao2.save(newMF);
						newFunctionIds.add(newMF.getId());
					} else { // 更新辅功能
						if (!newFunctionIds.contains(newMF.getId())) {
							newMF.setLastChangeTime(new Date());
							newMF.setName(omf.getName()); // 名称
							newMF.setDescription(omf.getDescription()); // 描述
							newMF.setGroupId(nf.getGroupId()); // 组id
							newMF.setOrderIndex(omf.getOrderIndex()); // 排序号
							newMF.setUri(this.replaceParam(omf.getUri(), SYSTEM_CODE, targetSystem.getSystemCode())); // 绑定URI，并将系统uri中的系统code修改为新系统
							newMF.setSystemId(targetSystemId); // 所属系统id
							newMF.setGoalSystemId(newGoalSystem.getId()); // 指向的系统id
							newMF.setMainFunctionId(nf.getId()); // 主功能id
							cloudFunctionDao2.update(newMF);
						}
					}
				}
			}
		}

		return nf.getId();
	}

	/**
	 * 根据旧功能，向上追加功能组信息到新库
	 * 
	 * @param of
	 * @return 返回该新功能所属的新功能组id
	 * @throws Exception
	 */
	private String addFunctionGroup(String targetSystemId, CloudFunction of) throws Exception {
		CloudFunctionGroup oldGroup = cloudFunctionGroupDao.findOne(of.getGroupId());

		// 取得将要拷贝的老功能组的列表，自下而上
		List<CloudFunctionGroup> waitCopiedOldGroups = Lists.newArrayList();
		List<String> pids = Lists.newArrayList();
		try {
			this.getOldGroups(targetSystemId, oldGroup, waitCopiedOldGroups, pids);
		} catch (RuntimeException e) {
			e.printStackTrace();

		}

		// 自上而下拷贝功能组，并将最后底层的供能组id返回，供拷贝功能号的时候用
		return this.copyGroup(targetSystemId, waitCopiedOldGroups, pids.get(0));
	}

	/**
	 * 拷贝功能组，返回最低级的功能组id给新功能设置组id
	 * 
	 * @param targetSystemId
	 *            目标系统id
	 * @param waitCopiedOldGroups
	 *            待拷贝的旧系统功能组列表
	 * @param firstParentId
	 *            待拷贝的第一个节点的父节点id
	 * @return
	 * @throws Exception
	 */
	private String copyGroup(String targetSystemId, List<CloudFunctionGroup> waitCopiedOldGroups, String firstParentId) throws Exception {
		String rst = null;
		String parentId = null;
		CloudFunctionGroup newGroup = null;

		for (int i = waitCopiedOldGroups.size() - 1; i >= 0; i--) {
			// 找到新库中同code的组
			newGroup = cloudFunctionGroupDao2.getFunctionGroupBySysidAndFgcode(targetSystemId, waitCopiedOldGroups.get(i).getCode());

			if (newGroup == null) { // 功能组新增
				parentId = i == waitCopiedOldGroups.size() - 1 ? firstParentId : rst;

				newGroup = new CloudFunctionGroup();
				newGroup.setId(UUIDGenerator.getUUID());
				newGroup.setCreateTime(new Date());
				newGroup.setLastChangeTime(new Date());
				newGroup.setStatus(0);
				newGroup.setBeenDeleted(BakDeleteModel.NO_DELETED);
				newGroup.setDeletedTime(null);

				newGroup.setCode(waitCopiedOldGroups.get(i).getCode()); // 编码
				newGroup.setName(waitCopiedOldGroups.get(i).getName()); // 名称
				newGroup.setDescription(waitCopiedOldGroups.get(i).getDescription()); // 描述
				newGroup.setParentId(parentId); // 父节点id
				newGroup.setOrderIndex(waitCopiedOldGroups.get(i).getOrderIndex()); // 顺序号
				newGroup.setSystemId(targetSystemId); // 所属系统id
				newGroup.setChildSerialNumber(new Integer(0)); // 子节点数量

				// 处理树形字段nodecode
				newGroup.setNodeCode(this.getNextNodecode(targetSystemId, CLOUD_FUNCTION_GROUP, parentId));
				cloudFunctionGroupDao2.save(newGroup);
				newFunctionGroupIds.add(newGroup.getId());

				// 处理父节点的childSerialNumer+1
				if (!parentId.equals(ROOT_ID)) {
					sessionFactory.getCurrentSession().clear();
					CloudFunctionGroup parentGroup = cloudFunctionGroupDao2.getById(parentId);
					Integer cn = parentGroup.getChildSerialNumber();
					parentGroup.setChildSerialNumber(cn == null ? 1 : cn + 1);
					cloudFunctionGroupDao2.update(parentGroup);
				}
			} else { // 功能组更新
				if (!newFunctionGroupIds.contains(newGroup.getId())) {
					sessionFactory.getCurrentSession().clear();
					newGroup.setName(waitCopiedOldGroups.get(i).getName());
					newGroup.setDescription(waitCopiedOldGroups.get(i).getDescription());
					newGroup.setLastChangeTime(new Date());
					cloudFunctionGroupDao2.update(newGroup);
				}
			}

			rst = newGroup.getId(); // 将每次新增或者更新后的新库中组id放到变量中
		}
		return rst;
	}

	private String getNextNodecode(String sysid, String tableName, String parentId) throws Exception {
		if (parentId.equals(ROOT_ID)) { // 如果父节点是-1，则查询到最大的nodecode后再处理
			String maxCode = cloudFunctionGroupDao2.getMaxChildNodecode(sysid, parentId, tableName);
			if (StringUtils.isEmpty(maxCode)) {
				return "01";
			} else {
				// 截取后两位定义整数
				Integer a = new Integer(maxCode.substring(maxCode.length() - 2, maxCode.length()));
				a++;
				String rst = (a + 100) + "";
				rst = rst.substring(rst.length() - 2, rst.length());

				return maxCode.substring(0, maxCode.length() - 2) + rst;
			}
		}

		// 如果父节点不是-1，则直接查询父节点，然后将childSerialNumer+1后拼接
		Integer cn = null;
		String nodeCode = null;
		if (tableName.equals(CLOUD_FUNCTION_GROUP)) {
			CloudFunctionGroup pg = cloudFunctionGroupDao2.getById(parentId);
			cn = pg.getChildSerialNumber();
			nodeCode = pg.getNodeCode();
		} else {
			CloudMenu pm = cloudMenuDao2.getById(parentId);
			cn = pm.getChildSerialNumer();
			nodeCode = pm.getNodeCode();
		}

		if (cn == null || cn == 0) {
			return nodeCode + "01";
		} else {
			String rst = (cn + 101) + "";
			return nodeCode + rst.substring(rst.length() - 2, rst.length());
		}
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
		if (StringUtils.isEmpty(uri) || StringUtils.isEmpty(paramName) || !uri.contains(paramName)) {
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

	public void save() {

	}
}
