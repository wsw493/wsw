import React from 'react';
import {connect} from 'dva';
import {message} from 'antd';
import {handleTableData,handleEditItem,getBaicPostData,formValidation} from '../../../utils/toolFunctions';
import {getConstantTable,saveConstant,validateConstant,updateConstant,getFunctionPermissionMap} from '../../../services/remoteData';
export default {
    namespace: 'constant',
    state: {
        tableData:[],
        selectedRowKeys:[],
        tableLoading:true,
        currentPageIndex: 1,
        pageSize: 10, 
        totalItems: 0,
        constantName:'',
        newItem:{
            visible:false,
            checkState:false,

            constantValue:'',
            constantCode:'',
            constantDescription:'',
            tenantId: '',
            id:''
        },
        editItem:{
            visible:false,
            checkState:true
        },
        viewItem:{
            visible:false
        },
        saving: false,
        defaultCloumns:['id','constantCode','constantValue','constantDescription','tenantId'],
        functionCodes: ['CF_MANAGE_TENANT_CONS_LIST','CF_MANAGE_TENANT_CONS_ADD','CF_MANAGE_TENANT_CONS_UPDATE',
        'CF_MANAGE_TENANT_CONS_VIEW'],
        functionCodesMap: {}
    },
    subscriptions: {
        setup({ dispatch, history }) {
            history.listen(({ pathname }) => {
                if (pathname === '/constant') {
                    dispatch({type:"getFunctionPermissionMap"});
                    dispatch({type:'getTableData'});
                }
            });
        }
    },
    effects: {
        *getTableData({payload},{select,call,put}){
            const state = yield select(({constant})=>constant);
            yield put({type:'updateState',payload:{tableLoading:true,selectedRowKeys:[]}});
            const data = yield call(getConstantTable ,{
                page: state.currentPageIndex,
                rows: state.pageSize,
                tenantId:'',
                keyword: state.constantName
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
        *addConstant({payload},{select,call,put}){
            const {newItem,codeUnique}=yield select(({constant})=>constant);
            let formCheck = [
                {checkType:'required', checkVal: newItem.constantCode },
                {checkType:'required', checkVal: newItem.constantValue },
                {checkType:'required', checkVal: newItem.constantDescription }
            ];
            if(formValidation(formCheck) && !codeUnique){
                yield put({type:'updateState',payload:{saving:true}});
                const data = yield call(saveConstant ,newItem);
                if(data && data.result == 0){
                    yield put({type:'updateNewItem',payload:{visible:false}});
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
        *updateConstant({payload},{select,call,put}){
            const {editItem,codeUnique,defaultCloumns}=yield select(({constant})=>constant);
            let formCheck = [
                {checkType:'required', checkVal: editItem.constantCode },
                {checkType:'required', checkVal: editItem.constantValue },
                {checkType:'required', checkVal: editItem.constantDescription }
            ];
            if(formValidation(formCheck) && !codeUnique){
                yield put({type:'updateState',payload:{saving:true}});
                const data = yield call(updateConstant ,handleEditItem(defaultCloumns,editItem));
                if(data && data.result == 0){
                    yield put({type:'updateEditItem',payload:{visible:false}});
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
        *validateConstant({payload},{select,call,put}){
            const data=yield call(validateConstant,{
                key: payload.obj.key,
                param:{
                    ...payload.obj
                }
            });
            if(data && data.result==0){
                yield put({type:'updateState',payload:{codeUnique:!data.data}});
            }
        },
        *getFunctionPermissionMap({payload},{select,call,put}){
            const {functionCodes} = yield select(({constant})=>constant);
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
                    constantValue:'',
                    constantCode:'',
                    constantDescription:'',
                    id:''
                }
            }
        }
    }
};
