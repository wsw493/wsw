package com.vortex.cloud.ums.dataaccess.dao;

import java.util.List;
import java.util.Map;

import com.vortex.cloud.ums.dto.TenantPramSettingDto;
import com.vortex.cloud.ums.model.TenantPramSetting;
import com.vortex.cloud.vfs.data.hibernate.repository.HibernateRepository;


public interface ITenantPramSettingDao extends HibernateRepository<TenantPramSetting, String> {

	long getCntByTenantId(String tenantId);

	/**
	 * 通过参数类型获取参数记录列表
	 * 
	 * @param tenantId
	 * @param paramTypeCode
	 * @return
	 */
	List<TenantPramSetting> findListByParamTypeCode(String tenantId, String paramTypeCode);

	/**
	 * 同时获取指定的多个参数类型下的相应参数列表。
	 * 
	 * @param tenantId
	 * @param paramTypeCodeList
	 * @return Map<String, List<TenantPramSettingDto>> key: 参数类型 value:
	 *         参数类型下的参数列表
	 */
	Map<String, List<TenantPramSettingDto>> findByParamTypeCodeList(String tenantId, List<String> paramTypeCodeList);

	/**
	 * 获取指定参数类型下，指定参数Code对应的参数记录
	 * 
	 * @param tenantId
	 *            租户Id
	 * @param paramTypeCode
	 *            参数类型Code
	 * @param paramCode
	 *            参数Code
	 * @return
	 */
	TenantPramSetting findOneByParamCode(String tenantId, String paramTypeCode, String paramCode);

	/**
	 * 获取指定参数类型下，指定参数Name对应的参数记录
	 * 
	 * @param tenantId
	 *            租户Id
	 * @param paramTypeCode
	 *            参数类型Code
	 * @param paramName
	 *            参数Name
	 * @return
	 */
	TenantPramSetting findOneByParamName(String tenantId, String paramTypeCode, String paramName);

	/**
	 * 获取指定参数类型下，指定参数Codes对应的参数记录
	 * 
	 * @param tenantId
	 * @param paramTypeCode
	 * @param paramCodes
	 * @return
	 */
	Map<String, TenantPramSetting> findListByParamCodes(String tenantId, String paramTypeCode, String[] paramCodes);

	/**
	 * 同时获取指定的多个参数类型下的相应参数列表。
	 * 
	 * @param tenantId
	 * @param paramTypeCodeList
	 * @return 参数类型下的参数列表
	 */
	List<TenantPramSettingDto> findByParamTypeCodes(String tenantId, List<String> paramTypeCodeList);
}
