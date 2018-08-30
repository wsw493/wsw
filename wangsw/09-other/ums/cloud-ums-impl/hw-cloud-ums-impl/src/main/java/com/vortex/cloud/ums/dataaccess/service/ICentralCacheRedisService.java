package com.vortex.cloud.ums.dataaccess.service;

import java.util.List;
import java.util.Map;

import com.vortex.cloud.vfs.data.redis.ICentralCacheService;

public interface ICentralCacheRedisService extends ICentralCacheService {

	// Value

	<T> List<T> getObjectByKeys(List<String> keys, Class<T> clazz);

	// Map
	<T> void putMap(String key, Map<String, T> map, long expireTime);

	<T> List<T> getMapFields(String key, List<String> fieldName, Class<T> clazz);

	Map<String, Object> getAll(String key);

}
