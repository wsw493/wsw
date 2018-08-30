package com.vortex.cloud.ums.dataaccess.dao.impl;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.vortex.cloud.ums.dataaccess.dao.ICloudFunctionGroupDao;
import com.vortex.cloud.ums.dto.CloudFunctionGroupDto;
import com.vortex.cloud.ums.model.CloudFunctionGroup;
import com.vortex.cloud.ums.support.ManagementConstant;
import com.vortex.cloud.vfs.data.hibernate.repository.SimpleHibernateRepository;
import com.vortex.cloud.vfs.data.model.BakDeleteModel;
import com.vortex.cloud.vfs.data.support.SearchFilter;
import com.vortex.cloud.vfs.data.support.SearchFilter.Operator;

/**
 * 功能组dao
 * 
 * @author lsm
 * @date 2016年4月1日
 */
@Repository("cloudFunctionGroupDao")
public class CloudFunctionGroupDaoImpl extends SimpleHibernateRepository<CloudFunctionGroup, String> implements ICloudFunctionGroupDao {

	@Resource
	private JdbcTemplate jdbcTemplate;

	@Override
	public DetachedCriteria getDetachedCriteria() {
		return defaultCriteria();
	}

	private DetachedCriteria defaultCriteria() {
		DetachedCriteria criteria = DetachedCriteria.forClass(getPersistentClass(), "cloudFunctionGroup");
		criteria.add(Restrictions.eq("beenDeleted", 0));
		return criteria;
	}

	@Override
	public <S extends CloudFunctionGroup> S save(S entity) {
		CloudFunctionGroup parent = super.findOne(entity.getParentId());
		if (parent == null) { // 顶级记录

			List<SearchFilter> filterList = new ArrayList<SearchFilter>();

			filterList.add(new SearchFilter("parentId", Operator.EQ, entity.getParentId()));
			filterList.add(new SearchFilter("beenDeleted", Operator.EQ, BakDeleteModel.NO_DELETED));

			List<CloudFunctionGroup> siblingList = super.findListByFilter(filterList, null);
			int siblingListSize = 0;
			if (CollectionUtils.isNotEmpty(siblingList)) {
				siblingListSize = siblingList.size();
			}

			entity.setNodeCode(StringUtils.EMPTY + new DecimalFormat("00").format(siblingListSize + 1));
		} else {
			parent.setChildSerialNumber(parent.getChildSerialNumber() + 1);
			super.update(parent);

			entity.setNodeCode(parent.getNodeCode() + new DecimalFormat("00").format(parent.getChildSerialNumber()));
		}

		entity.setChildSerialNumber(0);
		return super.save(entity);
	}

	@Override
	public CloudFunctionGroupDto findCloudFunctionGroupById(String id) {
		StringBuilder sql = new StringBuilder();
		sql.append("  SELECT                                               	   		");
		sql.append("  	a.*,                                                		");
		sql.append("  	b.`name` groupName,                                     	");
		sql.append("  	system.systemName cloudSystemName							");
		sql.append("  FROM                                                     		");
		sql.append("  	cloud_function_group a                                 		");
		sql.append("  		LEFT JOIN cloud_function_group b ON a.parentId = b.id			");
		sql.append("  		LEFT JOIN cloud_system system ON a.systemId = system.id	");
		sql.append("  WHERE                                                    				");
		sql.append("  	a.id = '" + id + "'          									");
		List<CloudFunctionGroupDto> lists = jdbcTemplate.query(sql.toString(), BeanPropertyRowMapper.newInstance(CloudFunctionGroupDto.class));
		return lists.get(0);
	}

	@Override
	public List<CloudFunctionGroup> getSourceList(String systemId, String defNodeCode) {
		String sql = "select * from cloud_function_group t where t.systemId='" + systemId + "' and t.nodeCode not like '" + defNodeCode + "%'";
		return jdbcTemplate.query(sql, BeanPropertyRowMapper.newInstance(CloudFunctionGroup.class));
	}

	@Override
	public CloudFunctionGroup getByCode(String systemId, String functionGroupCode) {
		List<SearchFilter> filterList = new ArrayList<SearchFilter>();

		filterList.add(new SearchFilter("systemId", Operator.EQ, systemId));
		filterList.add(new SearchFilter("code", Operator.EQ, functionGroupCode));

		List<CloudFunctionGroup> list = super.findListByFilter(filterList, null);
		if (CollectionUtils.isEmpty(list)) {
			return null;
		} else {
			return list.get(0);
		}
	}

	@Override
	public List<CloudFunctionGroup> getByParentId(String systemId, String parentId) {
		if (StringUtils.isEmpty(systemId) || StringUtils.isEmpty(parentId)) {
			return null;
		}

		String sql = "";
		if (parentId.equals("-1")) { // 第一级处理的时候，需要去掉注册业务系统的时候自动生成的系统管理员预设菜单组
			sql = "select * from cloud_function_group t where t.beenDeleted=0 and t.systemId='" + systemId + "' and t.parentId='" + parentId + "' and t.code <> '"
					+ ManagementConstant.SYS_ROOT_FUNCTION_GROUP + "'";
		} else {
			sql = "select * from cloud_function_group t where t.beenDeleted=0 and t.systemId='" + systemId + "' and t.parentId='" + parentId + "'";
		}

		return jdbcTemplate.query(sql, BeanPropertyRowMapper.newInstance(CloudFunctionGroup.class));
	}
}
