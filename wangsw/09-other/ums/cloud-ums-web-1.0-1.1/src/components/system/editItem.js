import React from 'react';
import VtxModal from '../vtxCommon/vtxModal/VtxModal';
import StateInput from '../vtxCommon/VtxForm/stateInput';
import StateSelect from '../vtxCommon/VtxForm/stateSelect';
import { Input, Select ,Button} from 'antd';
import styles from './item.less';
import {formValidation} from '../../utils/toolFunctions';
import Map from '../vtxCommon/Map/Map';

const Option = Select.Option;
import {validate} from '../regExpression';

const EditItem = (props)=>{
    const {modalProps,contentProps } = props;

    const {id,checkState,systemCode,systemName,userName,password,confirm_password,
            mapType,mapStr,website,longitudeDone,longitude,latitudeDone,welcomePage,
            latitude,mapPoints,centerPoint,showMap,codeUnique,nameUnique,passwordFlag,orderIndex} = contentProps;
    const {updateItem, isNewItem,validateCode,updateState,validateAccount} = contentProps;

    const getPoints = ()=>{
        if(latitude && longitude){
            updateState({mapCenter:[longitude,latitude]});
        }
        updateState({modal1Visible: true});
    }
    return (
        <VtxModal {...modalProps}>
            <div className={styles.formWrapper}>
                <div className={styles.formRow}>
                    <div className={styles.halfLayout}>
                        <div className={styles.requiredLabel}>
                            系统编码：
                        </div>
                        {
                            isNewItem?[
                                <StateInput onChange={(e)=>{
                                    let systemCode=e.target.value;
                                    updateItem({systemCode});
                                    validateCode({key:'systemCode',systemCode,id}); 
                                }} value={systemCode} errorMsg={codeUnique?'编码重复':(validate(['required','codeValidator'],systemCode))} 
                                validated={codeUnique?false:(checkState?(validate(['required','codeValidator'],systemCode)==''):true)} />
                            ]:[systemCode]
                        }
                        
                    </div>
                    <div className={styles.halfLayout}>
                        <div className={styles.requiredLabel}>
                            系统名称：
                        </div>
                        <StateInput onChange={(e)=>{
                            updateItem({systemName: e.target.value}); 
                        }} value={systemName} errorMsg={validate(['required','nameValidator'],systemName)} 
                        validated={checkState?(validate(['required','nameValidator'],systemName)==''):true} />
                    </div>
                </div>
                <div className={styles.formRow}>
                    <div className={styles.halfLayout}>
                        <div className={styles.requiredLabel}>
                            website：
                        </div>
                        <StateInput onChange={(e)=>{
                            let website=e.target.value;
                            updateItem({website});
                        }} value={website} errorMsg={validate(['required'],website)} 
                        validated={checkState?(validate(['required'],website)==''):true} />
                    </div>
                    <div className={styles.halfLayout}>
                        <div className={styles.label}>
                            欢迎页地址：
                        </div>
                        <StateInput onChange={(e)=>{
                            let welcomePage=e.target.value;
                            updateItem({welcomePage});
                        }} value={welcomePage} />
                    </div>
                </div>
                {
                    isNewItem?[
                        <div className={styles.formRow}>
                            <div className={styles.halfLayout}>
                                <div className={styles.requiredLabel}>
                                    用户名：
                                </div>
                                <StateInput onChange={(e)=>{
                                    updateItem({userName: e.target.value}); 
                                    validateAccount({userName:e.target.value,id});
                                }} value={userName} errorMsg={nameUnique?'用户名重复':validate(['required','nameValidator'],userName)} 
                                validated={nameUnique?false:(checkState?(validate(['required','nameValidator'],userName)==''):true)} />
                            </div>
                        </div>
                    ]:['']
                }
                {
                    isNewItem?[
                        <div className={styles.formRow}>
                            <div className={styles.halfLayout}>
                                <div className={styles.requiredLabel}>
                                    密码：
                                </div>
                                <StateInput type='password' onChange={(e)=>{
                                    updateItem({password: e.target.value});
                                    updateState({passwordFlag:e.target.value != confirm_password &&confirm_password });
                                }} value={password} errorMsg={(validate(['required'],password))} 
                                validated={(checkState?(validate(['required'],password)==''):true)} />
                            </div>
                            <div className={styles.halfLayout}>
                                <div className={styles.requiredLabel}>
                                    确认密码：
                                </div>
                                <StateInput onChange={(e)=>{
                                    updateItem({confirm_password: e.target.value}); 
                                    updateState({passwordFlag:e.target.value != password});
                                }} type='password' value={confirm_password} errorMsg={(passwordFlag&&password)?'密码不一致':validate(['required'],confirm_password)} 
                                validated={(passwordFlag&&password)?false:(checkState?(validate(['required'],confirm_password)==''):true)} />
                            </div>
                        </div>
                    ]:['']
                }
                <div className={styles.formRow}>
                    <div className={styles.halfLayout} style={{height:'46px'}}>
                        <div className={styles.requiredLabel}>
                            地图类型：
                        </div>
                        <StateSelect  onSelect={(value,option)=>{
                            updateItem({mapType: value}); 
                        }} value={mapType} errorMsg={validate(['required'],mapType)} 
                        validated={checkState?(validate(['required'],mapType)==''):true} >
                        {
                            ManagementConstant.VortexMapType.map((item)=>{
                                return <Option value={item.value}>{item.text}</Option>
                            })
                        }
                        </StateSelect>
                    </div>
                    <div className={styles.halfLayout}>
                        <div className={styles.label}>
                            地图配置字符串：
                        </div>
                        <StateInput onChange={(e)=>{
                            updateItem({mapStr: e.target.value}); 
                        }} value={mapStr}/>
                    </div>
                </div>
                <div className={styles.formRow}>
                    <div className={styles.halfLayout}>
                        <div className={styles.label}>
                            排序号：
                        </div>
                        <StateInput onChange={(e)=>{
                            updateItem({orderIndex: e.target.value}); 
                        }} value={orderIndex} errorMsg={validate(['number'],orderIndex)} 
                        validated={checkState?(validate(['number'],orderIndex)==''):true}/>
                    </div>
                </div>
                <div className={styles.formRow +' '+styles.pointBox}>
                    <div className={styles.halfLayout}>
                        <div className={styles.label}>
                            经度：
                        </div>
                        <StateInput disabled={true} value={longitude} className={styles.lalt}/>
                    </div>
                    <div className={styles.halfLayout}>
                        <div className={styles.label}>
                            纬度：
                        </div>
                        <StateInput disabled={true} value={latitude} className={styles.lalt}/>
                    </div>
                    <Button onClick={getPoints} type="primary" className={styles.getPoints}>定位</Button>
                </div>
                {
                    showMap?
                    [<div className={styles.map}>
                        <Map
                            mapId={id?'mapUpdate':'mapNew'}
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