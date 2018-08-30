import React from 'react';
import { connect } from 'dva';
import styles from './org.less';
import VtxDataGrid from '../../components/vtxCommon/VortexDatagrid/VortexDatagrid';
import VtxGrid from '../../components/vtxCommon/VtxGrid/VtxGrid';
import {Button,Input} from 'antd';
import {VtxMap} from 'vtx-ui';
import {VtxSearchMap} from 'vtx-ui';
import EditItem from '../../components/org/editItem';
import ViewItem from '../../components/org/viewItem';
import OrgTree from '../../components/org/orgTree';
import VtxPopconfirm from '../../components/vtxCommon/vtxPopconfirm';
function Org({dispatch,org}){
	const {tableData,tableLoading,newItem,editItem,viewItem,currentPageIndex,selectedRowKeys,saving,totalItems,
        pageSize,searchName,modal1Visible,mapCenter,codeUnique,treeData,searchParentId,divisionTree,
        searchParentName,orgName,functionCodesMap}=org;
	const column=[
		{
			title: '编码',
	        dataIndex: 'orgCode',
	        key: 'orgCode',
	        width:80,
	        nowrap:true
		},{
            title: '名称',
            dataIndex: 'orgName',
            key: 'orgName',
            width:80,
            nowrap:true,
            render:(text,record,index)=>{
                return (
                    functionCodesMap['CF_MANAGE_TENANT_ORG_VIEW']?
                    <a href="javascript:void(0);" style={{'textDecoration':'underline','color':'rgba(0,0,0,0.65)'}} onClick={
                        ()=>{
                            showViewWindow(record.id);
                        }
                    }>  {text}  </a>: <span>{text}</span>
                    )
            }
        },{
			title:'负责人',
			dataIndex:'head',
			key:'head',
			width: 80,
	        nowrap:true
		},{
			title:'负责人手机号',
			dataIndex:'headMobile',
			key:'headMobile',
			width: 80,
	        nowrap:true
		},{
			title:'地址',
			dataIndex:'address',
			key:'address',
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
						{functionCodesMap['CF_MANAGE_TENANT_ORG_VIEW']?<a onClick={()=>{
                                                    showViewWindow(record.id);
                                                }}>查看</a>:''}
						{functionCodesMap['CF_MANAGE_TENANT_ORG_UPDATE']?<span className="ant-divider" />:''}
						{functionCodesMap['CF_MANAGE_TENANT_ORG_UPDATE']?<a onClick={()=>{
                                                    showEditWindow(record.id);
                                                }}>修改</a>:""}
                        {functionCodesMap['CF_MANAGE_TENANT_ORG_DELETE']?<span className="ant-divider" />:''}
						<VtxPopconfirm title="确认删除此记录吗？" onConfirm={()=>{
                            dispatch({type:'org/deleteOrg',payload:{id:record.id}});
                        }}>
                           { functionCodesMap['CF_MANAGE_TENANT_ORG_DELETE']?<a>删除</a>:''}
                        </VtxPopconfirm>
					</span>
				)
			}
		}
	];
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
                dispatch({type:'org/updateState',payload:{
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
            	dispatch({type:'org/updateState',payload:{
                    currentPageIndex:page,
                    pageSize
                }})
                dispatch({type:'org/getTableData'})
            },
            // pageSize 变化的回调
            onShowSizeChange(current, size){
            	dispatch({type:'org/updateState',payload:{
                    currentPageIndex:current,
                    pageSize:size
                }})
                dispatch({type:'org/getTableData'})
            },
            showTotal: total => `合计 ${totalItems} 条`
        }
    }
    //新增页面参数
    const newItemProps = {
        modalProps:{
            title:'单位列表 > 新增单位',
            visible: newItem.visible,
            onCancel:hideNewWindow,
            width:1000,
            footer:[
            <Button key="cancel" size="large" onClick={()=>{
                dispatch({type:'org/clearNewItem'});
            }}>清空</Button>,
            <Button key="submit" type="primary" size="large" loading={saving} onClick={()=>{
                dispatch({type:'org/updateNewItem',payload:{checkState: true}});
                dispatch({type:'org/saveOrg'});
            }}>
              保存
            </Button>,
            ],
        },
        contentProps:{
            ...newItem,
            codeUnique,
            divisionTree,
            updateItem(obj){
                dispatch({type:'org/updateNewItem',payload:{
                    ...obj
                }})
            },
            validateCode(obj){
                dispatch({type:"org/validateCode",payload:{obj}});
            },
            updateState:(obj)=>{
                dispatch({type:'org/updateState',payload:{
                    ...obj
                }});
            }
        }
    }
    const editItemProps = {
        modalProps:{
            title:'单位列表 > 修改单位',
            visible: editItem.visible,
            onCancel:hideEditWindow,
            width:1000,
            footer:[
            <Button key="cancel" size="large" onClick={()=>{
                hideEditWindow();
            }}>取消</Button>,
            <Button key="submit" type="primary" size="large" loading={false} onClick={()=>{
                dispatch({type:'org/updateEditItem',payload:{checkState:true}});
                dispatch({type:'org/updateOrg'});
            }}>
              保存
            </Button>,
            ],
        },
        contentProps:{
            ...editItem,
            codeUnique,
            divisionTree,
            updateItem(obj){
                dispatch({type:'org/updateEditItem',payload:{
                    ...obj
                }})
            },
            validateCode(obj){
                dispatch({type:"org/validateCode",payload:{obj}});
            },
            updateState:(obj)=>{
                dispatch({type:'org/updateState',payload:{
                    ...obj
                }});
            }
        }
    }

    const viewItemProps = {
        modalProps:{
            title:'单位列表 > 查看单位',
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
    function showNewWindow(){
        dispatch({type:'org/getDivisionTreeData'});
        dispatch({type:'org/updateNewItem',payload:{
            parentId: searchParentId,
            parentName: searchParentName!=''?searchParentName:treeData[0].name
        }});
        dispatch({type:'org/clearNewItem'});
    }
    function hideNewWindow(){
        dispatch({type:'org/updateNewItem',payload:{
            visible:false
        }});
    }
    function showEditWindow(id){
        dispatch({type:'org/getDivisionTreeData'});
        dispatch({type:'org/getDtoById',payload:{id}});
        dispatch({type:'org/updateEditItem',payload:{
            visible:true
        }})
    }
    function hideEditWindow(){
        dispatch({type:'org/updateEditItem',payload:{
            visible:false
        }})
    }
    function showViewWindow(id){
        dispatch({type:'org/getDtoById',payload:{id}});
        dispatch({type:'org/updateViewItem',payload:{
            visible:true
        }});
    }
    function hideViewWindow(){
        dispatch({type:'org/updateViewItem',payload:{
            visible:false
        }})
    }
    //查询列表
    const querySelect = ()=>{
        dispatch({type:'org/initTableOpt'});
        dispatch({type:'org/getTableData'});
    }
    //清空查询
    const clearQuery = ()=>{
        dispatch({type:'org/updateState',payload:{orgName:''}});
        dispatch({type:'org/initTableOpt'});
        dispatch({type:'org/getTableData'});
    }
    //地图
    const mapCallback = (lt,lglt)=>{
        dispatch({type:'org/updateState',payload:{modal1Visible:false}});
        dispatch({type:'org/convertToAddress',payload:{
            location: lt.join(',')
        }});
    }
    const closeMapModal = ()=>{
        dispatch({type:'org/updateState',payload:{modal1Visible:false}});
    }
    const orgTreeProps = {
        orgTree:treeData,
        dispatch
    }
	return (
		<div className={styles.main}>
            <div className={styles.leftTree}>
                <div>
                    <OrgTree {...orgTreeProps}/>
                </div>
            </div>
			<div className={styles.rigthCont}>
				<VtxGrid 
	                titles={['名称']}
	                gridweight={[1]}
	                confirm={querySelect}
	                clear={clearQuery}
	                >
	                <Input style={{ width: '100%' }} placeholder={"请输入名称"} value={orgName} onChange={(e)=>{
	                    dispatch({type:'org/updateState',payload:{orgName:e.target.value}});
	                }}/>
	            </VtxGrid>
	            <div className={styles.bt_bar}>
	                {functionCodesMap['CF_MANAGE_TENANT_ORG_ADD']?<Button icon="file-add" onClick={showNewWindow}>新增</Button>:''}
	            </div>
	    		<div className={styles.table}>
	                <VtxDataGrid {...gridProps}/>
	            </div>
            </div>
            <EditItem  {...newItemProps} />
            <EditItem  {...editItemProps} />
            <ViewItem {...viewItemProps} />
            <VtxSearchMap
                callback={mapCallback}
                mapCenter={mapCenter}
                modal1Visible={modal1Visible}
                closeModal={closeMapModal}
            />
		</div>

		)
}
export default connect(({org})=>({org}))(Org);
