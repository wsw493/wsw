package com.vortex.cloud.ums.dataaccess.dao.impl;

import javax.annotation.Resource;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.vortex.cloud.ums.dataaccess.dao.IWorkElementTypeDao;
import com.vortex.cloud.ums.model.WorkElementType;
import com.vortex.cloud.vfs.data.hibernate.repository.SimpleHibernateRepository;

/**
 * 图元类型dao
 * 
 * @author lsm
 *
 */
@Repository("workElementTypeDao")
public class WorkElementTypeDaoImpl extends SimpleHibernateRepository<WorkElementType, String> implements IWorkElementTypeDao {
	@Override
	public DetachedCriteria getDetachedCriteria() {
		return defaultCriteria();
	}

	@Resource
	private JdbcTemplate jdbcTemplate;

	private DetachedCriteria defaultCriteria() {
		DetachedCriteria criteria = DetachedCriteria.forClass(getPersistentClass(), "workElementType");
		criteria.add(Restrictions.eq("beenDeleted", 0));
		return criteria;
	}

	@Override
	public boolean isCodeExists(String code, String tenantId) {

		StringBuffer sql = new StringBuffer();
		sql.append("   SELECT                                  ");
		sql.append("   	COUNT(1)                               ");
		sql.append("   FROM                                    ");
		sql.append("   	cloud_work_element_type bwet            ");
		sql.append("   WHERE                                   ");
		sql.append("   	bwet.f_code =        '" + code + "'                 ");
		sql.append("   AND bwet.f_tenantId =   '" + tenantId + "'              ");
		sql.append("   AND bwet.beenDeleted = 0                ");
		Integer count = jdbcTemplate.queryForObject(sql.toString(), Integer.class);
		return count > 0;

	}
}
