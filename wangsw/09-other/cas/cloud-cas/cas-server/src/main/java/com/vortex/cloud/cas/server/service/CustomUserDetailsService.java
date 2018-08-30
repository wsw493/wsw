package com.vortex.cloud.cas.server.service;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.vortex.cloud.cas.server.constant.CasConstant;
import com.vortex.cloud.cas.server.userdetails.CustomUserDetails;
import com.vortex.cloud.vfs.data.dto.RestResultDto;

/**
 * File Name : CustomUserDetailsService Author : luhao Create Date : 2016/7/29
 * Description : Reviewed By : Reviewed On : Version History : Modified By :
 * Modified Date : Comments : CopyRight : COPYRIGHT(c) www.XXXXX.com All Rights
 * Reserved
 * *******************************************************************************************
 */
@Service
public class CustomUserDetailsService implements ExtendUserDetailsService {

	private static final Logger logger = LoggerFactory.getLogger(CustomUserDetailsService.class);

	/*
	 * @Autowired private IUmsUserAccountFeignClient ums;
	 */

	@Value("${appAuthServiceUrl}")
	private String appAuthServiceUrl;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		// return dealResponse(requestUser(username, "test_tenant_role"),
		// username);
		return null;
	}

	@Override
	public UserDetails loadUserByUsername(String username, String password, String appAuthServiceUrl)
			throws UsernameNotFoundException {
		return dealResponse(requestUser(username, password, "", 1, appAuthServiceUrl), username);
	}

	@Override
	public UserDetails loadUserByUsername(String username, String password,String ip, Integer inside, String appAuthServiceUrl)
			throws UsernameNotFoundException {
		return dealResponse(requestUser(username, password, ip, 1, appAuthServiceUrl), username);
	}

	@Override
	public UserDetails loadUserByUsername(String username, String password, String ip, String appAuthServiceUrl)
			throws UsernameNotFoundException {
		return dealResponse(requestUser(username, password, ip, 1, appAuthServiceUrl), username);
	}

	/**
	 * 调用接口查询用户信息
	 *
	 * @param username
	 * @param password
	 * @param appAuthServiceUrl
	 * @return
	 */
	private Map<Object, Object> requestUser(String username, String password, String ip, Integer inside,
			String appAuthServiceUrl) {

		Map<String, Object> map = Maps.newHashMap();
		map.put("account", username);
		map.put("password", password);
		map.put("ip", ip);

		RestTemplate restTemplate = new RestTemplate();
		reInitMessageConverter(restTemplate);

		HttpEntity<Map> entity = new HttpEntity<>(map);
		ResponseEntity<RestResultDto> responseEntity = restTemplate.exchange(appAuthServiceUrl, HttpMethod.POST, entity,
				RestResultDto.class);

		// 初始result
		RestResultDto<?> result = RestResultDto.newSuccess();
		if (responseEntity.getStatusCode().is2xxSuccessful()) {
			result = responseEntity.getBody();
		}

		return (Map<Object, Object>) result.getData();
		/*
		 * } else { UserDto userDto = new UserDto();
		 * userDto.setAccount(username); userDto.setPassword(password);
		 * 
		 * Result<LoginUserDto> result = ums.login(userDto);
		 * 
		 * if (result.getRc() == 1) { throw new
		 * UsernameNotFoundException("username is not correct"); } else {
		 * LoginUserDto loginUserDto = result.getRet(); Map<Object, Object>
		 * resultMap = new BeanMap(loginUserDto); return resultMap; } }
		 */
	}

	/**
	 * 处理接口返回结果
	 *
	 * @param responseMap
	 * @param username
	 * @return
	 */
	private UserDetails dealResponse(Map<Object, Object> responseMap, String username) {
		logger.info("用户信息：" + responseMap);
		if (MapUtils.isEmpty(responseMap)) {
			logger.error("返回结果为空");
			throw new UsernameNotFoundException("用户名：" + username + "不存在");
		}

		if (CasConstant.FAILURE_RESULT_CODE.equals(responseMap.get("result"))) {
			logger.error("查询失败");
			throw new UsernameNotFoundException((String) responseMap.get("errMsg"));
		}

		// 用户服务获取用户的信息并比对
		CustomUserDetails userDetails = new CustomUserDetails(getValue(responseMap, "account"),
				getValue(responseMap, "password"), Lists.newArrayList());
		userDetails.setUserCode(getValue(responseMap, "id"));

		return userDetails;

	}

	/**
	 * 从map中获取value
	 *
	 * @param paramMap
	 * @param key
	 * @param <T>
	 * @return
	 */
	private <T> T getValue(Map<Object, Object> paramMap, String key) {
		return (T) paramMap.get(key);
	}

	/**
	 * 重新设置restTemplate的字符转换字符集
	 *
	 * @param restTemplate
	 */
	private void reInitMessageConverter(RestTemplate restTemplate) {
		List<HttpMessageConverter<?>> converterList = restTemplate.getMessageConverters();
		HttpMessageConverter<?> converterTarget = null;
		for (HttpMessageConverter<?> item : converterList) {
			if (item.getClass() == StringHttpMessageConverter.class) {
				converterTarget = item;
				break;
			}
		}
		if (converterTarget != null) {
			converterList.remove(converterTarget);
		}
		HttpMessageConverter<?> converter = new StringHttpMessageConverter(StandardCharsets.UTF_8);
		converterList.add(converter);
	}
}
