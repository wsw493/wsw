import React from 'react';
import {Modal} from 'antd';
import VortexUpload from './VortexUpload';

class VortexUploadModal extends React.Component{
    constructor(props){
        super(props)
        this.state={
            fileList: this.props.upload.fileList || [],
        };
    }

    render(){
        let t = this;
        let ulProps = {
            ...t.props.upload,
            onSuccess(file){
                if(t.props.upload.mode=='single'){
                    t.setState({
                        fileList:[file]
                    });
                }
                else{
                    t.setState({
                        fileList:[...t.state.fileList, file]
                    });
                }
                if(typeof(t.props.upload.onSuccess) =='function'){
                    t.props.upload.onSuccess(file);
                }
            },
            onError(res){
                if(typeof(t.props.upload.onError) =='function'){
                    t.props.upload.onError(res);
                }
            },
            onRemove(file){
                t.setState({
                    fileList: t.state.fileList.filter((item)=>item.id!=file.id)
                });
                if(typeof(t.props.upload.onRemove) =='function'){
                    t.props.upload.onRemove(file);
                }
            }

        }

        let mdProps = {
            title: "上传文件",
            okText: "确定",
            cancelText: "取消",
            ...t.props.modal,
            onOk(){
                if(typeof(t.props.modal.onOk)=='function'){
                    t.props.modal.onOk(t.state.fileList);
                }
            },
        }
        return (
            <Modal {...mdProps}>
                <VortexUpload {...ulProps}/>
                {
                    typeof(t.props.modal.setContent) =='function'? 
                    t.props.modal.setContent(t.state.fileList)
                    : null
                }
            </Modal>
        )
    }
}

export default VortexUploadModal;