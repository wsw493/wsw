package com.vortex.cloud.ums.util.model;

/**
 * @author lxw
 * @version 0.0.1
 * @since Feb 19, 2011
 * @功能：点对象
 * @说明：几何算法中的点，区别于上层系统的点
 */
public class APoint {
	private double x;
	private double y;

	public APoint() {
	}

	public APoint(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	public String out() {
		return "x[" + x + "],y[" + y + "]";
	}
}
