package com.vortex.cloud.ums.dto;

import com.vortex.cloud.ums.model.CloudRoleGroup;

public class CloudRoleGroupDto
		extends CloudRoleGroup {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1491960737443537955L;
	/** 所在角色组名称 */
	private String groupName;

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
}
