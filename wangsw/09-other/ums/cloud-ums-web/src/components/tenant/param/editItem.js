import React from 'react';
import {Alert,Input,Form} from 'antd';
import VtxModal from '../../vtxCommon/vtxModal/VtxModal';
import StateInput from '../../vtxCommon/VtxForm/stateInput';
import styles from './item.less';
import {formValidation} from '../../../utils/toolFunctions';
import {validate} from '../../regExpression';
const Edititem=(props)=>{
    const {modalProps,contentProps } = props;
	const {unique,id,name,code,orderIndex,description,checkState} = contentProps;
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
                            let code=e.target.value;
                            updateItem({code});
                            validateCode(code,id); 
                        }} value={code} errorMsg={unique?'编码重复':(validate(['required','codeValidator'],code))} 
                        validated={unique?false:(checkState?(validate(['required','codeValidator'],code)==''):true)} />
                    </div>
                </div>
                <div className={styles.formRow}>
                    <div className={styles.halfLayout}>
                        <div className={styles.requiredLabel}>
                            名称：
                        </div>
                        <StateInput onChange={(e)=>{
                            updateItem({name: e.target.value}); 
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
                        }} value={orderIndex} errorMsg={validate(['required'],orderIndex)} 
                        validated={checkState?(validate(['required'],orderIndex)==''):true} />
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

export default Edititem;