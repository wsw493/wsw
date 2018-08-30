package com.vortex.cloud.ums.dataaccess.dao;

import java.util.List;

import com.vortex.cloud.ums.dto.IdNameDto;
import com.vortex.cloud.ums.model.TenantDivision;
import com.vortex.cloud.vfs.data.hibernate.repository.HibernateRepository;

public interface ITenantDivisionDao extends HibernateRepository<TenantDivision, String> {

	void deleteByIds(String[] ids);

	/**
	 * 获取某个行政区划的所有子行政区划
	 * 
	 * @param parent
	 * @return
	 */
	List<TenantDivision> getAllChildren(TenantDivision parent);

	/**
	 * 获取某个行政区划的所有子行政区划
	 * 
	 * @param parent
	 * @return
	 */
	List<TenantDivision> getAllChildren(String parentId);

	/**
	 * 根据租户id和名称列表，返回名称-id的map列表，用于导入
	 * 
	 * @param tenantId
	 * @param names
	 * @return
	 * @throws Exception
	 */
	public List<IdNameDto> getDivisionsByNames(String tenantId, List<String> names) throws Exception;
}
