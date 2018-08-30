import React from 'react';
import VtxModal from '../vtxCommon/vtxModal/VtxModal';
import { Input, Icon, Radio,Select,AutoComplete, Checkbox ,Button} from 'antd';
import styles from './item.less';
import {VtxMap} from 'vtx-ui';
const ViewItem = (props)=>{
    const {modalProps,contentProps } = props;

    const {name,code,orgName,description,workElementTypeName,shape,workElementTypeList,divisionName,isDraw,
    mapDraw,mapPoints,mapLines,mapPolygons,mapCircles,editGraphicId,orderIndex,departmentName,mapType,setVisiblePoints} = contentProps;
    return (
        <VtxModal {...modalProps}>
            <div className={styles.formWrapper}>
                <div className={styles.formRow}>
                    <div className={styles.halfLayout}>
                        <div className={styles.requiredLabel}>
                            编号：
                        </div>
                        {code}
                    </div>
                    <div className={styles.halfLayout}>
                        <div className={styles.requiredLabel}>
                            名称：
                        </div>
                        {name}
                    </div>
                </div>
                <div className={styles.formRow}>
                    <div className={styles.halfLayout}>
                        <div className={styles.requiredLabel}>
                            图元类型：
                        </div>
                        {workElementTypeName}
                    </div>
                    <div className={styles.halfLayout}>
                        <div className={styles.requiredLabel}>
                            所属机构：
                        </div>
                        {departmentName}
                    </div>
                </div>
                <div className={styles.formRow}>
                    <div className={styles.halfLayout}>
                        <div className={styles.requiredLabel}>
                            所属行政区划：
                        </div>
                        {divisionName}
                    </div>
                </div>
                <div className={styles.map}>
                    <VtxMap 
                        mapId={'mapView'}
                        mapPoints={mapPoints}
                        mapLines={mapLines}
                        mapPolygons={mapPolygons}
                        mapCircles={mapCircles}

                        setVisiblePoints={setVisiblePoints}
                        mapVisiblePoints={{fitView:'all',type:'all'}}
                        mapType={mapType}
                    />
                </div>
            </div>
            
        </VtxModal>
    )
}

export default ViewItem;