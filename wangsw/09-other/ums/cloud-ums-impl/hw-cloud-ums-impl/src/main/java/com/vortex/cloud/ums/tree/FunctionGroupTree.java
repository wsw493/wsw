package com.vortex.cloud.ums.tree;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

import com.vortex.cloud.ums.config.SpringContextHolder;
import com.vortex.cloud.ums.dataaccess.service.ICloudFunctionGroupService;
import com.vortex.cloud.ums.dataaccess.service.ICloudFunctionService;
import com.vortex.cloud.ums.model.CloudFunction;
import com.vortex.cloud.ums.model.CloudFunctionGroup;
import com.vortex.cloud.ums.util.tree.CommonTree;
import com.vortex.cloud.ums.util.tree.CommonTreeNode;
import com.vortex.cloud.ums.util.utils.ObjectUtil;
import com.vortex.cloud.vfs.common.lang.StringUtil;
import com.vortex.cloud.vfs.data.support.SearchFilter;
import com.vortex.cloud.vfs.data.support.SearchFilter.Operator;



/**
 * 功能组树
 * 
 * @author lsm
 * @date 2016年4月5日
 */
public class FunctionGroupTree extends CommonTree {
	private static FunctionGroupTree instance;

	private FunctionGroupTree() {
	}

	public static FunctionGroupTree getInstance() {
		synchronized (FunctionGroupTree.class) {
			if (null == instance) {
				instance = new FunctionGroupTree();
			}
		}
		return instance;
	}

	@Override
	protected CommonTreeNode transform(Object info) {

		CommonTreeNode node = new CommonTreeNode();
		if (info instanceof CloudFunctionGroup) {
			CloudFunctionGroup dd = (CloudFunctionGroup) info;
			node.setNodeId(StringUtil.clean(dd.getId()));
			node.setParentId(StringUtil.clean(dd.getParentId()));
			node.setText(dd.getName());
			node.setType("FunctionGroup");
			// node.setQtip(node.getType() + node.getText());
			node.setQtip(node.getText());
			node.setBindData(ObjectUtil.attributesToMap(dd));
		} else if (info instanceof CommonTreeNode) {
			node = (CommonTreeNode) info;
		} else if (info instanceof CloudFunction) {
			CloudFunction dd = (CloudFunction) info;
			node.setNodeId(StringUtil.clean(dd.getId()));
			node.setParentId(StringUtil.clean(dd.getGroupId()));
			node.setText(dd.getName()+"("+dd.getCode()+")");
			node.setType("Function");
			// node.setQtip(node.getType() + node.getText());
			node.setQtip(node.getText());
			node.setBindData(ObjectUtil.attributesToMap(dd));
		}
		return node;
	}

	private CommonTreeNode generateRoot() {
		CommonTreeNode root = new CommonTreeNode();
		root.setNodeId("-1");
		root.setText("功能组");
		root.setParentId("0");
		root.setType("Root");
		return root;
	}

	public void reloadFunctionGroupTree(Map<String, Object> map) {
		List<Object> nodes = new ArrayList<Object>();
		try {
			// 添加根
			nodes.add(generateRoot());
			List<CloudFunctionGroup> actionGroups = findAllFunctionGroup(map);
			nodes.addAll(actionGroups);
			super.reload(nodes, null);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void reloadFunctionTree(Map<String, Object> map) {
		List<Object> nodes = new ArrayList<Object>();
		try {
			// 添加根
			nodes.add(generateRoot());
			List<CloudFunctionGroup> actionGroups = findAllFunctionGroup(map);
			List<CloudFunction> actions = findAllFunction(map);
			nodes.addAll(actionGroups);
			nodes.addAll(actions);
			super.reload(nodes, null);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private List<CloudFunctionGroup> findAllFunctionGroup(Map<String, Object> map) {
		ICloudFunctionGroupService actionGroupService = this.getFunctionGroupService();
		return actionGroupService.findListByProperty(map, new Sort(Direction.ASC, "orderIndex"));
	}

	private List<CloudFunction> findAllFunction(Map<String, Object> map) {
		ICloudFunctionService actionService = this.getFunctionService();
		return actionService.findListByProperty(map, new Sort(Direction.ASC, "orderIndex"));

	}

	private ICloudFunctionGroupService getFunctionGroupService() {
		return SpringContextHolder.getBean("cloudFunctionGroupService");
	}

	private ICloudFunctionService getFunctionService() {
		return SpringContextHolder.getBean("cloudFunctionService");
	}

	/**
	 * 加载云系统功能组的树
	 * @param map
	 */
	public void reloadCloudSystemFunctionGroupTree() {
		List<Object> nodes = new ArrayList<Object>();
		try {
			// 添加根
			nodes.add(generateRoot());
			
			List<SearchFilter> sfList = new ArrayList<SearchFilter>();
			sfList.add(new SearchFilter("cloudSystemId", Operator.NNULL, null));
			
			List<CloudFunctionGroup> actionGroups = this.findAllCloudSystemFunctionGroup(sfList);
			nodes.addAll(actionGroups);
			
			super.reload(nodes, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private List<CloudFunctionGroup> findAllCloudSystemFunctionGroup(Iterable<SearchFilter> searchFilter) {
		ICloudFunctionGroupService service = this.getFunctionGroupService();
		return service.findListByFilter(searchFilter, new Sort(Direction.ASC, "orderIndex"));
	}
	
	public void reloadCloudSystemFunctionTree() {
		List<Object> nodes = new ArrayList<Object>();
		try {
			// 添加根
			nodes.add(generateRoot());
			
			List<SearchFilter> sfList = new ArrayList<SearchFilter>();
			sfList.add(new SearchFilter("cloudSystemId", Operator.NNULL, null));
			
			List<CloudFunctionGroup> groupList = findAllCloudSystemFunctionGroup(sfList);
			List<CloudFunction> funList = findAllCloudSystemFunction(sfList);
			
			nodes.addAll(groupList);
			nodes.addAll(funList);
			
			super.reload(nodes, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private List<CloudFunction> findAllCloudSystemFunction(Iterable<SearchFilter> searchFilter) {
		ICloudFunctionService service = this.getFunctionService();
		
		return service.findListByFilter(searchFilter, new Sort(Direction.ASC, "orderIndex"));
	}
}

