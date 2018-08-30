package com.vortex.cloud.ums.dataaccess.dao.impl;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import com.vortex.cloud.ums.dataaccess.dao.IUploadResultInfoDao;
import com.vortex.cloud.ums.model.upload.UploadResultInfo;
import com.vortex.cloud.vfs.data.hibernate.repository.SimpleHibernateRepository;


/**
 * Excel上传信息Dao
 * 
 * @author SonHo
 *
 */
@Repository("uploadResultInfoDao")
public class UploadResultInfoDaoImpl extends SimpleHibernateRepository<UploadResultInfo, String> implements IUploadResultInfoDao {

	@Override
	public DetachedCriteria getDetachedCriteria() {
		DetachedCriteria criteria = DetachedCriteria.forClass(getPersistentClass(), "uploadResultInfo");
		criteria.add(Restrictions.eq("beenDeleted", 0));
		return criteria;
	}

}
