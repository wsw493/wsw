package com.vortex.cloud.ums.dataaccess.service;

import java.util.List;

import com.vortex.cloud.ums.dto.CloudFunctionGroupDto;
import com.vortex.cloud.ums.model.CloudFunctionGroup;
import com.vortex.cloud.vfs.data.hibernate.service.PagingAndSortingService;


/**
 * 角色组service
 * 
 * @author lsm
 * @date 2016年4月5日
 */
public interface ICloudFunctionGroupService extends PagingAndSortingService<CloudFunctionGroup, String> {
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
	 * 保存功能组信息
	 * 
	 * @param dto
	 * 
	 */
	public abstract void save(CloudFunctionGroupDto dto);

	/**
	 * 根据id查找记录
	 * 
	 * @param id
	 *            主键
	 * @return
	 * 
	 */
	public abstract CloudFunctionGroupDto findFunctionGroupById(String id);

	/**
	 * 保存功能组
	 * 
	 * @param dto
	 * 
	 */
	public abstract void update(CloudFunctionGroupDto dto);

	/**
	 * 
	 * @param fgId
	 *            根据id删除功能组
	 * 
	 */
	public abstract void deleteFunctionGroup(String fgId);

	/**
	 * 查找功能组和他的所在功能组
	 * 
	 * @param id
	 * @return
	 * 
	 */
	public abstract CloudFunctionGroupDto findFunctionGroupAndGroupNameById(String id);

	/**
	 * 批量删除
	 * 
	 * @param canBeDeletes
	 */
	public abstract void deletes(List<String> canBeDeletes);
}
