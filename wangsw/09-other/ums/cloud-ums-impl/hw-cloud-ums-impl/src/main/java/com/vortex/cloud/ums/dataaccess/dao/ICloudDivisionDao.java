package com.vortex.cloud.ums.dataaccess.dao;

import java.util.List;

import com.vortex.cloud.ums.model.CloudDivision;
import com.vortex.cloud.vfs.data.hibernate.repository.HibernateRepository;


public interface ICloudDivisionDao extends HibernateRepository<CloudDivision, String>
{

	void deleteByIds(String[] ids);

	/**
	 * 获取某个行政区划的所有子行政区划
	 * 
	 * @param parent
	 * @return
	 */
	List<CloudDivision> getAllChildren(CloudDivision parent);
}
