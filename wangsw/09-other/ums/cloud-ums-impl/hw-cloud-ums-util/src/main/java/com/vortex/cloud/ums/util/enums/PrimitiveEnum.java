package com.vortex.cloud.ums.util.enums;
/**
 * 图元类型
 * @author XY
 *
 */
public enum PrimitiveEnum {
	POINT("POINT", "point"), LINE("LINE", "line"), RECTANGLE("RECTANGLE", "rectangle"), CIRCLE("CIRCLE", "circle"), AREA("AREA", "area");
	String key;
	String value;

	private PrimitiveEnum(String key, String value) {
		this.key = key;
		this.value = value;
	}

	public String getKey() {
		return key;
	}

	public String getValue() {
		return value;
	}
}
