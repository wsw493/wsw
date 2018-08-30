package com.vortex.cloud.ums.dto;

import java.io.Serializable;
import java.util.Date;

import com.vortex.cloud.ums.enums.CompanyTypeEnum;
import com.vortex.cloud.ums.model.CloudDepartment;
import com.vortex.cloud.ums.model.CloudOrganization;

/**
 * @author LiShijun
 * @date 2016年3月30日 上午9:45:39
 * @Description 整合统一描述租户下的单位、机构。方便用在树的构造等功能上。 History <author> <time> <desc>
 */
public class TenantDeptOrgDto  implements Serializable{
	private String tenantId; // 租户id
	private String parentId; // 上级id
	private String departmentId; // 部门表id
	private String type; // 标识是环卫处、作业公司、下属的普通组织机构
	private String companyType; // 标识是depart层级还是org层级
	private String id; // 记录主键ID
	private String name; // 机构名称
	private String code; // 机构代码

	private String head; // 负责人
	private String headMobile; // 负责人电话
	private String description; // 描述
	private String lngLats; // 经纬度
	private String address; // 地址
	private String email; // 邮箱

	private Boolean fullChecked = true; // 该节点在checkebox中是否被全选（就是所有子节点都被选中就是全选状态）

	/** 备份删除 0:未删除，1：已删除 **/
	private Integer beenDeleted;
	/** 删除时间 **/
	private Date deletedTime;

	/** 记录创建时间 **/
	private Date createTime;
	/** 记录最后修改时间 **/
	private Date lastChangeTime;
	
	/**
	 * 新增，删除，修改标记
	 */
	private Integer flag;
	private Integer orderIndex;
	public Boolean getFullChecked() {
		return fullChecked;
	}

	public void setFullChecked(Boolean fullChecked) {
		this.fullChecked = fullChecked;
	}

	public String getCompanyType() {
		return companyType;
	}

	public void setCompanyType(String companyType) {
		this.companyType = companyType;
	}

	public String getTenantId() {
		return tenantId;
	}

	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public String getDepartmentId() {
		return departmentId;
	}

	public void setDepartmentId(String departmentId) {
		this.departmentId = departmentId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

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

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getHead() {
		return head;
	}

	public void setHead(String head) {
		this.head = head;
	}

	public String getHeadMobile() {
		return headMobile;
	}

	public void setHeadMobile(String headMobile) {
		this.headMobile = headMobile;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getLngLats() {
		return lngLats;
	}

	public void setLngLats(String lngLats) {
		this.lngLats = lngLats;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Integer getBeenDeleted() {
		return beenDeleted;
	}

	public void setBeenDeleted(Integer beenDeleted) {
		this.beenDeleted = beenDeleted;
	}

	public Date getDeletedTime() {
		return deletedTime;
	}

	public void setDeletedTime(Date deletedTime) {
		this.deletedTime = deletedTime;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getLastChangeTime() {
		return lastChangeTime;
	}

	public void setLastChangeTime(Date lastChangeTime) {
		this.lastChangeTime = lastChangeTime;
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
	/**
	 * 转化
	 * 
	 * @param funcDef
	 */
	public TenantDeptOrgDto transfer(CloudDepartment entity) {
		try {
			this.setTenantId(entity.getTenantId());
			this.setParentId("-1");
			this.setDepartmentId(entity.getId());
			this.setType(entity.getDepType());
			this.setCompanyType(CompanyTypeEnum.DEPART.getKey());
			this.setId(entity.getId());
			this.setName(entity.getDepName());
			this.setCode(entity.getDepCode());
			this.setLngLats(entity.getLngLats());
			this.setBeenDeleted(entity.getBeenDeleted());
			this.setDeletedTime(entity.getDeletedTime());
			this.setCreateTime(entity.getCreateTime());
			this.setLastChangeTime(entity.getLastChangeTime());
			this.setEmail(entity.getEmail());
			this.setAddress(entity.getAddress());
			this.setDescription(entity.getDescription());
			this.setHeadMobile(entity.getHeadMobile());
			this.setHead(entity.getHead());
			this.setOrderIndex(entity.getOrderIndex());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return this;
	}

	/**
	 * 转化
	 * 
	 * @param funcDef
	 */
	public TenantDeptOrgDto transfer(CloudOrganization entity) {
		try {
			this.setTenantId(entity.getTenantId());
			this.setParentId(entity.getParentId());
			this.setDepartmentId(entity.getDepartmentId());
			this.setType("3");
			this.setCompanyType(CompanyTypeEnum.ORG.getKey());
			this.setId(entity.getId());
			this.setName(entity.getOrgName());
			this.setCode(entity.getOrgCode());
			this.setLngLats(entity.getLngLats());
			this.setBeenDeleted(entity.getBeenDeleted());
			this.setDeletedTime(entity.getDeletedTime());
			this.setCreateTime(entity.getCreateTime());
			this.setLastChangeTime(entity.getLastChangeTime());
			this.setEmail(entity.getEmail());
			this.setAddress(entity.getAddress());
			this.setDescription(entity.getDescription());
			this.setHeadMobile(entity.getHeadMobile());
			this.setHead(entity.getHead());
			this.setOrderIndex(entity.getOrderIndex());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return this;
	}
}
