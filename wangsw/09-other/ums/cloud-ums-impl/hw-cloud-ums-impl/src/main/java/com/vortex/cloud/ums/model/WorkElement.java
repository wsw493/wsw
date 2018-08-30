package com.vortex.cloud.ums.model;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Lists;
import com.vortex.cloud.vfs.common.lang.StringUtil;
import com.vortex.cloud.vfs.data.model.BakDeleteModel;



/**
 * 工作图元
 * 
 * @author dejunx
 * 
 */

@SuppressWarnings("serial")
@Entity
@Table(name = "cloud_work_element")
public class WorkElement extends BakDeleteModel {
	private String tenantId; // 租户id

	/** 外形 **/
	private String shape;
	/** 编号 **/
	private String code;
	/** 名称 **/
	private String name;

	/**
     * 经纬度 xxx,xxx;xxx,xxx
     * (WGS84坐标系)
     */
	private String params;
	// 偏转后的经纬度(BD09坐标系)
	private String paramsDone;

	/** 面积(米) **/
	private Double area;
	// 长度
	private Double length;
	// 半径
	private Double radius;
	// 颜色
	private String color;

	/** 所属公司 **/
	private String departmentId;

	private List<BasePoint> points = Lists.newArrayList();

	/** 描述 **/
	private String description;

	/** 工作区域类型 ***/
	private String workElementTypeId;

	/**
	 * 所属用户
	 */
	private String userId;
	/**
	 * 行政区划ID
	 */
	private String divisionId;
	
	
	@Column(name = "f_division_id")
	public String getDivisionId() {
		return divisionId;
	}

	public void setDivisionId(String divisionId) {
		this.divisionId = divisionId;
	}

	@Column(name = "f_area")
	public Double getArea() {
		return area;
	}

	public void setArea(Double area) {
		this.area = area;
	}

	@Column(name = "f_work_element_type_id")
	public String getWorkElementTypeId() {
		return workElementTypeId;
	}

	public void setWorkElementTypeId(String workElementTypeId) {
		this.workElementTypeId = workElementTypeId;
	}

	@Column(name = "f_code")
	public String getCode() {
		return this.code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	@Column(name = "f_name")
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "f_params")
	@Lob
	public String getParams() {
		return this.params;
	}

	public void setParams(String params) {
		this.params = params;
	}

	@Column(name = "f_shape", nullable = true)
	public String getShape() {
		return shape;
	}

	public void setShape(String shape) {
		this.shape = shape;
	}

	@Column(name = "f_tenantId", nullable = false)
	public String getTenantId() {
		return tenantId;
	}

	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}

	@Transient
	@JsonIgnore
	public List<BasePoint> getPoints() {
		return this.points;
	}

	public void setPoints(List<BasePoint> points) {
		this.points = points;
	}

	@Column(name = "f_deptId")
	public String getDepartmentId() {
		return departmentId;
	}

	public void setDepartmentId(String departmentId) {
		this.departmentId = departmentId;
	}

	@Column(name = "f_description")
	@Lob
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Column(name = "f_params_done")
	@Lob
	public String getParamsDone() {
		return paramsDone;
	}

	public void setParamsDone(String paramsDone) {
		this.paramsDone = paramsDone;
	}

	@Column(name = "f_length")
	public Double getLength() {
		return length;
	}

	public void setLength(Double length) {
		this.length = length;
	}

	@Column(name = "f_radius")
	public Double getRadius() {
		return radius;
	}

	public void setRadius(Double radius) {
		this.radius = radius;
	}

	@Column(name = "f_color")
	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public static void main(String[] args) {
		System.out.println(new Date().getTime());
	}

	@Column(name = "f_user_id")
	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	@Transient
	@JsonIgnore
	public List<BasePoint> getTransferPoints() {
		List<BasePoint> pointLists = Lists.newArrayList();
		if (!StringUtil.isNullOrEmpty(params)) {
			String[] lnglats = params.split(";");
			BasePoint point = null;
			for (String s : lnglats) {
				String[] lnglat = s.split(",");
				point = new BasePoint();
				point.setLongitude(Double.valueOf(lnglat[0]));
				point.setLatitude(Double.valueOf(lnglat[1]));
				point.setDone(true);
				point.setLongitudeDone(Double.valueOf(lnglat[0]));
				point.setLatitudeDone(Double.valueOf(lnglat[1]));
				pointLists.add(point);
			}
		}
		return pointLists;
	}
}
