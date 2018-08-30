package com.vortex.cloud.ums.util.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.vortex.cloud.vfs.common.lang.StringUtil;

public class QueryUtil {
	public static final String PAGEING_ORACLE = "oracle";

	public static final String PAGEING_MYSQL = "mysql";

	private static String createQuerySql(final String sql,
			final Object... values) {
		if (values != null) {
			StringBuilder s = new StringBuilder("");
			char[] c = sql.toCharArray();
			int count = 0;
			int flag = 0;
			for (int i = 0; i < c.length; i++) {
				if (c[i] == '?') {
					s.append(sql.substring(flag, i)).append(
							values[count].toString());
					flag = i;
					count++;
					flag++;
				}
			}
			return s.toString();
		}
		return null;
	}

	public static String getSqlByConditions(Map<String, Object> conditions) {
		StringBuilder sql = new StringBuilder(" where 1=1");

		List<Object> paramsObject = new ArrayList<Object>();

		Set<String> entrys = conditions.keySet();
		for (Iterator<String> iterator = entrys.iterator(); iterator.hasNext();) {
			String propertyName = iterator.next();
			Object value = conditions.get(propertyName);
			if (value instanceof Integer || value instanceof Long
					|| value instanceof Double || value instanceof Float
					|| value instanceof Boolean) {
				if (value != null) {
					sql.append(" and ").append(propertyName).append(" = ?");
					paramsObject.add(value);
				}

			} else if (value instanceof String) {
				if (!StringUtil.isNullOrEmpty((String) value)) {
					sql.append(" and ").append(propertyName).append(" like ?");
					paramsObject.add("'%" + ((String) value).trim() + "%'");
				}
			} else if (value instanceof Object[]) {
				Object[] dateStrings = (Object[]) value;
				if (dateStrings.length != 2) {
					throw new RuntimeException("请按照格式输入开始时间和结束时间");
				}
				if (!dateStrings[0].equals("")) {
					sql.append(" and ").append(propertyName).append(" >= ?");
					paramsObject.add("'" + dateStrings[0] + "'");
				}
				if (!dateStrings[1].equals("")) {
					sql.append(" and ").append(propertyName).append(" <= ?");
					paramsObject.add("'" + dateStrings[1] + "'");
				}
			} else if (value instanceof Date) {
				if (value != null) {
					sql.append(" and ").append(propertyName).append(" = ?");
					paramsObject.add("'" + value + "'");
				}

			} else {
				// 除了这几种类型外，报错
				throw new RuntimeException("还有其他类型，请重新配置");
			}
		}
		return createQuerySql(sql.toString(), paramsObject.toArray());
	}

	public static void main(String[] args) {
		System.out.println(getSqlOr("code", "1,2", Integer.class));
	}

	/**
	 * 得到像( code='1' or code = '2')
	 * 
	 * @param property
	 *            列名
	 * @param value
	 *            1,2
	 * @param type
	 *            值的类型 Integer.class
	 * @return
	 */
	public static String getSqlOr(String property, String value, Class<?> type) {
		String typeName = type.getSimpleName();
		StringBuilder result = new StringBuilder();
		result.append("( ");
		String[] strs = value.split(",");
		StringBuilder sb = new StringBuilder("");
		for (String string : strs) {
			if (!StringUtil.isNullOrEmpty(string)) {
				if ("Integer".equals(typeName) || "Long".equals(typeName)
						|| "Double".equals(typeName)
						|| "Float".equals(typeName)
						|| "Boolean".equals(typeName)
						|| "Date".equals(typeName)) {
					sb.append(property).append(" = ").append(string)
							.append(" or ");

				} else if ("String".equals(typeName)) {
					sb.append(property).append(" = '").append(string)
							.append("' or ");
				}
			}
		}
		result.append(sb.substring(0, sb.lastIndexOf(" or ")));
		result.append(" )");
		return result.toString();
	}

	/**
	 * 得到像( code='1' or code = '2')
	 * 
	 * @param property
	 *            列名
	 * @param value
	 * @return
	 */
	public static String getSqlOr(String property, Collection<?> value) {
		if (value == null || value.size() == 0) {
			return "";
		}
		StringBuilder result = new StringBuilder();
		result.append("( ");
		StringBuilder sb = new StringBuilder("");
		for (Object string : value) {
			if (string instanceof Integer || string instanceof Long
					|| string instanceof Double || string instanceof Float
					|| string instanceof Boolean) {
				sb.append(property).append(" = ").append(string).append(" or ");

			} else if (string instanceof String) {
				sb.append(property).append(" = '").append(string)
						.append("' or ");
			}
		}
		result.append(sb.substring(0, sb.lastIndexOf(" or ")));
		result.append(" )");
		return result.toString();
	}
	/**
	 * 得到像( code='1' or code = '2')
	 * 
	 * @param property
	 *            列名
	 * @param value
	 * @return
	 */
	public static String getSqlOr(String property,String operator, Collection<?> value) {
		if (value == null || value.size() == 0) {
			return "";
		}
		StringBuilder result = new StringBuilder();
		result.append("( ");
		StringBuilder sb = new StringBuilder("");
		for (Object string : value) {
			if (string instanceof Integer || string instanceof Long
					|| string instanceof Double || string instanceof Float
					|| string instanceof Boolean) {
				sb.append(property).append(" "+ operator + " ").append(string).append(" or ");

			} else if (string instanceof String) {
				sb.append(property).append(" "+ operator +"  '").append(string)
						.append("' or ");
			}
		}
		result.append(sb.substring(0, sb.lastIndexOf(" or ")));
		result.append(" )");
		return result.toString();
	}
	/**
	 * 得到像( code='1' and code = '2')
	 * 
	 * @param property
	 *            列名
	 * @param value
	 * @return
	 */
	public static String getSqlAnd(String property, Collection<?> value) {
		if (value == null || value.size() == 0) {
			return "";
		}
		StringBuilder result = new StringBuilder();
		result.append("( ");
		StringBuilder sb = new StringBuilder("");
		for (Object string : value) {
			if (string instanceof Integer || string instanceof Long
					|| string instanceof Double || string instanceof Float
					|| string instanceof Boolean) {
				sb.append(property).append(" != ").append(string)
						.append(" and ");

			} else if (string instanceof String) {
				sb.append(property).append(" != '").append(string)
						.append("' and ");
			}
		}
		result.append(sb.substring(0, sb.lastIndexOf(" and ")));
		result.append(" )");
		return result.toString();
	}

	/**
	 * 根据不同的数据库来返回不同数据库的分页语句
	 * 
	 * @param sql
	 *            需要进行分页的sql
	 * @param startRow
	 *            起始行
	 * @param endRow
	 *            结束行
	 * @param type
	 *            数据库的类型
	 * @return
	 */
	public static String getPagingSql(String sql, Integer startRow,
			Integer endRow, String type) {
		StringBuffer pSql = new StringBuffer();
		if (type.equals(PAGEING_MYSQL)) {
			// 此方法效率有点低
			// pSql.append("select * from (")
			// .append( sql)
			// .append(" ) as t ")
			// .append(" LIMIT " + (endRow - startRow))
			// .append(" OFFSET " + startRow);
			pSql.append(sql).append(" LIMIT " + (endRow - startRow))
					.append(" OFFSET " + startRow);
		} else if (type.equals(PAGEING_ORACLE)) {
			pSql.append("select * from ( ")
					.append(" select r.*, ROWNUM RN from ( ")
					.append(" select  x.*, ROWNUM Rmin from(").append(sql)
					.append(" ) x ) r WHERE Rmin <= ").append(endRow)
					.append(" ) WHERE RN > ").append(startRow);
		}
		return pSql.toString();
	}

	/**
	 * 把查询语句转换成select count(*) 语句
	 * 
	 * @param selectSql
	 * @return
	 */
	public static String converSelectSqlToCountSql(String selectSql) {
		int endIndex = selectSql.indexOf("from");

		String countSql = null;
		if (endIndex == 0) {
			countSql = "select count(*) " + selectSql;
		} else {
			String replaced = selectSql.substring(0, endIndex);
			countSql = selectSql.replace(replaced, "select count(*) ");
		}
		return countSql;
	}

	/**
	 * 把查询语句转换成count(distinct userName)语句
	 * 
	 * @param selectSql
	 * @param userName
	 * @return
	 */
	public static String converSelectSqlToCountHql(String selectSql,
			String userName) {
		int endIndex = selectSql.indexOf("from");
		String replaced = selectSql.substring(0, endIndex);
		String countSql = selectSql.replace(replaced, "select count(distinct "
				+ userName + ") ");
		return countSql;
	}

	/**
	 * 得到in value 如：1,2,3   '1','2','3;
	 * @param value
	 * @param type
	 * @return
	 */
	public static String getInValue(String value, Class<?> type) {
		String typeName = type.getSimpleName();
		StringBuilder result = new StringBuilder("");
		String[] strs = value.split(",");
		if ("Integer".equals(typeName) || "Long".equals(typeName)
				|| "Double".equals(typeName) || "Float".equals(typeName)
				|| "Boolean".equals(typeName) || "Date".equals(typeName)) {
			result.append(value);
		} else if ("String".equals(typeName)) {
			for (String s : strs) {
				if (!StringUtil.isNullOrEmpty(s)) {
					result.append("'").append(StringUtil.trim(s)).append("'").append(",");
				}
			}
		}
		if (result.lastIndexOf(",") != -1){
			result.deleteCharAt(result.length() - 1);
		}
		return result.toString();
	}
}
