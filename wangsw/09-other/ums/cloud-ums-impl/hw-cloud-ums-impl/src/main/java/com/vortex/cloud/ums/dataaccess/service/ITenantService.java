package com.vortex.cloud.ums.dataaccess.service;

import java.util.List;
import java.util.Map;

import com.vortex.cloud.ums.dto.TenantDto;
import com.vortex.cloud.ums.dto.TenantUrlDto;
import com.vortex.cloud.ums.model.Tenant;
import com.vortex.cloud.vfs.data.hibernate.service.PagingAndSortingService;


public interface ITenantService extends PagingAndSortingService<Tenant, String> {
	/**
	 * 保存租户
	 * 
	 * @param tenantDto
	 */
	public Tenant saveTenant(TenantDto tenantDto);

	/**
	 * 根据id加载租户信息
	 * 
	 * @param id
	 */
	public TenantDto loadTenant(String id);

	/**
	 * 根据Code获取租户信息
	 * 
	 * @param code
	 */
	public TenantDto getTenantByCode(String code);

	/**
	 * 更新租户信息
	 * 
	 * @param tenantDto
	 */
	public void updateTenant(TenantDto tenantDto);

	/**
	 * 删除租户
	 * 
	 * @param id
	 */
	public void removeTeant(String id);

	/**
	 * 启用租户
	 * 
	 * @param idArr
	 */
	public void enableTenant(String[] idArr);

	/**
	 * 禁用租户
	 * 
	 * @param idArr
	 */
	public void disableTenant(String[] idArr);

	/**
	 * 根据选定的根节点，将节点本身及其底下所有节点，复制到租户下面
	 * 
	 * @param rootId
	 * @param tenantId
	 */
	public void copyDivisionTree(String rootId, String tenantId);

	/**
	 * 根据编码，得到租户
	 * 
	 * @param tenantCode
	 * @return
	 * @throws Exception
	 */
	public Tenant getByCode(String tenantCode);
	
	/**
	 * 根据，得到租户url
	 * @param tenantId
	 * @return
	 */
	public TenantUrlDto getTenantUrl(String tenantId);
	/**
	 * 一次删除多个记录
	 * 
	 * @param ids
	 */
	void delete(String[] ids);

	/**
	 * 根据id列表获取相应name
	 * 
	 * @param idList
	 * @return
	 */
	public Map<String, String> findTenantNameById(List<String> idList);

	/**
	 * 根据id列表获取租户codes
	 * 
	 * @param idList
	 * @return
	 */
	public Object getTenantCodesByIds(List<String> idList);

	/**
	 * 获取所有的租户信息
	 * 
	 * @return
	 */
	public Object getAllTenant();
}
