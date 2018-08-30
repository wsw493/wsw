package com.vortex.cloud.ums.dto;


/**
 * 租户配置云系统管理页面的搜索条件
 * 
 * @author lishijun
 *
 */
public class CloudSysSearchDto extends CloudSystemDto {
	private static final long serialVersionUID = 1L;
	
	private String tenantId;	// 租户id

	public String getTenantId() {
		return tenantId;
	}

	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}
}
