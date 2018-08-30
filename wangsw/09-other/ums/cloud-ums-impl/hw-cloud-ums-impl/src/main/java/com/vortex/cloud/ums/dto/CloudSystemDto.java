/*   
 * Copyright (C), 2005-2014, 苏州伏泰信息科技有限公司
 */
package com.vortex.cloud.ums.dto;

import com.vortex.cloud.ums.model.CloudSystem;

/**
 * @author LiShijun
 * @date 2016年5月20日 上午10:01:42
 * @Description 云系统
 * History
 * <author>      <time>           <desc> 
 */
public class CloudSystemDto extends CloudSystem {

	private static final long serialVersionUID = 1L;
	
	private String userName;		// Root用户名
	private String password;		// Root用户密码

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}
