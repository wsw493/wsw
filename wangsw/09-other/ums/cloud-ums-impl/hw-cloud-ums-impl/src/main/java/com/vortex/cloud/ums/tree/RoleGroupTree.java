package com.vortex.cloud.ums.tree;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;

import com.vortex.cloud.ums.config.SpringContextHolder;
import com.vortex.cloud.ums.dataaccess.service.ICloudRoleGroupService;
import com.vortex.cloud.ums.dataaccess.service.ICloudRoleService;
import com.vortex.cloud.ums.model.CloudRole;
import com.vortex.cloud.ums.model.CloudRoleGroup;
import com.vortex.cloud.ums.util.tree.CommonTree;
import com.vortex.cloud.ums.util.tree.CommonTreeNode;
import com.vortex.cloud.ums.util.utils.ObjectUtil;
import com.vortex.cloud.vfs.common.lang.StringUtil;

/**
 * 角色组树
 * 
 * @author lsm
 * @date 2016年4月5日
 */
public class RoleGroupTree extends CommonTree {
	private static RoleGroupTree instance;

	private RoleGroupTree() {
	}

	public static RoleGroupTree getInstance() {
		synchronized (RoleGroupTree.class) {
			if (null == instance) {
				instance = new RoleGroupTree();
			}
		}
		return instance;
	}

	@Override
	protected CommonTreeNode transform(Object info) {

		CommonTreeNode node = new CommonTreeNode();
		if (info instanceof CloudRoleGroup) {
			CloudRoleGroup dd = (CloudRoleGroup) info;
			node.setNodeId(StringUtil.clean(dd.getId()));
			node.setParentId(StringUtil.clean(dd.getParentId()));
			node.setText(dd.getName());
			node.setType("RoleGroup");
			// node.setQtip(node.getType() + node.getText());
			node.setQtip(node.getText());
			node.setBindData(ObjectUtil.attributesToMap(dd));
		} else if (info instanceof CommonTreeNode) {
			node = (CommonTreeNode) info;
		} else if (info instanceof CloudRole) {
			CloudRole dd = (CloudRole) info;
			node.setNodeId(StringUtil.clean(dd.getId()));
			node.setParentId(StringUtil.clean(dd.getGroupId()));
			node.setText(dd.getName());
			node.setType("Role");
			// node.setQtip(node.getType() + node.getText());
			node.setQtip(node.getText());
			node.setBindData(ObjectUtil.attributesToMap(dd));
		}
		return node;
	}

	private CommonTreeNode generateRoot() {
		CommonTreeNode root = new CommonTreeNode();
		root.setNodeId("-1");
		root.setText("角色组");
		root.setParentId("0");
		root.setType("Root");
		return root;
	}

	public void reloadRoleGroupTree(Map<String, Object> map) {
		List<Object> nodes = new ArrayList<Object>();
		try {
			// 添加根
			nodes.add(generateRoot());
			List<CloudRoleGroup> actionGroups = findAllRoleGroup(map);
			nodes.addAll(actionGroups);
			super.reload(nodes, null);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void reloadRoleTree(Map<String, Object> map) {
		List<Object> nodes = new ArrayList<Object>();
		try {
			// 添加根
			nodes.add(generateRoot());
			List<CloudRoleGroup> actionGroups = findAllRoleGroup(map);
			List<CloudRole> actions = findAllRole(map);
			nodes.addAll(actionGroups);
			nodes.addAll(actions);
			super.reload(nodes, null);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private List<CloudRoleGroup> findAllRoleGroup(Map<String, Object> map) {
		ICloudRoleGroupService actionGroupService = this.getRoleGroupService();
		return actionGroupService.findListByProperty(map, new Sort(Direction.ASC, "orderIndex"));

	}

	private List<CloudRole> findAllRole(Map<String, Object> map) {
		ICloudRoleService actionService = this.getRoleService();
		List<Order> orders = new ArrayList<Order>();
		Order order1 = new Order(Direction.ASC, "orderIndex");
		Order order2 = new Order(Direction.ASC, "name");
		orders.add(order1);
		orders.add(order2);
		return actionService.findListByProperty(map, new Sort(orders));

	}

	private ICloudRoleGroupService getRoleGroupService() {
		return SpringContextHolder.getBean("cloudRoleGroupService");
	}

	private ICloudRoleService getRoleService() {
		return SpringContextHolder.getBean("cloudRoleService");
	}

}
