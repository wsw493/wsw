package com.vortex.cloud.ums.util;

import java.util.Map;

import com.google.common.collect.Maps;
import com.vortex.cloud.ums.util.utils.ConnectHttpService;


public class LBS {
	private String ak = "rkFcTa05vMhMHO8zHEgkKKs8";
	/** 建表 */
	private static final String CREAT_TABLE = "http://api.map.baidu.com/geodata/v3/geotable/create";
	/** 查询表 */
	private static final String QUERY_TABLE = "http://api.map.baidu.com/geodata/v3/geotable/list";
	/** 根据id查询表 */
	private static final String QUERY_TABLE_BY_ID = "http://api.map.baidu.com/geodata/v3/geotable/detail";
	/** 更新表 */
	private static final String UPDATE_TABLE = "http://api.map.baidu.com/geodata/v3/geotable/update";
	/** 删除表 */
	private static final String DELETE_TABLE = "http://api.map.baidu.com/geodata/v3/geotable/delete";
	/** 创建列 */
	private static final String CREATE_COLUMN = "http://api.map.baidu.com/geodata/v3/column/create";
	/** 查询列 */
	private static final String QUERY_COLUMN = "http://api.map.baidu.com/geodata/v3/column/list";
	/** 更新列 */
	private static final String UPDATE_COLUMN = "http://api.map.baidu.com/geodata/v3/column/update";
	/** 删除列 */
	private static final String DELETE_COLUMN = "http://api.map.baidu.com/geodata/v3/column/delete";
	/** 创建数据 */
	private static final String CREATE_POI = "http://api.map.baidu.com/geodata/v3/poi/create ";
	/** 更新数据 */
	private static final String UPDATE_POI = "http://api.map.baidu.com/geodata/v3/poi/update";
	/** 查询数据 */
	private static final String QUERY_POI = "http://api.map.baidu.com/geodata/v3/poi/list";
	/** 根据id查询数据 */
	private static final String QUERY_POI_BY_ID = "http://api.map.baidu.com/geodata/v3/poi/detail";
	/** 删除数据 */
	private static final String DELETE_POI = "http://api.map.baidu.com/geodata/v3/poi/delete";

	/**
	 * 建表
	 * 
	 * @return
	 */
	public String createTable() {
		Map<String, Object> params = Maps.newHashMap();
		params.put("name", "tolite");
		params.put("geotype", 1);
		params.put("is_published", 1);
		params.put("ak", ak);
		String result = ConnectHttpService.callHttp(CREAT_TABLE, ConnectHttpService.METHOD_POST, params);
		return result;

	}

	/**
	 * 查询表
	 * 
	 * @return
	 */
	public String queryTable() {
		Map<String, Object> params = Maps.newHashMap();
		// params.put("name", "test");
		params.put("ak", ak);
		String result = ConnectHttpService.callHttp(QUERY_TABLE, ConnectHttpService.METHOD_GET, params);
		return result;

	}

	/**
	 * 查询表
	 * 
	 * @return
	 */
	public String queryTableById() {
		Map<String, Object> params = Maps.newHashMap();
		params.put("id", "154363");
		params.put("ak", ak);
		String result = ConnectHttpService.callHttp(QUERY_TABLE_BY_ID, ConnectHttpService.METHOD_GET, params);
		return result;

	}

	/**
	 * 更新表
	 * 
	 * @return
	 */
	public String updateTableById() {
		Map<String, Object> params = Maps.newHashMap();
		params.put("id", "154363");
		params.put("name", "test1");
		params.put("geotype", 1);
		params.put("is_published", 1);
		params.put("ak", ak);
		String result = ConnectHttpService.callHttp(UPDATE_TABLE, ConnectHttpService.METHOD_POST, params);
		return result;

	}

	/**
	 * 删除表
	 * 
	 * @return
	 */
	public String deleteTableById() {
		Map<String, Object> params = Maps.newHashMap();
		params.put("id", "154363");
		params.put("ak", ak);
		String result = ConnectHttpService.callHttp(DELETE_TABLE, ConnectHttpService.METHOD_POST, params);
		return result;

	}

	/**
	 * 创建列
	 * 
	 * @return
	 */
	public String createColumn() {
		Map<String, Object> params = Maps.newHashMap();
		params.put("name", "price");
		params.put("key", "price");
		params.put("type",2);
		params.put("is_sortfilter_field", 0);
		params.put("is_search_field", 0);
		params.put("is_index_field", 1);
		params.put("geotable_id", "155133");
		params.put("max_length", 510);
		params.put("ak", ak);
		String result = ConnectHttpService.callHttp(CREATE_COLUMN, ConnectHttpService.METHOD_POST, params);
		System.out.println(result);
		return result;

	}

	/**
	 * 查询列
	 * 
	 * @return
	 */
	public String queryColumn() {
		Map<String, Object> params = Maps.newHashMap();
		params.put("name", "链接");
		params.put("key", "url");
		params.put("geotable_id", "154363");
		params.put("ak", ak);
		String result = ConnectHttpService.callHttp(QUERY_COLUMN, ConnectHttpService.METHOD_GET, params);
		return result;

	}

	/**
	 * 查询列
	 * 
	 * @return
	 */
	public String queryColumnById() {
		Map<String, Object> params = Maps.newHashMap();
		params.put("id", 266355);
		params.put("geotable_id", "154363");
		params.put("ak", ak);
		String result = ConnectHttpService.callHttp(QUERY_COLUMN, ConnectHttpService.METHOD_GET, params);
		return result;

	}

	/**
	 * 更新列
	 * 
	 * @return
	 */
	public String updateColumn() {
		Map<String, Object> params = Maps.newHashMap();
		params.put("id", 266355);
		params.put("name", "链接");
		params.put("key", "url");
		params.put("type", 4);
		params.put("is_sortfilter_field", 0);
		params.put("is_search_field", 0);
		params.put("is_index_field", 0);
		params.put("geotable_id", "154363");
		params.put("ak", ak);
		String result = ConnectHttpService.callHttp(UPDATE_COLUMN, ConnectHttpService.METHOD_POST, params);
		return result;

	}

	/**
	 * 删除列
	 * 
	 * @return
	 */
	public String deleteColumn() {
		Map<String, Object> params = Maps.newHashMap();
		params.put("id", 266355);
		params.put("geotable_id", "154363");
		params.put("ak", ak);
		String result = ConnectHttpService.callHttp(DELETE_COLUMN, ConnectHttpService.METHOD_POST, params);
		return result;

	}

	/**
	 * 创建数据
	 * 
	 * @return
	 */
	public String createPOI() {
		Map<String, Object> params = Maps.newHashMap();
		params.put("address", "");
		params.put("tags", "");
		params.put("latitude", 39.927552);
		params.put("longitude", 116.403694);
		params.put("coord_type", 3);
		params.put("geotable_id", 154363);
		params.put("ak", ak);
		String result = ConnectHttpService.callHttp(CREATE_POI, ConnectHttpService.METHOD_POST, params);
		return result;

	}

	/**
	 * 查询数据
	 * 
	 * @return
	 */
	public String queryPOI() {
		Map<String, Object> params = Maps.newHashMap();
		params.put("geotable_id", 154363);
		params.put("ak", ak);
		String result = ConnectHttpService.callHttp(QUERY_POI, ConnectHttpService.METHOD_GET, params);
		return result;

	}

	/**
	 * 根据id查询数据
	 * 
	 * @return
	 */
	public String queryPOIById() {
		Map<String, Object> params = Maps.newHashMap();
		params.put("id", "1827428737");

		params.put("geotable_id", 154363);
		params.put("ak", ak);
		String result = ConnectHttpService.callHttp(QUERY_POI_BY_ID, ConnectHttpService.METHOD_GET, params);
		return result;

	}

	/**
	 * 修改数据
	 * 
	 * @return
	 */
	public String updatePOI() {
		Map<String, Object> params = Maps.newHashMap();
		params.put("id", "1827428737");
		params.put("address", "");
		params.put("tags", "");
		params.put("latitude", 39.927552);
		params.put("longitude", 116.403694);
		params.put("coord_type", 3);
		params.put("geotable_id", 154363);
		params.put("ak", ak);
		String result = ConnectHttpService.callHttp(UPDATE_POI, ConnectHttpService.METHOD_POST, params);
		return result;

	}

	/**
	 * 删除数据
	 * 
	 * @return
	 */
	public String deletePOI() {
		Map<String, Object> params = Maps.newHashMap();
		params.put("id", "1827428737");
		params.put("address", "");
		params.put("tags", "");
		params.put("latitude", 39.927552);
		params.put("longitude", 116.403694);
		params.put("coord_type", 3);
		params.put("geotable_id", 154363);
		params.put("ak", ak);
		String result = ConnectHttpService.callHttp(DELETE_POI, ConnectHttpService.METHOD_POST, params);
		return result;

	}

}
