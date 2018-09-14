import React from 'react';
import {connect} from 'dva';
import {message} from 'antd';
import {
    handleTableData,
    handleTreeData,
    handleRestResultData,
    handleEditItem,
    getBaicPostData,
    formValidation
} from '../../utils/toolFunctions';
import {
	getOrgTable,saveOrg,updateOrg,validateOrgForAdd,deleteOrg,validateOrgForUpdate,getDivisionTenantTree,
	getOrgTree,getOrgDtoById,getFunctionPermissionMap } from '../../services/remoteData';
export default{
	namespace:'org',
	state:{
        tableData:[],
        treeData:[],
	    selectedRowKeys:[],
	    tableLoading:true,
	    currentPageIndex: 1,
	    pageSize: 10, 
	    totalItems: 0,
	    modal1Visible: false,
        mapCenter: [116.483917,39.929221],
        orgName:'',	
        searchParentId:'',
        searchParentName:'',
        departmentId:'',	
	    newItem:{
	        visible:false,
			checkState:false,
			id:'',
            tenantId:'',
            parentId:'',
            parentName:'',
	        orgCode:'',
	        orgName:'',
	        head: '',
	        headMobile:'',
	        address:'',
	        email:'',
			description:'',
			orgType:'',
			lngLats:'',
			orderIndex:'',
			divisionId:''
	    },
	    editItem:{
	        visible:false,
	        checkState:true
	    },
	    viewItem:{
	        visible:false
	    },
		saving: false,
		codeUnique: false,
		defaultCloumns:['id','tenantId','orgTypeText','orgCode','orgName','head','parentId',
		'headMobile','address','email','description','orgType','lngLats','departmentId','divisionId','orderIndex'],
		functionCodes: ['CF_MANAGE_TENANT_ORG_LIST','CF_MANAGE_TENANT_ORG_ADD','CF_MANAGE_TENANT_ORG_UPDATE',
		'CF_MANAGE_TENANT_ORG_VIEW','CF_MANAGE_TENANT_ORG_DELETE'],
		functionCodesMap: {},
		divisionTree:[]
	},
	subscriptions:{
		setup({ dispatch, history }) {
            history.listen(({ pathname, query }) => {
                dispatch({type:'updateState',payload:{
                    departmentId:query['departmentId'],
                    searchParentId:query['departmentId']
                }}); 
                if (pathname === '/org') {
                	dispatch({type:'getFunctionPermissionMap'});
                    dispatch({type:'getTreeData'}); 
                    dispatch({type:'getTableData'});               
                }
            });
        }, 
	},
	effects:{
        *getTreeData({payload},{select,call,put}){
            const {departmentId} = yield select(({org})=>org);
            const data=yield call(getOrgTree,{
                departmentId: departmentId
            });
            if(data && data.result == 0){
                const treeData=handleTreeData(JSON.parse(data.data).items);
                yield put({type:'updateState',payload:{treeData}});
            }else {
                if(data && data.exception){
                    message.error(data.exception);
                }
            }
        },
		*getTableData({payload},{put,select,call}){
			const state = yield select(({org})=>org);
			yield put({type:'updateState',payload:{tableLoading:true,selectedRowKeys:[]}});
			const data = yield call(getOrgTable ,{
				page: state.currentPageIndex,
				rows: state.pageSize,
				orgName: state.orgName.trim(),
				parentId: state.searchParentId
			});
			if (data && data.result == 0) {
				yield put({
					type: 'updateState',
					payload: {
						tableData: handleTableData(data.data.rows),
						totalItems: data.data.total
					}
				});
			} else {
				if (data && data.exception) {
					message.error(data.exception);
				}
			}
			yield put({type:'updateState',payload:{tableLoading:false}});
		},
		*getDivisionTreeData({ payload }, {call, put, select}){
			const data=yield call(getDivisionTenantTree,{});
			if(data && data.result == 0){
				const divisionTree=handleSelectTreeData(JSON.parse(data.data).items);
				yield put({type:'updateState',payload:{divisionTree}});
			}else {
				if(data && data.exception){
					message.error(data.exception);
				}
			}
		},
		*saveOrg({payload},{put,select,call}){
			const {newItem, codeUnique, departmentId} = yield select(({org}) => org);
			let formCheck = [
				{checkType: 'required',checkVal: newItem.orgCode}, 
				{checkType: 'codeValidator',checkVal: newItem.orgCode}, 
				{checkType: 'required',checkVal: newItem.orgName}, 
				{checkType: 'nameValidator',checkVal: newItem.orgName}, 
				{checkType: 'phone',checkVal: newItem.headMobile}, 
				{checkType: 'email',checkVal: newItem.email}
			];
			console.log(formValidation(formCheck)+','+!codeUnique);
			if (formValidation(formCheck) && !codeUnique) {
				yield put({type: 'updateState',payload: {saving: true}});
				const data = yield call(saveOrg, {...newItem,departmentId});
				if (data && data.result == 0) {
					yield put({
						type: 'updateNewItem',
						payload: {
							visible: false
						}
					});
					yield put({type:'getTreeData'});
					yield put({type: 'getTableData'});
					message.success(data.msg, 3);
				} else {
					if (data && data.exception) {
						message.error(data.exception);
					}
				}
				yield put({type: 'updateState',payload: {saving: false}});
			}
		},
		*updateOrg({payload},{put,select,call}){
			const {editItem, codeUnique, defaultCloumns} = yield select(({org}) => org);
			let formCheck = [
				{checkType: 'required',checkVal: editItem.orgCode}, 
				{checkType: 'codeValidator',checkVal: editItem.orgCode}, 
				{checkType: 'required',checkVal: editItem.orgName}, 
				{checkType: 'nameValidator',checkVal: editItem.orgName}, 
				{checkType: 'phone',checkVal: editItem.headMobile}, 
				{checkType: 'email',checkVal: editItem.email}
			];
			if (formValidation(formCheck) && !codeUnique ) {
				yield put({
					type: 'updateState',
					payload: {
						saving: true
					}
				});
				const data = yield call(updateOrg, handleEditItem(defaultCloumns, editItem));
				if (data && data.result == 0) {
					yield put({
						type: 'updateEditItem',
						payload: {
							visible: false
						}
					});
					yield put({type:'getTreeData'});
					yield put({type: 'getTableData'});
					message.success(data.msg, 3);
				} else {
					if (data && data.exception) {
						message.error(data.exception);
					}
				}
				yield put({
					type: 'updateState',
					payload: {
						saving: false
					}
				});
			}
		},
		*validateCode({payload},{select,call,put}){
			let data;
			data = yield call(payload.obj.opType == 'add'?validateOrgForAdd:validateOrgForUpdate, {
				key: payload.obj.key,
				param: {...payload.obj}
			});
			if (data && data.result == 0) {
				yield put({
					type: 'updateState',
					payload: {
						codeUnique: !data.data
					}
				});
			}else {
				if(data && data.exception){
					message.error(data.exception);
				}
			}
		},
		*deleteOrg({payload},{select,call,put}){
			const data=yield call(deleteOrg, {orgId:payload.id});
			if(data && data.result == 0){
				yield put({type:'getTreeData'});
				yield put({type:'getTableData'});
				message.success(data.msg,3);
			}else {
				if(data && data.exception){
					message.error(data.exception);
				}
			}
		},
		*getDtoById({payload},{select,call,put}){
			const data=yield call(getOrgDtoById,{
				id: payload.id
			});
			if(data && data.result == 0){
				yield put({type:'updateEditItem',payload:{
					...data.data
				}});
				yield put({type:'updateViewItem',payload:{
					...data.data
				}});
			}else {
				if(data && data.exception){
					message.error(data.exception);
				}
			}
		},
		*getFunctionPermissionMap({payload},{select,call,put}){
            const {functionCodes} = yield select(({org})=>org);
            const data=yield call(getFunctionPermissionMap,{
                parameters:JSON.stringify({functionCodes:functionCodes})
            });
            if(data && data.result==0){
                yield put({type:'updateState',payload:{functionCodesMap:data.data}});
                yield put({type:'getTableData'});
            }else {
                if(data && data.exception){
                    message.error(data.exception);
                }
            }
        }
	},
	reducers:{
		updateState(state,action){
			return {
				...state,...action.payload
			}
		},
		updateNewItem(state,action){
			return {
				...state,
				newItem:{
					...state.newItem,
					...action.payload
				}
			}
		},
		updateEditItem(state,action){
			return {
				...state,
				editItem:{
					...state.editItem,
					...action.payload
				}
			}
		},
		updateViewItem(state,action){
		    return {
		        ...state,
		        viewItem:{
		          ...state.viewItem,
		          ...action.payload
		        }
		    }
	    },
	    clearNewItem(state,action){
	    	return {
	    		...state,
	    		newItem:{
					...state.newItem,
	    			visible:true,
					checkState:false,
					id:'',
					tenantId:'',
					orgTypeText:'',
					orgCode:'',
					orgName:'',
					head: '',
					headMobile:'',
					address:'',
					email:'',
					description:'',
					orgType:'',
					lngLats:'',
					divisionId:'',
					orderIndex:''
	    		}
	    	}
		},
		// 初始化表格状态数据
		initTableOpt(state, action){
			return {
				...state,
				currentPageIndex:1,
				selectedRowKeys:[],
				pageSize:10
			}
		},
		initSearchMap(state, action){
		  return {
			...state,
			mapCenter: [116.483917,39.929221]
		  }
		}
	}
}
//下拉选择树，数据处理
function handleSelectTreeData(data){
    if(typeof(data) == 'undefined'){
        data = [];
    }
    return data.map((item) => {
        if (item.length == 0) {
            return {
                key: item.id,
                name: item.name,
                attr: item,
				nodeType: item.nodeType,
				disabled: item.id == '-1'
            }
        }else {
            return {
                key: item.id,
                name: item.name,
                attr: item,
                nodeType: item.nodeType,
                children: handleTreeData(item.children),
				disabled: item.id == '-1'
            }
        }
        
    });
}