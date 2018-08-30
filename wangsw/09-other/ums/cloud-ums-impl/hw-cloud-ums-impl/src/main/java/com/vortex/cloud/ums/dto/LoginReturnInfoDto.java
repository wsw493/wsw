package com.vortex.cloud.ums.dto;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * 登录验证服务返回信息
 * 
 * @author XY
 *
 */
public class LoginReturnInfoDto {
	private String userName; // 用户名
	private String userId; // 用户id
	private String staffId; // 人员基本信息id
	private String name; // 姓名
	private String phone; // 电话
	private String email; // 邮箱
	private String tenantId; // 租户id
	private List<String> systemList = new ArrayList<String>(); // 可访问的系统列表
	private String departmentId; // 部门id
	private String orgId; // 机构id
	private String password; // 密码
	private String photoId; // 头像图片id
	private String departmentName; // 单位名称
	private String orgName; // 机构名称
	private Double latitude; // 纬度
	private Double latitudeDone; // 偏转后的纬度
	private Double longitude; // 经度
	private Double longitudeDone; // 偏转后的经度
	private String rongLianAccount; // 容联帐号
	private String imToken;//融云imToken

	public String getRongLianAccount() {
		return rongLianAccount;
	}

	public void setRongLianAccount(String rongLianAccount) {
		this.rongLianAccount = rongLianAccount;
	}

	public Double getLatitude() {
		return latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public Double getLatitudeDone() {
		return latitudeDone;
	}

	public void setLatitudeDone(Double latitudeDone) {
		this.latitudeDone = latitudeDone;
	}

	public Double getLongitude() {
		return longitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	public Double getLongitudeDone() {
		return longitudeDone;
	}

	public void setLongitudeDone(Double longitudeDone) {
		this.longitudeDone = longitudeDone;
	}

	public String getDepartmentName() {
		return departmentName;
	}

	public void setDepartmentName(String departmentName) {
		this.departmentName = departmentName;
	}

	public String getOrgName() {
		return orgName;
	}

	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getStaffId() {
		return staffId;
	}

	public void setStaffId(String staffId) {
		this.staffId = staffId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getTenantId() {
		return tenantId;
	}

	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}

	public List<String> getSystemList() {
		return systemList;
	}

	public void setSystemList(List<String> systemList) {
		this.systemList = systemList;
	}

	public String getDepartmentId() {
		return departmentId;
	}

	public void setDepartmentId(String departmentId) {
		this.departmentId = departmentId;
	}

	public String getOrgId() {
		return orgId;
	}

	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	public String getPhotoId() {
		return photoId;
	}

	public void setPhotoId(String photoId) {
		this.photoId = photoId;
	}

	public String getImToken() {
		return imToken;
	}

	public void setImToken(String imToken) {
		this.imToken = imToken;
	}
	
}
