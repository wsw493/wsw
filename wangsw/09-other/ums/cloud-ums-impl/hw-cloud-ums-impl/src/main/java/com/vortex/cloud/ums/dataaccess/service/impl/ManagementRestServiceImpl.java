package com.vortex.cloud.ums.dataaccess.service.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.vortex.cloud.ums.dataaccess.dao.ICloudDepartmentDao;
import com.vortex.cloud.ums.dataaccess.dao.ICloudFunctionDao;
import com.vortex.cloud.ums.dataaccess.dao.ICloudOrganizationDao;
import com.vortex.cloud.ums.dataaccess.dao.ICloudRoleDao;
import com.vortex.cloud.ums.dataaccess.dao.ICloudStaffDao;
import com.vortex.cloud.ums.dataaccess.dao.ICloudUserDao;
import com.vortex.cloud.ums.dataaccess.service.IManagementRestService;
import com.vortex.cloud.ums.dto.CloudStaffDto;
import com.vortex.cloud.ums.dto.CloudStaffSearchDto;
import com.vortex.cloud.ums.model.CloudDepartment;
import com.vortex.cloud.ums.model.CloudFunction;
import com.vortex.cloud.ums.model.CloudOrganization;
import com.vortex.cloud.ums.model.CloudRole;
import com.vortex.cloud.ums.model.CloudStaff;
import com.vortex.cloud.ums.model.CloudUser;
import com.vortex.cloud.vfs.common.exception.ServiceException;
import com.vortex.cloud.vfs.data.support.SearchFilter;
import com.vortex.cloud.vfs.data.support.SearchFilter.Operator;




@Transactional
@Service("managementRestService")
public class ManagementRestServiceImpl implements IManagementRestService {
	private Logger logger = LoggerFactory.getLogger(ManagementRestServiceImpl.class);
	@Resource
	private ICloudStaffDao cloudStaffDao;
	@Resource
	private ICloudRoleDao cloudRoleDao;
	@Resource
	private ICloudFunctionDao cloudFunctionDao;
	@Resource
	private ICloudUserDao cloudUserDao;
	@Resource
	private ICloudDepartmentDao cloudDepartmentDao;
	@Resource
	private ICloudOrganizationDao cloudOrganizationDao;

	@Override
	public CloudStaff getStaffById(String staffId) {
		if (StringUtils.isEmpty(staffId)) {
			logAndThrowException("staffid不能为空");
		}
		CloudStaff staff = cloudStaffDao.findOne(staffId);
		return staff;
	}

	@Override
	public CloudStaff getStaffByCode(String staffCode, String tennatId) {
		if (StringUtils.isEmpty(staffCode)) {
			logAndThrowException("staffCode不能为空");
		}
		if (StringUtils.isEmpty(tennatId)) {
			logAndThrowException("tennatId不能为空");
		}
		CloudStaff cloudStaff = cloudStaffDao.getStaffByCode(staffCode, tennatId);
		return cloudStaff;
	}

	@Override
	public CloudRole getRoleById(String roleId) {
		if (StringUtils.isEmpty(roleId)) {
			logAndThrowException("roleId不能为空");
		}
		CloudRole cloudRole = cloudRoleDao.findOne(roleId);
		return cloudRole;
	}

	@Override
	public CloudFunction getFunctionById(String functionId) {
		if (StringUtils.isEmpty(functionId)) {
			logAndThrowException("functionId不能为空");
		}
		CloudFunction cloudFunction = cloudFunctionDao.findOne(functionId);
		return cloudFunction;
	}

	@Override
	public CloudFunction getFunctionByCode(String functionCode, String tenantId, String systemCode) {

		if (StringUtils.isEmpty(functionCode)) {
			logAndThrowException("functionCode不能为空");
		}
		if (StringUtils.isEmpty(tenantId)) {
			logAndThrowException("tenantId不能为空");
		}
		if (StringUtils.isEmpty(systemCode)) {
			logAndThrowException("systemCode不能为空");
		}

		CloudFunction cloudFunction = cloudFunctionDao.getFunctionByCode(functionCode, tenantId, systemCode);
		return cloudFunction;
	}

	@Override
	public List<CloudRole> getRolesByUserId(String userId) {
		if (StringUtils.isEmpty(userId)) {
			logAndThrowException("userId不能为空");
		}
		List<CloudRole> list = cloudRoleDao.getRolesByUserId(userId);
		return list;
	}

	@Override
	public CloudStaff getStaffByUserId(String userId) {
		if (StringUtils.isEmpty(userId)) {
			logAndThrowException("userId不能为空");
		}

		CloudStaff cloudStaff = cloudStaffDao.getStaffByUserId(userId);
		return cloudStaff;
	}

	@Override
	public CloudUser getUserById(String userId) {
		if (StringUtils.isEmpty(userId)) {
			logAndThrowException("userId不能为空");
		}
		CloudUser cloudUser = cloudUserDao.findOne(userId);
		return cloudUser;
	}

	@Override
	public CloudUser getUserByStaffId(String staffId) {
		if (StringUtils.isEmpty(staffId)) {
			logAndThrowException("staffId不能为空");
		}
		CloudUser cloudUser = cloudUserDao.getUserByStaffId(staffId);
		return cloudUser;
	}

	@Override
	public CloudDepartment getDepartmentById(String departmentId) {
		if (StringUtils.isEmpty(departmentId)) {
			logAndThrowException("departmentId不能为空");
		}
		CloudDepartment cloudDepartment = cloudDepartmentDao.findOne(departmentId);
		return cloudDepartment;
	}

	@Override
	public CloudDepartment getDepartmentByCode(String departmentCode, String tenantId) {
		if (StringUtils.isEmpty(departmentCode)) {
			logAndThrowException("departmentCode不能为空");
		}
		if (StringUtils.isEmpty(tenantId)) {
			logAndThrowException("tenantId不能为空");
		}
		return cloudDepartmentDao.getDepartmentByCode(departmentCode, tenantId);
	}

	@Override
	public List<String> getStaffsByDepartmentId(String departmentId) {
		if (StringUtils.isEmpty(departmentId)) {
			logAndThrowException("departmentId不能为空");
		}
		CloudDepartment cloudDepartment = cloudDepartmentDao.findOne(departmentId);
		CloudOrganization cloudOrganization = cloudOrganizationDao.findOne(departmentId);
		List<String> ids = null;
		if (cloudDepartment == null && cloudOrganization == null) {
			logAndThrowException("不存在该部门");
		}
		if (cloudDepartment != null && cloudOrganization != null) {
			logAndThrowException("该id对应两个部门");
		}
		if (cloudDepartment != null) // 传过来的是公司id
		{
			ids = cloudStaffDao.getStaffsByDepartmentId(departmentId);
		}
		if (cloudOrganization != null)// 传过来的是部门id
		{
			ids = cloudStaffDao.getStaffsByOrgId(departmentId);
		}

		return ids;
	}

	@Override
	public List<String> getAllStaffsByDepartmentId(String departmentId) {
		if (StringUtils.isEmpty(departmentId)) {
			logAndThrowException("departmentId不能为空");
		}
		CloudDepartment cloudDepartment = cloudDepartmentDao.findOne(departmentId);
		CloudOrganization cloudOrganization = cloudOrganizationDao.findOne(departmentId);
		List<String> ids = null;
		if (cloudDepartment == null && cloudOrganization == null) {
			logAndThrowException("不存在该部门");
		}
		if (cloudDepartment != null && cloudOrganization != null) {
			logAndThrowException("该id对应两个部门");
		}
		if (cloudDepartment != null) // 传过来的是公司id
		{
			ids = cloudStaffDao.getAllStaffsByDepartmentId(departmentId);
		}
		if (cloudOrganization != null)// 传过来的是部门id
		{
			String nodeCode = cloudOrganization.getNodeCode();
			ids = cloudStaffDao.getAllStaffsByOrgNodeCode(nodeCode);
		}
		return ids;

	}

	@Override
	public List<String> getFunctionsByRoleId(String roleId) {

		if (StringUtils.isEmpty(roleId)) {
			logAndThrowException("roleId不能为空");
		}
		List<String> ids = cloudFunctionDao.getFunctionsByRoleId(roleId);
		return ids;
	}

	@Override
	public CloudOrganization getOrganizationById(String orgId) {
		if (StringUtils.isEmpty(orgId)) {
			logAndThrowException("orgId不能为空");
		}

		CloudOrganization cloudOrganization = cloudOrganizationDao.findOne(orgId);
		return cloudOrganization;
	}

	@Override
	public CloudOrganization getOrganizationByCode(String orgCode, String tenantId) {

		if (StringUtils.isEmpty(orgCode)) {
			logAndThrowException("orgCode不能为空");
		}
		if (StringUtils.isEmpty(tenantId)) {
			logAndThrowException("tenantId不能为空");
		}

		CloudOrganization cloudOrganization = cloudOrganizationDao.getOrganizationByCode(orgCode, tenantId);
		return cloudOrganization;
	}

	@Override
	public CloudUser getUserByUserName(String tenantId, String userName) {

		if (StringUtils.isEmpty(tenantId)) {
			logAndThrowException("tenantId不能为空");
		}
		if (StringUtils.isEmpty(userName)) {
			logAndThrowException("userName不能为空");
		}
		CloudUser cloudUser = cloudUserDao.getUserByUserName(tenantId, userName);
		return cloudUser;
	}

	@Override
	public boolean hasFunction(String userId, String systemId, String functionId) {
		if (StringUtils.isEmpty(userId) || StringUtils.isEmpty(systemId) || StringUtils.isEmpty(functionId)) {
			return false;
		}

		return cloudFunctionDao.hasFunction(userId, systemId, functionId);
	}

	@Override
	public List<String> getFunctionList(String userId, String systemId) {
		if (StringUtils.isEmpty(userId) || StringUtils.isEmpty(systemId)) {
			return null;
		}

		return cloudFunctionDao.getFunctionList(userId, systemId);
	}

	/**
	 * 抛错和记日志
	 * 
	 * @param msg 错误信息
	 */
	private void logAndThrowException(String msg) {
		logger.error(msg);
		throw new ServiceException(msg);
	}

	@Override
	public List<Map<String, Object>> getStaffListByUserRegisterType(CloudStaffSearchDto searchDto) {
		return cloudStaffDao.getStaffListByUserRegisterType(searchDto);
	}

	@Override
	public CloudStaffDto getCloudStaffDtoByStaffName(String name, String tenantId) {
		List<SearchFilter> searchFilters = Lists.newArrayList();
		searchFilters.add(new SearchFilter("name", Operator.EQ, name));
		searchFilters.add(new SearchFilter("tenantId", Operator.EQ, tenantId));
		List<CloudStaff> list = cloudStaffDao.findListByFilter(searchFilters, null);
		CloudStaff cloudStaff = null;
		CloudStaffDto cloudStaffDto = new CloudStaffDto();

		if (CollectionUtils.isNotEmpty(list)) {
			cloudStaff = list.get(0);
		}
		BeanUtils.copyProperties(cloudStaff, cloudStaffDto);
		return cloudStaffDto;

	}
}
