package com.vortex.cloud.ums.dataaccess2.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vortex.cloud.ums.dataaccess2.dao.ITenantDao2;
import com.vortex.cloud.ums.dataaccess2.service.ITenantService;
import com.vortex.cloud.ums.model.Tenant;

@Transactional
@Service("tenantService2")
public class TenantServiceImpl implements ITenantService {

	private static final Logger logger = LoggerFactory.getLogger(TenantServiceImpl.class);

	@Resource(name = "tenantDao2")
	private ITenantDao2 tenantDao;

	@Override
	public List<Tenant> findAll() {
		return tenantDao.findAll();
	}

}
