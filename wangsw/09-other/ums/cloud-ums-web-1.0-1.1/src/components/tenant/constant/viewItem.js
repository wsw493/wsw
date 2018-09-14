import React from 'react';
import styles from './item.less';
import VtxModal from '../../vtxCommon/vtxModal/VtxModal';

const ViewItem = (props)=>{
    const {modalProps,contentProps } = props;

    const {id,constantValue,constantCode,constantDescription,tenantId} = contentProps;

    return (
        <VtxModal {...modalProps}>
            <div className={styles.formWrapper}>
                <div className={styles.formRow}>
                    <div className={styles.halfLayout}>
                        <div className={styles.label}>
                            常量名：
                        </div>
                        {constantCode}
                    </div>
                </div>
                <div className={styles.formRow}>
                    <div className={styles.halfLayout}>
                        <div className={styles.label}>
                            常量值：
                        </div>
                        {constantValue}
                    </div>
                </div>
                <div className={styles.formRow}>
                    <div className={styles.halfLayout}>
                        <div className={styles.label}>
                            描述：
                        </div>
                        {constantDescription}
                    </div>
                </div>
            </div>
            
        </VtxModal>
    )
}

export default ViewItem;