import {getDivisionTenantTable, saveDivisionTenant,updateDivisionTenant,deletesDivisionTenant,DivisionCommonCode,getLoginInfo,
    validateXzqhCode,loadTreeAsync,getDivisionTenantTree,batchDivisionTenant,getDivisionTenantDtl,getFunctionPermissionMap} from '../../../services/remoteData';
import {handleTableData,handleTreeData,formValidation} from '../../../utils/toolFunctions';
import {message} from 'antd';
export default {
	namespace:'tenantxzqu',
	state: {
		tableLoading:false,
        tableData: [],
        routeList:[],
        currentPageIndex:1,
        pageSize:10,
        totalItems:0,
        selectedRowKeys:[],
        expandedkeys:['-1'],
        newWindow:false,
        viewWindow:false,
        editWindow:false,
        mapWindow:false,
        editLoading:false,
        operatedRow:{},
        searchTreeId: '',
        treeName: '',
        treeNodeId:'-1',
        saving:false,
        selectedRow:[],
        parentName:"",
        mapId:'',
        newItem:{
        	loading:false,
        	visible:false,
      		checkState:false,
        	parentName: '',
	        parentId: '-1',
            name: '',
            abbr: '',
            commonCode: '',
            level: '1',
            levelText:'',
            lonLatStr: '',
            orderIndex: '',
            latitude: '',
            longitude: '',
            startTime:'',
            checkOrder:false,
            scope:''
        },
        editItem:{
        	loading:false,
        	visible:false,
      		checkState:true,
        },
        viewItem:{
        	visible:false,
        },
        windowMapInfo: {
            showMap: false,
            mapPoints: [],
            mapLines: [],
            mapPolygons:[],
            centerPoint: [],
            isDraw: false,
            isClearAll:false,
            editGraphicId:'',
            mapDraw:{geometryType: 'polyline',data: {id: 'workE'}}
        },
        modal1Visible: false,
        modal2Visible:false,
        mapCenter: [
            116.483917, 39.929221
        ],
        mapCenter2: [
            116.483917, 39.929221
        ],
        treeData: [],
        searchCode: '',
        searchName: '',
        accountUnique:false,
        functionCodes: ['CF_MANAGE_TENANT_XZQH_LIST','CF_MANAGE_TENANT_XZQH_ADD','CF_MANAGE_TENANT_XZQH_UPDATE','CF_MANAGE_TENANT_XZQH_VIEW','CF_MANAGE_TENANT_XZQH_DEL'],
        functionCodesMap: {},
        mapType: 'bmap',
        shapeType: 'polyline'
	},
	subscriptions: {
  		setup({ dispatch, history }) {
            history.listen(({ pathname }) => {
                if (pathname === '/tenantxzqh') {
                    dispatch({type:'getFunctionPermissionMap'});
                    dispatch({type:'getTreeData'});
                    dispatch({type:'getLoginInfo'});          
                }
            });
        }, 
  },
  effects: {
  	/*获取列表数据*/
		*getTableData({ payload }, { call, put,select }){
            const state = yield select(({tenantxzqu})=>tenantxzqu);
            yield put({type:'updateState',payload:{tableLoading:true}});
            var searchTreeId = state.searchTreeId;
            if(state.searchTreeId == ""){
                searchTreeId = '-1';
            }
            const params={
            	pageIndex: state.currentPageIndex,
                pageSize: state.pageSize,
                parentId: searchTreeId,
            }
            const data  = yield call(getDivisionTenantTable, params);
           // console.log(data);
            if(data && data.result == 0) {
                const tableData = handleTableData(data.data.rows);
                const totalItems = data.data.total;
                yield put({type: 'updateState', payload: {tableData, totalItems, tableLoading: false,selectedRowKeys:[]}});
            }else {
                message.error("加载列表数据失败；");
            }
            yield put({type:'updateState',payload:{tableLoading:false}});
        },
        *getTreeData({payload},{select,call,put}){
            const {searchTreeId} = yield select(({tenantxzqu})=>tenantxzqu);
	        const data=yield call(getDivisionTenantTree,{
	        });
	        if(data && data.result == 0){
	            const treeData=handleTreeData(JSON.parse(data.data).items);
	            yield put({type:'updateState',payload:{treeData,searchTreeId:searchTreeId?searchTreeId:treeData[0].key,parentName:treeData[0].name}});
                yield put({type:"getTableData"});
	        }else {
	            if(data && data.exception){
	                message.error(data.exception);
	            }
	        }
	    },
        *getDivisionTenantDtl({payload},{put,call,select}){
            const {mapType}=yield select(({tenantxzqu})=>tenantxzqu);
            const data=yield call(getDivisionTenantDtl,{id:payload.id,mapType});
            if(data && data.result==0){
                yield put({type:'updateState',payload:{
                    operatedRow:data.data
                }});
                if(payload.type=='view'){
                    yield put({type:'updateViewItem',payload:{
                        ...data.data,
                        visible: true
                    }});
                }else {
                    yield put({type:'updateEditItem',payload:{
                        ...data.data,
                        visible: true
                    }});
                }
            }
        },
        *getLoginInfo({payload},{select,call,put}){
            const data=yield call(getLoginInfo);
			if(data && data.result == 0){
                let json = data.data.mapDefJson?JSON.parse(data.data.mapDefJson):[];
                let mapType = 'bmap';
                json.map((item)=>{
                    if(item.defaultMap){
                        mapType=item.mapType;
                    }
                })
                yield put({type: 'updateState',payload: {mapType}});

                if(data.data.latitudeDone && data.data.longitudeDone)
				yield put({type:'updateState',payload:{
                    setCenter: true,
                    mapCenter:[data.data.longitudeDone,data.data.latitudeDone],
                    mapCenter2:[data.data.longitudeDone,data.data.latitudeDone]
                }});
                yield call(delay,10);
                yield put({type:'updateState',payload:{
                    setCenter: false
                }});
			}else {
				if(data && data.exception){
					message.error(data.exception);
				}
			}
        },
        *addDivision({ payload }, { call, put,select }){
            const {newItem,searchTreeId,mapType,shapeType}=yield select(({tenantxzqu})=>tenantxzqu);
	        let formCheck = [
	            {checkType:'required', checkVal: newItem.name },
	            {checkType:'nameValidator', checkVal: newItem.name },
	            {checkType:'required', checkVal: newItem.abbr },
	            {checkType:'nameValidator', checkVal: newItem.abbr },
	            {checkType:'required', checkVal: newItem.commonCode },
	            {checkType:'number', checkVal:newItem.commonCode },
	            {checkType:'required', checkVal: newItem.level },
	            {checkType:'required', checkVal: newItem.startTime },
	        ];
	        if(newItem.orderIndex || newItem.orderIndex==0){
	        	formCheck.push({checkType:'number', checkVal:newItem.orderIndex});
	        }
            let param={
                ...newItem,
                parentId:searchTreeId,
                mapType,shapeType
            }
	        if(formValidation(formCheck)){
                yield put({type:'updateNewItem',payload:{loading:true}});
	            const data = yield call(saveDivisionTenant ,param);
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
	            yield put({type:'updateNewItem',payload:{loading:false}});
	        }
        },
        *updateDivision({ payload }, { call, put,select }){
            const state=yield select(({tenantxzqu})=>tenantxzqu);
            const {id,name,code,abbr,parentName,parentId,commonCode,level,levelText,lonLatStr,orderIndex,
            latitude,longitude,startTime,scope,mapType,shapeType}=state.editItem;
	        let formCheck = [
	            {checkType:'required', checkVal: name },
	            {checkType:'nameValidator', checkVal: name },
	            {checkType:'required', checkVal: abbr },
	            {checkType:'nameValidator', checkVal: abbr },
	            {checkType:'required', checkVal: commonCode },
	            {checkType:'number', checkVal:commonCode },
	            {checkType:'required', checkVal: level },
	            {checkType:'required', checkVal: startTime },
	        ];
	        if(orderIndex || orderIndex==0){
	        	formCheck.push({checkType:'number', checkVal:orderIndex});
	        }
	        if(formValidation(formCheck)){
                yield put({type:'updateEditItem',payload:{loading:true}});
                const params={
                    id,name,code,abbr,parentName,parentId,commonCode,level,levelText,lonLatStr,orderIndex,
                    latitude,longitude,startTime,scope,mapType,shapeType
                }
	            const data = yield call(updateDivisionTenant ,params);
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
	            yield put({type:'updateEditItem',payload:{loading:false}});
	        }
        },
        
        *deletesDivision({ payload }, {call, put, select}){
          const {selectedRowKeys}=yield select(({tenantxzqu})=>tenantxzqu);
          const data  = yield call(deletesDivisionTenant,payload&&payload.id?JSON.stringify([payload.id]):JSON.stringify(selectedRowKeys));
          //console.log(data);
            if(data && data.result == 0) {
                message.success(data.msg);
                yield put({type:'updateState',payload:{selectedRowKeys:[]}});
                yield put({type:'getTreeData',payload:{}});
                yield put({type:'getTableData'});
            }else {
                message.error('删除失败；');
            }
        },
        *validateAccount({payload},{select,call,put}){
            const data = yield call(DivisionCommonCode, {
                ...payload.obj
            });
            if(data && data.result==0){
                yield put({
                    type: 'updateState',
                    payload: {
                        accountUnique: !data.data
                    }
                });
            }
        },
        *batchDivisionTenant({payload},{call,put,select}){
            const {selectedRow}=yield select(({tenantxzqu})=>tenantxzqu);
            const data=yield call(batchDivisionTenant,JSON.stringify(selectedRow));
            if(data && data.result==0){
                message.success('批量保存成功；');
                yield put({type:'updateState',payload:{selectedRowKeys:[]}});
                yield put({type:'getTreeData',payload:{}});
                yield put({type:'getTableData'});
            }else{
               message.error('批量保存失败；'); 
            }

        },
        *getFunctionPermissionMap({payload},{select,call,put}){
            const {functionCodes} = yield select(({tenantxzqu})=>tenantxzqu);
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
  		return {
  			...state,...action.payload
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
    updateOperateRow(state,action){
        return {
            ...state,
            operatedRow:{
                ...state.operatedRow,
                ...action.payload
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
     initWindowMapInfo(state, action){
        return{
            ...state,
            windowMapInfo: {
                showMap: false,
	            mapPoints: [],
	            mapLines: [],
                mapPolygons:[],
	            centerPoint: [],
	            isDraw: false,
	            isClearAll:true,
	            editGraphicId:'',
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
    clearNewItem(state,action){
    	return {
    		...state,
    		newItem:{
	        	visible:true,
	      		checkState:false,
	        	parentName: '',
		        parentId: '-1',
	            name: '',
	            abbr: '',
	            code: '',
	            level: '',
	            lonLatStr: '',
	            orderIndex: '',
	            latitude: '',
	            latitudeDone: '',
	            longitude: '',
	            longitudeDone: '',
    		}
    	}
    }
  }
}
function delay(timeout){
    var pro = new Promise(function(resolve,reject){
      setTimeout(resolve, timeout);
    });
    return pro;
  }