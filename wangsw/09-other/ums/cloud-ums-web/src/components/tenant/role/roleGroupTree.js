import React from 'react';
import VtxTree from '../../vtxCommon/VtxTree/VtxTree';

const RoleGroupTree = ({roleGroupTree,dispatch}) => {
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
			dispatch({type:'tenantRole/updateState',payload:{searchParentId:key}});
			dispatch({type:'tenantRole/initTableOpt'});
            dispatch({type:'tenantRole/getTableData'});
		}
	}

	return (
		<VtxTree {...treeProps} />
	)
}

export default RoleGroupTree;