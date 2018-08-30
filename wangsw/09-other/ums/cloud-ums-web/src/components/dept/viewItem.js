import React from 'react';
import {Button} from 'antd';
import styles from './item.less';
import {VtxMap} from 'vtx-ui';
import VtxModal from '../vtxCommon/vtxModal/VtxModal';

const ViewItem = (props)=>{
    const {modalProps,contentProps } = props;

    const {id,checkState,depTypeText,depCode,depName,head,headMobile,address,email,description,divisionName,orderIndex} = contentProps;

    return (
        <VtxModal {...modalProps}>
            <div className={styles.formWrapper}>
                <div className={styles.formRow}>
                    <div className={styles.halfLayout}>
                        <div className={styles.requiredLabel}>
                            类型：
                        </div>
                        {depTypeText}
                    </div>
                    <div className={styles.halfLayout}>
                        <div className={styles.requiredLabel}>
                            编码：
                        </div>
                        {depCode}
                    </div>
                </div>
                <div className={styles.formRow}>
                    <div className={styles.halfLayout}>
                        <div className={styles.requiredLabel}>
                            名称：
                        </div>
                        {depName}
                    </div>
                    <div className={styles.halfLayout}>
                        <div className={styles.label}>
                            负责人：
                        </div>
                        {head}
                    </div>
                </div>
                <div className={styles.formRow}>
                    <div className={styles.halfLayout}>
                        <div className={styles.label}>
                            负责人手机号：
                        </div>
                        {headMobile}
                    </div>
                    <div className={styles.halfLayout}>
                        <div className={styles.label}>
                            邮箱：
                        </div>
                        {email}
                    </div>
                </div>
                <div className={styles.formRow}>
                    <div className={styles.halfLayout}>
                        <div className={styles.label}>
                            行政区划：
                        </div>
                        {divisionName}
                    </div>
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
                            描述：
                        </div>
                        {description}
                    </div>
                </div>
                <div className={styles.formRow}>
                    <div className={styles.label}>
                        地址：
                    </div>
                    {address}
                </div>
            </div>
            
        </VtxModal>
    )
}

export default ViewItem;