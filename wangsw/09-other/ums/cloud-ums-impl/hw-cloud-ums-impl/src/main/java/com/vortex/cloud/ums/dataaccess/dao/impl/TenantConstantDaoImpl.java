/*   
 * Copyright (C), 2005-2014, 苏州伏泰信息科技有限公司
 */
package com.vortex.cloud.ums.dataaccess.dao.impl;

import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.google.common.collect.Lists;
import com.vortex.cloud.ums.dataaccess.dao.ITenantConstantDao;
import com.vortex.cloud.ums.model.CloudConstant;
import com.vortex.cloud.vfs.data.hibernate.repository.SimpleHibernateRepository;
import com.vortex.cloud.vfs.data.model.BakDeleteModel;

/**
 * @author LiShijun
 * @date 2016年3月29日 上午10:19:52
 * @Description History <author> <time> <desc>
 */
@Repository("tenantConstantDao")
public class TenantConstantDaoImpl extends SimpleHibernateRepository<CloudConstant, String> implements ITenantConstantDao {
	@Resource
	private JdbcTemplate jdbcTemplate;

	@Override
	public DetachedCriteria getDetachedCriteria() {
		DetachedCriteria criteria = DetachedCriteria.forClass(getPersistentClass());
		criteria.add(Restrictions.eq("beenDeleted", BakDeleteModel.NO_DELETED));
		return criteria;
	}

	@Override
	public CloudConstant getConstantByCode(String constantCode, String tenantCode) {
		StringBuffer sql = new StringBuffer();
		sql.append("    SELECT                                              ");
		sql.append("    	cc.*                                            ");
		sql.append("    FROM                                                ");
		sql.append("    	cloud_constant cc,                              ");
		sql.append("    	cloud_management_tenant cmt                     ");
		sql.append("    WHERE                                               ");
		sql.append("    	cc.tenantId = cmt.id                            ");
		sql.append("    AND cmt.tenantCode = ?                              ");
		sql.append("    AND cc.constantCode = ?                             ");
		List<String> args = Lists.newArrayList();
		args.add(tenantCode);
		args.add(constantCode);
		List<CloudConstant> list = jdbcTemplate.query(sql.toString(), args.toArray(new String[args.size()]), BeanPropertyRowMapper.newInstance(CloudConstant.class));
		CloudConstant cloudConstant = null;
		if (CollectionUtils.isNotEmpty(list)) {
			cloudConstant = list.get(0);
		}
		return cloudConstant;
	}
}
