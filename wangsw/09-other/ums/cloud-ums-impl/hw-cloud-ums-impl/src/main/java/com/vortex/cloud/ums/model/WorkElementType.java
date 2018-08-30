package com.vortex.cloud.ums.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.vortex.cloud.vfs.data.model.BakDeleteModel;



/**
 * 工作图元类型
 * 
 * @author dejunx
 * 
 */

@SuppressWarnings("all")
@Entity
@Table(name = "cloud_work_element_type")
public class WorkElementType extends BakDeleteModel {

	private String tenantId; // 租户id

	private String code;// 编号

	private String name;// 名称

	/** 外形 point：点，line:线，polygon：多边形，rectangle：矩形，circle：圆 **/
	private String shape;

	private String info;
	/** 所属公司 **/
	private String departmentId;
	
	private Integer orderIndex;

	@Column(name = "f_code", nullable = false, length = 32)
	public String getCode() {
		return this.code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	@Column(name = "f_name", nullable = false, length = 32)
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "f_deptId", nullable = true, length = 32)
	public String getDepartmentId() {
		return departmentId;
	}

	public void setDepartmentId(String departmentId) {
		this.departmentId = departmentId;
	}

	@Column(name = "f_shape", nullable = true, length = 32)
	public String getShape() {
		return shape;
	}

	public void setShape(String shape) {
		this.shape = shape;
	}

	@Column(name = "f_info", nullable = true, length = 32)
	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	@Column(name = "f_tenantId", nullable = false, length = 32)
	public String getTenantId() {
		return tenantId;
	}

	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}

	public Integer getOrderIndex() {
		return orderIndex;
	}

	public void setOrderIndex(Integer orderIndex) {
		this.orderIndex = orderIndex;
	}
	
}
