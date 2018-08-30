package com.vortex.cloud.ums.dataaccess.service;

/**
 * 地图偏转服务
 * 
 * @author SonHo
 *
 * @param <T>
 */
public interface IDeflectService {
	/**
	 * 百度地图偏转偏转<br/>
	 * 
	 * @param t
	 * @param longitude
	 * @param latitude
	 * @return 偏转后的经纬度，格式为： 经度,纬度
	 */
	public String deflect(Double longitude, Double latitude);

	/**
	 * 经纬度转换
	 * bd09 -> wgs84
	 * @param params
	 *            经纬度序列，“,”分隔经度纬度，“;”分隔经纬度序列
	 * @return
	 */
	public String deflect(String params) throws Exception;

	/**
	 * 经纬度转换
	 * wgs84 -> bd09
	 * @param params
	 *			经纬度 xxx,xxx;xxx,xxx
	 * @return
	 */
	public String deflectToBD(String params);
}
