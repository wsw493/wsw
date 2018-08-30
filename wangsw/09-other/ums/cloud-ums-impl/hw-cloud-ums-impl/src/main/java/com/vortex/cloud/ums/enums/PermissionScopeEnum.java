package com.vortex.cloud.ums.enums;

public enum PermissionScopeEnum {
	ALL("1", "全部"), // 全部
	NONE("2", "无"), // 无
	CUSTOM("3", "自定义"), // 自定义
	SELF("4", "本级"), // 本级
	SELF_AND_DOWN("5", "本级及以下"); // 本级及以下

	String key;
	String value;

	private PermissionScopeEnum(String key, String value) {
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

		for (PermissionScopeEnum e : PermissionScopeEnum.values()) {
			if (e.key.equals(key)) {
				val = e.value;
				break;
			}
		}

		return val;
	}
}
