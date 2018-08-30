import React from 'react';
import { connect } from 'dva';
import styles from './loginLog.less';
import {Table,Input,Select,Button,Modal,Pagination } from 'antd';
import VtxDataGrid from '../../components/vtxCommon/VortexDatagrid/VortexDatagrid';
import VtxGrid from '../../components/vtxCommon/VtxGrid/VtxGrid';
import {VtxRangePicker } from '../../components/vtxCommon/VtxDate/VtxDate';

function LoginLog({dispatch,loginLog}) {
	const {tableData,tableLoading,pageSize,currentPageIndex,selectedRowKeys,totalItems,userName,ip,createTimeStart,createTimeEnd} = loginLog;
    const column=[
		{
			title: '登录账号',
	        dataIndex: 'userName',
	        key: 'userName',
	        width:80,
	        nowrap:true
		},{
			title:'姓名',
			dataIndex:'name',
			key:'name',
			width: 80,
	        nowrap:true
		},{
			title:'IP',
			dataIndex:'ip',
			key:'ip',
			width: 80,
	        nowrap:true
		},{
			title:'操作',
			dataIndex:'operation',
			key:'operation',
			width: 80,
	        nowrap:true
        },{
			title:'登录时间',
			dataIndex:'createTime',
			key:'createTime',
			width: 80,
            nowrap:true,
            render: (text,record,index)=>{
                return <span>{new Date(text).toLocaleString()}</span>
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
                dispatch({type:'loginLog/updateState',payload:{
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
            	dispatch({type:'loginLog/updateState',payload:{
                    currentPageIndex:page,
                    pageSize
                }})
                dispatch({type:'loginLog/getTableData'})
            },
            // pageSize 变化的回调
            onShowSizeChange(current, size){
            	dispatch({type:'loginLog/updateState',payload:{
                    currentPageIndex:current,
                    pageSize:size
                }})
                dispatch({type:'loginLog/getTableData'})
            },
            showTotal: total => `合计 ${totalItems} 条`
        }
    }

    const queryUserNameChanged = (e)=>{
    	dispatch({type:'loginLog/updateState',payload:{userName:e.target.value}});
    }
    const queryIPChanged = (e)=>{
    	dispatch({type:'loginLog/updateState',payload:{ip:e.target.value}});
    }
    const querySelect = ()=>{
        dispatch({type:'loginLog/initTableOpt'});
    	dispatch({type:'loginLog/getTableData'});
    }
    const clearQuery = ()=>{
    	dispatch({type:'loginLog/updateState',payload:{
            userName:'',
            ip:'',
            createTimeStart:'',
            createTimeEnd:''
        }});
        dispatch({type:'loginLog/initTableOpt'});
    	dispatch({type:'loginLog/getTableData'});
    }
    
	return (
		<div className={styles.main}>
			<VtxGrid 
            titles={['登录账号','IP','登录时间段']}
            gridweight={[1,1,2]}
            confirm={querySelect}
            clear={clearQuery}
            >
                <Input value={userName} onChange={queryUserNameChanged} placeholder='输入登录账号'/>
                <Input value={ip} onChange={queryIPChanged} placeholder='输入IP'/>
                <VtxRangePicker 
                    value={[createTimeStart,createTimeEnd]}
                    showTime={true}
                    onChange={(date,dateString)=>{
                        dispatch({type:'loginLog/updateState',payload:{
                            createTimeStart: dateString[0],
                            createTimeEnd: dateString[1]
                        }});
                    }}
                    disabledDate={function disabledDate(current) {
                        return current && current.valueOf() > Date.now();
                    }}
                />
            </VtxGrid>
    		<div className={styles.table}>
                <VtxDataGrid {...gridProps}/>
            </div>
		</div>
	)
}
export default connect(({loginLog})=>({loginLog}))(LoginLog);
