import React from 'react';
import VtxModal from '../vtxCommon/vtxModal/VtxModal';
import StateInput from '../vtxCommon/VtxForm/stateInput';
import StateSelect from '../vtxCommon/VtxForm/stateSelect';
import { Input, Icon, Radio,Select,AutoComplete, Checkbox ,Button} from 'antd';
import styles from './item.less';
import Map from '../vtxCommon/Map/Map';

const EditItem = (props)=>{
    const {modalProps,contentProps } = props;

    const {checkState,parentName,parentId,name,abbr,mapType,commonCode,level,levelText,lonLatStr,orderIndex,latitude,editGraphicId,
            latitudeDone,longitude,longitudeDone,startTime,checkOrder,showMap,mapPoints,centerPoint,mapLines,treeName,
            isDraw,isClearAll,mapDraw,lngLats} = contentProps;

   /* const arr=[];
    arr.push(lngLats);
    const lon=arr.toString().split(',')[0];
    const lat=arr.toString().split(',')[1];*/
    var levelName = "";
    switch(level){
        case 1:
            levelName = "省";
            break;
        case 2:
            levelName = "市";
            break;
        case 3:
            levelName = "区/县";
            break;
        case 4:
            levelName = "乡镇/街道";
            break;
        case 5:
            levelName = "居委会";
            break;
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
                       {name}
                    </div>
                </div>
                <div className={styles.formRow}>
                    <div className={styles.halfLayout}>
                        <div className={styles.requiredLabel}>
                            简称：
                        </div>
                        {abbr}
                    </div>
                    <div className={styles.halfLayout}>
                        <div className={styles.requiredLabel}>
                            数字代码：
                        </div>
                        {commonCode}
                    </div>
                </div>
                <div className={styles.formRow}>
                    <div className={styles.halfLayout}>
                        <div className={styles.requiredLabel}>
                            行政级别：
                        </div>
                       {levelName}
                    </div>
                    <div className={styles.halfLayout}>
                        <div className={styles.requiredLabel}>
                            生效日期：
                        </div>
                        {startTime}
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
                            中心点经度：
                        </div>
                        {longitude}
                    </div>
                    <div className={styles.halfLayout}>
                        <div className={styles.label}>
                            中心点纬度：
                        </div>
                        {latitude}
                    </div>
                </div>
                {
                    lngLats?
                    [<div className={styles.map}>
                        <Map
                            mapId={'mapViewx'}
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