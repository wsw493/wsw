package com.vortex.cloud.ums.dataaccess.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vortex.cloud.ums.dataaccess.dao.IParamSettingDao;
import com.vortex.cloud.ums.dataaccess.service.IParamSettingService;
import com.vortex.cloud.ums.dto.rest.PramSettingRestDto;
import com.vortex.cloud.ums.model.PramSetting;
import com.vortex.cloud.vfs.data.hibernate.repository.HibernateRepository;
import com.vortex.cloud.vfs.data.hibernate.service.SimplePagingAndSortingService;



@Service("paramSettingService")
@Transactional
public class ParamSettingServiceImpl extends SimplePagingAndSortingService<PramSetting, String> implements IParamSettingService {
	
	private static final Logger logger = LoggerFactory.getLogger(ParamSettingServiceImpl.class);
	
	@Resource
	private IParamSettingDao paramSettingDao;

	@Override
	public HibernateRepository<PramSetting, String> getDaoImpl() {
		return paramSettingDao;
	}

	@Override
	public long delete(String[] idArr) {
		logger.debug("delete(), start...");
		if(ArrayUtils.isEmpty(idArr)) {
			return 0;
		}
		
		long deleted = 0;
		for (String id : idArr) {
			this.delete(id);
			deleted++;
		}
		
		return deleted;
	}

	@Override
	public List<PramSettingRestDto> findListByParamTypeCode(String paramTypeCode, String tenantId) {
		return paramSettingDao.findListByParamTypeCode(paramTypeCode, tenantId);
	}
}