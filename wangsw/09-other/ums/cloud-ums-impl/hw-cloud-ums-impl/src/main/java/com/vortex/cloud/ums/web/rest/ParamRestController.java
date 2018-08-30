package com.vortex.cloud.ums.web.rest;

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
import com.vortex.cloud.ums.util.web.BaseController;
import com.vortex.cloud.vfs.common.exception.VortexException;
import com.vortex.cloud.vfs.common.mapper.JsonMapper;
import com.vortex.cloud.vfs.common.web.springmvc.SpringmvcUtils;
import  com.vortex.cloud.vfs.data.dto.LoginReturnInfoDto;
import com.vortex.cloud.vfs.data.dto.RestResultDto;

/**
 * @author LiShijun
 * @date 2016年3月29日 下午3:50:13
 * @Description 参数管理功能对外提供的RESTful web service History <author> <time> <desc>
 */
@SuppressWarnings("all")
@RestController
@RequestMapping("cloud/management/rest/param")
public class ParamRestController extends BaseController {
	private Logger logger = LoggerFactory.getLogger(ParamRestController.class);
	private JsonMapper jm = new JsonMapper();

	private static final String REQ_PARAM_TYPE_CODE = "paramTypeCode"; // 单个参数类型
	private static final String REQ_PARAM_TYPE_CODE_LIST = "paramTypeCodeList"; // 多个参数类型

	private static final String REQ_PARAM_CODE = "paramCode"; // TenantPramSetting.parmCode
	private static final String REQ_PARAM_NAME = "paramName"; // TenantPramSetting.parmName
	private static final String REQ_PARAM_CODES = "paramCodes";
	private static final String REQ_PARAM_TYPE_ID = "typeId"; // 参数类型
	private static final String ORDER_INDEX = "orderIndex";// 排序号
	@Resource
	private ITenantParamSettingService tenantParamSettingService;

	/**
	 * 通过参数类型，获取下面的参数列表
	 * 
	 * @param request
	 * @return
	 * 
	 */
	@RequestMapping(value = "getByParamTypeCode" + ManagementConstant.PERMISSION_SUFFIX_READ, method = { RequestMethod.GET, RequestMethod.POST })
	public RestResultDto getByParamTypeCode(HttpServletRequest request) {
		RestResultDto rst = new RestResultDto();
		Object data = null;
		String msg = null;
		Integer result = RestResultDto.RESULT_FAIL;
		String exception = null;

		try {
			com.vortex.cloud.vfs.data.dto.LoginReturnInfoDto loginInfo = super.getLoginInfo(request);
			String tenantId = loginInfo.getTenantId();

			Map<String, String> paramMap = jm.fromJson(SpringmvcUtils.getParameter(ManagementConstant.REST_PMS), Map.class);
			if (MapUtils.isEmpty(paramMap)) {
				throw new VortexException("参数不能为空");
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
			result = RestResultDto.RESULT_FAIL;
			msg = e.getMessage();
			exception = e.getMessage();
			data = null;

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
	 * 根据参数类型code获取参数列表
	 * 
	 * @param request
	 * @return
	 * 
	 */
	@RequestMapping(value = "getByParamTypeCodeList" + ManagementConstant.PERMISSION_SUFFIX_READ, method = { RequestMethod.GET, RequestMethod.POST })
	public RestResultDto getByParamTypeCodeList(HttpServletRequest request) {
		RestResultDto rst = new RestResultDto();
		Object data = null;
		String msg = null;
		Integer result = RestResultDto.RESULT_FAIL;
		String exception = null;

		try {
			LoginReturnInfoDto loginInfo = super.getLoginInfo(request);
			String tenantId = loginInfo.getTenantId();

			Map<String, Object> paramMap = jm.fromJson(SpringmvcUtils.getParameter(ManagementConstant.REST_PMS), Map.class);
			if (MapUtils.isEmpty(paramMap)) {
				throw new VortexException("参数不能为空");
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
			msg = e.getMessage();
			exception = e.getMessage();
			data = null;

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
	 * 获取指定参数类型下，指定参数Code对应的参数记录
	 * 
	 * @param request
	 * @return
	 * 
	 */
	@RequestMapping(value = "getByCode", method = { RequestMethod.GET, RequestMethod.POST })
	public RestResultDto getByCode(HttpServletRequest request) {
		Object data = null;
		String msg = null;
		Integer result = RestResultDto.RESULT_SUCC;
		RestResultDto rst = new RestResultDto();
		String exception = null;
		try {
			LoginReturnInfoDto loginInfo = super.getLoginInfo(request);
			String tenantId = loginInfo.getTenantId();

			Map<String, String> paramMap = jm.fromJson(SpringmvcUtils.getParameter(ManagementConstant.REST_PMS), Map.class);
			if (MapUtils.isEmpty(paramMap)) {
				throw new VortexException("参数不能为空");
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
			msg = "获取参数失败，" + e.getMessage();
			result = RestResultDto.RESULT_FAIL;
			exception = e.getMessage();
			logger.error(msg, e);
		} finally {
			rst.setException(exception);
			rst.setData(data);
			rst.setMsg(msg);
			rst.setResult(result);
			return rst;
		}
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
		Integer result = RestResultDto.RESULT_SUCC;
		RestResultDto rst = new RestResultDto();
		String exception = null;
		try {
			LoginReturnInfoDto loginInfo = super.getLoginInfo(request);
			String tenantId = loginInfo.getTenantId();

			Map<String, Object> paramMap = jm.fromJson(SpringmvcUtils.getParameter(ManagementConstant.REST_PMS), Map.class);
			if (MapUtils.isEmpty(paramMap)) {
				throw new VortexException("参数不能为空");
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
		Integer result = RestResultDto.RESULT_SUCC;

		try {
			LoginReturnInfoDto loginInfo = super.getLoginInfo(request);
			String tenantId = loginInfo.getTenantId();

			Map<String, String> paramMap = jm.fromJson(SpringmvcUtils.getParameter(ManagementConstant.REST_PMS), Map.class);
			if (MapUtils.isEmpty(paramMap)) {
				throw new VortexException("参数不能为空");
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
			result = RestResultDto.RESULT_SUCC;
		} catch (Exception e) {
			e.printStackTrace();
			data = null;
			msg = "获取参数失败，" + e.getMessage();
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

	/**
	 * 增加参数
	 * 
	 * @param request
	 * @return
	 * 
	 */
	@RequestMapping(value = "addParam", method = { RequestMethod.GET, RequestMethod.POST })
	public RestResultDto addParam(HttpServletRequest request) {
		Object data = null;
		String msg = null;
		Integer result = RestResultDto.RESULT_SUCC;

		try {
			LoginReturnInfoDto loginInfo = super.getLoginInfo(request);
			String tenantId = loginInfo.getTenantId();

			Map<String, Object> paramMap = jm.fromJson(SpringmvcUtils.getParameter(ManagementConstant.REST_PMS), Map.class);
			if (MapUtils.isEmpty(paramMap)) {
				throw new VortexException("参数不能为空");
			}

			// 参数
			String paramName = (String) paramMap.get(REQ_PARAM_NAME);// 必选
			String paramCode = (String) paramMap.get(REQ_PARAM_CODE);// 必选
			String typeId = (String) paramMap.get(REQ_PARAM_TYPE_ID);// 必选
			Integer orderIndex = (Integer) paramMap.get(ORDER_INDEX);// 可选

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
