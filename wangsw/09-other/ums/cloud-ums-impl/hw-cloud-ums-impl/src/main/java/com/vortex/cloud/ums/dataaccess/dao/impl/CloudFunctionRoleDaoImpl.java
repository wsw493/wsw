package com.vortex.cloud.ums.dataaccess.dao.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.vortex.cloud.ums.dataaccess.dao.ICloudFunctionRoleDao;
import com.vortex.cloud.ums.dto.CloudFunctionRoleDto;
import com.vortex.cloud.ums.model.CloudFunctionRole;
import com.vortex.cloud.ums.util.orm.Page;
import com.vortex.cloud.ums.util.utils.QueryUtil;
import com.vortex.cloud.vfs.data.hibernate.repository.SimpleHibernateRepository;
import com.vortex.cloud.vfs.data.model.BakDeleteModel;
import com.vortex.cloud.vfs.data.util.StaticDBType;






/**
 * 功能和角色的对应dao
 * 
 * @author lsm
 * @date 2016年4月1日
 */
@SuppressWarnings("all")
@Repository("cloudFunctionRoleDao")
public class CloudFunctionRoleDaoImpl 
	extends SimpleHibernateRepository<CloudFunctionRole, String> 
		implements ICloudFunctionRoleDao {
	@Resource
	private JdbcTemplate jdbcTemplate;

	@Override
	public DetachedCriteria getDetachedCriteria() {
		return defaultCriteria();
	}

	private DetachedCriteria defaultCriteria() {
		DetachedCriteria criteria = DetachedCriteria.forClass(getPersistentClass(), "cloudFunctionRole");
		criteria.add(Restrictions.eq("beenDeleted", 0));
		return criteria;
	}

	@Override
	public Page<CloudFunctionRoleDto> getPageBySystem(String roleId, String systemId, Pageable pageable) {
		StringBuffer sql = new StringBuffer();
		sql.append(" SELECT ");
		sql.append("	cfr.`id` id, ");
		sql.append("	cf.`name` functionName, ");
		sql.append("	cfg.`name` functionGroupName ");
		sql.append(" FROM ");
		sql.append("	cloud_function_role cfr ");
		sql.append(" LEFT JOIN cloud_function cf ON cfr.functionId = cf.id ");
		sql.append(" LEFT JOIN cloud_function_group cfg ON cf.groupId = cfg.id ");
		sql.append(" WHERE ");
		sql.append("	cfr.roleId = '" + roleId + "'  ");
		sql.append("	AND cfr.beenDeleted = '0'       ");
		sql.append("	AND cf.systemId = '" + systemId + "'  ");
		sql.append(" ORDER BY cfg.orderIndex ASC, cf.orderIndex ASC ");
		
		// 得到总记录数
		String sqlCount = " select count(*) from (" + sql.toString() + ") a";
		int totalRecords = jdbcTemplate.queryForObject(sqlCount, Integer.class);
		
		// 组合分页条件
		String sqlString = QueryUtil.getPagingSql(sql.toString(), pageable.getPageNumber() * pageable.getPageSize(), (pageable.getPageNumber() + 1) * pageable.getPageSize(), StaticDBType.getDbType());
		List<CloudFunctionRoleDto> results = jdbcTemplate.query(sqlString, BeanPropertyRowMapper.newInstance(CloudFunctionRoleDto.class));
		
		Page<CloudFunctionRoleDto> page = new Page<CloudFunctionRoleDto>();
		page.setTotalRecords(totalRecords);
		page.setResult(results);
		
		return page;
	}

	@Override
	public List<CloudFunctionRoleDto> getListBySystem(String roleId, String systemId) {
		Map<String, Object> map = this.getSqlOfListBySystem(roleId, systemId);

		StringBuffer sql = (StringBuffer) map.get("sql");
		List<Object> args = (List<Object>) map.get("args");
		
		return jdbcTemplate.query(sql.toString(), args.toArray(), BeanPropertyRowMapper.newInstance(CloudFunctionRoleDto.class));
	}

	/**
	 * 组装SQL
	 * @param roleId
	 * @param systemId
	 * @return
	 */
	private Map<String, Object> getSqlOfListBySystem(String roleId, String systemId) {
		Map<String, Object> map = new HashMap<String, Object>();
		StringBuffer sql = new StringBuffer("");
		List<Object> args = new ArrayList<Object>();

		sql.append(" SELECT 											");
		sql.append("	cfr.* 											");
		sql.append(" FROM 												");
		sql.append("	cloud_function_role cfr, cloud_function cf 		");
		sql.append(" WHERE 												");
		sql.append("	cfr.functionId = cf.id 							");
		sql.append("	AND cfr.roleId = ?  							");
		sql.append("	AND cfr.beenDeleted = ?       					");
		sql.append("	AND cf.systemId = ?  							");
		
		args.add(roleId);
		args.add(BakDeleteModel.NO_DELETED);
		args.add(systemId);
		
		map.put("sql", sql);
		map.put("args", args);
		return map;
	}
}
