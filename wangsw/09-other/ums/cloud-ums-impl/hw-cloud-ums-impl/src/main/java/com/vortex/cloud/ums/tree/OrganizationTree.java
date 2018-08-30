package com.vortex.cloud.ums.tree;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

import com.google.common.collect.Lists;
import com.vortex.cloud.ums.config.SpringContextHolder;
import com.vortex.cloud.ums.dataaccess.service.ICloudDepartmentService;
import com.vortex.cloud.ums.dataaccess.service.ICloudOrganizationService;
import com.vortex.cloud.ums.dataaccess.service.IWorkElementService;
import  com.vortex.cloud.vfs.data.dto.LoginReturnInfoDto;
import com.vortex.cloud.ums.dto.TenantDeptOrgDto;
import com.vortex.cloud.ums.dto.WorkElementDto;
import com.vortex.cloud.ums.enums.CompanyTypeEnum;
import com.vortex.cloud.ums.model.CloudDepartment;
import com.vortex.cloud.ums.model.CloudOrganization;
import com.vortex.cloud.ums.model.WorkElement;
import com.vortex.cloud.ums.support.IconConstant;
import com.vortex.cloud.ums.util.tree.CommonTree;
import com.vortex.cloud.ums.util.tree.CommonTreeNode;
import com.vortex.cloud.ums.util.utils.ObjectUtil;
import com.vortex.cloud.vfs.common.lang.StringUtil;
import com.vortex.cloud.vfs.data.support.SearchFilter;
import com.vortex.cloud.vfs.data.support.SearchFilters;



/**
 * 云平台租户部门下的机构树
 * 
 * @author LiShijun
 * 
 */
public class OrganizationTree extends CommonTree {
	private static OrganizationTree instance;

	private OrganizationTree() {
	}

	public static OrganizationTree getInstance() {
		synchronized (OrganizationTree.class) {
			if (null == instance) {
				instance = new OrganizationTree();
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
			node.setType(CompanyTypeEnum.ORG.getKey());	// CloudOrganization层级的
			node.setBindData(ObjectUtil.attributesToMap(dd));
		} else if (obj instanceof TenantDeptOrgDto) {
			TenantDeptOrgDto dd = (TenantDeptOrgDto) obj;
			node.setNodeId(StringUtil.clean(dd.getId()));
			node.setParentId(StringUtil.clean(dd.getParentId()));
			node.setIcon(IconConstant.TreeIcon.ICON_DEPARTMENT);
			node.setText(dd.getName());
			node.setType(dd.getCompanyType());
			node.setBindData(ObjectUtil.attributesToMap(dd));
		}  else if(obj instanceof WorkElementDto){
			WorkElementDto dto = (WorkElementDto) obj;
			node.setNodeId(StringUtil.clean(dto.getId()));
			node.setParentId(StringUtil.clean(dto.getDepartmentId()));

			switch (dto.getShape()){
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
	
	/**
	 * 机构树
	 * 
	 * @param rootDeptId
	 * @param searchFilter
	 */
	public void reloadOrganizationTree(String rootDeptId, Iterable<SearchFilter> searchFilter) {
		List<Object> nodes = new ArrayList<Object>();
		try {
			// 添加根
			nodes.add(generateRoot(rootDeptId));
			
			List<CloudOrganization> list = this.findOrgListByFilter(searchFilter);

			if(StringUtils.isNotBlank(rootDeptId)) {
				for (CloudOrganization entity : list) {
					if(entity.getDepartmentId().equals(rootDeptId)) {
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

	/**
	 * 部门树(单位 + 单位下的机构 + 图元)
	 * @param info
	 * @param filterList
	 */
	public void reloadOrganizationElementTree(LoginReturnInfoDto info, List<SearchFilter> filterList) {
		List<Object> nodes = new ArrayList<Object>();
		try {
			// 添加根
			nodes.add(generateRoot(""));

			List<TenantDeptOrgDto> list = this.findDeptOrgList(info.getTenantId(), "");
			nodes.addAll(list);

			List<WorkElementDto> workElements = findAllWorkElement(filterList);
			nodes.addAll(workElements);

			super.reloadWidthAllParent(nodes, null);

//			deleteEmptyNode();
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
	 * 部门树(单位 + 单位下的机构)
	 * @param tenantId 
	 * 
	 * @param rootDeptId
	 */
	public void reloadDeptOrgTree(String tenantId, String rootDeptId) {
		List<Object> nodes = new ArrayList<Object>();
		try {
			// 添加根
			nodes.add(generateRoot(rootDeptId));
			
			List<TenantDeptOrgDto> list = this.findDeptOrgList(tenantId, rootDeptId);

			if(StringUtils.isNotBlank(rootDeptId)) {
				for (TenantDeptOrgDto entity : list) {
					if(entity.getDepartmentId().equals(rootDeptId)) {
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

	/**
	 * 如果该部门没有路段，则删除该节点
	 */
	private void deleteEmptyNode(){
		CommonTreeNode root = this.getRootNode();
		if( root.getAllChildren() != null && root.getAllChildren().size() > 0 ) {
			List<CommonTreeNode> emptyList = Lists.newArrayList();
			for( CommonTreeNode node : root.getAllChildren() ) {
				if( "department".equals( node.getType() )){
					if(node.getAllChildren() == null || node.getAllChildren().size() == 0){
						emptyList.add(node);
					}
				}
			}

			for( CommonTreeNode commonTreeNode : emptyList ) {
				try{
					this.deleteTreeNode( commonTreeNode.getNodeId() );
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	private List<TenantDeptOrgDto> findDeptOrgList(String tenantId, String deptId) {
		ICloudDepartmentService service = this.getDepartmentService();
		
		return service.findDeptOrgList(tenantId, deptId, null);
	}

	private List<WorkElementDto> findAllWorkElement(List<SearchFilter> filterList) {
		IWorkElementService workElementService = this.getWorkElementService();

//		SearchFilters searchFilters = new SearchFilters(filterList, SearchFilters.Operator.AND);

		List<WorkElement> workElements = workElementService.findListByFilters(new SearchFilters(filterList, SearchFilters.Operator.AND), null);
//		List<WorkElement> workElements = workElementService.findListByProperty(map, null);
		List<WorkElementDto> workElementDTOs = Lists.newArrayList();


		try {
			workElementDTOs = workElementService.transferModelToDto(workElements);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return workElementDTOs;
	}
	
	private ICloudDepartmentService getDepartmentService() {
		return SpringContextHolder.getBean("cloudDepartmentService");
	}

	private IWorkElementService getWorkElementService() {
		return SpringContextHolder.getBean("workElementService");
	}

}
