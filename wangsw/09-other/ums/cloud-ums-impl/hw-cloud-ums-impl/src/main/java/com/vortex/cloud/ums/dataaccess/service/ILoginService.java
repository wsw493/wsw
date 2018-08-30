package com.vortex.cloud.ums.dataaccess.service;

import java.util.Map;

import  com.vortex.cloud.vfs.data.dto.LoginReturnInfoDto;

public interface ILoginService {
	/**
	 * 用户登录
	 * 
	 * @param tenantCode
	 * @param systemCode
	 * @param userName
	 * @param password
	 * @return
	 */
	public LoginReturnInfoDto login(String tenantCode, String systemCode, String userName, String password, String ip);

	/**
	 * 用户登录,并且更新他的推送id
	 * 
	 * @param tenantCode
	 * @param systemCode
	 * @param userName
	 * @param password
	 * @param mobilePushMsgId
	 * @return
	 */
	LoginReturnInfoDto login(String tenantCode, String systemCode, String userName, String password,
			String mobilePushMsgId, String ip);

	/**
	 * 登录
	 * 
	 * @param account
	 * @param password
	 * @return
	 * @throws Exception 
	 */
	public Map<String, String> login(String account, String password, String ip) throws Exception;

}
