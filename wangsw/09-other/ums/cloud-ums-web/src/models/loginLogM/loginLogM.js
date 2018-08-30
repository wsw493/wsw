import React from 'react';
import {connect} from 'dva';
import {message} from 'antd';
import {handleTableData} from '../../utils/toolFunctions';
import {getLoginLogTable} from '../../services/remoteData';
export default {
    namespace: 'loginLog',
    state: {
        tableData:[],
        selectedRowKeys:[],
        tableLoading:true,
        currentPageIndex: 1,
        pageSize: 10, 
        totalItems: 0,
        userName:'',
        ip:'',
        createTimeStart:'',
        createTimeEnd:''
    },
    subscriptions: {
        setup({ dispatch, history }) {
            history.listen(({ pathname }) => {
                if (pathname === '/loginlog') {
                    dispatch({type:'getTableData'});
                }
            });
        }
    },
    effects: {
        *getTableData({payload},{select,call,put}){
            const state = yield select(({loginLog})=>loginLog);
            yield put({type:'updateState',payload:{tableLoading:true,selectedRowKeys:[]}});
            const data = yield call(getLoginLogTable ,{
                page: state.currentPageIndex,
                rows: state.pageSize,
                userName: state.userName,
                ip: state.ip,
                createTimeStart: state.createTimeStart,
                createTimeEnd: state.createTimeEnd
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
        }
    },
    reducers: {
        updateState(state,action){
            return {...state,...action.payload}
        },
        initTableOpt(state, action){
            return {
                ...state,
                currentPageIndex:1,
                selectedRowKeys:[]
            }
        }
    }
};
