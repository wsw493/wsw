import React from 'react';
import VtxModal from '../../vtxCommon/vtxModal/VtxModal';
import StateInput from '../../vtxCommon/VtxForm/stateInput';
import StateSelect from '../../vtxCommon/VtxForm/stateSelect';
import VtxDatePicker from '../../vtxCommon/VtxDate/VtxDatePicker';
import VortexUpload from '../../vtxCommon/VortexUpload/VortexUpload';
import { Input, Icon, Radio,Select,AutoComplete, Checkbox ,Button,message} from 'antd';
import styles from './item.less';
import {formValidation,compareValue} from '../../../utils/toolFunctions';

const RadioGroup = Radio.Group;
const Option = Select.Option;
const CheckboxGroup = Checkbox.Group;
import {validate} from '../../regExpression';

const EditItem = (props)=>{
    const {modalProps,contentProps } = props;

    const {id,checkState,userName,password,confirm_password,phone,profilePhoto,gender,birthday} = contentProps;
    const {updateItem, phoneUnique,accountUnique,updateState,validateAccount,passwordFlag,validatePhone,fileListVersion} = contentProps;

    function disabledDate(current) {
        return current && current.valueOf() > Date.now();
    }
    const uploadProps = {
        fileList:profilePhoto?JSON.parse(profilePhoto):[],
        mode: 'single',
        listType:"picture-card",
        accept: "image/png, image/jpeg", // 接受上传的文件类型
        fileListVersion,
        onSuccess(file){
            message.info(`${file.name} 上传成功`);
            var fileList = [{id:file.id,name:file.name}];
            updateItem({profilePhoto:JSON.stringify(fileList)});
        },
        onError(res){
            message.info(`${res.name} 上传失败.`);
        },
        onRemove(file){
            updateItem({profilePhoto:"[]"});
        }
    }
    return (
        <VtxModal {...modalProps}>
            <div className={styles.formWrapper}>
                <div className={styles.formRow} style={{height: '46px'}}>
                    <div className={styles.halfLayout}>
                        <div className={styles.requiredLabel}>
                            手机号：
                        </div>
                        {
                            id?<span>{phone}</span>:
                            <StateInput onChange={(e)=>{
                                let phone=e.target.value;
                                updateItem({phone});
                                if(phone){
                                    validatePhone({key:'phone',phone,id}); 
                                }
                            }} value={phone} errorMsg={phoneUnique?'手机号已注册':(validate(['required','phone'],phone))} 
                            validated={phoneUnique?false:(checkState?(validate(['required','phone'],phone)==''):true)} />
                        }
                    </div>
                </div>
                <div className={styles.formRow} style={{height: '46px'}}>
                    <div className={styles.halfLayout}>
                        <div className={styles.requiredLabel}>
                            用户名：
                        </div>
                        {
                            id?<span>{userName}</span>:
                            <StateInput onChange={(e)=>{
                                let userName=e.target.value;
                                updateItem({userName});
                                if(userName){
                                    validatePhone({key:'userName',userName,id}); 
                                }
                            }} value={userName} errorMsg={accountUnique?'手机号已注册':(validate(['required'],userName))} 
                            validated={accountUnique?false:(checkState?(validate(['required'],userName)==''):true)} />
                        }
                    </div>
                </div>
                {
                    id?['']:[
                        <div className={styles.formRow}>
                            <div className={styles.halfLayout}>
                                <div className={styles.requiredLabel}>
                                    密码：
                                </div>
                                <StateInput type='password' onChange={(e)=>{
                                    updateItem({password: e.target.value});
                                    updateState({passwordFlag: e.target.value!=confirm_password && confirm_password});
                                }} value={password} errorMsg={(validate(['required'],password))} 
                                validated={(checkState?(validate(['required'],password)==''):true)} />
                            </div>
                        </div>
                    ]
                }
                {
                    id?['']:[
                        <div className={styles.formRow}>
                            <div className={styles.halfLayout}>
                                <div className={styles.requiredLabel}>
                                    确认密码：
                                </div>
                                <StateInput type='password' onChange={(e)=>{
                                    updateItem({confirm_password: e.target.value});
                                    updateState({passwordFlag: e.target.value!=password});
                                }} value={confirm_password} errorMsg={(passwordFlag&&password)?'密码不一致':validate(['required'],confirm_password)} 
                                validated={(passwordFlag&&password)?false:(checkState?(validate(['required'],confirm_password)==''):true)} />
                            </div>
                        </div>
                    ]
                }
                <div className={styles.formRow} style={{height: '46px'}}>
                    <div className={styles.halfLayout}>
                        <div className={styles.label}>
                            性别：
                        </div>
                        <RadioGroup value={gender} onChange={(e)=>{
                            updateItem({gender: e.target.value});
                        }}>
                            <Radio key='M' value='M'>男</Radio>
                            <Radio key='F' value='F'>女</Radio>
                        </RadioGroup>
                    </div>
                </div>
                <div className={styles.formRow} style={{height: '46px'}}>
                    <div className={styles.halfLayout}>
                        <div className={styles.label}>
                            生日：
                        </div>
                        <VtxDatePicker value={birthday} style={{width:"300px"}} disabledDate={disabledDate}
                            showToday={true} format="YYYY-MM-DD" onChange={(data,dateString)=>{
                                updateItem({birthday: dateString}); 
                        }}
                        /> 
                    </div>
                </div>
                <div className={styles.formRow}>
                    <div className={styles.halfLayout}>
                        <div className={styles.label}>
                            头像：
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