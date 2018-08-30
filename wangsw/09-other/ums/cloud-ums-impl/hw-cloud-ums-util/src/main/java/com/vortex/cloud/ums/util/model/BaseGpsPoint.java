package com.vortex.cloud.ums.util.model;

/**
 * 点
 * 
 * @author XY
 *
 */
public class BaseGpsPoint {
	private double latitude; // 纬度
	private double longitude; // 经度

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
}
