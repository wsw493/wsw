package com.vortex.cloud.ums.dataaccess.dao.impl;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import com.vortex.cloud.ums.dataaccess.dao.ICloudLogDao;
import com.vortex.cloud.ums.model.CloudLog;
import com.vortex.cloud.vfs.data.hibernate.repository.SimpleHibernateRepository;

@Repository("cloudLogDao")
public class CloudLogDaoImpl extends SimpleHibernateRepository<CloudLog, String> implements ICloudLogDao {

	@Override
	public DetachedCriteria getDetachedCriteria() {
		return defaultCriteria();
	}

	private DetachedCriteria defaultCriteria() {
		DetachedCriteria criteria = DetachedCriteria.forClass(getPersistentClass(), "cloudLog");
		criteria.add(Restrictions.eq("beenDeleted", 0));
		return criteria;
	}

}
