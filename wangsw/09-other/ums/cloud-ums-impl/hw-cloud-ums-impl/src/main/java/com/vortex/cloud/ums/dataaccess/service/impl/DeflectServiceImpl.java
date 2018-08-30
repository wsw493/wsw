package com.vortex.cloud.ums.dataaccess.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.JavaType;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.vortex.cloud.ums.dataaccess.service.IDeflectService;
import com.vortex.cloud.ums.util.PropertyUtils;
import com.vortex.cloud.ums.util.utils.ConnectHttpService;
import com.vortex.cloud.vfs.common.mapper.JsonMapper;
import com.vortex.cloud.vfs.data.dto.RestResultDto;





@Service("deflectService")
@Transactional
public class DeflectServiceImpl implements IDeflectService {
	private static final String URL_DEFLECT = PropertyUtils.getPropertyValue("URL_GPS_DEFLECT");
	private static final String URL = URL_DEFLECT + "/vortexapi/rest/deflect/v1/deflectone";

	private static final String URL_LBS = PropertyUtils.getPropertyValue("URL_LBS");
	private static final String REST_FUL = "/vortexapi/rest/lbs/coordconvert/v1";

	@Override
	public String deflect(Double longitude, Double latitude) {
		String positionStr;

		Map<String, Object> map = new HashMap<>();
		Map<String, Object> datamap = new HashMap<>();
		datamap.put("latitude", latitude);
		datamap.put("longitude", longitude);
		map.put("point", datamap);
		map.put("deflectType", "Baidu");
		String restResult = ConnectHttpService.callHttpByParameters(URL, ConnectHttpService.METHOD_POST, map);
		JsonMapper jsonMapper = new JsonMapper();
		JavaType javaType = jsonMapper.contructMapType(HashMap.class, String.class, Object.class);
		Map<String, Object> resultMap = jsonMapper.fromJson(restResult, javaType);
		positionStr = resultMap.get("longitudeDone").toString() + "," + resultMap.get("latitudeDone").toString();
		return positionStr;
	}

	@Override
	public String deflect(String params) throws Exception {
		String result = "";

		Map<String, Object> map = Maps.newHashMap();
		map.put("location", params);
		map.put("from", "bd09");
		map.put("to", "wgs84");

		// 请求数据(获取偏转前 经纬度)
		String restResult = ConnectHttpService.callHttp(URL_LBS + REST_FUL, ConnectHttpService.METHOD_GET, map);
		JsonMapper jm = new JsonMapper();
		Map<String, Object> resultData = jm.fromJson(restResult, HashMap.class);

		if (resultData.get("status") == RestResultDto.RESULT_SUCC) {
			List<Map<String, Object>> locations = (List<Map<String, Object>>) resultData.get("locations");

			for (Map<String, Object> location : locations) {
				result += location.get("longitudeDone") + "," + location.get("latitudeDone") + ";";
			}
			result = params.substring(0, params.length() - 1);
		} else {
			result = params;
		}

		return result;
	}

	@Override
	public String deflectToBD(String params) {
		String result = "";

		Map<String, Object> map = Maps.newHashMap();
		map.put("location", params);
		map.put("from", "wgs84");
		map.put("to", "bd09");

		// 请求数据(获取偏转前 经纬度)
		String restResult = ConnectHttpService.callHttp(URL_LBS + REST_FUL, ConnectHttpService.METHOD_GET, map);
		JsonMapper jm = new JsonMapper();
		Map<String, Object> resultData = jm.fromJson(restResult, HashMap.class);

		if (resultData.get("status") == RestResultDto.RESULT_SUCC) {
			List<Map<String, Object>> locations = (List<Map<String, Object>>) resultData.get("locations");

			for (Map<String, Object> location : locations) {
				result += location.get("longitudeDone") + "," + location.get("latitudeDone") + ";";
			}
			result = params.substring(0, params.length() - 1);
		} else {
			result = params;
		}

		return result;
	}

	public static void main(String[] args) {
		String a = "3.1415926";
		StringBuilder sb = new StringBuilder();
		// System.out.println(Double.parseDouble(a));
		// String url = "http://192.168.1.206:8083/vortexapi/rest/deflect/v1/deflectone";
		// Map<String, Object> map = new HashMap<String, Object>();
		Map<String, Object> datamap = new HashMap<>();
		datamap.put("latitude", 39.915);// ,
		datamap.put("longitude", 116.404);
		// map.put("point", datamap);
		// map.put("deflectType", "Baidu");
		// String rst = ConnectHttpService.callHttpByParameters(url, ConnectHttpService.METHOD_POST, map);
		// System.out.println(rst);

		String url2 = "http://192.168.1.206:8083/vortexapi/rest/deflect/v1/deflectpostions";
		Map<String, Object> map2 = new HashMap<>();
		Map<String, Object> datamap2 = new HashMap<>();
		List<Map<String, Object>> list = Lists.newArrayList();
		datamap2.put("latitude", a);
		datamap2.put("longitude", 120);
		list.add(datamap2);
		list.add(datamap);
		map2.put("points", list);
		map2.put("deflectType", "Baidu");
		String restResult = ConnectHttpService.callHttpByParameters(url2, ConnectHttpService.METHOD_POST, map2);
		JsonMapper jsonMapper = new JsonMapper();
		JavaType javaType = jsonMapper.contructCollectionType(List.class, Object.class);
		List<Map<String, Object>> resultList = jsonMapper.fromJson(restResult, javaType);
		for (Map<String, Object> temp : resultList) {
			String lng = temp.get("longitudeDone").toString();
			String lat = temp.get("latitudeDone").toString();
			sb.append(lng).append(",").append(lat).append(";");
		}
		String result = sb.substring(0, sb.length() - 1);

		System.out.println(restResult);
		System.out.println(result);
	}

}
