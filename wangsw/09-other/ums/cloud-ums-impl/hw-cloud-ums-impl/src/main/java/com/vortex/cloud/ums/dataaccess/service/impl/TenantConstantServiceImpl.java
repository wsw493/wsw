/*   
 * Copyright (C), 2005-2014, 苏州伏泰信息科技有限公司
 */
package com.vortex.cloud.ums.dataaccess.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vortex.cloud.ums.dataaccess.dao.ITenantConstantDao;
import com.vortex.cloud.ums.dataaccess.service.ITenantConstantService;
import com.vortex.cloud.ums.model.CloudConstant;
import com.vortex.cloud.vfs.common.exception.ServiceException;
import com.vortex.cloud.vfs.data.hibernate.repository.HibernateRepository;
import com.vortex.cloud.vfs.data.hibernate.service.SimplePagingAndSortingService;
import com.vortex.cloud.vfs.data.support.SearchFilter;

/**
 * @author LiShijun
 * @date 2016年3月29日 上午10:13:13
 * @Description 租户常量维护 History <author> <time> <desc>
 */
@Service("tenantConstantService")
@Transactional
public class TenantConstantServiceImpl extends SimplePagingAndSortingService<CloudConstant, String> implements ITenantConstantService {
	private Logger logger = LoggerFactory.getLogger(TenantConstantServiceImpl.class);
	@Resource
	private ITenantConstantDao tenantConstantDao;

	@Override
	public HibernateRepository<CloudConstant, String> getDaoImpl() {
		return tenantConstantDao;
	}

	@Override
	public boolean isExistConstantCode(List<SearchFilter> filterList) {
		List<CloudConstant> list = this.findListByFilter(filterList, null);

		if (CollectionUtils.isNotEmpty(list)) {
			return true;
		}

		return false;
	}

	@Override
	public CloudConstant getConstantByCode(String constantCode, String tenantCode) {
		if (StringUtils.isEmpty(constantCode)) {
			logger.error("常量code不能为空");
			throw new ServiceException("常量code不能为空");
		}
		if (StringUtils.isEmpty(tenantCode)) {
			logger.error("租户code不能为空");
			throw new ServiceException("租户code不能为空");
		}
		CloudConstant constant = tenantConstantDao.getConstantByCode(constantCode, tenantCode);
		return constant;
	}
}
