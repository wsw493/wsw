import {getBaicPostData,handleTreeData,formValidation,handleTableData} from '../../utils/toolFunctions';
import {getWorkElementTable, getWorkElementTree, saveWorkElement,getWorkElementDtoById,validateWorkElementCode,
        updateWorkElement,deletesWorkElement,getOrgTreeDataWithPermission,getWorkElementTypeList,validateWorkElementName,
        importWorkElementData,getImportTableData,getDivisionTenantTree,exportWorkelement,getLoginInfo} from '../../services/remoteData';
import {message,notification} from 'antd';
const selectTreedataHandler=(data)=>{
    if("undefined" == typeof(data)){
        data = [];
      }
      var disabled = false;
      return data.map((item) => {
        if (item.length == 0) {
          return {
            key: item.id,
            label: item.name,
            value:item.id,
            disabled: disabled
          }
        }else {
          return {
            key: item.id,
            label: item.name,
            value:item.id,
            disabled: disabled,
            children: selectTreedataHandler(item.children)
          }
        }
        
      });
}
export default {
	namespace: 'workElement',
	state: {
        tableLoading:false,
        tableData: [],
        routeList:[],
        currentPageIndex:1,
        pageSize:10,
        totalItems:0,
        selectedRowKeys:[],
        codeUnique:false,
        nameUnique:false,
        mapWindow:false,
        operatedRow:{},
        typeCode:'',
        newItem: {
            loading:false,
            visible:false,
            checkState:false,
            tenantId: '',
            code: '',
	        name: '',
            workElementTypeId: '',
            shape: '',
            departmentId: '',
            departmentName: '',
            divisionId: '',
            divisionName: '',
            mapType: '2',
            area: '',
            length: '',
            radius: '',
            color: '',
            paramsDone: ''
        },
        editItem:{
            loading:false,
            visible:false,
            checkState:true,
        },
        viewItem:{
            visible:false
        },
        searchName: '',
        searchWorkElementTypeId: '',
        searchOrgId: '',
        workElementTypeList: [],
        isDraw: false,
        mapDraw: {},
        mapPoints: [],
        mapLines: [],
        mapPolygons: [],
        mapCircles: [],
        editGraphicId: '',
        orgTreeData: [],
        divisionTreeData: [],
        isClearAll: false,
        codeValidatFlag: true,
        scrollHeight: '',
        saving:false,
        importWindow: false,
        exportWindow: false,
        isDoEdit: false,
        importDelWindow: false,
        radioG: 'select',
        importDetail: {
            selectedRowKeys: [],
            tableLoading: true,
            currentPageIndex: 1,
            pageSize: 10,
            totalItems: 0,
            tableData: [],
            visible: false,
            successful: ''
        },
        mapCenter: [],
        setCenter: false
    },
    
	subscriptions: {
        setup({ dispatch, history }) {
            // dispatch({type:'fetchRemote'})
            history.listen(({ pathname , query }) => {
                dispatch({type:'updateState',payload:{typeCode:query['typeCode']}});
                if(pathname == '/workelement'){
                    dispatch({type:'getWorkElementTypeList'});
                    dispatch({type:'getOrgTreeData'});
                    dispatch({type:'getTableData'});
                }
            });
        },  
    },

    effects: {
        /*获取列表数据*/
        *getTableData({ payload }, { call, put,select }){
            const state = yield select(({workElement})=>workElement);
            yield put({type:'updateState',payload:{tableLoading:true}});
            var searchOrgId=state.searchOrgId;
            if(state.searchOrgId=='-1'){
                searchOrgId='';
            }
            const data  = yield call(getWorkElementTable, {
                s_LIKE_name:state.searchName.trim(),
                s_EQ_departmentId:searchOrgId,
                s_EQ_workElementTypeId:state.searchWorkElementTypeId,
                typeCode:state.typeCode,
                page:state.currentPageIndex,
                rows:state.pageSize
            });
            if(data) {
                const tableData = handleTableData(data.rows);
                const totalItems = data.total;
                yield put({type: 'updateState', payload: {tableData, totalItems, tableLoading: false,selectedRowKeys:[]}});
            }else {
                message.error("加载列表数据失败；");
            }
            yield put({type:'updateState',payload:{tableLoading:false}});
        },
        *getWorkElementTypeList({payload},{select,call,put}){
          const state = yield select(({workElement})=>workElement);
          const data  = yield call(getWorkElementTypeList,{typeCode:state.typeCode});
          if(data && data.result == 0) {
              const workElementTypeList = data.data;
              yield put({type:'updateState',payload:{workElementTypeList}});
          }else {
              message.error('获取图元类型列表失败；');
          }
        },
        *getOrgTreeData({payload},{select,call,put}){
          const state = yield select(({workElement})=>workElement);
          const data  = yield call(getOrgTreeDataWithPermission,{isControlPermission:1});
          if(data && data.result == 0) {
              const orgTreeData = selectTreedataHandler(JSON.parse(data.data).items);
              yield put({type:'updateState',payload:{orgTreeData}});
          }else {
              message.error('获取部门机构数据失败；');
          }
        },
        *getDivisionTreeData({payload},{select,call,put}){
          const state = yield select(({workElement})=>workElement);
          const data  = yield call(getDivisionTenantTree);
          if(data && data.result == 0) {
              const divisionTreeData = selectTreedataHandler(JSON.parse(data.data).items);
              yield put({type:'updateState',payload:{divisionTreeData}});
          }else {
              message.error('获取行政区划数据失败；');
          }
        },
        *getLoginInfo({payload},{select,call,put}){
            const data=yield call(getLoginInfo);
			if(data && data.result == 0){
                if(data.data.latitudeDone && data.data.longitudeDone)
				yield put({type:'updateState',payload:{
                    setCenter: true,
                    mapCenter:[data.data.longitudeDone,data.data.latitudeDone]
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
        *addWorkElement({ payload }, { call, put,select }){
            yield put({type:'updateState',payload:{tableLoading:true}});
            const {newItem,codeUnique,nameUnique} = yield select(({workElement})=>workElement);
            let formCheck = [
                {checkType:'required', checkVal: newItem.code },
                {checkType:'codeValidator', checkVal: newItem.code },
                {checkType:'required', checkVal: newItem.name },
                {checkType:'nameValidator', checkVal: newItem.name },
                {checkType:'required', checkVal: newItem.workElementTypeId },
                {checkType:'required', checkVal: newItem.departmentId },
                {checkType:'required', checkVal: newItem.divisionId },
            ];
            if(formValidation(formCheck) && !codeUnique && !nameUnique){
                yield put({type:'updateNewItem',payload:{mapType:'2',loading:true}});
                const data  = yield call(saveWorkElement, newItem);
                if(data && data.result == 0) {
                    yield put({type:'updateNewItem',payload:{visible:false}});
                    yield put({type:'initTableArgs'});
                    yield put({type:'getTableData'});
                    message.success('添加成功；');
                }else {
                    message.error('添加失败；');
                }
                yield put({type:'updateNewItem',payload:{loading:false}});
            }
        },
        *updateWorkElement({ payload }, { call, put,select }){
            yield put({type:'updateState',payload:{tableLoading:true}});
            const state = yield select(({workElement})=>workElement);
            const {codeUnique,nameUnique} = yield select(({workElement})=>workElement);
            const {latitudes,longitudes,paramsDone,id,code,name,workElementTypeId,shape,departmentName,departmentId,divisionName,
            divisionId,radius,area,length,color}=state.editItem;
            let formCheck = [
                {checkType:'required', checkVal: code },
                {checkType:'codeValidator', checkVal: code },
                {checkType:'required', checkVal: name },
                {checkType:'nameValidator', checkVal: name },
                {checkType:'required', checkVal: workElementTypeId },
                {checkType:'required', checkVal: departmentId },
                {checkType:'required', checkVal: divisionId },
            ];
            if(formValidation(formCheck) && !codeUnique && !nameUnique){
                yield put({type:'updateEditItem',payload:{mapType:'2',loading:true}});
                const param={latitudes,longitudes,paramsDone,id,code,name,workElementTypeId,shape,departmentName,departmentId,divisionName,
                divisionId,radius,area,length,color};
                const data  = yield call(updateWorkElement, param);
                if(data && data.result == 0) {
                    yield put({type:'updateEditItem',payload:{visible:false}});
                    yield put({type:'getTableData'});
                    message.success('修改成功；');
                }else {
                    message.error('修改失败；');
                }
                yield put({type:'updateEditItem',payload:{loading:false}});
            }
            yield put({type:'updateState',payload:{tableLoading:false}});
        },
        *getDtoById({ payload }, { call, put,select }){
            let mapPoints=[],
                mapLines=[],
                mapPolygons=[],
                mapCircles=[],
                mapCenter=[];
            const {operatedRow} = yield select(({workElement})=>workElement);
            if(operatedRow){
                var shape = operatedRow.shape;
                var lonLatStr = operatedRow.paramsDone;
                switch(shape){
                  case "point":
                    var arr = lonLatStr.split(",");
                    mapPoints.push({id:"workE",longitude:arr[0],latitude:arr[1]});
                    mapCenter = [arr[0],arr[1]];
                    // yield put({type:"updateState",payload:{mapPoints:[{id:"workE",longitude:arr[0],latitude:arr[1]}],mapCenter:[arr[0],arr[1]]}});
                    break;
                  case "polyline":
                    mapLines.push({id:"workE",paths:splitOverride(lonLatStr)});
                    // yield put({type:"updateState",payload:{mapLines:[{id:"workE",paths:splitOverride(lonLatStr)}]}});
                    break;
                  case "rectangle":
                  case "polygon":
                    mapPolygons.push({id:"workE",rings:splitOverride(lonLatStr)});
                    // yield put({type:"updateState",payload:{mapPolygons:[{id:"workE",rings:splitOverride(lonLatStr)}]}});
                    break;
                  case "circle":
                    var arr = lonLatStr.split(",");
                    mapCircles.push({id:"workE",longitude:arr[0],latitude:arr[1],radius:operatedRow.radius});
                    mapCenter = [arr[0],arr[1]];
                    // yield put({type:"updateState",payload:{mapCircles:[{id:"workE",longitude:arr[0],latitude:arr[1],radius:data.ret.radius}],mapCenter:[arr[0],arr[1]]}});
                    break;
                }
            }
            yield put({type:"updateState",payload:{
                mapPoints,
                mapLines,
                mapPolygons,
                mapCircles,
                mapCenter
            }});
        },
        *deletesWorkElement({ payload }, {call, put, select}){
            const state = yield select(({workElement})=>workElement);
            const data  = yield call(deletesWorkElement, payload&&payload.id?JSON.stringify([payload.id]):JSON.stringify(state.selectedRowKeys));
            if(data && data.result == 0) {
                message.success('删除成功；');
                 yield put({type:"updateState",payload:{selectedRowKeys:[]}});
                yield put({type:'getTableData'});
            }else {
                message.error('删除失败；');
            }
        },
        *validateCode({payload},{select,call,put}){
            const data=yield call(validateWorkElementCode,{
                code:payload.obj
            });
            if(payload.obj && data && data.result==0 ){
                yield put({type:'updateState',payload:{codeUnique:!data.data}});
            }
        },
        *validateName({payload},{select,call,put}){
            const data=yield call(validateWorkElementName,{
                name:payload.obj
            });
            if(payload.obj && data && data.result==0){
                yield put({type:'updateState',payload:{nameUnique:!data.data}});
            }
        },
        //导入
        *importData({payload},{call,put,select}){
            const {typeCode} = yield select(({workElement})=>workElement);
            const importDataCallback = payload.importDataCallback;
            const data=yield call(importWorkElementData,{typeCode,param:payload.uploadFile});
            var result = JSON.parse(data);
            if(result && result.result == 0){
                if(typeof(importDataCallback) == "function"){
                    importDataCallback(result.msg);
                }
            }else{
                message.error(result.msg);
            }
            yield put({type:'updateState',payload:{uploading:false}});
        },
        //导出
        *exportData({payload},{call,put,select}){
            const state = yield select(({workElement})=>workElement);
            var downloadAll = false;
            var selectedRowKeys = '';
            var pageIndex = '';
            var pageSize = '';
            console.log(payload.downloadType);
            if(payload.downloadType == "all"){
                downloadAll = true;
            }else if(payload.downloadType == "page"){
                pageSize = state.pageSize;
                pageIndex = state.currentPageIndex;
            }else if(payload.downloadType == "select"){
                downloadAll = true;
                selectedRowKeys = state.selectedRowKeys.join(",");
            }

            var args = {
                columnFields: "code,name,workElementTypeName,departmentName,divisionName,params,paramsDone",
                columnNames: "编码,图元名称,图元类型,所属机构,所属行政区划,经纬度,偏转后经纬度",
                downloadAll: downloadAll,
                downloadIds: selectedRowKeys,
                page: pageIndex,
                rows: pageSize,
                name: state.searchName.trim(),
                workElementTypeId: state.searchWorkElementTypeId,
                orgId: state.searchOrgId
            };
            const data = yield call(exportWorkelement,args);
            // var url = "/cloud/management/workElement/download?";
            // for(var key in args){
            //     url  += (key+"="+args[key]+"&"); 
            // }
            // url = url.substring(0,url.length-1);
            // window.open(url);
        },
        *getImportTableData({payload }, { call, put,select }){
            const {importDetail} = yield select(({workElement})=>workElement);
            const successful = payload.successful;
            yield put({type:'updateImportState',payload:{tableLoading:true,selectedRowKeys:[]}});

            const data  = yield call(getImportTableData,{
                pageIndex: importDetail.currentPageIndex,
                pageSize: importDetail.pageSize,
                's_EQ_successful_B':successful
            });
            if(data && data.result == 0) {
                const tableData = handleTableData(data.data.rows);
                const totalItems = data.data.total;
                yield put({type: 'updateImportState', payload: {tableData, totalItems}});
            }else {
                message.error('获取列表数据失败；');
            }
            yield put({type: 'updateImportState', payload: {tableLoading: false}});
        },
        *drawWorkE({payload},{select,put,call}){
            let mapDraw = payload.mapDrawConfig;

            yield put({
                type: 'isDrawing',
                payload: {
                    mapDraw,
                    isDraw: true
                }
            });
            yield call(delay,1);
            yield put({
                type: 'isDrawing',
                payload: {
                    isDraw: false
                }
            })
        },
        *clearAllTY({payload},{select,put,call}){
            yield put({
                type:'updateState',
                payload:{
                    isClearAll: true,
                    mapPoints: [],
                    mapLines: [],
                    mapPolygons: [],
                    mapCircles: [],
                    boundaryName: [],
                }
            });
            yield call(delay,1);
             yield put({
                type:'updateState',
                payload:{
                    isClearAll: false,
                }
            });
        },
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
        }
    },
    reducers: {
    	updateState(state, action) {
    		return {
    			...state,
    			...action.payload
    		};
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
        updateViewItem(state,action){
            return {
                ...state,
                viewItem:{
                    ...state.viewItem,
                    ...action.payload
                }
            }
        },
        updateImportState(state, action) {
            return {
                ...state,
                importDetail: {
                    ...state.importDetail,
                    ...action.payload
                }
            }
        },
        clearNew(state, action) {
            return {
                ...state,
                newItem: {
                    ...state.newItem,
                    checkState:false,
                    tenantId: '',
                    code: '',
                    name: '',
                    workElementTypeId: '',
                    departmentId:'',
                    departmentName:'',
                    shape: '',
                    divisionId: '',
                    divisionName: '',
                    mapType: '2',
                    area: '',
                    length: '',
                    radius: '',
                    color: '',
                    paramsDone: ''
                }
            }
        },
        clearSearchFilter(state, action) {
          return {
            ...state,
            searchName: '',
            searchWorkElementTypeId: '',
            searchOrgId: ''
          }
        },
        isDrawing(state,action){
            return {...state,...action.payload};
        },
        clearMap(state, action) {
          return {
            ...state,
            isDraw: false,
            mapDraw: {},
            mapPoints: [],
            mapLines: [],
            mapPolygons: [],
            mapCircles: [],
            editGraphicId: ''
          }
        },
        initTableArgs(state, action) {
            return {
                ...state,
                selectedRowKeys:[],
                currentPageIndex:1,
                pageSize:10
            }
        }
    }
}

//延迟
function delay(timeout){
  var pro = new Promise(function(resolve,reject){
    setTimeout(resolve, timeout);
  });
  return pro;
}

const splitOverride = (data)=>{
  var arrObj = [];
  if (data != "") {
    var args1 = data.split(";");
    for(var i=0;i<args1.length;i++){
      var args2 = args1[i].split(",");
      arrObj.push([args2[0],args2[1]]);
    } 
  }
  return arrObj;
}