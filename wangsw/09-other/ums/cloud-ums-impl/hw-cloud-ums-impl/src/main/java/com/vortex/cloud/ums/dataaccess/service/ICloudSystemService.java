package com.vortex.cloud.ums.dataaccess.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.vortex.cloud.ums.dto.CloudSystemDto;
import com.vortex.cloud.ums.dto.CloudTreeDto;
import com.vortex.cloud.ums.dto.SystemSearchDto;
import com.vortex.cloud.ums.model.CloudSystem;
import com.vortex.cloud.vfs.data.hibernate.service.PagingAndSortingService;


public interface ICloudSystemService extends PagingAndSortingService<CloudSystem, String> {

	/**
	 * 增加租户的业务系统
	 * 
	 * @param dto
	 */
	public CloudSystem saveCloudSystem(CloudSystemDto dto);

	/**
	 * 根据id获取租户的业务系统
	 * 
	 * @param id
	 * @return
	 */
	public CloudSystemDto getCloudSystemById(String id);

	/**
	 * 根据Code获取租户的业务系统
	 * 
	 * @param tenantId
	 * @param code
	 * @return
	 */
	public CloudSystemDto getCloudSystemByCode(String tenantId, String code);

	/**
	 * 更新租户的业务系统
	 * 
	 * @param dto
	 */
	public CloudSystem updateCloudSystem(CloudSystemDto dto);

	/**
	 * 获取指定条件的业务系统列表
	 * 
	 * @param pageable
	 * @param searchDto
	 */
	public Page<CloudSystemDto> getPageOfBusinessSys(Pageable pageable, SystemSearchDto searchDto);

	/**
	 * 根据tenantId获取系统列表
	 * 
	 * @param tenantId
	 * @return
	 */
	public List<CloudSystem> getCloudSystems(String tenantId);
	/**
	 * 根据用户id获取系统列表
	 * @param userId
	 * @return
	 */
	public List<CloudTreeDto> getCloudSystemsByUserId(String userId);
	
	/**
	 * 根据用户id，得到用户有菜单的系统的列表；list中的每个字符串为“系统code||系统名称”
	 * @param userId
	 * @return
	 */
	public List<String> getSystemList(String userId);
	/**
	* @Title: getCloudSystemByRoleCode
	* @Description: 根据角色获取系统列表
	* @return List<CloudSystemDto> 
	* @throws
	*/
	public List<CloudSystemDto> getCloudSystemByRoleCode(String roleCode);
}
