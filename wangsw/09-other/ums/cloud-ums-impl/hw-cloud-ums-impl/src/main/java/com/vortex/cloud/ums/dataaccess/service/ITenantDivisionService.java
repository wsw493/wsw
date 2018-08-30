package com.vortex.cloud.ums.dataaccess.service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.vortex.cloud.ums.dto.TenantDivisionDto;
import com.vortex.cloud.ums.model.TenantDivision;
import com.vortex.cloud.vfs.data.hibernate.service.PagingAndSortingService;

/**
 * 租户的行政区域维护
 * 
 * @author LiShijun
 *
 */
public interface ITenantDivisionService extends PagingAndSortingService<TenantDivision, String> {
	/**
	 * 保存记录
	 * 
	 * @param dto
	 * @return
	 */
	public TenantDivision save(TenantDivisionDto dto);

	/**
	 * 根据指定的id，获取记录
	 * 
	 * @param id
	 * @return
	 */
	public TenantDivisionDto getById(String id);

	public long deleteByIdArr(String[] ids, boolean casecade);

	TenantDivision update(TenantDivisionDto dto);

	public List<TenantDivision> getAllChildren(TenantDivision parent);

	List<TenantDivision> findTenantDivisionList(TenantDivisionDto tenantDivision);

	/**
	 * 根据ids获取names
	 * 
	 * @param ids
	 *            主键ids
	 * @return id :name的map
	 */
	public Map<String, String> getDivisionNamesByIds(List<String> ids);

	/**
	 * 获取行政区划，并且包含该租户所在的行政区划的节点（isRoot=1）
	 * 
	 * @param tenantDivision
	 * @return
	 */
	List<TenantDivision> findTenantDivisionListWithRoot(TenantDivision tenantDivision);

	/**
	 * 根据names获取ids
	 * 
	 * @param names
	 *            主键names
	 * @param tenantId
	 * @return name :id的map
	 */
	Map<String, String> getDivisionIdsByNames(List<String> names, String tenantId);

	public List<TenantDivision> getChildren(String parentId);

	/**
	 * 获取租户下某一级行政区划的列表
	 * 
	 * @param tenantId
	 * @param level
	 * @return
	 */
	public List<TenantDivision> getByLevel(String tenantId, Integer level) throws Exception;

	/**
	 * 根据租户id和名称列表，返回名称-id的map列表，用于导入
	 * 
	 * @param tenantId
	 * @param names
	 * @return
	 * @throws Exception
	 */
	public LinkedHashMap<String, String> getDivisionsByNames(String tenantId, List<String> names) throws Exception;
}
