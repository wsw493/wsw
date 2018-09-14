import React from 'react';
import {connect} from 'dva';
import {message} from 'antd';
import {ParamType} from '../../utils/constant';
import {handleTableData,handleTreeData,handleRestResultData,handleEditItem,getBaicPostData,
    formValidation} from '../../utils/toolFunctions';
import {getSystemStaffTableData,loadOrgTreeByPermission,getSingleParam,saveSystemStaff,updateSystemStaff,validateSystemStaff,
		deletesSystemStaff,getSystemStaffDtoById,saveUser,updateUser,getUserById,getPermissionScopeList,validateTenantAccount,
        getMultiParam,validateStaffPhone,importSystemStaffData,loadSystemRoleTree,getUserRoleRelation,getDivisionTenantTree,
		userRoleTreeDataSave,loadDeptOrgTree,validateAccount_U,exportStaff,getFunctionPermissionMap,resetUserPassword,
		getTenantUserRoleRelation,loadTenantRoleTree,loadRoleStyle,getImportTableData} from '../../services/remoteData';
export default {
	namespace: 'systemStaff',
	state: {
		tableData: [],
		orgTree: [],
		selectOrgTree: [],
		currentPageIndex: 1,
        pageSize: 10,
        totalItems: 0,
        selectedRowKeys: [],
		tableLoading: false,
		companyType:'',
		searchDepartmentId: '-1',
		searchOrgId: '',
		searchOrgName: '',
		searchName: '',
		searchCode: '',
		credentialNum: '',
		educationId:'',
		ckRange: false,
		ageGroupStart:'',
		ageGroupEnd:'',
		workYearLimitStart:'',
		workYearLimitEnd:'',
		postId:'',
        gender: '',
		socialSecurityNo:'',
		isLeave: '',
		phone:'',
		newItem: {
			id:'',
			tenantId:'',
			visible: false,
            checkState: false,
			orgName: '',
			orgId: '',
			departmentId:'',
			name: '',
			code: '',
			gender: '男',
			birthday: '',
			credentialNum: '',
			nationId: '',
			maritalStatusId: '',
			politicalStatusId: '',
			joinWorkTime: null,
			workYearLimit: '',
			isLeave: '0',
			leaveTime: null,
			workTypeCode: '',
			orderIndex: '',
			description: '',

			birthPlace: '',
			presentPlace: '',
			livePlace: '',

			phone: '',
			officeTel: '',
			email: '',
			innerEmail: '',

			graduate: '',
			educationId: '',
			educationName: '',

			authorizeId: '',
			authorizeName: '',
			postId: '',
			postName: '',
			partyPostId: '',
			partyPostName: '',
			entryHereTime: null,
			idCard: '',
			socialSecurityNo: '',
			socialSecuritycase: '',
			outSourcing:false,
        	checkOrder:false,
			checkYear:false,
			outSourcingComp: '',
			isWillMan:'false',
			address:'',
			willCheckDivisionIds:'',
			willWorkUnit:'',
			photograph: '[]'
		},
		editItem:{
			visible: false,
            checkState: true,
            loading:false,
        	checkOrder:false,
        	checkYear:false
		},
		viewItem:{
			visible: false
		},
		permissionScopeList: [],
		newUserItem: {
			visible: false,
			checkState: false,

			id:'',
			staffId: '',
			staffName: '',
			userName: '',
			password: '',
			confirm_password: '',
			permissionScope: '1',
			customScope: '',
			photoId: ''
		},
		fileList:[],
		fileListVersion: 1,
		userNameUnique: false,
		passwordFlag: false,
		editUserItem: {
			visible: false,
			checkState: true
		},
		openuWindow: false,
		codeUnique: false,
		phoneUnique: false,
		saving:false,
		deptOrgTree: [],
		baseData: {
			//婚姻类型
			STAFF_MARITAL_STATUS : [],
			//政治面貌
			STAFF_POLITICAL_STATUS : [],
			//学历
			STAFF_EDUCATION : [],
			//人员编制
			STAFF_AUTHORIZE : [],
			//职位
			STAFF_POSITION : [],
			//职务
			STAFF_POST : [],
			//社保缴纳情况
			STAFF_SOCIAL_SECURITY_CASE : [],
			//健康状况
			// STAFF_HEALTH:[],
			//民族
			STAFF_NATION:[],
			//证件类型
			// STAFF_CREDENTIAL_TYPE:[],
			//用工类型
			STAFF_WORK_TYPE: []
		},
		defaultCloumns:['id','orgName','orgId','departmentId','name','code','gender','birthday','credentialNum','nationId',
			'maritalStatusId','politicalStatusId','joinWorkTime','workYearLimit','isLeave','leaveTime','workTypeCode','orderIndex','description',
			'birthPlace','presentPlace','livePlace','phone','officeTel','email','innerEmail','graduate','educationId','educationName',
			'authorizeId','authorizeName','postId','postName','partyPostId','partyPostName','entryHereTime','photograph',
			'idCard','socialSecurityNo','socialSecuritycase','outSourcing','outSourcingComp','isWillMan','address','willCheckDivisionIds','willWorkUnit'
		],
		defaultCloumns_user: [
			'id','userName','staffId','staffName','permissionScope', 'customScope', 'photoId'
		],
		importWindow: false,
		exportWindow: false,
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
        roleTreeData: [],
        checkedKeys: [],
        selectedRoleKeys: [],
        bindRoleWindow: false,
		userId_b: '',//用于绑定角色
		sort: '',
		order: '',
		functionCodes: ['CF_MANAGE_SYSTEM_STAFF_LIST','CF_MANAGE_SYSTEM_STAFF_ADD','CF_MANAGE_SYSTEM_STAFF_DELETE','CF_MANAGE_SYSTEM_STAFF_UPDATE',
			'CF_MANAGE_SYSTEM_STAFF_VIEW','CF_MANAGE_STAFF_DELETE','CF_MANAGE_USER_ADD','CF_MANAGE_USER_UPDATE','CF_MANAGE_USER_ROLE_ADD','CF_MANAGE_USER_RESET_PWD'],
		functionCodesMap: {},
		divisionTree:'',

		roleTreeData_tenant: [],
        checkedKeys_tenant: [],
		selectedRoleKeys_tenant: [],
		roleStyle: '0'
	},

	subscriptions: {
		setup({ dispatch, history }) {
            history.listen(({ pathname }) => {
                if(pathname == '/systemstaff'){
					dispatch({type:'getFunctionPermissionMap'});
					dispatch({type:'getBaseData'});
                	dispatch({type:'getTreeData'});
					// dispatch({type:'getTableData'});
                }
            });
        }
	},

	effects: {
		*getTableData({ payload }, { call, put,select }){
            const state = yield select(({systemStaff})=>systemStaff);
	        yield put({type:'updateState',payload:{tableLoading:true,selectedRowKeys:[]}});
	        const data = yield call(getSystemStaffTableData ,{
	            page: state.currentPageIndex,
	            rows: state.pageSize,
				departmentId: state.searchDepartmentId,
				selectedId: state.searchOrgId,
				selectedType: state.companyType,
				name: state.searchName,
				code: state.searchCode,
				credentialNum: state.credentialNum,
				gender: state.gender,
				educationId: state.educationId,
				partyPostId: state.postId,
				ageGroupStart: state.ageGroupStart,
				ageGroupEnd: state.ageGroupEnd,
				workYearLimitStart: state.workYearLimitStart,
				workYearLimitEnd: state.workYearLimitEnd,
                ckRange: state.ckRange,
				socialSecurityNo: state.socialSecurityNo.trim(),
				sort: state.sort,
				order: state.order,
				phone: state.phone,
				isLeave: state.isLeave
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
        },
		*getTreeData({ payload }, {call, put, select}){
			const data=yield call(loadOrgTreeByPermission,{
				isControlPermission: 1
			});
			if(data && data.result == 0){
				const orgTree=handleTreeData(JSON.parse(data.data).items);
				const selectOrgTree=handleSelectTreeData(JSON.parse(data.data).items);
				yield put({type:'updateState',payload:{orgTree,selectOrgTree}});
			}else {
				if(data && data.exception){
					message.error(data.exception);
				}
			}
		},
		*getDivisionTreeData({ payload }, {call, put, select}){
			const data=yield call(getDivisionTenantTree,{});
			if(data && data.result == 0){
				const divisionTree=handleSelectTreeData(JSON.parse(data.data).items);
				yield put({type:'updateState',payload:{divisionTree}});
			}else {
				if(data && data.exception){
					message.error(data.exception);
				}
			}
		},
		//获取参数类型值
		// *getBaseData({payload},{call,put,select}){
		// 	var baseData = {};
		// 	for( var key in ParamType){
		// 		var data=yield call(getSingleParam,{typeCode:key});
		// 		if(data && data.result == 0){
		// 			baseData[key] = data.data;
		// 		}else {
		// 			baseData[key] = [];
		// 		}
		// 	}
		// 	yield put({type:'updateState',payload:{baseData:baseData}});
		// },
		//获取参数类型值
		*getBaseData({payload},{call,put,select}){
			var baseData = {};
			var param = [];
			for( var key in ParamType){
				param.push(key);
			}
			var data=yield call(getMultiParam,JSON.stringify(param));
			if(data && data.result == 0){
				baseData = handleParam(data.data);
				yield put({type:'updateState',payload:{baseData}});
			}else {
				if(data && data.exception){
					message.error(data.exception);
				}
			}
		},
		*addSystemStaff({payload},{select,call,put}){
			const {newItem,codeUnique,searchOrgName}=yield select(({systemStaff})=>systemStaff);
			let formCheck = [
				{checkType:'required', checkVal: newItem.code },
				{checkType:'codeValidator', checkVal: newItem.code },
				{checkType:'required', checkVal: newItem.name },
				{checkType:'nameValidator', checkVal: newItem.name },
				{checkType:'number', checkVal:newItem.orderIndex },
				{checkType:'phone', checkVal:newItem.phone },
				{checkType:'email', checkVal:newItem.email },
				{checkType:'email', checkVal:newItem.innerEmail }
			];
			if(formValidation(formCheck) && !codeUnique){
				yield put({type:'updateState',payload:{saving:true}});
				const data = yield call(saveSystemStaff ,{...newItem,orgName:searchOrgName});
				if(data && data.result == 0){
					yield put({type:'updateNewItem',payload:{visible:false}});
					yield put({type:'getTableData'});
					message.success(data.msg,3);
				}else {
					if(data && data.exception){
						message.error(data.exception);
					}
				}
				yield put({type:'updateState',payload:{saving:false}});
			}
		},
		*updateSystemStaff({payload},{select,call,put}){
			const {editItem,codeUnique,defaultCloumns}=yield select(({systemStaff})=>systemStaff);
			let formCheck = [
				{checkType:'required', checkVal: editItem.code },
				{checkType:'codeValidator', checkVal: editItem.code },
				{checkType:'required', checkVal: editItem.name },
				{checkType:'nameValidator', checkVal: editItem.name },
				{checkType:'number', checkVal:editItem.orderIndex },
				{checkType:'phone', checkVal:editItem.phone },
				{checkType:'email', checkVal:editItem.email },
				{checkType:'email', checkVal:editItem.innerEmail }
			];
			if(formValidation(formCheck) && !codeUnique){
				yield put({type:'updateState',payload:{saving:true}});
				const data = yield call(updateSystemStaff ,handleEditItem(defaultCloumns, editItem));
				if(data && data.result == 0){
					yield put({type:'updateEditItem',payload:{visible:false}});
					yield put({type:'getTableData'});
					message.success(data.msg,3);
				}else {
					if(data && data.exception){
						message.error(data.exception);
					}
				}
				yield put({type:'updateState',payload:{saving:false}});
			}
		},
		*validateSystemStaff({payload},{select,call,put}){
			const data=yield call(validateSystemStaff,{
			  key: payload.obj.key,
			  param:{
				  ...payload.obj
			  }
			});
			if(data && data.result==0){
				yield put({type:'updateState',payload:{codeUnique:!data.data}});
			}
		},
		*validateStaffPhone({payload},{select,call,put}){
			const data=yield call(validateStaffPhone,{
			  key: payload.obj.key,
			  param:{
				  ...payload.obj
			  }
			});
			if(data && data.result==0){
				yield put({type:'updateState',payload:{phoneUnique:!data.data}});
			}
		},
		*validateUserName({payload},{select,call,put}){
			const data=yield call(validateTenantAccount,{
				...payload.obj
			});
			if(data && data.result==0){
				yield put({type:'updateState',payload:{userNameUnique:!data.data}});
			}
		},
		*validateUserName_U({payload},{select,call,put}){
			const data=yield call(validateAccount_U,{
				...payload.obj
			});
			if(data && data.result==0){
				yield put({type:'updateState',payload:{userNameUnique:!data.data}});
			}
		},
		*deletesSystemStaff({payload},{select,call,put}){
			const {selectedRowKeys}=yield select(({systemStaff})=>systemStaff);
			const data=yield call(deletesSystemStaff, JSON.stringify(selectedRowKeys));
			if(data && data.result == 0){
				yield put({type:'getTableData'});
				yield put({type:'updateState',payload:{selectedRowKeys:[]}});
				message.info(data.msg,3);
			}else {
				if(data && data.exception){
					message.error(data.exception);
				}
			}
		},
		*getStaffDtoById({payload},{select,call,put}){
			const {fileListVersion}=yield select(({systemStaff})=>systemStaff);
			yield put({type:'updateState',payload:{fileList:[]}});
			const data=yield call(getSystemStaffDtoById,{
				id: payload.id
			});
			if(data && data.result == 0){
				yield put({type:'updateEditItem',payload:{...data.data}});
				yield put({type:'updateViewItem',payload:{...data.data}});
				if(data.data.photograph && data.data.photograph != '[]'){
					yield put({type:'updateState',payload:{fileList: JSON.parse(data.data.photograph)}});
				}else {
					yield put({type:'updateState',payload:{fileList: []}});
				}
				yield put({type:'updateState',payload:{fileListVersion: fileListVersion+1}});
			}else {
				if(data && data.exception){
					message.error(data.exception);
				}
			}
		},
		*getUserById({payload},{select,call,put}){
			const {fileListVersion}=yield select(({systemStaff})=>systemStaff);
			const data=yield call(getUserById,{
				id: payload.id
			});
			if(data && data.result == 0){
				var user = JSON.parse(data.data);
				yield put({type:'updateEditUserItem',payload:{
					...user,
					staffName: payload.staffName
				}});
				if(user.photoId && user.photoId != '[]'){
					yield put({type:'updateState',payload:{fileList: JSON.parse(user.photoId)}});
				}else {
					yield put({type:'updateState',payload:{fileList: []}});
				}
				yield put({type:'updateState',payload:{fileListVersion: fileListVersion+1}});
			}else {
				if(data && data.exception){
					message.error(data.exception);
				}
			}
		},
		*openUser({payload},{select,call,put}){
			const {newUserItem,userNameUnique}=yield select(({systemStaff})=>systemStaff);
			let formCheck = [
				{checkType:'required', checkVal: newUserItem.userName },
				{checkType:'nameValidator', checkVal: newUserItem.userName },
				{checkType:'required', checkVal:newUserItem.password },
				{checkType:'required', checkVal:newUserItem.confirm_password }
			];
			if(newUserItem.permissionScope == '3'){
				formCheck.push({checkType:'required', checkVal:newUserItem.customScope});
			}
			if(formValidation(formCheck) && !userNameUnique){
				yield put({type:'updateState',payload:{saving:true}});
				const data = yield call(saveUser ,newUserItem);
				if(data && data.result == 0){
					yield put({type:'updateNewUserItem',payload:{visible:false}});
					yield put({type:'getTableData'});
					message.success(data.msg,3);
				}else {
					if(data && data.exception){
						message.error(data.exception);
					}
				}
				yield put({type:'updateState',payload:{saving:false}});
			}
		},
		*loadDeptOrgTree({ payload }, {call, put, select}){
			const data=yield call(loadDeptOrgTree);
			if(data && data.result == 0){
				const deptOrgTree=handleTreeData(JSON.parse(data.data).items);
				yield put({type:'updateState',payload:{deptOrgTree}});
			}else {
				if(data && data.exception){
					message.error(data.exception);
				}
			}
		},
		*updateUser({payload},{select,call,put}){
			const {editUserItem,userNameUnique,defaultCloumns_user}=yield select(({systemStaff})=>systemStaff);
			let formCheck = [
				{checkType:'required', checkVal: editUserItem.userName },
				{checkType:'nameValidator', checkVal: editUserItem.userName }
			];
			if(editUserItem.permissionScope == '3'){
				formCheck.push({checkType:'required', checkVal:editUserItem.customScope});
			}
			if(formValidation(formCheck) && !userNameUnique){
				yield put({type:'updateState',payload:{saving:true}});
				const data = yield call(updateUser ,handleEditItem(defaultCloumns_user,editUserItem));
				if(data && data.result == 0){
					yield put({type:'updateEditUserItem',payload:{visible:false}});
					yield put({type:'getTableData'});
					message.success(data.msg,3);
				}else {
					if(data && data.exception){
						message.error(data.exception);
					}
				}
				yield put({type:'updateState',payload:{saving:false}});
			}
		},
		//重置用户密码
		*resetUserPassword({payload},{select,call,put}){
			const data=yield call(resetUserPassword,{userId:payload.userId});
			if(data && data.result == 0){
				message.success(data.msg);
			}else {
				if(data && data.exception){
					message.error(data.exception);
				}
			}
		},
		*getPermissionScopeList({payload},{select,call,put}){
			const data=yield call(getPermissionScopeList);
			if(data && data.result == 0){
				yield put({type:'updateState',payload:{permissionScopeList:data.data}});
			}else {
				if(data && data.exception){
					message.error(data.exception);
				}
			}
		},
		//导入
		*importData({payload},{call,put,select}){
        	const importDataCallback = payload.importDataCallback;
        	let data=yield call(importSystemStaffData,{param:payload.uploadFile});
			var msg = '';
			if(data){
				data = JSON.parse(data);
				if(data.result==0){
					msg = data.msg;
				}else if(data.exception){
					msg = data.exception;
				}
				if(typeof(importDataCallback) == "function"){
					importDataCallback(msg);
				}
			}
			yield put({type:'updateState',payload:{uploading:false}});
		},
		//导出
        *exportData({payload},{call,put,select}){
        	const state = yield select(({systemStaff})=>systemStaff);
        	var downloadAll = false;
        	var selectedRowKeys = '';
        	var pageIndex = '';
        	var pageSize = '';
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
        		columnFields: "code,name,gender,birthday,credentialNum,partyPostName,userName,phone",
        		columnNames: "编码,姓名,性别,生日,身份证,职务,用户名,电话号码",
        		downloadAll: downloadAll,
        		downloadIds: selectedRowKeys,
        		page: state.currentPageIndex,
	            rows: state.pageSize,
				departmentId: state.searchDepartmentId,
				selectedId: state.searchOrgId,
				selectedType: state.companyType,
				name: state.searchName,
				code: state.searchCode,
				credentialNum: state.credentialNum,
				gender: state.gender,
				educationId: state.educationId,
				partyPostId: state.postId,
				ageGroupStart: state.ageGroupStart,
				ageGroupEnd: state.ageGroupEnd,
				workYearLimitStart: state.workYearLimitStart,
				workYearLimitEnd: state.workYearLimitEnd,
				ckRange: state.ckRange,
				phone: state.phone,
				isLeave: state.isLeave
			};
			const data = yield call(exportStaff,args);
        	// var url = ManagementConstant.URL_Table + "/cloud/management/staff/tenant/download?";
        	// for(var key in args){
        	// 	url  += (key+"="+args[key]+"&"); 
        	// }
        	// url = url.substring(0,url.length-1);
        	// window.open(url);
        },
        //加载角色树
        *loadSystemRoleTree({payload},{call,put,select}){
            const data=yield call(loadSystemRoleTree);
            if(data && data.result==0){
                const roleTreeData=treedataHandler(JSON.parse(data.data).items);
                yield put({type:'updateState',payload:{roleTreeData}});
            }else {
                if(data && data.exception){
                    message.error(data.exception);
                }
            }
        },
        //加载人员角色关系列表
        *getUserRoleRelation({payload},{select,call,put}){
            const data=yield call(getUserRoleRelation,{
                userId:payload.userId
            });
            if(data && data.result==0){
                const fData = data.data.map((item)=>{return item.id;});
                yield put({type:'updateState',payload:{checkedKeys:fData,selectedRoleKeys:fData}});
            }else {
                if(data && data.exception){
                    message.error(data.exception);
                }
            }
		},
		//加载角色类型
        *loadRoleStyle({payload},{call,put,select}){
            const data=yield call(loadRoleStyle,{userId: payload.userId});
            if(data && data.result==0){
				yield put({type:'updateState',payload:{roleStyle:data.data}});
				if(true || data.data != 0){
					yield put({type:'loadTenantRoleTree'});
					yield put({type:'getTenantUserRoleRelation',payload:{userId:payload.userId}});
				}
            }else {
                if(data && data.exception){
                    message.error(data.exception);
                }
            }
        },
		//加载租户角色树
        *loadTenantRoleTree({payload},{call,put,select}){
            const data=yield call(loadTenantRoleTree);
            if(data && data.result==0){
                const roleTreeData_tenant=treedataHandler(JSON.parse(data.data).items);
                yield put({type:'updateState',payload:{roleTreeData_tenant}});
            }else {
                if(data && data.exception){
                    message.error(data.exception);
                }
            }
        },
        //加载租户人员角色关系列表
        *getTenantUserRoleRelation({payload},{select,call,put}){
            const data=yield call(getTenantUserRoleRelation,{
                userId:payload.userId
            });
            if(data && data.result==0){
                const fData = data.data.map((item)=>{return item.id;});
                yield put({type:'updateState',payload:{checkedKeys_tenant:fData,selectedRoleKeys_tenant:fData}});
            }else {
                if(data && data.exception){
                    message.error(data.exception);
                }
            }
        },
        *userRoleTreeDataSave({payload},{select,call,put}){
            const {selectedRoleKeys,userId_b,selectedRoleKeys_tenant}=yield select(({systemStaff})=>systemStaff);
            const param={
                userId: userId_b,
				roleIds: selectedRoleKeys.toString(),
				tenantRoleIds: selectedRoleKeys_tenant.toString()
            }
            const data=yield call(userRoleTreeDataSave,param);
            if(data && data.result==0){
                message.success(data.msg,3);
                yield put({type:'updateState',payload:{bindRoleWindow:false}});
            }else{
                if(data && data.exception){
                    message.error(data.exception);
                }
            }
		},
		*getImportTableData({payload }, { call, put,select }){
			const {importDetail} = yield select(({systemStaff})=>systemStaff);
			const successful = payload.successful;
            yield put({type:'updateImportDetail',payload:{tableLoading:true,selectedRowKeys:[]}});

            const data  = yield call(getImportTableData, {
                page: importDetail.currentPageIndex,
                rows: importDetail.pageSize,
                's_EQ_successful_B': successful
            });
            if(data && data.result == 0) {
                const tableData = handleTableData(data.data.rows);
                const totalItems = data.data.total;
                yield put({type: 'updateImportDetail', payload: {tableData, totalItems}});
            }else {
                if(data && data.exception){
					message.error(data.exception);
				}
            }
            yield put({type: 'updateImportDetail', payload: {tableLoading: false}});
		},
		*getFunctionPermissionMap({payload},{select,call,put}){
			const {functionCodes} = yield select(({systemStaff})=>systemStaff);
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
        updateNewUserItem(state, action) {
        	return {
        		...state,
        		newUserItem: {
        			...state.newUserItem,
        			...action.payload
        		}
        	}
		},
		updateEditUserItem(state, action) {
        	return {
        		...state,
        		editUserItem: {
        			...state.editUserItem,
        			...action.payload
        		}
        	}
		},
		clearNewUserItem(state,action){
			return {
				...state,
				newUserItem: {
					...state.newUserItem,
					visible: true,
					checkState: false,
					id:'',
					userName: '',
					password: '',
					confirm_password: '',
					permissionScope: '1',
					customScope: '',
					photoId: ''
				}
			}
		},
		clearSearchFilter(state, action) {
			return {
				...state,
				searchName: '',
				searchCode: '',
				credentialNum: '',
				educationId:'',
				ckRange: false,
				ageGroupStart:'',
				ageGroupEnd:'',
				workYearLimitStart:'',
				workYearLimitEnd:'',
				postId:'',
                gender: '',
                socialSecurityNo:'',
				isLeave: '',
				phone:'',
			}
		},
        clearNewItem(state, action) {
        	return {
        		...state,
        		newItem: {
					...state.newItem,
					visible: true,
        			id: '',
					name: '',
					code: '',
					gender: '男',
					birthday: '',
					credentialNum: '',
					nationId: '',
					maritalStatusId: '',
					politicalStatusId: '',
					joinWorkTime: null,
					workYearLimit: '',
					isLeave: '0',
					leaveTime: null,
					workTypeCode: '',
					orderIndex: '',
					description: '',

					birthPlace: '',
					presentPlace: '',
					livePlace: '',

					phone: '',
					officeTel: '',
					email: '',
					innerEmail: '',

					graduate: '',
					educationId: '',
					educationName: '',

					authorizeId: '',
					authorizeName: '',
					postId: '',
					postName: '',
					partyPostId: '',
					partyPostName: '',
					entryHereTime: null,
					idCard: '',
					socialSecurityNo: '',
					socialSecuritycase: '',
					outSourcing:false,
					checkOrder:false,
					checkYear:false,
					isWillMan:'false',
					address:'',
					willCheckDivisionIds:'',
					willWorkUnit:'',
					photograph: '[]'
        		}
        	}
		},
		updateImportDetail(state, action) {
        	return {
        		...state,
        		importDetail: {
        			...state.importDetail,
        			...action.payload
        		}
        	}
		}
	}
}
//下拉选择树，数据处理
function handleSelectTreeData(data){
    if(typeof(data) == 'undefined'){
        data = [];
    }
    return data.map((item) => {
        if (item.length == 0) {
            return {
                key: item.id,
                name: item.name,
                attr: item,
				nodeType: item.nodeType,
				disabled: item.id == '-1'
            }
        }else {
            return {
                key: item.id,
                name: item.name,
                attr: item,
                nodeType: item.nodeType,
                children: handleTreeData(item.children),
				disabled: item.id == '-1'
            }
        }
        
    });
}

const handleParam = (data)=>{
	if("undefined" == typeof(data)){
		data={};
	}
	var result = {};
	for(var key in ParamType){
		if(typeof(data[ParamType[key]]) == 'undefined'){
			result[key]=[];
		}else{
			result[key] = data[ParamType[key]];
		}
	}
	return result;
}

const treedataHandler = (data)=>{
    return data.map((element)=>{
      var arr1 = [];
      var disabled = true;
      if(element.children && element.children.length != 0){
        arr1 = treedataHandler(element.children);
      }
      //功能树的筛选条件
      if(element.nodeType == "Role"){
          disabled = false;
      }
      return {
        key: element.id,
        name: element.name,
        nodeType: element.nodeType,
        isLeaf: element.leaf,
        children: arr1,
        // disableCheckbox: disabled
      }
    });
}