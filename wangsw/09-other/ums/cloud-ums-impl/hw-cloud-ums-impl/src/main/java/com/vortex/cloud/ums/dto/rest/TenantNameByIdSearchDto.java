package com.vortex.cloud.ums.dto.rest;

import java.util.List;

public class TenantNameByIdSearchDto {
	private List<String> idList;

	public List<String> getIdList() {
		return idList;
	}

	public void setIdList(List<String> idList) {
		this.idList = idList;
	}
}
