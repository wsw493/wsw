import React from 'react';
import VtxModal from '../vtxCommon/vtxModal/VtxModal';
import styles from './item.less';

const EditItem = (props)=>{
    const {modalProps,contentProps } = props;

    const {nameUnique,codeUnique,id,name,code,orderIndex,description,checkState,systemId,
        goalSystemId,functionType,mainFunctionId,uri,isMain,goalSystemName,mainFunctionName} = contentProps;
    const {updateItem,validateFunctions,systemList,functionTree} = contentProps;

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
                            所指向的系统：
                        </div>
                        {goalSystemName}
                    </div>
                    <div className={styles.halfLayout}>
                        <div className={styles.label}>
                            URI：
                        </div>
                       {uri}
                    </div>
                </div>
                <div className={styles.formRow}>
                    <div className={styles.halfLayout}>
                        <div className={styles.label}>
                            功能类型：
                        </div>
                        {functionType=='1'?'主功能':'辅功能'}
                    </div>
                    {
                        functionType=='2'?[
                            <div className={styles.halfLayout}>
                                <div className={styles.label}>
                                    主功能：
                                </div>
                                {mainFunctionName}
                            </div>
                        ]:['']
                    }        
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

export default EditItem;