import React, {Component,PropTypes} from 'react';
import styles from './VtxTreeSelect.less';

import { TreeSelect , Tooltip } from 'antd';
const TreeNode = TreeSelect.TreeNode;
class VtxTreeSelect extends React.Component{
    constructor(props){
        super(props);
        this.disableCheckboxKeys = [];
        this.disableKeys = [];
        this.id = `treeSelect${new Date().getTime()}${Math.random()}`
        this.state = {
            value: []
        };
    }
    onChange(value, label, extra){
        let t = this,v = [],l = [],treeNodes=[],parents=[];
        //单选时,返回的value是字符串,转成数组统一操作
        if(typeof(value) === 'string'){
            value = [value];
        }
        //判断当前选择的节点是否是disabled的数据
        if(value && t.disableKeys.indexOf(value[value.length - 1]) !== -1){
            return false;
        }
        //v == value 未disabled的value,l == label未disabled的label;
        if('onChange' in t.props && typeof(t.props.onChange) === 'function'){
            if(t.props.treeCheckable){//多选时
                value.map((item,index)=>{
                    if(t.disableCheckboxKeys.indexOf(item) == -1){
                        let tn = t.getTreeNodeByKey(t.props.data,item);
                        v.push(item);
                        l.push(label[index]);
                        treeNodes.push(tn.treeNode);
                        parents.push(tn.parent);
                    }
                });
                t.props.onChange({allValue: value,allLabel: label,value: v, label: l,treeNodes,parents});
            }else{
                t.props.onChange({allValue: value,allLabel: label,value: value, label: label,treeNodes,parents});
            }
        }
        this.setState({
            value: value || []
        });
    }
    //动态加载树数据
    onLoadData(node){
        let t = this;
        let key = node.props.eventKey;
        let isExpand = node.props.expanded;
        let treeNode = t.getTreeNodeByKey(t.props.data,key).treeNode;
        treeNode.children = !treeNode.children?[]:treeNode.children;
        if(treeNode.children.length === 0){
            return new Promise((resolve) => {
                this.props.onLoadData({key,treeNode,isExpand,resolve});
            })
        }else{
            return new Promise((resolve) => {
                resolve();
            })
        }
    }
    //通过key获取对应的treeNode
    getTreeNodeByKey(data,key){
        let t = this;
        let treeNode,parent;
        let loop = (item,index,p)=>{
            if(item.key == key){
                treeNode = item;
                if(p){
                    parent = p.key;
                }
                return true;
            }
            if('children' in item && t.isArray(item.children)){
                t.traverse(item.children,loop,item);
            }
        }
        t.traverse(data,loop);
        return {treeNode,parent};
    }
    //遍历tree数据方法
    traverse(ary,backcall,p){
        ary.map((item,index)=>{
            backcall(item,index,p);
        });
    }
    //判断对应参数是否是数组
    isArray(ary){
        return Object.prototype.toString.call(ary) === '[object Array]';
    }
    render(){
        let t = this;
        let treeSelect = t.props;
        let TreeSelectProps = {
            style: treeSelect.style || { width: 300 },
            dropdownStyle: treeSelect.dropdownStyle || { maxHeight: 400, overflow: 'auto' },
            value: treeSelect.value || t.state.value,
            treeDefaultExpandedKeys: treeSelect.treeDefaultExpandedKeys || [],

            showSearch: treeSelect.treeCheckable || treeSelect.multiple?false:(treeSelect.showSearch || false),
            multiple: treeSelect.treeCheckable || treeSelect.multiple || false,
            treeCheckable: treeSelect.treeCheckable || false,
            disabled: treeSelect.disabled || false,
            treeDefaultExpandAll: treeSelect.treeDefaultExpandAll || false,

            onChange: t.onChange.bind(t),
            onSelect: t.onChange.bind(t),

            treeNodeLabelProp: 'name',
            treeNodeFilterProp: 'name',
            placeholder: treeSelect.placeholder || '',
            searchPlaceholder: treeSelect.searchPlaceholder || '',
            getPopupContainer: ()=>document.getElementById(this.id)
        }
        //动态加载树数据
        if('onLoadData' in treeSelect){
            if(typeof(treeSelect.onLoadData) === 'function')
                TreeSelectProps.loadData = t.onLoadData.bind(t);
            else
                console.error('warn: VtxTreeSelect data: onLoadData is not a function!');
        }
        //加载节点树
        let loop = (data) => {
            //检索传入树的数据格式是否正确
            if(typeof(data) !== 'object' || (!data.length && data.length !== 0)){
                console.error('warn: VtxTreeSelect data: Data type error!');
                return false;
            }
            let render = data.map((item,index)=>{
                let name = item.name;
                let disabledClass = item.disabled || treeSelect.disabledAll?'disable':'';
                let _title = (
                    !!item.icon ?
                    <div className={`stNode ${disabledClass}`} onClick={(e)=>{
                        if(item.disabled || treeSelect.disabledAll){
                            e.stopPropagation();
                            e.nativeEvent.stopImmediatePropagation();
                            return false;
                        }
                    }}>
                        <i className={`iconfont ${item.icon} ${item.iconClassName || ''} icf`}></i>
                        {name}
                    </div>
                    :
                    (
                        !!item.img ?
                        <div className={`stNode ${disabledClass}`} onClick={(e)=>{
                            if(item.disabled || treeSelect.disabledAll){
                                e.stopPropagation();
                                e.nativeEvent.stopImmediatePropagation();
                                return false;
                            }
                        }}>
                            <img src={item.img} alt="" className={'imgs'}/>
                            {name}
                        </div>
                        :
                        <div className={`stNode ${disabledClass}`} onClick={(e)=>{
                            if(item.disabled || treeSelect.disabledAll){
                                e.stopPropagation();
                                e.nativeEvent.stopImmediatePropagation();
                                return false;
                            }
                        }}>
                            {name}
                        </div>
                    )
                );
                _title = (
                    <Tooltip placement="right" title={name}>
                        {_title}
                    </Tooltip>
                );
                let TreeNodeProps = {
                    // disabled: item.disabled || (treeSelect.disabledAll?true:false),
                    disableCheckbox:  item.disableCheckbox || (treeSelect.disableCheckboxAll?true:false),
                    title: _title,
                    key: item.key,
                    name: name,
                    value: item.key,
                    isLeaf: item.isLeaf || false,
                }
                return(
                    <TreeNode {...TreeNodeProps} >
                    {
                        //子节点数据处理,避免数据异常
                        (('children' in item) && t.isArray(item.children))?
                        loop(item.children):''
                    }
                    </TreeNode>
                );
            });
            return render;
        }
        let requiredCheck = treeSelect.required && this.state.value.length==0;
        return (
            <div className={requiredCheck ? styles.error: '' } data-errorMsg={'必填'} style={{display: 'inline-block'}}>
                <TreeSelect {...TreeSelectProps}>
                    {
                        loop(treeSelect.data)
                    }
                </TreeSelect>
                <div id={this.id} className={styles.vtxtreeselect}></div>
            </div>
        );
    }
    componentDidMount(){
        let t = this;
        let disableCheckboxKeys = [], disableKeys =[];
        let getKeys = (data)=>{
            data.map((item,index)=>{
                if(item.disabled || t.props.disabledAll){
                    t.disableKeys.push(item.key);
                }
                if(item.disableCheckbox || t.props.disableCheckboxAll){
                    t.disableCheckboxKeys.push(item.key);
                }
                if(t.isArray(item.children)){
                    getKeys(item.children);
                }
            });
        }
        //记录下所有disabled的keys,用于阻断选择事件
        getKeys(t.props.data);
    }
    componentDidUpdate(prevProps, prevState) {//重新渲染结束
        let t = this;
        let disableCheckboxKeys = [], disableKeys =[];
        let getKeys = (data)=>{
            data.map((item,index)=>{
                if(item.disabled || t.props.disabledAll){
                    t.disableKeys.push(item.key);
                }
                if(item.disableCheckbox || t.props.disableCheckboxAll){
                    t.disableCheckboxKeys.push(item.key);
                }
                if(t.isArray(item.children)){
                    getKeys(item.children);
                }
            });
        }
        //记录下所有disabled的keys,用于阻断选择事件
        getKeys(t.props.data);
    }
    componentWillReceiveProps(nextProps) {//已加载组件，收到新的参数时调用
        let t = this;
    }
}

export default VtxTreeSelect;