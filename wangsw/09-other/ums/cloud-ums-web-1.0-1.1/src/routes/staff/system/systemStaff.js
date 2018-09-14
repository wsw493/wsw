import React from 'react';
import {connect} from 'dva';
import {Button, Input, Table, Modal, Popconfirm, message, Alert,Select,Checkbox,Upload,Radio,notification,Pagination,Tabs  } from 'antd';
import VtxDataGrid from '../../../components/vtxCommon/VortexDatagrid/VortexDatagrid';
import VtxGrid from '../../../components/vtxCommon/VtxGrid/VtxGrid';
import VtxExport from '../../../components/vtxCommon/VortexUpload/VtxExport';
import style from './systemStaff.less';
import OrgTree from '../../../components/staff/tenantStaff/orgTree';
import RoleTree from '../../../components/staff/tenantStaff/roleTree';
import TenantRoleTree from '../../../components/staff/tenantStaff/tenantRoleTree';
import EditUser from '../../../components/staff/user/openUser';
import EditItem from '../../../components/staff/tenantStaff/editItem';
import ViewItem from '../../../components/staff/tenantStaff/viewItem';
import ImportResult from '../../../components/importResult/importResult';
import {getArgs} from '../../../utils/toolFunctions';
const RadioGroup = Radio.Group;
const Option = Select.Option;
const TabPane = Tabs.TabPane;
const SystemStaff = ({dispatch,tableData,searchCode,searchName,gender,postId,credentialNum,ageGroupStart,ageGroupEnd,
        workYearLimitStart,workYearLimitEnd,ckRange,selectedRowKeys,operatedRow,currentPageIndex,selectOrgTree,
        pageSize,totalItems,tableLoading,orgTree,newItem,editItem,viewItem,permissionScopeList,divisionTree,
        educationId,baseData,saving,searchDepartmentId,searchOrgId,fileList,fileListVersion,userNameUnique,
        newUserItem,editUserItem,phoneUnique,codeUnique,passwordFlag,importWindow,uploading,exportWindow,radioG,
        updateImportState,socialSecurityNo,checkedKeys,selectedRoleKeys,bindRoleWindow,roleTreeData,
        deptOrgTree,importDetail,sort,functionCodesMap,isLeave,phone,roleTreeData_tenant,checkedKeys_tenant,
        selectedRoleKeys_tenant,roleStyle,companyType})=>{
    let myForm = null;
	const column = [
	{
		title: '编码',
		dataIndex: 'code',
        key: 'code',
        width:50,
        nowrap:true
	},{
		title: '姓名',
		dataIndex: 'name',
		key: 'name',
        sorter: true,
        width:50,
        nowrap:true,
		render: (text, record, index)=>{
			return functionCodesMap['CF_MANAGE_SYSTEM_STAFF_VIEW']?<a onClick={()=>{
				showViewWindow(record.id);
			}}>{text}</a>:<span>{text}</span>
		}
	},
	{
		title: '性别',
		dataIndex: 'gender',
        key: 'gender',
        width:30,
        nowrap:true
	},{
		title: '生日',
		dataIndex: 'birthday',
        key: 'birthday',
        sorter: true,
        width:60,
        nowrap:true
	},{
		title: '身份证',
		dataIndex: 'credentialNum',
        key: 'credentialNum',
        width:60,
        nowrap:true
	},{
        title: '职务',
        dataIndex: 'partyPostName',
        key: 'partyPostName',
        sorter: true,
        width:40,
        nowrap:true
    },{
		title: '用户名',
		dataIndex: 'userName',
        key: 'userName',
        width:60,
        nowrap:true,
		render: (text, record, index)=>{
			if(null == record.userName){
				return functionCodesMap['CF_MANAGE_USER_ADD']?<a onClick={()=>{
					showNewUserWindow(record.id,record.name);
				}}>开启用户</a>:''
			}else {
				return functionCodesMap['CF_MANAGE_USER_UPDATE']?<a onClick={()=>{
					showEditUserWindow(record.userId,record.name);
				}}>{text}</a>:<span>{text}</span>
			}
		}
	},{
		title: '操作',
		dataIndex: 'op',
        key: 'op',
        width:120,
        nowrap:true,
		render: (text, record, index)=>{
            if(null != record.userId){
                return (
                <span>
                    {functionCodesMap['CF_MANAGE_USER_ROLE_ADD']?<a className={style.button} onClick={()=>{
                        showUserRoleTreeWindow(record.userId);
                    }}>角色配置</a>:''}
                    {functionCodesMap['CF_MANAGE_USER_RESET_PWD']?<a className={style.button} onClick={()=>{
                        resetUserPassword(record.userId);
                    }}>重置密码</a>:''}
                </span>
                )
			}
		}
    }];
    const resetUserPassword = (userId)=>{
        Modal.confirm({
            content: `确认重置该用户密码吗？`,
            okText: '确定',
            cancelText: '取消',
            onOk(){
                dispatch({type:'systemStaff/resetUserPassword',payload:{userId}});
            }
        });
    }
     const gridProps = {
        columns:column,
        dataSource:tableData,
        loading:tableLoading,
        indexColumn:true, //用了这个属性column里的width不能用百分比
        startIndex:(currentPageIndex-1)*pageSize+1,
        autoFit:true,
        rowSelection:{
            type:'checkbox',
            selectedRowKeys,
            onChange:(selectedRowKeys, selectedRows)=>{
                dispatch({type:'systemStaff/updateState',payload:{selectedRowKeys}});
            },
        },
        pagination:{
            showSizeChanger: true,
            pageSizeOptions: ['10', '20', '30', '40','50'],
            showQuickJumper: true,
            current:currentPageIndex,  //后端分页数据配置参数1
            total:totalItems, //后端分页数据配置参数2
            pageSize, //后端分页数据配置参数3
            // 当前页码改变的回调
            onChange(page, pageSize){
                dispatch({type:'systemStaff/updateState',payload:{
                    currentPageIndex:page,
                    pageSize
                }})
                dispatch({type:'systemStaff/getTableData'})
            },
            // pageSize 变化的回调
            onShowSizeChange(current, size){
                dispatch({type:'systemStaff/updateState',payload:{
                    currentPageIndex:current,
                    pageSize:size
                }})
                dispatch({type:'systemStaff/getTableData'})
            },
            showTotal: total => `合计 ${totalItems} 条`
        },
        onChange:(pagination, filters, sorter)=>{
            dispatch({type:'systemStaff/updateState',payload:{
                sort: sorter.field?sorter.field:sort,
                order: sorter.order=="descend"?'desc':'asc'
            }});
            dispatch({type:'systemStaff/getTableData'});
        }
    }

	//新增页面参数
    const newItemProps = {
        modalProps:{
            title:'人员管理 > 新增人员',
            visible: newItem.visible,
            onCancel:hideNewWindow,
            width:1000,
            footer:[
            <Button key="cancel" size="large" onClick={()=>{
                dispatch({type:'systemStaff/clearNewItem'});
            }}>清空</Button>,
            <Button key="submit" type="primary" size="large" loading={saving} onClick={()=>{
                dispatch({type:'systemStaff/updateNewItem',payload:{checkState: true}});
                dispatch({type:'systemStaff/addSystemStaff'});
            }}>
              保存
            </Button>,
            ],
        },
        contentProps:{
            ...newItem,
            baseData,
            selectOrgTree,
            divisionTree,
            codeUnique,
            phoneUnique,
            fileListVersion,
            fileList,
            updateItem(obj){
                dispatch({type:'systemStaff/updateNewItem',payload:{
                    ...obj
                }})
            },
            validateStaff(obj){
                dispatch({type:"systemStaff/validateSystemStaff",payload:{obj}});
            },
            validateStaffPhone(obj){
                dispatch({type:"systemStaff/validateStaffPhone",payload:{obj}});
            }
        }
    }
    //查看页面参数
    const viewItemProps={
        modalProps:{
            title:'人员管理 > 查看人员',
            visible: viewItem.visible,
            onCancel:hideViewWindow,
            width:1000,
            footer:[
                <Button key="cancel" size="large" onClick={()=>{
                    hideViewWindow();
                }}>关闭</Button>,
            ],
        },
        contentProps:{
            ...viewItem,
            fileListVersion,
            fileList
        }
    }
    //编辑页面参数
    const editItemProps={
        modalProps:{
            title:'人员管理 > 修改人员',
            visible: editItem.visible,
            onCancel:hideEditWindow,
            width:1000,
            footer:[
            <Button key="cancel" size="large" onClick={()=>{
                hideEditWindow();
            }}>取消</Button>,
            <Button key="submit" type="primary" size="large" loading={saving} onClick={()=>{
                dispatch({type:'systemStaff/updateSystemStaff'});
            }}>
              保存
            </Button>,
            ],
        },
        contentProps:{
            ...editItem,
            baseData,
            selectOrgTree,
            divisionTree,
            codeUnique,
            phoneUnique,
            fileListVersion,
            fileList,
            updateItem(obj){
                dispatch({type:'systemStaff/updateEditItem',payload:{
                    ...obj
                }})
            },
            validateStaff(obj){
                dispatch({type:"systemStaff/validateSystemStaff",payload:{obj}});
            },
            validateStaffPhone(obj){
                dispatch({type:"systemStaff/validateStaffPhone",payload:{obj}});
            }
        }
    }
    //开启用户参数
    const openUserProps = {
        modalProps:{
            title:'用户管理 > 开启用户',
            visible: newUserItem.visible,
            onCancel:hideNewUserWindow,
            width:1000,
            footer:[
            <Button key="cancel" size="large" onClick={()=>{
                hideNewUserWindow();
            }}>返回</Button>,
            <Button key="submit" type="primary" size="large" loading={saving} onClick={()=>{
                dispatch({type:'systemStaff/updateNewUserItem',payload:{checkState: true}});
                dispatch({type:'systemStaff/openUser'});
            }}>
              保存
            </Button>,
            ],
        },
        contentProps:{
            ...newUserItem,
            userNameUnique,
            deptOrgTree,
            isOpenUser: true,
            permissionScopeList,
            fileListVersion,
            fileList,
            passwordFlag,
            updateItem(obj){
                dispatch({type:'systemStaff/updateNewUserItem',payload:{
                    ...obj
                }})
            },
            updateState(obj){
                dispatch({type:'systemStaff/updateState',payload:{
                    ...obj
                }})
            },
            validateUserName(obj){
                dispatch({type:"systemStaff/validateUserName",payload:{obj}});
            }
        }
    }
    //修改用户参数
    const editUserProps = {
        modalProps:{
            title:'用户管理 > 修改用户',
            visible: editUserItem.visible,
            onCancel:hideEditUserWindow,
            width:1000,
            footer:[
            <Button key="cancel" size="large" onClick={()=>{
                hideEditUserWindow();
            }}>返回</Button>,
            <Button key="submit" type="primary" size="large" loading={saving} onClick={()=>{
                dispatch({type:'systemStaff/updateEditUserItem',payload:{checkState: true}});
                dispatch({type:'systemStaff/updateUser'});
            }}>
              保存
            </Button>,
            ],
        },
        contentProps:{
            ...editUserItem,
            userNameUnique,
            deptOrgTree,
            isOpenUser: false,
            permissionScopeList,
            fileListVersion,
            fileList,
            passwordFlag,
            updateItem(obj){
                dispatch({type:'systemStaff/updateEditUserItem',payload:{
                    ...obj
                }})
            },
            updateState(obj){
                dispatch({type:'systemStaff/updateState',payload:{
                    ...obj
                }})
            },
            validateUserName(obj){
                dispatch({type:"systemStaff/validateUserName_U",payload:{obj}});
            }
        }
    }
    const orgTreeProps = {
        dispatch,
        orgTree,
        namespace:'systemStaff'
    }

    const newClear = ()=>{
        dispatch({type:'systemStaff/clearItem'});
    }

    const addItemPanel = ()=>{
        dispatch({type:'systemStaff/updateState',payload:{newWindow:true,staff:{}}});
    }

    const toUpdatePanel = () => {
        if(selectedRowKeys.length != 1){
            message.warning("请选择一条记录进行编辑");
        }else {
            dispatch({type:'systemStaff/updateState',payload:{editWindow:true}});
        }
        
    }

    const toViewPanel = () => {
        if(selectedRowKeys.length != 1){
            message.warning("请选择一条记录查看");
        }else {
            dispatch({type:'systemStaff/updateState',payload:{viewWindow:true}});
        }
        
    }

    const bulkDelete = ()=>{
        if(selectedRowKeys.length > 0){
            Modal.confirm({
                content: `确定删除选中的${selectedRowKeys.length}条数据吗？`,
                okText: '确定',
                cancelText: '取消',
                onOk(){
                    dispatch({type:'systemStaff/deletesSystemStaff'});
                }
            });
        }else {
            message.warning("选择你要删除的数据");
        }
    }
    const querySelect=()=>{
        dispatch({type:'systemStaff/getTableData'});
    }
    const clearQuery=()=>{
        dispatch({type:'systemStaff/clearSearchFilter'});
        dispatch({type:'systemStaff/getTableData'});
    }
    const queryGenderChanged=(value, option)=>{
        dispatch({type:'systemStaff/updateState',payload:{gender:value}});
    }
    const queryIsLeaveChanged=(value, option)=>{
        dispatch({type:'systemStaff/updateState',payload:{isLeave:value}});
    }
    const queryEducationChanged=(value,option)=>{
        dispatch({type:'systemStaff/updateState',payload:{educationId:value}});
    }
    const queryPartyPostChanged=(value,option)=>{
        dispatch({type:'systemStaff/updateState',payload:{postId:value}});
    }
    function showNewWindow(){
        if(searchDepartmentId == '-1'){
            message.error("不能在根节点上新增");
        }else {
            dispatch({type:'systemStaff/updateState',payload:{fileList:[]}});
            dispatch({type:'systemStaff/getDivisionTreeData'});
            dispatch({type:'systemStaff/clearNewItem'});
            dispatch({type:'systemStaff/updateNewItem',payload:{
                departmentId: searchDepartmentId,
                orgId: searchOrgId
            }})
        }
    }
    function hideNewWindow(){
        dispatch({type:'systemStaff/updateNewItem',payload:{
            visible:false
        }})
    }
    function showEditWindow(id){
        if(selectedRowKeys.length == 0){
            message.error("请选择你要修改的数据")
        }else if(selectedRowKeys.length > 1){
            message.error("每次只能修改一条数据")
        }else{
            dispatch({type:'systemStaff/getDivisionTreeData'});
            dispatch({type:'systemStaff/getStaffDtoById',payload:{id:selectedRowKeys[0]}});
            dispatch({type:'systemStaff/updateEditItem',payload:{
                visible:true,
                checkOrder:false,
                checkYear:false
            }});
        }
    }
    function hideEditWindow(){
        dispatch({type:'systemStaff/updateEditItem',payload:{
            visible:false
        }})
    }
    function showViewWindow(id){
        if(typeof(id)=="string"){
            dispatch({type:'systemStaff/getStaffDtoById',payload:{id}});
            dispatch({type:'systemStaff/updateViewItem',payload:{
                visible:true
            }});
        }else {
            if(selectedRowKeys.length == 0){
                message.error("请选择你要查看的数据")
            }else if(selectedRowKeys.length > 1){
                message.error("每次只能查看一条数据")
            }else{
                dispatch({type:'systemStaff/getStaffDtoById',payload:{id:selectedRowKeys[0]}});
                dispatch({type:'systemStaff/updateViewItem',payload:{
                    visible:true
                }});
            }
        }
    }
    function hideViewWindow(){
        dispatch({type:'systemStaff/updateViewItem',payload:{
            visible:false
        }})
    }
    function showNewUserWindow(id,name){
        dispatch({type:'systemStaff/loadDeptOrgTree'});
        dispatch({type:'systemStaff/getPermissionScopeList'});
        dispatch({type:'systemStaff/updateNewUserItem',payload:{
            staffName: name,
            staffId: id
        }});
        dispatch({type:'systemStaff/clearNewUserItem'});
        dispatch({type:'systemStaff/updateState',payload:{fileListVersion:fileListVersion+1}});
    }
    function hideNewUserWindow (){
        dispatch({type:'systemStaff/updateNewUserItem',payload:{
            visible:false
        }})
    }
    function showEditUserWindow(userId,staffName){
        dispatch({type:'systemStaff/getPermissionScopeList'});
        dispatch({type:'systemStaff/getUserById',payload:{id:userId,staffName}});
        dispatch({type:'systemStaff/updateEditUserItem',payload:{
            visible: true
        }})
    }
    function hideEditUserWindow (){
        dispatch({type:'systemStaff/updateEditUserItem',payload:{
            visible:false
        }})
    }
    function showUserRoleTreeWindow (userId){
        dispatch({type:'systemStaff/updateState',payload:{userId_b:userId}});
        dispatch({type:'systemStaff/loadSystemRoleTree'});
        dispatch({type:'systemStaff/getUserRoleRelation',payload:{userId}});
        dispatch({type:'systemStaff/loadRoleStyle',payload:{userId}});
        // if(roleStyle!=0){
        //     dispatch({type:'systemStaff/loadTenantRoleTree'});
        //     dispatch({type:'systemStaff/getTenantUserRoleRelation',payload:{userId}});
        // }
        dispatch({type:'systemStaff/updateState',payload:{
            bindRoleWindow:true
        }})
    }
    function hideUserRoleTreeWindow (){
        dispatch({type:'systemStaff/updateState',payload:{
            bindRoleWindow:false
        }})
    }
    const userRoleProps = {
        roleTreeData,
        dispatch,
        checkedKeys,
        selectedRoleKeys
    }
    const userRoleProps_tenant = {
        roleTreeData_tenant,
        dispatch,
        checkedKeys_tenant,
        selectedRoleKeys_tenant
    }
    function bindSave (){
        dispatch({type:'systemStaff/userRoleTreeDataSave'});
    }
    const importDataCallback = (mess)=>{
        $("#file").val("");
        notification.open({
            message: '导入结果',
            description: mess,
            duration: 5,
            onClose: ()=>{
            },
            btn: (<Button onClick={()=>{
                dispatch({type:'systemStaff/updateImportDetail',payload:{visible:true}});
                dispatch({type:'systemStaff/getImportTableData',payload:{successful:""}});
            }}>查看详情</Button>)
        });
    }
    const handleFile = (e)=>{
        // dispatch({type:'systemStaff/updateState',payload:{uploadFile:e.target.files[0]}});
        myForm = new FormData();
        myForm.append("file",e.target.files[0]);
    }
    const importDelColumns = 
    [{
        title: '行号',
        dataIndex: 'rowNum',
        key: 'rowNum',
        width:70
    },{
        title: '是否成功',
        dataIndex: 'successful',
        key: 'successful',
        width:70,
        render: (text, record, index)=>{
            var txt = "成功"
            if (text == false){
                txt = "失败";
            }
            return <span>{txt}</span>
        }
    },{
        title: '信息',
        dataIndex: 'message',
        key: 'message',
        width:200
    }];
    const importDelGridProps = {
        columns:importDelColumns,
        dataSource:importDetail.tableData,
        loading:importDetail.tableLoading,
        indexColumn:true, //用了这个属性column里的width不能用百分比
        startIndex:(importDetail.currentPageIndex-1)*importDetail.pageSize+1,
        autoFit:true,
        rowSelection:{
            type:'checkbox',
            selectedRowKeys:importDetail.selectedRowKeys,
            onChange(selectedRowKeys){
                dispatch({type:'systemStaff/updateImportDetail',payload:{
                    selectedRowKeys:selectedRowKeys
                }});
            }
        },
        pagination:{
            showSizeChanger: true,
            pageSizeOptions: ['10', '20', '30', '40','50'],
            showQuickJumper: true,
            current:importDetail.currentPageIndex,  //后端分页数据配置参数1
            total:importDetail.totalItems, //后端分页数据配置参数2
            pageSize:importDetail.pageSize, //后端分页数据配置参数3
            // 当前页码改变的回调
            onChange(page, pageSize){
                dispatch({type:'systemStaff/updateImportDetail',payload:{
                    currentPageIndex:page,
                    pageSize
                }})
                dispatch({type:'systemStaff/getImportTableData'})
            },
            // pageSize 变化的回调
            onShowSizeChange(current, size){
                dispatch({type:'systemStaff/updateImportDetail',payload:{
                    currentPageIndex:current,
                    pageSize:size
                }})
                dispatch({type:'systemStaff/getImportTableData'})
            },
            showTotal: total => `合计 ${importDetail.totalItems} 条`
        }
    }
    //导入数据结果
    const importResultProps = {
        modalProps: {
            title: '导入 > 导入详情',
            visible: importDetail.visible,
            width: 1000,
            footer: null,
            onCancel:()=>{
                dispatch({'type':'systemStaff/updateImportDetail',payload:{visible:false}});
            }
        },
        contentProps: {
            importDetail,
            gridProps:importDelGridProps,
            querySelect: ()=>{
                dispatch({type:'systemStaff/getImportTableData',payload:{successful:importDetail.successful}});
            },
            clearQuery: ()=>{
                dispatch({type:'systemStaff/updateImportDetail',payload:{
                    successful: ''
                }});
                dispatch({type:'systemStaff/getImportTableData',payload:{successful:''}});
            },
            updateImportDetail: (obj)=>{
                dispatch({type:'systemStaff/updateImportDetail',payload:{...obj}});
            }
        }
    }
    const exportProps = {
        downloadURL: 'http://192.168.1.248:18082/cloud/management/staff/tenant/download',
        getExportParams(exportType){
            const columnFields = "code,name,gender,birthday,credentialNum,partyPostName,userName,phone",
                columnNames = "编码,姓名,性别,生日,身份证,职务,用户名,电话号码";
            let downloadAll = false,
                selectedRowKeys1 = '',
                pageIndex1 = '',
                pageSize1 = '';
            if(exportType == "all"){
                downloadAll = true;
            }else if(exportType == "page"){
                
            }else if(exportType == "rows"){
                downloadAll = true;
                selectedRowKeys1 = selectedRowKeys.join(",");
            }
            return {
                param:{
                    columnFields: "code,name,gender,birthday,credentialNum,partyPostName,userName,phone",
                    columnNames: "编码,姓名,性别,生日,身份证,职务,用户名,电话号码",
                    downloadAll: downloadAll,
                    downloadIds: selectedRowKeys1,
                    page: currentPageIndex,
                    rows: pageSize,
                    departmentId: searchDepartmentId,
                    selectedId: searchOrgId,
                    selectedType: companyType,
                    name: searchName,
                    code: searchCode,
                    credentialNum: credentialNum,
                    gender: gender,
                    educationId: educationId,
                    partyPostId: postId,
                    ageGroupStart: ageGroupStart,
                    ageGroupEnd: ageGroupEnd,
                    workYearLimitStart: workYearLimitStart,
                    workYearLimitEnd: workYearLimitEnd,
                    ckRange: ckRange,
                    phone: phone,
                    isLeave: isLeave,
                    'access_token': getArgs('token')
                }
            }
        }
    }
    return (
    	<div className={style.main}>
            <div className={style.leftTree}>
                <OrgTree {...orgTreeProps}/>
            </div>
    		<div className={style.rigthCont}>
                <VtxGrid 
                titles={['编码','姓名','学历','范围','身份证','年龄段','工作年限','职位','性别','社保号','在职状态','电话号码']}
                gridweight={[1,1,1,1,1,1,1,1,1,1,1,1]}
                confirm={querySelect}
                clear={clearQuery}
                hiddenMoreButtion={false}

                >
                    <Input value={searchCode} placeholder={"请输入编码"} onChange={(e)=>{
                        dispatch({type:'systemStaff/updateState',payload:{searchCode:e.target.value}});
                    }}/> 
                    <Input value={searchName} placeholder={"请输入姓名"} onChange={(e)=>{
                        dispatch({type:'systemStaff/updateState',payload:{searchName:e.target.value}});
                    }}/>
                    <Select style={{ width: '100%' }} value={educationId} onChange={queryEducationChanged}>
                        <Option value=''>无</Option>
                        {
                            baseData.STAFF_EDUCATION.map((item)=>{
                                return <Option key={item.value} value={item.value}>{item.text}</Option>
                            })
                        }
                    </Select>
                    <Checkbox checked={ckRange} onChange={(e)=>{
                        dispatch({type:'systemStaff/updateState',payload:{ckRange:e.target.checked}});
                        }}>查询本级及本级以下</Checkbox>
                    <Input value={credentialNum} placeholder={"请输入身份证"} onChange={(e)=>{
                        dispatch({type:'systemStaff/updateState',payload:{credentialNum:e.target.value}});
                    }}/>
                    <div style={{display: 'inline-block',width:'100%'}}>
                        <Input value={ageGroupStart} style={{width:'47%'}} onChange={(e)=>{
                            dispatch({type:'systemStaff/updateState',payload:{ageGroupStart:e.target.value}});
                        }}/>-
                        <Input value={ageGroupEnd} style={{width:'47%'}} onChange={(e)=>{
                            dispatch({type:'systemStaff/updateState',payload:{ageGroupEnd:e.target.value}});
                        }}/>
                    </div>
                    <div style={{display: 'inline-block',width:'100%'}}>
                        <Input value={workYearLimitStart} style={{width:'47%'}} onChange={(e)=>{
                            dispatch({type:'systemStaff/updateState',payload:{workYearLimitStart:e.target.value}});
                        }}/>-
                        <Input value={workYearLimitEnd} style={{width:'47%'}} onChange={(e)=>{
                            dispatch({type:'systemStaff/updateState',payload:{workYearLimitEnd:e.target.value}});
                        }}/>
                    </div>
                    <Select style={{ width: '100%' }} value={postId} onChange={queryPartyPostChanged}>
                        <Option value=''>无</Option>
                        {
                            baseData.STAFF_POST.map((item)=>{
                                return <Option  key={item.value} value={item.value}>{item.text}</Option>
                            })
                        }
                    </Select>
                    <Select style={{ width: '100%' }} value={gender} onChange={queryGenderChanged}>
                        <Option value="" key="0">全部</Option>
                        <Option value="男" key="1">男</Option>
                        <Option value="女" key="2">女</Option>
                    </Select>
                    <Input value={socialSecurityNo} placeholder={"请输入社保号"} onChange={(e)=>{
                        dispatch({type:'systemStaff/updateState',payload:{socialSecurityNo:e.target.value}});
                    }}/>
                    <Select style={{ width: '100%' }} value={isLeave} onChange={queryIsLeaveChanged}>
                        <Option value="" key="4">全部</Option>
                        <Option value="0" key="0">在职</Option>
                        <Option value="1" key="1">离职</Option>
                        <Option value="2" key="2">退休</Option>
                    </Select>
                    <Input value={phone} placeholder={"请输入手机号码"} onChange={(e)=>{
                        dispatch({type:'systemStaff/updateState',payload:{phone:e.target.value}});
                    }}/>
                </VtxGrid>
    			<div className={style.bt_bar}>
    				{functionCodesMap['CF_MANAGE_SYSTEM_STAFF_ADD']?<Button icon="file-add" className='primary' onClick={showNewWindow}>新增</Button>:''}
    				{functionCodesMap['CF_MANAGE_SYSTEM_STAFF_UPDATE']?<Button icon="form" className="primary" onClick={showEditWindow}>修改</Button>:''}
                    {functionCodesMap['CF_MANAGE_SYSTEM_STAFF_VIEW']?<Button icon="file-text" className="view" onClick={showViewWindow}>查看</Button>:''}
    				{functionCodesMap['CF_MANAGE_STAFF_DELETE']?<Button icon="delete" className='delete' onClick={bulkDelete}>删除</Button>:''}
    				<Button icon="select" className="primary" onClick={()=>{
    					dispatch({type:'systemStaff/updateState',payload:{importWindow:true}});
    				}}>导入</Button>
    				<Button icon="export" className="primary" onClick={()=>{
    					dispatch({type:'systemStaff/updateState',payload:{exportWindow:true}});
    				}}>导出</Button>
                    {/* <VtxExport {...exportProps}/> */}
    			</div>

    			<div className={style.table}>
                    <VtxDataGrid {...gridProps}/>
                </div>
                <EditItem  {...newItemProps} />
                <EditItem  {...editItemProps} />
                <ViewItem {...viewItemProps} />
                <EditUser {...openUserProps} />
                <EditUser {...editUserProps} />

                <Modal title="导入数据 " width={760} visible={importWindow} onCancel={()=>{
                        $("#file").val("");
                        dispatch({type:'systemStaff/updateState',payload:{importWindow:false}});
                    }} width={700}  maskClosable={false}
                    footer={[
                        <a className={style.commonBtn} href="/resources/template/人员导入模版.zip">下载模板</a>,
                        <Button key="import" type="primary" size="large" loading={uploading} disabled={uploading} onClick={()=>{
                            if(myForm != null){
                                dispatch({type:'systemStaff/updateState',payload:{uploading:true}});
                                dispatch({type:'systemStaff/importData',payload:{uploadFile:myForm,importDataCallback}});
                            }else {
                                message.error("请选择文件导入");
                            }
                        }}>导入</Button>,
                        <Button key="cancel" type="primary" size="large" onClick={()=>{
                            $("#file").val("");
                            dispatch({type:'systemStaff/updateState',payload:{uploadFile:{},importWindow:false,uploading:false}});
                        }}>取消</Button>
                    ]}>
                    <p className={style.standard}>请选择一个文件上传，完成后点击下方按钮中的"导入"按钮</p>
                   <input type="file" className={style.fileButton} name='file' id='file' onChange={handleFile}/>
                </Modal>

                <Modal title="导出数据 " width={760} visible={exportWindow} onCancel={()=>{
                        dispatch({type:'systemStaff/updateState',payload:{exportWindow:false}});
                    }} width={700}  maskClosable={false}
                    footer={[
                        <Button key="export" type="primary" size="large" onClick={()=>{
                            if(radioG == 'select' && selectedRowKeys.length == 0){
                                message.error("请选择你要导出的数据",2.5);
                            }else {
                                dispatch({type:'systemStaff/exportData',payload:{downloadType:radioG}});
                            }
                        }}>导出</Button>,
                        <Button key="cancel" type="primary" size="large" onClick={()=>{
                            dispatch({type:'systemStaff/updateState',payload:{exportWindow:false}});
                        }}>取消</Button>
                    ]}>
                    <RadioGroup value={radioG} onChange={(e)=>{
                        dispatch({type:'systemStaff/updateState',payload:{radioG:e.target.value}});
                    }}>
                        <Radio name="staff" value="select" defaultChecked="true">导出选中数据</Radio>
                        <Radio name="staff" value="page">导出当页数据</Radio>
                        <Radio name="staff" value="all">导出所有数据</Radio>
                    </RadioGroup>
                </Modal>

                <Modal title='绑定角色' width={760} visible={bindRoleWindow} maskClosable={false} onCancel={hideUserRoleTreeWindow} footer={[
                    <Button type="primary" size="large" key='submit' onClick={bindSave}>保存</Button>
                    ]}>
                    <Tabs defaultActiveKey="1">
                        <TabPane tab='角色树' key='1'>
                            <RoleTree {...userRoleProps}/>
                        </TabPane>
                        <TabPane tab='租户角色树' key='2'>
                            <TenantRoleTree {...userRoleProps_tenant}/>
                        </TabPane>
                    </Tabs>
                </Modal>

                <ImportResult {...importResultProps}/>
    		</div>
    	</div>
    )
}

SystemStaff.propTypes={}

export default connect(({systemStaff})=>{return systemStaff;})(SystemStaff);