package com.vortex.cloud.ums.dataaccess.dao;

import com.vortex.cloud.ums.model.TenantSystemRelation;
import com.vortex.cloud.vfs.data.hibernate.repository.HibernateRepository;

/**
 * 租户上面开启的云系统列表dao
 * @author lsm
 *
 */
public interface ITenantSystemRelationDao extends HibernateRepository<TenantSystemRelation, String> {
	/**
	 * 根据租户id和云系统编码，判断该租户是否开通了该云系统的服务
	 * 
	 * @param tenantId
	 * @param cloudSystemCode
	 * @return
	 */
	boolean isTenantOpenSystem(String tenantId, String cloudSystemCode);
}
