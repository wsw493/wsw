package com.vortex.cloud.ums.dto;

public class CodeDto {
	private String code; // 代码值
	private String name; // 代码显示名称

	public CodeDto() {
		super();
	}

	public CodeDto(String code, String name) {
		super();
		this.code = code;
		this.name = name;
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
}
