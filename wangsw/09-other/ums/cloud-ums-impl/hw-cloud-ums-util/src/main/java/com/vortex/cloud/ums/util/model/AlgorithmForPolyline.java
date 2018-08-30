package com.vortex.cloud.ums.util.model;

import static com.vortex.cloud.ums.util.model.Basic.distanceToSegment;
import static com.vortex.cloud.ums.util.model.Basic.min;

import java.util.List;

/**
 * @author lxw
 * @version 0.0.1
 * @since Feb 19, 2011
 * @功能：多折线的一些方法 @说明：
 */
public class AlgorithmForPolyline {

	/**
	 * @author lxw
	 * @功能：获取一个点到一条多折线的距离 @说明：
	 */
	public double getDistanceToLine(APoint aPoint, Polyline polyline) {
		double endDistance = -1;
		for (int i = 0; i < polyline.getAllPoint().size() - 1; i++) {
			APoint a = polyline.getAllPoint().get(i);
			APoint b = polyline.getAllPoint().get(i + 1);
			LineSegment segment = new LineSegment(a, b);
			double distance = distanceToSegment(aPoint, segment);
			// 如果在这条线段上，必然在多折线上，返回0
			if (distance == 0) {
				return 0;
			}
			// 如果需要返回的距离还没有完成初始化，先使用获取的值初始化
			if (endDistance == -1) {
				endDistance = distance;
			} else {
				// 取相对较小的值
				endDistance = min(endDistance, distance);
			}
		}

		return endDistance;
	}

	/**
	 * @author lxw
	 * @功能：判断一个点到一条多折线的距离是否在一个距离范围内 @说明：
	 */
	public boolean isDistanceInRange(APoint point, Polyline polyline, double min, double max) {
		List<APoint> allPoint = polyline.getAllPoint();
		for (int i = 0; i < allPoint.size() - 1; i++) {
			APoint a = allPoint.get(i);
			APoint b = allPoint.get(i + 1);
			LineSegment segment = new LineSegment(a, b);
			double distance = distanceToSegment(point, segment);
			if (distance < min || distance > max) {
				return false;
			}
		}
		return true;
	}

	/**
	 * @author lxw
	 * @功能：判断一个点到一条多折线的距离是否小于一个值 @说明：
	 */
	public boolean isDistanceShorter(APoint point, Polyline polyline, double max) {
		return isDistanceInRange(point, polyline, 0, max);
	}

	/**
	 * @author lxw
	 * @功能：判断一个点到一条多折线的距离是否大于一个值 @说明：
	 */
	public boolean isDistanceLonger(APoint point, Polyline polyline, double min) {
		return !isDistanceShorter(point, polyline, min);
	}
}
