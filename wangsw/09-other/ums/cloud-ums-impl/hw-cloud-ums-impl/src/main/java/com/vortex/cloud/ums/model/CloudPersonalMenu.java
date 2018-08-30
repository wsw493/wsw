package com.vortex.cloud.ums.model;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.vortex.cloud.vfs.data.model.BakDeleteModel;


@SuppressWarnings("serial")
@Entity
@Table(name = "cloud_personal_menu")
public class CloudPersonalMenu extends BakDeleteModel{
	private String userId; // 用户id
	private String menuId; // 菜单id
	private Integer orderIndex; // 排序号
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getMenuId() {
		return menuId;
	}
	public void setMenuId(String menuId) {
		this.menuId = menuId;
	}
	public Integer getOrderIndex() {
		return orderIndex;
	}
	public void setOrderIndex(Integer orderIndex) {
		this.orderIndex = orderIndex;
	}
}
