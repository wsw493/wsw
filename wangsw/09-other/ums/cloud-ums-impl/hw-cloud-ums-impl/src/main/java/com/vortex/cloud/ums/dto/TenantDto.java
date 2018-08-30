package com.vortex.cloud.ums.dto;

import com.vortex.cloud.ums.model.Tenant;

/**
 * 保存和load租户信息时和前台交互的dto
 * 
 * @author lishijun
 *
 */
public class TenantDto extends Tenant {
	private static final long serialVersionUID = 1L;
	private String divisionName;	// 根行政区划名称
	private String userName;		// 租户Root用户名
	private String password;		// 租户Root用户密码
	
	public String getDivisionName() {
		return divisionName;
	}

	public void setDivisionName(String divisionName) {
		this.divisionName = divisionName;
	}

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
