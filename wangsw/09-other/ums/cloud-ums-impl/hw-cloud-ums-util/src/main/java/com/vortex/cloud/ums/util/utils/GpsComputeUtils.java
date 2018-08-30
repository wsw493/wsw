package com.vortex.cloud.ums.util.utils;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import com.google.common.collect.Lists;
import com.vortex.cloud.ums.util.enums.PrimitiveEnum;
import com.vortex.cloud.ums.util.model.APoint;
import com.vortex.cloud.ums.util.model.AlgorithmForPolygon;
import com.vortex.cloud.ums.util.model.AlgorithmForPolyline;
import com.vortex.cloud.ums.util.model.BaseGpsPoint;
import com.vortex.cloud.ums.util.model.BasePrimitive;
import com.vortex.cloud.ums.util.model.Basic;
import com.vortex.cloud.ums.util.model.CoordinateConversion;
import com.vortex.cloud.ums.util.model.Polygon;
import com.vortex.cloud.ums.util.model.Polyline;
import com.vortex.cloud.ums.util.model.ReturnValue;

public class GpsComputeUtils {
	private static final String SEMICOLON = ";";
	private static final String COMMA = ",";

	// private static Logger loger =
	// LoggerFactory.getLogger(GpsComputeUtils.class);

	/**
	 * 得到点到图元的距离；外部返回正数，内部返回负数，在图元边线上返回0；图元类型参见PrimitiveEnum；入参中所有点坐标必须是同一坐标系
	 * 
	 * @param primitive
	 * @param pointData
	 * @return
	 */
	public static Double overMeter(BasePrimitive primitive, BaseGpsPoint pointData) {
		AlgorithmForPolyline algorithmForPolyline = new AlgorithmForPolyline();
		AlgorithmForPolygon algorithmForPolygon = new AlgorithmForPolygon();

		// 把需要判断的点先转换到高斯坐标系
		APoint aPoint = conversionPositionToAPoint(pointData.getLongitude(), pointData.getLatitude());
		if (aPoint == null) {
			return null;
		}

		// 把图元的所有点转换到高斯坐标系
		List<APoint> allPoint = convertPointStr(primitive.getParams());

		if (CollectionUtils.isEmpty(allPoint)) {
			return null;
		}

		/* 图元类型 */
		String shape = primitive.getShapeType();

		if (PrimitiveEnum.AREA.getValue().equals(shape)) {
			if (allPoint.size() < 3) {
				// loger.error("多边形图元不能少于3个点");
				return null;
			}
			// 新建一个多变形
			Polygon polygon = new Polygon();
			polygon.setAllPoint(allPoint);

			// 新建一条多折线
			Polyline polyline = new Polyline();
			polyline.setAllPoint(allPoint);
			polyline.getAllPoint().add(allPoint.get(0));

			// 判断关系
			String relation = algorithmForPolygon.ptAndPoly(polygon, aPoint);
			if (relation.equals(ReturnValue.out.toString())) {
				return algorithmForPolyline.getDistanceToLine(aPoint, polyline);
			} else {
				return algorithmForPolyline.getDistanceToLine(aPoint, polyline) * (-1);
			}
		} else if (PrimitiveEnum.LINE.getValue().equals(shape)) {
			if (allPoint.size() < 2) {
				// loger.error("多折线图元不能少于2个点");
				return null;
			}
			// 新建一条多折线
			Polyline polyline = new Polyline();
			polyline.setAllPoint(allPoint);
			return algorithmForPolyline.getDistanceToLine(aPoint, polyline);
		} else if (PrimitiveEnum.RECTANGLE.getValue().equals(shape)) {
			if (allPoint.size() != 4) {
				// loger.error("矩形图元必须4个点");
				return null;
			}
			// 新建一个多变形
			Polygon polygon = new Polygon();
			polygon.setAllPoint(allPoint);

			// 新建一条多折线
			Polyline polyline = new Polyline();
			polyline.setAllPoint(allPoint);
			polyline.getAllPoint().add(allPoint.get(0));

			// 判断关系
			String relation = algorithmForPolygon.ptAndPoly(polygon, aPoint);
			if (relation.equals(ReturnValue.out.toString())) {
				return algorithmForPolyline.getDistanceToLine(aPoint, polyline);
			} else {
				return algorithmForPolyline.getDistanceToLine(aPoint, polyline) * (-1);
			}
		} else if (PrimitiveEnum.CIRCLE.getValue().equals(shape)) {
			if (allPoint.size() != 1 || primitive.getRadius() == null) {
				// loger.error("圆形图元必须1个点及半径");
				return null;
			}
			/*
			 * APoint a = allPoint.get(0); APoint b = allPoint.get(1); double x
			 * = getDistance(a, b);// 区域的半径 double y = getDistance(aPoint, a);//
			 * 点到圆心的距离 return (y - x);// 点到圆的边的距离
			 */
			APoint a = allPoint.get(0);
			return Basic.getDistance(aPoint, a) - primitive.getRadius();
		} else if (PrimitiveEnum.POINT.getValue().equals(shape)) {
			if (allPoint.size() != 1) {
				// loger.error("点图元必须1个点");
				return null;
			}
			return Basic.getDistance(aPoint, allPoint.get(0));
		} else {
			// loger.error("图元外形类型不正确");
			return null;
		}
	}

	/**
	 * @功能：把地图上使用的经纬度转换成我们判断和计算距离的高斯坐标系坐标
	 * @说明：转换车载设备和手机设备上次的点
	 */
	private static APoint conversionPositionToAPoint(double longitude, double latitude) {
		APoint rst = null;
		try {
			rst = CoordinateConversion.BLToGauss(longitude, latitude);
		} catch (Exception e) {
			// loger.error("点坐标转换高斯坐标点失败！(longitude=" + longitude + ",latitude="
			// + latitude + ")");
		}
		return rst;
	}

	/**
	 * 将坐标的"经度,纬度;经度,纬度"，类似这样的坐标串，解析成高斯点列表
	 * 
	 * @param points
	 * @return
	 */
	private static List<APoint> convertPointStr(String points) {
		if (StringUtils.isEmpty(points)) {
			return null;
		}

		List<APoint> rst = Lists.newArrayList();
		String[] parray = points.split(SEMICOLON);
		if (parray == null || parray.length == 0) {
			return null;
		}

		String[] pointArray = null;
		for (int i = 0; i < parray.length; i++) {
			pointArray = parray[i].split(COMMA);
			if (StringUtils.isEmpty(parray[i])) {
				// loger.error("将坐标串解析成高斯点集时出错！坐标串=" + points);
				return null;
			}

			APoint point = conversionPositionToAPoint(Float.parseFloat(pointArray[0]), Float.parseFloat(pointArray[1]));
			if (point == null) {
				// loger.error("将坐标串解析成高斯点时出错！坐标串=" + parray[i]);
				return null;
			}
			rst.add(point);
		}

		return rst;
	}

	public Double getDistanceByCoordinate(Float lo1, Float la1, Float lo2, Float la2) {
		APoint p1 = conversionPositionToAPoint(lo1, la1);
		APoint p2 = conversionPositionToAPoint(lo2, la2);
		return Basic.getDistance(p1, p2);
	}

	/**
	 * 检查多边形是否包含了某点
	 * 
	 * @param gpsPoints
	 *            多边形点集合
	 * @param point
	 * @return
	 */
	public static boolean containsPoint(List<BaseGpsPoint> gpsPoints, BaseGpsPoint point) {
		if (gpsPoints == null || gpsPoints.size() < 3 || point == null) {
			return false;
		}
		int verticesCount = gpsPoints.size();
		int nCross = 0;
		for (int i = 0; i < verticesCount; ++i) {
			BaseGpsPoint p1 = gpsPoints.get(i);
			BaseGpsPoint p2 = gpsPoints.get((i + 1) % verticesCount);
			// 求解 y=p.y 与 p1 p2 的交点
			if (p1.getLongitude() == p2.getLongitude()) { // p1p2 与 y=p0.y平行
				continue;
			}
			if (point.getLongitude() < Math.min(p1.getLongitude(), p2.getLongitude())) { // 交点在p1p2延长线上
				continue;
			}
			if (point.getLongitude() >= Math.max(p1.getLongitude(), p2.getLongitude())) { // 交点在p1p2延长线上
				continue;
			}
			// 求交点的 X 坐标
			double x = (point.getLongitude() - p1.getLongitude()) * (p2.getLatitude() - p1.getLatitude()) / (p2.getLongitude() - p1.getLongitude()) + p1.getLatitude();
			if (x > point.getLatitude()) { // 只统计单边交点
				nCross++;
			}
		}
		// 单边交点为偶数，点在多边形之外
		return (nCross % 2 == 1);
	}
}
