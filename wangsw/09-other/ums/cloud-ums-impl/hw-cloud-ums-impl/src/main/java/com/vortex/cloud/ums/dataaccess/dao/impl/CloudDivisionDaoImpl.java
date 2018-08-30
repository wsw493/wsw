package com.vortex.cloud.ums.dataaccess.dao.impl;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Repository;

import com.vortex.cloud.ums.dataaccess.dao.ICloudDivisionDao;
import com.vortex.cloud.ums.model.CloudDivision;
import com.vortex.cloud.vfs.data.hibernate.repository.SimpleHibernateRepository;
import com.vortex.cloud.vfs.data.model.BakDeleteModel;
import com.vortex.cloud.vfs.data.support.SearchFilter;
import com.vortex.cloud.vfs.data.support.SearchFilter.Operator;

@Repository("cloudDivisionDao")
public class CloudDivisionDaoImpl extends SimpleHibernateRepository<CloudDivision, String> implements ICloudDivisionDao {

	@Override
	public DetachedCriteria getDetachedCriteria() {
		DetachedCriteria criteria = DetachedCriteria.forClass(getPersistentClass(), "division");
		criteria.add(Restrictions.eq("division.beenDeleted", BakDeleteModel.NO_DELETED));
		return criteria;
	}

	/**
	 * 重写。 实现：nodeCode的写入。nodeCode一级为2个整数位，即一级最多含99个记录。
	 */
	@Override
	public <S extends CloudDivision> S save(S entity) {
		CloudDivision parent = super.findOne(entity.getParentId());
		if (parent == null) { // 顶级记录
			List<SearchFilter> filterList = new ArrayList<SearchFilter>();
			filterList.add(new SearchFilter("enabled", Operator.EQ, 1));
			filterList.add(new SearchFilter("parentId", Operator.EQ, entity.getParentId()));
			List<CloudDivision> siblingList = super.findListByFilter(filterList, null);
			int siblingListSize = 0;
			if (CollectionUtils.isNotEmpty(siblingList)) {
				siblingListSize = siblingList.size();
			}

			entity.setNodeCode(StringUtils.EMPTY + new DecimalFormat("00").format(siblingListSize + 1));
		} else {
			parent.setChildSerialNumer(parent.getChildSerialNumer() + 1);
			super.update(parent);

			entity.setNodeCode(parent.getNodeCode() + new DecimalFormat("00").format(parent.getChildSerialNumer()));
		}

		entity.setChildSerialNumer(0);
		return super.save(entity);
	}

	@Override
	public void deleteByIds(String[] ids) {
		for (String id : ids) {
			deleteEntity(id);
		}
	}

	@Override
	public List<CloudDivision> getAllChildren(CloudDivision parent) {
		List<SearchFilter> filterList = new ArrayList<SearchFilter>();
		filterList.add(new SearchFilter("enabled", Operator.EQ, 1));
		filterList.add(new SearchFilter("parentId", Operator.EQ, parent.getId()));
		filterList.add(new SearchFilter("beenDeleted", Operator.EQ, 0));

		Sort sort = new Sort(Direction.ASC, "orderIndex", "commonCode");

		return this.findListByFilter(filterList, sort);
	}
}
