package com.vortex.cloud.ums.dataaccess.dao.impl;

import javax.annotation.Resource;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.vortex.cloud.ums.dataaccess.dao.ITenantSystemRelationDao;
import com.vortex.cloud.ums.model.TenantSystemRelation;
import com.vortex.cloud.vfs.data.hibernate.repository.SimpleHibernateRepository;
import com.vortex.cloud.vfs.data.model.BakDeleteModel;



@Repository("tenantSystemRelationDao")
public class TenantSystemRelationDaoImpl 
	extends SimpleHibernateRepository<TenantSystemRelation, String> 
		implements ITenantSystemRelationDao {
	
	@Resource
	private JdbcTemplate jdbcTemplate;
	
	@Override
	public DetachedCriteria getDetachedCriteria() {
		return defaultCriteria();
	}

	private DetachedCriteria defaultCriteria() {
		DetachedCriteria criteria = DetachedCriteria.forClass(getPersistentClass(), "tenantSystemRelation");
		criteria.add(Restrictions.eq("beenDeleted", BakDeleteModel.NO_DELETED));
		return criteria;
	}
	
	@Override
	public boolean isTenantOpenSystem(String tenantId, String cloudSystemCode) {
		boolean result = false;
		StringBuffer sql = new StringBuffer();
		sql.append(" SELECT count(1) ");
		sql.append("   from cloud_system a,cloud_tenantsystem_relation b ");
		sql.append("  where a.id=b.cloudSystemId ");
		sql.append("    and a.systemCode='" + cloudSystemCode + "' ");
		sql.append("    and b.tenantId='" + tenantId + "' ");
		sql.append("    and b.enabled='1' ");
		int count = jdbcTemplate.queryForObject(sql.toString(), Integer.class);
		if (count > 0) {
			result = true;
		}
		return result;
	}
}
