import React from 'react';
import { connect } from 'dva';
import styles from './user.less';
import {Table,Input,Select,Button,Modal,Pagination } from 'antd';
import VtxDataGrid from '../../../components/vtxCommon/VortexDatagrid/VortexDatagrid';
import VtxGrid from '../../../components/vtxCommon/VtxGrid/VtxGrid';
import EditItem from '../../../components/tenant/user/editItem';
import ViewItem from '../../../components/tenant/user/viewItem';
import VtxPopconfirm from '../../../components/vtxCommon/vtxPopconfirm';

const Option=Select.Option;
function TenantUser({dispatch,tenantUser}) {
	const {tableData,tableLoading,pageSize,currentPageIndex,newItem,editItem,viewItem,userName,phone,
        selectedRowKeys,saving,totalItems,phoneUnique,accountUnique,passwordFlag,functionCodesMap,fileListVersion} = tenantUser;

    const column=[
		{
			title: '用户名',
	        dataIndex: 'userName',
	        key: 'userName',
	        width:80,
	        nowrap:true
		},{
			title: '手机号',
	        dataIndex: 'phone',
	        key: 'phone',
	        width:80,
            nowrap:true
		},{
			title:'性别',
			dataIndex:'gender',
			key:'gender',
			width: 80,
            nowrap:true,
            render:(text,record)=>{
                return (
                    <span>{text=='M'?'男':'女'}</span>
                )
            }
		},{
			title:'生日',
			dataIndex:'birthday',
			key:'birthday',
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
                        {functionCodesMap['CF_MANAGE_TENANT_VIEW']?(<a onClick={()=>{
                                showViewWindow(record);
                        }}>查看</a>) :''}
                        {functionCodesMap['CF_MANAGE_TENANT_VIEW']?<span className="ant-divider" />:''}
                        {functionCodesMap['CF_MANAGE_TENANT_UPDATE']?(<a onClick={()=>{
                                showEditWindow(record);
                        }}>修改</a>) :''}
                        {functionCodesMap['CF_MANAGE_TENANT_UPDATE']?<span className="ant-divider" />:''}
                        <VtxPopconfirm title={'确认重置该用户的密码吗？'} onConfirm={()=>{
                            dispatch({type:'tenantUser/resetPassword',payload:{
                                id:record.id
                            }});
                        }}>
                            <a>重置密码</a>
                        </VtxPopconfirm>
					</span>
				)
			}
		}
	];
	//新增页面参数
    const newItemProps = {
        modalProps:{
            title:'用户管理 > 新增用户',
            visible: newItem.visible,
            onCancel:hideNewWindow,
            width:1000,
            footer:[
            <Button key="cancel" size="large" onClick={()=>{
                dispatch({type:'tenantUser/clearNewItem'});
                dispatch({type:'tenantUser/updateWindowMapInfo',payload:{
                    showMap: false,
                    mapPoints: [],
                    centerPoint: []
                }});
            }}>清空</Button>,
            <Button key="submit" type="primary" size="large" loading={saving} onClick={()=>{
                dispatch({type:'tenantUser/updateNewItem',payload:{checkState: true}});
                dispatch({type:'tenantUser/addTenantUser'});
            }}>
              保存
            </Button>,
            ],
        },
        contentProps:{
            ...newItem,
            phoneUnique,
            accountUnique,
            passwordFlag,
            fileListVersion: fileListVersion,
            validateAccount(obj){
                dispatch({type:"tenantUser/validateAccount",payload:{obj}});
            },
            validatePhone(obj){
                dispatch({type:"tenantUser/validatePhone",payload:{obj}});
            },
            updateState(obj){
                dispatch({type:"tenantUser/updateState",payload:{...obj}});
            },
            updateItem(obj){
                dispatch({type:"tenantUser/updateNewItem",payload:{...obj}});
            }
        }
    }
    const editItemProps = {
        modalProps:{
            title:'用户管理 > 编辑用户',
            visible: editItem.visible,
            onCancel:hideEditWindow,
            width:1000,
            footer:[
            <Button key="cancel" size="large" onClick={()=>{
                hideEditWindow();
            }}>取消</Button>,
            <Button key="submit" type="primary" size="large" loading={false} onClick={()=>{
                dispatch({type:'tenantUser/updateEditItem',payload:{checkState:true}});
                dispatch({type:'tenantUser/updateTenantUser'});
            }}>
              保存
            </Button>,
            ],
        },
        contentProps:{
            ...editItem,
            phoneUnique,
            accountUnique,
            passwordFlag,
            fileListVersion: fileListVersion,
            validateAccount(obj){
                dispatch({type:"tenantUser/validateAccount",payload:{obj}});
            },
            validatePhone(obj){
                dispatch({type:"tenantUser/validatePhone",payload:{obj}});
            },
            updateState(obj){
                dispatch({type:"tenantUser/updateState",payload:{...obj}});
            },
            updateItem(obj){
                dispatch({type:"tenantUser/updateEditItem",payload:{...obj}});
            }
        }
    }

    const viewItemProps = {
        modalProps:{
            title:'用户管理 > 查看用户',
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
            fileListVersion: fileListVersion
        }
    }

    const gridProps = {
        columns:column,
        dataSource:tableData,
        loading:tableLoading,
        indexColumn:true, //用了这个属性column里的width不能用百分比
        startIndex:(currentPageIndex)*pageSize+1,
        autoFit:true,
        rowSelection:{
            type:'checkbox',
            selectedRowKeys,
            onChange(selectedRowKeys){
                dispatch({type:'tenantUser/updateState',payload:{
                    selectedRowKeys:selectedRowKeys
                }});
            }
        },
        pagination:{
            showSizeChanger: true,
            pageSizeOptions: ['10', '20', '30', '40','50'],
            showQuickJumper: true,
            current:currentPageIndex+1,  //后端分页数据配置参数1
            total:totalItems, //后端分页数据配置参数2
            pageSize, //后端分页数据配置参数3
            // 当前页码改变的回调
            onChange(page, pageSize){
            	dispatch({type:'tenantUser/updateState',payload:{
                    currentPageIndex:page+1,
                    pageSize
                }})
                dispatch({type:'tenantUser/getTableData'})
            },
            // pageSize 变化的回调
            onShowSizeChange(current, size){
            	dispatch({type:'tenantUser/updateState',payload:{
                    currentPageIndex:current+1,
                    pageSize:size
                }})
                dispatch({type:'tenantUser/getTableData'})
            },
            showTotal: total => `合计 ${totalItems} 条`
        }
    }

    const queryTenantUserNameChanged = (e)=>{
    	dispatch({type:'tenantUser/updateState',payload:{userName:e.target.value}});
    }
    const queryPhoneChanged = (e)=>{
    	dispatch({type:'tenantUser/updateState',payload:{phone:e.target.value}});
    }
    const queryEnabledChanged = (value, option)=>{
    	dispatch({type:'tenantUser/updateState',payload:{enabled:value}});
    }
    const querySelect = ()=>{
        dispatch({type:'tenantUser/initTableOpt'});
    	dispatch({type:'tenantUser/getTableData'});
    }
    const clearQuery = ()=>{
    	dispatch({type:'tenantUser/updateState',payload:{
    		userName:'',
    		phone:''
        }});
        dispatch({type:'tenantUser/initTableOpt'});
    	dispatch({type:'tenantUser/getTableData'});
    }
    function showNewWindow(){
        dispatch({type:'tenantUser/clearNewItem'});
        dispatch({type:'tenantUser/updateState',payload:{
            fileListVersion:fileListVersion+1
        }});
    }
    function hideNewWindow(){
        dispatch({type:'tenantUser/updateState',payload:{codeUnique: false,nameUnique: false}});
        dispatch({type:'tenantUser/updateNewItem',payload:{
            checkState: false,
            visible:false
        }})
    }
    function showEditWindow(data){
        dispatch({type:'tenantUser/getDtoById',payload:{
            id: data.id
        }});
        dispatch({type:'tenantUser/updateEditItem',payload:{
            visible:true
        }});
        dispatch({type:'tenantUser/updateState',payload:{
            fileListVersion:fileListVersion+1
        }});
    }
    function hideEditWindow(){
        dispatch({type:'tenantUser/updateEditItem',payload:{
            visible:false
        }})
    }
    function showViewWindow(data){
        dispatch({type:'tenantUser/getDtoById',payload:{
            id: data.id
        }});
        dispatch({type:'tenantUser/updateViewItem',payload:{
            visible:true
        }})
        dispatch({type:'tenantUser/updateState',payload:{
            fileListVersion:fileListVersion+1
        }});
    }
    function hideViewWindow(){
        dispatch({type:'tenantUser/updateViewItem',payload:{
            visible:false
        }})
    }
    function bulkDelete(){
        if(selectedRowKeys.length==0){
            message.warning('当前没有选中的行可以删除！');
        } else{
            Modal.confirm({
                content: `确定删除选中的${selectedRowKeys.length}条数据吗？`,
                okText: '确定',
                cancelText: '取消',
                onOk(){
                    dispatch({type:'tenantUser/deletesTenantUser'});
                }
            });
        }
    }
	return (
		<div className={styles.main}>
			<VtxGrid 
            titles={['用户名称','手机号']}
            gridweight={[1,1]}
            confirm={querySelect}
            clear={clearQuery}
            >
                <Input value={userName} onChange={queryTenantUserNameChanged} placeholder='输入用户名称'/>
                <Input value={phone} onChange={queryPhoneChanged} placeholder='输入手机号'/>
            </VtxGrid>
            <div className={styles.bt_bar}>
                {functionCodesMap['CF_MANAGE_TENANT_ADD'] ?<Button icon="file-add" onClick={showNewWindow}>新增</Button>:''}
                <Button icon="delete" onClick={bulkDelete}>删除</Button>
            </div>
    		<div className={styles.table}>
                <VtxDataGrid {...gridProps}/>
            </div>
			<EditItem  {...newItemProps} />
            <EditItem  {...editItemProps} />
            <ViewItem {...viewItemProps} />
		</div>
	)
}
export default connect(({tenantUser})=>({tenantUser}))(TenantUser);
