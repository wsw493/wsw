package com.vortex.cloud.ums.util.web;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import com.google.common.collect.Maps;
import com.vortex.cloud.ums.util.utils.RestTemplateUtils;
import com.vortex.cloud.vfs.common.exception.VortexException;
import com.vortex.cloud.vfs.data.dto.LoginReturnInfoDto;
import com.vortex.cloud.vfs.data.dto.RestResultDto;

/**
 * @ClassName: BaseController
 * @Description: 该类用于获取cas登录的登录信息，controller只要继承 该类就可以访问登陆者信息
 * @author liShijun
 * @date 2016-4-15 下午09:13:00
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
public abstract class BaseController {
	@Value("${URL_GATEWAY}")
	private String url_gateway;

	private static final Logger logger = LoggerFactory.getLogger(BaseController.class);

	/**
	 * 获取业务系统访问基础设施云系统时的请求参数
	 * 
	 * @return
	 */
	public LoginReturnInfoDto getLoginInfo(HttpServletRequest request) throws Exception {

		LoginReturnInfoDto info = new LoginReturnInfoDto();

		if (logger.isDebugEnabled()) {
			logger.debug("getLoginInfo() will return with login info " + info);
		}

		String userId = request.getHeader("UserId");
		Map<String, String> parameters = Maps.newHashMap();
		parameters.put("userId", userId);
		RestResultDto<Map<String, Object>> restResultDto = RestTemplateUtils.post(url_gateway + "/cloud/management/rest/user/getUserAndStaffById", parameters);
		RestResultDto<List<String>> systemList = RestTemplateUtils.post(url_gateway + "/cloud/management/rest/system/getSystemListByUserId", parameters);

		if (restResultDto == null || restResultDto.getResult().equals(RestResultDto.RESULT_FAIL)) {
			logger.error("根据userId获取用户信息失败");
			throw new VortexException("根据userId获取用户信息失败");
		}
		if (systemList == null || systemList.getResult().equals(RestResultDto.RESULT_FAIL)) {
			logger.error("根据userId获取用户有权限的系统列表失败");
			throw new VortexException("根据userId获取用户有权限的系统列表失败");
		}
		Map<String, Object> attributes = restResultDto.getData();

		// 调用接口来获取人员信息

		info.setUserId(userId);
		String userName = (String) attributes.get("userName");
		info.setUserName(userName);
		String staffId = (String) attributes.get("staffId");
		info.setStaffId(staffId);
		info.setSystemList(systemList.getData());

		if (MapUtils.isNotEmpty((Map) attributes.get("staffDto"))) {
			Map<String, Object> staff = (Map<String, Object>) attributes.get("staffDto");
			String tenantId = (String) staff.get("tenantId");
			info.setTenantId(tenantId);
			String name = (String) staff.get("name");
			info.setName(name);
			String phone = (String) staff.get("phone");
			info.setPhone(phone);
			String email = (String) staff.get("email");
			info.setEmail(email);
			String departmentId = (String) staff.get("departmentId");
			info.setDepartmentId(departmentId);
			String orgId = (String) staff.get("orgId");
			info.setOrgId(orgId);

			String departmentName = (String) staff.get("departmentName");
			info.setDepartmentName(departmentName);
			String orgName = (String) staff.get("orgName");
			info.setOrgName(orgName);
		}
		return info;
	}

	/**
	 * 获取登录用户对应的租户ID
	 * 
	 * @param request
	 * @return
	 * @throws Exception
	 */
	protected String getTenantId(HttpServletRequest request) throws Exception {
		logger.info("getTenantId()");
		return getLoginInfo(request).getTenantId();
	}

	/**
	 * 获取登录用户的Id
	 * 
	 * @param request
	 * @return
	 * @throws Exception
	 */
	protected String getUserId(HttpServletRequest request) throws Exception {
		return getLoginInfo(request).getUserId();
	}
}
