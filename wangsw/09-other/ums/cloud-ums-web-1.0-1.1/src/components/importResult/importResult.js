import React from 'react';
import VtxModal from '../vtxCommon/vtxModal/VtxModal';
import VtxDataGrid from '../vtxCommon/VortexDatagrid/VortexDatagrid';
import VtxGrid from '../vtxCommon/VtxGrid/VtxGrid';
import { Select ,Button} from 'antd';
import styles from './item.less';

const Option = Select.Option;
const ImportResult = (props)=>{
    const {modalProps,contentProps } = props;

    const {gridProps,querySelect,clearQuery,importDetail,updateImportDetail} = contentProps;
    return (
        <VtxModal {...modalProps}>
            <div className={styles.main}>
                <VtxGrid 
                titles={['状态']}
                gridweight={[1]}
                confirm={querySelect}
                clear={clearQuery}
                className={styles.searchHead}
                >
                <Select value={importDetail.successful} onChange={(value,Option)=>{
                    updateImportDetail({successful:value});
                }} style={{width: '200px','margin-bottom': '5px'}}>
                    <Option value=''>全部</Option>
                    <Option value='true'>成功</Option>
                    <Option value='false'>失败</Option>
                </Select>
                </VtxGrid>
                <div className={styles.table}>
                    <VtxDataGrid {...gridProps}/>
                </div>
            </div>
        </VtxModal>
    )
}

export default ImportResult;