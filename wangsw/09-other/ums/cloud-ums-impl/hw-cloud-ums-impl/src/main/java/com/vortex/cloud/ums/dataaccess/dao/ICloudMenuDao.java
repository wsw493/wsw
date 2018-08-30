/*   
 * Copyright (C), 2005-2014, 苏州伏泰信息科技有限公司
 */
package com.vortex.cloud.ums.dataaccess.dao;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.vortex.cloud.ums.dto.CloudMenuDto;
import com.vortex.cloud.ums.dto.CloudMenuSearchDto;
import com.vortex.cloud.ums.dto.MenuTreeDto;
import com.vortex.cloud.ums.model.CloudMenu;
import com.vortex.cloud.vfs.data.hibernate.repository.HibernateRepository;


/**
 * @author LiShijun
 * @date 2016年5月23日 上午10:21:38
 * @Description DAO History <author> <time> <desc>
 */
public interface ICloudMenuDao extends HibernateRepository<CloudMenu, String> {

	/**
	 * 获取指定系统下的菜单列表分页
	 * 
	 * @param pageable
	 * @param searchDto
	 * @return
	 */
	Page<CloudMenuDto> findPage(Pageable pageable, CloudMenuSearchDto searchDto);

	/**
	 * 获取指定系统下的菜单列表
	 * 
	 * @param systemId
	 * @return
	 */
	List<CloudMenu> getMenuList(String systemId);

	/**
	 * 得到用户在某个系统下的菜单列表
	 * 
	 * @param systemCode
	 * @param userId
	 * @return
	 */
	public List<MenuTreeDto> getUserMenuList(String systemCode, String userId);

	/**
	 * 根据id得到菜单信息，得到树枝节点
	 * 
	 * @param id
	 * @return
	 */
	public MenuTreeDto getMenuTreeDtoById(String id);

	/**
	 * 根据功能id，得到所有菜单
	 * 
	 * @param functionId
	 * @return
	 */
	public List<CloudMenu> getMenusByFunctionId(String functionId);

	/**
	 * 复制菜单的时候，根据父节点得到菜单
	 * 
	 * @param systemId
	 * @param parentId
	 * @return
	 */
	public List<CloudMenu> getMenusByParentId(String systemId, String parentId);
}
