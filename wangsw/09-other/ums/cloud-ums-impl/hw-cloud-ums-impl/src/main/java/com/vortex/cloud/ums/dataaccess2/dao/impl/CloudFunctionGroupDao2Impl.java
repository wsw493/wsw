package com.vortex.cloud.ums.dataaccess2.dao.impl;

import java.io.Serializable;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.google.common.collect.Lists;
import com.vortex.cloud.ums.dataaccess2.dao.ICloudFunctionGroupDao2;
import com.vortex.cloud.ums.model.CloudFunctionGroup;
import com.vortex.cloud.vfs.data.model.BakDeleteModel;


@Repository("cloudFunctionGroupDao2")
public class CloudFunctionGroupDao2Impl implements ICloudFunctionGroupDao2 {

	@Resource(name = "jdbcTemplate2")
	private JdbcTemplate jdbcTemplate;

	@Resource(name = "sessionFactory2")
	private SessionFactory sessionFactory;

	@Override
	public Serializable save(CloudFunctionGroup bean) throws Exception {
		Session session = sessionFactory.getCurrentSession();
		Serializable rst =  session.save(bean);
		session.flush();
		return rst;
	}

	@Override
	public void update(CloudFunctionGroup bean) throws Exception {
		Session session = sessionFactory.getCurrentSession();
		session.update(bean);
		session.flush();
	}

	@Override
	public CloudFunctionGroup getFunctionGroupBySysidAndFgcode(String sysid, String fgcode) throws Exception {
		String sql = "select * from cloud_function_group a where a.systemId=? and a.code=? and a.beenDeleted = ?";

		List<Object> args = Lists.newArrayList();
		args.add(sysid);
		args.add(fgcode);
		args.add(BakDeleteModel.NO_DELETED);

		List<CloudFunctionGroup> list = jdbcTemplate.query(sql, args.toArray(), BeanPropertyRowMapper.newInstance(CloudFunctionGroup.class));

		if (CollectionUtils.isEmpty(list)) {
			return null;
		} else {
			return list.get(0);
		}
	}

	@Override
	public CloudFunctionGroup getBySerializableId(String serializableId) throws Exception {
		Session session = sessionFactory.getCurrentSession();
		return (CloudFunctionGroup)session.get(CloudFunctionGroup.class.getName(), serializableId);
	}

	@Override
	public String getMaxChildNodecode(String sysid, String parentId, String tableName) throws Exception {
		String sql = "select max(nodecode) from " + tableName + " a where a.parentId=? and a.systemId=? and a.beenDeleted = ?";
		List<Object> args = Lists.newArrayList();
		args.add(parentId);
		args.add(sysid);
		args.add(BakDeleteModel.NO_DELETED);
		List<String> list = jdbcTemplate.queryForList(sql, args.toArray(), String.class);

		if (CollectionUtils.isEmpty(list)) {
			return null;
		} else {
			return list.get(0);
		}
	}

	@Override
	public CloudFunctionGroup getById(String id) throws Exception {
		String sql = "select * from cloud_function_group a where a.id=? and a.beenDeleted = ?";
		List<Object> args = Lists.newArrayList();
		args.add(id);
		args.add(BakDeleteModel.NO_DELETED);
		List<CloudFunctionGroup> list = jdbcTemplate.query(sql, args.toArray(), BeanPropertyRowMapper.newInstance(CloudFunctionGroup.class));

		if (CollectionUtils.isEmpty(list)) {
			return null;
		} else {
			return list.get(0);
		}
	}
}
