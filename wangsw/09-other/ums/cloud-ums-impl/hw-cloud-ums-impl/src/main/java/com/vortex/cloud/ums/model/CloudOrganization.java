package com.vortex.cloud.ums.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.vortex.cloud.vfs.data.model.BakDeleteModel;

/**
 * 环卫处和作业公司的机构表。第一层存放在department表上面
 * 
 * @author XY
 *
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "cloud_organization")
public class CloudOrganization extends BakDeleteModel {
	/** 必要字段 */
	private String tenantId; // 租户id
	private String orgName; // 机构名称
	private String orgCode; // 机构代码
	private String departmentId; // 部门表id
	private String parentId; // 上级id。对应第一层机构，值为departmentId

	private String head; // 负责人
	private String headMobile; // 负责人电话
	private String description; // 描述
	private String lngLats; // 经纬度
	private String address; // 地址
	private String email; // 邮箱

	// 内置编号：用于层级数据结构的构造（如树）
	private String nodeCode;

	// 子层所有数据记录数，和己编号配置生成子编号
	private Integer childSerialNumer;
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

	public String getOrgName() {
		return orgName;
	}

	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}

	public String getOrgCode() {
		return orgCode;
	}

	public void setOrgCode(String orgCode) {
		this.orgCode = orgCode;
	}

	public String getDepartmentId() {
		return departmentId;
	}

	public void setDepartmentId(String departmentId) {
		this.departmentId = departmentId;
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
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

	@Column(name = "nodeCode", nullable = false)
	public String getNodeCode() {
		return nodeCode;
	}

	public void setNodeCode(String nodeCode) {
		this.nodeCode = nodeCode;
	}

	@Column(name = "childSerialNumber", nullable = false)
	public Integer getChildSerialNumer() {
		return childSerialNumer;
	}

	public void setChildSerialNumer(Integer childSerialNumer) {
		this.childSerialNumer = childSerialNumer;
	}

	public Integer getOrderIndex() {
		return orderIndex;
	}

	public void setOrderIndex(Integer orderIndex) {
		this.orderIndex = orderIndex;
	}

}
