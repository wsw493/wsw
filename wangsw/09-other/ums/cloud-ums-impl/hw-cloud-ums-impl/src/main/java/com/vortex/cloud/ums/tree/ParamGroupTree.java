package com.vortex.cloud.ums.tree;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

import com.vortex.cloud.ums.config.SpringContextHolder;
import com.vortex.cloud.ums.dataaccess.service.IParamGroupService;
import com.vortex.cloud.ums.model.PramGroup;
import com.vortex.cloud.ums.util.tree.CommonTree;
import com.vortex.cloud.ums.util.tree.CommonTreeNode;
import com.vortex.cloud.ums.util.utils.ObjectUtil;
import com.vortex.cloud.vfs.common.lang.StringUtil;
import com.vortex.cloud.vfs.data.support.SearchFilter;

public class ParamGroupTree extends CommonTree {
	private static ParamGroupTree instance;

	private ParamGroupTree() {
	}

	public static ParamGroupTree getInstance() {
		synchronized (ParamGroupTree.class) {
			if (null == instance) {
				instance = new ParamGroupTree();
			}
		}
		return instance;
	}

	@Override
	protected CommonTreeNode transform(Object obj) {
		CommonTreeNode node = new CommonTreeNode();
		if (obj instanceof PramGroup) {
			PramGroup dd = (PramGroup) obj;
			node.setNodeId(StringUtil.clean(dd.getId()));
			node.setParentId(StringUtil.clean(dd.getParentId()));
			node.setText(dd.getGroupName());
			node.setType("PramGroup");
			// node.setQtip(node.getType() + node.getText());
			node.setQtip(node.getText());
			node.setBindData(ObjectUtil.attributesToMap(dd));
		} else if (obj instanceof CommonTreeNode) {
			node = (CommonTreeNode) obj;
		}
		return node;
	}

	public void reloadParameterTypeTree(List<SearchFilter> filterList) {
		List<Object> nodes = new ArrayList<Object>();
		try {
			// 添加根
			nodes.add(this.generateRoot());
			List<PramGroup> parameterTypes = this.findAllParameterTypeGroup(filterList);
			nodes.addAll(parameterTypes);
			super.reload(nodes, null);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private CommonTreeNode generateRoot() {
		CommonTreeNode root = new CommonTreeNode();
		root.setNodeId("-1");
		root.setText("参数类型组");
		root.setParentId("0");
		root.setType("Root");
		return root;
	}

	private List<PramGroup> findAllParameterTypeGroup(List<SearchFilter> filterList) {
		IParamGroupService groupService = this.getParameterTypeGroupService();

		Sort sort = new Sort(Direction.ASC, "orderIndex");
		return groupService.findListByFilter(filterList, sort);
	}

	private IParamGroupService getParameterTypeGroupService() {
		return SpringContextHolder.getBean("paramGroupService");
	}
}
