package com.personal.cloud.data.support;


import java.sql.Timestamp;
import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.Maps;

public class SearchFilter {

	/** 字段操作类型. */
	public static enum Operator {
		EQ, NE, LIKE, NLIKE, LLIKE, RLIKE, GT, LT, GTE, LTE, IN, NIN, BETWEEN, NBETWEEN, NULL, NNULL
	}

	/** 字段数据类型. */
	public static enum FieldType {
		S(String.class), I(Integer.class), F(Float.class), L(Long.class), N(
				Double.class), D(Date.class), B(Boolean.class), T(
				Timestamp.class);

		private Class<?> clazz;

		private FieldType(Class<?> clazz) {
			this.clazz = clazz;
		}

		public Class<?> getValue() {
			return clazz;
		}
	}

	/** 字段名称 **/
	public String fieldName;
	/** 字段类型 **/
	private Class<?> fieldClass = null;
	/** 字段值 **/
	public Object value;
	public Operator operator;

	public SearchFilter(String fieldName, Operator operator, Object value) {
		this.fieldName = fieldName;
		this.value = value;
		this.operator = operator;
		this.fieldClass = String.class;
	}

	public SearchFilter(String fieldName, Class<?> fieldClass,
			Operator operator, Object value) {
		this.fieldName = fieldName;
		this.value = value;
		this.operator = operator;
		this.fieldClass = fieldClass;
	}

	public String getFieldName() {
		return fieldName;
	}

	public Class<?> getFieldClass() {
		return fieldClass;
	}

	public Object getValue() {
		return value;
	}

	public Operator getOperator() {
		return operator;
	}

	

	public static SearchFilter getSearchFilter(String fieldName,
			Class<?> fieldClass, Operator operator, String value) {
		Object keyValue = null;
		switch (operator) {
		case EQ:
		case NE:
		case LIKE:
		case NLIKE:
		case LLIKE:
		case RLIKE:
		case GT:
		case LT:
		case GTE:
		case LTE:
			keyValue = ConvertUtils.convert(value, fieldClass);
			break;
		case IN:
		case NIN:
		case NBETWEEN:
		case BETWEEN:
			String[] str = value.split(",");
			if (fieldClass == Enum.valueOf(FieldType.class, FieldType.D.name())
						.getValue()){
				ConvertUtils.register(new  DateConverter(), fieldClass);
			}
			keyValue = ConvertUtils.convert(str, fieldClass);
			break;
		case NULL:
			keyValue = null;
			break;
		case NNULL:
			keyValue = null;
			break;
		}
		return new SearchFilter(fieldName, fieldClass, operator, keyValue);
	}
	
	/**
	 * searchParams中key的格式为OPERATOR_FIELDNAME_Class
	 */
	public static Map<String, SearchFilter> parse(
			Map<String, Object> searchParams) {
		Map<String, SearchFilter> filters = Maps.newHashMap();

		for (Entry<String, Object> entry : searchParams.entrySet()) {
			// 过滤掉空值
			String key = entry.getKey();
			Class<?> keyType = null;
			String value = (String) entry.getValue();
			if (StringUtils.isBlank(value)) {
				continue;
			}

			// 拆分operator与filedAttribute
			String[] names = StringUtils.split(key, "_");
			if (names.length < 2) {
				throw new IllegalArgumentException(key
						+ " is not a valid search filter name");
			}
			if (names.length == 3) {
				keyType = Enum.valueOf(FieldType.class, names[2]).getValue();
			} else {
				keyType = Enum.valueOf(FieldType.class, FieldType.S.name())
						.getValue();
			}

			String filedName = names[1];
			Operator operator = Operator.valueOf(names[0]);

			// 创建searchFilter
			SearchFilter filter = getSearchFilter(filedName, keyType, operator,
					value);
			filters.put(key, filter);
		}

		return filters;
	}
}
