package com.vortex.cloud.ums.dataaccess.service;

import java.util.List;
import java.util.Map;

import com.vortex.cloud.ums.dto.CloudDepartmentDto;
import com.vortex.cloud.ums.dto.CloudDeptOrgDto;
import com.vortex.cloud.ums.dto.IdNameDto;
import com.vortex.cloud.ums.dto.TenantDeptOrgDto;
import com.vortex.cloud.ums.dto.TenantDeptOrgPageDto;
import com.vortex.cloud.ums.dto.TreeDto;
import com.vortex.cloud.ums.model.CloudDepartment;
import com.vortex.cloud.vfs.data.hibernate.service.PagingAndSortingService;

/**
 * 部门管理
 * 
 * @author LiShijun
 *
 */
public interface ICloudDepartmentService extends PagingAndSortingService<CloudDepartment, String> {

	/**
	 * 保存部门
	 * 
	 * @param dto
	 * @return
	 */
	public CloudDepartment save(CloudDepartmentDto dto);

	/**
	 * 更新部门
	 * 
	 * @param dto
	 */
	public void update(CloudDepartmentDto dto);

	/**
	 * 查询部门编号是否存在，存在返回true，传入代码为空返回false
	 * 
	 * @param tenantId
	 * @param code
	 * 
	 * @return
	 */
	public boolean isCodeExisted(String tenantId, String code);

	/**
	 * 根据指定的部门id，获取部门记录
	 * 
	 * @param id
	 * @return
	 */
	public CloudDepartmentDto getById(String id);

	boolean validateCodeOnUpdate(String tenantId, String id, String newDepCode);

	/**
	 * 获取指定条件下的单位及其下的机构
	 * 
	 * @param tenantId
	 * @param deptId
	 * @return
	 */
	public List<TenantDeptOrgDto> findDeptOrgList(String tenantId, String deptId, List<Integer> beenDeletedFlags);

	/**
	 * 根据部门code和租户Id，得到部门信息
	 * 
	 * @param departmentCode
	 * @param tenantId
	 * @return
	 */
	CloudDepartment getDepartmentByCode(String departmentCode, String tenantId);

	/**
	 * 根据ids查找
	 * 
	 * @param idsList
	 * @return
	 */
	public List<Map<String, Object>> findDepartmentByIds(List<String> idsList);

	/**
	 * 获取指定条件下的单位
	 * 
	 * @param tenantId
	 * @param deptId
	 * @return
	 */
	public List<TenantDeptOrgDto> findDeptList(String tenantId, String deptId);

	/**
	 * 删除单位。单位中有人不让删，单位有下级org不让删
	 * 
	 * @param departmentId
	 */
	public void deleteDepartment(String departmentId);

	/**
	 * 根据depart或者是org id查找其下部门
	 * 
	 * @param tenantId
	 * @param companyId
	 * @return
	 */
	public List<TenantDeptOrgDto> findDeptOrgListByCompandyId(String tenantId, String companyId, List<Integer> beenDeletedFlags);

	/**
	 * 根据租户id同步部门信息
	 * 
	 * @param tenantId
	 * @param syncTime
	 * @param pageSize
	 * @param pageNumber
	 * @return
	 */
	public List<TenantDeptOrgPageDto> syncDeptByPage(String tenantId, long syncTime, Integer pageSize, Integer pageNumber);

	/**
	 * 根据上级id，得到第一层下级的id和名称列表；如果id为空，直接根据tenantId查询dept表；如果id不为空且，则查询org表
	 * 
	 * @param tenantId
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public List<IdNameDto> findChildren(String tenantId, String id) throws Exception;

	/**
	 * 根据机构id，获取子机构列表；parentId为空或者为-1，则tenantId不得为空，查询租户下所有机构列表；parentId不为空或者-1，则查询其下所有子机构
	 * 
	 * @param tenantId
	 * @param parentId
	 * @return
	 * @throws Exception
	 */
	public List<TreeDto> listDetpByParentId(String tenantId, String parentId) throws Exception;

	/**
	 * 获取部门或机构的列表，带父节点id
	 * 
	 * @param ids
	 * @return
	 * @throws Exception
	 */
	public List<CloudDeptOrgDto> listByIds(List<String> ids) throws Exception;
}
