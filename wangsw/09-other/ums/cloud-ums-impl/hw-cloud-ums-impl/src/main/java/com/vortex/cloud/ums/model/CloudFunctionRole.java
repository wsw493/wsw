package com.vortex.cloud.ums.model;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.vortex.cloud.vfs.data.model.BakDeleteModel;



/**
 * 功能和角色的对应表
 * 
 * @author XY
 *
 */
@Entity
@Table(name = "cloud_function_role")
public class CloudFunctionRole extends BakDeleteModel {
	private static final long serialVersionUID = 1L;
	private String functionId; // 功能id
	private String roleId; // 角色id

	public String getFunctionId() {
		return functionId;
	}

	public void setFunctionId(String functionId) {
		this.functionId = functionId;
	}

	public String getRoleId() {
		return roleId;
	}

	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}
}
