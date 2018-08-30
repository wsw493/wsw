package com.vortex.cloud.ums.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.vortex.cloud.vfs.data.model.BakDeleteModel;



/**
 * 代码类型表
 * 
 * @author XY
 *
 */
@SuppressWarnings("all")
@Entity
@Table(name = "cloud_parameter_type")
public class PramType extends BakDeleteModel {
	private String typeCode; // 类型代码
	private String typeName; // 类型名称
	private String groupId; // 代码组id
	private Integer orderIndex; // 排序号
	private String description; // 描述

	@Column(name = "typeCode", length = 32, nullable = false)
	public String getTypeCode() {
		return typeCode;
	}

	public void setTypeCode(String typeCode) {
		this.typeCode = typeCode;
	}

	@Column(name = "typeName", length = 255, nullable = false)
	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	@Column(name = "groupId", length = 32, nullable = false)
	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	@Column(name = "orderIndex")
	public Integer getOrderIndex() {
		return orderIndex;
	}

	public void setOrderIndex(Integer orderIndex) {
		this.orderIndex = orderIndex;
	}

	@Column(name = "description", length = 255, nullable = true)
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
