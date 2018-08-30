import React from 'react';
import VtxModal from '../../vtxCommon/vtxModal/VtxModal';
import StateInput from '../../vtxCommon/VtxForm/stateInput';
import StateSelect from '../../vtxCommon/VtxForm/stateSelect';
import VtxDatePicker from '../../vtxCommon/VtxDate/VtxDatePicker';
import { Input, Icon, Radio,Select ,TreeSelect, Collapse,message} from 'antd';
import styles from './item.less';
import {formValidation} from '../../../utils/toolFunctions';
const Option = Select.Option;
const RadioGroup = Radio.Group;
import {validate} from '../../regExpression';
import VtxTreeSelect from '../../vtxCommon/VtxTreeSelect/VtxTreeSelect';
import VortexUpload from '../../vtxCommon/VortexUpload/VortexUpload';

const EditUser = (props)=>{
	const {modalProps,contentProps } = props;
    const {id,checkState,password,staffName,userName,confirm_password,passwordFlag,fileList,
            fileListVersion,photoId,permissionScope,permissionScopeList,customScope,
            deptOrgTree,userNameUnique,isOpenUser} = contentProps;
    const {updateItem,updateState,validateUserName} = contentProps;
    const uploadProps = {
        fileList:fileList,
        mode: 'single',
        listType:"picture-card",
        accept: "image/png, image/jpeg", // 接受上传的文件类型
        fileListVersion,
        onSuccess(file){
            message.info(`${file.name} 上传成功`);
            var fileList = [{id:file.id,name:file.name}];
            updateItem({photoId:JSON.stringify(fileList),fileList});
        },
        onError(res){
            message.info(`${res.name} 上传失败.`);
        },
        onRemove(file){
            updateItem({fileList:[],photoId:"[]"});
        }
    }

    return (
    	 <VtxModal {...modalProps}>
    	 	<div className={styles.formWrapper}>
                <div className={styles.formRow} style={{height:'46px'}}>
                    <div className={styles.halfLayout}>
                        <div className={styles.label}>
                            人员姓名：
                        </div>
                        {staffName}
                    </div>
                    <div className={styles.halfLayout}>
                        <div className={styles.requiredLabel}>
                            用户名：
                        </div>
                        <StateInput onChange={(e)=>{
                            var userName = e.target.value;
                            updateItem({userName}); 
                            validateUserName({key:'userName',userName,id});
                        }} value={userName} errorMsg={userNameUnique?'用户名重复':(validate(['required','nameValidator'],userName))} 
                        validated={userNameUnique?false:(checkState?(validate(['required','nameValidator'],userName)==''):true)} />
                    </div>
                </div>
                {isOpenUser?
                    [<div className={styles.formRow}>
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
                    </div>]:[]
                }

    	 		<div className={styles.formRow} style={{height:'46px'}}>
                    <div className={styles.halfLayout}>
                        <div className={styles.label}>
                            权限范围：
                        </div>
                        <StateSelect onChange={(value,label)=>{
                            updateItem({'permissionScope': value}); 
                        }} value={permissionScope}>
                        {
                            permissionScopeList.map((ele)=>{
                                return <Option key={ele.key} value={ele.key}>{ele.text}</Option>
                            })
                        }
                        </StateSelect>
                    </div>
                    <div className={styles.halfLayout}>
                        <div className={styles.requiredLabel}>
                            自定义范围：
                        </div>
                        <div style={{display:'inline-block'}}>
                            <VtxTreeSelect
                                data={deptOrgTree}
                                value={customScope?customScope.split(','):[]}
                                multiple={true}
                                treeDefaultExpandedKeys={['-1']}
                                onChange={({allValue,allLabel,value,label})=>{
                                    updateItem({customScope:value.join(',')});
                                }}
                                disabled={permissionScope!='3'}
                            />
                        </div>
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
export default EditUser;