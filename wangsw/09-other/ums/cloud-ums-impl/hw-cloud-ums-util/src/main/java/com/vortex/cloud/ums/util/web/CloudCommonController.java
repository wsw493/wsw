package com.vortex.cloud.ums.util.web;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.collect.Maps;
import com.vortex.cloud.ums.util.utils.RestTemplateUtils;
import com.vortex.cloud.vfs.common.exception.VortexException;
import com.vortex.cloud.vfs.common.mapper.JsonMapper;
import com.vortex.cloud.vfs.common.web.springmvc.SpringmvcUtils;
import com.vortex.cloud.vfs.data.dto.LoginReturnInfoDto;
import com.vortex.cloud.vfs.data.dto.RestResultDto;

@RestController
@RequestMapping("cloud/management/util")
public class CloudCommonController extends BaseController {
	private static final String USER_ID = "userId";
	private static final String SYSTEM_CODE = "systemCode";
	private Logger logger = LoggerFactory.getLogger(CloudCommonController.class);
	private JsonMapper jm = new JsonMapper();

	@Value("${URL_GATEWAY}")
	private String url_gateway;

	/**
	 * 获取文件服务地址
	 * 
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "fileServerAddress.sa", method = { RequestMethod.GET, RequestMethod.POST })
	public Map<String, String> getFileServerAddress(HttpServletRequest request, HttpServletResponse response) {
		Map<String, String> map = new HashMap<String, String>();
		String fileServerAdderss = String.valueOf(request.getServletContext().getAttribute("fileServer"));
		map.put("fileServer", fileServerAdderss);
		return map;
	}

	/**
	 * 获取菜单数据
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "getMenuJson.sa", method = { RequestMethod.GET, RequestMethod.POST })
	public RestResultDto<Map<String, Object>> getMenuJson(HttpServletRequest request, HttpServletResponse response) {
		try {
			if (StringUtils.isEmpty(url_gateway)) {
				throw new VortexException("请在cloud.properties中配置management地址属性：url_gateway");
			}
			String userId = SpringmvcUtils.getParameter("userId");
			String systemCode = SpringmvcUtils.getParameter("systemCode");

			// 组参数
			Map<String, Object> parameters = Maps.newHashMap();
			parameters.put(USER_ID, userId);
			parameters.put(SYSTEM_CODE, systemCode);

			RestResultDto<Map<String, Object>> aDto = RestTemplateUtils.post(url_gateway + "/cloud/management/rest/menu/getmenujson.read", parameters);

			if (aDto == null || RestResultDto.RESULT_FAIL.equals(aDto.getResult())) {
				String errMsg = "getMenuJson(), userId=" + userId + ",systemCode=" + systemCode + ", 调用REST服务异常";
				logger.error(errMsg);
				throw new VortexException(errMsg);
			}
			// 从中获取数据
			return aDto;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("getMenuJson()" + e.getMessage());
			return RestResultDto.newFalid("获取菜单列表出错", e.getMessage());
		}

	}

	/**
	 * 修改密码
	 * 
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "changePassword.sa", method = { RequestMethod.GET, RequestMethod.POST })
	public RestResultDto<String> changePassword(HttpServletRequest request, HttpServletResponse response) {
		try {
			String oldPassword = SpringmvcUtils.getParameter("oldPassword");
			String newPassword = SpringmvcUtils.getParameter("newPassword");
			Map<String, Object> parameters = Maps.newHashMap();
			parameters.put("oldPassword", oldPassword);
			parameters.put("newPassword", newPassword);
			// 调用rest服务
			String jsonStr = "";
			if (StringUtils.isEmpty(url_gateway)) {
				throw new VortexException("请在cloud.properties中配置management地址属性：url_gateway");
			}
			String url = url_gateway + "/cloud/management/rest/user/changepasswordbyjson.read";

			RestResultDto<String> restResultDto = RestTemplateUtils.post(url, parameters);
			if (restResultDto == null) {
				logger.error("修改密码出错");
				throw new VortexException("修改密码出错");
			}
			return restResultDto;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("changePassword()" + e.getMessage());
			return RestResultDto.newFalid("修改密码出错", e.getMessage());
		}

	}

	@SuppressWarnings({ "unchecked" })
	@RequestMapping(value = "hasFunctions", method = { RequestMethod.GET, RequestMethod.POST })
	public RestResultDto<Map<String, Boolean>> hasFunctions(HttpServletRequest request) throws Exception {
		try {

			Map<String, Object> paramMap = jm.fromJson(SpringmvcUtils.getParameter("parameters"), Map.class);
			if (MapUtils.isEmpty(paramMap)) {
				throw new VortexException("参数不能为空");
			}

			if (StringUtils.isEmpty(url_gateway)) {
				throw new VortexException("请在cloud.properties中配置management地址属性：url_gateway");
			}

			RestResultDto<Map<String, Boolean>> reString = RestTemplateUtils.get(url_gateway + "/cloud/management/rest/permission/hasFunctions", paramMap);
			return reString;

		} catch (Exception e) {
			e.printStackTrace();

			logger.error("获取该用户的权限列表出错", e);

			return RestResultDto.newFalid("获取该用户的权限列表出错", e.getMessage());
		}
	}

	@RequestMapping(value = "logininfo.sa", method = { RequestMethod.GET, RequestMethod.POST })
	public RestResultDto<LoginReturnInfoDto> logininfo(HttpServletRequest request, HttpServletResponse response) {
		try {
			LoginReturnInfoDto loginReturnInfoDto = super.getLoginInfo(request);
			return RestResultDto.newSuccess(loginReturnInfoDto);
		} catch (Exception e) {
			logger.error("获取用户信息失败", e);
			return RestResultDto.newFalid("获取用户信息失败", e.getMessage());
		}

	}

	@RequestMapping(value = "getSystemIdByCode", method = { RequestMethod.GET, RequestMethod.POST })
	public RestResultDto<String> getSystemIdByCode(HttpServletRequest request, HttpServletResponse response) {
		try {

			String systemCode = SpringmvcUtils.getParameter("systemCode");
			// 组参数
			Map<String, Object> parameters = Maps.newHashMap();
			parameters.put("systemCode", systemCode);

			RestResultDto<Map<String, Object>> aDto = RestTemplateUtils.get(url_gateway + "/cloud/management/rest/system/getByCode", parameters);
			if (aDto == null || aDto.getResult().equals(RestResultDto.RESULT_FAIL)) {
				logger.error("根据系统code获取系统id出错 ");
				throw new VortexException("根据系统code获取系统id出错 ");
			}
			return RestResultDto.newSuccess((String) aDto.getData().get("id"));
		} catch (Exception e) {
			logger.error("根据系统code获取系统id出错", e);
			return RestResultDto.newFalid("根据系统code获取系统id出错", e.getMessage());
		}

	}

}
