import React from 'react';
import {connect} from 'dva';
import {message} from 'antd';
import {handleTableData,handleTreeData,handleRestResultData,handleEditItem,getBaicPostData,
        formValidation} from '../../utils/toolFunctions';
import {getFunctionsTable,getFunctionGroupTree,saveFunctions,validateFunctions,updateFunctions,
        deleteFunctions,deletesFunctions,getFunctionDtoById,getSystemList,getFunctionTree,getFunctionPermissionMap,
        convertSystemCodeToId} from '../../services/remoteData';

export default {
  namespace: 'functions',
  state: {
  	tableData:[],
    treeData:[],
    selectedRowKeys:[],
    tableLoading:true,
    currentPageIndex: 1,
    pageSize: 10, 
    totalItems: 0,
	searchParentId:'-1',
	systemId:'',
	functionName:'',
    newItem:{
        visible:false,
		checkState:false,
		isMain: true,

        id:'',
        groupId: '',
        systemId: '',
        code:'',
        name:'',
        orderIndex: '',
		description:'',
		uri:'',
		goalSystemId:'',
		functionType:'1',
		mainFunctionId:'',
		groupName:'',
		goalSystemName:'',
		mainFunctionName:''
    },
    editItem:{
        visible:false,
		checkState:true,
		isMain: true
    },
    viewItem:{
        visible:false
    },
    saving: false,
    codeUnique: false,
	nameUnique: false,
	functionTree: [],
	systemList: [],
	defaultCloumns:['id','groupId','systemId','code','name','orderIndex','description',
		'uri','goalSystemId','functionType','mainFunctionId'],
  functionCodes: ['CF_MANAGE_FUN_LIST','CF_MANAGE_FUN_ADD','CF_MANAGE_FUN_VIEW','CF_MANAGE_FUN_UPDATE','CF_MANAGE_FUN_DEL'],
  functionCodesMap: {}
  },
  subscriptions: {
    setup({ dispatch, history }) {
            history.listen(({ pathname, query }) => {
                if (pathname === '/functions') {
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
        const {systemId} = yield select(({functions})=>functions);
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
        const state = yield select(({functions})=>functions);
        yield put({type:'updateState',payload:{tableLoading:true,selectedRowKeys:[]}});
        const data = yield call(getFunctionsTable ,{
            page: state.currentPageIndex,
            rows: state.pageSize,
            functionGroupId: state.searchParentId,
			systemId: state.systemId,
			name:state.functionName
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
    *addFunctions({payload},{select,call,put}){
        const {newItem,nameUnique,codeUnique}=yield select(({functions})=>functions);
        let formCheck = [
            {checkType:'required', checkVal: newItem.code },
            {checkType:'codeValidator', checkVal: newItem.code },
            {checkType:'required', checkVal: newItem.name },
            {checkType:'nameValidator', checkVal: newItem.name },
            {checkType:'number', checkVal:newItem.orderIndex },
            {checkType:'required', checkVal: newItem.orderIndex }
        ];
        if(formValidation(formCheck) && !codeUnique){
            yield put({type:'updateState',payload:{saving:true}});
            const data = yield call(saveFunctions ,newItem);
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
    *updateFunctions({payload},{select,call,put}){
        const {editItem,nameUnique,codeUnique,defaultCloumns}=yield select(({functions})=>functions);
        let formCheck = [
            {checkType:'required', checkVal: editItem.code },
            {checkType:'codeValidator', checkVal: editItem.code },
            {checkType:'required', checkVal: editItem.name },
            {checkType:'nameValidator', checkVal: editItem.name },
            {checkType:'number', checkVal:editItem.orderIndex },
            {checkType:'required', checkVal: editItem.orderIndex }
        ];
        if(formValidation(formCheck) && !codeUnique){
            yield put({type:'updateState',payload:{saving:true}});
            const data = yield call(updateFunctions ,handleEditItem(defaultCloumns,editItem));
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
    
    *validateFunctions({payload},{select,call,put}){
        const {systemId}=yield select(({functions})=>functions);
      const data=yield call(validateFunctions,{
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
    *deletesFunctions({payload},{select,call,put}){
        const {selectedRowKeys}=yield select(({functions})=>functions);
        const data=yield call(deletesFunctions, JSON.stringify(selectedRowKeys));
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
	*getDtoById({payload},{select,call,put}){
        const data=yield call(getFunctionDtoById,{
            id: payload.id
        });
        if(data && data.result == 0){
			yield put({type:'updateEditItem',payload:{...data.data}});
			yield put({type:'updateViewItem',payload:{...data.data}});
        }else {
            if(data && data.exception){
                message.error(data.exception);
            }
        }
	},
	*getSystemList({payload},{select,call,put}){
        const data=yield call(getSystemList);
        if(data && data.result == 0){
			yield put({type:'updateState',payload:{systemList:data.data.map((item)=>{return {...item,key:item.id,name:item.systemName}})}});
        }else {
            if(data && data.exception){
                message.error(data.exception);
            }
        }
	},
	*getFunctionTree({payload},{select,call,put}){
		const {systemId}=yield select(({functions})=>functions);
        const data=yield call(getFunctionTree,{
			systemId: systemId
		});
        if(data && data.result == 0){
			const functionTree = handleFunctionTreeData(JSON.parse(data.data).items);
			yield put({type:'updateState',payload:{functionTree}});
        }else {
            if(data && data.exception){
                message.error(data.exception);
            }
        }
	},
  *getFunctionPermissionMap({payload},{select,call,put}){
          const {functionCodes} = yield select(({functions})=>functions);
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
                  visible:true,
				  checkState:false,
				  isMain: true,
                  id:'',
				  groupId: '',
				  systemId: state.systemId,
				  code:'',
				  name:'',
				  orderIndex: '',
				  description:'',
				  uri:'',
				  goalSystemId:'',
				  functionType:'1',
				  mainFunctionId:'',
				  groupName:'',
				  goalSystemName:'',
				  mainFunctionName:''
              }
          }
      }
    }
};

function handleFunctionTreeData(data){
	if(typeof(data) == 'undefined'){
        data = [];
    }
    return data.map((item) => {
        if (item.length == 0) {
            return {
                key: item.id,
				name: item.name,
				disabled: item.nodeType!='Function'
            }
        }else {
            return {
                key: item.id,
                name: item.name,
				children: handleFunctionTreeData(item.children),
				disabled: item.nodeType!='Function'
            }
        }
        
    });
}
