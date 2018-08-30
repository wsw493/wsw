import React from 'react';
import {Button} from 'antd';
import styles from './item.less';
import {VtxMap} from 'vtx-ui';
import VtxModal from '../vtxCommon/vtxModal/VtxModal';

const ViewItem = (props)=>{
    const {modalProps,contentProps } = props;

    const {id,checkState,parentId,parentName,orgCode,orgName,head,headMobile,address,email,description,orderIndex,divisionName} = contentProps;

    return (
        <VtxModal {...modalProps}>
            <div className={styles.formWrapper}>
                <div className={styles.formRow}>
                    <div className={styles.halfLayout}>
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
                        {orgCode}
                    </div>
                </div>
                <div className={styles.formRow}>
                    <div className={styles.halfLayout}>
                        <div className={styles.requiredLabel}>
                            名称：
                        </div>
                        {orgName}
                    </div>
                </div>
                <div className={styles.formRow}>
                    <div className={styles.halfLayout}>
                        <div className={styles.label}>
                            负责人：
                        </div>
                        {head}
                    </div>
                </div>
                <div className={styles.formRow}>
                    <div className={styles.halfLayout}>
                        <div className={styles.label}>
                            负责人手机号：
                        </div>
                        {headMobile}
                    </div>
                </div>
                <div className={styles.formRow}>
                    <div className={styles.halfLayout}>
                        <div className={styles.label}>
                            邮箱：
                        </div>
                        {email}
                    </div>
                </div>
                <div className={styles.formRow}>
                    <div className={styles.halfLayout}>
                        <div className={styles.label}>
                            排序号：
                        </div>
                        {orderIndex}
                    </div>
                </div>
                <div className={styles.formRow}>
                    <div className={styles.halfLayout}>
                        <div className={styles.label}>
                            行政区划：
                        </div>
                        {divisionName}
                    </div>
                </div>
                <div className={styles.formRow}>
                    <div className={styles.halfLayout}>
                        <div className={styles.label}>
                            描述：
                        </div>
                        {description}
                    </div>
                </div>
                <div className={styles.formRow}>
                    <div className={styles.halfLayout} style={{width:'100%'}}>
                        <div className={styles.label}>
                            地址：
                        </div>
                        {address}
                    </div>
                </div>
            </div>
            
        </VtxModal>
    )
}

export default ViewItem;