import React from 'react';
import VtxModal from '../vtxCommon/vtxModal/VtxModal';
import StateInput from '../vtxCommon/VtxForm/stateInput';
import StateSelect from '../vtxCommon/VtxForm/stateSelect';
import { Input, Icon, Radio,Select,AutoComplete, Checkbox ,Button} from 'antd';
import styles from './item.less';
import {formValidation,compareValue} from '../../utils/toolFunctions';
import Map from '../vtxCommon/Map/Map';
import VtxDatePicker from '../vtxCommon/VtxDate/VtxDatePicker';
const RadioGroup = Radio.Group;
const Option = Select.Option;
const CheckboxGroup = Checkbox.Group;
import {validate} from '../regExpression';

const EditItem = (props)=>{
    const {modalProps,contentProps } = props;

    const {id,checkState,parentName,parentId,name,abbr,mapType,commonCode,level,levelText,lonLatStr,orderIndex,latitude,editGraphicId,
            latitudeDone,longitude,longitudeDone,startTime,checkOrder,showMap,mapPoints,centerPoint,mapLines,treeName,
            isDraw,isClearAll,mapDraw,lngLats,tenantId,accountUnique,mapId} = contentProps;
    const {updateItem,updateState,updateWindowMapInfo,validateAccount} = contentProps;
    const getPoints = ()=>{
        if(latitude  && longitude ){
            updateState({mapCenter:[longitude,latitude]});
        }
        updateState({modal1Visible: true});
    }
    function disabledDate(current) {
      return current && current.valueOf() > Date.now();
    }
    
    return (
        <VtxModal {...modalProps}>
            <div className={styles.formWrapper}>
                <div className={styles.formRow}>
                    <div className={styles.halfLayout}>
                        <div className={styles.requiredLabel}>
                            上级区划：
                        </div>
                        {treeName}
                    </div>
                    <div className={styles.halfLayout}>
                        <div className={styles.requiredLabel}>
                            区划名称：
                        </div>
                        <StateInput onChange={(e)=>{
                            updateItem({name: e.target.value}); 
                        }} value={name} errorMsg={validate(['required','nameValidator'],name)} 
                        validated={checkState?(validate(['required','nameValidator'],name)==''):true} />
                    </div>
                </div>
                <div className={styles.formRow}>
                    <div className={styles.halfLayout}>
                        <div className={styles.requiredLabel}>
                            简称：
                        </div>
                         <StateInput onChange={(e)=>{
                            updateItem({abbr: e.target.value}); 
                        }} value={abbr} errorMsg={validate(['required','nameValidator'],abbr)} 
                        validated={checkState?(validate(['required','nameValidator'],abbr)==''):true} />
                    </div>
                    <div className={styles.halfLayout}>
                        <div className={styles.requiredLabel}>
                            数字代码：
                        </div>
                        <StateInput onChange={(e)=>{
                            let commonCode=e.target.value;
                            updateItem({commonCode});
                            validateAccount({commonCode,id}); 
                        }} value={commonCode} errorMsg={accountUnique && commonCode?'数字代码重复':(validate(['required','number'],commonCode))} 
                        validated={accountUnique?false:(checkState?(validate(['required','number'],commonCode)==''):true)} />
                    </div>
                </div>
                <div className={styles.formRow}>
                    <div className={styles.halfLayout}>
                        <div className={styles.requiredLabel}>
                            行政级别：
                        </div>
                       <StateSelect onChange={(value,label)=>{
                            updateItem({'level': value}); 
                        }} value={level} errorMsg={validate(['required'],level)} 
                        validated={checkState?(validate(['required',],level)==''):true} >
                            <Option value={1} key="1" label='省'>省</Option>
                            <Option value={2} key="2" label='市'>市</Option>
                            <Option value={3} key="3" label='区/县'>区/县</Option>
                            <Option value={4} key="4" label='乡镇/街道'>乡镇/街道</Option>
                            <Option value={5} key="5" label='居委会'>居委会</Option>
                        </StateSelect>
                    </div>
                    <div className={styles.halfLayout}>
                        <div className={styles.requiredLabel}>
                            生效日期：
                        </div>
                        <VtxDatePicker value={startTime} style={{width:"300px"}}
                            showToday={true} format="YYYY-MM-DD" onChange={(data,dateString)=>{
                                updateItem({startTime: dateString}); 
                           }}
                        /> 
                    </div>
                </div>
                <div className={styles.formRow}>
                    <div className={styles.halfLayout}>
                        <div className={styles.label}>
                            排序号：
                        </div>
                        <StateInput onChange={(e)=>{
                            updateItem({orderIndex: e.target.value,checkOrder:true}); 
                        }} value={orderIndex} errorMsg={validate(['empty','number'],orderIndex)}
                        validated={(id?checkOrder:checkState)?(validate(['empty','number'],orderIndex)=='') :true}/>
                    </div>
                </div>
                <div className={styles.formRow +' '+styles.pointBox}>
                    <div className={styles.halfLayout}>
                        <div className={styles.label}>
                            中心点经度：
                        </div>
                        <StateInput disabled={true} value={longitude} className={styles.lalt}/>
                    </div>
                    <div className={styles.halfLayout}>
                        <div className={styles.label}>
                            中心点纬度：
                        </div>
                        <StateInput disabled={true} value={latitude} className={styles.lalt}/>
                    </div>
                    <Button onClick={getPoints} className={styles.getPoints} type="primary">定位</Button>
                </div>
                {
                    showMap?
                    [<div className={styles.map}>
                        <Map
                            mapId={mapId}
                            setCenter={true}
                            mapCenter={centerPoint}
                            mapPoints={mapPoints}
                            mapVisiblePoints={{fitView:'point',type:'all'}}
                        />
                    </div>]
                    :['']
                }
            </div>
            
        </VtxModal>
    )
}

export default EditItem;