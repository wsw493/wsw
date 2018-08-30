package com.vortex.cloud.ums.tree;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

import com.vortex.cloud.ums.config.SpringContextHolder;
import com.vortex.cloud.ums.dataaccess.service.IWorkElementService;
import com.vortex.cloud.ums.dataaccess.service.IWorkElementTypeService;
import com.vortex.cloud.ums.model.WorkElement;
import com.vortex.cloud.ums.model.WorkElementType;
import com.vortex.cloud.ums.support.ManagementConstant;
import com.vortex.cloud.ums.util.tree.CommonTree;
import com.vortex.cloud.ums.util.tree.CommonTreeNode;
import com.vortex.cloud.ums.util.utils.ObjectUtil;
import com.vortex.cloud.vfs.common.lang.StringUtil;
import com.vortex.cloud.vfs.data.support.SearchFilter;
import com.vortex.cloud.vfs.data.support.SearchFilter.Operator;



/**
 * 图元类型树
 * 
 * @author SonHo
 * 
 *
 */
public class WorkElementTree extends CommonTree {
	private static WorkElementTree instance;

	private WorkElementTree() {
	}

	public static WorkElementTree getInstance() {
		synchronized (WorkElementTree.class) {
			if (null == instance) {
				instance = new WorkElementTree();
			}
		}
		return instance;
	}

	@Override
	protected CommonTreeNode transform(Object info) {

		CommonTreeNode node = new CommonTreeNode();
		if (info instanceof WorkElementType) {
			WorkElementType wet = (WorkElementType) info;
			node.setNodeId(StringUtil.clean(wet.getId()));
			node.setParentId("-1");
			node.setText(wet.getName());
			node.setType("WorkElementType");
			node.setIcon(ManagementConstant.TREE_ICON_WORK_ELEMENT_TYPE);
			node.setQtip(node.getText());
			node.setBindData(ObjectUtil.attributesToMap(wet));
		} else if (info instanceof CommonTreeNode) {
			node = (CommonTreeNode) info;
		} else if (info instanceof WorkElement) {
			WorkElement we = (WorkElement) info;
			node.setNodeId(StringUtil.clean(we.getId()));
			node.setParentId(StringUtil.clean(we.getWorkElementTypeId()));
			node.setText(we.getName());
			node.setType("WorkElement");
			node.setIcon(ManagementConstant.TREE_ICON_WORK_ELEMENT);
			node.setQtip(node.getText());
			node.setBindData(ObjectUtil.attributesToMap(we));
		}
		return node;
	}

	private CommonTreeNode generateRoot() {
		CommonTreeNode root = new CommonTreeNode();
		root.setNodeId("-1");
		root.setText("图元类型");
		root.setParentId("0");
		root.setType("Root");
		return root;
	}

	public void reloadWorkElementTypeTree(Map<String, Object> map) {
		List<Object> nodes = new ArrayList<Object>();
		try {
			// 添加根
			nodes.add(generateRoot());
			List<WorkElementType> workElementTypes = findWorkElementType(map);
			nodes.addAll(workElementTypes);
			super.reload(nodes, null);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void reloadWorkElementTree(Map<String, Object> map) {
		List<Object> nodes = new ArrayList<Object>();
		try {
			// 添加根
			nodes.add(generateRoot());
			List<WorkElementType> workElementTypes = findWorkElementType(map);
			List<WorkElement> workElements = findWorkElement(map, workElementTypes);
			nodes.addAll(workElementTypes);
			nodes.addAll(workElements);
			super.reload(nodes, null);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private List<WorkElementType> findWorkElementType(Map<String, Object> map) {
		IWorkElementTypeService workElementTypeService = SpringContextHolder.getBean("workElementTypeService");
		List<SearchFilter> typeFilters = new ArrayList<SearchFilter>();
		String tenantId = (String) map.get("tenantId");
		if (StringUtils.isNotEmpty(tenantId)) {
			typeFilters.add(new SearchFilter("tenantId", Operator.EQ, tenantId));
		}
		String typeCode = (String) map.get("typeCode");
		if (StringUtils.isNotEmpty(typeCode)) {
			typeFilters.add(new SearchFilter("code", Operator.LIKE, typeCode));
		}
		String shape = (String) map.get("shape");
		if (StringUtils.isNotEmpty(shape)) {
			typeFilters.add(new SearchFilter("shape", Operator.EQ, shape));
		}
		List<WorkElementType> types = workElementTypeService.findListByFilter(typeFilters, new Sort(Direction.ASC, "orderIndex"));
		return types;
	}

	private List<WorkElement> findWorkElement(Map<String, Object> map, List<WorkElementType> workElementTypes) {
		IWorkElementService workElementService = SpringContextHolder.getBean("workElementService");
		List<SearchFilter> filters = new ArrayList<SearchFilter>();
		String tenantId = (String) map.get("tenantId");
		filters.add(new SearchFilter("tenantId", Operator.EQ, tenantId));
		String userId = (String) map.get("userId");
		if (StringUtils.isNotEmpty(userId)) {
			filters.add(new SearchFilter("userId", Operator.EQ, userId));
		}
		String shape = (String) map.get("shape");
		if (StringUtils.isNotEmpty(shape)) {
			filters.add(new SearchFilter("shape", Operator.EQ, shape));
		}
		if (CollectionUtils.isNotEmpty(workElementTypes)) {
			List<String> workElementTypeIds = new ArrayList<String>();
			for (WorkElementType temp : workElementTypes) {
				workElementTypeIds.add(temp.getId());
			}
			filters.add(new SearchFilter("workElementTypeId", Operator.IN, workElementTypeIds.toArray(new String[workElementTypeIds.size()]))) ;
		}
		return workElementService.findListByFilter(filters, new Sort(Direction.ASC, "code"));
	}

}
