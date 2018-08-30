package com.vortex.cloud.ums.dataaccess.dao.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.vortex.cloud.ums.dataaccess.dao.IParamSettingDao;
import com.vortex.cloud.ums.dto.rest.PramSettingRestDto;
import com.vortex.cloud.ums.model.PramSetting;
import com.vortex.cloud.vfs.data.hibernate.repository.SimpleHibernateRepository;
import com.vortex.cloud.vfs.data.model.BakDeleteModel;



@Repository("paramSettingDao")
public class ParamSettingDaoImpl extends SimpleHibernateRepository<PramSetting, String> implements IParamSettingDao {
	
	@Resource
	private JdbcTemplate jdbcTemplate;
	
	@Override
	public DetachedCriteria getDetachedCriteria() {
		DetachedCriteria criteria = DetachedCriteria.forClass(getPersistentClass(), "parameterSetting");
		criteria.add(Restrictions.eq("parameterSetting.beenDeleted", BakDeleteModel.NO_DELETED));
		
		return criteria;
	}

	@Override
	public List<PramSettingRestDto> findListByParamTypeCode(String paramTypeCode, String tenantId) {
		StringBuffer sql = new StringBuffer();
		List<Object> argList = new ArrayList<>();
		sql.append(" SELECT setting.id, setting.parmCode, setting.parmName ");
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
		
		return jdbcTemplate.query(sql.toString(), argList.toArray(), BeanPropertyRowMapper.newInstance(PramSettingRestDto.class));
	}
}
