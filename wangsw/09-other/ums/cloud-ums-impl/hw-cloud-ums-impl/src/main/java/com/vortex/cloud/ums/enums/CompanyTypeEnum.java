package com.vortex.cloud.ums.enums;

public enum CompanyTypeEnum {

	ORG("org", "部门"), // org

	DEPART("department", "公司"); // department
	String key;
	String value;

	private CompanyTypeEnum(String key, String value) {
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

		for (CompanyTypeEnum e : CompanyTypeEnum.values()) {
			if (e.key.equals(key)) {
				val = e.value;
				break;
			}
		}

		return val;
	}
}
