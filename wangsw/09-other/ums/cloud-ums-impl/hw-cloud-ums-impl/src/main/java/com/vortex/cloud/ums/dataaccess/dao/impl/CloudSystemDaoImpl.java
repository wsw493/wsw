package com.vortex.cloud.ums.dataaccess.dao.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.vortex.cloud.ums.dataaccess.dao.ICloudSystemDao;
import com.vortex.cloud.ums.dto.CloudSystemDto;
import com.vortex.cloud.ums.dto.CloudTreeDto;
import com.vortex.cloud.ums.model.CloudSystem;
import com.vortex.cloud.vfs.common.lang.StringUtil;
import com.vortex.cloud.vfs.data.hibernate.repository.SimpleHibernateRepository;
import com.vortex.cloud.vfs.data.model.BakDeleteModel;
import com.vortex.cloud.vfs.data.support.SearchFilter;
import com.vortex.cloud.vfs.data.support.SearchFilter.Operator;

@Repository("cloudSystemDao")
public class CloudSystemDaoImpl extends SimpleHibernateRepository<CloudSystem, String> implements ICloudSystemDao {

	@Resource
	private JdbcTemplate jdbcTemplate;

	@Override
	public DetachedCriteria getDetachedCriteria() {
		return defaultCriteria();
	}

	private DetachedCriteria defaultCriteria() {
		DetachedCriteria criteria = DetachedCriteria.forClass(getPersistentClass(), "cloudSystem");
		criteria.add(Restrictions.eq("beenDeleted", BakDeleteModel.NO_DELETED));
		return criteria;
	}

	@Override
	public CloudSystem getByCode(String code) {
		List<SearchFilter> filterList = new ArrayList<SearchFilter>();
		filterList.add(new SearchFilter("systemCode", Operator.EQ, code));
		List<CloudSystem> list = findListByFilter(filterList, null);
		if (CollectionUtils.isEmpty(list)) {
			return null;
		} else {
			return list.get(0);
		}
	}

	@Override
	public List<String> getSystemList(String userId) {
		StringBuffer sql = new StringBuffer();
		sql.append(" SELECT DISTINCT CONCAT(t.systemCode,'||',t.systemName) sys ");
		sql.append(" from cloud_system t ");
		sql.append(" where EXISTS (select 1 from cloud_user_role a,cloud_role b where a.userid=? and a.roleid=b.id and b.systemid=t.id) ");
		sql.append(" and t.beenDeleted=? ");

		List<Object> args = new ArrayList<Object>();
		args.add(userId);
		args.add(BakDeleteModel.NO_DELETED);

		return jdbcTemplate.queryForList(sql.toString(), args.toArray(), String.class);
	}

	@Override
	public List<CloudTreeDto> getCloudSystemsByUserId(String userId) {
		StringBuffer sql = new StringBuffer();
		sql.append(" select DISTINCT s.id,s.systemName name,'-1' parentId, 'System' as type ");
		sql.append(" from cloud_user_role ur,cloud_role r,cloud_system s ");
		sql.append(" where ur.userId= ? ");
		sql.append(" and ur.roleId=r.id ");
		sql.append(" and r.systemId=s.id ");
		sql.append(" and ur.beenDeleted= ?  ");
		sql.append(" and r.beenDeleted= ? ");
		sql.append(" and s.beenDeleted= ? ");
		List<Object> args = new ArrayList<Object>();
		args.add(userId);
		args.add(BakDeleteModel.NO_DELETED);
		args.add(BakDeleteModel.NO_DELETED);
		args.add(BakDeleteModel.NO_DELETED);
		return jdbcTemplate.query(sql.toString(), args.toArray(), BeanPropertyRowMapper.newInstance(CloudTreeDto.class));
	}
	
	@Override
	public List<CloudSystemDto> getCloudSystemByRoleCode(String roleCode) {
		if(StringUtil.isNullOrEmpty(roleCode)){
			return null;
		}
		StringBuffer sql = new StringBuffer();
		sql.append(" select  t.systemId as id,s.systemCode from cloud_role t ");
		sql.append(" left join cloud_system s on s.id = t.systemId" );
		sql.append(" where t.code = ? and t.beenDeleted=? and s.beenDeleted=? ");
		List<Object> args = new ArrayList<Object>();
		args.add(roleCode);
		args.add(BakDeleteModel.NO_DELETED);
		args.add(BakDeleteModel.NO_DELETED);
		return jdbcTemplate.query(sql.toString(), args.toArray(), BeanPropertyRowMapper.newInstance(CloudSystemDto.class));
	}
}
