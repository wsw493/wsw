package com.vortex.cloud.ums.dataaccess.dao;

import java.util.List;

import org.springframework.data.domain.Pageable;

import com.vortex.cloud.ums.dto.CloudFunctionRoleDto;
import com.vortex.cloud.ums.model.CloudFunctionRole;
import com.vortex.cloud.ums.util.orm.Page;
import com.vortex.cloud.vfs.data.hibernate.repository.HibernateRepository;

/**
 * 功能和角色的对应dao
 * 
 * @author lsm
 * @date 2016年4月1日
 */
public interface ICloudFunctionRoleDao extends HibernateRepository<CloudFunctionRole, String> {

	/**
	 * 查找角色下绑定的云系统功能列表的分页
	 * 
	 * @param roleId
	 * @param systemId
	 * @param pageable
	 * @return
	 */
	Page<CloudFunctionRoleDto> getPageBySystem(String roleId, String systemId, Pageable pageable);

	/**
	 * 查找角色下绑定的云系统功能列表
	 * 
	 * @param roleId
	 * @param systemId
	 * @return
	 */
	List<CloudFunctionRoleDto> getListBySystem(String roleId, String systemId);
}
