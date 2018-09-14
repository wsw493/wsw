import React from 'react';
import VtxTree from '../../vtxCommon/VtxTree/VtxTree';

const TenantRoleTree = ({roleTreeData_tenant,dispatch,checkedKeys_tenant,selectedRoleKeys_tenant}) => {
	if(typeof(roleTreeData_tenant) == "undefined"){
		roleTreeData_tenant = [];
	}
	console.log(roleTreeData_tenant)
	const treeProps = {
		data: roleTreeData_tenant,
		isExpandAll:'openAll',
		defaultExpandAll:true,
		checkable:true,
		checkedKeys_tenant: checkedKeys_tenant,
		render: (onChange, onSubmit)=>{
			return (
				<div>
					<input type="text" onChange={onChange} />
					<span onClick={onSubmit}>搜索</span>
				</div>
			);
		},
		onCheck: ({key,isChecked,checkedKeys_tenant,treeNode,leafNode})=>{
			var selectedRoleKeys_tenant = [];
			for(var i=0;i<leafNode.length;i++){
				if(leafNode[i].nodeType == 'Role'){
					selectedRoleKeys_tenant.push(leafNode[i].key);
				}
			}
			dispatch({type:"systemStaff/updateState",payload:{selectedRoleKeys_tenant,checkedKeys_tenant}});
		}
	}

	return (
		<VtxTree {...treeProps} />
	)
}

export default TenantRoleTree;