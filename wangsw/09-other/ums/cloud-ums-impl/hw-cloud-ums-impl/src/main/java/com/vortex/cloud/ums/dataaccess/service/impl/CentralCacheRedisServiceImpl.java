package com.vortex.cloud.ums.dataaccess.service.impl;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.BoundListOperations;
import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.BoundZSetOperations;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.vortex.cloud.ums.dataaccess.service.ICentralCacheRedisService;

/**
 * Created by sylar on 2017/1/26.
 */
@Service(CentralCacheRedisServiceImpl.CLASSNAME)
public class CentralCacheRedisServiceImpl implements ICentralCacheRedisService {
	public static final String CLASSNAME = "centralCacheRedisService";
	@Autowired
	private RedisTemplate<String, Object> redisTemplate;

	@Override
	public boolean containsKey(String key) {
		return redisTemplate.hasKey(key);
	}

	@Override
	public void removeObject(String key) {
		redisTemplate.delete(key);
	}

	@Override
	public void removeObjects(Collection<String> keys) {
		redisTemplate.delete(keys);
	}

	@Override
	public <T> T getObject(String key, Class<T> clazz) {
		if (containsKey(key)) {
			return (T) valOps(key).get();
		} else {
			return null;
		}
	}

	@Override
	public <T> List<T> getObjectByKeys(List<String> keys, Class<T> clazz) {
		return (List<T>) redisTemplate.opsForValue().multiGet(keys);
	}

	@Override
	public <T> void putObject(String key, T t) {
		if (t == null)
			return;
		Object value = t;
		valOps(key).set(value);
	}

	@Override
	public <T> void putObjectWithExpireTime(String key, T t, long expireTime) {
		if (t == null)
			return;
		Object value = t;
		valOps(key).set(value, expireTime, TimeUnit.SECONDS);
	}

	@Override
	public <T> void putMapValue(String key, String fieldName, T t, long expireTime) {
		Object value = t;
		BoundHashOperations<String, String, Object> ops = mapOps(key);
		ops.put(fieldName, value);
		if (expireTime > 0) {
			ops.expire(expireTime, TimeUnit.SECONDS);
		}
	}

	@Override
	public <T> void putMapValue(String key, String fieldName, T t) {
		putMapValue(key, fieldName, t, 0);
	}

	@Override
	public <T> void putMap(String key, Map<String, T> map, long expireTime) {
		BoundHashOperations<String, String, Object> ops = mapOps(key);
		// 获取已存在的集合
		Map<String, Object> keyMap = ops.entries();
		// 保存集合
		Map<String, Object> addMap = Maps.newHashMap();
		// 删除集合
		List<String> delList = Lists.newArrayList();

		for (String mapKey : map.keySet()) {
			addMap.put(mapKey, map.get(mapKey));
			if (keyMap.containsKey(mapKey)) {
				delList.add(mapKey);
			}
		}
		if (CollectionUtils.isNotEmpty(delList)) {
			ops.delete(delList.toArray(new Object[delList.size()]));
		}
		ops.putAll(addMap);
		if (expireTime > 0) {
			ops.expire(expireTime, TimeUnit.SECONDS);
		}
	}

	@Override
	public <T> T getMapField(String key, String fieldName, Class<T> clazz) {
		return (T) mapOps(key).get(fieldName);
	}

	@Override
	public <T> List<T> getMapFields(String key, List<String> fieldName, Class<T> clazz) {
		return (List<T>) mapOps(key).multiGet(fieldName);
	}

	@Override
	public <T> void updateMapField(String key, String fieldName, T t) {
		mapOps(key).put(fieldName, t);
	}

	@Override
	public <T> void updateMapFields(String key, Map<String, T> fields) {
		Map<String, Object> map = Maps.newHashMap();
		for (Map.Entry<String, T> field : fields.entrySet()) {
			map.put(field.getKey(), field.getValue());
		}
		mapOps(key).putAll(map);
	}

	@Override
	public void removeMapField(String key, String fieldName) {
		mapOps(key).delete(fieldName);
	}

	@Override
	public <T> Map<String, T> getAll(String key, Class<T> clazz) {
		Map<String, Object> map = mapOps(key).entries();
		Map<String, T> res = Maps.newHashMap();
		for (Map.Entry<String, Object> field : map.entrySet()) {
			res.put(field.getKey(), (T) field.getValue());
		}
		return res;
	}

	@Override
	public Map<String, Object> getAll(String key) {
		Map<String, Object> map = mapOps(key).entries();
		return map;
	}

	@Override
	public <T> long putObjectToSet(String key, T t) {
		return setOps(key).add(t);
	}

	@Override
	public <T> Set<T> getObjectsFromSet(String key, Class<T> clazz) {
		Set<Object> set = setOps(key).members();
		Set<T> res = Sets.newHashSet();
		for (Object s : set) {
			res.add((T) s);
		}
		return res;
	}

	@Override
	public <T> long removeObjectFromSet(String key, T t) {
		return setOps(key).remove(t);
	}

	@Override
	public <T> boolean putObjectToZSet(String key, T t, double score) {
		return zsetOps(key).add(t, score);
	}

	@Override
	public <T> void putObjectToZSetWithExpireTime(String key, T t, double score, long expireTime) {
		BoundZSetOperations<String, Object> zsetOps = zsetOps(key);
		zsetOps.add(t, score);
		if (expireTime > 0) {
			zsetOps.expire(expireTime, TimeUnit.SECONDS);
		}
	}

	@Override
	public <T> Set<T> getObjectsFromZSet(String key, double minScore, double maxScore, Class<T> clazz) {
		Set<Object> values = zsetOps(key).rangeByScore(minScore, maxScore);
		Set<T> res = Sets.newHashSet();
		for (Object value : values) {
			res.add((T) value);
		}
		return res;
	}

	@Override
	public void removeObjectFromZSet(String key, double minScore, double maxScore) {
		zsetOps(key).removeRangeByScore(minScore, maxScore);
	}

	@Override
	public <T> void removeObjectFromZSet(String key, T t) {
		zsetOps(key).remove(t);
	}

	@Override
	public <T> void putObjectToListWithLimitAndExpireTime(String key, T t, int limit, long expireTime) {
		final String _key = key;
		final T _t = t;
		final int _limit = limit;
		final long _expireTime = expireTime;
		redisTemplate.execute(new SessionCallback<Boolean>() {
			@Override
			@SuppressWarnings({ "rawtypes" })
			public Boolean execute(RedisOperations operations) throws DataAccessException {
				operations.multi();
				BoundListOperations<String, Object> ops = lstOps(_key);
				ops.leftPush(_t);
				if (_limit > 0) {
					ops.trim(0, _limit);
				}
				if (_expireTime > 0) {
					ops.expire(_expireTime, TimeUnit.SECONDS);
				}
				operations.exec();
				return true;
			}
		});
	}

	@Override
	public <T> List<T> getObjectsFromList(String key, int start, int end, Class<T> clazz) {
		List<Object> values = lstOps(key).range(start, end);
		List<T> res = Lists.newArrayList();
		for (Object value : values) {
			res.add((T) value);
		}
		return res;
	}

	@Override
	public <T> long removeObjectFormList(String key, T t) {
		return lstOps(key).remove(0, t);
	}

	// ==================================================================================================================

	BoundValueOperations<String, Object> valOps(String key) {
		return redisTemplate.boundValueOps(key);
	}

	BoundHashOperations<String, String, Object> mapOps(String key) {
		return redisTemplate.boundHashOps(key);
	}

	BoundListOperations<String, Object> lstOps(String key) {
		return redisTemplate.boundListOps(key);
	}

	BoundSetOperations<String, Object> setOps(String key) {
		return redisTemplate.boundSetOps(key);
	}

	BoundZSetOperations<String, Object> zsetOps(String key) {
		return redisTemplate.boundZSetOps(key);
	}

}
