package com.vortex.cloud.ums.dataaccess2.service;

import java.util.List;

import com.vortex.cloud.ums.model.CloudSystem;

public interface ICloudSystemService {
	/**
	 * 根据租户ID获取系统列表
	 * 
	 * @param tenantId
	 * @return
	 */
	List<CloudSystem> getCloudSystems(String tenantId);

}
