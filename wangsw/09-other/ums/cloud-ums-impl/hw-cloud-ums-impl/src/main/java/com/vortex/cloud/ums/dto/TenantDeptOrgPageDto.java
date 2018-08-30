package com.vortex.cloud.ums.dto;
/**
 * 整合统一描述租户下的单位、机构
 * @author ll
 *
 */
public class TenantDeptOrgPageDto {
	
	private String id; // 记录主键ID departmentId
	private String parentId; // 上级id
	private String name; // 机构名称
	/** 备份删除 0:未删除，1：已删除 **/
	private Integer beenDeleted;
	
	private Integer flag;
	
	private Integer orderIndex;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getParentId() {
		return parentId;
	}
	public void setParentId(String parentId) {
		this.parentId = parentId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
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
	public Integer getOrderIndex() {
		return orderIndex;
	}
	public void setOrderIndex(Integer orderIndex) {
		this.orderIndex = orderIndex;
	}
}
