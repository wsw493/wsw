package com.vortex.cloud.ums.dataaccess.service;

import java.util.List;

import com.vortex.cloud.ums.dto.rest.PramSettingRestDto;
import com.vortex.cloud.ums.model.PramSetting;
import com.vortex.cloud.vfs.data.hibernate.service.PagingAndSortingService;



public interface IParamSettingService extends PagingAndSortingService<PramSetting, String> {
	
	long delete(String[] idArr);
	/**
	 * 获取对应类型下的值
	 * @param paramTypeCode
	 * @return
	 */
	public List<PramSettingRestDto> findListByParamTypeCode(String paramTypeCode, String tenantId);
}