import React from 'react';
import VtxModal from '../vtxCommon/vtxModal/VtxModal';
import StateInput from '../vtxCommon/VtxForm/stateInput';
import StateSelect from '../vtxCommon/VtxForm/stateSelect';
import { Input, Icon, Radio,Select,AutoComplete, Checkbox ,Button ,message,TreeSelect} from 'antd';
import styles from './item.less';
import {formValidation} from '../../utils/toolFunctions';
import VtxTreeSelect from '../vtxCommon/VtxTreeSelect/VtxTreeSelect';
import {VtxMap} from 'vtx-ui';
const RadioGroup = Radio.Group;
const Option = Select.Option;
const CheckboxGroup = Checkbox.Group;
import {validate} from '../regExpression';

const EditItem = (props)=>{
    const {modalProps,contentProps } = props;

    const {id,checkState,tenantId,code,name,workElementTypeId,shape,departmentId,departmentName,divisionId,divisionName,mapType,area,
            length,radius,color,paramsDone,workElementTypeList,mapDraw,isDraw,mapPoints,codeUnique,nameUnique,
            mapLines,mapPolygons,mapCircles,editGraphicId,orgTreeData,divisionTreeData,isClearAll,isDoEdit} = contentProps;
    const {updateItem,validateName,validateCode,doEdit,updateState,dispatch} = contentProps;

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
                        positionStr = data.geometry.paths;
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
                  case 'polygon':
                        positionStr = data.geometry.rings;
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
                  case 'rectangle':
                        positionStr = data.geometry.rings;
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
    return (
        <VtxModal {...modalProps}>
            <div className={styles.formWrapper}>
                <div className={styles.formRow}>
                    <div className={styles.halfLayout}>
                        <div className={styles.requiredLabel}>
                            编号：
                        </div>
                        <StateInput onChange={(e)=>{
                            let code=e.target.value;
                            updateItem({code});
                            validateCode(code,id); 
                        }} value={code} errorMsg={codeUnique?'编码重复':(validate(['required','codeValidator'],code))} 
                        validated={codeUnique?false:(checkState?(validate(['required','codeValidator'],code)==''):true)} />
                    </div>
                    <div className={styles.halfLayout}>
                        <div className={styles.requiredLabel}>
                            名称：
                        </div>
                        <StateInput onChange={(e)=>{
                            let name=e.target.value;
                            updateItem({name});
                            validateName(name,id);
                        }} value={name} errorMsg={nameUnique?'名称重复':(validate(['required','nameValidator'],name))} 
                        validated={nameUnique?false:(checkState?(validate(['required','nameValidator'],name)==''):true)} />
                    </div>
                </div>
                <div className={styles.formRow}>
                   <div className={styles.halfLayout}>
                        <div className={styles.requiredLabel}>
                            图元类型：
                        </div>
                        <StateSelect onSelect={(value,option)=>{
                            updateItem({'workElementTypeId': value,shape:option.props.label}); 
                        }} value={workElementTypeId} errorMsg={validate(['required'],workElementTypeId)} 
                        validated={checkState?(validate(['required'],workElementTypeId)==''):true} >
                        {
                            workElementTypeList.map((item,index)=>{
                                return <Option value={item.id} key={index} label={item.shape}>{item.name}</Option>
                            })
                        }
                        </StateSelect>
                        <Button onClick={()=>{
                            if(shape == ""){
                                  message.error("请选择图元类型；");
                            }else {
                                  dispatch({type:'workElementGis/clearAllTY'});
                                  const mapDrawConfig = {
                                        geometryType: shape,
                                        parameter: {},
                                        data: {id: 'workE_'+shape}
                                  };
                                  dispatch({type:'workElementGis/drawWorkE2',payload:{mapDrawConfig:mapDrawConfig}});
                            }
                        }} style={{verticalAlign: 'top'}}>编辑图元</Button>
                    </div>
                    <div className={styles.halfLayout}>
                        <div className={styles.requiredLabel}>
                            所属机构：
                        </div>
                        <div className={styles.treeSelect}>
                            <VtxTreeSelect
                                data={orgTreeData}
                                value={departmentId?departmentId.split(','):[]}
                                treeDefaultExpandAll={true}
                                onChange={({allValue,allLabel,value,label})=>{
                                    updateItem({departmentId:value[0]});
                                }}
                            />
                        </div>
                    </div>
                </div>
                <div className={styles.formRow}>
                    <div className={styles.halfLayout}>
                        <div className={styles.requiredLabel}>
                            所属行政区划：
                        </div>
                        <div className={styles.treeSelect}>
                            <VtxTreeSelect
                                data={divisionTreeData}
                                value={divisionId?divisionId.split(','):[]}
                                treeDefaultExpandAll={true}
                                onChange={({allValue,allLabel,value,label})=>{
                                    updateItem({divisionId:value[0]});
                                }}
                            />
                        </div>
                    </div>
                </div>
                <div className={styles.map}>
                      <VtxMap 
                            mapId={'map2'}
                            mapPoints={mapPoints}
                            mapLines={mapLines}
                            mapPolygons={mapPolygons}
                            mapCircles={mapCircles}

                            isDraw={isDraw}
                            drawEnd={(obj)=>{
                                  updateState({editGraphicId:obj.id});
                                  saveMapData(obj);
                                  setTimeout(()=>{doEdit(obj)},1);
                            }}
                            mapDraw={mapDraw}

                            editGraphicId={editGraphicId}
                            editGraphicChange={(obj)=>{saveMapData(obj);}}
                            setVisiblePoints={true}
                            mapVisiblePoints={{fitView:'all',type:'all'}}

                            setZoomLevel={"true"}
                            // mapZoomLevel={13}

                            isClearAll={isClearAll}
                            isDoEdit={isDoEdit}
                            mapType={mapType}
                            />
                </div>
            </div>
            
        </VtxModal>
    )
}

export default EditItem;