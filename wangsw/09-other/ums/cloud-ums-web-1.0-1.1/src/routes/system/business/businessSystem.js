import React from 'react';
import { connect } from 'dva';
import styles from './businessSystem.less';
import {Table,Input,Select,Button,Modal,Pagination } from 'antd';
import VtxDataGrid from '../../../components/vtxCommon/VortexDatagrid/VortexDatagrid';
import EditItem from '../../../components/system/editItem';
import ViewItem from '../../../components/system/viewItem';
import Map from '../../../components/vtxCommon/Map/Map';
import SearchMap from '../../../components/vtxCommon/VtxSearchMap';
import VtxGrid from '../../../components/vtxCommon/VtxGrid/VtxGrid';
function BusinessSystem({dispatch,businessSystem}) {
	const {tableData,tableLoading,pageSize,currentPageIndex,newItem,editItem,viewItem,businessSystemName,enabled,
        selectedRowKeys,mapCenter,modal1Visible,saving,totalItems,windowMapInfo,systemName,
        codeUnique,nameUnique,passwordFlag,functionCodesMap} = businessSystem;
    const column=[
		{
			title: '系统编码',
	        dataIndex: 'systemCode',
	        key: 'systemCode',
	        width:80,
	        nowrap:true
		},{
			title: '系统名称',
	        dataIndex: 'systemName',
	        key: 'systemName',
	        width:80,
	        nowrap:true,
			render:(text,record,index)=>{
				return (
					functionCodesMap['CF_MANAGE_TENANT_BS_VIEW']?
                    <a href="javascript:void(0);" style={{'textDecoration':'underline','color':'rgba(0,0,0,0.65)'}} onClick={
                        ()=>{
                            showViewWindow(record);
                        }
                    }>  {text}  </a>: <span>{text}</span>
					)
			}
		},{
			title:'website',
			dataIndex:'website',
			key:'website',
			width: 80,
	        nowrap:true
		},{
            title:'地图类型',
            dataIndex:'mapType',
            key:'mapType',
            width: 80,
            nowrap:true,
            /*render: (text,record,index)=>{
                var txt = '';
                switch(text){
                    case 'AMAP':
                        txt = 'aMap';
                    case 'BMAP':
                        txt = 'bMap';
                    case 'ARCGIS':
                        txt = 'arcgis';
                }
                return (<span>{txt}</span>)
            }*/
        },{
            title:'地图配置字符串',
            dataIndex:'mapStr',
            key:'mapStr',
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
						{functionCodesMap['CF_MANAGE_TENANT_BS_VIEW']?<a onClick={()=>{
							showViewWindow(record);
						}}>查看</a>:''}
						{functionCodesMap['CF_MANAGE_TENANT_BS_UPDATE']?<span className="ant-divider" />:''}
						{functionCodesMap['CF_MANAGE_TENANT_BS_UPDATE']?<a onClick={()=>{
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
            title:'业务系统管理 > 新增业务系统',
            visible: newItem.visible,
            onCancel:hideNewWindow,
            width:1000,
            footer:[
            <Button key="cancel" size="large" onClick={()=>{
                dispatch({type:'businessSystem/clearNewItem'});
                dispatch({type:'businessSystem/updateWindowMapInfo',payload:{
                    showMap: false,
                    mapPoints: [],
                    centerPoint: []
                }});
            }}>清空</Button>,
            <Button key="submit" type="primary" size="large" loading={saving} onClick={()=>{
                dispatch({type:'businessSystem/updateNewItem',payload:{checkState: true}});
                dispatch({type:'businessSystem/addBusinessSystem'});
            }}>
              保存
            </Button>,
            ],
        },
        contentProps:{
            ...newItem,
            ...windowMapInfo,
            updateItem(obj){
                dispatch({type:'businessSystem/updateNewItem',payload:{
                    ...obj
                }})
            },
            isNewItem: true,
            codeUnique,
            nameUnique,
            passwordFlag,
            validateCode(obj){
                dispatch({type:"businessSystem/validateCode",payload:{obj}});
            },
            validateAccount(obj){
                dispatch({type:"businessSystem/validateAccount",payload:{obj}});
            },
            updateState:(obj)=>{
                dispatch({type:'businessSystem/updateState',payload:{
                    ...obj
                }});
            }
        }
    }
    const editItemProps = {
        modalProps:{
            title:'业务系统管理 > 编辑业务系统',
            visible: editItem.visible,
            onCancel:hideEditWindow,
            width:1000,
            footer:[
            <Button key="cancel" size="large" onClick={()=>{
                hideEditWindow();
            }}>取消</Button>,
            <Button key="submit" type="primary" size="large" loading={false} onClick={()=>{
                dispatch({type:'businessSystem/updateEditItem',payload:{checkState:true}});
                dispatch({type:'businessSystem/updateBusinessSystem'});
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
                dispatch({type:'businessSystem/updateEditItem',payload:{
                    ...obj
                }})
            },
            codeUnique,
            nameUnique,
            passwordFlag,
            validateCode(obj){
                dispatch({type:"businessSystem/validateCode",payload:{obj}});
            },
            updateState:(obj)=>{
                dispatch({type:'businessSystem/updateState',payload:{
                    ...obj
                }});
            }
        }
    }

    const viewItemProps = {
        modalProps:{
            title:'业务系统管理 > 查看业务系统',
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
        tableLoading,
        indexColumn:true, //用了这个属性column里的width不能用百分比
        startIndex:(currentPageIndex-1)*pageSize+1,
        autoFit:true,
        rowSelection:{
            type:'checkbox',
            selectedRowKeys,
            onChange(selectedRowKeys){
                dispatch({type:'businessSystem/updateState',payload:{
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
            	dispatch({type:'businessSystem/updateState',payload:{
                    currentPageIndex:page,
                    pageSize
                }})
                dispatch({type:'businessSystem/getTableData'})
            },
            // pageSize 变化的回调
            onShowSizeChange(current, size){
            	dispatch({type:'businessSystem/updateState',payload:{
                    currentPageIndex:current,
                    pageSize:size
                }})
                dispatch({type:'businessSystem/getTableData'})
            },
            showTotal: total => `合计 ${totalItems} 条`
        }
    }

    const mapCallback = (lglt)=>{
        dispatch({type:'businessSystem/updateState',payload:{modal1Visible:false}});
    	dispatch({'type':'businessSystem/updateWindowMapInfo',payload:{
            showMap: true,
            mapPoints: [{id:'point',longitude:lglt[0],latitude:lglt[1]}],
            centerPoint: lglt
        }});
        dispatch({type:'businessSystem/updateNewItem',payload:{
            longitude:lglt[0],
            latitude:lglt[1],
            longitudeDone:lglt[0],
            latitudeDone:lglt[1]
        }});
        dispatch({type:'businessSystem/updateEditItem',payload:{
            longitude:lglt[0],
            latitude:lglt[1],
            longitudeDone:lglt[0],
            latitudeDone:lglt[1]
        }});
    }
    function showNewWindow(){
        dispatch({type:'businessSystem/clearNewItem'});
        dispatch({type:'businessSystem/initWindowMapInfo'});
    }
    function hideNewWindow(){
        dispatch({type:'businessSystem/updateNewItem',payload:{
            visible:false
        }})
    }
    function showEditWindow(data){
        dispatch({type:'businessSystem/updateEditItem',payload:{
            ...data,
            visible:true
        }})
        if(data.latitude && data.longitude){
            setTimeout(()=>{
                dispatch({'type':'businessSystem/updateWindowMapInfo',payload:{
                    showMap: true,
                    mapPoints: [{id:'point',longitude:data.longitude,latitude:data.latitude}],
                    centerPoint: [data.longitude, data.latitude]
                }});},100);
        }else {
            dispatch({'type':'businessSystem/initWindowMapInfo'});
        }
    }
    function hideEditWindow(){
        dispatch({type:'businessSystem/updateEditItem',payload:{
            visible:false
        }})
    }
    function showViewWindow(data){
        dispatch({type:'businessSystem/updateViewItem',payload:{
            ...data,
            visible:true
        }})
        if(data.latitude && data.longitude){
            setTimeout(()=>{
                dispatch({'type':'businessSystem/updateWindowMapInfo',payload:{
                    showMap: true,
                    mapPoints: [{id:'point',longitude:data.longitude,latitude:data.latitude}],
                    centerPoint: [data.longitude, data.latitude]
                }});},100);
        }else {
            dispatch({'type':'businessSystem/initWindowMapInfo'});
        }
    }
    function hideViewWindow(){
        dispatch({type:'businessSystem/updateViewItem',payload:{
            visible:false
        }})
    }
    const closeMapModal = ()=>{
        dispatch({type:'businessSystem/updateState',payload:{modal1Visible:false}});
    }
    const querySelect = ()=>{
        dispatch({type:'businessSystem/initTableOpt'});
    	dispatch({type:'businessSystem/getTableData'});
    }
    const clearQuery = ()=>{
    	dispatch({type:'businessSystem/updateState',payload:{
    		systemName:''
        }});
        dispatch({type:'businessSystem/initTableOpt'});
    	dispatch({type:'businessSystem/getTableData'});
    }
    const querySystemNameChanged = (e)=>{
    	dispatch({type:'businessSystem/updateState',payload:{systemName:e.target.value}});
    }
	return (
		<div className={styles.main}>
            <VtxGrid 
            titles={['系统名称']}
            gridweight={[1]}
            confirm={querySelect}
            clear={clearQuery}
            >
                <Input value={systemName} onChange={querySystemNameChanged} placeholder='输入系统名称'/>
            </VtxGrid>
            <div className={styles.bt_bar}>
                {functionCodesMap['CF_MANAGE_TENANT_BS_ADD']?<Button icon="file-add" className='primary' onClick={showNewWindow}>新增</Button>:""}
            </div>
    		<div className={styles.table}>
                <VtxDataGrid {...gridProps}/>
            </div>
			<EditItem  {...newItemProps} />
            <EditItem  {...editItemProps} />
            <ViewItem {...viewItemProps} />

            <SearchMap
            	callback={mapCallback}
                mapCenter={mapCenter}
                modal1Visible={modal1Visible}
                closeModal={closeMapModal}
            />
		</div>
	)
}
export default connect(({businessSystem})=>({businessSystem}))(BusinessSystem);
