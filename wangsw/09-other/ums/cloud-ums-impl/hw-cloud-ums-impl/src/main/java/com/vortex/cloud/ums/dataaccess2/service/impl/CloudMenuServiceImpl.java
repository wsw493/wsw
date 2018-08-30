/*   
 * Copyright (C), 2005-2014, 苏州伏泰信息科技有限公司
 */
package com.vortex.cloud.ums.dataaccess2.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.vortex.cloud.ums.dataaccess2.dao.ICloudMenuDao2;
import com.vortex.cloud.ums.dataaccess2.service.ICloudMenuService;
import com.vortex.cloud.ums.model.CloudMenu;

/**
 * @author LiShijun
 * @date 2016年5月23日 上午10:34:27
 * @Description History <author> <time> <desc>
 */
@Service("cloudMenuService2")
@Transactional
public class CloudMenuServiceImpl implements ICloudMenuService {


	@Resource(name = "cloudMenuDao2")
	private ICloudMenuDao2 cloudMenuDao;

	@Override
	public List<CloudMenu> getMenuList(String systemId) {
		if (StringUtils.isEmpty(systemId)) {
			return Lists.newArrayList();
		}
		return cloudMenuDao.getMenuList(systemId);
	}

}
