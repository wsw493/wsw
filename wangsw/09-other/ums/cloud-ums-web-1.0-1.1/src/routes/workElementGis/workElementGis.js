import React from 'react';
import {connect} from 'dva';
import style from './workElementGis.less';
import {Button, Input, Table, Modal, Popconfirm, message, Select,Icon} from 'antd';
import Map from '../../components/vtxCommon/Map/Map';
import StateInput from '../../components/vtxCommon/VtxForm/stateInput';
import StateSelect from '../../components/vtxCommon/VtxForm/stateSelect';
import VtxTreeSelect from '../../components/vtxCommon/VtxTreeSelect/VtxTreeSelect';
import {validate} from '../../components/regExpression';
import EditItem from '../../components/workElementGis/editItem';

// import OrgWorkElementTree from '../../components/workElementGis/orgWorkElementTree';
import OrgWorkElementTree from '../../components/workElementGis/orgWorkElementTreeComp';

const WorkElementGis = ({dispatch,orgWorkElementTree,orgTreeData,divisionTreeData,mapPanelPosition,editWindow,newItem,workElementTypeList,searchWorkElementTypeId,
		mapPoints2,mapLines2,mapPolygons2,mapCircles2,isDraw2,mapDraw2,mapPoints,mapLines,mapPolygons,mapCircles,editGraphicId,isClearAll,searchName,lastMapState,
		mapCenter,checkedWorkElementList,codeValidatFlag,mapCenter2,setCenter,setCenter2,setVisiblePoints,mapVisiblePoints,initWorkElementKeys,checkedKeys_elem,
		editGraphicId2,isDoEdit2,isEndEdit,addPanelDisplay,isDraw,mapDraw,codeUnique,nameUnique,saving,editItem,isDoEdit,inputVal,isDoEditForUpdate,isRemove,mapRemove}) => {
	var form = null;
	var gis;
	let treeObj;
	// let orgWorkElementTree = [
	// 	{
	// 		key: '1',
	// 		name: '1-1',
	// 		attr: {},
	// 		nodeType: 'root',
	// 		children:[
	// 			{
	// 				key: '1-1-1',
	// 				name: '1-1-1',
	// 				attr: {},
	// 				nodeType: 'root',
	// 				children:[]
	// 			},
	// 			{
	// 				key: '1-1-2',
	// 				name: '1-1-2',
	// 				attr: {},
	// 				nodeType: 'root',
	// 				children:[]
	// 			}
	// 		]
	// 	},{
	// 		key: '2-1-1',
	// 		name: '2-1-1',
	// 		attr: {},
	// 		nodeType: 'root'
	// 	}
	// ];
	const treeSetting = {
    	dispatch,
    	orgWorkElementTree,
    	initWorkElementKeys,
		checkedKeys_elem,
		searchName
    }

    const setPanelPosi = (obj)=> {
    	var top = obj.top - 157-10;
    	var left = obj.left + 11;
    	return {
    		display: "block",
    		top: top+"px",
    		left: left+"px"
    	}
    }

    const deleteWorkElement = ()=>{
    	Modal.confirm({
            title: '确认删除该图元吗？',
            okText: '确定',
            onOk: ()=>{
                dispatch({type:'workElementGis/deletesWorkElement'});
            }
        });
	}
	
	const editWorkElement = ()=>{
		dispatch({type:'workElementGis/updateState',payload:{
			isDoEditForUpdate: true,
			editGraphicId2:newItem.editGraphicId2,
			isDoEdit2: true,
			isEndEdit: false,
			mapPanelPosition: {display:'none'},
			lastMapState:{mapPoints2,mapLines2,mapPolygons2,mapCircles2}
		}});
	}
	const updateItem = (obj)=>{
		dispatch({type:'workElementGis/updateNewItem',payload:{
			...obj
		}});
	}
	const saveMapData = (data)=>{
		const type = data.geometry.type;
		var lonlatArr = '';
		var positionStr = '';
		var latitudes='';
		var longitudes='';
		switch(type) {
			case 'point':
				lonlatArr = data.geometry.x + "," + data.geometry.y;
				updateItem({shape:type,paramsDone:lonlatArr,latitudes:data.geometry.y,longitudes:data.geometry.x});
				break;
			case 'line':
			case 'polyline':
				positionStr = data.geometry.paths[0];
				for(var i=0;i<positionStr.length;i++){
						lonlatArr += positionStr[i][0] + "," + positionStr[i][1] + ";";
						latitudes += positionStr[i][1]+',';
						longitudes += positionStr[i][0] + ",";
				}
				updateItem({
					shape:type,
					paramsDone:lonlatArr.substring(0,lonlatArr.length-1),
					latitudes:latitudes.substring(0,latitudes.length-1),
					longitudes:longitudes.substring(0,longitudes.length-1)
				});

				break;
			case 'circle':
				lonlatArr = data.geometry.x + "," + data.geometry.y;
				updateItem({shape:type,paramsDone:lonlatArr,radius:data.geometry.radius});
				break;
			case 'polygon':
			case 'rectangle':
				positionStr = data.geometry.rings[0];
				for(var i=0;i<positionStr.length;i++){
						lonlatArr += positionStr[i][0] + "," + positionStr[i][1] + ";";
						latitudes += positionStr[i][1]+',';
						longitudes += positionStr[i][0] + ",";
				}
				updateItem({
					shape:type,
					paramsDone:lonlatArr.substring(0,lonlatArr.length-1),
					latitudes:latitudes.substring(0,latitudes.length-1),
					longitudes:longitudes.substring(0,longitudes.length-1)
				});
				break;
		}
  }
    const newClear = ()=>{
        dispatch({type:'workElementGis/clearNew'});
    }
    const updateWorkElementGis = ()=>{
		// dispatch({type:'workElementGis/clearNew'});
    	dispatch({type:'workElementGis/clearMap'});
        dispatch({type:'workElementGis/getWorkElementTypeList'});
        dispatch({type:'workElementGis/getOrgTreeData'});
        dispatch({type:'workElementGis/getDivisionTreeData'});
        // dispatch({type:'workElementGis/updateAddItem',payload:{id:elementId});
        dispatch({type:'workElementGis/getDtoById'});
        dispatch({type:'workElementGis/updateState',payload:{editWindow:true}});
	}

	const getWorkElementDel = (mapId)=>{
		var workElement = '';
		for(var i=0;i<checkedWorkElementList.length;i++){
			if(mapId == checkedWorkElementList[i].mapId){
				workElement = checkedWorkElementList[i].attr;
			}
		}
		dispatch({type:'workElementGis/updateNewItem',payload:{
			...workElement,
			editGraphicId2: mapId
		}});
	}

	const updateWorkElement = ()=>{
		dispatch({type:'workElementGis/updateWorkElement'});
	}
	const cancleUpdateWorkElement = ()=>{
		dispatch({type:'workElementGis/updateState',payload:{
			isDoEditForUpdate: false,
			isDoEdit2:false,
			isEndEdit:true,
			editGraphicId2:['']
		}});
		var obj;
		var txt;
		switch(newItem.shape){
			case 'point':
				obj = {mapPoints2};
				txt = 'point';
				break;
			case 'polyline':
				obj = {mapLines2};
				txt = 'line';
				break;
			case 'polygon':
				obj = {mapPolygons2};
				txt = 'polygon';
				break;
			case 'circle':
				obj = {mapCircles2};
				txt = 'circle';
				break;
			case 'rectangle':
				obj = {mapPolygons2};
				txt = 'polygon';
				break;
		}
		dispatch({type:'workElementGis/updateState',payload:{
			isRemove: true,
			mapRemove:[{id:newItem.editGraphicId2,type:txt}]
		}});
		for(var key in lastMapState){
			if(key.indexOf(txt)){
				var arr = [];
				lastMapState[key].map((item)=>{
					if(item.id == newItem.editGraphicId2){
						arr.push({
							...item,
							id:item.id+'New'
						});
					}else {
						arr.push(item);
					}
				});
				obj[key] = arr;
			}
		}
		setTimeout(()=>{
			dispatch({type:'workElementGis/updateState',payload:{
				...obj,
				isRemove: false
			}});
		},100);
	}
	//修改页面参数
    const editItemProps = {
        modalProps:{
            title:'图元管理 >修改图元',
            visible: editItem.visible,
            onCancel:()=>{
                hideEditWindow();
            },
            width:1000,
            footer:[
            <Button key="cancel" size="large" onClick={()=>{
                hideEditWindow();
            }}>取消</Button>,
            <Button key="submit" type="primary" size="large" loading={saving} onClick={()=>{
                dispatch({type:'workElementGis/updateWorkElementForm'});
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
            updateItem(obj){
                dispatch({type:'workElementGis/updateEditItem',payload:{
                    ...obj
                }})
            },
            updateState:(obj)=>{
                dispatch({type:'workElementGis/updateState',payload:{
                    ...obj
                }});
            },
            doEdit:(obj)=>{
                dispatch({type:"workElementGis/doEdit2",payload:{editGraphicId: obj.id}})
            },
            codeUnique,
            nameUnique,
            validateName(obj){
                dispatch({type:"workElementGis/validateName",payload:{obj:obj}});
            },
            validateCode(obj){
                dispatch({type:"workElementGis/validateCode",payload:{obj:obj}});
            }
        }
	}
	
	const hideEditWindow = ()=>{
		dispatch({type:'workElementGis/updateEditItem',payload:{visible:false}});
	}
	const showEditWindow = ()=>{
		dispatch({type:'workElementGis/getOrgTreeData'});
		dispatch({type:'workElementGis/getDivisionTreeData'});
		dispatch({type:'workElementGis/getDtoById'});
		dispatch({type:'workElementGis/updateEditItem',payload:{
			visible:true,
			isDoEdit: true
		}});
	}

	//根据关键字搜索数据
    const searchList = ()=>{
        gis.searchPoints(inputVal).then((data)=>{
			if(data.pois.length==0){
				message.warn('没有搜索结果');
				return;
			}
			let list = data.pois.map((r)=>({
				id: r.uid,
				longitude: r.point.lng,
				latitude: r.point.lat,
				canShowLabel: true,
				config: {
					labelContent: r.title,
					labelPixelY: 27
				},
				other: 'search'
			}))
			dispatch({type:'workElementGis/updateState',payload:{
				mapPoints2:list
			}})
			gis.state.gis.setFitview(list.map((item)=>item.id));
		})
    }

	return (
		<div className={style.main}>
			<div className={style.fullMap}>
				<Map 
					ref={(map)=>{if(map)gis = map;}}
					mapId={'map1'}
					mapPoints={mapPoints2}
					mapLines={mapLines2}
					mapPolygons={mapPolygons2}
					mapCircles={mapCircles2}
					setCenter={setCenter}
					mapCenter={mapCenter}

					// setZoomLevel={"true"}
					mapZoomLevel={13}
					setVisiblePoints={setVisiblePoints}
					mapVisiblePoints={{fitView:'all',type:'all'}}

					clickGraphic={(obj)=>{
						dispatch({type:'workElementGis/updateState',payload:{setCenter:false}});
						getWorkElementDel(obj.attributes.id);
						dispatch({type:'workElementGis/updateState',payload:{mapPanelPosition:setPanelPosi(obj)}});
					}}

					isDoEdit={isDoEdit2}
					isEndEdit={isEndEdit}
					editGraphicId={editGraphicId2}
					editGraphicChange={(obj)=>{
						saveMapData(obj);
					}}

					isDraw={isDraw}
					mapDraw={mapDraw}
					drawEnd={(obj)=>{
						dispatch({type:'workElementGis/updateState',payload:{
							editGraphicId2:obj.attributes.id,
							isDoEdit2: true,
							isEndEdit: false
						}});
						saveMapData(obj);
					}}
					isRemove = {isRemove}
					mapRemove={mapRemove}
				/>
			</div>

			<div className={style.operate_search_panel}>
				<div><label>图元名称：</label><Input value={searchName} placeholder={"请输入图元名称"} onChange={(e)=>{
	                    dispatch({type:'workElementGis/updateState',payload:{searchName:e.target.value}});
	                }}/></div>
				<div><label>图元类型：</label><Select value={searchWorkElementTypeId} onSelect={(value, option)=>{
	                	dispatch({type:'workElementGis/updateState',payload:{searchWorkElementTypeId:value}});
	                }} style={{ width: 180 }}>
	                	<Select.Option value="" name="searchWorkElementTypeId">请选择---</Select.Option>
						{workElementTypeList.map((element)=>{return <Select.Option value={element.id} name="searchWorkElementTypeId">{element.name}</Select.Option>;})}
					</Select></div>
				<Button onClick={()=>{
					dispatch({type:"workElementGis/clearInitMap"});
					dispatch({type:'workElementGis/updateState',payload:{
						initWorkElementKeys:[],
						checkedKeys_elem:[],
						orgWorkElementTree: []
					}});
					dispatch({type:'workElementGis/getOrgWorkElementTree'});
					treeObj.filtrateTree(searchName);
				}}>查询</Button>
			</div>
			<div className={style.operate_tree_panel}>
				<OrgWorkElementTree ref={(tree)=>{
					if(tree){
						treeObj=tree.treeObj;//console.log(tree);
					}
				}} {...treeSetting}/>
			</div>
			
			{/* 图元信息气泡 */}
			<div className={style.map_info_panel} style={mapPanelPosition}>
				<div className={style.map_info_head}>
					<span className={style.map_info_title}>图元信息</span>
					<span className={style.map_info_closeBtn} onClick={()=>{dispatch({type:'workElementGis/updateState',payload:{mapPanelPosition:{display:"none"}}});}}>X</span>
				</div>
				<div className={style.map_info_content}>
					<div>
						<label>编号：</label>
						<span title="">{newItem.code}</span>
					</div>
					<div>
						<label>名称：</label>
						<span title="">{newItem.name}</span>
					</div>
					<div>
						<label>图元类型：</label>
						<span title="">{newItem.workElementTypeName}</span>
					</div>
					<div>
						<label>所属机构：</label>
						<span title="">{newItem.departmentName}</span>
					</div>
					<div>
						<label>所属行政区划：</label>
						<span title="">{newItem.divisionName}</span>
					</div>
					{
						(newItem.shape == 'line' || newItem.shape == 'polyline')?
						[
							<div>
								<label>长度：</label>
								<span title="">{newItem.length}m</span>
							</div>
						]:[]
					}
					{
						(newItem.shape == 'polygon'|| newItem.shape == 'rectangle')?
						[
							<div>
								<label>面积：</label>
								<span title="">{newItem.area}㎡</span>
							</div>
						]:[]
					}
					{
						newItem.shape == 'circle'?
						[
							<div>
								<label>半径：</label>
								<span title="">{newItem.radius}m</span>
							</div>
						]:[]
					}
				</div>
				<div className={style.map_info_opBtn}>
					<Button type="button" onClick={showEditWindow}>修改</Button>
					<Button type="button" onClick={deleteWorkElement}>删除</Button>
					<Button type="button" onClick={editWorkElement}>调整图元</Button>
				</div>
			</div>

			{/* 操作按钮 */}
			<div className={style.opBtnGroup}>
				{
					isDoEditForUpdate?
					[
						<span>
							<Button onClick={updateWorkElement}>保存</Button>
							<Button onClick={cancleUpdateWorkElement}>取消</Button>
						</span>
					]:[]
				}
				<Button style={{color:'#FFF',background:'rgb(43, 144, 43)'}} onClick={()=>{
					dispatch({type:'workElementGis/clearNewItem'});
					dispatch({type:'workElementGis/getOrgTreeData'});
					dispatch({type:'workElementGis/getDivisionTreeData'});
					dispatch({type:'workElementGis/updateState',payload:{addPanelDisplay:{display:'block'}}});
				}}>新增</Button>
				<Input value={inputVal} style={{width:'150px','margin-left':'15px'}} onChange={(e)=>{
					dispatch({type:'workElementGis/updateState',payload:{inputVal:e.target.value}});
				}}/>
				<Button onClick={searchList} style={{color:'#FFF',background:'#f00'}}>查询</Button>
			</div>

			{/* 新增图元面板 */}
			<div className={style.addPanel} style={addPanelDisplay}>
				<div className={style.header}>
					<span>新增图元</span>
					<Icon type='close' style={{color:'#fff',position:'absolute',right:'15px',top:'12px',cursor:'pointer'}}
						onClick={()=>{
							dispatch({type:'workElementGis/updateState',payload:{
								addPanelDisplay:{display:'none'},
								isDoEdit2: false,
								editGraphicId2:'',
								isDraw: false,
								isEndEdit: true,
								isRemove:true,
								mapRemove:[{id:'workE',type:'draw'}]
							}});
							setTimeout(()=>{
								dispatch({type:'workElementGis/updateState',payload:{isRemove:false}});
							},100);
						}}/>
				</div>
				<div className={style.content}>
					<div className={style.inputItem}>
						<label><span className={style.requireSpan}>*</span>编号：</label>
						<StateInput onChange={(e)=>{
                            let code=e.target.value;
                            updateItem({code});
                            dispatch({type:'workElementGis/validateCode',payload:{obj:code}});
                        }} value={newItem.code} errorMsg={codeUnique?'编码重复':(validate(['required','codeValidator'],newItem.code))} 
                        validated={codeUnique?false:(newItem.checkState?(validate(['required','codeValidator'],newItem.code)==''):true)}/>
					</div>
					<div className={style.inputItem}>
						<label><span className={style.requireSpan}>*</span>名称：</label>
						<StateInput onChange={(e)=>{
                            updateItem({name:e.target.value});
							dispatch({type:'workElementGis/validateName',payload:{obj:e.target.value}});
                        }} value={newItem.name} errorMsg={nameUnique?'名称重复':(validate(['required','nameValidator'],newItem.name))} 
                        validated={nameUnique?false:(newItem.checkState?(validate(['required','nameValidator'],newItem.name)==''):true)}/>
					</div>
					<div className={style.inputItem}>
						<label><span className={style.requireSpan}>*</span>图元类型：</label>
						<StateSelect onSelect={(value, option)=>{
                            updateItem({
								workElementTypeId: value,
								shape: option.props.shape
								});
                        }} style={{width:'180px'}} value={newItem.workElementTypeId} errorMsg={validate(['required'],newItem.workElementTypeId)} 
                        validated={newItem.checkState?(validate(['required'],newItem.workElementTypeId)==''):true}>
							{workElementTypeList.map((element)=>{return <Select.Option value={element.id} shape={element.shape}>{element.name}</Select.Option>;})}
						</StateSelect>
						<Button className={style.editWorkBtn} onClick={()=>{
							if(newItem.shape == ""){
                                  message.error("请选择图元类型；");
                            }else {
                                  {/* dispatch({type:'workElementGis/clearAllTY'}); */}
                                  const mapDrawConfig = {
                                        geometryType: newItem.shape,
                                        parameter: {},
                                        data: {id: 'workE'}
                                  };
                                  dispatch({type:'workElementGis/drawWorkE',payload:{mapDrawConfig:mapDrawConfig}});
                            }
						}}>编辑图元</Button>
					</div>
					<div className={style.inputItem}>
						<label><span className={style.requireSpan}>*</span>所属机构：</label>
						<VtxTreeSelect
							data={orgTreeData}
							value={newItem.departmentId?newItem.departmentId.split(','):[]}
							treeDefaultExpandAll={['-1']}
							required={newItem.checkState}
							style={{width:'180px'}}
							onChange={({allValue,allLabel,value,label})=>{
								updateItem({departmentId:value[0]});
							}}
						/>
					</div>
					<div className={style.inputItem}>
						<label><span className={style.requireSpan}>*</span>行政区划：</label>
						<VtxTreeSelect
							data={divisionTreeData}
							value={newItem.divisionId?newItem.divisionId.split(','):[]}
							treeDefaultExpandAll={['-1']}
							required={newItem.checkState}
							style={{width:'180px'}}
							onChange={({allValue,allLabel,value,label})=>{
								updateItem({divisionId:value[0]});
							}}
						/>
					</div>
					{
						(newItem.shape == 'line' || newItem.shape == 'polyline')?
						[
							<div className={style.inputItem}>
								<label>长度(m)：</label>
								{newItem.length}
							</div>
						]:[]
					}
					{
						(newItem.shape == 'polygon'|| newItem.shape == 'rectangle')?
						[
							<div className={style.inputItem}>
								<label>面积(㎡)：</label>
								{newItem.area}
							</div>
						]:[]
					}
					{
						newItem.shape == 'circle'?
						[
							<div className={style.inputItem}>
								<label>半径(m)：</label>
								{newItem.radius}
							</div>
						]:[]
					}
					
					
					<div className={style.inputItem}>
						<Button onClick={()=>{
							dispatch({type:'workElementGis/updateNewItem',payload:{checkState:true}});
							dispatch({type:'workElementGis/addWorkElement'});	
						}}>保存</Button>
						<Button onClick={()=>{
							dispatch({type:'workElementGis/updateState',payload:{
								addPanelDisplay:{display:'none'},
								isDoEdit2: false,
								editGraphicId2:'',
								isDraw: false,
								isEndEdit: true,
								isRemove:true,
								mapRemove:[{id:'workE',type:'draw'}]
							}});
							setTimeout(()=>{
								dispatch({type:'workElementGis/updateState',payload:{isRemove:false}});
							},100);
						}}>返回</Button>
					</div>
				</div>
			</div>
			<EditItem {...editItemProps}/>
		</div>
	)
}

export default connect(({workElementGis})=>workElementGis)(WorkElementGis);