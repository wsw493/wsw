package com.vortex.cloud.ums.dataaccess.dao;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.vortex.cloud.ums.dto.CloudStaffDto;
import com.vortex.cloud.ums.dto.CloudStaffPageDto;
import com.vortex.cloud.ums.dto.CloudStaffSearchDto;
import com.vortex.cloud.ums.dto.StaffDto;
import com.vortex.cloud.ums.dto.rest.CloudStaffRestDto;
import com.vortex.cloud.ums.model.CloudStaff;
import com.vortex.cloud.vfs.data.hibernate.repository.HibernateRepository;

/**
 * 个人信息dao
 * 
 * @author lsm
 * @date 2016年4月1日
 */
public interface ICloudStaffDao extends HibernateRepository<CloudStaff, String> {

	Page<CloudStaffDto> findPageBySearchDto(Pageable pageable, CloudStaffSearchDto searchDto);

	/**
	 * 根据code查找用户信息
	 * 
	 * @param staffCode
	 * @param tenantId
	 * @return
	 */
	CloudStaff getStaffByCode(String staffCode, String tenantId);

	/**
	 * 根据用户id，查询人员基本信息
	 * 
	 * @param userId
	 * @return
	 */
	CloudStaff getStaffByUserId(String userId);

	/**
	 * 根据公司id获取用户资料
	 * 
	 * @param departmentId
	 * @return
	 */
	List<String> getStaffsByDepartmentId(String departmentId);

	/**
	 * 根据用户ids获取staff
	 * 
	 * @param ids
	 * @return
	 */
	public List<CloudStaffDto> getStaffsByUserIds(List<String> ids);

	/**
	 * 根据部门id获取用户资料
	 * 
	 * @param orgId
	 * @return
	 */
	List<String> getStaffsByOrgId(String orgId);

	/**
	 * 根据公司id获取其下所有用户资料
	 * 
	 * @param departmentId
	 * @return
	 */
	List<String> getAllStaffsByDepartmentId(String departmentId);

	/**
	 * 根据机构的节点code 获取其下所有的用户
	 * 
	 * @param nodeCode
	 * @return
	 */
	List<String> getAllStaffsByOrgNodeCode(String nodeCode);

	/**
	 * 获取租户下的人员列表
	 * 
	 * @return
	 */
	List<Map<String, Object>> getStaffListByUserRegisterType(CloudStaffSearchDto searchDto);

	/**
	 * 根据名字查找列表
	 * 
	 * @param names
	 * @param tenantId
	 * @return
	 */
	List<CloudStaff> getStaffIdsByNames(List<String> names, String tenantId);

	/**
	 * 根据参数来获取人员信息，并且带上用户信息
	 * 
	 * @param paramMap
	 * @return
	 */
	List<CloudStaffDto> loadStaffsByFilter(Map<String, Object> paramMap);

	/**
	 * 同步人员信息
	 * 
	 * @param tenantId
	 * @param syncTime
	 * @param pageSize
	 * @param pageNumber
	 * @return
	 */
	public List<CloudStaffPageDto> syncStaffByPage(String tenantId, long syncTime, Integer pageSize, Integer pageNumber);

	/**
	 * 
	 * @param tenantId
	 * @param isDeleted
	 * @return
	 */
	public List<CloudStaffPageDto> findAllStaffByPage(String tenantId, Integer isDeleted);

	/**
	 * 根据id获取用户
	 * 
	 * @param id
	 * @return
	 */
	CloudStaffDto getById(String id);

	List<CloudStaffDto> findListBySearchDto(Sort defSort, CloudStaffSearchDto searchDto);

	/**
	 * 同步人员(分页)
	 * 
	 * @param paramMap
	 * @return
	 */
	public Page<CloudStaffDto> syncStaffsByPage(Pageable pageable, Map<String, Object> paramMap);

	/**
	 * @Title: getStaffInfoByUserIds @Description: 根据userIds获取人员基本信息 @return
	 *         List<CloudStaffDto> @throws
	 */
	public List<CloudStaffDto> getStaffInfoByUserIds(List<String> ids);

	/**
	 * @Title: getWillManStaffUser @Description: 获取是意愿者，并有用户的人员 @return
	 *         List<CloudStaffDto> @throws
	 */
	public List<CloudStaffDto> getWillManStaffUser(String tenantId, String name, String willCheckDivisionId);

	/**
	 * 通过code和租户code查找人员
	 * 
	 * @param code
	 * @param tenantCode
	 * @return
	 */
	public CloudStaffRestDto getStaffByCodeAndTenantCode(String code, String tenantCode);

	/**
	 * 查询机构列表下的人员信息，如果传租户就查租户下所有，否则查部门列表下人员
	 * 
	 * @param name
	 * @param phone
	 * @param orgIds
	 * @param tenantId
	 * @return
	 * @throws Exception
	 */
	public List<StaffDto> listStaff(String name, String phone, List<String> orgIds, String tenantId, String containManager) throws Exception;
}
