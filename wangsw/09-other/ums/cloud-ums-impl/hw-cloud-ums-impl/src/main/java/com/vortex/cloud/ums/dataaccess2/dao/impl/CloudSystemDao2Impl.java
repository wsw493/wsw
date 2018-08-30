package com.vortex.cloud.ums.dataaccess2.dao.impl;

import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.google.common.collect.Lists;
import com.vortex.cloud.ums.dataaccess2.dao.ICloudSystemDao2;
import com.vortex.cloud.ums.dto.CloudSystemDto;
import com.vortex.cloud.ums.model.CloudSystem;
import com.vortex.cloud.vfs.data.model.BakDeleteModel;

@Repository("cloudSystemDao2")
public class CloudSystemDao2Impl implements ICloudSystemDao2 {

	@Resource(name = "jdbcTemplate2")
	private JdbcTemplate jdbcTemplate;

	@Override
	public List<CloudSystem> getCloudSystems(String tenantId) {

		List<Object> args = Lists.newArrayList();
		StringBuffer sql = new StringBuffer();
		sql.append("    SELECT                              ");
		sql.append("    	*                               ");
		sql.append("    FROM                                ");
		sql.append("    	cloud_system  cs                ");
		sql.append("    WHERE                               ");
		sql.append("    	cs.tenantId = ?                 ");
		sql.append("    AND cs.beenDeleted = ?              ");
		args.add(tenantId);
		args.add(BakDeleteModel.NO_DELETED);
		return jdbcTemplate.query(sql.toString(), args.toArray(), BeanPropertyRowMapper.newInstance(CloudSystem.class));
	}

	@Override
	public CloudSystem getByCode(String sysCode) {
		String sql = "select * from cloud_system a where a.systemCode=? and a.beenDeleted=?";
		List<Object> args = Lists.newArrayList();
		args.add(sysCode);
		args.add(BakDeleteModel.NO_DELETED);

		List<CloudSystem> list = jdbcTemplate.query(sql.toString(), args.toArray(), BeanPropertyRowMapper.newInstance(CloudSystem.class));
		if (CollectionUtils.isEmpty(list)) {
			return null;
		} else {
			return list.get(0);
		}
	}

	@Override
	public CloudSystemDto getById(String systemId) {
		String sql = "select * from cloud_system where id=?";
		List<Object> args = Lists.newArrayList();
		args.add(systemId);

		List<CloudSystemDto> list = jdbcTemplate.query(sql.toString(), args.toArray(), BeanPropertyRowMapper.newInstance(CloudSystemDto.class));
		if (CollectionUtils.isEmpty(list)) {
			return null;
		} else {
			return list.get(0);
		}
	}
}
