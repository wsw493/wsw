package com.vortex.cloud.ums.dataaccess2.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vortex.cloud.ums.dataaccess2.dao.ICloudSystemDao2;
import com.vortex.cloud.ums.dataaccess2.service.ICloudSystemService;
import com.vortex.cloud.ums.model.CloudSystem;
import com.vortex.cloud.vfs.common.exception.ServiceException;


@Transactional
@Service("cloudSystemService2")
public class CloudSystemServiceImpl implements ICloudSystemService {
	private static final Logger logger = LoggerFactory.getLogger(CloudSystemServiceImpl.class);

	@Resource(name = "cloudSystemDao2")
	private ICloudSystemDao2 cloudSystemDao;

	@Override
	public List<CloudSystem> getCloudSystems(String tenantId) {

		if (StringUtils.isBlank(tenantId)) {
			logger.error("tenantId不能为空！");
			throw new ServiceException("tenantId不能为空！");
		}

		return cloudSystemDao.getCloudSystems(tenantId);
	}

}
