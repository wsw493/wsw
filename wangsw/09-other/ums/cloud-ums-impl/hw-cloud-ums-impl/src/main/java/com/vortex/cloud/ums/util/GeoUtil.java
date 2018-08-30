package com.vortex.cloud.ums.util;

import java.math.BigDecimal;

import com.vortex.cloud.ums.util.utils.GpsComputeUtils;



/**
 * 长度、面积的相关计算方法
 * 
 * @author ztj
 *
 */
public class GeoUtil {

	/**
	 * 地球半径
	 */
	public static Double EARTH_RADIUS = 6378137.0;// 6378137.0,WGS84椭球半径

	/**
	 * @功能：计算两点距离
	 * @说明：勾股定理
	 */
	public static Double getDistance(Double lng_p1, Double lat_p1, Double lng_p2, Double lat_p2) {
		Double xdiff = lng_p1 - lng_p2;
		Double ydiff = lat_p1 - lat_p2;
		return Math.pow((xdiff * xdiff + ydiff * ydiff), 0.5);
	}

	/**
	 * @功能：计算两点距离 @说明：
	 */
	public static Double getDistance2(Double lng_p1, Double lat_p1, Double lng_p2, Double lat_p2) {

		return new GpsComputeUtils().getDistanceByCoordinate(new BigDecimal(lng_p1).floatValue(), new BigDecimal(lat_p1).floatValue(), new BigDecimal(lng_p2).floatValue(), new BigDecimal(lat_p2).floatValue());

	}

	/**
	 * @功能：计算多折现长度,传入数据格式 经度,纬度;经度,纬度;经度,纬度;经度,纬度
	 * @param lngLat
	 * @return
	 */
	public static Double getLength(String lngLat) {

		Double length = 0D;

		try {
			String[] lngLatPointArr = lngLat.split(";");
			if (lngLatPointArr.length <= 1) {
				throw new Exception("请检查传入的坐标点格式！");
			}

			Double lngTemp = null;
			Double latTemp = null;

			for (String lngLatPoint : lngLatPointArr) {
				Double lng = Double.valueOf(lngLatPoint.split(",")[0]);
				Double lat = Double.valueOf(lngLatPoint.split(",")[1]);
				if (lngTemp == null && latTemp == null) {
					lngTemp = lng;
					latTemp = lat;
				} else {
					length += getDistance2(lng, lat, lngTemp, latTemp);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return length;
	}

	/**
	 * @功能：计算多边形面积,传入数据格式 经度,纬度;经度,纬度;经度,纬度;经度,纬度
	 * @param lngLat
	 * @return
	 */
	public static Double getPolygonArea(String lngLat) {

		Double totalArea = 0D;// 初始化总面积

		try {
			String[] lngLatPointArr = lngLat.split(";");
			if (lngLatPointArr.length < 3) {// 小于3个顶点，不能构建面
				throw new Exception("请检查传入的坐标点格式！");
			}
			Double[][] pts = new Double[lngLatPointArr.length][2];
			for (int i = 0; i < lngLatPointArr.length; i++) {
				pts[i][0] = Double.valueOf(lngLatPointArr[i].split(",")[0]);
				pts[i][1] = Double.valueOf(lngLatPointArr[i].split(",")[1]);
			}
			Double LowX = 0.0;
			Double LowY = 0.0;
			Double MiddleX = 0.0;
			Double MiddleY = 0.0;
			Double HighX = 0.0;
			Double HighY = 0.0;
			Double AM = 0.0;
			Double BM = 0.0;
			Double CM = 0.0;
			Double AL = 0.0;
			Double BL = 0.0;
			Double CL = 0.0;
			Double AH = 0.0;
			Double BH = 0.0;
			Double CH = 0.0;
			Double CoefficientL = 0.0;
			Double CoefficientH = 0.0;
			Double ALtangent = 0.0;
			Double BLtangent = 0.0;
			Double CLtangent = 0.0;
			Double AHtangent = 0.0;
			Double BHtangent = 0.0;
			Double CHtangent = 0.0;
			Double ANormalLine = 0.0;
			Double BNormalLine = 0.0;
			Double CNormalLine = 0.0;
			Double OrientationValue = 0.0;
			Double AngleCos = 0.0;
			Double Sum1 = 0.0;
			Double Sum2 = 0.0;
			Integer Count2 = 0;
			Integer Count1 = 0;
			Double Sum = 0.0;
			Integer Count = pts.length;
			for (int i = 0; i < Count; i++) {
				if (i == 0) {
					LowX = pts[Count - 1][0] * Math.PI / 180;
					LowY = pts[Count - 1][1] * Math.PI / 180;
					MiddleX = pts[0][0] * Math.PI / 180;
					MiddleY = pts[0][1] * Math.PI / 180;
					HighX = pts[1][0] * Math.PI / 180;
					HighY = pts[1][1] * Math.PI / 180;
				} else if (i == Count - 1) {
					LowX = pts[Count - 2][0] * Math.PI / 180;
					LowY = pts[Count - 2][1] * Math.PI / 180;
					MiddleX = pts[Count - 1][0] * Math.PI / 180;
					MiddleY = pts[Count - 1][1] * Math.PI / 180;
					HighX = pts[0][0] * Math.PI / 180;
					HighY = pts[0][1] * Math.PI / 180;
				} else {
					LowX = pts[i - 1][0] * Math.PI / 180;
					LowY = pts[i - 1][1] * Math.PI / 180;
					MiddleX = pts[i][0] * Math.PI / 180;
					MiddleY = pts[i][1] * Math.PI / 180;
					HighX = pts[i + 1][0] * Math.PI / 180;
					HighY = pts[i + 1][1] * Math.PI / 180;
				}
				AM = Math.cos(MiddleY) * Math.cos(MiddleX);
				BM = Math.cos(MiddleY) * Math.sin(MiddleX);
				CM = Math.sin(MiddleY);
				AL = Math.cos(LowY) * Math.cos(LowX);
				BL = Math.cos(LowY) * Math.sin(LowX);
				CL = Math.sin(LowY);
				AH = Math.cos(HighY) * Math.cos(HighX);
				BH = Math.cos(HighY) * Math.sin(HighX);
				CH = Math.sin(HighY);
				CoefficientL = (AM * AM + BM * BM + CM * CM) / (AM * AL + BM * BL + CM * CL);
				CoefficientH = (AM * AM + BM * BM + CM * CM) / (AM * AH + BM * BH + CM * CH);
				ALtangent = CoefficientL * AL - AM;
				BLtangent = CoefficientL * BL - BM;
				CLtangent = CoefficientL * CL - CM;
				AHtangent = CoefficientH * AH - AM;
				BHtangent = CoefficientH * BH - BM;
				CHtangent = CoefficientH * CH - CM;
				AngleCos = (AHtangent * ALtangent + BHtangent * BLtangent + CHtangent * CLtangent) / (Math.sqrt(AHtangent * AHtangent + BHtangent * BHtangent + CHtangent * CHtangent) * Math.sqrt(ALtangent * ALtangent + BLtangent * BLtangent + CLtangent * CLtangent));
				AngleCos = Math.acos(AngleCos);
				ANormalLine = BHtangent * CLtangent - CHtangent * BLtangent;
				BNormalLine = 0 - (AHtangent * CLtangent - CHtangent * ALtangent);
				CNormalLine = AHtangent * BLtangent - BHtangent * ALtangent;
				if (AM != 0)
					OrientationValue = ANormalLine / AM;
				else if (BM != 0)
					OrientationValue = BNormalLine / BM;
				else
					OrientationValue = CNormalLine / CM;
				if (OrientationValue > 0) {
					Sum1 += AngleCos;
					Count1++;
				} else {
					Sum2 += AngleCos;
					Count2++;
				}
			}
			Double tempSum1, tempSum2;
			tempSum1 = Sum1 + (2 * Math.PI * Count2 - Sum2);
			tempSum2 = (2 * Math.PI * Count1 - Sum1) + Sum2;
			if (Sum1 > Sum2) {
				if ((tempSum1 - (Count - 2) * Math.PI) < 1)
					Sum = tempSum1;
				else
					Sum = tempSum2;
			} else {
				if ((tempSum2 - (Count - 2) * Math.PI) < 1)
					Sum = tempSum2;
				else
					Sum = tempSum1;
			}
			totalArea = (Sum - (Count - 2) * Math.PI) * EARTH_RADIUS * EARTH_RADIUS;

		} catch (Exception e) {
			e.printStackTrace();
		}

		return totalArea; // 返回总面积
	}

	/**
	 * @功能：计算圆形面积,传入数据格式 半径： 米
	 * @param lngLat
	 * @return
	 */
	public static Double getCircleArea(Double radius) {
		return Math.PI * radius * radius;
	}
}
