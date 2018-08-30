package com.vortex.cloud.ums.dataaccess.service;

import java.util.List;

import org.springframework.data.domain.Pageable;

import com.vortex.cloud.ums.dto.CloudFunctionRoleDto;
import com.vortex.cloud.ums.model.CloudFunctionRole;
import com.vortex.cloud.ums.util.orm.Page;
import com.vortex.cloud.vfs.data.hibernate.service.PagingAndSortingService;



public interface ICloudFunctionRoleService extends PagingAndSortingService<CloudFunctionRole, String> {

	/**
	 * 更新角色绑定的系统功能列表
	 * @param roleId
	 * @param systemId
	 * @param functionIdArr
	 */
	void saveFunctionsForRole(String roleId, String systemId, String[] functionIdArr) ;

	/**
	 * 根据id删除一条记录
	 * @param id
	 * @
	 */
	public void deleteFunctionRole(String id) ;

	/**
	 * 查找角色绑定的指定业务系统功能列表的分页
	 * @param roleId
	 * @param systemId
	 * @param pageable
	 * @return
	 */
	public Page<CloudFunctionRoleDto> getPageBySystem(String roleId, String systemId, Pageable pageable) ;
	
	/**
	 * 查找角色绑定的指定业务系统功能列表
	 * @param roleId
	 * @param systemId
	 * @return
	 */
	public List<CloudFunctionRoleDto> getListBySystem(String roleId, String systemId) ;
}
