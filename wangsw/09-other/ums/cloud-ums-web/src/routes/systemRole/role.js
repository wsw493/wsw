import React from 'react';
import { connect } from 'dva';
import styles from './role.less';
import {Table,Input,Select,Button,Modal,Pagination,message,Radio } from 'antd';
import VtxDataGrid from '../../components/vtxCommon/VortexDatagrid/VortexDatagrid';
import VtxGrid from '../../components/vtxCommon/VtxGrid/VtxGrid';
import EditItem from '../../components/role/editItem';
import ViewItem from '../../components/role/viewItem';
import RoleGroupTree from '../../components/systemRole/roleGroupTree';
import VtxPopconfirm from '../../components/vtxCommon/vtxPopconfirm';
import MenuFunctionTree from '../../components/systemRole/menuFunctionTree';
const RadioGroup = Radio.Group;

function Role({dispatch,systemRole}) {
	const {tableData,tableLoading,pageSize,currentPageIndex,newItem,editItem,viewItem,treeSystemId,roleId,
        selectedRowKeys,saving,totalItems,treeData,searchParentId,nameUnique,codeUnique,nodeType,systemId,systemList,
        roleName,checkedKeys_func,haveChecked,menuFunctionTreeData,addFunwindow,sort,functionCodesMap} = systemRole;
    const column=[
		{
			title: '编码',
	        dataIndex: 'code',
	        key: 'code',
	        width:80,
            nowrap:true,
            sorter: true
		},{
            title: '名称',
            dataIndex: 'name',
            key: 'name',
            width:80,
            nowrap:true,
            render:(text,record,index)=>{
                return (
                    functionCodesMap['']?
                    <a href="javascript:void(0);" style={{'textDecoration':'underline','color':'rgba(0,0,0,0.65)'}} onClick={
                        ()=>{
                            showViewWindow(record.id);
                        }
                    }>  {text}  </a>: <span>{text}</span>
                    )
            }
        },{
			title:'描述',
			dataIndex:'description',
			key:'description',
			width: 80,
	        nowrap:true
		},{
			title:'排序值',
			dataIndex:'orderIndex',
			key:'orderIndex',
			width: 80,
	        nowrap:true
		},{
			title:'操作',
			dataIndex:'action',
			width: 120,
            // nowrap:true,
			render:(text,record,index)=>{
				return(
					<span>
                        {
                            functionCodesMap['CF_MANAGE_ROLE_FUN_MENU']?
                            <a onClick={()=>{
                                showAddFunctionWindow(record.id);
                            }}>绑定功能</a>:''
                        }
						
					</span>
				)
			}
		}
	];
	//新增页面参数
    const newItemProps = {
        modalProps:{
            title:'角色管理 > 新增角色',
            visible: newItem.visible,
            onCancel:hideNewWindow,
            width:800,
            footer:[
            <Button key="cancel" size="large" onClick={()=>{
                dispatch({type:'systemRole/clearNewItem'});
            }}>清空</Button>,
            <Button key="submit" type="primary" size="large" loading={saving} onClick={()=>{
                dispatch({type:'systemRole/updateNewItem',payload:{checkState: true}});
                dispatch({type:'systemRole/addRole'});
            }}>
              保存
            </Button>,
            ],
        },
        contentProps:{
            ...newItem,
            nameUnique,
            codeUnique,
            updateItem(obj){
                dispatch({type:'systemRole/updateNewItem',payload:{
                    ...obj
                }})
            },
            validateRole(obj){
                dispatch({type:"systemRole/validateRole",payload:{obj:obj}});
            }
        }
    }
    const editItemProps = {
        modalProps:{
            title:'角色管理 > 编辑角色',
            visible: editItem.visible,
            onCancel:hideEditWindow,
            width:800,
            footer:[
            <Button key="cancel" size="large" onClick={()=>{
                hideEditWindow();
            }}>取消</Button>,
            <Button key="submit" type="primary" size="large" loading={false} onClick={()=>{
                dispatch({type:'systemRole/updateEditItem',payload:{checkState:true}});
                dispatch({type:'systemRole/updateRole'});
            }}>
              保存
            </Button>,
            ],
        },
        contentProps:{
            ...editItem,
            nameUnique,
            codeUnique,
            updateItem(obj){
                dispatch({type:'systemRole/updateEditItem',payload:{
                    ...obj
                }})
            },
            validateRole(obj){
                dispatch({type:"systemRole/validateRole",payload:{obj:obj}});
            }
        }
    }

    const viewItemProps = {
        modalProps:{
            title:'角色管理 > 查看角色',
            visible: viewItem.visible,
            onCancel:hideViewWindow,
            width:600,
            footer:[
                <Button key="cancel" size="large" onClick={()=>{
                    hideViewWindow();
                }}>关闭</Button>,
            ],
        },
        contentProps:{
            ...viewItem,
        }
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
            onChange(selectedRowKeys){
                dispatch({type:'systemRole/updateState',payload:{
                    selectedRowKeys:selectedRowKeys
                }});
            }
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
            	dispatch({type:'systemRole/updateState',payload:{
                    currentPageIndex:page,
                    pageSize
                }})
                dispatch({type:'systemRole/getTableData'})
            },
            // pageSize 变化的回调
            onShowSizeChange(current, size){
            	dispatch({type:'systemRole/updateState',payload:{
                    currentPageIndex:current,
                    pageSize:size
                }})
                dispatch({type:'systemRole/getTableData'})
            },
            showTotal: total => `合计 ${totalItems} 条`
        },
        onChange:(pagination, filters, sorter)=>{
            dispatch({type:'systemRole/updateState',payload:{
                sort: sorter.field?sorter.field:sort,
                order: sorter.order=="descend"?'desc':'asc'
            }});
            dispatch({type:'systemRole/getTableData'});
        }
    }
    function showAddFunctionWindow(id){
        dispatch({type:'systemRole/updateState',payload:{roleId:id,addFunwindow:true}});
        dispatch({type:'systemRole/getSystemList'});
        // dispatch({type:'systemRole/getRolefunTreeData',payload:{roleId:id}});
    }
    function hideAddFunctionWindow(){
        dispatch({type:'systemRole/updateState',payload:{addFunwindow:false}});
    }
    function showNewWindow(){
        if(nodeType == 'Root' || nodeType == 'System'){
            message.error("请选择角色组添加角色");
        }else {
            dispatch({type:'systemRole/clearNewItem'});
            dispatch({type:'systemRole/updateNewItem',payload:{
                groupId: searchParentId,
                systemId
            }})
        }
    }
    function hideNewWindow(){
        dispatch({type:'systemRole/updateNewItem',payload:{
            visible:false
        }})
    }
    function showEditWindow(id){
        if(selectedRowKeys.length == 0){
            message.error("请选择你要修改的数据")
        }else if(selectedRowKeys.length > 1){
            message.error("每次只能修改一条数据")
        }else{
            dispatch({type:'systemRole/getDtoById',payload:{id:selectedRowKeys[0]}});
            dispatch({type:'systemRole/updateEditItem',payload:{
                visible:true
            }});
        }
    }
    function hideEditWindow(){
        dispatch({type:'systemRole/updateEditItem',payload:{
            visible:false
        }})
    }
    function showViewWindow(id){
        if(typeof(id)=="string"){
            dispatch({type:'systemRole/getDtoById',payload:{id}});
            dispatch({type:'systemRole/updateViewItem',payload:{
                visible:true
            }});
        }else {
            if(selectedRowKeys.length == 0){
                message.error("请选择你要查看的数据")
            }else if(selectedRowKeys.length > 1){
                message.error("每次只能查看一条数据")
            }else{
                dispatch({type:'systemRole/getDtoById',payload:{id:selectedRowKeys[0]}});
                dispatch({type:'systemRole/updateViewItem',payload:{
                    visible:true
                }});
            }
        }
    }
    function hideViewWindow(){
        dispatch({type:'systemRole/updateViewItem',payload:{
            visible:false
        }})
    }
    const roleGroupTreeProps = {
        roleGroupTree:treeData,
        dispatch
    }
    function bulkDelete(){
        if(selectedRowKeys.length==0){
            message.warning('当前没有选中的行可以删除！');
        }
        else{
            Modal.confirm({
                content: `确定删除选中的${selectedRowKeys.length}条数据吗？`,
                okText: '确定',
                cancelText: '取消',
                onOk(){
                    dispatch({type:'systemRole/deletesRole'});
                }
            });
        }
    }
    const querySelect = ()=>{
        dispatch({type:'systemRole/initTableOpt'});
        dispatch({type:'systemRole/getTableData'})
    }
    const clearQuery = ()=>{
        dispatch({type:'systemRole/initTableOpt'});
        dispatch({type:'systemRole/updateState',payload:{roleName:''}});
        dispatch({type:'systemRole/getTableData'})
    }
    const funroleAddProps={
    	checkedKeys_func,
    	haveChecked,
		menuFunctionTreeData:menuFunctionTreeData,
		dispatch
    }
    const bindSave = ()=>{
        dispatch({type:'systemRole/rolefunTreeDataSave'});
    }
    const radioStyle = {
        display: 'block'
    };
	return (
		<div className={styles.main}>
            <div className={styles.leftTree}>
                <div>
                    <RoleGroupTree {...roleGroupTreeProps}/>
                </div>
            </div>
            <div className={styles.rigthCont}>
                <VtxGrid 
                titles={['名称']}
                gridweight={[1]}
                confirm={querySelect}
                clear={clearQuery}
                >
                    <Input value={roleName} onChange={(e)=>{
                        dispatch({type:'systemRole/updateState',payload:{roleName:e.target.value}});
                    }} placeholder='输入名称'/>
                </VtxGrid>
                <div className={styles.bt_bar}>
                    {functionCodesMap['CF_MANAGE_SYSTEMROLE_ADD']?<Button icon="file-add" onClick={showNewWindow}>新增</Button>:''}
                    {functionCodesMap['CF_MANAGE_SYSTEMROLE_DEL']?<Button icon="delete" onClick={bulkDelete}>删除</Button>:''}
                    {functionCodesMap['CF_MANAGE_SYSTEMROLE_UPDATE']?<Button icon="edit" onClick={showEditWindow}>修改</Button>:""}
                    {functionCodesMap['CF_MANAGE_SYSTEMROLE_VIEW']?<Button icon="eye-o" onClick={showViewWindow}>查看</Button>:''}
                </div>
        		<div className={styles.table}>
                    <VtxDataGrid {...gridProps}/>
                </div>
            </div>
			<EditItem  {...newItemProps} />
            <EditItem  {...editItemProps} />
            <ViewItem {...viewItemProps} />

            <Modal title='添加功能' width={1100} visible={true||addFunwindow} maskClosable={false} onCancel={hideAddFunctionWindow} footer={[
				<Button type="primary" size="large" key='submit' onClick={bindSave}>保存</Button>
				]}>
                <div className={styles.TreePanel}>
                    <div className={styles.left}>
                        <div className={styles.title}>系统列表</div>
                        <div className={styles.listContent}>
                            <RadioGroup style={radioStyle} value={treeSystemId} onChange={(e)=>{
                                dispatch({type:'systemRole/updateState',payload:{treeSystemId: e.target.value}});
                                dispatch({type:'systemRole/getRolefunTreeData',payload:{roleId,systemId:e.target.value}});
                            }}>
                                {
                                    systemList.map((item,index)=>{
                                        return <Radio key={index} value={item.id}>{item.systemName}</Radio>
                                    })
                                }
                            </RadioGroup>
                        </div>
                    </div>
                    <div className={styles.right}>
                        <MenuFunctionTree {...funroleAddProps}/>
                    </div>
                </div>
			</Modal>

		</div>
	)
}
export default connect(({systemRole})=>({systemRole}))(Role);
