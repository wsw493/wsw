package com.vortex.cloud.ums.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.vortex.cloud.vfs.data.model.BakDeleteModel;



/**
 * 代码目录树
 * 
 * @author XY
 *
 */
@SuppressWarnings("all")
@Entity
@Table(name = "cloud_parameter_group")
public class PramGroup extends BakDeleteModel {
	private String groupCode; // 代码
	private String groupName; // 名称
	private String description; // 描述
	private Integer orderIndex; // 排序号
	private String parentId; // 父节点id
	
	// 内置编号：用于层级数据结构的构造（如树）
	private String nodeCode;
	
	// 子层所有数据记录数，和己编号配置生成子编号
	private Integer childSerialNumer;

	@Column(name = "groupCode", length = 32, nullable = false)
	public String getGroupCode() {
		return groupCode;
	}

	public void setGroupCode(String groupCode) {
		this.groupCode = groupCode;
	}

	@Column(name = "groupName", length = 32, nullable = false)
	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	@Column(name = "description", length = 255, nullable = true)
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Column(name = "orderIndex")
	public Integer getOrderIndex() {
		return orderIndex;
	}

	public void setOrderIndex(Integer orderIndex) {
		this.orderIndex = orderIndex;
	}

	@Column(name = "parentId", length = 32, nullable = false)
	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}
	
	@Column(name = "nodeCode", nullable = false, unique = true)
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
}