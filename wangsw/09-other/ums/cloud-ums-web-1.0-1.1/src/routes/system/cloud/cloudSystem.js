import React from 'react';
import { connect } from 'dva';
import styles from './cloudSystem.less';
import {Table,Input,Select,Button,Modal,Pagination } from 'antd';
import VtxDataGrid from '../../../components/vtxCommon/VortexDatagrid/VortexDatagrid';
import EditItem from '../../../components/system/editItem';
import ViewItem from '../../../components/system/viewItem';
import Map from '../../../components/vtxCommon/Map/Map';
import SearchMap from '../../../components/vtxCommon/VtxSearchMap';
function CloudSystem({dispatch,cloudSystem}) {
	const {tableData,tableLoading,pageSize,currentPageIndex,newItem,editItem,viewItem,cloudSystemName,enabled,
        selectedRowKeys,mapCenter,modal1Visible,saving,totalItems,windowMapInfo,systemName,
        codeUnique,nameUnique,passwordFlag,functionCodesMap} = cloudSystem;
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
                    functionCodesMap['CF_MANAGE_CS_VIEW']?
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
			title:'操作',
			dataIndex:'action',
			width: 120,
            // nowrap:true,
			render:(text,record,index)=>{
				return(
					<span>
						{functionCodesMap['CF_MANAGE_CS_VIEW']?<a onClick={()=>{
                                                    showViewWindow(record);
                                                }}>查看</a>:""}
						{functionCodesMap['CF_MANAGE_CS_UPDATE']?<span className="ant-divider" />:''}
						{functionCodesMap['CF_MANAGE_CS_UPDATE']?<a onClick={()=>{
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
            title:'云系统管理 > 新增云系统',
            visible: newItem.visible,
            onCancel:hideNewWindow,
            width:1000,
            footer:[
            <Button key="cancel" size="large" onClick={()=>{
                dispatch({type:'cloudSystem/clearNewItem'});
                dispatch({type:'cloudSystem/updateWindowMapInfo',payload:{
                    showMap: false,
                    mapPoints: [],
                    centerPoint: []
                }});
            }}>清空</Button>,
            <Button key="submit" type="primary" size="large" loading={saving} onClick={()=>{
                dispatch({type:'cloudSystem/updateNewItem',payload:{checkState: true}});
                dispatch({type:'cloudSystem/addCloudSystem'});
            }}>
              保存
            </Button>,
            ],
        },
        contentProps:{
            ...newItem,
            ...windowMapInfo,
            updateItem(obj){
                dispatch({type:'cloudSystem/updateNewItem',payload:{
                    ...obj
                }})
            },
            isNewItem: true,
            codeUnique,
            nameUnique,
            passwordFlag,
            validateCode(obj){
                dispatch({type:"cloudSystem/validateCode",payload:{obj}});
            },
            validateAccount(obj){
                dispatch({type:"cloudSystem/validateAccount",payload:{obj}});
            },
            updateState:(obj)=>{
                dispatch({type:'cloudSystem/updateState',payload:{
                    ...obj
                }});
            }
        }
    }
    const editItemProps = {
        modalProps:{
            title:'云系统管理 > 编辑云系统',
            visible: editItem.visible,
            onCancel:hideEditWindow,
            width:1000,
            footer:[
            <Button key="cancel" size="large" onClick={()=>{
                hideEditWindow();
            }}>取消</Button>,
            <Button key="submit" type="primary" size="large" loading={false} onClick={()=>{
                dispatch({type:'cloudSystem/updateEditItem',payload:{checkState:true}});
                dispatch({type:'cloudSystem/updateCloudSystem'});
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
                dispatch({type:'cloudSystem/updateEditItem',payload:{
                    ...obj
                }})
            },
            codeUnique,
            nameUnique,
            passwordFlag,
            validateCode(obj){
                dispatch({type:"cloudSystem/validateCode",payload:{obj}});
            },
            updateState:(obj)=>{
                dispatch({type:'cloudSystem/updateState',payload:{
                    ...obj
                }});
            }
        }
    }

    const viewItemProps = {
        modalProps:{
            title:'云系统管理 > 查看云系统',
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
                dispatch({type:'cloudSystem/updateState',payload:{
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
            	dispatch({type:'cloudSystem/updateState',payload:{
                    currentPageIndex:page,
                    pageSize
                }})
                dispatch({type:'cloudSystem/getTableData'})
            },
            // pageSize 变化的回调
            onShowSizeChange(current, size){
            	dispatch({type:'cloudSystem/updateState',payload:{
                    currentPageIndex:current,
                    pageSize:size
                }})
                dispatch({type:'cloudSystem/getTableData'})
            },
            showTotal: total => `合计 ${totalItems} 条`
        }
    }

    const mapCallback = (lglt)=>{
        dispatch({type:'cloudSystem/updateState',payload:{modal1Visible:false}});
    	dispatch({'type':'cloudSystem/updateWindowMapInfo',payload:{
            showMap: true,
            mapPoints: [{id:'point',longitude:lglt[0],latitude:lglt[1]}],
            centerPoint: lglt
        }});
        dispatch({type:'cloudSystem/updateNewItem',payload:{
            longitude:lglt[0],
            latitude:lglt[1],
            longitudeDone:lglt[0],
            latitudeDone:lglt[1]
        }});
        dispatch({type:'cloudSystem/updateEditItem',payload:{
            longitude:lglt[0],
            latitude:lglt[1],
            longitudeDone:lglt[0],
            latitudeDone:lglt[1]
        }});
    }
    function showNewWindow(){
        dispatch({type:'cloudSystem/clearNewItem'});
        dispatch({type:'cloudSystem/initWindowMapInfo'});
    }
    function hideNewWindow(){
        dispatch({type:'cloudSystem/updateNewItem',payload:{
            visible:false
        }})
    }
    function showEditWindow(data){
        dispatch({type:'cloudSystem/updateEditItem',payload:{
            ...data,
            visible:true
        }})
        if(data.latitude && data.longitude){
            setTimeout(()=>{
                dispatch({'type':'cloudSystem/updateWindowMapInfo',payload:{
                    showMap: true,
                    mapPoints: [{id:'point',longitude:data.longitude,latitude:data.latitude}],
                    centerPoint: [data.longitude, data.latitude]
                }});},100);
        }else {
            dispatch({'type':'cloudSystem/initWindowMapInfo'});
        }
    }
    function hideEditWindow(){
        dispatch({type:'cloudSystem/updateEditItem',payload:{
            visible:false
        }})
    }
    function showViewWindow(data){
        dispatch({type:'cloudSystem/updateViewItem',payload:{
            ...data,
            visible:true
        }})
        if(data.latitude && data.longitude){
            setTimeout(()=>{
                dispatch({'type':'cloudSystem/updateWindowMapInfo',payload:{
                    showMap: true,
                    mapPoints: [{id:'point',longitude:data.longitude,latitude:data.latitude}],
                    centerPoint: [data.longitude, data.latitude]
                }});},100);
        }else {
            dispatch({'type':'cloudSystem/initWindowMapInfo'});
        }
    }
    function hideViewWindow(){
        dispatch({type:'cloudSystem/updateViewItem',payload:{
            visible:false
        }})
    }
    const closeMapModal = ()=>{
        dispatch({type:'cloudSystem/updateState',payload:{modal1Visible:false}});
    }
    const querySelect = ()=>{
        dispatch({type:'cloudSystem/initTableOpt'});
    	dispatch({type:'cloudSystem/getTableData'});
    }
    const clearQuery = ()=>{
    	dispatch({type:'cloudSystem/updateState',payload:{
    		systemName:''
        }});
        dispatch({type:'cloudSystem/initTableOpt'});
    	dispatch({type:'cloudSystem/getTableData'});
    }
    const querySystemNameChanged = (e)=>{
    	dispatch({type:'cloudSystem/updateState',payload:{systemName:e.target.value}});
    }
	return (
		<div className={styles.main}>
            <div className={styles.bt_bar}>
                {functionCodesMap['CF_MANAGE_CS_ADD']?<Button icon="file-add" className='primary' onClick={showNewWindow}>新增</Button>:''}
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
export default connect(({cloudSystem})=>({cloudSystem}))(CloudSystem);
