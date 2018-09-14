import React from 'react';
import { connect } from 'dva';
import styles from './xzqh.less';
import style from '../../index.less';
import VtxDataGrid from '../../components/vtxCommon/VortexDatagrid/VortexDatagrid';
import VtxGrid from '../../components/vtxCommon/VtxGrid/VtxGrid';
import {Button, Input, Table, Modal, Popconfirm, message,Tree,Pagination} from 'antd';
import DivisionTree from '../../components/xzqh/divisionTree';
import {VtxMap} from 'vtx-ui';
import {VtxSearchMap} from 'vtx-ui';
import EditItem from '../../components/xzqh/editItem';
import ViewItem from '../../components/xzqh/viewItem';
function Xzqh({dispatch,xzqh}){
	const {tableLoading,tableData,selectedRowKeys,searchTreeId,newItem,editItem,viewItem,
    operatedRow,currentPageIndex,pageSize,totalItems,carCodeInput,mapWindow,
	treeData,treeName,codeValidatFlag,expandedkeys,accountUnique,
	searchName,searchCode,modal1Visible,mapCenter,windowMapInfo,selectedRow,mapId,functionCodesMap}=xzqh;
	const columns = [
		{
			title: '区划名称',
			dataIndex: 'name',
			key: 'name',
			width: 80,
	        nowrap:true
		}, {
			title: '数字代码',
			dataIndex: 'commonCode',
			key: 'commonCode',
			width: 80,
	        nowrap:true
		}, {
			title: '简称',
			dataIndex: 'abbr',
			key: 'abbr',
			width: 80,
	        nowrap:true
		}, {
			title: '操作',
			dataIndex: 'op',
			width: 80,
	        nowrap:true,
			render: (text, record, index)=>{
				return (
					<span>
						{functionCodesMap['CF_MANAGE_XZQH_VIEW']?<a onClick={()=>{
                            dispatch({type:'xzqh/getTemplateDetailByid',payload:{id:record.id}});
							showViewWindow(record);
	                    }}>查看</a>:''}
						{functionCodesMap['CF_MANAGE_XZQH_UPDATE']?<span className="ant-divider" />:''}
	                    {functionCodesMap['CF_MANAGE_XZQH_UPDATE']?<a onClick={()=>{
                            dispatch({type:'xzqh/getTemplateDetailByid',payload:{id:record.id}});
	                        showEditWindow(record);
	                    }}>修改</a>:''}
                        {functionCodesMap['CF_MANAGE_XZQH_DEL']?<span className="ant-divider" />:''}
	                    {functionCodesMap['CF_MANAGE_XZQH_DEL']?<Popconfirm title="确定删除此条数据吗?" onConfirm={()=>{
                            deleteItems(record.id);
                        }}  okText="是" cancelText="否">
                            <a>删除</a>
                        </Popconfirm>:''}
					</span>
				)
			}
		}
	];
	const gridProps = {
        columns:columns,
        dataSource:tableData,
        loading:tableLoading,
        indexColumn:true, //用了这个属性column里的width不能用百分比
        startIndex:(currentPageIndex-1)*pageSize+1,
        autoFit:true,
        rowSelection:{
            type:'checkbox',
            selectedRowKeys,
            onChange(selectedRowKeys,selectedRows){
                var s=selectedRows.map((item,index)=>{
                    return {
                        abbr:item.abbr,
                        commonCode:item.commonCode,
                        id:item.id,
                        name:item.name
                    };
                })
                dispatch({type:'xzqh/updateState',payload:{
                    selectedRowKeys:selectedRowKeys,
                    selectedRow:s
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
            	dispatch({type:'xzqh/updateState',payload:{
                    currentPageIndex:page,
                    pageSize
                }})
                dispatch({type:'xzqh/getTableData'})
            },
            // pageSize 变化的回调
            onShowSizeChange(current, size){
            	dispatch({type:'xzqh/updateState',payload:{
                    currentPageIndex:current,
                    pageSize:size
                }})
                dispatch({type:'xzqh/getTableData'})
            },
            showTotal: total => `合计 ${totalItems} 条`
        }
    }
    //新增页面参数
    const newItemProps = {
        modalProps:{
            title:'管理行政区划 > 新增行政区划',
            visible: newItem.visible,
            onCancel:hideNewWindow,
            width:1000,
            footer:[
            <Button key="cancel" size="large" onClick={()=>{
                dispatch({type:'xzqh/clearNewItem'});
                dispatch({type:'xzqh/initWindowMapInfo'});
            }}>清空</Button>,
            <Button key="submit" type="primary" size="large" loading={newItem.loading} onClick={()=>{
                dispatch({type:'xzqh/updateNewItem',payload:{checkState: true}});
                dispatch({type:'xzqh/addDivision'});
            }}>
              保存
            </Button>,
            ],
        },
        contentProps:{
            ...newItem,
            ...windowMapInfo,
            treeName,
            mapId:'mapNewx',
            accountUnique,
            updateItem(obj){
                dispatch({type:'xzqh/updateNewItem',payload:{
                    ...obj
                }})
            },
            isNewItem: true,
            updateState:(obj)=>{
                dispatch({type:'xzqh/updateState',payload:{
                    ...obj
                }});
            },
            updateWindowMapInfo:(obj)=>{
                dispatch({type:'xzqh/updateWindowMapInfo',payload:{
                    ...obj
                }})
            },
            validateAccount:(obj)=>{
                dispatch({type:'xzqh/validateAccount',payload:{obj:obj}});
            }
        }
    }
    //修改页面参数
    const editItemProps = {
        modalProps:{
            title:'管理行政区划 > 修改行政区划',
            visible: editItem.visible,
            onCancel:hideEditWindow,
            width:1000,
            footer:[
            <Button key="cancel" size="large" onClick={()=>{
                hideEditWindow();
            }}>取消</Button>,
            <Button key="submit" type="primary" size="large" loading={editItem.loading} onClick={()=>{
                dispatch({type:'xzqh/updateDivision'});
            }}>
              保存
            </Button>,
            ],
        },
        contentProps:{
            ...editItem,
            ...windowMapInfo,
            ...operatedRow,
            treeName,
            mapId:'mapUpdatex',
            accountUnique,
            updateItem(obj){
                dispatch({type:'xzqh/updateOperateRow',payload:{
                    ...obj
                }})
            },
            isNewItem: true,
            updateState:(obj)=>{
                dispatch({type:'xzqh/updateState',payload:{
                    ...obj
                }});
            },
            updateWindowMapInfo:(obj)=>{
                dispatch({type:'xzqh/updateWindowMapInfo',payload:{
                    ...obj
                }})
            },
            validateAccount:(obj)=>{
                dispatch({type:'xzqh/validateAccount',payload:{obj:obj}});
            }
        }
    }
    //查看页面参数
    const viewItemProps = {
        modalProps:{
            title:'管理行政区划 > 查看行政区划',
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
            ...operatedRow,
            ...windowMapInfo,
            treeName
        }
    }
    const treeSetting = {
    	dispatch,
    	treeData,expandedkeys
    }
    const mapCallback = (lglt)=>{
        dispatch({type:'xzqh/updateState',payload:{modal1Visible:false}});
    	dispatch({'type':'xzqh/updateWindowMapInfo',payload:{
            showMap: true,
            mapPoints: [{id:'point2',longitude:lglt[0],latitude:lglt[1]}],
            centerPoint: lglt,
        }});
        dispatch({type:'xzqh/updateNewItem',payload:{
            longitude:lglt[0],
            latitude:lglt[1]
        }});
        dispatch({type:'xzqh/updateOperateRow',payload:{
            longitude:lglt[0],
            latitude:lglt[1]
        }});
    }
    const closeMapModal = ()=>{
        dispatch({type:'xzqh/updateState',payload:{modal1Visible:false}});
    }
    function showNewWindow(){
        dispatch({type:'xzqh/clearNewItem'});
        dispatch({type:'xzqh/updateState',payload:{accountUnique:false}})
        dispatch({type:'xzqh/initWindowMapInfo'});
    	dispatch({type:'xzqh/updateNewItem',payload:{visible:true}});
    }
    function hideNewWindow(){
    	dispatch({type:'xzqh/updateNewItem',payload:{visible:false}});
    }
    function showEditWindow(data){
        dispatch({type:'xzqh/updateEditItem',payload:{visible:true}});
        dispatch({type:'xzqh/updateState',payload:{accountUnique:false}})
        if(data.lngLats){
            const arr=[];
            arr.push(data.lngLats);
            const lon=arr.toString().split(',')[0];
            const lat=arr.toString().split(',')[1];
            setTimeout(()=>{
                dispatch({'type':'xzqh/updateWindowMapInfo',payload:{
                    showMap: true,
                    mapPoints: [{id:'point1',longitude:lon,latitude:lat}],
                    centerPoint: [lon, lat]
                }});},10);
        }else {
            dispatch({'type':'xzqh/initWindowMapInfo'});
        }
    }
    function hideEditWindow(data){
        dispatch({type:'xzqh/updateEditItem',payload:{visible:false}});
    }
    function showViewWindow(data){
        dispatch({type:'xzqh/updateViewItem',payload:{...data,visible:true}});
        if(data.lngLats){
            const arr=[];
            arr.push(data.lngLats);
            const lon=arr.toString().split(',')[0];
            const lat=arr.toString().split(',')[1];
            setTimeout(()=>{
                dispatch({'type':'xzqh/updateWindowMapInfo',payload:{
                    showMap: true,
                    mapPoints: [{id:'point1',longitude:lon,latitude:lat}],
                    centerPoint: [lon, lat]
                }});},15);
        }else {
            dispatch({'type':'xzqh/initWindowMapInfo'});
        }
    }
    function hideViewWindow(data){
        dispatch({type:'xzqh/updateViewItem',payload:{visible:false}});
    }
    function deleteItems(id){
        if(id&&typeof id=='string'){
            dispatch({type:'xzqh/updateState',payload:{id}});
        }else if(selectedRowKeys.length==0){
            message.warning('当前没有选中的行可以删除！');
        }
        else{
            Modal.confirm({
                content: `确定删除选中的${selectedRowKeys.length}条数据吗？`,
                okText: '确定',
                cancelText: '取消',
                onOk(){
                    Modal.confirm({
                        title: '警告',
                        content: `要级联删除子记录？`,
                        okText: '确定',
                        cancelText: '取消',
                        onOk(){
                            dispatch({type:'xzqh/deletesDivision'});
                        }
                    });
                }
            });
        }
    }
    function batchSave(){

    }
    return (
    		<div className={styles.main}>
    			<div className={styles.leftTree}>
	                <div>
	                    <DivisionTree {...treeSetting}/>
	                </div>
	            </div>
	             <div className={styles.rigthCont}>
	             	<div className={styles.bt_bar}>
						{functionCodesMap['CF_MANAGE_XZQH_ADD']?<Button icon="file-add" onClick={showNewWindow}>新增</Button>:''}
						{functionCodesMap['CF_MANAGE_XZQH_DEL']?<Button icon="delete" onClick={deleteItems}>删除</Button>:''}
						{/*<Button icon="save" onClick={batchSave} onClick={()=>{
                            dispatch({type:'xzqh/batchSave'});
                        }}>保存</Button>*/}
					</div>
					<div className={styles.table}>
	                    <VtxDataGrid {...gridProps}/>
	                </div>
	             </div>
	             <EditItem  {...newItemProps} />
                 <EditItem  {...editItemProps} />
            	 <ViewItem  {...viewItemProps} />
	             <VtxSearchMap
	            	callback={mapCallback}
	                mapCenter={mapCenter}
	                modal1Visible={modal1Visible}
	                closeModal={closeMapModal}
	            />
    		</div>
    	)
}
export default connect(({xzqh})=>({xzqh}))(Xzqh);