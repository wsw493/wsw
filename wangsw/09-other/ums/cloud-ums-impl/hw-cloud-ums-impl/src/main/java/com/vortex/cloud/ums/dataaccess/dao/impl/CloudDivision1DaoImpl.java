package com.vortex.cloud.ums.dataaccess.dao.impl;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import com.google.common.collect.Lists;
import com.vortex.cloud.ums.dataaccess.dao.ICloudDivision1Dao;
import com.vortex.cloud.ums.model.CloudDivision;
import com.vortex.cloud.vfs.data.hibernate.repository.SimpleHibernateRepository;
import com.vortex.cloud.vfs.data.model.BakDeleteModel;
import com.vortex.cloud.vfs.data.support.SearchFilter;




@Repository("cloudDivision1Dao")
public class CloudDivision1DaoImpl extends SimpleHibernateRepository<CloudDivision, String> implements ICloudDivision1Dao {

	@Override
	public List<CloudDivision> getListByParentId(String parentId) {
		if (StringUtils.isEmpty(parentId)) {
			return null;
		}

		List<SearchFilter> searchFilters = Lists.newArrayList();
		SearchFilter filter = new SearchFilter("parentId", SearchFilter.Operator.EQ, parentId);
		searchFilters.add(filter);
		return this.findListByFilter(searchFilters, null);
	}

	@Override
	public DetachedCriteria getDetachedCriteria() {
		return defaultCriteria();
	}

	private DetachedCriteria defaultCriteria() {
		DetachedCriteria criteria = DetachedCriteria.forClass(getPersistentClass(), "cloudDivision");
		criteria.add(Restrictions.eq("beenDeleted", BakDeleteModel.NO_DELETED));
		return criteria;
	}
}
