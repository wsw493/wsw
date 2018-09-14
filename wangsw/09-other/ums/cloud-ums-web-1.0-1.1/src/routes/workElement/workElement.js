import React from 'react';
import {connect} from 'dva';
import styles from './workElement.less';
import {Button, Input, Table, Modal, Popconfirm, message, Select, TreeSelect,Pagination,Radio,notification} from 'antd';
import VtxDataGrid from '../../components/vtxCommon/VortexDatagrid/VortexDatagrid';
import VtxGrid from '../../components/vtxCommon/VtxGrid/VtxGrid';
import EditItem from '../../components/workElement/editItem';
import ViewItem from '../../components/workElement/viewItem';
import Map from '../../components/vtxCommon/Map/Map';
const RadioGroup = Radio.Group;
const Option = Select.Option;
import VtxTreeSelect from '../../components/vtxCommon/VtxTreeSelect/VtxTreeSelect';
import ImportResult from '../../components/importResult/importResult';
const WorkElement = ({dispatch,workElement}) => {
    const {tableLoading,tableData,routeList,selectedRowKeys,orgTreeData,divisionTreeData,codeValidatFlag,
    operatedRow,currentPageIndex,pageSize,totalItems,newItem,editItem,viewItem,carCodeInput,editLoading,searchName,searchWorkElementTypeId,searchOrgId,isDoEdit,
    workElementTypeList,mapDraw,isDraw,mapPoints,mapLines,mapPolygons,mapCircles,editGraphicId,isClearAll,scrollHeight,saving,importWindow,exportWindow,uploading,
    radioG,importDetail,importDelWindow,codeUnique,nameUnique,mapCenter,setCenter}=workElement;
    let myForm = null;
    const columns = [
		{
			title: '编码',
			dataIndex: 'code',
			key: 'code',
			width: 100,
	        nowrap:true
		}, {
			title: '图元名称',
			dataIndex: 'name',
			key: 'name',
			width: 100,
	        nowrap:true
		}, {
			title: '图元类型',
			dataIndex: 'workElementTypeName',
			key: 'workElementTypeName',
			width: 100,
	        nowrap:true
		}, {
			title: '所属机构',
			dataIndex: 'departmentName',
			key: 'departmentName',
			width: 100,
	        nowrap:true
		}, {
            title: '所属行政区划',
            dataIndex: 'divisionName',
            key: 'divisionName',
            width: 100,
	        nowrap:true
        }, {
            title: '操作',
            dataIndex: 'op',
            key: 'op',
            width: 100,
	        nowrap:true,
            render: (text, record, index)=>{
                return (
                    <span>
                        <a onClick={()=>{
                            showViewWindow(record);
                        }}>查看</a>
                        <span className="ant-divider" />
                        <a onClick={()=>{
                            showEditWindow(record);    
                        }}>修改</a>
                        <span className="ant-divider" />
                        <Popconfirm title="确定删除此条数据吗?" onConfirm={()=>{
                            deleteItem(record.id);
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
        startIndex:(currentPageIndex-1)*pageSize+1,
        autoFit:true,
        hideColumn: true,
        rowSelection:{
            type:'checkbox',
            selectedRowKeys,
            onChange(selectedRowKeys,selectedRows){
                dispatch({type:'workElement/updateState',payload:{
                    selectedRowKeys:selectedRowKeys,
                    operatedRow:selectedRows[0]
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
                dispatch({type:'workElement/updateState',payload:{
                    currentPageIndex:page,
                    pageSize
                }})
                dispatch({type:'workElement/getTableData'})
            },
            // pageSize 变化的回调
            onShowSizeChange(current, size){
                dispatch({type:'workElement/updateState',payload:{
                    currentPageIndex:current,
                    pageSize:size
                }})
                dispatch({type:'workElement/getTableData'})
            },
            showTotal: total => `合计 ${totalItems} 条`
        }
    }
    //新增页面参数
    const newItemProps = {
        modalProps:{
            title:'图元管理 >新增图元',
            visible: newItem.visible,
            onCancel:hideNewWindow,
            width:1000,
            footer:[
            <Button key="cancel" size="large" onClick={()=>{
                dispatch({type:'workElement/clearAllTY'});
                dispatch({type:'workElement/clearNew'});
            }}>清空</Button>,
            <Button key="submit" type="primary" size="large" loading={newItem.loading} onClick={()=>{
                dispatch({type:'workElement/updateNewItem',payload:{checkState: true}});
                dispatch({type:'workElement/addWorkElement'});
            }}>
              保存
            </Button>,
            ],
        },
        contentProps:{
            ...newItem,
            dispatch,
            workElementTypeList,
            mapDraw,
            isDraw,
            mapPoints,
            mapLines,
            mapPolygons,
            mapCircles,
            editGraphicId,
            orgTreeData,
            divisionTreeData,
            isClearAll,
            isDoEdit,
            mapCenter,
            setCenter,
            updateItem(obj){
                dispatch({type:'workElement/updateNewItem',payload:{
                    ...obj
                }})
            },
            updateState:(obj)=>{
                dispatch({type:'workElement/updateState',payload:{
                    ...obj
                }});
            },
            doEdit:(obj)=>{
                dispatch({type:"workElement/doEdit",payload:{editGraphicId: obj.id}})
            },
            codeUnique,
            nameUnique,
            validateName(obj){
                dispatch({type:"workElement/validateName",payload:{obj:obj}});
            },
            validateCode(obj){
                dispatch({type:"workElement/validateCode",payload:{obj:obj}});
            }
        }
    }
    //修改页面参数
    const editItemProps = {
        modalProps:{
            title:'图元管理 >修改图元',
            visible: editItem.visible,
            onCancel:hideEditWindow,
            width:1000,
            footer:[
            <Button key="cancel" size="large" onClick={()=>{
                hideEditWindow();
            }}>取消</Button>,
            <Button key="submit" type="primary" size="large" loading={editItem.loading} onClick={()=>{
                dispatch({type:'workElement/updateWorkElement'});
            }}>
              保存
            </Button>,
            ],
        },
        contentProps:{
            ...editItem,
            dispatch,
            workElementTypeList,
            mapDraw,
            isDraw,
            mapPoints,
            mapLines,
            mapPolygons,
            mapCircles,
            editGraphicId,
            orgTreeData,
            divisionTreeData,
            isClearAll,
            isDoEdit,
            mapCenter,
            setCenter,
            updateItem(obj){
                dispatch({type:'workElement/updateEditItem',payload:{
                    ...obj
                }})
            },
            updateState:(obj)=>{
                dispatch({type:'workElement/updateState',payload:{
                    ...obj
                }});
            },
            doEdit:(obj)=>{
                dispatch({type:"workElement/doEdit",payload:{editGraphicId: obj.id}})
            },
            codeUnique,
            nameUnique,
            validateName(obj){
                dispatch({type:"workElement/validateName",payload:{obj:obj}});
            },
            validateCode(obj){
                dispatch({type:"workElement/validateCode",payload:{obj:obj}});
            }
        }
    }
    const viewItemProps={
       modalProps:{
            title:'图元管理 >查看图元',
            visible: viewItem.visible,
            onCancel:hideViewWindow,
            width:1000,
            footer:[
            <Button key="cancel" size="large" onClick={()=>{
                dispatch({type:"workElement/updateViewItem",payload:{visible:false}});
            }}>关闭</Button>
            ],
        }, 
        contentProps:{
            ...viewItem,
            mapDraw,
            isDraw,
            mapPoints,
            mapLines,
            mapPolygons,
            mapCircles,
            editGraphicId,
            isClearAll,
            isDoEdit
        }
    }
    function showNewWindow(){
        dispatch({type:'workElement/clearNew'});
        dispatch({type:'workElement/clearAllTY'});
        dispatch({type:'workElement/clearMap'});
        dispatch({type:'workElement/getLoginInfo'});
        dispatch({type:'workElement/getDivisionTreeData'});
        dispatch({type:'workElement/updateNewItem',payload:{visible:true}});
        dispatch({type:"workElement/updateState",payload:{nameUnique:false,codeUnique:false}});
    }
    function hideNewWindow(){
        dispatch({type:'workElement/updateNewItem',payload:{visible:false}});
    }
    // function showEditWindow(){
    //     if(selectedRowKeys.length > 1){
    //         message.warning("每次只能修改一条记录详情;");
    //     }else if(selectedRowKeys.length == 0){
    //         message.warning("选择要操作的行;");
    //     }else{
    //         dispatch({type:'workElement/getDtoById'});
    //         dispatch({type:'workElement/getDivisionTreeData'});
    //         dispatch({type:'workElement/updateEditItem',payload:{visible:true,...operatedRow}});
    //     }
    // }
    function showEditWindow(data){
        dispatch({type:'workElement/clearMap'});
        dispatch({type:'workElement/getLoginInfo'});
        dispatch({type:'workElement/getDivisionTreeData'});
        dispatch({type:'workElement/updateState',payload:{operatedRow:data}});
        dispatch({type:'workElement/updateEditItem',payload:{
            visible:true,
            ...data
        }});
        setTimeout(()=>{
            dispatch({type:'workElement/getDtoById'});
        },100);
    }
    function hideEditWindow(){
        dispatch({type:'workElement/updateEditItem',payload:{visible:false}});
    }
    function hideViewWindow(){
        dispatch({type:'workElement/updateViewItem',payload:{visible:false}});
    }
    // function showViewWindow(){
    //     if(selectedRowKeys.length > 1){
    //         message.warning("每次只能查看一条记录详情;");
    //     }else if(selectedRowKeys.length == 0){
    //         message.warning("选择要操作的行;");
    //     }else{
    //         dispatch({type:'workElement/clearMap'});
    //         dispatch({type:'workElement/updateViewItem',payload:{visible:true,...operatedRow}});
    //         setTimeout(()=>{
    //             dispatch({type:'workElement/getDtoById'});
    //         },100);
    //     }
    // }
    function showViewWindow(data){
        dispatch({type:'workElement/clearMap'});
        dispatch({type:'workElement/getLoginInfo'});
        dispatch({type:'workElement/updateState',payload:{operatedRow:data}});
        dispatch({type:'workElement/updateViewItem',payload:{
            visible:true,
            ...data
        }});
        setTimeout(()=>{
            dispatch({type:'workElement/getDtoById'});
        },100);
    }
   

    const deleteItem = (id)=>{
        if(id && typeof(id)=='string'){
            dispatch({type:'workElement/deletesWorkElement',payload: {id}});
        }else if(selectedRowKeys.length > 0){
            Modal.confirm({
                title: '确认删除所选中的数据吗？',
                okText: '确定',
                onOk: ()=>{
                    dispatch({type:'workElement/deletesWorkElement'});
                }
            });
        }else {
            message.warning("选择你要删除的数据;");
        }
    }

    const handleFile = (e)=>{
        myForm = new FormData();
        myForm.append("file",e.target.files[0]);
        console.log(e.target.files[0]);
        console.log(myForm);
    }
    const importDelColumns = 
        [{
            title: '行号',
            dataIndex: 'rowNum',
            key: 'rowNum',
            width:30
        },{
            title: '是否成功',
            dataIndex: 'successful',
            key: 'successful',
            width:80,
            render: (text, record, index)=>{
                var txt = "成功"
                if (text == false){
                    txt = "失败";
                }
                return <span>{txt}</span>
            }
        },{
            title: '消息',
            dataIndex: 'message',
            key: 'message',
            width:200
        }];
        const importDelGridProps = {
        columns:importDelColumns,
        dataSource:importDetail.tableData,
        loading:importDetail.tableLoading,
        indexColumn:true, //用了这个属性column里的width不能用百分比
        startIndex:(importDetail.currentPageIndex-1)*importDetail.pageSize+1,
        autoFit:true,
        rowSelection:{
            type:'checkbox',
            selectedRowKeys:importDetail.selectedRowKeys,
            onChange(selectedRowKeys){
                dispatch({type:'workElement/updateImportState',payload:{
                    selectedRowKeys:selectedRowKeys
                }});
            }
        },
        pagination:{
            showSizeChanger: true,
            pageSizeOptions: ['10', '20', '30', '40','50'],
            showQuickJumper: true,
            current:importDetail.currentPageIndex,  //后端分页数据配置参数1
            total:importDetail.totalItems, //后端分页数据配置参数2
            pageSize:importDetail.pageSize, //后端分页数据配置参数3
            // 当前页码改变的回调
            onChange(page, pageSize){
                dispatch({type:'workElement/updateImportState',payload:{
                    currentPageIndex:page,
                    pageSize
                }})
                dispatch({type:'workElement/getImportTableData'})
            },
            // pageSize 变化的回调
            onShowSizeChange(current, size){
                dispatch({type:'workElement/updateImportState',payload:{
                    currentPageIndex:current,
                    pageSize:size
                }})
                dispatch({type:'workElement/getImportTableData'})
            },
            showTotal: total => `合计 ${importDetail.totalItems} 条`
        }
    }
    
    const importDataCallback = (mess)=>{
        notification.open({
            message: '导入结果',
            description: mess,
            duration: null,
            onClose: ()=>{
                console.log("close notification");
            },
            btn: (<Button onClick={()=>{
                dispatch({type:'workElement/updateImportState',payload:{visible:true}});
                dispatch({type:'workElement/getImportTableData',payload:{successful:""}});
            }}>查看详情</Button>)
        });
    }
    function clearQuery(){
        dispatch({type:'workElement/initTableArgs'});
        dispatch({type:'workElement/clearSearchFilter'});
        dispatch({type:'workElement/getTableData'});
    }
    function querySelect(){
        dispatch({type:'workElement/initTableArgs'});
        dispatch({type:'workElement/getTableData'});
    }
    //导入数据结果
    const importResultProps = {
        modalProps: {
            title: '导入 > 导入详情',
            visible: importDetail.visible,
            width: 1000,
            footer: null,
            onCancel:()=>{
                dispatch({'type':'workElement/updateImportState',payload:{visible:false}});
            }
        },
        contentProps: {
            importDetail,
            gridProps:importDelGridProps,
            querySelect: ()=>{
                dispatch({type:'workElement/getImportTableData',payload:{successful:importDetail.successful}});
            },
            clearQuery: ()=>{
                dispatch({type:'workElement/updateImportState',payload:{
                    successful: ''
                }});
                dispatch({type:'workElement/getImportTableData',payload:{successful:''}});
            },
            updateImportDetail: (obj)=>{
                dispatch({type:'workElement/updateImportState',payload:{...obj}});
            }
        }
    }
	return (
		<div className={styles.main}>
            <VtxGrid 
            titles={['名称','图元类型','所属机构']}
            gridweight={[1,1,1]}
            confirm={querySelect}
            clear={clearQuery}
            >
                <Input value={searchName} placeholder={"请输入名称"} onChange={(e)=>{
                    dispatch({type:'workElement/updateState',payload:{searchName:e.target.value}});
                }}/>
                <Select value={searchWorkElementTypeId} onSelect={(value, option)=>{
                    dispatch({type:'workElement/updateState',payload:{searchWorkElementTypeId:value}});
                }} style={{ width: "100%","marginRight":'10px' }}>
                    <Option value="" name="searchWorkElementTypeId">请选择---</Option>
                    {workElementTypeList.map((element)=>{return <Option value={element.id} name="searchWorkElementTypeId">{element.name}</Option>;})}
                </Select>
                <TreeSelect
                    treeData={orgTreeData}
                    treeDefaultExpandAll
                    style={{width: "100%"}}
                    dropdownStyle={{ maxHeight: 400, overflow: 'auto' }}
                    onChange={(value)=>{
                        dispatch({type:"workElement/updateState",payload:{searchOrgId: value}});
                    }}
                    value={searchOrgId}
                />
                
            </VtxGrid>
			<div className={styles.bt_bar}>
                <Button icon="file-add" className='primary' onClick={showNewWindow}>新增</Button>
                {/* <Button icon="file-text" className="view" onClick={showViewWindow}>查看</Button>
				<Button icon="form" className="primary" onClick={showEditWindow}>更新</Button> */}
				<Button icon="delete" className='delete' type="Button" onClick={deleteItem}>删除</Button>
				<Button icon="select" className="primary" type="Button" onClick={()=>{
                    dispatch({type:'workElement/updateState',payload:{importWindow:true}});
                }}>导入</Button>
                <Button icon="export" className="primary" type="Button" onClick={()=>{
                    dispatch({type:'workElement/updateState',payload:{exportWindow:true}});
                }}>导出</Button>
			</div>
			<div className={styles.table}>
                <VtxDataGrid {...gridProps}/>
            </div>
            <EditItem {...newItemProps}/>
            <EditItem {...editItemProps}/>
            <ViewItem {...viewItemProps}/>
			<Modal title="导入数据 " width={760} visible={importWindow} onCancel={()=>{
		            $("#file").val("");
		            dispatch({type:'workElement/updateState',payload:{importWindow:false}});
		        }} width={700}  maskClosable={false}
		        footer={[
		            <a className={styles.commonBtn} href="/resources/template/图元导入模版.zip">下载模板</a>,
		            <Button key="import" type="primary" size="large" loading={uploading} disabled={uploading} onClick={()=>{
		                if(myForm != null){
		                    dispatch({type:'workElement/updateState',payload:{uploading:true}});
		                    dispatch({type:'workElement/importData',payload:{uploadFile:myForm,importDataCallback}});
		                }else {
		                    message.error("请选择文件导入");
		                }
		            }}>导入</Button>,
		            <Button key="cancel" type="primary" size="large" onClick={()=>{
		                $("#file").val("");
		                dispatch({type:'workElement/updateState',payload:{uploadFile:{},importWindow:false,uploading:false}});
		            }}>取消</Button>
		        ]}>
		        <p className={styles.standard}>请选择一个文件上传，完成后点击下方按钮中的"导入"按钮</p>
		       <input type="file" className={styles.fileButton} name='file' id='file' onChange={handleFile}/>
		    </Modal>

		    <Modal title="导出数据 " width={760} visible={exportWindow} onCancel={()=>{
                    dispatch({type:'workElement/updateState',payload:{exportWindow:false}});
                }} width={700}  maskClosable={false}
                footer={[
                    <Button key="export" type="primary" size="large" onClick={()=>{
                        if(radioG == 'select' && selectedRowKeys.length == 0){
                            message.error("请选择你要导出的数据",2.5);
                        }else {
                            dispatch({type:'workElement/exportData',payload:{downloadType:radioG}});
                        }
                    }}>导出</Button>,
                    <Button key="cancel" type="primary" size="large" onClick={()=>{
                        dispatch({type:'workElement/updateState',payload:{exportWindow:false}});
                    }}>取消</Button>
                ]}>
                <RadioGroup value={radioG} onChange={(e)=>{
                    dispatch({type:'workElement/updateState',payload:{radioG:e.target.value}});
                }}>
                    <Radio name="staff" value="select" defaultChecked="true">导出选中数据</Radio>
                    <Radio name="staff" value="page">导出当页数据</Radio>
                    <Radio name="staff" value="all">导出所有数据</Radio>
                </RadioGroup>
            </Modal>
            <ImportResult {...importResultProps}/>
            {/*<Modal title="导入详情 " visible={importDelWindow} onCancel={()=>{
                    dispatch({type:'workElement/updateState',payload:{importDelWindow:false}});
                }} width={960}  maskClosable={false} wrapClassName={styles.importDelWindow} style={{position: 'relative'}}
                footer={[
                    <Button key="back" size="large" onClick={()=>{
                        dispatch({type:'workElement/updateState',payload:{importDelWindow:false}});
                    }}>关闭</Button>
                ]}>
                <Select defaultValue="" onChange={(value,Option)=>{
                     dispatch({type:'workElement/getImportTableData',payload:{successful:value}});
                }} style={{width: '200px','margin-bottom': '5px'}}>
                    <Option value=''>全部</Option>
                    <Option value='true'>成功</Option>
                    <Option value='false'>失败</Option>
                </Select>
                <Table columns={importColumns} dataSource={importDetail.tableData} rowSelection={importRowSelectionSetting}
                    pagination={false} loading={importDetail.tableLoading} scroll={{y:600}}/>
                <Pagination {...importPaginationSetting} onChange={importTablePageChange} onShowSizeChange={importTablePageChange} className={styles.pagenation}/>
            </Modal>*/}
		</div>
	)
}

export default connect(({workElement})=>({workElement}))(WorkElement);