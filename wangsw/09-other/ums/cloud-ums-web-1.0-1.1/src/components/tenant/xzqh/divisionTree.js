import React from 'react';
import VtxTree from '../../vtxCommon/VtxTree/VtxTree';

const DivisionTree = ({treeData,dispatch,searchTreeId}) => {
	const treeProps = {
		data: treeData,
		isExpandAll:'openAll',
		defaultExpandAll:true,
		isShowSearchInput:true,
		selectedKeys:[searchTreeId],
		render: (onChange, onSubmit)=>{
			return (
				<div>
					<input type="text" onChange={onChange} />
					<span onClick={onSubmit}>搜索</span>
				</div>
			);
		},
		onClick: ({key, selectRows, treeNode})=>{
			//console.log(treeNode);
			dispatch({type:'tenantxzqu/updateState',payload:{searchTreeId:key,parentName:treeNode.name}});
			dispatch({type:'tenantxzqu/initTableOpt'});
            dispatch({type:'tenantxzqu/getTableData'});
		}
	}

	return (
		<VtxTree {...treeProps} />
	)
}

export default DivisionTree;