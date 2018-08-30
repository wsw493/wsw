package com.vortex.cloud.ums.dto;
/**
 * 图元
 * @author ll
 *
 */
public class WorkElementPageDto {
	
	private String id;
	
	private String name;
	
	/** 外形 **/
	private String shape;
	
	/** 所属公司 **/
	private String departmentId;
	
	// 偏转后的经纬度(BD09坐标系)
    private String paramsDone;
    
    // 半径
 	private Double radius;

    private Integer beenDeleted;
    
    private Integer flag;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getShape() {
		return shape;
	}

	public void setShape(String shape) {
		this.shape = shape;
	}

	public String getDepartmentId() {
		return departmentId;
	}

	public void setDepartmentId(String departmentId) {
		this.departmentId = departmentId;
	}

	public String getParamsDone() {
		return paramsDone;
	}

	public void setParamsDone(String paramsDone) {
		this.paramsDone = paramsDone;
	}

	public Integer getBeenDeleted() {
		return beenDeleted;
	}

	public void setBeenDeleted(Integer beenDeleted) {
		this.beenDeleted = beenDeleted;
	}

	public Integer getFlag() {
		return flag;
	}

	public void setFlag(Integer flag) {
		this.flag = flag;
	}

	public Double getRadius() {
		return radius;
	}

	public void setRadius(Double radius) {
		this.radius = radius;
	}
	
}
