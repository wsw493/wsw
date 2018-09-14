import React from 'react';
import { connect } from 'dva';
import styles from './param.less';
import style from '../../../index.less';
import VtxDataGrid from '../../../components/vtxCommon/VortexDatagrid/VortexDatagrid';
import VtxGrid from '../../../components/vtxCommon/VtxGrid/VtxGrid';
import {Button, Input, Table, Modal, Popconfirm, message,Tree,Pagination} from 'antd';
import ParamGroupTree from '../../../components/tenant/param/paramGroupTree';
import Paramvaluelist from '../../../components/tenant/param/paramvalue/paramvalueList';
import ViewItem from '../../../components/tenant/param/viewItem';
function TenantParam({dispatch,tenantparam}){
	const {treeData,tableData,currentPageIndex,totalItems,pageSize,selectedRowKeys,tableLoading,paramValueWindow,searchTreeId,
		valueItems,addWindow,newItems,editItem,viewItem,editWindow,viewWindow,valueNewItems,searchCode,searchName,codeValidatFlag,
        scrollHeight,saving,functionCodesMap}=tenantparam;
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
                functionCodesMap['CF_MANAGE_TENANT_PARAM_TYPE_VIEW']?
				<a style={{'textDecoration':'underline','color':'rgba(0,0,0,0.60)'}} onClick={()=>{
						dispatch({type:'tenantparam/updateNewItem',payload:{id: record.id}});
						dispatch({type:'tenantparam/getDtoById'});
						dispatch({type:'tenantparam/updateState',payload:{viewWindow:true}});
					}}>{text}</a>: <span>{text}</span>
				)
		}
		
	},{
		title:'操作',
		dataIndex:'action',
		width:80,
		render:(text,record,index)=>{
			return (
				<span>
					{functionCodesMap['CF_MANAGE_TENANT_PARAM_TYPE_VIEW']?<a onClick={()=>{
                                            showViewWindow(record);
                                        }}>查看</a>:''}
					{functionCodesMap['CF_MANAGE_TENANT_PARAM_LIST']?<span className="ant-divider" />:''}
					{functionCodesMap['CF_MANAGE_TENANT_PARAM_LIST']?<a onClick={()=>{
                                            dispatch({type:'tenantparam/updateValueItems',payload:{typeId:record.id}});
                                            dispatch({type:'tenantparam/getParamValueTableData'});
                                            dispatch({type:'tenantparam/updateState',payload:{paramValueWindow:true}});
                                        }}>参数维护</a>:''}
				</span>
				)
		}
	}
];

//查看页面参数
const viewItemProps = {
    modalProps:{
        title:'通用系统>查看参数类型',
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
        tableLoading,
        indexColumn:true, //用了这个属性column里的width不能用百分比
        startIndex:(currentPageIndex-1)*pageSize+1,
        autoFit:true,
        rowSelection:{
            type:'checkbox',
            selectedRowKeys,
            onChange(selectedRowKeys){
                dispatch({type:'tenantparam/updateState',payload:{
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
                console.log(page);
            	dispatch({type:'tenantparam/updateState',payload:{
                    currentPageIndex:page,
                    pageSize
                }})
                dispatch({type:'tenantparam/getParamTypeTableData'})
            },
            // pageSize 变化的回调
            onShowSizeChange(current, size){
            	dispatch({type:'tenantparam/updateState',payload:{
                    currentPageIndex:current,
                    pageSize:size
                }})
                dispatch({type:'tenantparam/getParamTypeTableData'})
            },
            showTotal: total => `合计 ${totalItems} 条`
        }
    }
    function showViewWindow(data){
        dispatch({type:'tenantparam/updateViewItem',payload:{
            ...data,
            visible:true
        }})
    }
    function hideViewWindow(){
        dispatch({type:'tenantparam/updateViewItem',payload:{
            visible:false
        }})
    }
    const paramTreeProps={
		dispatch,
		paramGroupTreeData:treeData
	}
	//查询列表
    const querySelect = ()=>{
        dispatch({type:'tenantparam/updateState',payload:{searchTreeId:'',currentPage:0,pageSize:10}});
        dispatch({type:'tenantparam/getParamTypeTableData'});
    }
    //清空查询
    const clearQuery = ()=>{
        dispatch({type:'tenantparam/clear'});
        dispatch({type:'tenantparam/getParamTypeTableData'});
    }
    const pramValueProps={
        dispatch,
        ...valueItems,
        valueNewItems,
        functionCodesMap
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
                className={styles.queryBox}
                >
                    <Input style={{ width: '100%' }} placeholder={"请输入编码"} value={searchCode} onChange={(e)=>{
                        dispatch({type:'tenantparam/updateState',payload:{searchCode:e.target.value}});
                    }}/>
                    <Input style={{ width: '100%' }} placeholder={"请输入名称"} value={searchName} onChange={(e)=>{
                        dispatch({type:'tenantparam/updateState',payload:{searchName:e.target.value}});
                    }}/>
                </VtxGrid>
                {/*<div className={styles.bt_bar}>
                    <Button icon="file-add" className='primary' onClick={showNewWindow}>新增</Button>
                    <Button icon="delete" className='delete' onClick={deleteRows}>删除</Button>
                </div>*/}
        		<div className={styles.table}>
                    <VtxDataGrid {...gridProps}/>
                </div>
            </div>	
            <ViewItem {...viewItemProps} />
            <Modal title='参数值列表' width={760} maskClosable={false} visible={paramValueWindow} onCancel={
                ()=>{
                    dispatch({type:'tenantparam/updateState',payload:{paramValueWindow:false}});
                    dispatch({type:'tenantparam/initValueTableArgs'});
                }
            } footer={null} width={1000}>
                <Paramvaluelist {...pramValueProps}/>
            </Modal>
    	</div>
    	)
}	
export default connect(({tenantparam})=>({tenantparam}))(TenantParam);
