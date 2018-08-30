package com.vortex.cloud.ums.dataaccess.dao;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.vortex.cloud.ums.dto.WorkElementPageDto;
import com.vortex.cloud.ums.model.WorkElement;
import com.vortex.cloud.vfs.data.hibernate.repository.HibernateRepository;


/**
 * 图元dao
 * 
 * @author lsm
 *
 */
public interface IWorkElementDao extends HibernateRepository<WorkElement, String> {
	/**
	 * 判断该code在当前租户是否存在
	 * @param newCode
	 * @param tenantId
	 * @return
	 */
	boolean isCodeExists(String newCode, String tenantId);
	
	/**
	 * 判断该param在当前租户是否存在
	 * @param param
	 * @param value
	 * @param tenantId
	 * @return
	 */
	boolean isParamExists(String param, String value, String tenantId);
	/**
	 * 同步图元信息
	 * @param tenantId
	 * @param syncTime
	 * @param pageSize
	 * @param pageNumber
	 * @return
	 */
	public List<WorkElementPageDto> syncWeByPage(String tenantId, long syncTime, Integer pageSize, Integer pageNumber);
	/**
	 * 同步图元信息(分页)
	 * @param pageable
	 * @param paramMap
	 * @return
	 */
	public Page<WorkElement> syncWorkElementsByPage(Pageable pageable,Map<String, Object> paramMap);
}
