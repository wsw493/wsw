/*   
 * Copyright (C), 2005-2014, 苏州伏泰信息科技有限公司
 */
package com.vortex.cloud.ums.dataaccess.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.vortex.cloud.ums.dto.CloudUserRoleDto;
import com.vortex.cloud.ums.dto.CloudUserRoleSearchDto;
import com.vortex.cloud.ums.model.CloudRole;
import com.vortex.cloud.ums.model.CloudUserRole;
import com.vortex.cloud.vfs.data.hibernate.service.PagingAndSortingService;


/**
 * @author LiShijun
 * @date 2016年4月7日 上午11:09:41
 * @Description 用户配置角色
 * History
 * <author>      <time>           <desc> 
 */
public interface ICloudUserRoleService extends PagingAndSortingService<CloudUserRole, String> {

	/**
	 * 为指定用户配置角色
	 * 
	 * @param userId
	 * @param roleIdArr
	 */
	void addRoles(String userId, String[] roleIdArr);
	
	Page<CloudUserRoleDto> findPageBySearchDto(Pageable pageable, CloudUserRoleSearchDto searchDto);
	
	/**
	 * 根据用户id，得到用户所拥有的角色列表
	 * 
	 * @param userId
	 * @return
	 */
	List<CloudRole> getRolesByUserId(String userId);
	
}
