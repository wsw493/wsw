package com.vortex.cloud.ums.dataaccess.dao.impl;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import com.vortex.cloud.ums.dataaccess.dao.IParamTypeDao;
import com.vortex.cloud.ums.model.PramType;
import com.vortex.cloud.vfs.data.hibernate.repository.SimpleHibernateRepository;
import com.vortex.cloud.vfs.data.model.BakDeleteModel;



@Repository("paramTypeDao")
public class ParamTypeDaoImpl 
	extends SimpleHibernateRepository<PramType, String> 
	implements IParamTypeDao {	
	
	@Override
	public DetachedCriteria getDetachedCriteria() {
		DetachedCriteria criteria = DetachedCriteria.forClass(getPersistentClass(), "parameterType");
		criteria.add(Restrictions.eq("parameterType.beenDeleted", BakDeleteModel.NO_DELETED));
		return criteria;
	}
}
