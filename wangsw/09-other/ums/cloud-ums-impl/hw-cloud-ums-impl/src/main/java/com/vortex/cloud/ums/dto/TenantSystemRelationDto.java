package com.vortex.cloud.ums.dto;

import com.vortex.cloud.ums.model.TenantSystemRelation;

/**
 * 租户上面开启的云系统列表
 * 
 * @author lishijun
 *
 */
public class TenantSystemRelationDto extends TenantSystemRelation {

	private static final long serialVersionUID = 1L;

	private String code; // 系统编号
	private String name; // 名称

	private Integer status; // 系统状态0:可用

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

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

}
