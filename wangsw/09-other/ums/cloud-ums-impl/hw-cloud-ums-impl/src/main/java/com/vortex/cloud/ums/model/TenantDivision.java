package com.vortex.cloud.ums.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

import com.vortex.cloud.vfs.data.model.BakDeleteModel;



/**
 * 新增租户后，根据选择的行政区划根节点，复制该节点及其以下的所有节点到本表，供租户个性化
 * 
 * @author XY
 *
 */
@Entity
@Table(name = "cloud_tenant_division")
public class TenantDivision extends BakDeleteModel {
	private static final long serialVersionUID = -1481135267652365105L;
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

	public static final Integer ENABLED_YES = 1;
	public static final Integer ENABLED_NOT = 0;

	public static final String ROOT_YES = "1";
	public static final String ROOT_NOT = "0";

	private String tenantId; // 租户Id

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

	private String lngLats; // 行政区划中心点

	// 生效日期
	private String startTime;
	// 失效日期
	private String endTime;
	// 是否有效1 ： 是，0 ：否；
	private Integer enabled = 1;

	private String isRoot; // 是否根节点，1：是，0：否

	// 内置编号：用于层级数据结构的构造（如树）
	private String nodeCode;

	// 子层所有数据记录数，和己编号配置生成子编号
	private Integer childSerialNumer;
	// 行政区划范围，经纬度用,分隔，点之间用;分割
	private String scope;
	// 顺序号
	private Integer orderIndex;

	public Integer getOrderIndex() {
		return orderIndex;
	}

	public void setOrderIndex(Integer orderIndex) {
		this.orderIndex = orderIndex;
	}

	@Column(name = "tenantId", length = 32, nullable = false)
	public String getTenantId() {
		return tenantId;
	}

	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}

	@Column(name = "isRoot", length = 1, nullable = false)
	public String getIsRoot() {
		return isRoot;
	}

	public void setIsRoot(String isRoot) {
		this.isRoot = isRoot;
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

	@Column(name = "nodeCode", nullable = false)
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

	@Type(type = "text")
	@Column(name = "scope", nullable = true)
	public String getScope() {
		return scope;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}

}
