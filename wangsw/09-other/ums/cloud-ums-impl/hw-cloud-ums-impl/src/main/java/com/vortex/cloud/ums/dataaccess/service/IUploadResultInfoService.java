package com.vortex.cloud.ums.dataaccess.service;

import java.util.List;

import org.springframework.data.domain.Pageable;

import com.vortex.cloud.ums.model.upload.UploadResultInfo;
import com.vortex.cloud.vfs.data.dto.DataStore;
import com.vortex.cloud.vfs.data.hibernate.service.PagingAndSortingService;
import com.vortex.cloud.vfs.data.support.SearchFilter;

public interface IUploadResultInfoService extends PagingAndSortingService<UploadResultInfo, String> {

	DataStore<UploadResultInfo> queryDataStorePage(Pageable pageable, List<SearchFilter> searchFilters);

}
