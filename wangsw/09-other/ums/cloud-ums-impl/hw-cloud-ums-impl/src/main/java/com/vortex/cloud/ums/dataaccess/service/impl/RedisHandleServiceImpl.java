package com.vortex.cloud.ums.dataaccess.service.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vortex.cloud.ums.dataaccess.service.IRedisHandleService;

@Transactional
@Service("redisHandleService")
public class RedisHandleServiceImpl implements IRedisHandleService {

	@Autowired
	private StringRedisTemplate redisTemplate;

	BoundValueOperations<String, String> valOps(String key) {
		return redisTemplate.boundValueOps(key);
	}

	boolean containsKey(String key) {
		return redisTemplate.hasKey(key);
	}

	@SuppressWarnings("unchecked")
	<T> T decode(String text, Class<T> clazz) {
		if (text == null || "null".equals(text))
			return null;
		return (T) text;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.vortex.cloud.management.dataaccess.service.impl.IRedisHandleService
	 * #getSingle(java.lang.String)
	 */
	@Override
	public String getSingle(String key) {
		if (containsKey(key)) {
			return decode(valOps(key).get(), String.class);
		} else {
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.vortex.cloud.management.dataaccess.service.impl.IRedisHandleService
	 * #getMulti(java.lang.String)
	 */

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.vortex.cloud.management.dataaccess.service.impl.IRedisHandleService
	 * #storageOne(java.lang.String, java.lang.String)
	 */
	@Override
	public void storageOne(String key, String value) {
		if (value == null)
			return;
		valOps(key).set(value);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.vortex.cloud.management.dataaccess.service.impl.IRedisHandleService
	 * #storageMap(java.util.Map)
	 */
	// @Override
	// public void storageMap(Map<String, String> map) {
	// final Map<String, String> mapf = map;
	// redisTemplate.execute(new RedisCallback<Long>() {
	// @Override
	// public Long doInRedis(RedisConnection connection)
	// throws DataAccessException {
	// for (String key : mapf.keySet()) {
	// byte[] keyb = key.getBytes();
	// byte[] valueb = toByteArray(mapf.get(key));
	// connection.set(keyb, valueb);
	// }
	// return 1L;
	// }
	// });
	// }

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.vortex.cloud.management.dataaccess.service.impl.IRedisHandleService
	 * #clear(java.lang.String)
	 */
	@Override
	public void clear(String key) {
		Set<String> keyset = redisTemplate.keys(key);
		redisTemplate.delete(keyset);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.vortex.cloud.management.dataaccess.service.impl.IRedisHandleService
	 * #clearAll()
	 */
	// @Override
	// public void clearAll() {
	// redisTemplate.execute(new RedisCallback<String>() {
	// @Override
	// public String doInRedis(RedisConnection connection)
	// throws DataAccessException {
	// connection.flushDb();
	// return "ok";
	// }
	// });
	// }

	/**
	 * 字节数组转对象
	 * 
	 * @param bytes
	 * @return
	 */
	private Object toObject(byte[] bytes) {
		Object obj = null;
		try {
			ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
			ObjectInputStream ois = new ObjectInputStream(bis);
			obj = ois.readObject();
			ois.close();
			bis.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		} catch (ClassNotFoundException ex) {
			ex.printStackTrace();
		}
		return obj;
	}

	/**
	 * 对象转字节数组
	 * 
	 * @param obj
	 * @return
	 */
	private byte[] toByteArray(Object obj) {
		byte[] bytes = null;
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try {
			ObjectOutputStream oos = new ObjectOutputStream(bos);
			oos.writeObject(obj);
			oos.flush();
			bytes = bos.toByteArray();
			oos.close();
			bos.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return bytes;
	}


}
