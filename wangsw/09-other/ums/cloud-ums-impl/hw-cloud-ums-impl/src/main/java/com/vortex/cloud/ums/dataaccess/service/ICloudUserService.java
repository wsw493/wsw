/*   
 * Copyright (C), 2005-2014, 苏州伏泰信息科技有限公司
 */
package com.vortex.cloud.ums.dataaccess.service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.vortex.cloud.ums.dto.CloudStaffSearchDto;
import com.vortex.cloud.ums.dto.CloudUserDto;
import com.vortex.cloud.ums.dto.UserDeptDto;
import com.vortex.cloud.ums.dto.UserDto;
import com.vortex.cloud.ums.dto.rest.CloudUserRestDto;
import com.vortex.cloud.ums.model.CloudUser;
import com.vortex.cloud.vfs.data.hibernate.service.PagingAndSortingService;

/**
 * @author LiShijun
 * @date 2016年4月6日 上午11:49:47
 * @Description 用户管理 History <author> <time> <desc>
 */
public interface ICloudUserService extends PagingAndSortingService<CloudUser, String> {

	/**
	 * 查询名称是否存在，存在返回true，传入代码为空返回false
	 * 
	 * @param name
	 * @return
	 */
	public boolean isNameExisted(String name);

	/**
	 * 保存
	 * 
	 * @param dto
	 * @return
	 */
	public CloudUser save(CloudUserDto dto);

	/**
	 * 根据指定的id，获取记录
	 * 
	 * @param id
	 * @return
	 */
	public CloudUserDto getById(String id);

	boolean validateNameOnUpdate(String id, String newName);

	/**
	 * 更新
	 * 
	 * @param dto
	 */
	public void update(CloudUserDto dto);

	/**
	 * 修改用户密码
	 * 
	 * @param userId
	 *            用户id
	 * @param oldPwd
	 *            旧密码
	 * @param newPwd
	 *            新密码
	 * @throws Exception
	 */
	public void changePassword(String userId, String oldPwd, String newPwd) throws Exception;

	/**
	 * 根据租户code和用户登录名，得到用户信息
	 * 
	 * @param userName
	 * @param tenantCode
	 * @return
	 */
	public CloudUserRestDto getUserByUserNameAndTenantCode(String userName, String tenantCode) throws Exception;

	/**
	 * 上传用户头像返回图片URl
	 * 
	 * @param userId
	 *            用户id
	 * @param fileName
	 *            文件名
	 * @param imgStr
	 *            照片string
	 * @return 照片id
	 * @throws Exception
	 */
	public String uploadPhoto(String userId, String fileName, String imgStr) throws Exception;

	/**
	 * 根据条件来获取指定租户下的用户列表
	 * 
	 * @param paramMap
	 * @return
	 */
	public List<CloudUserDto> getUsersByCondiction(Map<String, String> paramMap);

	/**
	 * 获取这些公司id下的人员
	 * 
	 * @param companyIds
	 * @return
	 */
	public List<CloudUserDto> findListByCompanyIds(List<String> companyIds);

	/**
	 * 更新容联账号
	 * 
	 * @param userId
	 * @param rongLianAccount
	 * @throws Exception
	 */
	public void updateRongLianAccount(String userId, String rongLianAccount) throws Exception;

	/**
	 * 更新ImToken
	 * 
	 * @param userId
	 * @param userName
	 * @param imToken
	 * @return
	 */
	public void updateImToken(String userId, String userName, String imToken);

	/**
	 * 根据人员基本信息id，得到用户信息，没有返回null
	 * 
	 * 
	 * @param staffId
	 * @return
	 */
	public CloudUser getUserByStaffId(String staffId);

	/**
	 * 根据id获取用户
	 * 
	 * @param userId
	 * @return
	 */
	public UserDto getUserById(String userId);

	/**
	 * 重置用户密码
	 * 
	 * @param userId
	 */
	public void resetPassword(String userId);

	/**
	 * 查询名称是否存在，存在返回true，传入代码为空返回false
	 * 
	 * @param name
	 * @return
	 */
	public boolean isNameExisted(String name, String staffId);

	/**
	 * 根据用户id，得到此用户所在的部门信息
	 * 
	 * @param userId
	 * @return
	 */
	public UserDeptDto getDeptInfo(String userId);

	/**
	 * 根据人员账号id列表，得到人员登录账号列表
	 * 
	 * @param ids
	 * @return 返回人员id-userName的有序键值对
	 * @throws Exception
	 */
	public LinkedHashMap<String, String> getUserNamesByIds(List<String> ids) throws Exception;

	/**
	 * 获取用户列表
	 * 
	 * @param pageable
	 * @param searchDto
	 * @return
	 */
	public Page<CloudUserDto> findPageListBySearchDto(Pageable pageable, CloudStaffSearchDto searchDto);
}
