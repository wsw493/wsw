package com.vortex.cloud.ums.dataaccess.service;

import com.vortex.cloud.ums.model.CloudLog;
import com.vortex.cloud.vfs.data.hibernate.service.PagingAndSortingService;


/**
 * 日志服务
 * 
 * @author lusm
 *
 */
public interface ICloudLogService extends PagingAndSortingService<CloudLog, String> {
	/**
	 * 保存日志表
	 * 
	 * @param cloudLog
	 */
	public void saveCloudLog(CloudLog cloudLog);
}
