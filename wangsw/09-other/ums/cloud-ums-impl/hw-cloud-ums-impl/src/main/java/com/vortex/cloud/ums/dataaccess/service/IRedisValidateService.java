package com.vortex.cloud.ums.dataaccess.service;

import java.util.List;
import java.util.Map;

import com.vortex.cloud.ums.dto.TenantDeptOrgDto;
import com.vortex.cloud.ums.model.CloudStaff;

public interface IRedisValidateService {
	/**
	 * 根据用户id，校验用户是否拥有此功能号
	 * 
	 * @param userId
	 * @param functionCode
	 * @return
	 */
	public boolean hasFunction(String userId, String functionCode);

	/**
	 * 根据用户id，得到用户在此系统下面的菜单json
	 * 
	 * @param userId
	 * @param systemCode
	 * @return
	 */
	public String getBsMenuJson(String userId, String systemCode);

	/**
	 * 根据用户id，校验用户是否拥有此功能号
	 * 
	 * @param userId
	 * @param functionCodes
	 * @return
	 */
	Map<String, Boolean> hasFunction(String userId, List<String> functionCodes);
	
	
	/**
	* @Title: getDeptOrgList
	* @Description: 根据租户id,部门id获取机构部门列表
	* @return List<TenantDeptOrgDto> 
	* @throws
	*/
	List<TenantDeptOrgDto> getDeptOrgList(String tenantId, String deptId);
	
	/**
	* @Title: getDeptOrgListByIds
	* @Description: 根据租户id,机构部门id数据获取机构部门列表
	* @return List<TenantDeptOrgDto> 
	* @throws
	*/
	List<TenantDeptOrgDto> getDeptOrgListByIds(String tenantId, String[] ids);
	
	/**
	* @Title: getChildDeptOrgList
	* @Description: 根据租户id,机构id获取子集列表
	* @return List<TenantDeptOrgDto> 
	* @throws
	*/
	List<TenantDeptOrgDto> getChildDeptOrgList(String tenantId, String companyId);
	
	/**
	* @Title: getDeptOrgById
	* @Description: 根据租户id,机构部门id获取数据
	* @return TenantDeptOrgDto 
	* @throws
	*/
	TenantDeptOrgDto getDeptOrgById(String tenantId, String id);
	
	/**
	* @Title: getOrderListByDeptOrgIds
	* @Description: 根据租户id,机构部门id数据获取人员排序列表
	* @return List<CloudStaff> 
	* @throws
	*/
	List<CloudStaff> getStaffOrderListByDeptOrgIds(String tenantId,List<String> id_list, String order);
	
	
	List<CloudStaff> getStaffListByIds(List<String> id_list);
}
