import React from 'react';
import VtxTree from '../vtxCommon/VtxTree/VtxTree';

const RoleGroupTree = ({roleGroupTree,searchParentId,dispatch}) => {
	//console.log(roleGroupTree);
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
            let parentId = -1,
            systemId = -1;
            if(treeNode.nodeType == 'Root'){
                parentId = -1;
                systemId = -1;
            }else if(treeNode.nodeType == 'System'){
                parentId = -1;
                systemId = key;
            }else if(treeNode.nodeType == 'RoleGroup'){
                parentId = key;
                systemId = treeNode.attr.attributes.systemId;
            }
			dispatch({type:'systemRoleGroup/updateState',payload:{searchParentId:parentId,systemId}});
			dispatch({type:'systemRoleGroup/initTableOpt'});
            dispatch({type:'systemRoleGroup/getTableData'});
		}
	}

	return (
		<VtxTree {...treeProps} />
	)
}

export default RoleGroupTree;