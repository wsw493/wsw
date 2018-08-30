package com.vortex.cloud.ums.dataaccess.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vortex.cloud.ums.dataaccess.dao.ICloudPersonalMenuDao;
import com.vortex.cloud.ums.dataaccess.service.ICloudPersonalMenuService;
import com.vortex.cloud.ums.dto.CloudPersonalMenuDisplayDto;
import com.vortex.cloud.ums.model.CloudPersonalMenu;
import com.vortex.cloud.vfs.common.exception.ServiceException;
import com.vortex.cloud.vfs.data.hibernate.repository.HibernateRepository;
import com.vortex.cloud.vfs.data.hibernate.service.SimplePagingAndSortingService;




@Service("cloudPersonalMenuService")
@Transactional
public class CloudPersonalMenuServiceImpl extends SimplePagingAndSortingService<CloudPersonalMenu, String> implements ICloudPersonalMenuService {

	@Resource
	private ICloudPersonalMenuDao cloudPersonalMenuDao;
	
	@Override
	public HibernateRepository<CloudPersonalMenu, String> getDaoImpl() {
		return cloudPersonalMenuDao;
	}

	@Override
	public CloudPersonalMenu addSinglePersonalMenu(String userId, String menuId, Integer orderIndex) {
		if (StringUtils.isBlank(userId)) {
			throw new ServiceException("用户id不能为空！");
		}
		if (StringUtils.isBlank(menuId)) {
			throw new ServiceException("菜单id不能为空！");
		}
		CloudPersonalMenu cpm = new CloudPersonalMenu();
		cpm.setMenuId(menuId);
		cpm.setUserId(userId);
		cpm.setOrderIndex(orderIndex);
		cpm = cloudPersonalMenuDao.save(cpm);
		return cpm;
	}

	@Override
	public List<CloudPersonalMenuDisplayDto> getPersonalMenu(String userId) {
		return cloudPersonalMenuDao.getPersonalMenu(userId);
	}

}
