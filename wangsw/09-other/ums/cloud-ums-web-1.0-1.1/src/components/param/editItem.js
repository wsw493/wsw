import React from 'react';
import {Alert,Input,Form} from 'antd';
import VtxModal from '../vtxCommon/vtxModal/VtxModal';
import StateInput from '../vtxCommon/VtxForm/stateInput';
import styles from './item.less';
import {formValidation} from '../../utils/toolFunctions';
import {validate} from '../regExpression';
const Edititem=(props)=>{
    const {modalProps,contentProps } = props;
	const {codeUnique,nameUnique,id,typeName,typeCode,orderIndex,description,checkState} = contentProps;
    const {updateItem,validateParamType} = contentProps;
    return (
    	<VtxModal {...modalProps}>
            <div className={styles.formWrapper}>
                <div className={styles.formRow}>
                    <div className={styles.halfLayout}>
                        <div className={styles.requiredLabel}>
                            编码：
                        </div>
                        <StateInput onChange={(e)=>{
                            let typeCode=e.target.value;
                            updateItem({typeCode});
                            validateParamType({key:'typeCode',typeCode,id}); 
                        }} value={typeCode} errorMsg={codeUnique?'编码重复':(validate(['required','codeValidator'],typeCode))} 
                        validated={codeUnique?false:(checkState?(validate(['required','codeValidator'],typeCode)==''):true)} />
                    </div>
                </div>
                <div className={styles.formRow}>
                    <div className={styles.halfLayout}>
                        <div className={styles.requiredLabel}>
                            名称：
                        </div>
                        <StateInput onChange={(e)=>{
                            let typeName=e.target.value;
                            updateItem({typeName});
                            validateParamType({key:'typeName',typeName,id}); 
                        }} value={typeName} errorMsg={nameUnique?'名称重复':(validate(['required','nameValidator'],typeName))} 
                        validated={nameUnique?false:(checkState?(validate(['required','nameValidator'],typeName)==''):true)} />
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

export default Edititem;