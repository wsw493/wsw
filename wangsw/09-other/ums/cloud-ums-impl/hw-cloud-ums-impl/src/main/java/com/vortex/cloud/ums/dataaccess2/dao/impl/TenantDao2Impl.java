package com.vortex.cloud.ums.dataaccess2.dao.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.google.common.collect.Lists;
import com.vortex.cloud.ums.dataaccess2.dao.ITenantDao2;
import com.vortex.cloud.ums.model.Tenant;
import com.vortex.cloud.vfs.data.model.BakDeleteModel;


@Repository("tenantDao2")
public class TenantDao2Impl implements ITenantDao2 {

	@Resource(name = "jdbcTemplate2")
	private JdbcTemplate jdbcTemplate;

	@Override
	public List<Tenant> findAll() {

		List<Object> args = Lists.newArrayList();
		StringBuffer sql = new StringBuffer();
		sql.append("    SELECT                              ");
		sql.append("    	*                               ");
		sql.append("    FROM                                ");
		sql.append("    	cloud_management_tenant  cmt                ");
		sql.append("    WHERE                               ");
		sql.append("	 cmt.beenDeleted = ?              ");
		args.add(BakDeleteModel.NO_DELETED);
		return jdbcTemplate.query(sql.toString(), args.toArray(), BeanPropertyRowMapper.newInstance(Tenant.class));

	}

}
