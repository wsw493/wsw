package com.vortex.cloud.ums.dataaccess.dao;

import java.util.List;

import com.vortex.cloud.ums.dto.CloudFunctionDto;
import com.vortex.cloud.ums.dto.CloudTreeDto;
import com.vortex.cloud.ums.dto.android.CloudFunctionAndroidDto;
import com.vortex.cloud.ums.model.CloudFunction;
import com.vortex.cloud.vfs.data.hibernate.repository.HibernateRepository;


/**
 * 功能dao
 * 
 * @author lsm
 * @date 2016年4月1日
 */
public interface ICloudFunctionDao extends HibernateRepository<CloudFunction, String> {

	/**
	 * 判断指定Code是否存在相应记录
	 * 
	 * @param systemId
	 * @param code
	 * @return
	 */
	boolean isCodeExistsForSystem(String systemId, String code);

	/**
	 * 根据功能code和租户code和系统code，得到功能信息
	 * 
	 * 
	 * @param functionCode
	 * @param tenantId
	 * @param systemCode
	 * @return
	 */
	CloudFunction getFunctionByCode(String functionCode, String tenantId, String systemCode);

	/**
	 * 根据角色id，得到角色上面所有功能id的列表
	 * 
	 * 
	 * @param roleId
	 * @return
	 */
	List<String> getFunctionsByRoleId(String roleId);

	/**
	 * 根据id获取功能组
	 * 
	 * @param id
	 * @return
	 */
	CloudFunctionDto getFunctionById(String id);

	/**
	 * 传入业务系统id和人员id，返回该人员是否拥有该功能
	 * 
	 * @param userId
	 *            用户id
	 * @param systemId
	 *            业务系统
	 * @param functionId
	 *            功能id
	 * @return
	 * 
	 */
	boolean hasFunction(String userId, String systemId, String functionId);

	/**
	 * 根据人员id，查询该人员在指定业务系统中所拥有的功能列表(已过期)
	 * 
	 * @param userId
	 *            用户id
	 * @param systemId
	 *            业务系统Id
	 * @return
	 * 
	 */
	List<String> getFunctionList(String userId, String systemId);

	/**
	 * 根据角色id，得到function列表
	 * 
	 * @param roleId
	 * @return
	 */
	List<CloudFunction> getFunctionListByRoleId(String roleId);

	/**
	 * 得到一个人拥有的所有功能号列表
	 * 
	 * @param userId
	 * @return
	 */
	List<String> getAllFunctions(String userId);

	/**
	 * 根据功能组id，得到下面的所有功能列表
	 * 
	 * @param groupId
	 * @return
	 */
	List<CloudFunction> getByGroupId(String groupId);

	/**
	 * 获取用户在systemCode下的功能
	 * 
	 * 
	 * @param userId
	 * @param systemCode
	 * @return
	 */
	Object getFunctionsByUsreIdAndSystem(String userId, String systemCode);
	/**
	 * 根据功能IDS获取其完整URL
	 * @param functionIds
	 * @return
	 */
	
	Object getFunctionsByIds(String functionIds);
	/**
	 * 
	 * @param userId
	 * @return
	 */
	
	List<CloudTreeDto> getCloudFunctionByUserId(String userId);

	/**
	 * 根据系统id和功能code，得到功能信息
	 * 
	 * @param sysId
	 * @param funcCode
	 * @return
	 */
	CloudFunction getByCode(String sysId, String funcCode);

	/**
	 * 根据主功能id，得到辅功能列表
	 * 
	 * @param mainFunctionId
	 * @return
	 */
	List<CloudFunctionDto> listByMainId(String mainFunctionId);
	
	/**
	* @Title: getFunctionsByUserId
	* @Description: 根据用户id获取功能码，功能id
	* @return List<CloudFunctionAndroidDto> 
	* @throws
	*/
	public List<CloudFunctionAndroidDto> getFunctionsByUserId(String userId);
}
