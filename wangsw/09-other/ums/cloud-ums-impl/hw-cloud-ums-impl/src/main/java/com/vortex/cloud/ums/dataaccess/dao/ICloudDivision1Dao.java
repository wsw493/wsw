package com.vortex.cloud.ums.dataaccess.dao;

import java.util.List;

import com.vortex.cloud.ums.model.CloudDivision;
import com.vortex.cloud.vfs.data.hibernate.repository.HibernateRepository;


public interface ICloudDivision1Dao extends HibernateRepository<CloudDivision, String> {
	public List<CloudDivision> getListByParentId(String parentId);
}
