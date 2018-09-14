import React, { Component, PropTypes } from 'react';
import { connect } from 'dva';
import { Link } from 'dva/router';

import moment from 'moment';
import 'moment/locale/zh-cn';
moment.locale('zh-cn');

import {DatePicker} from 'antd';
const {RangePicker} = DatePicker;
function VtxRangePicker(props) {
    const {
        showTime,format,allowClear,disabled,open,
        value,placeholder,size,style,disabledDate,
        onChange,onOpenChange,disabledTime,onOk
    } = props;

    let DatePickerProps = {
        allowClear: allowClear || false,
        showTime: showTime || false,
        disabled: disabled || false,

        format: format || (!showTime?'YYYY-MM-DD':'YYYY-MM-DD HH:mm:ss'),
        // value: !value || value == []? [null,null]: [moment(value[0],(format || (!showTime?'YYYY-MM-DD':'YYYY-MM-DD HH:mm:ss'))),moment(value[1],(format || (!showTime?'YYYY-MM-DD':'YYYY-MM-DD HH:mm:ss')))],
        value: !value
            ?[null,null]
            :[
            value[0]
                ?moment(value[0],(format || (!showTime?'YYYY-MM-DD':'YYYY-MM-DD HH:mm:ss')))
                :null,
            value[1]
                ?moment(value[1],(format || (!showTime?'YYYY-MM-DD':'YYYY-MM-DD HH:mm:ss')))
                :null
            ],
        style: style || {},
        placeholder: placeholder?[placeholder,placeholder] : ['请选择时间','请选择时间'],
        size: size || 'default',

        onChange: onChange,
        onOpenChange: onOpenChange,
        disabledTime: disabledTime,
        onOk: onOk
    }
    if('open' in  props){
        DatePickerProps.open = open;
    }
    if('disabledDate' in props){
        if(typeof(disabledDate) === 'function'){
            DatePickerProps.disabledDate = disabledDate;
        }
        // DatePickerProps.disabledDate = disabledDateFun;
    }
    return(
        <RangePicker {...DatePickerProps}/>
    )
}

export default VtxRangePicker;