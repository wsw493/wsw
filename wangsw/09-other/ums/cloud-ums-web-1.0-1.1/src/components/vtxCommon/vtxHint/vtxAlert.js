import React from 'react';
import {Alert} from 'antd';
import style from './vtxAlert.less';
class VtxAlert extends React.Component {
	constructor(props){
		super(props);
		this.state = {
			type: props.type || 'warning',
        	message: props.message||'警告提示',//警告提示内容
		}
		// console.log(props);
	}
	//关闭回调事件
	onClose(){
		let t = this;
		if('onClose' in t.props && typeof(t.props.onClose) === 'function'){
			t.props.onClose();
		}
	}	
	render(){
		let t=this;
		let _alert=t.props;
		let VtxAlertProps= {
			type: ('type' in _alert?_alert.type : 'warning'),
        	message: ('message' in _alert?_alert.message : '警告提示'),//警告提示内容
			closable:('closable' in _alert?_alert.closable : false),//默认不显示关闭按钮
        	closeText:('closeText' in _alert?_alert.closeText : ''),//自定义关闭按钮
        	description:('description' in _alert?_alert.description : false),//警告提示的辅助性文字介绍
        	showIcon:(_alert.banner?true:false),
        	banner: ('banner' in _alert?_alert.banner : false),
        	placement:('placement' in _alert?_alert.placement :''),
        	vtxStyle:('vtxStyle' in _alert?{..._alert.vtxStyle,display:'inline-block'} :{display:'inline-block'})
		}
		//关闭回调事件
		if('onClose' in _alert && typeof(_alert.onClose) === 'function'){
			VtxAlertProps.onClose = t.onClose.bind(t); 
		}
		let position;
		switch(VtxAlertProps.placement){
			case 'TC':position={position:'fixed',top:'10px',textAlign:'center',width:'100%'};break;
			case 'TL':position={position:'fixed',top:'10px',left:'0'};break;
			case 'TR':position={position:'fixed',top:'10px',right:'0'};break;
			case 'BC':position={position:'fixed',bottom:'20px',textAlign:'center',width:'100%'};break;
			case 'BL':position={position:'fixed',bottom:'20px',left:'0'};break;
			case 'BR':position={position:'fixed',bottom:'20px',right:'0'};break;
			case 'LC':position={position:'fixed',top:'48%',left:'0'};break;
			case 'LT':position={position:'fixed',top:'28%',left:'0'};break;
			case 'LB':position={position:'fixed',top:'68%',left:'0'};break;
			case 'RC':position={position:'fixed',top:'48%',right:'0'};break;
			case 'RT':position={position:'fixed',bottom:'68%',right:'0'};break;
			case 'RB':position={position:'fixed',bottom:'28%',right:'0'};break;
			default:position={position:'fixed',bottom:'50%',textAlign:'center',width:'100%'}
		}
	  	return (
		    <div className='clearfix' style={position}>
		      	<Alert {...VtxAlertProps} style={VtxAlertProps.vtxStyle}>
		      	</Alert>
		    </div>
	  	);
  	}
};

export default VtxAlert;
