package com.vortex.cloud.ums.dataaccess.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.vortex.cloud.ums.dataaccess.dao.ICloudFunctionDao;
import com.vortex.cloud.ums.dataaccess.dao.ICloudFunctionGroupDao;
import com.vortex.cloud.ums.dataaccess.dao.ICloudFunctionRoleDao;
import com.vortex.cloud.ums.dataaccess.service.ICloudFunctionService;
import com.vortex.cloud.ums.dataaccess.service.ICloudSystemService;
import com.vortex.cloud.ums.dataaccess.service.IRedisSyncService;
import com.vortex.cloud.ums.dto.CloudFunctionDto;
import com.vortex.cloud.ums.dto.CloudTreeDto;
import com.vortex.cloud.ums.model.CloudFunction;
import com.vortex.cloud.ums.model.CloudFunctionGroup;
import com.vortex.cloud.ums.model.CloudFunctionRole;
import com.vortex.cloud.ums.model.CloudSystem;
import com.vortex.cloud.vfs.common.exception.VortexException;
import com.vortex.cloud.vfs.data.hibernate.repository.HibernateRepository;
import com.vortex.cloud.vfs.data.hibernate.service.SimplePagingAndSortingService;
import com.vortex.cloud.vfs.data.support.SearchFilter;

@Transactional
@Service("cloudFunctionService")
public class CloudFunctionServiceImpl extends SimplePagingAndSortingService<CloudFunction, String>
		implements ICloudFunctionService {
	private static final Logger logger = LoggerFactory.getLogger(CloudFunctionServiceImpl.class);

	@Resource
	private ICloudFunctionDao cloudFunctionDao;

	@Resource
	private ICloudFunctionRoleDao cloudFunctionRoleDao;

	@Resource
	private ICloudFunctionGroupDao cloudFunctionGroupDao;

	@Resource
	private ICloudSystemService cloudSystemService;
	@Resource
	private IRedisSyncService redisSyncService;

	@Override
	public String save(CloudFunctionDto dto) {
		// 校验入参
		this.checkData(dto);

		CloudFunction bean = new CloudFunction();
		BeanUtils.copyProperties(dto, bean);
		bean = cloudFunctionDao.save(bean);

		return bean.getId();
	}

	@Override
	public void update(CloudFunctionDto dto) {
		// 校验入参
		if (StringUtils.isEmpty(dto.getId())) {
			logger.error("保存功能时传入的功能id为空！");
			throw new VortexException("保存功能时传入的功能id为空！");
		}
		this.checkData(dto);

		CloudFunction bean = this.cloudFunctionDao.findOne(dto.getId());
		if (bean == null) {
			logger.error("根据id【" + dto.getId() + "】未找到功能！");
			throw new VortexException("根据id【" + dto.getId() + "】未找到功能！");
		}
		bean.setCode(dto.getCode());
		bean.setName(dto.getName());
		bean.setGoalSystemId(dto.getGoalSystemId());
		bean.setUri(dto.getUri());
		bean.setOrderIndex(dto.getOrderIndex());
		bean.setDescription(dto.getDescription());
		bean.setFunctionType(dto.getFunctionType());
		bean.setMainFunctionId(dto.getMainFunctionId());

		this.cloudFunctionDao.update(bean);
		// 更新数据库
		cloudFunctionDao.flush();

		try {
			// 刷新缓存
			CloudSystem system = this.cloudSystemService.findOne(bean.getSystemId());
			redisSyncService.syncSystemMenuByTenant(Lists.newArrayList(system.getTenantId()));
		} catch (Exception e) {
			logger.error("CloudFunctionServiceImpl.update:更新功能后刷新缓存出错！");
		}
	}

	private void checkData(CloudFunctionDto dto) {
		if (dto == null) {
			logger.error("保存功能时传入的参数为空！");
			throw new VortexException("保存功能时传入的参数为空！");
		}

		if (StringUtils.isEmpty(dto.getName())) {
			logger.error("保存功能时传入的名称为空！");
			throw new VortexException("保存功能时传入的名称为空！");
		}
	}

	@Override
	public void deleteFunction(String functionId) {
		if (StringUtils.isEmpty(functionId)) {
			logger.error("删除功能时，传入的功能id为空！");
			throw new VortexException("删除功能时，传入的功能id为空！");
		}

		List<SearchFilter> searchFilters = Lists.newArrayList();
		SearchFilter filter = new SearchFilter("functionId", SearchFilter.Operator.EQ, functionId);
		searchFilters.add(filter);
		List<CloudFunctionRole> urList = cloudFunctionRoleDao.findListByFilter(searchFilters, null);
		if (CollectionUtils.isNotEmpty(urList)) {
			logger.error("id为[" + functionId + "]的功能已经被使用，无法删除！");
			throw new VortexException("id为[" + functionId + "]的功能已经被使用，无法删除！");
		}

		cloudFunctionDao.delete(functionId);
	}

	@Override
	public boolean isCodeExistsForSystem(String systemId, String functionId, String newCode) {
		boolean rst = false;
		if (StringUtils.isEmpty(functionId)) { // id为空，认为是新增，只要判断租户下面不存在同code的就行
			rst = cloudFunctionDao.isCodeExistsForSystem(systemId, newCode);
		} else { // id不为空，看新旧code是否相同，如果相同，不用判断，如果不相同，需要判断
			CloudFunction function = this.cloudFunctionDao.findOne(functionId);
			if (function == null) {
				logger.error("根据id【" + functionId + "】未找到功能！");
				throw new VortexException("根据id【" + functionId + "】未找到功能！");
			}

			if (!newCode.equals(function.getCode())) {
				rst = cloudFunctionDao.isCodeExistsForSystem(systemId, newCode);
			}
		}
		return rst;
	}

	@Override
	public CloudFunctionDto getFunctionInfoById(String functionId) {
		if (StringUtils.isEmpty(functionId)) {
			logger.error("根据角色id查询功能信息时，传入的功能id为空！");
			throw new VortexException("根据角色id查询角色信息时，传入的角色id为空！");
		}

		CloudFunctionDto dto = cloudFunctionDao.getFunctionById(functionId);
		return dto;
	}

	@Override
	public HibernateRepository<CloudFunction, String> getDaoImpl() {
		return cloudFunctionDao;
	}

	@Override
	public void deletes(List<String> idList) {
		if (CollectionUtils.isEmpty(idList)) {
			return;
		}

		for (String id : idList) {
			super.delete(id);
		}
	}

	@Override
	public Object getFunctionsByUsreIdAndSystem(String userId, String systemCode) {
		if (StringUtils.isEmpty(userId)) {
			logger.error("用户id不能为空");
			throw new VortexException("用户id不能为空");
		}
		return cloudFunctionDao.getFunctionsByUsreIdAndSystem(userId, systemCode);
	}

	@Override
	public Object getFunctionsByIds(String functionIds) {
		if (StringUtils.isEmpty(functionIds)) {
			logger.error("functionIds不能为空");
			throw new VortexException("functionIds不能为空");
		}
		return cloudFunctionDao.getFunctionsByIds(functionIds);
	}

	@Override
	public List<CloudTreeDto> getTreeData(String userId) {
		List<CloudTreeDto> systemList = cloudSystemService.getCloudSystemsByUserId(userId);
		List<CloudTreeDto> functionList = this.getCloudFunctionByUserId(userId);
		List<CloudTreeDto> fgList = new ArrayList<>();

		List<CloudTreeDto> allList = new ArrayList<>();

		if (CollectionUtils.isEmpty(systemList) || CollectionUtils.isEmpty(functionList)) {
			return null;
		}
		for (CloudTreeDto dto : functionList) {
			findGroup(fgList, dto.getParentId());
		}
		if (CollectionUtils.isNotEmpty(systemList)) {
			allList.addAll(systemList);
		}
		if (CollectionUtils.isNotEmpty(functionList)) {
			allList.addAll(functionList);
		}
		if (CollectionUtils.isNotEmpty(fgList)) {
			allList.addAll(fgList);
		}
		return allList;
	}

	/**
	 * 根据groupId递归查询上级功能组
	 * 
	 * @param fgList
	 * @param groupId
	 */
	private void findGroup(List<CloudTreeDto> fgList, String groupId) {
		if (isContain(fgList, groupId)) {
			return;
		} else {
			CloudFunctionGroup group = cloudFunctionGroupDao.findOne(groupId);
			CloudTreeDto dto = new CloudTreeDto();
			dto.setId(group.getId());
			dto.setName(group.getName());
			if (group.getParentId().equals("-1")) {
				dto.setParentId(group.getSystemId());
				fgList.add(dto);
				return;
			} else {
				dto.setParentId(group.getParentId());
				fgList.add(dto);
				findGroup(fgList, group.getParentId()); // 递归
			}
		}
	}

	/**
	 * 是否已经包含其中
	 * 
	 * @param fgList
	 * @param groupId
	 * @return
	 */
	private boolean isContain(List<CloudTreeDto> fgList, String groupId) {
		boolean isContain = false;
		for (CloudTreeDto dto : fgList) {
			if (dto != null && dto.getId().equals(groupId)) {
				isContain = true;
			}
		}
		return isContain;
	}

	@Override
	public List<CloudTreeDto> getCloudFunctionByUserId(String userId) {
		if (StringUtils.isEmpty(userId)) {
			logger.error("userId不能为空");
			throw new VortexException("userId不能为空");
		}
		return cloudFunctionDao.getCloudFunctionByUserId(userId);
	}

}
