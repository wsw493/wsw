import React from 'react';
import { connect } from 'dva';
import styles from './bindTenant.less';
import {Table,Input,Select,Button,Modal,Pagination,Transfer } from 'antd';
import VtxDataGrid from '../../components/vtxCommon/VortexDatagrid/VortexDatagrid';
import VtxGrid from '../../components/vtxCommon/VtxGrid/VtxGrid';
const Option=Select.Option;

function BindTenant({dispatch,bindTenant}) {
	const {tableData,tableLoading,pageSize,currentPageIndex,bindTenantName,enabled,
        selectedRowKeys,saving,totalItems,sort,bindTenantWindow,dataSource,targetKeys} = bindTenant;

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
            sorter: true
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
                        <a onClick={()=>{showBindWindow(record.id)}}>绑定子租户</a>
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
                dispatch({type:'bindTenant/updateState',payload:{
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
            	dispatch({type:'bindTenant/updateState',payload:{
                    currentPageIndex:page,
                    pageSize
                }})
                dispatch({type:'bindTenant/getTableData'})
            },
            // pageSize 变化的回调
            onShowSizeChange(current, size){
            	dispatch({type:'bindTenant/updateState',payload:{
                    currentPageIndex:current,
                    pageSize:size
                }})
                dispatch({type:'bindTenant/getTableData'})
            },
            showTotal: total => `合计 ${totalItems} 条`
        },
        onChange:(pagination, filters, sorter)=>{
            dispatch({type:'bindTenant/updateState',payload:{
                sort: sorter.field?sorter.field:sort,
                order: sorter.order=="descend"?'desc':'asc'
            }});
            dispatch({type:'bindTenant/getTableData'});
        }
    }

    const queryTenantNameChanged = (e)=>{
    	dispatch({type:'bindTenant/updateState',payload:{bindTenantName:e.target.value}});
    }
    const queryEnabledChanged = (value, option)=>{
    	dispatch({type:'bindTenant/updateState',payload:{enabled:value}});
    }
    const querySelect = ()=>{
        dispatch({type:'bindTenant/initTableOpt'});
    	dispatch({type:'bindTenant/getTableData'});
    }
    const clearQuery = ()=>{
    	dispatch({type:'bindTenant/updateState',payload:{
    		bindTenantName:'',
    		enabled:''
        }});
        dispatch({type:'bindTenant/initTableOpt'});
    	dispatch({type:'bindTenant/getTableData'});
    }

    const showBindWindow = (id)=>{
        dispatch({type:'bindTenant/updateState',payload:{
            bindTenantWindow:true,
            mainTenantId:id,
            dataSource: [],
            targetKeys: []
        }});
        // dispatch({type:'bindTenant/getHadBindTenantList',payload:{id}});
        dispatch({type:'bindTenant/getAllTenantList'});
    }

    const hideBindWindow = ()=>{
        dispatch({type:'bindTenant/updateState',payload:{
            bindTenantWindow:false,
            dataSource: [],
            targetKeys: []
        }});
    }

    const bindSaving = ()=>{
        dispatch({type:'bindTenant/bindTenantSaving'});
    }

	return (
		<div className={styles.main}>
			<VtxGrid 
            titles={['租户名称','使用状态']}
            gridweight={[1,1]}
            confirm={querySelect}
            clear={clearQuery}
            >
                <Input value={bindTenantName} onChange={queryTenantNameChanged} placeholder='输入租户名称'/>
                <Select style={{ width: '100%' }} value={enabled} onChange={queryEnabledChanged}>
                    <Option value="">全部</Option>
                    <Option value="1">启用</Option>
                    <Option value="0">禁用</Option>
                </Select>
            </VtxGrid>
    		<div className={styles.table}>
                <VtxDataGrid {...gridProps}/>
            </div>
			
            <Modal title="绑定子租户" visible={bindTenantWindow} onCancel={hideBindWindow} maskClosable={false} width={760} footer={[
                <Button type="primary" saving={saving} onClick={bindSaving}>保存</Button>,
                <Button onClick={hideBindWindow}>取消</Button>
            ]}>
                <Transfer 
                    dataSource={dataSource}
                    targetKeys={targetKeys}
                    showSearch={true}
                    render={item => item.title}
                    onChange={(targetKeys, direction, moveKeys)=>{
                        dispatch({type:'bindTenant/updateState',payload:{targetKeys}});
                    }}
                    listStyle={{
                        width: 300,
                        height: 300,
                    }}
                />
            </Modal>
		</div>
	)
}
export default connect(({bindTenant})=>({bindTenant}))(BindTenant);
