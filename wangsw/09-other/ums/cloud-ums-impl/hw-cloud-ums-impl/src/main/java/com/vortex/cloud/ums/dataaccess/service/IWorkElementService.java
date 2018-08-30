package com.vortex.cloud.ums.dataaccess.service;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.vortex.cloud.ums.dto.WorkElementDto;
import com.vortex.cloud.ums.dto.WorkElementPageDto;
import com.vortex.cloud.ums.model.WorkElement;
import com.vortex.cloud.vfs.data.hibernate.service.PagingAndSortingService;


public interface IWorkElementService extends PagingAndSortingService<WorkElement, String> {
	/**
	 * 根据id获取信息
	 * 
	 * @param id
	 * @return
	 */
	WorkElementDto getWorkElementById(String id) throws Exception;

	/**
	 * 保存数据
	 * 
	 * @param dto
	 */
	WorkElementDto saveWorkElement(WorkElementDto dto) throws Exception;

	/**
	 * 更新数据
	 * 
	 * @param dto
	 */
	WorkElementDto updateWorkElement(WorkElementDto dto) throws Exception;

	/**
	 * 删除数据
	 * 
	 * @param id
	 */
	void deleteWorkElement(String id) throws Exception;

	/**
	 * 批量删除数据
	 * 
	 * @param id
	 */
	void deleteWorkElements(List<String> ids) throws Exception;

	/**
	 * 判断是否重复
	 * 
	 * @param id
	 * @param paramName
	 * @param value
	 * @param tenantId
	 * @return
	 * @throws Exception
	 */
	boolean isParamNameExists(String id, String paramName, String value, String tenantId) throws Exception;

	/**
	 * 根据图元类型获取租户下图元列表
	 * 
	 * @param shapeTypes
	 * @param tenantId
	 * @return
	 * @throws Exception
	 */
	List<WorkElement> getWorkElementsByType(String[] shapeTypes, String tenantId) throws Exception;

	/**
	 * model转dto
	 * 
	 * @param list
	 * @return
	 * @throws Exception
	 */
	List<WorkElementDto> transferModelToDto(List<WorkElement> list) throws Exception;
	
	
	/**
	 * 同步图元信息
	 * @param tenantId
	 * @param syncTime
	 * @param pageSize
	 * @param pageNumber
	 * @return
	 */
	public List<WorkElementPageDto> syncWeByPage(String tenantId, long syncTime, Integer pageSize, Integer pageNumber);
	/**同步图元信息(分页)
	 * @param pageable
	 * @param paramMap
	 * @return
	 */
	public Page<WorkElement> syncWorkElementsByPage(Pageable pageable,Map<String, Object> paramMap);
}
