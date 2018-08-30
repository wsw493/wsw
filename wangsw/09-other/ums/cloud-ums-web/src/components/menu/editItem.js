import React from 'react';
import VtxModal from '../vtxCommon/vtxModal/VtxModal';
import StateInput from '../vtxCommon/VtxForm/stateInput';
import styles from './item.less';
import {formValidation} from '../../utils/toolFunctions';
import {validate} from '../regExpression';
import {Radio,message} from 'antd';
import VortexUpload from '../vtxCommon/VortexUpload/VortexUpload';
import VtxTreeSelect from '../vtxCommon/VtxTreeSelect/VtxTreeSelect';

const RadioGroup = Radio.Group;
const EditItem = (props)=>{
    const {modalProps,contentProps } = props;

    const {nameUnique,codeUnique,id,name,code,orderIndex,description,checkState,isControlled,iconFont,
            isHidden,isWelcomeMenu,functionId,parentId,photoIds,fileList,fileListVersion} = contentProps;
    const {updateItem,validateMenu,menuTreeData,funTreeData} = contentProps;
    
    const uploadProps = {
        fileList:fileList,
        mode: 'single',
        listType:"picture-card",
        accept: "image/png, image/jpeg, application/zip", // 接受上传的文件类型
        fileListVersion,
        onSuccess(file){
            message.info(`${file.name} 上传成功`);
            var fileList = [{id:file.id,name:file.name}];
            updateItem({photoIds:JSON.stringify(fileList),fileList});
        },
        onError(res){
            message.info(`${res.name} 上传失败.`);
        },
        onRemove(file){
            updateItem({fileList:[],photoIds:"[]"});
        }
    }

    return (
        <VtxModal {...modalProps}>
            <div className={styles.formWrapper}>
                <div className={styles.formRow}>
                    <div className={styles.halfLayout} style={{height: '46px'}}>
                        <div className={styles.requiredLabel}>
                            上级菜单：
                        </div>
                        <div style={{display:'inline-block'}}>
                            <VtxTreeSelect
                                data={menuTreeData}
                                value={parentId?parentId.split(','):[]}
                                treeDefaultExpandAll={true}
                                required={id!=""?false:checkState}
                                onChange={({allValue,allLabel,value,label})=>{
                                    updateItem({parentId:value.join()});
                                }}
                            />
                        </div>
                    </div>
                </div>
                <div className={styles.formRow}>
                    <div className={styles.halfLayout}>
                        <div className={styles.requiredLabel}>
                            编码：
                        </div>
                        <StateInput onChange={(e)=>{
                            let code=e.target.value;
                            updateItem({code});
                            validateMenu({
                                key:'code',
                                code,
                                id
                            });
                        }} value={code} errorMsg={(codeUnique&&code!='')?'编码重复':(validate(['required','codeValidator'],code))} 
                        validated={(codeUnique&&code!='')?false:(checkState?(validate(['required','codeValidator'],code)==''):true)} />
                    </div>
                    <div className={styles.halfLayout}>
                        <div className={styles.requiredLabel}>
                            名称：
                        </div>
                        <StateInput onChange={(e)=>{
                            let name=e.target.value;
                            updateItem({name}); 
                            validateMenu({
                                key:'name',
                                name,
                                id
                            });
                        }} value={name} errorMsg={nameUnique?'名称重复':(validate(['required','nameValidator'],name))} 
                        validated={nameUnique?false:(checkState?(validate(['required','nameValidator'],name)==''):true)} />
                    </div>
                </div>
                <div className={styles.formRow}>
                    <div className={styles.halfLayout}>
                        <div className={styles.label}>
                            绑定功能：
                        </div>
                        <div style={{display:'inline-block'}}>
                            <VtxTreeSelect
                                data={funTreeData}
                                value={functionId?functionId.split(','):[]}
                                treeDefaultExpandAll={true}
                                onChange={({allValue,allLabel,value,label})=>{
                                    updateItem({functionId:value.join()});
                                }}
                            />
                        </div>
                    </div>
                    <div className={styles.halfLayout}>
                        <div className={styles.requiredLabel}>
                            排序号：
                        </div>
                        <StateInput onChange={(e)=>{
                            updateItem({orderIndex: e.target.value}); 
                        }} value={orderIndex} errorMsg={validate(['required','number'],orderIndex)} 
                        validated={checkState?(validate(['required','number'],orderIndex)==''):true} />
                    </div>
                    
                </div>

                <div className={styles.formRow} style={{height: '46px'}}>
                    <div className={styles.halfLayout}>
                        <div className={styles.label}>
                            欢迎页面：
                        </div>
                        <RadioGroup value={isWelcomeMenu} onChange={(e)=>{
                            updateItem({isWelcomeMenu:e.target.value});
                            }}>
                            <Radio value={1}>是</Radio>
                            <Radio value={0}>否</Radio>
                        </RadioGroup>
                    </div>
                    <div className={styles.halfLayout}>
                        <div className={styles.label}>
                            隐藏：
                        </div>
                        <RadioGroup value={isHidden} onChange={(e)=>{
                            updateItem({isHidden:e.target.value});
                            }}>
                            <Radio value={1}>是</Radio>
                            <Radio value={0}>否</Radio>
                        </RadioGroup>
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
                    <div className={styles.halfLayout}>
                        <div className={styles.label}>
                            权限控制：
                        </div>
                        <RadioGroup value={isControlled} onChange={(e)=>{
                            updateItem({isControlled:e.target.value});
                            }}>
                            <Radio value={1}>是</Radio>
                            <Radio value={0}>否</Radio>
                        </RadioGroup>
                    </div>
                    
                </div>
                <div className={styles.formRow}>
                    <div className={styles.halfLayout}>
                        <div className={styles.label}>
                            iconFont：
                        </div>
                        <StateInput onChange={(e)=>{
                            updateItem({iconFont: e.target.value}); 
                        }} value={iconFont}/>
                    </div>
                    <div className={styles.halfLayout}>
                        <div className={styles.label}>
                            图标：
                        </div>
                        <div style={{display:'inline-block'}}>
                            <VortexUpload {...uploadProps}/>
                        </div>
                    </div>
                </div>
            </div>
            
        </VtxModal>
    )
}

export default EditItem;