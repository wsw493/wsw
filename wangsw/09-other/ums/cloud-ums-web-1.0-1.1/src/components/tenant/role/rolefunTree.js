import React from 'react';
import VtxTree from '../../vtxCommon/VtxTree/VtxTree';

const RolefunTree = ({checkedKeys_func,haveChecked,rolefunTreeData,dispatch}) => {
	if("undefined" == typeof(rolefunTreeData)){
		rolefunTreeData=[];
	}
	const treeProps = {
		data: rolefunTreeData,
		isExpandAll:'openAll',
		defaultExpandAll:true,
		checkable:true,
		checkedKeys: checkedKeys_func,
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
				if(leafNode[i].nodeType == 'Function'){
					selectedRoleKeys.push(leafNode[i].key);
				}
			}
			dispatch({type:'tenantRole/updateState',payload:{haveChecked:selectedRoleKeys,checkedKeys_func:checkedKeys}});
		}


	}

	return (
		<VtxTree {...treeProps} />
	)
}

export default RolefunTree;