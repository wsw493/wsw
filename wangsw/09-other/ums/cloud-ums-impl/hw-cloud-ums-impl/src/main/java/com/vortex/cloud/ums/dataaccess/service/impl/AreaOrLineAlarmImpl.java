package com.vortex.cloud.ums.dataaccess.service.impl;

import java.util.ArrayList;
import java.util.List;

import com.vortex.cloud.ums.dataaccess.service.AreaOrLineAlarm;
import com.vortex.cloud.ums.model.BasePoint;
import com.vortex.cloud.ums.model.WorkElement;
import com.vortex.cloud.ums.model.gps.APoint;
import com.vortex.cloud.ums.model.gps.AlgorithmForPolygon;
import com.vortex.cloud.ums.model.gps.AlgorithmForPolyline;
import com.vortex.cloud.ums.model.gps.Basic;
import com.vortex.cloud.ums.model.gps.Constants;
import com.vortex.cloud.ums.model.gps.CoordinateConversion;
import com.vortex.cloud.ums.model.gps.LineSegment;
import com.vortex.cloud.ums.model.gps.Polygon;
import com.vortex.cloud.ums.model.gps.Polyline;
import com.vortex.cloud.ums.model.gps.Position;
import com.vortex.cloud.ums.model.gps.ReturnValue;

public class AreaOrLineAlarmImpl implements AreaOrLineAlarm {

	/**
	 * @author lxw
	 * @功能：计算一个上传点到一个图元的距离 @说明：如果在内部为负，在外部为正【好像】
	 */
	@Override
	public double overMeter(Position position, WorkElement element) throws Exception {
		AlgorithmForPolyline algorithmForPolyline = new AlgorithmForPolyline();
		AlgorithmForPolygon algorithmForPolygon = new AlgorithmForPolygon();

		/* 原始 */
		String shape = element.getShape();
		// 把需要判断的点先转换到高斯坐标系
		APoint aPoint = conversionPositionToAPoint(position);
		// 把图元的所有点转换到高斯坐标系
		List<APoint> allPoint = new ArrayList<APoint>();
		for (BasePoint point : element.getTransferPoints()) {
			if (point != null) {
				allPoint.add(conversionPointToAPoint(point));
			} else {
				throw new Exception("有点为空");
			}
		}

		if (shape.equals(Constants.ShapeType.POLYGON)) {
			if (element.getTransferPoints().size() < 3) {
				throw new Exception("多边形图元不能少于3个点");
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
		} else if (shape.equals(Constants.ShapeType.lINE)) {
			if (element.getTransferPoints().size() < 2) {
				throw new Exception("多折线图元不能少于2个点");
			}
			// 新建一条多折线
			Polyline polyline = new Polyline();
			polyline.setAllPoint(allPoint);
			return algorithmForPolyline.getDistanceToLine(aPoint, polyline);
		} else if (shape.equals(Constants.ShapeType.RECTANGLE)) {
			if (element.getTransferPoints().size() != 2) {
				throw new Exception("矩形图元必须2个点");
			}
			APoint a = allPoint.get(0);
			APoint b = allPoint.get(1);
			// 新建一个多边形，把矩形表示成一个多边形
			Polygon polygon = new Polygon();
			List<APoint> a1 = new ArrayList<APoint>();
			a1.add(a);
			a1.add(new APoint(a.getX(), b.getY()));
			a1.add(b);
			a1.add(new APoint(b.getX(), a.getY()));
			polygon.setAllPoint(a1);
			// 新建一条多折线
			Polyline polyline = new Polyline();
			polyline.setAllPoint(a1);
			polyline.getAllPoint().add(a1.get(0));
			// 先判断是否在这个多边形内部
			String relation = algorithmForPolygon.ptAndPoly(polygon, aPoint);
			if (relation.equals(ReturnValue.out.toString())) {
				return algorithmForPolyline.getDistanceToLine(aPoint, polyline);
			} else {
				return algorithmForPolyline.getDistanceToLine(aPoint, polyline) * (-1);
			}
		} else if (shape.equals(Constants.ShapeType.CIRCLE)) {
			if (element.getTransferPoints().size() != 1 || element.getRadius() == null) {
				throw new Exception("圆形图元必须1个点及半径");
			}
			/*
			 * APoint a = allPoint.get(0); APoint b = allPoint.get(1); double x
			 * = Basic.getDistance(a, b);// 区域的半径 double y =
			 * Basic.getDistance(aPoint, a);// 点到圆心的距离 return (y - x);//
			 * 点到圆的边的距离
			 */
			APoint a = allPoint.get(0);
			return Basic.getDistance(aPoint, a) - element.getRadius();
		} else if (shape.equals(Constants.ShapeType.POINT)) {
			if (element.getTransferPoints().size() != 1) {
				throw new Exception("点图元必须1个点");
			}
			return Basic.getDistance(aPoint, allPoint.get(0));
		} else {
			throw new Exception("图元外形类型不正确");
		}
	}

	/**
	 * @author lxw
	 * @功能：判断两个图元是否相交
	 * @说明：主要还是圆形比较特殊
	 */
	@Override
	public boolean hasOverlapBetweenElement(WorkElement e1, WorkElement e2) throws Exception {
		// 如果两个图圆中有一个是圆形，那个计算方法不一样，因为矩形可以转化为多边形，所以一起按照多边形处理
		if (isCircle(e1) && isCircle(e2)) {
			return hasOverlapBetweenCircleAndCircle(e1, e2);
		}
		if (isCircle(e1)) {
			return hasOverlapBetweenCircleAndElement(e1, e2);
		}
		if (isCircle(e2)) {
			return hasOverlapBetweenCircleAndElement(e2, e1);
		}

		// 如果没有圆形，则可能是矩形，多边形，多折线
		Polygon p1 = elementToPolygon(e1);
		Polygon p2 = elementToPolygon(e2);// 两个多边形
		// 获取多边形和多折线的算法
		AlgorithmForPolygon algorithmForPolygon = new AlgorithmForPolygon();
		return algorithmForPolygon.polygonIsIntersect(p1, p2);
	}

	/**
	 * @author lxw
	 * @功能：判断一个图元是否是圆形 @说明：
	 */
	private boolean isCircle(WorkElement element) {
		String shape = element.getShape();// shape1
		return shape.equals(Constants.ShapeType.CIRCLE);
	}

	/**
	 * @author lxw
	 * @功能：获取图园中所有的点 @说明：
	 */
	private List<APoint> getAPointListFromElement(WorkElement element) throws Exception {
		List<APoint> apointList = new ArrayList<APoint>();// allpoint2
		for (BasePoint point : element.getTransferPoints()) {
			if (point != null) {
				APoint aPoint = new APoint();
				aPoint.setX(point.getLongitude());
				aPoint.setY(point.getLatitude());
				apointList.add(aPoint);
			} else {
				throw new Exception("有点为空");
			}
		}

		return apointList;
	}

	/**
	 * @author lxw
	 * @功能：将矩形,多边形，多折线转换为多边形 @说明： 主要是矩形的情况有点特殊
	 */
	private Polygon elementToPolygon(final WorkElement element) throws Exception {
		String shape = element.getShape();// shape2
		List<APoint> apointList = getAPointListFromElement(element);
		Polygon polygon = new Polygon();
		if (shape.equals(Constants.ShapeType.RECTANGLE)) {
			if (element.getTransferPoints().size() != 2) {
				throw new Exception("矩形图元必须2个点");
			}
			List<APoint> allPoint = new ArrayList<APoint>();
			allPoint.add(apointList.get(0));
			allPoint.add(new APoint(apointList.get(0).getX(), apointList.get(1).getY()));
			allPoint.add(apointList.get(1));
			allPoint.add(new APoint(apointList.get(1).getX(), apointList.get(0).getY()));
			polygon.setAllPoint(allPoint);
		} else {
			polygon.setAllPoint(apointList);
		}
		return polygon;
	}

	/**
	 * @author lxw
	 * @throws Exception
	 * @功能：判断圆和圆之间是否相交 @说明：
	 */
	private boolean hasOverlapBetweenCircleAndCircle(WorkElement circle, WorkElement otherCircle) throws Exception {
		/* 第一个圆的信息 */
		List<APoint> circleApointList = getAPointListFromElement(circle);
		if (circleApointList.size() != 2) {
			throw new Exception("圆图元必须2个点");
		}
		// 计算circle的半径
		APoint center = circleApointList.get(0);
		APoint circumferencePoint = circleApointList.get(1);
		double radius = Basic.getDistance(center, circumferencePoint);
		/* 另一个圆的信息 */
		List<APoint> otherCircleApointList = getAPointListFromElement(otherCircle);
		if (otherCircleApointList.size() != 2) {
			throw new Exception("圆图元必须2个点");
		}
		// 计算otherCircle的半径
		APoint otherCenter = otherCircleApointList.get(0);
		APoint otherCircumferencePoint = otherCircleApointList.get(1);
		double otherRadius = Basic.getDistance(otherCenter, otherCircumferencePoint);

		/* 判断两个圆是否相交 */
		// 计算两个圆心的距离
		double distance = Basic.getDistance(center, otherCenter);
		if (distance > radius + otherRadius) {
			return false;
		}
		return true;
	}

	/**
	 * @author lxw
	 * @功能：判断圆和矩形、多折线、多边形是否有交集
	 * @说明：其实最终都转换成了判断圆形是否和多边形有相交，这个方法有点麻烦
	 */
	private boolean hasOverlapBetweenCircleAndElement(WorkElement circle, WorkElement element) throws Exception {
		List<APoint> circleApointList = getAPointListFromElement(circle);
		if (circleApointList.size() != 2) {
			throw new Exception("圆图元必须2个点");
		}
		// 计算circle的半径
		APoint center = circleApointList.get(0);
		APoint circumferencePoint = circleApointList.get(1);
		double radius = Basic.getDistance(center, circumferencePoint);

		// 把另一个图元转换成多边形
		Polygon polygon = elementToPolygon(element);
		List<APoint> polygonApointList = polygon.getAllPoint();
		// 判断多边形是否有边的距离小于矩形，有的话肯定是相交了
		for (int i = 0; i < polygonApointList.size(); i++) {
			// 得到多边形的一条边
			LineSegment S2 = new LineSegment();
			S2.setPt1(polygonApointList.get(i));
			S2.setPt2(polygonApointList.get((i + 1) % polygonApointList.size()));
			// 计算圆心到多边形每条边的距离
			double distance = Basic.distanceToSegment(center, S2);
			if (distance < radius) {
				return true;
			}
		}

		// 没有的话，就有两种可能了，一种是多边形完全包含了圆，另一种就是矩形和圆确实不相交
		// 判断圆心是否在多边形内,通过这种方法就可以判断多边形是否包含了圆
		AlgorithmForPolygon algorithmForPolygon = new AlgorithmForPolygon();
		if (algorithmForPolygon.ptAndPoly(polygon, center).equals(ReturnValue.in.toString())) {
			return true;
		}
		return false;
	}

	/**
	 * @author lxw
	 * @功能：把地图上使用的经纬度转换成我们判断和计算距离的高斯坐标系坐标
	 * @说明：转换车载设备和手机设备上次的点
	 */
	private APoint conversionPositionToAPoint(Position position) {
		// if (!position.getDone()) {
		// DeflecttorService deflecttorService = new DeflecttorService();
		// Position position2 = (Position)
		// deflecttorService.disposeOne(position);
		// position.setDone(position2.getDone());
		// position.setLongitudeDone(position2.getLongitudeDone());
		// position.setLatitudeDone(position2.getLatitudeDone());
		// }
		return CoordinateConversion.BLToGauss(position.getLongitudeDone(), position.getLatitudeDone());
	}

	/**
	 * @author lxw
	 * @功能：把地图上使用的经纬度转换成我们判断和计算距离的高斯坐标系坐标
	 * @说明：图元图层部分的点
	 */
	private APoint conversionPointToAPoint(BasePoint point) {
		return CoordinateConversion.BLToGauss(point.getLongitude(), point.getLatitude());
	}
}
