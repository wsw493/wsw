package com.vortex.cloud.ums.dataaccess.dao;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.vortex.cloud.ums.dto.CloudStaffSearchDto;
import com.vortex.cloud.ums.dto.CloudUserDto;
import com.vortex.cloud.ums.dto.IdNameDto;
import com.vortex.cloud.ums.dto.rest.CloudUserRestDto;
import com.vortex.cloud.ums.dto.rest.UserFunctionDto;
import com.vortex.cloud.ums.model.CloudUser;
import com.vortex.cloud.vfs.data.dto.LoginReturnInfoDto;
import com.vortex.cloud.vfs.data.hibernate.repository.HibernateRepository;

/**
 * 用户dao
 * 
 * @author lsm
 * @date 2016年4月1日
 */
public interface ICloudUserDao extends HibernateRepository<CloudUser, String> {
	/**
	 * 根据用户名和租户code得到用户信息
	 * 
	 * 
	 * @param userName
	 * @param tenantId
	 * @return
	 */
	CloudUser getUserByUserName(String tenantId, String userName);

	/**
	 * 根据id和用户名获取用户
	 * 
	 * @param userId
	 * @param userName
	 * @return
	 */
	CloudUser getUserByIdAndName(String userId, String userName);

	/**
	 * 根据人员基本信息id，得到用户信息，没有返回null
	 * 
	 * 
	 * @param staffId
	 * @return
	 */
	CloudUser getUserByStaffId(String staffId);

	/**
	 * 人员登录时查询符合条件的人员
	 * 
	 * @param tenantCode
	 *            租户code
	 * @param userName
	 *            用户名
	 * @param password
	 *            密码
	 * @return
	 */
	public List<LoginReturnInfoDto> getLoginInfo(String tenantCode, String systemCode, String userName);

	/**
	 * 根据用户id，得到用户可以登录的系统列表
	 * 
	 * @param userId
	 *            用户id
	 * @return
	 */
	public List<String> getBusinessSystemCodeList(String userId);

	/**
	 * 得到所有有效的用户id集合
	 * 
	 * @return
	 */
	public List<String> getAllUserIds();

	/**
	 * 根据租户code和用户登录名，得到用户信息
	 * 
	 * @param userName
	 * @param tenantCode
	 * @return
	 */
	public CloudUserRestDto getUserByUserNameAndTenantCode(String userName, String tenantCode);

	/**
	 * 根据用户id，得到所有的功能号
	 * 
	 * @param userId
	 * @return
	 */
	public List<UserFunctionDto> getFunctionsByUserId(String userId);

	/**
	 * 根据系统code和用户名获取登录信息
	 * 
	 * @param systemCode
	 * @param userName
	 * @return
	 */
	List<LoginReturnInfoDto> getLoginInfoBySystemCode(String userName);

	/**
	 * 根据条件来获取指定租户下的用户列表
	 * 
	 * @param paramMap
	 * @return
	 */
	List<CloudUserDto> getUsersByCondiction(Map<String, String> paramMap);

	/**
	 * 根据staffIDs获取用户名
	 * 
	 * @param ids
	 * @return
	 */
	Map<String, String> findUserNamesByStaffIds(List<String> ids);

	/**
	 * 根据staffIds获取users
	 * 
	 * @param deleteList
	 * @return
	 */
	List<CloudUser> getUsersByStaffIds(List<String> deleteList);

	/**
	 * 获取这些公司id下的人员
	 * 
	 * 
	 * @param companyIds
	 * @return
	 */
	List<CloudUserDto> findListByCompanyIds(List<String> companyIds);

	/**
	 * 根据人员账号id列表，得到人员登录账号列表
	 * 
	 * @param ids
	 * @return
	 * @throws Exception
	 */
	List<IdNameDto> getUserNamesByIds(List<String> ids) throws Exception;
	/**
	 * @Title: findIdListByTenantId @Description: 根据租户id获取用户id列表 @return
	 *         List<String> @throws
	 */
	List<String> findIdListByTenantId(String tenantId);

	/**
	 * @Title: findNoTenantUserIdList @Description: 获取无租户的用户id列表 @return
	 *         List<String> @throws
	 */
	List<String> findNoTenantUserIdList();
	
	Page<CloudUserDto> findPageListBySearchDto(Pageable pageable, CloudStaffSearchDto searchDto);
}
