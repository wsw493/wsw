package com.vortex.cloud.ums.enums;

import com.vortex.cloud.vfs.common.lang.StringUtil;

/**
 * 外形枚举
 * 
 * @author SonHo
 *
 */
public enum SharpTypeEnum {

	POINT("point", "点"), LINE("line", "多折线"), POLYGON("polygon", "多边形"), RECTANGLE("rectangle", "矩形"), CIRCLE("circle", "圆形");

	private final String key;
	private final String value;

	private SharpTypeEnum(String key, String value) {
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

		for (SharpTypeEnum e : SharpTypeEnum.values()) {
			if (e.getKey().equals(key)) {
				return e.getValue();
			}
		}
		return null;
	}

	public static String getKeyByValue(String value) {
		if (!StringUtil.isNullOrEmpty(value)) {
			for (SharpTypeEnum e : SharpTypeEnum.values()) {
				if (e.getValue().equals(value)) {
					return e.getKey();
				}
			}
		}
		return null;
	}
}
