import React from 'react';
import VtxTree from '../vtxCommon/VtxTree/VtxTree';

const OrgTree = ({orgTree,dispatch}) => {
	const treeProps = {
		data: orgTree,
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
			dispatch({type:'org/updateState',payload:{searchParentId:key,searchParentName:treeNode.name}});
			dispatch({type:'org/initTableOpt'});
            dispatch({type:'org/getTableData'});
		}
	}

	return (
		<VtxTree {...treeProps} />
	)
}

export default OrgTree;