package com.vortex.cloud.ums.dto;

/**
 * @author LiShijun
 * @date 2016年4月15日 上午9:18:00
 * @Description 封装业务系统访问management云系统时的请求参数。
 * History 
 * <author> <time> <desc>
 */
public class ManagementReqParamDto extends LoginReturnInfoDto {
	private String systemCode;
	private String systemId;

	public String getSystemCode() {
		return systemCode;
	}

	public void setSystemCode(String systemCode) {
		this.systemCode = systemCode;
	}

	public String getSystemId() {
		return systemId;
	}

	public void setSystemId(String systemId) {
		this.systemId = systemId;
	}
}
