import React from 'react';
import {connect} from 'dva';
import {message} from 'antd';
import {toLogin} from '../../../utils/request';
import {
    handleTableData,
    handleTreeData,
    handleRestResultData,
    handleEditItem,
    getBaicPostData,
    formValidation
} from '../../../utils/toolFunctions';
import {
    getTenantUserTable,
    saveTenantUser,
    updateTenantUser,
    deleteTenantUser,
    deletesTenantUser,
    validateAccount,
    getFunctionPermissionMap,
    getTenantUserDtoById,
    validatePhone,
    resetTenantUserPassword
} from '../../../services/remoteData';

export default {
    namespace : 'tenantUser',
    state : {
        tableData: [],
        userName: '',
        phone: '',
        selectedRowKeys: [],
        tableLoading: true,
        currentPageIndex: 0,
        pageSize: 10,
        totalItems: 0,
        newItem: {
            visible: false,
            checkState: false,

            id: '',
            phone: '',
            userName: '',
            password: '',
            confirm_password: '',
            birthday: '',
            gender: 'M',
            profilePhoto: '[]'
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
        passwordFlag: false,
        phoneUnique: false,
        accountUnique: false,
        fileListVersion: 1,
        defaultCloumns: [
            'id','gender','birthday','profilePhoto','phone'
        ],
        functionCodes: ['CF_MANAGE_TENANT_LIST','CF_MANAGE_TENANT_ADD','CF_MANAGE_TENANT_UPDATE','CF_MANAGE_TENANT_VIEW'],
        functionCodesMap: {
            'CF_MANAGE_TENANT_LIST': true,
            'CF_MANAGE_TENANT_ADD': true,
            'CF_MANAGE_TENANT_UPDATE': true,
            'CF_MANAGE_TENANT_VIEW': true,
        }
    },
    subscriptions : {
        setup({dispatch, history}) {
            history.listen(({pathname,query}) => {
                if (pathname === '/tenantuser') {
                    // dispatch({type:'getFunctionPermissionMap'});
                    dispatch({type: 'getTableData'});
                }
            });
        }
    },
    effects : {
        *getTableData({
            payload
        }, {select, call, put}) {
            const state = yield select(({tenantUser}) => tenantUser);
            yield put({
                type: 'updateState',
                payload: {
                    tableLoading: true,
                    selectedRowKeys: []
                }
            });
            const data = yield call(getTenantUserTable, {
                page: state.currentPageIndex,
                rows: state.pageSize,
                phone: state.phone.trim(),
                userName: state.userName.trim()
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
            yield put({type: 'updateState',payload: {tableLoading: false}});
        },
        *addTenantUser({
            payload
        }, {select, call, put}) {
            const {newItem, phoneUnique, accountUnique, passwordFlag} = yield select(({tenantUser}) => tenantUser);
            let formCheck = [
                { checkType: 'required',checkVal: newItem.userName },
                { checkType: 'required',checkVal: newItem.password },
                { checkType: 'required',checkVal: newItem.phone },
                { checkType: 'phone',checkVal: newItem.phone },
            ];
            if (formValidation(formCheck) && !phoneUnique && !accountUnique && !passwordFlag) {
                yield put({type: 'updateState',payload: {saving: true}});
                const data = yield call(saveTenantUser, {...newItem,password:hex_md5(newItem.password)});
                if (data && data.result == 0) {
                    yield put({type: 'updateNewItem',payload: {visible: false}});
                    yield put({type: 'getTableData'});
                    message.success(data.msg, 3);
                } else {
                    if (data && data.exception) {
                        message.error(data.exception);
                    }
                }
                yield put({type: 'updateState', payload: {saving: false}});
            }
        },
        *updateTenantUser({
            payload
        }, {select, call, put}) {
            const {editItem , defaultCloumns} = yield select(({tenantUser}) => tenantUser);
            let formCheck = [ ];
            if (formValidation(formCheck)) {
                yield put({type: 'updateState',payload: {saving: true}});
                const data = yield call(updateTenantUser, handleEditItem(defaultCloumns, editItem));
                if (data && data.result == 0) {
                    yield put({ type: 'updateEditItem',payload: {visible: false}});
                    yield put({type: 'getTableData'});
                    message.success(data.msg, 3);
                } else {
                    if (data && data.exception) {
                        message.error(data.exception);
                    }
                }
                yield put({type: 'updateState', payload: {saving: false}});
            }
        },
        *validatePhone({
            payload
        }, {select, call, put}) {
            const data = yield call(validatePhone, {
                ...payload.obj
            });
            if (data && data.result == 0) {
                yield put({
                    type: 'updateState',
                    payload: {
                        phoneUnique: !data.data
                    }
                });
            }
        },
        *validateAccount({
            payload
        }, {select, call, put}) {
            const data = yield call(validatePhone, {
                ...payload.obj
            });
            if (data && data.result == 0) {
                yield put({
                    type: 'updateState',
                    payload: {
                        accountUnique: !data.data
                    }
                });
            }
        },
        *resetPassword({
            payload
        }, {select, call, put}) {
            const data = yield call(resetTenantUserPassword, {
                id: payload.id
            });
            if (data && data.result == 0) {
                message.success(data.msg);
            }else {
                message.error(data.exception);
            }
        },
        *getDtoById({payload},{select,call,put}){
            const {fileListVersion} = yield select(({tenantUser})=>tenantUser);
            const data=yield call(getTenantUserDtoById,{
                id: payload.id
            });
            if(data && data.result == 0){
                yield put({type:'updateEditItem',payload:{...data.data}});
                yield put({type:'updateViewItem',payload:{...data.data}});
                yield put({type:'updateState',payload:{fileListVersion: fileListVersion+1}});
            }else {
                if(data && data.exception){
                    message.error(data.exception);
                }
            }
        },
        *deletesTenantUser({payload},{select,call,put}){
            const {selectedRowKeys}=yield select(({tenantUser})=>tenantUser);
            const data=yield call(deletesTenantUser, JSON.stringify(selectedRowKeys));
            if(data && data.result == 0){
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
            const {functionCodes} = yield select(({tenantUser})=>tenantUser);
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
        //清空
        clearNewItem(state, action) {
            return {
                ...state,
                newItem: {
                    visible: true,
                    checkState: false,

                    id: '',
                    phone: '',
                    userName: '',
                    password: '',
                    confirm_password: '',
                    birthday: '',
                    gender: 'M',
                    profilePhoto: '[]'
                }
            }
        },
        // 初始化表格状态数据
        initTableOpt(state, action) {
            return {
                ...state,
                currentPageIndex: 0,
                selectedRowKeys: [],
                pageSize: 10
            }
        }
    }
};
