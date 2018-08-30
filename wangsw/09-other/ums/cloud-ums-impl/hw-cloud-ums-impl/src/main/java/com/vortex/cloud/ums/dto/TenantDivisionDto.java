package com.vortex.cloud.ums.dto;

import java.util.ArrayList;

import com.vortex.cloud.ums.model.TenantDivision;

public class TenantDivisionDto extends TenantDivision {

	private static final long serialVersionUID = 1L;

	private String parentName; // 上级区域
	private String levelText; // 行政级别的文本描述

	private Double defLongitude; // 建议的默认经度：取父节点的值
	private Double defLatitude; // 建议的默认纬度：取父节点的值

	private Double longitude; // 经度
	private Double latitude; // 纬度

	private String containsRoot;// 是否包含root节点 ，1包含，0不包含
	public static final String CONTAIN_ROOT_NO = "0";
	public static final String CONTAIN_ROOT_YES = "1";

	public String getContainsRoot() {
		return containsRoot;
	}

	public void setContainsRoot(String containsRoot) {
		this.containsRoot = containsRoot;
	}

	public String getParentName() {
		return parentName;
	}

	public void setParentName(String parentName) {
		this.parentName = parentName;
	}

	public String getLevelText() {
		return levelText;
	}

	public void setLevelText(String levelText) {
		this.levelText = levelText;
	}

	public Double getDefLongitude() {
		return defLongitude;
	}

	public void setDefLongitude(Double defLongitude) {
		this.defLongitude = defLongitude;
	}

	public Double getDefLatitude() {
		return defLatitude;
	}

	public void setDefLatitude(Double defLatitude) {
		this.defLatitude = defLatitude;
	}

	public Double getLongitude() {
		return longitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	public Double getLatitude() {
		return latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public static class BatchUpdate {
		/** 主键 uuid **/
		private String id;
		// 通用编号
		private String commonCode;
		// 区划名称
		private String name;
		// 简称
		private String abbr;

		public BatchUpdate() {

		}

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getCommonCode() {
			return commonCode;
		}

		public void setCommonCode(String commonCode) {
			this.commonCode = commonCode;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getAbbr() {
			return abbr;
		}

		public void setAbbr(String abbr) {
			this.abbr = abbr;
		}
	}

	public static class BatchUpdateList extends ArrayList<BatchUpdate> {
		private static final long serialVersionUID = -8862714475178790945L;

		public BatchUpdateList() {
			super();
		}
	}
}
