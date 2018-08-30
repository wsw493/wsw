/*   
 * Copyright (C), 2005-2014, 苏州伏泰信息科技有限公司
 */
package com.vortex.cloud.ums.enums;

/**
 * @author LiShijun
 * @date 2016年5月19日 上午11:56:46
 * @Description 行政级别
 * History
 * <author>      <time>           <desc> 
 */
public enum CloudDivisionLevelEnum {
	LEVEL_PROVINCE	(1, "省"),
	LEVEL_CITY		(2, "市"),
	LEVEL_DISTRICT	(3, "区/县"),
	LEVEL_VILLAGE	(4, "乡镇/街道"),
	LEVEL_RUSTIC	(5, "居委会")
	;
	
	Integer value;
	String text;
	
	private CloudDivisionLevelEnum(Integer value, String text) {
		this.value = value;
		this.text = text;
	}
	
	public Integer getValue() {
		return value;
	}

	public String getText() {
		return text;
	}

	public static String getTextByValue(Integer value) {
		String text = null;
		
		for (CloudDivisionLevelEnum e : CloudDivisionLevelEnum.values()) {
			if(e.value.equals(value)) {
				text = e.text;
				break;
			}
		}
		
		return text;
	}
}
