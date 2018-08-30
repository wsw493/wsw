import React from 'react';
import {connect} from 'dva';
import {message} from 'antd';
import {handleTableData,handleTreeData,handleRestResultData,handleEditItem,getBaicPostData,
        formValidation} from '../../utils/toolFunctions';
import {getFunctionGroupTable,getFunctionGroupTree,saveFunctionGroup,validateFunctionGroup,updateFunctionGroup,
        deleteFunctionGroup,deletesFunctionGroup,getFunctionPermissionMap,convertSystemCodeToId} from '../../services/remoteData';

export default {
  namespace: 'functionGroup',
  state: {
  	tableData:[],
    treeData:[],
    selectedRowKeys:[],
    tableLoading:true,
    currentPageIndex: 1,
    pageSize: 10, 
    totalItems: 0,
    searchParentId:'-1',
    systemCode:'',
    systemId:'',
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
    functionCodes: ['CF_MANAGE_FG_LIST','CF_MANAGE_FG_ADD','CF_MANAGE_FG_UPDATE','CF_MANAGE_FG_DEL','CF_MANAGE_FG_VIEW'],
    functionCodesMap: {}
  },
  subscriptions: {
    setup({ dispatch, history }) {
            history.listen(({ pathname, query }) => {
                if (pathname === '/functiongroup') {
                    if(query){
                        dispatch({type:'convertSystemCodeToId',payload:{systemCode:query['systemCode']}});
                    }
                }
            });
        },  
    },
  effects: {
    *convertSystemCodeToId({payload},{select,call,put}){
        const data=yield call(convertSystemCodeToId,{
            systemCode: payload.systemCode
        });
        if(data && data.result==0){
            yield put({type:'updateState',payload:{systemId:data.data}});
            yield put({type:'getFunctionPermissionMap'});
            yield put({type:'getTreeData'});
            yield put({type:'getTableData'});
        }
    },
    *getTreeData({payload},{select,call,put}){
        const {systemId} = yield select(({functionGroup})=>functionGroup);
        const data=yield call(getFunctionGroupTree,{
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
        const state = yield select(({functionGroup})=>functionGroup);
        yield put({type:'updateState',payload:{tableLoading:true,selectedRowKeys:[]}});
        const data = yield call(getFunctionGroupTable ,{
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
    *addFunctionGroup({payload},{select,call,put}){
        const {newItem,nameUnique,codeUnique}=yield select(({functionGroup})=>functionGroup);
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
            const data = yield call(saveFunctionGroup ,newItem);
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
    *updateFunctionGroup({payload},{select,call,put}){
        const {editItem,nameUnique,codeUnique,defaultCloumns}=yield select(({functionGroup})=>functionGroup);
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
            const data = yield call(updateFunctionGroup ,handleEditItem(defaultCloumns,editItem));
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
    
    *validateFunctionGroup({payload},{select,call,put}){
        const {systemId}=yield select(({functionGroup})=>functionGroup);
      const data=yield call(validateFunctionGroup,{
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
    *deleteFunctionGroup({payload},{select,call,put}){
        const data=yield call(deleteFunctionGroup, payload.id);
        if(data && data.result == 0){
            yield put({type:'initTableOpt'});
            yield put({type:'getTreeData'});
            yield put({type:'getTableData'});
            message.success(data.msg,3);
        }else {
            if(data && data.exception){
                message.error(data.exception);
            }
        }
    },
    *deletesFunctionGroup({payload},{select,call,put}){
        const {selectedRowKeys}=yield select(({functionGroup})=>functionGroup);
        const data=yield call(deletesFunctionGroup, JSON.stringify(selectedRowKeys));
        if(data && data.result == 0){
            yield put({type:'initTableOpt'});
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
            const {functionCodes} = yield select(({functionGroup})=>functionGroup);
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
  }
};
