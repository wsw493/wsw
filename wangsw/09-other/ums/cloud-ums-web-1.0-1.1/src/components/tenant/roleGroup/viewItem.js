import React from 'react';
import VtxModal from '../../vtxCommon/vtxModal/VtxModal';
import styles from './item.less';
const ViewItem = (props)=>{
    const {modalProps,contentProps } = props;

    const {unique,id,name,code,orderIndex,description} = contentProps;
    const {updateItem,validateCode} = contentProps;

    return (
        <VtxModal {...modalProps}>
            <div className={styles.formWrapper}>
                <div className={styles.formRow}>
                    <div className={styles.halfLayout}>
                        <div className={styles.requiredLabel}>
                            编码：
                        </div>
                        {code}
                    </div>
                </div>
                <div className={styles.formRow}>
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
                            排序：
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
            </div>
            
        </VtxModal>
    )
}

export default ViewItem;