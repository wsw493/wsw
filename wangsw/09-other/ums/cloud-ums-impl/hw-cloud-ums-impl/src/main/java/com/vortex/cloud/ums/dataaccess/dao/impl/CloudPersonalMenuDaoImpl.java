package com.vortex.cloud.ums.dataaccess.dao.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.vortex.cloud.ums.dataaccess.dao.ICloudPersonalMenuDao;
import com.vortex.cloud.ums.dto.CloudPersonalMenuDisplayDto;
import com.vortex.cloud.ums.model.CloudPersonalMenu;
import com.vortex.cloud.vfs.data.hibernate.repository.SimpleHibernateRepository;
import com.vortex.cloud.vfs.data.model.BakDeleteModel;

@Repository("cloudPersonalMenuDao")
public class CloudPersonalMenuDaoImpl extends SimpleHibernateRepository<CloudPersonalMenu, String> implements ICloudPersonalMenuDao {

	@Resource
	private JdbcTemplate jdbcTemplate;

	@Override
	public DetachedCriteria getDetachedCriteria() {
		DetachedCriteria criteria = DetachedCriteria.forClass(getPersistentClass(), "cloudPersonalMenu");
		criteria.add(Restrictions.eq("cloudPersonalMenu.beenDeleted", BakDeleteModel.NO_DELETED));
		return criteria;
	}

	@Override
	public List<CloudPersonalMenuDisplayDto> getPersonalMenu(String userId) {
		StringBuffer sql = new StringBuffer();
		List<Object> argList = new ArrayList<Object>();
		sql.append(" SELECT cpm.id, cm. NAME, cm.photoIds, cf.uri, cpm.orderIndex ");
		sql.append(" FROM cloud_personal_menu cpm, cloud_menu cm, cloud_function cf ");
		sql.append(" WHERE cpm.menuId = cm.id AND cm.functionId = cf.id AND cpm.userId = ? ");
		sql.append(" AND cpm.beenDeleted = ? AND cm.beenDeleted = ? AND cf.beenDeleted = ? ORDER BY cpm.orderIndex ASC ");

		argList.add(userId);
		argList.add(false);
		argList.add(false);
		argList.add(false);

		List<CloudPersonalMenuDisplayDto> dtoList = jdbcTemplate.query(sql.toString(), argList.toArray(), BeanPropertyRowMapper.newInstance(CloudPersonalMenuDisplayDto.class));
		return dtoList;
	}

}
