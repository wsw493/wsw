package com.vortex.cloud.ums.web.rest;

import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.vortex.cloud.ums.dataaccess.service.ILoginService;
import com.vortex.cloud.ums.enums.LoginErrEnum;
import com.vortex.cloud.ums.support.ManagementConstant;
import com.vortex.cloud.vfs.common.digest.MD5;
import com.vortex.cloud.vfs.common.exception.VortexException;
import com.vortex.cloud.vfs.common.mapper.JsonMapper;
import com.vortex.cloud.vfs.common.web.springmvc.SpringmvcUtils;
import com.vortex.cloud.vfs.data.dto.RestResultDto;

@SuppressWarnings("all")
@RestController
@RequestMapping("cloud/management/rest/userlogin")
public class UserLoginRestController {
	private Logger logger = LoggerFactory.getLogger(UserLoginRestController.class);
	private JsonMapper jm = new JsonMapper();

	private static final String TEANANT_CODE = "tenantCode";
	private static final String SYSTEM_CODE = "businessSystemCode";
	private static final String USER_NAME = "userName";
	private static final String PASSWORD = "password";
	private static final String IP = "ip";

	@Resource
	private ILoginService loginService;

	/**
	 * 登录信息
	 * 
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "login" + ManagementConstant.PERMISSION_SUFFIX_READ, method = { RequestMethod.GET, RequestMethod.POST })
	public RestResultDto login(HttpServletRequest request) throws Exception {
		String msg = null;
		Integer result = ManagementConstant.REST_RESULT_FAIL;
		Object data = null;
		String exception = null;
		RestResultDto restResultDto = new RestResultDto();

		try {
			Map<String, String> paramMap = jm.fromJson(SpringmvcUtils.getParameter(ManagementConstant.REST_PMS), Map.class);
			if (MapUtils.isEmpty(paramMap)) {
				throw new VortexException("参数不能为空");
			}

			data = loginService.login(paramMap.get(TEANANT_CODE), paramMap.get(SYSTEM_CODE), paramMap.get(USER_NAME), MD5.getMD5(paramMap.get(PASSWORD)), paramMap.get(IP));
			result = ManagementConstant.REST_RESULT_SUCC;
		} catch (Exception e) {
			e.printStackTrace();
			result = ManagementConstant.REST_RESULT_FAIL;
			msg = e.getMessage();
			data = null;
			exception = e.toString();
			logger.error(msg, e);
		} finally {
			restResultDto.setData(data);
			if (StringUtils.isNotEmpty(msg)
					&& !(msg.equals(LoginErrEnum.LOGIN_ERR_NOT_FOUND.getKey()) || msg.equals(LoginErrEnum.LOGIN_ERR_FOUND_MUTI.getKey()) || msg.equals(LoginErrEnum.LOGIN_ERR_PASSWORD.getKey()))) {
				msg = LoginErrEnum.LOGIN_ERR_SYSTEM.getKey();
			}
			restResultDto.setMsg(msg);
			restResultDto.setException(exception);
			restResultDto.setResult(result);
		}

		return restResultDto;
	}
}
