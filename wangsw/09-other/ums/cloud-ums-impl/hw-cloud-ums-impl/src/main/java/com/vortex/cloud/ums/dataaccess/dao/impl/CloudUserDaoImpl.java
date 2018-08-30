package com.vortex.cloud.ums.dataaccess.dao.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.vortex.cloud.ums.dataaccess.dao.ICloudUserDao;
import com.vortex.cloud.ums.dto.CloudStaffSearchDto;
import com.vortex.cloud.ums.dto.CloudUserDto;
import com.vortex.cloud.ums.dto.IdNameDto;
import com.vortex.cloud.ums.dto.rest.CloudUserRestDto;
import com.vortex.cloud.ums.dto.rest.UserFunctionDto;
import com.vortex.cloud.ums.model.CloudUser;
import com.vortex.cloud.ums.util.utils.QueryUtil;
import com.vortex.cloud.vfs.data.dto.LoginReturnInfoDto;
import com.vortex.cloud.vfs.data.hibernate.repository.SimpleHibernateRepository;
import com.vortex.cloud.vfs.data.model.BakDeleteModel;
import com.vortex.cloud.vfs.data.support.SearchFilter;
import com.vortex.cloud.vfs.data.support.SearchFilter.Operator;
import com.vortex.cloud.vfs.data.util.StaticDBType;

/**
 * 用户dao
 * 
 * @author lsm
 * @date 2016年4月1日
 */
@Repository("cloudUserDao")
public class CloudUserDaoImpl extends SimpleHibernateRepository<CloudUser, String> implements ICloudUserDao {

	private static final Object USER_NAME = "userName";
	private static final Object TENANT_ID = "tenantId";
	@Resource
	private JdbcTemplate jdbcTemplate;

	@Override
	public DetachedCriteria getDetachedCriteria() {
		return defaultCriteria();
	}

	private DetachedCriteria defaultCriteria() {
		DetachedCriteria criteria = DetachedCriteria.forClass(getPersistentClass(), "cloudUser");
		criteria.add(Restrictions.eq("beenDeleted", BakDeleteModel.NO_DELETED));
		return criteria;
	}

	@Override
	public CloudUser getUserByUserName(String tenantId, String userName) {
		StringBuffer sql = new StringBuffer();
		sql.append("       SELECT DISTINCT                                                      ");
		sql.append("       	*                                                                   ");
		sql.append("       FROM                                                                 ");
		sql.append("       	cloud_user cu                                                       ");
		sql.append("       LEFT JOIN cloud_staff cs ON cs.id = cu.staffId                       ");
		sql.append("       LEFT JOIN cloud_management_tenant cmt ON cmt.id = cs.tenantId        ");
		sql.append("       WHERE                                                                ");
		sql.append("       	cu.userName = '" + userName + "'                                                    ");
		sql.append("       AND cmt.id = '" + tenantId + "'                                                      ");
		CloudUser cloudUser = null;
		List<CloudUser> cloudUsers = jdbcTemplate.query(sql.toString(),
				BeanPropertyRowMapper.newInstance(CloudUser.class));
		if (CollectionUtils.isNotEmpty(cloudUsers)) {
			cloudUser = cloudUsers.get(0);
		}
		return cloudUser;
	}

	@Override
	public CloudUser getUserByIdAndName(String userId, String userName) {
		CloudUser user = null;
		if (StringUtils.isNotBlank(userId)) {
			user = this.findOne(userId);
		}

		if (user != null) {
			return user;
		}
		List<Object> argList = new ArrayList<Object>();
		List<CloudUser> resultList = Lists.newArrayList();
		StringBuffer sql = new StringBuffer();
		sql.append("   SELECT                                 ");
		sql.append("   	*                                     ");
		sql.append("   FROM                                   ");
		sql.append("   	cloud_user cu                         ");
		sql.append("   WHERE cu.userName = ?         ");
		argList.add(userName);
		resultList = jdbcTemplate.query(sql.toString(), argList.toArray(),
				BeanPropertyRowMapper.newInstance(CloudUser.class));
		if (resultList.size() > 0) {
			return resultList.get(0);
		} else {
			return null;
		}
	}

	@Override
	public CloudUser getUserByStaffId(String staffId) {
		List<Object> argList = new ArrayList<Object>();
		List<CloudUser> resultList = Lists.newArrayList();
		StringBuffer sql = new StringBuffer();
		sql.append("   SELECT                                 ");
		sql.append("   	*                                     ");
		sql.append("   FROM                                   ");
		sql.append("   	cloud_user cu                         ");
		sql.append("   WHERE                                  ");
		sql.append("   	cu.staffId = ?         ");
		argList.add(staffId);

		// CloudUser cloudUser = jdbcTemplate.queryForObject(sql.toString(),
		// BeanPropertyRowMapper.newInstance(CloudUser.class));
		resultList = jdbcTemplate.query(sql.toString(), argList.toArray(),
				BeanPropertyRowMapper.newInstance(CloudUser.class));
		if (resultList.size() > 0) {
			return resultList.get(0);
		} else {
			return null;
		}
	}

	/**
	 * 此方法应该允许以下用户登录： 超级管理员 租户管理员 系统管理员 普通用户
	 */
	@Override
	public List<LoginReturnInfoDto> getLoginInfo(String tenantCode, String systemCode, String userName) {
		if (StringUtils.isEmpty(userName)) {
			return null;
		}

		List<LoginReturnInfoDto> rst = null;
		rst = this.getLoginInfo(tenantCode, userName);

		return rst;

	}

	/**
	 * 系统code不做校验,校验userName，他可以为username，也可以是phone（但是此时用户一定要存在）
	 * 
	 * @param tenantCode
	 * @param userName
	 * @return
	 */
	private List<LoginReturnInfoDto> getLoginInfo(String tenantCode, String userName) {
		StringBuffer sql = new StringBuffer();
		List<Object> args = new ArrayList<Object>();

		sql.append(
				"       SELECT                                                                                        ");
		sql.append(
				"       	u.userName,                                                                                  ");
		sql.append(
				"       	u.id userId,                                                                                 ");
		sql.append(
				"       	u.staffId,                                                                                   ");
		sql.append(
				"       	u. PASSWORD,                                                                                 ");
		sql.append(
				"       	u.photoId,                                                                                   ");
		sql.append(
				"       	u.rongLianAccount,                                                                           ");
		sql.append(
				"       	u.imToken,			                                                                         ");
		sql.append(
				"       	s. NAME,                                                                                     ");
		sql.append(
				"       	s.phone,                                                                                     ");
		sql.append(
				"       	s.email,                                                                                     ");
		sql.append(
				"       	s.tenantId,                                                                                  ");
		sql.append(
				"       	s.departmentId,                                                                              ");
		sql.append(
				"       	s.orgId,                                                                                     ");
		sql.append(
				"       	dp.depName departmentName,                                                                   ");
		sql.append(
				"       	org.orgName,                                                                                 ");
		sql.append(
				"       	t.latitude,                                                                                  ");
		sql.append(
				"       	t.latitudeDone,                                                                              ");
		sql.append(
				"       	t.longitude,                                                                                 ");
		sql.append(
				"       	t.longitudeDone                                                                              ");
		sql.append(
				"       FROM                                                                                          ");
		sql.append(
				"       	cloud_user u                                                                                 ");
		sql.append(
				"       LEFT JOIN cloud_staff s ON s.id = u.staffId AND s.beenDeleted = ?                             ");
		sql.append(
				"       LEFT JOIN cloud_department dp ON dp.id = s.departmentId                                       ");
		sql.append(
				"       LEFT JOIN cloud_organization org ON org.id = s.orgId LEFT JOIN                                ");
		sql.append(
				"        cloud_management_tenant t ON s.tenantId = t.id AND t.beenDeleted = ?                         ");
		sql.append(
				"       WHERE                                                                                         ");
		sql.append(
				"        (                                                                                            ");
		sql.append(
				"       	u.userName = ? and u.beenDeleted=?                                                           ");
		sql.append(
				"       	OR (                                                                                         ");
		sql.append(
				"       		s.phone = ?                                                                              ");
		sql.append(
				"       		AND u.userName IS NOT NULL and u.beenDeleted=?                                           ");
		sql.append(
				"       	)                                                                                            ");
		sql.append(
				"       )                                                                                             ");

		args.add(BakDeleteModel.NO_DELETED);
		args.add(BakDeleteModel.NO_DELETED);
		args.add(userName);
		args.add(BakDeleteModel.NO_DELETED);
		args.add(userName);
		args.add(BakDeleteModel.NO_DELETED);

		return jdbcTemplate.query(sql.toString(), args.toArray(),
				BeanPropertyRowMapper.newInstance(LoginReturnInfoDto.class));
	}

	private List<LoginReturnInfoDto> getLoginInfoByTenantCode(String tenantCode, String userName) {
		StringBuffer sql = new StringBuffer();
		List<Object> args = new ArrayList<Object>();

		sql.append(" SELECT u.userName,u.id userId,u.staffId,u.password, u.photoId, u.rongLianAccount,");
		sql.append(" 	s.name,s.phone,s.email,s.tenantId,s.departmentId,s.orgId,	");
		sql.append(" 	dp.depName departmentName, ");
		sql.append(" 	org.orgName, ");
		sql.append("    t.latitude,t.latitudeDone,t.longitude,t.longitudeDone ");
		sql.append(" FROM cloud_user u,  											");
		sql.append(
				" 	cloud_staff s left join cloud_department dp on dp.id=s.departmentId left join cloud_organization org on org.id=s.orgId ,cloud_management_tenant t					");
		sql.append(" WHERE s.tenantId=t.id AND s.id=u.staffId AND t.tenantCode=?    ");
		sql.append("   AND t.beenDeleted=? AND u.userName=? 						");
		sql.append("   AND s.beenDeleted=? 											");
		sql.append("   AND u.beenDeleted=? 											");

		args.add(tenantCode);
		args.add(BakDeleteModel.NO_DELETED);

		args.add(userName);
		args.add(BakDeleteModel.NO_DELETED);
		args.add(BakDeleteModel.NO_DELETED);

		return jdbcTemplate.query(sql.toString(), args.toArray(),
				BeanPropertyRowMapper.newInstance(LoginReturnInfoDto.class));
	}

	private List<LoginReturnInfoDto> getLoginInfoBySystemCode(String systemCode, String userName) {
		StringBuffer sql = new StringBuffer();
		List<Object> args = new ArrayList<Object>();

		sql.append(
				" SELECT u.userName,u.id userId,u.staffId,u.password, u.photoId,s.name,s.phone,s.email,s.tenantId,s.departmentId,s.orgId, ");
		sql.append(" 	dp.depName departmentName, ");
		sql.append(" 	org.orgName, ");
		sql.append("    tnt.latitude,tnt.latitudeDone,tnt.longitude,tnt.longitudeDone ");
		sql.append(
				" from cloud_user u,cloud_staff s LEFT JOIN cloud_management_tenant tnt on tnt.id=s.tenantId left join cloud_department dp on dp.id=s.departmentId left join cloud_organization org on org.id=s.orgId");
		sql.append(" where u.staffId=s.id ");
		sql.append("   and u.userName=? ");
		sql.append(" and ( ");
		sql.append(" 		EXISTS(select 1 from cloud_system t where t.tenantId=s.tenantId and t.systemCode=?) ");
		sql.append(" 		OR ");
		sql.append(
				" 		EXISTS(select 1 from cloud_user_role ur,cloud_role r,cloud_system t where u.id=ur.userId and ur.roleId=r.id and r.systemId=t.id and t.systemCode=?) ");
		sql.append(" 	) ");
		sql.append("   AND s.beenDeleted=? ");
		sql.append("   AND u.beenDeleted=? ");

		args.add(userName);
		args.add(systemCode);
		args.add(systemCode);
		args.add(BakDeleteModel.NO_DELETED);
		args.add(BakDeleteModel.NO_DELETED);

		return jdbcTemplate.query(sql.toString(), args.toArray(),
				BeanPropertyRowMapper.newInstance(LoginReturnInfoDto.class));
	}

	@Override
	public List<String> getBusinessSystemCodeList(String userId) {
		StringBuffer sql = new StringBuffer();
		List<Object> args = new ArrayList<Object>();
		sql.append(" SELECT DISTINCT c.systemCode ");
		sql.append("  from cloud_user_role a,cloud_role b,cloud_system c ");
		sql.append(" where a.userId=? ");
		sql.append("   and a.roleId=b.id ");
		sql.append("   and b.systemId=c.id ");
		sql.append("   and a.beenDeleted=? ");
		sql.append("   and b.beenDeleted=? ");
		sql.append("   and c.beenDeleted=? ");

		args.add(userId);
		args.add(BakDeleteModel.NO_DELETED);
		args.add(BakDeleteModel.NO_DELETED);
		args.add(BakDeleteModel.NO_DELETED);

		return jdbcTemplate.queryForList(sql.toString(), args.toArray(), String.class);
	}

	@Override
	public List<String> getAllUserIds() {
		String sql = "select id from cloud_user a where a.beenDeleted=?";
		List<Object> args = new ArrayList<Object>();
		args.add(BakDeleteModel.NO_DELETED);
		return jdbcTemplate.queryForList(sql, args.toArray(), String.class);
	}

	@Override
	public CloudUserRestDto getUserByUserNameAndTenantCode(String userName, String tenantCode) {
		if (StringUtils.isEmpty(userName) || StringUtils.isEmpty(tenantCode)) {
			return null;
		}

		StringBuffer sql = new StringBuffer();
		sql.append("       SELECT                                                      ");
		sql.append("       	cu.*                                                                ");
		sql.append("       FROM                                                                 ");
		sql.append("       	cloud_user cu                                                       ");
		sql.append("       LEFT JOIN cloud_staff cs ON cs.id = cu.staffId                       ");
		sql.append("       LEFT JOIN cloud_management_tenant cmt ON cmt.id = cs.tenantId        ");
		sql.append("       WHERE                                                                ");
		sql.append("       	cu.userName = '" + userName + "' 									");
		sql.append("       AND cmt.tenantCode = '" + tenantCode + "'  							");

		List<CloudUserRestDto> cloudUserList = jdbcTemplate.query(sql.toString(), new Object[0],
				BeanPropertyRowMapper.newInstance(CloudUserRestDto.class));
		if (CollectionUtils.isEmpty(cloudUserList)) {
			return null;
		} else {
			return cloudUserList.get(0);
		}
	}

	@Override
	public List<UserFunctionDto> getFunctionsByUserId(String userId) {
		if (StringUtils.isEmpty(userId)) {
			return null;
		}

		StringBuffer sql = new StringBuffer();
		List<Object> args = new ArrayList<Object>();
		sql.append(" SELECT c.code functionCode,d.systemCode ");
		sql.append(" from cloud_user_role a,cloud_function_role b,cloud_function c,cloud_system d ");
		sql.append(" where a.userId=? ");
		sql.append("   and a.roleId=b.roleId ");
		sql.append("   and b.functionId=c.id ");
		sql.append("   and c.systemId=d.id ");
		sql.append("   and a.beenDeleted=? ");
		sql.append("   and b.beenDeleted=? ");
		sql.append("   and c.beenDeleted=? ");
		sql.append(" order by d.systemCode,c.code ");

		args.add(userId);
		args.add(BakDeleteModel.NO_DELETED);
		args.add(BakDeleteModel.NO_DELETED);
		args.add(BakDeleteModel.NO_DELETED);

		return jdbcTemplate.query(sql.toString(), args.toArray(),
				BeanPropertyRowMapper.newInstance(UserFunctionDto.class));
	}

	@Override
	public List<LoginReturnInfoDto> getLoginInfoBySystemCode(String userName) {
		StringBuffer sql = new StringBuffer();
		List<Object> args = new ArrayList<Object>();
		sql.append("  SELECT                                       ");
		sql.append("  	cu.userName,                               ");
		sql.append("  	cu.photoId,                                ");
		sql.append("       cu.rongLianAccount,                     ");
		sql.append("  	cu.id userId,                              ");
		sql.append("  	cu.staffId,                                ");
		sql.append("  	cu. PASSWORD,                              ");
		sql.append("  	cs. NAME,                                  ");
		sql.append("  	cs.phone,                                  ");
		sql.append("  	cs.email,                                  ");
		sql.append("  	cs.tenantId,                               ");
		sql.append("                                               ");
		sql.append("  	cs.departmentId,                           ");
		sql.append("  	cs.orgId                                   ");
		sql.append("  FROM                                         ");
		sql.append("  	cloud_user cu,                             ");
		sql.append("  	cloud_staff cs                             ");
		sql.append("  WHERE                                        ");
		sql.append("  	cu.staffId = cs.id                         ");
		sql.append("  AND cu.userName = ?                      ");
		sql.append("  AND cs.beenDeleted = ?                       ");
		sql.append("  AND cu.beenDeleted = ?                       ");
		args.add(userName);
		args.add(BakDeleteModel.NO_DELETED);
		args.add(BakDeleteModel.NO_DELETED);
		return jdbcTemplate.query(sql.toString(), args.toArray(),
				BeanPropertyRowMapper.newInstance(LoginReturnInfoDto.class));
	}

	@Override
	public List<CloudUserDto> getUsersByCondiction(Map<String, String> paramMap) {
		List<Object> args = Lists.newArrayList();
		StringBuffer sql = new StringBuffer();
		sql.append("  SELECT                                                                 ");
		sql.append(
				"  	cu.*, cs.id staffId,cs.name staffName, cs.orgId,cs.departmentId,                                                 ");
		sql.append(
				"  	cu.userName ,   cs.orgName ,	cd.depName departName ,                                                  ");
		sql.append("   cs.tenantId,                          ");
		sql.append("   	cs. CODE,                            ");
		sql.append("   	cs.gender,                           ");
		sql.append("   	cs.nationId,                         ");
		sql.append("   	cs.nationName,                       ");
		sql.append("   	cs.birthday,                         ");
		sql.append("   	cs.healthId,                         ");
		sql.append("   	cs.healthName,                       ");
		sql.append("   	cs.credentialType,                   ");
		sql.append("   	cs.credentialNum,                    ");
		sql.append("   	cs.maritalStatusId,                  ");
		sql.append("   	cs.maritalStatusName,                ");
		sql.append("   	cs.politicalStatusId,                ");
		sql.append("   	cs.politicalStatusName,              ");
		sql.append("   	cs.joinWorkTime,                     ");
		sql.append("   	cs.workYearLimit,                    ");
		sql.append("   	cs.isLeave,                          ");
		sql.append("   	cs.leaveTime,                        ");
		sql.append("   	cs.description,                      ");
		sql.append("   	cs.birthPlace,                       ");
		sql.append("   	cs.presentPlace,                     ");
		sql.append("   	cs.livePlace,                        ");
		sql.append("   	cs.phone,                            ");
		sql.append("   	cs.officeTel,                        ");
		sql.append("   	cs.email,                            ");
		sql.append("   	cs.innerEmail,                       ");
		sql.append("   	cs.graduate,                         ");
		sql.append("   	cs.educationId,                      ");
		sql.append("   	cs.educationName,                    ");
		sql.append("   	cs.authorizeId,                      ");
		sql.append("   	cs.authorizeName,                    ");
		sql.append("   	cs.postId,                           ");
		sql.append("   	cs.postName,                         ");
		sql.append("   	cs.partyPostId,                      ");
		sql.append("   	cs.partyPostName,                    ");
		sql.append("   	cs.entryHereTime,                    ");
		sql.append("   	cs.idCard,                           ");
		sql.append("   	cs.socialSecurityNo,                 ");
		sql.append("   	cs.socialSecuritycase,               ");
		sql.append("   	cs.orderIndex,                       ");
		sql.append("   	cs.workTypeCode                      ");
		sql.append("  FROM                                                                   ");
		sql.append("  	cloud_user cu                                                        ");
		sql.append("  LEFT JOIN cloud_staff cs ON cu.staffId = cs.id                         ");
		sql.append("  LEFT JOIN cloud_department cd ON cs.departmentId = cd.id                         ");
		sql.append("  WHERE                                                                  ");
		sql.append("  	cs.tenantId = ?                                                      ");
		sql.append("  AND cs.beenDeleted = ?                                                 ");
		sql.append("  AND cu.beenDeleted = ?                                                 ");
		args.add(paramMap.get(TENANT_ID));
		args.add(BakDeleteModel.NO_DELETED);
		args.add(BakDeleteModel.NO_DELETED);
		/* 此处为可选条件 */
		if (StringUtils.isNotBlank(paramMap.get(USER_NAME))) {
			sql.append("     AND cu.userName = ?                                                ");
			args.add(paramMap.get(USER_NAME));
		}
		List<CloudUserDto> cloudStaffDtos = jdbcTemplate.query(sql.toString(), args.toArray(),
				BeanPropertyRowMapper.newInstance(CloudUserDto.class));
		return cloudStaffDtos;
	}

	@Override
	public Map<String, String> findUserNamesByStaffIds(List<String> ids) {
		Map<String, String> idNameMap = Maps.newHashMap();

		List<CloudUser> cloudUsers = null;
		// 根据staffIds获取用户信息
		cloudUsers = getUsersByStaffIds(ids);

		if (CollectionUtils.isNotEmpty(cloudUsers)) {
			for (CloudUser cloudUser : cloudUsers) {
				idNameMap.put(cloudUser.getStaffId(), cloudUser.getUserName());
			}
		}
		return idNameMap;
	}

	@Override
	public List<CloudUser> getUsersByStaffIds(List<String> ids) {
		List<SearchFilter> searchFilters = Lists.newArrayList();
		List<CloudUser> cloudUsers = null;
		// 为空返回一个空的list
		if (CollectionUtils.isEmpty(ids)) {
			return Lists.newArrayList();
		}
		searchFilters.add(new SearchFilter("staffId", Operator.IN, ids.toArray()));
		cloudUsers = this.findListByFilter(searchFilters, null);
		return cloudUsers;
	}

	@Override
	public List<CloudUserDto> findListByCompanyIds(List<String> companyIds) {

		List<Object> args = Lists.newArrayList();
		StringBuffer sql = new StringBuffer();
		sql.append("  SELECT                                                                 ");
		sql.append(
				"  	cu.*, cs.id staffId,cs.name staffName, cs.orgId,cs.departmentId,                                                 ");
		sql.append(
				"  	cu.userName ,   cs.orgName ,	cd.depName departName ,                                                  ");
		sql.append("   cs.tenantId,                          ");
		sql.append("   	cs. CODE,                            ");
		sql.append("   	cs.gender,                           ");
		sql.append("   	cs.nationId,                         ");
		sql.append("   	cs.nationName,                       ");
		sql.append("   	cs.birthday,                         ");
		sql.append("   	cs.healthId,                         ");
		sql.append("   	cs.healthName,                       ");
		sql.append("   	cs.credentialType,                   ");
		sql.append("   	cs.credentialNum,                    ");
		sql.append("   	cs.maritalStatusId,                  ");
		sql.append("   	cs.maritalStatusName,                ");
		sql.append("   	cs.politicalStatusId,                ");
		sql.append("   	cs.politicalStatusName,              ");
		sql.append("   	cs.joinWorkTime,                     ");
		sql.append("   	cs.workYearLimit,                    ");
		sql.append("   	cs.isLeave,                          ");
		sql.append("   	cs.leaveTime,                        ");
		sql.append("   	cs.description,                      ");
		sql.append("   	cs.birthPlace,                       ");
		sql.append("   	cs.presentPlace,                     ");
		sql.append("   	cs.livePlace,                        ");
		sql.append("   	cs.phone,                            ");
		sql.append("   	cs.officeTel,                        ");
		sql.append("   	cs.email,                            ");
		sql.append("   	cs.innerEmail,                       ");
		sql.append("   	cs.graduate,                         ");
		sql.append("   	cs.educationId,                      ");
		sql.append("   	cs.educationName,                    ");
		sql.append("   	cs.authorizeId,                      ");
		sql.append("   	cs.authorizeName,                    ");
		sql.append("   	cs.postId,                           ");
		sql.append("   	cs.postName,                         ");
		sql.append("   	cs.partyPostId,                      ");
		sql.append("   	cs.partyPostName,                    ");
		sql.append("   	cs.entryHereTime,                    ");
		sql.append("   	cs.idCard,                           ");
		sql.append("   	cs.socialSecurityNo,                 ");
		sql.append("   	cs.socialSecuritycase,               ");
		sql.append("   	cs.orderIndex,                       ");
		sql.append("   	cs.workTypeCode                      ");
		sql.append("  FROM                                                                   ");
		sql.append("  	cloud_user cu                                                        ");
		sql.append("  LEFT JOIN cloud_staff cs ON cu.staffId = cs.id                         ");
		sql.append("  LEFT JOIN cloud_department cd ON cs.departmentId = cd.id               ");
		sql.append("  WHERE                                                                  ");
		sql.append("   cs.beenDeleted = ?                                                 ");
		sql.append("  AND cu.beenDeleted = ?                                                 ");

		args.add(BakDeleteModel.NO_DELETED);
		args.add(BakDeleteModel.NO_DELETED);
		String[] array = new String[companyIds.size()];
		if (CollectionUtils.isNotEmpty(companyIds)) {
			sql.append("  AND (                                                                     ");
			sql.append("       cs.orgId in (                                                   ");
			Arrays.fill(array, 0, companyIds.size(), " ? ");
			sql.append("                    " + StringUtils.join(array, " , ")
					+ "                                                  ");
			sql.append("                   )                                                              ");

			sql.append("       OR                                                              ");
			sql.append("   cs.departmentId in (                                                   ");
			Arrays.fill(array, 0, companyIds.size(), " ? ");
			sql.append("                    " + StringUtils.join(array, " , ")
					+ "                                                  ");
			sql.append("                      )                                                              ");

			sql.append("       )                                                              ");
			args.addAll(companyIds);
			args.addAll(companyIds);
		}

		return jdbcTemplate.query(sql.toString(), args.toArray(),
				BeanPropertyRowMapper.newInstance(CloudUserDto.class));
	}

	public static void main(String[] args) {
		StringBuffer sql = new StringBuffer();
		List<String> companyIds = Lists.newArrayList();
		companyIds.add("2");
		companyIds.add("6");
		companyIds.add("3");
		companyIds.add("4");
		String[] array = new String[companyIds.size()];
		if (CollectionUtils.isNotEmpty(companyIds)) {
			sql.append("  AND (                                                                     ");
			sql.append("       cs.orgId in (                                                   ");
			Arrays.fill(array, 0, companyIds.size(), " ? ");
			sql.append("                    " + StringUtils.join(array, " , ")
					+ "                                                  ");
			sql.append("       )                                                              ");

			sql.append("       OR                                                              ");
			sql.append("   cs.departmentId in (                                                   ");
			Arrays.fill(array, 0, companyIds.size(), " ? ");
			sql.append("                    " + StringUtils.join(array, " , ")
					+ "                                                  ");
			sql.append("       )                                                              ");

			sql.append("       )                                                              ");
			System.out.println(sql);
		}
	}

	@Override
	public List<IdNameDto> getUserNamesByIds(List<String> ids) throws Exception {
		if (CollectionUtils.isEmpty(ids)) {
			return null;
		}
		String str = "";
		for (int i = 0; i < ids.size(); i++) {
			if (i == 0) {
				str += "?";
			} else {
				str += ",?";
			}
		}

		String sql = "select id,userName name from cloud_user where beenDeleted=? and id in (" + str + ")";

		List<Object> argList = Lists.newArrayList();
		argList.add(BakDeleteModel.NO_DELETED);
		argList.addAll(ids);

		return jdbcTemplate.query(sql, argList.toArray(), BeanPropertyRowMapper.newInstance(IdNameDto.class));
	}

	@Override
	public List<String> findIdListByTenantId(String tenantId) {
		if (StringUtils.isEmpty(tenantId)) {
			return null;
		}
		StringBuffer sql = new StringBuffer();
		sql.append("       SELECT                                                      ");
		sql.append("       	cu.id                                                                ");
		sql.append("       FROM                                                                 ");
		sql.append("       	cloud_user cu                                                       ");
		sql.append("       LEFT JOIN cloud_staff cs ON cs.id = cu.staffId                       ");
		sql.append("       LEFT JOIN cloud_management_tenant cmt ON cmt.id = cs.tenantId        ");
		sql.append("       WHERE cu.beenDeleted = ? and cs.beenDeleted = ? and cmt.beenDeleted = ?");
		sql.append("       and cmt.id = ?");
		List<Object> args = Lists.newArrayList();
		args.add(BakDeleteModel.NO_DELETED);
		args.add(BakDeleteModel.NO_DELETED);
		args.add(BakDeleteModel.NO_DELETED);
		args.add(tenantId);
		return jdbcTemplate.queryForList(sql.toString(), args.toArray(), String.class);
	}

	@Override
	public List<String> findNoTenantUserIdList() {
		StringBuffer sql = new StringBuffer();
		sql.append("       SELECT                                                      ");
		sql.append("       	cu.id                                                                ");
		sql.append("       FROM                                                                 ");
		sql.append("       	cloud_user cu                                                       ");
		sql.append("       LEFT JOIN cloud_staff cs ON cs.id = cu.staffId                       ");
		sql.append("       WHERE cu.beenDeleted = ? and cs.beenDeleted = ? and cs.tenantId is null ");
		List<Object> args = Lists.newArrayList();
		args.add(BakDeleteModel.NO_DELETED);
		args.add(BakDeleteModel.NO_DELETED);
		return jdbcTemplate.queryForList(sql.toString(), args.toArray(), String.class);
	}

	@Override
	public Page<CloudUserDto> findPageListBySearchDto(Pageable pageable, CloudStaffSearchDto searchDto) {
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

		List<CloudUserDto> pageList = jdbcTemplate.query(sqlString, argList.toArray(),
				BeanPropertyRowMapper.newInstance(CloudUserDto.class));

		return new PageImpl<>(pageList, pageable, totalCnt);
	}

	private Map<String, Object> getSqlOfPageBySearchDto(CloudStaffSearchDto searchDto) {
		StringBuffer sql = new StringBuffer();
		List<Object> argList = new ArrayList<>();

		sql.append(
				"       SELECT        u.*                                                                                  ");
		sql.append("            ,s.code                                                       ");
		sql.append("            ,s.`name`  staffName                                                       ");
		sql.append(
				"       FROM                                                                                            ");
		sql.append(
				"       	cloud_staff s                                                                                  ");
		sql.append(
				"        JOIN cloud_user u ON s.id = u.staffId                                                      ");
		sql.append(
				"       AND u.beenDeleted = ?                                                                           ");
		sql.append(
				"       WHERE                                                                                           ");
		sql.append("       	s.tenantId =?                                                ");
		sql.append(
				"       AND s.beenDeleted = ?                                                                          ");
		argList.add(BakDeleteModel.NO_DELETED);
		argList.add(searchDto.getTenantId());
		argList.add(BakDeleteModel.NO_DELETED);

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
		// 返回结果
		Map<String, Object> map = new HashMap<>();
		map.put("sql", sql);
		map.put("argList", argList);
		return map;
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

}
