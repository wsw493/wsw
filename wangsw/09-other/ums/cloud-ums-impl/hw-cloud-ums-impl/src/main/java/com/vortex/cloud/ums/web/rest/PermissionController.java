package com.vortex.cloud.ums.web.rest;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.vortex.cloud.ums.dataaccess.service.IRedisValidateService;
import com.vortex.cloud.ums.support.ManagementConstant;
import com.vortex.cloud.ums.util.web.BaseController;
import com.vortex.cloud.vfs.common.exception.VortexException;
import com.vortex.cloud.vfs.common.mapper.JsonMapper;
import com.vortex.cloud.vfs.common.web.springmvc.SpringmvcUtils;
import com.vortex.cloud.vfs.data.dto.RestResultDto;

/**
 * 提供给其他项目的权限验证接口
 * 
 * @author XY
 *
 */
@RestController
@RequestMapping("cloud/management/rest/permission")
public class PermissionController extends BaseController {
	private JsonMapper jm = new JsonMapper();
	private Logger logger = LoggerFactory.getLogger(PermissionController.class);
	private static final String USER_ID = "userId";
	private static final String FUNCTION_CODE = "functionCode";
	private static final String FUNCTION_CODES = "functionCodes";
	@Resource
	private IRedisValidateService redisValidateService;

	@SuppressWarnings({ "unchecked", "finally" })
	@RequestMapping(value = "hasfunction" + ManagementConstant.PERMISSION_SUFFIX_READ, method = { RequestMethod.GET, RequestMethod.POST })
	public RestResultDto getByParamTypeCode(HttpServletRequest request) throws Exception {
		String msg = null;
		Integer result = ManagementConstant.REST_RESULT_SUCC;
		Object data = null;

		try {
			Map<String, String> paramMap = jm.fromJson(SpringmvcUtils.getParameter(ManagementConstant.REST_PMS), Map.class);
			if (MapUtils.isEmpty(paramMap)) {
				throw new VortexException("参数不能为空");
			}

			String userId = paramMap.get(USER_ID);
			String functionCode = paramMap.get(FUNCTION_CODE);

			data = redisValidateService.hasFunction(userId, functionCode);
		} catch (Exception e) {
			e.printStackTrace();
			result = ManagementConstant.REST_RESULT_FAIL;
			msg = e.getMessage();
			data = null;

			logger.error(msg, e);
		} finally {
			RestResultDto rst = new RestResultDto();
			rst.setResult(result);
			rst.setMsg(msg);
			rst.setData(data);
			return rst;
		}
	}

	@SuppressWarnings({ "unchecked" })
	@RequestMapping(value = "hasFunctions", method = { RequestMethod.GET, RequestMethod.POST })
	public RestResultDto<Map<String, Boolean>> hasFunctions(HttpServletRequest request) throws Exception {

		try {

			Map<String, Object> paramMap = jm.fromJson(SpringmvcUtils.getParameter(ManagementConstant.REST_PMS), Map.class);
			if (MapUtils.isEmpty(paramMap)) {
				throw new VortexException("参数不能为空");
			}

			String userId = super.getLoginInfo(request).getUserId();
			List<String> functionCodes = (List<String>) paramMap.get(FUNCTION_CODES);
			Map<String, Boolean> functions = redisValidateService.hasFunction(userId, functionCodes);

			return RestResultDto.newSuccess(functions);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("获取该用户的权限列表出错", e);
			return RestResultDto.newFalid("获取该用户的权限列表出错", e.getMessage());
		}

	}
}
