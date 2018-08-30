import React from 'react';
import VtxModal from '../../vtxCommon/vtxModal/VtxModal';
import VortexUpload from '../../vtxCommon/VortexUpload/VortexUpload';
import styles from './item.less';

const ViewItem = (props)=>{
    const {modalProps,contentProps } = props;

    const {id,userName,phone,profilePhoto,gender,birthday} = contentProps;
    const {fileListVersion} = contentProps;

    const uploadProps = {
        fileList:profilePhoto?JSON.parse(profilePhoto):[],
        mode: 'single',
        listType:"picture-card",
        accept: "image/png, image/jpeg", // 接受上传的文件类型
        fileListVersion,
        viewMode: true
    }
    return (
        <VtxModal {...modalProps}>
            <div className={styles.formWrapper}>
                <div className={styles.formRow}>
                    <div className={styles.halfLayout}>
                        <div className={styles.requiredLabel}>
                            手机号：
                        </div>
                        {phone}
                    </div>
                <div className={styles.formRow}>
                </div>
                    <div className={styles.halfLayout}>
                        <div className={styles.requiredLabel}>
                            用户名：
                        </div>
                        {userName}
                    </div>
                </div>
                <div className={styles.formRow}>
                    <div className={styles.halfLayout}>
                        <div className={styles.label}>
                            性别：
                        </div>
                        {gender=='M'?'男':'女'}
                    </div>
                <div className={styles.formRow}>
                </div>
                    <div className={styles.halfLayout}>
                        <div className={styles.label}>
                            生日：
                        </div>
                        {birthday}
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

export default ViewItem;