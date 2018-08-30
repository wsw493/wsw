package com.vortex.cloud.ums.dto.rest;
/**
 * 参数值dto
 * @author ll
 *
 */
public class PramSettingRestDto {
	
	
	private String id;
	
	private String parmCode; // 代码值
	
	private String parmName; // 代码显示名称

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getParmCode() {
		return parmCode;
	}

	public void setParmCode(String parmCode) {
		this.parmCode = parmCode;
	}

	public String getParmName() {
		return parmName;
	}

	public void setParmName(String parmName) {
		this.parmName = parmName;
	}
}
