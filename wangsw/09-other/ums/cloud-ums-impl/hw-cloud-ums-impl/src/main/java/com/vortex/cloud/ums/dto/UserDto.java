package com.vortex.cloud.ums.dto;

import com.vortex.cloud.ums.model.CloudUser;

public class UserDto extends CloudUser {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String staffName;

	public String getStaffName() {
		return staffName;
	}

	public void setStaffName(String staffName) {
		this.staffName = staffName;
	}

}
