package com.vortex.cloud.ums.model;

import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Lob;
import javax.persistence.Table;

import com.vortex.cloud.vfs.data.model.BakDeleteModel;



@Entity
@Table(name = "cloud_user")
public class CloudUser extends BakDeleteModel {
	private static final long serialVersionUID = 1L;

	private String staffId; // 人员基本信息id
	private String mobilePushMsgId; // 手机推送id
	private String rongLianAccount; // 容联帐号
	private String userName; // 用户名
	private String password; // 密码
	private Boolean beenSsoLogin; // 是否可以单点登录
	private Date lastSsoLoginTime; // 最后登录时间
	private Integer isRoot = IS_ROOT_NO; // 是否超级管理员
	private String photoId; // 头像id
	private String permissionScope;// 权限范围对应PermissionScopeEnum的值
	private String customScope;// 自定义范围 depart，orgId 用,分割
	private String imToken;//融云imToken
	public static final Integer IS_ROOT_YES = 1;
	public static final Integer IS_ROOT_NO = 0;

	@Column(name = "rongLianAccount", length = 32)
	public String getRongLianAccount() {
		return rongLianAccount;
	}

	public void setRongLianAccount(String rongLianAccount) {
		this.rongLianAccount = rongLianAccount;
	}

	public String getPermissionScope() {
		return permissionScope;
	}

	public void setPermissionScope(String permissionScope) {
		this.permissionScope = permissionScope;
	}

	@Lob
	@Basic(fetch = FetchType.LAZY)
	@Column(name = "customScope")
	public String getCustomScope() {
		return customScope;
	}

	public void setCustomScope(String customScope) {
		this.customScope = customScope;
	}

	public Integer getIsRoot() {
		return isRoot;
	}

	public void setIsRoot(Integer isRoot) {
		this.isRoot = isRoot;
	}

	public String getStaffId() {
		return staffId;
	}

	public void setStaffId(String staffId) {
		this.staffId = staffId;
	}

	public String getMobilePushMsgId() {
		return mobilePushMsgId;
	}

	public void setMobilePushMsgId(String mobilePushMsgId) {
		this.mobilePushMsgId = mobilePushMsgId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Boolean getBeenSsoLogin() {
		return beenSsoLogin;
	}

	public void setBeenSsoLogin(Boolean beenSsoLogin) {
		this.beenSsoLogin = beenSsoLogin;
	}

	public Date getLastSsoLoginTime() {
		return lastSsoLoginTime;
	}

	public void setLastSsoLoginTime(Date lastSsoLoginTime) {
		this.lastSsoLoginTime = lastSsoLoginTime;
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
