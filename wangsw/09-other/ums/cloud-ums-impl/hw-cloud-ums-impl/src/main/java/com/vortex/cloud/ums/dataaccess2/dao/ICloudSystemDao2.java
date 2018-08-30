package com.vortex.cloud.ums.dataaccess2.dao;

import java.util.List;

import com.vortex.cloud.ums.dto.CloudSystemDto;
import com.vortex.cloud.ums.model.CloudSystem;

/**
 * 
 * 云平台上面的云系统 dao
 * 
 * @author lsm
 * 
 *
 */
public interface ICloudSystemDao2 {
	/**
	 * 根据租户ID获取系统列表
	 * 
	 * 
	 * @param tenantId
	 * @return
	 */
	List<CloudSystem> getCloudSystems(String tenantId);

	/**
	 * 根据系统code得到系统信息
	 * 
	 * @param sysCode
	 * @return
	 */
	CloudSystem getByCode(String sysCode);

	/**
	 * 根据系统id得到系统信息
	 * 
	 * @param systemId
	 * @return
	 */
	CloudSystemDto getById(String systemId);
}
