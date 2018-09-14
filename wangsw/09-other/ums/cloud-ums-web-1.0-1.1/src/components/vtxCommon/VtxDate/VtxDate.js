import React, { Component, PropTypes } from 'react';
import { connect } from 'dva';
import { Link } from 'dva/router';

import VtxDatePicker from './VtxDatePicker';
import VtxMonthPicker from './VtxMonthPicker';
import VtxRangePicker from './VtxRangePicker';
import VtxTimePicker from './VtxTimePicker';
import VtxYearPicker from './VtxYearPicker';

function VtxDate(props) {
}

VtxDate.VtxDatePicker = VtxDatePicker;
VtxDate.VtxMonthPicker = VtxMonthPicker;
VtxDate.VtxRangePicker = VtxRangePicker;
VtxDate.VtxTimePicker = VtxTimePicker;
VtxDate.VtxYearPicker = VtxYearPicker;


export default VtxDate;