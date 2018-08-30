package com.vortex.cloud.ums.web.rest;

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

import com.vortex.cloud.ums.dataaccess.service.ICloudSystemService;
import com.vortex.cloud.ums.dataaccess.service.ITenantService;
import com.vortex.cloud.ums.dto.CloudSystemDto;
import  com.vortex.cloud.vfs.data.dto.LoginReturnInfoDto;
import com.vortex.cloud.ums.support.ManagementConstant;
import com.vortex.cloud.ums.util.web.BaseController;
import com.vortex.cloud.vfs.common.exception.VortexException;
import com.vortex.cloud.vfs.common.mapper.JsonMapper;
import com.vortex.cloud.vfs.common.web.springmvc.SpringmvcUtils;
import com.vortex.cloud.vfs.data.dto.RestResultDto;

@SuppressWarnings("all")
@RestController
@RequestMapping("cloud/management/rest/system")
public class SystemRestController extends BaseController {
	private Logger logger = LoggerFactory.getLogger(SystemRestController.class);

	private JsonMapper jm = new JsonMapper();

	private static final String REQ_PARAM_SYSTEM_CODE = "systemCode"; // 系统code
	private static final String REQ_PARAM_SYSTEM_ID = "systemId"; // 系统code

	@Resource
	private ITenantService tenantService;

	@Resource
	private ICloudSystemService cloudSystemService;

	/**
	 * 根据id得到业务系统实体
	 * 
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "getbyid", method = { RequestMethod.GET, RequestMethod.POST })
	public RestResultDto getById(HttpServletRequest request) throws Exception {
		Object data = null;
		String msg = null;
		Integer result = RestResultDto.RESULT_SUCC;
		String exception = null;
		RestResultDto rst = new RestResultDto();

		try {
			Map<String, Object> pms = jm.fromJson(SpringmvcUtils.getParameter(ManagementConstant.REST_PMS), Map.class);
			if (MapUtils.isEmpty(pms) || pms.get(REQ_PARAM_SYSTEM_ID) == null) {
				throw new VortexException("传入的参数为空！");
			}

			data = cloudSystemService.getCloudSystemById(pms.get(REQ_PARAM_SYSTEM_ID).toString());
			msg = "获取租户成功";
		} catch (Exception e) {
			e.printStackTrace();
			result = RestResultDto.RESULT_FAIL;
			exception = e.getMessage();
			msg = "获取租户失败：" + e.getMessage();
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
	 * 根据Code得到业务系统实体
	 * 
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "getByCode", method = { RequestMethod.GET, RequestMethod.POST })
	public RestResultDto getByCode(HttpServletRequest request) throws Exception {
		Object data = null;
		String msg = null;
		Integer result = RestResultDto.RESULT_SUCC;
		String exception = null;
		RestResultDto rst = new RestResultDto();

		try {
			LoginReturnInfoDto loginInfo = super.getLoginInfo(request);
			String tenantId = loginInfo.getTenantId();

			Map<String, String> paramMap = jm.fromJson(SpringmvcUtils.getParameter(ManagementConstant.REST_PMS), Map.class);
			if (MapUtils.isEmpty(paramMap)) {
				throw new VortexException("参数不能为空");
			}

			String systemCode = paramMap.get(REQ_PARAM_SYSTEM_CODE);
			if (StringUtils.isBlank(systemCode)) {
				throw new VortexException("请传入参数：" + REQ_PARAM_SYSTEM_CODE);
			}

			if (logger.isDebugEnabled()) {
				logger.debug("getByCode(), tenantId=" + tenantId + ",systemCode=" + systemCode);
			}

			CloudSystemDto dto = cloudSystemService.getCloudSystemByCode(tenantId, systemCode);

			result = RestResultDto.RESULT_SUCC;
			msg = "成功获取到业务系统";
			data = dto;
		} catch (Exception e) {
			e.printStackTrace();
			msg = e.getMessage();
			result = RestResultDto.RESULT_FAIL;
			data = null;
			exception = e.getMessage();
			logger.error(e.getMessage());
		} finally {
			rst.setData(data);
			rst.setMsg(msg);
			rst.setResult(result);
			rst.setException(exception);
		}
		return rst;
	}

	/**
	 * 根据用户id，得到用户有菜单的系统的列表；list中的每个字符串为“系统code||系统名称”
	 * 
	 * 
	 * 
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "getSystemListByUserId", method = { RequestMethod.GET, RequestMethod.POST })
	public RestResultDto getSystemListByUserId(HttpServletRequest request) throws Exception {
		Object data = null;
		String msg = null;
		Integer result = RestResultDto.RESULT_SUCC;
		String exception = null;
		RestResultDto rst = new RestResultDto();

		try {
			Map<String, Object> pms = jm.fromJson(SpringmvcUtils.getParameter(ManagementConstant.REST_PMS), Map.class);
			if (MapUtils.isEmpty(pms) ) {
				throw new VortexException("传入的参数为空！");
			}

			data = cloudSystemService.getSystemList(pms.get("userId").toString());
			msg = "根据用户id，得到用户有菜单的系统成功";
		} catch (Exception e) {
			e.printStackTrace();
			result = RestResultDto.RESULT_FAIL;
			exception = e.getMessage();
			msg = "根据用户id，得到用户有菜单的系统失败：" + e.getMessage();
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
