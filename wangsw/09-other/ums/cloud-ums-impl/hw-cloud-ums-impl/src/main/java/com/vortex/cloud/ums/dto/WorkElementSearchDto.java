package com.vortex.cloud.ums.dto;

/**
 * 查询workElementType列表的参数dto
 * 
 * @author lsm
 *
 */
public class WorkElementSearchDto {
	/** 租户id */
	private String tenantId;
	/** 图形类型 ，格式xx,xx */
	private String shapeTypes;

	public String getTenantId() {
		return tenantId;
	}

	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}

	public String getShapeTypes() {
		return shapeTypes;
	}

	public void setShapeTypes(String shapeTypes) {
		this.shapeTypes = shapeTypes;
	}

}
