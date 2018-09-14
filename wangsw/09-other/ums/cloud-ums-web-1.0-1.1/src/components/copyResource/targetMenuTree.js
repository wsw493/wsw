import React from 'react';
import VtxTree from '../vtxCommon/VtxTree/VtxTree';

const TargetMenuTree = ({targetMenuTreeData,dispatch}) => {
	if(typeof(targetMenuTreeData) == "undefined"){
		targetMenuTreeData = [];
	}
	const treeProps = {
		data: targetMenuTreeData,
		isExpandAll:'openAll',
		defaultExpandAll:true,
		render: (onChange, onSubmit)=>{
			return (
				<div>
					<input type="text" onChange={onChange} />
					<span onClick={onSubmit}>搜索</span>
				</div>
			);
		}
	}

	return (
		<VtxTree {...treeProps} />
	)
}

export default TargetMenuTree;