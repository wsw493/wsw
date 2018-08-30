import React from 'react';
import VtxTree from '../vtxCommon/VtxTree/VtxTree';
import {VtxZtree} from 'vtx-ui';

const MenuFunctionTree = ({checkedKeys_func,haveChecked,menuFunctionTreeData,dispatch}) => {
	if("undefined" == typeof(menuFunctionTreeData)){
		menuFunctionTreeData=[];
	}
	const treeProps = {
		data: menuFunctionTreeData,
		isExpandAll:'openAll',
		defaultExpandAll:true,
		checkable:true,
        checkedKeys: checkedKeys_func,
        customCfg:{
            check: {
                enable: true,
                chkStyle: "checkbox",
                chkboxType: { "Y": "ps", "N": "s" }
            }
        },
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
			for(var i=0;i<treeNode.length;i++){
				if(treeNode[i].nodeType == 'Function'||treeNode[i].nodeType == 'menuTT'){
					selectedRoleKeys.push(treeNode[i].key);
				}
			}
			dispatch({type:'systemRole/updateState',payload:{haveChecked:selectedRoleKeys,checkedKeys_func:checkedKeys}});
		}


	}

	return (
		<VtxZtree {...treeProps} />
	)
}

export default MenuFunctionTree;