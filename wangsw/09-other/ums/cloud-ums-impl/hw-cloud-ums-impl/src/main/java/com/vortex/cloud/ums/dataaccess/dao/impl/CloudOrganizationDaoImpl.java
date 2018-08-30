package com.vortex.cloud.ums.dataaccess.dao.impl;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.google.common.collect.Lists;
import com.vortex.cloud.ums.dataaccess.dao.ICloudOrganizationDao;
import com.vortex.cloud.ums.dataaccess.service.ICentralCacheRedisService;
import com.vortex.cloud.ums.dataaccess.service.impl.CentralCacheRedisServiceImpl;
import com.vortex.cloud.ums.dto.CloudDeptOrgDto;
import com.vortex.cloud.ums.dto.TenantDeptOrgDto;
import com.vortex.cloud.ums.dto.TreeDto;
import com.vortex.cloud.ums.enums.CompanyTypeEnum;
import com.vortex.cloud.ums.model.CloudOrganization;
import com.vortex.cloud.ums.support.ManagementConstant;
import com.vortex.cloud.vfs.common.lang.StringUtil;
import com.vortex.cloud.vfs.data.hibernate.repository.SimpleHibernateRepository;
import com.vortex.cloud.vfs.data.model.BakDeleteModel;
import com.vortex.cloud.vfs.data.support.SearchFilter;
import com.vortex.cloud.vfs.data.support.SearchFilter.Operator;

@Repository("cloudOrganizationDao")
public class CloudOrganizationDaoImpl extends SimpleHibernateRepository<CloudOrganization, String> implements ICloudOrganizationDao {
	@Resource
	private JdbcTemplate jdbcTemplate;
	@Resource(name = CentralCacheRedisServiceImpl.CLASSNAME)
	private ICentralCacheRedisService centralCacheRedisService;

	@Override
	public DetachedCriteria getDetachedCriteria() {
		DetachedCriteria criteria = DetachedCriteria.forClass(getPersistentClass(), "org");
		criteria.add(Restrictions.eq("beenDeleted", BakDeleteModel.NO_DELETED));

		return criteria;
	}

	@Override
	public <S extends CloudOrganization> S save(S entity) {
		CloudOrganization parent = super.findOne(entity.getParentId());
		if (parent == null) { // 顶级记录
			List<SearchFilter> filterList = new ArrayList<>();
			filterList.add(new SearchFilter("tenantId", Operator.EQ, entity.getTenantId()));
			filterList.add(new SearchFilter("parentId", Operator.EQ, entity.getParentId()));
			filterList.add(new SearchFilter("beenDeleted", Operator.EQ, BakDeleteModel.NO_DELETED));
			List<CloudOrganization> siblingList = super.findListByFilter(filterList, null);
			int siblingListSize = 0;
			if (CollectionUtils.isNotEmpty(siblingList)) {
				siblingListSize = siblingList.size();
			}

			entity.setNodeCode(StringUtils.EMPTY + new DecimalFormat("00").format(siblingListSize + 1));
		} else {
			parent.setChildSerialNumer(parent.getChildSerialNumer() + 1);
			super.update(parent);

			entity.setNodeCode(parent.getNodeCode() + new DecimalFormat("00").format(parent.getChildSerialNumer()));
		}

		entity.setChildSerialNumer(0);
		CloudOrganization _entity = super.save(entity);
		// 更新机构部门缓存
		updateRedis(new TenantDeptOrgDto().transfer(_entity));
		return (S) _entity;
	}

	@Override
	public CloudOrganization update(CloudOrganization entity) {
		CloudOrganization _entity = super.update(entity);
		// 更新机构部门缓存
		updateRedis(new TenantDeptOrgDto().transfer(_entity));
		return _entity;
	}

	@Override
	public void delete(CloudOrganization entity) {
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
		logger.error(String.format("[同步redis,机构变动]，总耗时：%sms", (System.currentTimeMillis() - t0)));
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
		logger.error(String.format("[同步redis,机构刪除]，总耗时：%sms", (System.currentTimeMillis() - t0)));
	}

	@Override
	public CloudOrganization getOrganizationByCode(String orgCode, String tenantId) {
		StringBuffer sql = new StringBuffer();
		sql.append("  SELECT                                                ");
		sql.append("  	co.*                                                ");
		sql.append("  FROM                                                  ");
		sql.append("  	cloud_organization co, cloud_management_tenant cmt	");
		sql.append("  WHERE                                                 ");
		sql.append("  	cmt.id = co.tenantId 								");
		sql.append("  	AND co.orgCode = ?                  				");
		sql.append(" 	AND cmt.id= ?                      					");

		List<Object> argsList = new ArrayList<>();
		argsList.add(orgCode);
		argsList.add(tenantId);

		List<CloudOrganization> list = jdbcTemplate.query(sql.toString(), argsList.toArray(), BeanPropertyRowMapper.newInstance(CloudOrganization.class));
		if (CollectionUtils.isEmpty(list)) {
			return null;
		}

		return list.get(0);
	}

	@Override
	public boolean hasChild(String orgId) {
		String sql = "select count(1) from cloud_organization t where t.parentId=? and t.beenDeleted=?";
		List<Object> argList = new ArrayList<>();
		argList.add(orgId);
		argList.add(BakDeleteModel.NO_DELETED);

		Long count = jdbcTemplate.queryForObject(sql, argList.toArray(), Long.class);

		if (count > 0) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean hasStaff(String orgId) {
		String sql = "select count(1) from cloud_staff t where t.orgId=? and t.beenDeleted=?";
		List<Object> argList = new ArrayList<>();
		argList.add(orgId);
		argList.add(BakDeleteModel.NO_DELETED);

		Long count = jdbcTemplate.queryForObject(sql, argList.toArray(), Long.class);

		if (count > 0) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public List<TenantDeptOrgDto> findOrganizationChild(String departId, String nodeCode, String id, List<Integer> beenDeletedFlags) {

		StringBuffer sql = new StringBuffer();
		List<Object> argList = new ArrayList<>();

		sql.append("  SELECT                                                       ");
		sql.append("            co.tenantId,                                       ");
		sql.append("            	co.parentId,                                   ");
		sql.append("            	co.departmentId,                               ");
		sql.append("            	'3' type,                                      ");
		sql.append("            	co.id,                                         ");
		sql.append("            	co.orgName `name`,                             ");
		sql.append("            	co.orgCode `code`,                             ");
		sql.append("            	co.head,                                       ");
		sql.append("            	co.headMobile,                                 ");
		sql.append("            	co.description,                                ");
		sql.append("            	co.lngLats,                                    ");
		sql.append("            	co.address,                                    ");
		sql.append("            	co.email  , co.beenDeleted,co.deletedTime,co.createTime,co.lastChangeTime                                     ");

		sql.append("  FROM                                                         ");
		sql.append("  	cloud_organization co                                      ");
		sql.append("  WHERE        1=1                                                ");
		if (CollectionUtils.isEmpty(beenDeletedFlags)) {
			sql.append("  	AND co.beenDeleted = ?                                         ");
			argList.add(BakDeleteModel.NO_DELETED);
		} else {
			sql.append(" AND co.beenDeleted in ( ");
			for (int j = 0; j < beenDeletedFlags.size(); j++) {
				if (j != 0)
					sql.append(",");
				sql.append(" ? ");
			}
			sql.append(")");

			argList.addAll(beenDeletedFlags);
		}

		sql.append("  AND co.nodeCode LIKE ?                                       ");
		sql.append("  AND  co.departmentId = ?                                     ");
		sql.append("  AND  co.id != ?                                     ");
		argList.add(nodeCode + "%");
		argList.add(departId);
		argList.add(id);
		List<TenantDeptOrgDto> tenantDeptOrgDtos = jdbcTemplate.query(sql.toString(), argList.toArray(), BeanPropertyRowMapper.newInstance(TenantDeptOrgDto.class));
		return tenantDeptOrgDtos;
	}

	@Override
	public List<TenantDeptOrgDto> getDepartmentsOrOrgByIds(String[] ids, List<Integer> beenDeletedFlags) {

		StringBuffer sql = new StringBuffer();
		List<Object> argList = new ArrayList<>();
		sql.append("                     SELECT                                    ");
		sql.append("                     	cd.tenantId,                           ");
		sql.append("                     	-1 parentId,                         ");
		sql.append("                     	cd.id departmentId,                    ");
		sql.append("                     	cd.depType type,                       ");
		sql.append("                     		'" + CompanyTypeEnum.DEPART.getKey() + "' companyType   ,                         ");
		sql.append("                     	cd.id,                                 ");
		sql.append("                     	cd.depName NAME,                       ");
		sql.append("                     	cd.depCode CODE,                       ");
		sql.append("                     	cd.head,                               ");
		sql.append("                     	cd.headMobile,                         ");
		sql.append("                     	cd.description,                        ");
		sql.append("                     	cd.lngLats,                            ");
		sql.append("                     	cd.address,                            ");
		sql.append("                     	cd.email, cd.beenDeleted,cd.deletedTime,cd.createTime,cd.lastChangeTime                               ");
		sql.append("                     FROM                                      ");
		sql.append("                     	cloud_department cd                    ");
		sql.append("                     WHERE          1=1                           ");

		if (CollectionUtils.isEmpty(beenDeletedFlags)) {
			sql.append("                AND 	cd.beenDeleted = ?                     ");
			argList.add(BakDeleteModel.NO_DELETED);
		} else {
			sql.append(" AND cd.beenDeleted in ( ");
			for (int j = 0; j < beenDeletedFlags.size(); j++) {
				if (j != 0)
					sql.append(",");
				sql.append(" ? ");
			}
			sql.append(")");

			argList.addAll(beenDeletedFlags);
		}

		if (ArrayUtils.isNotEmpty(ids)) {
			sql.append("                     AND cd.id IN (                      ");
			for (int i = 0; i < ids.length; i++) {
				if (i != ids.length - 1) {
					sql.append("                     ?,                     ");
				} else {
					sql.append("                     ?                     ");
				}
				argList.add(ids[i]);
			}

			sql.append("                    )                                        ");
		}
		sql.append("                     UNION      ALL                               ");
		sql.append("                     	SELECT                                 ");
		sql.append("                     		co.tenantId,                       ");
		sql.append("                     		co.parentId,                       ");
		sql.append("                     		co.departmentId,                   ");
		sql.append("                     		'3' type,                          ");
		sql.append("                     		'" + CompanyTypeEnum.ORG.getKey() + "' companyType   ,                         ");
		sql.append("                     		co.id,                             ");
		sql.append("                     		co.orgName NAME,                   ");
		sql.append("                     		co.orgCode CODE,                   ");
		sql.append("                     		co.head,                           ");
		sql.append("                     		co.headMobile,                     ");
		sql.append("                     		co.description,                    ");
		sql.append("                     		co.lngLats,                        ");
		sql.append("                     		co.address,                        ");
		sql.append("                     		co.email  ,co.beenDeleted,co.deletedTime,co.createTime,co.lastChangeTime                               ");
		sql.append("                     	FROM                                   ");
		sql.append("                     		cloud_organization co              ");
		sql.append("                     	WHERE        1=1                          ");
		if (CollectionUtils.isEmpty(beenDeletedFlags)) {
			sql.append("                     AND		co.beenDeleted = ?                 ");
			argList.add(BakDeleteModel.NO_DELETED);
		} else {
			sql.append(" AND co.beenDeleted in ( ");
			for (int j = 0; j < beenDeletedFlags.size(); j++) {
				if (j != 0)
					sql.append(",");
				sql.append(" ? ");
			}
			sql.append(")");
			argList.addAll(beenDeletedFlags);
		}

		if (ArrayUtils.isNotEmpty(ids)) {
			sql.append("                     AND co.id IN (                      ");
			for (int i = 0; i < ids.length; i++) {
				if (i != ids.length - 1) {
					sql.append("                     ?,                     ");
				} else {
					sql.append("                     ?                     ");
				}
				argList.add(ids[i]);
			}

			sql.append("                    )                                        ");
		}
		List<TenantDeptOrgDto> tenantDeptOrgDtos = jdbcTemplate.query(sql.toString(), argList.toArray(), BeanPropertyRowMapper.newInstance(TenantDeptOrgDto.class));
		return tenantDeptOrgDtos;
	}

	@Override
	public CloudOrganization findById(String id, List<Integer> beenDeletedFlags) {
		StringBuffer sql = new StringBuffer();
		List<Object> argList = new ArrayList<>();
		sql.append(" SELECT * FROM cloud_organization WHERE id = ?  ");
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

		List<CloudOrganization> cloudOrganizationList = jdbcTemplate.query(sql.toString(), argList.toArray(), BeanPropertyRowMapper.newInstance(CloudOrganization.class));
		if (CollectionUtils.isNotEmpty(cloudOrganizationList)) {
			return cloudOrganizationList.get(0);
		}
		return null;
	}

	@Override
	public List<TreeDto> listOrgByDeptId(String deptId) throws Exception {
		if (StringUtils.isEmpty(deptId)) {
			return null;
		}

		StringBuffer sql = new StringBuffer();
		sql.append(" select id,orgName name,parentId ");
		sql.append(" from cloud_organization t ");
		sql.append(" where t.beenDeleted=? ");
		sql.append("   and t.departmentId=? ");

		List<Object> args = new ArrayList<Object>();
		args.add(BakDeleteModel.NO_DELETED);
		args.add(deptId);

		return jdbcTemplate.query(sql.toString(), args.toArray(), BeanPropertyRowMapper.newInstance(TreeDto.class));
	}

	@Override
	public List<TreeDto> listOrgByParentId(String parentId) throws Exception {
		if (StringUtils.isEmpty(parentId)) {
			return null;
		}

		CloudOrganization org = this.findOne(parentId);
		if (org == null) {
			return null;
		}

		StringBuffer sql = new StringBuffer();
		sql.append(" select id,orgName name,parentId ");
		sql.append(" from cloud_organization t ");
		sql.append(" where t.beenDeleted=? ");
		sql.append("   and t.departmentId=? ");
		sql.append("   and t.nodeCode like ? ");
		sql.append("   and t.id<>? ");

		List<Object> args = new ArrayList<Object>();
		args.add(BakDeleteModel.NO_DELETED);
		args.add(org.getDepartmentId());
		args.add(org.getNodeCode() + "%");
		args.add(org.getId());

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

		String sql = "select a.*,a.orgName depName,a.orgCode depCode from cloud_organization a where a.id in (" + idstr + ")";
		List<Object> args = new ArrayList<Object>();
		args.addAll(ids);
		return jdbcTemplate.query(sql.toString(), args.toArray(), BeanPropertyRowMapper.newInstance(CloudDeptOrgDto.class));
	}
}
