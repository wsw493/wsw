/*   
 * Copyright (C), 2005-2014, 苏州伏泰信息科技有限公司
 */
package com.vortex.cloud.ums.dto;


/**
 * @author LiShijun
 * @date 2016年5月12日 下午3:36:01
 * @Description 封装业务系统访问management云系统的用户管理模块时的请求参数
 * History
 * <author>      <time>           <desc> 
 */
public class CloudManagementUserReqParamDto extends ManagementReqParamDto {
	private String departmentCode;			// 部门code
	private String departmentId;			// 部门ID
	
	public String getDepartmentCode() {
		return departmentCode;
	}

	public void setDepartmentCode(String departmentCode) {
		this.departmentCode = departmentCode;
	}

	public String getDepartmentId() {
		return departmentId;
	}

	public void setDepartmentId(String departmentId) {
		this.departmentId = departmentId;
	}
}
