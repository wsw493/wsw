package com.vortex.cloud.ums.dto;
/**
 * 用户自定义展示菜单
 * @author ll
 *
 */
public class CloudPersonalMenuDisplayDto {
	
	private String id;
	
	private String menuName;
	
	private String photoIds;
	
	private String uri;
	
	private String orderIndex;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getMenuName() {
		return menuName;
	}

	public void setMenuName(String menuName) {
		this.menuName = menuName;
	}

	public String getPhotoIds() {
		return photoIds;
	}

	public void setPhotoIds(String photoIds) {
		this.photoIds = photoIds;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public String getOrderIndex() {
		return orderIndex;
	}

	public void setOrderIndex(String orderIndex) {
		this.orderIndex = orderIndex;
	}
}
