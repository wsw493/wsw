package com.vortex.cloud.ums.dataaccess.service;

import java.util.List;

import com.vortex.cloud.ums.dto.CloudPersonalMenuDisplayDto;
import com.vortex.cloud.ums.model.CloudPersonalMenu;
import com.vortex.cloud.vfs.data.hibernate.service.PagingAndSortingService;

/**
 * 用户菜单service
 * @author ll
 *
 */
public interface ICloudPersonalMenuService extends PagingAndSortingService<CloudPersonalMenu, String> {
	/**
	 * 新增用户菜单
	 * @param userId
	 * @param menuId
	 * @return
	 */
	public CloudPersonalMenu addSinglePersonalMenu(String userId, String menuId, Integer orderIndex);
	/**
	 * 加载用户自定义的菜单
	 * @param userId
	 * @return
	 */
	public List<CloudPersonalMenuDisplayDto> getPersonalMenu(String userId);

}
