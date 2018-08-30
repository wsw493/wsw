package com.vortex.cloud.ums.enums;
/**
 * 同步kafka信息标志
 * @author ll
 *
 */
public enum SyncFlagEnum {
	ADD("ADD", "新增"), UPDATE("UPDATE", "更新"), DELETE("DELETE", "删除");
	
	private String key;
    private String value;

    SyncFlagEnum(String key, String value) {
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
