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
import com.vortex.cloud.ums.dataaccess.dao.ICloudRoleDao;
import com.vortex.cloud.ums.dataaccess.dao.ICloudRoleGroupDao;
import com.vortex.cloud.ums.dataaccess.service.ICloudRoleGroupService;
import com.vortex.cloud.ums.dto.CloudRoleGroupDto;
import com.vortex.cloud.ums.model.CloudRole;
import com.vortex.cloud.ums.model.CloudRoleGroup;
import com.vortex.cloud.vfs.common.exception.ServiceException;
import com.vortex.cloud.vfs.common.exception.VortexException;
import com.vortex.cloud.vfs.data.hibernate.repository.HibernateRepository;
import com.vortex.cloud.vfs.data.hibernate.service.SimplePagingAndSortingService;
import com.vortex.cloud.vfs.data.support.SearchFilter;

@Transactional
@Service("cloudRoleGroupService")
public class CloudRoleGroupServiceImpl extends SimplePagingAndSortingService<CloudRoleGroup, String> implements ICloudRoleGroupService {
	@Resource
	private ICloudRoleGroupDao cloudRoleGroupDao;

	@Resource
	private ICloudRoleDao cloudRoleDao;

	private Logger loger = LoggerFactory.getLogger(CloudDepartmentServiceImpl.class);

	@Override
	public void deleteAllById(String id) {
		if (StringUtils.isEmpty(id)) {
			loger.error("主键不能为空");
			throw new ServiceException("主键不能为空");
		}
		List<String> childrenIds = this.getAllChildrenId(id);
		if (!CollectionUtils.isEmpty(childrenIds)) {
			List<CloudRoleGroup> list = cloudRoleGroupDao.findAllByIds(childrenIds.toArray(new String[childrenIds.size()]));
			cloudRoleGroupDao.deleteInBatch(list);
		}
		cloudRoleGroupDao.delete(id);

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
		filterPropertyMap.put("cloudRoleGroup.parentId", id);
		List<CloudRoleGroup> list = cloudRoleGroupDao.findListByProperty(filterPropertyMap, null);
		if (!CollectionUtils.isEmpty(list)) {
			results = new ArrayList<String>();
			for (CloudRoleGroup actionGroup : list) {
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
		CloudRoleGroup actionGroup = cloudRoleGroupDao.findOne(id);

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
	public HibernateRepository<CloudRoleGroup, String> getDaoImpl() {
		return cloudRoleGroupDao;
	}

	@Override
	public void saveRoleGroup(CloudRoleGroupDto dto) {
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

		CloudRoleGroup cloudRoleGroup = new CloudRoleGroup();
		BeanUtils.copyProperties(dto, cloudRoleGroup);
		cloudRoleGroupDao.save(cloudRoleGroup);
	}

	@Override
	public CloudRoleGroupDto findRoleGroupById(String id) {
		if (StringUtils.isEmpty(id)) {
			loger.error("主键不能为空");
			throw new ServiceException("主键不能为空");
		}
		CloudRoleGroup cloudRoleGroup = cloudRoleGroupDao.findOne(id);
		if (cloudRoleGroup == null) {
			loger.error("不存在id为" + id + "的记录");
			throw new ServiceException("不存在id为" + id + "的记录");

		}
		CloudRoleGroupDto cloudRoleGroupDto = new CloudRoleGroupDto();
		BeanUtils.copyProperties(cloudRoleGroup, cloudRoleGroupDto);
		return cloudRoleGroupDto;

	}

	@Override
	public CloudRoleGroupDto findRoleGroupAndGroupNameById(String id) {
		if (StringUtils.isEmpty(id)) {
			loger.error("主键不能为空");
			throw new ServiceException("主键不能为空");
		}
		CloudRoleGroupDto cloudRoleGroupDto = cloudRoleGroupDao.findRoleGroupAndGroupNameById(id);
		if (cloudRoleGroupDto == null) {
			loger.error("不存在id为" + id + "的记录");
			throw new ServiceException("不存在id为" + id + "的记录");
		}
		if (StringUtils.isEmpty(cloudRoleGroupDto.getGroupName())) {
			cloudRoleGroupDto.setGroupName("角色组");
		}
		return cloudRoleGroupDto;

	}

	@Override
	public void updateRoleGroup(CloudRoleGroupDto dto) {
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

		CloudRoleGroup cloudRoleGroup = findOne(dto.getId());
		if (null == cloudRoleGroup) {
			loger.error("不存在id为" + dto.getId() + "的数据");
			throw new ServiceException("不存在id为" + dto.getId() + "的数据");
		}
		cloudRoleGroup.setCode(dto.getCode());
		cloudRoleGroup.setName(dto.getName());
		cloudRoleGroup.setSystemId(dto.getSystemId());
		cloudRoleGroup.setParentId(dto.getParentId());
		cloudRoleGroup.setDescription(dto.getDescription());
		cloudRoleGroup.setOrderIndex(dto.getOrderIndex());
		update(cloudRoleGroup);

	}

	@Override
	public void deleteRoleGroup(String rgId) {
		if (StringUtils.isEmpty(rgId)) {
			loger.error("删除角色组时，传入的id为空！");
			throw new ServiceException("删除角色组时，传入的id为空！");
		}

		CloudRoleGroup rg = cloudRoleGroupDao.findOne(rgId);
		if (rg == null) {
			loger.error("根据id[" + rgId + "]未找到角色组！");
			throw new ServiceException("根据id[" + rgId + "]未找到角色组！");
		}

		// 有子节点的不能删除
		List<SearchFilter> searchFilters = Lists.newArrayList();
		SearchFilter filter = new SearchFilter("parentId", SearchFilter.Operator.EQ, rgId);
		searchFilters.add(filter);
		List<CloudRoleGroup> children = this.cloudRoleGroupDao.findListByFilter(searchFilters, null);
		if (CollectionUtils.isNotEmpty(children)) {
			loger.error("当前角色组有子节点，无法删除！");
			throw new ServiceException("当前角色组有子节点，无法删除！");
		}

		// 节点上挂有角色的，也不能删除
		searchFilters = Lists.newArrayList();
		filter = new SearchFilter("groupId", SearchFilter.Operator.EQ, rgId);
		searchFilters.add(filter);
		List<CloudRole> rl = cloudRoleDao.findListByFilter(searchFilters, null);
		if (CollectionUtils.isNotEmpty(rl)) {
			loger.error("当前角色组下面有角色，无法删除！");
			throw new ServiceException("当前角色组下面有角色，无法删除！");
		}

		cloudRoleGroupDao.delete(rg);
	}

	@Override
	public void deletes(List<String> canBeDeletes) {
		if (CollectionUtils.isEmpty(canBeDeletes)) {
			return;
		}

		List<CloudRoleGroup> cloudRoleGroups = cloudRoleGroupDao.findAllByIds(canBeDeletes.toArray(new String[canBeDeletes.size()]));
		if (CollectionUtils.isEmpty(cloudRoleGroups)) {
			throw new VortexException("根据ids未能找到对应记录");
		}

		cloudRoleGroupDao.delete(cloudRoleGroups);
	}
}
