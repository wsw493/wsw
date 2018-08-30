package com.vortex.cloud.ums.dataaccess.dao.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.google.common.collect.Maps;
import com.vortex.cloud.ums.dataaccess.dao.ITenantPramSettingDao;
import com.vortex.cloud.ums.dto.TenantPramSettingDto;
import com.vortex.cloud.ums.model.TenantPramSetting;
import com.vortex.cloud.vfs.data.hibernate.repository.SimpleHibernateRepository;
import com.vortex.cloud.vfs.data.model.BakDeleteModel;

@SuppressWarnings("all")
@Repository("tenantPramSettingDao")
public class TenantPramSettingDaoImpl extends SimpleHibernateRepository<TenantPramSetting, String> implements ITenantPramSettingDao {

	@Resource
	private JdbcTemplate jdbcTemplate;

	@Override
	public DetachedCriteria getDetachedCriteria() {
		return defaultCriteria();
	}

	private DetachedCriteria defaultCriteria() {
		DetachedCriteria criteria = DetachedCriteria.forClass(getPersistentClass(), "tenantPramSetting");
		criteria.add(Restrictions.eq("beenDeleted", BakDeleteModel.NO_DELETED));
		return criteria;
	}

	@Override
	public long getCntByTenantId(String tenantId) {
		String sql = " select count(1) from cloud_tenantparameter_setting where tenantId = ? ";

		return jdbcTemplate.queryForObject(sql, Long.class, new Object[] { tenantId });
	}

	@Override
	public List<TenantPramSetting> findListByParamTypeCode(String tenantId, String paramTypeCode) {
		Map<String, Object> map = this.sqlForListByParamTypeCode(tenantId, paramTypeCode);
		StringBuffer sql = (StringBuffer) map.get("sql");
		List<Object> argList = (List<Object>) map.get("argList");

		return jdbcTemplate.query(sql.toString(), argList.toArray(), BeanPropertyRowMapper.newInstance(TenantPramSetting.class));
	}

	private Map<String, Object> sqlForListByParamTypeCode(String tenantId, String paramTypeCode) {
		StringBuffer sql = new StringBuffer();
		List<Object> argList = new ArrayList<Object>();

		sql.append(" SELECT setting.*                                                      ");
		sql.append(" FROM cloud_tenantparameter_setting setting, cloud_parameter_type type ");
		sql.append(" WHERE                                                                 ");
		sql.append(" 	type.beenDeleted = ?                                               ");
		sql.append(" 	AND type.typeCode = ?                                              ");
		sql.append(" 	AND setting.typeId = type.id                                        ");
		sql.append(" 	AND setting.tenantId = ?            								");
		sql.append(" 	AND setting.beenDeleted = ?                                        	");
		sql.append(" ORDER BY setting.orderIndex ASC                                         	");

		argList.add(BakDeleteModel.NO_DELETED);
		argList.add(paramTypeCode);
		argList.add(tenantId);
		argList.add(BakDeleteModel.NO_DELETED);

		// 返回结果
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("sql", sql);
		map.put("argList", argList);
		return map;
	}

	@Override
	public Map<String, List<TenantPramSettingDto>> findByParamTypeCodeList(String tenantId, List<String> paramTypeCodeList) {
		List<TenantPramSettingDto> list = findByParamTypeCodes(tenantId, paramTypeCodeList);

		// 按照参数类型进行分组
		if (CollectionUtils.isEmpty(list)) {
			return null;
		}

		Map<String, List<TenantPramSettingDto>> resultMap = new HashMap<String, List<TenantPramSettingDto>>();
		String typeCode = null;
		List<TenantPramSettingDto> paramList = null;
		for (TenantPramSettingDto dto : list) {
			typeCode = dto.getTypeCode();

			paramList = resultMap.get(typeCode);
			if (paramList == null) {
				paramList = new ArrayList<TenantPramSettingDto>();
				resultMap.put(typeCode, paramList);
			}

			paramList.add(dto);
		}

		return resultMap;
	}

	@Override
	public List<TenantPramSettingDto> findByParamTypeCodes(String tenantId, List<String> paramTypeCodeList) {
		Map<String, Object> map = this.sqlForByParamTypeCodeList(tenantId, paramTypeCodeList);
		StringBuffer sql = (StringBuffer) map.get("sql");
		List<Object> argList = (List<Object>) map.get("argList");

		List<TenantPramSettingDto> list = jdbcTemplate.query(sql.toString(), argList.toArray(), BeanPropertyRowMapper.newInstance(TenantPramSettingDto.class));
		return list;
	}

	private Map<String, Object> sqlForByParamTypeCodeList(String tenantId, List<String> paramTypeCodeList) {
		StringBuffer sql = new StringBuffer();
		List<Object> argList = new ArrayList<Object>();

		sql.append(" SELECT type.typeCode, setting.*                                       ");
		sql.append(" FROM cloud_tenantparameter_setting setting, cloud_parameter_type type ");
		sql.append(" WHERE                                                                 ");
		sql.append(" 	type.beenDeleted = ?                                               ");
		argList.add(BakDeleteModel.NO_DELETED);

		if (CollectionUtils.isNotEmpty(paramTypeCodeList)) {
			sql.append(" 	AND type.typeCode IN (	");

			int size = paramTypeCodeList.size();
			for (int i = 0; i < size; i++) {
				sql.append("?");
				if (i < size - 1) {
					sql.append(", ");
				}

				argList.add(paramTypeCodeList.get(i));
			}

			sql.append(" 	)                  		");
		}

		sql.append(" 	AND setting.typeId = type.id                                        ");
		sql.append(" 	AND setting.tenantId = ?            								");
		sql.append(" 	AND setting.beenDeleted = ?                                        	");
		sql.append(" ORDER BY setting.orderIndex ASC ,type.typeCode ASC, setting.parmName ASC                      	");

		argList.add(tenantId);
		argList.add(BakDeleteModel.NO_DELETED);

		// 返回结果
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("sql", sql);
		map.put("argList", argList);
		return map;
	}

	@Override
	public TenantPramSetting findOneByParamCode(String tenantId, String paramTypeCode, String paramCode) {
		Map<String, Object> map = this.sqlForfindOneByParamCode(tenantId, paramTypeCode, paramCode);
		StringBuffer sql = (StringBuffer) map.get("sql");
		List<Object> argList = (List<Object>) map.get("argList");

		List<TenantPramSetting> list = jdbcTemplate.query(sql.toString(), argList.toArray(), BeanPropertyRowMapper.newInstance(TenantPramSetting.class));
		if (CollectionUtils.isEmpty(list)) {
			return null;
		}

		return list.get(0);
	}

	private Map<String, Object> sqlForfindOneByParamCode(String tenantId, String paramTypeCode, String paramCode) {
		StringBuffer sql = new StringBuffer();
		List<Object> argList = new ArrayList<Object>();

		sql.append(" SELECT setting.*                                                      	");
		sql.append(" FROM cloud_tenantparameter_setting setting, cloud_parameter_type type	");
		sql.append(" WHERE                                                                 	");
		sql.append(" 	type.beenDeleted = ?                                               	");
		sql.append(" 	AND setting.beenDeleted = ?                                        	");
		sql.append(" 	AND type.typeCode = ?                                              	");
		sql.append(" 	AND setting.typeId = type.id                                        ");
		sql.append(" 	AND setting.tenantId = ?            								");
		sql.append(" 	AND setting.parmCode = ?            								");

		argList.add(BakDeleteModel.NO_DELETED);
		argList.add(BakDeleteModel.NO_DELETED);
		argList.add(paramTypeCode);
		argList.add(tenantId);
		argList.add(paramCode);

		// 返回结果
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("sql", sql);
		map.put("argList", argList);
		return map;
	}

	private Map<String, Object> sqlForfindListByParamCodes(String tenantId, String paramTypeCode, String[] paramCodes) {
		StringBuffer sql = new StringBuffer();
		List<Object> argList = new ArrayList<Object>();

		sql.append(" SELECT setting.*                                                      	");
		sql.append(" FROM cloud_tenantparameter_setting setting, cloud_parameter_type type	");
		sql.append(" WHERE                                                                 	");
		sql.append(" 	type.beenDeleted = ?                                               	");
		sql.append(" 	AND setting.beenDeleted = ?                                        	");
		sql.append(" 	AND type.typeCode = ?                                              	");
		sql.append(" 	AND setting.typeId = type.id                                        ");
		sql.append(" 	AND setting.tenantId = ?            								");

		argList.add(BakDeleteModel.NO_DELETED);
		argList.add(BakDeleteModel.NO_DELETED);
		argList.add(paramTypeCode);
		argList.add(tenantId);
		if (CollectionUtils.isNotEmpty(Arrays.asList(paramCodes))) {
			sql.append(" 	AND (setting.parmCode = ?            								");
			argList.add(paramCodes[0]);
			for (int i = 1; i < paramCodes.length - 1; i++) {
				sql.append(" 	OR setting.parmCode = ?            								");
				argList.add(paramCodes[i]);
			}
			sql.append(" 	OR setting.parmCode = ?    )        								");
			argList.add(paramCodes[paramCodes.length - 1]);

		}
		sql.append(" ORDER BY setting.orderIndex ASC                  	");
		// 返回结果
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("sql", sql);
		map.put("argList", argList);
		return map;
	}

	@Override
	public TenantPramSetting findOneByParamName(String tenantId, String paramTypeCode, String paramName) {
		Map<String, Object> map = this.sqlForfindOneByParamName(tenantId, paramTypeCode, paramName);
		StringBuffer sql = (StringBuffer) map.get("sql");
		List<Object> argList = (List<Object>) map.get("argList");

		List<TenantPramSetting> list = jdbcTemplate.query(sql.toString(), argList.toArray(), BeanPropertyRowMapper.newInstance(TenantPramSetting.class));
		if (CollectionUtils.isEmpty(list)) {
			return null;
		}

		return list.get(0);
	}

	private Map<String, Object> sqlForfindOneByParamName(String tenantId, String paramTypeCode, String paramName) {
		StringBuffer sql = new StringBuffer();
		List<Object> argList = new ArrayList<Object>();

		sql.append(" SELECT setting.*                                                      	");
		sql.append(" FROM cloud_tenantparameter_setting setting, cloud_parameter_type type	");
		sql.append(" WHERE                                                                 	");
		sql.append(" 	type.beenDeleted = ?                                               	");
		sql.append(" 	AND setting.beenDeleted = ?                                        	");
		sql.append(" 	AND type.typeCode = ?                                              	");
		sql.append(" 	AND setting.typeId = type.id                                        ");
		sql.append(" 	AND setting.tenantId = ?            								");
		sql.append(" 	AND setting.parmName = ?            								");

		argList.add(BakDeleteModel.NO_DELETED);
		argList.add(BakDeleteModel.NO_DELETED);
		argList.add(paramTypeCode);
		argList.add(tenantId);
		argList.add(paramName);

		// 返回结果
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("sql", sql);
		map.put("argList", argList);
		return map;
	}

	@Override
	public Map<String, TenantPramSetting> findListByParamCodes(String tenantId, String paramTypeCode, String[] paramCodes) {
		Map<String, Object> map = this.sqlForfindListByParamCodes(tenantId, paramTypeCode, paramCodes);
		StringBuffer sql = (StringBuffer) map.get("sql");
		List<Object> argList = (List<Object>) map.get("argList");

		List<TenantPramSetting> list = jdbcTemplate.query(sql.toString(), argList.toArray(), BeanPropertyRowMapper.newInstance(TenantPramSetting.class));
		if (CollectionUtils.isEmpty(list)) {
			return null;
		}
		Map<String, TenantPramSetting> resultMap = Maps.newHashMap();
		for (TenantPramSetting tenantPramSetting : list) {
			resultMap.put(tenantPramSetting.getParmCode(), tenantPramSetting);
		}
		return resultMap;
	}
}
