package com.vortex.cloud.ums.dataaccess.service;

import com.vortex.cloud.ums.model.CloudLoginLog;
import com.vortex.cloud.vfs.data.hibernate.service.PagingAndSortingService;


public interface ICloudLoginLogService extends PagingAndSortingService<CloudLoginLog, String>{
	/**
	 * 保存登录日志
	 * @param dto
	 * @throws Exception
	 */
	public void saveCloudLoginLog(String userName, String name, String ip) throws Exception;
	
	
}
