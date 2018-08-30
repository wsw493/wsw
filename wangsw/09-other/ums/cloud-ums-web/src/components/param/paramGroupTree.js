import React from 'react';
import VtxTree from '../vtxCommon/VtxTree/VtxTree';

const ParamGroupTree = ({paramGroupTreeData,dispatch}) => {
	if("undefined" == paramGroupTreeData){
		paramGroupTreeData = [];
	}
	const treeProps = {
		data: paramGroupTreeData,
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
			dispatch({type:'param/updateState',payload:{searchParentId:key}});
			dispatch({type:'param/initTableArgs'});
			dispatch({type:'param/getParamTypeTableData'});
		}
	}

	return (
		<VtxTree {...treeProps} />
	)
}

export default ParamGroupTree;