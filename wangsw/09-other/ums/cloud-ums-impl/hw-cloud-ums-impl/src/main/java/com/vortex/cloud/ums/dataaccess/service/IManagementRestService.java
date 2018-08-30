package com.vortex.cloud.ums.dataaccess.service;

import java.util.List;
import java.util.Map;

import com.vortex.cloud.ums.dto.CloudStaffDto;
import com.vortex.cloud.ums.dto.CloudStaffSearchDto;
import com.vortex.cloud.ums.model.CloudDepartment;
import com.vortex.cloud.ums.model.CloudFunction;
import com.vortex.cloud.ums.model.CloudOrganization;
import com.vortex.cloud.ums.model.CloudRole;
import com.vortex.cloud.ums.model.CloudStaff;
import com.vortex.cloud.ums.model.CloudUser;

/**
 * 对我提供rest接口的服务
 * 
 * @author XY
 *
 */
public interface IManagementRestService {
	/**
	 * 根据人员id，得到人员基本信息
	 * 
	 * @param staffId
	 * @return
	 * 
	 */
	public CloudStaff getStaffById(String staffId);

	/**
	 * 根据人员code和租户Id，得到人员基本信息
	 * 
	 * @param staffCode
	 * @param tenantId
	 * @return
	 * 
	 */
	public CloudStaff getStaffByCode(String staffCode, String tenantId);

	/**
	 * 根据用户id，查询人员基本信息
	 * 
	 * @param userId
	 * @return
	 * 
	 */
	public CloudStaff getStaffByUserId(String userId);

	/**
	 * 根据id，得到用户信息
	 * 
	 * @param userId
	 * @return
	 * 
	 */
	public CloudUser getUserById(String userId);

	/**
	 * 根据用户名和租户Id，得到用户信息
	 * 
	 * @param userName
	 * @param tenantId
	 * @return
	 * 
	 */
	public CloudUser getUserByUserName(String tenantId, String userName);

	/**
	 * 根据人员基本信息id，得到用户信息，没有返回null
	 * 
	 * @param staffId
	 * @return
	 * 
	 */
	public CloudUser getUserByStaffId(String staffId);

	/**
	 * 根据角色id，得到角色
	 * 
	 * @param roleId
	 * @return
	 * 
	 */
	public CloudRole getRoleById(String roleId);

	/**
	 * 根据功能id，得到功能信息
	 * 
	 * @param functionId
	 * @return
	 * 
	 */
	public CloudFunction getFunctionById(String functionId);

	/**
	 * 根据功能code和租户Id和业务系统Id，得到功能信息
	 * 
	 * @param functionCode
	 * @param tenantId
	 * @param systemCode
	 * @return
	 * 
	 */
	public CloudFunction getFunctionByCode(String functionCode, String tenantId, String systemCode);

	/**
	 * 根据用户id，得到用户所拥有的角色列表
	 * 
	 * @param userId
	 * @return
	 * 
	 */
	public List<CloudRole> getRolesByUserId(String userId);

	/**
	 * 传入业务系统id和人员id，返回该人员是否拥有该功能
	 * 
	 * @param userId
	 * @param systemId
	 * @param functionId
	 * @return
	 */
	public boolean hasFunction(String userId, String systemId, String functionId);

	/**
	 * 根据人员id，查询该人员在指定业务系统中所拥有的功能列表
	 * 
	 * @param userId 用户id
	 * @param systemId 系统Id
	 * @return
	 * 
	 */
	public List<String> getFunctionList(String userId, String systemId);

	/**
	 * 根据部门id，得到部门信息
	 * 
	 * @param departmentId
	 * @return
	 * 
	 */
	public CloudDepartment getDepartmentById(String departmentId);

	/**
	 * 根据部门code和租户id，得到部门信息
	 * 
	 * @param departmentCode
	 * @param tenantId
	 * @return
	 * 
	 */
	public CloudDepartment getDepartmentByCode(String departmentCode, String tenantId);

	/**
	 * 根据机构id，得到机构信息
	 * 
	 * @param orgId
	 * @return
	 * 
	 */
	public CloudOrganization getOrganizationById(String orgId);

	/**
	 * 根据机构code和租户Id，得到机构信息
	 * 
	 * @param orgCode
	 * @param tenantId
	 * @return
	 * 
	 */
	public CloudOrganization getOrganizationByCode(String orgCode, String tenantId);

	/**
	 * 根据部门id，得到只属于此部门的所有人员id（因人员信息字段过多，暂不考虑直接返回人员所有信息）
	 * 
	 * @param departmentId
	 * @return
	 * 
	 */
	public List<String> getStaffsByDepartmentId(String departmentId);

	/**
	 * 根据人员部门id，得到部门及其所有子部门的人员
	 * 
	 * @param departmentId
	 * @return
	 * 
	 */
	public List<String> getAllStaffsByDepartmentId(String departmentId);

	/**
	 * 根据角色id，得到角色上面所有功能id的列表
	 * 
	 * @param roleId
	 * @return
	 * 
	 */
	public List<String> getFunctionsByRoleId(String roleId);

	/**
	 * 获取租户下的人员列表
	 * 
	 * @param tenantId
	 * @param registerType 标明人员是否注册为用户
	 * @return
	 */
	public List<Map<String, Object>> getStaffListByUserRegisterType(	CloudStaffSearchDto searchDto);

	/**
	 * 根据name，获取staff信息
	 * 
	 * @param name
	 * @param tenantId
	 * @return
	 */
	CloudStaffDto getCloudStaffDtoByStaffName(String name, String tenantId);
}
