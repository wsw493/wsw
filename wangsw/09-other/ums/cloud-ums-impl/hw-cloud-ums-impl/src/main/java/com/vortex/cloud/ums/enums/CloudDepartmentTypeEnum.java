package com.vortex.cloud.ums.enums;

/**
 * 部门类型,现在已经将部门类型加入到参数中，**但是ORG这个还在使用**
 * 
 * @author LiShijun
 *
 */
public enum CloudDepartmentTypeEnum {
	HW("1", "环卫处"), // 环卫处
	CP("2", "作业公司"), // 作业公司
	ORG("3", "组织"); // 组织机构

	String key;
	String value;

	private CloudDepartmentTypeEnum(String key, String value) {
		this.key = key;
		this.value = value;
	}

	public String getKey() {
		return key;
	}

	public String getValue() {
		return value;
	}

	public static String getTextByValue(String key) {
		String val = null;

		for (CloudDepartmentTypeEnum e : CloudDepartmentTypeEnum.values()) {
			if (e.key.equals(key)) {
				val = e.value;
				break;
			}
		}

		return val;
	}
}
