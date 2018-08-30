package com.vortex.cloud.ums.util.model;

import com.vortex.cloud.ums.util.enums.CoordinateEnum;

/**
 * @author lxw
 * @version 0.0.1
 * @since Feb 19, 2011
 * @功能：几何算法中一些基本的算法 @说明：
 */
public class Basic {
	/* 可以接受的误差，无穷小 0.00001 */
	public static final double ESP = 1e-5;

	/**
	 * @author lxw
	 * @功能：计算叉乘 |P0Pa| x |P0Pb| 向量的点乘积 @说明：
	 */
	public static double Multiply(APoint point, LineSegment segment) {
		// 待测点的x、y坐标
		double x0 = point.getX();
		double y0 = point.getY();
		// 2个端点的x、y坐标
		double xa = segment.getPt1().getX();
		double ya = segment.getPt1().getY();
		double xb = segment.getPt2().getX();
		double yb = segment.getPt2().getY();
		return ((xa - x0) * (yb - y0) - (xb - x0) * (ya - y0));
	}

	/**
	 * @author lxw
	 * @功能：计算点乘乘 |P0Pa| * |P0Pb| 向量的点乘积 @说明：
	 */
	public static double Dot(APoint point, LineSegment segment) {
		// 待测点的x、y坐标
		double x0 = point.getX();
		double y0 = point.getY();
		// 2个端点的x、y坐标
		double xa = segment.getPt1().getX();
		double ya = segment.getPt1().getY();
		double xb = segment.getPt2().getX();
		double yb = segment.getPt2().getY();
		return ((xa - x0) * (xb - x0) + (ya - y0) * (yb - y0));
	}

	/**
	 * @author lxw
	 * @功能：判断线段是否包含点point @说明：
	 */
	public static boolean IsOnline(APoint point, LineSegment segment) {
		// 待测点的x、y坐标
		double x0 = point.getX();
		double y0 = point.getY();
		// 2个端点的x、y坐标
		double xa = segment.getPt1().getX();
		double ya = segment.getPt1().getY();
		double xb = segment.getPt2().getX();
		double yb = segment.getPt2().getY();

		return ((Math.abs(Multiply(point, segment)) < ESP) && ((x0 - xa) * (x0 - xb) <= 0) && ((y0 - ya) * (y0 - yb) <= 0));
	}

	/**
	 * @author lxw
	 * @功能：计算两点距离 @说明：
	 */
	public static double getDistance(APoint p1, APoint p2) {

		double x0 = p1.getX();
		double y0 = p1.getY();
		double xa = p2.getX();
		double ya = p2.getY();

		double xdiff = x0 - xa;
		double ydiff = y0 - ya;
		return Math.pow((xdiff * xdiff + ydiff * ydiff), 0.5);
	}

	/**
	 * @author lxw
	 * @功能：判断线段相交 @说明：
	 */
	public static boolean intersect(LineSegment S1, LineSegment S2) {
		// 两条线段的4个端点
		APoint p1 = S1.getPt1();
		APoint p2 = S1.getPt2();
		APoint p3 = S2.getPt1();
		APoint p4 = S2.getPt2();
		// 4个端点的x、y坐标
		double xa = p1.getX();
		double ya = p1.getY();
		double xb = p2.getX();
		double yb = p2.getY();
		double xc = p3.getX();
		double yc = p3.getY();
		double xd = p4.getX();
		double yd = p4.getY();

		if (!(max(xa, xb) >= min(xc, xd))) {
			return false;
		}
		if (!(max(xc, xd) >= min(xa, xb))) {
			return false;
		}
		if (!(max(ya, yb) >= min(yc, yd))) {
			return false;
		}
		if (!(max(yc, yd) >= min(ya, yb))) {
			return false;
		}
		LineSegment S3 = new LineSegment(p3, p2);
		LineSegment S4 = new LineSegment(p2, p4);
		if (!(Multiply(p1, S3) * Multiply(p1, S4) >= 0)) {
			return false;
		}
		LineSegment S5 = new LineSegment(p1, p4);
		LineSegment S6 = new LineSegment(p4, p2);
		if (!(Multiply(p3, S5) * Multiply(p3, S6) >= 0)) {
			return false;
		}
		return true;
	}

	/**
	 * @author lxw
	 * @功能：一个点到一条线段的距离 @说明：
	 */
	public static double distanceToSegment(APoint point, LineSegment segment) {
		APoint point1 = segment.getPt1();
		APoint point2 = segment.getPt2();

		if (IsOnline(point, segment)) {
			return 0.0;
		} else if (Multiply(point1, new LineSegment(point, point2)) == 0) {
			return min(getDistance(point, segment.getPt1()), getDistance(point, segment.getPt2()));
		} else if (Dot(point1, new LineSegment(point, point2)) < 0) {
			return getDistance(point, segment.getPt1());
		} else if (Dot(point2, new LineSegment(point, point1)) < 0) {
			return getDistance(point, segment.getPt2());
		} else {
			double a, b, c;// 三条边
			a = getDistance(point, segment.getPt1());
			b = getDistance(point, segment.getPt2());
			c = getDistance(segment.getPt1(), segment.getPt2());
			double p = (a + b + c) / 2;
			double s = Math.sqrt(p * (p - a) * (p - b) * (p - c));
			return 2 * s / c;
		}
	}

	/**
	 * @author lxw
	 * @功能：获取两个数字中较大的数 @说明：
	 */
	public static double max(double x, double y) {
		return (x > y ? x : y);
	}

	/**
	 * @author lxw
	 * @功能：获取两个数字中较小的数 @说明：
	 */
	public static double min(double x, double y) {
		return (x < y ? x : y);
	}

	/**
	 * 
	 * 得到经纬度距离 (单位：米)
	 * 
	 * @param lng1
	 *            经度 x
	 * @param lat1
	 *            纬度 y
	 * @param lng2
	 *            经度 x
	 * @param lat2
	 *            经度 x
	 * @param gs
	 *            坐标系
	 * @return
	 */
	public static double getDistance(double lng1, double lat1, double lng2, double lat2, CoordinateEnum coordinate) {
		double radLat1 = rad(lat1);
		double radLat2 = rad(lat2);
		double a = radLat1 - radLat2;
		double b = rad(lng1) - rad(lng2);
		double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2) + Math.cos(radLat1) * Math.cos(radLat2) * Math.pow(Math.sin(b / 2), 2)));
		s = s * (coordinate == CoordinateEnum.WGS84 ? 6378137.0 : (coordinate == CoordinateEnum.Xian80 ? 6378140.0 : 6378245.0));
		s = Math.round(s * 10000) / 10000;
		return s;
	}

	/***************************************************************************
	 * 根据两个点经纬度求距离
	 * 
	 * @author lxw
	 **************************************************************************/
	private static double rad(double d) {
		return d * Math.PI / 180.0;
	}

	static double DEF_PI = 3.14159265359; // PI
	static double DEF_2PI = 6.28318530712; // 2*PI
	static double DEF_PI180 = 0.01745329252; // PI/180.0
	static double DEF_R = 6370693.5; // radius of earth

	public double GetShortDistance(double lon1, double lat1, double lon2, double lat2) {
		double ew1, ns1, ew2, ns2;
		double dx, dy, dew;
		double distance;
		// 角度转换为弧度
		ew1 = lon1 * DEF_PI180;
		ns1 = lat1 * DEF_PI180;
		ew2 = lon2 * DEF_PI180;
		ns2 = lat2 * DEF_PI180;
		// 经度差
		dew = ew1 - ew2;
		// 若跨东经和西经180 度，进行调整
		if (dew > DEF_PI)
			dew = DEF_2PI - dew;
		else if (dew < -DEF_PI)
			dew = DEF_2PI + dew;
		dx = DEF_R * Math.cos(ns1) * dew; // 东西方向长度(在纬度圈上的投影长度)
		dy = DEF_R * (ns1 - ns2); // 南北方向长度(在经度圈上的投影长度)
		// 勾股定理求斜边长
		distance = Math.sqrt(dx * dx + dy * dy);
		return distance;
	}

	public double GetLongDistance(double lon1, double lat1, double lon2, double lat2) {
		double ew1, ns1, ew2, ns2;
		double distance;
		// 角度转换为弧度
		ew1 = lon1 * DEF_PI180;
		ns1 = lat1 * DEF_PI180;
		ew2 = lon2 * DEF_PI180;
		ns2 = lat2 * DEF_PI180;
		// 求大圆劣弧与球心所夹的角(弧度)
		distance = Math.sin(ns1) * Math.sin(ns2) + Math.cos(ns1) * Math.cos(ns2) * Math.cos(ew1 - ew2);
		// 调整到[-1..1]范围内，避免溢出
		if (distance > 1.0)
			distance = 1.0;
		else if (distance < -1.0)
			distance = -1.0;
		// 求大圆劣弧长度
		distance = DEF_R * Math.acos(distance);
		return distance;
	}

	double mLat1 = 39.90923; // point1纬度
	double mLon1 = 116.357428; // point1经度
	double mLat2 = 39.90923;// point2纬度
	double mLon2 = 116.397428;// point2经度
	double distance = GetShortDistance(mLon1, mLat1, mLon2, mLat2);

	public static void main(String[] args) {
		Basic a = new Basic();
		double mLat1 = 39.90923; // point1纬度
		double mLon1 = 116.357428; // point1经度
		double mLat2 = 39.90923;// point2纬度
		double mLon2 = 116.397428;// point2经度

	}
}
