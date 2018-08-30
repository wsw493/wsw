package com.vortex.cloud.ums.dataaccess.dao;

import java.util.List;

import com.vortex.cloud.ums.dto.CloudDeptOrgDto;
import com.vortex.cloud.ums.dto.IdNameDto;
import com.vortex.cloud.ums.dto.TenantDeptOrgDto;
import com.vortex.cloud.ums.dto.TenantDeptOrgPageDto;
import com.vortex.cloud.ums.dto.TreeDto;
import com.vortex.cloud.ums.model.CloudDepartment;
import com.vortex.cloud.vfs.data.hibernate.repository.HibernateRepository;

/**
 * 部门dao
 * 
 * @author LiShijun
 *
 */
public interface ICloudDepartmentDao extends HibernateRepository<CloudDepartment, String> {

	List<TenantDeptOrgDto> findDeptOrgList(String tenantId, String deptId, List<Integer> beenDeletedFlags);

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
	 * 根据部门code和租户code，得到部门信息
	 * 
	 * 
	 * @param departmentCode
	 * @param tenantId
	 * @return
	 */
	CloudDepartment getDepartmentByCode(String departmentCode, String tenantId);

	List<TenantDeptOrgDto> findDeptList(String tenantId, String deptId);

	/**
	 * 部门中是否有有效的人员
	 * 
	 * @param departmentId
	 * @return
	 */
	public boolean hasStaff(String departmentId);

	/**
	 * 部门中是否有有效的机构
	 * 
	 * @param departmentId
	 * @return
	 */
	public boolean hasOrg(String departmentId);

	/**
	 * 根据id查找
	 * 
	 * @param id
	 * @param beenDeletedFlags
	 * @return
	 */
	public CloudDepartment findById(String id, List<Integer> beenDeletedFlags);

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
	 * 得到租户下的部门和机构的列表
	 * 
	 * @param tenantId
	 * @return
	 * @throws Exception
	 */
	public List<TreeDto> listByTenantId(String tenantId) throws Exception;

	/**
	 * 根据id得到部门机构信息，带父节点id
	 * 
	 * @param ids
	 * @return
	 * @throws Exception
	 */
	public List<CloudDeptOrgDto> listByIds(List<String> ids) throws Exception;
}
