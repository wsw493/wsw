import React from 'react';
import VtxModal from '../vtxCommon/vtxModal/VtxModal';
import StateInput from '../vtxCommon/VtxForm/stateInput';
import StateSelect from '../vtxCommon/VtxForm/stateSelect';
import styles from './item.less';
import {formValidation} from '../../utils/toolFunctions';
import {validate} from '../regExpression';
import {Select,Radio} from 'antd';
import VtxTreeSelect from '../vtxCommon/VtxTreeSelect/VtxTreeSelect';

const Option = Select.Option;
const RadioGroup = Radio.Group;
const EditItem = (props)=>{
    const {modalProps,contentProps } = props;

    const {nameUnique,codeUnique,id,name,code,orderIndex,description,checkState,systemId,
        goalSystemId,functionType,mainFunctionId,uri,isMain} = contentProps;
    const {updateItem,validateFunctions,systemList,functionTree} = contentProps;

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
                            validateFunctions({
                                key:'code',
                                code,
                                id
                            });
                        }} value={code} errorMsg={codeUnique?'编码重复':(validate(['required','codeValidator'],code))} 
                        validated={codeUnique?false:(checkState?(validate(['required','codeValidator'],code)==''):true)} />
                    </div>
                    <div className={styles.halfLayout}>
                        <div className={styles.requiredLabel}>
                            名称：
                        </div>
                        <StateInput onChange={(e)=>{
                            let name=e.target.value;
                            updateItem({name}); 
                            validateFunctions({
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
                            所指向的系统：
                        </div>
                        {/* <StateSelect onChange={(value,option)=>{
                            updateItem({goalSystemId: value}); 
                        }} value={goalSystemId} >
                            {
                                systemList.map((item)=>{
                                    return <Option value={item.id}>{item.systemName}</Option>
                                })
                            }
                        </StateSelect> */}
                        <VtxTreeSelect
                            data={systemList}
                            value={[goalSystemId]}
                            showSearch={true}
                            onChange={({allValue,allLabel,value,label})=>{
                                updateItem({goalSystemId:value[0]});
                            }}
                        />
                    </div>
                    <div className={styles.halfLayout}>
                        <div className={styles.label}>
                            URI：
                        </div>
                        <StateInput onChange={(e)=>{
                            updateItem({uri: e.target.value}); 
                        }} value={uri} />
                    </div>
                </div>
                <div className={styles.formRow}>
                    <div className={styles.halfLayout} style={{height:'46px'}}>
                        <div className={styles.label}>
                            功能类型：
                        </div>
                        <RadioGroup value={functionType} onChange={(e)=>{
                            updateItem({
                                functionType:e.target.value,
                                isMain:e.target.value=='1',
                                mainFunctionId:''
                            });
                            }}>
                            <Radio value='1'>主功能</Radio>
                            <Radio value='2'>辅功能</Radio>
                        </RadioGroup>
                    </div>
                    {
                        functionType=='2'?[
                            <div className={styles.halfLayout}>
                                <div className={styles.label}>
                                    主功能：
                                </div>
                                <div style={{display:'inline-block'}}>
                                    <VtxTreeSelect
                                        data={functionTree}
                                        value={mainFunctionId}
                                        treeDefaultExpandAll={true}
                                        onChange={({allValue,allLabel,value,label})=>{
                                            updateItem({mainFunctionId:value[0]});
                                        }}
                                    />
                                </div>
                            </div>
                        ]:['']
                    }        
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