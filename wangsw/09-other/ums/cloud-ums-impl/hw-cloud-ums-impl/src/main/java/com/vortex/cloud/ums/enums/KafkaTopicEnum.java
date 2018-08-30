package com.vortex.cloud.ums.enums;
/**
 * topic枚举
 * @author ll
 *
 */
public enum KafkaTopicEnum {
	UMS_STAFF_SYNC("UMS_STAFF_SYNC", "人员同步"), UMS_USER_SYNC("UMS_USER_SYNC", "用户同步"), UMS_DEPARTMENT_SYNC("UMS_DEPARTMENT_SYNC", "部门同步"),
	UMS_ORGAN_SYNC("UMS_ORGAN_SYNC", "组织同步"), UMS_TENANT_DIVISION_SYNC("UMS_TENANT_DIVISION_SYNC", "租户行政区划同步");
	
	private String key;
    private String value;

    KafkaTopicEnum(String key, String value) {
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
