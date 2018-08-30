package com.vortex.cloud.ums.dto;

import com.vortex.cloud.ums.model.CloudMenu;

/**
 * 菜单和功能关联信息
 * 
 * @author XY
 *
 */
public class CloudMenuFunctionDto extends CloudMenu {
	private static final long serialVersionUID = 1L;

	private String uri; // 访问的uri
	private Integer isLeaf; // 是否叶子节点1：是，0：否

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public Integer getIsLeaf() {
		return isLeaf;
	}

	public void setIsLeaf(Integer isLeaf) {
		this.isLeaf = isLeaf;
	}
}
