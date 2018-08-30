import React from 'react';
import VtxTree from '../vtxCommon/VtxTree/VtxTree';

const SourceMenuTree = ({sourceMenuTreeData,dispatch,checkedKeys,selectedRoleKeys}) => {
	if(typeof(sourceMenuTreeData) == "undefined"){
		sourceMenuTreeData = [];
		
	}
	const treeProps = {
		data: sourceMenuTreeData,
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
				if(leafNode[i].nodeType != 'Root' && leafNode[i].attr.attributes.childSerialNumer == 0){
					selectedRoleKeys.push(leafNode[i].key);
				}
			}
			dispatch({type:"copyMenuResource/updateState",payload:{selectedRoleKeys,checkedKeys}});
		}
	}

	return (
		<VtxTree {...treeProps} />
	)
}

export default SourceMenuTree;