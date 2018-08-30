package com.vortex.cloud.ums.support;

public class RegexConstant {

	// 数字、字母、下划线验证
	public static final String NUMBER_CHARACTER_UNDERLINE_RULE = "^\\w+$";
	// 中文、数字、字母、下划线验证
	public static final String CHINESE_NUMBER_CHARACTER_UNDERLINE_RULE = "^[a-zA-Z0-9_\u4e00-\u9fa5]+$";
	// 手机号码验证（13，15，17，18开头的11位数字）
	public static final String MOBILE_RULE = "^(((13[0-9]{1})|(15[0-9]{1})|(17[0-9]{1})|(18[0-9]{1}))+\\d{8})$";
	
}
