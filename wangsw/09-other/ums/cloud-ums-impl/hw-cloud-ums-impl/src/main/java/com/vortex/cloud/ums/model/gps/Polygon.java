package com.vortex.cloud.ums.model.gps;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lxw
 * @version 0.0.1
 * @since Feb 19, 2011
 * @功能：多边形对象
 * @说明：包含一组点对象
 */
public class Polygon {
	private List<APoint> allPoint = new ArrayList<APoint>();

	public Polygon() {
	}

	public Polygon(List<APoint> allPoint) {
		this.allPoint = allPoint;
	}

	public List<APoint> getAllPoint() {
		return allPoint;
	}

	public void setAllPoint(List<APoint> allPoint) {
		this.allPoint = allPoint;
	}
}
