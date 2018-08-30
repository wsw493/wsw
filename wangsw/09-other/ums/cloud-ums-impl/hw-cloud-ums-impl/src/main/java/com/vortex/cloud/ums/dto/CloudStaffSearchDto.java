/*   
 * Copyright (C), 2005-2014, 苏州伏泰信息科技有限公司
 */
package com.vortex.cloud.ums.dto;

import java.util.List;

/**
 * @author LiShijun
 * @date 2016年4月6日 下午3:29:45
 * @Description 人员管理：搜索条件 History <author> <time> <desc>
 */
public class CloudStaffSearchDto extends CloudStaffDto {

	private static final long serialVersionUID = 1L;
	private String registerType;// 是否注册用户 "N"：没有注册 ,"Y":注册了
	private List<String> deptIds; // 部门ids
	private String containsTenant;// 是否包含租户管理员 "1":包含 "0"
	public static final String CONTAINS_TENANT_NO = "0";
	public static final String CONTAINS_TENANT_YES = "1";

	private String ageGroupStart; // 年龄段开始
	private String ageGroupEnd; // 年龄段结束

	private String workYearLimitStart; // 工作年限开始
	private String workYearLimitEnd; // 工作年限结束
	List<String> companyIds;// 拥有权限的部门
	List<String> ids;// in 查询 ids
	private List<String> partyPostIds;// 职务ids,以都好分割，杭向明要求根据职务来过滤

	private String ckRange;// ture 查询本级及本级一下，false 本级

	public List<String> getIds() {
		return ids;
	}

	public void setIds(List<String> ids) {
		this.ids = ids;
	}

	public List<String> getPartyPostIds() {
		return partyPostIds;
	}

	public void setPartyPostIds(List<String> partyPostIds) {
		this.partyPostIds = partyPostIds;
	}

	public String getWorkYearLimitStart() {
		return workYearLimitStart;
	}

	public void setWorkYearLimitStart(String workYearLimitStart) {
		this.workYearLimitStart = workYearLimitStart;
	}

	public String getWorkYearLimitEnd() {
		return workYearLimitEnd;
	}

	public void setWorkYearLimitEnd(String workYearLimitEnd) {
		this.workYearLimitEnd = workYearLimitEnd;
	}

	public String getAgeGroupStart() {
		return ageGroupStart;
	}

	public void setAgeGroupStart(String ageGroupStart) {
		this.ageGroupStart = ageGroupStart;
	}

	public String getAgeGroupEnd() {
		return ageGroupEnd;
	}

	public void setAgeGroupEnd(String ageGroupEnd) {
		this.ageGroupEnd = ageGroupEnd;
	}

	public String getRegisterType() {
		return registerType;
	}

	public void setRegisterType(String registerType) {
		this.registerType = registerType;
	}

	public List<String> getDeptIds() {
		return deptIds;
	}

	public void setDeptIds(List<String> deptIds) {
		this.deptIds = deptIds;
	}

	public String getContainsTenant() {
		return containsTenant;
	}

	public void setContainsTenant(String containsTenant) {
		this.containsTenant = containsTenant;
	}

	public List<String> getCompanyIds() {
		return companyIds;
	}

	public void setCompanyIds(List<String> companyIds) {
		this.companyIds = companyIds;
	}

	public String getCkRange() {
		return ckRange;
	}

	public void setCkRange(String ckRange) {
		this.ckRange = ckRange;
	}
}
