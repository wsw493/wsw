/*   
 * Copyright (C), 2005-2014, 苏州伏泰信息科技有限公司
 */
package com.vortex.cloud.ums.dto;

import com.vortex.cloud.ums.model.CloudUser;

/**
 * @author LiShijun
 * @date 2016年4月6日 上午11:45:43
 * @Description 用户管理 History <author> <time> <desc>
 */
public class CloudUserDto extends CloudUser {
	private static final long serialVersionUID = 1L;

	private CloudStaffDto staffDto; // 人员记录
	private String staffId;
	private String departmentId; // 所属公司或者环卫处
	private String orgId; // 所属部门
	private String tenantId; // 租户id
	private String orgName; // 所属单位/部门名称
	private String departName; // 所属公司的名称

	// 人员代码 Varchar(64)
	private String code;
	// 姓名 Varchar(20) Not null
	private String staffName;

	// 性别 Char(32) FK 男，女
	private String gender;
	// 民族 Char(32) FK 汉族等 pk ParameterSetting
	private String nationId;
	private String nationName;

	// 生日 Date 可以用于计算年龄
	private String birthday;

	// 身体状况 Char(32) pk ParameterSetting
	private String healthId;
	private String healthName;

	// 证件类型 Char(32) FK 身份证等 pk ParameterSetting
	private String credentialType;
	// 证件号 Varchar(64)
	private String credentialNum;

	// 婚姻状况 Char(32) FK 未婚、已婚、离异、丧偶 pk ParameterSetting
	private String maritalStatusId;
	private String maritalStatusName;
	// 政治面貌 Char(32) FK 党员等 pk ParameterSetting
	private String politicalStatusId;
	private String politicalStatusName;

	// 参加工作时间
	private String joinWorkTime;
	// 工作年限
	private String workYearLimit;

	// 是否离职退休
	private String isLeave;
	// 离职日期
	private String leaveTime;

	// 描述 varchar(4000) 可以用HTML
	private String description;

	// 原籍 Varchar(64)
	private String birthPlace;
	// 现籍 Varchar(64)
	private String presentPlace;
	// 居住地 Varchar(64)
	private String livePlace;

	// 手机
	private String phone;
	// 办公室电话 **/
	private String officeTel;
	// 邮箱 **/
	private String email;
	// 内部邮件 **/
	private String innerEmail;

	// 毕业学校
	private String graduate;
	// 学历
	private String educationId;
	private String educationName;

	// 人员编制性质
	private String authorizeId;
	private String authorizeName;
	// 职位 varchar(32)
	private String postId;
	private String postName;
	// 职务
	private String partyPostId;
	private String partyPostName;

	// 进入本单位时间
	private String entryHereTime;
	// ID卡
	private String idCard;

	// 社保号
	private String socialSecurityNo;
	// 社保缴纳情况
	private String socialSecuritycase;

	public CloudStaffDto getStaffDto() {
		return staffDto;
	}

	public void setStaffDto(CloudStaffDto staffDto) {
		this.staffDto = staffDto;
	}

	@Override
	public String getStaffId() {
		return staffId;
	}

	@Override
	public void setStaffId(String staffId) {
		this.staffId = staffId;
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

	public String getTenantId() {
		return tenantId;
	}

	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}

	public String getOrgName() {
		return orgName;
	}

	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getStaffName() {
		return staffName;
	}

	public void setStaffName(String staffName) {
		this.staffName = staffName;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getNationId() {
		return nationId;
	}

	public void setNationId(String nationId) {
		this.nationId = nationId;
	}

	public String getNationName() {
		return nationName;
	}

	public void setNationName(String nationName) {
		this.nationName = nationName;
	}

	public String getBirthday() {
		return birthday;
	}

	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}

	public String getHealthId() {
		return healthId;
	}

	public void setHealthId(String healthId) {
		this.healthId = healthId;
	}

	public String getHealthName() {
		return healthName;
	}

	public void setHealthName(String healthName) {
		this.healthName = healthName;
	}

	public String getCredentialType() {
		return credentialType;
	}

	public void setCredentialType(String credentialType) {
		this.credentialType = credentialType;
	}

	public String getCredentialNum() {
		return credentialNum;
	}

	public void setCredentialNum(String credentialNum) {
		this.credentialNum = credentialNum;
	}

	public String getMaritalStatusId() {
		return maritalStatusId;
	}

	public void setMaritalStatusId(String maritalStatusId) {
		this.maritalStatusId = maritalStatusId;
	}

	public String getMaritalStatusName() {
		return maritalStatusName;
	}

	public void setMaritalStatusName(String maritalStatusName) {
		this.maritalStatusName = maritalStatusName;
	}

	public String getPoliticalStatusId() {
		return politicalStatusId;
	}

	public void setPoliticalStatusId(String politicalStatusId) {
		this.politicalStatusId = politicalStatusId;
	}

	public String getPoliticalStatusName() {
		return politicalStatusName;
	}

	public void setPoliticalStatusName(String politicalStatusName) {
		this.politicalStatusName = politicalStatusName;
	}

	public String getJoinWorkTime() {
		return joinWorkTime;
	}

	public void setJoinWorkTime(String joinWorkTime) {
		this.joinWorkTime = joinWorkTime;
	}

	public String getWorkYearLimit() {
		return workYearLimit;
	}

	public void setWorkYearLimit(String workYearLimit) {
		this.workYearLimit = workYearLimit;
	}

	public String getIsLeave() {
		return isLeave;
	}

	public void setIsLeave(String isLeave) {
		this.isLeave = isLeave;
	}

	public String getLeaveTime() {
		return leaveTime;
	}

	public void setLeaveTime(String leaveTime) {
		this.leaveTime = leaveTime;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getBirthPlace() {
		return birthPlace;
	}

	public void setBirthPlace(String birthPlace) {
		this.birthPlace = birthPlace;
	}

	public String getPresentPlace() {
		return presentPlace;
	}

	public void setPresentPlace(String presentPlace) {
		this.presentPlace = presentPlace;
	}

	public String getLivePlace() {
		return livePlace;
	}

	public void setLivePlace(String livePlace) {
		this.livePlace = livePlace;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getOfficeTel() {
		return officeTel;
	}

	public void setOfficeTel(String officeTel) {
		this.officeTel = officeTel;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getInnerEmail() {
		return innerEmail;
	}

	public void setInnerEmail(String innerEmail) {
		this.innerEmail = innerEmail;
	}

	public String getGraduate() {
		return graduate;
	}

	public void setGraduate(String graduate) {
		this.graduate = graduate;
	}

	public String getEducationId() {
		return educationId;
	}

	public void setEducationId(String educationId) {
		this.educationId = educationId;
	}

	public String getEducationName() {
		return educationName;
	}

	public void setEducationName(String educationName) {
		this.educationName = educationName;
	}

	public String getAuthorizeId() {
		return authorizeId;
	}

	public void setAuthorizeId(String authorizeId) {
		this.authorizeId = authorizeId;
	}

	public String getAuthorizeName() {
		return authorizeName;
	}

	public void setAuthorizeName(String authorizeName) {
		this.authorizeName = authorizeName;
	}

	public String getPostId() {
		return postId;
	}

	public void setPostId(String postId) {
		this.postId = postId;
	}

	public String getPostName() {
		return postName;
	}

	public void setPostName(String postName) {
		this.postName = postName;
	}

	public String getPartyPostId() {
		return partyPostId;
	}

	public void setPartyPostId(String partyPostId) {
		this.partyPostId = partyPostId;
	}

	public String getPartyPostName() {
		return partyPostName;
	}

	public void setPartyPostName(String partyPostName) {
		this.partyPostName = partyPostName;
	}

	public String getEntryHereTime() {
		return entryHereTime;
	}

	public void setEntryHereTime(String entryHereTime) {
		this.entryHereTime = entryHereTime;
	}

	public String getIdCard() {
		return idCard;
	}

	public void setIdCard(String idCard) {
		this.idCard = idCard;
	}

	public String getSocialSecurityNo() {
		return socialSecurityNo;
	}

	public void setSocialSecurityNo(String socialSecurityNo) {
		this.socialSecurityNo = socialSecurityNo;
	}

	public String getSocialSecuritycase() {
		return socialSecuritycase;
	}

	public void setSocialSecuritycase(String socialSecuritycase) {
		this.socialSecuritycase = socialSecuritycase;
	}

	public String getDepartName() {
		return departName;
	}

	public void setDepartName(String departName) {
		this.departName = departName;
	}

}
