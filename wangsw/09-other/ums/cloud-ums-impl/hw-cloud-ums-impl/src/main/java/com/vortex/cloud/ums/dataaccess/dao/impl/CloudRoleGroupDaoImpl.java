package com.vortex.cloud.ums.dataaccess.dao.impl;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.vortex.cloud.ums.dataaccess.dao.ICloudRoleGroupDao;
import com.vortex.cloud.ums.dto.CloudRoleGroupDto;
import com.vortex.cloud.ums.model.CloudRoleGroup;
import com.vortex.cloud.vfs.data.hibernate.repository.SimpleHibernateRepository;
import com.vortex.cloud.vfs.data.model.BakDeleteModel;
import com.vortex.cloud.vfs.data.support.SearchFilter;
import com.vortex.cloud.vfs.data.support.SearchFilter.Operator;

/**
 * 角色与组关系dao
 * 
 * @author lsm
 * @date 2016年4月1日
 */
@Repository("cloudRoleGroupDao")
public class CloudRoleGroupDaoImpl extends SimpleHibernateRepository<CloudRoleGroup, String> implements ICloudRoleGroupDao {

	@Resource
	private JdbcTemplate jdbcTemplate;

	@Override
	public DetachedCriteria getDetachedCriteria() {
		return defaultCriteria();
	}

	private DetachedCriteria defaultCriteria() {
		DetachedCriteria criteria = DetachedCriteria.forClass(getPersistentClass(), "cloudRoleGroup");
		criteria.add(Restrictions.eq("beenDeleted", 0));
		return criteria;
	}

	@Override
	public <S extends CloudRoleGroup> S save(S entity) {
		CloudRoleGroup parent = super.findOne(entity.getParentId());
		if (parent == null) { // 顶级记录
			List<SearchFilter> filterList = new ArrayList<SearchFilter>();

			filterList.add(new SearchFilter("parentId", Operator.EQ, entity.getParentId()));
			filterList.add(new SearchFilter("beenDeleted", Operator.EQ, BakDeleteModel.NO_DELETED));

			List<CloudRoleGroup> siblingList = super.findListByFilter(filterList, null);
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
	public CloudRoleGroupDto findRoleGroupAndGroupNameById(String id) {
		StringBuilder sql = new StringBuilder();
		sql.append("  SELECT                                               ");
		sql.append("  	a.*,                                               ");
		sql.append("  	b.`name` groupName                                     ");
		sql.append("  FROM                                                     ");
		sql.append("  	cloud_role_group a                                     ");
		sql.append("  LEFT JOIN cloud_role_group b ON a.parentId = b.id        ");
		sql.append("  WHERE                                                    ");
		sql.append("  	a.id = '" + id + "'          							");
		List<CloudRoleGroupDto> lists = jdbcTemplate.query(sql.toString(), BeanPropertyRowMapper.newInstance(CloudRoleGroupDto.class));
		return lists.get(0);
	}

}
