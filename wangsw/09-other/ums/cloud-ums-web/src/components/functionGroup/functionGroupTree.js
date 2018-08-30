import React from 'react';
import VtxTree from '../vtxCommon/VtxTree/VtxTree';

const FunctionGroupTree = ({functionGroupTree,searchParentId,dispatch}) => {
	//console.log(functionGroupTree);
	const treeProps = {
		data: functionGroupTree,
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
			dispatch({type:'functionGroup/updateState',payload:{searchParentId:key}});
			dispatch({type:'functionGroup/initTableOpt'});
            dispatch({type:'functionGroup/getTableData'});
		}
	}

	return (
		<VtxTree {...treeProps} />
	)
}

export default FunctionGroupTree;