package com.vortex.cloud.ums.tree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.vortex.cloud.ums.config.SpringContextHolder;
import com.vortex.cloud.ums.dataaccess.service.ICloudDepartmentService;
import com.vortex.cloud.ums.dataaccess.service.ICloudOrganizationService;
import com.vortex.cloud.ums.dataaccess.service.ICloudStaffService;
import com.vortex.cloud.ums.dataaccess.service.ICloudUserService;
import com.vortex.cloud.ums.dto.CloudUserDto;
import com.vortex.cloud.ums.dto.TenantDeptOrgDto;
import com.vortex.cloud.ums.enums.CloudDepartmentTypeEnum;
import com.vortex.cloud.ums.enums.PermissionScopeEnum;
import com.vortex.cloud.ums.model.CloudDepartment;
import com.vortex.cloud.ums.model.CloudOrganization;
import com.vortex.cloud.ums.model.CloudStaff;
import com.vortex.cloud.ums.model.CloudUser;
import com.vortex.cloud.ums.util.tree.CommonTree;
import com.vortex.cloud.ums.util.tree.CommonTreeNode;
import com.vortex.cloud.ums.util.utils.ObjectUtil;
import com.vortex.cloud.vfs.common.lang.StringUtil;
import com.vortex.cloud.vfs.data.support.SearchFilter;
import com.vortex.cloud.vfs.data.support.SearchFilter.Operator;
import com.vortex.cloud.vfs.data.support.SearchFilters;

/**
 * 机构人员树（所谓带权限）
 * 
 * @author LiShijun
 * 
 */
public class StaffOrganizationTreeWithPermission extends CommonTree {
	private static StaffOrganizationTreeWithPermission instance;
	private static final String orderByOrderIndex = "1";
	private static final String orderByPY = "2";

	private StaffOrganizationTreeWithPermission() {
	}

	public static StaffOrganizationTreeWithPermission getInstance() {
		synchronized (StaffOrganizationTreeWithPermission.class) {
			if (null == instance) {
				instance = new StaffOrganizationTreeWithPermission();
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
	 * 生成root
	 * 
	 * @param rootDeptId
	 * @return
	 */
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
	 * 部门树(单位 + 单位下的机构+人员)
	 * 
	 * @param tenantId
	 * 
	 * @param map
	 */
	public void reloadDeptOrgStaffTree(Map<String, String> param) {
		String userId = param.get("userId");
		String tenantId = param.get("tenantId");
		String isControlPermission = param.get("isControlPermission");
		String order = param.get("order");// 1.orderIndex排序，2.全拼排序
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

			nodes.addAll(list);
			nodes.addAll(getStaffList(list, order));
		} else if (PermissionScopeEnum.NONE.getKey().equals(user.getPermissionScope())) {
			// 没有权限
			nodes.add(generateRoot(null));

		} else if (PermissionScopeEnum.CUSTOM.getKey().equals(user.getPermissionScope())) {
			// 添加根
			nodes.add(generateRoot(null));
			if (ArrayUtils.isNotEmpty(customScope)) {
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
				if (CollectionUtils.isNotEmpty(getStaffList(treeList, order))) {
					nodes.addAll(getStaffList(treeList, order));
				}
			}
		} else if (PermissionScopeEnum.SELF.getKey().equals(user.getPermissionScope())) {
			// 本级
			// 添加根
			nodes.add(generateRoot(null));
			// 获取自己

			// 如果该人员不署于任何depart，name就返回空的树
			if (StringUtils.isNotBlank(companyId)) {

				// 返回自己属于的节点，挂在root节点下
				TenantDeptOrgDto tenantDeptOrgDto = getCompany(companyId);
				tenantDeptOrgDto.setParentId("-1");
				if (tenantDeptOrgDto != null) {
					nodes.add(tenantDeptOrgDto);
					if (CollectionUtils.isNotEmpty(getStaffList(Arrays.asList(tenantDeptOrgDto), order))) {
						nodes.addAll(getStaffList(Arrays.asList(tenantDeptOrgDto), order));
					}

				}
			}
		} else if (PermissionScopeEnum.SELF_AND_DOWN.getKey().equals(user.getPermissionScope())) {
			// 本级及以下

			try {
				// 添加根
				nodes.add(generateRoot(null));
				// 如果该人员部署于任何depart，name就返回空的树
				List<TenantDeptOrgDto> list = Lists.newArrayList();
				// 全部部门节点
				List<TenantDeptOrgDto> companys = Lists.newArrayList();

				if (StringUtils.isNotBlank(companyId)) {
					// 本身
					TenantDeptOrgDto self = getCompany(companyId);
					// 将本身挂在root下，所以父节点设为-1
					self.setParentId(DepartmentTree.ROOT_NODE_ID);
					// 下面节点
					list = this.findDeptOrgListByCompandyId(tenantId, companyId);
					if (null != self) {
						companys.add(self);
					}
					if (CollectionUtils.isNotEmpty(list)) {
						companys.addAll(list);
					}

				}
				if (CollectionUtils.isNotEmpty(companys)) {
					nodes.addAll(companys);
				}
				if (CollectionUtils.isNotEmpty(getStaffList(companys, order))) {
					nodes.addAll(getStaffList(companys, order));
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		super.reload(nodes, null);

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
	 * 根据部门机构列表查询人员列表
	 * 
	 * @param list
	 * @return
	 */
	private List<CloudStaff> getStaffList(List<TenantDeptOrgDto> list, String order) {
		if (CollectionUtils.isEmpty(list)) {
			return null;
		}
		List<String> companyIds = Lists.newArrayList();
		Sort sort = null;
		// 为空获取按照传1，按照orderIndex
		if (StringUtils.isEmpty(order) || orderByOrderIndex.equals(order)) {
			sort = new Sort(Direction.ASC, "orderIndex");
		} else if (orderByPY.equals(order)) {
			sort = new Sort(Direction.ASC, "nameInitial");
		}
		for (TenantDeptOrgDto tenantDeptOrgDto : list) {
			companyIds.add(tenantDeptOrgDto.getId());
		}
		List<SearchFilter> searchFilterCollection = Lists.newArrayList();
		searchFilterCollection.add(new SearchFilter("departmentId", Operator.IN, companyIds.toArray()));
		searchFilterCollection.add(new SearchFilter("orgId", Operator.IN, companyIds.toArray()));
		SearchFilters searchFilters = new SearchFilters(searchFilterCollection, SearchFilters.Operator.OR);
		return getCloudStaffService().findListByFilters(searchFilters, sort);

	}

	/**
	 * 根据部门机构列表查询人员列表
	 * 
	 * @param list
	 * @return
	 */
	private List<CloudStaff> getStaffList(List<TenantDeptOrgDto> list) {
		return getStaffList(list, null);
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

	private ICloudUserService getCloudUserService() {
		return SpringContextHolder.getBean("cloudUserService");
	}

	/**
	 * 根据orgId或者是departmentid 查询所有子节点
	 * 
	 * @param tenantId
	 * @param companyId
	 * @return
	 */
	private List<TenantDeptOrgDto> findDeptOrgListByCompandyId(String tenantId, String companyId) {
		ICloudDepartmentService service = this.getDepartmentService();

		return service.findDeptOrgListByCompandyId(tenantId, companyId, null);
	}

	private List<TenantDeptOrgDto> findDeptOrgList(String tenantId, String deptId) {
		ICloudDepartmentService service = this.getDepartmentService();

		return service.findDeptOrgList(tenantId, deptId, null);
	}

	private ICloudDepartmentService getDepartmentService() {
		return SpringContextHolder.getBean("cloudDepartmentService");
	}

	private TenantDeptOrgDto getCompany(String companyId) {
		ICloudOrganizationService organizationService = getOrganizationService();

		return organizationService.getDepartmentOrOrgById(companyId, null);
	}

	private ICloudStaffService getCloudStaffService() {
		return SpringContextHolder.getBean("cloudStaffService");
	}

	private ICloudOrganizationService getOrganizationService() {
		return SpringContextHolder.getBean("cloudOrganizationService");
	}

}
