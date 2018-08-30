package com.vortex.cloud.ums.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.vortex.cloud.vfs.data.model.BakDeleteModel;



@Entity
@Table(name = "cloud_division")
public class CloudDivision extends BakDeleteModel {
	private static final long serialVersionUID = 2147802252513055173L;
	/**
	 * 行政区划的级别：省
	 */
	public static final int LEVEL_PROVINCE = 1;
	/**
	 * 行政区划的级别：市
	 */
	public static final int LEVEL_CITY = LEVEL_PROVINCE + 1;
	/**
	 * 行政区划的级别：区/县
	 */
	public static final int LEVEL_DISTRICT = LEVEL_CITY + 1;
	/**
	 * 行政区划的级别：乡镇/街道
	 */
	public static final int LEVEL_VILLAGE = LEVEL_DISTRICT + 1;
	/**
	 * 行政区划的级别：居委会
	 */
	public static final int LEVEL_RUSTIC = LEVEL_VILLAGE + 1;

	// 通用编号
	private String commonCode;
	// 区划名称
	private String name;
	// 简称
	private String abbr;
	// 行政级别
	private Integer level;
	// 上级区划
	private String parentId;

	private String lngLats; //行政区划中心点

	// 生效日期
	private String startTime;
	// 失效日期
	private String endTime;
	// 是否有效1 ： 是，0 ：否；
	private Integer enabled = 1;

	private Integer defFlag; // 预设标志，全国省市区的都是预设的，下面的可以自定义，预设的不能删除

	// 内置编号：用于层级数据结构的构造（如树）
	private String nodeCode;
	
	// 子层所有数据记录数，和己编号配置生成子编号
	private Integer childSerialNumer;
	// 顺序号
	private Integer orderIndex; 
	
	
	
	public Integer getOrderIndex() {
		return orderIndex;
	}

	public void setOrderIndex(Integer orderIndex) {
		this.orderIndex = orderIndex;
	}

	@Column(name = "defFlag")
	public Integer getDefFlag() {
		return defFlag;
	}

	public void setDefFlag(Integer defFlag) {
		this.defFlag = defFlag;
	}

	@Column(name = "startTime")
	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	@Column(name = "endTime")
	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	@Column(name = "enabled")
	public Integer getEnabled() {
		return enabled;
	}

	public void setEnabled(Integer enabled) {
		this.enabled = enabled;
	}

	/**
	 * 关于unique = true, 不要此约束，因为删除的记录仍然会参与约束，造成前后端不一致。应该由程序自身控制此字段的唯一性。
	 * @return
	 */
	@Column(name = "commonCode", nullable = false)
	public String getCommonCode() {
		return commonCode;
	}

	public void setCommonCode(String commonCode) {
		this.commonCode = commonCode;
	}

	@Column(name = "name", nullable = false)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "abbr")
	public String getAbbr() {
		return abbr;
	}

	public void setAbbr(String abbr) {
		this.abbr = abbr;
	}

	@Column(name = "lvl", nullable = false)
	public Integer getLevel() {
		return level;
	}

	public void setLevel(Integer level) {
		this.level = level;
	}

	@Column(name = "parentId", length = 32, nullable = true)
	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	@Column(name = "lngLats")
	public String getLngLats() {
		return lngLats;
	}

	public void setLngLats(String lngLats) {
		this.lngLats = lngLats;
	}

	@Column(name = "nodeCode", unique = true, nullable = false)
	public String getNodeCode() {
		return nodeCode;
	}

	public void setNodeCode(String nodeCode) {
		this.nodeCode = nodeCode;
	}

	@Column(name = "childSerialNumber", nullable = false)
	public Integer getChildSerialNumer() {
		return childSerialNumer;
	}

	public void setChildSerialNumer(Integer childSerialNumer) {
		this.childSerialNumer = childSerialNumer;
	}
}
