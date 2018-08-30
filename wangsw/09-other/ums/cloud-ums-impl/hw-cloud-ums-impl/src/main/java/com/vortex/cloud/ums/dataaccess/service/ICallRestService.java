package com.vortex.cloud.ums.dataaccess.service;

import java.util.Map;

/**
 * 访问rest服务
 * 
 * @author XY
 *
 */
public interface ICallRestService {
	/**
	 * 访问rest服务
	 * 
	 * @param URL
	 *            访问地址
	 * @param parameters
	 *            参数集合
	 * @param method
	 *            post或者get
	 * @return
	 */
	public String callRest(String URL, Map<String, Object> parameters, String method);
}
