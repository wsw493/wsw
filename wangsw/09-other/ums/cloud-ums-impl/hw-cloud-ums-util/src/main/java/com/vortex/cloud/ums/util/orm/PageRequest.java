package com.vortex.cloud.ums.util.orm;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.Lists;

/**
 * 分页参数封装类.
 */
public class PageRequest implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 3707846210554603196L;

	public static final int DEFAULT_PAGE_SIZE = 20;

	protected int pageNo = 1;
	protected int pageSize = DEFAULT_PAGE_SIZE;
	protected long totalRecords = 0;// 记录总数

	protected String orderBy = null;
	protected String orderDir = null;

	protected Map<String, String> sortedProperty = new HashMap<String, String>();// 排序列

	protected boolean countTotal = true;

	public PageRequest() {
		this(1, DEFAULT_PAGE_SIZE, 0);
	}

	public PageRequest(int pageSize, long totalRecords) {
		this(1, pageSize, totalRecords);
	}

	public PageRequest(int pageNo, int pageSize, long totalRecords) {
		this.pageSize = pageSize;
		this.totalRecords = totalRecords;
		if (countTotal){
			this.pageNo = pageNo;
		} else {
			this.setPageNo(pageNo);
		}
		
	}

	/**
	 * 得到当前页的条数
	 * 
	 * @return
	 */
	public long getCurrentPageSize() {
		return Math.min(pageSize, getRowEnd() - getRowStart());
	}

	/**
	 * 得到结束行
	 * 
	 * @return
	 */
	public long getRowEnd() {
		return totalRecords == 0 ? (pageNo <= 0 ? pageSize : pageNo * pageSize)
				: Math.min(totalRecords, pageNo * pageSize);
	}

	/**
	 * 得到起始行
	 * 
	 * @return
	 */
	public long getRowStart() {
		return pageNo <= 0 ? 0 : (pageNo - 1) * pageSize;
	}

	/**
	 * 根据pageSize与totalItems计算总页数.
	 */
	public int getTotalPages() {
		return (int) Math.ceil((double) totalRecords / (double) getPageSize());

	}

	/**
	 * 获得总记录数, 默认值为0.
	 */
	public long getTotalRecords() {
		return totalRecords;
	}

	/**
	 * 设置总记录数.
	 */
	public void setTotalRecords(long totalRecords) {
		this.totalRecords = totalRecords < 0 ? 0 : totalRecords;
	}

	/**
	 * 获得当前页的页号, 序号从1开始, 默认为1.
	 */
	public int getPageNo() {
		return pageNo;
	}

	/**
	 * 设置当前页的页号, 序号从1开始, 低于1时自动调整为1.
	 */
	public void setPageNo(final int pageNo) {
		int validPage = pageNo > getTotalPages() ? getTotalPages() : pageNo;
		this.pageNo = validPage <= 0 ? 1 : validPage;
	}

	/**
	 * 获得每页的记录数量, 默认为10.
	 */
	public int getPageSize() {
		return pageSize;
	}

	/**
	 * 设置每页的记录数量, 低于1时自动调整为默认的值.
	 */
	public void setPageSize(final int pageSize) {
		this.pageSize = pageSize;

		if (pageSize < 1) {
			this.pageSize = DEFAULT_PAGE_SIZE;
		}
	}

	/**
	 * 获得排序字段, 无默认值. 多个排序字段时用','分隔.
	 */
	public String getOrderBy() {
		return orderBy;
	}

	/**
	 * 设置排序字段, 多个排序字段时用','分隔.
	 */
	public void setOrderBy(final String orderBy) {
		this.orderBy = orderBy;
	}

	/**
	 * 获得排序方向, 无默认值.
	 */
	public String getOrderDir() {
		return orderDir;
	}

	/**
	 * 设置排序方式向.
	 * 
	 * @param orderDir
	 *            可选值为desc或asc,多个排序字段时用','分隔.
	 */
	public void setOrderDir(final String orderDir) {
		if (StringUtils.isNotEmpty(orderDir)) {
			String lowcaseOrderDir = StringUtils.lowerCase(orderDir);

			// 检查order字符串的合法值
			String[] orderDirs = StringUtils.split(lowcaseOrderDir, ',');
			for (String orderDirStr : orderDirs) {
				if (!StringUtils.equals(Sort.DESC, orderDirStr)
						&& !StringUtils.equals(Sort.ASC, orderDirStr)) {
					throw new IllegalArgumentException("排序方向" + orderDirStr
							+ "不是合法值");
				}
			}

			this.orderDir = lowcaseOrderDir;
		}

	}

	/**
	 * 获得排序参数.
	 */
	public List<Sort> getSort() {
		String[] orderBys = StringUtils.split(orderBy, ',');
		String[] orderDirs = StringUtils.split(orderDir, ',');

		List<Sort> orders = Lists.newArrayList();
		for (int i = 0; i < orderBys.length; i++) {
			orders.add(new Sort(orderBys[i], orderDirs[i]));
		}

		return orders;
	}

	/**
	 * 是否已设置排序字段,无默认值.
	 */
	public boolean isOrderBySetted() {
		return (StringUtils.isNotBlank(orderBy) && StringUtils
				.isNotBlank(orderDir));
	}

	/**
	 * 是否默认计算总记录数.
	 */
	public boolean isCountTotal() {
		return countTotal;
	}

	/**
	 * 设置是否默认计算总记录数.
	 */
	public void setCountTotal(boolean countTotal) {
		this.countTotal = countTotal;
	}

	/**
	 * 根据pageNo和pageSize计算当前页第一条记录在总结果集中的位置, 序号从0开始.
	 */
	public int getOffset() {
		return ((pageNo - 1) * pageSize);
	}

	/**
	 * 刷新当前页
	 * 
	 * @param pageNo
	 */
	public void refresh(int pageNo) {
		this.setPageNo(pageNo);
	}

	public static class Sort {
		public static final String ASC = "asc";
		public static final String DESC = "desc";

		private final String property;
		private final String dir;

		public Sort(String property, String dir) {
			this.property = property;
			this.dir = dir;
		}

		public String getProperty() {
			return property;
		}

		public String getDir() {
			return dir;
		}
	}
}
