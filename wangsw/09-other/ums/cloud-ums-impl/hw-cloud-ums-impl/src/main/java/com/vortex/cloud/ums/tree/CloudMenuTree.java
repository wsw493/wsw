package com.vortex.cloud.ums.tree;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vortex.cloud.ums.config.SpringContextHolder;
import com.vortex.cloud.ums.dataaccess.service.ICloudMenuService;
import com.vortex.cloud.ums.model.CloudMenu;
import com.vortex.cloud.ums.util.tree.CommonTree;
import com.vortex.cloud.ums.util.tree.CommonTreeNode;
import com.vortex.cloud.ums.util.utils.ObjectUtil;
import com.vortex.cloud.vfs.common.lang.StringUtil;

/**
 * 云平台菜单树
 * 
 * @author LiShijun
 * 
 */
public class CloudMenuTree extends CommonTree {

	private static final Logger logger = LoggerFactory.getLogger(CloudMenuTree.class);
	public static final String ROOT_NODE_ID = "-1"; // 菜单树根节点Id
	public static final String ROOT_NODE_TEXT = "所有菜单"; // 菜单树根节点Text

	private static CloudMenuTree instance;

	private CloudMenuTree() {
	}

	public static CloudMenuTree getInstance() {
		synchronized (CloudMenuTree.class) {
			if (null == instance) {
				instance = new CloudMenuTree();
			}
		}
		return instance;
	}

	@Override
	protected CommonTreeNode transform(Object obj) {
		CommonTreeNode node = new CommonTreeNode();
		if (obj instanceof CommonTreeNode) {
			node = (CommonTreeNode) obj;
		} else if (obj instanceof CloudMenu) {
			CloudMenu entity = (CloudMenu) obj;
			node.setNodeId(StringUtil.clean(entity.getId()));
			node.setParentId(StringUtil.clean(entity.getParentId()));
			node.setText(entity.getName());
			node.setBindData(ObjectUtil.attributesToMap(entity));
		}

		return node;
	}

	/**
	 * 加载系统对应的菜单树
	 * 
	 * @param tenantId
	 * @param systemId
	 */
	public void reloadMenuTree(String systemId) {
		List<Object> nodes = new ArrayList<Object>();
		try {
			// 添加根
			nodes.add(this.generateRoot());

			List<CloudMenu> list = this.getMenuList(systemId);
			nodes.addAll(list);

			super.reload(nodes, null);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("reloadMenuTree()：" + e);
		}
	}

	/**
	 * 生成树的根节点
	 * 
	 * @return
	 */
	private Object generateRoot() {

		CommonTreeNode node = new CommonTreeNode();
		node.setNodeId(CloudMenuTree.ROOT_NODE_ID);
		node.setText(CloudMenuTree.ROOT_NODE_TEXT);
		node.setParentId("0");
		node.setType("Root");

		return node;
	}

	/**
	 * 获取指定系统下的菜单列表
	 * 
	 * @param systemId
	 * @return
	 */
	private List<CloudMenu> getMenuList(String systemId) {
		ICloudMenuService service = this.getMenuService();

		return service.getMenuList(systemId);
	}

	private ICloudMenuService getMenuService() {
		return SpringContextHolder.getBean("cloudMenuService");
	}
}
