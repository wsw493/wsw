package com.personal.cloud.redis.service.impl;

import com.personal.cloud.redis.service.ICloudRedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

/**
 *  redis 接口定义
 * @date 2018-08-06
 * @author  wsw
 * Created by wsw on 2018/8/6.
 */
@Service
public class CloudRedisServiceImpl implements ICloudRedisService {

    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public boolean set(String key, Object value) {
        boolean result = false;
        redisTemplate.opsForValue().set(key,value);
        return result;
    }

    @Override
    public Object get(String key) {
        redisTemplate.opsForValue().get(key);
        return redisTemplate.opsForValue().get(key);
    }

    @Override
    public void setString(String key, String value) {
        stringRedisTemplate.opsForValue().set(key,value);
    }

    @Override
    public String getString(String key) {
        String value = stringRedisTemplate.opsForValue().get(key);
        return value;
    }
}
