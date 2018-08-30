/*   
 * Copyright (C), 2005-2014, 苏州伏泰信息科技有限公司
 */
package com.vortex.cloud.ums.dto;

import com.vortex.cloud.ums.model.CloudStaff;

/**
 * @author LiShijun
 * @date 2016年4月5日 上午10:14:51
 * @Description 人员 History <author> <time> <desc>
 */
public class CloudStaffDto extends CloudStaff {

	private static final long serialVersionUID = 1L;

	private String unitName; // 所属单位
	private String userId; // 登录用户记录ID
	private String userName; // 登录用户名

	private String mobilePushMsgId;// 手机推送id
	private String rongLianAccount; // 容联帐号

	private String workTypeName;
	private String imToken;// 融云账号token
	private String photoId; // 头像ID
	private String companyName;// department name,只存储departmentName！！！
	private String postOrderIndex;// 职位参数的排序号（前端职位排序按照这个排序的）

	/**
	 * 新增，删除，修改标记
	 */
	private Integer flag;
	// 意愿检查区域(行政区划Names)
	private String willCheckDivisionNames;

	public String getPostOrderIndex() {
		return postOrderIndex;
	}

	public void setPostOrderIndex(String postOrderIndex) {
		this.postOrderIndex = postOrderIndex;
	}

	public String getPhotoId() {
		return photoId;
	}

	public void setPhotoId(String photoId) {
		this.photoId = photoId;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getImToken() {
		return imToken;
	}

	public void setImToken(String imToken) {
		this.imToken = imToken;
	}

	public String getRongLianAccount() {
		return rongLianAccount;
	}

	public void setRongLianAccount(String rongLianAccount) {
		this.rongLianAccount = rongLianAccount;
	}

	public String getMobilePushMsgId() {
		return mobilePushMsgId;
	}

	public void setMobilePushMsgId(String mobilePushMsgId) {
		this.mobilePushMsgId = mobilePushMsgId;
	}

	public String getUnitName() {
		return unitName;
	}

	public void setUnitName(String unitName) {
		this.unitName = unitName;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getWorkTypeName() {
		return workTypeName;
	}

	public void setWorkTypeName(String workTypeName) {
		this.workTypeName = workTypeName;
	}

	public Integer getFlag() {
		return flag;
	}

	public void setFlag(Integer flag) {
		this.flag = flag;
	}

	public String getWillCheckDivisionNames() {
		return willCheckDivisionNames;
	}

	public void setWillCheckDivisionNames(String willCheckDivisionNames) {
		this.willCheckDivisionNames = willCheckDivisionNames;
	}

}
