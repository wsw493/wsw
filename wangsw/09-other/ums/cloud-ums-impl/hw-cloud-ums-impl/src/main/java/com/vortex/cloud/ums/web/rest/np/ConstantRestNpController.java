package com.vortex.cloud.ums.web.rest.np;

import java.util.Arrays;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.vortex.cloud.ums.dataaccess.service.IRedisValidateService;
import com.vortex.cloud.ums.dataaccess.service.ITenantConstantService;
import com.vortex.cloud.ums.model.CloudConstant;
import com.vortex.cloud.ums.support.ManagementConstant;
import com.vortex.cloud.vfs.common.exception.VortexException;
import com.vortex.cloud.vfs.common.lang.StringUtil;
import com.vortex.cloud.vfs.common.mapper.JsonMapper;
import com.vortex.cloud.vfs.common.web.springmvc.SpringmvcUtils;
import com.vortex.cloud.vfs.data.dto.RestResultDto;

@SuppressWarnings("all")
@RestController
@RequestMapping("cloud/management/rest/np/constant")
public class ConstantRestNpController {

	private static final Object CONSTANT_CODE = "constantCode";
	private static final Object TENANT_CODE = "tenantCode";
	private JsonMapper jm = new JsonMapper();
	private Logger logger = LoggerFactory.getLogger(ConstantRestNpController.class);

	@Resource
	private ITenantConstantService tenantConstantService;
	@Resource
	private IRedisValidateService redisValidateService;
	/**
	 * 根据租户code和常量code获取常量
	 * 
	 * @return
	 */
	@RequestMapping(value = "getConstantByCode" + ManagementConstant.BACK_DYNAMIC_SUFFIX, method = { RequestMethod.GET,
			RequestMethod.POST })
	public RestResultDto getConstantByCode() {

		String msg = null;
		Integer result = ManagementConstant.REST_RESULT_SUCC;
		Object data = null;
		JsonMapper mapper = new JsonMapper();

		try {
			Map<String, Object> paramMap = jm.fromJson(SpringmvcUtils.getParameter(ManagementConstant.REST_PMS),
					Map.class);
			if (MapUtils.isEmpty(paramMap)) {
				throw new VortexException("参数不能为空");
			}

			String constantCode = (String) paramMap.get(CONSTANT_CODE);
			String tenantCode = (String) paramMap.get(TENANT_CODE);
			if (StringUtils.isEmpty(constantCode)) {
				throw new VortexException("常量code不能为空");
			}
			if (StringUtils.isEmpty(tenantCode)) {
				throw new VortexException("租户code不能为空");
			}

			CloudConstant cloudConstant = tenantConstantService.getConstantByCode(constantCode, tenantCode);
			data = cloudConstant;
			msg = "获取常量成功";

		} catch (Exception e) {
			e.printStackTrace();
			result = ManagementConstant.REST_RESULT_FAIL;
			msg = "获取常量失败：" + e.getMessage();
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

	@RequestMapping(value = "testStaffRedis", method = { RequestMethod.GET, RequestMethod.POST })
	public RestResultDto testRedis() {

		String msg = null;
		Integer result = ManagementConstant.REST_RESULT_SUCC;
		Object data = null;
		JsonMapper mapper = new JsonMapper();

		try {
			Map<String, Object> paramMap = jm.fromJson(SpringmvcUtils.getParameter(ManagementConstant.REST_PMS),
					Map.class);
			if (MapUtils.isEmpty(paramMap)) {
				throw new VortexException("参数不能为空");
			}

			String ids = (String) paramMap.get("ids");

			data = redisValidateService.getStaffListByIds(Arrays.asList(StringUtil.splitComma(ids)));
			msg = "获取人员（redis）成功";

		} catch (Exception e) {
			e.printStackTrace();
			result = ManagementConstant.REST_RESULT_FAIL;
			msg = "获取人员（redis）失败：" + e.getMessage();
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
}
