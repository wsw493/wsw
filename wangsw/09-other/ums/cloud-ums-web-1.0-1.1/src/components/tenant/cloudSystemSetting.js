import React from 'react';
import VtxModal from '../vtxCommon/vtxModal/VtxModal';
import VtxDataGrid from '../vtxCommon/VortexDatagrid/VortexDatagrid';
import VtxGrid from '../vtxCommon/VtxGrid/VtxGrid';
import { Input ,Button} from 'antd';
import styles from './item.less';

const CloudSystem = (props)=>{
    const {modalProps,contentProps } = props;

    const {gridProps,querySelect,clearQuery,systemName,querySystemNameChanged} = contentProps;
    return (
        <VtxModal {...modalProps}>
            <div className={styles.main}>
                <VtxGrid 
                titles={['系统名称']}
                gridweight={[1]}
                confirm={querySelect}
                clear={clearQuery}
                className={styles.searchHead}
                >
                    <Input value={systemName} onChange={querySystemNameChanged} placeholder='输入系统名称'/>
                </VtxGrid>
                <div className={styles.table}>
                    <VtxDataGrid {...gridProps}/>
                </div>
            </div>
        </VtxModal>
    )
}

export default CloudSystem;