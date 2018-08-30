package com.vortex.cloud.ums.dataaccess.dao;

import java.util.List;

import com.vortex.cloud.ums.dto.CloudPersonalMenuDisplayDto;
import com.vortex.cloud.ums.model.CloudPersonalMenu;
import com.vortex.cloud.vfs.data.hibernate.repository.HibernateRepository;


public interface ICloudPersonalMenuDao extends HibernateRepository<CloudPersonalMenu, String> {

	/**
	 * 加载用户自定义的菜单
	 * @param userId
	 * @return
	 */
	public List<CloudPersonalMenuDisplayDto> getPersonalMenu(String userId);

}
