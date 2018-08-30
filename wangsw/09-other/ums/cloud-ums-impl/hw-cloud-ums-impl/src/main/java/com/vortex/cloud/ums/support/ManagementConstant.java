package com.vortex.cloud.ums.support;

import org.apache.commons.lang3.StringUtils;

import com.vortex.cloud.vfs.common.lang.PropertiesHelper;

public class ManagementConstant {
	public static final String YES = "1"; // 是
	public static final String NO = "0"; // 否
	public static final String SYSTEM_CODE = "CLOUD_MANAGEMENT"; // 系统code
	/**
	 * 后台请求的后缀
	 */
	public static final String BACK_DYNAMIC_SUFFIX = ".smvc";
	/**
	 * 前台请求的后缀
	 */
	public static final String FORE_DYNAMIC_SUFFIX = ".jhtml";

	public static final String PERMISSION_SUFFIX_READ = ".read"; // 云服务访问后缀:此服务不涉及后台数据修改，只读权限即可调用
	public static final String PERMISSION_SUFFIX_EDIT = ".edit"; // 云服务访问后缀:此服务会修改数据库，完全权限方可调用
	public static final String PERMISSION_SUFFIX_SA = ".sa"; // 云服务访问后缀:此服务不会被过滤，为超级管理员权限

	public static final String URI_HEAD = "/cloud"; // 云服务URI的头
	public static final String COPY_RESOURCE_URI = "registe/copyresources" + PERMISSION_SUFFIX_SA; // 云服务提供资源拷贝到租户的rest服务的后缀

	public static final String ENABLED_YES = "1"; // 启用
	public static final String ENABLED_NO = "0"; // 禁用

	public static final String HAS_RESOURCE_YES = "1"; // 复制过
	public static final String HAS_RESOURCE_NO = "0"; // 未复制过资源

	public static final String TENANT_ID_KEY = "tenantId"; // 租户id
	public static final String REST_RESULT = "result"; // 调用rest服务后返回的map中的访问结果
	public static final String REST_MSG = "msg"; // 调用rest服务后返回的map中的访问结果
	public static final String REST_DATA = "data"; // 调用rest服务后返回的map中的访问结果
	public static final String REST_PMS = "parameters"; // rest服务接收参数的字符串
	public static final Integer REST_RESULT_SUCC = 0; // 调用rest后返回结果为成功
	public static final Integer REST_RESULT_FAIL = 1; // 调用rest后返回结果为失败

	// 是否已拷贝资源
	public static final String HAS_COPY_RESOURCE_YES = "1"; // 已拷贝
	public static final String HAS_COPY_RESOURCE_NO = "0"; // 未拷贝

	// 是否根节点
	public static final String ROOT_YES = "1"; // 是
	public static final String ROOT_NO = "0"; // 否

	// 访问方式
	public static final String METHOD_POST = "POST"; //
	public static final String METHOD_GET = "GET"; //

	public static final String TENANT_ROOT_ROLE = "tenantRootRole"; // 云系统上面的租户管理员角色code
	public static final String SYSTEM_ROOT_ROLE = "systemRootRole"; // 云系统上面的业务系统管理员角色code

	/*
	 * =========================================================================
	 * 读取property文件 - start
	 * =========================================================================
	 */
	private static final String PROPERTIES_FILE_NAME = "cloud.properties";

	/**
	 * 返回配置文件参数常量
	 * 
	 * @param propertyName
	 *            PROPERTIES_FILE_NAME中定义的参数名称
	 * @return PROPERTIES_FILE_NAME中定义的参数名称对应值
	 */
	public static String getPropertyValue(String propertyName) {
		if (StringUtils.isBlank(propertyName)) {
			return null;
		}
		return PropertiesHelper.getInstance(PROPERTIES_FILE_NAME).getProperty(PROPERTIES_FILE_NAME, propertyName);
	}

	/*
	 * ============================== end =======================================
	 */

	/*
	 * =========================================================================
	 * 业务系统请求云系统时，携带的参数 - start
	 * =========================================================================
	 */
	// 公共参数
	public static final String REQ_PARAM_SYSTEM_CODE = "systemCode"; // 业务系统Code
	public static final String REQ_PARAM_USER_ID = "userId"; // 用户Id

	// 其他参数

	/*
	 * ============================== end =======================================
	 */

	public static final String CLOUD_TENANT_ID = "CLOUD_TENANT"; // 云平台租户id
	public static final String REDIS_SEPARATOR = "_"; // 分隔符
	public static final String REDIS_EXISTS_VALUE = "1"; // redis中表示存在的value值
	public static final String REDIS_PRE_MENU = "menu"; // 用户菜单打头
	public static final String REDIS_PRE_FUNCTION = "function"; // 功能码打头
	public static final String REDIS_PRE_DEPTORG = "deptorg"; // 机构部门信息打头
	public static final String REDIS_PRE_STAFF = "staff"; // 人员信息打头
	public static final String REDIS_PRE_KEY_SYS_MENU = "key_sys_menu"; // 菜单信息打头
	public static final String REDIS_PRE_SYS_MENU = "sys_menu"; // 系统菜单key打头
	public static final String REDIS_PRE_KEY_USER_MENU = "key_user_menu"; // 用户菜单key打头
	public static final String REDIS_PRE_TENANT_DEPTORGIDS = "key_tenant_deptorgids"; // 机构部门key打头
	public static final String REDIS_PRE_TENANT_STAFFIDS = "key_tenant_staffids"; // 人员key打头
	public static final String REDIS_PRE_MAP_STAFF = "map_staff"; // 人员key打头
	public static final String REDIS_PRE_MAP_DEPTORG = "map_deptorg"; // 人员key打头

	// 系统拷贝等相关常量
	public static final String SYS_ROOT_ROLE_GROUP = "sysRootGroup"; // 业务系统的系统管理员默认角色组
	public static final String SYS_ROOT_FUNCTION_GROUP = "sysRootFunctionGroup"; // 业务系统的系统管理员默认功能组
	public static final String SYS_ROOT_MENU_GROUP = "sysRootMenuGroup"; // 业务系统的系统管理员默认菜单组

	public static final String TREE_ICON_WORK_ELEMENT_TYPE = "../../../../resources/img/tree/workElementType.png";// 图元类型
	public static final String TREE_ICON_WORK_ELEMENT = "../../../../resources/img/tree/workElement.png";// 图元
	// 文件上传信息的marks 的key值前缀
	public static final String MARK_KEY_PREFIX = "UMS_MARKS_";

	/**
	 * 搜索字符串前缀
	 */
	public static final String SEARCH_PREFIX = "s";
	/**
	 * 页面默认记录数
	 */
	public static final int DEFAULT_PAGE_SIZE = 20;
}
