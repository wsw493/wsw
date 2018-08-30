import React from 'react';
import {connect} from 'dva';
import {message} from 'antd';
import {handleTableData,handleTreeData,handleRestResultData,handleEditItem,getBaicPostData,
    formValidation} from '../../utils/toolFunctions';
import {getWorkElementTypeList,getOrgWorkElementTree,deleteWorkElement,updateWorkElementGis,
    loadOrgTreeByPermission,getDivisionTenantTree,validateWorkElementCode,validateWorkElementName,
    saveWorkElement,updateWorkElement,getLoginInfo} from '../../services/remoteData';

const treedataHandler = (data)=>{
  return data.map((element)=>{
    var arr = [];
    var disabled = true;
    if(element.children && element.children.length != 0){
      arr = treedataHandler(element.children);
    }
    if(element.nodeType == "workElement"){
      disabled = false;
    }
    return {
      key: element.id,
      name: element.name,
      nodeType: element.nodeType,
      isLeaf: element.leaf,
      children: arr,
      // disableCheckbox: disabled
    }
  });
}
export default {
	namespace: 'workElementGis',
	state: {
        editWindow:false,
        mapWindow:false,
        editLoading:false,
        operatedRow:{},
        searchName: '',
        searchWorkElementTypeId: '',
        searchOrgId: '',

        typeCode:'',
        editGraphicId2: '',
        isDoEdit2: false,
        isEndEdit: true,
        
        isDraw: false,
        mapDraw: {},
        mapPoints2: [],
        mapLines2: [],
        mapPolygons2: [],
        mapCircles2: [],
        mapCenter: [],
        setCenter: false,
        lastMapState: {},
        
        setVisiblePoints: true,
        mapVisiblePoints:[],

        mapPanelPosition: {},
        newItem: {
            id: '',
            tenantId: '',
            code: '',
            name: '',
            workElementTypeId: '',
            workElementTypeName:'',
            shape: '',
            shapeName: '',
            departmentId: '',
            departmentName: '',
            divisionId: '',
            divisionName: '',

            mapType: '2',

            area: '',
            length: '',
            radius: '',
            color: '',
            lonLatStr: '',
            editMapId: '',
            orderIndex: '',
            editGraphicId2: ''
        },
        editItem: {
            checkState: true,
            visible: false
        },
        defaultCloumns:['id','tenantId','code','name','workElementTypeId','shape','departmentId','divisionId','mType','area',
            'paramsDone','length','radius'],
        isClearAll: false,
        isDraw2: false,
        mapDraw2: {},
        mapPoints: [],
        mapLines: [],
        mapPolygons: [],
        mapCircles: [],
        mapCenter2: [],
        setCenter2: false,
        editGraphicId: '',
        orgWorkElementTree: [],
        workElementTypeList: [],

        orgTreeData: [],
        divisionTreeData: [],

        initWorkElementKeys: [],
        checkedKeys_elem: [],
        elementId: '',
        checkedWorkElementList: [],//已选中的图元Id-mapId对应关系；
        codeValidatFlag: true,
        isDoEdit2: false,
        addPanelDisplay:{},
        codeUnique:false,
        nameUnique:false,
        saving:false,
        inputVal: '',
        isDoEditForUpdate: false,
        isRemove: false,
        mapRemove: [],
        mapType: 'bmap',
        panelStatus: 'open'
    },
    
	subscriptions: {
        setup({ dispatch, history }) {
            history.listen(({ pathname, query }) => {
                dispatch({type:'updateState',typeCode:query['typeCode']});
                if(pathname == '/workelementgis'){
                    dispatch({type:'getLoginInfo'});
                    // dispatch({type:'getWorkElementTypeList'});
                    // dispatch({type:'getOrgWorkElementTree'});
                }
            });
        },  
    },

    effects: {
        *getOrgWorkElementTree({payload},{select,call,put}){
          const state = yield select(({workElementGis})=>workElementGis);
          let callback;
          if(payload && payload.callback){
            callback = payload.callback
          }
          const data  = yield call(getOrgWorkElementTree, {
              's_LIKE_name': state.searchName,
              's_EQ_workElementTypeId': state.searchWorkElementTypeId,
              's_EQ_departmentId': state.departmentId,
              'typeCode': state.typeCode,
              mapType: state.mapType
          });
          if(data && data.result == 0) {
              const orgWorkElementTree = handleTreeData(JSON.parse(data.data).items);
              yield put({type:'updateState',payload:{orgWorkElementTree}});
              if(typeof callback == 'function'){
                    callback(orgWorkElementTree,payload.code);
                    var workElement = newObjAfterAdd.attr.attributes;
                    if(workElement){
                        var checkedWorkElementList = state.checkedWorkElementList;
                        checkedWorkElementList.push({attr:workElement,mapId:"workAdd"});
                      var shape = workElement.shape;
                      var lonLatStr = workElement.paramsDone;
                      yield put({type:"updateState",payload:{checkedWorkElementList}});
                      switch(shape){
                        case "point":
                          var arr = lonLatStr.split(",");
                          yield put({type:"updateState",payload:{mapPoints2:[...state.mapPoints2,{id:"workAdd",longitude:arr[0],latitude:arr[1]}],mapCenter:[arr[0],arr[1]]}});
                          break;
                        case "polyline":
                          yield put({type:"updateState",payload:{mapLines2:[...state.mapLines2,{id:"workAdd",paths:splitOverride(lonLatStr)}]}});
                          break;
                        case "polygon":
                          yield put({type:"updateState",payload:{mapPolygons2:[...state.mapPolygons2,{id:"workAdd",rings:splitOverride(lonLatStr)}]}});
                          break;
                        case "circle":
                          var arr = lonLatStr.split(",");
                          yield put({type:"updateState",payload:{mapCircles2:[...state.mapCircles2,{id:"workAdd",longitude:arr[0],latitude:arr[1],radius:workElement.radius}],mapCenter:[arr[0],arr[1]]}});
                          break;
                        case "rectangle":
                          yield put({type:"updateState",payload:{mapPolygons2:[...state.mapPolygons2,{id:"workAdd",rings:splitOverride(lonLatStr)}]}});
                          break;
                      }
              }
            }
          }else {
                if(data && data.exception){
                    message.error(data.exception);
                }
          }
        },
        *getWorkElementTypeList({payload},{select,call,put}){
            const data=yield call(getWorkElementTypeList);
			if(data && data.result == 0){
				yield put({type:'updateState',payload:{workElementTypeList:data.data}});
			}else {
				if(data && data.exception){
					message.error(data.exception);
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
            yield put({type:'getWorkElementTypeList'});
            yield put({type:'getOrgWorkElementTree'});
        },
        *getOrgTreeData({payload},{select,call,put}){
          const state = yield select(({workElementGis})=>workElementGis);
          const data  = yield call(loadOrgTreeByPermission, {isControlPermission:'1'});
          if(data && data.result == 0) {
              const orgTreeData = handleTreeData(JSON.parse(data.data).items);
              yield put({type:'updateState',payload:{orgTreeData}});
          }else {
                if(data && data.exception){
                    message.error(data.exception);
                }
          }
        },
        *getDivisionTreeData({payload},{select,call,put}){
          const state = yield select(({workElementGis})=>workElementGis);
          const data  = yield call(getDivisionTenantTree);
          if(data && data.result == 0) {
              const divisionTreeData = handleTreeData(JSON.parse(data.data).items);
              yield put({type:'updateState',payload:{divisionTreeData}});
          }else {
            if(data && data.exception){
                message.error(data.exception);
            }
          }
        },
        *addWorkElement({ payload }, { call, put,select }){
            const {newItem,codeUnique,nameUnique,defaultCloumns,mapType} = yield select(({workElementGis})=>workElementGis);
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
                yield put({type:'updateState',payload:{saving:true}});
                const data  = yield call(saveWorkElement, {...handleEditItem(defaultCloumns ,newItem),mapType});
                if(data && data.result == 0) {
                    yield put({type:'updateState',payload:{
                        addPanelDisplay:{},
                        isDoEdit2:false,
                        isEndEdit:true,
                        editGraphicId2:[''],
                        isRemove: true,
                        mapRemove:[{id:'workE',type:'draw'}]
                    }});
                    yield put({type:'getOrgWorkElementTree',payload:{callback:functionAfterAdd,code:newItem.code}});
                    yield put({type:'updateState',payload:{
                        isRemove: false
                    }});
                    message.success(data.msg);
                }else {
                    if(data && data.exception){
                        message.error(data.exception);
                    }
                }
                yield put({type:'updateState',payload:{saving:false}});
            }
        },
        *updateWorkElementForm({ payload }, { call, put,select }){
            const {editItem,codeUnique,nameUnique,defaultCloumns,mapType} = yield select(({workElementGis})=>workElementGis);
            let formCheck = [
                {checkType:'required', checkVal: editItem.code },
                {checkType:'codeValidator', checkVal: editItem.code },
                {checkType:'required', checkVal: editItem.name },
                {checkType:'nameValidator', checkVal: editItem.name },
                {checkType:'required', checkVal: editItem.workElementTypeId },
                {checkType:'required', checkVal: editItem.departmentId },
                {checkType:'required', checkVal: editItem.divisionId },
            ];
            if(formValidation(formCheck) && !codeUnique && !nameUnique){
                yield put({type:'updateState',payload:{saving:true}});
                const data  = yield call(updateWorkElementGis, {...handleEditItem(defaultCloumns ,editItem),mapType});
                if(data && data.result == 0) {
                    yield put({type:'updateEditItem',payload:{visible:false}});
                    message.success(data.msg);
                }else {
                    if(data && data.exception){
                        message.error(data.exception);
                    }
                }
                yield put({type:'updateState',payload:{saving:false}});
            }
        },
        *updateWorkElement({ payload }, { call, put,select }){
            yield put({type:'updateAddItem',payload:{mapType:'2'}});
            const {defaultCloumns,newItem,mapType} = yield select(({workElementGis})=>workElementGis);
            const data  = yield call(updateWorkElementGis, {...handleEditItem(defaultCloumns,newItem),mapType});
            if(data && data.result == 0) {
                yield put({type:'getOrgWorkElementTree'});
                yield put({type:'workElementGis/updateState',payload:{
                    isDoEditForUpdate: false,
                    isDoEdit2:false,
                    isEndEdit:true,
                    editGraphicId2:['']
                }});
                message.success(data.msg);
            }else {
                if(data && data.exception){
					message.error(data.exception);
				}
            }
        },
        *deletesWorkElement({ payload }, {call, put, select}){
          const state = yield select(({workElementGis})=>workElementGis);
          const data  = yield call(deleteWorkElement, {id:state.newItem.id});
            if(data && data.result == 0) {
                yield put({type:'clearInitMap'});
                yield put({type:'updateState',payload:{mapPanelPosition:{display:"none"}}});
                yield put({type:'getWorkElementTypeList'});
                yield put({type:'getOrgWorkElementTree'});
                message.success(data.msg);
            }else {
                if(data && data.exception){
					message.error(data.exception);
				}
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
        *getDtoById({ payload }, { call, put,select }){
            const {checkedWorkElementList,newItem} = yield select(({workElementGis})=>workElementGis);
            var workElement = '';
            for(var i=0;i<checkedWorkElementList.length;i++){
                if(newItem.editGraphicId2 == checkedWorkElementList[i].mapId){
                    workElement = checkedWorkElementList[i].attr;
                }
            }
            if(workElement){
                var shape = workElement.shape;
                var lonLatStr = workElement.paramsDone;
                yield put({type:"updateEditItem",payload:{...workElement}});
                yield put({type:"updateState",payload:{
                    editGraphicId:newItem.editGraphicId2,
                    isDoEdit: true
                }});
                switch(shape){
                  case "point":
                    var arr = lonLatStr.split(",");
                    yield put({type:"updateState",payload:{mapPoints:[{id:"workE_"+shape,longitude:arr[0],latitude:arr[1]}],mapCenter:[arr[0],arr[1]]}});
                    break;
                  case "polyline":
                    yield put({type:"updateState",payload:{mapLines:[{id:"workE_"+shape,paths:splitOverride(lonLatStr)}]}});
                    break;
                  case "polygon":
                    yield put({type:"updateState",payload:{mapPolygons:[{id:"workE_"+shape,rings:splitOverride(lonLatStr)}]}});
                    break;
                  case "circle":
                    var arr = lonLatStr.split(",");
                    yield put({type:"updateState",payload:{mapCircles:[{id:"workE_"+shape,longitude:arr[0],latitude:arr[1],radius:workElement.radius}],mapCenter:[arr[0],arr[1]]}});
                    break;
                  case "rectangle":
                    yield put({type:"updateState",payload:{mapPolygons:[{id:"workE_"+shape,rings:splitOverride(lonLatStr)}]}});
                    break;
                }
            }
        },
        *drawWorkE({payload},{select,put,call}){
            let mapDraw2 = payload.mapDrawConfig;
            yield put({
                type: 'isDrawing',
                payload: {
                    mapDraw2,
                    isDraw2: true
                }
            });
            yield call(delay,1);
            yield put({
                type: 'isDrawing',
                payload: {
                    isDraw2: false
                }
            })
        },
        *drawWorkE2({payload},{select,put,call}){
            let mapDraw2 = payload.mapDrawConfig;
            yield put({
                type: 'isDrawing2',
                payload: {
                    mapDraw2,
                    isDraw2: true
                }
            });
            yield call(delay,1);
            yield put({
                type: 'isDrawing2',
                payload: {
                    isDraw2: false
                }
            })
        },
        *clearAllTY({payload},{select,put,call}){
            yield put({
                type:'updateState',
                payload:{
                    isClearAll: true,
                    mapPoints2: [],
                    mapLines2: [],
                    mapPolygons2: [],
                    mapCircles2: []
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
                    isDoEdit2: true,
                    editGraphicId2: payload.editGraphicId
                }
            })
            yield call(delay,1);
            yield put({
                type: 'updateState',
                payload: {
                    isDoEdit2: false
                }
            })
        },
        *doEdit2({payload},{select,call,put}){
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
        clearNewItem(state, action) {
            return {
                ...state,
                newItem: {
                    id: '',
                    tenantId: '',
                    code: '',
                    name: '',
                    workElementTypeId: '',
                    workElementTypeName:'',
                    shape: '',
                    shapeName: '',
                    departmentId: '',
                    departmentName: '',
                    divisionId: '',
                    divisionName: '',
        
                    mapType: '2',
        
                    area: '',
                    length: '',
                    radius: '',
                    color: '',
                    lonLatStr: '',
                    editMapId: '',
                    orderIndex: '',
                    editGraphicId2: ''
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
            const isDraw2 = action.payload.isDraw2;
            const mapDraw2 = action.payload.mapDraw2;
            return {
              ...state,
              isDraw: isDraw2,
              mapDraw: mapDraw2,
              editGraphicId2: ''
            };
        },
        isDrawing2(state,action){
            const isDraw2 = action.payload.isDraw2;
            const mapDraw2 = action.payload.mapDraw2;
            return {
              ...state,
              isDraw: isDraw2,
              mapDraw: mapDraw2,
              editGraphicId: ''
            };
        },
        clearMap(state, action) {
          return {
            ...state,
            isDraw2: false,
            mapDraw2: {},
            mapPoints: [],
            mapLines: [],
            mapPolygons: [],
            mapCircles: [],
            editGraphicId: ''
          }
        },
        clearInitMap(state, action) {
          return {
            ...state,
            mapPoints2: [],
            mapLines2: [],
            mapPolygons2: [],
            mapCircles2: []
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
var newObjAfterAdd = {};
function functionAfterAdd(data,code){
    if(typeof data == 'undefined'){
        data = []
    }
    data.map((item)=>{
        if(item.attr.attributes.code == code){
            newObjAfterAdd = item;
        }else if(item.children && item.children.length>0){
            functionAfterAdd(item.children,code);
        }
    });
}