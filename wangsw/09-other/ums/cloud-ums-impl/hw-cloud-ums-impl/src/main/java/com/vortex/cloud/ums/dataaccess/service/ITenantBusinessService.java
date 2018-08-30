package com.vortex.cloud.ums.dataaccess.service;

import java.util.List;

import com.vortex.cloud.ums.dto.CloudSystemDto;
import com.vortex.cloud.ums.dto.TenantDto;
import com.vortex.cloud.ums.model.Tenant;
import com.vortex.cloud.vfs.data.support.SearchFilter;


public interface ITenantBusinessService {
	/**
	 * 业务系统新增、修改时，需要判断系统编码、系统名称是否存在
	 * 
	 * @param filterList
	 * @return
	 * 
	 */
	public boolean isExistSystem(List<SearchFilter> filterList);

	/**
	 * 将云平台上面的某个云系统的模版资源，复制一份到租户下；以便在业务系统使用该云系统时，做个性化的修改；在租户开通某个云系统服务的时候调用
	 * 
	 * @param tenantId
	 * @param cloudSystemId
	 * 
	 */
	public void copyResources(String tenantId, String cloudSystemId);

	/**
	 * 复制参数表到租户下，在新增租户的时候调用
	 * 
	 * @param tenantId
	 */
	public void copyPrams(String tenantId);
	
	/**
	 * 为租户在云系统上生成一个默认的管理员
	 * @param tenantDto
	 */
	void setTenantRootUser(TenantDto tenantDto);
	
	/**
	 * 为业务系统在云系统上生成一个默认的管理员
	 * @param tenant
	 * @param systemDto
	 */
	void setBusinessSysRootUser(Tenant tenant, CloudSystemDto systemDto);
}
