import React from 'react';
import VtxTree from '../vtxCommon/VtxTree/VtxTree';

const RoleGroupTree = ({roleGroupTree,searchParentId,dispatch}) => {
	//console.log(roleGroupTree);
	const treeProps = {
		data: roleGroupTree,
		selectedKeys:[searchParentId],
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
			dispatch({type:'roleGroup/updateState',payload:{searchParentId:key}});
			dispatch({type:'roleGroup/initTableOpt'});
            dispatch({type:'roleGroup/getTableData'});
		}
	}

	return (
		<VtxTree {...treeProps} />
	)
}

export default RoleGroupTree;