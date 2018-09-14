import React, { Component, PropTypes } from 'react';
import { connect } from 'dva';
import { Link } from 'dva/router';
import { Col,Tooltip } from 'antd';
import styles from './VtxGrid.less';
//获取方法名,通过apply引用  如getFunctionName.apply(funa)  //输出'funa'
function VtxCol(props) {
    var getFunctionName = function(){
        return this.name || this.toString().match(/function\s*([^(]*)\(/)[1];
    }
    let addStyle = (d)=>{
        if(typeof(d.type) === 'function' && d.props.hasOwnProperty('attr') && d.props.attr == 'row'){
            return d;
        }else{
            if(d.type === 'colon'){
                return(
                    <div className={styles.Lists}>
                        <div className={styles.colon}>{d.props.children[0]}</div>
                        <div className={styles.list}>{d.props.children[1]}</div>
                    </div>
                );
            }
            let sty ={
                lineHeight: '28px',
                height: '38px'
            }
            if(d.type === 'fieldName'){
                sty = {
                    ...sty,
                    padding: '5px 0px',
                    overflow: 'hidden',
                    textOverflow: 'ellipsis',
                    whiteSpace: 'nowrap',
                    textAlign: 'right',
                    cursor: 'default'
                }
            }
            if(d.type === 'bt'){
                sty ={
                    ...sty,
                    padding: '5px 0px'
                }
            }
            return (
                <div style={{...sty}}>
                    {
                        d.type === 'fieldName'?
                        <Tooltip placement="rightTop" title={d.props.children}>
                            {d}
                        </Tooltip>
                        :
                        d
                    }
                </div>
            )
        }
    }
    let render = (d)=>{
        if(d == '')return '';
        if(!d.length){
            return addStyle(d);
        }
        return d.map((item,index)=>{
           return addStyle(item);
        });
    }
    return (
        <Col {...props}>
            {
                render(props.children)
            }
        </Col>
    );
}

VtxCol.propTypes = {
};
export default VtxCol;
