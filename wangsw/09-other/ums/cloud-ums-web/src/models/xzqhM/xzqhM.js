import {getDivisionTemplateTable, saveDivisionTemplate,updateDivisionTemplate,deletesDivisionTemplate,TemplateCommonCode,
    validateXzqhCode,loadTreeAsync,getDivisionTemplateTree,batchDivisionTemplate,getRootDivisionId,getTemplateDetailByid,getFunctionPermissionMap} from '../../services/remoteData';
import {handleTableData,handleTreeData,formValidation} from '../../utils/toolFunctions';
import {message} from 'antd';
const  getNewTreeData=(treeData, curKey,child)=> {
  const loop = (data) =>{
     return data.map((item) => {
        if (item.key==curKey) {
            return {
                ...item,
                children: child
            }
        }else{
            return {
                ...item,
                children: item.children?loop(item.children):''
            }
        }
    });
  };
  return loop(treeData);
}
const treeDataHandlerd = (data) => {
    return data.map((item) => {
        return {
            "key":item.id,
            "name" :item.name,
            "isLeaf":!item.isParent
            //"parentId":item.parentId
        }
    });
}
export default {
	namespace:'xzqh',
	state: {
		tableLoading:false,
        tableData: [],
        routeList:[],
        currentPageIndex:1,
        pageSize:10,
        totalItems:0,
        selectedRowKeys:[],
        expandedkeys:[],
        newWindow:false,
        viewWindow:false,
        editWindow:false,
        mapWindow:false,
        editLoading:false,
        operatedRow:{},
        searchTreeId: '',
        treeName: '中国',
        treeNodeId:'',
        saving:false,
        selectedRow:[],
        parentName:'',
        mapId:'',
        newItem:{
            id:'',
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
            tenantId:'',
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
            centerPoint: [],
            isDraw: false,
            isClearAll:false,
            editGraphicId:'',
            mapDraw:{geometryType: 'polyline',data: {id: 'workE'}}
        },
        modal1Visible: false,
        mapCenter: [
            116.483917, 39.929221
        ],
        treeData: [],
        searchCode: '',
        searchName: '',
        accountUnique:false,
        functionCodes: ['CF_MANAGE_XZQH_LIST','CF_MANAGE_XZQH_ADD','CF_MANAGE_XZQH_UPDATE','CF_MANAGE_XZQH_DEL','CF_MANAGE_XZQH_VIEW'],
        functionCodesMap: {}
	},
	subscriptions: {
  		setup({ dispatch, history }) {
            history.listen(({ pathname }) => {
                if (pathname === '/xzqh') {
                    dispatch({type:'getFunctionPermissionMap'});
                      dispatch({type:'getTreeData',payload:{}});  
                      //dispatch({type:"getTableData"});

                }
            });
        }, 
  },
  effects: {
  	/*获取列表数据*/
		*getTableData({ payload }, { call, put,select }){
            const state = yield select(({xzqh})=>xzqh);
            yield put({type:'updateState',payload:{tableLoading:true}});
            var searchTreeId = state.searchTreeId;
            /*if(state.searchTreeId == ""){
                searchTreeId = '-1';
            }*/
            const params={
            	pageIndex: state.currentPageIndex,
                pageSize: state.pageSize,
                parentId: searchTreeId,
            }
            const data  = yield call(getDivisionTemplateTable, params);
            if(data && data.result == 0) {
                const tableData = handleTableData(data.data.rows);
                const totalItems = data.data.total;
                yield put({type: 'updateState', payload: {tableData, totalItems, tableLoading: false,selectedRowKeys:[]}});
            }else {
                message.error("加载列表数据失败；");
            }
            yield put({type:'updateState',payload:{tableLoading:false}});
        },
        *getTreeData({ payload }, { call, put,select }){
            const {treeNodeId,treeData}=yield select(({xzqh})=>xzqh);
            const data=yield call(loadTreeAsync,{id:treeNodeId});
            if(data && data.result == 0) { 
                if(!treeNodeId){
                    const tree =treeDataHandlerd(data.data);
                    yield put({type: 'updateState', payload: {treeData:tree}}); 
                    yield put({type:'getRootDivisionId'});
                }else{
                    const child =treeDataHandlerd(data.data);
                    let tree = getNewTreeData(treeData,treeNodeId,child);
                    yield put({type: 'updateState', payload: {treeData:tree}});
                }
                //关闭加载动画的
                if(payload && payload.resolve){
                    return payload.resolve();
                }
            }else {
                message.error("行政区划树加载失败；");
                return payload.resolve();
            }
        },
        *getRootDivisionId({payload},{call,put,select}){
            const {treeNodeId,treeData,expandedkeys}=yield select(({xzqh})=>xzqh);
            const data=yield call(getRootDivisionId);
            if(data && data.result==0){
                expandedkeys.push(data.data);
                yield put({type:'updateState',payload:{
                    treeNodeId:data.data,
                    expandedkeys,
                    searchTreeId:data.data
                }});
                yield put({type:'getTreeData',payload:{}});
                yield put({type:'getTableData'});
            }
        },
        *getTemplateDetailByid({payload},{call,put,select}){
            const data=yield call(getTemplateDetailByid,{id:payload.id});
            console.log(data);
            if(data && data.result==0){
                yield put({type:'updateState',payload:{
                    operatedRow:data.data
                }});
            }
        },
        *addDivision({ payload }, { call, put,select }){
            const {newItem,searchTreeId}=yield select(({xzqh})=>xzqh);
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
	        if(newItem.orderIndex.length!=0){
	        	formCheck.push({checkType:'number', checkVal:newItem.orderIndex});
	        }
	        if(formValidation(formCheck)){
                yield put({type:'updateNewItem',payload:{loading:true}});
                let param={
                    ...newItem,
                    parentId:searchTreeId
                }
	            const data = yield call(saveDivisionTemplate ,param);
	            if(data && data.result == 0){
	                yield put({type:'updateNewItem',payload:{visible:false}});
	                yield put({type:'getTreeData',payload:{}});
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
            const state=yield select(({xzqh})=>xzqh);
            const {id,name,code,abbr,parentName,parentId,commonCode,level,levelText,lonLatStr,orderIndex,
            latitude,longitude,startTime}=state.operatedRow;
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
	        if(orderIndex){
	        	formCheck.push({checkType:'number', checkVal:orderIndex});
	        }
	        if(formValidation(formCheck)){
                yield put({type:'updateEditItem',payload:{loading:true}});
                const params={
                    id,name,code,abbr,parentName,parentId,commonCode,level,levelText,lonLatStr,orderIndex,
                    latitude,longitude,startTime
                }
	            const data = yield call(updateDivisionTemplate ,params);
	            if(data && data.result == 0){
	                yield put({type:'updateEditItem',payload:{visible:false}});
	                yield put({type:'getTreeData',payload:{}});
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
          const state = yield select(({xzqh})=>xzqh);
          const data  = yield call(deletesDivisionTemplate, payload&&payload.id?JSON.stringify([payload.id]):JSON.stringify(state.selectedRowKeys));
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
            const data = yield call(TemplateCommonCode, {
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
        *batchSave({payload},{call,put,select}){
            const {selectedRow}=yield select(({xzqh})=>xzqh);
            const data=yield call(batchDivisionTemplate,JSON.stringify(selectedRow));
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
            const {functionCodes} = yield select(({xzqh})=>xzqh);
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
    updateOperateRow(state, action) {
        return {
            ...state,
            operatedRow: {
                ...state.operatedRow,
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