package com.vortex.cloud.ums.web.rest;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.vortex.cloud.ums.dataaccess.service.IManagementRestService;
import  com.vortex.cloud.vfs.data.dto.LoginReturnInfoDto;
import com.vortex.cloud.ums.model.CloudFunction;
import com.vortex.cloud.ums.support.ManagementConstant;
import com.vortex.cloud.ums.util.web.BaseController;
import com.vortex.cloud.vfs.common.exception.VortexException;
import com.vortex.cloud.vfs.common.mapper.JsonMapper;
import com.vortex.cloud.vfs.common.web.springmvc.SpringmvcUtils;
import com.vortex.cloud.vfs.data.dto.RestResultDto;

/**
 * 对外提供人员和功能号相关的接口
 * 
 * @author XY
 *
 */
@SuppressWarnings("all")
@RestController
@RequestMapping("cloud/management/rest/userfunction")
public class UserFunctionController extends BaseController {
	private Logger logger = LoggerFactory.getLogger(UserFunctionController.class);
	private JsonMapper jm = new JsonMapper();
	@Resource
	private IManagementRestService managementRestService;

	private static final Object SYSTEM_CODE = "systemCode";
	private static final Object SYSTEM_ID = "systemId";
	private static final Object USER_ID = "userId";
	private static final Object FUNCTION_CODE = "functionCode";
	private static final Object FUNCTION_ID = "functionId";
	private static final Object ROLE_ID = "roleId";

	/**
	 * 根据系统id、人员id、功能id，判断此人在此系统中是否拥有此功能
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "hasfunction" + ManagementConstant.PERMISSION_SUFFIX_READ, method = { RequestMethod.GET, RequestMethod.POST })
	public RestResultDto hasFunction(HttpServletRequest request) {
		RestResultDto restResultDto = new RestResultDto();
		String msg = null;
		Integer result = null;
		Object data = null;
		String exception = null;
		try {
			Map<String, String> paramMap = jm.fromJson(SpringmvcUtils.getParameter(ManagementConstant.REST_PMS), Map.class);
			if (MapUtils.isEmpty(paramMap)) {
				throw new VortexException("参数不能为空");
			}

			String userId = paramMap.get(USER_ID);
			String systemId = paramMap.get(SYSTEM_ID);
			String functionId = paramMap.get(FUNCTION_ID);

			data = managementRestService.hasFunction(userId, systemId, functionId);
			msg = "获取人员权限信息成功";
		} catch (Exception e) {
			e.printStackTrace();
			result = ManagementConstant.REST_RESULT_FAIL;
			msg = "获取人员权限信息失败：" + e.getMessage();
			data = null;
			exception = e.getMessage();
			logger.error(msg, e);
		} finally {
			restResultDto.setException(exception);
			restResultDto.setData(data);
			restResultDto.setMsg(msg);
			restResultDto.setResult(result);

		}
		return restResultDto;
	}

	/**
	 * 根据功能code和租户id，得到功能信息
	 * 
	 * 
	 * 
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "getfunctionbycode" + ManagementConstant.PERMISSION_SUFFIX_READ, method = { RequestMethod.GET, RequestMethod.POST })
	public RestResultDto getFunctionByCode(HttpServletRequest request) throws Exception {
		String msg = null;
		Integer result = null;
		Object data = null;

		try {
			LoginReturnInfoDto loginInfo = super.getLoginInfo(request);
			String tenantId = loginInfo.getTenantId();

			Map<String, String> paramMap = jm.fromJson(SpringmvcUtils.getParameter(ManagementConstant.REST_PMS), Map.class);
			if (MapUtils.isEmpty(paramMap)) {
				throw new VortexException("参数不能为空");
			}

			String systemCode = paramMap.get(SYSTEM_CODE);
			String functionCode = paramMap.get(FUNCTION_CODE);

			if (StringUtils.isBlank(functionCode)) {
				throw new VortexException("请传入参数：" + FUNCTION_CODE);
			}

			logger.debug("getfunctionbycode(), functionCode=" + functionCode + "tenantId=" + tenantId + "systemCode=" + systemCode);
			CloudFunction cloudFunction = managementRestService.getFunctionByCode(functionCode, tenantId, systemCode);
			result = ManagementConstant.REST_RESULT_SUCC;
			msg = "成功获取功能信息";
			data = cloudFunction;
		} catch (Exception e) {
			e.printStackTrace();
			result = ManagementConstant.REST_RESULT_FAIL;
			msg = e.getMessage();
			data = null;
			logger.error(msg, e);
		} finally {
			RestResultDto restResultDto = new RestResultDto();
			restResultDto.setData(data);
			restResultDto.setMsg(msg);
			restResultDto.setResult(result);
			return restResultDto;
		}
	}

	/**
	 * 根据功能id，得到功能信息
	 * 
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "getfunctionbyid" + ManagementConstant.PERMISSION_SUFFIX_READ, method = { RequestMethod.GET, RequestMethod.POST })
	public RestResultDto getFunctionById(HttpServletRequest request) throws Exception {
		String msg = null;
		Integer result = null;
		Object data = null;

		try {

			Map<String, String> paramMap = jm.fromJson(SpringmvcUtils.getParameter(ManagementConstant.REST_PMS), Map.class);
			if (MapUtils.isEmpty(paramMap)) {
				throw new VortexException("参数不能为空");
			}

			String functionId = paramMap.get(FUNCTION_ID);
			if (StringUtils.isBlank(functionId)) {
				throw new VortexException("请传入参数：" + FUNCTION_ID);
			}

			if (logger.isDebugEnabled()) {
				logger.debug("getFunctionById(), functionId=" + functionId);
			}

			CloudFunction cloudFunction = managementRestService.getFunctionById(functionId);
			result = ManagementConstant.REST_RESULT_SUCC;
			msg = "成功获取功能信息";
			data = cloudFunction;
		} catch (Exception e) {
			e.printStackTrace();
			result = ManagementConstant.REST_RESULT_FAIL;
			msg = e.getMessage();
			data = null;
			logger.error(msg, e);
		} finally {
			RestResultDto restResultDto = new RestResultDto();
			restResultDto.setData(data);
			restResultDto.setMsg(msg);
			restResultDto.setResult(result);
			return restResultDto;
		}
	}

	/**
	 * 根据角色id，得到角色上面所有功能id的列表
	 * 
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "getfunctionsbyroleid" + ManagementConstant.PERMISSION_SUFFIX_READ, method = { RequestMethod.GET, RequestMethod.POST })
	public RestResultDto getFunctionsByRoleId(HttpServletRequest request) throws Exception {
		String msg = null;
		Integer result = null;
		Object data = null;

		try {
			Map<String, String> paramMap = jm.fromJson(SpringmvcUtils.getParameter(ManagementConstant.REST_PMS), Map.class);
			if (MapUtils.isEmpty(paramMap)) {
				throw new VortexException("参数不能为空");
			}

			String roleId = paramMap.get(ROLE_ID);
			if (StringUtils.isBlank(roleId)) {
				throw new VortexException("请传入参数：" + ROLE_ID);
			}

			if (logger.isDebugEnabled()) {
				logger.debug("getFunctionsByRoleId(), roleId=" + roleId);
			}
			List<String> ids = managementRestService.getFunctionsByRoleId(roleId);
			result = ManagementConstant.REST_RESULT_SUCC;
			msg = "成功获取功能列表信息";
			data = ids;
		} catch (Exception e) {
			e.printStackTrace();
			result = ManagementConstant.REST_RESULT_FAIL;
			msg = e.getMessage();
			data = null;
			logger.error(msg, e);
		} finally {
			RestResultDto restResultDto = new RestResultDto();
			restResultDto.setData(data);
			restResultDto.setMsg(msg);
			restResultDto.setResult(result);
			return restResultDto;
		}
	}

}
