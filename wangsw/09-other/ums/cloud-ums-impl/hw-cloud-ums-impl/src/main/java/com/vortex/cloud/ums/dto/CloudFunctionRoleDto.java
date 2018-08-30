package com.vortex.cloud.ums.dto;

import com.vortex.cloud.ums.model.CloudFunctionRole;

public class CloudFunctionRoleDto extends CloudFunctionRole {

	private static final long serialVersionUID = -540522722957088795L;
	/** 功能名 */
	private String functionName;
	/** 功能组名*/
	private String functionGroupName;

	public String getFunctionName() {
		return functionName;
	}

	public void setFunctionName(String functionName) {
		this.functionName = functionName;
	}

	public String getFunctionGroupName() {
		return functionGroupName;
	}

	public void setFunctionGroupName(String functionGroupName) {
		this.functionGroupName = functionGroupName;
	}

}
