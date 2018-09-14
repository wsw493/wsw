import React from 'react';
import {connect} from 'dva';
import {message} from 'antd';
import {handleTableData,handleTreeData,handleRestResultData,handleEditItem,getBaicPostData,
        formValidation} from '../../utils/toolFunctions';
import {getMenuTable,getMenuTree,saveMenu,validateMenu,updateMenu,getMenuDtoById,
        deletesMenu,getFunctionTree,getFunctionPermissionMap,convertSystemCodeToId} from '../../services/remoteData';

export default {
  namespace: 'menu',
  state: {
  	tableData:[],
    treeData:[],
    selectedRowKeys:[],
    tableLoading:true,
    currentPageIndex: 1,
    pageSize: 10, 
    totalItems: 0,
    searchParentId:'-1',
    canAddNewMenu: true,
    menuName:'',
    menuCode:'',
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
        description:'',
        photoIds:'',
        isHidden: 0,
        functionId: '',
        isControlled: 1,
        isWelcomeMenu: 0,
        fileList: [],
        iconFont:''
    },
    fileListVersion: 1,
    editItem:{
        visible:false,
        checkState:true,
        fileList:[]
    },
    viewItem:{
        visible:false,
        fileList:[]
    },
    saving: false,
    codeUnique: false,
    nameUnique: false,
    funTreeData:[],
    defaultCloumns:['id','parentId','systemId','code','name','orderIndex','description','iconFont',
        'photoIds','isHidden','functionId','isControlled','isWelcomeMenu'],
    functionCodes: ['CF_MANAGE_MENU_LIST','CF_MANAGE_MENU_ADD','CF_MANAGE_MENU_UPDATE','CF_MANAGE_MENU_VIEW','CF_MANAGE_MENU_DEL'],
    functionCodesMap: {}
  },
  subscriptions: {
    setup({ dispatch, history }) {
            history.listen(({ pathname, query }) => {
                if (pathname === '/menu') {
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
        const {systemId} = yield select(({menu})=>menu);
        const data=yield call(getMenuTree,{
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
        const state = yield select(({menu})=>menu);
        yield put({type:'updateState',payload:{tableLoading:true,selectedRowKeys:[]}});
        const data = yield call(getMenuTable ,{
            page: state.currentPageIndex,
            rows: state.pageSize,
            parentId: state.searchParentId,
            systemId: state.systemId,
            name: state.menuName.trim(),
            code: state.menuCode.trim()
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
    *addMenu({payload},{select,call,put}){
        const {newItem,nameUnique,codeUnique}=yield select(({menu})=>menu);
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
            const data = yield call(saveMenu ,newItem);
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
    *updateMenu({payload},{select,call,put}){
        const {editItem,nameUnique,codeUnique,defaultCloumns}=yield select(({menu})=>menu);
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
            const data = yield call(updateMenu ,handleEditItem(defaultCloumns,editItem));
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
    
    *validateMenu({payload},{select,call,put}){
        const {systemId}=yield select(({menu})=>menu);
      const data=yield call(validateMenu,{
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
    *deletesMenu({payload},{select,call,put}){
        const {selectedRowKeys}=yield select(({menu})=>menu);
        const data=yield call(deletesMenu, payload&&payload.id?JSON.stringify([payload.id]):JSON.stringify(selectedRowKeys));
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
        const {fileListVersion}=yield select(({menu})=>menu);
        const data=yield call(getMenuDtoById,{
            id: payload.id
        });
        if(data && data.result == 0){
            
			yield put({type:'updateEditItem',payload:{
                ...data.data
            }});
            yield put({type:'updateViewItem',payload:{
                ...data.data
            }});
            if(data.data.photoIds != ""){
                yield put({type:'updateEditItem',payload:{fileList:JSON.parse(data.data.photoIds)}});
                yield put({type:'updateViewItem',payload:{fileList:JSON.parse(data.data.photoIds)}});
            }
            if(data.data.photoIds == "") {
                yield put({type:'updateEditItem',payload:{fileList:[]}});
                yield put({type:'updateViewItem',payload:{fileList:[]}});
            }
            yield put({type:'updateState',payload:{
                fileListVersion: fileListVersion+1
            }})
        }else {
            if(data && data.exception){
                message.error(data.exception);
            }
        }
    },  	
    *getFunctionTree({payload},{select,call,put}){
        const {systemId}=yield select(({menu})=>menu);
        const data=yield call(getFunctionTree,{systemId:systemId});
        if(data && data.result==0){
            const funTreeData=treedataHandler(JSON.parse(data.data).items);
            yield put({type:'updateState',payload:{funTreeData}});
        }else {
            if(data && data.exception){
                message.error(data.exception);
            }
        }
    },
    *getFunctionPermissionMap({payload},{select,call,put}){
            const {functionCodes} = yield select(({menu})=>menu);
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
                parentId: '',
                systemId: state.systemId,
                code:'',
                name:'',
                orderIndex: '',
                description:'',
                photoIds:'',
                isHidden: 0,
                functionId: '',
                isControlled: 1,
                isWelcomeMenu: 0,
                fileList:[],
                iconFont:''
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
