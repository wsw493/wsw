package com.vortex.cloud.ums.util.tree;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vortex.cloud.vfs.common.lang.StringUtil;

public abstract class CommonTree {
	private static Logger log = LoggerFactory.getLogger(CommonTree.class);

	private Map<String, CommonTreeNode> treeNodeMaps = new Hashtable<String, CommonTreeNode>();

	private CommonTreeNode root;

	/**
	 * root if it's parent is empty
	 */
	protected void reload(List<Object> nodes, Object rootNode) {
		if (log.isInfoEnabled()) {
			log.info("tree will start reload all data");
		}
		boolean hasRoot = false;
		synchronized (this) {
			// initialize
			treeNodeMaps.clear();
			root = null;

			List<CommonTreeNode> treeNodes = new ArrayList<CommonTreeNode>();
			for (int i = 0; i < nodes.size(); i++) {
				CommonTreeNode node = this.transform(nodes.get(i)); // transform
				if (node == null) {
					continue;
				}
				treeNodes.add(node);
				node.setTree(this);
				treeNodeMaps.put(node.getNodeId().trim(), node);
			}
			if (rootNode != null) {
				CommonTreeNode node = this.transform(rootNode);
				node.setType("Root");
				treeNodes.add(node);
				node.setTree(this);
				treeNodeMaps.put(StringUtil.clean(node.getNodeId()), node);

				root = node;
				hasRoot = true;
			}
			for (int i = 0; i < treeNodes.size(); i++) {
				CommonTreeNode node = treeNodes.get(i);
				String parentId = StringUtil.clean(node.getParentId());

				if (hasRoot == false && this.isRootNode(node)) {
					if (root == null) {
						root = node;
					} else {
						log.error("find more then one root node. ignore.");
					}
				} else {
					CommonTreeNode parent = treeNodeMaps.get(parentId);
					if (parent != null) {
						parent.addChild(node);
						node.setParent(parent);
					} else {
						log.warn("node [id=" + node.getNodeId() + " text=" + node.getText() + " parentid=" + node.getParentId() + "]: missing parent node.");
						// bug
						// root.addChild(node);
						// node.setParent(root);
					}
				}
			}
		}

		if (root == null) {
			log.error("the root node is not be defined");
		}
	}

	/**
	 * root if it's parent is empty
	 */
	protected void reloadWidthAllParent(List<Object> nodes, Object rootNode) {
		if (log.isInfoEnabled()) {
			log.info("tree will start reload all data");
		}
		boolean hasRoot = false;
		synchronized (this) {
			// initialize
			treeNodeMaps.clear();
			root = null;

			List<CommonTreeNode> treeNodes = new ArrayList<CommonTreeNode>();
			for (int i = 0; i < nodes.size(); i++) {
				CommonTreeNode node = this.transform(nodes.get(i)); // transform
				if (node == null) {
					continue;
				}
				treeNodes.add(node);
				node.setTree(this);
				treeNodeMaps.put(node.getNodeId().trim(), node);
			}
			if (rootNode != null) {
				CommonTreeNode node = this.transform(rootNode);
				node.setType("Root");
				treeNodes.add(node);
				node.setTree(this);
				treeNodeMaps.put(StringUtil.clean(node.getNodeId()), node);

				root = node;
				hasRoot = true;
			}
			for (int i = 0; i < treeNodes.size(); i++) {
				CommonTreeNode node = treeNodes.get(i);
				String parentId = StringUtil.clean(node.getParentId());

				if (hasRoot == false && this.isRootNode(node)) {
					if (root == null) {
						root = node;
					} else {
						log.error("find more then one root node. ignore.");
					}
				} else {
					CommonTreeNode parent = treeNodeMaps.get(parentId);
					if (parent != null) {
						parent.addChild(node);
						node.setParent(parent);
					} else {
						log.warn("node [id=" + node.getNodeId() + " text=" + node.getText() + " parentid=" + node.getParentId() + "]: missing parent node.");
						// bug
						root.addChild(node);
						node.setParent(root);
					}
				}
			}
		}

		if (root == null) {
			log.error("the root node is not be defined");
		}
	}

	/**
	 * root if it's parent is empty
	 */
	protected void reload(List<Object> nodes, Object rootNode, String rootType) {
		log.info("tree will start reload all data");
		boolean hasRoot = false;
		synchronized (this) {
			// initialize
			treeNodeMaps.clear();
			root = null;

			List<CommonTreeNode> treeNodes = new ArrayList<CommonTreeNode>();
			for (int i = 0; i < nodes.size(); i++) {
				CommonTreeNode node = this.transform(nodes.get(i)); // transform
				if (node == null) {
					continue;
				}
				treeNodes.add(node);
				node.setTree(this);
				treeNodeMaps.put(node.getNodeId().trim(), node);
			}
			if (rootNode != null) {
				CommonTreeNode node = this.transform(rootNode);
				node.setType(rootType == null ? "Root" : rootType);
				treeNodes.add(node);
				node.setTree(this);
				treeNodeMaps.put(node.getNodeId().trim(), node);

				root = node;
				hasRoot = true;
			}
			for (int i = 0; i < treeNodes.size(); i++) {
				CommonTreeNode node = treeNodes.get(i);
				String parentId = StringUtil.clean(node.getParentId());

				if (hasRoot == false && this.isRootNode(node)) {
					if (root == null) {
						root = node;
					} else {
						log.error("find more then one root node. ignore.");
					}
				} else {
					CommonTreeNode parent = treeNodeMaps.get(parentId);
					if (parent != null) {
						parent.addChild(node);
						node.setParent(parent);
					} else {
						log.warn("node [id=" + node.getNodeId() + " text=" + node.getText() + " parentid=" + node.getParentId() + "]: missing parent node.");
					}
				}
			}
		}

		if (root == null) {
			log.error("the root node is not be defined");
		}
	}

	protected boolean isRootNode(CommonTreeNode node) {
		if (node.getParentId() == null || "0".equals(node.getParentId()) || "null".equals(node.getParentId())) {
			return true;
		} else {
			return false;
		}
		// return StringUtils.isBlank(node.getParentId());
	}

	public CommonTreeNode getRootNode() {
		return root;
	}

	public CommonTreeNode getTreeNode(String nodeId) {
		return treeNodeMaps.get(nodeId);
	}

	public void addTreeNode(CommonTreeNode node) {
		synchronized (this) {
			treeNodeMaps.put(node.getNodeId(), node);

			String parentId = node.getParentId();
			if (StringUtils.isNotBlank(parentId)) {
				CommonTreeNode parent = getTreeNode(parentId);
				if (parent != null) {
					parent.addChild(node);
					node.setParent(parent);
				} else {
					log.error("parent cannot be found: " + node.getParentId());
				}
			} else {
				if (root == null) {
					root = node;
				} else {
					log.error("find more then one root node. ignore.");
				}
			}
		}
	}

	public void deleteTreeNode(String nodeId) {
		synchronized (this) {
			CommonTreeNode node = getTreeNode(nodeId);
			if (node == null)
				throw new IllegalArgumentException(nodeId + " cannot be found.");

			if (node.getParent() == null) {
				root = null;
				treeNodeMaps.clear();
				log.warn("the root node has been removed.");
			} else {
				node.getParent().getChildren().remove(node);

				treeNodeMaps.remove(nodeId);
				List<CommonTreeNode> children = node.getAllChildren();
				for (int i = 0; i < children.size(); i++) {
					CommonTreeNode n = children.get(i);
					treeNodeMaps.remove(n.getNodeId());
				}
			}
		}
	}

	protected abstract CommonTreeNode transform(Object info);
}
