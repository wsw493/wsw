package com.vortex.cloud.ums.dataaccess.service;

import java.util.List;

import com.vortex.cloud.ums.dto.CloudDivisionDto;
import com.vortex.cloud.ums.model.CloudDivision;
import com.vortex.cloud.vfs.data.hibernate.service.PagingAndSortingService;


public interface ICloudDivisionService extends PagingAndSortingService<CloudDivision, String> 
{
	/**
	 * 保存记录
	 * 
	 * @param dto
	 * @return 
	 */
	public CloudDivision save(CloudDivisionDto dto) ;

	/**
	 * 根据指定的id，获取记录
	 * 
	 * @param id
	 * @return
	 */
	public CloudDivisionDto getById(String id);

	public long deleteByIdArr(String[] ids, boolean casecade);
	
	CloudDivision update(CloudDivisionDto dto);
	
	public List<CloudDivision> getAllChildren(CloudDivision parent);
}
