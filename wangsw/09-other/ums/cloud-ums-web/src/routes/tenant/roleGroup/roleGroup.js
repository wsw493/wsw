import React from 'react';
import { connect } from 'dva';
import styles from './roleGroup.less';
import {Table,Input,Select,Button,Modal,Pagination } from 'antd';
import VtxDataGrid from '../../../components/vtxCommon/VortexDatagrid/VortexDatagrid';
import VtxGrid from '../../../components/vtxCommon/VtxGrid/VtxGrid';
import EditItem from '../../../components/tenant/roleGroup/editItem';
import ViewItem from '../../../components/tenant/roleGroup/viewItem';
import RoleGroupTree from '../../../components/tenant/roleGroup/roleGroupTree';
import VtxPopconfirm from '../../../components/vtxCommon/vtxPopconfirm';

function RoleGroup({dispatch,tenantRoleGroup}) {
	const {tableData,tableLoading,pageSize,currentPageIndex,newItem,editItem,viewItem,
		selectedRowKeys,saving,totalItems,treeData,searchParentId,nameUnique,codeUnique,functionCodesMap } = tenantRoleGroup;
    const column=[
		{
            title: '角色组名称',
            dataIndex: 'name',
            key: 'name',
            width:80,
            nowrap:true,
            render:(text,record,index)=>{
                return (
                    functionCodesMap['CF_MANAGE_TENANT_RG_VIEW']?
                    <a href="javascript:void(0);" style={{'textDecoration':'underline','color':'rgba(0,0,0,0.65)'}} onClick={
                        ()=>{
                            showViewWindow(record);
                        }
                    }>  {text}  </a>:<span>{text}</span>
                    )
            }
        },{
			title: '编码',
	        dataIndex: 'code',
	        key: 'code',
	        width:80,
	        nowrap:true
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
						{functionCodesMap['CF_MANAGE_TENANT_RG_VIEW']?<a onClick={()=>{
                                                    showViewWindow(record);
                                                }}>查看</a>:''}
						{functionCodesMap['CF_MANAGE_TENANT_RG_UPDATE']?<span className="ant-divider" />:''}
						{functionCodesMap['CF_MANAGE_TENANT_RG_UPDATE']?<a onClick={()=>{
                                                    showEditWindow(record);
                                                }}>修改</a>:''}
                        {functionCodesMap['CF_MANAGE_TENANT_RG_DEL']?<span className="ant-divider" />:''}
                        <VtxPopconfirm title="确认删除此记录吗？" onConfirm={()=>{
                            dispatch({type:'tenantRoleGroup/deleteRoleGroup',payload:{id:record.id}});
                        }}>
                            {functionCodesMap['CF_MANAGE_TENANT_RG_DEL']?<a>删除</a>:''}
                        </VtxPopconfirm>
					</span>
				)
			}
		}
	];
	//新增页面参数
    const newItemProps = {
        modalProps:{
            title:'角色组管理 > 新增角色组',
            visible: newItem.visible,
            onCancel:hideNewWindow,
            width:600,
            footer:[
            <Button key="cancel" size="large" onClick={()=>{
                dispatch({type:'tenantRoleGroup/clearNewItem'});
            }}>清空</Button>,
            <Button key="submit" type="primary" size="large" loading={saving} onClick={()=>{
                dispatch({type:'tenantRoleGroup/updateNewItem',payload:{checkState: true}});
                dispatch({type:'tenantRoleGroup/addRoleGroup'});
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
                dispatch({type:'tenantRoleGroup/updateNewItem',payload:{
                    ...obj
                }})
            },
            validateRoleGroup(obj){
                dispatch({type:"tenantRoleGroup/validateRoleGroup",payload:{obj:obj}});
            }
        }
    }
    const editItemProps = {
        modalProps:{
            title:'角色组管理 > 编辑角色组',
            visible: editItem.visible,
            onCancel:hideEditWindow,
            width:1000,
            footer:[
            <Button key="cancel" size="large" onClick={()=>{
                hideEditWindow();
            }}>取消</Button>,
            <Button key="submit" type="primary" size="large" loading={false} onClick={()=>{
                dispatch({type:'tenantRoleGroup/updateEditItem',payload:{checkState:true}});
                dispatch({type:'tenantRoleGroup/updateRoleGroup'});
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
                dispatch({type:'tenantRoleGroup/updateEditItem',payload:{
                    ...obj
                }})
            },
            validateRoleGroup(obj){
                dispatch({type:"tenantRoleGroup/validateRoleGroup",payload:{obj:obj}});
            }
        }
    }

    const viewItemProps = {
        modalProps:{
            title:'角色组管理 > 查看角色组',
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
                dispatch({type:'tenantRoleGroup/updateState',payload:{
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
            	dispatch({type:'tenantRoleGroup/updateState',payload:{
                    currentPageIndex:page,
                    pageSize
                }})
                dispatch({type:'tenantRoleGroup/getTableData'})
            },
            // pageSize 变化的回调
            onShowSizeChange(current, size){
            	dispatch({type:'tenantRoleGroup/updateState',payload:{
                    currentPageIndex:current,
                    pageSize:size
                }})
                dispatch({type:'tenantRoleGroup/getTableData'})
            },
            showTotal: total => `合计 ${totalItems} 条`
        }/*,
        onChange: (pagination, filters, sorter)=>{
            console.log(sorter)
        }*/
    }
    function showNewWindow(){
        dispatch({type:'tenantRoleGroup/clearNewItem'});
        dispatch({type:'tenantRoleGroup/updateNewItem',payload:{
            parentId: searchParentId
        }})
    }
    function hideNewWindow(){
        dispatch({type:'tenantRoleGroup/updateNewItem',payload:{
            visible:false
        }})
    }
    function showEditWindow(data){
        dispatch({type:'tenantRoleGroup/updateEditItem',payload:{
            ...data,
            visible:true
        }})
    }
    function hideEditWindow(){
        dispatch({type:'tenantRoleGroup/updateEditItem',payload:{
            visible:false
        }})
    }
    function showViewWindow(data){
        dispatch({type:'tenantRoleGroup/updateViewItem',payload:{
            ...data,
            visible:true
        }})
    }
    function hideViewWindow(){
        dispatch({type:'tenantRoleGroup/updateViewItem',payload:{
            visible:false
        }})
    }
    const roleGroupTreeProps = {
        roleGroupTree:treeData,
        searchParentId,
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
                    dispatch({type:'tenantRoleGroup/deletesRoleGroup'});
                }
            });
        }
    }
	return (
		<div className={styles.main}>
            <div className={styles.leftTree}>
                <div>
                    <RoleGroupTree {...roleGroupTreeProps}/>
                </div>
            </div>
            <div className={styles.rigthCont}>
                <div className={styles.bt_bar}>
                    {functionCodesMap['CF_MANAGE_TENANT_RG_ADD']?<Button icon="file-add" onClick={showNewWindow}>新增</Button>:''}
                    {/*<Button icon="delete" onClick={bulkDelete}>删除</Button>*/}
                </div>
        		<div className={styles.table}>
                    <VtxDataGrid {...gridProps}/>
                </div>
            </div>
			<EditItem  {...newItemProps} />
            <EditItem  {...editItemProps} />
            <ViewItem {...viewItemProps} />

		</div>
	)
}
export default connect(({tenantRoleGroup})=>({tenantRoleGroup}))(RoleGroup);
