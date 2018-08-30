package com.vortex.cloud.ums.dto;

/**
 * 业务系统访问云服务的权限的dto
 * 
 * @author XY
 *
 */
public class PermissionValidateDto {
	private String cloudSystemId; // 云系统id
	private String userId; // 业务系统用户id
	private String resultMsg; // 反馈的结果信息
	private boolean result; // 结果标志
	private String validateURL; // 回访的URL
	private String permission; // 回访成功后返回的权限等级，暂定read和edit两种，分别单表只读和完全的权限

	public String getCloudSystemId() {
		return cloudSystemId;
	}

	public void setCloudSystemId(String cloudSystemId) {
		this.cloudSystemId = cloudSystemId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getResultMsg() {
		return resultMsg;
	}

	public void setResultMsg(String resultMsg) {
		this.resultMsg = resultMsg;
	}

	public boolean isResult() {
		return result;
	}

	public void setResult(boolean result) {
		this.result = result;
	}

	public String getValidateURL() {
		return validateURL;
	}

	public void setValidateURL(String validateURL) {
		this.validateURL = validateURL;
	}

	public String getPermission() {
		return permission;
	}

	public void setPermission(String permission) {
		this.permission = permission;
	}
}
