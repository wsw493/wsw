package com.vortex.cloud.ums.tree;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.vortex.cloud.ums.config.SpringContextHolder;
import com.vortex.cloud.ums.dataaccess.service.ICloudDepartmentService;
import com.vortex.cloud.ums.dataaccess.service.ICloudOrganizationService;
import com.vortex.cloud.ums.dataaccess.service.ICloudStaffService;
import com.vortex.cloud.ums.dataaccess.service.ICloudUserService;
import com.vortex.cloud.ums.dataaccess.service.IWorkElementService;
import com.vortex.cloud.ums.dto.CloudUserDto;
import com.vortex.cloud.ums.dto.TenantDeptOrgDto;
import com.vortex.cloud.ums.dto.WorkElementDto;
import com.vortex.cloud.ums.enums.CompanyTypeEnum;
import com.vortex.cloud.ums.enums.PermissionScopeEnum;
import com.vortex.cloud.ums.model.CloudDepartment;
import com.vortex.cloud.ums.model.CloudOrganization;
import com.vortex.cloud.ums.model.CloudStaff;
import com.vortex.cloud.ums.model.CloudUser;
import com.vortex.cloud.ums.model.WorkElement;
import com.vortex.cloud.ums.support.IconConstant;
import com.vortex.cloud.ums.util.tree.CommonTree;
import com.vortex.cloud.ums.util.tree.CommonTreeNode;
import com.vortex.cloud.ums.util.utils.ObjectUtil;
import com.vortex.cloud.vfs.common.lang.StringUtil;
import com.vortex.cloud.vfs.data.dto.LoginReturnInfoDto;
import com.vortex.cloud.vfs.data.support.SearchFilter;
import com.vortex.cloud.vfs.data.support.SearchFilter.Operator;
import com.vortex.cloud.vfs.data.support.SearchFilters;

/**
 * 云平台租户部门下的机构树 <br>
 * 根据人员的权限过滤树
 * 
 * @author lusm
 * 
 */
public class OrganizationTreeWithPermission extends CommonTree {
	private static OrganizationTreeWithPermission instance;

	private OrganizationTreeWithPermission() {
	}

	public static OrganizationTreeWithPermission getInstance() {
		synchronized (OrganizationTreeWithPermission.class) {
			if (null == instance) {
				instance = new OrganizationTreeWithPermission();
			}
		}
		return instance;
	}

	@Override
	protected CommonTreeNode transform(Object obj) {
		CommonTreeNode node = new CommonTreeNode();
		if (obj instanceof CommonTreeNode) {
			node = (CommonTreeNode) obj;
			node.setIcon(IconConstant.TreeIcon.ICON_ALL_DEPARTMENT);
		} else if (obj instanceof CloudOrganization) {
			CloudOrganization dd = (CloudOrganization) obj;
			node.setNodeId(StringUtil.clean(dd.getId()));
			node.setParentId(StringUtil.clean(dd.getParentId()));
			node.setIcon(IconConstant.TreeIcon.ICON_DEPARTMENT);
			node.setText(dd.getOrgName());
			node.setType(CompanyTypeEnum.ORG.getKey()); // CloudOrganization层级的
			node.setBindData(ObjectUtil.attributesToMap(dd));
		} else if (obj instanceof TenantDeptOrgDto) {
			TenantDeptOrgDto dd = (TenantDeptOrgDto) obj;
			node.setNodeId(StringUtil.clean(dd.getId()));
			node.setParentId(StringUtil.clean(dd.getParentId()));
			node.setIcon(IconConstant.TreeIcon.ICON_DEPARTMENT);
			node.setText(dd.getName());
			node.setType(dd.getCompanyType());
			node.setBindData(ObjectUtil.attributesToMap(dd));
		} else if (obj instanceof WorkElementDto) {
			WorkElementDto dto = (WorkElementDto) obj;
			node.setNodeId(StringUtil.clean(dto.getId()));
			node.setParentId(StringUtil.clean(dto.getDepartmentId()));

			switch (dto.getShape()) {
			case "point":
				node.setIcon(IconConstant.TreeIcon.ICON_POINT);
			case "line":
				node.setIcon(IconConstant.TreeIcon.ICON_LINE);
				break;
			case "area":
			case "polygon":
			case "rectangle":
			case "circle":
				node.setIcon(IconConstant.TreeIcon.ICON_AREA);
				break;
			}

			node.setText(dto.getName());
			node.setType("WorkElement");
			node.setBindData(ObjectUtil.attributesToMap(dto));
		}
		return node;
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

	/**
	 * 部门树(单位 + 单位下的机构)
	 * 
	 * @param tenantId
	 * 
	 * @param rootDeptId
	 */
	public void reloadDeptOrgTree(Map<String, String> param) {
		String userId = param.get("userId");
		String tenantId = param.get("tenantId");
		String isControlPermission = param.get("isControlPermission");
		// 获取用户
		CloudUser user = getCloudUserService().findOne(userId);

		String scope = user.getCustomScope();
		// 人员信息
		CloudStaff staffDto = getCloudStaffService().findOne(user.getStaffId());
		String orgId = null;
		String departmentId = null;
		String companyId = null;
		if (staffDto != null) {
			orgId = staffDto.getOrgId();
			departmentId = staffDto.getDepartmentId();
			companyId = StringUtils.isNotBlank(orgId) ? orgId : departmentId;
			tenantId = staffDto.getTenantId();
		}
		// 自定义范围
		String[] customScope = StringUtils.isNotEmpty(scope) ? scope.split(",") : null;
		// 没有设置范围，直接返回
		List<Object> nodes = new ArrayList<>();

		// 为了兼容以前的，没有设置权限就给他全部的权限

		// 不控制权限或者拥有的是全部权限，显示该租户下的
		if (StringUtils.isEmpty(user.getPermissionScope()) || PermissionScopeEnum.ALL.getKey().equals(user.getPermissionScope()) || StringUtils.isBlank(isControlPermission)
				|| "0".equals(isControlPermission)) {

			// 添加根
			nodes.add(generateRoot(null));

			List<TenantDeptOrgDto> list = this.findDeptOrgList(tenantId, null);
			if (CollectionUtils.isNotEmpty(list)) {
				nodes.addAll(list);
			}
		} else if (PermissionScopeEnum.NONE.getKey().equals(user.getPermissionScope())) {
			// 没有权限
			nodes.add(generateRoot(null));

		} else if (PermissionScopeEnum.CUSTOM.getKey().equals(user.getPermissionScope())) {
			// 添加根
			nodes.add(generateRoot(null));
			if (ArrayUtils.isNotEmpty(customScope)) {
				// 全选的机构列表
				List<TenantDeptOrgDto> selected = getOrganizationService().getDepartmentsOrOrgByIds(customScope);

				// 租户下所有的depart和org
				Map<String, TenantDeptOrgDto> allMap = Maps.newHashMap();
				List<TenantDeptOrgDto> all = getDepartmentService().findDeptOrgList(tenantId, null, null);
				// 利用map,去除重复
				if (CollectionUtils.isNotEmpty(all)) {
					for (TenantDeptOrgDto tenantDeptOrgDto : all) {
						allMap.put(tenantDeptOrgDto.getId(), tenantDeptOrgDto);
					}
				}

				// 用来构造树的数据，利用map去除重复
				Map<String, TenantDeptOrgDto> treeMap = Maps.newHashMap();
				for (TenantDeptOrgDto child : selected) {
					// 添加所有的父节点（父节点设置为半选中）
					getParents(child, allMap, treeMap);

				}
				// 因为父节点中可能存在选中的节点，最后添加全选的节点，才不会被父节点中半选覆盖
				for (TenantDeptOrgDto child : selected) {
					// 添加自己
					treeMap.put(child.getId(), child);
				}

				// 树的机构数据
				List<TenantDeptOrgDto> treeList = new ArrayList<>(treeMap.values());
				if (CollectionUtils.isNotEmpty(treeList)) {

					nodes.addAll(treeList);
				}
			}
		} else if (PermissionScopeEnum.SELF.getKey().equals(user.getPermissionScope())) {
			// 本级
			// 添加根
			nodes.add(generateRoot(null));
			// 获取自己

			// 如果该人员部署于任何depart，name就返回空的树
			if (StringUtils.isNotBlank(companyId)) {

				// 返回自己属于的节点，挂在root节点下
				TenantDeptOrgDto tenantDeptOrgDto = getCompany(companyId);
				tenantDeptOrgDto.setParentId("-1");
				if (tenantDeptOrgDto != null) {
					nodes.add(tenantDeptOrgDto);
				}
			}
		} else if (PermissionScopeEnum.SELF_AND_DOWN.getKey().equals(user.getPermissionScope())) {
			// 本级及以下

			try {
				// 添加根
				nodes.add(generateRoot(null));
				// 如果该人员不属于任何depart，name就返回空的树
				List<TenantDeptOrgDto> list = Lists.newArrayList();
				if (StringUtils.isNotBlank(companyId)) {
					// 本身
					TenantDeptOrgDto self = getCompany(companyId);
					// 将本身挂在root下，所以父节点设为-1
					self.setParentId(DepartmentTree.ROOT_NODE_ID);
					list = this.findDeptOrgListByCompandyId(tenantId, companyId, null);
					list.add(self);
				}
				if (CollectionUtils.isNotEmpty(list)) {
					nodes.addAll(list);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		super.reload(nodes, null);
	}

	/**
	 * 部门树(单位 + 单位下的机构 + 图元)
	 * 
	 * @param info
	 * @param filterList
	 */
	public void reloadOrganizationElementTree(LoginReturnInfoDto info, List<SearchFilter> filterList) {

		try {

			String userId = info.getUserId();
			String tenantId = info.getTenantId();
			String isControlPermission = "1";
			// 获取用户
			CloudUser user = getCloudUserService().findOne(userId);

			String scope = user.getCustomScope();
			// 人员信息
			CloudStaff staffDto = getCloudStaffService().findOne(user.getStaffId());
			String orgId = null;
			String departmentId = null;
			String companyId = null;
			if (staffDto != null) {
				orgId = staffDto.getOrgId();
				departmentId = staffDto.getDepartmentId();
				companyId = StringUtils.isNotBlank(orgId) ? orgId : departmentId;
				tenantId = staffDto.getTenantId();
			}

			// 自定义范围
			String[] customScope = StringUtils.isNotEmpty(scope) ? scope.split(",") : new String[0];
			// 没有设置范围，直接返回
			List<Object> nodes = new ArrayList<>();
			// 真正有权限的机构节点（自定义中全选的才算有权限）
			List<TenantDeptOrgDto> permissionCompanyNodes = Lists.newArrayList();

			// 为了兼容以前的，没有设置权限就给他全部的权限

			// 不控制权限或者拥有的是全部权限，显示该租户下的
			if (StringUtils.isEmpty(user.getPermissionScope()) || PermissionScopeEnum.ALL.getKey().equals(user.getPermissionScope()) || StringUtils.isBlank(isControlPermission)
					|| "0".equals(isControlPermission)) {

				// 添加根
				nodes.add(generateRoot(null));

				List<TenantDeptOrgDto> list = this.findDeptOrgList(tenantId, null);
				if (CollectionUtils.isNotEmpty(list)) {
					nodes.addAll(list);
					permissionCompanyNodes.addAll(list);
				}
			} else if (PermissionScopeEnum.NONE.getKey().equals(user.getPermissionScope())) {
				// 没有权限
				nodes.add(generateRoot(null));

			} else if (PermissionScopeEnum.CUSTOM.getKey().equals(user.getPermissionScope())) {
				// 添加根
				nodes.add(generateRoot(null));

				// 全选的机构列表
				List<TenantDeptOrgDto> selected = getOrganizationService().getDepartmentsOrOrgByIds(customScope);

				// 租户下所有的depart和org
				Map<String, TenantDeptOrgDto> allMap = Maps.newHashMap();
				List<TenantDeptOrgDto> all = getDepartmentService().findDeptOrgList(tenantId, null, null);
				// 利用map,去除重复
				if (CollectionUtils.isNotEmpty(all)) {
					for (TenantDeptOrgDto tenantDeptOrgDto : all) {
						allMap.put(tenantDeptOrgDto.getId(), tenantDeptOrgDto);
					}
				}

				// 用来构造树的数据，利用map去除重复
				Map<String, TenantDeptOrgDto> treeMap = Maps.newHashMap();
				for (TenantDeptOrgDto child : selected) {
					// 添加所有的父节点（父节点设置为半选中）
					getParents(child, allMap, treeMap);

				}
				// 因为父节点中可能存在选中的节点，最后添加全选的节点，才不会被父节点中半选覆盖
				for (TenantDeptOrgDto child : selected) {
					// 添加自己
					treeMap.put(child.getId(), child);
				}

				// 树的机构数据
				List<TenantDeptOrgDto> treeList = new ArrayList<>(treeMap.values());
				if (CollectionUtils.isNotEmpty(treeList)) {

					nodes.addAll(treeList);
					permissionCompanyNodes.addAll(selected);
				}

			} else if (PermissionScopeEnum.SELF.getKey().equals(user.getPermissionScope())) {
				// 本级
				// 添加根
				nodes.add(generateRoot(null));
				// 获取自己

				// 如果该人员部署于任何depart，name就返回空的树
				if (StringUtils.isNotBlank(companyId)) {

					// 返回自己属于的节点，挂在root节点下
					TenantDeptOrgDto tenantDeptOrgDto = getCompany(companyId);
					tenantDeptOrgDto.setParentId("-1");
					if (tenantDeptOrgDto != null) {
						nodes.add(tenantDeptOrgDto);
						permissionCompanyNodes.add(tenantDeptOrgDto);
					}
				}
			} else if (PermissionScopeEnum.SELF_AND_DOWN.getKey().equals(user.getPermissionScope())) {
				// 本级及以下

				try {
					// 添加根
					nodes.add(generateRoot(null));
					// 如果该人员不属于任何depart，name就返回空的树
					List<TenantDeptOrgDto> list = Lists.newArrayList();
					if (StringUtils.isNotBlank(companyId)) {
						// 本身
						TenantDeptOrgDto self = getCompany(companyId);
						// 将本身挂在root下，所以父节点设为-1
						self.setParentId(DepartmentTree.ROOT_NODE_ID);
						list = this.findDeptOrgListByCompandyId(tenantId, companyId, null);
						list.add(self);
					}
					if (CollectionUtils.isNotEmpty(list)) {
						nodes.addAll(list);
						permissionCompanyNodes.addAll(list);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			List<WorkElementDto> workElements = findWorkElementByPermission(filterList, permissionCompanyNodes);
			nodes.addAll(workElements);

			super.reloadWidthAllParent(nodes, null);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 根据权限来获取图元
	 * 
	 * @param filterList
	 * @return
	 */
	private List<WorkElementDto> findWorkElementByPermission(List<SearchFilter> filterList, List<TenantDeptOrgDto> permissionCompanyNodes) {

		IWorkElementService workElementService = this.getWorkElementService();

		SearchFilters andSearchFilters = new SearchFilters(filterList, SearchFilters.Operator.AND);

		// 过滤机构，用or链接，包括departmentId在列表中的，或者是为""或者是null
		SearchFilters orSearchFilters = new SearchFilters();
		orSearchFilters.setOperator(SearchFilters.Operator.OR);
		if (CollectionUtils.isNotEmpty(permissionCompanyNodes)) {
			List<String> companyIds = Lists.newArrayList();
			for (TenantDeptOrgDto tenantDeptOrgDto : permissionCompanyNodes) {
				companyIds.add(tenantDeptOrgDto.getId());
			}
			orSearchFilters.add(new SearchFilter("departmentId", Operator.IN, companyIds.toArray()));
		}
		orSearchFilters.add(new SearchFilter("departmentId", Operator.NULL, null));
		orSearchFilters.add(new SearchFilter("departmentId", Operator.EQ, ""));

		andSearchFilters.add(orSearchFilters);

		List<WorkElement> workElements = workElementService.findListByFilters(andSearchFilters, null);
		// List<WorkElement> workElements =
		// workElementService.findListByProperty(map, null);
		List<WorkElementDto> workElementDTOs = Lists.newArrayList();

		try {
			workElementDTOs = workElementService.transferModelToDto(workElements);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return workElementDTOs;
	}

	private List<WorkElementDto> findAllWorkElement(List<SearchFilter> filterList) {
		IWorkElementService workElementService = this.getWorkElementService();

		// SearchFilters searchFilters = new SearchFilters(filterList,
		// SearchFilters.Operator.AND);

		List<WorkElement> workElements = workElementService.findListByFilters(new SearchFilters(filterList, SearchFilters.Operator.AND), null);
		// List<WorkElement> workElements =
		// workElementService.findListByProperty(map, null);
		List<WorkElementDto> workElementDTOs = Lists.newArrayList();

		try {
			workElementDTOs = workElementService.transferModelToDto(workElements);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return workElementDTOs;
	}

	private IWorkElementService getWorkElementService() {
		return SpringContextHolder.getBean("workElementService");
	}

	/**
	 * 获取当前子节点的父节点，将他存到map中
	 * 
	 * @param child
	 * @param allMap
	 * @param treeMap
	 * @return
	 */
	private void getParents(TenantDeptOrgDto child, Map<String, TenantDeptOrgDto> allMap, Map<String, TenantDeptOrgDto> treeMap) {
		// 获取父节点
		TenantDeptOrgDto parent = allMap.get(child.getParentId());
		// 如果父节点为null ，就返回，结束递归
		if (parent == null) {
			return;
		}
		// 父节点认为是半选中状态
		parent.setFullChecked(false);
		// 将父节点放到treeMap中
		treeMap.put(parent.getId(), parent);
		// 如果父节点==-1 ，就返回，结束递归
		if (parent.getParentId().equals(DepartmentTree.ROOT_NODE_ID)) {
			return;
		}
		// 递归
		getParents(parent, allMap, treeMap);

	}

	/**
	 * 获取用户
	 * 
	 * @param userId
	 * @return
	 */
	private CloudUserDto findByUserId(String userId) {
		return getCloudUserService().getById(userId);
	}

	private List<TenantDeptOrgDto> findDeptOrgList(String tenantId, String deptId) {
		ICloudDepartmentService service = this.getDepartmentService();

		return service.findDeptOrgList(tenantId, deptId, null);
	}

	/**
	 * 根据orgId或者是departmentid 查询所有子节点
	 * 
	 * @param tenantId
	 * @param companyId
	 * @return
	 */
	private List<TenantDeptOrgDto> findDeptOrgListByCompandyId(String tenantId, String companyId, List<Integer> beenDeletedFlags) {
		ICloudDepartmentService service = this.getDepartmentService();

		return service.findDeptOrgListByCompandyId(tenantId, companyId, beenDeletedFlags);
	}

	private TenantDeptOrgDto getCompany(String companyId) {
		ICloudOrganizationService organizationService = getOrganizationService();

		return organizationService.getDepartmentOrOrgById(companyId, null);
	}

	private ICloudDepartmentService getDepartmentService() {
		return SpringContextHolder.getBean("cloudDepartmentService");
	}

	private ICloudUserService getCloudUserService() {
		return SpringContextHolder.getBean("cloudUserService");
	}

	private ICloudStaffService getCloudStaffService() {
		return SpringContextHolder.getBean("cloudStaffService");
	}

	private ICloudOrganizationService getOrganizationService() {
		return SpringContextHolder.getBean("cloudOrganizationService");
	}
}
