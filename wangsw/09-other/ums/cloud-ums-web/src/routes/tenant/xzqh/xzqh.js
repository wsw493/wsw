import React from 'react';
import { connect } from 'dva';
import styles from './xzqh.less';
import style from '../../../index.less';
import VtxDataGrid from '../../../components/vtxCommon/VortexDatagrid/VortexDatagrid';
import VtxGrid from '../../../components/vtxCommon/VtxGrid/VtxGrid';
import {Button, Input, Table, Modal, Popconfirm, message,Tree,Pagination} from 'antd';
import DivisionTree from '../../../components/tenant/xzqh/divisionTree';
import {VtxMap} from 'vtx-ui';
import {VtxSearchMap} from 'vtx-ui';
import EditItem from '../../../components/tenant/xzqh/editItem';
import ViewItem from '../../../components/tenant/xzqh/viewItem';
function Tenantxzqh({dispatch,tenantxzqu}){
	const {tableLoading,tableData,selectedRowKeys,searchTreeId,newItem,editItem,viewItem,
    operatedRow,currentPageIndex,pageSize,totalItems,carCodeInput,mapWindow,mapType,shapeType,
	treeData,treeName,codeValidatFlag,expandedkeys,parentName,accountUnique,functionCodesMap,
	searchName,searchCode,modal1Visible,mapCenter,windowMapInfo,selectedRow,modal2Visible,mapCenter2,mapId}=tenantxzqu;
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
						{functionCodesMap['CF_MANAGE_TENANT_XZQH_VIEW']?<a onClick={()=>{
                                                    dispatch({type:'tenantxzqu/initWindowMapInfo'});
                                                    dispatch({type:'tenantxzqu/getDivisionTenantDtl',payload:{id:record.id,type:'view'}});
                                                    showViewWindow(record);
                                                }}>查看</a>:''}
						{functionCodesMap['CF_MANAGE_TENANT_XZQH_UPDATE']?<span className="ant-divider" />:''}
	                    {functionCodesMap['CF_MANAGE_TENANT_XZQH_UPDATE']?<a onClick={()=>{
                                                    dispatch({type:'tenantxzqu/initWindowMapInfo'});
                                                    dispatch({type:'tenantxzqu/getDivisionTenantDtl',payload:{id:record.id,type:'edit'}});
                                                    showEditWindow(record);
                                                }}>修改</a>:''}
                        {functionCodesMap['CF_MANAGE_TENANT_XZQH_DEL']?<span className="ant-divider" />:''}
                        {functionCodesMap['CF_MANAGE_TENANT_XZQH_DEL']?<Popconfirm title="确定删除此条数据吗?" onConfirm={()=>{
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
                dispatch({type:'tenantxzqu/updateState',payload:{
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
            	dispatch({type:'tenantxzqu/updateState',payload:{
                    currentPageIndex:page,
                    pageSize
                }})
                dispatch({type:'tenantxzqu/getTableData'})
            },
            // pageSize 变化的回调
            onShowSizeChange(current, size){
            	dispatch({type:'tenantxzqu/updateState',payload:{
                    currentPageIndex:current,
                    pageSize:size
                }})
                dispatch({type:'tenantxzqu/getTableData'})
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
                dispatch({type:'tenantxzqu/clearNewItem'});
                dispatch({type:'tenantxzqu/initWindowMapInfo'});
            }}>清空</Button>,
            <Button key="submit" type="primary" size="large" loading={newItem.loading} onClick={()=>{
                dispatch({type:'tenantxzqu/updateNewItem',payload:{checkState: true}});
                dispatch({type:'tenantxzqu/addDivision'});
            }}>
              保存
            </Button>,
            ],
        },
        contentProps:{
            ...newItem,
            ...windowMapInfo,
            parentName,
            accountUnique,
            mapId:'mapNewtx',
            mapType,
            updateItem(obj){
                dispatch({type:'tenantxzqu/updateNewItem',payload:{
                    ...obj
                }})
            },
            isNewItem: true,
            updateState:(obj)=>{
                dispatch({type:'tenantxzqu/updateState',payload:{
                    ...obj
                }});
            },
            updateWindowMapInfo:(obj)=>{
                dispatch({type:'tenantxzqu/updateWindowMapInfo',payload:{
                    ...obj
                }})
            },
            validateAccount:(obj)=>{
                dispatch({type:'tenantxzqu/validateAccount',payload:{obj:obj}});
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
                dispatch({type:'tenantxzqu/updateDivision'});
            }}>
              保存
            </Button>,
            ],
        },
        contentProps:{
            // ...operatedRow,
            ...editItem,
            ...windowMapInfo,
            parentName,
            accountUnique,
            mapType,
            mapId:'mapUpdatetx',
            updateItem(obj){
                // dispatch({type:'tenantxzqu/updateOperateRow',payload:{
                //     ...obj
                // }})
                dispatch({type:'tenantxzqu/updateEditItem',payload:{
                    ...obj
                }})
            },
            isNewItem: true,
            updateState:(obj)=>{
                dispatch({type:'tenantxzqu/updateState',payload:{
                    ...obj
                }});
            },
            updateWindowMapInfo:(obj)=>{
                dispatch({type:'tenantxzqu/updateWindowMapInfo',payload:{
                    ...obj
                }})
            },
            validateAccount:(obj)=>{
                dispatch({type:'tenantxzqu/validateAccount',payload:{obj:obj}});
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
            parentName,
            mapType,
        }
    }
    const treeSetting = {
    	dispatch,
    	treeData,expandedkeys,searchTreeId
    }
    const mapCallback = (lglt)=>{
        dispatch({type:'tenantxzqu/updateState',payload:{modal1Visible:false}});
    	dispatch({'type':'tenantxzqu/updateWindowMapInfo',payload:{
            showMap: true,
            mapPoints: [{id:'pointt',longitude:lglt[0],latitude:lglt[1]}],
            centerPoint: lglt,
        }});
        dispatch({type:'tenantxzqu/updateNewItem',payload:{
            longitude:lglt[0],
            latitude:lglt[1]
        }});
        dispatch({type:'tenantxzqu/updateEditItem',payload:{
            longitude:lglt[0],
            latitude:lglt[1]
        }});
    }
    const mapCallback2=(lglt)=>{
        var positionStr = lglt.paths;
        var lonlatArr='';
        for(var i=0;i<positionStr.length;i++){
              lonlatArr += positionStr[i][0] + "," + positionStr[i][1] + ";";
        }
        dispatch({type:'tenantxzqu/updateState',payload:{modal2Visible:false}});
        dispatch({'type':'tenantxzqu/updateWindowMapInfo',payload:{
            showMap: true,
            mapLines: [{id:'lines',paths:positionStr}],
        }});
        dispatch({type:'tenantxzqu/updateNewItem',payload:{
            scope:lonlatArr.substring(0,lonlatArr.length-1)
        }});
        dispatch({type:'tenantxzqu/updateEditItem',payload:{
            scope:lonlatArr.substring(0,lonlatArr.length-1)
        }})
    }
    const splitOverride = (data)=>{
      var arrObj = [];
      if (data) {
        var args1 = data.split(";");
        for(var i=0;i<args1.length;i++){
          var args2 = args1[i].split(",");
          arrObj.push([args2[0],args2[1]]);
        } 
      }
      return arrObj;
    }
    const closeMapModal = ()=>{
        dispatch({type:'tenantxzqu/updateState',payload:{modal1Visible:false}});
    }
    const closeMapModal2=()=>{
        dispatch({type:'tenantxzqu/updateState',payload:{modal2Visible:false}});
    }
    function showNewWindow(){
        dispatch({type:'tenantxzqu/clearNewItem'});
        dispatch({type:'tenantxzqu/initWindowMapInfo'});
        dispatch({type:'tenantxzqu/updateState',payload:{accountUnique:false}});
    	dispatch({type:'tenantxzqu/updateNewItem',payload:{visible:true}});
    }
    function hideNewWindow(){
    	dispatch({type:'tenantxzqu/updateNewItem',payload:{visible:false}});
    }
    function showEditWindow(data){
        // dispatch({type:'tenantxzqu/updateEditItem',payload:{
        //     visible:true
        // }});
        dispatch({type:'tenantxzqu/updateState',payload:{accountUnique:false}});
        if(data.lngLats && data.scope){
            const arr=[];
            arr.push(data.lngLats);
            const lon=arr.toString().split(',')[0];
            const lat=arr.toString().split(',')[1];
            setTimeout(()=>{
                dispatch({'type':'tenantxzqu/updateWindowMapInfo',payload:{
                    showMap: true,
                    mapPoints: [{id:'point1',longitude:lon,latitude:lat}],
                    mapLines:[{id:'point1',paths:splitOverride(data.scope)}],
                    centerPoint: [lon, lat]
                }});},10);
        }else if(data.lngLats){
            const arr=[];
            arr.push(data.lngLats);
            const lon=arr.toString().split(',')[0];
            const lat=arr.toString().split(',')[1];
            setTimeout(()=>{
                dispatch({'type':'tenantxzqu/updateWindowMapInfo',payload:{
                    showMap: true,
                    mapPoints: [{id:'point1',longitude:lon,latitude:lat}],
                    centerPoint: [lon, lat]
                }});},10);
        }else if(data.scope){
            setTimeout(()=>{
                dispatch({'type':'tenantxzqu/updateWindowMapInfo',payload:{
                    showMap: true,
                    mapLines:[{id:'point1',paths:splitOverride(data.scope)}],
                }});},10);
        }else {
            dispatch({'type':'tenantxzqu/initWindowMapInfo'});
        }
    }
    function hideEditWindow(data){
        dispatch({type:'tenantxzqu/updateEditItem',payload:{visible:false}});
    }
    function showViewWindow(data){
        // dispatch({type:'tenantxzqu/updateViewItem',payload:{...data,visible:true}});
        if(data.lngLats && data.scope){
            const arr=[];
            arr.push(data.lngLats);
            const lon=arr.toString().split(',')[0];
            const lat=arr.toString().split(',')[1];
            setTimeout(()=>{
                dispatch({'type':'tenantxzqu/updateWindowMapInfo',payload:{
                    showMap: true,
                    mapPoints: [{id:'point1',longitude:lon,latitude:lat}],
                    mapLines:[{id:'point1',paths:splitOverride(data.scope)}],
                    centerPoint: [lon, lat]
                }});},10);
        }else if(data.lngLats){
            const arr=[];
            arr.push(data.lngLats);
            const lon=arr.toString().split(',')[0];
            const lat=arr.toString().split(',')[1];
            setTimeout(()=>{
                dispatch({'type':'tenantxzqu/updateWindowMapInfo',payload:{
                    showMap: true,
                    mapPoints: [{id:'point1',longitude:lon,latitude:lat}],
                    centerPoint: [lon, lat]
                }});},10);
        }else if(data.scope){
            setTimeout(()=>{
                dispatch({'type':'tenantxzqu/updateWindowMapInfo',payload:{
                    showMap: true,
                    mapLines:[{id:'point1',paths:splitOverride(data.scope)}],
                }});},10);
        }else {
            dispatch({'type':'tenantxzqu/initWindowMapInfo'});
        }
    }
    function hideViewWindow(data){
        dispatch({type:'tenantxzqu/updateViewItem',payload:{visible:false}});

    }
    function deleteItems(id){
        if(id&&typeof id =='string'){
            dispatch({type:'tenantxzqu/deletesDivision',payload:{id}});
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
                            dispatch({type:'tenantxzqu/deletesDivision'});
                        }
                    });
                }
            });
        }
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
						{functionCodesMap['CF_MANAGE_TENANT_XZQH_ADD']?<Button icon="file-add" onClick={showNewWindow}>新增</Button>:''}
						{functionCodesMap['CF_MANAGE_TENANT_XZQH_DEL']?<Button icon="delete" onClick={deleteItems}>删除</Button>:''}
						{/*<Button icon="save" onClick={()=>{
                            dispatch({type:'tenantxzqu/batchDivisionTenant'});
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
                    graphicType="point"
                    mapType={mapType}
	            />
                <VtxSearchMap
                    callback={mapCallback2}
                    mapCenter={mapCenter2}
                    modal1Visible={modal2Visible}
                    closeModal={closeMapModal2}
                    graphicType={shapeType}
                    mapType={mapType}
                />
    		</div>
    	)
}
export default connect(({tenantxzqu})=>({tenantxzqu}))(Tenantxzqh);