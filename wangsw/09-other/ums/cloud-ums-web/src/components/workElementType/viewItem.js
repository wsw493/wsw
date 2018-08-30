import React from 'react';
import VtxModal from '../vtxCommon/vtxModal/VtxModal';
import { Input, Icon, Radio,Select,AutoComplete, Checkbox ,Button} from 'antd';
import styles from './item.less';

const RadioGroup = Radio.Group;
const Option = Select.Option;
const CheckboxGroup = Checkbox.Group;
import {validate} from '../regExpression';

const ViewItem = (props)=>{
    const {modalProps,contentProps } = props;
    const {id,loading,visible,checkState,code,name,shape,departmentId,departmentName,info,orderIndex,codeUnique,orgTreeData,
     nameUnique,} = contentProps;
    var shapeName = '';
    switch(shape){
        case "point":
            shapeName = "点";
            break;
        case "line":
        case "polyline":
            shapeName = "多折线";
            break;
        case "polygon":
            shapeName = "多边形";
            break;
        case "rectangle":
            shapeName = "矩形";
            break;
        case "circle":
            shapeName = "圆形";
            break;
    }
    return (
        <VtxModal {...modalProps}>
            <div className={styles.formWrapper}>
                <div className={styles.formRow}>
                    <div className={styles.halfLayout}>
                        <div className={styles.requiredLabel}>
                            编号：
                        </div>
                       {code}
                    </div>
                    <div className={styles.halfLayout}>
                        <div className={styles.requiredLabel}>
                            类型名称：
                        </div>
                        {name}
                    </div>
                </div>
                <div className={styles.formRow}>
                    <div className={styles.halfLayout}>
                        <div className={styles.requiredLabel}>
                            外形：
                        </div>
                        {shapeName}
                    </div>
                    <div className={styles.halfLayout}>
                        <div className={styles.label}>
                            所属机构：
                        </div>
                        {departmentName}
                    </div>
                </div>
                <div className={styles.formRow}>
                    <div className={styles.halfLayout}>
                        <div className={styles.label}>
                            详细信息：
                        </div>
                        {info}
                    </div>
                </div>
            </div>
            
        </VtxModal>
    )
}

export default ViewItem;