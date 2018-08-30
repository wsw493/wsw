package com.vortex.cloud.ums.dataaccess.service.impl;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.vortex.cloud.ums.dataaccess.service.ICentralCacheRedisService;
import com.vortex.cloud.ums.dataaccess.service.ICloudUserService;
import com.vortex.cloud.ums.dataaccess.service.IRedisValidateService;
import com.vortex.cloud.ums.dto.TenantDeptOrgDto;
import com.vortex.cloud.ums.dto.cp.CloudStaffNameInitialCp;
import com.vortex.cloud.ums.dto.cp.CloudStaffOrderIndexCp;
import com.vortex.cloud.ums.dto.cp.DeptOrgDtoOrderIndexCp;
import com.vortex.cloud.ums.enums.CompanyTypeEnum;
import com.vortex.cloud.ums.model.CloudStaff;
import com.vortex.cloud.ums.model.CloudUser;
import com.vortex.cloud.ums.support.ManagementConstant;
import com.vortex.cloud.vfs.common.lang.StringUtil;

@Transactional
@Service("redisValidateService")
public class RedisValidateServiceImpl implements IRedisValidateService {
	@Resource(name = CentralCacheRedisServiceImpl.CLASSNAME)
	private ICentralCacheRedisService centralCacheRedisService;
	@Resource
	private ICloudUserService cloudUserService;

	@Override
	public boolean hasFunction(String userId, String functionCode) {
		boolean rst = false;
		if (StringUtils.isEmpty(userId) || StringUtils.isEmpty(functionCode)) {
			return false;
		}

		CloudUser user = cloudUserService.findOne(userId);
		if (user == null) {
			return false;
		}

		// 如果是超级管理员，直接放过
		if (CloudUser.IS_ROOT_YES.equals(user.getIsRoot())) {
			return true;
		}

		List<String> code_list = getUserFunctionCodeList(userId);

		if (code_list.contains(functionCode)) {
			rst = true;
		}
		return rst;
	}

	@Override
	public Map<String, Boolean> hasFunction(String userId, List<String> functionCodes) {
		Map<String, Boolean> result = Maps.newHashMap();
		if (StringUtils.isEmpty(userId) || CollectionUtils.isEmpty(functionCodes)) {
			return null;
		}

		CloudUser user = cloudUserService.findOne(userId);
		if (user == null) {
			return null;
		}

		// 如果是超级管理员，直接放过
		if (CloudUser.IS_ROOT_YES.equals(user.getIsRoot())) {
			for (String functionCode : functionCodes) {
				result.put(functionCode, true);
			}
			return result;
		}
		List<String> code_list = getUserFunctionCodeList(userId);

		for (String functionCode : functionCodes) {
			if (!code_list.contains(functionCode)) {
				result.put(functionCode, false);
			} else {
				result.put(functionCode, true);
			}
		}
		return result;
	}

	@Override
	public String getBsMenuJson(String userId, String systemCode) {
		String redisKey = ManagementConstant.REDIS_PRE_MENU + ManagementConstant.REDIS_SEPARATOR + userId
				+ ManagementConstant.REDIS_SEPARATOR + systemCode;
		String value = centralCacheRedisService.getObject(redisKey, String.class);
		return value;
	}

	@Override
	public List<TenantDeptOrgDto> getDeptOrgList(String tenantId, String deptId) {
		if (StringUtil.isNullOrEmpty(tenantId)) {
			return null;
		}
		// 获取机构部门列表
		List<TenantDeptOrgDto> list = getDeptOrgListByTenantId(tenantId);
		if (CollectionUtils.isEmpty(list)) {
			return null;
		}
		List<TenantDeptOrgDto> returnValue = Lists.newArrayList();
		for (TenantDeptOrgDto entity : list) {
			if (!StringUtil.isNullOrEmpty(deptId)) {// 过滤部门时
				if (StringUtil.isNullOrEmpty(entity.getCompanyType())) {// 过滤
																		// 标识是depart层级还是org层级
					continue;
				}
				if (CompanyTypeEnum.DEPART.getKey().equals(entity.getCompanyType())) {// 过滤
																						// 标识是depart层级
					continue;
				}
				if (!deptId.equals(entity.getDepartmentId())) {// 过滤部门id
					continue;
				}
			}
			returnValue.add(entity);
		}
		return returnValue;
	}

	@Override
	public List<TenantDeptOrgDto> getDeptOrgListByIds(String tenantId, String[] ids) {
		if (StringUtil.isNullOrEmpty(tenantId) || ArrayUtils.isEmpty(ids)) {
			return null;
		}
		// 获取机构部门列表
		List<TenantDeptOrgDto> list = getDeptOrgListByTenantId(tenantId);
		if (CollectionUtils.isEmpty(list)) {
			return null;
		}
		List<String> id_list = Arrays.asList(ids);
		List<TenantDeptOrgDto> returnValue = Lists.newArrayList();
		for (TenantDeptOrgDto entity : list) {
			if (StringUtil.isNullOrEmpty(entity.getId())) {
				continue;
			}
			if (!id_list.contains(entity.getId())) {
				continue;
			}
			returnValue.add(entity);
		}
		return returnValue;
	}

	public List<TenantDeptOrgDto> getChildDeptOrgList(String tenantId, String id) {
		if (StringUtil.isNullOrEmpty(tenantId)) {
			return null;
		}
		if (StringUtil.isNullOrEmpty(id)) {
			return this.getDeptOrgList(tenantId, id);
		}
		TenantDeptOrgDto root = this.getDeptOrgById(tenantId, id);
		if (null == root || StringUtil.isNullOrEmpty(root.getCompanyType())) {
			return null;
		}
		if (CompanyTypeEnum.DEPART.getKey().equals(root.getCompanyType())) { // 传过来的是departid
			return this.getDeptOrgList(tenantId, id);
		}
		// 传过来是orgid

		// 获取机构部门列表
		List<TenantDeptOrgDto> list = getDeptOrgListByTenantId(tenantId);
		if (CollectionUtils.isEmpty(list)) {
			return null;
		}
		List<TenantDeptOrgDto> returnValue = Lists.newArrayList();
		List<TenantDeptOrgDto> filter_list = Lists.newArrayList();
		for (TenantDeptOrgDto entity : list) {
			if (root.getId().equals(entity.getId())) {// 过滤自己
				continue;
			}
			if (StringUtil.isNullOrEmpty(entity.getCompanyType())) {// 过滤
				// 标识是depart层级还是org层级
				continue;
			}
			if (CompanyTypeEnum.DEPART.getKey().equals(entity.getCompanyType())) {// 过滤
				// 标识是depart层级
				continue;
			}
			if (!root.getDepartmentId().equals(entity.getDepartmentId())) {// 过滤部门id
				continue;
			}
			filter_list.add(entity);
		}
		for (TenantDeptOrgDto entity : filter_list) {
			if (entity.getParentId().equals(root.getId())) {
				returnValue.add(entity);
				doFil(entity.getId(), filter_list, returnValue);
			}
		}
		return returnValue;
	}

	@Override
	public TenantDeptOrgDto getDeptOrgById(String tenantId, String id) {
		if (StringUtil.isNullOrEmpty(tenantId) || StringUtil.isNullOrEmpty(id)) {
			return null;
		}
		// 获取机构部门列表
		List<TenantDeptOrgDto> list = getDeptOrgListByTenantId(tenantId);
		if (CollectionUtils.isEmpty(list)) {
			return null;
		}
		TenantDeptOrgDto returnValue = null;
		for (TenantDeptOrgDto entity : list) {
			if (StringUtil.isNullOrEmpty(entity.getId())) {
				continue;
			}
			if (id.equals(entity.getId())) {
				returnValue = entity;
				break;
			}
		}
		return returnValue;
	}

	public List<CloudStaff> getStaffOrderListByDeptOrgIds(String tenantId, List<String> id_list, String order) {
		if (StringUtil.isNullOrEmpty(tenantId)) {
			return null;
		}
		// 获取人员列表
		List<CloudStaff> list = getStaffListByTenantId(tenantId);
		if (CollectionUtils.isEmpty(list)) {
			return null;
		}
		List<CloudStaff> sort_list = Lists.newArrayList();
		List<CloudStaff> nosort_list = Lists.newArrayList();
		for (CloudStaff entity : list) {
			if (StringUtil.isNullOrEmpty(entity.getDepartmentId()) && StringUtil.isNullOrEmpty(entity.getOrgId())) {// 机构部门id都为空过来
				continue;
			}
			if (!id_list.contains(entity.getDepartmentId()) && !id_list.contains(entity.getOrgId())) {// 过滤机构部门
				continue;
			}
			if ("orderIndex".equals(order)) {// 按排号排序
				if (null == entity.getOrderIndex()) {
					nosort_list.add(entity);
				} else {
					sort_list.add(entity);
				}
			} else if ("nameInitial".equals(order)) {// 按拼音排序
				if (StringUtil.isNullOrEmpty(entity.getNameInitial())) {
					nosort_list.add(entity);
				} else {
					sort_list.add(entity);
				}
			}
		}
		List<CloudStaff> returnValue = Lists.newArrayList();

		if ("orderIndex".equals(order)) {// 按排号排序
			Collections.sort(sort_list, new CloudStaffOrderIndexCp());
		} else if ("nameInitial".equals(order)) {// 按拼音排序
			Collections.sort(sort_list, new CloudStaffNameInitialCp());
		}
		returnValue.addAll(nosort_list);
		returnValue.addAll(sort_list);
		return returnValue;
	}

	private void doFil(String parentId, List<TenantDeptOrgDto> filter_list, List<TenantDeptOrgDto> returnValue) {
		// 得到当前层级的菜单列表
		List<TenantDeptOrgDto> childList = Lists.newArrayList();
		for (TenantDeptOrgDto entity : filter_list) {
			if (entity.getParentId().equals(parentId)) {
				if (!returnValue.contains(childList)) {
					childList.add(entity);
				}
			}
		}
		if (CollectionUtils.isEmpty(childList)) {
			return;
		}
		// 将父节点加入集合
		returnValue.addAll(childList);

		for (TenantDeptOrgDto entity : childList) {
			doFil(entity.getId(), filter_list, returnValue);
		}
	}

	/**
	 * @Title: getUserFunctionCodeList @Description: 根据用户id获取功能码 @return
	 *         List<String> @throws
	 */
	private List<String> getUserFunctionCodeList(String userId) {
		List<String> returnValue = Lists.newArrayList();
		// 拼接redisKey
		String redisKey = ManagementConstant.REDIS_PRE_FUNCTION + ManagementConstant.REDIS_SEPARATOR + userId;

		String value = centralCacheRedisService.getObject(redisKey, String.class);
		if (StringUtil.isNullOrEmpty(value)) {
			return returnValue;
		}
		return Arrays.asList(value.split(","));
	}

	/**
	 * @Title: getDeptOrgListByTenantId @Description: 根据租户获取机构部门列表 @return
	 *         List<TenantDeptOrgDto> @throws
	 */
	private List<TenantDeptOrgDto> getDeptOrgListByTenantId(String tenantId) {

		List<TenantDeptOrgDto> returnValue = Lists.newArrayList();
		String redisKey = ManagementConstant.REDIS_PRE_TENANT_DEPTORGIDS + ManagementConstant.REDIS_SEPARATOR
				+ tenantId;
		// 获取租户下，机构部门缓存的key
		List<String> id_list = centralCacheRedisService.getObject(redisKey, List.class);
		if (CollectionUtils.isEmpty(id_list)) {
			return returnValue;
		}
		List<TenantDeptOrgDto> sort_list = Lists.newArrayList();
		List<TenantDeptOrgDto> nosort_list = Lists.newArrayList();

		List<TenantDeptOrgDto> data_list = getDeptOrgListByIds(id_list);
		for (TenantDeptOrgDto entity : data_list) {
			if (null == entity.getOrderIndex()) {
				nosort_list.add(entity);
			} else {
				sort_list.add(entity);
			}
		}
		Collections.sort(sort_list, new DeptOrgDtoOrderIndexCp());

		returnValue.addAll(nosort_list);
		returnValue.addAll(sort_list);
		return returnValue;

	}

	/**
	 * @Title: getStaffListByTenantId @Description: 根据租户获取人员列表 @return
	 *         List<TenantDeptOrgDto> @throws
	 */
	private List<CloudStaff> getStaffListByTenantId(String tenantId) {
		List<CloudStaff> returnValue = Lists.newArrayList();
		String redisKey = ManagementConstant.REDIS_PRE_TENANT_STAFFIDS + ManagementConstant.REDIS_SEPARATOR + tenantId;
		// 获取租户下，人员缓存的key
		List<String> id_list = centralCacheRedisService.getObject(redisKey, List.class);
		if (CollectionUtils.isEmpty(id_list)) {
			return returnValue;
		}
		return this.getStaffListByIds(id_list);
	}

	@Override
	public List<CloudStaff> getStaffListByIds(List<String> id_list) {
		List<CloudStaff> returnValue = Lists.newArrayList();
		if (CollectionUtils.isEmpty(id_list)) {
			return returnValue;
		}
		List<String> ids = Lists.newArrayList();
		for (String id : id_list) {
			if (StringUtil.isNullOrEmpty(id)) {
				continue;
			}
			if (ids.contains(id)) {
				continue;
			}
			ids.add(id);
		}
		if (CollectionUtils.isEmpty(ids)) {
			return returnValue;
		}
		List<CloudStaff> list = centralCacheRedisService.getMapFields(ManagementConstant.REDIS_PRE_MAP_STAFF, ids,
				CloudStaff.class);
		if (CollectionUtils.isEmpty(list)) {
			return returnValue;
		}
		for (CloudStaff entity : list) {
			if (null == entity) {
				continue;
			}
			returnValue.add(entity);
		}
		return returnValue;
	}

	// @Override
	public List<TenantDeptOrgDto> getDeptOrgListByIds(List<String> id_list) {
		List<TenantDeptOrgDto> returnValue = Lists.newArrayList();
		if (CollectionUtils.isEmpty(id_list)) {
			return returnValue;
		}
		List<String> ids = Lists.newArrayList();
		for (String id : id_list) {
			if (StringUtil.isNullOrEmpty(id)) {
				continue;
			}
			if (ids.contains(id)) {
				continue;
			}
			ids.add(id);
		}
		if (CollectionUtils.isEmpty(ids)) {
			return returnValue;
		}
		List<TenantDeptOrgDto> list = centralCacheRedisService.getMapFields(ManagementConstant.REDIS_PRE_MAP_DEPTORG, ids,
				TenantDeptOrgDto.class);
		if (CollectionUtils.isEmpty(list)) {
			return returnValue;
		}
		for (TenantDeptOrgDto entity : list) {
			if (null == entity) {
				continue;
			}
			returnValue.add(entity);
		}
		return returnValue;
	}
}
