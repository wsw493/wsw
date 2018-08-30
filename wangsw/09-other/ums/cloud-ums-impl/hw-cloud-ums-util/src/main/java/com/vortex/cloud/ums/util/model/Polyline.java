package com.vortex.cloud.ums.util.model;

import java.util.List;

/**
 * @author lxw
 * @version 0.0.1
 * @since Feb 19, 2011
 * @功能：多折线对象
 * @说明：包含一组点对象
 */
public class Polyline {
	private List<APoint> allPoint = null;// new ArrayList<Point>();

	public Polyline() {
	}

	public Polyline(List<APoint> allPoint) {
		this.allPoint = allPoint;
	}

	public List<APoint> getAllPoint() {
		return allPoint;
	}

	public void setAllPoint(List<APoint> allPoint) {
		this.allPoint = allPoint;
	}
}
