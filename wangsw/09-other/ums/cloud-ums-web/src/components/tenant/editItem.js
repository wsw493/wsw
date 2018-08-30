import React from 'react';
import VtxModal from '../vtxCommon/vtxModal/VtxModal';
import StateInput from '../vtxCommon/VtxForm/stateInput';
import StateSelect from '../vtxCommon/VtxForm/stateSelect';
import { Input, Icon, Radio,Select,AutoComplete, Checkbox ,Button} from 'antd';
import styles from './item.less';
import {formValidation,compareValue} from '../../utils/toolFunctions';
// import {VtxMap} from 'vtx-ui';
import {VtxMap} from 'vtx-ui';
import VtxTreeSelect from '../vtxCommon/VtxTreeSelect/VtxTreeSelect';

const RadioGroup = Radio.Group;
const Option = Select.Option;
const CheckboxGroup = Checkbox.Group;
import {validate} from '../regExpression';

const EditItem = (props)=>{
    const {modalProps,contentProps } = props;

    const {id,checkState,tenantCode,tenantName,userName,password,confirm_password,
            divisionId,domain,contact,phone,email,longitudeDone,longitude,latitudeDone,
            latitude,mapPoints,centerPoint,showMap,codeUnique,nameUnique,domainUnique,mapType,
            menuUrl,navigationUrl,divisionName,checkBoxStatus,radioStatus,basicData,coverage,coordinate,wkid,minZoom,maxZoom} = contentProps;
    const {updateObj,divisionTreeLoadBack,updateItem, isNewItem,validateTenant,updateState,validateAccount,passwordFlag,divisionTree} = contentProps;
    
    const getPoints = ()=>{
        if(latitude  && longitude ){
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
                            编码：
                        </div>
                        <StateInput onChange={(e)=>{
                            let tenantCode=e.target.value;
                            updateItem({tenantCode});
                            validateTenant({key:'tenantCode',tenantCode,id}); 
                        }} value={tenantCode} errorMsg={codeUnique?'编码重复':(validate(['required','codeValidator'],tenantCode))} 
                        validated={codeUnique?false:(checkState?(validate(['required','codeValidator'],tenantCode)==''):true)} />
                    </div>
                    <div className={styles.halfLayout}>
                        <div className={styles.requiredLabel}>
                            名称：
                        </div>
                        <StateInput onChange={(e)=>{
                            updateItem({tenantName: e.target.value}); 
                        }} value={tenantName} errorMsg={validate(['required','nameValidator'],tenantName)} 
                        validated={checkState?(validate(['required','nameValidator'],tenantName)==''):true} />
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
                                    var userName=e.target.value;
                                    updateItem({userName}); 
                                    validateAccount({key:'name',userName,id});
                                }} value={userName} errorMsg={nameUnique?'用户名重复':(validate(['required','nameValidator'],userName))} 
                                validated={nameUnique?false:(checkState?(validate(['required','nameValidator'],userName)==''):true)} />
                            </div>
                            <div className={styles.halfLayout}>
                                <div className={styles.requiredLabel}>
                                    密码：
                                </div>
                                <StateInput type='password' onChange={(e)=>{
                                    updateItem({password: e.target.value});
                                    updateState({passwordFlag: e.target.value!=confirm_password && confirm_password});
                                }} value={password} errorMsg={(validate(['required'],password))} 
                                validated={(checkState?(validate(['required'],password)==''):true)} />
                            </div>
                        </div>
                    ]:['']
                }
                <div className={styles.formRow}>
                    {
                        isNewItem?[
                            <div className={styles.halfLayout}>
                                <div className={styles.requiredLabel}>
                                    确认密码：
                                </div>
                                <StateInput type='password' onChange={(e)=>{
                                    updateItem({confirm_password: e.target.value});
                                    updateState({passwordFlag: e.target.value!=password});
                                }} value={confirm_password} errorMsg={(passwordFlag&&password)?'密码不一致':validate(['required'],confirm_password)} 
                                validated={(passwordFlag&&password)?false:(checkState?(validate(['required'],confirm_password)==''):true)} />
                            </div>
                        ]:['']
                    }
                    {isNewItem?
                        [<div className={styles.halfLayout} style={{height:'46px'}}>
                            <div className={styles.requiredLabel}>
                                行政区划节点：
                            </div>
                            <div style={{display:'inline-block'}}>
                            <VtxTreeSelect
                                data={divisionTree}
                                value={divisionId.split()}
                                onChange={({allValue,allLabel,value,label})=>{
                                    updateItem({divisionId:value.join()});
                                }}
                                onLoadData={divisionTreeLoadBack}
                            />
                            </div>
                        </div>]:
                        [
                            <div className={styles.halfLayout} style={{height:'46px'}}>
                                <div className={styles.requiredLabel}>
                                    行政区划节点：
                                </div>
                                {divisionName}
                            </div>
                        ]
                    }
                </div>
                <div className={styles.formRow}>
                    <div className={styles.halfLayout}>
                        <div className={styles.label}>
                            登陆地址：
                        </div>
                        <StateInput onChange={(e)=>{
                            updateItem({domain: e.target.value});
                            validateTenant({key:'domain',domain,id});
                        }} value={domain} errorMsg={domainUnique?'域名重复':''} 
                        validated={domainUnique?false:true} />
                    </div>
                    <div className={styles.halfLayout}>
                        <div className={styles.label}>
                            联系人：
                        </div>
                        <StateInput onChange={(e)=>{
                            updateItem({contact: e.target.value}); 
                        }} value={contact}/>
                    </div>
                </div>
                <div className={styles.formRow}>
                    <div className={styles.halfLayout}>
                        <div className={styles.label}>
                            菜单地址：
                        </div>
                        <StateInput onChange={(e)=>{
                            updateItem({menuUrl: e.target.value}); 
                        }} value={menuUrl}/>
                    </div>
                    <div className={styles.halfLayout}>
                        <div className={styles.label}>
                            导航地址：
                        </div>
                        <StateInput onChange={(e)=>{
                            updateItem({navigationUrl: e.target.value}); 
                        }} value={navigationUrl}/>
                    </div>
                </div>
                <div className={styles.formRow}>
                    <div className={styles.halfLayout}>
                        <div className={styles.label}>
                            联系人电话：
                        </div>
                        <StateInput onChange={(e)=>{
                            updateItem({phone: e.target.value}); 
                        }} value={phone} errorMsg={validate(['phone'],phone)} 
                        validated={checkState?(validate(['phone'],phone)==''):true} />
                    </div>
                    <div className={styles.halfLayout}>
                        <div className={styles.label}>
                            Email：
                        </div>
                        <StateInput onChange={(e)=>{
                            updateItem({email: e.target.value});
                        }} value={email} errorMsg={validate(['email'],email)} 
                        validated={checkState?(validate(['email'],email)==''):true} />
                    </div>
                </div>
                <div className={styles.mapArgs}>
                    <div className={styles.tTb}>
                        <div className={styles.tHead}>
                            <span className={styles.ck}></span>
                            <span className={styles.th}>地图类型</span>
                            <span className={styles.th}>默认类型</span>
                        </div>
                        <div className={styles.tBody}>
                            {
                                MapType.map((item,index)=>{
                                    return (
                                        <div className={styles.tr} key={index}>
                                            <span className={styles.ck}>
                                                <Checkbox checked={checkBoxStatus&&checkBoxStatus[item.mapType]} disabled={item.mapType=='bmap'} onChange={(e)=>{
                                                    updateObj({key:'checkBoxStatus',data:{[item.mapType]: e.target.checked}})
                                                    if(!e.target.checked){
                                                        updateState({radioStatus: {bmap:true}}) 
                                                    }
                                                }} />
                                            </span>
                                            <span className={styles.td}>{item.mapName}</span>
                                            <span className={styles.td}>
                                                <Radio disabled={!(checkBoxStatus&&checkBoxStatus[item.mapType])} checked={radioStatus&&radioStatus[item.mapType]} onChange={(e)=>{
                                                    updateState({radioStatus: {[item.mapType]:true}})  
                                                }}/>
                                            </span>
                                        </div>
                                    )
                                })
                            }
                        </div>
                    </div>
                    {
                        checkBoxStatus&&checkBoxStatus['gmap']?
                            <div className={styles.inputBox}>
                                <div className={styles.item}>
                                    <span><span style={{color: '#FF0000'}}>*</span>坐标系：</span>
                                    <StateInput value={coordinate} onChange={(e)=>{
                                        updateObj({key: 'arcgisInfo',data:{coordinate:e.target.value}})
                                    }} errorMsg={validate(['required'],coordinate)} 
                                    validated={checkState?(validate(['required'],coordinate)==''):true} />
                                </div>
                                <div className={styles.item}>
                                    <span><span style={{color: '#FF0000'}}>*</span>图层：</span>
                                    <StateInput value={coverage} onChange={(e)=>{
                                        updateObj({key: 'arcgisInfo',data:{coverage:e.target.value}})
                                    }} errorMsg={validate(['required'],coverage)} 
                                    validated={checkState?(validate(['required'],coverage)==''):true} />
                                </div>
                                <div className={styles.item}>
                                    <span><span style={{color: '#FF0000'}}>*</span>基础信息：</span>
                                    <StateInput value={basicData} onChange={(e)=>{
                                        updateObj({key: 'arcgisInfo',data:{basicData:e.target.value}})
                                    }} errorMsg={validate(['required'],basicData)} 
                                    validated={checkState?(validate(['required'],basicData)==''):true} />
                                </div>
                                <div className={styles.item}>
                                    <span><span style={{color: '#FF0000'}}>*</span>wkid：</span>
                                    <StateInput value={wkid} onChange={(e)=>{
                                        updateObj({key: 'arcgisInfo',data:{wkid:e.target.value}})
                                    }} errorMsg={validate(['required'],wkid)} 
                                    validated={checkState?(validate(['required'],wkid)==''):true} />
                                </div>
                                <div className={styles.item}>
                                    <span><span style={{color: '#FF0000'}}>*</span>minZoom：</span>
                                    <StateInput value={minZoom} onChange={(e)=>{
                                        updateObj({key: 'arcgisInfo',data:{minZoom:e.target.value}})
                                    }} errorMsg={validate(['required'],minZoom)} 
                                    validated={checkState?(validate(['required'],minZoom)==''):true} />
                                </div>
                                <div className={styles.item}>
                                    <span><span style={{color: '#FF0000'}}>*</span>maxZoom：</span>
                                    <StateInput value={maxZoom} onChange={(e)=>{
                                        updateObj({key: 'arcgisInfo',data:{maxZoom:e.target.value}})
                                    }} errorMsg={validate(['required'],maxZoom)} 
                                    validated={checkState?(validate(['required'],maxZoom)==''):true} />
                                </div>
                            </div>
                        :[]
                    }
                    
                </div>
                <div className={styles.formRow +' '+styles.pointBox}>
                    <div className={styles.halfLayout}>
                        <div className={styles.label}>
                            经度：
                        </div>
                        <StateInput disabled={true} value={longitude} style={{width:"230px"}}/>
                    </div>
                    <div className={styles.halfLayout}>
                        <div className={styles.label}>
                            纬度：
                        </div>
                        <StateInput disabled={true} value={latitude} style={{width:"230px"}}/>
                    </div>
                    <Button onClick={getPoints} className={styles.getPoints} type="primary">定位</Button>
                </div>
                {
                    showMap?
                    [<div className={styles.map}>
                        <VtxMap
                            mapId={id?'mapUpdate':'mapNew'}
                            setCenter={true}
                            mapCenter={centerPoint}
                            mapPoints={mapPoints}
                            mapVisiblePoints={{fitView:'point',type:'all'}}
                            mapType={mapType}
                        />
                    </div>]
                    :['']
                }
            </div>
            
        </VtxModal>
    )
}

export default EditItem;