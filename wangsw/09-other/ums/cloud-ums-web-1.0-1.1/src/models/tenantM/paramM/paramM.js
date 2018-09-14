import React from 'react';
import {connect} from 'dva';
import {message} from 'antd';
import {handleTableData,handleTreeData,handleRestResultData,handleEditItem,getBaicPostData,
    formValidation} from '../../../utils/toolFunctions';
import {getParamTypeTable,getParamGroupTree,saveParamType,updateParamType,deleteParamType,
    validateParamType,getTenantParamValueTable,saveTenantParamValue,updateTenantParamValue,deleteTenantParamValue,getFunctionPermissionMap
    } from '../../../services/remoteData';

const paramDataHandler=(data)=>{
  return data.map((item) => {
    return {
          ...item,
           key:item.id,
           editable:false
    }
  });
}
export default {
  namespace: 'tenantparam',
  state: {
        tableData:[],
        treeData:[],
        currentPageIndex:1,
        totalItems:'',
        pageSize:10,
        selectedRowKeys:'',
        tableLoading:false,
        paramValueWindow:false,
        operatedRow:{},
        codeValidatFlag:true,
        searchParentId:'-1',
        searchCode: '',
        searchName: '',
        saving:false,
        valueItems:{
            pramValueData:[],
            totalItems: 0,
            listTableLoading:false,
            valueTotalItems:'',
            currentPageIndex:1,
            pageSize:10,
            selectedRowKeys:'',
            newValue:'',
            count:1,
            typeId: '',
            typeName: '',
            codeValidatFlag1: false
        },
        newItem:{
            visible:false,
            checkState:false,
            id: '',
            typeCode:'',
            typeName:'',
            orderIndex:'',
            description:'',
            groupId: '',
            groupName: ''
        },
        editItem:{
            visible:false,
            checkState:true
        },
        viewItem:{
            visible:false
        },
        valueNewItems:{
            id: '',
            parmCode:'',
            parmName:'',
            orderIndex:'',
            typeId: '',
            editable: false
        },
        codeUnique: false,
        nameUnique: false,
        defaultCloumns:['id','typeCode','typeName','orderIndex','description','groupId'],
        defaultValueCloumns:['id','parmCode','parmName','typeId','orderIndex'],
        functionCodes: ['CF_MANAGE_TENANT_PARAM_TYPE_LIST','CF_MANAGE_TENANT_PARAM_TYPE_VIEW',
        'CF_MANAGE_TENANT_PARAM_ADD','CF_MANAGE_TENANT_PARAM_LIST','CF_MANAGE_TENANT_PARAM_BAT_DEL','CF_MANAGE_TENANT_PARAM_UPDATE'],
        functionCodesMap: {}
    },
  subscriptions: {
    setup({ dispatch, history }) {
            history.listen(({ pathname }) => {
                if (pathname === '/tenantparam') {
                    dispatch({type:'getFunctionPermissionMap'});
                     dispatch({type:'getParamGroupTreeData'});
                     dispatch({type:'getParamTypeTableData'});               
                }
            });
        }, 
  },
  effects: {
    *getParamGroupTreeData({payload},{select,call,put}){
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
    *getParamTypeTableData({payload},{select,call,put}){
        const state = yield select(({tenantparam})=>tenantparam);
        yield put({type:'updateState',payload:{tableLoading:true,selectedRowKeys:[]}});
        const data = yield call(getParamTypeTable ,{
            page: state.currentPageIndex,
            rows: state.pageSize,
            's_EQ_parameterType.groupId': state.searchParentId,
            's_LIKE_parameterType.typeCode': state.searchCode.trim(),
            's_LIKE_parameterType.typeName': state.searchName.trim()
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
    *addParamType({ payload }, { call, put,select }){
        const {newItem,codeUnique,nameUnique}=yield select(({tenantparam})=>tenantparam);
        let formCheck = [
            {checkType:'required', checkVal: newItem.typeCode },
            {checkType:'codeValidator', checkVal: newItem.typeCode },
            {checkType:'required', checkVal: newItem.typeName },
            {checkType:'nameValidator', checkVal: newItem.typeName },
            {checkType:'number', checkVal:newItem.orderIndex },
            {checkType:'required', checkVal: newItem.orderIndex }
        ];
        if(formValidation(formCheck) && !nameUnique && !codeUnique){
            yield put({type:'updateState',payload:{saving:true}});
            const data = yield call(saveParamType ,newItem);
            if(data && data.result == 0){
                yield put({type:'updateNewItem',payload:{visible:false}});
                yield put({type:'getParamTypeTableData'});
                message.success(data.msg,3);
            }else {
                if(data && data.exception){
                    message.error(data.exception);
                }
            }
            yield put({type:'updateState',payload:{saving:false}});
        }
    },
    *updateParamType({ payload }, { call, put,select }){
        const {editItem,codeUnique,nameUnique,defaultCloumns}=yield select(({tenantparam})=>tenantparam);
        let formCheck = [
            {checkType:'required', checkVal: editItem.typeCode },
            {checkType:'codeValidator', checkVal: editItem.typeCode },
            {checkType:'required', checkVal: editItem.typeName },
            {checkType:'nameValidator', checkVal: editItem.typeName },
            {checkType:'number', checkVal:editItem.orderIndex },
            {checkType:'required', checkVal: editItem.orderIndex }
        ];
        if(formValidation(formCheck) && !nameUnique && !codeUnique){
            yield put({type:'updateState',payload:{saving:true}});
            const data = yield call(updateParamType ,handleEditItem(defaultCloumns,editItem));
            if(data && data.result == 0){
                yield put({type:'updateEditItem',payload:{visible:false}});
                yield put({type:'getParamTypeTableData'});
                message.success(data.msg,3);
            }else {
                if(data && data.exception){
                    message.error(data.exception);
                }
            }
            yield put({type:'updateState',payload:{saving:false}});
        }
    },
    *deleteParamType({payload},{select,call,put}){
        const data=yield call(deleteParamType, {id:payload.id});
        if(data && data.result == 0){
            yield put({type:'getParamTypeTableData'});
            message.success(data.msg,3);
        }else {
            if(data && data.exception){
                message.error(data.exception);
            }
        }
    },
    *validateParamType({payload},{select,call,put}){
        const data=yield call(validateParamType,{
            key: payload.obj.key,
            param:{
                ...payload.obj
            }
        });
        if(data && data.result==0){
            if('typeName' == payload.obj.key){
                yield put({type:'updateState',payload:{nameUnique:!data.data}});
            }else if('typeCode' == payload.obj.key){
                yield put({type:'updateState',payload:{codeUnique:!data.data}});
            }
        }
    },
    *getFunctionPermissionMap({payload},{select,call,put}){
        const {functionCodes} = yield select(({tenantparam})=>tenantparam);
        const data=yield call(getFunctionPermissionMap,{
            parameters:JSON.stringify({functionCodes:functionCodes})
        });
        if(data && data.result==0){
            yield put({type:'updateState',payload:{functionCodesMap:data.data}});
            yield put({type:'getParamTypeTableData'});
        }else {
            if(data && data.exception){
                message.error(data.exception);
            }
        }
    },
    /*********参数值操作****************/
    *getParamValueTableData({payload},{select,call,put}){
        var newData = null;;
        if(payload && payload.newData){
          newData = payload.newData;
        }
        const {valueItems} = yield select(({tenantparam})=>tenantparam);
        yield put({type:'updateValueItems',payload:{tableLoading:true,selectedRowKeys:[]}});
        const data = yield call(getTenantParamValueTable ,{
            page: valueItems.currentPageIndex,
            rows: valueItems.pageSize,
            's_EQ_typeId': valueItems.typeId
        });
        if(data && data.result == 0){
            let pramValueData = paramDataHandler(data.data.rows);
            let valueTotalItems = data.data.total;
            yield put({type:'updateValueItems',payload:{
                pramValueData,
                valueTotalItems
            }});
            if(newData && typeof(newData) != "undefined"){
              yield put({type: 'updatePramValueData', payload: {pramValueData:[...pramValueData,newData],valueNewItems:newData, count:valueItems.count}});
            }
        }else {
            if(data && data.exception){
                message.error(data.exception);
            }
        }
        yield put({type:'updateValueItems',payload:{tableLoading:false}});
    },
    *addParamValue({ payload }, { call, put,select }){
        const {valueNewItems} = yield select(({tenantparam})=>tenantparam);
        const data = yield call(saveTenantParamValue ,valueNewItems);
        if(data && data.result == 0){
            yield put({type:'getParamValueTableData'});
            message.success(data.msg,3);
        }else {
            if(data && data.exception){
                message.error(data.exception);
            }
        }
        
    },
    *updateParamValue({ payload }, { call, put,select }){
        const {valueNewItems,defaultValueCloumns} = yield select(({tenantparam})=>tenantparam);
        const data = yield call(updateTenantParamValue ,handleEditItem(defaultValueCloumns,valueNewItems));
        if(data && data.result == 0){
            yield put({type:'getParamValueTableData'});
            message.success(data.msg,3);
        }else {
            if(data && data.exception){
                message.error(data.exception);
            }
        }
    },
    *deletesParamValue({ payload }, {call, put, select}){
        const {valueItems} = yield select(({tenantparam})=>tenantparam);
        const data  = yield call(deleteTenantParamValue, {ids:(valueItems.selectedRowKeys).toString()});
        if(data && data.result == 0) {
            message.success(data.msg,3);
            yield put({type:'updateValueItems',payload:{selectedRowKeys:[]}});
            yield put({type:'getParamValueTableData'});
        }else {
            if(data && data.exception){
                message.error(data.exception);
            }
        }
    }
  },
  reducers: {
    updateState(state,action){
      return {
        ...state,...action.payload
      }
    },
    updateValueItems(state,action){
      return {
        ...state,
        valueItems:{
          ...state.valueItems,
          ...action.payload
        }
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
    updateValueNewItems(state,action){
      return {
        ...state,
        valueNewItems:{
          ...state.valueNewItems,
          ...action.payload
        }
      }
    },
    clearNewItem(state,action){
      return {
        ...state,
        newItem:{
                ...state.newItem,
                visible: true,
                checkState:false,
                id: '',
                typeCode:'',
                typeName:'',
                orderIndex:'',
                description:''
        }
      }
    },
    clearValueNewItem(state,action){
      return {
        ...state,
        valueNewItems:{
          id: '',
          parmCode:'',
          parmName:'',
          orderIndex:'',
          typeId: '',
          editable:false
        }
      }
    },
    initTableArgs(state, action) {
        return {
            ...state,
            selectedRowKeys:[],
            currentPageIndex:1,
            pageSize:10
        }
    },
    initValueTableArgs(state, action) {
      return {
        ...state,
        valueItems: {
          pramValueData:[],
          listTableLoading:false,
          valueTotalItems:'',
          currentPageIndex:1,
          pageSize:10,
          selectedRowKeys:[],
          newValue:'',
          count:1,
          typeId: '',
        }
      }
    },
    updatePramValueData(state, action) {
        const pramValueData  = state.valueItems.pramValueData;
        const valueNewItems  = state.valueNewItems;
        var newData = [];
        pramValueData.map((item)=>{
            if(item.id != valueNewItems.id){
                newData.push(item);
            }
        });
        newData.push(valueNewItems);
        return {
            ...state,
            valueItems: {
                ...state.valueItems,
                pramValueData: newData
            }
        }
    }
  },
 
 
};
