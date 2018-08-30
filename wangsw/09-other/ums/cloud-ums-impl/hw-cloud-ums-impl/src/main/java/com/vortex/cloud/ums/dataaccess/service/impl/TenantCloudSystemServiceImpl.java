package com.vortex.cloud.ums.dataaccess.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.vortex.cloud.ums.dataaccess.dao.ICloudSystemDao;
import com.vortex.cloud.ums.dataaccess.dao.ITenantSystemRelationDao;
import com.vortex.cloud.ums.dataaccess.service.ITenantBusinessService;
import com.vortex.cloud.ums.dataaccess.service.ITenantCloudSystemService;
import com.vortex.cloud.ums.dto.CloudSysSearchDto;
import com.vortex.cloud.ums.dto.TenantSystemRelationDto;
import com.vortex.cloud.ums.model.CloudSystem;
import com.vortex.cloud.ums.model.TenantSystemRelation;
import com.vortex.cloud.vfs.common.exception.ServiceException;
import com.vortex.cloud.vfs.data.hibernate.repository.HibernateRepository;
import com.vortex.cloud.vfs.data.hibernate.service.SimplePagingAndSortingService;
import com.vortex.cloud.vfs.data.support.SearchFilter;
import com.vortex.cloud.vfs.data.support.SearchFilter.Operator;

@Transactional
@Service("tenantCloudSystemService")
public class TenantCloudSystemServiceImpl extends SimplePagingAndSortingService<TenantSystemRelation, String> implements ITenantCloudSystemService {
	private Logger logger = LoggerFactory.getLogger(TenantBusinessServiceImpl.class);
	@Resource
	private ITenantSystemRelationDao tenantSystemRelationDao;

	@Resource
	private ICloudSystemDao cloudSystemDao;

	@Resource
	private ITenantBusinessService tenantBusinessService;

	@Override
	public HibernateRepository<TenantSystemRelation, String> getDaoImpl() {
		return tenantSystemRelationDao;
	}

	@Transactional(readOnly = true)
	@Override
	public Page<TenantSystemRelationDto> getPageOfCloudSys(Pageable pageable, CloudSysSearchDto searchDto) {
		List<SearchFilter> sysFilter = new ArrayList<SearchFilter>();

		String systemName = searchDto.getSystemName();
		if (!StringUtils.isBlank(systemName)) {
			sysFilter.add(new SearchFilter("cloudSystem.systemName", Operator.LIKE, systemName));
		}
		if (StringUtils.isEmpty(searchDto.getTenantId())) {
			logger.error("租户id不能为空");
			throw new ServiceException("租户id不能为空");
		}
		sysFilter.add(new SearchFilter("systemType", Operator.EQ, CloudSystem.SYSTEM_TYPE_CLOUD));
		// 查询分页
		Page<CloudSystem> page = cloudSystemDao.findPageByFilter(pageable, sysFilter);

		// 分页结果
		long total = 0;
		List<TenantSystemRelationDto> content = new ArrayList<TenantSystemRelationDto>();
		if (page != null) {
			total = page.getTotalElements();

			String tenantId = searchDto.getTenantId();
			TenantSystemRelationDto relationDto = null;
			for (CloudSystem sys : page.getContent()) {
				relationDto = new TenantSystemRelationDto();
				relationDto.setTenantId(tenantId);
				relationDto.setCloudSystemId(sys.getId());
				relationDto.setCode(sys.getSystemCode());
				relationDto.setName(sys.getSystemName());
				relationDto.setStatus(sys.getStatus());

				this.setCloudSysRealtion(relationDto, tenantId, sys.getId());

				content.add(relationDto);
			}
		}

		return new PageImpl<TenantSystemRelationDto>(content, pageable, total);
	}

	private void setCloudSysRealtion(TenantSystemRelationDto relationDto, String tenantId, String sysId) {
		List<SearchFilter> relationFilter = Lists.newArrayList();
		relationFilter.add(new SearchFilter("tenantSystemRelation.tenantId", Operator.EQ, tenantId));
		relationFilter.add(new SearchFilter("tenantSystemRelation.cloudSystemId", Operator.EQ, sysId));

		List<TenantSystemRelation> relationList = tenantSystemRelationDao.findListByFilter(relationFilter, null);
		if (CollectionUtils.isNotEmpty(relationList)) {
			TenantSystemRelation relation = relationList.get(0);
			if (relation != null) {
				relationDto.setId(relation.getId());
				relationDto.setEnabled(relation.getEnabled());
			} else {
				relationDto.setEnabled(TenantSystemRelation.DISABLE);
			}
		}
	}

	@Override
	public void enableCloudSystem(String tenantId, String systemId) {
		if (StringUtils.isEmpty(tenantId) || StringUtils.isEmpty(systemId)) {
			throw new ServiceException("开通租户云系统时，传入的数据不足！");
		}

		TenantSystemRelation relation = null;

		List<SearchFilter> relationFilter = Lists.newArrayList();
		relationFilter.add(new SearchFilter("tenantSystemRelation.tenantId", Operator.EQ, tenantId));
		relationFilter.add(new SearchFilter("tenantSystemRelation.cloudSystemId", Operator.EQ, systemId));

		List<TenantSystemRelation> relationList = tenantSystemRelationDao.findListByFilter(relationFilter, null);
		if (CollectionUtils.isNotEmpty(relationList)) {
			relation = relationList.get(0);
		}

		if (relation == null) {
			relation = new TenantSystemRelation();
			relation.setTenantId(tenantId);
			relation.setCloudSystemId(systemId);
			relation.setEnabled(TenantSystemRelation.ENABLE);
			relation.setHasResource(TenantSystemRelation.NOT);

			tenantSystemRelationDao.save(relation);
		} else {
			relation.setEnabled(TenantSystemRelation.ENABLE);

			tenantSystemRelationDao.update(relation);
		}

		// 为此云系统，准备相关资源
		tenantBusinessService.copyResources(tenantId, systemId);
	}

	@Override
	public void disableCloudSystem(String id) {
		if (StringUtils.isEmpty(id)) {
			throw new ServiceException("禁用租户的系统时，传入的数据不足！");
		}

		TenantSystemRelation relation = tenantSystemRelationDao.findOne(id);

		if (relation == null) {
			throw new ServiceException("禁用租户的系统时，未找到租户和系统关系表，请检查数据！");
		}

		relation.setEnabled(TenantSystemRelation.DISABLE);
		tenantSystemRelationDao.update(relation);
	}

	@Override
	public boolean isTenantOpenSystem(String tenantId, String cloudSystemCode) {
		return tenantSystemRelationDao.isTenantOpenSystem(tenantId, cloudSystemCode);
	}
}
