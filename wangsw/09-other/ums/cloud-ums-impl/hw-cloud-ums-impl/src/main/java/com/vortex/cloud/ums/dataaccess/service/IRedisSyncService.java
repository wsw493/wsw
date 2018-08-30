package com.vortex.cloud.ums.dataaccess.service;

import java.util.List;

/**
 * 同步redis缓存数据的service(菜单json、用户功能码等)
 * 
 * @author XY
 *
 */
public interface IRedisSyncService {
	/**
	 * 同步用户的菜单
	 */
	public void syncUserMenu();

	/**
	 * 同步用户的功能码
	 */
	public void syncUserFunction();
	
	
	/**
	* @Title: syncUserAuthorityByTenant
	* @Description:按租户同步用户权限信息（菜单，功能码）
	* @return void 
	* @throws
	*/
	public void syncUserAuthorityByTenant(List<String> tenantIds);
	
	/**
	* @Title: syncSystemMenuByTenant
	* @Description: 按租户同步系统菜单信息（不包含权限）
	* @return void 
	* @throws
	*/
	public void syncSystemMenuByTenant(List<String> tenantIds);
	
	/**
	* @Title: syncDeptOrgByTenant
	* @Description: 按租户同步机构部门信息（包含已删除的数据，不包含权限）
	* @return void 
	* @throws
	*/
	public void syncDeptOrgByTenant(List<String> tenantIds);
	
	/**
	* @Title: syncStaffTenant
	* @Description: 按租户同步人员信息（不包含权限）
	* @return void 
	* @throws
	*/
	public void syncStaffByTenant(List<String> tenantIds);
}
