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
    getDeptTable,saveDept,updateDept,validateDeptForAdd,deleteDept,validateDeptForUpdate,getDivisionTenantTree,
	getParamsValue,getDeptDtoById,getFunctionPermissionMap,convertToAddress} from '../../services/remoteData';
export default{
	namespace:'dept',
	state:{
		tableData:[],
	    selectedRowKeys:[],
	    tableLoading:true,
	    currentPageIndex: 1,
	    pageSize: 10, 
	    totalItems: 0,
	    modal1Visible: false,
        mapCenter: [116.483917,39.929221],
        deptypesment:[],
		depName:'',		
	    newItem:{
	        visible:false,
			checkState:false,
			loading:false,
			id:'',
			tenantId:'',
	        depTypeText:'',
	        depCode:'',
	        depName:'',
	        head: '',
	        headMobile:'',
	        address:'',
	        email:'',
			description:'',
			depType:'',
			lngLats:'',
			divisionId:'',
			orderIndex:''
	    },
	    editItem:{
	        visible:false,
	        checkState:true,
	        loading:false
	    },
	    viewItem:{
	        visible:false
	    },
		codeUnique: false,
		defaultCloumns:['id','tenantId','depTypeText','depCode','depName','head',
		'headMobile','address','email','description','depType','lngLats','divisionId','orderIndex'],
		functionCodes: ['CF_MANAGE_TENANT_DEPT_LIST','CF_MANAGE_TENANT_DEPT_ADD','CF_MANAGE_TENANT_DEPT_UPDATE','CF_MANAGE_TENANT_DEPT_DELETE',
		'CF_MANAGE_TENANT_DEPT_VIEW'],
		functionCodesMap: {},
		divisionTree:[]
	},
	subscriptions:{
		setup({ dispatch, history }) {
            history.listen(({ pathname }) => {
                if (pathname === '/dept') {
                	dispatch({type:'getFunctionPermissionMap'});
                     dispatch({type:'getTableData'});               
                }
            });
        }, 
	},
	effects:{
		*getTableData({payload},{put,select,call}){
			const state = yield select(({dept})=>dept);
			yield put({type:'updateState',payload:{tableLoading:true,selectedRowKeys:[]}});
			const data = yield call(getDeptTable ,{
				page: state.currentPageIndex,
				rows: state.pageSize,
				depName: state.depName.trim(),
				tenantId: state.tenantId
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
		*saveDept({payload},{put,select,call}){
			const {newItem, codeUnique} = yield select(({dept}) => dept);
			let formCheck = [
				{checkType: 'required',checkVal: newItem.depCode}, 
				{checkType: 'codeValidator',checkVal: newItem.depCode}, 
				{checkType: 'required',checkVal: newItem.depName}, 
				{checkType: 'nameValidator',checkVal: newItem.depName}, 
				{checkType: 'phone',checkVal: newItem.headMobile}, 
				{checkType: 'email',checkVal: newItem.email}, 
				{checkType: 'required',checkVal: newItem.depType}
			];
			if (formValidation(formCheck) && !codeUnique) {
				yield put({
					type: 'updateNewItem',
					payload: {
						loading: true
					}
				});
				const data = yield call(saveDept, newItem);
				if (data && data.result == 0) {
					yield put({
						type: 'updateNewItem',
						payload: {
							visible: false
						}
					});
					yield put({type: 'getTableData'});
					message.success(data.msg, 3);
				} else {
					if (data && data.exception) {
						message.error(data.exception);
					}
				}
				yield put({
					type: 'updateNewItem',
					payload: {
						loading: false
					}
				});
			}
		},
		*updateDept({payload},{put,select,call}){
			const {editItem, codeUnique, defaultCloumns} = yield select(({dept}) => dept);
			let formCheck = [
				{checkType: 'required',checkVal: editItem.depCode}, 
				{checkType: 'codeValidator',checkVal: editItem.depCode}, 
				{checkType: 'required',checkVal: editItem.depName}, 
				{checkType: 'nameValidator',checkVal: editItem.depName}, 
				{checkType: 'phone',checkVal: editItem.headMobile}, 
				{checkType: 'email',checkVal: editItem.email}, 
				{checkType: 'required',checkVal: editItem.depType}
			];
			if (formValidation(formCheck) && !codeUnique ) {
				yield put({
					type: 'updateEditItem',
					payload: {
						loading: true
					}
				});
				const data = yield call(updateDept, handleEditItem(defaultCloumns, editItem));
				if (data && data.result == 0) {
					yield put({
						type: 'updateEditItem',
						payload: {
							visible: false
						}
					});
					yield put({type: 'getTableData'});
					message.success(data.msg, 3);
				} else {
					if (data && data.exception) {
						message.error(data.exception);
					}
				}
				yield put({
					type: 'updateEditItem',
					payload: {
						loading: false
					}
				});
			}
		},
		//获取类型
		*getdepTypeText({payload},{put,select,call}){
			const data=yield call(getParamsValue,{typeCode : 'DEPARTMENT_TYPE'});
	    	if(data && data.result==0){
                yield put({type:'updateState',payload:{
	                deptypesment: data.data
	            }});
            }else {
				if(data && data.exception){
					message.error(data.exception);
				}
			}
		},
		*validateCode({payload},{select,call,put}){
			let data;
			data = yield call(payload.obj.opType == 'add'?validateDeptForAdd:validateDeptForUpdate, {
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
			}
		},
		*deleteDept({payload},{select,call,put}){
			const data=yield call(deleteDept, {departmentId:payload.id});
			if(data && data.result == 0){
				yield put({type:'getTableData'});
				message.success(data.msg,3);
			}else {
				if(data && data.exception){
					message.error(data.exception);
				}
			}
		},
		*convertToAddress({payload},{select,call,put}){
			const data=yield call(convertToAddress, {
				batch: false,
				location: payload.location,
				coordtype: 'wgs84'
			});
			if(data && data.result == 0){
				yield put({type:'updateNewItem',payload: {address: data.data.address}});
				yield put({type:'updateEditItem',payload: {address: data.data.address}});
			}else {
				if(data && data.exception){
					message.error(data.exception);
				}
			}
		},
		*getDtoById({payload},{select,call,put}){
			const data=yield call(getDeptDtoById,{
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
            const {functionCodes} = yield select(({dept})=>dept);
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
	    			visible:true,
					checkState:false,
					id:'',
					tenantId:'',
					depTypeText:'',
					depCode:'',
					depName:'',
					head: '',
					headMobile:'',
					address:'',
					email:'',
					description:'',
					depType:'',
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