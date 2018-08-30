package com.vortex.cloud.ums.dataaccess.dao;

import com.vortex.cloud.ums.model.WorkElementType;
import com.vortex.cloud.vfs.data.hibernate.repository.HibernateRepository;


/**
 * 图元类型dao
 * 
 * @author lsm
 *
 */
public interface IWorkElementTypeDao extends HibernateRepository<WorkElementType, String> {
	/**
	 * code在本部门是否存在
	 * 
	 * @param code
	 * @param tenantId
	 * @return
	 */
	public boolean isCodeExists(String code, String tenantId);

}
