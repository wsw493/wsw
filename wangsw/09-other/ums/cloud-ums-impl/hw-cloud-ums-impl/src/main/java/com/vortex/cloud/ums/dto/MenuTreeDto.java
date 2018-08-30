package com.vortex.cloud.ums.dto;

import java.io.Serializable;
import java.util.List;

import com.vortex.cloud.ums.model.CloudMenu;

/**
 * 框架菜单的树形数据结构
 * 
 * @author XY
 *
 */
@SuppressWarnings("rawtypes")
public class MenuTreeDto implements Serializable, Cloneable, Comparable {
	private String name; // 名称
	private String description; // 描述
	private String id; // 菜单id
	private String code; // 菜单编码
	private String uri; // 菜单uri
	private String parentId; // 父节点id
	private String photoIds; // json格式的字符串
	private Integer level; // 树形层级，根节点为0
	private Integer isLeaf; // 是否叶子节点1：是，0：否
	private List<MenuTreeDto> children; // 子节点列表
	private Integer orderIndex; // 排序号
	private Integer isWelcomeMenu; // 是否欢迎页
	private String functionId; // 功能id
	private Integer isControlled; // 是否受控制，默认1-受控，0-不受控；不受控的菜单，所有人都可以访问

	public Integer getIsWelcomeMenu() {
		return isWelcomeMenu;
	}

	public void setIsWelcomeMenu(Integer isWelcomeMenu) {
		this.isWelcomeMenu = isWelcomeMenu;
	}

	public Integer getOrderIndex() {
		return orderIndex;
	}

	public void setOrderIndex(Integer orderIndex) {
		this.orderIndex = orderIndex;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
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

	public Integer getLevel() {
		return level;
	}

	public void setLevel(Integer level) {
		this.level = level;
	}

	public Integer getIsLeaf() {
		return isLeaf;
	}

	public void setIsLeaf(Integer isLeaf) {
		this.isLeaf = isLeaf;
	}

	public List<MenuTreeDto> getChildren() {
		return children;
	}

	public void setChildren(List<MenuTreeDto> children) {
		this.children = children;
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

	@Override
	public int compareTo(Object o) {
		if (o == null) {
			return -1;
		}
		if (o instanceof MenuTreeDto) {
			MenuTreeDto obj = (MenuTreeDto) o;
			if (this.getOrderIndex() == null) {
				return -1;
			}

			int v = this.getOrderIndex() - obj.getOrderIndex();
			if (v < 0) {
				return 1;
			} else if (v > 0) {
				return -1;
			} else {
				return 0;
			}
		}
		return -1;
	}

	/**
	 * 转化
	 * 
	 * @param funcDef
	 */
	public MenuTreeDto transfer(CloudMenu entity) {
		// 名称
		this.setName(entity.getName());
		// 描述
		this.setDescription(entity.getDescription());
		// 菜单id
		this.setId(entity.getId());
		// 菜单编码
		this.setCode(entity.getCode());
		// 父节点id
		this.setParentId(entity.getParentId());
		// json格式的字符串
		this.setPhotoIds(entity.getPhotoIds());
		// 排序号
		this.setOrderIndex(entity.getOrderIndex());
		// 是否欢迎页
		this.setIsWelcomeMenu(entity.getIsWelcomeMenu());
		// 是否受控制，默认1-受控，0-不受控；不受控的菜单，所有人都可以访问
		this.setIsControlled(entity.getIsControlled());
		this.setUri("");
		// 功能id
		this.setFunctionId(entity.getFunctionId());
		return this;
	}
}
