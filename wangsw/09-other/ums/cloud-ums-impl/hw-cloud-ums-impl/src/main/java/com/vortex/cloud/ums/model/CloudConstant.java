package com.vortex.cloud.ums.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.vortex.cloud.vfs.data.model.BakDeleteModel;



/**
 * 云系统常量表
 * 
 * @author XY
 *
 */
@Entity
@Table(name = "cloud_constant")
public class CloudConstant extends BakDeleteModel {
	private static final long serialVersionUID = 6044165004557919518L;
	private String constantValue; // 值
	private String constantCode; // 常量代码
	private String constantDescription; // 描述
	private String tenantId; // 租户id

	@Column(name = "constantValue", length = 64, nullable = false)
	public String getConstantValue() {
		return constantValue;
	}

	public void setConstantValue(String constantValue) {
		this.constantValue = constantValue;
	}

	@Column(name = "constantCode", length = 64, nullable = false)
	public String getConstantCode() {
		return constantCode;
	}

	public void setConstantCode(String constantCode) {
		this.constantCode = constantCode;
	}

	@Column(name = "constantDescription", length = 128, nullable = false)
	public String getConstantDescription() {
		return constantDescription;
	}

	public void setConstantDescription(String constantDescription) {
		this.constantDescription = constantDescription;
	}

	@Column(name = "tenantId", length = 32, nullable = false)
	public String getTenantId() {
		return tenantId;
	}

	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}
}
