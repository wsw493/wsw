package com.vortex.cloud.ums.dataaccess.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.vortex.cloud.ums.dto.CloudSysSearchDto;
import com.vortex.cloud.ums.dto.TenantSystemRelationDto;
import com.vortex.cloud.ums.model.TenantSystemRelation;
import com.vortex.cloud.vfs.data.hibernate.service.PagingAndSortingService;


public interface ITenantCloudSystemService extends PagingAndSortingService<TenantSystemRelation, String> {
	
	/**
	 * 获取指定条件的云系统列表
	 * 
	 * @param pageable
	 * @param searchDto
	 */
	public Page<TenantSystemRelationDto> getPageOfCloudSys(Pageable pageable, CloudSysSearchDto searchDto);

	/**
	 * 为租户开通某个云系统的权限
	 * 
	 * @param tenantId
	 * @param systemId
	 */
	public void enableCloudSystem(String tenantId, String systemId);

	/**
	 * 为租户关闭某个云系统的权限
	 * 
	 * @param id
	 */
	public void disableCloudSystem(String id);
	
	/**
	 * 根据租户id和云系统编码，判断该租户是否开通了该云系统的服务
	 * 
	 * @param tenantId
	 * @param cloudSystemCode
	 * @return
	 */
	boolean isTenantOpenSystem(String tenantId, String cloudSystemCode);
}
