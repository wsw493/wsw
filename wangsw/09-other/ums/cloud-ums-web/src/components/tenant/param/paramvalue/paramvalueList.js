import React from 'react';
import styles from './item.css';
import {Table,Button,Modal,Input,message} from 'antd';
import VtxDataGrid from '../../../vtxCommon/VortexDatagrid/VortexDatagrid';
const Paramvaluelist=({dispatch,pramValueData,listTableLoading,currentPageIndex,pageSize,selectedRowKeys,valueTotalItems,
	editable,valueNewItems,count,typeId,codeValidatFlag1,functionCodesMap})=>{
	//console.log(valueNewItems)
	const column=[{
		title:'显示值',
		dataIndex:'parmName',
		width: 80,
		render:(text,record,index)=>{
			return (
				<div>
			        {
			          valueNewItems.editable && valueNewItems.id == record.id ?
			           [ <div>
			         		<Input value={valueNewItems.parmName} onChange={handleChange} name='parmName' key='input'/>
			           	</div>]
			            :
			            [<div>
			            	{text || ' '}
			            </div>]
			        }
			    </div>
				)
		}

	},{
		title:'值',
		dataIndex:'parmCode',
		width: 80,
		render:(text,record,index)=>{
			return (
				<div>
			        {
			          (valueNewItems.editable && valueNewItems.id == record.id) ?
			           [ <div>
			         		<Input value={valueNewItems.parmCode} onChange={handleChange} name='parmCode' key='input'/>
			           	</div>]
			            :
			            [<div>
			            	{text || ' '}
			            </div>]
			        }
			    </div>
				)
		}
	},{
		title:'排序值',
		dataIndex:'orderIndex',
		width: 80,
		render:(text,record,index)=>{
			return (
				<div>
			        {
			          (valueNewItems.editable && valueNewItems.id == record.id) ?
			           [ <div>
			         		<Input value={valueNewItems.orderIndex} onChange={handleChange} name='orderIndex' key='input'/>
			           	</div>]
			            :
			            [<div>
			            	{text || ' '}
			            </div>]
			        }
			    </div>
				)
		}

	},{
		title:'操作',
		dataIndex:'action',
		width: 80,
		render:(text,record,index)=>{
			return(
				<div>
		            {
		              (valueNewItems.editable && valueNewItems.id == record.id) ?
		                <span>
		                  <a onClick={()=>{
		                  	if(valueNewItems.parmCode=="" || valueNewItems.parmName == ""){
					          message.error("显示值和值都不能为空");
					        }else{
					        	if(record.id == ""){
		                  			dispatch({type:"tenantparam/addParamValue"}); 
								}else {
									dispatch({type:"tenantparam/updateParamValue"}); 
								}
								dispatch({type:'tenantparam/updateValueNewItems',payload:{editable:false}});
					        }
		                  }}>保存</a>
		                </span>
		                :
		                <span>
		                  {functionCodesMap['CF_MANAGE_TENANT_PARAM_UPDATE']?<a onClick={()=>{
		                  		                  	record.editable = true;
		                  		                  	dispatch({type:'tenantparam/updateState',payload:{valueNewItems:record}});
		                  		                  }}>修改</a>:''}
		                </span>
		            }
		        </div>
				)
		}
	}];

	const handleChange=(e)=> {
	    const paramName = e.target.name;
	    switch(paramName){
	    	case'parmName':dispatch({type:'tenantparam/updateValueNewItems',payload:{parmName:e.target.value}});break;
	    	case'parmCode':dispatch({type:'tenantparam/updateValueNewItems',payload:{parmCode:e.target.value}});break;
	    	case'orderIndex':dispatch({type:'tenantparam/updateValueNewItems',payload:{orderIndex:e.target.value}});break;
	    }
	  }
	
	
	//分页
	const tablePagination={
		showSizeChanger: true,
		pageSizeOptions: ['10', '20', '30', '40','50'],
		showQuickJumper: true,
        current:currentPageIndex,
        total:valueTotalItems,
        pageSize
	}
	// 表格页码变化事件
    const tablePageChange = (pagination, filters, sorter)=>{
        //console.log(pagination)
        dispatch({type:'tenantparam/updateValueItems',payload:{
            currentPageIndex:pagination.current,
            pageSize:pagination.pageSize
        }});
        dispatch({type:'tenantparam/getParamValueTableData'});
    }
    const rowSelectionSetting={
		onChange:(selectedRowKeys, selectedRows)=>{
            dispatch({type:'tenantparam/updateValueItems',payload:{selectedRowKeys}});
        },
        selectedRowKeys
	}
	// 按钮栏删除事件
    const deleteItems = ()=>{
        if(selectedRowKeys.length>0){
             Modal.confirm({
                title:`确认删除选中的${selectedRowKeys.length}条数据？`,
                okText:'是',
                onOk:()=>{
                    dispatch({type:'tenantparam/deletesParamValue'});
                }
            })
        }
        else{
            message.warning('请选取需要删除的行',3);
        }    
    }
    //新增按钮
    count=valueTotalItems
    const paramValueAdd=()=>{
    	if(pramValueData.length>valueTotalItems && valueTotalItems%10 != 0){
			message.warning('请先保存');
    	}else{
    		const newData={
	    		 key:count,
	    		 id:'',
			     parmName: '',
			     parmCode: '',
			     orderIndex: `${count+1}`,
			     editable:true
    	    }
    		dispatch({type:'tenantparam/updateValueItems',payload:{currentPageIndex:parseInt(valueTotalItems/10)+1}});
	    	if(parseInt(valueTotalItems/10)+1 != currentPageIndex){
		    	dispatch({type:'tenantparam/getParamValueTableData',payload:{newData:newData}});
		    }else{
		    	dispatch({type:'tenantparam/updatePramValueData',payload:{pramValueData:[...pramValueData,newData],count:count+1}});
		    }
	    	if(valueTotalItems%10 == 0){
    			 dispatch({type:'tenantparam/updateValueItems',payload:{valueTotalItems:valueTotalItems+1}});
    		}
    		
	    	dispatch({type:'tenantparam/updateState',payload:{valueNewItems:newData}});
	    	// dispatch({type:'tenantparam/updatePramValueData',payload:{pramValueData:[...pramValueData,newData],count:count+1}});
	    	dispatch({type:'tenantparam/updateValueNewItems',payload:{typeId}});

    	}
    	
    }
	return (
		<div>
			<div className={styles.button}>
				{functionCodesMap['CF_MANAGE_TENANT_PARAM_ADD']?<Button icon="file-add" onClick={paramValueAdd}>新增</Button>:''}
				{functionCodesMap['CF_MANAGE_TENANT_PARAM_BAT_DEL']?<Button icon="delete" onClick={deleteItems}>删除</Button>:''}
			</div>
			<div className={styles.table}>
				<Table columns={column} loading={listTableLoading} dataSource={pramValueData} onChange={tablePageChange} rowSelection={rowSelectionSetting}
				pagination={tablePagination}/>
			</div>
			
			
		</div>

		)
}
export default Paramvaluelist;