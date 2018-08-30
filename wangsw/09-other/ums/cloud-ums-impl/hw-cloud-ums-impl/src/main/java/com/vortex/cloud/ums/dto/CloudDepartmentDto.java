package com.vortex.cloud.ums.dto;

import com.vortex.cloud.ums.model.CloudDepartment;

public class CloudDepartmentDto extends CloudDepartment {

	private static final long serialVersionUID = 1L;
	private String depTypeText; // 机构类型描述文本：环卫处，作业公司
	private Double latitude; // 纬度
	private Double longitude; // 经度
	private String divisionName; // 行政区划名称

	public String getDivisionName() {
		return divisionName;
	}

	public void setDivisionName(String divisionName) {
		this.divisionName = divisionName;
	}

	public String getDepTypeText() {
		return depTypeText;
	}

	public void setDepTypeText(String depTypeText) {
		this.depTypeText = depTypeText;
	}

	public Double getLatitude() {
		return latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public Double getLongitude() {
		return longitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}
}
