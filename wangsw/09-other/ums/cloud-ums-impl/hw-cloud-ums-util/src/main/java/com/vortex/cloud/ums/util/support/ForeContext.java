package com.vortex.cloud.ums.util.support;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;

import com.google.common.collect.Lists;
import com.vortex.cloud.vfs.common.lang.StringUtil;

public abstract class ForeContext {

	public static final String BACK_DYNAMIC_SUFFIX = "back_dynamic_suffix";
	public static final String FORE_DYNAMIC_SUFFIX = "fore_dynamic_suffix";

	public static void setData(Map<String, Object> data) {
		data.put(BACK_DYNAMIC_SUFFIX, Constants.BACK_DYNAMIC_SUFFIX);
		data.put(FORE_DYNAMIC_SUFFIX, Constants.FORE_DYNAMIC_SUFFIX);
	}

	/**
	 * 得到当前url
	 * 
	 * @param request
	 * @return
	 */
	public static String getCurrentUrl(HttpServletRequest request) {
		String uri = request.getRequestURI();
		String queryString = request.getQueryString();
		if (StringUtils.isNotBlank(queryString)) {
			uri += "?" + queryString;
		}
		return uri;
	}

	/**
	 * 得到排序
	 * 
	 * @param request
	 * @return
	 */
	public static final Sort getSort(HttpServletRequest request) {
		return getSort(request, null);
	}

	public static final Sort getSort(HttpServletRequest request, Order defaultOrder) {
		String sort = request.getParameter("sort");
		String order = request.getParameter("order");
		List<Order> ords = Lists.newArrayList();
		Order ord = null;
		if (!StringUtil.isNullOrEmpty(sort) && !StringUtil.isNullOrEmpty(order)) {
			String[] sorts = sort.split(",");
			String[] orders = order.split(",");
			for (int i = 0, len = sorts.length; i < len; i++) {
				ord = new Order(Direction.fromString(orders[i]), sorts[i]);
				ords.add(ord);
			}
		} else {
			if (defaultOrder != null) {
				ords.add(defaultOrder);
			}
		}
		if (ords.size() == 0) {
			return null;
		} else {
			return new Sort(ords);
		}

	}

	/**
	 * 得到分页
	 * 
	 * @param request
	 * @return
	 */
	public static final Pageable getPageable(HttpServletRequest request) {
		return getPageable(request, null);
	}

	public static final Pageable getPageable(HttpServletRequest request, Sort defaultSort) {
		int page = NumberUtils.toInt(request.getParameter("page"), 0);
		if (page >= 1) {
			page = page - 1;
		}
		int rows = NumberUtils.toInt(request.getParameter("rows"), Constants.DEFAULT_PAGE_SIZE);
		Sort sort = getSort(request);
		if (sort == null) {
			sort = defaultSort;
		}
		PageRequest pageable = new PageRequest(page, rows, sort);
		return pageable;
	}

	public static final Pageable getPageableAndSort(HttpServletRequest request, Sort sort) {
		int page = NumberUtils.toInt(request.getParameter("page"), 0);
		if (page >= 1) {
			page = page - 1;
		}
		int rows = NumberUtils.toInt(request.getParameter("rows"), Constants.DEFAULT_PAGE_SIZE);
		PageRequest pageable = new PageRequest(page, rows, sort);
		return pageable;
	}

	/**
	 * 页数线程变量
	 */
	private static ThreadLocal<Integer> totalPagesHolder = new ThreadLocal<Integer>();

	public static void setTotalPages(Integer totalPages) {
		totalPagesHolder.set(totalPages);
	}

	public static Integer getTotalPages() {
		return totalPagesHolder.get();
	}

	public static void resetTotalPages() {
		totalPagesHolder.remove();
	}

}
