package com.vortex.cloud.ums.dto;

import com.vortex.cloud.ums.model.WorkElementType;

public class WorkElementTypeDto extends WorkElementType {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1715791319782177460L;
	/**
	 * 所属公司名称
	 */
	private String departmentName;

	public String getDepartmentName() {
		return departmentName;
	}

	public void setDepartmentName(String departmentName) {
		this.departmentName = departmentName;
	}

}
