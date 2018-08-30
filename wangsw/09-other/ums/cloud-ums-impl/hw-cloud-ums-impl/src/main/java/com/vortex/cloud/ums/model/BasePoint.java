package com.vortex.cloud.ums.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.vortex.cloud.vfs.data.model.BaseModel;


/**
 * 点
 * 
 * @author dejunx
 * 
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "cloud_base_point")
public class BasePoint extends BaseModel {
	/** 经度 **/
	private Double longitude;
	/** 纬度 **/
	private Double latitude;

	/** 是否偏转 **/
	private Boolean done;
	/** 偏转经度 **/
	private Double longitudeDone;
	/** 偏转纬度 **/
	private Double latitudeDone;

	/** 排序 **/
	private Integer orderIndex;;

	@Column(name = "f_longitude")
	public Double getLongitude() {
		return longitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	@Column(name = "f_latitude")
	public Double getLatitude() {
		return latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	@Column(name = "f_longitude_done")
	public Double getLongitudeDone() {
		return longitudeDone;
	}

	public void setLongitudeDone(Double longitudeDone) {
		this.longitudeDone = longitudeDone;
	}

	@Column(name = "f_latitude_done")
	public Double getLatitudeDone() {
		return latitudeDone;
	}

	public void setLatitudeDone(Double latitudeDone) {
		this.latitudeDone = latitudeDone;
	}

	@Column(name = "f_order_index")
	public Integer getOrderIndex() {
		return orderIndex;
	}

	public void setOrderIndex(Integer orderIndex) {
		this.orderIndex = orderIndex;
	}

	@Column(name = "f_done")
	public Boolean getDone() {
		return done;
	}

	public void setDone(Boolean done) {
		this.done = done;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
