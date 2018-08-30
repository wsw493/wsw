package com.vortex.cloud.ums.dataaccess.dao;

import com.vortex.cloud.ums.model.CloudLoginLog;
import com.vortex.cloud.vfs.data.hibernate.repository.HibernateRepository;

/**
 * 登录日志dao
 * @author ll
 *
 */
public interface ICloudLoginLogDao extends HibernateRepository<CloudLoginLog, String>{

}
