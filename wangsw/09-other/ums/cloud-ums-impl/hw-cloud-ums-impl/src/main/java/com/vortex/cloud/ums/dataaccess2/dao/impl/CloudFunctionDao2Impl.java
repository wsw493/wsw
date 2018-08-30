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
import com.vortex.cloud.ums.dataaccess2.dao.ICloudFunctionDao2;
import com.vortex.cloud.ums.dto.CloudFunctionDto;
import com.vortex.cloud.ums.model.CloudFunction;
import com.vortex.cloud.vfs.data.model.BakDeleteModel;


@Repository("cloudFunctionDao2")
public class CloudFunctionDao2Impl implements ICloudFunctionDao2 {
	@Resource(name = "jdbcTemplate2")
	private JdbcTemplate jdbcTemplate;
	
	@Resource(name = "sessionFactory2")
	private SessionFactory sessionFactory;

	@Override
	public Serializable save(CloudFunction bean) throws Exception {
		Session session = sessionFactory.getCurrentSession();
		Serializable rst =  session.save(bean);
		session.flush();
		return rst;
	}

	@Override
	public void update(CloudFunction bean) throws Exception {
		Session session = sessionFactory.getCurrentSession();
		session.update(bean);
		session.flush();
	}

	@Override
	public CloudFunction getFunctionBySysidAndFcode(String sysid, String fcode) throws Exception {
		String sql = "select * from cloud_function a where a.systemId=? and a.code=? and a.beenDeleted = ?";
		List<Object> args = Lists.newArrayList();
		args.add(sysid);
		args.add(fcode);
		args.add(BakDeleteModel.NO_DELETED);

		List<CloudFunction> list = jdbcTemplate.query(sql, args.toArray(), BeanPropertyRowMapper.newInstance(CloudFunction.class));

		if (CollectionUtils.isEmpty(list)) {
			return null;
		} else {
			return list.get(0);
		}
	}

	@Override
	public CloudFunctionDto getFunctionDtoBySysidAndFcode(String sysid, String fcode) throws Exception {
		String sql = "select * from cloud_function a where a.systemId=? and a.code=? and a.beenDeleted = ?";
		List<Object> args = Lists.newArrayList();
		args.add(sysid);
		args.add(fcode);
		args.add(BakDeleteModel.NO_DELETED);

		List<CloudFunctionDto> list = jdbcTemplate.query(sql, args.toArray(), BeanPropertyRowMapper.newInstance(CloudFunctionDto.class));

		if (CollectionUtils.isEmpty(list)) {
			return null;
		} else {
			return list.get(0);
		}
	}

	@Override
	public CloudFunction getById(String id) throws Exception {
		String sql = "select * from cloud_function a where a.id = ?";
		List<Object> args = Lists.newArrayList();
		args.add(id);

		List<CloudFunction> list = jdbcTemplate.query(sql, args.toArray(), BeanPropertyRowMapper.newInstance(CloudFunction.class));

		if (CollectionUtils.isEmpty(list)) {
			return null;
		} else {
			return list.get(0);
		}
	}
}
