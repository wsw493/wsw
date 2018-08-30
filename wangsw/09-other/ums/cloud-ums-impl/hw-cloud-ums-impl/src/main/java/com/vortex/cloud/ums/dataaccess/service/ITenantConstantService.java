/*   
 * Copyright (C), 2005-2014, 苏州伏泰信息科技有限公司
 */
package com.vortex.cloud.ums.dataaccess.service;

import java.util.List;

import com.vortex.cloud.ums.model.CloudConstant;
import com.vortex.cloud.vfs.data.hibernate.service.PagingAndSortingService;
import com.vortex.cloud.vfs.data.support.SearchFilter;



/**
 * @author LiShijun
 * @date 2016年3月29日 上午10:08:13
 * @Description 租户常量维护 History <author> <time> <desc>
 */
public interface ITenantConstantService extends PagingAndSortingService<CloudConstant, String> {

	/**
	 * 常量记录的名称是否存在于数据库中
	 * 
	 * @param filterList
	 * @return
	 */
	boolean isExistConstantCode(List<SearchFilter> filterList);

	/**
	 * 根据code获取常量
	 * @param constantCode
	 * @param tenantCode
	 * @return
	 */
	CloudConstant getConstantByCode(String constantCode, String tenantCode);
}
