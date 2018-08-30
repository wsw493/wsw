package com.vortex.cloud.ums.dataaccess.dao;

import com.vortex.cloud.ums.model.CloudLog;
import com.vortex.cloud.vfs.data.hibernate.repository.HibernateRepository;


/**
 * 部门dao
 * 
 * @author LiShijun
 *
 */
public interface ICloudLogDao
	extends HibernateRepository<CloudLog, String> {

	
}
