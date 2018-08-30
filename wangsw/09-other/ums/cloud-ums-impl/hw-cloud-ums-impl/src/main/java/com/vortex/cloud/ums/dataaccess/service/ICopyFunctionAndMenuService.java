package com.vortex.cloud.ums.dataaccess.service;

public interface ICopyFunctionAndMenuService {

	/**
	 * 从某个业务系统拷贝功能和菜单到目标业务系统
	 * 
	 * @param sourceBsCode 资源业务系统code
	 * @param targetBsCode 目标业务系统code
	 * @throws Exception
	 */
	public void copyFunctionAndMenu(String sourceBsCode, String targetBsCode) throws Exception;
}
