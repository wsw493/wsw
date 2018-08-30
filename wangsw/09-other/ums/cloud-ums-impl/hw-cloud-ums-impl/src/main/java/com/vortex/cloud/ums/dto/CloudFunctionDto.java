package com.vortex.cloud.ums.dto;

import com.vortex.cloud.ums.model.CloudFunction;

public class CloudFunctionDto
	extends CloudFunction {
	
	private static final long serialVersionUID = -2562461829176572889L;
	private String groupName;	// 功能组名称
	private String goalSystemName; // 指向的系统name
	private String mainFunctionName;//主功能Name

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	
	public String getGoalSystemName() {
		return goalSystemName;
	}

	public void setGoalSystemName(String goalSystemName) {
		this.goalSystemName = goalSystemName;
	}

	public String getMainFunctionName() {
		return mainFunctionName;
	}

	public void setMainFunctionName(String mainFunctionName) {
		this.mainFunctionName = mainFunctionName;
	}
	
	
}
