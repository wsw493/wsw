package com.vortex.cloud.ums.dataaccess.dao.impl;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.google.common.collect.Lists;
import com.vortex.cloud.ums.dataaccess.dao.ITenantDivisionDao;
import com.vortex.cloud.ums.dto.IdNameDto;
import com.vortex.cloud.ums.enums.KafkaTopicEnum;
import com.vortex.cloud.ums.enums.SyncFlagEnum;
import com.vortex.cloud.ums.model.TenantDivision;
import com.vortex.cloud.ums.mq.produce.KafkaProducer;
import com.vortex.cloud.vfs.data.hibernate.repository.SimpleHibernateRepository;
import com.vortex.cloud.vfs.data.model.BakDeleteModel;
import com.vortex.cloud.vfs.data.support.SearchFilter;
import com.vortex.cloud.vfs.data.support.SearchFilter.Operator;

@Repository("tenantDivisionDao")
public class TenantDivisionDaoImpl extends SimpleHibernateRepository<TenantDivision, String> implements ITenantDivisionDao {
	@Resource
	private JdbcTemplate jdbcTemplate;

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
	public <S extends TenantDivision> S save(S entity) {
		// 如果是方法调用者是租户记录的创建（从模版中拷贝树），则直接存储
		if (StringUtils.isNotBlank(entity.getNodeCode())) {
			return super.save(entity);
		}

		TenantDivision parent = super.findOne(entity.getParentId());
		if (parent == null) { // 顶级记录
			List<SearchFilter> filterList = new ArrayList<SearchFilter>();
			filterList.add(new SearchFilter("enabled", Operator.EQ, 1));
			filterList.add(new SearchFilter("tenantId", Operator.EQ, entity.getTenantId()));
			filterList.add(new SearchFilter("parentId", Operator.EQ, entity.getParentId()));
			filterList.add(new SearchFilter("beenDeleted", Operator.EQ, BakDeleteModel.NO_DELETED));
			List<TenantDivision> siblingList = super.findListByFilter(filterList, null);
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
			TenantDivision entity = this.findOne(id);
			if (entity != null) {
				try {
					KafkaProducer.getInstance().produce(KafkaTopicEnum.UMS_TENANT_DIVISION_SYNC.getKey(), SyncFlagEnum.DELETE.getKey(), entity);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public List<TenantDivision> getAllChildren(TenantDivision parent) {
		DetachedCriteria criteria = getDetachedCriteria();

		criteria.add(Restrictions.eq("enabled", TenantDivision.ENABLED_YES));

		criteria.add(Restrictions.eq("parentId", parent.getId()));

		criteria.addOrder(Order.asc("orderIndex"));

		return super.findListByCriteria(criteria);
	}

	@Override
	public List<TenantDivision> getAllChildren(String parentId) {
		DetachedCriteria criteria = getDetachedCriteria();
		criteria.add(Restrictions.eq("enabled", TenantDivision.ENABLED_YES));
		criteria.add(Restrictions.eq("parentId", parentId));
		criteria.addOrder(Order.asc("orderIndex"));
		return super.findListByCriteria(criteria);
	}

	@Override
	public List<IdNameDto> getDivisionsByNames(String tenantId, List<String> names) throws Exception {
		if (StringUtils.isEmpty(tenantId) || CollectionUtils.isEmpty(names)) {
			return null;
		}

		String str = "";
		for (int i = 0; i < names.size(); i++) {
			if (i == 0) {
				str += "?";
			} else {
				str += ",?";
			}
		}

		String sql = "SELECT id,name from cloud_tenant_division a where a.`name` in(" + str + ") and a.tenantId=? and a.beenDeleted=0 GROUP BY a.`name` ORDER BY a.orderIndex";
		List<Object> argList = Lists.newArrayList();
		argList.addAll(names);
		argList.add(tenantId);
		return jdbcTemplate.query(sql.toString(), argList.toArray(), BeanPropertyRowMapper.newInstance(IdNameDto.class));
	}
}
