package com.vortex.cloud.ums.tree;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

import com.google.common.collect.Maps;
import com.vortex.cloud.ums.config.SpringContextHolder;
import com.vortex.cloud.ums.dataaccess.service.ICloudDepartmentService;
import com.vortex.cloud.ums.dataaccess.service.ICloudOrganizationService;
import com.vortex.cloud.ums.dataaccess.service.ICloudUserService;
import com.vortex.cloud.ums.dto.CloudUserDto;
import com.vortex.cloud.ums.dto.TenantDeptOrgDto;
import com.vortex.cloud.ums.enums.CloudDepartmentTypeEnum;
import com.vortex.cloud.ums.model.CloudDepartment;
import com.vortex.cloud.ums.model.CloudOrganization;
import com.vortex.cloud.ums.util.tree.CommonTree;
import com.vortex.cloud.ums.util.tree.CommonTreeNode;
import com.vortex.cloud.ums.util.utils.ObjectUtil;
import com.vortex.cloud.vfs.common.lang.StringUtil;
import com.vortex.cloud.vfs.data.support.SearchFilter;



/**
 * 云平台租户部门下的机构人员树
 * 
 * @author LiShijun
 * 
 */
public class UserOrganizationTree extends CommonTree {
	private static UserOrganizationTree instance;

	private UserOrganizationTree() {
	}

	public static UserOrganizationTree getInstance() {
		synchronized (UserOrganizationTree.class) {
			if (null == instance) {
				instance = new UserOrganizationTree();
			}
		}
		return instance;
	}

	@Override
	protected CommonTreeNode transform(Object obj) {
		CommonTreeNode node = new CommonTreeNode();
		if (obj instanceof CommonTreeNode) {
			node = (CommonTreeNode) obj;
		} else if (obj instanceof CloudOrganization) {
			CloudOrganization dd = (CloudOrganization) obj;
			node.setNodeId(StringUtil.clean(dd.getId()));
			node.setParentId(StringUtil.clean(dd.getParentId()));
			node.setText(dd.getOrgName());
			node.setType(CloudDepartmentTypeEnum.ORG.getKey()); // 普通的组织机构
			node.setBindData(ObjectUtil.attributesToMap(dd));
		} else if (obj instanceof TenantDeptOrgDto) {
			TenantDeptOrgDto dd = (TenantDeptOrgDto) obj;
			node.setNodeId(StringUtil.clean(dd.getId()));
			node.setParentId(StringUtil.clean(dd.getParentId()));
			node.setText(dd.getName());
			node.setType(dd.getType());
			node.setBindData(ObjectUtil.attributesToMap(dd));
		} else if (obj instanceof CloudUserDto) {
			CloudUserDto dd = (CloudUserDto) obj;
			node.setNodeId(StringUtil.clean(dd.getId()));
			node.setParentId(StringUtil.clean(StringUtils.isEmpty(dd.getOrgId()) ? dd.getDepartmentId() : dd.getOrgId()));
			node.setText(dd.getStaffName());
			node.setType("user");
			node.setBindData(ObjectUtil.attributesToMap(dd));
		}
		return node;
	}

	/**
	 * 机构树
	 * 
	 * @param map
	 */
	public void reloadOrganizationTree(String rootDeptId, Iterable<SearchFilter> searchFilter) {
		List<Object> nodes = new ArrayList<Object>();
		try {
			// 添加根
			nodes.add(generateRoot(rootDeptId));

			List<CloudOrganization> list = this.findOrgListByFilter(searchFilter);

			if (StringUtils.isNotBlank(rootDeptId)) {
				for (CloudOrganization entity : list) {
					if (entity.getDepartmentId().equals(rootDeptId)) {
						nodes.add(entity);
					}
				}
			} else {
				nodes.addAll(list);
			}

			super.reload(nodes, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private Object generateRoot(String rootDeptId) {
		Object root = null;

		if (StringUtils.isBlank(rootDeptId) || "-1".equals(rootDeptId)) {
			CommonTreeNode node = new CommonTreeNode();
			node.setNodeId("-1");
			node.setText("所有机构");
			node.setParentId("0");
			node.setType("Root");
			root = node;
		} else {
			CloudDepartment department = this.getDepartmentService().findOne(rootDeptId);
			TenantDeptOrgDto node = new TenantDeptOrgDto();
			node.setId(department.getId());
			node.setName(department.getDepName());
			node.setParentId("0");
			node.setType("Root");
			node.setDepartmentId(department.getId());
			root = node;
		}

		return root;
	}

	private List<CloudOrganization> findOrgListByFilter(Iterable<SearchFilter> searchFilter) {
		Sort sort = new Sort(Direction.ASC, "org.orgCode");

		ICloudOrganizationService orgService = this.getOrganizationService();

		return orgService.findListByFilter(searchFilter, sort);
	}

	private ICloudOrganizationService getOrganizationService() {
		return SpringContextHolder.getBean("cloudOrganizationService");
	}

	/**
	 * 部门树(单位 + 单位下的机构+人员)
	 * 
	 * @param tenantId
	 * 
	 * @param map
	 */
	public void reloadDeptOrgUserTree(String tenantId, String rootDeptId) {
		List<Object> nodes = new ArrayList<Object>();
		try {
			// 添加根
			nodes.add(generateRoot(rootDeptId));

			List<TenantDeptOrgDto> list = this.findDeptOrgList(tenantId, rootDeptId);

			List<CloudUserDto> users = this.findUserList(tenantId);
			if (StringUtils.isNotBlank(rootDeptId)) {
				for (TenantDeptOrgDto entity : list) {
					if (entity.getDepartmentId().equals(rootDeptId)) {
						nodes.add(entity);
					}
				}
			} else {
				nodes.addAll(list);
			}
			nodes.addAll(users);
			super.reload(nodes, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private List<TenantDeptOrgDto> findDeptOrgList(String tenantId, String deptId) {
		ICloudDepartmentService service = this.getDepartmentService();

		return service.findDeptOrgList(tenantId, deptId, null);
	}

	/**
	 * 根据租户id查询下面的所有用户
	 * 
	 * @param tenantId 租户id
	 * @return
	 */
	private List<CloudUserDto> findUserList(String tenantId) {
		ICloudUserService service = this.getCloudUserService();
		Map<String, String> paramMap = Maps.newHashMap();
		paramMap.put("tenantId", tenantId);
		return service.getUsersByCondiction(paramMap);
	}

	private ICloudDepartmentService getDepartmentService() {
		return SpringContextHolder.getBean("cloudDepartmentService");
	}

	private ICloudUserService getCloudUserService() {
		return SpringContextHolder.getBean("cloudUserService");
	}
}
