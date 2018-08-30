package com.vortex.cloud.ums.dataaccess.dao.impl;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import com.vortex.cloud.ums.dataaccess.dao.ICloudLoginLogDao;
import com.vortex.cloud.ums.model.CloudLoginLog;
import com.vortex.cloud.vfs.data.hibernate.repository.SimpleHibernateRepository;

@Repository("cloudLoginLogDao")
public class CloudLoginLogDaoImpl extends SimpleHibernateRepository<CloudLoginLog, String> implements ICloudLoginLogDao {

	@Override
	public DetachedCriteria getDetachedCriteria() {
		return defaultCriteria();
	}

	private DetachedCriteria defaultCriteria() {
		DetachedCriteria criteria = DetachedCriteria.forClass(getPersistentClass(), "cloudLoginLog");
		criteria.add(Restrictions.eq("beenDeleted", 0));
		return criteria;
	}

}
