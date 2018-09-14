import React from 'react';
import { Select } from 'antd';
import styles from './stateSelect.less';

const StateSelect = (props)=>{
    const {errorMsg=' ', validated=true} = props;
    const selectProps = {
        style:{width:300},
        ...props,
    };
    delete selectProps.errorMsg;
    delete selectProps.validated;
    return (
        <div className={validated? styles.normal: styles.error} data-errorMsg={errorMsg}>
            <Select {...selectProps}/>
        </div>
    )
}

export default StateSelect