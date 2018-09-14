import React from 'react';
import {Icon,Tooltip} from 'antd';
import style from './vtxRighttool.less';
var Timer;
class VtxRightTool extends React.Component {
	constructor(props){
		super(props);
		this.state = {
			checkedKeys:[],//记录点击的key并返回
			len:0
		}
		// console.log(props);
	}
	//标题点击事件
	onClick(item,index){
		console.log(11)
		let t=this;
		if('onClick' in t.props && typeof(t.props.onClick) === 'function'&&!('hover' in t.props && typeof(t.props.hover) === 'function')){
			item.brandchShow=!item.brandchShow;
			t.props.onClick(item,index);
		}
	}
	//标题hover事件
	hover(item,isShow,index){
		let t=this;
		if('hover' in t.props && typeof(t.props.hover) === 'function'&&!('onClick' in t.props && typeof(t.props.onClick) === 'function')){
			item.brandchShow=isShow;
			t.props.hover(item,index);
		}
	}
	//子标题点击事件
	onSelect(e,data,item,index){
		let t=this;
		e.preventDefault();
    	e.stopPropagation();//阻止冒泡
    	if(data.multiple)
    	{
    		item.checked=!item.checked;
    		if(JSON.stringify(t.state.checkedKeys).indexOf(item.key)==-1)
    		{
    			if(item.checked){
    				t.state.checkedKeys.push(item.key);
    			}
    		}else{
    			t.state.checkedKeys=t.state.checkedKeys.filter((itemM)=>{return itemM!=item.key});
    			
    		}
    		t.setState({
    			checkedKeys:t.state.checkedKeys
    		})
    	}else{
    		for(var i=0;i<data.children.length;i++){
    			if(index==i){
    				if(item.checked){
    					data.children[index].checked=false;
    					item.checked=false;
    				}else{
    					data.children[index].checked=true;
    					item.checked=true;
    				}
    			}else{
    				data.children[i].checked=false;
    			}
    		}
			if(JSON.stringify(t.state.checkedKeys).indexOf(item.key)==-1)
			{
				if(item.checked){
					t.state.checkedKeys.splice(0,1,item.key);
				}
			}else{
				t.state.checkedKeys.splice(0,1);
			}	
    		t.setState({
    			checkedKeys:t.state.checkedKeys
    		})
    	}
		if('onSelect' in t.props && typeof(t.props.onSelect) === 'function'){
			let checkedKeys=t.state.checkedKeys.filter((item)=>
			{
				let d=false;
				data.children.map((itemM)=>{
					if(itemM.key==item){
						d=true;
					}
				})
				return d;
			})
			t.props.onSelect(checkedKeys,item,index);
			return false;
		}
	}
	//遍历子标题
	renderBranch(data,item,index){
		let t=this;
		if(!item.key) 
			console.error(data.title+"下第"+(index+1)+"个title缺少key");
		let VtxTitleProps={
			...item,
			className:style.branchTitle,
			style:{border: '1px solid #ddd'}
		}

		//checked默认为false
		if(!('checked' in item)){
			item.checked=false;
		}
		return(
			<li 
				key={index} 
				onClick={e=>t.onSelect(e,data,item,index)} 
				style={item.style} 
				className={item.checked?`${style.selected} ${style.vtxSelectli} ${style.className}`:`${style.vtxSelectli} ${item.className}`}>
				<VtxTitle {...VtxTitleProps}/>
			</li>
		)
	}
	//遍历导航栏title
	renderTool(item,index){
		let t=this;
		if(!item.key) 
			console.error("右侧浮标第"+(index+1)+"个title缺少key");
		let branchList=[];
		if(item.children&&Object.prototype.toString.call(item.children) === '[object Array]')
			branchList=item.children.map(t.renderBranch.bind(t,item));
		/*let showStyle=item.show?'':'display:none';*/

		if('style' in item)
		{
			item.style={...item.style,width:item.width}
		}else{
			item.style={width:item.width}
		} 

		//filters默认 false
		if(!('filters' in item)){
			item.filters=false;
		}

		//filters默认 false
		if(!('show' in item)){
			item.show=true;
		}

		//multiple默认 false
		if(!('multiple' in item)){
			item.multiple=false;
		}
		//haveEvent默认false
		if(!('haveEvent' in item)){
			item.haveEvent=false;
		}
		
		//brandchShow默认false
		if(!('brandchShow' in item)){
			item.brandchShow=false;
		}

		//当有onClick事件时，haveEvent默认为true
		if('onClick' in t.props && typeof(t.props.onClick) === 'function'&&!('hover' in t.props && typeof(t.props.hover) === 'function')){
			item.haveEvent=true;
		}
		let VtxTitleProps={
			...item,
			className:style.titleWrapper,
			style:index<t.state.len-1?{borderRight: '1px solid #ddd'}:{}
		}
		return( 
			<div key={index} style={item.style} className={item.show?`${style.rightToolItem} ${item.className}`:style.displayNone}>
				<div onMouseOver={t.hover.bind(t,item,true,index)} onMouseOut={t.hover.bind(t,item,false,index)} onClick={item.haveEvent?t.onClick.bind(t,item,index):''} className={style.toolbox}>
					<VtxTitle {...VtxTitleProps}/>
					{
						item.filters?
						<ul className={item.brandchShow?style.toolBranch:style.displayNone}>
							{branchList}
						</ul>
						:''
					}
				</div>
			</div>
		)
	}
	render(){
		let t=this;
		let _righttool=t.props;
		let VtxRightToolProps= {
			data: _righttool.data||[],
			knock:_righttool.knock||true,//是否含有时钟
        	top:_righttool.top||15,
        	right:_righttool.right||15,
        	className:_righttool.className,
        	style:{..._righttool.style,top:_righttool.top+'px',right:_righttool.right+'px'}
		}
		//右侧浮标子项点击回调
		if('onClick' in _righttool && typeof(_righttool.onClick) === 'function'&&!('hover' in _righttool && typeof(_righttool.hover) === 'function')){
			VtxRightToolProps.onClick = _righttool.onClick; 
		}
		//右侧浮标子项hover回调
		if('hover' in _righttool && typeof(_righttool.hover) === 'function'&&!('onClick' in _righttool && typeof(_righttool.onClick) === 'function')){
			VtxRightToolProps.hover = _righttool.hover;
		}
		//右侧浮标子项下拉点击回调
		if('onSelect' in _righttool && typeof(_righttool.onSelect) === 'function'){
			VtxRightToolProps.onSelect = _righttool.onSelect; 
		}
		t.state.len=0;
		t.props.data.map((item)=>{
				if(item.show||!('show' in item))
				{
					t.state.len=t.state.len+1;		
				}
			}
		);
		let toolList=VtxRightToolProps.data.map(t.renderTool.bind(t));
	  	return (
		      	<div className={`${VtxRightToolProps.className} ${style.rightToolWrapper}`} style={VtxRightToolProps.style}>
		      		<div className={style.topRight}>
			      		<div className={VtxRightToolProps.knock?style.rightToolItem:style.displayNone} style={{width:'150px'}}>
							<Knock/>
						</div>
			      		{toolList}
		      		</div>
		      	</div>
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

export default VtxRightTool;
class VtxTitle extends React.Component {
	constructor(props){
		super(props);
		this.state = {
			/*visible: ('visible' in props?props.visible : false),*/
		}
		// console.log(props);
	}
	render(){
		let titleProps=this.props;
	  	return (
		      	<Tooltip placement="right" title={titleProps.title}>
		      		<div className={titleProps.className} style={titleProps.style}>
						{
							!!titleProps.icon ?
							<div className={style.titleContent}>
								<i className={`iconfont ${titleProps.icon} ${titleProps.iconClassName || ''}`}
									style={{'verticalAlign':'middle','marginRight':'6px'}}></i>
								{titleProps.title}
							</div>
							:
							(
								!!titleProps.img ?
								<div className={style.titleContent}>
									<img src={titleProps.img} alt=""
										style={{'width':'16px','height':'16px','verticalAlign':'middle','marginRight':'6px'}}/>
									{titleProps.title}
								</div>
								:
								<div className={style.titleContent}>
									{titleProps.title}
								</div>
							)
						}
					</div>
				</Tooltip>
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
}
class Knock extends React.Component{
  constructor(props){
    super(props);
    //初始化时间
    let time = new Date().getTime();
    this.state={
      time: time,
      date: this.formatDate(time),
    }
  }
  //时间递增方法
  getFormatTime(){
    let t = this;
    let time = t.state.time;
    if(Timer)
      clearTimeout(Timer);
    Timer=setTimeout(()=>{
      time += 1000;
      t.setState({
        date: t.formatDate(time),
        time:time
      })
      t.getFormatTime();
    },999);
  }
  //格式化数据
  formatDate(time){
      var _d=new Date(time);
      return _d.getFullYear() + "-" 
          +(_d.getMonth()+1<10?'0'+(_d.getMonth()+1):_d.getMonth()+1) + "-" 
          + (_d.getDate()<10?'0'+(_d.getDate()):_d.getDate()) + " " 
          + (_d.getHours()<10?'0'+(_d.getHours()):_d.getHours()) + ":" 
          + (_d.getMinutes()<10?'0'+(_d.getMinutes()):_d.getMinutes()) + ":" 
          + (_d.getSeconds()<10?'0'+(_d.getSeconds()):_d.getSeconds());
  }
  render(){
    return(
      <div className={style.knockWrapper}>
        <div className={style.titleWrapper} style={{cursor:'default'}}>
            <span>
              {this.state.date}
            </span>
        </div>
      </div>
    );
  }
  componentDidMount(){
    let t = this;
    t.getFormatTime()
  }
  componentWillUnmount() {//移除真是的DOM(等于关闭单页时调用)
    clearTimeout(Timer);
  }
}
