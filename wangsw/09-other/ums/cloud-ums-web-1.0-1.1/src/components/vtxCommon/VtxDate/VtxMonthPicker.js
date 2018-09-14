import React, { Component, PropTypes } from 'react';
import { connect } from 'dva';
import { Link } from 'dva/router';

import moment from 'moment';
import 'moment/locale/zh-cn';
moment.locale('zh-cn');

import { DatePicker } from 'antd';
const {MonthPicker} = DatePicker;

function VtxMonthPicker(props) {
    const {
        allowClear,disabled,open,style,placeholder,size,
        value,
        onChange,onOpenChange,disabledDate
    } = props;

    let MonthPickerProps = {
        allowClear: allowClear || false,
        disabled: disabled || false,
        style: style || {},
        placeholder: placeholder || '请选择时间',
        size: size || 'default',
        value: value ? moment(value,'YYYY-MM'):null,

        onChange: onChange,
        onOpenChange: onOpenChange,
        disabledDate: disabledDate,
        format: 'YYYY-MM',
    }
    if('open' in  props){
        DatePickerProps.open = open;
    }
    return(
        <MonthPicker {...MonthPickerProps}/>
    )
}

export default VtxMonthPicker;
