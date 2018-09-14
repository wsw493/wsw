import React from 'react';
import VtxTree from '../vtxCommon/VtxTree/VtxTree';

const ParamGroupTree = ({paramGroupTree,dispatch}) => {
	const treeProps = {
		data: paramGroupTree,
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
			dispatch({type:'paramGroup/updateState',payload:{searchParentId:key}});
			dispatch({type:'paramGroup/initTableOpt'});
            dispatch({type:'paramGroup/getTableData'});
		}
	}

	return (
		<VtxTree {...treeProps} />
	)
}

export default ParamGroupTree;