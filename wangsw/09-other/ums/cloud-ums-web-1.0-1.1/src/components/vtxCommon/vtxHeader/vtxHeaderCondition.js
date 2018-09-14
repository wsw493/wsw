import React from 'react';
import {Icon,Tooltip} from 'antd';
import style from './vtxHeaderCondition.less';
import {Button,Radio,DatePicker} from 'antd';
const RadioGroup = Radio.Group;
import VtxGrid from '../VtxGrid/VtxGrid';
const {VtxRow,VtxCol} = VtxGrid;
import moment from 'moment';
const { MonthPicker, RangePicker } = DatePicker;
import {VtxDatePicker,VtxMonthPicker,VtxYearPicker} from '../VtxDate/VtxDate';
import {getDateByMoment,getTimeForDate} from '../../../config/config_yyz';

class VtxHeaderCondition extends React.Component {
	constructor(props){
		super(props);
		this.state = {
			day:props.day?{show:true,checked:true}:{},
			month:props.month?{show:true,checked:false}:{},
			year:props.year?{show:true,checked:false}:{},
			date:props.date,
			type:'day'
		}
		// console.log(props);
	}
	clickTimeType(value){
		console.log(value)
		let t=this;
		let day=t.state.day;
		let month=t.state.month;
		let year=t.state.year;
		let type=t.state.type;
		let date=t.state.date;
		switch(value){
			case 'day': day.checked=true;
						type='day';
						date=getDateByMoment('YYYY-MM-DD');
						if(day.checked){
							month.checked=false;
							year.checked=false;
						};break;
			case 'month': month.checked=true;
						type='month';
						date=getDateByMoment('YYYY-MM');
						if(month.checked){
							day.checked=false;
							year.checked=false;
						};break;
			case 'year': year.checked=true;
						type='year';
						date=getDateByMoment('YYYY');
						if(year.checked){
							month.checked=false;
							day.checked=false;
						};break;
		}
		t.setState({
			day,month,year,type,date
		})
	}
	clear(){
		let t=this;
		t.setState({
			date:getTimeForDate()[1]
		})
		t.clickTimeType('day');
		t.props.clear();
	}
	onChange(date, dateString) {
		let t=this;
	  	t.props.onChangeDate(date, dateString);
	}
	disabledMonth(current) {
        return current && current.valueOf() > moment(new Date()).valueOf();
    }
    disabledYear(current) {
        return current && current.valueOf() > new Date().getTime();
    }
	disabledDate(current){
		return current.valueOf() > moment().startOf('days').valueOf();
	}
	render(){
		let t=this;
	  	return (
		  		<VtxGrid 
		            titles={[
		                '时间类型','时间'
		            ]}
		            gridweight = {[2,1]}
		            confirm={()=>t.props.confirm(t.state.date,t.state.type)}
		            clear={t.clear.bind(t)}
		            showMore={true}
		            hiddenMoreButtion={true}
		            >
		             <RadioGroup onChange={(e)=>t.clickTimeType(e.target.value)} value={t.state.type}>
				        <Radio className={t.state.day.show?style.btn:'dis-n'} value='day'>按日统计</Radio>
				        <Radio className={t.state.month.show?style.btn:'dis-n'} value='month'>按月统计</Radio>
				        <Radio className={t.state.year.show?style.btn:'dis-n'} value='year'>按年统计</Radio>
				      </RadioGroup>
		           	<div>
		           	{
		    			t.state.day.checked?
		    			<VtxDatePicker 
			    			value={t.props.date} 
			    			showToday={true}
			    			style={{width:'100%'}}
			    			format='YYYY-MM-DD'
			    			disabledDate={t.disabledDate.bind(t)}
			    			onChange={t.onChange.bind(t)}/>
		    			:
		    			''
		    		}
		    		{
		    			t.state.month.checked
	    				?
	    				<VtxMonthPicker 
		    				value={t.props.date}
		    				style={{width:'100%'}}
		    				onChange={t.onChange.bind(t)}
		    				disabledDate={t.disabledMonth.bind(t)}
		    			/>
	    				:
	    				''
		    		}
		    		{
		    			t.state.year.checked
	    				?
	    				<VtxYearPicker 
		                    value={t.props.date}
		                    style={{width:'100%'}}
		    				onChange={t.onChange.bind(t)}
		    				disabledDate={t.disabledYear.bind(t)}
		                />
	    				:
	    				''
		    		}	
		           	</div>
	        </VtxGrid>
	  	);
  	}
  	componentDidMount(){
	}
	componentDidUpdate(prevProps, prevState) {//重新渲染结束
    }
	componentWillReceiveProps(nextProps) {//已加载组件，收到新的参数时调用
    	let t = this;
    	t.setState({
    		date : nextProps.date
    	})
    }
};

export default VtxHeaderCondition;