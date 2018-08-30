/*   
 * Copyright (C), 2005-2014, 苏州伏泰信息科技有限公司
 */
package com.vortex.cloud.ums.dataaccess.dao;

import com.vortex.cloud.ums.model.CloudConstant;
import com.vortex.cloud.vfs.data.hibernate.repository.HibernateRepository;


/**
 * @author LiShijun
 * @date 2016年3月29日 上午10:16:17
 * @Description History <author> <time> <desc>
 */
public interface ITenantConstantDao extends HibernateRepository<CloudConstant, String> {
	/**
	 * 根据code获取常量
	 * 
	 * @param constantCode
	 * @param tenantCode
	 * @return
	 */
	CloudConstant getConstantByCode(String constantCode, String tenantCode);

}
