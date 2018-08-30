/*   
 * Copyright (C), 2005-2014, 苏州伏泰信息科技有限公司
 */
package com.vortex.cloud.ums.dataaccess2.service;

import java.util.List;

import com.vortex.cloud.ums.model.CloudMenu;

/**
 * @author LiShijun
 * @date 2016年5月23日 上午10:33:34
 * @Description History <author> <time> <desc>
 */
public interface ICloudMenuService {
	/**
	 * 根据系统ID获取菜单列表
	 * 
	 * @param systemId
	 * @return
	 */
	List<CloudMenu> getMenuList(String systemId);
}
