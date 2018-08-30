package com.vortex.cloud.ums.dataaccess.dao;

import java.util.List;

import com.vortex.cloud.ums.dto.CloudSystemDto;
import com.vortex.cloud.ums.dto.CloudTreeDto;
import com.vortex.cloud.ums.model.CloudSystem;
import com.vortex.cloud.vfs.data.hibernate.repository.HibernateRepository;


/**
 * 
 * 云平台上面的云系统 dao
 * 
 * @author lsm
 * 
 *
 */
public interface ICloudSystemDao extends HibernateRepository<CloudSystem, String> {
	/**
	 * 根据系统code得到系统
	 * 
	 * @param code
	 * @return
	 */
	public CloudSystem getByCode(String code);
	
	/**
	 * 根据用户id，得到用户有菜单的系统的列表；list中的每个字符串为“系统code||系统名称”
	 * @param userId
	 * @return
	 */
	public List<String> getSystemList(String userId);
	/**
	 * 根据用户id获取 系统list
	 * @param userId
	 * @return
	 */
	public List<CloudTreeDto> getCloudSystemsByUserId(String userId);
	/**
	* @Title: getCloudSystemByRoleCode
	* @Description: 根据角色获取系统列表
	* @return List<CloudSystemDto> 
	* @throws
	*/
	public List<CloudSystemDto> getCloudSystemByRoleCode(String roleCode);
}
