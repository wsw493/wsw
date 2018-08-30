package com.vortex.cloud.ums.dataaccess.dao;

import java.util.List;

import com.vortex.cloud.ums.dto.CloudFunctionGroupDto;
import com.vortex.cloud.ums.model.CloudFunctionGroup;
import com.vortex.cloud.vfs.data.hibernate.repository.HibernateRepository;


/**
 * 功能组dao
 * 
 * @author lsm
 * @date 2016年4月1日
 */
public interface ICloudFunctionGroupDao extends HibernateRepository<CloudFunctionGroup, String> {
	/**
	 * 根据id查询功能组信息和所属功能组
	 * 
	 * @param id
	 * @return
	 */
	CloudFunctionGroupDto findCloudFunctionGroupById(String id);

	/**
	 * 得到系统中的自定义功能组（非系统注册时预设的）
	 * 
	 * @param systemId
	 * @param defNodeCode
	 * @return
	 */
	public List<CloudFunctionGroup> getSourceList(String systemId, String defNodeCode);

	/**
	 * 根据系统id和功能组code，得到功能组
	 * 
	 * @param systemId
	 * @param functionGroupCode
	 * @return
	 */
	public CloudFunctionGroup getByCode(String systemId, String functionGroupCode);

	/**
	 * 根据系统id和父节点id，得到第一级子功能组列表
	 * 
	 * @param systemId
	 * @param parentId
	 * @return
	 */
	public List<CloudFunctionGroup> getByParentId(String systemId, String parentId);
}
