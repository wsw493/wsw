import React from 'react';
import {connect} from 'dva';
import {message} from 'antd';
import {toLogin} from '../../utils/request';
import {
    handleTableData,
    handleTreeData,
    handleRestResultData,
    handleEditItem,
    getBaicPostData,
    formValidation
} from '../../utils/toolFunctions';
import {
    getTenantTable,
    getDivisionTree,
    saveTenant,
    validateTenant,
    updateTenant,
    disableTenant,
    enableTenant,
    deleteTenant,
    deletesTenant,
    validateTenantAccount,
    disableCloudSystemt,
    enableCloudSystem,
    getCloudSystemTableData,
    getFunctionPermissionMap,
    getTenantDtoById
} from '../../services/remoteData';

const selectTreedataHandler = (data) => {
    return data.map((item) => {
        if (item.length == 0) {
            return {key: item.id, label: item.name, value: item.id}
        } else {
            return {
                key: item.id,
                label: item.name,
                value: item.id,
                children: selectTreedataHandler(item.children)
            }
        }

    });
}
export default {
    namespace : 'tenant',
    state : {
        tableData: [],
        treeData: [],
        tenantName: '',
        enabled: '',
        selectedRowKeys: [],
        tableLoading: true,
        currentPageIndex: 1,
        pageSize: 10,
        totalItems: 0,
        newItem: {
            visible: false,
            checkState: false,

            id: '',
            tenantCode: '',
            tenantName: '',
            domain: '',
            menuUrl: '',
            navigationUrl: '',
            contact: '',
            phone: '',
            email: '',
            latitude: '',
            latitudeDone: '',
            longitude: '',
            longitudeDone: '',
            enabled: '',
            divisionId: '',
            divisionName: '',
            userName: '',
            password: '',
            confirm_password: ''
        },
        windowMapInfo: {
            showMap: false,
            mapPoints: [],
            centerPoint: []
        },
        editItem: {
            visible: false,
            checkState: true
        },
        viewItem: {
            visible: false
        },
        codeValidatFlag: true,
        saving: false,
        modal1Visible: false,
        mapCenter: [
            116.483917, 39.929221
        ],

        cloudData: {
            tenantId:'',
            selectedRowKeys: [],
            tableLoading: true,
            currentPageIndex: 1,
            pageSize: 10,
            totalItems: 0,
            tableData: [],
            visible: false,
            systemName: ''
        },
        codeUnique: false,
        nameUnique: false,
        domainUnique: false,
        passwordFlag: false,
        defaultCloumns: [
            'id',
            'tenantCode',
            'tenantName',
            'domain',
            'contact',
            'phone',
            'email',
            'latitude',
            'latitudeDone',
            'longitude',
            'longitudeDone',
            'enabled',
            'divisionId',
            'userName',
            'password',
            'menuUrl',
            'navigationUrl'
        ],
        userName:'',
        sort: '',
        order: '',
        functionCodes: ['CF_MANAGE_TENANT_LIST','CF_MANAGE_TENANT_ADD','CF_MANAGE_TENANT_ENABLE','CF_MANAGE_TENANT_DISABLE','CF_MANAGE_TENANT_UPDATE',
        'CF_MANAGE_TENANT_VIEW','CF_MANAGE_TENANT_CS_LIST','CF_MANAGE_TENANT_CS_ENABLE','CF_MANAGE_TENANT_CS_DISABLE'],
        functionCodesMap: {}
    },
    subscriptions : {
        setup({dispatch, history}) {
            history.listen(({pathname,query}) => {
                if(query['userName']){
                    dispatch({type: 'updateState',payload:{
                        userName:query['userName']
                    }});
                }
                if (pathname === '/tenant') {
                    dispatch({type:'getFunctionPermissionMap'});
                    dispatch({type: 'getTableData'});
                }
            });
        }
    },
    effects : {
        *login({payload},{select,call,put}){
            const {userName} = yield select(({tenant}) => tenant);
            message.info(yield call(toLogin,{userName:userName}));
        },
        *doEdit({
            payload
        }, {select, call, put}) {
            yield put({
                type: 'updateState',
                payload: {
                    isDoEdit: true,
                    editGraphicId: payload.editGraphicId
                }
            })
            yield call(delay, 1);
            yield put({
                type: 'updateState',
                payload: {
                    isDoEdit: false
                }
            })
        },
        *getTableData({
            payload
        }, {select, call, put}) {
            const state = yield select(({tenant}) => tenant);
            yield put({
                type: 'updateState',
                payload: {
                    tableLoading: true,
                    selectedRowKeys: []
                }
            });
            const data = yield call(getTenantTable, {
                page: state.currentPageIndex,
                rows: state.pageSize,
                tenantName: state
                    .tenantName
                    .trim(),
                enabled: state.enabled,
                sort: state.sort,
				order: state.order
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
            yield put({
                type: 'updateState',
                payload: {
                    tableLoading: false
                }
            });
        },
        *addTenant({
            payload
        }, {select, call, put}) {
            const {newItem, nameUnique, codeUnique, domainUnique, passwordFlag} = yield select(({tenant}) => tenant);
            let formCheck = [
                {
                    checkType: 'required',
                    checkVal: newItem.tenantCode
                }, {
                    checkType: 'codeValidator',
                    checkVal: newItem.tenantCode
                }, {
                    checkType: 'required',
                    checkVal: newItem.tenantName
                }, {
                    checkType: 'nameValidator',
                    checkVal: newItem.tenantName
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
                }, {
                    checkType: 'phone',
                    checkVal: newItem.phone
                }, {
                    checkType: 'email',
                    checkVal: newItem.email
                }
            ];
            if (formValidation(formCheck) && !nameUnique && !codeUnique && !passwordFlag) {
                yield put({
                    type: 'updateState',
                    payload: {
                        saving: true
                    }
                });
                const data = yield call(saveTenant, newItem);
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
        *updateTenant({
            payload
        }, {select, call, put}) {
            const {editItem, codeUnique, defaultCloumns} = yield select(({tenant}) => tenant);
            let formCheck = [
                {
                    checkType: 'required',
                    checkVal: editItem.tenantCode
                }, {
                    checkType: 'codeValidator',
                    checkVal: editItem.tenantCode
                }, {
                    checkType: 'required',
                    checkVal: editItem.tenantName
                }, {
                    checkType: 'nameValidator',
                    checkVal: editItem.tenantName
                }, {
                    checkType: 'phone',
                    checkVal: editItem.phone
                }, {
                    checkType: 'email',
                    checkVal: editItem.email
                }
            ];
            if (formValidation(formCheck) && !codeUnique) {
                yield put({
                    type: 'updateState',
                    payload: {
                        saving: true
                    }
                });
                const data = yield call(updateTenant, handleEditItem(defaultCloumns, editItem));
                if (data && data.result == 0) {
                    yield put({
                        type: 'updateEditItem',
                        payload: {
                            visible: false
                        }
                    });
                    yield put({type: 'getTreeData'});
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
        *getDivisionTreeData({
            payload
        }, {select, call, put}) {
            let { key, resolve} = payload;
            const {treeData} = yield select(({tenant}) => tenant);
            const data = yield call(getDivisionTree, {id: key});
            if (data && data.result == 0) {
                let daliy = (d)=>{
                    return d.map((item,index)=>{
                        if(item.key == key){
                            return {
                                ...item,
                                children: handleTreeData(data.data)
                            }
                        }else{
                            return {
                                ...item,
                                children: item.children?daliy(item.children):[]
                            }
                        }
                    })
                }
                console.log(daliy(treeData))
                yield put({type: 'updateState',payload: {treeData: daliy(treeData)}});
                if(typeof resolve == 'function'){
                    return resolve();
                }else {
                    const treeData = handleTreeData(data.data);
                    yield put({type: 'updateState', payload: {
                        treeData
                    }});
                }
                
            } else {
                if (data && data.exception) {
                    message.error(data.exception);
                }
            }
        },
        *validateTenant({
            payload
        }, {select, call, put}) {
            const data = yield call(validateTenant, {
                key: payload.obj.key,
                param: {
                    ...payload.obj
                }
            });
            if (data && data.result == 0) {
                if (payload.obj.key == "domain") {
                    yield put({
                        type: 'updateState',
                        payload: {
                            domainUnique: !data.data
                        }
                    });
                } else if (payload.obj.key == "tenantCode") {
                    yield put({
                        type: 'updateState',
                        payload: {
                            codeUnique: !data.data
                        }
                    });
                }
            }
        },
        *validateAccount({
            payload
        }, {select, call, put}) {
            const data = yield call(validateTenantAccount, {
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
        *processTenant({
            payload
        }, {select, call, put}) {
            let data;
            if (payload.opType == 'disable') {
                data = yield call(disableTenant, {ids: payload.id});
            } else if (payload.opType == 'enable') {
                data = yield call(enableTenant, {ids: payload.id});
            }
            if (data && data.result == 0) {
                yield put({type: 'getTableData'});
                message.success(data.msg, 3);
            } else {
                if (data && data.exception) {
                    message.error(data.exception);
                }
            }
        },
        *getDtoById({payload},{select,call,put}){
            const data=yield call(getTenantDtoById,{
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
        *getCloudSystemTableData({
            payload
        }, {select, call, put}) {
            const {cloudData} = yield select(({tenant}) => tenant);
            yield put({
                type: 'updateCloudData',
                payload: {
                    tableLoading: true,
                    selectedRowKeys: []
                }
            });
            let data = yield call(getCloudSystemTableData, {
                page: cloudData.currentPageIndex,
                rows: cloudData.pageSize,
                systemName: cloudData.systemName.trim(),
                tenantId: (payload && payload.tenantId)?payload.tenantId:cloudData.tenantId
            });
            data = {
                result: '0',
                data: data
            }
            if (data && data.result == 0) {
                yield put({
                    type: 'updateCloudData',
                    payload: {
                        tableData: handleCloudSystemTableData(data.data.rows),
                        totalItems: data.data.total
                    }
                });
            } else {
                if (data && data.exception) {
                    message.error(data.exception);
                }
            }
            yield put({
                type: 'updateCloudData',
                payload: {
                    tableLoading: false
                }
            });
        },
        *processCloudSystem({payload}, {select, call, put}) {
            const {cloudData} = yield select(({tenant}) => tenant);
            let data;
            if (payload.opType == 'disable') {
                data = yield call(disableCloudSystemt, {id: payload.id});
            } else if (payload.opType == 'enable') {
                data = yield call(enableCloudSystem, {
                    tenantId: cloudData.tenantId,
                    cloudSystemId: payload.id
                });
            }
            if (data && data.result == 0) {
                yield put({type: 'getCloudSystemTableData'});
                message.success(data.msg, 3);
            } else {
                if (data && data.exception) {
                    message.error(data.exception);
                }
            }
        },
        *getFunctionPermissionMap({payload},{select,call,put}){
            const {functionCodes} = yield select(({tenant})=>tenant);
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
    reducers : {
        updateState(state, action) {
            return {
                ...state,
                ...action.payload
            }
        },
        updateNewItem(state, action) {
            return {
                ...state,
                newItem: {
                    ...state.newItem,
                    ...action.payload
                }
            }
        },
        updateEditItem(state, action) {
            return {
                ...state,
                editItem: {
                    ...state.editItem,
                    ...action.payload
                }
            }
        },
        updateViewItem(state, action) {
            return {
                ...state,
                viewItem: {
                    ...state.viewItem,
                    ...action.payload
                }
            }
        },
        updateWindowMapInfo(state, action) {
            return {
                ...state,
                windowMapInfo: {
                    ...state.windowMapInfo,
                    ...action.payload
                }
            }
        },
        updateCloudData(state, action) {
            return {
                ...state,
                cloudData: {
                    ...state.cloudData,
                    ...action.payload
                }
            }
        },
        //清空
        clearNewItem(state, action) {
            return {
                ...state,
                newItem: {
                    visible: true,
                    checkState: false,

                    id: '',
                    tenantCode: '',
                    tenantName: '',
                    domain: '',
                    menuUrl: '',
                    navigationUrl: '',
                    contact: '',
                    phone: '',
                    email: '',
                    latitude: '',
                    latitudeDone: '',
                    longitude: '',
                    longitudeDone: '',
                    enabled: '',
                    divisionId: '',
                    divisionName: '',
                    userName: '',
                    password: '',
                    confirm_password: ''
                }
            }
        },
        // 初始化表格状态数据
        initTableOpt(state, action) {
            return {
                ...state,
                currentPageIndex: 1,
                selectedRowKeys: [],
                pageSize: 10
            }
        },
        initSearchMap(state, action) {
            return {
                ...state,
                mapCenter: [116.483917, 39.929221]
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
    }
};

function delay(time) {
    let promise = new Promise((resolve) => {
        setTimeout(resolve, time);
    });
    return promise;
}
function handleCloudSystemTableData(data){
    if(typeof(data) == 'undefined'){
        data = [];
    }
    return data.map((item)=>{
        return {
            ...item,
            key: item.cloudSystemId
        }
    });
}