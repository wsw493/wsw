package com.vortex.cloud.ums.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.vortex.cloud.vfs.data.model.BakDeleteModel;



/**
 * 人员和部门关系表
 * 
 * @author XY
 *
 */
@Entity
@Table(name = "cloud_user_dep_relation")
public class CloudUserDepRelation extends BakDeleteModel {
	private static final long serialVersionUID = 1L;

	private String userId; // 人员id
	private String departmentId; // 部门或者机构id

	@Column(name = "userId", length = 32, nullable = false)
	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	@Column(name = "departmentId", length = 32, nullable = false)
	public String getDepartmentId() {
		return departmentId;
	}

	public void setDepartmentId(String departmentId) {
		this.departmentId = departmentId;
	}
}
