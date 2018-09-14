import React from 'react';
import {Popconfirm} from 'antd';
class VtxPopconfirm extends React.Component {
	constructor(props){
		super(props);
		this.state = {
			/*visible: ('visible' in props?props.visible : false),*/
		}
		// console.log(props);
	}
	//显隐回调事件
	onVisibleChange(visible){
		let t = this;
		if('onVisibleChange' in t.props && typeof(t.props.onVisibleChange) === 'function'){
			t.props.onVisibleChange(visible);
		}
	}	
	onConfirm(){
		let t=this;
	}
	onCancel(){
		let t=this;
	}
	render(){
		let t=this;
		let _popconfirm=t.props;
		let VtxPopconfirmProps= {
			children:_popconfirm.children,
			onConfirm:'onConfirm' in _popconfirm && typeof(_popconfirm.onConfirm) === 'function'?_popconfirm.onConfirm:t.onConfirm.bind(t),
			onCancel:'onCancel' in _popconfirm && typeof(_popconfirm.onCancel) === 'function'?_popconfirm.onCancel:t.onCancel.bind(t),
			okText: _popconfirm.okText||'确认',
        	cancelText: _popconfirm.cancelText||'取消',
        	title:_popconfirm.title||'您确定继续此操作？',
        	placement:_popconfirm.placement||'topRight',
        	arrowPointAtCenter:_popconfirm.arrowPointAtCenter||false,
        	overlayClassName:_popconfirm.className,
        	overlayStyle:_popconfirm.style
		}
		//关闭回调事件
		if('onVisibleChange' in _popconfirm && typeof(_popconfirm.onVisibleChange) === 'function'){
			VtxPopconfirmProps.onVisibleChange = t.onVisibleChange.bind(t); 
		}
		if('visible' in  _popconfirm)
		{
			VtxPopconfirmProps.visible=_popconfirm.visible;
		}
	  	return (
		      	 <Popconfirm {...VtxPopconfirmProps}>
				    {VtxPopconfirmProps.children}
				 </Popconfirm>
	  	);
  	}
  	componentDidMount(){
	}
	componentDidUpdate(prevProps, prevState) {//重新渲染结束
    }
	/*componentWillReceiveProps(nextProps) {//已加载组件，收到新的参数时调用
    	let t = this;
    	t.setState({
    		visible : (nextProps.visible?nextProps.visible:t.state.visible)
    	})
    }*/
};

export default VtxPopconfirm;
