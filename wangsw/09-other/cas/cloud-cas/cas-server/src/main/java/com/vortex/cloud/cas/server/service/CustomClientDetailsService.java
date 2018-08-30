package com.vortex.cloud.cas.server.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.ClientRegistrationException;
import org.springframework.security.oauth2.provider.client.BaseClientDetails;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;

/**
 * File Name : CustomClientDetailsService Author : luhao Create Date : 2016/7/29
 * Description : Reviewed By : Reviewed On : Version History : Modified By :
 * Modified Date : Comments : CopyRight : COPYRIGHT(c) www.XXXXX.com All Rights
 * Reserved
 * *******************************************************************************************
 */
@Service
public class CustomClientDetailsService implements ClientDetailsService {
	/*
	 * @Autowired private IUmsUserAccountFeignClient userAccountFeignClient;
	 */
	@Value("${appAuthServiceUrl}")
	private String appAuthServiceUrl;

	private static final Logger logger = LoggerFactory.getLogger(CustomClientDetailsService.class);

	@Override
	public ClientDetails loadClientByClientId(String clientId) throws ClientRegistrationException {
		return getBaseClientDetails(clientId);
	}

	/**
	 * @param clientId
	 * @return
	 */
	private BaseClientDetails getBaseClientDetails(String clientId) {

		// Result<?> result = userAccountFeignClient.getAppByAppKey(clientId);
		// // 获取app信息失败
		// if (result.getRc() == Result.FAILD) {
		// logger.error(result.getErr());
		// throw new ClientRegistrationException(result.getErr());
		// }
		//
		// Map<String, String> resultMap = (Map<String, String>)
		// result.getRet();
		// if (MapUtils.isEmpty(resultMap)) {
		// logger.error(" fetch app result is empty! ");
		// throw new ClientRegistrationException(" fetch app result is empty!
		// ");
		// }

		BaseClientDetails baseClientDetails = new BaseClientDetails();
		baseClientDetails.setClientId("fc6fbbc9-6d84-4abf-9e3f-84f285c03286");
		baseClientDetails.setClientSecret("124f1a71-7f2f-410c-ac42-ec48ce2de1c2");
		baseClientDetails.setAuthorizedGrantTypes(Lists.newArrayList("authorization_code", "refresh_token", "password",
				"implicit", "client_credentials"));
		baseClientDetails.setScope(Lists.newArrayList("openid", "read", "write"));
		baseClientDetails.setAutoApproveScopes(Lists.newArrayList("openid", "read", "write"));
		baseClientDetails.isAutoApprove("true");
		// 放入校验用户名和密码的url
		baseClientDetails.addAdditionalInformation("appAuthServiceUrl", appAuthServiceUrl);
		baseClientDetails.addAdditionalInformation("inside", "0");

		return baseClientDetails;

	}

}
