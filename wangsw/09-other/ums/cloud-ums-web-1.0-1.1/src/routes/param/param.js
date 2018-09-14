import React from 'react';
import { connect } from 'dva';
import styles from './param.less';
import style from '../../index.less';
import VtxDataGrid from '../../components/vtxCommon/VortexDatagrid/VortexDatagrid';
import VtxGrid from '../../components/vtxCommon/VtxGrid/VtxGrid';
import {Button, Input, Table, Modal, Popconfirm, message,Tree,Pagination} from 'antd';
import ParamGroupTree from '../../components/param/paramGroupTree';
import Paramvaluelist from '../../components/param/paramvalue/paramvalueList';
import EditItem from '../../components/param/editItem';
import ViewItem from '../../components/param/viewItem';
import VtxPopconfirm from '../../components/vtxCommon/vtxPopconfirm';
function Param({dispatch,param}){
	const {tableData,tableLoading,pageSize,currentPageIndex,newItem,editItem,viewItem,valueItems,valueNewItems,
    selectedRowKeys,saving,totalItems,treeData,searchName,searchCode,codeUnique,nameUnique,paramValueWindow,
    searchParentId,functionCodesMap}=param;

	const column=[{
		title:'编码',
		dataIndex:'typeCode',
		width:80
	},{
		title:'名称',
		dataIndex:'typeName',
		width:80,
		render:(text,record,index)=>{
			return (
                functionCodesMap['CF_MANAGE_PARAM_TYPE_VIEW']?
				<a style={{'textDecoration':'underline','color':'rgba(0,0,0,0.60)'}} onClick={()=>{
						showViewWindow(record);
					}}>{text}</a>: <sapn>{text}</sapn>
				)
		}
		
	},{
		title:'操作',
		dataIndex:'action',
		width:120,
		render:(text,record,index)=>{
			return (
				<span>
					{functionCodesMap['CF_MANAGE_PARAM_TYPE_VIEW']?<a onClick={()=>{
                                            showViewWindow(record);
                                        }}>查看</a>:''}
					{functionCodesMap['CF_MANAGE_PARAM_TYPE_UPDATE']?<span className="ant-divider" />:""}
					{functionCodesMap['CF_MANAGE_PARAM_TYPE_UPDATE']?<a onClick={()=>{
                                            showEditWindow(record);
                                        }}>修改</a>:''}
                    {functionCodesMap['CF_MANAGE_PARAM_TYPE_DEL']?<span className="ant-divider" />:''}
					<VtxPopconfirm title="确认删除此记录吗？" onConfirm={()=>{
                        dispatch({type:'param/deleteParamType',payload:{id:record.id}});
                    }}>
                        {functionCodesMap['CF_MANAGE_PARAM_TYPE_DEL']?<a>删除</a>:''}
                    </VtxPopconfirm>
					{functionCodesMap['CF_MANAGE_PARAM_LIST']?<span className="ant-divider" />:''}
					{functionCodesMap['CF_MANAGE_PARAM_LIST']?<a onClick={()=>{
                                            dispatch({type:'param/updateValueItems',payload:{typeId:record.id}});
                                            dispatch({type:'param/getParamValueTableData'});
                                            dispatch({type:'param/updateState',payload:{paramValueWindow:true}});
                                        }}>参数维护</a>:''}
				</span>
				)
		}
	}]
    //新增页面参数
    const newItemProps = {
        modalProps:{
            title:'参数管理>新增参数类型',
            visible: newItem.visible,
            onCancel:hideNewWindow,
            width:600,
            footer:[
            <Button key="cancel" size="large" onClick={()=>{
                dispatch({type:'param/clearNewItem'});
            }}>清空</Button>,
            <Button key="submit" type="primary" size="large" loading={saving} onClick={()=>{
                dispatch({type:'param/updateNewItem',payload:{checkState: true}});
                dispatch({type:'param/addParamType'});
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
                dispatch({type:'param/updateNewItem',payload:{
                    ...obj
                }})
            },
            validateParamType(obj){
                dispatch({type:"param/validateParamType",payload:{obj}});
            }
        }
    }
    //编辑页面参数
    const editItemProps = {
        modalProps:{
            title:'参数管理>修改参数类型',
            visible: editItem.visible,
            onCancel:hideEditWindow,
            width:600,
            footer:[
            <Button key="cancel" size="large" onClick={()=>{
                hideEditWindow();
            }}>取消</Button>,
            <Button key="submit" type="primary" size="large" loading={false} onClick={()=>{
                dispatch({type:'param/updateEditItem',payload:{checkState:true}});
                dispatch({type:'param/updateParamType'});
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
                dispatch({type:'param/updateEditItem',payload:{
                    ...obj
                }})
            },
            validateParamType(obj){
                dispatch({type:"param/validateParamType",payload:{obj}});
            }
        }
    }
    //查看页面参数
    const viewItemProps = {
        modalProps:{
            title:'参数管理>查看参数类型',
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
            ...viewItem
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
                dispatch({type:'param/updateState',payload:{
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
            	dispatch({type:'param/updateState',payload:{
                    currentPageIndex:page,
                    pageSize
                }})
                dispatch({type:'param/getParamTypeTableData'})
            },
            // pageSize 变化的回调
            onShowSizeChange(current, size){
            	dispatch({type:'param/updateState',payload:{
                    currentPageIndex:current,
                    pageSize:size
                }})
                dispatch({type:'param/getParamTypeTableData'})
            },
            showTotal: total => `合计 ${totalItems} 条`
        }
    }
	//新增
    function showNewWindow(){
        dispatch({type:'param/updateState',payload:{unique:false}});
        if(searchParentId=='' || searchParentId=='-1'){
            message.error("请选择参数组新增");
        }else {
            dispatch({type:'param/updateNewItem',payload:{groupId:searchParentId}});
            dispatch({type:'param/clearNewItem'});
        }
    }
    function hideNewWindow(){
        dispatch({type:'param/updateNewItem',payload:{
            visible:false
        }})
    }
    function showEditWindow(data){
        dispatch({type:'param/updateEditItem',payload:{
            ...data,
            visible:true
        }})
    }
    function hideEditWindow(){
        dispatch({type:'param/updateEditItem',payload:{
            visible:false
        }})
    }
    function showViewWindow(data){
        dispatch({type:'param/updateViewItem',payload:{
            ...data,
            visible:true
        }})
    }
    function hideViewWindow(){
        dispatch({type:'param/updateViewItem',payload:{
            visible:false
        }})
    }
    const paramTreeProps={
		dispatch,
		paramGroupTreeData:treeData
	}
	//查询列表
    const querySelect = ()=>{
        dispatch({type:'param/initTableArgs'});
        dispatch({type:'param/getParamTypeTableData'});
    }
    //清空查询
    const clearQuery = ()=>{
        dispatch({type:'param/updateState',payload:{searchCode:'',searchName:''}});
        dispatch({type:'param/getParamTypeTableData'});
    }
    //删除
    function deleteRows(){
        if(selectedRowKeys.length==0){
            message.warning('当前没有选中的行可以删除！');
        }
        else{
            Modal.confirm({
                content: `确定删除选中的${selectedRowKeys.length}条数据吗？`,
                okText: '确定',
                cancelText: '取消',
                onOk(){
                    dispatch({type:'param/deletesParamType',payload:{
                        ids:selectedRowKeys
                    }});
                }
            });
        }
    }
    const pramValueProps={
		dispatch,
		...valueItems,
		valueNewItems,
        functionCodesMap
       /* updateValueNewItems(obj){
            console.log(obj);
            dispatch({type:'param/updateValueNewItems',payload:{...obj}});
        },*/
	}
    return(
    	<div className={styles.main}>
    		<div className={styles.leftTree}>
                <div>
                    <ParamGroupTree {...paramTreeProps}/>
                </div>
            </div>
            <div className={styles.rigthCont}>
            	<VtxGrid 
                titles={['编码','名称']}
                gridweight={[1,1]}
                confirm={querySelect}
                clear={clearQuery}
                >
                    <Input value={searchCode} placeholder={"请输入编码"} onChange={(e)=>{
                        dispatch({type:'param/updateState',payload:{searchCode:e.target.value}});
                    }}/>
                    <Input value={searchName} placeholder={"请输入名称"} onChange={(e)=>{
                        dispatch({type:'param/updateState',payload:{searchName:e.target.value}});
                    }}/>
                </VtxGrid>
                <div className={styles.bt_bar}>
                    {functionCodesMap['CF_MANAGE_PARAM_TYPE_ADD']?<Button icon="file-add" className='primary' onClick={showNewWindow}>新增</Button>:""}
                </div>
        		<div className={styles.table}>
                    <VtxDataGrid {...gridProps}/>
                </div>
            </div>	
            <EditItem  {...newItemProps} />
            <EditItem  {...editItemProps} />
            <ViewItem {...viewItemProps} />
            <Modal title='参数值列表' width={760} maskClosable={false} visible={paramValueWindow} onCancel={
				()=>{
					dispatch({type:'param/updateState',payload:{paramValueWindow:false}});
					dispatch({type:'param/initValueTableArgs'});
				}
			} footer={null} width={1000}>
				<Paramvaluelist {...pramValueProps}/>
			</Modal>
    	</div>
    )
}	
export default connect(({param})=>({param}))(Param);
