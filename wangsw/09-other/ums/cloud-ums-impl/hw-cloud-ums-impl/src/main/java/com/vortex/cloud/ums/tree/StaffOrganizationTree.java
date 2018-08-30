package com.vortex.cloud.ums.tree;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.vortex.cloud.ums.config.SpringContextHolder;
import com.vortex.cloud.ums.dataaccess.service.ICloudDepartmentService;
import com.vortex.cloud.ums.dataaccess.service.ICloudOrganizationService;
import com.vortex.cloud.ums.dataaccess.service.ICloudStaffService;
import com.vortex.cloud.ums.dto.TenantDeptOrgDto;
import com.vortex.cloud.ums.enums.CloudDepartmentTypeEnum;
import com.vortex.cloud.ums.model.CloudOrganization;
import com.vortex.cloud.ums.model.CloudStaff;
import com.vortex.cloud.ums.util.tree.CommonTree;
import com.vortex.cloud.ums.util.tree.CommonTreeNode;
import com.vortex.cloud.ums.util.utils.ObjectUtil;
import com.vortex.cloud.vfs.common.lang.StringUtil;
import com.vortex.cloud.vfs.data.support.SearchFilter;
import com.vortex.cloud.vfs.data.support.SearchFilter.Operator;



/**
 * 云平台租户部门下的机构人员树
 * 
 * @author LiShijun
 * 
 */
public class StaffOrganizationTree extends CommonTree {
	private static StaffOrganizationTree instance;
	private static final String DEPARTMENT_ID = "departmentId";
	private static final String TENANT_ID = "tenantId";
	private static final String COMPANY_ID = "companyId";;

	private StaffOrganizationTree() {
	}

	public static StaffOrganizationTree getInstance() {
		synchronized (StaffOrganizationTree.class) {
			if (null == instance) {
				instance = new StaffOrganizationTree();
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
		} else if (obj instanceof CloudStaff) {
			CloudStaff dd = (CloudStaff) obj;
			node.setNodeId(StringUtil.clean(dd.getId()));
			node.setParentId(StringUtil.clean(StringUtils.isNotEmpty(dd.getOrgId()) ? dd.getOrgId() : dd.getDepartmentId()));
			node.setText(dd.getName());
			node.setType("staff");
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
		List<Object> nodes = new ArrayList<>();
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
			TenantDeptOrgDto node = this.getOrganizationService().getDepartmentOrOrgById(rootDeptId, null);
			node.setParentId("0");
			node.setType("Root");
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
	 * 部门树(单位 + 单位下的机构+人员)(根据条件过滤)
	 * 
	 * @param paramMap 条件
	 * @param map
	 */
	public void reloadDeptOrgStaffTreeByFilter(Map<String, Object> paramMap) {
		List<Object> nodes = new ArrayList<>();
		try {
			String companyId = (String) paramMap.get(COMPANY_ID);
			// 添加根
			nodes.add(generateRoot(companyId));

			List<TenantDeptOrgDto> list = this.findDeptOrgListByCompandyId((String) paramMap.get(TENANT_ID), companyId);

			List<CloudStaff> staffs = this.findStaffList(paramMap);

			nodes.addAll(list);

			nodes.addAll(staffs);
			super.reload(nodes, null);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 根据depart或者org 的id查找其下的部门
	 * 
	 * @param tenantId
	 * @param companyId
	 * @return
	 */
	private List<TenantDeptOrgDto> findDeptOrgListByCompandyId(String tenantId, String companyId) {
		ICloudDepartmentService service = this.getDepartmentService();

		return service.findDeptOrgListByCompandyId(tenantId, companyId, null);
	}

	/**
	 * 部门树(单位 + 单位下的机构+人员)
	 * 
	 * @param tenantId
	 * 
	 * @param map
	 */
	public void reloadDeptOrgStaffTree(String tenantId, String rootDeptId) {
		List<Object> nodes = new ArrayList<>();
		try {
			// 添加根
			nodes.add(generateRoot(rootDeptId));

			List<TenantDeptOrgDto> list = this.findDeptOrgList(tenantId, rootDeptId);
			Map<String, Object> map = Maps.newHashMap();
			map.put(TENANT_ID, tenantId);
			List<CloudStaff> staffs = this.findStaffList(map);
			if (StringUtils.isNotBlank(rootDeptId)) {
				for (TenantDeptOrgDto entity : list) {
					if (entity.getDepartmentId().equals(rootDeptId)) {
						nodes.add(entity);
					}
				}
			} else {
				nodes.addAll(list);
			}
			nodes.addAll(staffs);
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
	 * 根据租户id查询下面的所有人员
	 * 
	 * @param paramMap 租户id
	 * @return
	 */
	private List<CloudStaff> findStaffList(Map<String, Object> paramMap) {
		ICloudStaffService service = this.getCloudStaffService();
		List<SearchFilter> searchFilters = Lists.newArrayList();
		searchFilters.add(new SearchFilter(TENANT_ID, Operator.EQ, paramMap.get(TENANT_ID)));

		if (StringUtils.isNotBlank((CharSequence) paramMap.get(DEPARTMENT_ID))) {
			searchFilters.add(new SearchFilter(DEPARTMENT_ID, Operator.EQ, paramMap.get(DEPARTMENT_ID)));
		}
		searchFilters.add(new SearchFilter("tenantId", Operator.EQ, paramMap.get(TENANT_ID)));
		return service.findListByFilter(searchFilters, new Sort("orderIndex"));
	}

	private ICloudDepartmentService getDepartmentService() {
		return SpringContextHolder.getBean("cloudDepartmentService");
	}

	private ICloudStaffService getCloudStaffService() {
		return SpringContextHolder.getBean("cloudStaffService");
	}

}
