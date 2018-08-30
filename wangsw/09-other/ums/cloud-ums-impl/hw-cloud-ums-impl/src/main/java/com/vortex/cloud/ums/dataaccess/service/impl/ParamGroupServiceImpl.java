package com.vortex.cloud.ums.dataaccess.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vortex.cloud.ums.dataaccess.dao.IParamGroupDao;
import com.vortex.cloud.ums.dataaccess.service.IParamGroupService;
import com.vortex.cloud.ums.model.PramGroup;
import com.vortex.cloud.vfs.data.hibernate.repository.HibernateRepository;
import com.vortex.cloud.vfs.data.hibernate.service.SimplePagingAndSortingService;



@Service("paramGroupService")
@Transactional
public class ParamGroupServiceImpl extends SimplePagingAndSortingService<PramGroup, String>
	implements IParamGroupService {
	
	@Resource
	private IParamGroupDao paramGroupDao;

	@Override
	public HibernateRepository<PramGroup, String> getDaoImpl() {
		return paramGroupDao;
	}
	
}
