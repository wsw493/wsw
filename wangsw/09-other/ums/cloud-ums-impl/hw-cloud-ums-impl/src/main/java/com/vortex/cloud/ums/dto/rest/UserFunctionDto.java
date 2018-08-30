package com.vortex.cloud.ums.dto.rest;

public class UserFunctionDto {
	private String functionCode; // 功能号
	private String systemCode; // 系统号

	public String getFunctionCode() {
		return functionCode;
	}

	public void setFunctionCode(String functionCode) {
		this.functionCode = functionCode;
	}

	public String getSystemCode() {
		return systemCode;
	}

	public void setSystemCode(String systemCode) {
		this.systemCode = systemCode;
	}
}
