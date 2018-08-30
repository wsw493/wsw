package com.vortex.cloud.ums.enums;

import com.vortex.cloud.vfs.common.lang.StringUtil;

/**
 * 用工类型枚举
 * 
 * @author SonHo
 *
 */
public enum WorkTypeEnum {
	PATROL("Patrol", "巡检"),
	CLEAN("Clean", "保洁");

	private final String key;
	private final String value;

	private WorkTypeEnum(String key, String value) {
		this.key = key;
		this.value = value;
	}

	public String getKey() {
		return key;
	}

	public String getValue() {
		return value;
	}

	public static String getValueByKey(String key) {
		for (WorkTypeEnum e : WorkTypeEnum.values()) {
			if (e.getKey().equals(key)) {
				return e.getValue();
			}
		}
		return null;
	}

	public static String getKeyByValue(String value) {
		if (!StringUtil.isNullOrEmpty(value)) {
			for (WorkTypeEnum e : WorkTypeEnum.values()) {
				if (e.getValue().equals(value)) {
					return e.getKey();
				}
			}
		}
		return null;
	}
}
