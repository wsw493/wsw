package com.vortex.cloud.ums.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.vortex.cloud.vfs.data.model.BakDeleteModel;



/**
 * 租户上面开启的云系统列表
 * 
 * @author XY
 *
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "cloud_tenantSystem_relation")
public class TenantSystemRelation extends BakDeleteModel {
	private String tenantId; // 租户id
	private String cloudSystemId; // 云上面的系统id
	private String enabled; // 系统是否开通，开通为1，未开通为0
	private String hasResource; // 是否复制过资源给租户，1复制过，0未复制

	// 系统是否开通
	public static final String ENABLE = "1";	// 启用系统
	public static final String DISABLE = "0";	// 禁用系统
	
	// 是否复制过资源给租户
	public static final String YES = "1";	// 复制过
	public static final String NOT = "0";	// 未复制
	
	@Column(name = "tenantId", length = 32, nullable = false)
	public String getTenantId() {
		return tenantId;
	}

	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}

	@Column(name = "cloudSystemId", length = 32, nullable = false)
	public String getCloudSystemId() {
		return cloudSystemId;
	}

	public void setCloudSystemId(String cloudSystemId) {
		this.cloudSystemId = cloudSystemId;
	}

	@Column(name = "enabled", length = 32, nullable = false)
	public String getEnabled() {
		return enabled;
	}

	public void setEnabled(String enabled) {
		this.enabled = enabled;
	}

	@Column(name = "hasResource", length = 32, nullable = false)
	public String getHasResource() {
		return hasResource;
	}

	public void setHasResource(String hasResource) {
		this.hasResource = hasResource;
	}
}
