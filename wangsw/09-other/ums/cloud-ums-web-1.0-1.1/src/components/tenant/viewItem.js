import React from 'react';
import {Button} from 'antd';
import styles from './item.less';
import Map from '../vtxCommon/Map/Map';
import VtxModal from '../vtxCommon/vtxModal/VtxModal';

const ViewItem = (props)=>{
    const {modalProps,contentProps } = props;

    const {id,checkState,tenantCode,tenantName,divisionName,menuUrl,navigationUrl,
            divisionId,domain,contact,phone,email,longitudeDone,longitude,latitudeDone,
            latitude,unique,mapPoints,centerPoint,showMap} = contentProps;

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
                        <Map
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