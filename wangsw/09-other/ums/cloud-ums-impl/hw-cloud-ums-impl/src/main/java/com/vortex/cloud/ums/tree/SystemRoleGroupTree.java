package com.vortex.cloud.ums.tree;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;

import com.google.common.collect.Lists;
import com.vortex.cloud.ums.config.SpringContextHolder;
import com.vortex.cloud.ums.dataaccess.service.ICloudRoleGroupService;
import com.vortex.cloud.ums.dataaccess.service.ICloudRoleService;
import com.vortex.cloud.ums.dataaccess.service.ICloudSystemService;
import com.vortex.cloud.ums.model.CloudRole;
import com.vortex.cloud.ums.model.CloudRoleGroup;
import com.vortex.cloud.ums.model.CloudSystem;
import com.vortex.cloud.ums.util.tree.CommonTree;
import com.vortex.cloud.ums.util.tree.CommonTreeNode;
import com.vortex.cloud.ums.util.utils.ObjectUtil;
import com.vortex.cloud.vfs.common.lang.StringUtil;
import com.vortex.cloud.vfs.data.support.SearchFilter;
import com.vortex.cloud.vfs.data.support.SearchFilter.Operator;



/**
 * 系统角色组树
 * 
 * @author lsm
 * @date 2016年4月5日
 */
public class SystemRoleGroupTree extends CommonTree {
	private static SystemRoleGroupTree instance;

	private SystemRoleGroupTree() {
	}

	public static SystemRoleGroupTree getInstance() {
		synchronized (SystemRoleGroupTree.class) {
			if (null == instance) {
				instance = new SystemRoleGroupTree();
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
			// 父节点为-1就使用系统id作为parentid
			node.setParentId("-1".equals(dd.getParentId()) ? dd.getSystemId() : dd.getParentId());
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
		} else if (info instanceof CloudSystem) {
			CloudSystem dd = (CloudSystem) info;
			node.setNodeId(StringUtil.clean(dd.getId()));
			node.setParentId("-1");
			node.setText(dd.getSystemName());
			node.setType("System");
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

	public void reloadSystemRoleTree(String tenantId) {
		List<Object> nodes = new ArrayList<Object>();
		try {
			// 添加根
			nodes.add(generateRoot());
			List<CloudSystem> systems = this.findAllSystem(tenantId);
			List<CloudRoleGroup> actionGroups = findAllRoleGroup();
			List<CloudRole> actions = findAllRole();
			if (CollectionUtils.isNotEmpty(systems)) {
				nodes.addAll(systems);
			}
			if (CollectionUtils.isNotEmpty(actionGroups)) {
				nodes.addAll(actionGroups);
			}
			if (CollectionUtils.isNotEmpty(actions)) {
				nodes.addAll(actions);
			}

			super.reload(nodes, null);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private List<CloudSystem> findAllSystem(String tenantId) {
		ICloudSystemService cloudSystemService = this.getCloudSystemService();

		List<SearchFilter> searchFilters = Lists.newArrayList();
		searchFilters.add(new SearchFilter("tenantId", Operator.EQ, tenantId));
		return cloudSystemService.findListByFilter(searchFilters, new Sort(Direction.ASC, "createTime"));
	}

	private List<CloudRoleGroup> findAllRoleGroup() {
		ICloudRoleGroupService actionGroupService = this.getRoleGroupService();
		return actionGroupService.findListByProperty(null, new Sort(Direction.ASC, "orderIndex"));
	}

	private List<CloudRole> findAllRole() {
		ICloudRoleService actionService = this.getRoleService();
		List<Order> orders = new ArrayList<Order>();
		Order order1 = new Order(Direction.ASC, "orderIndex");
		Order order2 = new Order(Direction.ASC, "name");
		orders.add(order1);
		orders.add(order2);
		return actionService.findListByProperty(null, new Sort(orders));

	}

	private ICloudRoleGroupService getRoleGroupService() {
		return SpringContextHolder.getBean("cloudRoleGroupService");
	}

	private ICloudRoleService getRoleService() {
		return SpringContextHolder.getBean("cloudRoleService");
	}

	private ICloudSystemService getCloudSystemService() {
		return SpringContextHolder.getBean("cloudSystemService");
	}

}
