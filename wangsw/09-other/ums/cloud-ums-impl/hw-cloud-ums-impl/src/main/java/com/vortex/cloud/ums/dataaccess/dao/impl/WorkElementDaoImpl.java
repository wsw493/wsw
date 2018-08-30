package com.vortex.cloud.ums.dataaccess.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.google.common.collect.Lists;
import com.vortex.cloud.ums.dataaccess.dao.ICloudLogDao;
import com.vortex.cloud.ums.dataaccess.dao.IWorkElementDao;
import com.vortex.cloud.ums.dto.WorkElementPageDto;
import com.vortex.cloud.ums.model.CloudLog;
import com.vortex.cloud.ums.model.WorkElement;
import com.vortex.cloud.ums.util.utils.QueryUtil;
import com.vortex.cloud.vfs.common.exception.ServiceException;
import com.vortex.cloud.vfs.common.lang.StringUtil;
import com.vortex.cloud.vfs.common.mapper.JsonMapper;
import com.vortex.cloud.vfs.data.hibernate.repository.SimpleHibernateRepository;
import com.vortex.cloud.vfs.data.model.BakDeleteModel;
import com.vortex.cloud.vfs.data.util.StaticDBType;




/**
 * 图元dao
 * 
 * @author lsm
 *
 */
@Repository("workElementDao")
public class WorkElementDaoImpl extends SimpleHibernateRepository<WorkElement, String> implements IWorkElementDao {
	
	public static final long ZERO_LONG = 0L;
	@Resource
	private ICloudLogDao cloudLogDao;

	@Override
	public DetachedCriteria getDetachedCriteria() {
		return defaultCriteria();
	}

	@Resource
	private JdbcTemplate jdbcTemplate;

	private DetachedCriteria defaultCriteria() {
		DetachedCriteria criteria = DetachedCriteria.forClass(getPersistentClass(), "workElement");
		criteria.add(Restrictions.eq("beenDeleted", 0));
		return criteria;
	}

	@Override
	public <S extends WorkElement> S update(S entity) {
		this.updateLog(entity);
		return super.update(entity);
	}

	/**
	 * 绕过hibernate缓存，直接取得数据库中的图元数据
	 * 
	 * @param id
	 * @return
	 * @throws Exception
	 */
	private void updateLog(WorkElement entity) {
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT * FROM cloud_work_element WHERE id = ?");
		WorkElement workElement = jdbcTemplate.queryForObject(sql.toString(), new Object[] { entity.getId() }, new int[] { Types.VARCHAR }, new WorkElementRowMapper());
		if (null == workElement) {
			logger.error("不存在id为" + entity.getId() + "的数据");
			throw new ServiceException("不存在id为" + entity.getId() + "的数据");
		}

		JsonMapper jm = new JsonMapper();
		String jsonText = jm.toJson(workElement);

		Date now = new Date();
		CloudLog cloudLog = new CloudLog();
		cloudLog.setCalledMethod(this.toString() + "update");
		cloudLog.setEndTime(now);
		cloudLog.setHasPermission(1);
		cloudLog.setStartTime(now);
		cloudLog.setUserId(entity.getUserId());
		cloudLogDao.save(cloudLog);
	}

	private class WorkElementRowMapper implements RowMapper<WorkElement> {

		@Override
		public WorkElement mapRow(ResultSet rs, int rowNum) throws SQLException {
			WorkElement workElement = new WorkElement();
			workElement.setArea(rs.getDouble("f_area"));
			workElement.setBeenDeleted(rs.getInt("beenDeleted"));
			workElement.setCode(rs.getString("f_code"));
			workElement.setColor(rs.getString("f_color"));
			workElement.setCreateTime(rs.getTimestamp("createTime"));
			workElement.setDeletedTime(rs.getTimestamp("deletedTime"));
			workElement.setDepartmentId(rs.getString("f_deptId"));
			workElement.setDescription(rs.getString("f_description"));
			workElement.setId(rs.getString("id"));
			workElement.setLastChangeTime(rs.getTimestamp("lastChangeTime"));
			workElement.setLength(rs.getDouble("f_length"));
			workElement.setName(rs.getString("f_name"));
			workElement.setParams(rs.getString("f_params"));
			workElement.setParamsDone(rs.getString("f_params_done"));
			workElement.setRadius(rs.getDouble("f_radius"));
			workElement.setShape(rs.getString("f_shape"));
			workElement.setStatus(rs.getInt("status"));
			workElement.setTenantId(rs.getString("f_tenantId"));
			workElement.setUserId(rs.getString("f_user_id"));
			workElement.setWorkElementTypeId(rs.getString("f_work_element_type_id"));
			workElement.setDivisionId(rs.getString("f_division_id"));
			return workElement;
		}

	}

	@Override
	public boolean isCodeExists(String newCode, String tenantId) {
		StringBuffer sql = new StringBuffer();
		sql.append("   SELECT                                  ");
		sql.append("   	COUNT(1)                               ");
		sql.append("   FROM                                    ");
		sql.append("   	cloud_work_element bwe            ");
		sql.append("   WHERE                                   ");
		sql.append("   	bwe.f_code =        '" + newCode + "'                  ");
		sql.append("   AND bwe.f_tenantId =   '" + tenantId + "'             ");
		sql.append("   AND bwe.beenDeleted = 0                ");
		Integer count = jdbcTemplate.queryForObject(sql.toString(), Integer.class);
		return count > 0;
	}

	@Override
	public boolean isParamExists(String param, String value, String tenantId) {
		StringBuffer sql = new StringBuffer();
		sql.append("   SELECT                                  ");
		sql.append("   	COUNT(1)                               ");
		sql.append("   FROM                                    ");
		sql.append("   	cloud_work_element bwe            ");
		sql.append("   WHERE                                   ");
		if ("code".equals(param)) {
			sql.append("   	bwe.f_code =        '" + value + "'                  ");
		} else if ("name".equals(param)) {
			sql.append("   	bwe.f_name =        '" + value + "'                  ");
		}
		sql.append("   AND bwe.f_tenantId =   '" + tenantId + "'             ");
		sql.append("   AND bwe.beenDeleted = 0                ");
		Integer count = jdbcTemplate.queryForObject(sql.toString(), Integer.class);
		return count > 0;
	}

	@Override
	public List<WorkElementPageDto> syncWeByPage(String tenantId, long syncTime, Integer pageSize, Integer pageNumber) {
		StringBuffer sql = new StringBuffer();
		List<Object> args = Lists.newArrayList();
		sql.append(" SELECT bwe.id as id, bwe.f_name as name, bwe.f_shape as shape, f_deptId  as departmentId, f_params_done  as paramsDone, f_radius as radius, bwe.beenDeleted as beenDeleted,   (case  when (UNIX_TIMESTAMP(bwe.deletedTime)*1000> ? and bwe.beenDeleted = ? and UNIX_TIMESTAMP(bwe.createTime)<= ?) then 3  ");
		sql.append(" when (bwe.beenDeleted= ? and (UNIX_TIMESTAMP(bwe.createTime)*1000> ?)) then 1 ");
		sql.append(" when (UNIX_TIMESTAMP(bwe.lastChangeTime)*1000> ? and bwe.beenDeleted = ? and UNIX_TIMESTAMP(bwe.createTime)<= ?) then 2 else 1 end  ) as flag ");
		sql.append("  FROM cloud_work_element bwe WHERE bwe.f_tenantId = ? ");
		args.add(syncTime);
		args.add(BakDeleteModel.DELETED);
		args.add(syncTime);
		args.add(BakDeleteModel.NO_DELETED);
		args.add(syncTime);
		args.add(syncTime);
		args.add(BakDeleteModel.NO_DELETED);
		args.add(syncTime);
		args.add(tenantId);
		if (syncTime == ZERO_LONG) {
			sql.append(" AND bwe.beenDeleted = ?  ");
			args.add(BakDeleteModel.NO_DELETED);
		}
		if (syncTime != ZERO_LONG) {
			sql.append(" AND  ((UNIX_TIMESTAMP(bwe.createTime)*1000> ? and bwe.beenDeleted = ? ) ");
			sql.append(" or (UNIX_TIMESTAMP(bwe.lastChangeTime)*1000> ? and bwe.beenDeleted = ? and UNIX_TIMESTAMP(bwe.createTime)<= ? ) ");
			sql.append(" or (UNIX_TIMESTAMP(bwe.deletedTime)*1000> ? and bwe.beenDeleted = ? and UNIX_TIMESTAMP(bwe.createTime)<= ?)) ");
			args.add(syncTime);
			args.add(BakDeleteModel.NO_DELETED);
			args.add(syncTime);
			args.add(BakDeleteModel.NO_DELETED);
			args.add(syncTime);
			args.add(syncTime);
			args.add(BakDeleteModel.DELETED);
			args.add(syncTime);
		}
		
		sql.append(" ORDER BY bwe.createTime desc limit " +pageNumber*pageSize+","+pageSize+  "");
		
		return jdbcTemplate.query(sql.toString(), args.toArray(), BeanPropertyRowMapper.newInstance(WorkElementPageDto.class));
	}
	
	@Override
	public Page<WorkElement> syncWorkElementsByPage(Pageable pageable, Map<String, Object> paramMap) {
		// 租户id
		String tenantId = (String) paramMap.get("tenantId");
		Long syncTime = null;
		if (paramMap.containsKey("lastSyncTime")) {
			if (!StringUtil.isNullOrEmpty(String.valueOf(paramMap.get("lastSyncTime")))) {
				syncTime = Long.valueOf(String.valueOf(paramMap.get("lastSyncTime")));
			}
		}
		List<String> workElementTypeIds = (List<String>) paramMap.get("workElementTypeIds");

		List<Object> args = Lists.newArrayList();
		StringBuffer sql = new StringBuffer();
		sql.append("          SELECT bwe.* ");
		sql.append("          	FROM cloud_work_element bwe ");
		sql.append("          WHERE bwe.f_tenantId = ?                              ");
		if (CollectionUtils.isNotEmpty(workElementTypeIds)) {
			sql.append(" and bwe.f_work_element_type_id in ('" + StringUtils.join(workElementTypeIds, "','") + "')");
		}
		args.add(tenantId);
		if (null != syncTime && ZERO_LONG != syncTime) {

			sql.append(" AND  ((UNIX_TIMESTAMP(bwe.createTime)*1000> ? and bwe.beenDeleted = ? ) ");
			sql.append(
					" or (UNIX_TIMESTAMP(bwe.lastChangeTime)*1000> ? and bwe.beenDeleted = ? and UNIX_TIMESTAMP(bwe.createTime)<= ? ) ");
			sql.append(
					" or (UNIX_TIMESTAMP(bwe.deletedTime)*1000> ? and bwe.beenDeleted = ? and UNIX_TIMESTAMP(bwe.createTime)<= ?)) ");
			args.add(syncTime);
			args.add(BakDeleteModel.NO_DELETED);
			args.add(syncTime);
			args.add(BakDeleteModel.NO_DELETED);
			args.add(syncTime);
			args.add(syncTime);
			args.add(BakDeleteModel.DELETED);
			args.add(syncTime);
		}
		// 得到总记录数
		String sqlCnt = " SELECT COUNT(1) FROM ( " + sql.toString() + " ) t ";
		long totalCnt = jdbcTemplate.queryForObject(sqlCnt, args.toArray(), Integer.class);

		// 加入排序
		sql.append("order by bwe.createTime desc");

		// 组合分页条件
		Integer startRow = pageable.getPageNumber() * pageable.getPageSize();
		Integer endRow = (pageable.getPageNumber() + 1) * pageable.getPageSize();
		String sqlString = QueryUtil.getPagingSql(sql.toString(), startRow, endRow, StaticDBType.getDbType());

		List<WorkElement> pageList = jdbcTemplate.query(sqlString, args.toArray(),
				new WorkElementRowMapper());
		return new PageImpl<>(pageList, pageable, totalCnt);
	}
}
