package com.vortex.cloud.ums.util.model;

/**
 * @author lxw
 * @version 0.0.1
 * @since Feb 19, 2011
 * @功能：线段对象
 * @说明：包含两个点对象
 */
public class LineSegment {
	private APoint pt1;
	private APoint pt2;

	public LineSegment() {
	}

	public LineSegment(APoint pt1, APoint pt2) {
		this.pt1 = pt1;
		this.pt2 = pt2;
	}

	public APoint getPt1() {
		return pt1;
	}

	public void setPt1(APoint pt1) {
		this.pt1 = pt1;
	}

	public APoint getPt2() {
		return pt2;
	}

	public void setPt2(APoint pt2) {
		this.pt2 = pt2;
	}

}
