package com.vortex.cloud.ums.util.support;

/**
 * 常量
 * 
 * @author dejunx
 * 
 */
public class Constants {

	/**
	 * 后台请求的后缀
	 */
	public static final String BACK_DYNAMIC_SUFFIX = ".smvc";
	/**
	 * 前台请求的后缀
	 */
	public static final String FORE_DYNAMIC_SUFFIX = ".jhtml";

	/** 连数分页数 **/
	public static final String PAGE_SPLIT = "/";

	/**
	 * 页面默认记录数
	 */
	public static final int DEFAULT_PAGE_SIZE = 20;
	/**
	 * 上下文路径
	 */
	public static final String CTX = "ctx";
	/**
	 * 页面操作状态
	 */
	public static final String OPRT = "oprt";
	/**
	 * 编辑状态
	 */
	public static final String EDIT = "edit";
	/**
	 * 新增状态
	 */
	public static final String CREATE = "create";
	/**
	 * 重定向至修改页面
	 */
	public static final String REDIRECT_EDIT = "edit";
	/**
	 * 重定向至列表页面
	 */
	public static final String REDIRECT_LIST = "list";
	/**
	 * 重定向至新增页面
	 */
	public static final String REDIRECT_CREATE = "create";
	/**
	 * 搜索字符串前缀
	 */
	public static final String SEARCH_PREFIX = "s";
	/**
	 * 搜索字符串
	 */
	public static final String SEARCH_STRING = "searchstring";
	/**
	 * 搜索字符串（不含排序）
	 */
	public static final String SEARCH_STRING_NO_SORT = "searchstringnosort";

	public static final String STATUS = "status";
	public static final String MESSAGE = "message";
	public static final String OPERATION_SUCCESS = "operationSuccess";
	public static final String OPERATION_FAILURE = "operationFailure";
	public static final String SAVE_SUCCESS = "saveSuccess";
	public static final String DELETE_SUCCESS = "deleteSuccess";
}
