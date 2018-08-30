import React from 'react';
import { connect } from 'dva';
import styles from './paramGroup.less';
import {Table,Input,Select,Button,Modal,Pagination } from 'antd';
import VtxDataGrid from '../../components/vtxCommon/VortexDatagrid/VortexDatagrid';
import VtxGrid from '../../components/vtxCommon/VtxGrid/VtxGrid';
import EditItem from '../../components/paramGroup/editItem';
import ViewItem from '../../components/paramGroup/viewItem';
import ParamGroupTree from '../../components/paramGroup/paramGroupTree';
import VtxPopconfirm from '../../components/vtxCommon/vtxPopconfirm';

function ParamGroup({dispatch,paramGroup}) {
	const {tableData,tableLoading,pageSize,currentPageIndex,newItem,editItem,viewItem,functionCodesMap,
		selectedRowKeys,saving,totalItems,treeData,groupCode,groupName,codeUnique,searchParentId} = paramGroup;
    const column=[
		{
            title: '参数组名称',
            dataIndex: 'groupName',
            key: 'groupName',
            width:80,
            nowrap:true,
            render:(text,record,index)=>{
                return (
                    //functionCodesMap['']?
                    <a href="javascript:void(0);" style={{'textDecoration':'underline','color':'rgba(0,0,0,0.65)'}} onClick={
                        ()=>{
                            showViewWindow(record);
                        }
                    }>  {text}  </a>
                    )
            }
        },{
			title: '编码',
	        dataIndex: 'groupCode',
	        key: 'groupCode',
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
						<a onClick={()=>{
							showViewWindow(record);
						}}>查看</a>
						{functionCodesMap['CF_MANAGE_PARAM_G_UPDATE']?<span className="ant-divider" />:''}
						{functionCodesMap['CF_MANAGE_PARAM_G_UPDATE']?<a onClick={()=>{
                                                    showEditWindow(record);
                                                }}>修改</a>:''}
                       {functionCodesMap['CF_MANAGE_PARAM_G_DEL']?<span className="ant-divider" />:''}
						<VtxPopconfirm title="确认删除此记录吗？" onConfirm={()=>{
                            dispatch({type:'paramGroup/deleteParamGroup',payload:{id:record.id}});
                        }}>
                            {functionCodesMap['CF_MANAGE_PARAM_G_DEL']?<a>删除</a>:''}
                        </VtxPopconfirm>
					</span>
				)
			}
		}
	];
	//新增页面参数
    const newItemProps = {
        modalProps:{
            title:'参数组管理 > 新增参数组',
            visible: newItem.visible,
            onCancel:hideNewWindow,
            width:600,
            footer:[
            <Button key="cancel" size="large" onClick={()=>{
                dispatch({type:'paramGroup/clearNewItem'});
            }}>清空</Button>,
            <Button key="submit" type="primary" size="large" loading={saving} onClick={()=>{
                dispatch({type:'paramGroup/updateNewItem',payload:{checkState: true}});
                dispatch({type:'paramGroup/addParamGroup'});
            }}>
              保存
            </Button>,
            ],
        },
        contentProps:{
            ...newItem,
            codeUnique,
            updateItem(obj){
                dispatch({type:'paramGroup/updateNewItem',payload:{
                    ...obj
                }})
            },
            validateCode(obj){
                dispatch({type:"paramGroup/validateParamGroup",payload:{obj}});
            }
        }
    }
    const editItemProps = {
        modalProps:{
            title:'参数组管理 > 编辑参数组',
            visible: editItem.visible,
            onCancel:hideEditWindow,
            width:1000,
            footer:[
            <Button key="cancel" size="large" onClick={()=>{
                hideEditWindow();
            }}>取消</Button>,
            <Button key="submit" type="primary" size="large" loading={false} onClick={()=>{
                dispatch({type:'paramGroup/updateEditItem',payload:{checkState:true}});
                dispatch({type:'paramGroup/updateParamGroup'});
            }}>
              保存
            </Button>,
            ],
        },
        contentProps:{
            ...editItem,
            codeUnique,
            updateItem(obj){
                dispatch({type:'paramGroup/updateEditItem',payload:{
                    ...obj
                }})
            },
            validateCode(obj){
                dispatch({type:"paramGroup/validateParamGroup",payload:{obj}});
            }
        }
    }

    const viewItemProps = {
        modalProps:{
            title:'参数组管理 > 查看参数组',
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
                dispatch({type:'paramGroup/updateState',payload:{
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
            	dispatch({type:'paramGroup/updateState',payload:{
                    currentPageIndex:page,
                    pageSize
                }})
                dispatch({type:'paramGroup/getTableData'})
            },
            // pageSize 变化的回调
            onShowSizeChange(current, size){
            	dispatch({type:'paramGroup/updateState',payload:{
                    currentPageIndex:current,
                    pageSize:size
                }})
                dispatch({type:'paramGroup/getTableData'})
            },
            showTotal: total => `合计 ${totalItems} 条`
        }
    }
    function showNewWindow(){
        dispatch({type:'paramGroup/updateNewItem',payload:{parentId:searchParentId}});
        dispatch({type:'paramGroup/clearNewItem'});
    }
    function hideNewWindow(){
        dispatch({type:'paramGroup/updateNewItem',payload:{
            visible:false
        }})
    }
    function showEditWindow(data){
        dispatch({type:'paramGroup/updateEditItem',payload:{
            ...data,
            visible:true
        }})
    }
    function hideEditWindow(){
        dispatch({type:'paramGroup/updateEditItem',payload:{
            visible:false
        }})
    }
    function showViewWindow(data){
        dispatch({type:'paramGroup/updateViewItem',payload:{
            ...data,
            visible:true
        }})
    }
    function hideViewWindow(){
        dispatch({type:'paramGroup/updateViewItem',payload:{
            visible:false
        }})
    }
    const paramGroupTreeProps = {
        paramGroupTree:treeData,
        dispatch
    }
    const querySelect = ()=>{
        dispatch({type:'paramGroup/initTableOpt'});
        dispatch({type:'paramGroup/getTableData'});
    }
    const clearQuery = ()=>{
        dispatch({type:'paramGroup/updateState',payload:{
            groupCode:'',
            groupName:''
        }});
        dispatch({type:'paramGroup/initTableOpt'});
        dispatch({type:'paramGroup/getTableData'});
    }
	return (
		<div className={styles.main}>
            <div className={styles.leftTree}>
                <div>
                    <ParamGroupTree {...paramGroupTreeProps}/>
                </div>
            </div>
            <div className={styles.rigthCont}>
                <VtxGrid 
                titles={['编码','名称']}
                gridweight={[1,1]}
                confirm={querySelect}
                clear={clearQuery}
                >
                    <Input value={groupCode} placeholder={"请输入编码"} onChange={(e)=>{
                        dispatch({type:'paramGroup/updateState',payload:{groupCode:e.target.value}});
                    }}/>
                    <Input value={groupName} placeholder={"请输入名称"} onChange={(e)=>{
                        dispatch({type:'paramGroup/updateState',payload:{groupName:e.target.value}});
                    }}/>
                </VtxGrid>
                <div className={styles.bt_bar}>
                    {functionCodesMap['CF_MANAGE_PARAM_G_ADD']?<Button icon="file-add" onClick={showNewWindow}>新增</Button>:''}
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
export default connect(({paramGroup})=>({paramGroup}))(ParamGroup);
