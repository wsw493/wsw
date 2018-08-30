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
import com.vortex.cloud.ums.dataaccess.dao.ICloudFunctionRoleDao;
import com.vortex.cloud.ums.dataaccess.dao.ICloudRoleDao;
import com.vortex.cloud.ums.dataaccess.dao.ICloudUserRoleDao;
import com.vortex.cloud.ums.dataaccess.service.ICloudRoleService;
import com.vortex.cloud.ums.dto.CloudRoleDto;
import com.vortex.cloud.ums.model.CloudFunctionRole;
import com.vortex.cloud.ums.model.CloudRole;
import com.vortex.cloud.ums.model.CloudUserRole;
import com.vortex.cloud.vfs.common.exception.VortexException;
import com.vortex.cloud.vfs.data.hibernate.repository.HibernateRepository;
import com.vortex.cloud.vfs.data.hibernate.service.SimplePagingAndSortingService;
import com.vortex.cloud.vfs.data.support.SearchFilter;
import com.vortex.cloud.vfs.data.support.SearchFilter.Operator;

@Transactional
@Service("cloudRoleService")
public class CloudRoleServiceImpl extends SimplePagingAndSortingService<CloudRole, String> implements ICloudRoleService {
	private static final Logger logger = LoggerFactory.getLogger(CloudRoleServiceImpl.class);

	@Resource
	private ICloudRoleDao cloudRoleDao;

	@Resource
	private ICloudUserRoleDao cloudUserRoleDao;

	@Resource
	private ICloudFunctionRoleDao cloudFunctionRoleDao;

	@Override
	public String saveRole(CloudRoleDto dto) {
		// 校验入参
		this.checkData(dto);
		// TODO 应该是什么类型
		dto.setRoleType(CloudRole.ROLE_TYPE_CUSTOM);

		CloudRole bean = new CloudRole();
		BeanUtils.copyProperties(dto, bean);
		bean = cloudRoleDao.save(bean);

		return bean.getId();
	}

	@Override
	public void updateRole(CloudRoleDto dto) {
		// 校验入参
		this.checkData(dto);

		CloudRole bean = this.cloudRoleDao.findOne(dto.getId());
		if (bean == null) {
			logger.error("根据id【" + dto.getId() + "】未找到角色！");
			throw new VortexException("根据id【" + dto.getId() + "】未找到角色！");
		}

		bean.setCode(dto.getCode());
		bean.setName(dto.getName());
		bean.setGroupId(dto.getGroupId());
		bean.setSystemId(dto.getSystemId());
		bean.setOrderIndex(dto.getOrderIndex());
		bean.setDescription(dto.getDescription());

		this.cloudRoleDao.update(bean);
	}

	private void checkData(CloudRoleDto dto) {
		if (dto == null) {
			logger.error("保存角色时传入的参数为空！");
			throw new VortexException("保存角色时传入的参数为空！");
		}

		if (StringUtils.isEmpty(dto.getCode())) {
			logger.error("保存角色时传入的编码为空！");
			throw new VortexException("保存角色时传入的编码为空！");
		}

		if (StringUtils.isEmpty(dto.getName())) {
			logger.error("保存角色时传入的名称为空！");
			throw new VortexException("保存角色时传入的名称为空！");
		}

		if (StringUtils.isEmpty(dto.getName())) {
			logger.error("保存角色时传入的角色组id为空！");
			throw new VortexException("保存角色时传入的角色组id为空！");
		}

		if (this.isRoleCodeExists(dto.getId(), dto.getCode(), dto.getSystemId())) {
			logger.error("保存角色时同一个租户下面的code不能重复！");
			throw new VortexException("保存角色时同一个租户下面的code不能重复！");
		}
	}

	@Override
	public void deleteRole(String roleId) {
		if (StringUtils.isEmpty(roleId)) {
			logger.error("删除角色时，传入的角色id为空！");
			throw new VortexException("删除角色时，传入的角色id为空！");
		}

		List<SearchFilter> searchFilters = Lists.newArrayList();
		SearchFilter filter = new SearchFilter("roleId", SearchFilter.Operator.EQ, roleId);
		searchFilters.add(filter);
		List<CloudUserRole> urList = cloudUserRoleDao.findListByFilter(searchFilters, null);
		if (CollectionUtils.isNotEmpty(urList)) {
			logger.error("id为[" + roleId + "]的角色已经被用户使用，无法删除！");
			throw new VortexException("id为[" + roleId + "]的角色已经被用户使用，无法删除！");
		}

		searchFilters = Lists.newArrayList();
		filter = new SearchFilter("roleId", SearchFilter.Operator.EQ, roleId);
		searchFilters.add(filter);
		List<CloudFunctionRole> frList = cloudFunctionRoleDao.findListByFilter(searchFilters, null);
		if (CollectionUtils.isNotEmpty(frList)) {
			logger.error("id为[" + roleId + "]的角色已经和功能关联使用，无法删除！");
			throw new VortexException("id为[" + roleId + "]的角色已经和功能关联使用，无法删除！");
		}

		cloudRoleDao.delete(roleId);
	}

	@Override
	public HibernateRepository<CloudRole, String> getDaoImpl() {
		return cloudRoleDao;
	}

	@Override
	public boolean isRoleCodeExists(String roleId, String newCode, String systemId) {
		boolean rst = false;
		if (StringUtils.isEmpty(roleId)) { // id为空，认为是新增，只要判断租户下面不存在同code的就行
			rst = cloudRoleDao.isCodeExists(newCode, systemId);
		} else { // id不为空，看新旧code是否相同，如果相同，不用判断，如果不相同，需要判断
			CloudRole role = this.cloudRoleDao.findOne(roleId);
			if (role == null) {
				logger.error("根据id【" + roleId + "】未找到角色！");
				throw new VortexException("根据id【" + roleId + "】未找到角色！");
			}

			if (!newCode.equals(role.getCode())) {
				rst = cloudRoleDao.isCodeExists(newCode, systemId);
			}
		}

		return rst;
	}

	@Override
	public CloudRoleDto getRoleInfoById(String roleId) {
		if (StringUtils.isEmpty(roleId)) {
			logger.error("根据角色id查询角色信息时，传入的角色id为空！");
			throw new VortexException("根据角色id查询角色信息时，传入的角色id为空！");
		}

		return cloudRoleDao.getById(roleId);
	}

	@Override
	public CloudRoleDto getRoleByCode(String code) {
		if (StringUtils.isBlank(code)) {
			String msg = "角色code为空！";
			logger.error(msg);
			throw new VortexException(msg);
		}

		List<SearchFilter> filterList = new ArrayList<>();
		filterList.add(new SearchFilter("code", Operator.EQ, code));

		List<CloudRole> list = this.findListByFilter(filterList, null);
		if (CollectionUtils.isEmpty(list)) {
			logger.error("根据角色code未能获取到角色记录！");
			return null;
		}

		CloudRole role = list.get(0);
		CloudRoleDto dto = new CloudRoleDto();
		BeanUtils.copyProperties(role, dto);
		return dto;
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
	public List<String> getUserIdsByRole(String tenantId, String roleCode) {

		if (StringUtils.isBlank(tenantId)) {
			String msg = "租户id为空！";
			logger.error(msg);
			throw new VortexException(msg);
		}
		if (StringUtils.isBlank(roleCode)) {
			String msg = "角色code为空！";
			logger.error(msg);
			throw new VortexException(msg);
		}
		List<String> userIds = cloudRoleDao.getUserIdsByRole(tenantId, roleCode);
		return userIds;
	}

	@Override
	public List<String> getUserIdsByRoleAndOrg(String orgId, String roleCode) {
		if (StringUtils.isBlank(orgId)) {
			String msg = "orgId为空！";
			logger.error(msg);
			throw new VortexException(msg);
		}
		if (StringUtils.isBlank(roleCode)) {
			String msg = "角色code为空！";
			logger.error(msg);
			throw new VortexException(msg);
		}
		List<String> userIds = cloudRoleDao.getUserIdsByRoleAndOrg(orgId, roleCode);
		return userIds;
	}
}
