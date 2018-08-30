package com.vortex.cloud.ums.dataaccess.dao.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.google.common.collect.Lists;
import com.vortex.cloud.ums.dataaccess.dao.ICloudDepartmentDao;
import com.vortex.cloud.ums.dataaccess.dao.ICloudOrganizationDao;
import com.vortex.cloud.ums.dataaccess.dao.ICloudStaffDao;
import com.vortex.cloud.ums.dataaccess.service.ICentralCacheRedisService;
import com.vortex.cloud.ums.dataaccess.service.impl.CentralCacheRedisServiceImpl;
import com.vortex.cloud.ums.dto.CloudStaffDto;
import com.vortex.cloud.ums.dto.CloudStaffPageDto;
import com.vortex.cloud.ums.dto.CloudStaffSearchDto;
import com.vortex.cloud.ums.dto.StaffDto;
import com.vortex.cloud.ums.dto.TenantDeptOrgDto;
import com.vortex.cloud.ums.dto.rest.CloudStaffRestDto;
import com.vortex.cloud.ums.model.CloudOrganization;
import com.vortex.cloud.ums.model.CloudStaff;
import com.vortex.cloud.ums.support.ManagementConstant;
import com.vortex.cloud.ums.util.PropertyUtils;
import com.vortex.cloud.ums.util.utils.QueryUtil;
import com.vortex.cloud.vfs.common.lang.StringUtil;
import com.vortex.cloud.vfs.data.hibernate.repository.SimpleHibernateRepository;
import com.vortex.cloud.vfs.data.model.BakDeleteModel;
import com.vortex.cloud.vfs.data.util.StaticDBType;

/**
 * 个人信息dao
 * 
 * @author lsm
 * @date 2016年4月1日
 */
@SuppressWarnings("all")
@Repository("cloudStaffDao")
public class CloudStaffDaoImpl extends SimpleHibernateRepository<CloudStaff, String> implements ICloudStaffDao {
	public static final long ZERO_LONG = 0L;
	@Resource
	private JdbcTemplate jdbcTemplate;
	public static final String FALSE = "false";
	public static final String TRUE = "true";
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	@Resource
	private ICloudDepartmentDao cloudDepartmentDao;
	@Resource
	private ICloudOrganizationDao cloudOrganizationDao;
	@Resource(name = CentralCacheRedisServiceImpl.CLASSNAME)
	private ICentralCacheRedisService centralCacheRedisService;

	@Override
	public CloudStaff save(CloudStaff entity) {
		CloudStaff _entity = super.save(entity);
		// 更新人员缓存
		updateRedis(_entity);
		return _entity;
	}

	@Override
	public CloudStaff update(CloudStaff entity) {
		CloudStaff _entity = super.update(entity);
		// 更新人员缓存
		updateRedis(_entity);
		return _entity;
	}

	@Override
	public void delete(CloudStaff entity) {
		super.delete(entity);
		// 更新人员缓存
		deleteRedis(entity.getTenantId(), entity.getId());
	}

	/**
	 * @Title: updateRedis @Description: 更新人员缓存 @return void @throws
	 */
	private void updateRedis(CloudStaff entity) {
		long t0 = System.currentTimeMillis();
		if (StringUtil.isNullOrEmpty(entity.getTenantId())) {
			return;
		}
		String redisKey = ManagementConstant.REDIS_PRE_TENANT_STAFFIDS + ManagementConstant.REDIS_SEPARATOR
				+ entity.getTenantId();
		// 获取租户下人员缓存的key
		List<String> id_list = centralCacheRedisService.getObject(redisKey, List.class);
		if (CollectionUtils.isEmpty(id_list)) {
			id_list = Lists.newArrayList();
		}
		if (!id_list.contains(entity.getId())) {// 添加未存在的key
			id_list.add(entity.getId());
		}
		// 更新租户下人员缓存key
		centralCacheRedisService.putObject(redisKey, id_list);
		// 更新人员缓存
		centralCacheRedisService.updateMapField(ManagementConstant.REDIS_PRE_MAP_STAFF, entity.getId(), entity);
		logger.error(String.format("[同步redis,人员变动]，总耗时：%sms", (System.currentTimeMillis() - t0)));
	}

	/**
	 * @Title: deleteRedis @Description: 更新人员缓存 @return void @throws
	 */
	private void deleteRedis(String tenantId, String id) {
		long t0 = System.currentTimeMillis();
		if (StringUtil.isNullOrEmpty(tenantId)) {
			return;
		}
		String redisKey = ManagementConstant.REDIS_PRE_TENANT_STAFFIDS + ManagementConstant.REDIS_SEPARATOR + tenantId;
		// 获取租户下人员缓存的key
		List<String> id_list = centralCacheRedisService.getObject(redisKey, List.class);
		if (CollectionUtils.isEmpty(id_list)) {
			id_list = Lists.newArrayList();
		}
		if (CollectionUtils.isNotEmpty(id_list) && id_list.contains(id)) {// 刪除已未存在的key
			id_list.remove(id);
		}
		// 更新租户下人员缓存key
		if (CollectionUtils.isEmpty(id_list)) {
			centralCacheRedisService.removeObject(redisKey);
		} else {
			centralCacheRedisService.putObject(redisKey, id_list);
		}
		// 删除人员缓存
		centralCacheRedisService.updateMapField(ManagementConstant.REDIS_PRE_MAP_STAFF, id, null);
		logger.error(String.format("[同步redis,人员删除]，总耗时：%sms", (System.currentTimeMillis() - t0)));
	}

	@Override
	public DetachedCriteria getDetachedCriteria() {
		return defaultCriteria();
	}

	private DetachedCriteria defaultCriteria() {
		DetachedCriteria criteria = DetachedCriteria.forClass(getPersistentClass(), "cloudStaff");
		criteria.add(Restrictions.eq("beenDeleted", BakDeleteModel.NO_DELETED));
		return criteria;
	}

	@Override
	public Page<CloudStaffDto> findPageBySearchDto(Pageable pageable, CloudStaffSearchDto searchDto) {

		Map<String, Object> map = this.getSqlOfPageBySearchDto(searchDto);
		StringBuffer sql = (StringBuffer) map.get("sql");
		List<Object> argList = (List<Object>) map.get("argList");

		// 得到总记录数
		String sqlCnt = " SELECT COUNT(1) FROM ( " + sql.toString() + " ) t ";
		long totalCnt = jdbcTemplate.queryForObject(sqlCnt, argList.toArray(), Integer.class);

		// 加入排序
		this.addOrder("s", sql, pageable.getSort());

		// 组合分页条件
		Integer startRow = pageable.getPageNumber() * pageable.getPageSize();
		Integer endRow = (pageable.getPageNumber() + 1) * pageable.getPageSize();
		String sqlString = QueryUtil.getPagingSql(sql.toString(), startRow, endRow, StaticDBType.getDbType());

		List<CloudStaffDto> pageList = jdbcTemplate.query(sqlString, argList.toArray(),
				BeanPropertyRowMapper.newInstance(CloudStaffDto.class));

		return new PageImpl<>(pageList, pageable, totalCnt);
	}

	private Map<String, Object> getSqlOfPageBySearchDto(CloudStaffSearchDto searchDto) {
		StringBuffer sql = new StringBuffer();
		List<Object> argList = new ArrayList<>();

		sql.append(
				"       SELECT                                                                                          ");

		sql.append("            s.id                                                          ");
		sql.append("            ,s.createTime                                                 ");
		sql.append("            ,s.lastChangeTime                                             ");
		sql.append("            ,s.status                                                     ");
		sql.append("            ,s.beenDeleted                                                ");
		sql.append("            ,s.deletedTime                                                ");
		sql.append("            ,s.authorizeId                                                ");
		sql.append("            ,s.authorizeName                                              ");
		sql.append("            ,s.birthPlace                                                 ");
		sql.append("            ,s.birthday                                                   ");
		sql.append("            ,s.code                                                       ");
		sql.append("            ,s.credentialNum                                              ");
		sql.append("            ,s.credentialType                                             ");
		sql.append("            ,s.departmentId                                               ");
		sql.append("            ,s.description                                                ");
		sql.append("            ,s.educationId                                                ");
		sql.append("            ,s.educationName                                              ");
		sql.append("            ,s.email                                                      ");
		sql.append("            ,s.entryHereTime                                              ");
		sql.append("            ,s.gender                                                     ");
		sql.append("            ,s.graduate                                                   ");
		sql.append("            ,s.healthId                                                   ");
		sql.append("            ,s.healthName                                                 ");
		sql.append("            ,s.idCard                                                     ");
		sql.append("            ,s.innerEmail                                                 ");
		sql.append("            ,s.isLeave                                                    ");
		sql.append("            ,s.joinWorkTime                                               ");
		sql.append("            ,s.leaveTime                                                  ");
		sql.append("            ,s.livePlace                                                  ");
		sql.append("            ,s.maritalStatusId                                            ");
		sql.append("            ,s.maritalStatusName                                          ");
		sql.append("            ,s.`name`                                                         ");
		sql.append("            ,s.nationId                                                   ");
		sql.append("            ,s.nationName                                                 ");
		sql.append("            ,s.officeTel                                                  ");
		sql.append("            ,s.orgId                                                      ");
		sql.append("            ,s.partyPostId                                                ");

		sql.append("            ,s.phone                                                      ");
		sql.append("            ,s.politicalStatusId                                          ");
		sql.append("            ,s.politicalStatusName                                        ");
		sql.append("            ,s.postId                                                     ");
		sql.append("            ,s.presentPlace                                               ");
		sql.append("            ,s.socialSecurityNo                                           ");
		sql.append("            ,s.socialSecuritycase                                         ");
		sql.append("            ,s.tenantId                                                   ");
		sql.append("            ,s.workYearLimit                                              ");
		sql.append("            ,s.orgName                                                    ");

		sql.append("            ,s.orderIndex                                                 ");
		sql.append("            ,s.workTypeCode                                               ");
		sql.append("            ,s.outSourcing                                                ");
		sql.append("            ,s.outSourcingComp                                            ");
		sql.append(
				"       	     , u.id userId,                                                                             ");
		sql.append("        u.userName userName ,s.postName ,  cps.parmName   partyPostName   ");
		sql.append(
				"                                                                                                       ");
		sql.append(
				"       FROM                                                                                            ");
		sql.append(
				"       	cloud_staff s                                                                                  ");
		sql.append(
				"       LEFT JOIN cloud_user u ON s.id = u.staffId                                                      ");
		sql.append(
				"       AND u.beenDeleted = ?                                                                           ");
		sql.append("       LEFT JOIN cloud_parameter_type cpt ON  cpt.typeCode=?                       ");
		sql.append(
				"       AND cpt.beenDeleted = ?                                                                         ");
		sql.append(
				"       LEFT JOIN cloud_tenantparameter_setting cps ON cps.typeId = cpt.id AND cps.parmCode = s.partyPostId   AND cps.tenantId=s.tenantId     ");
		sql.append(
				"       AND cps.beenDeleted = ?                                                                         ");
		sql.append(
				"                                                                                                       ");
		sql.append(
				"       WHERE                                                                                           ");
		sql.append("       	s.tenantId =?                                                ");
		sql.append(
				"       AND s.beenDeleted = ?                                                                          ");
		argList.add(BakDeleteModel.NO_DELETED);
		// 人员职位参数类型值
		argList.add(PropertyUtils.getPropertyValue("STAFF_POST"));
		argList.add(BakDeleteModel.NO_DELETED);
		argList.add(BakDeleteModel.NO_DELETED);
		argList.add(searchDto.getTenantId());
		argList.add(BakDeleteModel.NO_DELETED);

		String ckRange = searchDto.getCkRange();
		if (FALSE.equals(ckRange)) {
			String departmentId = searchDto.getDepartmentId();
			if (StringUtils.isNotBlank(departmentId)) {
				sql.append(" 	AND s.departmentId = ?                             ");
				argList.add(departmentId);
			}

			String orgId = searchDto.getOrgId();
			if (StringUtils.isNotBlank(orgId)) {
				sql.append(" 	AND s.orgId = ?                             ");
				argList.add(orgId);
			}
			List<String> companyIds = searchDto.getCompanyIds();
			if (CollectionUtils.isNotEmpty(companyIds)) {
				String param = generateInParam(companyIds);
				sql.append(" 	AND (s.orgId in( " + param + "                   )         ");
				argList.addAll(companyIds);
				sql.append(" 	OR s.departmentId   in( " + param + "                   )      )      ");
				argList.addAll(companyIds);
			}
		} else if (TRUE.equals(ckRange)) {
			String orgId = searchDto.getOrgId();
			String departmentId = searchDto.getDepartmentId();
			List<String> orgIds = Lists.newArrayList();
			if (StringUtils.isBlank(orgId)) {
				if (StringUtils.isNotBlank(departmentId)) {
					sql.append(" 	AND ( s.departmentId = ?                             ");
					argList.add(departmentId);
				}
				// 获取部门下的组织
				List<TenantDeptOrgDto> tenantDeptOrgDtoList = cloudDepartmentDao
						.findDeptOrgList(searchDto.getTenantId(), departmentId, null);
				if (CollectionUtils.isNotEmpty(tenantDeptOrgDtoList)) {
					for (TenantDeptOrgDto tdo : tenantDeptOrgDtoList) {
						orgIds.add(tdo.getId());
					}
				}
				if (CollectionUtils.isNotEmpty(orgIds)) {
					String param = generateInParam(orgIds);
					sql.append(" 	OR s.orgId   in( " + param + " )      )     ");
					argList.addAll(orgIds);
				} else {
					sql.append(" ) ");
				}

			} else {
				CloudOrganization cloudOrganization = cloudOrganizationDao.findOne(orgId);
				String departId = cloudOrganization.getDepartmentId();
				String nodeCode = cloudOrganization.getNodeCode();
				String id = cloudOrganization.getId();
				List<TenantDeptOrgDto> tenantDeptOrgDtos = cloudOrganizationDao.findOrganizationChild(departId,
						nodeCode, id, null);
				List<String> orggIds = Lists.newArrayList();
				orggIds.add(id);
				for (TenantDeptOrgDto tentantOrg : tenantDeptOrgDtos) {
					orggIds.add(tentantOrg.getId());
				}
				if (CollectionUtils.isNotEmpty(orggIds)) {
					String param = generateInParam(orggIds);
					sql.append(" AND s.orgId   in( " + param + " )     ");
					argList.addAll(orggIds);
				}
			}
			List<String> companyIds = searchDto.getCompanyIds();
			if (CollectionUtils.isNotEmpty(companyIds)) {
				String param = generateInParam(companyIds);
				sql.append(" 	AND (s.orgId in( " + param + "                   )         ");
				argList.addAll(companyIds);
				sql.append(" 	OR s.departmentId   in( " + param + "                   )      )      ");
				argList.addAll(companyIds);
			}
		}

		String code = searchDto.getCode();
		if (StringUtils.isNotBlank(code)) {
			sql.append(" 	AND s.code LIKE ?                              ");
			argList.add("%" + code + "%");
		}

		String name = searchDto.getName();
		if (StringUtils.isNotBlank(name)) {
			sql.append(" 	AND s.name LIKE ?                           ");
			argList.add("%" + name + "%");
		}

		String socialSecurityNo = searchDto.getSocialSecurityNo();
		if (StringUtils.isNotBlank(socialSecurityNo)) {
			sql.append(" 	AND s.socialSecurityNo LIKE ?                  ");
			argList.add("%" + socialSecurityNo + "%");
		}

		String credentialNum = searchDto.getCredentialNum();
		if (StringUtils.isNotBlank(credentialNum)) {
			sql.append(" 	AND s.credentialNum LIKE ?                     ");
			argList.add("%" + credentialNum + "%");
		}
		// 性别
		String gender = searchDto.getGender();
		if (StringUtils.isNotBlank(gender)) {
			sql.append(" 	AND s.gender =?                     ");
			argList.add(gender);
		}
		// 学历
		String educationId = searchDto.getEducationId();
		if (StringUtils.isNotBlank(educationId)) {
			sql.append(" 	AND s.educationId =?                     ");
			argList.add(educationId);
		}
		// 职务
		String partyPostId = searchDto.getPartyPostId();
		if (StringUtils.isNotBlank(partyPostId)) {
			sql.append(" 	AND s.partyPostId =?                     ");
			argList.add(partyPostId);
		}

		// 年龄段开始
		String ageGroupStart = searchDto.getAgeGroupStart();
		if (StringUtils.isNotBlank(ageGroupStart)) {
			sql.append(
					" 	AND ? <= (year(now())-year(s.birthday)-1) + ( DATE_FORMAT(s.birthday, '%m%d') <= DATE_FORMAT(NOW(), '%m%d') )                     ");
			argList.add(ageGroupStart);
		}

		// 年龄段结束
		String ageGroupEnd = searchDto.getAgeGroupEnd();
		if (StringUtils.isNotBlank(ageGroupEnd)) {
			sql.append(
					" 	AND ? >= (year(now())-year(s.birthday)-1) + ( DATE_FORMAT(s.birthday, '%m%d') <= DATE_FORMAT(NOW(), '%m%d') )                     ");
			argList.add(ageGroupEnd);
		}

		// 工作年限开始
		String workYearLimitStart = searchDto.getWorkYearLimitStart();
		if (StringUtils.isNotBlank(workYearLimitStart)) {
			sql.append(" 	AND ? <=     cast(s.workYearLimit AS UNSIGNED)                 ");
			sql.append(" 	AND s.workYearLimit !='' AND s.workYearLimit IS NOT NULL                           ");
			argList.add(Integer.valueOf(workYearLimitStart));
		}
		// 工作年限结束
		String workYearLimitEnd = searchDto.getWorkYearLimitEnd();
		if (StringUtils.isNotBlank(workYearLimitEnd)) {
			sql.append(" 	AND ? >=   cast(s.workYearLimit  AS UNSIGNED)                    ");
			sql.append(" 	AND s.workYearLimit !='' AND s.workYearLimit IS NOT NULL                               ");
			argList.add(Integer.valueOf(workYearLimitEnd));
		}

		// 职务
		List<String> partyPostIds = searchDto.getPartyPostIds();
		if (CollectionUtils.isNotEmpty(partyPostIds)) {
			String param = generateInParam(partyPostIds);

			sql.append(" 	AND s.partyPostId in (   " + param + "      )                     ");
			argList.addAll(partyPostIds);
		}

		// 是否在职
		String isLeave = searchDto.getIsLeave();
		if (StringUtils.isNotEmpty(isLeave)) {
			sql.append(" 	AND s.isLeave =?                     ");
			argList.add(isLeave);
		}

		// 手机
		String phone = searchDto.getPhone();
		if (StringUtils.isNotEmpty(phone)) {
			sql.append(" 	AND s.phone like ?                     ");
			argList.add("%" + phone + "%");
		}
		if (logger.isDebugEnabled()) {
			logger.debug("getSqlOfPageBySearchDto()," + sql.toString());
		}
		// 过滤ids
		List<String> ids = searchDto.getIds();
		if (CollectionUtils.isNotEmpty(ids)) {
			String param = generateInParam(ids);
			sql.append(" 	AND s.id  in  (                   " + param + " 	)                                 ");

			argList.addAll(ids);
		}
		// 返回结果
		Map<String, Object> map = new HashMap<>();
		map.put("sql", sql);
		map.put("argList", argList);
		return map;
	}

	/**
	 * 生成in查询的参数
	 *
	 * @param list
	 * @return
	 */
	private String generateInParam(List<String> list) {
		String[] fillArray = new String[list.size()];
		Arrays.fill(fillArray, "?");
		return StringUtils.join(fillArray, ",");
	}

	/**
	 * @param staffAli
	 *            staff表关联时取的别名
	 * @param sql
	 * @param sort
	 */
	private void addOrder(String staffAli, StringBuffer sql, Sort sort) {

		if (sort == null) {
			return;
		}

		StringBuffer sb = new StringBuffer("");
		sb.append(" ORDER BY ");
		for (Order order : sort) {

			// 名字按名字首字母排序
			if (order.getProperty().equalsIgnoreCase("name")) {
				sb.append(" " + staffAli + ".nameInitial" + (order.isAscending() ? " ASC " : " DESC ") + ", ");
			} else if (order.getProperty().equalsIgnoreCase("partyPostName")) { // 职位按参数表orderIndex排序
				sb.append(" " + "cps.orderIndex" + (order.isAscending() ? " ASC " : " DESC ") + ", ");
			} else {
				sb.append(
						" " + staffAli + "." + order.getProperty() + (order.isAscending() ? " ASC " : " DESC ") + ", ");
			}
		}

		sql.append(sb.substring(0, sb.lastIndexOf(",") - 1));

		if (logger.isDebugEnabled()) {
			logger.debug("addOrder()," + sql.toString());
		}
	}

	@Override
	public CloudStaff getStaffByCode(String staffCode, String tenantId) {
		StringBuffer sql = new StringBuffer();
		sql.append("  SELECT DISTINCT                                                           ");
		sql.append("  	cs.*                                                                    ");
		sql.append("  FROM                                                                      ");
		sql.append("  	cloud_staff cs                                                          ");
		sql.append("                                                                            ");
		sql.append("  LEFT JOIN cloud_management_tenant cmt ON cmt.id = cs.tenantId             ");
		sql.append("  WHERE                                                                     ");
		sql.append("  	cs.`code` = '" + staffCode + "'                                                          ");
		sql.append("  AND cmt.id = '" + tenantId + "'                                                   ");
		CloudStaff cloudStaff = jdbcTemplate.queryForObject(sql.toString(),
				BeanPropertyRowMapper.newInstance(CloudStaff.class));
		return cloudStaff;
	}

	@Override
	public CloudStaff getStaffByUserId(String userId) {

		StringBuffer sql = new StringBuffer();
		sql.append(" SELECT                                                  ");
		sql.append(" 	cs.*                                                 ");
		sql.append(" FROM                                                    ");
		sql.append(" 	cloud_staff cs                                       ");
		sql.append(" LEFT JOIN cloud_user cu ON cs.id = cu.staffId           ");
		sql.append(" WHERE                                                   ");
		sql.append(" 	cu.id = '" + userId + "'                             ");
		CloudStaff cloudStaff = jdbcTemplate.queryForObject(sql.toString(),
				BeanPropertyRowMapper.newInstance(CloudStaff.class));
		return cloudStaff;
	}

	@Override
	public List<String> getStaffsByDepartmentId(String departmentId) {
		StringBuffer sql = new StringBuffer();
		sql.append("   SELECT      DISTINCT                                               ");
		sql.append("   	cs.id                                                     ");
		sql.append("   FROM                                                       ");
		sql.append("   	cloud_staff cs                                            ");
		sql.append("   WHERE                                                      ");
		sql.append("   	cs.departmentId = '" + departmentId + "'      ");
		sql.append("   	AND ( cs.orgId IS NULL  or cs.orgId ='')      ");
		sql.append("   	AND cs.beenDeleted='0'      ");

		List<String> ids = jdbcTemplate.queryForList(sql.toString(), String.class);
		return ids;
	}

	@Override
	public List<CloudStaffDto> getStaffsByUserIds(List<String> ids) {
		StringBuffer sql = new StringBuffer();
		List<Object> args = Lists.newArrayList();
		sql.append(
				" SELECT cs.*, cu.id as userId FROM cloud_staff cs LEFT JOIN cloud_user cu ON cu.staffId = cs.id WHERE cs.beenDeleted = ? AND cu.beenDeleted = ? ");
		args.add(false);
		args.add(false);
		if (CollectionUtils.isNotEmpty(ids)) {
			String param = generateInParam(ids);
			sql.append(" AND cu.id in( " + param + " )");
			args.addAll(ids);
		}
		List<CloudStaffDto> CloudStaffDtoList = jdbcTemplate.query(sql.toString(), args.toArray(),
				BeanPropertyRowMapper.newInstance(CloudStaffDto.class));
		return CloudStaffDtoList;
	}

	@Override
	public List<String> getStaffsByOrgId(String orgId) {
		StringBuffer sql = new StringBuffer();
		sql.append("   SELECT        DISTINCT                                             ");
		sql.append("   	cs.id                                                     ");
		sql.append("   FROM                                                       ");
		sql.append("   	cloud_staff cs                                            ");
		sql.append("   WHERE                                                      ");
		sql.append("   	cs.orgId = '" + orgId + "'      ");
		sql.append("   	AND cs.beenDeleted='0'      ");
		List<String> ids = jdbcTemplate.queryForList(sql.toString(), String.class);
		return ids;
	}

	@Override
	public List<String> getAllStaffsByDepartmentId(String departmentId) {
		StringBuffer sql = new StringBuffer();
		sql.append("   SELECT         DISTINCT                                            ");
		sql.append("   	cs.id                                                     ");
		sql.append("   FROM                                                       ");
		sql.append("   	cloud_staff cs                                            ");
		sql.append("   WHERE                                                      ");
		sql.append("   	cs.departmentId = '" + departmentId + "'      ");
		sql.append("   	AND cs.beenDeleted='0'      ");
		List<String> ids = jdbcTemplate.queryForList(sql.toString(), String.class);
		return ids;
	}

	@Override
	public List<String> getAllStaffsByOrgNodeCode(String nodeCode) {
		StringBuffer sql = new StringBuffer();
		sql.append("    SELECT         DISTINCT                                                 ");
		sql.append("    	cs.id                                                       ");
		sql.append("    FROM                                                            ");
		sql.append("    	cloud_staff cs                                              ");
		sql.append("    LEFT JOIN cloud_organization co ON co.id = cs.orgId             ");
		sql.append("     where co.nodeCode LIKE '" + nodeCode + "%'                     ");
		sql.append("   	AND cs.beenDeleted='0'      ");
		List<String> ids = jdbcTemplate.queryForList(sql.toString(), String.class);
		return ids;
	}

	@Override
	public List<Map<String, Object>> getStaffListByUserRegisterType(CloudStaffSearchDto searchDto) {

		Map<String, Object> map = this.sqlForGetStaffListByUserRegisterType(searchDto);
		StringBuffer sql = (StringBuffer) map.get("sql");
		List<Object> argList = (List<Object>) map.get("argList");

		if (logger.isDebugEnabled()) {
			logger.debug("getStaffListByUserRegisterType(), sql=" + sql.toString());
			logger.debug("getStaffListByUserRegisterType(), argList=" + argList);
		}

		// 查询
		return jdbcTemplate.queryForList(sql.toString(), argList.toArray());
	}

	private Map<String, Object> sqlForGetStaffListByUserRegisterType(CloudStaffSearchDto searchDto) {

		String containsTenant = searchDto.getContainsTenant();
		List<String> deptIds = searchDto.getDeptIds();
		StringBuffer sql = new StringBuffer();
		List<Object> argList = new ArrayList<>();

		if ("Y".equalsIgnoreCase(searchDto.getRegisterType())) { // 人员已经注册成为了用户
			sql.append(" SELECT s.id, s.name, s.postName, s.phone, s.email      ");
			sql.append(" FROM cloud_staff s, cloud_user u                       ");
			sql.append(" WHERE s.tenantId = ?  									");
			sql.append(" 	AND s.beenDeleted = ?                               ");
			sql.append(" 	AND u.staffId = s.id                                 ");
			sql.append(" 	AND u.beenDeleted = ?                               ");

			argList.add(searchDto.getTenantId());
			argList.add(BakDeleteModel.NO_DELETED);
			argList.add(BakDeleteModel.NO_DELETED);

		} else if ("N".equalsIgnoreCase(searchDto.getRegisterType())) { // 人员没有注册成为用户
			sql.append(" SELECT s.id, s.name, s.postName, s.phone, s.email      ");
			sql.append(" FROM cloud_staff s                                     ");
			sql.append(" WHERE s.tenantId = ?  									");
			sql.append(" 	AND s.beenDeleted = ?                               ");
			sql.append(" 	AND NOT EXISTS (                                      ");
			sql.append(" 		SELECT u.id                                         ");
			sql.append(" 		FROM cloud_user u                                   ");
			sql.append(" 		WHERE u.staffId = s.id                              ");
			sql.append(" 			AND u.beenDeleted = ?                           ");
			sql.append(" 	)                                                     ");

			argList.add(searchDto.getTenantId());
			argList.add(BakDeleteModel.NO_DELETED);
			argList.add(BakDeleteModel.NO_DELETED);

		} else { // 所有人员
			sql.append(" SELECT s.id, s.name, s.postName, s.phone, s.email      ");
			sql.append(" FROM cloud_staff s                                     ");
			sql.append(" WHERE s.tenantId = ?  									");
			sql.append(" 	AND s.beenDeleted = ?                               ");

			argList.add(searchDto.getTenantId());
			argList.add(BakDeleteModel.NO_DELETED);
		}

		if (CollectionUtils.isNotEmpty(deptIds)) {
			String[] array = new String[deptIds.size()];
			sql.append("  AND (                                                                     ");
			sql.append("       s.orgId in (                                                   ");
			Arrays.fill(array, 0, deptIds.size(), " ? ");
			sql.append("                    " + StringUtils.join(array, " , ")
					+ "                                                  ");
			sql.append("                   )                                                              ");

			sql.append("       OR                                                              ");
			sql.append("   s.departmentId in (                                                   ");
			Arrays.fill(array, 0, deptIds.size(), " ? ");
			sql.append("                    " + StringUtils.join(array, " , ")
					+ "                                                  ");
			sql.append("                      )                                                              ");

			sql.append("       )                                                              ");
			argList.addAll(deptIds);
			argList.addAll(deptIds);
		}
		if (StringUtils.isNotBlank(containsTenant) && CloudStaffSearchDto.CONTAINS_TENANT_NO.equals(containsTenant)) {
			sql.append(" 	AND s.orgId is NOT NULL                              ");
			sql.append(" 	AND s.departmentId is NOT NULL                               ");
		}
		sql.append(" 	ORDER BY s.lastChangeTime DESC                             ");
		// 返回结果
		Map<String, Object> map = new HashMap<>();
		map.put("sql", sql);
		map.put("argList", argList);
		return map;
	}

	@Override
	public List<CloudStaff> getStaffIdsByNames(List<String> names, String tenantId) {
		StringBuffer sql = new StringBuffer();
		sql.append("  SELECT                                              ");
		sql.append("  	*                                                 ");
		sql.append("  FROM                                                ");
		sql.append("  	cloud_staff cs                                    ");
		sql.append("  WHERE                                               ");
		sql.append("  cs.tenantId= ?                                       ");
		List<String> argList = Lists.newArrayList();
		argList.add(tenantId);
		if (CollectionUtils.isNotEmpty(names)) {
			sql.append("  AND	cs.`name` IN (              ");
			for (int i = 0; i < names.size(); i++) {

				// 最后一个特殊处理
				if (i == names.size() - 1) {
					sql.append("  	?              ");
				} else {
					sql.append("  	? ,             ");
				}
				argList.add(names.get(i));
			}

			sql.append("  	)                ");
		}
		List<CloudStaff> list = jdbcTemplate.query(sql.toString(), argList.toArray(new String[argList.size()]),
				BeanPropertyRowMapper.newInstance(CloudStaff.class));
		return list;
	}

	@Override
	public List<CloudStaffDto> loadStaffsByFilter(Map<String, Object> paramMap) {
		// 租户id
		String tenantId = (String) paramMap.get("tenantId");
		// 所属公司
		String companyId = (String) paramMap.get("companyId");
		// 职务
		List<String> partyPostIds = (List<String>) paramMap.get("partyPostIds");

		// 是否包含管理员
		Boolean containManager = (Boolean) paramMap.get("containManager");
		List<Object> args = Lists.newArrayList();
		StringBuffer sql = new StringBuffer();
		sql.append("          SELECT                                         ");
		sql.append("          	s.*, u.id userId,                            ");
		sql.append("          	u.userName userName,                         ");
		sql.append("          	u.rongLianAccount,                           ");
		sql.append("          	u.mobilePushMsgId  ,u.imToken ,u.photoId               ");
		sql.append("          FROM                                           ");
		sql.append("          	cloud_staff s                                ");
		sql.append("          LEFT JOIN cloud_user u ON s.id = u.staffId     ");
		sql.append("          WHERE                                          ");
		sql.append("          	s.tenantId = ?                              ");
		sql.append("          AND s.beenDeleted = ?                          ");
		args.add(tenantId);
		args.add(BakDeleteModel.NO_DELETED);

		if (StringUtils.isNotBlank(companyId)) {
			sql.append("          AND (                                          ");
			sql.append("          	s.orgId = ?                                 ");
			sql.append("          	OR s.departmentId = ?                       ");
			sql.append("          )                                              ");

			args.add(companyId);
			args.add(companyId);
		}

		// 不包含管理员(部门id或者机构id不为空)
		if (containManager != null && !containManager) {
			sql.append("          AND (                                          ");
			sql.append("          	s.orgId IS NOT NULL                              ");
			sql.append("          	OR s.departmentId IS NOT NULL                   ");
			sql.append("          )                                              ");
		}

		// 传递的职务列表不为空
		if (CollectionUtils.isNotEmpty(partyPostIds)) {
			String param = generateInParam(partyPostIds);
			sql.append(" 	AND  s.partyPostId in ( " + param + "                   )         ");
			args.addAll(partyPostIds);

		}
		return jdbcTemplate.query(sql.toString(), args.toArray(),
				BeanPropertyRowMapper.newInstance(CloudStaffDto.class));
	}

	@Override
	public CloudStaffDto getById(String id) {
		List<Object> args = Lists.newArrayList();
		StringBuffer sql = new StringBuffer();

		sql.append("             SELECT                                                           ");

		sql.append("  		cs.id,                              ");
		sql.append("  	cs.createTime,                          ");
		sql.append("  	cs.lastChangeTime,                      ");
		sql.append("  	cs. STATUS,                             ");
		sql.append("  	cs.beenDeleted,                         ");
		sql.append("  	cs.deletedTime,                         ");
		sql.append("  	cs.authorizeId,                         ");
		sql.append("  	cps10.parmName authorizeName,                       ");
		sql.append("  	cs.birthPlace,                          ");
		sql.append("  	cs.birthday,                            ");
		sql.append("  	cs. CODE,                               ");
		sql.append("  	cs.credentialNum,                       ");
		sql.append("  	cs.credentialType,                      ");
		sql.append("  	cs.departmentId,                        ");
		sql.append("  	cs.description,                         ");
		sql.append("  	cs.educationId,                         ");
		sql.append("  	cps9.parmName educationName,                       ");
		sql.append("  	cs.email,                               ");
		sql.append("  	cs.entryHereTime,                       ");
		sql.append("  	cs.gender,                              ");
		sql.append("  	cs.graduate,                            ");
		sql.append("  	cs.healthId,                            ");
		sql.append("  	cps6.parmName healthName,                          ");
		sql.append("  	cs.idCard,                              ");
		sql.append("  	cs.innerEmail,                          ");
		sql.append("  	cs.isLeave,                             ");
		sql.append("  	cs.joinWorkTime,                        ");
		sql.append("  	cs.leaveTime,                           ");
		sql.append("  	cs.livePlace,                           ");
		sql.append("  	cs.maritalStatusId,                     ");
		sql.append("  	cps7.parmName maritalStatusName,                   ");
		sql.append("  	cs. NAME,                               ");
		sql.append("  	cs.nationId,                            ");
		sql.append("  	cps5.parmName nationName,                          ");
		sql.append("  	cs.officeTel,                           ");
		sql.append("  	cs.orgId,                               ");
		sql.append("  	cs.partyPostId,                         ");
		sql.append("  	cps1.parmName partyPostName,            ");
		sql.append("  	cs.phone,                               ");
		sql.append("  	cs.politicalStatusId,                   ");
		sql.append("  	cps8.parmName politicalStatusName,                 ");
		sql.append("  	cs.postId,                              ");
		sql.append("  	cps2.parmName postName,                            ");
		sql.append("  	cs.presentPlace,                        ");
		sql.append("  	cs.socialSecurityNo,                    ");
		sql.append("  	cs.socialSecuritycase,                  ");
		sql.append("  	cs.tenantId,                            ");
		sql.append("  	cs.workYearLimit,                       ");
		sql.append("  	co.orgName orgName,                             ");
		sql.append("  	cs.orderIndex,                          ");
		sql.append("  	cs.workTypeCode,                        ");
		sql.append("  	cps3.parmName       workTypeName  ,                        ");
		sql.append("  	cs.outSourcing,                         ");
		sql.append("  	cs.outSourcingComp,                     ");
		sql.append("             	 cu.id userId,                                           ");
		sql.append("             	cu.mobilePushMsgId mobilePushMsgId,                           ");
		sql.append("             	cu.userName userName,                                         ");
		sql.append("             	cu.photoId,cd.depName companyName,                             ");
		sql.append(
				"  	cs.isWillMan,cs.willCheckDivisionIds,cs.willWorkUnit,cs.address                               ");
		sql.append("             FROM                                                             ");
		sql.append("             	cloud_staff cs                                                ");

		sql.append("              LEFT JOIN cloud_parameter_type cpt1 ON cpt1.typeCode = ?       ");
		sql.append("              AND cpt1.beenDeleted = ?                                                        ");
		sql.append("              LEFT JOIN cloud_tenantparameter_setting cps1 ON cps1.typeId = cpt1.id           ");
		sql.append("              AND cps1.parmCode = cs.partyPostId                                              ");
		sql.append("              AND cps1.tenantId = cs.tenantId                                                 ");
		sql.append("              AND cps1.beenDeleted = ?                                                        ");

		sql.append("              LEFT JOIN cloud_parameter_type cpt2 ON cpt2.typeCode = ?       ");
		sql.append("              AND cpt2.beenDeleted = ?                                                        ");
		sql.append("              LEFT JOIN cloud_tenantparameter_setting cps2 ON cps2.typeId = cpt2.id           ");
		sql.append("              AND cps2.parmCode = cs.postId                                              ");
		sql.append("              AND cps2.tenantId = cs.tenantId                                                 ");
		sql.append("              AND cps2.beenDeleted = ?                                                        ");

		sql.append("              LEFT JOIN cloud_parameter_type cpt3 ON cpt3.typeCode = ?       ");
		sql.append("              AND cpt3.beenDeleted = ?                                                        ");
		sql.append("              LEFT JOIN cloud_tenantparameter_setting cps3 ON cps3.typeId = cpt3.id           ");
		sql.append("              AND cps3.parmCode = cs.workTypeCode                                              ");
		sql.append("              AND cps3.tenantId = cs.tenantId                                                 ");
		sql.append("              AND cps3.beenDeleted = ?                                                        ");

		sql.append("              LEFT JOIN cloud_parameter_type cpt4 ON cpt4.typeCode = ?       ");
		sql.append("              AND cpt4.beenDeleted = ?                                                        ");
		sql.append("              LEFT JOIN cloud_tenantparameter_setting cps4 ON cps4.typeId = cpt4.id           ");
		sql.append("              AND cps4.parmCode = cs.credentialType                                              ");
		sql.append("              AND cps4.tenantId = cs.tenantId                                                 ");
		sql.append("              AND cps4.beenDeleted = ?                                                        ");

		sql.append("              LEFT JOIN cloud_parameter_type cpt5 ON cpt5.typeCode = ?       ");
		sql.append("              AND cpt5.beenDeleted = ?                                                        ");
		sql.append("              LEFT JOIN cloud_tenantparameter_setting cps5 ON cps5.typeId = cpt5.id           ");
		sql.append("              AND cps5.parmCode = cs.nationId                                              ");
		sql.append("              AND cps5.tenantId = cs.tenantId                                                 ");
		sql.append("              AND cps5.beenDeleted = ?                                                        ");

		sql.append("              LEFT JOIN cloud_parameter_type cpt6 ON cpt6.typeCode = ?       ");
		sql.append("              AND cpt6.beenDeleted = ?                                                        ");
		sql.append("              LEFT JOIN cloud_tenantparameter_setting cps6 ON cps6.typeId = cpt6.id           ");
		sql.append("              AND cps6.parmCode = cs.healthId                                              ");
		sql.append("              AND cps6.tenantId = cs.tenantId                                                 ");
		sql.append("              AND cps6.beenDeleted = ?                                                        ");

		sql.append("              LEFT JOIN cloud_parameter_type cpt7 ON cpt7.typeCode = ?       ");
		sql.append("              AND cpt7.beenDeleted = ?                                                        ");
		sql.append("              LEFT JOIN cloud_tenantparameter_setting cps7 ON cps7.typeId = cpt7.id           ");
		sql.append(
				"              AND cps7.parmCode = cs.maritalStatusId                                              ");
		sql.append("              AND cps7.tenantId = cs.tenantId                                                 ");
		sql.append("              AND cps7.beenDeleted = ?                                                        ");

		sql.append("              LEFT JOIN cloud_parameter_type cpt8 ON cpt8.typeCode = ?       ");
		sql.append("              AND cpt8.beenDeleted = ?                                                        ");
		sql.append("              LEFT JOIN cloud_tenantparameter_setting cps8 ON cps8.typeId = cpt8.id           ");
		sql.append(
				"              AND cps8.parmCode = cs.politicalStatusId                                              ");
		sql.append("              AND cps8.tenantId = cs.tenantId                                                 ");
		sql.append("              AND cps8.beenDeleted = ?                                                        ");

		sql.append("              LEFT JOIN cloud_parameter_type cpt9 ON cpt9.typeCode = ?       ");
		sql.append("              AND cpt9.beenDeleted = ?                                                        ");
		sql.append("              LEFT JOIN cloud_tenantparameter_setting cps9 ON cps9.typeId = cpt9.id           ");
		sql.append("              AND cps9.parmCode = cs.educationId                                              ");
		sql.append("              AND cps9.tenantId = cs.tenantId                                                 ");
		sql.append("              AND cps9.beenDeleted = ?                                                        ");

		sql.append("              LEFT JOIN cloud_parameter_type cpt10 ON cpt10.typeCode = ?       ");
		sql.append("              AND cpt10.beenDeleted = ?                                                        ");
		sql.append("              LEFT JOIN cloud_tenantparameter_setting cps10 ON cps10.typeId = cpt10.id           ");
		sql.append("              AND cps10.parmCode = cs.authorizeId                                              ");
		sql.append("              AND cps10.tenantId = cs.tenantId                                                 ");
		sql.append("              AND cps10.beenDeleted = ?                                                        ");

		sql.append("              LEFT JOIN cloud_parameter_type cpt11 ON cpt11.typeCode = ?       ");
		sql.append("              AND cpt11.beenDeleted = ?                                                        ");
		sql.append("              LEFT JOIN cloud_tenantparameter_setting cps11 ON cps11.typeId = cpt11.id           ");
		sql.append(
				"              AND cps11.parmCode = cs.socialSecuritycase                                              ");
		sql.append("              AND cps11.tenantId = cs.tenantId                                                 ");
		sql.append("              AND cps11.beenDeleted = ?                                                        ");

		sql.append("             LEFT JOIN cloud_user cu ON cu.staffId = cs.id                    ");
		sql.append("             AND cu.beenDeleted = ?                                           ");

		sql.append("             LEFT JOIN cloud_department cd ON cd.id = cs.departmentId         ");
		sql.append("             AND cd.beenDeleted = ?                                           ");

		sql.append("             LEFT JOIN cloud_organization co ON co.id = cs.orgId         ");
		sql.append("             AND co.beenDeleted = ?                                           ");
		sql.append("             WHERE                                                            ");
		sql.append("             	cs.id = ?                    ");
		sql.append("             AND cs.beenDeleted = ?                                           ");
		// 1
		args.add(PropertyUtils.getPropertyValue("STAFF_POST"));
		args.add(BakDeleteModel.NO_DELETED);
		args.add(BakDeleteModel.NO_DELETED);
		// 2
		args.add(PropertyUtils.getPropertyValue("STAFF_POSITION"));
		args.add(BakDeleteModel.NO_DELETED);
		args.add(BakDeleteModel.NO_DELETED);
		// 3
		args.add(PropertyUtils.getPropertyValue("STAFF_WORK_TYPE"));
		args.add(BakDeleteModel.NO_DELETED);
		args.add(BakDeleteModel.NO_DELETED);

		// 4
		args.add(PropertyUtils.getPropertyValue("STAFF_CREDENTIAL_TYPE"));
		args.add(BakDeleteModel.NO_DELETED);
		args.add(BakDeleteModel.NO_DELETED);

		// 5
		args.add(PropertyUtils.getPropertyValue("STAFF_NATION"));
		args.add(BakDeleteModel.NO_DELETED);
		args.add(BakDeleteModel.NO_DELETED);

		// 6
		args.add(PropertyUtils.getPropertyValue("STAFF_HEALTH"));
		args.add(BakDeleteModel.NO_DELETED);
		args.add(BakDeleteModel.NO_DELETED);

		// 7
		args.add(PropertyUtils.getPropertyValue("STAFF_MARITAL_STATUS"));
		args.add(BakDeleteModel.NO_DELETED);
		args.add(BakDeleteModel.NO_DELETED);
		// 8
		args.add(PropertyUtils.getPropertyValue("STAFF_POLITICAL_STATUS"));
		args.add(BakDeleteModel.NO_DELETED);
		args.add(BakDeleteModel.NO_DELETED);

		// 9
		args.add(PropertyUtils.getPropertyValue("STAFF_EDUCATION"));
		args.add(BakDeleteModel.NO_DELETED);
		args.add(BakDeleteModel.NO_DELETED);

		// 10
		args.add(PropertyUtils.getPropertyValue("STAFF_AUTHORIZE"));
		args.add(BakDeleteModel.NO_DELETED);
		args.add(BakDeleteModel.NO_DELETED);

		// 11
		args.add(PropertyUtils.getPropertyValue("STAFF_SOCIAL_SECURITY_CASE"));
		args.add(BakDeleteModel.NO_DELETED);
		args.add(BakDeleteModel.NO_DELETED);

		args.add(BakDeleteModel.NO_DELETED);
		args.add(BakDeleteModel.NO_DELETED);
		args.add(BakDeleteModel.NO_DELETED);

		args.add(id);
		args.add(BakDeleteModel.NO_DELETED);
		List<CloudStaffDto> cloudStaffDtos = jdbcTemplate.query(sql.toString(), args.toArray(),
				BeanPropertyRowMapper.newInstance(CloudStaffDto.class));
		if (CollectionUtils.isNotEmpty(cloudStaffDtos)) {
			return cloudStaffDtos.get(0);
		}
		return null;
	}

	@Override
	public List<CloudStaffPageDto> syncStaffByPage(String tenantId, long syncTime, Integer pageSize,
			Integer pageNumber) {
		List<Object> args = Lists.newArrayList();
		StringBuffer sql = new StringBuffer();
		sql.append(
				" SELECT s.id ,u.id as userId, s.name, s.orgId as orgId, s.departmentId as departmentId,  u.imToken, u.photoId, s.beenDeleted, s.orderIndex, s.phone, s.nameInitial, ");
		sql.append(
				" cps.id as cpId, cps.parmCode, cps.parmName, cpsPost.id AS postionId, cpsPost.parmCode AS postionCode, cpsPost.parmName AS postionName ");
		sql.append(" FROM cloud_staff s LEFT JOIN cloud_user u ON s.id = u.staffId ");

		sql.append(" LEFT JOIN (select cps.* from cloud_parameter_type cpt,cloud_tenantparameter_setting cps ");
		sql.append(" where cpt.beenDeleted= ? and cps.beenDeleted= ? and cpt.id =cps.typeId and cpt.typeCode = ? ");
		sql.append(" ) cps on cps.parmCode = s.partyPostId  AND cps.tenantId = s.tenantId ");

		sql.append(" LEFT JOIN ( select cps.* from cloud_parameter_type cpt,cloud_tenantparameter_setting cps  ");
		sql.append("  where cpt.beenDeleted= ? and cps.beenDeleted= ? and cpt.id =cps.typeId and cpt.typeCode = ? ");
		sql.append(" ) cpsPost on cpsPost.parmCode = s.PostId  AND cpsPost.tenantId = s.tenantId  ");

		sql.append(" WHERE s.tenantId = ? ");

		args.add(BakDeleteModel.NO_DELETED);
		args.add(BakDeleteModel.NO_DELETED);
		args.add(PropertyUtils.getPropertyValue("STAFF_POST"));

		args.add(BakDeleteModel.NO_DELETED);
		args.add(BakDeleteModel.NO_DELETED);
		args.add(PropertyUtils.getPropertyValue("STAFF_POSITION"));

		args.add(tenantId);

		if (syncTime == ZERO_LONG) {
			sql.append(" AND s.beenDeleted = ? ");
			args.add(BakDeleteModel.NO_DELETED);
		}
		if (syncTime != ZERO_LONG) {

			sql.append(
					" AND CASE  1=1 WHEN ((u.id is null) or (s.lastChangeTime >= u.lastChangeTime ) ) THEN  ((UNIX_TIMESTAMP(s.createTime)*1000> ? and s.beenDeleted = ? ) ");
			sql.append(
					" or (UNIX_TIMESTAMP(s.lastChangeTime)*1000> ?  and s.beenDeleted = ? and UNIX_TIMESTAMP(s.createTime) *1000  <= ?) ");
			sql.append(
					" or (UNIX_TIMESTAMP(s.deletedTime)*1000> ? and s.beenDeleted = ? and UNIX_TIMESTAMP(s.createTime) *1000 <= ?)) ");

			sql.append(" ELSE   ");
			sql.append("  ((UNIX_TIMESTAMP(u.createTime)*1000> ? and u.beenDeleted = ? ) ");
			sql.append(
					" or (UNIX_TIMESTAMP(u.lastChangeTime)*1000> ?  and u.beenDeleted = ? and UNIX_TIMESTAMP(u.createTime) *1000  <= ?) ");
			sql.append(
					" or (UNIX_TIMESTAMP(u.deletedTime)*1000> ? and u.beenDeleted = ? and UNIX_TIMESTAMP(u.createTime) *1000 <= ?)) END ");

			args.add(syncTime);
			args.add(BakDeleteModel.NO_DELETED);
			args.add(syncTime);
			args.add(BakDeleteModel.NO_DELETED);
			args.add(syncTime);
			args.add(syncTime);
			args.add(BakDeleteModel.DELETED);
			args.add(syncTime);

			args.add(syncTime);
			args.add(BakDeleteModel.NO_DELETED);
			args.add(syncTime);
			args.add(BakDeleteModel.NO_DELETED);
			args.add(syncTime);
			args.add(syncTime);
			args.add(BakDeleteModel.DELETED);
			args.add(syncTime);
		}
		sql.append(" ORDER BY s.createTime desc  limit " + pageNumber * pageSize + "," + pageSize + "");

		return jdbcTemplate.query(sql.toString(), args.toArray(),
				BeanPropertyRowMapper.newInstance(CloudStaffPageDto.class));
	}

	@Override
	public List<CloudStaffPageDto> findAllStaffByPage(String tenantId, Integer isDeleted) {
		List<Object> args = Lists.newArrayList();
		StringBuffer sql = new StringBuffer();
		sql.append(
				" SELECT s.id , s.name, s.orgId as orgId, s.departmentId as departmentId,  u.imToken, u.photoId, s.beenDeleted, s.orderIndex, s.nameInitial, ");
		sql.append(" cps.id as cpId, cps.parmCode, cps.parmName ");
		sql.append(" FROM cloud_staff s LEFT JOIN cloud_user u ON s.id = u.staffId ");
		sql.append(" LEFT JOIN cloud_parameter_type cpt ON cpt.typeCode = ? ");
		sql.append(" AND cpt.beenDeleted = ?  ");
		sql.append(" LEFT JOIN cloud_tenantparameter_setting cps ON cps.typeId = cpt.id ");
		sql.append(" AND cps.parmCode = s.partyPostId  AND cps.tenantId = s.tenantId  AND cps.beenDeleted = ?   ");
		sql.append(" WHERE s.tenantId = ?  ");

		args.add(PropertyUtils.getPropertyValue("STAFF_POST"));
		args.add(BakDeleteModel.NO_DELETED);
		args.add(BakDeleteModel.NO_DELETED);
		args.add(tenantId);

		if (isDeleted != null) {
			sql.append(" AND s.beenDeleted = ? ");
			args.add(isDeleted);
		}

		return jdbcTemplate.query(sql.toString(), args.toArray(),
				BeanPropertyRowMapper.newInstance(CloudStaffPageDto.class));
	}

	@Override
	public List<CloudStaffDto> findListBySearchDto(Sort defSort, CloudStaffSearchDto searchDto) {
		Map<String, Object> map = this.getSqlOfPageBySearchDto(searchDto);
		StringBuffer sql = (StringBuffer) map.get("sql");
		List<Object> argList = (List<Object>) map.get("argList");

		// 加入排序
		this.addOrder("s", sql, defSort);

		List<CloudStaffDto> pageList = jdbcTemplate.query(sql.toString(), argList.toArray(),
				BeanPropertyRowMapper.newInstance(CloudStaffDto.class));

		return pageList;
	}

	@Override
	public Page<CloudStaffDto> syncStaffsByPage(Pageable pageable, Map<String, Object> paramMap) {
		// 租户id
		String tenantId = (String) paramMap.get("tenantId");
		Long syncTime = null;
		if (paramMap.containsKey("lastSyncTime")) {
			if (!StringUtil.isNullOrEmpty(String.valueOf(paramMap.get("lastSyncTime")))) {
				syncTime = Long.valueOf(String.valueOf(paramMap.get("lastSyncTime")));
			}
		}
		List<Object> args = Lists.newArrayList();
		StringBuffer sql = new StringBuffer();
		sql.append("          SELECT                                         ");
		sql.append("          	s.*, u.id userId,                            ");
		sql.append("          	u.userName userName,                         ");
		sql.append("          	u.rongLianAccount,                           ");
		sql.append("          	u.mobilePushMsgId  ,u.imToken ,u.photoId               ");
		sql.append("          FROM                                           ");
		sql.append("          	cloud_staff s                                ");
		sql.append("          LEFT JOIN cloud_user u ON s.id = u.staffId     ");
		sql.append("          WHERE                                          ");
		sql.append("          	s.tenantId = ?                              ");
		args.add(tenantId);
		if (null != syncTime && ZERO_LONG != syncTime) {

			sql.append(
					" AND CASE  1=1 WHEN ((u.id is null) or (s.lastChangeTime >= u.lastChangeTime ) ) THEN  ((UNIX_TIMESTAMP(s.createTime)*1000> ? and s.beenDeleted = ? ) ");
			sql.append(
					" or (UNIX_TIMESTAMP(s.lastChangeTime)*1000> ?  and s.beenDeleted = ? and UNIX_TIMESTAMP(s.createTime) *1000  <= ?) ");
			sql.append(
					" or (UNIX_TIMESTAMP(s.deletedTime)*1000> ? and s.beenDeleted = ? and UNIX_TIMESTAMP(s.createTime) *1000 <= ?)) ");

			sql.append(" ELSE   ");
			sql.append("  ((UNIX_TIMESTAMP(u.createTime)*1000> ? and u.beenDeleted = ? ) ");
			sql.append(
					" or (UNIX_TIMESTAMP(u.lastChangeTime)*1000> ?  and u.beenDeleted = ? and UNIX_TIMESTAMP(u.createTime) *1000  <= ?) ");
			sql.append(
					" or (UNIX_TIMESTAMP(u.deletedTime)*1000> ? and u.beenDeleted = ? and UNIX_TIMESTAMP(u.createTime) *1000 <= ?)) END ");

			args.add(syncTime);
			args.add(BakDeleteModel.NO_DELETED);
			args.add(syncTime);
			args.add(BakDeleteModel.NO_DELETED);
			args.add(syncTime);
			args.add(syncTime);
			args.add(BakDeleteModel.DELETED);
			args.add(syncTime);

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
		sql.append("order by s.createTime desc");

		// 组合分页条件
		Integer startRow = pageable.getPageNumber() * pageable.getPageSize();
		Integer endRow = (pageable.getPageNumber() + 1) * pageable.getPageSize();
		String sqlString = QueryUtil.getPagingSql(sql.toString(), startRow, endRow, StaticDBType.getDbType());

		List<CloudStaffDto> pageList = jdbcTemplate.query(sqlString, args.toArray(),
				BeanPropertyRowMapper.newInstance(CloudStaffDto.class));
		return new PageImpl<>(pageList, pageable, totalCnt);
	}

	@Override
	public List<CloudStaffDto> getStaffInfoByUserIds(List<String> ids) {
		if (CollectionUtils.isEmpty(ids)) {
			return Lists.newArrayList();
		}
		List<Object> args = Lists.newArrayList();
		StringBuffer sql = new StringBuffer();
		sql.append("          SELECT                                         ");
		sql.append("          	s.*, u.id userId,                            ");
		sql.append("          	u.userName userName,                         ");
		sql.append("          	u.rongLianAccount,                           ");
		sql.append("          	u.mobilePushMsgId  ,u.imToken ,u.photoId               ");
		sql.append("          FROM                                           ");
		sql.append("          	cloud_staff s                                ");
		sql.append("          LEFT JOIN cloud_user u ON s.id = u.staffId     ");
		sql.append("          WHERE    s.beenDeleted = ?                          ");
		sql.append("    AND      s.id   in ('" + StringUtils.join(ids, "','") + "') ");
		args.add(BakDeleteModel.NO_DELETED);
		return jdbcTemplate.query(sql.toString(), args.toArray(),
				BeanPropertyRowMapper.newInstance(CloudStaffDto.class));
	}

	@Override
	public List<CloudStaffDto> getWillManStaffUser(String tenantId, String name, String willCheckDivisionId) {
		if (StringUtil.isNullOrEmpty(tenantId)) {
			return Lists.newArrayList();
		}
		List<Object> args = Lists.newArrayList();
		StringBuffer sql = new StringBuffer();
		sql.append("          SELECT                                         ");
		sql.append("          	s.*, u.id userId,                            ");
		sql.append("          	u.userName userName                         ");
		sql.append("          FROM                                           ");
		sql.append("          	cloud_staff s                                ");
		sql.append("          LEFT JOIN cloud_user u ON s.id = u.staffId     ");
		sql.append("          WHERE    s.beenDeleted = ?                          ");
		sql.append("    AND      u.beenDeleted = ? ");
		sql.append("    AND      s.tenantId = ? ");
		sql.append("    AND      s.isWillMan = ? ");

		args.add(BakDeleteModel.NO_DELETED);
		args.add(BakDeleteModel.NO_DELETED);
		args.add(tenantId);
		args.add(true);
		if (!StringUtil.isNullOrEmpty(name)) {
			sql.append("    AND      s.name LIKE ? ");
			args.add("%" + name + "%");
		}
		if (!StringUtil.isNullOrEmpty(willCheckDivisionId)) {
			sql.append("    AND      s.willCheckDivisionIds LIKE ? ");
			args.add("%" + willCheckDivisionId + "%");
		}
		return jdbcTemplate.query(sql.toString(), args.toArray(),
				BeanPropertyRowMapper.newInstance(CloudStaffDto.class));
	}

	@Override
	public CloudStaffRestDto getStaffByCodeAndTenantCode(String code, String tenantCode) {
		if (StringUtils.isEmpty(code) || StringUtils.isEmpty(tenantCode)) {
			return null;
		}

		StringBuffer sql = new StringBuffer();
		sql.append("       SELECT                                                      ");
		sql.append("       	cs.name, cs.orgId, cs.orgName, cs.phone, cu.photoId                ");
		sql.append("       FROM                                                                 ");
		sql.append("       	cloud_user cu                                                       ");
		sql.append("       LEFT JOIN cloud_staff cs ON cs.id = cu.staffId                       ");
		sql.append("       LEFT JOIN cloud_management_tenant cmt ON cmt.id = cs.tenantId        ");
		sql.append("       WHERE                                                                ");
		sql.append("       	cs.code = '" + code + "' 									");
		sql.append("       AND cmt.tenantCode = '" + tenantCode + "'  							");

		List<CloudStaffRestDto> cloudStaffList = jdbcTemplate.query(sql.toString(), new Object[0],
				BeanPropertyRowMapper.newInstance(CloudStaffRestDto.class));
		if (CollectionUtils.isEmpty(cloudStaffList)) {
			return null;
		} else {
			return cloudStaffList.get(0);
		}
	}

	@Override
	public List<StaffDto> listStaff(String name, String phone, List<String> orgIds, String tenantId,
			String containManager) throws Exception {
		if (CollectionUtils.isEmpty(orgIds) && StringUtils.isEmpty(tenantId)) {
			return null;
		}

		StringBuffer sql = new StringBuffer();
		List<Object> args = Lists.newArrayList();

		/**
		 * 如果单位列表不为空，则查询列表下的人;如果单位列表为空，则查询租户下所有人员
		 */
		if (CollectionUtils.isNotEmpty(orgIds)) {
			String ids = "";
			for (int i = 0; i < orgIds.size(); i++) {
				if (i == 0) {
					ids += "?";
				} else {
					ids += ",?";
				}
			}

			sql.append(
					" SELECT t.id staffId,t.`name`,t.`code`,t.phone,case when t.orgId is not null and t.orgId<>'' then t.orgId else t.departmentId end orgId,orgName ");
			sql.append(" from cloud_staff t ");
			sql.append(" where (t.orgId is null and t.departmentId in (" + ids
					+ ")) or (t.orgid is not null and t.orgId in (" + ids + ")) ");

			args.addAll(orgIds);
			args.addAll(orgIds);
		} else {
			sql.append(
					" SELECT t.id staffId,t.`name`,t.`code`,t.phone,case when t.orgId is not null and t.orgId<>'' then t.orgId else t.departmentId end orgId,orgName ");
			sql.append(" from cloud_staff t ");
			sql.append(" where t.tenantId=? ");

			args.add(tenantId);
		}

		sql.append(" and t.beenDeleted=? ");
		args.add(BakDeleteModel.NO_DELETED);

		if (StringUtils.isNotEmpty(name)) {
			sql.append(" and t.`name` like ? ");
			args.add("%" + name + "%");
		}
		if (StringUtils.isNotEmpty(phone)) {
			sql.append(" and t.phone like ? ");
			args.add("%" + phone + "%");
		}
		if (StringUtils.isEmpty(containManager) || ManagementConstant.NO.equals(containManager)) { // 默认不包含管理员(orgId和departmentId都为空的人)
			sql.append(" and not ((t.orgId is null or t.orgId='') and (t.departmentId is null or t.departmentId='')) ");
		}
		sql.append(" ORDER BY t.orderIndex ");

		return jdbcTemplate.query(sql.toString(), args.toArray(), BeanPropertyRowMapper.newInstance(StaffDto.class));
	}
}
