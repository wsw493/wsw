package com.vortex.cloud.ums.dataaccess.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.vortex.cloud.ums.dataaccess.dao.ICloudSystemDao;
import com.vortex.cloud.ums.dataaccess.service.ICloudSystemService;
import com.vortex.cloud.ums.dataaccess.service.ITenantBusinessService;
import com.vortex.cloud.ums.dataaccess.service.ITenantService;
import com.vortex.cloud.ums.dto.CloudSystemDto;
import com.vortex.cloud.ums.dto.CloudTreeDto;
import com.vortex.cloud.ums.dto.SystemSearchDto;
import com.vortex.cloud.ums.model.CloudSystem;
import com.vortex.cloud.ums.model.Tenant;
import com.vortex.cloud.vfs.common.exception.ServiceException;
import com.vortex.cloud.vfs.common.exception.VortexException;
import com.vortex.cloud.vfs.data.hibernate.repository.HibernateRepository;
import com.vortex.cloud.vfs.data.hibernate.service.SimplePagingAndSortingService;
import com.vortex.cloud.vfs.data.support.SearchFilter;
import com.vortex.cloud.vfs.data.support.SearchFilter.Operator;

@Transactional
@Service("cloudSystemService")
public class CloudSystemServiceImpl extends SimplePagingAndSortingService<CloudSystem, String> implements ICloudSystemService {
	private static final Logger logger = LoggerFactory.getLogger(CloudSystemServiceImpl.class);

	@Resource
	private ICloudSystemDao cloudSystemDao;

	@Resource
	private ITenantBusinessService tenantBusinessService;

	@Resource
	private ITenantService tenantService;

	@Override
	public HibernateRepository<CloudSystem, String> getDaoImpl() {
		return cloudSystemDao;
	}

	@Override
	public CloudSystem saveCloudSystem(CloudSystemDto dto) {
		// 检查必要信息
		if (StringUtils.isBlank(dto.getSystemCode())) {
			throw new ServiceException("系统编码不能为空！");
		}

		if (StringUtils.isBlank(dto.getSystemName())) {
			throw new ServiceException("系统名称不能为空！");
		}

		if (StringUtils.isBlank(dto.getWebsite())) {
			throw new ServiceException("站点不能为空！");
		}

		if (StringUtils.isBlank(dto.getMapType())) {
			throw new ServiceException("地图类型不能为空！");
		}

		CloudSystem sys = cloudSystemDao.getByCode(dto.getSystemCode());
		if (sys != null) {
			logger.error("此系统code已经存在！");
			throw new VortexException("此系统code已经存在！");
		}

		// 保存
		CloudSystem entity = new CloudSystem();
		BeanUtils.copyProperties(dto, entity);

		entity = cloudSystemDao.save(entity);

		dto.setId(entity.getId());
		String tenantId = dto.getTenantId();
		// 为业务系统设置一个默认root管理员

		logger.debug("saveCloudSystem(), tenantId=" + tenantId);

		if (StringUtils.isEmpty(tenantId)) // tennatid为空，是云系统的新增
		{
			tenantBusinessService.setBusinessSysRootUser(null, dto);
		} else {
			Tenant tenant = tenantService.findOne(tenantId);
			tenantBusinessService.setBusinessSysRootUser(tenant, dto);
		}

		return entity;
	}

	@Transactional(readOnly = true)
	@Override
	public Page<CloudSystemDto> getPageOfBusinessSys(Pageable pageable, SystemSearchDto searchDto) {
		List<SearchFilter> sysFilter = new ArrayList<SearchFilter>();

		String tenantId = searchDto.getTenantId();
		if (!StringUtils.isBlank(tenantId)) {
			sysFilter.add(new SearchFilter("cloudSystem.tenantId", Operator.EQ, tenantId));
		}

		String systemName = searchDto.getSystemName();
		if (!StringUtils.isBlank(systemName)) {
			sysFilter.add(new SearchFilter("cloudSystem.systemName", Operator.LIKE, systemName));
		}

		Integer systemType = searchDto.getSystemType();
		if (systemType != null) {
			sysFilter.add(new SearchFilter("cloudSystem.systemType", Operator.EQ, systemType));
		}

		// 查询分页
		Page<CloudSystem> page = cloudSystemDao.findPageByFilter(pageable, sysFilter);

		// 分页结果
		long total = 0;
		List<CloudSystemDto> content = new ArrayList<CloudSystemDto>();
		if (page != null) {
			total = page.getTotalElements();

			CloudSystemDto dto = null;
			for (CloudSystem sys : page.getContent()) {
				dto = new CloudSystemDto();
				BeanUtils.copyProperties(sys, dto);

				content.add(dto);
			}
		}

		return new PageImpl<CloudSystemDto>(content, pageable, total);
	}

	@Transactional(readOnly = true)
	@Override
	public CloudSystemDto getCloudSystemById(String id) {
		// 检查必要信息
		if (StringUtils.isBlank(id)) {
			throw new ServiceException("请输入业务系统id！");
		}

		CloudSystem entity = cloudSystemDao.findOne(id);
		if (entity == null) {
			throw new ServiceException("根据业务系统id(" + id + ")未找到业务系统信息！");
		}

		CloudSystemDto dto = new CloudSystemDto();
		BeanUtils.copyProperties(entity, dto);

		return dto;
	}

	@Override
	public CloudSystemDto getCloudSystemByCode(String tenantId, String systemCode) {
		List<SearchFilter> filterList = new ArrayList<SearchFilter>();

		if (StringUtils.isNotBlank(tenantId)) {
			filterList.add(new SearchFilter("tenantId", Operator.EQ, tenantId));
		} else {
			filterList.add(new SearchFilter("tenantId", Operator.NULL, null));
		}

		filterList.add(new SearchFilter("systemCode", Operator.EQ, systemCode));

		List<CloudSystem> list = cloudSystemDao.findListByFilter(filterList, null);
		if (CollectionUtils.isEmpty(list)) {
			return null;
		}
		CloudSystem entity = list.get(0);

		CloudSystemDto dto = new CloudSystemDto();
		BeanUtils.copyProperties(entity, dto);
		return dto;
	}

	@Override
	public CloudSystem updateCloudSystem(CloudSystemDto dto) {
		// 检查必要信息
		if (StringUtils.isBlank(dto.getSystemName())) {
			throw new ServiceException("系统名称不能为空！");
		}

		if (StringUtils.isBlank(dto.getWebsite())) {
			throw new ServiceException("站点不能为空！");
		}

		if (StringUtils.isBlank(dto.getMapType())) {
			throw new ServiceException("地图类型不能为空！");
		}

		CloudSystem entity = cloudSystemDao.findOne(dto.getId());
		entity.setSystemName(dto.getSystemName());
		entity.setWebsite(dto.getWebsite());
		entity.setMapType(dto.getMapType());
		entity.setMapStr(dto.getMapStr());
		entity.setLongitude(dto.getLongitude());
		entity.setLatitude(dto.getLatitude());
		entity.setLongitudeDone(dto.getLongitudeDone());
		entity.setLatitudeDone(dto.getLatitudeDone());
		entity.setWelcomePage(dto.getWelcomePage());

		return cloudSystemDao.update(entity);
	}

	@Override
	public List<CloudSystem> getCloudSystems(String tenantId) {

		if (StringUtils.isBlank(tenantId)) {
			logger.error("tenantId不能为空！");
			throw new ServiceException("tenantId不能为空！");
		}
		List<SearchFilter> searchFilters = Lists.newArrayList();
		searchFilters.add(new SearchFilter("tenantId", Operator.EQ, tenantId));
		return cloudSystemDao.findListByFilter(searchFilters, null);

	}

	@Override
	public List<CloudTreeDto> getCloudSystemsByUserId(String userId) {
		if (StringUtils.isBlank(userId)) {
			logger.error("userId不能为空！");
			throw new ServiceException("userId不能为空！");
		}
		return cloudSystemDao.getCloudSystemsByUserId(userId);
	}

	@Override
	public List<String> getSystemList(String userId) {
		if (StringUtils.isBlank(userId)) {
			logger.error("userId不能为空！");
			throw new ServiceException("userId不能为空！");
		}
		return cloudSystemDao.getSystemList(userId);

	}

	@Override
	public List<CloudSystemDto> getCloudSystemByRoleCode(String roleCode) {
		return cloudSystemDao.getCloudSystemByRoleCode(roleCode);
	}

}
