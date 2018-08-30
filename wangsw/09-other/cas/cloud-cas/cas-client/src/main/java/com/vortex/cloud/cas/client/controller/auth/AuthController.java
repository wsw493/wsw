package com.vortex.cloud.cas.client.controller.auth;

import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeResourceDetails;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vortex.cloud.cas.client.util.HttpHelper;
import com.vortex.cloud.vfs.data.dto.RestResultDto;

/**
 * File Name : AuthController Author : luhao Create Date : 2016/8/26 Description
 * : Reviewed By : Reviewed On : Version History : Modified By : Modified Date :
 * Comments : CopyRight : COPYRIGHT(c) www.XXXXX.com All Rights Reserved
 * *******************************************************************************************
 */
@RestController
@RequestMapping("/cas")
public class AuthController {
	@Autowired
	private AuthorizationCodeResourceDetails client;

	@Autowired
	private HttpHelper httpHelper;

	@RequestMapping(value = "/login", method = { RequestMethod.GET, RequestMethod.POST })
	public RestResultDto<?> postAccessToken(@RequestBody Map<String, String> parameters, HttpServletResponse response) {
		parameters.put("grant_type", "password");
		return httpHelper.getAccessToken(response, client.getClientId(), client.getClientSecret(), parameters);
	}

	@RequestMapping(value = "/refreshToken", method = { RequestMethod.GET, RequestMethod.POST })
	public RestResultDto<?> refreshAccessToken(@RequestParam Map<String, String> parameters, HttpServletResponse response) {
		parameters.put("grant_type", "refresh_token");
		return httpHelper.getAccessToken(response, client.getClientId(), client.getClientSecret(), parameters);
	}

}
