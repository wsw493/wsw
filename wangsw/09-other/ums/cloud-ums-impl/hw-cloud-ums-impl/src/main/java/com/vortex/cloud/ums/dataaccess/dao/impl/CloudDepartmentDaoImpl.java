package com.vortex.cloud.ums.dataaccess.dao.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.HibernateException;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.google.common.collect.Lists;
import com.vortex.cloud.ums.dataaccess.dao.ICloudDepartmentDao;
import com.vortex.cloud.ums.dataaccess.service.ICentralCacheRedisService;
import com.vortex.cloud.ums.dataaccess.service.impl.CentralCacheRedisServiceImpl;
import com.vortex.cloud.ums.dto.CloudDeptOrgDto;
import com.vortex.cloud.ums.dto.IdNameDto;
import com.vortex.cloud.ums.dto.TenantDeptOrgDto;
import com.vortex.cloud.ums.dto.TenantDeptOrgPageDto;
import com.vortex.cloud.ums.dto.TreeDto;
import com.vortex.cloud.ums.enums.CompanyTypeEnum;
import com.vortex.cloud.ums.model.CloudDepartment;
import com.vortex.cloud.ums.support.ManagementConstant;
import com.vortex.cloud.vfs.common.lang.StringUtil;
import com.vortex.cloud.vfs.data.hibernate.repository.SimpleHibernateRepository;
import com.vortex.cloud.vfs.data.model.BakDeleteModel;

@SuppressWarnings("all")
@Repository("cloudDepartmentDao")
public class CloudDepartmentDaoImpl extends SimpleHibernateRepository<CloudDepartment, String> implements ICloudDepartmentDao {
	public static final long ZERO_LONG = 0L;
	private static final Logger logger = LoggerFactory.getLogger(CloudDepartmentDaoImpl.class);

	@Resource
	private JdbcTemplate jdbcTemplate;
	@Resource(name = CentralCacheRedisServiceImpl.CLASSNAME)
	private ICentralCacheRedisService centralCacheRedisService;

	@Override
	public CloudDepartment save(CloudDepartment entity) {
		CloudDepartment _entity = super.save(entity);
		// 更新机构部门缓存
		updateRedis(new TenantDeptOrgDto().transfer(_entity));
		return _entity;
	}

	@Override
	public CloudDepartment update(CloudDepartment entity) {
		CloudDepartment _entity = super.update(entity);
		// 更新机构部门缓存
		updateRedis(new TenantDeptOrgDto().transfer(_entity));
		return _entity;
	}

	@Override
	public void delete(CloudDepartment entity) {
		super.delete(entity);
		// 更新机构部门缓存
		deleteRedis(entity.getTenantId(), entity.getId());
	}

	/**
	 * @Title: updateRedis @Description: 更新机构部门缓存 @return void @throws
	 */
	private void updateRedis(TenantDeptOrgDto entity) {
		long t0 = System.currentTimeMillis();
		if (StringUtil.isNullOrEmpty(entity.getTenantId())) {
			return;
		}
		String redisKey = ManagementConstant.REDIS_PRE_TENANT_DEPTORGIDS + ManagementConstant.REDIS_SEPARATOR + entity.getTenantId();
		// 获取租户下机构部门缓存的key
		List<String> id_list = centralCacheRedisService.getObject(redisKey, List.class);
		if (CollectionUtils.isEmpty(id_list)) {
			id_list = Lists.newArrayList();
		}
		if (!id_list.contains(entity.getId())) {// 添加未存在的key
			id_list.add(entity.getId());
		}
		// 更新租户下机构部门缓存key
		centralCacheRedisService.putObject(redisKey, id_list);
		// 更新机构部门缓存
		centralCacheRedisService.updateMapField(ManagementConstant.REDIS_PRE_MAP_DEPTORG, entity.getId(), entity);
		logger.error(String.format("[同步redis,部门变动]，总耗时：%sms", (System.currentTimeMillis() - t0)));
	}

	/**
	 * @Title: deleteRedis @Description: 更新机构部门缓存 @return void @throws
	 */
	private void deleteRedis(String tenantId, String id) {
		long t0 = System.currentTimeMillis();
		if (StringUtil.isNullOrEmpty(tenantId)) {
			return;
		}
		String redisKey = ManagementConstant.REDIS_PRE_TENANT_DEPTORGIDS + ManagementConstant.REDIS_SEPARATOR + tenantId;
		// 获取租户下机构部门缓存的key
		List<String> id_list = centralCacheRedisService.getObject(redisKey, List.class);
		if (CollectionUtils.isEmpty(id_list)) {
			id_list = Lists.newArrayList();
		}
		if (CollectionUtils.isNotEmpty(id_list) && id_list.contains(id)) {// 刪除已未存在的key
			id_list.remove(id);
		}
		// 更新租户下机构部门缓存key
		if (CollectionUtils.isEmpty(id_list)) {
			centralCacheRedisService.removeObject(redisKey);
		} else {
			centralCacheRedisService.putObject(redisKey, id_list);
		}
		// 删除机构部门缓存
		centralCacheRedisService.updateMapField(ManagementConstant.REDIS_PRE_MAP_DEPTORG, id, null);
		logger.error(String.format("[同步redis,部门刪除]，总耗时：%sms", (System.currentTimeMillis() - t0)));
	}

	@Override
	public DetachedCriteria getDetachedCriteria() {
		DetachedCriteria criteria = DetachedCriteria.forClass(getPersistentClass(), "dept");
		criteria.add(Restrictions.eq("beenDeleted", BakDeleteModel.NO_DELETED));

		return criteria;
	}

	@Override
	public List<TenantDeptOrgDto> findDeptOrgList(String tenantId, String deptId, List<Integer> beenDeletedFlags) {
		Map<String, Object> map = this.getDeptOrgList(tenantId, deptId, beenDeletedFlags);
		StringBuffer sql = (StringBuffer) map.get("sql");
		List<Object> argList = (List<Object>) map.get("argList");

		return jdbcTemplate.query(sql.toString(), argList.toArray(), BeanPropertyRowMapper.newInstance(TenantDeptOrgDto.class));
	}

	/**
	 * 如果没有指定单位ID，则取租户下的所有单位及下属组织机构； 如果指定了单位ID，则仅需要取该单位下的组织机构；
	 * 
	 * @param tenantId
	 * @param deptId
	 * @return
	 */
	private Map<String, Object> getDeptOrgList(String tenantId, String deptId, List<Integer> beenDeletedFlags) {
		StringBuffer sql = new StringBuffer();
		List<Object> argList = new ArrayList<>();

		if (StringUtils.isBlank(deptId)) {
			sql.append(" SELECT tenantId, -1 parentId, id departmentId, depType type,'" + CompanyTypeEnum.DEPART.getKey() + "' companyType   ,");
			sql.append(" 	id, depName name, depCode code, lngLats   , beenDeleted,deletedTime,createTime,lastChangeTime                 ");
			sql.append("     ,email,                                ");
			sql.append("     	address,                           ");
			sql.append("     	lngLats,                           ");
			sql.append("     	description,                       ");
			sql.append("     	headMobile,                        ");
			sql.append("     	head,orderIndex                               ");
			sql.append(" FROM cloud_department                                         ");
			sql.append(" WHERE tenantId = ?                                            ");
			argList.add(tenantId);
			if (CollectionUtils.isEmpty(beenDeletedFlags)) {
				sql.append(" 	AND beenDeleted = ?                                          ");
				argList.add(BakDeleteModel.NO_DELETED);
			} else {
				sql.append(" AND beenDeleted in ( ");
				for (int j = 0; j < beenDeletedFlags.size(); j++) {
					if (j != 0)
						sql.append(",");
					sql.append(" ? ");
				}
				sql.append(")");

				argList.addAll(beenDeletedFlags);
			}

			sql.append("                                                               ");
			sql.append(" 	UNION                                                        ");
		}

		sql.append("                                                               ");
		sql.append(" SELECT org.tenantId, org.parentId, org.departmentId, '3' type,   '" + CompanyTypeEnum.ORG.getKey() + "' companyType   ,"); // 3
		// -
		// CloudDepartmentTypeEnum.ORG
		sql.append(" 	org.id, org.orgName name, org.orgCode code, org.lngLats , org.beenDeleted,org.deletedTime,org.createTime,org.lastChangeTime                  ");
		sql.append("     ,org.email,                                ");
		sql.append("     	org.address,                           ");
		sql.append("     	org.lngLats,                           ");
		sql.append("     	org.description,                       ");
		sql.append("     	org.headMobile,                        ");
		sql.append("     	org.head,org.orderIndex                               ");
		sql.append(" FROM (                                                        ");
		sql.append(" 	SELECT org.*                                                 ");
		sql.append(" 	FROM cloud_organization org, cloud_department dept           ");
		sql.append(" 	WHERE                                                        ");
		sql.append(" 			org.tenantId = dept.tenantId                             ");
		sql.append(" 			AND org.departmentId = dept.id                           ");
		sql.append(" 			AND org.tenantId = ?                                     ");

		argList.add(tenantId);

		if (StringUtils.isNotBlank(deptId)) {
			sql.append(" 			AND org.departmentId = ?                                 ");
			argList.add(deptId);
		}
		if (CollectionUtils.isEmpty(beenDeletedFlags)) {
			sql.append(" 			AND org.beenDeleted = ?                                  ");
			sql.append(" 			AND dept.beenDeleted = ?                                 ");
			argList.add(BakDeleteModel.NO_DELETED);
			argList.add(BakDeleteModel.NO_DELETED);
		} else {
			sql.append(" AND org.beenDeleted in ( ");
			for (int j = 0; j < beenDeletedFlags.size(); j++) {
				if (j != 0)
					sql.append(",");
				sql.append(" ? ");
			}
			sql.append(")");
			argList.addAll(beenDeletedFlags);

			sql.append(" AND dept.beenDeleted in ( ");
			for (int j = 0; j < beenDeletedFlags.size(); j++) {
				if (j != 0)
					sql.append(",");
				sql.append(" ? ");
			}
			sql.append(")");
			argList.addAll(beenDeletedFlags);

		}

		sql.append(" ) org                                                         ");

		// 返回结果
		Map<String, Object> map = new HashMap<>();
		map.put("sql", sql);
		map.put("argList", argList);
		return map;
	}

	@Override
	public CloudDepartment getDepartmentByCode(String departmentCode, String tenantId) {

		StringBuffer sql = new StringBuffer();
		List<Object> argsList = new ArrayList<>();

		sql.append(" SELECT                         ");
		sql.append(" 	d.*                         ");
		sql.append(" FROM                           ");
		sql.append(" 	cloud_department d,        	");
		sql.append("    cloud_management_tenant t	");
		sql.append(" WHERE                          ");
		sql.append(" 	t.id = d.tenantId        	");
		sql.append("    AND d.depCode = ?          	");
		sql.append("    AND t.id = ?      	");

		argsList.add(departmentCode);
		argsList.add(tenantId);

		List<CloudDepartment> list = jdbcTemplate.query(sql.toString(), argsList.toArray(), BeanPropertyRowMapper.newInstance(CloudDepartment.class));
		if (CollectionUtils.isEmpty(list)) {
			String msg = "getDepartmentByCode(),未能根据[tenantId=" + tenantId + ",departmentCode=" + departmentCode + "]获取部门记录";
			logger.debug(msg);
			throw new HibernateException(msg);
		}

		return list.get(0);
	}

	@Override
	public List<TenantDeptOrgPageDto> syncDeptByPage(String tenantId, long syncTime, Integer pageSize, Integer pageNumber) {
		Map<String, Object> map = this.getSyncDeptOrgList(tenantId, syncTime, pageSize, pageNumber);
		StringBuffer sql = (StringBuffer) map.get("sql");
		List<Object> argList = (List<Object>) map.get("argList");
		return jdbcTemplate.query(sql.toString(), argList.toArray(), BeanPropertyRowMapper.newInstance(TenantDeptOrgPageDto.class));
	}

	private Map<String, Object> getSyncDeptOrgList(String tenantId, long syncTime, Integer pageSize, Integer pageNumber) {
		StringBuffer sql = new StringBuffer();
		List<Object> argList = new ArrayList<Object>();
		sql.append(" SELECT deptOrg.* from ( ");
		// 部门
		sql.append(" SELECT mm.*FROM ( SELECT dept.id as id, - 1 parentId,   dept.depName as name, dept.beenDeleted as beenDeleted , dept.orderIndex, ");
		sql.append("  (case  when (UNIX_TIMESTAMP(dept.deletedTime)*1000> ?  and dept.beenDeleted = ? and UNIX_TIMESTAMP(dept.createTime)<= ? ) then 3");
		sql.append(" when (dept.beenDeleted= ? and (UNIX_TIMESTAMP(dept.createTime)*1000> ? )) then 1 ");
		sql.append(" when (UNIX_TIMESTAMP(dept.lastChangeTime)*1000> ?  and dept.beenDeleted = ? and UNIX_TIMESTAMP(dept.createTime)<= ? ) then 2 else 1 end  ) as flag ");
		sql.append(" FROM cloud_department dept WHERE dept.tenantId = ?  ");
		argList.add(syncTime);
		argList.add(BakDeleteModel.DELETED);
		argList.add(syncTime);
		argList.add(BakDeleteModel.NO_DELETED);
		argList.add(syncTime);
		argList.add(syncTime);
		argList.add(BakDeleteModel.NO_DELETED);
		argList.add(syncTime);
		argList.add(tenantId);
		if (syncTime == ZERO_LONG) {// 传0获取所有未删除的
			sql.append(" AND dept.beenDeleted = ? ");
			argList.add(BakDeleteModel.NO_DELETED);
		}
		if (syncTime != ZERO_LONG) {
			sql.append(" AND ((UNIX_TIMESTAMP(dept.createTime)*1000> ? and dept.beenDeleted = ? ) ");
			sql.append(" or (UNIX_TIMESTAMP(dept.lastChangeTime)*1000> ? and dept.beenDeleted = ? and UNIX_TIMESTAMP(dept.createTime)<= ? ) ");
			sql.append(" or (UNIX_TIMESTAMP(dept.deletedTime)*1000> ?  and dept.beenDeleted = ? and UNIX_TIMESTAMP(dept.createTime)<= ? )) ");
			argList.add(syncTime);
			argList.add(BakDeleteModel.NO_DELETED);
			argList.add(syncTime);
			argList.add(BakDeleteModel.NO_DELETED);
			argList.add(syncTime);
			argList.add(syncTime);
			argList.add(BakDeleteModel.DELETED);
			argList.add(syncTime);
		}
		sql.append(" ) as  mm ");
		sql.append(" UNION ALL ");

		// 组织
		sql.append(" SELECT mm1.* FROM ( SELECT org.id as id, org.parentId as parentId, org.orgName as name, org.beenDeleted as  beenDeleted, org.orderIndex,");
		sql.append(" (case  when (UNIX_TIMESTAMP(org.deletedTime)*1000> ? and org.beenDeleted = ?  and UNIX_TIMESTAMP(org.createTime)<= ? ) then 3  ");
		sql.append("  when (org.beenDeleted= ? and (UNIX_TIMESTAMP(org.createTime)*1000> ? )) then 1 ");
		sql.append("  when (UNIX_TIMESTAMP(org.lastChangeTime)*1000> ?  and org.beenDeleted = ? and UNIX_TIMESTAMP(org.createTime)<= ? ) then 2 else 1 end  ) as flag ");
		sql.append(" FROM cloud_organization org, 	cloud_department dept WHERE org.tenantId = dept.tenantId AND org.departmentId = dept.id ");
		sql.append(" AND org.tenantId = ? ");
		argList.add(syncTime);
		argList.add(BakDeleteModel.DELETED);
		argList.add(syncTime);
		argList.add(BakDeleteModel.NO_DELETED);
		argList.add(syncTime);
		argList.add(syncTime);
		argList.add(BakDeleteModel.NO_DELETED);
		argList.add(syncTime);
		argList.add(tenantId);
		if (syncTime == ZERO_LONG) {// 传0获取所有未删除的
			sql.append(" AND dept.beenDeleted = ? AND org.beenDeleted = ? ");
			argList.add(BakDeleteModel.NO_DELETED);
			argList.add(BakDeleteModel.NO_DELETED);
		}
		if (syncTime != ZERO_LONG) {
			sql.append(" AND( (UNIX_TIMESTAMP(org.createTime)*1000> ?  and org.beenDeleted = ? ) ");
			sql.append("  or (UNIX_TIMESTAMP(org.lastChangeTime)*1000> ? and org.beenDeleted = ? and UNIX_TIMESTAMP(org.createTime)<= ? ) ");
			sql.append("  or (UNIX_TIMESTAMP(org.deletedTime)*1000> ? and org.beenDeleted = ? and UNIX_TIMESTAMP(org.createTime)<= ? )) ");
			argList.add(syncTime);
			argList.add(BakDeleteModel.NO_DELETED);
			argList.add(syncTime);
			argList.add(BakDeleteModel.NO_DELETED);
			argList.add(syncTime);
			argList.add(syncTime);
			argList.add(BakDeleteModel.DELETED);
			argList.add(syncTime);
		}

		sql.append(" ) as mm1 ) as deptOrg 	ORDER BY deptOrg.id desc  LIMIT " + pageNumber * pageSize + "," + pageSize + "");

		// 返回结果
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("sql", sql);
		map.put("argList", argList);
		return map;
	}

	@Override
	public List<TenantDeptOrgDto> findDeptList(String tenantId, String deptId) {
		StringBuffer sql = new StringBuffer();
		List<Object> argList = new ArrayList<>();

		if (StringUtils.isBlank(deptId)) {
			sql.append(" SELECT tenantId, -1 parentId, id departmentId, depType type,  ");
			sql.append(" 	id, depName name, depCode code  ,beenDeleted,deletedTime,createTime,lastChangeTime                              ");
			sql.append("     ,email,                                ");
			sql.append("     	address,                           ");
			sql.append("     	lngLats,                           ");
			sql.append("     	description,                       ");
			sql.append("     	headMobile,                        ");
			sql.append("     	head,orderIndex                               ");
			sql.append(" FROM cloud_department                                         ");
			sql.append(" WHERE tenantId = ?                                            ");
			sql.append(" 	AND beenDeleted = ?                                          ");
			argList.add(tenantId);
			argList.add(BakDeleteModel.NO_DELETED);
		}
		return jdbcTemplate.query(sql.toString(), argList.toArray(), BeanPropertyRowMapper.newInstance(TenantDeptOrgDto.class));
	}

	@Override
	public boolean hasStaff(String departmentId) {
		String sql = "select count(1) from cloud_staff t where t.departmentId=? and t.beenDeleted=?";
		List<Object> argList = new ArrayList<>();
		argList.add(departmentId);
		argList.add(BakDeleteModel.NO_DELETED);

		Long count = jdbcTemplate.queryForObject(sql, argList.toArray(), Long.class);

		if (count > 0) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean hasOrg(String departmentId) {
		String sql = "select count(1) from cloud_organization t where t.departmentId=? and t.beenDeleted=?";
		List<Object> argList = new ArrayList<>();
		argList.add(departmentId);
		argList.add(BakDeleteModel.NO_DELETED);

		Long count = jdbcTemplate.queryForObject(sql, argList.toArray(), Long.class);

		if (count > 0) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public CloudDepartment findById(String id, List<Integer> beenDeletedFlags) {
		StringBuffer sql = new StringBuffer();
		List<Object> argList = new ArrayList<>();
		sql.append(" SELECT * FROM cloud_department WHERE id = ?  ");
		argList.add(id);
		if (CollectionUtils.isEmpty(beenDeletedFlags)) {
			sql.append(" AND  beenDeleted = ?  ");
			argList.add(BakDeleteModel.NO_DELETED);
		} else {
			sql.append(" AND beenDeleted in ( ");
			for (int j = 0; j < beenDeletedFlags.size(); j++) {
				if (j != 0)
					sql.append(",");
				sql.append(" ? ");
			}
			sql.append(")");
			argList.addAll(beenDeletedFlags);
		}

		List<CloudDepartment> cloudDepartmentList = jdbcTemplate.query(sql.toString(), argList.toArray(), BeanPropertyRowMapper.newInstance(CloudDepartment.class));
		if (CollectionUtils.isNotEmpty(cloudDepartmentList)) {
			return cloudDepartmentList.get(0);
		}
		return null;
	}

	@Override
	public List<IdNameDto> findChildren(String tenantId, String id) throws Exception {
		if (StringUtils.isEmpty(id) && StringUtils.isEmpty(tenantId)) {
			return null;
		}

		StringBuffer sql = new StringBuffer();
		List<Object> argList = new ArrayList<>();
		if (StringUtils.isEmpty(id)) {
			sql.append("select id,depName name from cloud_department where tenantId=? and beenDeleted=? order by orderIndex");
			argList.add(tenantId);
			argList.add(BakDeleteModel.NO_DELETED);
		} else {
			sql.append(" select id,orgName name from cloud_organization where parentId=? and beenDeleted=? ORDER BY orderIndex ");
			argList.add(id);
			argList.add(BakDeleteModel.NO_DELETED);
		}

		return jdbcTemplate.query(sql.toString(), argList.toArray(), BeanPropertyRowMapper.newInstance(IdNameDto.class));
	}

	@Override
	public List<TreeDto> listByTenantId(String tenantId) throws Exception {
		if (StringUtils.isEmpty(tenantId)) {
			return null;
		}

		StringBuffer sql = new StringBuffer();
		sql.append(" select id,depName name,-1 parentId ");
		sql.append(" from cloud_department t ");
		sql.append(" where t.beenDeleted=? ");
		sql.append("   and t.tenantId=? ");
		sql.append(" UNION ALL ");
		sql.append(" select id,orgName name,parentId ");
		sql.append(" from cloud_organization t ");
		sql.append(" where t.beenDeleted=? ");
		sql.append("   and t.tenantId=? ");

		List<Object> args = new ArrayList<Object>();
		args.add(BakDeleteModel.NO_DELETED);
		args.add(tenantId);
		args.add(BakDeleteModel.NO_DELETED);
		args.add(tenantId);

		return jdbcTemplate.query(sql.toString(), args.toArray(), BeanPropertyRowMapper.newInstance(TreeDto.class));
	}

	@Override
	public List<CloudDeptOrgDto> listByIds(List<String> ids) throws Exception {
		if (CollectionUtils.isEmpty(ids)) {
			return null;
		}

		String idstr = "";
		for (int i = 0; i < ids.size(); i++) {
			if (i == 0) {
				idstr += "?";
			} else {
				idstr += ",?";
			}
		}

		String sql = "select a.*,'-1' parentId from cloud_department a where a.id in (" + idstr + ")";
		List<Object> args = new ArrayList<Object>();
		args.addAll(ids);
		return jdbcTemplate.query(sql.toString(), args.toArray(), BeanPropertyRowMapper.newInstance(CloudDeptOrgDto.class));
	}
}
