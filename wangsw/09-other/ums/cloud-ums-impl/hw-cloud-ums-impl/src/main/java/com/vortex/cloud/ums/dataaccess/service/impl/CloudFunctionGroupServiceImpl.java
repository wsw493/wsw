package com.vortex.cloud.ums.dataaccess.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.vortex.cloud.ums.dataaccess.dao.ICloudFunctionDao;
import com.vortex.cloud.ums.dataaccess.dao.ICloudFunctionGroupDao;
import com.vortex.cloud.ums.dataaccess.service.ICloudFunctionGroupService;
import com.vortex.cloud.ums.dto.CloudFunctionGroupDto;
import com.vortex.cloud.ums.model.CloudFunction;
import com.vortex.cloud.ums.model.CloudFunctionGroup;
import com.vortex.cloud.vfs.common.exception.ServiceException;
import com.vortex.cloud.vfs.common.exception.VortexException;
import com.vortex.cloud.vfs.data.hibernate.repository.HibernateRepository;
import com.vortex.cloud.vfs.data.hibernate.service.SimplePagingAndSortingService;
import com.vortex.cloud.vfs.data.support.SearchFilter;

@Transactional
@Service("cloudFunctionGroupService")
public class CloudFunctionGroupServiceImpl extends SimplePagingAndSortingService<CloudFunctionGroup, String> implements ICloudFunctionGroupService {

	private Logger loger = LoggerFactory.getLogger(CloudDepartmentServiceImpl.class);

	@Resource
	private ICloudFunctionGroupDao cloudFunctionGroupDao;

	@Resource
	private ICloudFunctionDao cloudFunctionDao;

	@Override
	public HibernateRepository<CloudFunctionGroup, String> getDaoImpl() {
		return cloudFunctionGroupDao;
	}

	@Override
	public void deleteAllById(String id) {
		if (StringUtils.isEmpty(id)) {
			loger.error("主键不能为空");
			throw new ServiceException("主键不能为空");
		}
		List<String> childrenIds = this.getAllChildrenId(id);
		if (!CollectionUtils.isEmpty(childrenIds)) {
			List<CloudFunctionGroup> list = cloudFunctionGroupDao.findAllByIds(childrenIds.toArray(new String[childrenIds.size()]));
			cloudFunctionGroupDao.deleteInBatch(list);
		}
		cloudFunctionGroupDao.delete(id);
	}

	@Transactional(readOnly = true)
	@Override
	public List<String> getAllChildrenId(String id) {
		if (StringUtils.isEmpty(id)) {
			loger.error("主键不能为空");
			throw new ServiceException("主键不能为空");
		}
		List<String> results = null;

		Map<String, Object> filterPropertyMap = new HashMap<String, Object>();
		filterPropertyMap.put("cloudFunctionGroup.parentId", id);
		List<CloudFunctionGroup> list = cloudFunctionGroupDao.findListByProperty(filterPropertyMap, null);
		if (!CollectionUtils.isEmpty(list)) {
			results = new ArrayList<String>();
			for (CloudFunctionGroup actionGroup : list) {
				results.add(actionGroup.getId());

				List<String> childrens = this.getAllChildrenId(actionGroup.getId());
				if (childrens != null) {
					results.addAll(childrens);
				}
			}
		}
		return results;
	}

	@Transactional(readOnly = true)
	@Override
	public List<String> getAllParentId(String id) {
		if (StringUtils.isEmpty(id)) {
			loger.error("主键不能为空");
			throw new ServiceException("主键不能为空");
		}
		List<String> results = null;
		CloudFunctionGroup actionGroup = cloudFunctionGroupDao.findOne(id);

		if (actionGroup != null) {
			results = new ArrayList<String>();
			List<String> parentIds = this.getAllParentId(actionGroup.getParentId());
			if (parentIds != null) {
				results.add(actionGroup.getParentId());
				results.addAll(parentIds);
			}
		}
		return results;
	}

	@Override
	public void save(CloudFunctionGroupDto dto) {

		this.checkFormForAdd(dto);

		CloudFunctionGroup cloudFunctionGroup = new CloudFunctionGroup();
		BeanUtils.copyProperties(dto, cloudFunctionGroup);

		cloudFunctionGroupDao.save(cloudFunctionGroup);
	}

	private void checkFormForAdd(CloudFunctionGroupDto dto) {
		if (null == dto) {
			loger.error("dto不能为空");
			throw new ServiceException("dto不能为空");
		}

		if (StringUtils.isEmpty(dto.getCode())) {
			loger.error("编码不能为空");
			throw new ServiceException("编码不能为空");
		}

		if (StringUtils.isEmpty(dto.getName())) {
			loger.error("名称不能为空");
			throw new ServiceException("名称不能为空");
		}
	}

	@Override
	public CloudFunctionGroupDto findFunctionGroupById(String id) {
		if (StringUtils.isEmpty(id)) {
			loger.error("主键不能为空");
			throw new ServiceException("主键不能为空");
		}
		CloudFunctionGroup cloudFunctionGroup = cloudFunctionGroupDao.findOne(id);
		if (cloudFunctionGroup == null) {
			loger.error("不存在id为" + id + "的记录");
			throw new ServiceException("不存在id为" + id + "的记录");
		}
		CloudFunctionGroupDto cloudFunctionGroupDto = new CloudFunctionGroupDto();
		BeanUtils.copyProperties(cloudFunctionGroup, cloudFunctionGroupDto);
		return cloudFunctionGroupDto;
	}

	@Override
	public CloudFunctionGroupDto findFunctionGroupAndGroupNameById(String id) {
		if (StringUtils.isEmpty(id)) {
			loger.error("主键不能为空");
			throw new ServiceException("主键不能为空");
		}
		CloudFunctionGroupDto cloudFunctionGroupDto = cloudFunctionGroupDao.findCloudFunctionGroupById(id);
		if (cloudFunctionGroupDto == null) {
			loger.error("不存在id为" + id + "的记录");
			throw new ServiceException("不存在id为" + id + "的记录");
		}
		if (StringUtils.isEmpty(cloudFunctionGroupDto.getGroupName())) {
			cloudFunctionGroupDto.setGroupName("功能组");
		}
		return cloudFunctionGroupDto;
	}

	@Override
	public void update(CloudFunctionGroupDto dto) {
		this.checkFormForUpdate(dto);

		CloudFunctionGroup cloudFunctionGroup = findOne(dto.getId());
		if (null == cloudFunctionGroup) {
			loger.error("不存在id为" + dto.getId() + "的数据");
			throw new ServiceException("不存在id为" + dto.getId() + "的数据");
		}

		cloudFunctionGroup.setCode(dto.getCode());
		cloudFunctionGroup.setName(dto.getName());
		cloudFunctionGroup.setOrderIndex(dto.getOrderIndex());
		cloudFunctionGroup.setDescription(dto.getDescription());

		super.update(cloudFunctionGroup);
	}

	private void checkFormForUpdate(CloudFunctionGroupDto dto) {
		if (null == dto) {
			loger.error("dto不能为空");
			throw new ServiceException("dto不能为空");
		}
		if (StringUtils.isEmpty(dto.getId())) {
			loger.error("id不能为空");
			throw new ServiceException("id不能为空");
		}

		if (StringUtils.isEmpty(dto.getCode())) {
			loger.error("编码不能为空");
			throw new ServiceException("编码不能为空");
		}

		if (StringUtils.isEmpty(dto.getName())) {
			loger.error("名称不能为空");
			throw new ServiceException("名称不能为空");
		}
	}

	@Override
	public void deleteFunctionGroup(String fgId) {
		if (StringUtils.isEmpty(fgId)) {
			loger.error("删除功能组时，传入的id为空！");
			throw new ServiceException("删除功能组时，传入的id为空！");
		}

		CloudFunctionGroup fg = cloudFunctionGroupDao.findOne(fgId);
		if (fg == null) {
			loger.error("根据id[" + fgId + "]未找到功能组！");
			throw new ServiceException("根据id[" + fgId + "]未找到功能组！");
		}

		// 有子节点的不能删除
		List<SearchFilter> searchFilters = Lists.newArrayList();
		SearchFilter filter = new SearchFilter("parentId", SearchFilter.Operator.EQ, fgId);
		searchFilters.add(filter);
		List<CloudFunctionGroup> children = this.cloudFunctionGroupDao.findListByFilter(searchFilters, null);
		if (CollectionUtils.isNotEmpty(children)) {
			loger.error("当前功能组有子节点，无法删除！");
			throw new ServiceException("当前功能组有子节点，无法删除！");
		}

		// 节点上挂有功能的，也不能删除
		searchFilters = Lists.newArrayList();
		filter = new SearchFilter("groupId", SearchFilter.Operator.EQ, fgId);
		searchFilters.add(filter);
		List<CloudFunction> rl = cloudFunctionDao.findListByFilter(searchFilters, null);
		if (CollectionUtils.isNotEmpty(rl)) {
			loger.error("当前功能组下面有功能，无法删除！");
			throw new ServiceException("当前功能组下面有功能，无法删除！");
		}

		cloudFunctionGroupDao.delete(fg);
	}

	@Override
	public void deletes(List<String> canBeDeletes) {
		if (CollectionUtils.isEmpty(canBeDeletes)) {
			return;
		}

		List<CloudFunctionGroup> cloudFunctionGroups = cloudFunctionGroupDao.findAllByIds(canBeDeletes.toArray(new String[canBeDeletes.size()]));
		if (CollectionUtils.isEmpty(cloudFunctionGroups)) {
			throw new VortexException("根据ids未能找到对应记录");
		}

		cloudFunctionGroupDao.delete(cloudFunctionGroups);
	}
}
