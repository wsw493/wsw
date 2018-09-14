import React from 'react';
import VtxModal from '../vtxCommon/vtxModal/VtxModal';
import StateInput from '../vtxCommon/VtxForm/stateInput';
import styles from './item.less';
import {formValidation} from '../../utils/toolFunctions';
import {validate} from '../regExpression';

const EditItem = (props)=>{
    const {modalProps,contentProps } = props;

    const {codeUnique,id,groupName,groupCode,orderIndex,description,checkState} = contentProps;
    const {updateItem,validateCode} = contentProps;

    return (
        <VtxModal {...modalProps}>
            <div className={styles.formWrapper}>
                <div className={styles.formRow}>
                    <div className={styles.halfLayout}>
                        <div className={styles.requiredLabel}>
                            编码：
                        </div>
                        <StateInput onChange={(e)=>{
                            let groupCode=e.target.value;
                            updateItem({groupCode});
                            validateCode({
                                groupCode,
                                id,
                                key:'groupCode'
                            }); 
                        }} value={groupCode} errorMsg={codeUnique?'编码重复':(validate(['required','codeValidator'],groupCode))} 
                        validated={codeUnique?false:(checkState?(validate(['required','codeValidator'],groupCode)==''):true)} />
                    </div>
                </div>
                <div className={styles.formRow}>
                    <div className={styles.halfLayout}>
                        <div className={styles.requiredLabel}>
                            名称：
                        </div>
                        <StateInput onChange={(e)=>{
                            updateItem({groupName: e.target.value}); 
                        }} value={groupName} errorMsg={validate(['required','nameValidator'],groupName)} 
                        validated={checkState?(validate(['required','nameValidator'],groupName)==''):true} />
                    </div>
                </div>
                <div className={styles.formRow}>
                    <div className={styles.halfLayout}>
                        <div className={styles.requiredLabel}>
                            排序：
                        </div>
                        <StateInput onChange={(e)=>{
                            updateItem({orderIndex: e.target.value}); 
                        }} value={orderIndex} errorMsg={validate(['required','number'],orderIndex)} 
                        validated={checkState?(validate(['required','number'],orderIndex)==''):true} />
                    </div>
                </div>
                <div className={styles.formRow}>
                    <div className={styles.halfLayout}>
                        <div className={styles.label}>
                            描述：
                        </div>
                        <StateInput onChange={(e)=>{
                            updateItem({description: e.target.value}); 
                        }} value={description} type='textarea'/>
                    </div>
                </div>
            </div>
            
        </VtxModal>
    )
}

export default EditItem;