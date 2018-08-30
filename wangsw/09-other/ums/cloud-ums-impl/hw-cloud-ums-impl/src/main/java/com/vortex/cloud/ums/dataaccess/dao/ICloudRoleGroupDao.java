package com.vortex.cloud.ums.dataaccess.dao;

import com.vortex.cloud.ums.dto.CloudRoleGroupDto;
import com.vortex.cloud.ums.model.CloudRoleGroup;
import com.vortex.cloud.vfs.data.hibernate.repository.HibernateRepository;


/**
 * 角色与组关系dao
 * 
 * @author lsm
 * @date 2016年4月1日
 */
public interface ICloudRoleGroupDao
		extends HibernateRepository<CloudRoleGroup, String> {
	/**
	 * 根据id查找角色组和他所在角色组
	 * @param id
	 * @return
	 */
	CloudRoleGroupDto findRoleGroupAndGroupNameById(String id);

}
