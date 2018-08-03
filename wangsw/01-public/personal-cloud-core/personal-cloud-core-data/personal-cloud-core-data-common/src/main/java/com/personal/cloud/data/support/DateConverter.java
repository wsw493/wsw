package com.personal.cloud.data.support;



import java.text.SimpleDateFormat;
import org.apache.commons.beanutils.Converter;
import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;
import java.text.ParseException;

/**
 * 
 * 日期转换对象，使用该转换器，在BaseForm当中做一下注册，系统自动地帮助字符的日期表示转换为java.util.Date对象.
 * 
 * @author zizz. Create Time：2006-9-11 19:11:47.
 */
public class DateConverter implements Converter {
	/**
	 * 日期格式化对象.
	 */
	private static SimpleDateFormat df = new SimpleDateFormat();

	/**
	 * 模式集合.
	 */
	private static Set<String> patterns = new HashSet<String>();
	// 注册一下日期的转换格式
	static {
		DateConverter.patterns.add("yyyy-MM-dd HH:mm:ss");
	}

	/**
	 * 日期转换器.
	 * 
	 * @param type
	 *            Class
	 * @param value
	 *            Object return Date Object.
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public Object convert(Class type, Object value) {
		if (value == null) {
			return null;
		} else if (value instanceof String) {
			Object dateObj = null;
			Iterator it = patterns.iterator();
			while (it.hasNext()) {
				try {
					String pattern = (String) it.next();
					df.applyPattern(pattern);
					dateObj = df.parse((String) value);
					break;
				} catch (ParseException ex) {
					// do iterator continue
				}
			}
			return dateObj;
		} else {
			return null;
		}
	}
}