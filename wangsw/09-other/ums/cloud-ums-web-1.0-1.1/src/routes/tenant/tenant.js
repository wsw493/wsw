import React from 'react';
import { connect } from 'dva';
import styles from './tenant.less';
import {Table,Input,Select,Button,Modal,Pagination } from 'antd';
import VtxDataGrid from '../../components/vtxCommon/VortexDatagrid/VortexDatagrid';
import VtxGrid from '../../components/vtxCommon/VtxGrid/VtxGrid';
import EditItem from '../../components/tenant/editItem';
import ViewItem from '../../components/tenant/viewItem';
import Map from '../../components/vtxCommon/Map/Map';
import SearchMap from '../../components/vtxCommon/VtxSearchMap';
import CloudSystem from '../../components/tenant/cloudSystemSetting';
import VtxPopconfirm from '../../components/vtxCommon/vtxPopconfirm';

const Option=Select.Option;
function Tenant({dispatch,tenant}) {
	const {tableData,tableLoading,pageSize,currentPageIndex,newItem,editItem,viewItem,tenantName,enabled,
        selectedRowKeys,mapCenter,modal1Visible,saving,totalItems,windowMapInfo,cloudData,treeData,
        codeUnique,nameUnique,domainUnique,passwordFlag,sort,functionCodesMap} = tenant;
    /*console.log(mapCenter);
    console.log('mapCentermapCenter');*/
    const column=[
		{
			title: '编码',
	        dataIndex: 'tenantCode',
	        key: 'tenantCode',
	        width:80,
	        nowrap:true
		},{
			title: '名称',
	        dataIndex: 'tenantName',
	        key: 'tenantName',
	        width:80,
            nowrap:true,
            sorter: true,
			render:(text,record,index)=>{
				return (
					functionCodesMap['CF_MANAGE_TENANT_VIEW']?
                    <a href="javascript:void(0);" style={{'textDecoration':'underline','color':'rgba(0,0,0,0.65)'}} onClick={
						()=>{showViewWindow(record);}
					}>{text}</a>: <span>{text}</span>
					)
			}
		},{
			title:'联系人',
			dataIndex:'contact',
			key:'contact',
			width: 80,
	        nowrap:true
		},{
			title:'联系人Email',
			dataIndex:'email',
			key:'email',
			width: 80,
	        nowrap:true
		},{
			title:'联系电话',
			dataIndex:'phone',
			key:'phone',
			width: 80,
	        nowrap:true
		},{
			title:'是否启用',
			dataIndex:'enabled',
			key:'enabled',
			width: 80,
	        // nowrap:true,
	        render: (text,record,index)=>{
	        	return (
	        	<span>
	        	{
	        		text=="1"?
	        		[<span style={{color:'#0C0'}}>启用</span>]:
	        		[<span style={{color:'#D00'}}>禁用</span>]
	        	}
	        	</span>
	        	)
	        }
		},{
			title:'操作',
			dataIndex:'action',
			width: 120,
            // nowrap:true,
			render:(text,record,index)=>{
				return(
					<span>
                        <VtxPopconfirm title={(record.enabled == 1)?'确认禁用此租户吗？':'确认启用此租户吗？'} onConfirm={()=>{
                            dispatch({type:'tenant/processTenant',payload:{
                                id:record.id,
                                opType: (record.enabled == 1)?'disable':'enable'
                            }});
                        }}>
                            <a>{(record.enabled == 1)?(functionCodesMap['CF_MANAGE_TENANT_DISABLE']?'禁用':''):(functionCodesMap['CF_MANAGE_TENANT_ENABLE']?'启用':'')}</a>
                        </VtxPopconfirm>
						{functionCodesMap['CF_MANAGE_TENANT_ENABLE'] || functionCodesMap['CF_MANAGE_TENANT_DISABLE']?<span className="ant-divider" />:""}
                        {functionCodesMap['CF_MANAGE_TENANT_UPDATE']?(<a onClick={()=>{
                                showEditWindow(record);
                        }}>修改</a>) :''}
                        {functionCodesMap['CF_MANAGE_TENANT_UPDATE']?<span className="ant-divider" />:''}
						{functionCodesMap['CF_MANAGE_TENANT_CS_LIST']?
                        <a onClick={()=>{showCloudSystemWindow(record.id);}}>云系统</a>:''}
					</span>
				)
			}
		}
	];

    //云系统列表
    const cloud_column=[
        {
            title: '系统编码',
            dataIndex: 'code',
            key: 'code',
            width:80,
            nowrap:true
        },{
            title: '系统名称',
            dataIndex: 'name',
            key: 'name',
            width:80,
            nowrap:true
        },{
            title: '租户是否已启用系统',
            dataIndex: 'enabled',
            key: 'enabled',
            width:80,
            nowrap:true,
            render: (text,record,index)=>{
                return (
                   <span>
                    {
                        (text != '1')?
                        [<span style={{color:'#D00'}}>未启用</span>]:
                        [<span style={{color:'#0C0'}}>已启用</span>]
                    }
                    </span> 
                )
            }
        },{
            title:'操作',
            dataIndex:'action',
            width: 120,
            // nowrap:true,
            render:(text,record,index)=>{
                return(
                    <span>
                        <VtxPopconfirm title={(record.enabled && record.enabled=='1')?'确认禁用此系统吗？':'确认启用此系统吗？'} onConfirm={()=>{
                            dispatch({type:'tenant/processCloudSystem',payload:{
                                id:(record.enabled && record.enabled=='1') ? record.id : record.cloudSystemId,
                                opType: (record.enabled && record.enabled=='1')?'disable':'enable'
                            }});
                        }}>
                            <a>{(record.enabled && record.enabled=='1')?
                                (functionCodesMap['CF_MANAGE_TENANT_CS_DISABLE']?'禁用':''):
                                (functionCodesMap['CF_MANAGE_TENANT_CS_ENABLE']?'启用':'')
                            }</a>
                        </VtxPopconfirm>
                    </span>
                )
            }
        }
    ];
    const cloud_gridProps = {
        columns:cloud_column,
        dataSource:cloudData.tableData,
        loading:cloudData.tableLoading,
        indexColumn:true, //用了这个属性column里的width不能用百分比
        startIndex:(cloudData.currentPageIndex-1)*cloudData.pageSize+1,
        autoFit:true,
        rowSelection:{
            type:'checkbox',
            selectedRowKeys:cloudData.selectedRowKeys,
            onChange(selectedRowKeys){
                dispatch({type:'tenant/updateCloudData',payload:{
                    selectedRowKeys:selectedRowKeys
                }});
            }
        },
        pagination:{
            showSizeChanger: true,
            pageSizeOptions: ['10', '20', '30', '40','50'],
            showQuickJumper: true,
            current:cloudData.currentPageIndex,  //后端分页数据配置参数1
            total:cloudData.totalItems, //后端分页数据配置参数2
            pageSize:cloudData.pageSize, //后端分页数据配置参数3
            // 当前页码改变的回调
            onChange(page, pageSize){
                dispatch({type:'tenant/updateCloudData',payload:{
                    currentPageIndex:page,
                    pageSize
                }})
                dispatch({type:'tenant/getCloudSystemTableData'})
            },
            // pageSize 变化的回调
            onShowSizeChange(current, size){
                dispatch({type:'tenant/updateCloudData',payload:{
                    currentPageIndex:current,
                    pageSize:size
                }})
                dispatch({type:'tenant/getCloudSystemTableData'})
            },
            showTotal: total => `合计 ${cloudData.totalItems} 条`
        }
    }
    //云系统界面
    const cloudSystemProps = {
        modalProps: {
            title: '租户 > 配置云系统',
            visible: cloudData.visible,
            width: 1000,
            footer: null,
            onCancel:hideCloudSystemWindow
        },
        contentProps: {
            gridProps:cloud_gridProps,
            querySelect: ()=>{
                dispatch({type:'tenant/getCloudSystemTableData'});
            },
            clearQuery: ()=>{
                dispatch({type:'tenant/updateCloudData',payload:{
                    systemName: ''
                }});
                dispatch({type:'tenant/getCloudSystemTableData'});
            },
            querySystemNameChanged: (e)=>{
                dispatch({type:'tenant/updateCloudData',payload:{
                    systemName: e.target.value
                }});
            },
            systemName: cloudData.systemName
        }
    }
	//新增页面参数
    const newItemProps = {
        modalProps:{
            title:'租户管理 > 新增租户',
            visible: newItem.visible,
            onCancel:hideNewWindow,
            width:1000,
            footer:[
            <Button key="cancel" size="large" onClick={()=>{
                dispatch({type:'tenant/clearNewItem'});
                dispatch({type:'tenant/updateWindowMapInfo',payload:{
                    showMap: false,
                    mapPoints: [],
                    centerPoint: []
                }});
            }}>清空</Button>,
            <Button key="submit" type="primary" size="large" loading={saving} onClick={()=>{
                dispatch({type:'tenant/updateNewItem',payload:{checkState: true}});
                dispatch({type:'tenant/addTenant'});
            }}>
              保存
            </Button>,
            ],
        },
        contentProps:{
            ...newItem,
            ...windowMapInfo,
            updateItem(obj){
                dispatch({type:'tenant/updateNewItem',payload:{
                    ...obj
                }})
            },
            isNewItem: true,
            updateState:(obj)=>{
                dispatch({type:'tenant/updateState',payload:{
                    ...obj
                }});
            },
            codeUnique,
            nameUnique,
            domainUnique,
            passwordFlag,
            validateTenant(obj){
                dispatch({type:"tenant/validateTenant",payload:{obj:obj}});
            },
            validateAccount(obj){
                dispatch({type:"tenant/validateAccount",payload:{obj:obj}});
            },
            divisionTree:treeData,
            divisionTreeLoadBack({key,treeNode,isExpand,resolve}){
                dispatch({type:"tenant/getDivisionTreeData",payload:{key,resolve}});
            }
        }
    }
    const editItemProps = {
        modalProps:{
            title:'租户管理 > 编辑租户',
            visible: editItem.visible,
            onCancel:hideEditWindow,
            width:1000,
            footer:[
            <Button key="cancel" size="large" onClick={()=>{
                hideEditWindow();
            }}>取消</Button>,
            <Button key="submit" type="primary" size="large" loading={false} onClick={()=>{
                dispatch({type:'tenant/updateEditItem',payload:{checkState:true}});
                dispatch({type:'tenant/updateTenant'});
            }}>
              保存
            </Button>,
            ],
        },
        contentProps:{
            ...editItem,
            ...windowMapInfo,
            isNewItem: false,
            updateItem(obj){
                dispatch({type:'tenant/updateEditItem',payload:{
                    ...obj
                }})
            },
            updateState:(obj)=>{
                dispatch({type:'tenant/updateState',payload:{
                    ...obj
                }});
            },
            codeUnique,
            domainUnique,
            validateTenant(obj){
                dispatch({type:"tenant/validateTenant",payload:{obj:obj}});
            },
            divisionTree:treeData
        }
    }

    const viewItemProps = {
        modalProps:{
            title:'租户管理 > 查看租户',
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
            ...windowMapInfo,
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
                dispatch({type:'tenant/updateState',payload:{
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
            	dispatch({type:'tenant/updateState',payload:{
                    currentPageIndex:page,
                    pageSize
                }})
                dispatch({type:'tenant/getTableData'})
            },
            // pageSize 变化的回调
            onShowSizeChange(current, size){
            	dispatch({type:'tenant/updateState',payload:{
                    currentPageIndex:current,
                    pageSize:size
                }})
                dispatch({type:'tenant/getTableData'})
            },
            showTotal: total => `合计 ${totalItems} 条`
        },
        onChange:(pagination, filters, sorter)=>{
            dispatch({type:'tenant/updateState',payload:{
                sort: sorter.field?sorter.field:sort,
                order: sorter.order=="descend"?'desc':'asc'
            }});
            dispatch({type:'tenant/getTableData'});
        }
    }

    const mapCallback = (lglt)=>{
        dispatch({type:'tenant/updateState',payload:{modal1Visible:false}});
    	dispatch({'type':'tenant/updateWindowMapInfo',payload:{
            showMap: true,
            mapPoints: [{id:'pointtt',longitude:lglt[0],latitude:lglt[1]}],
            centerPoint: lglt
        }});
        dispatch({type:'tenant/updateNewItem',payload:{
            longitude:lglt[0],
            latitude:lglt[1],
            longitudeDone:lglt[0],
            latitudeDone:lglt[1]
        }});
        dispatch({type:'tenant/updateEditItem',payload:{
            longitude:lglt[0],
            latitude:lglt[1],
            longitudeDone:lglt[0],
            latitudeDone:lglt[1]
        }});
    }

    const queryTenantNameChanged = (e)=>{
    	dispatch({type:'tenant/updateState',payload:{tenantName:e.target.value}});
    }
    const queryEnabledChanged = (value, option)=>{
    	dispatch({type:'tenant/updateState',payload:{enabled:value}});
    }
    const querySelect = ()=>{
        dispatch({type:'tenant/initTableOpt'});
    	dispatch({type:'tenant/getTableData'});
    }
    const clearQuery = ()=>{
    	dispatch({type:'tenant/updateState',payload:{
    		tenantName:'',
    		enabled:''
        }});
        dispatch({type:'tenant/initTableOpt'});
    	dispatch({type:'tenant/getTableData'});
    }
    function showNewWindow(){
        dispatch({type:'tenant/getDivisionTreeData',payload:{key:'-1'}});
        dispatch({type:'tenant/clearNewItem'});
        dispatch({type:'tenant/initWindowMapInfo'});
    }
    function hideNewWindow(){
        dispatch({type:'tenant/updateState',payload:{codeUnique: false,nameUnique: false}});
        dispatch({type:'tenant/updateNewItem',payload:{
            checkState: false,
            visible:false
        }})
    }
    function showEditWindow(data){
        dispatch({type:'tenant/getDtoById',payload:{
            id: data.id
        }});
        dispatch({type:'tenant/updateEditItem',payload:{
            visible:true
        }});
        if(data.latitude && data.longitude){
            setTimeout(()=>{
                dispatch({'type':'tenant/updateWindowMapInfo',payload:{
                    showMap: true,
                    mapPoints: [{id:'point',longitude:data.longitude,latitude:data.latitude}],
                    centerPoint: [data.longitude, data.latitude]
                }});},100);
        }else {
            dispatch({'type':'tenant/initWindowMapInfo'});
        }
    }
    function hideEditWindow(){
        dispatch({type:'tenant/updateEditItem',payload:{
            visible:false
        }})
    }
    function showViewWindow(data){
        dispatch({type:'tenant/getDtoById',payload:{
            id: data.id
        }});
        dispatch({type:'tenant/updateViewItem',payload:{
            visible:true
        }})
        if(data.latitude && data.longitude){
            setTimeout(()=>{
                dispatch({'type':'tenant/updateWindowMapInfo',payload:{
                    showMap: true,
                    mapPoints: [{id:'point',longitude:data.longitude,latitude:data.latitude}],
                    centerPoint: [data.longitude, data.latitude]
                }});},100);
        }else {
            dispatch({'type':'tenant/initWindowMapInfo'});
        }
    }
    function hideViewWindow(){
        dispatch({type:'tenant/updateViewItem',payload:{
            visible:false
        }})
    }
    function showCloudSystemWindow(tenantId){
        dispatch({type:'tenant/getCloudSystemTableData',payload:{tenantId}});
        dispatch({type:'tenant/updateCloudData',payload:{
            tenantId,
            visible:true
        }});
    }
    function hideCloudSystemWindow(){
        dispatch({type:'tenant/updateCloudData',payload:{
            visible:false
        }})
    }
    const closeMapModal = ()=>{
        dispatch({type:'tenant/updateState',payload:{modal1Visible:false}});
    }
	return (
		<div className={styles.main}>
			<VtxGrid 
            titles={['租户名称','使用状态']}
            gridweight={[1,1]}
            confirm={querySelect}
            clear={clearQuery}
            >
                <Input value={tenantName} onChange={queryTenantNameChanged} placeholder='输入租户名称'/>
                <Select style={{ width: '100%' }} value={enabled} onChange={queryEnabledChanged}>
                    <Option value="">全部</Option>
                    <Option value="1">启用</Option>
                    <Option value="0">禁用</Option>
                </Select>
            </VtxGrid>
            <div className={styles.bt_bar}>
                {functionCodesMap['CF_MANAGE_TENANT_ADD'] ?<Button icon="file-add" className='primary' onClick={showNewWindow}>新增</Button>:''}
                {/* <Button icon="file-add" className='primary' onClick={()=>{
                    dispatch({type:'tenant/login'});
                    }}>登陆</Button> */}
            </div>
    		<div className={styles.table}>
                <VtxDataGrid {...gridProps}/>
            </div>
			<EditItem  {...newItemProps} />
            <EditItem  {...editItemProps} />
            <ViewItem {...viewItemProps} />
            <CloudSystem {...cloudSystemProps} />

            <SearchMap
            	callback={mapCallback}
                mapCenter={mapCenter}
                modal1Visible={modal1Visible}
                closeModal={closeMapModal}
            />
		</div>
	)
}
export default connect(({tenant})=>({tenant}))(Tenant);
