import React from 'react';
import {Button,Checkbox,Radio} from 'antd';
import styles from './item.less';
import {VtxMap} from 'vtx-ui';
import VtxModal from '../vtxCommon/vtxModal/VtxModal';

const ViewItem = (props)=>{
    const {modalProps,contentProps } = props;

    const {id,checkState,tenantCode,tenantName,divisionName,menuUrl,navigationUrl,
            divisionId,domain,contact,phone,email,longitudeDone,longitude,latitudeDone,
            latitude,unique,mapPoints,centerPoint,showMap,checkBoxStatus,radioStatus,basicData,coverage,coordinate,wkid,minZoom,maxZoom} = contentProps;

    return (
        <VtxModal {...modalProps}>
            <div className={styles.formWrapper}>
                <div className={styles.formRow}>
                    <div className={styles.halfLayout}>
                        <div className={styles.requiredLabel}>
                            编码：
                        </div>
                        {tenantCode}
                    </div>
                    <div className={styles.halfLayout}>
                        <div className={styles.requiredLabel}>
                            名称：
                        </div>
                        {tenantName}
                    </div>
                </div>
                <div className={styles.formRow}>
                    <div className={styles.halfLayout}>
                        <div className={styles.requiredLabel}>
                            行政区划节点：
                        </div>
                        {divisionName}
                    </div>
                </div>
                <div className={styles.formRow}>
                    <div className={styles.halfLayout}>
                        <div className={styles.label}>
                            域名(需指向云平台)：
                        </div>
                        {domain}
                    </div>
                    <div className={styles.halfLayout}>
                        <div className={styles.label}>
                            联系人：
                        </div>
                        {contact}
                    </div>
                </div>
                <div className={styles.formRow}>
                    <div className={styles.halfLayout}>
                        <div className={styles.label}>
                            菜单地址：
                        </div>
                        {menuUrl}
                    </div>
                    <div className={styles.halfLayout}>
                        <div className={styles.label}>
                            导航地址：
                        </div>
                        {navigationUrl}
                    </div>
                </div>
                <div className={styles.formRow}>
                    <div className={styles.halfLayout}>
                        <div className={styles.label}>
                            联系人电话：
                        </div>
                        {phone}
                    </div>
                    <div className={styles.halfLayout}>
                        <div className={styles.label}>
                            Email：
                        </div>
                        {email}
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
                                                <Checkbox checked={checkBoxStatus&&checkBoxStatus[item.mapType]} disabled={true}/>
                                            </span>
                                            <span className={styles.td}>{item.mapName}</span>
                                            <span className={styles.td}>
                                                <Radio disabled={true} checked={radioStatus&&radioStatus[item.mapType]}/>
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
                                    {coordinate}
                                </div>
                                <div className={styles.item}>
                                    <span><span style={{color: '#FF0000'}}>*</span>图层：</span>
                                    {coverage}
                                </div>
                                <div className={styles.item}>
                                    <span><span style={{color: '#FF0000'}}>*</span>基础信息：</span>
                                    {basicData}
                                </div>
                                <div className={styles.item}>
                                    <span><span style={{color: '#FF0000'}}>*</span>wkid：</span>
                                    {wkid}
                                </div>
                                <div className={styles.item}>
                                    <span><span style={{color: '#FF0000'}}>*</span>minZoom：</span>
                                    {minZoom}
                                </div>
                                <div className={styles.item}>
                                    <span><span style={{color: '#FF0000'}}>*</span>maxZoom：</span>
                                    {maxZoom}
                                </div>
                            </div>
                        :[]
                    }
                </div>
                <div className={styles.formRow}>
                    <div className={styles.halfLayout}>
                        <div className={styles.label}>
                            经度：
                        </div>
                        {longitude}
                    </div>
                    <div className={styles.halfLayout}>
                        <div className={styles.label}>
                            纬度：
                        </div>
                        {latitude}
                    </div>
                </div>
                {
                    showMap?
                    [<div className={styles.map}>
                        <VtxMap
                            mapId={'mapView'}
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

export default ViewItem;