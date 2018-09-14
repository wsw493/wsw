import React from 'react';
import VtxTree from '../vtxCommon/VtxTree/VtxTree';

const DivisionTree = ({dispatch,treeData,expandedkeys}) => {
	if(null == treeData){
		treeData = [];
	}
	const treeProps = {
		data: treeData,
		isExpandAll:'other',
		expandedKeys:expandedkeys,
		isShowSearchInput:true,
		render: (onChange, onSubmit)=>{
			return (
				<div>
					<input type="text" onChange={onChange} />
					<span onClick={onSubmit}>搜索</span>
				</div>
			);
		},
		onExpand(obj){
			const {key} = obj;
			let keys = [...expandedkeys];
			if(keys.indexOf(key) == -1){
				keys.push(key);
			}else{
				keys.splice(keys.indexOf(key),1);
			}
			dispatch({
				type: 'xzqh/updateState',
				payload: {
					expandedkeys: keys
				}
			})
		},
		onClick: ({key, selectRows, treeNode})=>{
			dispatch({type: 'xzqh/updateState',payload:{searchTreeId:key,treeName:treeNode.name}});
			dispatch({type: 'xzqh/initTableArgs'});
			dispatch({type: 'xzqh/getTableData'});
		},
		onLoadData:({key,treeNode,isExpand,resolve})=>{
			expandedkeys.push(key);
			dispatch({type:'xzqh/updateState',payload:{treeNodeId:key,expandedkeys}});
			return dispatch({type:'xzqh/getTreeData',payload:{resolve:resolve}});
		}
	}
	return (
		<VtxTree {...treeProps} />
	)
}

export default DivisionTree;