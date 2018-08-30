package com.vortex.cloud.ums.dataaccess.service.impl;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.vortex.cloud.ums.dataaccess.dao.ICloudFunctionDao;
import com.vortex.cloud.ums.dataaccess.dao.ICloudFunctionGroupDao;
import com.vortex.cloud.ums.dataaccess.dao.ICloudFunctionRoleDao;
import com.vortex.cloud.ums.dataaccess.dao.ICloudMenuDao;
import com.vortex.cloud.ums.dataaccess.dao.ICloudRoleDao;
import com.vortex.cloud.ums.dataaccess.dao.ICloudRoleGroupDao;
import com.vortex.cloud.ums.dataaccess.dao.ICloudSystemDao;
import com.vortex.cloud.ums.dataaccess.dao.ICloudUserRoleDao;
import com.vortex.cloud.ums.dataaccess.dao.IPramSettingDao;
import com.vortex.cloud.ums.dataaccess.dao.ITenantDao;
import com.vortex.cloud.ums.dataaccess.dao.ITenantPramSettingDao;
import com.vortex.cloud.ums.dataaccess.dao.ITenantSystemRelationDao;
import com.vortex.cloud.ums.dataaccess.service.ICallRestService;
import com.vortex.cloud.ums.dataaccess.service.ICentralCacheRedisService;
import com.vortex.cloud.ums.dataaccess.service.ICloudRoleService;
import com.vortex.cloud.ums.dataaccess.service.ICloudStaffService;
import com.vortex.cloud.ums.dataaccess.service.ICloudUserRoleService;
import com.vortex.cloud.ums.dataaccess.service.ICloudUserService;
import com.vortex.cloud.ums.dataaccess.service.ITenantBusinessService;
import com.vortex.cloud.ums.dto.CloudRoleDto;
import com.vortex.cloud.ums.dto.CloudSystemDto;
import com.vortex.cloud.ums.dto.CloudSystemFunctionDto;
import com.vortex.cloud.ums.dto.CloudUserDto;
import com.vortex.cloud.ums.dto.MenuTreeDto;
import com.vortex.cloud.ums.dto.TenantDto;
import com.vortex.cloud.ums.model.CloudFunction;
import com.vortex.cloud.ums.model.CloudFunctionGroup;
import com.vortex.cloud.ums.model.CloudFunctionRole;
import com.vortex.cloud.ums.model.CloudMenu;
import com.vortex.cloud.ums.model.CloudRole;
import com.vortex.cloud.ums.model.CloudRoleGroup;
import com.vortex.cloud.ums.model.CloudStaff;
import com.vortex.cloud.ums.model.CloudSystem;
import com.vortex.cloud.ums.model.CloudUser;
import com.vortex.cloud.ums.model.CloudUserRole;
import com.vortex.cloud.ums.model.PramSetting;
import com.vortex.cloud.ums.model.Tenant;
import com.vortex.cloud.ums.model.TenantPramSetting;
import com.vortex.cloud.ums.model.TenantSystemRelation;
import com.vortex.cloud.ums.support.ManagementConstant;
import com.vortex.cloud.ums.tree.CloudMenuTree;
import com.vortex.cloud.vfs.common.exception.ServiceException;
import com.vortex.cloud.vfs.common.exception.VortexException;
import com.vortex.cloud.vfs.common.lang.StringUtil;
import com.vortex.cloud.vfs.common.mapper.JsonMapper;
import com.vortex.cloud.vfs.common.uuid.UUIDGenerator;
import com.vortex.cloud.vfs.data.dto.RestResultDto;
import com.vortex.cloud.vfs.data.support.SearchFilter;
import com.vortex.cloud.vfs.data.support.SearchFilter.Operator;








@Transactional
@Service("tenantBusinessService")
public class TenantBusinessServiceImpl implements ITenantBusinessService {

	private static final Logger logger = LoggerFactory.getLogger(TenantBusinessServiceImpl.class);
	private static final String JCSS_SYS_CODE = "CLOUD_JCSS";

	@Resource
	private ICallRestService callRestService;

	@Resource
	private ICloudSystemDao cloudSystemDao;

	@Resource
	private ITenantDao tenantDao;

	@Resource
	private ITenantSystemRelationDao tenantSystemRelationDao;

	@Resource
	private IPramSettingDao pramSettingDao;

	@Resource
	private ITenantPramSettingDao tenantPramSettingDao;

	@Resource
	private ICloudStaffService cloudStaffService;

	@Resource
	private ICloudUserService cloudUserService;

	@Resource
	private ICloudRoleService cloudRoleService;

	@Resource
	private ICloudUserRoleService cloudUserRoleService;

	@Resource
	private ICloudRoleDao cloudRoleDao;

	@Resource
	private ICloudRoleGroupDao cloudRoleGroupDao;

	@Resource
	private ICloudUserRoleDao cloudUserRoleDao;

	@Resource
	private ICloudFunctionDao cloudFunctionDao;

	@Resource
	private ICloudFunctionGroupDao cloudFunctionGroupDao;

	@Resource
	private ICloudFunctionRoleDao cloudFunctionRoleDao;

	@Resource
	private ICloudMenuDao cloudMenuDao;
	@Resource(name = CentralCacheRedisServiceImpl.CLASSNAME)
	private ICentralCacheRedisService centralCacheRedisService;
	private JsonMapper jsonMapper = new JsonMapper();
	private static List<String> TENANT_USER_FUNCTION_GROUP = Lists.newArrayList("CFG_MANAGE_FG", "CFG_MANAGE_FUN", "CFG_MANAGE_RG", "CFG_MANAGE_ROLE", "CFG_MANAGE_MENU", "CFG_MANAGE_SYSTEM_STAFF");

	@Transactional
	@Override
	public boolean isExistSystem(List<SearchFilter> filterList) {

		List<CloudSystem> list = cloudSystemDao.findListByFilter(filterList, null);

		if (CollectionUtils.isNotEmpty(list)) {
			return true;
		}

		return false;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void copyResources(String tenantId, String cloudSystemId) {
		Tenant tenant = tenantDao.findOne(tenantId);
		CloudSystem cloudSystem = cloudSystemDao.findOne(cloudSystemId);
		// 目前只有基础设施有模版的概念，暂不支持其他系统的拷贝
		if (tenant == null || cloudSystem == null || !cloudSystem.getSystemCode().equals(JCSS_SYS_CODE)) {
			return;
		}

		List<SearchFilter> searchFilters = Lists.newArrayList();
		SearchFilter filter = new SearchFilter("tenantId", SearchFilter.Operator.EQ, tenantId);
		SearchFilter filter1 = new SearchFilter("cloudSystemId", SearchFilter.Operator.EQ, cloudSystemId);
		searchFilters.add(filter);
		searchFilters.add(filter1);
		List<TenantSystemRelation> list = tenantSystemRelationDao.findListByFilter(searchFilters, null);

		// 如果关联表存在，未开通或者已经复制过，就不用再去复制资源了
		if (CollectionUtils.isNotEmpty(list) && (ManagementConstant.ENABLED_NO.equals(list.get(0).getEnabled()) || ManagementConstant.HAS_RESOURCE_YES.equals(list.get(0).getHasResource()))) {
			return;
		}

		/******************* 以下开始复制资源 *******************/
		String systemCode = cloudSystem.getSystemCode();
		String URL = cloudSystem.getWebsite() + ManagementConstant.URI_HEAD + "/" + systemCode.substring(systemCode.indexOf("_") + 1, systemCode.length()).toLowerCase() + "/"
				+ ManagementConstant.COPY_RESOURCE_URI;
		Map<String, Object> prams = Maps.newHashMap();
		prams.put(ManagementConstant.TENANT_ID_KEY, tenantId);

		String result = "";
		try {
			result = callRestService.callRest(URL, prams, ManagementConstant.METHOD_POST); // 调用云系统资源拷贝的rest服务
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("copyResources(), 调用云系统资源拷贝的rest服务时异常", e);
			throw new ServiceException("调用云系统资源拷贝的rest服务时异常");
		}

		if (StringUtils.isEmpty(result)) {
			return;
		}

		JsonMapper jm = new JsonMapper();
		RestResultDto map = jm.fromJson(result.toString(), RestResultDto.class);
		Integer obj = map.getResult();

		// 如果执行成功了，就修改TenantSystemRelation里面的复制标志hasResource=1
		if (obj != null && obj.equals(RestResultDto.RESULT_SUCC)) {
			TenantSystemRelation relation = list.get(0);
			relation.setHasResource(ManagementConstant.HAS_COPY_RESOURCE_YES);
			tenantSystemRelationDao.update(relation);
		}
	}

	@Override
	public void copyPrams(String tenantId) {
		if (StringUtils.isEmpty(tenantId)) {
			return;
		}

		long count = tenantPramSettingDao.getCntByTenantId(tenantId);
		if (count > 0) { // 如果已经拷贝过，直接返回
			return;
		}

		List<PramSetting> list = pramSettingDao.findAll();
		if (CollectionUtils.isEmpty(list)) {
			return;
		}

		for (PramSetting ps : list) {
			TenantPramSetting tps = new TenantPramSetting();
			tps.setTenantId(tenantId); // 租户id
			tps.setParmCode(ps.getParmCode()); // 代码值
			tps.setParmName(ps.getParmName()); // 代码显示名称
			tps.setTypeId(ps.getTypeId()); // 代码类型id
			tps.setOrderIndex(ps.getOrderIndex());//排序号
			tenantPramSettingDao.save(tps);
		}
	}

	@Override
	public void setTenantRootUser(TenantDto tenantDto) {
		// 获取角色记录
		String roleCode = ManagementConstant.TENANT_ROOT_ROLE;
		CloudRoleDto roleDto = cloudRoleService.getRoleByCode(roleCode);
		if (roleDto == null) {
			String msg = "角色code=[" + roleCode + "]记录不存在";
			logger.error(msg);
			throw new ServiceException(msg);
		}
		String roleId = roleDto.getId();

		// 创建人员记录
		CloudStaff staff = this.generateTenantRootStaff(tenantDto);
		String staffId = staff.getId();

		// 创建用户记录
		CloudUser user = this.generateUserForStaff(staffId, tenantDto.getUserName(), tenantDto.getPassword());
		String userId = user.getId();

		// 为用户分配角色
		this.generateUserRole(userId, roleId);
		// 为每个租户管理员添加管理员菜单
		Map<String, List<MenuTreeDto>> system_menu_map = Maps.newHashMap();
		addTenantMenu(tenantDto.getId(), system_menu_map);
		// 更新缓存数据
		syncSystemMenu(tenantDto.getId(), system_menu_map);
	}
	/**
	 * @Title: syncSystemMenu @Description: 更新缓存数据 @return void @throws
	 */
	private void syncSystemMenu(String tenantId, Map<String, List<MenuTreeDto>> system_menu_map) {
		if (StringUtil.isNullOrEmpty(tenantId)) {// 无租户信息，不同步
			return;
		}
		Set<String> mapKey_set = system_menu_map.keySet();
		Map<String, List<MenuTreeDto>> one_tenant_map = Maps.newHashMap();
		String base_key = ManagementConstant.REDIS_PRE_KEY_SYS_MENU + ManagementConstant.REDIS_SEPARATOR;
		// 获取缓存中关于系统菜单key的信息
		for (String key : mapKey_set) {
			if (key.indexOf(tenantId) != -1) {// 包含该租户信息
				one_tenant_map.put(key, system_menu_map.get(key));
			}
		}
		// 刪除原有数据
		List<String> rsm_list = centralCacheRedisService.getObject(base_key + tenantId, List.class);
		if (CollectionUtils.isNotEmpty(rsm_list)) {
			centralCacheRedisService.removeObjects(rsm_list);
		}
		if (MapUtils.isEmpty(one_tenant_map)) {// 无数据则继续，刪除key
			centralCacheRedisService.removeObject(base_key + tenantId);
			return;
		}
		// 更新缓存中关于系统菜单key的信息
		centralCacheRedisService.putObject(base_key + tenantId, Lists.newArrayList(one_tenant_map.keySet()));
		// 更新数据
		for (String key : one_tenant_map.keySet()) {
			centralCacheRedisService.putObject(key, jsonMapper.toJson(one_tenant_map.get(key)));
		}
	}

	/**
	 * @Title: getMenuList @Description: 根据系统id获取所有菜单 @return
	 *         List<CloudMenu> @throws
	 */
	private List<CloudMenu> getMenuList(List<String> systemIds) {
		List<CloudMenu> returnValue = Lists.newArrayList();
		if (CollectionUtils.isEmpty(systemIds)) {// 無系統信息，返回空
			return returnValue;
		}
		List<SearchFilter> searchFilters = Lists.newArrayList();
		Sort sort = new Sort(Direction.ASC, "orderIndex");
		searchFilters.add(
				new SearchFilter("systemId", Operator.IN, (String[]) systemIds.toArray(new String[systemIds.size()])));
		// 未隐藏的菜单
		searchFilters.add(new SearchFilter("isHidden", Operator.EQ, 0));
		return cloudMenuDao.findListByFilter(searchFilters, sort);
	}

	/**
	 * @Title: getTenantMap @Description: 获取功能信息 @return
	 *         Map<String,Tenant> @throws
	 */
	private Map<String, CloudSystemFunctionDto> getFunctionMap(List<CloudMenu> menu_list) {
		Map<String, CloudSystemFunctionDto> returnValue = Maps.newHashMap();
		if (CollectionUtils.isEmpty(menu_list)) {
			return returnValue;
		}
		List<String> functionId_list = Lists.newArrayList();
		for (CloudMenu entity : menu_list) {
			if (StringUtil.isNullOrEmpty(entity.getFunctionId())) {
				continue;
			}
			if (functionId_list.contains(entity.getFunctionId())) {
				continue;
			}
			functionId_list.add(entity.getFunctionId());
		}

		if (CollectionUtils.isEmpty(functionId_list)) {
			return returnValue;
		}
		List<CloudSystemFunctionDto> function_list = (List<CloudSystemFunctionDto>) cloudFunctionDao
				.getFunctionsByIds(StringUtils.join(functionId_list, ","));
		if (CollectionUtils.isEmpty(function_list)) {
			return returnValue;
		}
		for (CloudSystemFunctionDto entity : function_list) {// key:功能id,value:功能信息
			returnValue.put(entity.getId(), entity);
		}
		return returnValue;
	}

	/**
	 * @Title: addTenantMenu @Description: //为每个租户管理员添加管理员菜单 @return
	 *         void @throws
	 */
	private void addTenantMenu(String tenantId, Map<String, List<MenuTreeDto>> system_menu_map) {
		if (StringUtil.isNullOrEmpty(tenantId)) {
			return;
		}
		String roleCode = ManagementConstant.TENANT_ROOT_ROLE;

		List<CloudSystemDto> sys_list = cloudSystemDao.getCloudSystemByRoleCode(roleCode);
		if (CollectionUtils.isEmpty(sys_list)) {
			return;
		}
		String sys_id = sys_list.get(0).getId();
		String sys_code = sys_list.get(0).getSystemCode();
		// 根据系统id获取所有菜单
		List<CloudMenu> menu_list = getMenuList(Lists.newArrayList(sys_id));
		// 获取功能信息
		Map<String, CloudSystemFunctionDto> function_map = getFunctionMap(menu_list);
		List<MenuTreeDto> ts_menu_list = Lists.newArrayList();

		if (ManagementConstant.CLOUD_TENANT_ID.equals(tenantId)) {// 云平台租户不添加
			return;
		}
		String key = ManagementConstant.REDIS_PRE_SYS_MENU + ManagementConstant.REDIS_SEPARATOR + tenantId
				+ ManagementConstant.REDIS_SEPARATOR + sys_code;
		MenuTreeDto dto = null;
		for (CloudMenu entity : menu_list) {
			if (StringUtil.isNullOrEmpty(entity.getSystemId())) {// 菜单未绑定系统
				continue;
			}
			dto = new MenuTreeDto().transfer(entity);
			if (!StringUtil.isNullOrEmpty(dto.getFunctionId()) && function_map.containsKey(dto.getFunctionId())) {
				// 菜单uri
				dto.setUri(function_map.get(dto.getFunctionId()).getUrl());
			}
			if (!StringUtil.isNullOrEmpty(entity.getNodeCode())) {
				// 树形层级，根节点为0
				dto.setLevel(entity.getNodeCode().length() / 2);
			}
			ts_menu_list.add(dto);
		}
		system_menu_map.put(key, ts_menu_list);
	}
	/**
	 * 租户管理员：创建人员记录
	 * 
	 * @param tenant
	 * 
	 * @return
	 */
	private CloudStaff generateTenantRootStaff(TenantDto tenantDto) {
		CloudStaff staff = new CloudStaff();
		staff.setTenantId(tenantDto.getId());
		staff.setCode("role_is_" + ManagementConstant.TENANT_ROOT_ROLE);
		staff.setName(tenantDto.getTenantName() + "管理员");
		staff.setDescription("租户名称：" + tenantDto.getTenantName());
		staff.setOrderIndex(9999);

		return cloudStaffService.save(staff);
	}

	/**
	 * 创建用户记录
	 * 
	 * @param staffId
	 * @param userName
	 * @param password
	 * @return
	 */
	private CloudUser generateUserForStaff(String staffId, String userName, String password) {
		CloudUserDto user = new CloudUserDto();
		user.setStaffId(staffId);
		user.setUserName(userName);
		user.setPassword(password);

		return cloudUserService.save(user);
	}

	/**
	 * 用户分配角色
	 * 
	 * @param userId
	 * @param roleId
	 * @return
	 */
	private CloudUserRole generateUserRole(String userId, String roleId) {
		CloudUserRole userRole = new CloudUserRole();
		userRole.setUserId(userId);
		userRole.setRoleId(roleId);
		return cloudUserRoleService.save(userRole);
	}

	@Override
	public void setBusinessSysRootUser(Tenant tenant, CloudSystemDto systemDto) {
		// 创建人员记录
		CloudStaff staff = this.generateSysRootStaff(tenant, systemDto);
		String staffId = staff.getId();

		// 创建用户记录
		CloudUser user = this.generateUserForStaff(staffId, systemDto.getUserName(), systemDto.getPassword());
		String userId = user.getId();

		// 为用户分配角色，copy默认功能组、功能、菜单
		this.addDefaulSystemRoot(userId, systemDto.getId(), systemDto.getSystemCode());
	}

	/**
	 * 业务系统管理员：创建人员记录
	 * 
	 * @param tenant
	 * @param systemDto
	 * @return
	 */
	private CloudStaff generateSysRootStaff(Tenant tenant, CloudSystemDto systemDto) {
		CloudStaff staff = new CloudStaff();
		if (null == tenant) // 非业务系统
		{
			staff.setName(systemDto.getSystemName() + "管理员");
			staff.setDescription(" 系统名称：" + systemDto.getSystemName());

		} else { // 业务系统新增一个staff
			staff.setTenantId(tenant.getId());
			staff.setName(tenant.getTenantName() + "_" + systemDto.getSystemName() + "管理员");
			staff.setDescription("租户名称：" + tenant.getTenantName() + ", 业务系统名称：" + systemDto.getSystemName());
		}
		staff.setCode("role_is_" + ManagementConstant.SYSTEM_ROOT_ROLE);
		staff.setOrderIndex(9999); // 将管理员等排到最后
		return cloudStaffService.save(staff);
	}

	/**
	 * 为系统管理员用户添加系统管理员角色:复制模版角色及其功能、菜单等资源
	 * 
	 * @param userId
	 * @param systemId
	 */
	private void addDefaulSystemRoot(String userId, String systemId, String systemCode) {
		if (StringUtils.isEmpty(userId) || StringUtils.isEmpty(systemId)) {
			logger.error("复制系统管理员角色时，未传入用户id或系统id！");
			throw new ServiceException("复制系统管理员角色时，未传入用户id或系统id！");
		}

		CloudSystem oldsys = cloudSystemDao.getByCode(ManagementConstant.SYSTEM_CODE);
		if (oldsys == null) {
			logger.error("根据系统code[" + ManagementConstant.SYSTEM_CODE + "]未找到系统！");
			throw new ServiceException("根据系统code[" + ManagementConstant.SYSTEM_CODE + "]未找到系统！");
		}

		CloudRole oldRole = cloudRoleDao.getRoleBySystemIdAndRoleCode(oldsys.getId(), ManagementConstant.SYSTEM_ROOT_ROLE);
		if (oldRole == null) {
			logger.error("拷贝业务系统root角色时，根据code[" + ManagementConstant.SYSTEM_ROOT_ROLE + "]未找到角色！");
			throw new VortexException("根据系统code[" + ManagementConstant.SYSTEM_CODE + "]未找到系统！");
		}

		// 1.新建一个新角色组，新建一个新角色，将人绑定角色
		CloudRoleGroup group = new CloudRoleGroup();
		group.setCode(ManagementConstant.SYS_ROOT_ROLE_GROUP);
		group.setName("系统管理员角色组");
		group.setOrderIndex(1);
		group.setDescription("该角色组在新增系统时自动生成");
		group.setParentId("-1");
		group.setSystemId(systemId);
		group = this.cloudRoleGroupDao.saveAndFlush(group);

		CloudRole newRole = new CloudRole();
		BeanUtils.copyProperties(oldRole, newRole);
		newRole.setId(UUIDGenerator.getUUID());
		newRole.setSystemId(systemId);
		newRole.setGroupId(group.getId());
		newRole.setOrderIndex(1);
		newRole.setDescription("该角色在新增系统时自动生成");
		newRole.setRoleType(CloudRole.ROLE_TYPE_PRESET);
		newRole = this.cloudRoleDao.saveAndFlush(newRole);

		CloudUserRole ur = new CloudUserRole();
		ur.setRoleId(newRole.getId());
		ur.setUserId(userId);
		cloudUserRoleDao.save(ur);

		// 2.复制原角色的功能及其菜单
		/*
		 * List<CloudFunction> oldFunList =
		 * this.cloudFunctionDao.getFunctionListByRoleId(oldRole.getId()); if
		 * (CollectionUtils.isEmpty(oldFunList)) { return; }
		 */

		// 新建一个功能组
		CloudFunctionGroup funGroup = new CloudFunctionGroup();
		funGroup.setCode(ManagementConstant.SYS_ROOT_FUNCTION_GROUP);
		funGroup.setName("系统管理员功能组");
		funGroup.setDescription("该功能组在新增系统时自动生成");
		funGroup.setParentId("-1");
		funGroup.setOrderIndex(1);
		funGroup.setSystemId(systemId);
		funGroup.setNodeCode("01");
		funGroup = cloudFunctionGroupDao.saveAndFlush(funGroup);

		// 新建一个菜单（相当于菜单组）
		CloudMenu menuGroup = new CloudMenu();
		menuGroup.setParentId(CloudMenuTree.ROOT_NODE_ID);
		menuGroup.setSystemId(systemId);
		menuGroup.setCode(ManagementConstant.SYS_ROOT_MENU_GROUP);
		menuGroup.setName("系统管理");
		menuGroup.setOrderIndex(1);
		menuGroup.setDescription("该菜单在新增系统时自动生成");
		menuGroup.setIsHidden(CloudMenu.IS_WELCOME_MENU_NOT);
		menuGroup.setFunctionId(null);
		menuGroup.setPhotoIds(null);
		menuGroup.setIsControlled(CloudMenu.CONTROLLED_YES);
		menuGroup.setNodeCode("01");
		menuGroup = cloudMenuDao.saveAndFlush(menuGroup);

		// 获取需要拷贝的功能组
		List<SearchFilter> searchFilters = Lists.newArrayList();
		searchFilters.add(new SearchFilter("code", Operator.IN, TENANT_USER_FUNCTION_GROUP.toArray(new String[TENANT_USER_FUNCTION_GROUP.size()])));
		searchFilters.add(new SearchFilter("systemId", Operator.EQ, oldsys.getId()));
		List<CloudFunctionGroup> cloudFunctionGroups = cloudFunctionGroupDao.findListByFilter(searchFilters, null);

		if (CollectionUtils.isEmpty(cloudFunctionGroups)) {
			logger.error("预设的功能组被删除");
			throw new VortexException("预设的功能组被删除");
		}

		for (CloudFunctionGroup cloudFunctionGroup : cloudFunctionGroups) {
			copyChildren(cloudFunctionGroup, systemId,funGroup.getId(), menuGroup, newRole, systemCode);
		}

		/*
		 * for (CloudFunction oldFun : oldFunList) {
		 * 
		 * // 保存新功能 CloudFunction newFun = new CloudFunction();
		 * BeanUtils.copyProperties(oldFun, newFun);
		 * newFun.setId(UUIDGenerator.getUUID());
		 * newFun.setDescription("该功能在新增系统时自动生成");
		 * newFun.setGroupId(funGroup.getId()); newFun.setSystemId(systemId);
		 * newFun.setUri(this.replaceParam(newFun.getUri(),
		 * ManagementConstant.REQ_PARAM_SYSTEM_CODE, systemCode)); newFun =
		 * cloudFunctionDao.saveAndFlush(newFun);
		 * 
		 * // 新功能与新角色建立关联 CloudFunctionRole fr = new CloudFunctionRole();
		 * fr.setRoleId(newRole.getId()); fr.setFunctionId(newFun.getId()); fr =
		 * this.cloudFunctionRoleDao.saveAndFlush(fr);
		 * 
		 * // 得到原功能上面的所有菜单 List<CloudMenu> oldMenus =
		 * cloudMenuDao.getMenusByFunctionId(oldFun.getId()); if
		 * (CollectionUtils.isEmpty(oldMenus)) { continue; }
		 * 
		 * for (CloudMenu oldMenu : oldMenus) { CloudMenu newMenu = new
		 * CloudMenu(); BeanUtils.copyProperties(oldMenu, newMenu);
		 * newMenu.setId(UUIDGenerator.getUUID());
		 * newMenu.setSystemId(systemId);
		 * newMenu.setDescription("该功能在新增系统时自动生成");
		 * newMenu.setParentId(menuGroup.getId());
		 * newMenu.setIsHidden(CloudMenu.HIDDEN_NOT);
		 * newMenu.setFunctionId(newFun.getId());
		 * newMenu.setIsControlled(CloudMenu.CONTROLLED_YES); newMenu =
		 * cloudMenuDao.saveAndFlush(newMenu); } }
		 */
	}

	private void copyChildren(CloudFunctionGroup cloudFunctionGroup, String systemId,String parentId, CloudMenu menuGroup, CloudRole newRole, String systemCode) {
		if (cloudFunctionGroup == null) {
			return;
		}
		// 保存当前功能组
		CloudFunctionGroup funGroup = new CloudFunctionGroup();
		funGroup.setId(UUIDGenerator.getUUID());
		funGroup.setCode(cloudFunctionGroup.getCode());
		funGroup.setName(cloudFunctionGroup.getName());
		funGroup.setDescription("该功能组在新增系统时自动生成");
		funGroup.setParentId(parentId);
		funGroup.setOrderIndex(1);
		funGroup.setSystemId(systemId);
		
		funGroup = cloudFunctionGroupDao.saveAndFlush(funGroup);

		List<SearchFilter> searchFilters = Lists.newArrayList();
		searchFilters.add(new SearchFilter("parentId", Operator.EQ, cloudFunctionGroup.getId()));
		List<CloudFunctionGroup> children = cloudFunctionGroupDao.findListByFilter(searchFilters, null);
		List<SearchFilter> functionFilter = Lists.newArrayList();
		functionFilter.add(new SearchFilter("groupId", Operator.EQ, cloudFunctionGroup.getId()));
		List<CloudFunction> functionChildren = cloudFunctionDao.findListByFilter(functionFilter, null);
		if (CollectionUtils.isNotEmpty(functionChildren)) {
			for (CloudFunction oldFun : functionChildren) {

				// 保存新功能
				CloudFunction newFun = new CloudFunction();
				BeanUtils.copyProperties(oldFun, newFun);
				newFun.setId(UUIDGenerator.getUUID());
				newFun.setDescription("该功能在新增系统时自动生成");
				newFun.setGroupId(funGroup.getId());
				newFun.setSystemId(systemId);
				newFun.setUri(this.replaceParam(newFun.getUri(), ManagementConstant.REQ_PARAM_SYSTEM_CODE, systemCode));
				newFun = cloudFunctionDao.saveAndFlush(newFun);

			// 新功能与新角色建立关联
			CloudFunctionRole fr = new CloudFunctionRole();
			fr.setRoleId(newRole.getId());
			fr.setFunctionId(newFun.getId());
			fr = this.cloudFunctionRoleDao.saveAndFlush(fr);

			// 得到原功能上面的所有菜单
			List<CloudMenu> oldMenus = cloudMenuDao.getMenusByFunctionId(oldFun.getId());
			if (CollectionUtils.isEmpty(oldMenus)) {
				continue;
			}

			for (CloudMenu oldMenu : oldMenus) {
				CloudMenu newMenu = new CloudMenu();
				BeanUtils.copyProperties(oldMenu, newMenu);
				newMenu.setId(UUIDGenerator.getUUID());
				newMenu.setSystemId(systemId);
				newMenu.setDescription("该功能在新增系统时自动生成");
				newMenu.setParentId(menuGroup.getId());
				newMenu.setIsHidden(CloudMenu.HIDDEN_NOT);
				newMenu.setFunctionId(newFun.getId());
				newMenu.setIsControlled(CloudMenu.CONTROLLED_YES);
				newMenu = cloudMenuDao.saveAndFlush(newMenu);
			}
		}
	}

		if (CollectionUtils.isNotEmpty(children)) {
			for (CloudFunctionGroup cloudFunctionGroup2 : children) {
				copyChildren(cloudFunctionGroup2, systemId,funGroup.getId(),  menuGroup, newRole, systemCode);
			}
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
