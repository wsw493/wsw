package com.vortex.cloud.ums.dataaccess2.dao;

import java.io.Serializable;
import java.util.List;

import com.vortex.cloud.ums.model.CloudMenu;

public interface ICloudMenuDao2 {
	/**
	 * 根据系统ID获取菜单列表
	 * 
	 * @param systemId
	 * @return
	 */
	List<CloudMenu> getMenuList(String systemId);

	/**
	 * 保存菜单
	 * 
	 * @param bean
	 * @return
	 * @throws Exception
	 */
	public Serializable save(CloudMenu bean) throws Exception;

	/**
	 * 更新菜单
	 * 
	 * @param bean
	 * @throws Exception
	 */
	public void update(CloudMenu bean) throws Exception;

	/**
	 * 根据目标系统id和菜单code，查询目标系统中的菜单信息
	 * 
	 * @param sysid
	 * @param mcode
	 * @return
	 */
	public CloudMenu getMenuBySysidAndMcode(String sysid, String mcode) throws Exception;

	/**
	 * 根据id查询菜单信息
	 * 
	 * @param menuId
	 * @return
	 * @throws Exception
	 */
	public CloudMenu getById(String menuId) throws Exception;
}
