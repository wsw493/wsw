package com.vortex.cloud.ums.enums;

/**
 * 登录错误
 * 
 * @author XY
 *
 */
public enum LoginErrEnum {
	LOGIN_ERR_NOT_FOUND("1", "根据用户名，未找到有效的用户！"), LOGIN_ERR_FOUND_MUTI("2", "根据用户名和密码，找到多个有效的用户！"), LOGIN_ERR_PASSWORD("3", "密码错误！"), LOGIN_ERR_SYSTEM("4", "系统错误！");

	String key;
	String value;

	private LoginErrEnum(String key, String value) {
		this.key = key;
		this.value = value;
	}

	public String getKey() {
		return key;
	}

	public String getValue() {
		return value;
	}
}
