import React from 'react';
import VtxTree from '../../vtxCommon/VtxTree/VtxTree';

const RoleGroupTree = ({roleGroupTree,searchParentId,dispatch}) => {
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
			dispatch({type:'tenantRoleGroup/updateState',payload:{searchParentId:key}});
			dispatch({type:'tenantRoleGroup/initTableOpt'});
            dispatch({type:'tenantRoleGroup/getTableData'});
		}
	}

	return (
		<VtxTree {...treeProps} />
	)
}

export default RoleGroupTree;