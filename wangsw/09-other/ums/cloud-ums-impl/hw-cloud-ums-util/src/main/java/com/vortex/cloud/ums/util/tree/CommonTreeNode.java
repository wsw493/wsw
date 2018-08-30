package com.vortex.cloud.ums.util.tree;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.ObjectUtils;

public class CommonTreeNode {

	private CommonTree tree;
	private CommonTreeNode parent;
	private List<CommonTreeNode> children = new ArrayList<CommonTreeNode>();
	private List<CommonTreeNode> childrenGroup = new ArrayList<CommonTreeNode>();
	private String nodeId;
	private String parentId;
	private Object bindData;
	private String text;
	private boolean isHidden = false;
	private String qtip;// 节点上的提示信息
	private boolean checked = false;// 是否选中
	private String type;// 节点类型
	private String icon;// 节点图标
	private String iconSkin;//节点css

	// 扩展字段，可以临时用于树上特殊的需求 
	private String extendValue;
	
	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getNodeId() {
		return nodeId;
	}

	public void setNodeId(String nodeId) {
		this.nodeId = nodeId;
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public Object getBindData() {
		return bindData;
	}

	public void setBindData(Object bindData) {
		this.bindData = bindData;
	}

	public CommonTree getTree() {
		return tree;
	}

	public void setTree(CommonTree tree) {
		this.tree = tree;
	}

	public void setParent(CommonTreeNode parent) {
		this.parent = parent;
	}

	public CommonTreeNode getParent() {
		return this.parent;
	}

	public List<CommonTreeNode> getChildren() {
		return this.children;
	}

	public void addChild(CommonTreeNode node) {
		children.add(node);
	}

	public boolean isLeaf() {
		return children.size() == 0 ? true : false;
	}

	public String getQtip() {
		if (qtip == null) {
			setQtip(getText());
		}
		return qtip;
	}

	public void setQtip(String qtip) {
		this.qtip = qtip;
	}

	public boolean isChecked() {
		return checked;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public boolean isHidden() {
		return isHidden;
	}

	public void setHidden(boolean isHidden) {
		this.isHidden = isHidden;
	}

	public String getIconSkin() {
		return iconSkin;
	}

	public void setIconSkin(String iconSkin) {
		this.iconSkin = iconSkin;
	}

	public String getExtendValue() {
		return extendValue;
	}

	public void setExtendValue(String extendValue) {
		this.extendValue = extendValue;
	}

	/**
	 * get all children, and chilren's children
	 */
	public List<CommonTreeNode> getAllChildren() {
		if (this.childrenGroup.isEmpty()) {
			synchronized (this.tree) {
				for (int i = 0; i < this.children.size(); i++) {
					CommonTreeNode node = this.children.get(i);
					this.childrenGroup.add(node);
					this.childrenGroup.addAll(node.getAllChildren());
				}
			}
		}
		return this.childrenGroup;
	}

	public List<CommonTreeNode> getAllChildren(CommonTreeNode findnode) {
		List<CommonTreeNode> groups = new ArrayList<CommonTreeNode>();
		fillAllChildren(groups, findnode);
		return groups;
	}

	private void fillAllChildren(List<CommonTreeNode> groups,
			CommonTreeNode findnode) {
		for (int i = 0; i < this.children.size(); i++) {
			CommonTreeNode node = this.children.get(i);
			if (findnode.equals(node)) {
				groups.add(node);
				node.fillAllChildren(groups, findnode);
			}
		}
	}

	/**
	 * get all parents, and parent's parent
	 */
	public List<CommonTreeNode> getParents() {
		List<CommonTreeNode> results = new ArrayList<CommonTreeNode>();
		CommonTreeNode parent = this.getParent();
		while (parent != null) {
			results.add(parent);
			parent = parent.getParent();
		}
		return results;
	}

	/**
	 * A.isMyParent(B) == B is A' parent ? <br>
	 * root.isMyParent(null) == true; <br>
	 * root.isMyParent(*) == false <br>
	 * *.isMyParent(null) == false
	 */
	public boolean isMyParent(String nodeId) {
		CommonTreeNode target = tree.getTreeNode(nodeId);
		CommonTreeNode parent = this.getParent();
		if (parent == null) {
			return target == null;
		} else {
			return parent.equals(target);
		}
	}

	/**
	 * A.isMyAncestor(B) == B is A' ancestor ? <br>
	 * *.isMyAncestor(null) == true;
	 */
	public boolean isMyAncestor(String nodeId) {
		CommonTreeNode target = tree.getTreeNode(nodeId);
		if (target == null)
			return true;

		return target.getAllChildren().contains(this);
	}

	/**
	 * A.isMyBrother(B) == B is A' brother ? <br>
	 * *.isMyBrother(null) == false
	 */
	public boolean isMyBrother(String nodeId) {
		CommonTreeNode target = tree.getTreeNode(nodeId);
		if (target == null)
			return false;

		CommonTreeNode p1 = this.getParent();
		CommonTreeNode p2 = target.getParent();
		return ObjectUtils.equals(p1, p2);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj != null) {
			if (obj instanceof CommonTreeNode) {
				CommonTreeNode node = (CommonTreeNode) obj;
				if (node.getNodeId().equals(this.nodeId)) {
					return true;
				} else {
					return false;
				}
			}
		}
		return false;
	}

}
