import React from 'react';
import {connect} from 'dva';
import {message} from 'antd';
import {toLogin} from '../../utils/request';
import {handleTableData} from '../../utils/toolFunctions';
import {getTenantTable,getAllTenantList,bindTenantSaving,getHadBindTenantList} from '../../services/remoteData';

export default {
    namespace : 'bindTenant',
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
        saving: false,
        sort: '',
        order: '',
        dataSource: [],
        targetKeys: [],
        bindTenantWindow: false,
        mainTenantId: ''
    },
    subscriptions : {
        setup({dispatch, history}) {
            history.listen(({pathname,query}) => {
                if (pathname === '/bindtenant') {
                    dispatch({type: 'getTableData'});
                }
            });
        }
    },
    effects : {
        *getTableData({payload}, {select, call, put}) {
            const state = yield select(({bindTenant}) => bindTenant);
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
                tenantName: state.tenantName.trim(),
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
        *getAllTenantList({payload}, {select, call, put}) {
            const {dataSource,mainTenantId} = yield select(({bindTenant}) => bindTenant);
            const data = yield call(getAllTenantList, {});
            if (data && data.result == 0) {
                let arr = [];
                data.data.map((item)=>{
                    if (mainTenantId != item.tenantId){
                        arr.push({
                            key: item.tenantId,
                            title: item.tenantName
                        })
                    }
                })
                yield put({type: 'updateState',payload: {
                        dataSource: arr
                    }
                });
                yield put({type: 'getHadBindTenantList'});
            } else {
                if (data && data.exception) {
                    message.error(data.exception);
                }
            }
        },
        *getHadBindTenantList({payload}, {select, call, put}) {
            const {mainTenantId,dataSource} = yield select(({bindTenant}) => bindTenant);
            const data = yield call(getHadBindTenantList, {
                parameters:JSON.stringify({
                    mainTenantId:mainTenantId
                })
            });
            if (data && data.result == 0) {
                yield put({type: 'updateState',payload: {
                        dataSource: [...dataSource,...data.data.map((item)=>{
                            return {
                                key: item.tenantId,
                                title: item.tenantName
                            }
                        })],
                        targetKeys: data.data.map((item)=>{
                            return item.tenantId
                        })
                    }
                });
            } else {
                if (data && data.exception) {
                    message.error(data.exception);
                }
            }
        },
        *bindTenantSaving({payload}, {select, call, put}) {
            const {mainTenantId,targetKeys} = yield select(({bindTenant}) => bindTenant);
            yield put({type: 'updateState',payload: {saving:true}});
            const data = yield call(bindTenantSaving, {
                parameters:JSON.stringify({
                    mainTenantId,
                    ids: targetKeys.toString()
                })
            });
            if (data && data.result == 0) {
                message.success(data.msg);
                yield put({type: 'updateState',payload: {bindTenantWindow:false}});
            } else {
                if (data && data.exception) {
                    message.error(data.exception);
                }
            }
            yield put({type: 'updateState',payload: {saving:false}});
        }
    },
    reducers : {
        updateState(state, action) {
            return {
                ...state,
                ...action.payload
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
        }
    }
};
