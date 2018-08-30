package com.vortex.cloud.ums.dataaccess.dao.impl;

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

import com.google.common.collect.Lists;
import com.vortex.cloud.ums.dataaccess.dao.ICloudRoleDao;
import com.vortex.cloud.ums.dto.CloudRoleDto;
import com.vortex.cloud.ums.model.CloudRole;
import com.vortex.cloud.vfs.data.hibernate.repository.SimpleHibernateRepository;
import com.vortex.cloud.vfs.data.model.BakDeleteModel;
import com.vortex.cloud.vfs.data.support.SearchFilter;
import com.vortex.cloud.vfs.data.support.SearchFilter.Operator;





/**
 * 角色dao
 * 
 * @author lsm
 * @date 2016年4月1日
 */
@Repository("cloudRoleDao")
public class CloudRoleDaoImpl extends SimpleHibernateRepository<CloudRole, String> implements ICloudRoleDao {

	@Resource
	private JdbcTemplate jdbcTemplate;

	@Override
	public DetachedCriteria getDetachedCriteria() {
		return defaultCriteria();
	}

	private DetachedCriteria defaultCriteria() {
		DetachedCriteria criteria = DetachedCriteria.forClass(getPersistentClass(), "cloudRole");
		criteria.add(Restrictions.eq("beenDeleted", 0));
		return criteria;
	}

	@Override
	public boolean isCodeExists(String code, String systemId) {
		boolean rst = false;
		if (StringUtils.isEmpty(code) || StringUtils.isEmpty(systemId)) {
			return rst;
		}

		String sql = "select count(1) from cloud_role where code='" + code + "'" + " and systemId='" + systemId + "' and beenDeleted= " + BakDeleteModel.NO_DELETED;
		int count = jdbcTemplate.queryForObject(sql.toString(), Integer.class);
		if (count > 0) {
			rst = true;
		}

		return rst;
	}

	@Override
	public CloudRoleDto getById(String id) {
		StringBuffer sql = new StringBuffer();
		sql.append(" SELECT                                 ");
		sql.append(" 	r.*, g.`name` groupName             ");
		sql.append(" FROM                                   ");
		sql.append(" 	cloud_role r, cloud_role_group g	");
		sql.append(" WHERE                                  ");
		sql.append(" 	g.id = r.groupId                    ");
		sql.append(" 	AND r.id = ?                        ");

		List<String> args = Lists.newArrayList();
		args.add(id);

		return jdbcTemplate.queryForObject(sql.toString(), args.toArray(), BeanPropertyRowMapper.newInstance(CloudRoleDto.class));
	}

	@Override
	public CloudRole getRoleByCode(String roleCode, String systemCode) {
		StringBuffer sql = new StringBuffer();
		sql.append("        SELECT   DISTINCT                                                  ");
		sql.append("        	cr*                                                      ");
		sql.append("        FROM                                                       ");
		sql.append("        	cloud_role cr                                           ");
		sql.append("        	LEFT JOIN cloud_system s ON s.id = cr.systemId          ");
		sql.append("        WHERE                                                      ");
		sql.append("        	cr.`code` = '" + roleCode + "'                          ");
		sql.append("        	AND s.systemCode = '" + systemCode + "'   				");
		CloudRole cloudRole = jdbcTemplate.queryForObject(sql.toString(), BeanPropertyRowMapper.newInstance(CloudRole.class));
		return cloudRole;

	}

	@Override
	public List<CloudRole> getRolesByUserId(String userId) {
		StringBuffer sql = new StringBuffer();
		List<Object> argList = new ArrayList<>();

		sql.append("   SELECT                               ");
		sql.append("   	r.*                                 ");
		sql.append("   FROM                                 ");
		sql.append("   	cloud_role r, cloud_user_role cur	");
		sql.append("   WHERE                                ");
		sql.append("   	r.id = cur.roleId            		");
		sql.append("   	AND cur.userId = ?                  ");
		sql.append("   	AND cur.beenDeleted = ?                  ");
		argList.add(userId);
		argList.add(BakDeleteModel.NO_DELETED);

		return jdbcTemplate.query(sql.toString(), argList.toArray(), BeanPropertyRowMapper.newInstance(CloudRole.class));
	}

	@Override
	public CloudRole getRoleBySystemIdAndRoleCode(String systemId, String roleCode) {
		List<SearchFilter> filterList = new ArrayList<>();
		filterList.add(new SearchFilter("systemId", Operator.EQ, systemId));
		filterList.add(new SearchFilter("code", Operator.EQ, roleCode));
		List<CloudRole> list = findListByFilter(filterList, null);
		if (CollectionUtils.isEmpty(list)) {
			return null;
		} else {
			return list.get(0);
		}
	}

	@Override
	public List<String> getUserIdsByRole(String tenantId, String roleCode) {

		StringBuffer sql = new StringBuffer();
		List<Object> argList = new ArrayList<>();

		sql.append("                SELECT                                                        ");
		sql.append("                	 ur.userId                                                ");
		sql.append("                FROM                                                          ");
		sql.append("                	cloud_user_role ur,                                       ");
		sql.append("                	cloud_role r,                                             ");
		sql.append("                	cloud_system s                                            ");
		sql.append("                WHERE                                                         ");
		sql.append("                	ur.roleId = r.id                                          ");
		sql.append("                AND ur.beenDeleted =?                                         ");
		sql.append("                AND r.beenDeleted = ?                                         ");
		sql.append("                AND r.systemId = s.id                                         ");
		sql.append("                AND s.beenDeleted = ?                                         ");
		sql.append("                AND r.`code` = ?                                              ");
		sql.append("                AND s.tenantId = ?                                            ");
		argList.add(BakDeleteModel.NO_DELETED);
		argList.add(BakDeleteModel.NO_DELETED);
		argList.add(BakDeleteModel.NO_DELETED);
		argList.add(roleCode);
		argList.add(tenantId);
		return jdbcTemplate.queryForList(sql.toString(), argList.toArray(), String.class);
	}
	
	@Override
	public List<String> getUserIdsByRoleAndOrg(String orgId, String roleCode) {
		StringBuffer sql = new StringBuffer();
		List<Object> argList = new ArrayList<>();
		sql.append(" SELECT DISTINCT cu.id FROM cloud_staff cs , cloud_user cu , cloud_user_role ur, cloud_role r ");
		sql.append(" WHERE cs.id = cu.staffId AND cu.id = ur.userId  and ur.roleId = r.id  ");
		sql.append(" AND cs.beenDeleted  = ? AND cu.beenDeleted  = ? AND ur.beenDeleted = ?  AND r.beenDeleted = ? AND r.code = ? ");
		sql.append(" AND (cs.departmentId  = ? or cs.orgId = ? ) ");
		
		argList.add(BakDeleteModel.NO_DELETED);
		argList.add(BakDeleteModel.NO_DELETED);
		argList.add(BakDeleteModel.NO_DELETED);
		argList.add(BakDeleteModel.NO_DELETED);
		argList.add(roleCode);
		argList.add(orgId);
		argList.add(orgId);
		
		
		return jdbcTemplate.queryForList(sql.toString(), argList.toArray(), String.class);
	}
}
