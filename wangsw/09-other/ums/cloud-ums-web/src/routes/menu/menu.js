import React from 'react';
import { connect } from 'dva';
import styles from './menu.less';
import {Table,Input,Select,Button,Modal,Pagination,message,Popconfirm } from 'antd';
import VtxDataGrid from '../../components/vtxCommon/VortexDatagrid/VortexDatagrid';
import VtxGrid from '../../components/vtxCommon/VtxGrid/VtxGrid';
import EditItem from '../../components/menu/editItem';
import ViewItem from '../../components/menu/viewItem';
import MenuTree from '../../components/menu/menuTree';
import VtxPopconfirm from '../../components/vtxCommon/vtxPopconfirm';

function Menu({dispatch,menu}) {
	const {tableData,tableLoading,pageSize,currentPageIndex,newItem,editItem,viewItem,
        selectedRowKeys,saving,totalItems,treeData,searchParentId,nameUnique,codeUnique,
        menuCode,menuName,canAddNewMenu,funTreeData,fileListVersion,functionCodesMap} = menu;
    const column=[
		{
			title: '编码',
	        dataIndex: 'code',
	        key: 'code',
	        width:80,
	        nowrap:true
		},{
            title: '名称',
            dataIndex: 'name',
            key: 'name',
            width:80,
            nowrap:true,
            render:(text,record,index)=>{
                return (
                    functionCodesMap['CF_MANAGE_MENU_VIEW']?
                    <a href="javascript:void(0);" style={{'textDecoration':'underline','color':'rgba(0,0,0,0.65)'}} onClick={
                        ()=>{
                            showViewWindow(record.id);
                        }
                    }>  {text}  </a>: <span>{text}</span>
                    )
            }
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
						{functionCodesMap['CF_MANAGE_MENU_VIEW']?<a onClick={()=>{
                                                    showViewWindow(record.id);
                                                }}>查看</a>:''}
						{functionCodesMap['CF_MANAGE_MENU_UPDATE']?<span className="ant-divider" />:''}
						{functionCodesMap['CF_MANAGE_MENU_UPDATE']?<a onClick={()=>{
                                                    showEditWindow(record.id);
                                                }}>修改</a>:''}
                        {functionCodesMap['CF_MANAGE_MENU_DEL']?<span className="ant-divider" />:''}
						{functionCodesMap['CF_MANAGE_MENU_DEL']?<Popconfirm title="确定删除此条数据吗?" onConfirm={()=>{
                            bulkDelete(record.id);
                         }}  okText="是" cancelText="否">
                             <a>删除</a>
                        </Popconfirm>:''}
					</span>
				)
			}
		}
	];
	//新增页面参数
    const newItemProps = {
        modalProps:{
            title:'菜单管理 > 新增菜单',
            visible: newItem.visible,
            onCancel:hideNewWindow,
            width:1000,
            footer:[
            <Button key="cancel" size="large" onClick={()=>{
                dispatch({type:'menu/clearNewItem'});
                dispatch({type:'menu/updateState',payload:{
                    fileListVersion: fileListVersion+1
                }})
            }}>清空</Button>,
            <Button key="submit" type="primary" size="large" loading={saving} onClick={()=>{
                dispatch({type:'menu/updateNewItem',payload:{checkState: true}});
                dispatch({type:'menu/addMenu'});
            }}>
              保存
            </Button>,
            ],
        },
        contentProps:{
            ...newItem,
            nameUnique,
            codeUnique,
            funTreeData,
            menuTreeData:treeData,
            fileListVersion,
            updateItem(obj){
                dispatch({type:'menu/updateNewItem',payload:{
                    ...obj
                }})
            },
            validateMenu(obj){
                dispatch({type:"menu/validateMenu",payload:{obj:obj}});
            }
        }
    }
    const editItemProps = {
        modalProps:{
            title:'菜单管理 > 编辑菜单',
            visible: editItem.visible,
            onCancel:hideEditWindow,
            width:1000,
            footer:[
            <Button key="cancel" size="large" onClick={()=>{
                hideEditWindow();
            }}>取消</Button>,
            <Button key="submit" type="primary" size="large" loading={false} onClick={()=>{
                dispatch({type:'menu/updateEditItem',payload:{checkState:true}});
                dispatch({type:'menu/updateMenu'});
            }}>
              保存
            </Button>,
            ],
        },
        contentProps:{
            ...editItem,
            nameUnique,
            codeUnique,
            funTreeData,
            menuTreeData:treeData,
            fileListVersion,
            updateItem(obj){
                dispatch({type:'menu/updateEditItem',payload:{
                    ...obj
                }})
            },
            validateMenu(obj){
                dispatch({type:"menu/validateMenu",payload:{obj:obj}});
            }
        }
    }

    const viewItemProps = {
        modalProps:{
            title:'菜单管理 > 查看菜单',
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
            fileListVersion
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
                dispatch({type:'menu/updateState',payload:{
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
            	dispatch({type:'menu/updateState',payload:{
                    currentPageIndex:page,
                    pageSize
                }})
                dispatch({type:'menu/getTableData'})
            },
            // pageSize 变化的回调
            onShowSizeChange(current, size){
            	dispatch({type:'menu/updateState',payload:{
                    currentPageIndex:current,
                    pageSize:size
                }})
                dispatch({type:'menu/getTableData'})
            },
            showTotal: total => `合计 ${totalItems} 条`
        }/*,
        onChange: (pagination, filters, sorter)=>{
            console.log(sorter)
        }*/
    }
    function showNewWindow(){
        if(canAddNewMenu){
            dispatch({type:'menu/getFunctionTree'});
            dispatch({type:'menu/clearNewItem'});
            dispatch({type:'menu/updateNewItem',payload:{
                parentId: searchParentId
            }})
            dispatch({type:'menu/updateState',payload:{
                fileListVersion: fileListVersion+1
            }})
        }else {
            message.error('菜单已经绑定功能，不允许增加子菜单！');
        }
    }
    function hideNewWindow(){
        dispatch({type:'menu/updateNewItem',payload:{
            visible:false
        }})
    }
    function showEditWindow(id){
        dispatch({type:'menu/getFunctionTree'});
        dispatch({type:'menu/getDtoById',payload:{id}});
        dispatch({type:'menu/updateEditItem',payload:{
            visible:true
        }});
        
    }
    function hideEditWindow(){
        dispatch({type:'menu/updateEditItem',payload:{
            visible:false
        }})
    }
    function showViewWindow(id){
        dispatch({type:'menu/getDtoById',payload:{id}});
        dispatch({type:'menu/updateViewItem',payload:{
            visible:true
        }})
    }
    function hideViewWindow(){
        dispatch({type:'menu/updateViewItem',payload:{
            visible:false
        }})
    }
    const menuTreeProps = {
        menuTree:treeData,
        dispatch
    }
    function bulkDelete(id){
        if(id&&typeof id == 'string'){
            dispatch({type:'menu/deletesMenu',payload:{id}});
        }else if(selectedRowKeys.length==0){
            message.warning('当前没有选中的行可以删除！');
        }
        else{
            Modal.confirm({
                content: `确定删除选中的${selectedRowKeys.length}条数据吗？`,
                okText: '确定',
                cancelText: '取消',
                onOk(){
                    dispatch({type:'menu/deletesMenu'});
                }
            });
        }
    }
    const querySelect = ()=>{
        dispatch({type:'menu/initTableOpt'});
        dispatch({type:'menu/getTableData'})
    }
    const clearQuery = ()=>{
        dispatch({type:'menu/initTableOpt'});
        dispatch({type:'menu/updateState',payload:{menuCode:'',menuName:''}});
        dispatch({type:'menu/getTableData'})
    }
	return (
		<div className={styles.main}>
            <div className={styles.leftTree}>
                <div>
                    <MenuTree {...menuTreeProps}/>
                </div>
            </div>
            <div className={styles.rigthCont}>
                <VtxGrid 
                titles={['编码','名称']}
                gridweight={[1,1]}
                confirm={querySelect}
                clear={clearQuery}
                >
                    <Input value={menuCode} onChange={(e)=>{
                        dispatch({type:'menu/updateState',payload:{menuCode:e.target.value}});
                    }} placeholder='输入编码'/>
                    <Input value={menuName} onChange={(e)=>{
                        dispatch({type:'menu/updateState',payload:{menuName:e.target.value}});
                    }} placeholder='输入名称'/>
                </VtxGrid>
                <div className={styles.bt_bar}>
                    {functionCodesMap['CF_MANAGE_MENU_ADD']?<Button icon="file-add" onClick={showNewWindow}>新增</Button>:''}
                    {functionCodesMap['CF_MANAGE_MENU_DEL']?<Button icon="delete" onClick={bulkDelete}>删除</Button>:''}
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
export default connect(({menu})=>({menu}))(Menu);
