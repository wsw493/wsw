package com.vortex.cloud.ums.dataaccess2.dao;

import java.io.Serializable;

import com.vortex.cloud.ums.model.CloudFunctionGroup;

public interface ICloudFunctionGroupDao2 {
	/**
	 * 保存
	 * 
	 * @param bean
	 * @return
	 * @throws Exception
	 */
	public Serializable save(CloudFunctionGroup bean) throws Exception;

	/**
	 * 更新
	 * 
	 * @param bean
	 * @throws Exception
	 */
	public void update(CloudFunctionGroup bean) throws Exception;

	/**
	 * 根据目标系统和功能组code，查询目标系统中的功能组信息
	 * 
	 * @param sysid
	 * @param fgcode
	 * @return
	 */
	public CloudFunctionGroup getFunctionGroupBySysidAndFgcode(String sysid, String fgcode) throws Exception;

	/**
	 * 根据id得到实体
	 * 
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public CloudFunctionGroup getById(String id) throws Exception;

	/**
	 * 根据id得到实体
	 * 
	 * @param serializableId
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public CloudFunctionGroup getBySerializableId(String serializableId) throws Exception;

	/**
	 * 查询某父节点下面的所有子节点中的最大的nodecode
	 * 
	 * @param sysid
	 * @param parentId
	 * @param tableName
	 * @return
	 * @throws Exception
	 */
	public String getMaxChildNodecode(String sysid, String parentId, String tableName) throws Exception;
}
