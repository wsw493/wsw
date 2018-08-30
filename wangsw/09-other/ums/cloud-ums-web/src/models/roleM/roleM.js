import React from 'react';
import {connect} from 'dva';
import {message} from 'antd';
import {handleTableData,handleTreeData,handleRestResultData,handleEditItem,getBaicPostData,
        formValidation} from '../../utils/toolFunctions';
import {getRoleTable,getRoleGroupTree,saveRole,validateRole,updateRole,getRoleDtoById,
        deleteRole,deletesRole,getFunctionTree,getRoleFunRelation,roleBindFunctionTreeSave,getFunctionPermissionMap,
        convertSystemCodeToId} from '../../services/remoteData';

export default {
  namespace: 'role',
  state: {
  	tableData:[],
    treeData:[],
    selectedRowKeys:[],
    tableLoading:true,
    currentPageIndex: 1,
    pageSize: 10, 
    totalItems: 0,
    searchParentId:'-1',
    roleName:'',
    systemId:'',
    systemCode: '',
    newItem:{
        visible:false,
        checkState:false,

        id:'',
        groupId: '',
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
    rolefunTreeData:'',
    haveChecked:[],
    checkedKeys_func:[],
    roleId:'',
    addFunwindow:false,
    sort: '',
    order: '',
    defaultCloumns:['id','groupId','systemId','code','name','orderIndex','description'],
    functionCodes: ['CF_MANAGE_ROLE_LIST','CF_MANAGE_ROLE_ADD','CF_MANAGE_ROLE_UPDATE','CF_MANAGE_ROLE_DEL',
    'CF_MANAGE_ROLE_VIEW','CF_MANAGE_ROLE_FUN_ADD'],
    functionCodesMap: {}
  },
  subscriptions: {
    setup({ dispatch, history }) {
            history.listen(({ pathname, query }) => {
                if (pathname === '/role') {
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
        const {systemId} = yield select(({role})=>role);
        const data=yield call(getRoleGroupTree,{
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
        const state = yield select(({role})=>role);
        yield put({type:'updateState',payload:{tableLoading:true,selectedRowKeys:[]}});
        const data = yield call(getRoleTable ,{
            page: state.currentPageIndex,
            rows: state.pageSize,
            roleGroupId: state.searchParentId,
            systemId: state.systemId,
            name:state.roleName,
            sort: state.sort,
            order: state.order
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
    *addRole({payload},{select,call,put}){
        const {newItem,nameUnique,codeUnique}=yield select(({role})=>role);
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
            const data = yield call(saveRole ,newItem);
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
    *updateRole({payload},{select,call,put}){
        const {editItem,nameUnique,codeUnique,defaultCloumns}=yield select(({role})=>role);
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
            const data = yield call(updateRole ,handleEditItem(defaultCloumns,editItem));
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
    
    *validateRole({payload},{select,call,put}){
        const {systemId}=yield select(({role})=>role);
      const data=yield call(validateRole,{
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
    *deletesRole({payload},{select,call,put}){
        const {selectedRowKeys}=yield select(({role})=>role);
        const data=yield call(deletesRole, JSON.stringify(selectedRowKeys));
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
    *getDtoById({payload},{select,call,put}){
        const data=yield call(getRoleDtoById,{
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
    *getRolefunTreeData({payload},{select,call,put}){
        const {systemId}=yield select(({role})=>role);
        const data=yield call(getFunctionTree,{systemId:systemId});
        if(data && data.result==0){
            const rolefunTreeData=treedataHandler(JSON.parse(data.data).items);
            yield put({type:'updateState',payload:{rolefunTreeData}});
        }else {
            if(data && data.exception){
                message.error(data.exception);
            }
        }
    },
    *getRoleFunRelation({payload},{select,call,put}){
        const {systemId}=yield select(({role})=>role);
        const data=yield call(getRoleFunRelation,{
            roleId:payload.roleId,
            systemId:systemId
        });
        if(data && data.result==0){
            const fData = data.data.map((item)=>{return item.functionId;});
            yield put({type:'updateState',payload:{haveChecked:fData,checkedKeys_func:fData}});
        }else {
            if(data && data.exception){
                message.error(data.exception);
            }
        }
    },
    *rolefunTreeDataSave({payload},{select,call,put}){
        const {haveChecked,systemId,roleId}=yield select(({role})=>role);
        const param={
			id: roleId,
			param: {
				functionIds: haveChecked.toString(),
				systemId: systemId
			}
        }
        const data=yield call(roleBindFunctionTreeSave,param);
        if(data && data.result==0){
			message.success(data.msg,3);
			yield put({type:'updateState',payload:{addFunwindow:false}});
        }else{
            if(data && data.exception){
                message.error(data.exception);
            }
        }
    },
    *getFunctionPermissionMap({payload},{select,call,put}){
            const {functionCodes} = yield select(({role})=>role);
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

const treedataHandler = (data)=>{
    return data.map((element)=>{
      var arr1 = [];
      var disabled = true;
      if(element.children && element.children.length != 0){
        arr1 = treedataHandler(element.children);
      }
      //功能树的筛选条件
      if(element.nodeType == "function"){
          disabled = false;
      }
      return {
        key: element.id,
        name: element.name,
        nodeType: element.nodeType,
        isLeaf: element.leaf,
        children: arr1,
        // disableCheckbox: disabled
      }
    });
}
