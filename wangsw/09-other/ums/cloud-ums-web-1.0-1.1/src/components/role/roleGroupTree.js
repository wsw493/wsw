import React from 'react';
import VtxTree from '../vtxCommon/VtxTree/VtxTree';

const RoleGroupTree = ({roleGroupTree,dispatch}) => {
	//console.log(roleGroupTree);
	const treeProps = {
		data: roleGroupTree,
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
			dispatch({type:'role/updateState',payload:{searchParentId:key}});
			dispatch({type:'role/initTableOpt'});
            dispatch({type:'role/getTableData'});
		}
	}

	return (
		<VtxTree {...treeProps} />
	)
}

export default RoleGroupTree;