package com.vortex.cloud.ums.dataaccess.dao;

import java.util.List;
import java.util.Map;

import com.vortex.cloud.ums.dto.TenantUrlDto;
import com.vortex.cloud.ums.model.Tenant;
import com.vortex.cloud.vfs.data.hibernate.repository.HibernateRepository;


/**
 * 租户dao
 * 
 * @author lsm
 *
 */
public interface ITenantDao extends HibernateRepository<Tenant, String> {

	Map<String, String> findTenantNameById(List<String> idList);
	
	/**
	 * 根据，得到租户url
	 * @param tenantId
	 * @return
	 */
	public TenantUrlDto getTenantUrl(String tenantId);

}
