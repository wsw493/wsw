import React from 'react';
import {connect} from 'dva';
import style from './workElementType.less';
import {Button, Input, Table, Modal, Popconfirm, message, Select,Pagination} from 'antd';
import VtxDataGrid from '../../components/vtxCommon/VortexDatagrid/VortexDatagrid';
import VtxGrid from '../../components/vtxCommon/VtxGrid/VtxGrid';
import EditItem from '../../components/workElementType/editItem';
import ViewItem from '../../components/workElementType/viewItem';
function WorkElementType({dispatch,workElementType}) {
	const {tableLoading,tableData,routeList,selectedRowKeys,orgTreeData,codeValidatFlag,codeUnique,nameUnique,code,
    newItem,editItem,operatedRow,currentPageIndex,pageSize,totalItems,viewItem,carCodeInput,searchName,searchShape}=workElementType;
	const columns = [
		{
			title: '编码',
			dataIndex: 'code',
			key: 'code',
			width: 80,
	        nowrap:true
		}, {
			title: '类型名称',
			dataIndex: 'name',
			key: 'name',
			width: 80,
	        nowrap:true
		}, {
			title: '外形',
			dataIndex: 'shape',
			key: 'shape',
			width: 80,
	        nowrap:true,
			render: (text, record, index)=>{
				var val = '';
				switch(text){
					case "point":
						val = "点";
						break;
					case "line":
						val = "多折线";
						break;
					case "polygon":
						val = "多边形";
						break;
					case "rectangle":
						val = "矩形";
						break;
					case "circle":
						val = "圆形";
						break;
				}
				return (<span>{val}</span>);
			}
		}, {
			title: '所属机构',
			dataIndex: 'departmentName',
			key: 'departmentName',
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
						<a onClick={()=>{
	                        showViewWindow(record);
                            //dispatch({type:'workElementType/WorkElementTypeById',payload:{id:record.id}});
	                    }}>查看</a>
	                    <span className="ant-divider" />
	                    <a onClick={()=>{
	                    	showEditWindow(record);
                            //dispatch({type:'workElementType/WorkElementTypeById',payload:{id:record.id}});
	                    }}>修改</a>
                        <span className="ant-divider" />
                        <Popconfirm title="确定删除此条数据吗?" onConfirm={()=>{
                           dispatch({type:'workElementType/deletesWorkElementType',payload:{id:record.id}});
                        }}  okText="是" cancelText="否">
                            <a>删除</a>
                        </Popconfirm>
					</span>
				)
			}
		}
	];
	const gridProps = {
        columns,
        dataSource:tableData,
        loading:tableLoading,
        indexColumn:true, //用了这个属性column里的width不能用百分比
        // startIndex:(currentPageIndex)*pageSize+1,
        autoFit:true,
        hideColumn: true,
        rowSelection:{
            type:'checkbox',
            selectedRowKeys,
            onChange(selectedRowKeys){
                dispatch({type:'workElementType/updateState',payload:{
                    selectedRowKeys:selectedRowKeys
                }});
            }
        },
        pagination:{
            showSizeChanger: true,
            pageSizeOptions: ['10', '20', '30', '40','50'],
            showQuickJumper: true,
            current:currentPageIndex+1,  //后端分页数据配置参数1
            total:totalItems, //后端分页数据配置参数2
            pageSize, //后端分页数据配置参数3
            // 当前页码改变的回调
            onChange(page, pageSize){
            	dispatch({type:'workElementType/updateState',payload:{
                    currentPageIndex:page-1,
                    pageSize
                }})
                dispatch({type:'workElementType/getTableData'})
            },
            // pageSize 变化的回调
            onShowSizeChange(current, size){
            	dispatch({type:'workElementType/updateState',payload:{
                    currentPageIndex:current-1,
                    pageSize:size
                }})
                dispatch({type:'workElementType/getTableData'})
            },
            showTotal: total => `合计 ${totalItems} 条`
        }
    }
    //新增页面参数
    const newItemProps = {
        modalProps:{
            title:'图元图层 >新增图元类型',
            visible: newItem.visible,
            onCancel:hideNewWindow,
            width:600,
            footer:[
            <Button key="cancel" size="large" onClick={()=>{
                dispatch({type:'workElementType/clearNewItem'});
            }}>清空</Button>,
            <Button key="submit" type="primary" size="large" loading={newItem.loading} onClick={()=>{
                dispatch({type:'workElementType/updateNewItem',payload:{checkState: true}});
                dispatch({type:'workElementType/addWorkElementType'});
            }}>
              保存
            </Button>,
            ],
        },
        contentProps:{
            ...newItem,
            updateItem(obj){
                dispatch({type:'workElementType/updateNewItem',payload:{
                    ...obj
                }})
            },
            updateState:(obj)=>{
                dispatch({type:'workElementType/updateState',payload:{
                    ...obj
                }});
            },
            codeUnique,
            nameUnique,
            validateName(obj){
                dispatch({type:"workElementType/validateName",payload:{obj:obj}});
            },
            validateCode(obj){
                dispatch({type:"workElementType/validateCode",payload:{obj:obj}});
            },
            orgTreeData
        }
    }
    const editItemProps = {
        modalProps:{
            title:'图元图层 >修改图元类型',
            visible: editItem.visible,
            onCancel:hideEditWindow,
            width:600,
            footer:[
            <Button key="cancel" size="large" onClick={()=>{
            	hideEditWindow();
            }}>取消</Button>,
            <Button key="submit" type="primary" size="large" loading={editItem.loading} onClick={()=>{
                dispatch({type:'workElementType/updateEditItem',payload:{checkState: true}});
                dispatch({type:'workElementType/updateWorkElementType'});
            }}>
              保存
            </Button>,
            ],
        },
        contentProps:{
            ...editItem,
            updateItem(obj){
                dispatch({type:'workElementType/updateEditItem',payload:{
                    ...obj
                }})
            },
            updateState:(obj)=>{
                dispatch({type:'workElementType/updateState',payload:{
                    ...obj
                }});
            },
            codeUnique,
            nameUnique,
            validateName(obj){
                dispatch({type:"workElementType/validateName",payload:{obj:obj}});
            },
            validateCode(obj){
                dispatch({type:"workElementType/validateCode",payload:{obj:obj}});
            },
            orgTreeData
        }
    }
    const viewItemProps={
    	modalProps:{
            title:'图元图层 >查看图元类型',
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
    	dispatch({type:'workElementType/clearNewItem'});
    	dispatch({type:'workElementType/updateNewItem',payload:{visible:true,checkState:false}});
    	dispatch({type:'workElementType/getOrgTreeData'});
        dispatch({type:"workElementType/updateState",payload:{nameUnique:false,codeUnique:false}});
    }
    function hideNewWindow(){
    	dispatch({type:'workElementType/updateNewItem',payload:{visible:false}});
    }
	function showEditWindow(rowData){
    	dispatch({type:'workElementType/updateEditItem',payload:{visible:true,...rowData,checkState:false}});
    	dispatch({type:'workElementType/getOrgTreeData'});
    }
    function hideEditWindow(){
    	dispatch({type:'workElementType/updateEditItem',payload:{visible:false}});
    }
	function hideViewWindow(){
		dispatch({type:'workElementType/updateViewItem',payload:{visible:false}});
	}
	function showViewWindow(rowData){
		dispatch({type:'workElementType/updateViewItem',payload:{visible:true,...rowData}});
	}
    const deleteItem = ()=>{
        if(selectedRowKeys.length > 0){
            Modal.confirm({
                title: '确认删除所选中的数据吗？',
                okText: '确定',
                onOk: ()=>{
                    dispatch({type:'workElementType/deletesWorkElementType'});
                }
            });
        }else {
            message.warning("选择你要删除的数据");
        }
    }
    function querySelect(){
    	dispatch({type:'workElementType/initTableArgs'});
        dispatch({type:'workElementType/getTableData'});
    }
    function clearQuery(){
    	dispatch({type:'workElementType/initTableArgs'});
        dispatch({type:'workElementType/clearSearchFilter'});
        dispatch({type:'workElementType/getTableData'});
    }
	return (
		<div className={style.main}>
			<VtxGrid 
            titles={['类型名称','外形']}
            gridweight={[1,1]}
            confirm={querySelect}
            clear={clearQuery}
            >
                <Input value={searchName} placeholder={"请输入类型名称"} onChange={(e)=>{
                    dispatch({type:'workElementType/updateState',payload:{searchName:e.target.value}});
                }}/>
                <Select value={searchShape} onSelect={(value, option)=>{
                	dispatch({type:'workElementType/updateState',payload:{searchShape:value}});
                }} style={{ width: "100%" }}>
                	<Select.Option value="" name="searchShape">请选择---</Select.Option>
					<Select.Option value="point" name="searchShape">点</Select.Option>
					<Select.Option value="line" name="searchShape">多折线</Select.Option>
					<Select.Option value="polygon" name="searchShape">多边形</Select.Option>
					<Select.Option value="rectangle" name="searchShape">矩形</Select.Option>
					<Select.Option value="circle" name="searchShape">圆形</Select.Option>
				</Select>
            </VtxGrid>
			<div className={style.bt_bar}>
				<Button className={style.addBtn} icon="file-add" onClick={showNewWindow}>新增</Button>
				<Button className={style.deleteBtn} icon="delete" type="Button" onClick={deleteItem}>删除</Button>
			</div>
			<div className={style.table}>
                <VtxDataGrid {...gridProps}/>
            </div>
            <EditItem {...newItemProps}/>
            <EditItem {...editItemProps}/>
            <ViewItem {...viewItemProps}/>
			
		</div>
	)
}

export default connect(({workElementType})=>({workElementType}))(WorkElementType);