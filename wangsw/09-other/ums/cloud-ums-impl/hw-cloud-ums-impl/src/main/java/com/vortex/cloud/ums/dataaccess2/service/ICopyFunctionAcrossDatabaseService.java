package com.vortex.cloud.ums.dataaccess2.service;

import java.util.List;

public interface ICopyFunctionAcrossDatabaseService {
	/**
	 * 从资源数据库拷贝菜单（连带拷贝功能）到目标数据库
	 * 
	 * @param sourceSystemId
	 *            资源数据库id
	 * @param targetSystemId
	 *            目标数据库id
	 * @param smenus
	 *            资源数据库的待拷贝的菜单id列表
	 * @throws Exception
	 */
	public void coyp(String sourceSystemId, String targetSystemId, List<String> smenus) throws Exception;
}
