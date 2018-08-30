package com.vortex.cloud.ums.dataaccess.service;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.vortex.cloud.ums.dto.WorkElementTypeDto;
import com.vortex.cloud.ums.dto.WorkElementTypeSearchDto;
import com.vortex.cloud.ums.model.WorkElementType;
import com.vortex.cloud.vfs.data.hibernate.service.PagingAndSortingService;
import com.vortex.cloud.vfs.data.support.SearchFilter;

/**
 * 图元类型服务
 * 
 * @author lusm
 *
 */
public interface IWorkElementTypeService extends PagingAndSortingService<WorkElementType, String> {

	/**
	 * 保存图元类型
	 * 
	 * @param dto
	 */
	public void saveWorkElementType(WorkElementTypeDto dto) throws Exception;

	/**
	 * 判断code是否已经存在（在该部门中）
	 * 
	 * @param id
	 *            id
	 * @param tenantId
	 *            租户id
	 * @param value
	 *            参数值
	 * @return
	 */
	public boolean isCodeExists(String id, String value, String tenantId) throws Exception;

	/**
	 * 判断图元类型 参数是否已存在
	 * 
	 * @param id
	 * @param param
	 * @param paramValue
	 * @param tenantId
	 * @return
	 * @throws Exception
	 */
	boolean isParamExists(String id, String param, String paramValue, String tenantId) throws Exception;

	/**
	 * 根据id获取实体
	 * 
	 * @param id
	 * @return
	 */
	public WorkElementTypeDto findWorkElementTypeDtoById(String id) throws Exception;

	/**
	 * 更新数据
	 * 
	 * @param dto
	 */
	public void updateWorkElementType(WorkElementTypeDto dto) throws Exception;

	/**
	 * 判断是否能够删除
	 * 
	 * @param id
	 * @return
	 */
	public boolean canBeDelete(String id);

	/**
	 * 根据id删除数据
	 * 
	 * @param id
	 * @return
	 */
	public void deleteWorkElementType(String id) throws Exception;

	/**
	 * 根据ids删除数据
	 * 
	 * @param ids
	 */
	public void deleteWorkElementTypes(List<String> ids) throws Exception;

	/**
	 * 根据 租户id查找列表
	 * 
	 * @param tenantId
	 * @return
	 */
	public List<WorkElementType> findListByCondition(WorkElementTypeSearchDto dto);

	/**
	 * 手持端接口处理查询返回数据
	 * 
	 * @param workElementTypes
	 * @return
	 */
	List<Map<String, Object>> processData(List<WorkElementType> workElementTypes);

	/**
	 * 根据ids获取图元类型name
	 * 
	 * @param typeIds
	 */
	public Map<String, String> getWorkElementTypeNamesByIds(List<String> typeIds);

	/**
	 * 根据人员权限获取图元类型
	 * 
	 * @param pageable
	 * @param searchFilters
	 * @param userId
	 * @return
	 */
	public Page<WorkElementType> findPageByPermission(Pageable pageable, List<SearchFilter> searchFilters, String userId, String tenantId);

}
