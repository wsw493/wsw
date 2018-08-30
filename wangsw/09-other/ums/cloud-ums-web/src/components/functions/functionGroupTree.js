import React from 'react';
import VtxTree from '../vtxCommon/VtxTree/VtxTree';

const FunctionGroupTree = ({functionGroupTree,dispatch}) => {
	const treeProps = {
		data: functionGroupTree,
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
			dispatch({type:'functions/updateState',payload:{searchParentId:key}});
			dispatch({type:'functions/initTableOpt'});
            dispatch({type:'functions/getTableData'});
		}
	}

	return (
		<VtxTree {...treeProps} />
	)
}

export default FunctionGroupTree;