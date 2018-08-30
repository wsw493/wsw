import React from 'react';
import VtxTree from '../../vtxCommon/VtxTree/VtxTree';

const OrgTree = ({dispatch,orgTree,namespace}) => {
	if(typeof(orgTree) == "undefined"){
		orgTree = [];
	}
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
			dispatch({type:namespace+'/updateState',payload:{
				searchOrgId:'',
				searchDepartmentId: '-1',
				searchOrgName:'',
				companyType: treeNode.attr.attributes.companyType
			}});
			if(treeNode.nodeType == 'org'){
				dispatch({type:namespace+'/updateState',payload:{
					searchOrgId:key,
					searchDepartmentId: treeNode.attr.attributes.departmentId,
					searchOrgName:treeNode.attr.attributes.name
				}});
			}else if(treeNode.nodeType == 'department'){
				dispatch({type:namespace+'/updateState',payload:{
					searchDepartmentId:key,
					searchOrgName:treeNode.attr.attributes.name
				}});
			}
			dispatch({type:namespace+'/initTableOpt'});
			dispatch({type:namespace+'/getTableData'});
		}
	}

	return (
		<VtxTree {...treeProps} />
	)
}

export default OrgTree;