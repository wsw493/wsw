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
} from '../../utils/toolFunctions';
import {getCustomValue,getSourceTenantList,getTargetTenantList,getSourceSystemList,getTargetSystemList,
        getSourceMenuTree,getTargetMenuTree,startToCopy} from '../../services/remoteData';
export default{
	namespace:'copyMenuResource',
	state:{
        sourceUrl: '',
        targetUrl: '',

        sourceTenant: '',
        targetTenant: '',

        sourceSystem: '',
        targetSystem: '',

        sourceTenantList: [],
        targetTenantList: [],

        sourceSystemList: [],
        targetSystemList: [],

        sourceMenuTreeData: [],
        targetMenuTreeData: [],
        checkedKeys: [],
        selectedRoleKeys: [],

        saving: false
	},
	subscriptions:{
		setup({ dispatch, history }) {
            history.listen(({ pathname }) => {
                if (pathname === '/copymenuresource') {
                     dispatch({type:'getCustomValue'});
                     dispatch({type:'getSourceTenantList'});   
                     dispatch({type:'getTargetTenantList'});          
                }
            });
        }, 
	},
	effects:{
		*getCustomValue({payload},{put,select,call}){
			const data1 = yield call(getCustomValue ,{
				propertyName: 'jdbc.url'
            });
            const data2 = yield call(getCustomValue ,{
				propertyName: 'jdbc.url2'
			});
			if(data1 && data1.result==0){
                var data = data1.data;
                yield put({type:'updateState',payload:{sourceUrl:data.url.substring(data.url.indexOf("//")+2, data.url.indexOf("?"))}});
            }
            if(data2 && data2.result==0){
                var data = data2.data;
                yield put({type:'updateState',payload:{targetUrl:data.url.substring(data.url.indexOf("//")+2, data.url.indexOf("?"))}});
            }
        },
        *getSourceTenantList({payload},{put,select,call}){
            const data = yield call(getSourceTenantList,{});
            if(data && data.result==0){
                yield put({type:'updateState',payload:{sourceTenantList:data.data.map((item)=>{return {...item,key:item.id,name:item.tenantName}})}});
                if(data.data.length!=0){
                    yield put({type:'updateState',payload:{sourceTenant:data.data[0].id}});
                }
                yield put({type:'getSourceSystemList'});
            }else {
                if(data && data.exception){
                    message.error(data.exception);
                }
            }
        },
        *getTargetTenantList({payload},{put,select,call}){
            const data = yield call(getTargetTenantList,{});
            // yield put({type:'updateState',payload:{targetTenantList:data}});
            // if(data.length!=0){
            //     yield put({type:'updateState',payload:{targetTenant:data[0].id}});
            // }
            // yield put({type:'getTargetSystemList'});

            if(data && data.result==0){
                yield put({type:'updateState',payload:{targetTenantList:data.data.map((item)=>{return {...item,key:item.id,name:item.tenantName}})}});
                if(data.data.length!=0){
                    yield put({type:'updateState',payload:{targetTenant:data.data[0].id}});
                }
                yield put({type:'getTargetSystemList'});
            }else {
                if(data && data.exception){
                    message.error(data.exception);
                }
            }
        },
        *getSourceSystemList({payload},{put,select,call}){
            const {sourceTenant} = yield select(({copyMenuResource}) => copyMenuResource);
            const data = yield call(getSourceSystemList,{
                tenantId: sourceTenant
            });
            if(data && data.result==0){
                yield put({type:'updateState',payload:{sourceSystemList:data.data.map((item)=>{return {...item,key:item.id,name:item.systemName}})}});
                if(data.data.length!=0){
                    yield put({type:'updateState',payload:{sourceSystem:data.data[0].id}});
                }
                yield put({type:'getSourceMenuTree'});
            }else {
                if(data && data.exception){
                    message.error(data.exception);
                }
            }
        },
        *getTargetSystemList({payload},{put,select,call}){
            const {targetTenant} = yield select(({copyMenuResource}) => copyMenuResource);
            const data = yield call(getTargetSystemList,{
                tenantId: targetTenant
            });

            // yield put({type:'updateState',payload:{targetSystemList:data}});
            // if(data && data.length!=0){
            //     yield put({type:'updateState',payload:{targetSystem:data[0].id}});
            // }
            // yield put({type:'getTargetMenuTree'});

            if(data && data.result==0){
                yield put({type:'updateState',payload:{targetSystemList:data.data.map((item)=>{return {...item,key:item.id,name:item.systemName}})}});
                if(data.data.length!=0){
                    yield put({type:'updateState',payload:{targetSystem:data.data[0].id}});
                }
                yield put({type:'getTargetMenuTree'});
            }else {
                if(data && data.exception){
                    message.error(data.exception);
                }
            }
        },
        *getSourceMenuTree({payload},{put,select,call}){
            const {sourceSystem} = yield select(({copyMenuResource}) => copyMenuResource);
            const data = yield call(getSourceMenuTree,{
                systemId: sourceSystem
            });
            if(data && data.result==0){
                var sourceMenuTreeData = handleTreeData(JSON.parse(data.data).items);
                yield put({type:'updateState',payload:{sourceMenuTreeData}});
            }else {
                if(data && data.exception){
                    message.error(data.exception);
                }
            }
        },
        *getTargetMenuTree({payload},{put,select,call}){
            const {targetSystem} = yield select(({copyMenuResource}) => copyMenuResource);
            const data = yield call(getTargetMenuTree,{
                systemId: targetSystem
            });
            // var targetMenuTreeData = handleTreeData(data.items);
            // yield put({type:'updateState',payload:{targetMenuTreeData}});
            if(data && data.result==0){
                var targetMenuTreeData = handleTreeData(JSON.parse(data.data).items);
                yield put({type:'updateState',payload:{targetMenuTreeData}});
            }else {
                if(data && data.exception){
                    message.error(data.exception);
                }
            }
        },
        *startToCopy({payload},{put,select,call}){
            const {targetSystem,sourceSystem,selectedRoleKeys} = yield select(({copyMenuResource}) => copyMenuResource);
            yield put({type:'updateState',payload:{saving:true}});
            const data = yield call(startToCopy,{
                sourceSystemId: sourceSystem,
                targetSystemId: targetSystem,
                smenus: selectedRoleKeys.join(',')
            });
            if(data && data.result==0){
                yield put({type:'getSourceMenuTree'});
                yield put({type:'getTargetMenuTree'});
            }else {
                if(data && data.exception){
                    message.error(data.exception);
                }
            }
            yield put({type:'updateState',payload:{saving:false}});
        },
	},
	reducers:{
		updateState(state,action){
			return {
				...state,...action.payload
			}
		}
	}
}