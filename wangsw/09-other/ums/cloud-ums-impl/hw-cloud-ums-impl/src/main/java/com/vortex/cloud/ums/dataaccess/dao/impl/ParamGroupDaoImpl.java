package com.vortex.cloud.ums.dataaccess.dao.impl;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import com.vortex.cloud.ums.dataaccess.dao.IParamGroupDao;
import com.vortex.cloud.ums.model.PramGroup;
import com.vortex.cloud.vfs.data.hibernate.repository.SimpleHibernateRepository;
import com.vortex.cloud.vfs.data.model.BakDeleteModel;
import com.vortex.cloud.vfs.data.support.SearchFilter;
import com.vortex.cloud.vfs.data.support.SearchFilter.Operator;





@Repository("paramGroupDao")
public class ParamGroupDaoImpl extends SimpleHibernateRepository<PramGroup, String> 
	implements IParamGroupDao {
	
	@Override
	public DetachedCriteria getDetachedCriteria() {
		DetachedCriteria criteria = DetachedCriteria.forClass(getPersistentClass(), "parameterTypeGroup");
		criteria.add(Restrictions.eq("parameterTypeGroup.beenDeleted", BakDeleteModel.NO_DELETED));
		return criteria;
	}

	@Override
	public <S extends PramGroup> S save(S entity) {
		PramGroup parent = super.findOne(entity.getParentId());
		if(parent == null) { // 顶级记录
			List<SearchFilter> filterList = new ArrayList<SearchFilter>();
			filterList.add(new SearchFilter("parentId", Operator.EQ, entity.getParentId()));
			filterList.add(new SearchFilter("beenDeleted", Operator.EQ, BakDeleteModel.NO_DELETED));
			List<PramGroup> siblingList = super.findListByFilter(filterList, null);
			int siblingListSize = 0;
			if(CollectionUtils.isNotEmpty(siblingList)) {
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
}
