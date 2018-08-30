package com.vortex.cloud.ums.dto;
/**
 * 人员
 * @author ll
 *
 */
public class CloudStaffPageDto {
	
	private String id;
	
	private String name;
	
	private String departmentId; // 所属公司或者环卫处
	
	private String orgId; // 所属部门
	
	private String imToken;// 融云账号token
	
	private String photoId; // 头像ID
	
	private Integer beenDeleted;
	
	private Integer orderIndex;
	
	private String nameInitial;
	
	/**
	 * 租户参数id
	 */
	private String cpId;
	/**
	 * 租户参数code
	 */
	private String parmCode;
	/**
	 * 租户参数name
	 */
	private String parmName;
	
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

	public String getDepartmentId() {
		return departmentId;
	}

	public void setDepartmentId(String departmentId) {
		this.departmentId = departmentId;
	}

	public String getImToken() {
		return imToken;
	}

	public void setImToken(String imToken) {
		this.imToken = imToken;
	}

	public String getPhotoId() {
		return photoId;
	}

	public void setPhotoId(String photoId) {
		this.photoId = photoId;
	}

	public Integer getBeenDeleted() {
		return beenDeleted;
	}

	public void setBeenDeleted(Integer beenDeleted) {
		this.beenDeleted = beenDeleted;
	}

	public String getOrgId() {
		return orgId;
	}

	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}

	public Integer getOrderIndex() {
		return orderIndex;
	}

	public void setOrderIndex(Integer orderIndex) {
		this.orderIndex = orderIndex;
	}

	public String getNameInitial() {
		return nameInitial;
	}

	public void setNameInitial(String nameInitial) {
		this.nameInitial = nameInitial;
	}

	public String getCpId() {
		return cpId;
	}

	public void setCpId(String cpId) {
		this.cpId = cpId;
	}

	public String getParmCode() {
		return parmCode;
	}

	public void setParmCode(String parmCode) {
		this.parmCode = parmCode;
	}

	public String getParmName() {
		return parmName;
	}

	public void setParmName(String parmName) {
		this.parmName = parmName;
	}
}
