package com.vortex.cloud.ums.util.utils;

import org.apache.commons.lang3.StringUtils;

import com.vortex.cloud.vfs.common.lang.PropertiesHelper;

/**
 * @ClassName: Constants
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author nj
 * @date 2016年1月19日 上午11:18:38
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
public class PropertyUtils {
	// 参数配置文件
	public static final String PROPERTIES_FILE_NAME = "cloud.properties";

	/**
	 * @Title: getPropertyValue
	 * @Description: 返回配置文件参数常量
	 * @return String
	 * @throws
	 */
	public static String getPropertyValue(String propertyName) {
		if (StringUtils.isBlank(propertyName)) {
			return null;
		}
		return PropertiesHelper.getInstance(PROPERTIES_FILE_NAME).getProperty(PROPERTIES_FILE_NAME, propertyName);
	}

}