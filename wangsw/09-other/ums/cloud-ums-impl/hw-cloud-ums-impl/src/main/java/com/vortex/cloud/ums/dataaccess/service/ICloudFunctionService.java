package com.vortex.cloud.ums.dataaccess.service;

import java.util.List;

import com.vortex.cloud.ums.dto.CloudFunctionDto;
import com.vortex.cloud.ums.dto.CloudTreeDto;
import com.vortex.cloud.ums.model.CloudFunction;
import com.vortex.cloud.vfs.data.hibernate.service.PagingAndSortingService;


public interface ICloudFunctionService extends PagingAndSortingService<CloudFunction, String> {
	/**
	 * 保存功能
	 * 
	 * @param dto
	 * @return @
	 */
	public String save(CloudFunctionDto dto);

	/**
	 * 更新功能
	 * 
	 * @param dto @
	 */
	public void update(CloudFunctionDto dto);

	/**
	 * 删除功能
	 * 
	 * @param functionId @
	 */
	public void deleteFunction(String functionId);

	/**
	 * 判断code是否重复，在同一个系统下面，不能重复
	 * 
	 * @param systemId
	 * @param functionId
	 * @param newCode
	 * @return
	 */
	public boolean isCodeExistsForSystem(String systemId, String functionId, String newCode);

	/**
	 * 根据id，得到功能信息，转存入dto中返回
	 * 
	 * @param functionId
	 * @return @
	 */
	public CloudFunctionDto getFunctionInfoById(String functionId);

	/**
	 * 删除指定的功能列表
	 * 
	 * @param idList
	 */
	public void deletes(List<String> idList);

	/**
	 * 获取用户在systemCode下的功能
	 * 
	 * @param userId 用户ID
	 * @param systemCode 系统code
	 * @return
	 */
	public Object getFunctionsByUsreIdAndSystem(String userId, String systemCode);
	/**
	 * 根据功能IDS获取其完整URL
	 * @param functionIds
	 * @return
	 */
	public Object getFunctionsByIds(String functionIds);
	
	/**
	 * 根据用户id获取功能list
	 * @param userId
	 * @return
	 */
	public List<CloudTreeDto> getTreeData(String userId);
	
	/**
	 * 根据用户id获取功能
	 * @param userId
	 * @return
	 */
	public List<CloudTreeDto> getCloudFunctionByUserId(String userId);
}
