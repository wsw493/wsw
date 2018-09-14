import React from 'react';
import VtxTree from '../../vtxCommon/VtxTree/VtxTree';

const RoleTree = ({roleTreeData,dispatch,checkedKeys,selectedRoleKeys}) => {
	if(typeof(roleTreeData) == "undefined"){
		roleTreeData = [];
		
	}
	const treeProps = {
		data: roleTreeData,
		isExpandAll:'openAll',
		defaultExpandAll:true,
		checkable:true,
		checkedKeys: checkedKeys,
		render: (onChange, onSubmit)=>{
			return (
				<div>
					<input type="text" onChange={onChange} />
					<span onClick={onSubmit}>搜索</span>
				</div>
			);
		},
		onCheck: ({key,isChecked,checkedKeys,treeNode,leafNode})=>{
			var selectedRoleKeys = [];
			for(var i=0;i<leafNode.length;i++){
				if(leafNode[i].nodeType == 'Role'){
					selectedRoleKeys.push(leafNode[i].key);
				}
			}
			dispatch({type:"systemStaff/updateState",payload:{selectedRoleKeys,checkedKeys}});
		}
	}

	return (
		<VtxTree {...treeProps} />
	)
}

export default RoleTree;