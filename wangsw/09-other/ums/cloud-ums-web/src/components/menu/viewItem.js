import React from 'react';
import VtxModal from '../vtxCommon/vtxModal/VtxModal';
import StateInput from '../vtxCommon/VtxForm/stateInput';
import styles from './item.less';
import {formValidation} from '../../utils/toolFunctions';
import {validate} from '../regExpression';
import {Radio} from 'antd';
import VortexUpload from '../vtxCommon/VortexUpload/VortexUpload';
import VtxTreeSelect from '../vtxCommon/VtxTreeSelect/VtxTreeSelect';

const RadioGroup = Radio.Group;
const EditItem = (props)=>{
    const {modalProps,contentProps } = props;

    const {id,name,code,orderIndex,description,isControlled,functionName,parentName,iconFont,
            isHidden,isWelcomeMenu,functionId,parentId,photoIds,fileList,fileListVersion} = contentProps;
            console.log(fileList);
    const uploadProps = {
        fileList: fileList,
        mode: 'single',
        listType:"picture-card",
        accept: "image/png, image/jpeg", // 接受上传的文件类型
        fileListVersion,
        disabled: true,
        viewMode: true
    }

    return (
        <VtxModal {...modalProps}>
            <div className={styles.formWrapper}>
                <div className={styles.formRow}>
                    <div className={styles.halfLayout}>
                        <div className={styles.requiredLabel}>
                            上级菜单：
                        </div>
                        {parentName}
                    </div>
                </div>
                <div className={styles.formRow}>
                    <div className={styles.halfLayout}>
                        <div className={styles.requiredLabel}>
                            编码：
                        </div>
                        {code}
                    </div>
                    <div className={styles.halfLayout}>
                        <div className={styles.requiredLabel}>
                            名称：
                        </div>
                        {name}
                    </div>
                </div>
                <div className={styles.formRow}>
                    <div className={styles.halfLayout}>
                        <div className={styles.label}>
                            绑定功能：
                        </div>
                        {functionName}
                    </div>
                    <div className={styles.halfLayout}>
                        <div className={styles.requiredLabel}>
                            排序号：
                        </div>
                        {orderIndex}
                    </div>
                    
                </div>

                <div className={styles.formRow}>
                    <div className={styles.halfLayout}>
                        <div className={styles.label}>
                            欢迎页面：
                        </div>
                        {isWelcomeMenu=='1'?'是':'否'}
                    </div>
                    <div className={styles.halfLayout}>
                        <div className={styles.label}>
                            隐藏：
                        </div>
                        {isHidden=='1'?'是':'否'}
                    </div>
                </div>

                <div className={styles.formRow}>
                    <div className={styles.halfLayout}>
                        <div className={styles.label}>
                            描述：
                        </div>
                        {description}
                    </div>
                    <div className={styles.halfLayout}>
                        <div className={styles.label}>
                            权限控制：
                        </div>
                        {isControlled=='1'?'是':'否'}
                    </div>
                    
                </div>
                <div className={styles.formRow}>
                    <div className={styles.halfLayout}>
                        <div className={styles.label}>
                            iconFont：
                        </div>
                        {iconFont}
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