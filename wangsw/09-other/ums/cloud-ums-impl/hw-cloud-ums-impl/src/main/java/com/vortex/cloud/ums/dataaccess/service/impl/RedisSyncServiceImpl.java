package com.vortex.cloud.ums.dataaccess.service.impl;


import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.JavaType;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.vortex.cloud.ums.dataaccess.dao.ICloudDepartmentDao;
import com.vortex.cloud.ums.dataaccess.dao.ICloudFunctionDao;
import com.vortex.cloud.ums.dataaccess.dao.ICloudOrganizationDao;
import com.vortex.cloud.ums.dataaccess.dao.ICloudStaffDao;
import com.vortex.cloud.ums.dataaccess.dao.ICloudUserDao;
import com.vortex.cloud.ums.dataaccess.service.ICentralCacheRedisService;
import com.vortex.cloud.ums.dataaccess.service.ICloudMenuService;
import com.vortex.cloud.ums.dataaccess.service.ICloudSystemService;
import com.vortex.cloud.ums.dataaccess.service.IRedisSyncService;
import com.vortex.cloud.ums.dataaccess.service.ITenantService;
import com.vortex.cloud.ums.dto.CloudSystemDto;
import com.vortex.cloud.ums.dto.CloudSystemFunctionDto;
import com.vortex.cloud.ums.dto.MenuTreeDto;
import com.vortex.cloud.ums.dto.TenantDeptOrgDto;
import com.vortex.cloud.ums.dto.android.CloudFunctionAndroidDto;
import com.vortex.cloud.ums.model.CloudMenu;
import com.vortex.cloud.ums.model.CloudStaff;
import com.vortex.cloud.ums.model.CloudSystem;
import com.vortex.cloud.ums.model.Tenant;
import com.vortex.cloud.ums.support.ManagementConstant;
import com.vortex.cloud.vfs.common.lang.Reflections;
import com.vortex.cloud.vfs.common.lang.StringUtil;
import com.vortex.cloud.vfs.common.mapper.JsonMapper;
import com.vortex.cloud.vfs.data.support.SearchFilter;
import com.vortex.cloud.vfs.data.support.SearchFilter.Operator;

@Service("redisSyncService")
@Transactional
public class RedisSyncServiceImpl implements IRedisSyncService {
	private static final Logger logger = LoggerFactory.getLogger(RedisSyncServiceImpl.class);

	private JsonMapper jsonMapper = new JsonMapper();
	@Resource
	private ICentralCacheRedisService centralCacheRedisService;
	@Resource
	private ITenantService tenantService;
	@Resource
	private ICloudUserDao cloudUserDao;
	@Resource
	private ICloudSystemService cloudSystemService;
	@Resource
	private ICloudMenuService cloudMenuService;
	@Resource
	private ICloudFunctionDao cloudFunctionDao;
	@Resource
	private ICloudDepartmentDao cloudDepartmentDao;
	@Resource
	private ICloudOrganizationDao cloudOrganizationDao;
	@Resource
	private ICloudStaffDao cloudStaffDao;

	@Override
	public void syncUserMenu() {
		// 查询出所有有效的人员
		long t0 = System.currentTimeMillis();
		List<String> userList = cloudUserDao.getAllUserIds();
		logger.error("[同步redis,菜单]，查询用户耗时：" + (System.currentTimeMillis() - t0) + "ms，数量：" + userList.size());

		if (CollectionUtils.isEmpty(userList)) {
			return;
		}
		t0 = System.currentTimeMillis();
		long t1 = 0l;
		long del = 0l;
		long search = 0l;
		long add = 0l;
		String redisKey = "";
		String redisValue = "";
		String redisKeyPre = null;
		List<String> codes = null;
		// 循环人员列表同步
		for (String userId : userList) {
			redisKeyPre = ManagementConstant.REDIS_PRE_MENU + ManagementConstant.REDIS_SEPARATOR + userId
					+ ManagementConstant.REDIS_SEPARATOR;
			t1 = System.currentTimeMillis();
			try {
				// 删除人员菜单
				centralCacheRedisService.removeObject(redisKeyPre + "*");
			} catch (Exception e) {
				logger.error("[同步redis,菜单]，[" + userId + "]，删除时失败！" + (new Date()).toString());
				e.printStackTrace();
			}
			del = System.currentTimeMillis() - t1;
			// 查询系统
			t1 = System.currentTimeMillis();
			codes = cloudUserDao.getBusinessSystemCodeList(userId);
			search = System.currentTimeMillis() - t1;

			t1 = System.currentTimeMillis();
			if (CollectionUtils.isNotEmpty(codes)) {
				// 循环同步人员菜单
				for (String syscode : codes) {
					try {
						MenuTreeDto dto = cloudMenuService.getMenuTree(syscode, userId);
						redisKey = redisKeyPre + syscode;
						redisValue = jsonMapper.toJson(dto);
						centralCacheRedisService.putObject(redisKey, redisValue);
					} catch (Exception e) {
						logger.error(
								"[同步redis,菜单]，[" + userId + "]，系统编号[" + syscode + "]，添加时失败！" + (new Date()).toString());
						e.printStackTrace();
					}
				}
			}
			add = System.currentTimeMillis() - t1;
			logger.error("[同步redis,菜单]，[用户id：" + userId + "]，刪除耗时：" + del + "ms，查询耗时：" + search + "ms，添加耗时：" + add
					+ "ms，数量：" + codes.size());
		}
		logger.error("[同步redis,菜单]，耗时：" + (System.currentTimeMillis() - t0) + "ms，用户数量：" + userList.size());
	}

	@Override
	public void syncUserFunction() {
		// 查询出所有有效的人员
		long t0 = System.currentTimeMillis();
		List<String> userList = cloudUserDao.getAllUserIds();
		logger.error("[同步redis,功能]，查询用户耗时：" + (System.currentTimeMillis() - t0) + "ms，数量：" + userList.size());
		if (CollectionUtils.isEmpty(userList)) {
			return;
		}
		String base_key = ManagementConstant.REDIS_PRE_FUNCTION + ManagementConstant.REDIS_SEPARATOR;
		String redisKey = "";
		String redisValue = "";
		List<String> allFunctions = null;
		t0 = System.currentTimeMillis();
		long t1 = 0l;
		long del = 0l;
		long search = 0l;
		long add = 0l;
		// 循环人员列表同步功能列表
		for (String userId : userList) {
			redisKey = base_key + userId;
			t1 = System.currentTimeMillis();
			try {
				// 删除人员所有功能
				centralCacheRedisService.removeObject(redisKey);
			} catch (Exception e) {
				logger.error("[同步redis,功能]，[" + userId + "]，刪除时失败！" + (new Date()).toString());
				e.printStackTrace();
			}
			del = System.currentTimeMillis() - t1;
			// 得到人员的功能
			t1 = System.currentTimeMillis();
			allFunctions = cloudFunctionDao.getAllFunctions(userId);
			search = System.currentTimeMillis() - t1;

			t1 = System.currentTimeMillis();
			if (CollectionUtils.isNotEmpty(allFunctions)) {
				redisValue = StringUtils.join(allFunctions, ",");
				// 将人员的功能封装进redis同步列表
				try {
					// 同步人员功能列表
					centralCacheRedisService.putObject(redisKey, redisValue);
				} catch (Exception e) {
					logger.error("[同步redis,功能]，[" + userId + "]，添加时失败！" + (new Date()).toString());
					e.printStackTrace();
				}
			}
			add = System.currentTimeMillis() - t1;
			logger.error("[同步redis,功能]，[用户id：" + userId + "]，刪除耗时：" + del + "ms，查询耗时：" + search + "ms，添加耗时：" + add
					+ "ms，数量：" + allFunctions.size());
		}
		logger.error("[同步redis,功能]，耗时：" + (System.currentTimeMillis() - t0) + "ms，用户数量：" + userList.size());
	}

	@Override
	public void syncUserAuthorityByTenant(List<String> tenantIds) {
		long t0 = System.currentTimeMillis();
		// 获取租户信息
		List<String> tenantId_list = getTenantIdList(tenantIds);
		List<String> userId_list = null;
		Map<String, List<MenuTreeDto>> one_tenant_menu = null;
		Map<String, String> user_function_map = null;
		Integer log_userNum = 0;
		for (String tenantId : tenantId_list) {
			// 获取一个租户下的所有租户
			userId_list = getUserIdsByTenantId(tenantId);

			if (CollectionUtils.isEmpty(userId_list)) {// 无用户，则继续
				continue;
			}
			// 统计用户数量
			log_userNum = log_userNum + userId_list.size();

			// 获取一个租户下的所有系统菜单(key:系统编号,value:[菜单])
			one_tenant_menu = getRedisSysMenu(tenantId);

			for (String userId : userId_list) {
				// 获取一个用户的所有功能码及id(key:id,value:code)
				user_function_map = getUserFunctionMap(userId);
				if (MapUtils.isEmpty(user_function_map)) {// 无权限，继续
					continue;
				}
				// 同步功能码
				syncUserFunctionCode(userId, user_function_map);

				if (MapUtils.isEmpty(one_tenant_menu)) {// 无菜单，则继续
					continue;
				}
				// 同步菜单
				syncUserMenuTree(userId, user_function_map, one_tenant_menu);
			}
		}
		logger.error("[同步redis,初始化用户功能码及菜单]，总耗时：" + (System.currentTimeMillis() - t0) + "ms，租户数量："
				+ tenantId_list.size() + "，用户数量：" + log_userNum);
	}

	@Override
	public void syncSystemMenuByTenant(List<String> tenantIds) {
		long t0 = System.currentTimeMillis();

		long t1 = System.currentTimeMillis();
		// 获取租户信息
		List<String> tenantId_list = getTenantIdList(tenantIds);
		long t_tenantId = System.currentTimeMillis() - t1;

		t1 = System.currentTimeMillis();
		// 根据租户id获取所有系统信息
		Map<String, CloudSystem> system_map = getSystemMap(tenantId_list);
		long t_system = System.currentTimeMillis() - t1;

		t1 = System.currentTimeMillis();
		// 根据系统id获取所有菜单
		List<CloudMenu> menu_list = getMenuList(Lists.newArrayList(system_map.keySet()));
		long t_menu = System.currentTimeMillis() - t1;

		t1 = System.currentTimeMillis();
		// 获取功能信息
		Map<String, CloudSystemFunctionDto> function_map = getFunctionMap(menu_list);
		long t_function = System.currentTimeMillis() - t1;

		t1 = System.currentTimeMillis();
		// 遍历菜单，组合新的数据 key:租户id_系统编号,value:[菜单]
		Map<String, List<MenuTreeDto>> system_menu_map = formatMenuInfo(menu_list, system_map, function_map);
		long t_format = System.currentTimeMillis() - t1;

		t1 = System.currentTimeMillis();
		// 为每个租户管理员添加管理员菜单
		addTenantMenu(tenantId_list, system_menu_map);
		long t_tenant_menu = System.currentTimeMillis() - t1;

		t1 = System.currentTimeMillis();
		// 更新缓存数据
		syncSystemMenu(tenantId_list, system_menu_map);
		long t_save = System.currentTimeMillis() - t1;

		// 统计菜单数量
		Integer log_menuNum = 0;
		for (String set_key : system_menu_map.keySet()) {
			log_menuNum = log_menuNum + system_menu_map.get(set_key).size();
		}
		logger.error(String.format(
				"[同步redis,系统完整菜单]，总耗时：%sms，租户数量：%s，系统数量：%s，菜单数量：%s，获取租户耗时：%sms，获取所有系统耗时：%sms，获取所有菜单耗时：%sms，获取对应功能耗时：%sms，封装菜单耗时：%sms，添加租户管理员菜单耗时：%sms，存入缓存耗时：%sms",
				(System.currentTimeMillis() - t0), tenantId_list.size(), system_menu_map.keySet().size(), log_menuNum,
				t_tenantId, t_system, t_menu, t_function, t_format, t_tenant_menu, t_save));
	}

	@Override
	public void syncDeptOrgByTenant(List<String> tenantIds) {
		long t0 = System.currentTimeMillis();
		long t1 = System.currentTimeMillis();
		// 获取租户信息
		List<String> tenantId_list = getTenantIdList(tenantIds);
		long t_tenantId = System.currentTimeMillis() - t1;
		// 根据租户id获取所有部门机构信息
		List<TenantDeptOrgDto> list = null;
		List<TenantDeptOrgDto> total_list = Lists.newArrayList();
		for (String tenantId : tenantId_list) {
			list = cloudDepartmentDao.findDeptOrgList(tenantId, null, null);
			// 通过机构部门
			syncTenantDeptOrg(tenantId, list);
			if (CollectionUtils.isNotEmpty(list)) {
				total_list.addAll(list);
			}
		}
		t1 = System.currentTimeMillis();
		syncTotalDeptOrg(total_list);
		long t_sync = System.currentTimeMillis() - t1;
		logger.error(String.format("[同步redis,完整机构部门]，总耗时：%sms，存入redis%sms，租户数量：%s，机构部门数量：%s，获取租户耗时：%sms",
				(System.currentTimeMillis() - t0), t_sync, tenantId_list.size(), total_list.size(), t_tenantId));
	}

	@Override
	public void syncStaffByTenant(List<String> tenantIds) {
		long t0 = System.currentTimeMillis();
		long t1 = System.currentTimeMillis();
		// 获取租户信息
		List<String> tenantId_list = getTenantIdList(tenantIds);
		long t_tenantId = System.currentTimeMillis() - t1;

		// 根据租户id获取所有人员信息
		List<CloudStaff> list = null;
		List<SearchFilter> searchFilters = null;
		List<CloudStaff> total_list = Lists.newArrayList();
		for (String tenantId : tenantId_list) {
			searchFilters = Lists.newArrayList();
			searchFilters.add(new SearchFilter("tenantId", Operator.EQ, tenantId));
			list = cloudStaffDao.findListByFilter(searchFilters, null);
			// 同步人员信息
			syncTenantStaff(tenantId, list);
			if (CollectionUtils.isNotEmpty(list)) {
				total_list.addAll(list);
			}
		}
		t1 = System.currentTimeMillis();
		syncTotalStaff(total_list);
		long t_sync = System.currentTimeMillis() - t1;
		logger.error(String.format("[同步redis,完整人员]，总耗时：%sms，存入redis%sms，租户数量：%s，人员数量：%s，获取租户耗时：%sms",
				(System.currentTimeMillis() - t0), t_sync, tenantId_list.size(), total_list.size(), t_tenantId));
	}

	/**
	 * @Title: addTenantMenu @Description: //为每个租户管理员添加管理员菜单 @return
	 *         void @throws
	 */
	private void addTenantMenu(List<String> tenantIds, Map<String, List<MenuTreeDto>> system_menu_map) {
		if (CollectionUtils.isEmpty(tenantIds)) {
			return;
		}
		String roleCode = ManagementConstant.TENANT_ROOT_ROLE;

		List<CloudSystemDto> sys_list = cloudSystemService.getCloudSystemByRoleCode(roleCode);
		if (CollectionUtils.isEmpty(sys_list)) {
			return;
		}
		String sys_id = sys_list.get(0).getId();
		String sys_code = sys_list.get(0).getSystemCode();
		// 根据系统id获取所有菜单
		List<CloudMenu> menu_list = getMenuList(Lists.newArrayList(sys_id));
		// 获取功能信息
		Map<String, CloudSystemFunctionDto> function_map = getFunctionMap(menu_list);
		List<MenuTreeDto> ts_menu_list = null;
		String key = null;
		MenuTreeDto dto = null;
		for (String tenantId : tenantIds) {
			if (ManagementConstant.CLOUD_TENANT_ID.equals(tenantId)) {// 云平台租户不添加
				continue;
			}
			// 系统
			key = ManagementConstant.REDIS_PRE_SYS_MENU + ManagementConstant.REDIS_SEPARATOR + tenantId
					+ ManagementConstant.REDIS_SEPARATOR + sys_code;
			ts_menu_list = Lists.newArrayList();
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
	}

	/**
	 * @Title: getTenantMap @Description: 获取租户信息 @return
	 *         Map<String,Tenant> @throws
	 */
	private List<String> getTenantIdList(List<String> tenantIds) {
		List<String> returnValue = Lists.newArrayList();
		List<SearchFilter> searchFilters = Lists.newArrayList();
		if (CollectionUtils.isNotEmpty(tenantIds)) {
			searchFilters.add(
					new SearchFilter("id", Operator.IN, (String[]) tenantIds.toArray(new String[tenantIds.size()])));
		} else {// 跑全部数据，则默认加入云平台租户id
			tenantIds = Lists.newArrayList();
			tenantIds.add(ManagementConstant.CLOUD_TENANT_ID);
		}
		List<Tenant> tenant_list = tenantService.findListByFilter(searchFilters, null);
		if (CollectionUtils.isEmpty(tenant_list) && !tenantIds.contains(ManagementConstant.CLOUD_TENANT_ID)) {// 未查到数据，并且不包含云平台租户id，返回空
			return returnValue;
		}

		for (Tenant entity : tenant_list) {// key:租户id,value:租户信息
			returnValue.add(entity.getId());
		}
		// 添加云平台租户（租户id为空）
		if (tenantIds.contains(ManagementConstant.CLOUD_TENANT_ID)) {
			returnValue.add(ManagementConstant.CLOUD_TENANT_ID);
		}
		return returnValue;
	}

	/**
	 * @Title: getSystemMap @Description: 根据租户id获取所有系统信息 @return
	 *         Map<String,CloudSystem> @throws
	 */
	private Map<String, CloudSystem> getSystemMap(List<String> tenantIds) {
		Map<String, CloudSystem> returnValue = Maps.newHashMap();
		if (CollectionUtils.isEmpty(tenantIds)) {// 無租戶信息，返回空
			return returnValue;
		}
		List<SearchFilter> searchFilters = Lists.newArrayList();
		searchFilters.add(
				new SearchFilter("tenantId", Operator.IN, (String[]) tenantIds.toArray(new String[tenantIds.size()])));
		List<CloudSystem> system_list = cloudSystemService.findListByFilter(searchFilters, null);

		if (tenantIds.contains(ManagementConstant.CLOUD_TENANT_ID)) {// 若包含云平台租户id，则添加云平台租户系统
			searchFilters.clear();
			searchFilters.add(new SearchFilter("tenantId", Operator.NULL, ManagementConstant.CLOUD_TENANT_ID));
			system_list.addAll(cloudSystemService.findListByFilter(searchFilters, null));
		}
		if (CollectionUtils.isEmpty(system_list)) {// 未查到数据，返回空
			return returnValue;
		}
		for (CloudSystem entity : system_list) {// key:系统id,value:系统信息
			returnValue.put(entity.getId(), entity);
		}
		return returnValue;
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
		return cloudMenuService.findListByFilter(searchFilters, sort);
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
	 * @Title: formatMenuInfo @Description: 遍历菜单，组合新的数据
	 *         key:租户id_系统编号,value:[菜单] @return
	 *         Map<String,List<CloudMenu>> @throws
	 */
	private Map<String, List<MenuTreeDto>> formatMenuInfo(List<CloudMenu> menu_list,
			Map<String, CloudSystem> system_map, Map<String, CloudSystemFunctionDto> function_map) {
		Map<String, List<MenuTreeDto>> returnValue = Maps.newHashMap();
		if (CollectionUtils.isEmpty(menu_list) || MapUtils.isEmpty(system_map)) {// 無菜单信息或系统信息，返回空
			return returnValue;
		}
		List<MenuTreeDto> ts_menu_list = null;
		CloudSystem sys = null;
		String key = null;
		String tenantId = null;
		MenuTreeDto dto = null;
		for (CloudMenu entity : menu_list) {
			if (StringUtil.isNullOrEmpty(entity.getSystemId())) {// 菜单未绑定系统
				continue;
			}
			if (!system_map.containsKey(entity.getSystemId())) {// 无指定系统信息
				continue;
			}
			sys = system_map.get(entity.getSystemId());
			tenantId = sys.getTenantId();
			if (StringUtil.isNullOrEmpty(tenantId)) {// 若无租户id，则为云平台租户id
				tenantId = ManagementConstant.CLOUD_TENANT_ID;
			}
			// 系统
			key = ManagementConstant.REDIS_PRE_SYS_MENU + ManagementConstant.REDIS_SEPARATOR + tenantId
					+ ManagementConstant.REDIS_SEPARATOR + sys.getSystemCode();
			if (!returnValue.containsKey(key)) {// 未找到key，则新建
				ts_menu_list = Lists.newArrayList();
			} else {
				ts_menu_list = returnValue.get(key);
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
			returnValue.put(key, ts_menu_list);
		}
		return returnValue;
	}

	/**
	 * @Title: getUserIdsByTenantId @Description: 获取一个租户下所有用户id @return
	 *         List<String> @throws
	 */
	private List<String> getUserIdsByTenantId(String tenantId) {
		List<String> returnValue = Lists.newArrayList();
		if (StringUtil.isNullOrEmpty(tenantId)) {
			return returnValue;
		}
		if (ManagementConstant.CLOUD_TENANT_ID.equals(tenantId)) {// 若是云平台租户，返回云平台租户信息
			return cloudUserDao.findNoTenantUserIdList();
		}
		return cloudUserDao.findIdListByTenantId(tenantId);
	}

	/**
	 * @Title: getRedisSysMenu @Description:
	 *         获取一个租户下所有系统的菜单(key:系统编号,value:[菜单]) @return
	 *         Map<String,List<CloudMenu>> @throws
	 */
	private Map<String, List<MenuTreeDto>> getRedisSysMenu(String tenantId) {
		Map<String, List<MenuTreeDto>> returnValue = Maps.newHashMap();
		String base_key = ManagementConstant.REDIS_PRE_KEY_SYS_MENU + ManagementConstant.REDIS_SEPARATOR + tenantId;
		String base_value_key = ManagementConstant.REDIS_PRE_SYS_MENU + ManagementConstant.REDIS_SEPARATOR + tenantId
				+ ManagementConstant.REDIS_SEPARATOR;
		// 获取缓存中关于系统菜单key的信息
		List<String> rsm_list = centralCacheRedisService.getObject(base_key, List.class);
		if (CollectionUtils.isEmpty(rsm_list)) {
			return returnValue;
		}
		JavaType javaType = jsonMapper.contructCollectionType(List.class, MenuTreeDto.class);
		String key = null;
		for (String redis_key : rsm_list) {// 获取信息
			key = redis_key.replace(base_value_key, "");// 去除多余信息，获取系统编号
			returnValue.put(key, (List<MenuTreeDto>) jsonMapper
					.fromJson(centralCacheRedisService.getObject(redis_key, String.class), javaType));
		}
		return returnValue;
	}

	/**
	 * @Title: getUserFunctionMap @Description:
	 *         获取一个用户户下所有功能(key:id,value:code) @return
	 *         Map<String,String> @throws
	 */
	private Map<String, String> getUserFunctionMap(String userId) {
		Map<String, String> returnValue = Maps.newHashMap();
		List<CloudFunctionAndroidDto> list = cloudFunctionDao.getFunctionsByUserId(userId);
		if (CollectionUtils.isEmpty(list)) {
			return returnValue;
		}
		for (CloudFunctionAndroidDto entity : list) {
			returnValue.put(entity.getId(), entity.getCode());
		}
		return returnValue;
	}

	private static final String ROOT_ID = "-1"; // 根节点id

	private List<MenuTreeDto> clonePosList(List<MenuTreeDto> posList) {
		List<MenuTreeDto> list = Lists.newArrayList();
		if (CollectionUtils.isEmpty(posList)) {
			return list;
		}
		for (MenuTreeDto pos : posList) {
			list.add(shallowClone(pos));
		}
		return list;
	}

	@SuppressWarnings("unchecked")
	private <T extends Cloneable> T shallowClone(T object) {
		String methodName = "clone";
		Method method = Reflections.getAccessibleMethod(object, methodName);
		if (method == null) {
			try {
				method = Object.class.getDeclaredMethod(methodName);
				Reflections.makeAccessible(method);
			} catch (Exception e) {
				logger.error(null, e);
			}
		}
		try {
			return (T) method.invoke(object);
		} catch (Exception e) {
			logger.error(null, e);
			return null;
		}
	}

	private MenuTreeDto formatMenuTree(List<MenuTreeDto> menu_list, List<String> functionId_list) {
		// 递归完毕后，返回根节点
		if (CollectionUtils.isEmpty(menu_list) || CollectionUtils.isEmpty(functionId_list)) {
			return null;
		}
		// 拷贝一份
		List<MenuTreeDto> my_list = clonePosList(menu_list);

		Map<String, MenuTreeDto> nodeMap = Maps.newHashMap();
		List<MenuTreeDto> list = Lists.newArrayList();
		for (MenuTreeDto entity : my_list) {
			// 是否叶子节点1：是，0：否
			entity.setIsLeaf(1);
			// 清除功能id信息
			nodeMap.put(entity.getId(), entity);
			// 是否受控制，默认1-受控，0-不受控；不受控的菜单，所有人都可以访问
			if (null == entity.getIsControlled()) {
				continue;
			}
			if (1 == entity.getIsControlled()) {// 受控
				if (StringUtil.isNullOrEmpty(entity.getFunctionId())
						|| !functionId_list.contains(entity.getFunctionId())) {// 无功能权限
					continue;
				}
			}
			entity.setFunctionId(null);
			list.add(entity);
		}
		if (CollectionUtils.isEmpty(list)) {// 无有功能的节点，返回
			return null;
		}
		int maxLevel = 0; // nodeCode树形字段的最大长度，最深level的叶节点所在
		for (MenuTreeDto dto : list) {
			if (dto.getLevel() > maxLevel) {
				maxLevel = dto.getLevel();
			}
		}
		if (maxLevel == 0) { // 最大深度为0，直接返回空
			return null;
		}

		Map<String, List<MenuTreeDto>> treeMap = Maps.newLinkedHashMap();

		// 组合树形结构
		doFil(list, maxLevel, treeMap, nodeMap);
		MenuTreeDto rst = new MenuTreeDto();
		rst.setName("根节点");
		rst.setDescription("");
		rst.setId(ROOT_ID);
		rst.setCode("");
		rst.setParentId("");
		rst.setPhotoIds("");
		rst.setLevel(0);
		rst.setIsLeaf(0);
		rst.setChildren(treeMap.get(ROOT_ID));
		return rst;
	}

	private void doFil(List<MenuTreeDto> list, int level, Map<String, List<MenuTreeDto>> treeMap,
			Map<String, MenuTreeDto> nodeMap) {
		if (level == 0) {
			return;
		}
		// 得到当前层级的菜单列表
		Map<String, List<MenuTreeDto>> map = Maps.newLinkedHashMap();
		List<MenuTreeDto> parentList = Lists.newArrayList();
		MenuTreeDto tree = null;
		List<MenuTreeDto> mlist = null;
		for (MenuTreeDto dto : list) {
			if (dto.getLevel().intValue() == level) {
				tree = new MenuTreeDto();
				BeanUtils.copyProperties(dto, tree);
				tree.setChildren(treeMap.get(tree.getId())); // 将上次封装好的子节点列表设置好
				// 封装上层需要用到的treeMap
				if (map.get(tree.getParentId()) == null) {
					mlist = Lists.newArrayList();
					mlist.add(tree);
					map.put(tree.getParentId(), mlist);

					if (level != 1) {
						// map中未找到列表，代表是第一次用到该父节点，需要加载父节点到全体节点列表list中
						MenuTreeDto parent = nodeMap.get(tree.getParentId());
						if (null != parent) {
							// 是否叶子节点1：是，0：否
							parent.setFunctionId(null);
							parent.setIsLeaf(0);
							parentList.add(parent);
						}
					}
				} else {
					map.get(tree.getParentId()).add(tree);
				}
			}
		}

		// 将父节点加入集合
		list.addAll(parentList);

		// 将map中list排序
		Iterator<String> it = map.keySet().iterator();
		while (it.hasNext()) {
			String key = it.next();
			Collections.sort(map.get(key));
		}

		treeMap.clear();
		treeMap.putAll(map);

		// 级别调至上一层
		level--;

		// 递归调用
		this.doFil(list, level, treeMap, nodeMap);
		return;
	}

	/**
	 * @Title: syncSystemMenu @Description: 更新缓存数据 @return void @throws
	 */
	private void syncSystemMenu(List<String> tenantIds, Map<String, List<MenuTreeDto>> system_menu_map) {
		if (CollectionUtils.isEmpty(tenantIds)) {// 无租户信息，不同步
			return;
		}
		Set<String> mapKey_set = system_menu_map.keySet();
		Map<String, List<MenuTreeDto>> one_tenant_map = null;
		List<String> rsm_list = null;
		String base_key = ManagementConstant.REDIS_PRE_KEY_SYS_MENU + ManagementConstant.REDIS_SEPARATOR;
		for (String tenantId : tenantIds) {
			one_tenant_map = Maps.newHashMap();
			// 获取缓存中关于系统菜单key的信息
			for (String key : mapKey_set) {
				if (key.indexOf(tenantId) != -1) {// 包含该租户信息
					one_tenant_map.put(key, system_menu_map.get(key));
				}
			}
			// 刪除原有数据
			rsm_list = centralCacheRedisService.getObject(base_key + tenantId, List.class);
			if (CollectionUtils.isNotEmpty(rsm_list)) {
				centralCacheRedisService.removeObjects(rsm_list);
			}
			if (MapUtils.isEmpty(one_tenant_map)) {// 无数据则继续，刪除key
				centralCacheRedisService.removeObject(base_key + tenantId);
				continue;
			}
			// 更新缓存中关于系统菜单key的信息
			centralCacheRedisService.putObject(base_key + tenantId, Lists.newArrayList(one_tenant_map.keySet()));
			// 更新数据
			for (String key : one_tenant_map.keySet()) {
				centralCacheRedisService.putObject(key, jsonMapper.toJson(one_tenant_map.get(key)));
			}
		}
	}

	/**
	 * @Title: syncUserFunctionCode @Description: 同步用户功能码 @return void @throws
	 */
	private void syncUserFunctionCode(String userId, Map<String, String> function_map) {
		if (StringUtil.isNullOrEmpty(userId)) {// 无数据返回
			return;
		}
		// 删除原有功能码
		String redisKey = ManagementConstant.REDIS_PRE_FUNCTION + ManagementConstant.REDIS_SEPARATOR + userId;
		List<String> functionCodes = Lists.newArrayList();
		for (String value : function_map.values()) {
			if (StringUtil.isNullOrEmpty(value)) {// 判断是否为空
				continue;
			}
			if (functionCodes.contains(value)) {// 去除重复功能码
				continue;
			}
			functionCodes.add(value);
		}
		if (CollectionUtils.isEmpty(functionCodes)) {
			centralCacheRedisService.removeObject(redisKey);
			return;
		}
		// 同步人员功能列表，以","连接
		centralCacheRedisService.putObject(redisKey, StringUtils.join(functionCodes, ","));
	}

	/**
	 * @Title: syncUserMenuTree @Description: 同步用户菜单 @return void @throws
	 */
	private void syncUserMenuTree(String userId, Map<String, String> function_map,
			Map<String, List<MenuTreeDto>> menu_map) {
		if (StringUtil.isNullOrEmpty(userId)) {// 无数据返回
			return;
		}
		String base_key = ManagementConstant.REDIS_PRE_KEY_USER_MENU + ManagementConstant.REDIS_SEPARATOR + userId;
		String base_value_key = ManagementConstant.REDIS_PRE_MENU + ManagementConstant.REDIS_SEPARATOR + userId
				+ ManagementConstant.REDIS_SEPARATOR;
		// 获取缓存中关于系统菜单key的信息
		List<String> exist_list = centralCacheRedisService.getObject(base_key, List.class);
		List<String> redisKey_list = Lists.newArrayList();
		String redis_key = null;
		MenuTreeDto tree = null;
		List<MenuTreeDto> menu_list = null;
		for (String systemCode : menu_map.keySet()) {
			redis_key = base_value_key + systemCode;
			menu_list = menu_map.get(systemCode);
			if (CollectionUtils.isEmpty(menu_list)) {
				continue;
			}
			tree = formatMenuTree(menu_list, Lists.newArrayList(function_map.keySet()));
			if (null == tree) {
				continue;
			}
			// 更新数据
			centralCacheRedisService.putObject(redis_key, jsonMapper.toJson(tree));
			redisKey_list.add(redis_key);
			if (CollectionUtils.isNotEmpty(exist_list) && exist_list.contains(redis_key)) {// 获取需要删除的缓存
				exist_list.remove(redis_key);
			}
		}
		if (CollectionUtils.isNotEmpty(redisKey_list)) {// 更新缓存中关于系统菜单key的信息
			centralCacheRedisService.putObject(base_key, redisKey_list);
		} else {// 无数据，删除缓存
			centralCacheRedisService.removeObject(base_key);
		}
		if (CollectionUtils.isNotEmpty(exist_list)) {// 删除垃圾数据
			centralCacheRedisService.removeObjects(exist_list);
		}
	}

	/**
	 * @Title: syncTenantDeptOrg @Description: 同步租户机构部门 @return void @throws
	 */
	private void syncTenantDeptOrg(String tenantId, List<TenantDeptOrgDto> list) {
		if (StringUtil.isNullOrEmpty(tenantId)) {// 无数据返回
			return;
		}
		// key前缀
		String redisKey = ManagementConstant.REDIS_PRE_TENANT_DEPTORGIDS + ManagementConstant.REDIS_SEPARATOR
				+ tenantId;
		// 获取缓存中关于机构部门key的信息
		List<String> exist_id_list = centralCacheRedisService.getObject(redisKey, List.class);
		List<String> id_list = Lists.newArrayList();
		if (CollectionUtils.isNotEmpty(list)) {
			for (TenantDeptOrgDto entity : list) {
				id_list.add(entity.getId());
				if (CollectionUtils.isNotEmpty(exist_id_list) && exist_id_list.contains(entity.getId())) {// 获取需要删除的缓存
					exist_id_list.remove(entity.getId());
				}
			}
			centralCacheRedisService.putObject(redisKey, id_list);
		} else {// 该租户无数据，删除缓存
			centralCacheRedisService.removeObject(redisKey);
		}
		if (CollectionUtils.isNotEmpty(exist_id_list)) {// 删除垃圾数据
			for (String id : exist_id_list) {
				centralCacheRedisService.updateMapField(ManagementConstant.REDIS_PRE_MAP_DEPTORG, id, null);
			}
		}
	}

	/**
	 * @Title: syncTenantStaff @Description: 同步租户人员 @return void @throws
	 */
	private void syncTenantStaff(String tenantId, List<CloudStaff> list) {
		if (StringUtil.isNullOrEmpty(tenantId)) {// 无数据返回
			return;
		}
		// key前缀
		String redisKey = ManagementConstant.REDIS_PRE_TENANT_STAFFIDS + ManagementConstant.REDIS_SEPARATOR + tenantId;
		// 获取缓存中关于人员key的信息
		List<String> exist_id_list = centralCacheRedisService.getObject(redisKey, List.class);
		List<String> id_list = Lists.newArrayList();
		if (CollectionUtils.isNotEmpty(list)) {
			for (CloudStaff entity : list) {
				id_list.add(entity.getId());
				if (CollectionUtils.isNotEmpty(exist_id_list) && exist_id_list.contains(entity.getId())) {// 获取需要删除的缓存
					exist_id_list.remove(entity.getId());
				}
			}
			centralCacheRedisService.putObject(redisKey, id_list);
		} else {// 该租户无数据，删除缓存
			centralCacheRedisService.removeObject(redisKey);
		}
		if (CollectionUtils.isNotEmpty(exist_id_list)) {// 删除垃圾数据
			for (String id : exist_id_list) {
				centralCacheRedisService.updateMapField(ManagementConstant.REDIS_PRE_MAP_STAFF, id, null);
			}
		}
	}

	private void syncTotalDeptOrg(List<TenantDeptOrgDto> list) {
		if (CollectionUtils.isEmpty(list)) {// 无数据返回
			return;
		}
		Map<String, TenantDeptOrgDto> mapValue = Maps.newHashMap();
		for (TenantDeptOrgDto entity : list) {
			mapValue.put(entity.getId(), entity);
		}
		centralCacheRedisService.updateMapFields(ManagementConstant.REDIS_PRE_MAP_DEPTORG, mapValue);
	}

	private void syncTotalStaff(List<CloudStaff> list) {
		if (CollectionUtils.isEmpty(list)) {// 无数据返回
			return;
		}
		Map<String, CloudStaff> mapValue = Maps.newHashMap();
		for (CloudStaff entity : list) {
			mapValue.put(entity.getId(), entity);
		}
		centralCacheRedisService.updateMapFields(ManagementConstant.REDIS_PRE_MAP_STAFF, mapValue);
	}
}
