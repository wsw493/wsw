package com.vortex.cloud.ums.dto;

import com.vortex.cloud.ums.model.CloudFunctionGroup;

public class CloudFunctionGroupDto extends CloudFunctionGroup {

	private static final long serialVersionUID = 3342351417812503975L;

	private String cloudSystemName;

	private String groupName; // 所在功能组

	public String getCloudSystemName() {
		return cloudSystemName;
	}

	public void setCloudSystemName(String cloudSystemName) {
		this.cloudSystemName = cloudSystemName;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
}
