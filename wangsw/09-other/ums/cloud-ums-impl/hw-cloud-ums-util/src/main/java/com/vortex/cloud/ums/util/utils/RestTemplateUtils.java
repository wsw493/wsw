package com.vortex.cloud.ums.util.utils;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.google.common.collect.Maps;
import com.vortex.cloud.vfs.common.mapper.JsonMapper;
import com.vortex.cloud.vfs.data.dto.RestResultDto;

public class RestTemplateUtils {
	public static final String REST_PMS = "parameters"; // 参数key
	private static JsonMapper mapper = new JsonMapper();

	private static class SingletonRestTemplate {
		/**
		 * 单例对象实例
		 */
		static final RestTemplate INSTANCE = new RestTemplate();
	}

	private RestTemplateUtils() {

	}

	/**
	 * 单例实例
	 */
	public static RestTemplate getInstance() {

		return SingletonRestTemplate.INSTANCE;
	}

	/**
	 * post请求 <br>
	 * 比如参数：parameters={"systemCode":"CLOUD_MANAGEMENT"}
	 * ，那么调用该方法，只需要传调用的url和{"systemCode":"CLOUD_MANAGEMENT"}这个map数据
	 * 
	 * @param url
	 *            url
	 * @param param
	 *            map参数
	 * @return
	 * @throws Exception
	 */
	public static <T> RestResultDto<T> post(String url, Map<String, ?> param) throws Exception {

		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder

				.getRequestAttributes()).getRequest();
		HttpHeaders headers = new HttpHeaders();

		headers.add("Accept", "application/json");
		headers.add("Accpet-Encoding", "gzip");
		headers.add("Content-Encoding", "UTF-8");
		headers.add("Content-Type", "application/json; charset=UTF-8");
		headers.add("Authorization", request.getHeader("Authorization"));
		Map<String, String> paramMap = Maps.newHashMap();

		paramMap.put(REST_PMS, mapper.toJson(param));
		HttpEntity<Map<String, ?>> formEntity = new HttpEntity<Map<String, ?>>(paramMap, headers);
		url = url + "?" + REST_PMS + "={" + REST_PMS + "}";
		RestResultDto<T> restResultDto = RestResultDto.newSuccess();
		return RestTemplateUtils.getInstance().postForObject(url, formEntity, restResultDto.getClass(), paramMap);
	}

	/**
	 * get请求 <br>
	 * 比如参数：parameters={"systemCode":"CLOUD_MANAGEMENT"}
	 * ，那么调用该方法，只需要传调用的url和{"systemCode":"CLOUD_MANAGEMENT"}这个map数据
	 * 
	 * @param url
	 *            url
	 * @param param
	 *            map参数
	 * @return
	 * @throws Exception
	 */
	public static <T> RestResultDto<T> get(String url, Map<String, ?> param) throws Exception {

		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder

				.getRequestAttributes()).getRequest();
		HttpHeaders headers = new HttpHeaders();

		headers.add("Accept", "application/json");
		headers.add("Accpet-Encoding", "gzip");
		headers.add("Content-Encoding", "UTF-8");
		headers.add("Content-Type", "application/json; charset=UTF-8");
		headers.add("Authorization", request.getHeader("Authorization"));
		Map<String, String> paramMap = Maps.newHashMap();
		paramMap.put(REST_PMS, mapper.toJson(param));
		HttpEntity<Map<String, ?>> formEntity = new HttpEntity<Map<String, ?>>(headers);
		url = url + "?" + REST_PMS + "={" + REST_PMS + "}";
		RestResultDto<T> restResultDto = RestResultDto.newSuccess();
		ResponseEntity<? extends RestResultDto> result = RestTemplateUtils.getInstance().exchange(url, HttpMethod.GET, formEntity, restResultDto.getClass(), paramMap);

		return result.getBody();
	}

}