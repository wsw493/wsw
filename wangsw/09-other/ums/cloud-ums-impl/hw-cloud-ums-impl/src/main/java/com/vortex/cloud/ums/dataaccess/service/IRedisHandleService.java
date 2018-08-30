package com.vortex.cloud.ums.dataaccess.service;

import java.util.List;
import java.util.Map;

public interface IRedisHandleService {

		/**
		 * 获取单个结果
		 * 
		 * @param key
		 * @return
		 */
		public abstract String getSingle(String key);

		/**
		 * 获取集合,支持模糊查询
		 * 
		 * @param key
		 * @return
		 */
//		public abstract List<String> getMulti(String key);

		/**
		 * 使用管道存储多个键值对
		 * 
		 * @param map
		 */
		public abstract void storageOne(String key, String value);

		/**
		 * 使用管道存储多个键值对
		 * 
		 * @param map
		 */
//		public abstract void storageMap(Map<String, String> map);

		/**
		 * 清空指定键的所有数据,可用模糊查询*
		 */
		public abstract void clear(String key);

		/**
		 * 清空数据库所有数据(不会失败,慎用)
		 */
//		public abstract void clearAll();
}
