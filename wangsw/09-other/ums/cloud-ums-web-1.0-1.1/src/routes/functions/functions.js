import React from 'react';
import { connect } from 'dva';
import styles from './functions.less';
import {Table,Input,Select,Button,Modal,Pagination,message } from 'antd';
import VtxDataGrid from '../../components/vtxCommon/VortexDatagrid/VortexDatagrid';
import VtxGrid from '../../components/vtxCommon/VtxGrid/VtxGrid';
import EditItem from '../../components/functions/editItem';
import ViewItem from '../../components/functions/viewItem';
import FunctionGroupTree from '../../components/functions/functionGroupTree';
import VtxPopconfirm from '../../components/vtxCommon/vtxPopconfirm';

function Functions({dispatch,functions}) {
	const {tableData,tableLoading,pageSize,currentPageIndex,newItem,editItem,viewItem,
        selectedRowKeys,saving,totalItems,treeData,searchParentId,nameUnique,codeUnique,
        systemList,functionTree,functionName,functionCodesMap} = functions;
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
                    functionCodesMap['CF_MANAGE_FUN_VIEW']?
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
		}
	];
	//新增页面参数
    const newItemProps = {
        modalProps:{
            title:'功能管理 > 新增功能',
            visible: newItem.visible,
            onCancel:hideNewWindow,
            width:1000,
            footer:[
            <Button key="cancel" size="large" onClick={()=>{
                dispatch({type:'functions/clearNewItem'});
            }}>清空</Button>,
            <Button key="submit" type="primary" size="large" loading={saving} onClick={()=>{
                dispatch({type:'functions/updateNewItem',payload:{checkState: true}});
                dispatch({type:'functions/addFunctions'});
            }}>
              保存
            </Button>,
            ],
        },
        contentProps:{
            ...newItem,
            nameUnique,
            codeUnique,
            systemList,
            functionTree,
            updateItem(obj){
                dispatch({type:'functions/updateNewItem',payload:{
                    ...obj
                }})
            },
            validateFunctions(obj){
                dispatch({type:"functions/validateFunctions",payload:{obj:obj}});
            }
        }
    }
    const editItemProps = {
        modalProps:{
            title:'功能管理 > 编辑功能',
            visible: editItem.visible,
            onCancel:hideEditWindow,
            width:1000,
            footer:[
            <Button key="cancel" size="large" onClick={()=>{
                hideEditWindow();
            }}>取消</Button>,
            <Button key="submit" type="primary" size="large" loading={false} onClick={()=>{
                dispatch({type:'functions/updateEditItem',payload:{checkState:true}});
                dispatch({type:'functions/updateFunctions'});
            }}>
              保存
            </Button>,
            ],
        },
        contentProps:{
            ...editItem,
            nameUnique,
            codeUnique,
            systemList,
            functionTree,
            updateItem(obj){
                dispatch({type:'functions/updateEditItem',payload:{
                    ...obj
                }})
            },
            validateFunctions(obj){
                dispatch({type:"functions/validateFunctions",payload:{obj:obj}});
            }
        }
    }

    const viewItemProps = {
        modalProps:{
            title:'功能管理 > 查看功能',
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
                dispatch({type:'functions/updateState',payload:{
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
            	dispatch({type:'functions/updateState',payload:{
                    currentPageIndex:page,
                    pageSize
                }})
                dispatch({type:'functions/getTableData'})
            },
            // pageSize 变化的回调
            onShowSizeChange(current, size){
            	dispatch({type:'functions/updateState',payload:{
                    currentPageIndex:current,
                    pageSize:size
                }})
                dispatch({type:'functions/getTableData'})
            },
            showTotal: total => `合计 ${totalItems} 条`
        }
    }
    function showNewWindow(){
        if(searchParentId == '-1'){
            message.error("不能在根节点上新增");
        }else {
            dispatch({type:'functions/getSystemList'});
            dispatch({type:'functions/getFunctionTree'});
            dispatch({type:'functions/clearNewItem'});
            dispatch({type:'functions/updateNewItem',payload:{
                groupId: searchParentId
            }});
        }
    }
    function hideNewWindow(){
        dispatch({type:'functions/updateNewItem',payload:{
            visible:false
        }})
    }
    function showEditWindow(){
        if(selectedRowKeys.length == 0){
            message.error("请选择你要修改的数据")
        }else if(selectedRowKeys.length > 1){
            message.error("每次只能修改一条数据")
        }else{
            dispatch({type:'functions/getSystemList'});
            dispatch({type:'functions/getFunctionTree'});
            dispatch({type:'functions/getDtoById',payload:{id:selectedRowKeys[0]}});
            dispatch({type:'functions/updateEditItem',payload:{
                visible:true
            }});
        }
    }
    function hideEditWindow(){
        dispatch({type:'functions/updateEditItem',payload:{
            visible:false
        }})
    }
    function showViewWindow(id){
        if(typeof(id)=="string"){
            dispatch({type:'functions/getDtoById',payload:{id}});
            dispatch({type:'functions/updateViewItem',payload:{
                visible:true
            }});
        }else {
            if(selectedRowKeys.length == 0){
                message.error("请选择你要查看的数据")
            }else if(selectedRowKeys.length > 1){
                message.error("每次只能查看一条数据")
            }else{
                dispatch({type:'functions/getDtoById',payload:{id:selectedRowKeys[0]}});
                dispatch({type:'functions/updateViewItem',payload:{
                    visible:true
                }});
            }
        }
    }
    function hideViewWindow(){
        dispatch({type:'functions/updateViewItem',payload:{
            visible:false
        }})
    }
    const functionGroupTreeProps = {
        functionGroupTree:treeData,
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
                    dispatch({type:'functions/deletesFunctions'});
                }
            });
        }
    }
    const querySelect = ()=>{
        dispatch({type:'functions/initTableOpt'});
        dispatch({type:'functions/getTableData'})
    }
    const clearQuery = ()=>{
        dispatch({type:'functions/initTableOpt'});
        dispatch({type:'functions/updateState',payload:{functionName:''}});
        dispatch({type:'functions/getTableData'})
    }
	return (
		<div className={styles.main}>
            <div className={styles.leftTree}>
                <div>
                    <FunctionGroupTree {...functionGroupTreeProps}/>
                </div>
            </div>
            <div className={styles.rigthCont}>
                <VtxGrid 
                titles={['名称']}
                gridweight={[1]}
                confirm={querySelect}
                clear={clearQuery}
                >
                    <Input value={functionName} onChange={(e)=>{
                        dispatch({type:'functions/updateState',payload:{functionName:e.target.value}});
                    }} placeholder='输入名称'/>
                </VtxGrid>
                <div className={styles.bt_bar}>
                   { functionCodesMap['CF_MANAGE_FUN_ADD']?<Button icon="file-add" className='primary' onClick={showNewWindow}>新增</Button>:""}
                    {functionCodesMap['CF_MANAGE_FUN_DEL']?<Button icon="delete" className='delete' onClick={bulkDelete}>删除</Button>:''}
                    {functionCodesMap['CF_MANAGE_FUN_UPDATE']?<Button icon="form" className="primary" onClick={showEditWindow}>修改</Button>:''}
                    {functionCodesMap['CF_MANAGE_FUN_VIEW']?<Button icon="file-text" className="view" onClick={showViewWindow}>查看</Button>:''}
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
export default connect(({functions})=>({functions}))(Functions);
