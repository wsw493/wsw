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

import com.vortex.cloud.ums.dataaccess.service.IParamSettingService;
import com.vortex.cloud.ums.dto.rest.PramSettingRestDto;
import com.vortex.cloud.ums.support.ManagementConstant;
import com.vortex.cloud.ums.util.web.BaseController;
import com.vortex.cloud.vfs.common.exception.VortexException;
import com.vortex.cloud.vfs.common.mapper.JsonMapper;
import com.vortex.cloud.vfs.common.web.springmvc.SpringmvcUtils;
import com.vortex.cloud.vfs.data.dto.LoginReturnInfoDto;
import com.vortex.cloud.vfs.data.dto.RestResultDto;

/**
 * 参数模板
 * 
 * @author ll
 *
 */
@SuppressWarnings("all")
@RestController
@RequestMapping("cloud/management/rest/param/setting")
public class ParamSettingRestController extends BaseController {

	private Logger logger = LoggerFactory.getLogger(ParamSettingRestController.class);
	private JsonMapper jm = new JsonMapper();

	private static final String REQ_PARAM_TYPE_CODE = "paramTypeCode"; // 单个参数类型

	@Resource
	private IParamSettingService paramSettingService;

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

			List<PramSettingRestDto> list = paramSettingService.findListByParamTypeCode(paramTypeCode, tenantId);

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

}
