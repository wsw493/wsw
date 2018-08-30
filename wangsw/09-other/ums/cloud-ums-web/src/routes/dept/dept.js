import React from 'react';
import { connect } from 'dva';
import styles from './dept.less';
import VtxDataGrid from '../../components/vtxCommon/VortexDatagrid/VortexDatagrid';
import VtxGrid from '../../components/vtxCommon/VtxGrid/VtxGrid';
import {Button,Input} from 'antd';
import {VtxMap} from 'vtx-ui';
import {VtxSearchMap} from 'vtx-ui';
import EditItem from '../../components/dept/editItem';
import ViewItem from '../../components/dept/viewItem';
import VtxPopconfirm from '../../components/vtxCommon/vtxPopconfirm';
function Dept({dispatch,dept}){
	const {tableData,tableLoading,newItem,editItem,viewItem,currentPageIndex,selectedRowKeys,totalItems,divisionTree,
		pageSize,searchName,modal1Visible,mapCenter,deptypesment,codeUnique,depName,functionCodesMap}=dept;
	const column=[
		{
			title: '编码',
	        dataIndex: 'depCode',
	        key: 'depCode',
	        width:80,
	        nowrap:true
		},{
            title: '名称',
            dataIndex: 'depName',
            key: 'depName',
            width:80,
            nowrap:true,
            render:(text,record,index)=>{
                return (
                    functionCodesMap['CF_MANAGE_TENANT_DEPT_VIEW']?
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
						{functionCodesMap['CF_MANAGE_TENANT_DEPT_VIEW']?<a onClick={()=>{
                                                    showViewWindow(record.id);
                                                }}>查看</a>:''}
						{functionCodesMap['CF_MANAGE_TENANT_DEPT_UPDATE']?<span className="ant-divider" />:''}
						{functionCodesMap['CF_MANAGE_TENANT_DEPT_UPDATE']?<a onClick={()=>{
                                                    showEditWindow(record.id);
                                                }}>修改</a>:''}
                        {functionCodesMap['CF_MANAGE_TENANT_DEPT_DELETE']?<span className="ant-divider" />:''}
						<VtxPopconfirm title="确认删除此记录吗？" onConfirm={()=>{
                            dispatch({type:'dept/deleteDept',payload:{id:record.id}});
                        }}>
                            {functionCodesMap['CF_MANAGE_TENANT_DEPT_DELETE']?<a>删除</a>:''}
                        </VtxPopconfirm>
						<span className="ant-divider" />
						<a onClick={()=>{
							window.open("/#/org?departmentId="+record.id+'&token='+token,'组织机构','toolbar=no,menubar=no,location=no');
						}}>组织机构</a>
					</span>
				)
			}
		}
	];
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
                dispatch({type:'dept/updateState',payload:{
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
            	dispatch({type:'dept/updateState',payload:{
                    currentPageIndex:page,
                    pageSize
                }})
                dispatch({type:'dept/getTableData'})
            },
            // pageSize 变化的回调
            onShowSizeChange(current, size){
            	dispatch({type:'dept/updateState',payload:{
                    currentPageIndex:current,
                    pageSize:size
                }})
                dispatch({type:'dept/getTableData'})
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
                dispatch({type:'dept/clearNewItem'});
            }}>清空</Button>,
            <Button key="submit" type="primary" size="large" loading={newItem.loading} onClick={()=>{
                dispatch({type:'dept/updateNewItem',payload:{checkState: true}});
                dispatch({type:'dept/saveDept'});
            }}>
              保存
            </Button>,
            ],
        },
        contentProps:{
            ...newItem,
            deptypesment,
            codeUnique,
            divisionTree,
            updateItem(obj){
                dispatch({type:'dept/updateNewItem',payload:{
                    ...obj
                }})
            },
            validateCode(obj){
                dispatch({type:"dept/validateCode",payload:{obj}});
            },
            updateState:(obj)=>{
                dispatch({type:'dept/updateState',payload:{
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
            <Button key="submit" type="primary" size="large" loading={editItem.loading} onClick={()=>{
                dispatch({type:'dept/updateEditItem',payload:{checkState:true}});
                dispatch({type:'dept/updateDept'});
            }}>
              保存
            </Button>,
            ],
        },
        contentProps:{
            ...editItem,
            deptypesment,
            codeUnique,
            divisionTree,
            updateItem(obj){
                dispatch({type:'dept/updateEditItem',payload:{
                    ...obj
                }})
            },
            validateCode(obj){
                dispatch({type:"dept/validateCode",payload:{obj}});
            },
            updateState:(obj)=>{
                dispatch({type:'dept/updateState',payload:{
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
        dispatch({type:'dept/getDivisionTreeData'});
        dispatch({type:'dept/getdepTypeText'});
        dispatch({type:'dept/clearNewItem'});
    }
    function hideNewWindow(){
        dispatch({type:'dept/updateNewItem',payload:{
            visible:false
        }});
    }
    function showEditWindow(id){
        dispatch({type:'dept/getDivisionTreeData'});
        dispatch({type:'dept/getdepTypeText'});
        dispatch({type:'dept/getDtoById',payload:{id}});
        dispatch({type:'dept/updateEditItem',payload:{
            visible:true
        }})
    }
    function hideEditWindow(){
        dispatch({type:'dept/updateEditItem',payload:{
            visible:false
        }})
    }
    function showViewWindow(id){
        dispatch({type:'dept/getDtoById',payload:{id}});
        dispatch({type:'dept/updateViewItem',payload:{
            visible:true
        }})
    }
    function hideViewWindow(){
        dispatch({type:'dept/updateViewItem',payload:{
            visible:false
        }})
    }
    //查询列表
    const querySelect = ()=>{
        dispatch({type:'dept/initTableOpt'});
        dispatch({type:'dept/getTableData'});
    }
    //清空查询
    const clearQuery = ()=>{
        dispatch({type:'dept/updateState',payload:{depName:''}});
        dispatch({type:'dept/initTableOpt'});
        dispatch({type:'dept/getTableData'});
    }
    //地图
    const mapCallback = (lt)=>{
        dispatch({type:'dept/updateState',payload:{modal1Visible:false}});
        dispatch({type:'dept/convertToAddress',payload:{
            location: lt.join(',')
        }});
    }
    const closeMapModal = ()=>{
        dispatch({type:'dept/updateState',payload:{modal1Visible:false}});
    }
	return (
		<div className={styles.main}>
			<div className={styles.contBox}>
				<VtxGrid 
	                titles={['名称']}
	                gridweight={[1,1]}
	                confirm={querySelect}
	                clear={clearQuery}
	                >
	                <Input style={{ width: '100%' }} placeholder={"请输入名称"} value={depName} onChange={(e)=>{
	                    dispatch({type:'dept/updateState',payload:{depName:e.target.value}});
	                }}/>
	            </VtxGrid>
	            <div className={styles.bt_bar}>
	                {functionCodesMap['CF_MANAGE_TENANT_DEPT_ADD']?<Button icon="file-add" onClick={showNewWindow}>新增</Button>:''}
	                {/* <Button icon="delete" onClick={showNewWindow}>删除</Button> */}
	            </div>
	    		<div className={styles.table}>
	                <VtxDataGrid {...gridProps}/>
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
		</div>

		)
}
export default connect(({dept})=>({dept}))(Dept);
