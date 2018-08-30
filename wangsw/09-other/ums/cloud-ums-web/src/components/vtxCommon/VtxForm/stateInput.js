import React from 'react';
import { Input, Icon } from 'antd';
import styles from './stateInput.less';

const stateInput = (props)=>{
    const {errorMsg=' ', validated=true} = props;
    const inputProps = {
        suffix: validated ? null : <Icon type="close-circle" style={{color:'red'}}/>,
        style:{width:300},
        ...props,
    };
    delete inputProps.errorMsg;
    delete inputProps.validated;
    return (
        <div className={validated? styles.normal: styles.error} data-errorMsg={errorMsg}>
            <Input {...inputProps}/>
        </div>
    )
}

export default stateInput