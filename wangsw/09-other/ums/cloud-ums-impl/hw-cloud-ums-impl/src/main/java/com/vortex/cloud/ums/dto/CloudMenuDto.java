/*   
 * Copyright (C), 2005-2014, 苏州伏泰信息科技有限公司
 */
package com.vortex.cloud.ums.dto;

import com.vortex.cloud.ums.model.CloudMenu;

/**
 * @author LiShijun
 * @date 2016年5月23日 上午10:11:53
 * @Description 菜单管理
 * History
 * <author>      <time>           <desc> 
 */
public class CloudMenuDto extends CloudMenu {

	private static final long serialVersionUID = 1L;

	private String parentName;	// 上级菜单Name
	private String isHiddenText;
	private String isControlledText;
	private String functionName;
	private boolean leafNode;	// 是否为叶子节点
	
	public String getParentName() {
		return parentName;
	}

	public void setParentName(String parentName) {
		this.parentName = parentName;
	}

	public String getIsHiddenText() {
		return isHiddenText;
	}

	public void setIsHiddenText(String isHiddenText) {
		this.isHiddenText = isHiddenText;
	}

	public String getIsControlledText() {
		return isControlledText;
	}

	public void setIsControlledText(String isControlledText) {
		this.isControlledText = isControlledText;
	}

	public String getFunctionName() {
		return functionName;
	}

	public void setFunctionName(String functionName) {
		this.functionName = functionName;
	}

	public boolean isLeafNode() {
		return leafNode;
	}

	public void setLeafNode(boolean leafNode) {
		this.leafNode = leafNode;
	}
}
