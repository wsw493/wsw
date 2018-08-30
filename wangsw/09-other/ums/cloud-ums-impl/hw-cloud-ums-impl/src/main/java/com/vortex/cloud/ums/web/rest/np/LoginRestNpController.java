package com.vortex.cloud.ums.web.rest.np;

import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.vortex.cloud.ums.dataaccess.service.ICloudUserService;
import com.vortex.cloud.ums.dataaccess.service.ILoginService;
import com.vortex.cloud.ums.util.tree.ITreeService;
import com.vortex.cloud.vfs.common.exception.VortexException;
import com.vortex.cloud.vfs.data.dto.RestResultDto;

@RequestMapping("cloud/management/rest/np/login")
@RestController
public class LoginRestNpController {
	private Logger logger = LoggerFactory.getLogger(LoginRestNpController.class);
	@Resource
	private ICloudUserService cloudUserService;
	@Resource
	private ILoginService loginService;
	@Resource
	private ITreeService treeService;

	/**
	 * 根据人员用户名，得到人员基本信息
	 * 
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "login", method = { RequestMethod.GET, RequestMethod.POST })
	public RestResultDto<Map<String, String>> getUserByUserName(HttpServletRequest request, @RequestBody Map<String, String> params) throws Exception {

		try {

			String account = params.get("account");
			String password = params.get("password");
			String ip = params.get("ip");
			if (StringUtils.isBlank(password)) {
				throw new VortexException("请传入参数：password");
			}

			if (StringUtils.isBlank(account)) {
				throw new VortexException("请传入参数：account");
			}

			// 查询人员信息
			Map<String, String> userInfo = loginService.login(account, password, ip);
			return RestResultDto.newSuccess(userInfo);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("登录失败", e);
			return RestResultDto.newFalid("登录失败", e.getMessage());

		}
	}

}
