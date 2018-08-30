package com.vortex.cloud.ums.model.gps;

import java.util.ArrayList;
import java.util.List;
import static com.vortex.cloud.ums.model.gps.Basic.*;

/**
 * @author lxw
 * @version 0.0.1
 * @since Feb 19, 2011
 * @功能：多边形的功能
 * @说明：
 */
public class AlgorithmForPolygon {
	/* 可以接受的误差 */
	private final double ESP = Basic.ESP;  
	/* 另外一个点的x坐标，无穷大 */
	private final double INFINITY = 1e10; 

	/**
	 * @author lxw
	 * @功能：点与多边形的关系：（1）点在多边形内部		（2）点在多边形外部	（3）点在多边形边上
	 * @说明：判断点在多边形内部的方法：
	 * 		（1）通过点做向右的一条射线,如果射线与多边形相交的点为奇数，则在多边形内.
	 * 		（2）所有边（点在此边之左）的和如果是奇数，则在多边形内
	 *  是否相交判断： 1。判断在边的左边或右边（也有可能在线段上或线段的延伸线上）2。判断点的Y值是否在线段两个端点Y值之间
	 */
	public String ptAndPoly(Polygon polygon, APoint point) {
		List<APoint> allPoint = new ArrayList<APoint>();
		allPoint = polygon.getAllPoint();

		double x0 = point.getX();
		double y0 = point.getY();

		int n = allPoint.size();
		if (n == 1) {
			double xa = allPoint.get(0).getX();
			double ya = allPoint.get(0).getY();
			if ((Math.abs(xa - x0) < ESP) && (Math.abs(ya - y0) < ESP)) {
				return ReturnValue.in.toString();
			} else {
				return ReturnValue.out.toString();
			}
		} else if (n == 2) {
			LineSegment segment = new LineSegment();
			segment.setPt1(allPoint.get(0));
			segment.setPt2(allPoint.get(1));
			if (IsOnline(point, segment)) {
				return ReturnValue.in.toString();
			} else {
				return ReturnValue.out.toString();
			}
		}
		// 相交的边的总数
		int count = 0;
		// 从需要判断的点，向左作出一条射线
		LineSegment S1 = new LineSegment();
		S1.setPt1(point);
		S1.setPt2(new APoint(-INFINITY, point.getY()));
		for (int i = 0; i < n; i++) {
			// 得到多边形的一条边
			LineSegment S2 = new LineSegment();
			S2.setPt1(allPoint.get(i));
			S2.setPt2(allPoint.get((i + 1) % n));
			double ya = S2.getPt1().getY();
			double yb = S2.getPt2().getY();

			// 判断是否在这条边上
			if (IsOnline(point, S2)) {
				return ReturnValue.at.toString();
			}
			// 如果S2平行x轴则不作考虑
			if (Math.abs(ya - yb) < ESP) {
				continue;
			}
			// 如果穿过多边形的顶点，不加入总数
			if (IsOnline(S2.getPt1(), S1)) {
				if (ya > yb)
					count++;
			} else if (IsOnline(S2.getPt2(), S1)) {
				if (yb > ya)
					count++;
			} else if (intersect(S1, S2)) {
				count++;
			}
		}
		return (count % 2 == 1) ? ReturnValue.in.toString() : ReturnValue.out
				.toString();
	}

	/**
	 * @author lxw
	 * @功能：判断两个多边形是否相交
	 * @说明：
	 */
	public boolean polygonIsIntersect(Polygon polygon1, Polygon polygon2) {
		//如果两个多边形中，有一个矩形包含了另一个多边形的点
		if (polygon1HasPointInPolygon2(polygon1, polygon2)
				|| polygon1HasPointInPolygon2(polygon2, polygon1)) {
			return true;
		}
		
		// 如果没有一个点在另一个多边形的内部
		for (int i = 0; i < polygon1.getAllPoint().size() - 1; i++) {
			LineSegment segment = new LineSegment(polygon1.getAllPoint().get(i),
					polygon1.getAllPoint().get(i + 1));// 获取第一个多边形的一条线段
			for (int j = 0; j < polygon2.getAllPoint().size() - 1; j++) {
				LineSegment segment2 = new LineSegment(polygon2.getAllPoint()
						.get(j), polygon2.getAllPoint().get(j + 1));// 获取第二个多边形的一条线段
				if (intersect(segment, segment2)) {// 判断是否相交，如果有线段相交，这是有重叠
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * @author lxw
	 * @功能：判断第一个多边形是否有点在第二个多边形内部
	 * @说明：
	 */
	private boolean polygon1HasPointInPolygon2(Polygon polygon1,
			Polygon polygon2) {
		for (int i = 0; i < polygon1.getAllPoint().size(); i++) {
			APoint aPoint = polygon1.getAllPoint().get(i);
			if (ptAndPoly(polygon2, aPoint).equals(ReturnValue.in.toString())) {
				return true;
			}
		}
		return false;
	}
}
