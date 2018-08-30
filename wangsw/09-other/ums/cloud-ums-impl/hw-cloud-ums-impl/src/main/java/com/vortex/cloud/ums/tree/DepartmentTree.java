package com.vortex.cloud.ums.tree;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.vortex.cloud.ums.config.SpringContextHolder;
import com.vortex.cloud.ums.dataaccess.service.ICloudDepartmentService;
import com.vortex.cloud.ums.dataaccess.service.ITenantParamSettingService;
import com.vortex.cloud.ums.dataaccess.service.IWorkElementService;
import  com.vortex.cloud.vfs.data.dto.LoginReturnInfoDto;
import com.vortex.cloud.ums.dto.TenantDeptOrgDto;
import com.vortex.cloud.ums.dto.WorkElementDto;
import com.vortex.cloud.ums.model.TenantPramSetting;
import com.vortex.cloud.ums.model.WorkElement;
import com.vortex.cloud.ums.support.IconConstant;
import com.vortex.cloud.ums.support.ManagementConstant;
import com.vortex.cloud.ums.util.tree.CommonTree;
import com.vortex.cloud.ums.util.tree.CommonTreeNode;
import com.vortex.cloud.ums.util.utils.ObjectUtil;
import com.vortex.cloud.vfs.common.lang.StringUtil;



/**
 * 云平台租户部门下的机构树
 * 
 * @author LiShijun
 * 
 */
public class DepartmentTree extends CommonTree {
	private static DepartmentTree instance;
	public static final String ROOT_NODE_ID = "-1";
	public static final String ROOT_NODE_TEXT = "所有公司";

	private DepartmentTree() {
	}

	public static DepartmentTree getInstance() {
		synchronized (DepartmentTree.class) {
			if (null == instance) {
				instance = new DepartmentTree();
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
		} else if (obj instanceof TenantPramSetting) {
			TenantPramSetting dd = (TenantPramSetting) obj;
			node.setNodeId(StringUtil.clean(dd.getParmCode()));
			node.setParentId(StringUtil.clean("-1"));
			node.setIcon(IconConstant.TreeIcon.ICON_DEPARTMENT);
			node.setText(dd.getParmName());
			node.setType("departmentCategory"); // 普通的组织机构
			node.setBindData(ObjectUtil.attributesToMap(dd));
		} else if (obj instanceof TenantDeptOrgDto) {
			TenantDeptOrgDto dd = (TenantDeptOrgDto) obj;
			node.setNodeId(StringUtil.clean(dd.getId()));
			node.setParentId(StringUtil.clean(dd.getType()));
			node.setIcon(IconConstant.TreeIcon.ICON_DEPARTMENT);
			node.setText(dd.getName());
			node.setType("department");
			node.setBindData(ObjectUtil.attributesToMap(dd));
		} else if(obj instanceof WorkElementDto){
            WorkElementDto dto = (WorkElementDto) obj;
            node.setNodeId(StringUtil.clean(dto.getId()));
            node.setParentId(StringUtil.clean(dto.getDepartmentId()));

			switch (dto.getShape()){
				case "point":
					node.setIcon(IconConstant.TreeIcon.ICON_POINT);
					break;
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

	private Object generateRoot() {

		CommonTreeNode node = new CommonTreeNode();
		node.setNodeId(DepartmentTree.ROOT_NODE_ID);
		node.setText(DepartmentTree.ROOT_NODE_TEXT);
		node.setParentId("0");
		node.setType("Root");

		return node;
	}

	/**
	 * 部门树(单位 + 单位下的机构)
	 * 
	 * @param tenantId
	 * 
	 * @param rootDeptId
	 */
	public void reloadDeptTree(String tenantId, String rootDeptId) {
		List<Object> nodes = new ArrayList<Object>();
		try {
			// 添加根
			// 添加根
			nodes.add(generateRoot());
			List<TenantPramSetting> types = findDepartmentTypes(tenantId);
			List<TenantDeptOrgDto> list = this.findDeptOrgList(tenantId, rootDeptId);
			nodes.addAll(types);
			nodes.addAll(list);
			super.reload(nodes, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

    /**
     * 部门树(单位 + 单位下的机构 + 图元)
     * @param info
     * @param workEleMap
     */
	public void reloadDeptElementTree(LoginReturnInfoDto info, Map<String, Object> workEleMap) {
        List<Object> nodes = new ArrayList<Object>();

        // 添加根
        nodes.add(generateRoot());

        List<TenantPramSetting> types = findDepartmentTypes(info.getTenantId());
        List<TenantDeptOrgDto> list = this.findDeptOrgList(info.getTenantId(), null);
        nodes.addAll(types);
        nodes.addAll(list);

        List<WorkElementDto> workElements = findAllWorkElement(workEleMap);
        nodes.addAll(workElements);

        super.reloadWidthAllParent(nodes, null);

		deleteEmptyNode();
    }

	/**
	 * 如果该部门没有路段，则删除该节点
	 */
	private void deleteEmptyNode(){
		CommonTreeNode root = this.getRootNode();
		if( root.getAllChildren() != null && root.getAllChildren().size() > 0 ) {
			List<CommonTreeNode> emptyList = Lists.newArrayList();
			Integer count = 0;
			for( CommonTreeNode node : root.getAllChildren() ) {
				if( "departmentCategory".equals( node.getType() ) && node.getAllChildren() != null && node.getAllChildren().size() > 0){
					List<CommonTreeNode> childEmptyList = Lists.newArrayList();

					Integer childCount = 0;
					for(CommonTreeNode n : node.getAllChildren()){
						if("department".equals(n.getType())){
							childCount = getCount(n);
							count += childCount;
							if( childCount == 0 ){
								childEmptyList.add( n );
							}
						}
					}

					if(count == 0){
						emptyList.add(node);
					} else {
						emptyList.addAll(childEmptyList);
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

	/**
	 * 获取数量
	 */
	private Integer getCount(CommonTreeNode node) {
		Integer total = 0;
		if( node.getAllChildren() != null && node.getAllChildren().size() > 0 ) {
			for( CommonTreeNode n : node.getAllChildren() ) {
				if ( "WorkElement".equals(n.getType()) ) {
					total++;
				}
			}
		}
		return total;
	}

	private List<TenantDeptOrgDto> findDeptOrgList(String tenantId, String deptId) {
		ICloudDepartmentService service = this.getDepartmentService();
		return service.findDeptList(tenantId, deptId);
	}

    private List<WorkElementDto> findAllWorkElement(Map<String, Object> map) {
        IWorkElementService workElementService = this.getWorkElementService();
        List<WorkElement> workElements = workElementService.findListByProperty(map, null);
        List<WorkElementDto> workElementDTOs = Lists.newArrayList();


        try {
            workElementDTOs = workElementService.transferModelToDto(workElements);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return workElementDTOs;
    }

	/**
	 * 根据tenantId查找部门类型
	 * 
	 * @param tenantId
	 * @return
	 */
	private List<TenantPramSetting> findDepartmentTypes(String tenantId) {
		ITenantParamSettingService service = this.getTenantParamSettingService();
		return service.findListByParamTypeCode(tenantId, ManagementConstant.getPropertyValue("DEPARTMENT_TYPE"));
	}

    private ICloudDepartmentService getDepartmentService() {
        return SpringContextHolder.getBean("cloudDepartmentService");
    }

	private ITenantParamSettingService getTenantParamSettingService() {
		return SpringContextHolder.getBean("tenantParamSettingService");
	}

    private IWorkElementService getWorkElementService() {
        return SpringContextHolder.getBean("workElementService");
    }
}
