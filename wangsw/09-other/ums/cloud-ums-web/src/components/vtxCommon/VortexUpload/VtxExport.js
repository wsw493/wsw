import React from 'react';
import { Dropdown,Menu,Button,Icon } from 'antd';

class VtxExport extends React.Component{
    constructor(props){
        super(props);
        this.downloadURL = props.downloadURL;
        this.exportButtonClick = this.exportButtonClick.bind(this);
    }
    exportButtonClick(param){
        let pass_val = typeof(this.props.getExportParams)=='function'? this.props.getExportParams(param.key):null;
        pass_val && this.downLoadFile(this.downloadURL,'parameters',JSON.stringify(pass_val));
    }
    downLoadFile(reqURL,paramName,paramVal){
        var formDom = document.createElement('form');
        formDom.style.display='none';
        formDom.setAttribute('target','');
        formDom.setAttribute('method','post');
        formDom.setAttribute('action',this.downloadURL);
        
        var input = document.createElement('input');
        input.setAttribute('type','hidden');
        input.setAttribute('name',paramName);
        input.setAttribute('value',paramVal);

        document.body.appendChild(formDom);
        formDom.appendChild(input);
        formDom.submit();
        formDom.parentNode.removeChild(formDom);
    }
    render(){
        let props = this.props;
        const exportMenu = <Menu onClick={this.exportButtonClick}>
            {this.props.rowButton===false?null:<Menu.Item key="rows">导出选中行</Menu.Item>}
            {this.props.pageButton===false?null:<Menu.Item key="page" >导出当前页</Menu.Item>}
            {this.props.allButton===false?null:<Menu.Item key="all">导出全部</Menu.Item>}
        </Menu>
        return (
            <Dropdown overlay={exportMenu} trigger={["click"]}>
                <Button icon="export">
                    导出 <Icon type="down" />
                </Button>
            </Dropdown>  
        )
    }
}

// function exportFile(ids=undefined){
//     // columnFields,columnNames,sort,order:asc,title,downloadAll:false
//     let param = JSON.stringify(getBaicPostData({
//         ...form,
//         columnNames:"编号,名称,所属处置单位,监测类型,开始运行日期,排序号",
//         columnFields:"code,name,factoryName,deviceTypeName,validStartTime,orderIndex",
//         downloadAll: !ids,
//         downloadIds: ids
//     }))
//     downLoadFile(disposalFacilityDownloadURL,'parameters',param);
// }

export default VtxExport;