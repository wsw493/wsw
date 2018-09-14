import React from 'react';
import VtxTree from '../vtxCommon/VtxTree/VtxTree';

const MenuTree = ({menuTree,dispatch}) => {
	//console.log(menuTree);
	const treeProps = {
		data: menuTree,
		isExpandAll:'openAll',
		defaultExpandAll:true,
		isShowSearchInput:true,
		render: (onChange, onSubmit)=>{
			return (
				<div>
					<input type="text" onChange={onChange} />
					<span onClick={onSubmit}>搜索</span>
				</div>
			);
		},
		onClick: ({key, selectRows, treeNode})=>{
			dispatch({type:'menu/updateState',payload:{
				searchParentId:key,
				canAddNewMenu:!(treeNode.attr.attributes && treeNode.attr.attributes.functionId && treeNode.attr.attributes.functionId!="")
			}});
			dispatch({type:'menu/initTableOpt'});
            dispatch({type:'menu/getTableData'});
		}
	}

	return (
		<VtxTree {...treeProps} />
	)
}

export default MenuTree;