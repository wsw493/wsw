import React from 'react';
import { connect } from 'dva';
import styles from './roleGroup.less';
import {Table,Input,Select,Button,Modal,Pagination,message } from 'antd';
import VtxDataGrid from '../../components/vtxCommon/VortexDatagrid/VortexDatagrid';
import VtxGrid from '../../components/vtxCommon/VtxGrid/VtxGrid';
import EditItem from '../../components/roleGroup/editItem';
import ViewItem from '../../components/roleGroup/viewItem';
import RoleGroupTree from '../../components/systemRoleGroup/roleGroupTree';
import VtxPopconfirm from '../../components/vtxCommon/vtxPopconfirm';

function SystemRoleGroup({dispatch,systemRoleGroup}) {
	const {tableData,tableLoading,pageSize,currentPageIndex,newItem,editItem,viewItem,
		selectedRowKeys,saving,totalItems,treeData,searchParentId,nameUnique,codeUnique,functionCodesMap,systemId } = systemRoleGroup;
    const column=[
		{
            title: '角色组名称',
            dataIndex: 'name',
            key: 'name',
            width:80,
            nowrap:true,
            render:(text,record,index)=>{
                return (
                    functionCodesMap['CF_MANAGE_SYSTEMRG_VIEW']?
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
						{functionCodesMap['CF_MANAGE_SYSTEMRG_VIEW']?<a onClick={()=>{
                                                    showViewWindow(record);
                                                }}>查看</a>:''}
						{functionCodesMap['CF_MANAGE_SYSTEMRG_UPDATE']?<span className="ant-divider" />:''}
						{functionCodesMap['CF_MANAGE_SYSTEMRG_UPDATE']?<a onClick={()=>{
                                                    showEditWindow(record);
                                                }}>修改</a>:''}
                        {functionCodesMap['CF_MANAGE_SYSTEMRG_DEL']?<span className="ant-divider" />:''}
                        <VtxPopconfirm title="确认删除此记录吗？" onConfirm={()=>{
                            dispatch({type:'systemRoleGroup/deleteRoleGroup',payload:{id:record.id}});
                        }}>
                            {functionCodesMap['CF_MANAGE_SYSTEMRG_DEL']?<a>删除</a>:''}
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
                dispatch({type:'systemRoleGroup/clearNewItem'});
            }}>清空</Button>,
            <Button key="submit" type="primary" size="large" loading={saving} onClick={()=>{
                dispatch({type:'systemRoleGroup/updateNewItem',payload:{checkState: true}});
                dispatch({type:'systemRoleGroup/addRoleGroup'});
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
                dispatch({type:'systemRoleGroup/updateNewItem',payload:{
                    ...obj
                }})
            },
            validateRoleGroup(obj){
                dispatch({type:"systemRoleGroup/validateRoleGroup",payload:{obj:obj}});
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
                dispatch({type:'systemRoleGroup/updateEditItem',payload:{checkState:true}});
                dispatch({type:'systemRoleGroup/updateRoleGroup'});
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
                dispatch({type:'systemRoleGroup/updateEditItem',payload:{
                    ...obj
                }})
            },
            validateRoleGroup(obj){
                dispatch({type:"systemRoleGroup/validateRoleGroup",payload:{obj:obj}});
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
                dispatch({type:'systemRoleGroup/updateState',payload:{
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
            	dispatch({type:'systemRoleGroup/updateState',payload:{
                    currentPageIndex:page,
                    pageSize
                }})
                dispatch({type:'systemRoleGroup/getTableData'})
            },
            // pageSize 变化的回调
            onShowSizeChange(current, size){
            	dispatch({type:'systemRoleGroup/updateState',payload:{
                    currentPageIndex:current,
                    pageSize:size
                }})
                dispatch({type:'systemRoleGroup/getTableData'})
            },
            showTotal: total => `合计 ${totalItems} 条`
        }/*,
        onChange: (pagination, filters, sorter)=>{
            console.log(sorter)
        }*/
    }
    function showNewWindow(){
        if(searchParentId=='-1'&&systemId=='-1'){
            message.error("请选择非根节点操作");
        }else {
            dispatch({type:'systemRoleGroup/clearNewItem'});
            dispatch({type:'systemRoleGroup/updateNewItem',payload:{
                parentId: searchParentId,
                systemId
            }})
        }
    }
    function hideNewWindow(){
        dispatch({type:'systemRoleGroup/updateNewItem',payload:{
            visible:false
        }})
    }
    function showEditWindow(data){
        dispatch({type:'systemRoleGroup/updateEditItem',payload:{
            ...data,
            visible:true
        }})
    }
    function hideEditWindow(){
        dispatch({type:'systemRoleGroup/updateEditItem',payload:{
            visible:false
        }})
    }
    function showViewWindow(data){
        dispatch({type:'systemRoleGroup/updateViewItem',payload:{
            ...data,
            visible:true
        }})
    }
    function hideViewWindow(){
        dispatch({type:'systemRoleGroup/updateViewItem',payload:{
            visible:false
        }})
    }
    const systemRoleGroupTreeProps = {
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
                    dispatch({type:'systemRoleGroup/deletesRoleGroup'});
                }
            });
        }
    }
	return (
		<div className={styles.main}>
            <div className={styles.leftTree}>
                <div>
                    <RoleGroupTree {...systemRoleGroupTreeProps}/>
                </div>
            </div>
            <div className={styles.rigthCont}>
                <div className={styles.bt_bar}>
                    {functionCodesMap['CF_MANAGE_SYSTEMRG_ADD']?<Button icon="file-add" onClick={showNewWindow}>新增</Button>:''}
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
export default connect(({systemRoleGroup})=>({systemRoleGroup}))(SystemRoleGroup);
