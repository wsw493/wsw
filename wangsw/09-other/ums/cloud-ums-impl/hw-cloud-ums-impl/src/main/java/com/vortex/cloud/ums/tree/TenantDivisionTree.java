package com.vortex.cloud.ums.tree;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

import com.vortex.cloud.ums.config.SpringContextHolder;
import com.vortex.cloud.ums.dataaccess.service.ITenantDivisionService;
import com.vortex.cloud.ums.dto.TenantDivisionDto;
import com.vortex.cloud.ums.model.TenantDivision;
import com.vortex.cloud.ums.util.tree.CommonTree;
import com.vortex.cloud.ums.util.tree.CommonTreeNode;
import com.vortex.cloud.ums.util.utils.ObjectUtil;
import com.vortex.cloud.vfs.common.exception.ServiceException;
import com.vortex.cloud.vfs.common.lang.StringUtil;
import com.vortex.cloud.vfs.data.support.SearchFilter;
import com.vortex.cloud.vfs.data.support.SearchFilter.Operator;




/**
 * 云平台租户行政区划树
 * 
 * @author LiShijun
 * 
 */
public class TenantDivisionTree extends CommonTree {
	private static TenantDivisionTree instance;

	private TenantDivisionTree() {
	}

	public static TenantDivisionTree getInstance() {
		synchronized (TenantDivisionTree.class) {
			if (null == instance) {
				instance = new TenantDivisionTree();
			}
		}
		return instance;
	}

	@Override
	protected CommonTreeNode transform(Object obj) {
		CommonTreeNode node = new CommonTreeNode();
		if (obj instanceof CommonTreeNode) {
			node = (CommonTreeNode) obj;
		} else if (obj instanceof TenantDivision) {
			TenantDivision dd = (TenantDivision) obj;
			node.setNodeId(StringUtil.clean(dd.getId()));
			node.setParentId(StringUtil.clean(dd.getParentId()));
			node.setText(dd.getName());
			node.setType(dd.getLevel() == null ? null : dd.getLevel().toString());
			node.setQtip(node.getText());
			node.setBindData(ObjectUtil.attributesToMap(dd));
		}

		return node;
	}

	private CommonTreeNode generateRoot(String tenantId, String divisionId) throws Exception {
		CommonTreeNode root = new CommonTreeNode();

		if (StringUtils.isBlank(divisionId) || "-1".equals(divisionId)) {

			List<SearchFilter> filterList = new ArrayList<>();
			filterList.add(new SearchFilter("tenantId", Operator.EQ, tenantId));
			filterList.add(new SearchFilter("enabled", Operator.EQ, TenantDivision.ENABLED_YES));
			filterList.add(new SearchFilter("isRoot", Operator.EQ, TenantDivision.ROOT_YES));

			Sort sort = new Sort(Direction.ASC, "orderIndex","commonCode");
			ITenantDivisionService service = this.getTenantDivisionService();
			List<TenantDivision> list = service.findListByFilter(filterList, sort);

			if (CollectionUtils.isEmpty(list)) {
				root.setNodeId("-1");
				root.setText("所有");
			} else {
				TenantDivision division = list.get(0);
				root.setNodeId(division.getId());
				root.setText(division.getName());
			}
		} else {
			TenantDivision division = this.getTenantDivisionService().findOne(divisionId);
			if (division == null) {
				throw new ServiceException("不存在id为" + divisionId + "的数据");
			}
			root.setNodeId(divisionId);
			root.setText(division.getName());
		}

		root.setParentId("0");
		root.setType("Root");
		return root;
	}

	/**
	 * 行政区划树
	 * 
	 * @param tenantId 租户ID
	 * @param divisionId 用于在租户树下，过滤出子树
	 */
	public void reloadTenantDivisionTree(String tenantId, String divisionId) throws Exception{
		List<Object> nodes = new ArrayList<>();
			// 添加根
			nodes.add(generateRoot(tenantId, divisionId));

			List<TenantDivision> list = this.findTenantDivisionList(tenantId, divisionId);

			if (StringUtils.isNotBlank(divisionId)) {
				for (TenantDivision entity : list) {
					if (entity.getParentId().equals(divisionId)) {
						nodes.add(entity);
					}
				}
			} else {
				nodes.addAll(list);
			}

			super.reload(nodes, null);
	}

	private List<TenantDivision> findTenantDivisionList(String tenantId, String divisionId) {
		// 取租户下的所有节点
		ITenantDivisionService service = this.getTenantDivisionService();
		TenantDivisionDto tenantDivision = new TenantDivisionDto();
		tenantDivision.setTenantId(tenantId);
		tenantDivision.setParentId(divisionId);
		return service.findTenantDivisionList(tenantDivision);
	}

	private ITenantDivisionService getTenantDivisionService() {
		return SpringContextHolder.getBean("tenantDivisionService");
	}
}
