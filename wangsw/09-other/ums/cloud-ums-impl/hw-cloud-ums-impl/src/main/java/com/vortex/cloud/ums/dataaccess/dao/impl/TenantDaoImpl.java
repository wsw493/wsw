package com.vortex.cloud.ums.dataaccess.dao.impl;

import java.util.ArrayList;
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

import com.google.common.collect.Lists;
import com.vortex.cloud.ums.dataaccess.dao.ITenantDao;
import com.vortex.cloud.ums.dto.TenantUrlDto;
import com.vortex.cloud.ums.model.Tenant;
import com.vortex.cloud.vfs.data.hibernate.repository.SimpleHibernateRepository;
import com.vortex.cloud.vfs.data.model.BakDeleteModel;
import com.vortex.cloud.vfs.data.support.SearchFilter;
import com.vortex.cloud.vfs.data.support.SearchFilter.Operator;





@Repository("tenantDao")
public class TenantDaoImpl extends SimpleHibernateRepository<Tenant, String> implements ITenantDao {

	@Resource
	private JdbcTemplate jdbcTemplate;
	
	@Override
	public DetachedCriteria getDetachedCriteria() {
		return defaultCriteria();
	}

	private DetachedCriteria defaultCriteria() {
		DetachedCriteria criteria = DetachedCriteria.forClass(getPersistentClass(), "tenant");
		criteria.add(Restrictions.eq("beenDeleted", BakDeleteModel.NO_DELETED));
		return criteria;
	}

	@Override
	public Map<String, String> findTenantNameById(List<String> idList) {
		if(CollectionUtils.isEmpty(idList)) {
			return null;
		}
		
		Map<String, String> resultMap = new HashMap<String, String> ();
		
		List<SearchFilter> searchFilter = new ArrayList<SearchFilter>();
		searchFilter.add(new SearchFilter("id", Operator.IN, idList.toArray()));
		StringBuffer sql = new StringBuffer(" SELECT id, tenantName FROM cloud_management_tenant WHERE id IN ( ");
		
		StringBuffer idSb = new StringBuffer("");
		for(String id: idList) {
			idSb.append("?,");
		}
		sql.append(idSb.substring(0, idSb.lastIndexOf(",")));
		
		sql.append(" ) ");
		
		List<Map<String, Object>> list = jdbcTemplate.queryForList(sql.toString(), idList.toArray());
		for(Map<String, Object> map : list) {
			resultMap.put((String)map.get("id"), (String)map.get("tenantName"));
		}
		
		return resultMap;
	}

	@Override
	public TenantUrlDto getTenantUrl(String tenantId) {
		StringBuffer sql = new StringBuffer();
		List<Object> args = Lists.newArrayList();
		sql.append(" SELECT cmt.domain, cmt.menuUrl, cmt.navigationUrl  FROM cloud_management_tenant cmt WHERE cmt.id = ? AND cmt.beenDeleted = ? ");
		args.add(tenantId);
		args.add(false);
		List<TenantUrlDto> tenantUrlDtoList = jdbcTemplate.query(sql.toString(), args.toArray(), BeanPropertyRowMapper.newInstance(TenantUrlDto.class));
		if (CollectionUtils.isNotEmpty(tenantUrlDtoList)) {
			return tenantUrlDtoList.get(0);
		}
		return null;
	}
	
	
}
