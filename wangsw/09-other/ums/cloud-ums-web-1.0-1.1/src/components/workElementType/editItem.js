import React from 'react';
import VtxModal from '../vtxCommon/vtxModal/VtxModal';
import StateInput from '../vtxCommon/VtxForm/stateInput';
import StateSelect from '../vtxCommon/VtxForm/stateSelect';
import { Input, Icon, Radio,Select,AutoComplete, Checkbox ,Button} from 'antd';
import styles from './item.less';
import {formValidation} from '../../utils/toolFunctions';
import VtxTreeSelect from '../vtxCommon/VtxTreeSelect/VtxTreeSelect';

const RadioGroup = Radio.Group;
const Option = Select.Option;
const CheckboxGroup = Checkbox.Group;
import {validate} from '../regExpression';

const EditItem = (props)=>{
    const {modalProps,contentProps } = props;

    const {id,loading,visible,checkState,code,name,shape,departmentId,departmentName,info,orderIndex,codeUnique,orgTreeData,
     nameUnique,} = contentProps;
    const {updateItem,validateName,validateCode} = contentProps;
    return (
        <VtxModal {...modalProps}>
            <div className={styles.formWrapper}>
                <div className={styles.formRow}>
                    <div className={styles.halfLayout}>
                        <div className={styles.requiredLabel}>
                            编号：
                        </div>
                        <StateInput onChange={(e)=>{
                            let code=e.target.value;
                            updateItem({code});
                            validateCode(code,id); 
                        }} value={code} errorMsg={codeUnique?'编码重复':(validate(['required','codeValidator'],code))} 
                        validated={codeUnique?false:(checkState?(validate(['required','codeValidator'],code)==''):true)} />
                    </div>
                    <div className={styles.halfLayout}>
                        <div className={styles.requiredLabel}>
                            类型名称：
                        </div>
                        <StateInput onChange={(e)=>{
                            let name=e.target.value;
                            updateItem({name});
                            validateName(name,id);
                        }} value={name} errorMsg={nameUnique?'名称重复':(validate(['required','nameValidator'],name))} 
                        validated={nameUnique?false:(checkState?(validate(['required','nameValidator'],name)==''):true)} />
                    </div>
                </div>
                <div className={styles.formRow}>
                    <div className={styles.halfLayout}>
                        <div className={styles.requiredLabel}>
                            外形：
                        </div>
                        <StateSelect onChange={(value,label)=>{
                            updateItem({'shape': value}); 
                        }} value={shape} errorMsg={validate(['required'],shape)} 
                        validated={checkState?(validate(['required'],shape)==''):true} >
                            <Option value='point' key="1">点</Option>
                            <Option value="line" key="2">多折线</Option>
                            <Option value="polygon" key="3">多边形</Option>
                            <Option value="rectangle" key="4">矩形</Option>
                            <Option value="circle" key="5">圆形</Option>
                        </StateSelect>
                    </div>
                    <div className={styles.halfLayout}>
                        <div className={styles.label}>
                            所属机构：
                        </div>
                        <div className={styles.treeSelect}>
                            <VtxTreeSelect
                                data={orgTreeData}
                                value={departmentId?departmentId.split():[]}
                                treeDefaultExpandAll={true}
                                onChange={({allValue,allLabel,value,label})=>{
                                    updateItem({departmentId:value.join()});
                                }}
                            />
                        </div>
                    </div>
                </div>
                <div className={styles.formRow}>
                    <div className={styles.halfLayout}>
                        <div className={styles.label}>
                            详细信息：
                        </div>
                        <StateInput onChange={(e)=>{
                            updateItem({info: e.target.value}); 
                        }} value={info} type='textarea'/>
                    </div>
                </div>
            </div>
            
        </VtxModal>
    )
}

export default EditItem;