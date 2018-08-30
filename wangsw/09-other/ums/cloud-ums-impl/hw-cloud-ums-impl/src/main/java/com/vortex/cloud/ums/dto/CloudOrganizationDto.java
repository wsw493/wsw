package com.vortex.cloud.ums.dto;

import com.vortex.cloud.ums.model.CloudOrganization;

public class CloudOrganizationDto extends CloudOrganization {

	private static final long serialVersionUID = 1L;

	private String parentName; // 上级名称
	private Double latitude; // 纬度
	private Double longitude; // 经度
	private String divisionName; // 行政区划名称

	public String getDivisionName() {
		return divisionName;
	}

	public void setDivisionName(String divisionName) {
		this.divisionName = divisionName;
	}

	public String getParentName() {
		return parentName;
	}

	public void setParentName(String parentName) {
		this.parentName = parentName;
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
