package com.vortex.cloud.ums.dataaccess2.dao;

import java.io.Serializable;

import com.vortex.cloud.ums.dto.CloudFunctionDto;
import com.vortex.cloud.ums.model.CloudFunction;

public interface ICloudFunctionDao2 {
	/**
	 * 新增
	 * 
	 * @param bean
	 * @return
	 * @throws Exception
	 */
	public Serializable save(CloudFunction bean) throws Exception;

	/**
	 * 更新
	 * 
	 * @param bean
	 * @throws Exception
	 */
	public void update(CloudFunction bean) throws Exception;

	/**
	 * 根据目标系统和功能code，得到功能
	 * 
	 * @param sysid
	 * @param fcode
	 * @return
	 */
	public CloudFunction getFunctionBySysidAndFcode(String sysid, String fcode) throws Exception;
	
	/**
	 * 根据目标系统和功能code，得到功能
	 * 
	 * @param sysid
	 * @param fcode
	 * @return
	 */
	public CloudFunctionDto getFunctionDtoBySysidAndFcode(String sysid, String fcode) throws Exception;
	
	public CloudFunction getById(String id) throws Exception;
}
