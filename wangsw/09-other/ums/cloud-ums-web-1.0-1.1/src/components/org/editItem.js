import React from 'react';
import VtxModal from '../vtxCommon/vtxModal/VtxModal';
import StateInput from '../vtxCommon/VtxForm/stateInput';
import StateSelect from '../vtxCommon/VtxForm/stateSelect';
import { Input, Icon, Radio,Select,AutoComplete, Checkbox ,Button} from 'antd';
import styles from './item.less';
import {formValidation} from '../../utils/toolFunctions';
import Map from '../vtxCommon/Map/Map';
import VtxTreeSelect from '../vtxCommon/VtxTreeSelect/VtxTreeSelect';

const RadioGroup = Radio.Group;
const Option = Select.Option;
const CheckboxGroup = Checkbox.Group;
import {validate} from '../regExpression';

const EditItem = (props)=>{
    const {modalProps,contentProps } = props;

    const {id,checkState,orgTypeText,orgCode,orgName,head,headMobile,address,email,description,orderIndex,divisionId,
        codeUnique,orgType,lngLats,tenantId,parentName,parentId} = contentProps;
    const {updateItem,validateCode,updateState,divisionTree} = contentProps;

    const getPoints = ()=>{
        updateState({modal1Visible: true});
    }
    return (
        <VtxModal {...modalProps}>
            <div className={styles.formWrapper}>
                <div className={styles.formRow}>
                    <div className={styles.halfLayout} style={{height:'46px'}}>
                        <div className={styles.requiredLabel}>
                            上级机构：
                        </div>
                        {parentName}
                    </div>
                </div>
                <div className={styles.formRow}>
                    <div className={styles.halfLayout}>
                        <div className={styles.requiredLabel}>
                            编码：
                        </div>
                        <StateInput onChange={(e)=>{
                            let orgCode=e.target.value;
                            updateItem({orgCode});
                            validateCode({
                                key: 'orgCode',
                                id,
                                orgCode,
                                tenantId,
                                opType: (id && id!='')?'update':'add'
                            }); 
                        }} value={orgCode} errorMsg={codeUnique?'编码重复':(validate(['required','codeValidator'],orgCode))} 
                        validated={codeUnique?false:(checkState?(validate(['required','codeValidator'],orgCode)==''):true)} />
                    </div>
                    <div className={styles.halfLayout}>
                        <div className={styles.requiredLabel}>
                            名称：
                        </div>
                        <StateInput onChange={(e)=>{
                            updateItem({orgName: e.target.value}); 
                        }} value={orgName} errorMsg={validate(['required','nameValidator'],orgName)} 
                        validated={checkState?(validate(['required','nameValidator'],orgName)==''):true} />
                    </div>
                    
                </div>
                <div className={styles.formRow}>
                    <div className={styles.halfLayout}>
                        <div className={styles.label}>
                            负责人：
                        </div>
                        <StateInput onChange={(e)=>{
                            updateItem({head: e.target.value}); 
                        }} value={head} />
                    </div>
                    <div className={styles.halfLayout}>
                        <div className={styles.label}>
                            负责人手机号：
                        </div>
                        <StateInput onChange={(e)=>{
                            updateItem({headMobile: e.target.value}); 
                        }} value={headMobile} errorMsg={validate(['phone'],headMobile)} 
                        validated={checkState?(validate(['phone'],headMobile)==''):true}/>
                    </div>
                    
                </div>
                <div className={styles.formRow}>
                    <div className={styles.halfLayout}>
                        <div className={styles.label}>
                            邮箱：
                        </div>
                        <StateInput onChange={(e)=>{
                            updateItem({email: e.target.value}); 
                        }} value={email} errorMsg={validate(['email'],email )} 
                        validated={checkState?(validate(['email'],email)==''):true}/>
                    </div>
                    <div className={styles.halfLayout}>
                        <div className={styles.label}>
                            排序号：
                        </div>
                        <StateInput onChange={(e)=>{
                            updateItem({orderIndex: e.target.value}); 
                        }} value={orderIndex} errorMsg={validate(['number'],orderIndex )} 
                        validated={checkState?(validate(['number'],orderIndex)==''):true}/>
                    </div>
                </div>
                <div className={styles.formRow}>
                    <div className={styles.halfLayout}>
                        <div className={styles.label}>
                            行政区划：
                        </div>
                        <div style={{display:'inline-block'}}>
                            <VtxTreeSelect
                                data={divisionTree}
                                value={divisionId?divisionId.split(','):[]}
                                treeDefaultExpandAll={true}
                                onChange={({allValue,allLabel,value,label})=>{
                                    updateItem({divisionId:value.join()});
                                }}
                            />
                        </div>
                    </div>
                    <div className={styles.halfLayout}>
                        <div className={styles.label}>
                            描述：
                        </div>
                        <StateInput onChange={(e)=>{
                            updateItem({description: e.target.value}); 
                        }} value={description} type='textarea'/>
                    </div>
                </div>
                <div className={styles.formRow}>
                    <div className={styles.halfLayout}>
                        <div className={styles.label}>
                            地址：
                        </div>
                        <StateInput onChange={(e)=>{
                            updateItem({address: e.target.value}); 
                        }} value={address} type='textarea'/>
                    </div>
                </div>
                <Button onClick={getPoints} type="primary" className={styles.getPoints}>定位</Button>
            </div>
            
        </VtxModal>
    )
}

export default EditItem;