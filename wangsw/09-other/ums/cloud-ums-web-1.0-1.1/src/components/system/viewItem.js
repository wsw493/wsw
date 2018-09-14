import React from 'react';
import VtxModal from '../vtxCommon/vtxModal/VtxModal';
import styles from './item.less';
import Map from '../vtxCommon/Map/Map';

const ViewItem = (props)=>{
    const {modalProps,contentProps } = props;

    const {id,checkState,systemCode,systemName,userName,password,confirm_password,
            mapType,mapStr,website,longitudeDone,longitude,latitudeDone,welcomePage,
            latitude,unique,mapPoints,centerPoint,showMap,orderIndex} = contentProps;

    return (
        <VtxModal {...modalProps}>
            <div className={styles.formWrapper}>
                <div className={styles.formRow}>
                    <div className={styles.halfLayout}>
                        <div className={styles.requiredLabel}>
                            系统编码：
                        </div>
                        {systemCode}
                    </div>
                    <div className={styles.halfLayout}>
                        <div className={styles.requiredLabel}>
                            系统名称：
                        </div>
                        {systemName}
                    </div>
                </div>
                <div className={styles.formRow}>
                    <div className={styles.halfLayout}>
                        <div className={styles.requiredLabel}>
                            website：
                        </div>
                       {website}
                    </div>
                    <div className={styles.halfLayout}>
                        <div className={styles.label}>
                            欢迎页地址：
                        </div>
                       {welcomePage}
                    </div>
                </div>
                <div className={styles.formRow}>
                    <div className={styles.halfLayout}>
                        <div className={styles.requiredLabel}>
                            地图类型：
                        </div>
                        {mapType}
                    </div>
                    <div className={styles.halfLayout}>
                        <div className={styles.label}>
                            地图配置字符串：
                        </div>
                        {mapStr}
                    </div>
                </div>
                <div className={styles.formRow}>
                    <div className={styles.halfLayout}>
                        <div className={styles.label}>
                            排序号：
                        </div>
                        {orderIndex}
                    </div>
                </div>
                <div className={styles.formRow}>
                    <div className={styles.halfLayout}>
                        <div className={styles.label}>
                            经度：
                        </div>
                        {longitudeDone}
                    </div>
                    <div className={styles.halfLayout}>
                        <div className={styles.label}>
                            纬度：
                        </div>
                        {latitudeDone}
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