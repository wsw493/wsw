package com.vortex.cloud.ums.util.model;

import com.vortex.cloud.vfs.common.lang.StringUtil;

/**
 * 图元
 * 
 * @author XY
 *
 */
public class BasePrimitive {
	private String shapeType; // 图元类型
	private String params; // "经度,纬度;经度,纬度"，类似这样的坐标串；其中圆用"圆心经度,圆心纬度,半径"表示

	/**
	 * 得到半径
	 * 
	 * @return
	 */
	public Double getRadius() {
		if (!StringUtil.isNullOrEmpty(params)) {
			String[] lnglats = params.split(",");
			if (lnglats.length == 3) {
				return Double.valueOf(lnglats[2]);
			}
		}
		return null;
	}

	public String getShapeType() {
		return shapeType;
	}

	public void setShapeType(String shapeType) {
		this.shapeType = shapeType;
	}

	public String getParams() {
		return params;
	}

	public void setParams(String params) {
		this.params = params;
	}
}
