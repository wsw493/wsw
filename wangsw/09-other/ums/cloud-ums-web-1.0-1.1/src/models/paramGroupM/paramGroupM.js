import React from 'react';
import {connect} from 'dva';
import {message} from 'antd';
import {handleTableData,handleTreeData,handleRestResultData,handleEditItem,getBaicPostData,
    formValidation} from '../../utils/toolFunctions';
import {getParamGroupTable,getParamGroupTree,saveParamGroup,validateParamGroup,updateParamGroup,
    deleteParamGroup,getFunctionPermissionMap} from '../../services/remoteData';
export default {
  namespace: 'paramGroup',
  state: {
  	tableData:[],
    treeData:[],
    selectedRowKeys:[],
    tableLoading:true,
    currentPageIndex: 1,
    pageSize: 10, 
    totalItems: 0,
    searchParentId:'-1',
    groupName: '',
    groupCode: '',
    newItem:{
        visible:false,
        checkState:false,

        id:'',
        groupCode:'',
        groupName:'',
        orderIndex: '',
        description:'',
        parentId:''
    },
    editItem:{
        visible:false,
        checkState:true
    },
    viewItem:{
        visible:false
    },
    saving: false,
    codeUnique:false,
    defaultCloumns:['id','groupCode','groupName','orderIndex','description','parentId'],
    functionCodes: ['CF_MANAGE_PARAM_G_DEL','CF_MANAGE_PARAM_G_ADD','CF_MANAGE_PARAM_G_UPDATE'],
    functionCodesMap: {}
  },
  subscriptions: {
    setup({ dispatch, history }) {
            history.listen(({ pathname }) => {
                if (pathname === '/paramgroup') {
                    dispatch({type:'getFunctionPermissionMap'});
                    dispatch({type:'getTableData'});
                    dispatch({type:'getTreeData'});
                }
            });
        },  
    },
  effects: {
    *getTreeData({payload},{select,call,put}){
        const data=yield call(getParamGroupTree);
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
        const state = yield select(({paramGroup})=>paramGroup);
        yield put({type:'updateState',payload:{tableLoading:true,selectedRowKeys:[]}});
        const data = yield call(getParamGroupTable ,{
            page: state.currentPageIndex,
            rows: state.pageSize,
            parentId: state.searchParentId,
            name: state.groupName.trim(),
            code: state.groupCode.trim()
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
    *addParamGroup({payload},{select,call,put}){
        const {newItem,codeUnique}=yield select(({paramGroup})=>paramGroup);
        let formCheck = [
            {checkType:'required', checkVal: newItem.groupCode },
            {checkType:'codeValidator', checkVal: newItem.groupCode },
            {checkType:'required', checkVal: newItem.groupName },
            {checkType:'nameValidator', checkVal: newItem.groupName },
            {checkType:'number', checkVal:newItem.orderIndex },
            {checkType:'required', checkVal: newItem.orderIndex }
        ];
        if(formValidation(formCheck) && !codeUnique){
            yield put({type:'updateState',payload:{saving:true}});
            const data = yield call(saveParamGroup ,newItem);
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
    *updateParamGroup({payload},{select,call,put}){
        const {editItem,codeUnique,defaultCloumns}=yield select(({paramGroup})=>paramGroup);
        let formCheck = [
            {checkType:'required', checkVal: editItem.groupCode },
            {checkType:'codeValidator', checkVal: editItem.groupCode },
            {checkType:'required', checkVal: editItem.groupName },
            {checkType:'nameValidator', checkVal: editItem.groupName },
            {checkType:'number', checkVal:editItem.orderIndex },
            {checkType:'required', checkVal: editItem.orderIndex }
        ];
        if(formValidation(formCheck) && !codeUnique){
            yield put({type:'updateState',payload:{saving:true}});
            const data = yield call(updateParamGroup ,handleEditItem(defaultCloumns,editItem));
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
    
    *validateParamGroup({payload},{select,call,put}){
        const data=yield call(validateParamGroup,{
            key: payload.obj.key,
            param:{
                ...payload.obj
            }
        });
        if(data && data.result==0){
            yield put({type:'updateState',payload:{codeUnique:!data.data}});
        }
    },
    *deleteParamGroup({payload},{select,call,put}){
        const data=yield call(deleteParamGroup, {id:payload.id});
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
    *getFunctionPermissionMap({payload},{select,call,put}){
        const {functionCodes} = yield select(({paramGroup})=>paramGroup);
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
                groupCode:'',
                groupName:'',
                orderIndex: '',
                description:''
            }
        }
    }
  }
};
