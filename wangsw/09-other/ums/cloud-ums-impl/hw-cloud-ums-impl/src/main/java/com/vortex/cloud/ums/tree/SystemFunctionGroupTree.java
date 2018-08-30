package com.vortex.cloud.ums.tree;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import com.vortex.cloud.ums.config.SpringContextHolder;
import com.vortex.cloud.ums.dataaccess.service.ICloudFunctionService;
import com.vortex.cloud.ums.dto.CloudTreeDto;
import com.vortex.cloud.ums.util.tree.CommonTree;
import com.vortex.cloud.ums.util.tree.CommonTreeNode;
import com.vortex.cloud.ums.util.utils.ObjectUtil;
import com.vortex.cloud.vfs.common.lang.StringUtil;



public class SystemFunctionGroupTree extends CommonTree {
	
	private static SystemFunctionGroupTree instance;
	
	private SystemFunctionGroupTree() {}
	
	public static SystemFunctionGroupTree getInstance() {
		synchronized (SystemFunctionGroupTree.class) {
			if (instance == null) {
				instance = new SystemFunctionGroupTree();
			}
		}
		return instance;
	}

	@Override
	protected CommonTreeNode transform(Object info) {
		CommonTreeNode node = new CommonTreeNode();
		if (info instanceof CloudTreeDto) {
			CloudTreeDto dd = (CloudTreeDto) info;
			node.setNodeId(StringUtil.clean(dd.getId()));
			node.setParentId(StringUtil.clean(dd.getParentId()));
			node.setText(dd.getName());
			node.setType(dd.getType());
			node.setQtip(node.getText());
			node.setBindData(ObjectUtil.attributesToMap(dd));
		} else if (info instanceof CommonTreeNode) {
			node = (CommonTreeNode) info;
		}
		return node;
	}
	
	public void reloadSystemFunctionTree(String userId) {
		List<Object> nodes = new ArrayList<Object>();
		try {
			// 添加根
			nodes.add(generateRoot());
			List<CloudTreeDto> treeDto = this.getTreeData(userId);
			
			if (CollectionUtils.isNotEmpty(treeDto)) {
				nodes.addAll(treeDto);
			}
			super.reload(nodes, null);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	/**
	 * 获取树数据
	 * @param userId
	 * @return
	 */
	private List<CloudTreeDto> getTreeData(String userId) {
		ICloudFunctionService functionService = this.getFunctionService();
		return functionService.getTreeData(userId);
	}
	
	private CommonTreeNode generateRoot() {
		CommonTreeNode root = new CommonTreeNode();
		root.setNodeId("-1");
		root.setText("系统功能树");
		root.setParentId("0");
		root.setType("Root");
		return root;
	}
	
	private ICloudFunctionService getFunctionService() {
		return SpringContextHolder.getBean("cloudFunctionService");
	}

}
