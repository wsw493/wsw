package com.vortex.cloud.ums.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.vortex.cloud.vfs.data.model.BakDeleteModel;



/**
 * 业务系统菜单，一个菜单只对应一个功能号
 * 
 * @author XY
 *
 */
@Entity
@Table(name = "cloud_menu")
public class CloudMenu extends BakDeleteModel {
	private static final long serialVersionUID = 1L;
	private String systemId; // 云系统id
	private String code; // 编码
	private String name; // 名称
	private Integer orderIndex; // 排序号
	private String description; // 描述
	private String parentId; // 父节点id
	private String photoIds; // json格式的字符串
	private Integer isHidden = 0; // 是否隐藏，默认0显示，1隐藏
	private String functionId; // 绑定的功能码
	private Integer isControlled = 1; // 是否受控制，默认1-受控，0-不受控；不受控的菜单，所有人都可以访问
	private Integer isWelcomeMenu = 0; // 是否欢迎页面，默认0-否，1-是
	// 内置编号：用于层级数据结构的构造（如树）
	private String nodeCode;

	// 子层所有数据记录数，和己编号配置生成子编号
	private Integer childSerialNumer;

	public static final Integer HIDDEN_YES = 1;
	public static final Integer HIDDEN_NOT = 0;

	public static final Integer CONTROLLED_YES = 1;
	public static final Integer CONTROLLED_NOT = 0;

	public static final Integer IS_WELCOME_MENU_YES = 1;
	public static final Integer IS_WELCOME_MENU_NOT = 0;

	public Integer getIsWelcomeMenu() {
		return isWelcomeMenu;
	}

	public void setIsWelcomeMenu(Integer isWelcomeMenu) {
		this.isWelcomeMenu = isWelcomeMenu;
	}

	public String getSystemId() {
		return systemId;
	}

	public void setSystemId(String systemId) {
		this.systemId = systemId;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getOrderIndex() {
		return orderIndex;
	}

	public void setOrderIndex(Integer orderIndex) {
		this.orderIndex = orderIndex;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public String getPhotoIds() {
		return photoIds;
	}

	public void setPhotoIds(String photoIds) {
		this.photoIds = photoIds;
	}

	public Integer getIsHidden() {
		return isHidden;
	}

	public void setIsHidden(Integer isHidden) {
		this.isHidden = isHidden;
	}

	public String getFunctionId() {
		return functionId;
	}

	public void setFunctionId(String functionId) {
		this.functionId = functionId;
	}

	public Integer getIsControlled() {
		return isControlled;
	}

	public void setIsControlled(Integer isControlled) {
		this.isControlled = isControlled;
	}

	public String getNodeCode() {
		return nodeCode;
	}

	public void setNodeCode(String nodeCode) {
		this.nodeCode = nodeCode;
	}

	// 由于一开始少一个b将childSerialNumber写为childSerialNumer，后期各个数据库都已经引用
	@Column(name="childSerialNumer")
	public Integer getChildSerialNumer() {
		return childSerialNumer;
	}

	public void setChildSerialNumer(Integer childSerialNumer) {
		this.childSerialNumer = childSerialNumer;
	}
}
