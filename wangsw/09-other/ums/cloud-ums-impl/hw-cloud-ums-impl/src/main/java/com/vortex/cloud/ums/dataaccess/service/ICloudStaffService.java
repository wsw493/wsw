/*   
 * Copyright (C), 2005-2014, 苏州伏泰信息科技有限公司
 */
package com.vortex.cloud.ums.dataaccess.service;

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
import com.vortex.cloud.vfs.data.hibernate.service.PagingAndSortingService;

/**
 * @author LiShijun
 * @date 2016年4月5日 下午1:20:14
 * @Description 人员信息 History <author> <time> <desc>
 */
public interface ICloudStaffService extends PagingAndSortingService<CloudStaff, String> {
	/**
	 * 查询编号是否存在，存在返回true，传入代码为空返回false
	 * 
	 * @param tenantId
	 * @param code
	 * @return
	 */
	public boolean isCodeExisted(String tenantId, String code);

	/**
	 * 保存
	 * 
	 * @param dto
	 * @return
	 * @throws Exception
	 */
	public CloudStaff save(CloudStaffDto dto) throws Exception;

	/**
	 * 根据指定的id，获取记录
	 * 
	 * @param id
	 * @return
	 */
	public CloudStaffDto getById(String id);

	boolean validateCodeOnUpdate(String tenantId, String id, String newCode);

	/**
	 * 更新
	 * 
	 * @param dto
	 * @throws Exception
	 */
	public void update(CloudStaffDto dto) throws Exception;

	Page<CloudStaffDto> findPageBySearchDto(Pageable pageable, CloudStaffSearchDto searchDto);

	/**
	 * 根据ids获取id和 name的map
	 * 
	 * @param ids
	 * @return
	 */
	public Map<String, String> getStaffNamesByIds(List<String> ids);

	/**
	 * 根据名字获取ids
	 * 
	 * @param names
	 *            staffNames
	 * @param names
	 *            tenantId
	 * @return
	 */
	public Map<String, String> getStaffIdsByNames(List<String> names, String tenantId);

	/**
	 * 根据ids获取id和 信息的map
	 * 
	 * @param ids
	 * @return
	 */
	Map<String, Object> getStaffsByIds(List<String> ids);

	/**
	 * 根据用户ids获取id和 信息的map
	 * 
	 * @param ids
	 * @return
	 */
	Map<String, Object> getStaffsByUserIds(List<String> ids);

	/**
	 * 根据id删除对应的user和staff
	 * 
	 * @param id
	 * @throws Exception
	 */
	public void deleteStaffAndUser(String id) throws Exception;

	/**
	 * 将dto变为model
	 * 
	 * @param list
	 * @return
	 */
	public List<CloudStaffDto> transferModelToDto(List<CloudStaff> list);

	/**
	 * 校验该数据能否被删除
	 * 
	 * @param id
	 * @return
	 */
	boolean canBeDeleted(String id);

	/**
	 * 删除多条数据
	 * 
	 * @param deleteList
	 * @throws Exception
	 */
	public void deletesStaffAndUser(List<String> deleteList) throws Exception;

	/**
	 * 验证社保卡号是否存在
	 * 
	 * @param socialSecurityNo
	 * @return
	 */
	public boolean isSocialSecurityNoExist(String staffId, String socialSecurityNo);

	/**
	 * 身份证号是否存在
	 * 
	 * @param credentialNum
	 * @return
	 */
	public boolean isCredentialNumExist(String staffId, String credentialNum);

	/**
	 * 判断手机号是否存在
	 * 
	 * @param id
	 *            staffId
	 * @param phone
	 *            用户手机
	 * @return
	 */
	public boolean isPhoneExists(String id, String phone);

	/**
	 * 根据参数来获取人员信息，并且带上用户信息
	 * 
	 * @param paramMap
	 *            参数map
	 * @return
	 */
	public List<CloudStaffDto> loadStaffsByFilter(Map<String, Object> paramMap);

	/**
	 * 根据条件和人员权限过滤人员列表
	 * 
	 * @param pageable
	 * @param searchDto
	 * @return
	 */
	public Page<CloudStaffDto> findPageWithPermissionBySearchDto(Pageable pageable, CloudStaffSearchDto searchDto);

	/**
	 * 设置名字首字母
	 */
	public void setNameInitial();

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

	public List<CloudStaffDto> findListBySearchDto(Sort defSort, CloudStaffSearchDto searchDto);

	/**
	 * 同步人员(分页)
	 * 
	 * @param paramMap
	 * @return
	 */
	public Page<CloudStaffDto> syncStaffsByPage(Pageable pageable, Map<String, Object> paramMap);

	/**
	 * @Title: getStaffInfoByUserIds @Description: 根据userIds获取人员基本信息 @return
	 * List<CloudStaffDto> @throws
	 */
	public List<CloudStaffDto> getStaffInfoByUserIds(List<String> ids);

	/**
	 * @Title: getWillManStaffUser @Description: 获取是意愿者，并有用户的人员 @return
	 * List<CloudStaffDto> @throws
	 */
	public List<Object> getWillManStaffUser(String tenantId, String name, String willCheckDivisionId, Integer num);

	/**
	 * 通过code和租户code查找人员
	 * 
	 * @param code
	 * @param tenantCode
	 * @return
	 */
	public CloudStaffRestDto getStaffByCodeAndTenantCode(String code, String tenantCode);

	public Page<CloudStaffDto> findPageListBySearchDto(Pageable pageable, CloudStaffSearchDto searchDto);

	/**
	 * 根据用户权限和条件查询人员列表
	 * 
	 * @param conditions
	 * @return
	 * @throws Exception
	 */
	public List<StaffDto> listStaff(Map<String, String> conditions) throws Exception;
}
