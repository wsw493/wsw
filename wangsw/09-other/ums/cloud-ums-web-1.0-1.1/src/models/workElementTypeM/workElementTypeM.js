import {getBaicPostData,handleTreeData,formValidation,handleTableData} from '../../utils/toolFunctions';
import {getWorkElementTypeTable,saveWorkElementType,validateWorkElementTypeName,getWorkElementTypeList,WorkElementTypeById,
        getWorkElementTypeDtoById,updateWorkElementType,deletesWorkElementType,getOrgTreeDataWithPermission,validateWorkElementTypeCode} from '../../services/remoteData';
import {message} from 'antd';

export default {
	namespace: 'workElementType',
	state: {
        tableLoading:false,
        tableData: [],
        routeList:[],
        currentPageIndex:0,
        pageSize:10,
        totalItems:0,
        selectedRowKeys:[],
        editLoading:false,
        operatedRow:{},
        codeUnique:false,
        nameUnique:false,
        code:'',
        newItem:{
            loading:false,
            visible:false,
            checkState:false,
            code: '',
            name: '',
            shape: 'point',
            departmentName:'',
            departmentId:'',
            info: '',
            orderIndex: ''
        },
        editItem:{
            loading:false,
            visible:false,
            checkState:true,
        },
        viewItem:{
            visible:false,
        },
        searchName: '',
        searchShape: '',
        orgTreeData: [],
        codeValidatFlag: true,
        scrollHeight: '',
        saving:false
    },
    
	subscriptions: {
        setup({ dispatch, history }) {
            // dispatch({type:'fetchRemote'})
            history.listen(({ pathname,query }) => {
                dispatch({type:'updateState',payload:{code:query['code']}});
                if(pathname == '/workelementtype'){
                    dispatch({type:'getTableData'});

                }
            });
        },  
    },

    effects: {
        /*获取列表数据*/
        *getTableData({ payload }, { call, put,select }){
            const state = yield select(({workElementType})=>workElementType);
            yield put({type:'updateState',payload:{tableLoading:true}});
            const data  = yield call(getWorkElementTypeTable,{
                pageIndex: state.currentPageIndex,
                pageSize: state.pageSize,
                s_LIKE_code:state.code,
                s_LIKE_name: state.searchName.trim(),
                s_EQ_shape: state.searchShape
            });
            if(data && data.result == 0) {
                const tableData = handleTableData(data.data.rows);
                const totalItems = data.data.total;
                yield put({type: 'updateState', payload: {tableData, totalItems,selectedRowKeys:[]}});
            }else {
                message.error("加载列表数据失败；");
            }
            yield put({type:'updateState',payload:{tableLoading: false}});
        },
        *getOrgTreeData({payload},{select,call,put}){
          const state = yield select(({workElementType})=>workElementType);
          const data  = yield call(getOrgTreeDataWithPermission,{isControlPermission:1});
          if(data && data.result == 0) {
              const orgTreeData = handleTreeData(JSON.parse(data.data).items);
              yield put({type:'updateState',payload:{orgTreeData}});
          }else {
              message.error('获取数据失败；');
          }
        },
        *getWorkElementTypeList({payload},{put,call,select}){
            const data=yield call(getWorkElementTypeList);
        },
        /**WorkElementTypeById({payload},{put,call,select}){
            const data=yield call(WorkElementTypeById,{id:payload.id});
            if(data && data.result==0){
                yield put({type:'updateState',payload:{operatedRow:data.data}})
            }
        },*/
        *addWorkElementType({ payload }, {select,call, put}){
            const {newItem,codeUnique,nameUnique}=yield select(({workElementType})=>workElementType);
            let formCheck = [
                {checkType:'required', checkVal: newItem.code },
                {checkType:'codeValidator', checkVal: newItem.code },
                {checkType:'required', checkVal: newItem.name },
                {checkType:'nameValidator', checkVal: newItem.name },
                {checkType:'required', checkVal: newItem.shape },
            ];
            if(formValidation(formCheck) && !codeUnique && !nameUnique){
                yield put({type:'updateNewItem',payload:{loading:true}});
                const data = yield call(saveWorkElementType ,newItem);
                if(data && data.result == 0){
                    yield put({type:'updateNewItem',payload:{visible:false}});
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
        *updateWorkElementType({ payload }, { call, put,select }){
            const state=yield select(({workElementType})=>workElementType);
            const {editItem,codeUnique,nameUnique}=yield select(({workElementType})=>workElementType);
            const {id,code,name,shape,departmentName,departmentId,info,orderIndex}=state.editItem;
            let formCheck = [
                {checkType:'required', checkVal: editItem.code },
                {checkType:'codeValidator', checkVal: editItem.code },
                {checkType:'required', checkVal: editItem.name },
                {checkType:'nameValidator', checkVal: editItem.name },
                {checkType:'required', checkVal: editItem.shape },
            ];
            if(formValidation(formCheck) && !codeUnique && !nameUnique){
                const param={
                    id,code,name,shape,departmentName,departmentId,info,orderIndex
                }
                yield put({type:'updateEditItem',payload:{loading:true}});
                const data = yield call(updateWorkElementType ,param);
                if(data && data.result == 0){
                    yield put({type:'updateEditItem',payload:{visible:false}});
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
        *deletesWorkElementType({ payload }, {call, put, select}){
          const {selectedRowKeys} = yield select(({workElementType})=>workElementType);
          var ids=[];
          if(payload && payload.id){
            ids=[payload.id];
          }else{
            ids=selectedRowKeys;
          }
          const data  = yield call(deletesWorkElementType, JSON.stringify(ids));
            if(data && data.result == 0) {
                message.success('删除成功；');
                yield put({type:'updateState',payload:{selectedRowKeys:[]}});
                yield put({type:'getTableData'});
            }else {
                message.error('删除失败；');
            }
        },
        *validateCode({payload},{select,call,put}){
            const data=yield call(validateWorkElementTypeCode,{
                code:payload.obj
            });
            if(payload.obj && data && data.result==0 ){
                yield put({type:'updateState',payload:{codeUnique:!data.data}});
            }
        },
        *validateName({payload},{select,call,put}){
            const data=yield call(validateWorkElementTypeName,{
                name:payload.obj
            });
            if(payload.obj && data && data.result==0){
                yield put({type:'updateState',payload:{nameUnique:!data.data}});
            }
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
        /*updateOperateRow(state,action){
            return {
                ...state,
                operatedRow:{
                    ...state.operatedRow,
                    ...action.payload
                }
            }
        },*/
        updateViewItem(state,action){
            return {
                ...state,
                viewItem:{
                    ...state.viewItem,
                    ...action.payload
                }
            }
        },
        clearNewItem(state, action) {
            return {
                ...state,
                newItem: {
                    ...state.newItem,
                    visible:true,
                    checkState: false,
                    loading:false,
                    code: '',
                    name: '',
                    shape: 'point',
                    departmentName:'',
                    departmentId:'',
                    info: '',
                    orderIndex: ''
                }
            }
        },
        clearSearchFilter(state, action) {
          return {
            ...state,
            searchName: '',
            searchShape: ''
          }
        },
        initTableArgs(state, action) {
            return {
                ...state,
                selectedRowKeys:[],
                currentPageIndex:0,
                pageSize:10
            }
        }
    }
}