import React from 'react';
import {connect} from 'dva';
import {message} from 'antd';
import {handleTableData,handleTreeData,handleRestResultData,handleEditItem,getBaicPostData,
        formValidation} from '../../../utils/toolFunctions';
import {getTenantRoleGroupTable,getTenantRoleGroupTree,saveTenantRoleGroup,validateTenantRoleGroup,updateTenantRoleGroup,
        deleteTenantRoleGroup,deletesTenantRoleGroup,getFunctionPermissionMap,convertSystemCodeToId} from '../../../services/remoteData';

export default {
  namespace: 'tenantRoleGroup',
  state: {
  	tableData:[],
    treeData:[],
    selectedRowKeys:[],
    tableLoading:true,
    currentPageIndex: 1,
    pageSize: 10, 
    totalItems: 0,
    searchParentId:'-1',
    newItem:{
        visible:false,
        checkState:false,

        id:'',
        parentId: '',
        systemId: '',
        code:'',
        name:'',
        orderIndex: '',
        description:''
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
    nameUnique: false,
    defaultCloumns:['id','parentId','systemId','code','name','orderIndex','description'],
    functionCodes: ['CF_MANAGE_TENANT_RG_LIST','CF_MANAGE_TENANT_RG_ADD','CF_MANAGE_TENANT_RG_UPDATE','CF_MANAGE_TENANT_RG_VIEW','CF_MANAGE_TENANT_RG_DEL'],
    functionCodesMap: {
        'CF_MANAGE_TENANT_RG_LIST': true,
        'CF_MANAGE_TENANT_RG_ADD': true,
        'CF_MANAGE_TENANT_RG_UPDATE': true,
        'CF_MANAGE_TENANT_RG_VIEW': true,
        'CF_MANAGE_TENANT_RG_DEL': true
    }
  },
  subscriptions: {
    setup({ dispatch, history }) {
            history.listen(({ pathname, query }) => {
                if (pathname === '/tenantrolegroup') {
                    // if(query){
                    //     dispatch({type:'convertSystemCodeToId',payload:{systemCode:query['systemCode']}});
                    // }
                    // dispatch({type:'getFunctionPermissionMap'});
                    dispatch({type:'getTreeData'});
                    dispatch({type:'getTableData'});
                }
            });
        },  
    },
  effects: {
    // *convertSystemCodeToId({payload},{select,call,put}){
    //     const data=yield call(convertSystemCodeToId,{
    //         systemCode: payload.systemCode
    //     });
    //     if(data && data.result==0){
    //         yield put({type:'updateState',payload:{systemId:data.data}});
    //         yield put({type:'getFunctionPermissionMap'});
    //         yield put({type:'getTreeData'});
    //         yield put({type:'getTableData'});
    //     }
    // },
    *getTreeData({payload},{select,call,put}){
        const {systemId} = yield select(({tenantRoleGroup})=>tenantRoleGroup);
        const data=yield call(getTenantRoleGroupTree,{
            systemId: systemId
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
    *getTableData({payload},{select,call,put}){
        const state = yield select(({tenantRoleGroup})=>tenantRoleGroup);
        yield put({type:'updateState',payload:{tableLoading:true,selectedRowKeys:[]}});
        const data = yield call(getTenantRoleGroupTable ,{
            page: state.currentPageIndex,
            rows: state.pageSize,
            parentId: state.searchParentId,
            systemId: state.systemId
        });
        if(data && data.result == 0){
            yield put({type:'updateState',payload:{
                tableData:handleTableData(data.data.rows),
                totalItems:data.data.total
            }});
        }else {
            if(data && data.exception){
                message.error(data.exception);
            }
        }
        
        yield put({type:'updateState',payload:{tableLoading:false}});
    },
    *addRoleGroup({payload},{select,call,put}){
        const {newItem,nameUnique,codeUnique}=yield select(({tenantRoleGroup})=>tenantRoleGroup);
        let formCheck = [
            {checkType:'required', checkVal: newItem.code },
            {checkType:'codeValidator', checkVal: newItem.code },
            {checkType:'required', checkVal: newItem.name },
            {checkType:'nameValidator', checkVal: newItem.name },
            {checkType:'number', checkVal:newItem.orderIndex },
            {checkType:'required', checkVal: newItem.orderIndex }
        ];
        if(formValidation(formCheck) && !nameUnique && !codeUnique){
            yield put({type:'updateState',payload:{saving:true}});
            const data = yield call(saveTenantRoleGroup ,newItem);
            if(data && data.result == 0){
                yield put({type:'updateNewItem',payload:{visible:false}});
                yield put({type:'getTreeData'});
                yield put({type:'getTableData'});
                message.success(data.msg,3);
            }else {
                if(data && data.exception){
                    message.error(data.exception);
                }
            }
            yield put({type:'updateState',payload:{saving:false}});
        }
    },
    *updateRoleGroup({payload},{select,call,put}){
        const {editItem,nameUnique,codeUnique,defaultCloumns}=yield select(({tenantRoleGroup})=>tenantRoleGroup);
        let formCheck = [
            {checkType:'required', checkVal: editItem.code },
            {checkType:'codeValidator', checkVal: editItem.code },
            {checkType:'required', checkVal: editItem.name },
            {checkType:'nameValidator', checkVal: editItem.name },
            {checkType:'number', checkVal:editItem.orderIndex },
            {checkType:'required', checkVal: editItem.orderIndex }
        ];
        if(formValidation(formCheck) && !nameUnique && !codeUnique){
            yield put({type:'updateState',payload:{saving:true}});
            const data = yield call(updateTenantRoleGroup ,handleEditItem(defaultCloumns,editItem));
            if(data && data.result == 0){
                yield put({type:'updateEditItem',payload:{visible:false}});
                yield put({type:'getTreeData'});
                yield put({type:'getTableData'});
                message.success(data.msg,3);
            }else {
                if(data && data.exception){
                    message.error(data.exception);
                }
            }
            yield put({type:'updateState',payload:{saving:false}});
        }
    },
    
    *validateRoleGroup({payload},{select,call,put}){
        const {systemId}=yield select(({tenantRoleGroup})=>tenantRoleGroup);
      const data=yield call(validateTenantRoleGroup,{
        key: payload.obj.key,
        param:{
            systemId: systemId,
            ...payload.obj
        }
      });
      if(data && data.result==0){
        if(payload.obj.key == "name"){
            yield put({type:'updateState',payload:{nameUnique:!data.data}});
        }else if(payload.obj.key == "code") {
            yield put({type:'updateState',payload:{codeUnique:!data.data}});
        }
      }
    },
    *deleteRoleGroup({payload},{select,call,put}){
        const data=yield call(deleteTenantRoleGroup, payload.id);
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
    *deletesRoleGroup({payload},{select,call,put}){
        const {selectedRowKeys}=yield select(({tenantRoleGroup})=>tenantRoleGroup);
        const data=yield call(deletesTenantRoleGroup, JSON.stringify(selectedRowKeys));
        if(data && data.result == 0){
            yield put({type:'getTreeData'});
            yield put({type:'getTableData'});
            yield put({type:'updateState',payload:{selectedRowKeys:[]}});
            message.success(data.msg,3);
        }else {
            if(data && data.exception){
                message.error(data.exception);
            }
        }
    },
    *getFunctionPermissionMap({payload},{select,call,put}){
            const {functionCodes} = yield select(({tenantRoleGroup})=>tenantRoleGroup);
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
  reducers: {
  	updateState(state,action){
  		return {...state,...action.payload}
  	},
    updateNewItem(state, action){
        return {
            ...state,
            newItem:{
                ...state.newItem,
                ...action.payload
            }
        }
    },
    updateEditItem(state, action){
        return {
            ...state,
            editItem:{
                ...state.editItem,
                ...action.payload
            }
        }
    },
    updateViewItem(state, action){
        return {
            ...state,
            viewItem:{
                ...state.viewItem,
                ...action.payload
            }
        }
    },
    // 初始化表格状态数据
    initTableOpt(state, action){
        return {
            ...state,
            currentPageIndex:1,
            selectedRowKeys:[]
        }
    },
    clearNewItem(state, action){
        return {
            ...state,
            newItem: {
                ...state.newItem,
                visible:true,
                checkState:false,
                id:'',
                systemId: state.systemId,
                code:'',
                name:'',
                orderIndex: '',
                description:''
            }
        }
    }
  },
  
  
};
