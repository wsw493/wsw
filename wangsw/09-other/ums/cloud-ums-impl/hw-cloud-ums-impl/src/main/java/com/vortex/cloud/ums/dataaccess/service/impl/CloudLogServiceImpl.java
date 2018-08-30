package com.vortex.cloud.ums.dataaccess.service.impl;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vortex.cloud.ums.dataaccess.dao.ICloudLogDao;
import com.vortex.cloud.ums.dataaccess.service.ICloudLogService;
import com.vortex.cloud.ums.model.CloudLog;
import com.vortex.cloud.vfs.data.hibernate.repository.HibernateRepository;
import com.vortex.cloud.vfs.data.hibernate.service.SimplePagingAndSortingService;



@Transactional
@Service("cloudLogService")
public class CloudLogServiceImpl extends SimplePagingAndSortingService<CloudLog, String> implements ICloudLogService {

	private static final Logger logger = LoggerFactory.getLogger(CloudLogServiceImpl.class);

	@Resource
	private ICloudLogDao cloudLogDao;

	@Override
	public HibernateRepository<CloudLog, String> getDaoImpl() {
		return cloudLogDao;
	}

	@Override
	public void saveCloudLog(CloudLog cloudLog) {
		this.save(cloudLog);
		
	}

}
