package com.vortex.cloud.ums.dataaccess.dao.impl;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import com.vortex.cloud.ums.dataaccess.dao.IPramSettingDao;
import com.vortex.cloud.ums.model.PramSetting;
import com.vortex.cloud.vfs.data.hibernate.repository.SimpleHibernateRepository;
import com.vortex.cloud.vfs.data.model.BakDeleteModel;



@Repository("pramSettingDao")
public class PramSettingDaoImpl extends SimpleHibernateRepository<PramSetting, String> implements IPramSettingDao {

	@Override
	public DetachedCriteria getDetachedCriteria() {
		return defaultCriteria();
	}

	private DetachedCriteria defaultCriteria() {
		DetachedCriteria criteria = DetachedCriteria.forClass(getPersistentClass(), "pramSetting");
		criteria.add(Restrictions.eq("beenDeleted", BakDeleteModel.NO_DELETED));
		return criteria;
	}
}
