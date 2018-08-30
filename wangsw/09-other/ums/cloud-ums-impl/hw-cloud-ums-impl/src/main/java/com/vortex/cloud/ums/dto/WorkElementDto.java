package com.vortex.cloud.ums.dto;

import com.vortex.cloud.ums.model.WorkElement;

@SuppressWarnings("serial")
public class WorkElementDto extends WorkElement {
	/* 部门名称 */
	private String departmentName;
	/* 图元类型名称 */
	private String workElementTypeCode;
	/* 图元类型名称 */
	private String workElementTypeName;
	/* 行政区划名称 */
	private String divisionName;
	
	private Integer flag;
	
	
	public String getDivisionName() {
		return divisionName;
	}

	public void setDivisionName(String divisionName) {
		this.divisionName = divisionName;
	}

	public String getDepartmentName() {
		return departmentName;
	}

	public void setDepartmentName(String departmentName) {
		this.departmentName = departmentName;
	}

	public String getWorkElementTypeCode() {
		return workElementTypeCode;
	}

	public void setWorkElementTypeCode(String workElementTypeCode) {
		this.workElementTypeCode = workElementTypeCode;
	}

	public String getWorkElementTypeName() {
		return workElementTypeName;
	}

	public void setWorkElementTypeName(String workElementTypeName) {
		this.workElementTypeName = workElementTypeName;
	}

	public Integer getFlag() {
		return flag;
	}

	public void setFlag(Integer flag) {
		this.flag = flag;
	}

}
