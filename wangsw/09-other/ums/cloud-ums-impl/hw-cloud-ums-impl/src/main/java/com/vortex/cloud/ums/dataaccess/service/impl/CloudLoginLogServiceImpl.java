package com.vortex.cloud.ums.dataaccess.service.impl;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vortex.cloud.ums.dataaccess.dao.ICloudLoginLogDao;
import com.vortex.cloud.ums.dataaccess.service.ICloudLoginLogService;
import com.vortex.cloud.ums.model.CloudLoginLog;
import com.vortex.cloud.vfs.data.hibernate.repository.HibernateRepository;
import com.vortex.cloud.vfs.data.hibernate.service.SimplePagingAndSortingService;



@Transactional
@Service("cloudLoginLogService")
public class CloudLoginLogServiceImpl extends SimplePagingAndSortingService<CloudLoginLog, String> implements ICloudLoginLogService {

	private static final Logger logger = LoggerFactory.getLogger(CloudLoginLogServiceImpl.class);
	
	@Resource
	private ICloudLoginLogDao cloudLoginLogDao;
	

	@Override
	public HibernateRepository<CloudLoginLog, String> getDaoImpl() {
		return cloudLoginLogDao;
	}


	@Override
	public void saveCloudLoginLog(String userName, String name, String ip) throws Exception {
		CloudLoginLog cloudLoginLog = new CloudLoginLog();
		cloudLoginLog.setUserName(userName);
		cloudLoginLog.setName(name);
		cloudLoginLog.setIp(ip);
		cloudLoginLogDao.save(cloudLoginLog);
		
	}

	
	
}
