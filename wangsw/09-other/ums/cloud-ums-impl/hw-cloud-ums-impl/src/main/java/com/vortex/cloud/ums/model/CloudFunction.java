package com.vortex.cloud.ums.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.vortex.cloud.vfs.data.model.BakDeleteModel;



/**
 * 功能
 * 
 * @author XY
 *
 */
@Entity
@Table(name = "cloud_function")
public class CloudFunction extends BakDeleteModel {
	private static final long serialVersionUID = -7261588940906053768L;
	private String code; // 编码
	private String name; // 名称
	private String description; // 描述
	private String groupId; // 组id
	private Integer orderIndex; // 排序号
	private String uri; // 绑定URI
	private String systemId; // 所属系统id
	private String goalSystemId; // 指向的系统id
	private String functionType; // 功能类型 1-主功能，2-辅功能
	private String mainFunctionId; // 主功能id
	
	public static final String FUNCTION_TYPE_MAIN = "1";  // 主功能
	public static final String FUNCTION_TYPE_MINOR = "2"; // 辅功能

	
	@Column(name = "mainFunctionId", nullable = true)
	public String getMainFunctionId() {
		return mainFunctionId;
	}

	public void setMainFunctionId(String mainFunctionId) {
		this.mainFunctionId = mainFunctionId;
	}

	@Column(name = "functionType", nullable = true)
	public String getFunctionType() {
		return functionType;
	}

	public void setFunctionType(String functionType) {
		this.functionType = functionType;
	}

	public String getSystemId() {
		return systemId;
	}

	public void setSystemId(String systemId) {
		this.systemId = systemId;
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

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public Integer getOrderIndex() {
		return orderIndex;
	}

	public void setOrderIndex(Integer orderIndex) {
		this.orderIndex = orderIndex;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public String getGoalSystemId() {
		return goalSystemId;
	}

	public void setGoalSystemId(String goalSystemId) {
		this.goalSystemId = goalSystemId;
	}
}
