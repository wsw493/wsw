package com.vortex.cloud.ums.web.rest.np;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.vortex.cloud.ums.dataaccess.service.ITenantParamSettingService;
import com.vortex.cloud.ums.dto.TenantPramSettingDto;
import com.vortex.cloud.ums.model.TenantPramSetting;
import com.vortex.cloud.ums.support.ManagementConstant;
import com.vortex.cloud.vfs.common.exception.VortexException;
import com.vortex.cloud.vfs.common.mapper.JsonMapper;
import com.vortex.cloud.vfs.common.web.springmvc.SpringmvcUtils;
import com.vortex.cloud.vfs.data.dto.RestResultDto;

@RestController
@RequestMapping("cloud/management/rest/np/param")
@SuppressWarnings("unchecked")
public class ParamRestNpController {
	@Resource
	private ITenantParamSettingService tenantParamSettingService;
	private JsonMapper jm = new JsonMapper();
	private static final Logger logger = LoggerFactory.getLogger(ParamRestNpController.class);

	private static final String REQ_PARAM_TENANT_CODE = "tenantCode"; //
	private static final String REQ_PARAM_TENATN_ID = "tenantId";

	private static final String REQ_PARAM_TYPE_CODE = "paramTypeCode"; // 单个参数类型
	private static final String REQ_PARAM_TYPE_CODE_LIST = "paramTypeCodeList"; // 多个参数类型

	private static final String REQ_PARAM_CODE = "paramCode"; // TenantPramSetting.parmCode
	private static final String REQ_PARAM_NAME = "paramName"; // TenantPramSetting.parmName
	private static final String REQ_PARAM_CODES = "paramCodes";
	private static final String REQ_PARAM_TYPE_ID = "typeId"; // 参数类型
	private static final String ORDER_INDEX = "orderIndex";// 排序号

	/**
	 * 通过参数类型Code，获取参数列表
	 * 
	 * @param request
	 * @return
	 * 
	 */
	@SuppressWarnings("all")
	@RequestMapping(value = "getByParamTypeCode" + ManagementConstant.PERMISSION_SUFFIX_READ, method = { RequestMethod.GET, RequestMethod.POST })
	public RestResultDto getByParamTypeCode(HttpServletRequest request) {
		String msg = null;
		Integer result = ManagementConstant.REST_RESULT_SUCC;
		Object data = null;
		String exception = null;
		RestResultDto restResultDto = new RestResultDto();

		try {
			JsonMapper jm = new JsonMapper();
			Map<String, String> paramMap = jm.fromJson(SpringmvcUtils.getParameter(ManagementConstant.REST_PMS), Map.class);
			if (MapUtils.isEmpty(paramMap)) {
				throw new VortexException("参数不能为空");
			}

			String paramTypeCode = paramMap.get(REQ_PARAM_TYPE_CODE);
			String tenantCode = paramMap.get(REQ_PARAM_TENANT_CODE);

			if (StringUtils.isBlank(paramTypeCode)) {
				throw new VortexException("请传入参数：" + REQ_PARAM_TYPE_CODE);
			}

			if (StringUtils.isBlank(tenantCode)) {
				throw new VortexException("请传入参数：" + REQ_PARAM_TENANT_CODE);
			}

			List<TenantPramSetting> list = tenantParamSettingService.findListByParamTypeCodeAndTenantCode(tenantCode, paramTypeCode);

			msg = "成功获取到参数列表";
			data = list;
		} catch (Exception e) {
			e.printStackTrace();
			result = ManagementConstant.REST_RESULT_FAIL;
			msg = "获取参数列表失败！";
			exception = e.getMessage();
			data = null;
			logger.error(msg, e);
		} finally {
			restResultDto.setResult(result);
			restResultDto.setMsg(msg);
			restResultDto.setData(data);
			restResultDto.setException(exception);
		}

		return restResultDto;
	}

	/**
	 * 通过参数类型Code，获取参数列表
	 * 
	 * @param request
	 * @return
	 * 
	 */
	@RequestMapping(value = "getByParamTypeCode", method = { RequestMethod.GET, RequestMethod.POST })
	public RestResultDto getByParamTypeCode2(HttpServletRequest request) {
		RestResultDto restResultDto = new RestResultDto();
		String msg = null;
		Integer result = RestResultDto.RESULT_FAIL;
		Object data = null;
		String exception = null;
		try {
			Map<String, String> paramMap = jm.fromJson(SpringmvcUtils.getParameter(ManagementConstant.REST_PMS), Map.class);
			if (MapUtils.isEmpty(paramMap)) {
				throw new VortexException("参数不能为空");
			}

			String tenantId = paramMap.get(REQ_PARAM_TENATN_ID);
			if (StringUtils.isBlank(tenantId)) {
				throw new VortexException("请传入参数：" + REQ_PARAM_TENATN_ID);
			}

			String paramTypeCode = paramMap.get(REQ_PARAM_TYPE_CODE);
			if (StringUtils.isBlank(paramTypeCode)) {
				throw new VortexException("请传入参数：" + REQ_PARAM_TYPE_CODE);
			}

			if (logger.isDebugEnabled()) {
				logger.debug("getByParamTypeCode(), tenantId=" + tenantId + ",paramTypeCode=" + paramTypeCode);
			}

			List<TenantPramSetting> list = tenantParamSettingService.findListByParamTypeCode(tenantId, paramTypeCode);

			result = RestResultDto.RESULT_SUCC;
			msg = "成功获取到参数列表";
			data = list;
		} catch (Exception e) {
			e.printStackTrace();
			msg = "获取到参数列表失败";
			data = null;
			exception = e.getMessage();
			logger.error(msg, e);
		} finally {
			restResultDto.setData(data);
			restResultDto.setException(exception);
			restResultDto.setResult(result);
			restResultDto.setMsg(msg);
		}
		return restResultDto;
	}

	/**
	 * 根据参数类型code获取参数列表
	 * 
	 * @param request
	 * @return
	 * 
	 */
	@RequestMapping(value = "getByParamTypeCodeList", method = { RequestMethod.GET, RequestMethod.POST })
	public RestResultDto getByParamTypeCodeList(HttpServletRequest request) {
		RestResultDto restResultDto = new RestResultDto();
		String msg = null;
		Integer result = RestResultDto.RESULT_FAIL;
		Object data = null;
		String exception = null;

		try {
			Map<String, Object> paramMap = jm.fromJson(SpringmvcUtils.getParameter(ManagementConstant.REST_PMS), Map.class);
			if (MapUtils.isEmpty(paramMap)) {
				throw new VortexException("参数不能为空");
			}

			String tenantId = (String) paramMap.get(REQ_PARAM_TENATN_ID);
			if (StringUtils.isBlank(tenantId)) {
				throw new VortexException("请传入参数：" + REQ_PARAM_TENATN_ID);
			}

			List<String> paramTypeCodeList = (List<String>) paramMap.get(REQ_PARAM_TYPE_CODE_LIST);
			if (CollectionUtils.isEmpty(paramTypeCodeList)) {
				throw new VortexException("请传入参数：" + REQ_PARAM_TYPE_CODE_LIST);
			}

			if (logger.isDebugEnabled()) {
				logger.debug("getByParamTypeCodeList(), tenantId=" + tenantId + ",paramTypeCodeList=" + paramTypeCodeList);
			}

			Map<String, List<TenantPramSettingDto>> dataMap = tenantParamSettingService.findByParamTypeCodeList(tenantId, paramTypeCodeList);

			result = RestResultDto.RESULT_SUCC;
			msg = "成功获取到参数";
			data = dataMap;
		} catch (Exception e) {
			e.printStackTrace();
			result = RestResultDto.RESULT_FAIL;
			exception = e.getMessage();
			msg = "获取到参数失败";
			data = null;

			logger.error(msg, e);
		} finally {
			restResultDto.setData(data);
			restResultDto.setException(exception);
			restResultDto.setResult(result);
			restResultDto.setMsg(msg);

		}
		return restResultDto;
	}

	/**
	 * 获取指定参数类型下，指定参数Code对应的参数记录
	 * 
	 * @param request
	 * @return
	 * 
	 */
	@RequestMapping(value = "getByCode", method = { RequestMethod.GET, RequestMethod.POST })
	public RestResultDto getByCode(HttpServletRequest request) {
		RestResultDto restResultDto = new RestResultDto();
		String msg = null;
		Integer result = RestResultDto.RESULT_FAIL;
		Object data = null;
		String exception = null;

		try {
			Map<String, String> paramMap = jm.fromJson(SpringmvcUtils.getParameter(ManagementConstant.REST_PMS), Map.class);
			if (MapUtils.isEmpty(paramMap)) {
				throw new VortexException("参数不能为空");
			}

			String tenantId = paramMap.get(REQ_PARAM_TENATN_ID);
			if (StringUtils.isBlank(tenantId)) {
				throw new VortexException("请传入参数：" + REQ_PARAM_TENATN_ID);
			}

			String paramTypeCode = paramMap.get(REQ_PARAM_TYPE_CODE);
			if (StringUtils.isBlank(paramTypeCode)) {
				throw new VortexException("请传入参数：" + REQ_PARAM_TYPE_CODE);
			}

			String paramCode = paramMap.get(REQ_PARAM_CODE);
			if (StringUtils.isBlank(paramCode)) {
				throw new VortexException("请传入参数：" + paramCode);
			}

			if (logger.isDebugEnabled()) {
				logger.debug("getByParamTypeCode(), tenantId=" + tenantId + ",paramTypeCode=" + paramTypeCode + ",paramCode=" + paramCode);
			}

			data = tenantParamSettingService.findOneByParamCode(tenantId, paramTypeCode, paramCode);
			msg = "成功获取参数";
			result = RestResultDto.RESULT_SUCC;
		} catch (Exception e) {
			e.printStackTrace();
			data = null;
			msg = "获取参数失败";
			exception = e.getMessage();
			result = ManagementConstant.REST_RESULT_FAIL;

			logger.error(msg, e);
		} finally {
			restResultDto.setData(data);
			restResultDto.setException(exception);
			restResultDto.setResult(result);
			restResultDto.setMsg(msg);
		}
		return restResultDto;
	}

	/**
	 * 获取指定参数类型下，指定参数Codes对应的参数记录
	 * 
	 * @param request
	 * @return code：TenantPramSetting 的map
	 * 
	 */
	@RequestMapping(value = "getByCodes", method = { RequestMethod.GET, RequestMethod.POST })
	public RestResultDto getByCodes(HttpServletRequest request) {
		Object data = null;
		String msg = null;
		Integer result = RestResultDto.RESULT_FAIL;
		String exception = null;
		RestResultDto rst = new RestResultDto();
		try {
			Map<String, Object> paramMap = jm.fromJson(SpringmvcUtils.getParameter(ManagementConstant.REST_PMS), Map.class);
			if (MapUtils.isEmpty(paramMap)) {
				throw new VortexException("参数不能为空");
			}

			String tenantId = (String) paramMap.get(REQ_PARAM_TENATN_ID);
			if (StringUtils.isBlank(tenantId)) {
				throw new VortexException("请传入参数：" + REQ_PARAM_TENATN_ID);
			}

			String paramTypeCode = (String) paramMap.get(REQ_PARAM_TYPE_CODE);
			if (StringUtils.isBlank(paramTypeCode)) {
				throw new VortexException("请传入参数：" + REQ_PARAM_TYPE_CODE);
			}

			List<String> paramCodes = (List<String>) paramMap.get(REQ_PARAM_CODES);
			if (CollectionUtils.isEmpty(Arrays.asList(paramCodes))) {
				throw new VortexException("请传入参数：" + paramCodes);
			}

			if (logger.isDebugEnabled()) {
				logger.debug("getByParamTypeCode(), tenantId=" + tenantId + ",paramTypeCode=" + paramTypeCode + ",paramCodes=" + paramCodes);
			}

			data = tenantParamSettingService.findListByParamCodes(tenantId, paramTypeCode, paramCodes.toArray(new String[paramCodes.size()]));
			msg = "成功获取参数";
			result = RestResultDto.RESULT_SUCC;
		} catch (Exception e) {
			e.printStackTrace();
			data = null;
			msg = "获取参数失败，" + e.getMessage();

			result = RestResultDto.RESULT_FAIL;
			exception = e.getMessage();
			logger.error(msg, e);
		} finally {

			rst.setData(data);
			rst.setMsg(msg);
			rst.setResult(result);
			rst.setException(exception);

		}
		return rst;
	}

	/**
	 * 获取指定参数类型下，指定参数Codes对应的参数记录
	 * 
	 * @param request
	 * @return code：TenantPramSetting 的map
	 * 
	 */
	@RequestMapping(value = "getParamByCodes", method = { RequestMethod.GET, RequestMethod.POST })
	public RestResultDto getPramByCodes(HttpServletRequest request) {
		Object data = null;
		String msg = null;
		Integer result = ManagementConstant.REST_RESULT_SUCC;
		RestResultDto rst = new RestResultDto();
		String exception = null;
		try {
			Map<String, Object> paramMap = jm.fromJson(SpringmvcUtils.getParameter(ManagementConstant.REST_PMS), Map.class);
			if (MapUtils.isEmpty(paramMap)) {
				throw new VortexException("参数不能为空");
			}

			String tenantId = (String) paramMap.get(REQ_PARAM_TENATN_ID);
			if (StringUtils.isBlank(tenantId)) {
				throw new VortexException("请传入参数：" + REQ_PARAM_TENATN_ID);
			}

			String paramTypeCode = (String) paramMap.get(REQ_PARAM_TYPE_CODE);
			if (StringUtils.isBlank(paramTypeCode)) {
				throw new VortexException("请传入参数：" + REQ_PARAM_TYPE_CODE);
			}

			List<String> paramCodes = (List<String>) paramMap.get(REQ_PARAM_CODES);
			if (CollectionUtils.isEmpty(Arrays.asList(paramCodes))) {
				throw new VortexException("请传入参数：" + paramCodes);
			}

			if (logger.isDebugEnabled()) {
				logger.debug("getByParamTypeCode(), tenantId=" + tenantId + ",paramTypeCode=" + paramTypeCode + ",paramCodes=" + paramCodes);
			}

			data = tenantParamSettingService.findListByParamCodes(tenantId, paramTypeCode, paramCodes.toArray(new String[paramCodes.size()]));
			msg = "成功获取参数";
			result = ManagementConstant.REST_RESULT_SUCC;
		} catch (Exception e) {
			e.printStackTrace();
			data = null;
			msg = "获取参数失败，" + e.getMessage();
			result = ManagementConstant.REST_RESULT_FAIL;
			exception = e.getMessage();
			logger.error(msg, e);
		} finally {
			rst.setException(exception);
			rst.setData(data);
			rst.setMsg(msg);
			rst.setResult(result);

		}
		return rst;
	}

	/**
	 * 获取指定参数类型下，指定参数Name对应的参数记录
	 * 
	 * @param request
	 * @return
	 * 
	 */
	@RequestMapping(value = "getByName", method = { RequestMethod.GET, RequestMethod.POST })
	public RestResultDto getByName(HttpServletRequest request) {
		Object data = null;
		String msg = null;
		Integer result = ManagementConstant.REST_RESULT_SUCC;
		String exception = null;
		RestResultDto rst = new RestResultDto();
		try {
			Map<String, String> paramMap = jm.fromJson(SpringmvcUtils.getParameter(ManagementConstant.REST_PMS), Map.class);
			if (MapUtils.isEmpty(paramMap)) {
				throw new VortexException("参数不能为空");
			}

			String tenantId = paramMap.get(REQ_PARAM_TENATN_ID);
			if (StringUtils.isBlank(tenantId)) {
				throw new VortexException("请传入参数：" + REQ_PARAM_TENATN_ID);
			}

			String paramTypeCode = paramMap.get(REQ_PARAM_TYPE_CODE);
			if (StringUtils.isBlank(paramTypeCode)) {
				throw new VortexException("请传入参数：" + REQ_PARAM_TYPE_CODE);
			}

			String paramName = paramMap.get(REQ_PARAM_NAME);
			if (StringUtils.isBlank(paramName)) {
				throw new VortexException("请传入参数：" + paramName);
			}

			if (logger.isDebugEnabled()) {
				logger.debug("getByParamTypeCode(), tenantId=" + tenantId + ",paramTypeCode=" + paramTypeCode + ",paramName=" + paramName);
			}

			data = tenantParamSettingService.findOneByParamName(tenantId, paramTypeCode, paramName);
			msg = "成功获取参数";
			result = ManagementConstant.REST_RESULT_SUCC;
		} catch (Exception e) {
			e.printStackTrace();
			data = null;
			msg = "获取参数失败，" + e.getMessage();
			result = ManagementConstant.REST_RESULT_FAIL;
			exception = e.getMessage();
			logger.error(msg, e);
		} finally {

			rst.setData(data);
			rst.setMsg(msg);
			rst.setResult(result);
			rst.setException(exception);

		}
		return rst;
	}

	/**
	 * 增加参数
	 * 
	 * @param request
	 * @return
	 * 
	 */

	@SuppressWarnings("finally")
	@RequestMapping(value = "addParam", method = { RequestMethod.GET, RequestMethod.POST })
	public RestResultDto addParam(HttpServletRequest request) {
		Object data = null;
		String msg = null;
		Integer result = RestResultDto.RESULT_SUCC;

		try {

			Map<String, Object> paramMap = jm.fromJson(SpringmvcUtils.getParameter(ManagementConstant.REST_PMS), Map.class);
			if (MapUtils.isEmpty(paramMap)) {
				throw new VortexException("参数不能为空");
			}

			// 参数
			String paramName = (String) paramMap.get(REQ_PARAM_NAME);// 必选
			String paramCode = (String) paramMap.get(REQ_PARAM_CODE);// 必选
			String typeId = (String) paramMap.get(REQ_PARAM_TYPE_ID);// 必选
			Integer orderIndex = (Integer) paramMap.get(ORDER_INDEX);// 可选
			String tenantId = (String) paramMap.get(REQ_PARAM_TENATN_ID);// 必选

			if (StringUtils.isBlank(tenantId)) {
				throw new VortexException("请传入参数：" + REQ_PARAM_TENATN_ID);
			}
			if (StringUtils.isBlank(paramCode)) {
				throw new VortexException("请传入参数：" + REQ_PARAM_CODE);
			}

			if (StringUtils.isBlank(paramName)) {
				throw new VortexException("请传入参数：" + REQ_PARAM_NAME);
			}
			if (StringUtils.isBlank(typeId)) {
				throw new VortexException("请传入参数：" + REQ_PARAM_TYPE_ID);
			}

			if (logger.isDebugEnabled()) {
				logger.debug("getByParamTypeCode(), tenantId=" + tenantId + ",paramName=" + paramName + ",parmCode=" + paramCode + ",typeId" + typeId);
			}
			TenantPramSetting tenantPramSetting = new TenantPramSetting();
			tenantPramSetting.setParmCode(paramCode);
			tenantPramSetting.setParmName(paramName);
			tenantPramSetting.setOrderIndex(orderIndex);
			tenantPramSetting.setTypeId(typeId);
			tenantPramSetting.setTenantId(tenantId);

			data = tenantParamSettingService.save(tenantPramSetting);
			msg = "保存参数成功";
			result = RestResultDto.RESULT_SUCC;
		} catch (Exception e) {
			e.printStackTrace();
			data = null;
			msg = "保存参数失败，" + e.getMessage();
			result = RestResultDto.RESULT_FAIL;

			logger.error(msg, e);
		} finally {
			RestResultDto rst = new RestResultDto();
			rst.setData(data);
			rst.setMsg(msg);
			rst.setResult(result);
			return rst;
		}
	}
}
