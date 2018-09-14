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
} from '../../../utils/toolFunctions';
import {
    getCloudSystemTable,saveCloudSystem,updateCloudSystem,validateCloudSystemAccount,validateCloudSystem,getFunctionPermissionMap
} from '../../../services/remoteData';
export default {
  namespace: 'cloudSystem',
  state: {
      tableData:[],
      selectedRowKeys:[],
      tableLoading:true,
      currentPageIndex: 1,
      pageSize: 10, 
      totalItems: 0,
      systemName:'',
      newItem:{
          visible:false,
          checkState:false,
          systemCode:'',
          systemName:'',
          id:'',
          website:'',
          userName:'',
          password:'',
          confirm_password:'',
          mapType:'',
          mapStr:'',
          longitudeDone:'',
          longitude:'',
          latitude:'',
          latitudeDone:'',
          welcomePage:'',
          orderIndex: ''
      },
      windowMapInfo: {
          showMap: false,
          mapPoints: [],
          centerPoint: []
      },
      editItem:{
          visible:false,
          checkState:true
      },
      viewItem:{
          visible:false
      },
      saving: false,
      modal1Visible: false,
      mapCenter: [116.483917,39.929221],
      codeUnique: false,
      nameUnique: false,
      passwordFlag: false,
      defaultCloumns:[ 'orderIndex', 'systemCode', 'systemName', 'id', 'website', 'confirm_password', 'mapType', 'mapStr', 'longitudeDone', 'longitude', 'latitude', 'latitudeDone','welcomePage'],
      functionCodes: ['CF_MANAGE_CS_LIST','CF_MANAGE_CS_ADD','CF_MANAGE_CS_UPDATE','CF_MANAGE_CS_VIEW'],
      functionCodesMap: {}
  },
  subscriptions: {
    setup({ dispatch, history }) {
            history.listen(({ pathname }) => {
                if (pathname === '/cloudsystem') {
                    dispatch({type:'getFunctionPermissionMap'});
                    dispatch({type:'getTableData'});
                }
            });
        },  
  },
  effects: {
    *doEdit({payload},{select,call,put}){
        yield put({
            type: 'updateState',
            payload: {
                isDoEdit: true,
                editGraphicId: payload.editGraphicId
            }
        })
        yield call(delay,1);
        yield put({
            type: 'updateState',
            payload: {
                isDoEdit: false
            }
        })
    },
    *getTableData({payload},{select,call,put}){
        const state = yield select(({cloudSystem})=>cloudSystem);
        yield put({type:'updateState',payload:{tableLoading:true,selectedRowKeys:[]}});
        const data = yield call(getCloudSystemTable ,{
            page: state.currentPageIndex,
            rows: state.pageSize,
            systemName: state.systemName.trim(),
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
    *addCloudSystem({payload},{select,call,put}){
        const {newItem, nameUnique, codeUnique, passwordFlag} = yield select(({cloudSystem}) => cloudSystem);
        let formCheck = [
            {
                checkType: 'required',
                checkVal: newItem.systemCode
            }, {
                checkType: 'codeValidator',
                checkVal: newItem.systemCode
            }, {
                checkType: 'required',
                checkVal: newItem.systemName
            }, {
                checkType: 'nameValidator',
                checkVal: newItem.systemName
            }, {
                checkType: 'required',
                checkVal: newItem.userName
            }, {
                checkType: 'nameValidator',
                checkVal: newItem.userName
            }, {
                checkType: 'required',
                checkVal: newItem.password
            }, {
                checkType: 'required',
                checkVal: newItem.confirm_password
            }
        ];
        if (formValidation(formCheck) && !nameUnique && !codeUnique && !passwordFlag) {
            yield put({
                type: 'updateState',
                payload: {
                    saving: true
                }
            });
            const data = yield call(saveCloudSystem, newItem);
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
                type: 'updateState',
                payload: {
                    saving: false
                }
            });
        }
    },
    *updateCloudSystem({payload},{select,call,put}){
        const {editItem, codeUnique, defaultCloumns} = yield select(({cloudSystem}) => cloudSystem);
        let formCheck = [
            {
                checkType: 'required',
                checkVal: editItem.systemCode
            }, {
                checkType: 'codeValidator',
                checkVal: editItem.systemCode
            }, {
                checkType: 'required',
                checkVal: editItem.systemName
            }, {
                checkType: 'nameValidator',
                checkVal: editItem.systemName
            }
        ];
        if (formValidation(formCheck) && !codeUnique ) {
            yield put({
                type: 'updateState',
                payload: {
                    saving: true
                }
            });
            const data = yield call(updateCloudSystem, handleEditItem(defaultCloumns, editItem));
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
                type: 'updateState',
                payload: {
                    saving: false
                }
            });
        }
    },
    *validateCode({payload},{select,call,put}){
        const data = yield call(validateCloudSystem, {
            key: payload.obj.key,
            param: {
                ...payload.obj
            }
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
    *validateAccount({payload},{select,call,put}){
        const data = yield call(validateCloudSystemAccount, {
            ...payload.obj
        });
        // if(data && data.result==0){     yield
        // put({type:'updateState',payload:{nameUnique:!data.data}}); }
        yield put({
            type: 'updateState',
            payload: {
                nameUnique: !data.data
            }
        });
    },
    *getFunctionPermissionMap({payload},{select,call,put}){
        const {functionCodes} = yield select(({cloudSystem})=>cloudSystem);
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
    updateWindowMapInfo(state, action){
        return {
            ...state,
            windowMapInfo:{
                ...state.windowMapInfo,
                ...action.payload
            }
        }
    },
    //清空
    clearNewItem(state, action){
	      return {
		        ...state,
		        newItem:{
		            visible:true,
			        checkState:false,
			        systemCode:'',
			        systemName:'',
			        id:'',
			        website:'',
			        userName:'',
			        password:'',
			        confirm_password:'',
			        mapType:'',
			        mapStr:'',
			        longitudeDone:'',
			        longitude:'',
			        latitude:'',
                    latitudeDone:'',
                    welcomePage:''
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
    },
    initWindowMapInfo(state, action){
        return{
            ...state,
            windowMapInfo: {
                showMap: false,
                mapPoints: [],
                centerPoint: []
            }
        }
    }
  },
  
  
};

function delay(time) {
    let promise = new Promise((resolve)=>{
        setTimeout(resolve,time);
    });
    return promise;
}
