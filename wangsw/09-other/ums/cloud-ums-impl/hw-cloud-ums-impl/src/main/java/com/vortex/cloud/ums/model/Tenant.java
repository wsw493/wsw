package com.vortex.cloud.ums.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.vortex.cloud.vfs.data.model.BakDeleteModel;



/**
 * 租户表
 * 
 * @author XY
 *
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "cloud_management_tenant")
public class Tenant extends BakDeleteModel {
	private String tenantCode; // 租户Code
	private String tenantName; // 租户名称
	private String domain; // 租户访问url
	private String menuUrl; // 租户菜单url
	private String navigationUrl; // 导航url
	private String contact; // 联系人
	private String phone; // 联系电话
	private String email; // 邮件地址
	private Double latitude; // 纬度
	private Double latitudeDone; // 偏转后的纬度
	private Double longitude; // 经度
	private Double longitudeDone; // 偏转后的经度
	private String enabled; // 是否启用1：启用，0不启用
	private String divisionId; // 起始行政区划id
	
	@Column(name = "tenantCode", length = 64, nullable = false)
	public String getTenantCode() {
		return tenantCode;
	}

	public void setTenantCode(String tenantCode) {
		this.tenantCode = tenantCode;
	}

	@Column(name = "tenantName", length = 64, nullable = false)
	public String getTenantName() {
		return tenantName;
	}

	public void setTenantName(String tenantName) {
		this.tenantName = tenantName;
	}

	@Column(name = "domain", length = 64, nullable = true)
	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	@Column(name = "contact", length = 64, nullable = true)
	public String getContact() {
		return contact;
	}

	public void setContact(String contact) {
		this.contact = contact;
	}

	@Column(name = "phone", length = 32, nullable = true)
	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	@Column(name = "email", length = 128, nullable = true)
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Column(name = "longitude", nullable = true)
	public Double getLongitude() {
		return longitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	@Column(name = "latitude", nullable = true)
	public Double getLatitude() {
		return latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	@Column(name = "longitudeDone", nullable = true)
	public Double getLongitudeDone() {
		return longitudeDone;
	}

	public void setLongitudeDone(Double longitudeDone) {
		this.longitudeDone = longitudeDone;
	}

	@Column(name = "latitudeDone", nullable = true)
	public Double getLatitudeDone() {
		return latitudeDone;
	}

	public void setLatitudeDone(Double latitudeDone) {
		this.latitudeDone = latitudeDone;
	}

	@Column(name = "enabled", length = 1, nullable = false)
	public String getEnabled() {
		return enabled;
	}

	public void setEnabled(String enabled) {
		this.enabled = enabled;
	}
	
	@Column(name = "divisionId", length = 64, nullable = true)
	public String getDivisionId() {
		return divisionId;
	}

	public void setDivisionId(String divisionId) {
		this.divisionId = divisionId;
	}
	@Column(name = "menuUrl", length = 64, nullable = true)
	public String getMenuUrl() {
		return menuUrl;
	}

	public void setMenuUrl(String menuUrl) {
		this.menuUrl = menuUrl;
	}
	@Column(name = "navigationUrl", length = 64, nullable = true)
	public String getNavigationUrl() {
		return navigationUrl;
	}

	public void setNavigationUrl(String navigationUrl) {
		this.navigationUrl = navigationUrl;
	}
}
