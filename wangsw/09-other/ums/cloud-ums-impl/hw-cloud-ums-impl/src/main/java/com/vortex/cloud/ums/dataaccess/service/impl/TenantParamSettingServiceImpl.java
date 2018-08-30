package com.vortex.cloud.ums.dataaccess.service.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.vortex.cloud.ums.dataaccess.dao.ITenantDao;
import com.vortex.cloud.ums.dataaccess.dao.ITenantPramSettingDao;
import com.vortex.cloud.ums.dataaccess.service.ITenantParamSettingService;
import com.vortex.cloud.ums.dto.TenantPramSettingDto;
import com.vortex.cloud.ums.model.Tenant;
import com.vortex.cloud.ums.model.TenantPramSetting;
import com.vortex.cloud.ums.util.PropertyUtils;
import com.vortex.cloud.vfs.common.exception.VortexException;
import com.vortex.cloud.vfs.data.hibernate.repository.HibernateRepository;
import com.vortex.cloud.vfs.data.hibernate.service.SimplePagingAndSortingService;
import com.vortex.cloud.vfs.data.support.SearchFilter;





@Service("tenantParamSettingService")
@Transactional
public class TenantParamSettingServiceImpl extends SimplePagingAndSortingService<TenantPramSetting, String>
		implements ITenantParamSettingService {

	private static final Logger logger = LoggerFactory.getLogger(TenantParamSettingServiceImpl.class);

	@Resource
	private ITenantPramSettingDao tenantPramSettingDao;

	@Resource
	private ITenantDao tenantDao;

	@Override
	public HibernateRepository<TenantPramSetting, String> getDaoImpl() {
		return tenantPramSettingDao;
	}

	@Override
	public long delete(String[] idArr) {
		if (ArrayUtils.isEmpty(idArr)) {
			if (logger.isDebugEnabled()) {
				logger.debug("delete(), idArr is Empty");
			}
			return 0;
		}

		long deleted = 0;
		for (String id : idArr) {
			this.delete(id);
			deleted++;
		}

		return deleted;
	}

	@Transactional(readOnly = true)
	@Override
	public List<TenantPramSetting> findListByParamTypeCode(String tenantId, String paramTypeCode) {
		return tenantPramSettingDao.findListByParamTypeCode(tenantId, paramTypeCode);
	}

	@Override
	public List<TenantPramSetting> findListByParamTypeCodeAndTenantCode(String tenantCode, String paramTypeCode) {
		if (StringUtils.isEmpty(tenantCode) || StringUtils.isEmpty(paramTypeCode)) {
			logger.error("参数不能为空！");
			throw new VortexException("参数不能为空！");
		}
		Tenant tenant = null;
		List<SearchFilter> searchFilters = Lists.newArrayList();
		SearchFilter filter = new SearchFilter("tenantCode", SearchFilter.Operator.EQ, tenantCode);
		searchFilters.add(filter);
		List<Tenant> list = tenantDao.findListByFilter(searchFilters, null);
		if (CollectionUtils.isNotEmpty(list)) {
			tenant = list.get(0);
		} else {
			tenant = null;
		}

		if (tenant == null) {
			logger.error("根据租户code[" + tenantCode + "]未找到租户！");
			throw new VortexException("根据租户code[" + tenantCode + "]未找到租户！");
		}

		return findListByParamTypeCode(tenant.getId(), paramTypeCode);
	}

	@Override
	public Map<String, List<TenantPramSettingDto>> findByParamTypeCodeList(String tenantId,
			List<String> paramTypeCodeList) {
		return tenantPramSettingDao.findByParamTypeCodeList(tenantId, paramTypeCodeList);
	}

	@Override
	public TenantPramSetting findOneByParamCode(String tenantId, String paramTypeCode, String paramCode) {
		return tenantPramSettingDao.findOneByParamCode(tenantId, paramTypeCode, paramCode);
	}

	@Override
	public TenantPramSetting findOneByParamName(String tenantId, String paramTypeCode, String paramName) {
		return tenantPramSettingDao.findOneByParamName(tenantId, paramTypeCode, paramName);
	}

	@Override
	public Map<String, TenantPramSetting> findListByParamCodes(String tenantId, String paramTypeCode,
			String[] paramCodes) {
		return tenantPramSettingDao.findListByParamCodes(tenantId, paramTypeCode, paramCodes);
	}

	@Override
	public Map<String, List<Map<String, Object>>> loadMultiParamList(String tenantId, List<String> typeCodes) {
		if (StringUtils.isEmpty(tenantId)) {
			logger.error("租户id为空");
			throw new VortexException("租户id为空");

		}

		if (CollectionUtils.isEmpty(typeCodes)) {
			logger.error("参数类型列表为空");
			throw new VortexException("参数类型列表为空");

		}
		List<String> typeCodeList = Lists.newArrayList();
		typeCodes.forEach(typeCode -> {
			typeCodeList.add(PropertyUtils.getPropertyValue(typeCode));
		});
		List<TenantPramSettingDto> tenantPramSettingDtos = tenantPramSettingDao.findByParamTypeCodes(tenantId,
				typeCodeList);

		// 按照参数类型进行分组
		if (CollectionUtils.isEmpty(tenantPramSettingDtos)) {
			return Maps.newHashMap();
		}

		Map<String, List<Map<String, Object>>> resultMap = Maps.newConcurrentMap();
		String typeCode = null;
		List<Map<String, Object>> paramList = null;
		
		for (TenantPramSettingDto dto : tenantPramSettingDtos) {
			typeCode = dto.getTypeCode();

			paramList = resultMap.get(typeCode);
			if (paramList == null) {
				paramList = Lists.newArrayList();
				resultMap.put(typeCode, paramList);
			}
			Map<String, Object> tempMap = Maps.newHashMap();
			tempMap.put("text", dto.getParmName());
			tempMap.put("value", dto.getParmCode());
			paramList.add(tempMap);
		}

		return resultMap;
	}
}