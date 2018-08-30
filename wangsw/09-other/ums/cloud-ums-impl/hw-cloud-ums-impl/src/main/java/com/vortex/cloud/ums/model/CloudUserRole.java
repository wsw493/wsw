package com.vortex.cloud.ums.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.vortex.cloud.vfs.data.model.BakDeleteModel;



@Entity
@Table(name = "cloud_user_role")
public class CloudUserRole extends BakDeleteModel {
	private static final long serialVersionUID = 1L;

	private String userId; // 用户id
	private String roleId; // 角色id

	@Column(name = "userId", length = 32, nullable = false)
	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	@Column(name = "roleId", length = 32, nullable = false)
	public String getRoleId() {
		return roleId;
	}

	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}
}
