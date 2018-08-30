package com.vortex.cloud.cas.client.util;

import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

//import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.resource.ResourceServerProperties;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeResourceDetails;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.stereotype.Component;
import org.springframework.util.Base64Utils;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Preconditions;
import com.vortex.cloud.vfs.data.dto.RestResultDto;

/**
 * Created by luhao on 2017/4/10.
 */
@Component
public class HttpHelper {

	@Autowired
	private ResourceServerProperties resourceServerProperties;

	@Autowired
	private AuthorizationCodeResourceDetails client;

	private static final Logger logger = LoggerFactory.getLogger(HttpHelper.class);

	/**
	 * 登录获取access_token并获取用的唯一编码
	 *
	 * @param response
	 * @param appId
	 * @param appSecret
	 * @param parameters
	 * @return
	 */
	public RestResultDto<?> getAccessToken(HttpServletResponse response, String appId, String appSecret,
			Map<String, String> parameters) {
		Preconditions.checkNotNull(appId, "请配置security.oauth2.client.client-id");
		Preconditions.checkNotNull(appSecret, "请配置security.oauth2.client.client-secret");

		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "text/html");
		headers.add("Accept",
				"text/html,application/xhtml+xml,application/xml,application/json;q=0.9,image/webp,*/*;q=0.8");
		headers.add("Accept-Encoding", "gzip, deflate, sdch");
		headers.add("Cache-Control", "max-age=0");
		headers.add("Connection", "keep-alive");
		headers.add("Authorization",
				"Basic ".concat(Base64Utils.encodeToString(appId.concat(":").concat(appSecret).getBytes())));

		HttpComponentsClientHttpRequestFactory clientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory();

		RestTemplate restTemplate = new RestTemplate(clientHttpRequestFactory);
		Set<String> keySet = parameters.keySet();
		StringBuilder stringBuilder = new StringBuilder();
		int index = 0;
		for (String key : keySet) {
			if (index == 0) {
				stringBuilder.append("?");
			} else {
				stringBuilder.append("&");
			}
			stringBuilder.append(key).append("={").append(key).append("}");
			index++;
		}

		try {
			ResponseEntity responseEntity = restTemplate.exchange(client.getAccessTokenUri() + stringBuilder.toString(),
					HttpMethod.POST, new HttpEntity<>(headers), DefaultOAuth2AccessToken.class, parameters);

			OAuth2AccessToken auth2AccessToken = (OAuth2AccessToken) responseEntity.getBody();
			response.addHeader("ACCESS-TOKEN", new ObjectMapper().writeValueAsString(auth2AccessToken));
			return getUser(auth2AccessToken.getValue());
		} catch (Exception e) {
			logger.error("access_token转换异常", e);
			if ("password".equals(parameters.get("grant_type"))) {
				return RestResultDto.newFalid("登录失败");
			} else {
				return RestResultDto.newFalid("刷新token失败");
			}
		}

	}

	/**
	 * 獲取用戶信息
	 *
	 * @param accessToken
	 * @return
	 */
	public RestResultDto<?> getUser(String accessToken) {
		HttpHeaders headers = new HttpHeaders();
		// headers.add("SM_USER", username);
		headers.add("Authorization", "Bearer " + accessToken);

		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<RestResultDto> responseEntity = restTemplate.exchange(resourceServerProperties.getUserInfoUri(),
				HttpMethod.POST, new HttpEntity<>(headers), RestResultDto.class);

		return responseEntity.getBody();
	}
}
