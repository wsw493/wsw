package com.vortex.cloud.ums.dto;
/**
 * 系统功能组装dto
 * @author ll
 *
 */
public class CloudSystemFunctionDto {
	
	private String id;
	
	private String functionCode;
	
	private String functionName;
	
	private String url;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getFunctionCode() {
		return functionCode;
	}

	public void setFunctionCode(String functionCode) {
		this.functionCode = functionCode;
	}

	public String getFunctionName() {
		return functionName;
	}

	public void setFunctionName(String functionName) {
		this.functionName = functionName;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	
	

}
