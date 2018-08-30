import React from 'react';
import VtxModal from '../../vtxCommon/vtxModal/VtxModal';
import StateInput from '../../vtxCommon/VtxForm/stateInput';
import styles from './item.less';
import {formValidation} from '../../../utils/toolFunctions';
import {validate} from '../../regExpression';

const EditItem = (props)=>{
    const {modalProps,contentProps } = props;

    const {codeUnique,id,constantValue,constantCode,constantDescription,tenantId,checkState} = contentProps;
    const {updateItem,validateConstant} = contentProps;

    return (
        <VtxModal {...modalProps}>
            <div className={styles.formWrapper}>
                <div className={styles.formRow}>
                    <div className={styles.halfLayout}>
                        <div className={styles.requiredLabel}>
                            常量名：
                        </div>
                        <StateInput onChange={(e)=>{
                            let constantCode=e.target.value;
                            updateItem({constantCode});
                            validateConstant({
                                key:'constantCode',
                                constantCode,
                                id
                            });
                        }} value={constantCode} errorMsg={codeUnique?'常量名重复':(validate(['required'],constantCode))} 
                        validated={codeUnique?false:(checkState?(validate(['required'],constantCode)==''):true)} />
                    </div>
                </div>
                <div className={styles.formRow}>
                    <div className={styles.halfLayout}>
                        <div className={styles.requiredLabel}>
                            常量值：
                        </div>
                        <StateInput onChange={(e)=>{
                            let constantValue=e.target.value;
                            updateItem({constantValue}); 
                        }} value={constantValue} errorMsg={validate(['required'],constantValue)} 
                        validated={checkState?(validate(['required'],constantValue)==''):true} />
                    </div>
                </div>
                <div className={styles.formRow}>
                    <div className={styles.halfLayout}>
                        <div className={styles.requiredLabel}>
                            描述：
                        </div>
                        <StateInput onChange={(e)=>{
                            updateItem({constantDescription: e.target.value}); 
                        }} value={constantDescription} type='textarea' errorMsg={validate(['required'],constantDescription)} 
                        validated={checkState?(validate(['required'],constantDescription)==''):true}/>
                    </div>
                </div>
            </div>
            
        </VtxModal>
    )
}

export default EditItem;