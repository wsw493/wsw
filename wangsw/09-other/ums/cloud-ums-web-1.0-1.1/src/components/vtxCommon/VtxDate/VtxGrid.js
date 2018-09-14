import React, { Component, PropTypes } from 'react';
import { connect } from 'dva';
import { Link } from 'dva/router';

import VtxRow from './VtxRow.js';
import VtxCol from './VtxCol.js';

import styles from './VtxGrid.less';

import {Button,Icon} from 'antd';

class VtxGrid extends React.Component{
    constructor(props){
        super(props);
        this.weightiness = 0;
        props.gridweight.map((item,index)=>{
            this.weightiness += item;
        })
        let height = 48,style={borderBottom: '1px solid #e1e1e1'};
        if(props.showAll || props.showMore){
            style = this.weightiness>4?{boxShadow: '0 1px 10px -3px #999'}:{borderBottom: '1px solid #e1e1e1'};
            height = this.weightiness>4?this.getHeight(this.weightiness):48;
        }
        this.state = {
            height: height,
            style: style,
            hiddenMoreButtion: props.hiddenMoreButtion || false,
            hiddenconfrimButtion: props.hiddenconfrimButtion || false,
            hiddenclearButtion: props.hiddenclearButtion || false,
        }
    }
    getHeight(w){
        return Math.ceil(w/4)*38 + 10;
    }
    isShowMore(weightiness){
        let t = this;
        let h = t.state.height;
        if(t.props.showAll || t.props.showMore){
            t.setState({
                height: t.getHeight(weightiness),
                style: {
                    boxShadow: '0 1px 10px -3px #999'
                }
            })
            return false;
        }
        if(h > 48){
            t.setState({
                height: 48,
                style: {
                    borderBottom: '1px solid #e1e1e1'
                }
            })
        }else{
            t.setState({
                height: t.getHeight(weightiness),
                style: {
                    boxShadow: '0 1px 10px -3px #999'
                }
            })
        }
    }
    render(){
        let t = this;
        let props = t.props;
        let render = (d,i)=>{
            // console.log(d.props.gridweight);
            let b = 4, c = 20,gwt = props.gridweight[i];
            if(gwt === 2){
                b = 2;
                c = 22;
            }
            if(gwt === 4){
                b = 1;
                c = 23;
            }
            return (
                <VtxCol key={i} span={6*gwt}>
                    <VtxRow gutter={2}>
                        <VtxCol span={b}><fieldName>{props.titles[i]}</fieldName></VtxCol>
                        <VtxCol span={c}><colon>：{d}</colon></VtxCol>
                    </VtxRow>
                </VtxCol>
            );
        }
        let analyzeChildern = (data)=>{
            if(!data)return '';
            if(!data.length){
                return render(data,0);
            }else{
                return data.map((item,index)=>{
                    return render(item,index);
                })
            }
        }
        return(
            <div className={`${styles.normal} ${t.props.className}`} style={{height: `${t.state.height}px`,...t.state.style}}>
                <VtxRow gutter={10}>
                    <VtxCol span={19} xl={{span:21}}>
                        <VtxRow gutter={10}>
                            {
                                analyzeChildern(props.children)
                            }
                        </VtxRow>
                    </VtxCol>
                    <VtxCol span={5} xl={{span:3}}>
                        <VtxRow gutter={10}>
                            {
                                t.state.hiddenconfrimButtion?"":
                                <VtxCol span={10}><Button style={{width:'100%'}} type="primary" onClick={props.confirm}>{props.confirmText || '查询'}</Button></VtxCol>
                            }
                            {
                                t.state.hiddenclearButtion?"":
                                <VtxCol span={10}><Button style={{width:'100%'}} onClick={props.clear}>{props.clearText || '清空'}</Button></VtxCol>
                            }
                            <VtxCol span={4}>
                                {
                                    this.weightiness > 4 && !t.state.hiddenMoreButtion?
                                    <Button type="primary" shape="circle" icon="ellipsis" onClick={()=>t.isShowMore(this.weightiness)}/>:
                                    ''
                                }
                            </VtxCol>
                        </VtxRow>
                    </VtxCol>
                </VtxRow>
            </div>
        );
    }
    componentWillReceiveProps(nextProps) {
        let t = this;
        if(this.weightiness > 4 && (t.props.showAll || t.props.showMore)){
            t.isShowMore(this.weightiness);
        }
        t.setState({
            hiddenMoreButtion: nextProps.hiddenMoreButtion || false,
            hiddenconfrimButtion: nextProps.hiddenconfrimButtion || false,
            hiddenclearButtion: nextProps.hiddenclearButtion || false,
        })
    }
}

VtxGrid.VtxRow = VtxRow;
VtxGrid.VtxCol = VtxCol;

export default VtxGrid;