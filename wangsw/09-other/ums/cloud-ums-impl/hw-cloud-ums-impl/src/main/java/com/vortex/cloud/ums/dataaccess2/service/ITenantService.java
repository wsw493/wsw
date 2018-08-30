package com.vortex.cloud.ums.dataaccess2.service;

import java.util.List;

import com.vortex.cloud.ums.model.Tenant;

public interface ITenantService {

	/**
	 * 获取所有的租户信息
	 * 
	 * @return
	 */
	public List<Tenant> findAll();
}
