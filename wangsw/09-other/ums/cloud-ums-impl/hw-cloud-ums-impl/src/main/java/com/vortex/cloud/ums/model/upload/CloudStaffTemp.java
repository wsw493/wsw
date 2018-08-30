package com.vortex.cloud.ums.model.upload;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.List;

import javax.persistence.Transient;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.util.StringUtils;

import com.vortex.cloud.ums.dto.excelutil.ExcelCellDTO;
import com.vortex.cloud.ums.dto.excelutil.ExcelRowDTO;

/**
 * 人员 导入model
 * 
 * @author lusm
 *
 */
@SuppressWarnings("serial")
public class CloudStaffTemp extends UploadTempModel {
	private String orgName; // 所属单位/部门名称
	// 人员代码 Varchar(64)
	private String code;
	// 姓名 Varchar(20) Not null
	private String name;
	// 性别 Char(32) FK 男，女
	private String gender;

	// 生日 Date 可以用于计算年龄
	private String birthday;

	// 证件号 Varchar(64)
	private String credentialNum;

	// 民族 Char(32) FK 汉族等 pk ParameterSetting
	private String nationName;
	// 婚姻状况 Char(32) FK 未婚、已婚、离异、丧偶 pk ParameterSetting
	private String maritalStatusName;
	// 政治面貌 Char(32) FK 党员等 pk ParameterSetting
	private String politicalStatusName;

	// 参加工作时间
	private String joinWorkTime;
	// 工作年限
	private String workYearLimit;

	// 是否离职退休
	private String isLeave;
	// 离职日期
	private String leaveTime;

	/**
	 * 用工类型,WorkTypeEnum 管理=巡检人员，作业=保洁人员
	 */
	private String workTypeName;

	// 排序号
	private String orderIndex;

	// 描述 varchar(4000) 可以用HTML
	private String description;

	/*********** 居住情况 ***********/
	// 原籍 Varchar(64)
	private String birthPlace;
	// 现籍 Varchar(64)
	private String presentPlace;
	// 居住地 Varchar(64)
	private String livePlace;

	/*********** 联系方式 ***********/
	// 手机
	private String phone;
	// 办公室电话 **/
	private String officeTel;
	// 邮箱 **/
	private String email;
	// 内部邮件 **/
	private String innerEmail;
	/*********** 教育背景 ***********/

	// 毕业学校
	private String graduate;
	// 学历
	private String educationName;

	/*********** 用工情况 ***********/

	// 人员编制性质
	private String authorizeName;
	// 职位 varchar(32)
	private String postName;
	// 职务
	private String partyPostName;
	// 进入本单位时间
	private String entryHereTime;
	// ID卡
	private String idCard;
	// 社保号
	private String socialSecurityNo;
	// 社保缴纳情况
	private String socialSecuritycase;

	/**
	 * 是否外包 默认否
	 * 
	 */
	private String  outSourcing ;
	/**
	 * 外包单位 text，是外包的时候开放填写
	 */
	private String outSourcingComp;

	

	public String getOutSourcing() {
		return outSourcing;
	}

	public void setOutSourcing(String outSourcing) {
		this.outSourcing = outSourcing;
	}

	public String getOutSourcingComp() {
		return outSourcingComp;
	}

	public void setOutSourcingComp(String outSourcingComp) {
		this.outSourcingComp = outSourcingComp;
	}

	@Transient
	@Override
	public UploadTempModel storeCell(ExcelRowDTO rowDTO) throws Exception {
		List<ExcelCellDTO> cellList = rowDTO.getCellList();
		if (CollectionUtils.isNotEmpty(cellList)) {
			// 遍历单元格cell的结果，封装成Temp对象
			for (ExcelCellDTO cellDTO : cellList) {
				// 保存数据到Temp
				this.setByIndex(cellDTO.getCellIndex(), cellDTO.getValue());
			}
		}
		return this;
	}

	@Transient
	@Override
	public void setByIndex(int index, String content) throws Exception {
		// 和Excel中列字段顺序对应
		String[] indexFeildName = new String[] { "serialNum", "orgName", "code", "name", "gender", "birthday", "credentialNum", "nationName", "maritalStatusName", "politicalStatusName", "joinWorkTime", "workYearLimit", "isLeave", "leaveTime", "workTypeName", "orderIndex", "description", "birthPlace", "presentPlace", "livePlace", "phone", "officeTel", "email", "innerEmail", "graduate", "educationName", "authorizeName", "postName", "partyPostName", "entryHereTime", "idCard", "socialSecurityNo", "socialSecuritycase", "outSourcing", "outSourcingComp" };
		if (index < indexFeildName.length) {
			PropertyDescriptor pd = new PropertyDescriptor(indexFeildName[index], this.getClass());
			// 获得set方法
			Method method = pd.getWriteMethod();
			method.setAccessible(true);
			method.invoke(this, StringUtils.trimWhitespace(content));
		}
	}

	public String getOrgName() {
		return orgName;
	}

	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}

	public String getCredentialNum() {
		return credentialNum;
	}

	public void setCredentialNum(String credentialNum) {
		this.credentialNum = credentialNum;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getBirthday() {
		return birthday;
	}

	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}

	public String getNationName() {
		return nationName;
	}

	public void setNationName(String nationName) {
		this.nationName = nationName;
	}

	public String getMaritalStatusName() {
		return maritalStatusName;
	}

	public void setMaritalStatusName(String maritalStatusName) {
		this.maritalStatusName = maritalStatusName;
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

	public String getWorkTypeName() {
		return workTypeName;
	}

	public void setWorkTypeName(String workTypeName) {
		this.workTypeName = workTypeName;
	}

	public String getOrderIndex() {
		return orderIndex;
	}

	public void setOrderIndex(String orderIndex) {
		this.orderIndex = orderIndex;
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

	public String getEducationName() {
		return educationName;
	}

	public void setEducationName(String educationName) {
		this.educationName = educationName;
	}

	public String getAuthorizeName() {
		return authorizeName;
	}

	public void setAuthorizeName(String authorizeName) {
		this.authorizeName = authorizeName;
	}

	public String getPostName() {
		return postName;
	}

	public void setPostName(String postName) {
		this.postName = postName;
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

}
