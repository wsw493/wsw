package com.vortex.cloud.ums.dataaccess.service;

import java.util.List;

import com.vortex.cloud.ums.dto.CloudRoleGroupDto;
import com.vortex.cloud.ums.model.CloudRoleGroup;
import com.vortex.cloud.vfs.data.hibernate.service.PagingAndSortingService;


/**
 * 角色组service
 * 
 * @author lsm
 * @date 2016年4月5日
 */
public interface ICloudRoleGroupService extends PagingAndSortingService<CloudRoleGroup, String> {
	/**
	 * 根据id删除所有的
	 * 
	 * @param id
	 * 
	 */
	public abstract void deleteAllById(final String id);

	/**
	 * 获取所有的子节点id
	 * 
	 * @param id
	 * @return
	 * 
	 */
	public abstract List<String> getAllChildrenId(String id);

	/**
	 * 获取所有的父节点
	 * 
	 * @param id
	 * @return
	 * 
	 */
	public abstract List<String> getAllParentId(String id);

	/**
	 * 保存角色组信息
	 * 
	 * @param dto
	 * 
	 */
	public abstract void saveRoleGroup(CloudRoleGroupDto dto);

	/**
	 * 根据id查找记录
	 * 
	 * @param id
	 *            主键
	 * @return
	 * 
	 */
	public abstract CloudRoleGroupDto findRoleGroupById(String id);

	/**
	 * 更新角色组
	 * 
	 * @param dto
	 * 
	 */
	public abstract void updateRoleGroup(CloudRoleGroupDto dto);

	/**
	 * 删除角色组
	 * 
	 * @param rgId
	 * 
	 */
	public abstract void deleteRoleGroup(String rgId);

	/**
	 * 根据id查找角色组信息和所属角色组名字
	 * 
	 * @param id
	 * 
	 */
	public abstract CloudRoleGroupDto findRoleGroupAndGroupNameById(String id);

	/**
	 * 批量删除
	 * 
	 * @param canBeDeletes
	 */
	public abstract void deletes(List<String> canBeDeletes);
}
