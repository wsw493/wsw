import React from 'react';
import VtxModal from '../../vtxCommon/vtxModal/VtxModal';
import StateInput from '../../vtxCommon/VtxForm/stateInput';
import styles from './item.less';
import {formValidation} from '../../../utils/toolFunctions';
import {validate} from '../../regExpression';

const EditItem = (props)=>{
    const {modalProps,contentProps } = props;

    const {nameUnique,codeUnique,id,name,code,orderIndex,description,checkState} = contentProps;
    const {updateItem,validateRole} = contentProps;

    return (
        <VtxModal {...modalProps}>
            <div className={styles.formWrapper}>
                <div className={styles.formRow}>
                    <div className={styles.halfLayout}>
                        <div className={styles.requiredLabel}>
                            编码：
                        </div>
                        <StateInput onChange={(e)=>{
                            let code=e.target.value;
                            updateItem({code});
                            validateRole({
                                key:'code',
                                code,
                                id
                            });
                        }} value={code} errorMsg={codeUnique?'编码重复':(validate(['required','codeValidator'],code))} 
                        validated={codeUnique?false:(checkState?(validate(['required','codeValidator'],code)==''):true)} />
                    </div>
                </div>
                <div className={styles.formRow}>
                    <div className={styles.halfLayout}>
                        <div className={styles.requiredLabel}>
                            名称：
                        </div>
                        <StateInput onChange={(e)=>{
                            let name=e.target.value;
                            updateItem({name}); 
                            validateRole({
                                key:'name',
                                name,
                                id
                            });
                        }} value={name} errorMsg={validate(['required','nameValidator'],name)} 
                        validated={checkState?(validate(['required','nameValidator'],name)==''):true} />
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