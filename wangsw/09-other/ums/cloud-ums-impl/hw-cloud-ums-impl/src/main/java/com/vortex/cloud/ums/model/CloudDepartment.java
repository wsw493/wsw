package com.vortex.cloud.ums.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.vortex.cloud.vfs.data.model.BakDeleteModel;

/**
 * 云平台上面的部门表。只存放环卫处和作业公司；底下的机构，不存放在本表
 * 
 * @author XY
 *
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "cloud_department")
public class CloudDepartment extends BakDeleteModel {
	/** 必要字段 */
	private String tenantId; // 租户id
	private String depName; // 部门名称
	private String depCode; // 部门编码
	private String depType; // 机构类型：配置在参数表中

	private String head; // 负责人
	private String headMobile; // 负责人电话
	private String description; // 描述
	private String lngLats; // 经纬度
	private String address; // 地址
	private String email; // 邮箱
	/**
	 * 排序号
	 */
	private Integer orderIndex;

	private String divisionId; // 行政区划id

	@Column(name = "divisionId", length = 32)
	public String getDivisionId() {
		return divisionId;
	}

	public void setDivisionId(String divisionId) {
		this.divisionId = divisionId;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getLngLats() {
		return lngLats;
	}

	public void setLngLats(String lngLats) {
		this.lngLats = lngLats;
	}

	public String getTenantId() {
		return tenantId;
	}

	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}

	public String getDepName() {
		return depName;
	}

	public void setDepName(String depName) {
		this.depName = depName;
	}

	public String getDepCode() {
		return depCode;
	}

	public void setDepCode(String depCode) {
		this.depCode = depCode;
	}

	public String getDepType() {
		return depType;
	}

	public void setDepType(String depType) {
		this.depType = depType;
	}

	public String getHead() {
		return head;
	}

	public void setHead(String head) {
		this.head = head;
	}

	public String getHeadMobile() {
		return headMobile;
	}

	public void setHeadMobile(String headMobile) {
		this.headMobile = headMobile;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Integer getOrderIndex() {
		return orderIndex;
	}

	public void setOrderIndex(Integer orderIndex) {
		this.orderIndex = orderIndex;
	}

}
