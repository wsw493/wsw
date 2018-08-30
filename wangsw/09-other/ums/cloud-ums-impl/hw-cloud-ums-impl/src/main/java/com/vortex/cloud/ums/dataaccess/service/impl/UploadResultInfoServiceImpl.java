package com.vortex.cloud.ums.dataaccess.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vortex.cloud.ums.dataaccess.dao.IUploadResultInfoDao;
import com.vortex.cloud.ums.dataaccess.service.IUploadResultInfoService;
import com.vortex.cloud.ums.model.upload.UploadResultInfo;
import com.vortex.cloud.vfs.data.dto.DataStore;
import com.vortex.cloud.vfs.data.hibernate.repository.HibernateRepository;
import com.vortex.cloud.vfs.data.hibernate.service.SimplePagingAndSortingService;
import com.vortex.cloud.vfs.data.support.SearchFilter;

/**
 * Excel上传信息service
 * 
 * @author SonHo
 *
 */
@Service("uploadResultInfoService")
@Transactional
public class UploadResultInfoServiceImpl extends SimplePagingAndSortingService<UploadResultInfo, String> implements IUploadResultInfoService {
	@Resource
	private IUploadResultInfoDao uploadResultInfoDao;

	@Override
	public HibernateRepository<UploadResultInfo, String> getDaoImpl() {
		return uploadResultInfoDao;
	}

	@Override
	public DataStore<UploadResultInfo> queryDataStorePage(Pageable pageable, List<SearchFilter> searchFilters) {
		Page<UploadResultInfo> page = this.findPageByFilter(pageable, searchFilters);
		DataStore<UploadResultInfo> ds = new DataStore<UploadResultInfo>();
		if (null != page && page.hasContent()) {
			ds.setRows(page.getContent());
			ds.setTotal(page.getTotalElements());
		}
		return ds;
	}
}
