package com.vortex.cloud.cas.server.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * File Name : ExtendUserDetailsService Author : luhao Create Date : 2016/8/11
 * Description : Reviewed By : Reviewed On : Version History : Modified By :
 * Modified Date : Comments : CopyRight : COPYRIGHT(c) www.XXXXX.com All Rights
 * Reserved
 * *******************************************************************************************
 */
public interface ExtendUserDetailsService extends UserDetailsService {

	/**
	 * 根据用户名和租户信息查询用户信息
	 *
	 * @param username
	 * @param password
	 * @param appAuthServiceUrl
	 * @return
	 * @throws UsernameNotFoundException
	 */
	UserDetails loadUserByUsername(String username, String password, String appAuthServiceUrl)
			throws UsernameNotFoundException;

	/**
	 * 根据用户名和租户信息查询用户信息
	 *
	 * @param username
	 * @param password
	 * @param inside
	 * @param appAuthServiceUrl
	 * @return
	 * @throws UsernameNotFoundException
	 */
	UserDetails loadUserByUsername(String username, String password, String ip, Integer inside,
			String appAuthServiceUrl) throws UsernameNotFoundException;

	UserDetails loadUserByUsername(String username, String password, String ip, String appAuthServiceUrl)
			throws UsernameNotFoundException;

}
