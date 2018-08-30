package com.vortex.cloud.ums.dto.rest;

import java.util.ArrayList;
import java.util.List;

import com.vortex.cloud.ums.model.CloudUser;

/**
 * 为杭州提供的用户信息+用户所有功能号列表
 * 
 * @author XY
 *
 */
public class CloudUserRestDto extends CloudUser {
	private static final long serialVersionUID = 1L;
	private List<UserFunctionDto> functionList = new ArrayList<UserFunctionDto>(); // 功能号列表

	public List<UserFunctionDto> getFunctionList() {
		return functionList;
	}

	public void setFunctionList(List<UserFunctionDto> functionList) {
		this.functionList = functionList;
	}
}
