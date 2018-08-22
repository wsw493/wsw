package com.personal.cloud.redis.service;

/**
 *  redis 接口定义
 * @date 2018-08-06
 * @author  wsw
 * Created by wsw on 2018/8/6.
 */
public interface ICloudRedisService {

    /**
     * 写入缓存对象
     * @param key
     * @param value
     * @return
     */
    public boolean set(final String key, Object value);

    /**
     * 读取缓存
     * @param key
     * @return
     */
    public Object get(final String key);


    /**
     * 写入缓存字符串
     * @param key
     * @param value
     * @return
     */
    public void setString(final String key, String value);

    /**
     * 获取值
     * @param key
     * @return
     */
    public String getString(final String key);
}
