package com.vortex.cloud.ums.dataaccess.dao;

import java.util.List;

import com.vortex.cloud.ums.dto.CloudRoleDto;
import com.vortex.cloud.ums.model.CloudRole;
import com.vortex.cloud.vfs.data.hibernate.repository.HibernateRepository;


/**
 * 角色dao
 * 
 * @author lsm
 * @date 2016年4月1日
 */
public interface ICloudRoleDao extends HibernateRepository<CloudRole, String> {

	/**
	 * 判断指定的Code是否存在记录
	 * 
	 * @param code
	 * @param systemId
	 * @return
	 */
	boolean isCodeExists(String code, String systemId);

	/**
	 * 根据id获取记录
	 * 
	 * @param id
	 * @return
	 */
	CloudRoleDto getById(String id);

	/**
	 * 根据角色code和租户code，得到角色信息
	 * 
	 * 
	 * @param roleCode
	 * @param systemCode
	 */
	CloudRole getRoleByCode(String roleCode, String systemCode);

	/**
	 * 根据用户id，得到用户所拥有的角色列表
	 * 
	 * 
	 * @param userId
	 * @return
	 */
	List<CloudRole> getRolesByUserId(String userId);

	/**
	 * 根据系统id和角色code，得到角色
	 * 
	 * @param systemId
	 * @param roleCode
	 * @return
	 */
	public CloudRole getRoleBySystemIdAndRoleCode(String systemId, String roleCode);

	/**
	 * 获取绑定该角色的人员
	 * 
	 * @param tenantId
	 * @param roleCode
	 * @return
	 */
	List<String> getUserIdsByRole(String tenantId, String roleCode);
	
	/**
	 * 返回部门直属的用户ids
	 * @param orgId
	 * @param roleCode
	 * @return
	 */
	public List<String> getUserIdsByRoleAndOrg(String orgId, String roleCode);
}
