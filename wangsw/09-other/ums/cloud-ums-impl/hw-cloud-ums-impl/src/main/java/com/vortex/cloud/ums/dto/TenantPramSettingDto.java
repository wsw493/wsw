/*   
 * Copyright (C), 2005-2014, 苏州伏泰信息科技有限公司
 */
package com.vortex.cloud.ums.dto;

import com.vortex.cloud.ums.model.TenantPramSetting;

/**
 * @author LiShijun
 * @date 2016年4月1日 上午10:15:02
 * @Description 
 * History
 * <author>      <time>           <desc> 
 */
public class TenantPramSettingDto extends TenantPramSetting {
	
	private static final long serialVersionUID = 1L;
	
	private String typeCode;

	public String getTypeCode() {
		return typeCode;
	}

	public void setTypeCode(String typeCode) {
		this.typeCode = typeCode;
	}
}
