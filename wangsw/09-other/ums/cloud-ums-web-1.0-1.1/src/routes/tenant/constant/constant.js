import React from 'react';
import { connect } from 'dva';
import styles from './constant.less';
import {Table,Input,Select,Button,Modal,Pagination } from 'antd';
import VtxDataGrid from '../../../components/vtxCommon/VortexDatagrid/VortexDatagrid';
import VtxGrid from '../../../components/vtxCommon/VtxGrid/VtxGrid';
import EditItem from '../../../components/tenant/constant/editItem';
import ViewItem from '../../../components/tenant/constant/viewItem';

function Constant({dispatch,constant}) {
	const {tableData,tableLoading,pageSize,currentPageIndex,newItem,editItem,viewItem,
        selectedRowKeys,saving,totalItems,constantName,codeUnique,functionCodesMap} = constant;
    const column=[
		{
			title: '常量名',
	        dataIndex: 'constantCode',
	        key: 'constantCode',
	        width:80,
	        nowrap:true,
			render:(text,record,index)=>{
				return (
                    functionCodesMap['CF_MANAGE_TENANT_CONS_VIEW']?
					<a href="javascript:void(0);" style={{'textDecoration':'underline','color':'rgba(0,0,0,0.65)'}} onClick={
						()=>{
							showViewWindow(record);
						}
					}>  {text}  </a>: <span>{text}</span>
					)
			}
		},{
			title:'常量值',
			dataIndex:'constantValue',
			key:'constantValue',
			width: 80,
	        nowrap:true
		},{
			title:'描述',
			dataIndex:'constantDescription',
			key:'constantDescription',
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
                        {functionCodesMap['CF_MANAGE_TENANT_CONS_VIEW']?<a onClick={()=>{
                                                    showViewWindow(record);    
                                                }}>查看</a>:""}
						{functionCodesMap['CF_MANAGE_TENANT_CONS_UPDATE']?<span className="ant-divider" />:""}
						{functionCodesMap['CF_MANAGE_TENANT_CONS_UPDATE']?<a onClick={()=>{
                                                    showEditWindow(record);
                                                }}>修改</a>:''}
					</span>
				)
			}
		}
	];

	//新增页面参数
    const newItemProps = {
        modalProps:{
            title:'常量管理 > 新增常量',
            visible: newItem.visible,
            onCancel:hideNewWindow,
            width:600,
            footer:[
            <Button key="cancel" size="large" onClick={()=>{
                dispatch({type:'constant/clearNewItem'});
            }}>清空</Button>,
            <Button key="submit" type="primary" size="large" loading={saving} onClick={()=>{
                dispatch({type:'constant/updateNewItem',payload:{checkState: true}});
                dispatch({type:'constant/addConstant'});
            }}>
              保存
            </Button>,
            ],
        },
        contentProps:{
            ...newItem,
            updateItem(obj){
                dispatch({type:'constant/updateNewItem',payload:{
                    ...obj
                }})
            },
            validateConstant(obj){
                dispatch({type:"constant/validateConstant",payload:{obj:obj}});
            },
            codeUnique
        }
    }
    const editItemProps = {
        modalProps:{
            title:'常量管理 > 编辑常量',
            visible: editItem.visible,
            onCancel:hideEditWindow,
            width:600,
            footer:[
            <Button key="cancel" size="large" onClick={()=>{
                hideEditWindow();
            }}>取消</Button>,
            <Button key="submit" type="primary" size="large" loading={false} onClick={()=>{
                dispatch({type:'constant/updateEditItem',payload:{checkState:true}});
                dispatch({type:'constant/updateConstant'});
            }}>
              保存
            </Button>,
            ],
        },
        contentProps:{
            ...editItem,
            updateItem(obj){
                dispatch({type:'constant/updateEditItem',payload:{
                    ...obj
                }})
            },
            validateConstant(obj){
                dispatch({type:"constant/validateConstant",payload:{obj:obj}});
            },
            codeUnique
        }
    }

    const viewItemProps = {
        modalProps:{
            title:'常量管理 > 查看常量',
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
        tableLoading,
        indexColumn:true, //用了这个属性column里的width不能用百分比
        startIndex:(currentPageIndex-1)*pageSize+1,
        autoFit:true,
        rowSelection:{
            type:'checkbox',
            selectedRowKeys,
            onChange(selectedRowKeys){
                dispatch({type:'constant/updateState',payload:{
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
            	dispatch({type:'constant/updateState',payload:{
                    currentPageIndex:page,
                    pageSize
                }})
                dispatch({type:'constant/getTableData'})
            },
            // pageSize 变化的回调
            onShowSizeChange(current, size){
            	dispatch({type:'constant/updateState',payload:{
                    currentPageIndex:current,
                    pageSize:size
                }})
                dispatch({type:'constant/getTableData'})
            },
            showTotal: total => `合计 ${totalItems} 条`
        }
    }

    const queryConstantNameChanged = (e)=>{
    	dispatch({type:'constant/updateState',payload:{constantName:e.target.value}});
    }
    const querySelect = ()=>{
        dispatch({type:'constant/initTableOpt'});
    	dispatch({type:'constant/getTableData'});
    }
    const clearQuery = ()=>{
    	dispatch({type:'constant/updateState',payload:{
    		constantName:''
        }});
        dispatch({type:'constant/initTableOpt'});
    	dispatch({type:'constant/getTableData'});
    }
    function showNewWindow(){
        dispatch({type:'constant/clearNewItem'});
    }
    function hideNewWindow(){
        dispatch({type:'constant/updateNewItem',payload:{
            visible:false
        }})
    }
    function showEditWindow(data){
        dispatch({type:'constant/updateEditItem',payload:{
            ...data,
            visible:true
        }});
    }
    function hideEditWindow(){
        dispatch({type:'constant/updateEditItem',payload:{
            visible:false
        }})
    }
    function showViewWindow(data){
        dispatch({type:'constant/updateViewItem',payload:{
            ...data,
            visible:true
        }})
    }
    function hideViewWindow(){
        dispatch({type:'constant/updateViewItem',payload:{
            visible:false
        }})
    }
	return (
		<div className={styles.main}>
			<VtxGrid 
            titles={['变量名']}
            gridweight={[1]}
            confirm={querySelect}
            clear={clearQuery}
            >
                <Input value={constantName} onChange={queryConstantNameChanged} placeholder='输入关键字'/>
            </VtxGrid>
            <div className={styles.bt_bar}>
               { functionCodesMap['CF_MANAGE_TENANT_CONS_ADD']?<Button icon="file-add" className='primary' onClick={showNewWindow}>新增</Button>:""}
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
export default connect(({constant})=>({constant}))(Constant);
