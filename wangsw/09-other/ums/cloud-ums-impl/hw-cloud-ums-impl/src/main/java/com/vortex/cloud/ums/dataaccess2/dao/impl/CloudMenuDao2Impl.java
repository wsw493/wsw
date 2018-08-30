/*   
\ * Copyright (C), 2005-2014, 苏州伏泰信息科技有限公司
 */
package com.vortex.cloud.ums.dataaccess2.dao.impl;

import java.io.Serializable;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.google.common.collect.Lists;
import com.vortex.cloud.ums.dataaccess2.dao.ICloudMenuDao2;
import com.vortex.cloud.ums.model.CloudMenu;
import com.vortex.cloud.vfs.data.model.BakDeleteModel;


/**
 * @author LiShijun
 * @date 2016年5月23日 上午10:29:40
 * @Description History <author> <time> <desc>
 */
@Repository("cloudMenuDao2")
public class CloudMenuDao2Impl implements ICloudMenuDao2 {

	@Resource(name = "jdbcTemplate2")
	private JdbcTemplate jdbcTemplate;
	
	@Resource(name = "sessionFactory2")
	private SessionFactory sessionFactory;

	@Override
	public List<CloudMenu> getMenuList(String systemId) {
		List<Object> args = Lists.newArrayList();
		StringBuffer sql = new StringBuffer();
		sql.append("        SELECT                     ");
		sql.append("        	*                      ");
		sql.append("        FROM                       ");
		sql.append("        	cloud_menu cm          ");
		sql.append("        WHERE                      ");
		sql.append("        	cm.systemId = ?        ");
		sql.append("        AND cm.beenDeleted = ?     ");
		sql.append("        ORDER BY cm.orderIndex     ");
		args.add(systemId);
		args.add(BakDeleteModel.NO_DELETED);
		return jdbcTemplate.query(sql.toString(), args.toArray(), BeanPropertyRowMapper.newInstance(CloudMenu.class));

	}

	@Override
	public Serializable save(CloudMenu bean) throws Exception {
		Session session = sessionFactory.getCurrentSession();
		Serializable rst =  session.save(bean);
		session.flush();
		return rst;
	}

	@Override
	public void update(CloudMenu bean) throws Exception {
		Session session = sessionFactory.getCurrentSession();
		session.update(bean);
		session.flush();
	}

	@Override
	public CloudMenu getMenuBySysidAndMcode(String sysid, String mcode) throws Exception {
		String sql = "select * from cloud_menu a where a.systemId=? and a.code=? and a.beenDeleted = ?";
		List<Object> args = Lists.newArrayList();
		args.add(sysid);
		args.add(mcode);
		args.add(BakDeleteModel.NO_DELETED);

		List<CloudMenu> list = jdbcTemplate.query(sql, args.toArray(), BeanPropertyRowMapper.newInstance(CloudMenu.class));

		if (CollectionUtils.isEmpty(list)) {
			return null;
		} else {
			return list.get(0);
		}
	}

	@Override
	public CloudMenu getById(String menuId) throws Exception {
		String sql = "select * from cloud_menu where id=? and beenDeleted=?";
		List<Object> args = Lists.newArrayList();
		args.add(menuId);
		args.add(BakDeleteModel.NO_DELETED);

		List<CloudMenu> list = jdbcTemplate.query(sql, args.toArray(), BeanPropertyRowMapper.newInstance(CloudMenu.class));

		if (CollectionUtils.isEmpty(list)) {
			return null;
		} else {
			return list.get(0);
		}
	}
}
