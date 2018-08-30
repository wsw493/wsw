import React, { Component, PropTypes } from 'react';
import { connect } from 'dva';
import { Link } from 'dva/router';

import moment from 'moment';
import 'moment/locale/zh-cn';
moment.locale('zh-cn');

import { TimePicker } from 'antd';

function VtxTimePicker(props) {
    const {
        value,className,popupClassName,
        open,format,disabled,hideDisabledOptions,placeholder,
        onOpenChange,onChange,disabledHours,disabledMinutes,disabledSeconds,addon
    } = props;

    let TimePickerProps = {
        value: value?moment(value,(format ||'HH:mm:ss')):null,

        className: className,
        popupClassName: popupClassName,
        disabled: disabled || false,
        hideDisabledOptions: hideDisabledOptions || false,
        format: format || 'HH:mm:ss',
        placeholder: placeholder || '请选择时间',

        onChange: onChange,
        onOpenChange: onOpenChange,
        disabledHours: disabledHours,
        disabledMinutes: disabledMinutes,
        disabledSeconds: disabledSeconds,
        addon: addon,
    }
    if('open' in  props){
        TimePickerProps.open = open;
    }
    return(
        <TimePicker {...TimePickerProps}/>
    )
}

export default VtxTimePicker;