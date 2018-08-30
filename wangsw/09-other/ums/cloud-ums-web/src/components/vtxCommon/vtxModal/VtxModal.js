import React from 'react';
import {Modal} from 'antd';
import styles from './VtxModal.less';

const VtxModal = (props)=>{
    const wrapClassName = props.wrapClassName ? `${styles.vtxModal} ${props.wrapClassName}` : styles.vtxModal;
    const maskClosable = props.maskClosable===undefined?false:props.maskClosable;
    const newProps = {
        ...props,
        wrapClassName,
        maskClosable
    }
    return (
        <Modal {...newProps}>
        {props.children}
        </Modal>
    )
}

export default VtxModal;