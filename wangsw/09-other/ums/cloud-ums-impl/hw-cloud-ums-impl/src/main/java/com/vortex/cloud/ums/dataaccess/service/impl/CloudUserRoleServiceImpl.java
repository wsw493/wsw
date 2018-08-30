/*   
 * Copyright (C), 2005-2014, 苏州伏泰信息科技有限公司
 */
package com.vortex.cloud.ums.dataaccess.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vortex.cloud.ums.dataaccess.dao.ICloudRoleDao;
import com.vortex.cloud.ums.dataaccess.dao.ICloudUserRoleDao;
import com.vortex.cloud.ums.dataaccess.service.ICloudUserRoleService;
import com.vortex.cloud.ums.dto.CloudUserRoleDto;
import com.vortex.cloud.ums.dto.CloudUserRoleSearchDto;
import com.vortex.cloud.ums.model.CloudRole;
import com.vortex.cloud.ums.model.CloudUserRole;
import com.vortex.cloud.vfs.common.exception.ServiceException;
import com.vortex.cloud.vfs.data.hibernate.repository.HibernateRepository;
import com.vortex.cloud.vfs.data.hibernate.service.SimplePagingAndSortingService;
import com.vortex.cloud.vfs.data.support.SearchFilter;
import com.vortex.cloud.vfs.data.support.SearchFilter.Operator;

/**
 * @author LiShijun
 * @date 2016年4月7日 上午11:11:02
 * @Description 用户配置角色 History <author> <time> <desc>
 */
@Service("cloudUserRoleService")
@Transactional
public class CloudUserRoleServiceImpl extends SimplePagingAndSortingService<CloudUserRole, String> implements ICloudUserRoleService {

	private static final Logger logger = LoggerFactory.getLogger(CloudUserRoleServiceImpl.class);

	@Resource
	private ICloudUserRoleDao cloudUserRoleDao;

	@Resource
	private ICloudRoleDao cloudRoleDao;

	@Override
	public HibernateRepository<CloudUserRole, String> getDaoImpl() {
		return cloudUserRoleDao;
	}

	@Override
	public void addRoles(String userId, String[] roleIdArr) {
		if (StringUtils.isBlank(userId) || ArrayUtils.isEmpty(roleIdArr)) {
			String msg = "用户ID或者角色ID为空！";
			logger.error(msg);
			throw new ServiceException(msg);
		}

		// 获取用户已经绑定的角色
		List<SearchFilter> filterList = new ArrayList<SearchFilter>();
		filterList.add(new SearchFilter("userId", Operator.EQ, userId));
		List<CloudUserRole> oldList = this.findListByFilter(filterList, null);
		// 删除之前的所有角色
		cloudUserRoleDao.delete(oldList);

		// 新增现在的人员角色关系
		List<CloudUserRole> list = new ArrayList<CloudUserRole>();
		CloudUserRole userRole = null;
		for (String roleId : roleIdArr) {
			userRole = new CloudUserRole();
			userRole.setUserId(userId);
			userRole.setRoleId(roleId);
			list.add(userRole);
		}
		this.save(list);
	}

	@Override
	public Page<CloudUserRoleDto> findPageBySearchDto(Pageable pageable, CloudUserRoleSearchDto searchDto) {
		return cloudUserRoleDao.findPageBySearchDto(pageable, searchDto);
	}

	@Override
	public List<CloudRole> getRolesByUserId(String userId) {
		if (StringUtils.isBlank(userId)) {
			logger.error("getRolesByUserId(),入参用户Id为空");
			throw new ServiceException("入参用户Id为空");
		}

		return cloudRoleDao.getRolesByUserId(userId);
	}

}
