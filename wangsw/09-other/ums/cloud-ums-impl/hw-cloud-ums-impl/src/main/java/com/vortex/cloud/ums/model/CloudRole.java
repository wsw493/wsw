package com.vortex.cloud.ums.model;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.vortex.cloud.vfs.data.model.BakDeleteModel;



@Entity
@Table(name = "cloud_role")
public class CloudRole extends BakDeleteModel {
	private static final long serialVersionUID = 1L;

	private String code; // 编码
	private String name; // 名称
	private String groupId; // 角色组id
	private Integer orderIndex; // 排序号
	private String description; // 描述
	private String roleType; // 角色类型
	public static final String ROLE_TYPE_CUSTOM = "2"; // 自定义角色：业务系统自定义
	public static final String ROLE_TYPE_PRESET = "1"; // 预设角色：系统初始化好的角色，如伏泰管理员、租户管理员、业务系统管理员角色
	private String systemId; // 所属系统id

	public String getSystemId() {
		return systemId;
	}

	public void setSystemId(String systemId) {
		this.systemId = systemId;
	}

	public String getRoleType() {
		return roleType;
	}

	public void setRoleType(String roleType) {
		this.roleType = roleType;
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
