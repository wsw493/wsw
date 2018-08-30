package com.vortex.cloud.ums.dataaccess.service;

import java.util.List;
import java.util.Map;

import com.vortex.cloud.ums.dto.CloudOrganizationDto;
import com.vortex.cloud.ums.dto.TenantDeptOrgDto;
import com.vortex.cloud.ums.model.CloudOrganization;
import com.vortex.cloud.vfs.data.hibernate.service.PagingAndSortingService;

/**
 * 组织机构管理
 * 
 * @author LiShijun
 *
 */
public interface ICloudOrganizationService extends PagingAndSortingService<CloudOrganization, String> {
	/**
	 * 查询编号是否存在，存在返回true，传入代码为空返回false
	 * 
	 * @param tenantId
	 * @param code
	 * 
	 * @return
	 */
	public boolean isCodeExisted(String tenantId, String code);

	/**
	 * 保存组织机构
	 * 
	 * @param dto
	 * @return
	 */
	public CloudOrganization save(CloudOrganizationDto dto);

	/**
	 * 根据指定的组织机构id，获取组织机构记录
	 * 
	 * @param id
	 * @return
	 */
	public CloudOrganizationDto getById(String id);

	boolean validateCodeOnUpdate(String tenantId, String id, String newCode);

	/**
	 * 更新组织机构
	 * 
	 * @param dto
	 */
	public void update(CloudOrganizationDto dto);

	/**
	 * 根据id获取机构或者是部门的名字
	 * 
	 * @param id
	 * @return
	 */
	Map<String, Object> getDepartmentOrOrgNameById(String id);

	/**
	 * 根据ids获取机构或者是部门的名字 ，结果是id：name的map集合
	 * 
	 * @param ids
	 * @return 结果是id：name的map集合
	 */
	public Map<String, String> getDepartmentsOrOrgNamesByIds(String[] ids);

	/**
	 * 根据条件获取机构或者是部门的名字 ，结果是list 中是map map是text：xx，id：xx
	 * 
	 * @param paramMap
	 * @return 结果是list 中是map map是text：xx，id：xx
	 */
	public List<Map<String, String>> getDepartmentsOrOrgByCondiction(Map<String, Object> paramMap);

	/**
	 * 删除机构。机构下有人不给删，有有效的子机构不给删
	 * 
	 * @param orgId
	 */
	public void deleteOrg(String orgId);

	/**
	 * 根据部门的名称获取对应的id
	 * 
	 * @param names
	 * @param tenantId
	 * @return
	 */
	public Map<String, String> getDepartmentsOrOrgIdsByName(List<String> names, String tenantId);

	/**
	 * 根据id查找
	 * 
	 * @param rootDeptId
	 * @return
	 */
	public TenantDeptOrgDto getDepartmentOrOrgById(String rootDeptId, List<Integer> beenDeletedFlags);

	/**
	 * 根据名字查找部门或者机构的map
	 * 
	 * @param names
	 * @param tenantId
	 * @return
	 */
	Map<String, Object> getDepartmentsOrOrgByName(List<String> names, String tenantId);

	/**
	 * 根据ID获取部门或者是结构
	 * 
	 * @param ids
	 * @return
	 */
	public List<TenantDeptOrgDto> getDepartmentsOrOrgByIds(String[] ids);

	/**
	 * 根据权限来获取部门列表(不包含半选的节点)
	 * 
	 * @param paramMap
	 * @return
	 */
	public List<TenantDeptOrgDto> loadDepartmentsWithPermission(Map<String, Object> paramMap);

	/**
	 * 根据权限来获取部门列表(包含半选的节点) <br>
	 * paramMap中的tenantId必传，根据userId不一定可以获取到tenantId
	 * 
	 * @param paramMap
	 * @return
	 */
	public List<TenantDeptOrgDto> loadAllDepartmentsWithPermission(Map<String, Object> paramMap);

	/**
	 * 根据自己的权限，获取自己有权限的org和depart
	 * 
	 * @param userId
	 * @return
	 */
	List<String> getCompanyIdsWithPermission(String userId, String tenantId);

	/**
	 * 获取用有权限操作的部门id列表，如果是全部权限，则返回空，因为id列表太长，此种情况可以根据租户id另做比较高效的业务逻辑
	 * 
	 * @param userId
	 * @return
	 */
	public Map<String, Object> getOrgIdsByPermission(String userId);
}
