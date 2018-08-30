package com.vortex.cloud.ums.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.vortex.cloud.vfs.data.model.BakDeleteModel;



/**
 * 功能组
 * 
 * @author XY
 *
 */
@Entity
@Table(name = "cloud_function_group")
public class CloudFunctionGroup extends BakDeleteModel {
	private static final long serialVersionUID = 1L;
	private String code; // 编码
	private String name; // 名称
	private String description; // 描述
	private String parentId; // 父节点id
	private Integer orderIndex; // 顺序号
	private String systemId; // 所属系统id

	// 内置编号：用于层级数据结构的构造（如树）
	private String nodeCode;

	// 子层所有数据记录数，和己编号配置生成子编号
	private Integer childSerialNumber;

	public static final String ROOT_ID = "-1";

	public Integer getOrderIndex() {
		return orderIndex;
	}

	public void setOrderIndex(Integer orderIndex) {
		this.orderIndex = orderIndex;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	@Column(name = "nodeCode", nullable = false)
	public String getNodeCode() {
		return nodeCode;
	}

	public void setNodeCode(String nodeCode) {
		this.nodeCode = nodeCode;
	}

	

	public String getSystemId() {
		return systemId;
	}

	public void setSystemId(String systemId) {
		this.systemId = systemId;
	}

	@Column(name = "childSerialNumber", nullable = false)
	public Integer getChildSerialNumber() {
		return childSerialNumber;
	}

	public void setChildSerialNumber(Integer childSerialNumber) {
		this.childSerialNumber = childSerialNumber;
	}
}
