package com.vortex.cloud.ums.dataaccess.dao.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.google.common.collect.Lists;
import com.vortex.cloud.ums.dataaccess.dao.ICloudFunctionDao;
import com.vortex.cloud.ums.dto.CloudFunctionDto;
import com.vortex.cloud.ums.dto.CloudSystemFunctionDto;
import com.vortex.cloud.ums.dto.CloudTreeDto;
import com.vortex.cloud.ums.dto.android.CloudFunctionAndroidDto;
import com.vortex.cloud.ums.model.CloudFunction;
import com.vortex.cloud.vfs.data.hibernate.repository.SimpleHibernateRepository;
import com.vortex.cloud.vfs.data.model.BakDeleteModel;
import com.vortex.cloud.vfs.data.support.SearchFilter;
import com.vortex.cloud.vfs.data.support.SearchFilter.Operator;

/**
 * 功能dao
 * 
 * @author lsm
 * @date 2016年4月1日
 */
@Repository("cloudFunctionDao")
public class CloudFunctionDaoImpl extends SimpleHibernateRepository<CloudFunction, String> implements ICloudFunctionDao {
	@Resource
	private JdbcTemplate jdbcTemplate;

	@Override
	public DetachedCriteria getDetachedCriteria() {
		return defaultCriteria();
	}

	private DetachedCriteria defaultCriteria() {
		DetachedCriteria criteria = DetachedCriteria.forClass(getPersistentClass(), "cloudFunction");
		criteria.add(Restrictions.eq("beenDeleted", 0));
		return criteria;
	}

	@Override
	public boolean isCodeExistsForSystem(String systemId, String code) {
		boolean rst = false;
		if (StringUtils.isEmpty(code) || StringUtils.isEmpty(systemId)) {
			return rst;
		}

		String sql = "select count(1) from cloud_function where code='" + code + "' and systemId='" + systemId + "' AND beenDeleted = 0";

		int count = jdbcTemplate.queryForObject(sql.toString(), Integer.class);
		if (count > 0) {
			rst = true;
		}

		return rst;
	}

	@Override
	public CloudFunction getFunctionByCode(String functionCode, String tenantId, String systemCode) {

		StringBuffer sql = new StringBuffer();
		sql.append("     SELECT DISTINCT                        ");
		sql.append("     	f.*                                 ");
		sql.append("     FROM                                   ");
		sql.append("     	cloud_function f, cloud_system s	");
		sql.append("     WHERE                                  ");
		sql.append("     	f.systemId = s.id 					");
		sql.append("     	AND s.systemCode = ?                ");
		sql.append("     	AND f.`code` = ?                    ");

		List<Object> args = Lists.newArrayList();
		args.add(systemCode);
		args.add(functionCode);

		List<CloudFunction> list = jdbcTemplate.query(sql.toString(), args.toArray(), BeanPropertyRowMapper.newInstance(CloudFunction.class));
		if (CollectionUtils.isEmpty(list)) {
			return null;
		}

		return list.get(0);

	}

	@Override
	public List<String> getFunctionsByRoleId(String roleId) {
		List<String> ids = jdbcTemplate.queryForList("select a.functionId from cloud_function_role a where a.roleId='" + roleId + "' and a.beenDeleted=0", String.class);
		return ids;
	}

	@Override
	public CloudFunctionDto getFunctionById(String id) {
		StringBuffer sql = new StringBuffer();
		sql.append(" SELECT                                                          ");
		sql.append(" 	cf.*, cfg.`name` groupName, s.systemName goalSystemName,cf2.name  mainFunctionName      ");
		sql.append(" FROM                                                            ");
		sql.append(" 	cloud_function cf                                            ");
		sql.append(" LEFT JOIN cloud_function_group cfg ON cfg.id = cf.groupId      ");
		sql.append(" LEFT JOIN cloud_function cf2   ON cf2.id = cf.mainFunctionId,       ");
		sql.append(" 	cloud_system s       										");
		sql.append(" WHERE                                                           ");
		sql.append(" 	cf.id = ?                                                      ");
		sql.append(" 	AND s.id = cf.goalSystemId                                    ");

		List<String> args = Lists.newArrayList();
		args.add(id);
		CloudFunctionDto dto = jdbcTemplate.queryForObject(sql.toString(), args.toArray(), BeanPropertyRowMapper.newInstance(CloudFunctionDto.class));
		return dto;
	}

	@Override
	public boolean hasFunction(String userId, String systemId, String functionId) {
		StringBuffer sql = new StringBuffer();

		sql.append(" SELECT count(1)            	");
		sql.append(" FROM cloud_function f,     	");
		sql.append(" 	cloud_function_role fr,   	");
		sql.append(" 	cloud_user_role ur        	");
		sql.append(" WHERE ur.userId = ?       		");
		sql.append(" 	AND ur.roleId = fr.roleId	");
		sql.append(" 	AND f.systemId = ?       	");
		sql.append(" 	AND f.id = ?             	");
		sql.append(" 	AND f.beenDeleted = ?   	");
		sql.append(" 	AND fr.beenDeleted = ?  	");
		sql.append(" 	AND ur.beenDeleted = ?  	");

		Object[] params = new Object[] { userId, systemId, functionId, BakDeleteModel.NO_DELETED, BakDeleteModel.NO_DELETED, BakDeleteModel.NO_DELETED };

		int count = jdbcTemplate.queryForObject(sql.toString(), params, Integer.class);

		if (count > 0) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public List<String> getFunctionList(String userId, String systemId) {
		StringBuffer sql = new StringBuffer();

		sql.append(" SELECT f.id                	");
		sql.append(" FROM cloud_function f,     	");
		sql.append(" 	cloud_function_role fr,   	");
		sql.append(" 	cloud_user_role ur        	");
		sql.append(" WHERE ur.userId = ?       		");
		sql.append(" 	AND ur.roleId = fr.roleId	");
		sql.append(" 	AND f.systemId = ?       	");
		sql.append(" 	AND f.beenDeleted = ?   	");
		sql.append(" 	AND fr.beenDeleted = ?  	");
		sql.append(" 	AND ur.beenDeleted = ?  	");

		Object[] params = new Object[] { userId, systemId, BakDeleteModel.NO_DELETED, BakDeleteModel.NO_DELETED, BakDeleteModel.NO_DELETED };

		return jdbcTemplate.queryForList(sql.toString(), params, String.class);
	}

	@Override
	public List<CloudFunction> getFunctionListByRoleId(String roleId) {
		StringBuffer sql = new StringBuffer();
		sql.append(" SELECT a.* ");
		sql.append(" from cloud_function a,cloud_function_role b ");
		sql.append(" where a.id=b.functionId ");
		sql.append(" and b.roleId=? ");
		sql.append("   and a.beenDeleted=? ");
		sql.append("   and b.beenDeleted=? ");

		List<Object> args = new ArrayList<Object>();
		args.add(roleId);
		args.add(BakDeleteModel.NO_DELETED);
		args.add(BakDeleteModel.NO_DELETED);

		return jdbcTemplate.query(sql.toString(), args.toArray(), BeanPropertyRowMapper.newInstance(CloudFunction.class));
	}

	@Override
	public List<String> getAllFunctions(String userId) {
		StringBuffer sql = new StringBuffer();
		sql.append(" SELECT DISTINCT c.code ");
		sql.append(" from cloud_user_role a,cloud_function_role b,cloud_function c ");
		sql.append(" where a.userId=? ");
		sql.append("   and a.roleId=b.roleId ");
		sql.append("   and b.functionId=c.id ");
		sql.append("   and a.beenDeleted=? ");
		sql.append("   and b.beenDeleted=? ");
		sql.append("   and c.beenDeleted=? ");

		List<Object> args = new ArrayList<Object>();
		args.add(userId);
		args.add(BakDeleteModel.NO_DELETED);
		args.add(BakDeleteModel.NO_DELETED);
		args.add(BakDeleteModel.NO_DELETED);

		return jdbcTemplate.queryForList(sql.toString(), args.toArray(), String.class);
	}

	@Override
	public List<CloudFunction> getByGroupId(String groupId) {
		List<SearchFilter> filterList = new ArrayList<SearchFilter>();

		filterList.add(new SearchFilter("groupId", Operator.EQ, groupId));

		Order order = new Order("functionType");
		List<Order> ol = Lists.newArrayList();
		ol.add(order);
		Sort sort = new Sort(ol);
		return super.findListByFilter(filterList, sort);
	}

	@Override
	public List<CloudFunctionAndroidDto> getFunctionsByUsreIdAndSystem(String userId, String systemCode) {
		List<Object> args = new ArrayList<Object>();
		StringBuffer sql = new StringBuffer();
		sql.append("        SELECT DISTINCT                                      ");
		sql.append("        	f.id,                                            ");
		sql.append("        	f. CODE,                                         ");
		sql.append("        	f. NAME,                                         ");
		sql.append("        	f.orderIndex                                     ");
		sql.append("        FROM                                                 ");
		sql.append("        	cloud_user_role ur,                              ");
		sql.append("        	cloud_function_role fr,                          ");
		sql.append("        	cloud_function f,                                ");
		sql.append("        	cloud_system s                                   ");
		sql.append("        WHERE                                                ");
		sql.append("        	ur.userId = ?                                    ");
		sql.append("        AND ur.roleId = fr.roleId AND ur.beenDeleted=?           ");
		sql.append("        AND fr.functionId = f.id AND fr.beenDeleted=?        ");
		args.add(userId);
		args.add(BakDeleteModel.NO_DELETED);
		args.add(BakDeleteModel.NO_DELETED);
		sql.append("        AND f.systemId = s.id                                ");
		if (StringUtils.isNotBlank(systemCode)) {
			sql.append("        AND s.systemCode = ?                                 ");
			args.add(systemCode);
		}
		sql.append("        ORDER BY                                             ");
		sql.append("        	f.orderIndex                                     ");

		return jdbcTemplate.query(sql.toString(), args.toArray(), BeanPropertyRowMapper.newInstance(CloudFunctionAndroidDto.class));
	}

	@Override
	public CloudFunction getByCode(String sysId, String funcCode) {
		String sql = "select * from cloud_function a where a.code=? and a.systemId=? and a.beenDeleted=?";
		List<Object> args = Lists.newArrayList();
		args.add(funcCode);
		args.add(sysId);
		args.add(BakDeleteModel.NO_DELETED);

		List<CloudFunction> list = jdbcTemplate.query(sql, args.toArray(), BeanPropertyRowMapper.newInstance(CloudFunction.class));
		if (CollectionUtils.isEmpty(list)) {
			return null;
		} else {
			return list.get(0);
		}
	}

	@Override
	public List<CloudFunctionDto> listByMainId(String mainFunctionId) {
		if (StringUtils.isEmpty(mainFunctionId)) {
			return null;
		}

		String sql = "select * from cloud_function where mainFunctionId=? and beenDeleted=?";
		List<Object> args = Lists.newArrayList();
		args.add(mainFunctionId);
		args.add(BakDeleteModel.NO_DELETED);

		return jdbcTemplate.query(sql, args.toArray(), BeanPropertyRowMapper.newInstance(CloudFunctionDto.class));
	}

	@Override
	public List<CloudSystemFunctionDto> getFunctionsByIds(String functionIds) {
		if (StringUtils.isEmpty(functionIds)) {
			return null;
		}

		StringBuffer sql = new StringBuffer();
		sql.append(" SELECT f.id,f.code as functionCode ,f.name as functionName ,CONCAT(s.website,'/',f.uri) url ");
		sql.append(" from cloud_function f left join cloud_system s on f.goalSystemId=s.id ");
		sql.append(" where f.beenDeleted= ? and s.beenDeleted= ? ");
		sql.append(" and f.uri is not null ");
		sql.append(" and f.id in ( ");
		String[] str = functionIds.split(",");
		List<String> arrList = Arrays.asList(str);
		for (int j = 0; j < arrList.size(); j++) {
			if (j != 0)
				sql.append(",");
			sql.append(" ? ");
		}
		sql.append(")");

		List<Object> args = Lists.newArrayList();
		args.add(BakDeleteModel.NO_DELETED);
		args.add(BakDeleteModel.NO_DELETED);
		args.addAll(arrList);

		return jdbcTemplate.query(sql.toString(), args.toArray(), BeanPropertyRowMapper.newInstance(CloudSystemFunctionDto.class));
	}

	@Override
	public List<CloudTreeDto> getCloudFunctionByUserId(String userId) {
		StringBuffer sql = new StringBuffer();
		sql.append(" select DISTINCT f.id,f.name,f.groupId parentId, 'Function' as type ");
		sql.append(" from cloud_user_role ur,cloud_function_role fr,cloud_function f ");
		sql.append(" where ur.userId= ? ");
		sql.append(" and ur.roleId=fr.roleId ");
		sql.append(" and fr.functionId=f.id ");
		sql.append(" and ur.beenDeleted= ? ");
		sql.append(" and fr.beenDeleted= ? ");
		sql.append(" and f.beenDeleted= ?  ");

		List<Object> args = new ArrayList<Object>();
		args.add(userId);
		args.add(BakDeleteModel.NO_DELETED);
		args.add(BakDeleteModel.NO_DELETED);
		args.add(BakDeleteModel.NO_DELETED);
		return jdbcTemplate.query(sql.toString(), args.toArray(), BeanPropertyRowMapper.newInstance(CloudTreeDto.class));
	}
	@Override
	public List<CloudFunctionAndroidDto> getFunctionsByUserId(String userId) {
		List<Object> args = Lists.newArrayList();
		StringBuffer sql = new StringBuffer();
		sql.append(" SELECT DISTINCT c.id,c.code ");
		sql.append(" from cloud_user_role a,cloud_function_role b,cloud_function c ");
		sql.append(" where a.userId=? ");
		sql.append("   and a.roleId=b.roleId ");
		sql.append("   and b.functionId=c.id ");
		sql.append("   and a.beenDeleted=? ");
		sql.append("   and b.beenDeleted=? ");
		sql.append("   and c.beenDeleted=? ");
		args.add(userId);
		args.add(BakDeleteModel.NO_DELETED);
		args.add(BakDeleteModel.NO_DELETED);
		args.add(BakDeleteModel.NO_DELETED);
		return jdbcTemplate.query(sql.toString(), args.toArray(),
				BeanPropertyRowMapper.newInstance(CloudFunctionAndroidDto.class));
	}
}
