package com.vortex.cloud.ums.dataaccess2.dao;

import java.util.List;

import com.vortex.cloud.ums.model.Tenant;

/**
 * 租户dao
 * 
 * @author lsm
 *
 */
public interface ITenantDao2 {
	/**
	 * 查找所有的租户
	 * 
	 * @return
	 */
	List<Tenant> findAll();

}
