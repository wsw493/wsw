package com.vortex.cloud.ums.dataaccess.dao.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.vortex.cloud.ums.dataaccess.dao.ICloudUserRoleDao;
import com.vortex.cloud.ums.dto.CloudUserRoleDto;
import com.vortex.cloud.ums.dto.CloudUserRoleSearchDto;
import com.vortex.cloud.ums.model.CloudUserRole;
import com.vortex.cloud.ums.util.utils.QueryUtil;
import com.vortex.cloud.vfs.common.exception.ServiceException;
import com.vortex.cloud.vfs.data.hibernate.repository.SimpleHibernateRepository;
import com.vortex.cloud.vfs.data.model.BakDeleteModel;
import com.vortex.cloud.vfs.data.util.StaticDBType;

/**
 * 用户角色dao
 * 
 * @author lsm
 * @date 2016年4月1日
 */
@SuppressWarnings("all")
@Repository("cloudUserRoleDao")
public class CloudUserRoleDaoImpl extends SimpleHibernateRepository<CloudUserRole, String> implements ICloudUserRoleDao {

	@Resource
	private JdbcTemplate jdbcTemplate;

	@Override
	public DetachedCriteria getDetachedCriteria() {
		return defaultCriteria();
	}

	private DetachedCriteria defaultCriteria() {
		DetachedCriteria criteria = DetachedCriteria.forClass(getPersistentClass(), "cloudUserRole");
		criteria.add(Restrictions.eq("beenDeleted", BakDeleteModel.NO_DELETED));
		return criteria;
	}

	@Override
	public Page<CloudUserRoleDto> findPageBySearchDto(Pageable pageable, CloudUserRoleSearchDto searchDto) {
		// 校验输入的搜索条件
		this.checkSearchDto(searchDto);

		Map<String, Object> map = this.getSqlOfPageBySearchDto(searchDto);
		StringBuffer sql = (StringBuffer) map.get("sql");
		List<Object> argList = (List<Object>) map.get("argList");

		// 得到总记录数
		String sqlCnt = " SELECT COUNT(1) FROM ( " + sql.toString() + " ) t ";
		long totalCnt = jdbcTemplate.queryForObject(sqlCnt, argList.toArray(), Integer.class);

		// 组合分页条件
		Integer startRow = pageable.getPageNumber() * pageable.getPageSize();
		Integer endRow = (pageable.getPageNumber() + 1) * pageable.getPageSize();
		String sqlString = QueryUtil.getPagingSql(sql.toString(), startRow, endRow, StaticDBType.getDbType());

		List<CloudUserRoleDto> pageList = jdbcTemplate.query(sqlString, argList.toArray(), BeanPropertyRowMapper.newInstance(CloudUserRoleDto.class));

		return new PageImpl<CloudUserRoleDto>(pageList, pageable, totalCnt);
	}

	private void checkSearchDto(CloudUserRoleSearchDto searchDto) {
		if (StringUtils.isBlank(searchDto.getUserId())) {
			throw new ServiceException("用户ID为空！");
		}
	}

	private Map<String, Object> getSqlOfPageBySearchDto(CloudUserRoleSearchDto searchDto) {
		StringBuffer sql = new StringBuffer();
		List<Object> argList = new ArrayList<Object>();

		sql.append(" SELECT ur.id id, u.id userId, u.userName userName,                       ");
		sql.append(" 	r.id roleId, r.name roleName,                                           ");
		sql.append(" 	rg.id roleGroupId, rg.name roleGroupName                                ");
		sql.append(" FROM cloud_user u, cloud_role r, cloud_user_role ur, cloud_role_group rg ");
		sql.append(" WHERE                                                                    ");
		sql.append(" 	u.id = ?                               									");
		sql.append(" 	AND u.beenDeleted = ?                                                   ");
		sql.append(" 	AND u.id = ur.userId                                                    ");
		sql.append(" 	AND r.id = ur.roleId                                                    ");
		sql.append(" 	AND r.beenDeleted = ?                                                   ");
		sql.append(" 	AND ur.beenDeleted = ?                                                  ");
		sql.append(" 	AND r.groupId = rg.id                                                   ");
		sql.append(" 	AND rg.beenDeleted = ?                                                  ");
		sql.append("order by r.orderIndex , r.name");

		argList.add(searchDto.getUserId());
		argList.add(BakDeleteModel.NO_DELETED);
		argList.add(BakDeleteModel.NO_DELETED);
		argList.add(BakDeleteModel.NO_DELETED);
		argList.add(BakDeleteModel.NO_DELETED);

		if (logger.isDebugEnabled()) {
			logger.debug("getSqlOfPageBySearchDto()," + sql.toString());
		}

		// 返回结果
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("sql", sql);
		map.put("argList", argList);
		return map;
	}
}
