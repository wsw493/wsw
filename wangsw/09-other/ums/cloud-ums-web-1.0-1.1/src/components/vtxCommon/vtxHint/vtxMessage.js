import React from 'react';
import {message} from 'antd';
export const VtxMessage={
    funcList:'info,success,error,warning,warn,loading,config,destroy',
    info:function(content, duration, onClose){
        message.info(content||'温馨提示', duration, onClose);
    },
    success: function(content, duration, onClose) {
        message.success(content||'保存成功', duration, 'success',onClose);
    },
    error: function(content, duration, onClose) {
        message.error(content||'保存失败', duration, 'error', onClose);
    },

    // Departed usage, please use warning()
    warn: function(content, duration, onClose) {
        message.warn(content||'警告', duration, 'warning', onClose);
    },
    warning: function(content, duration, onClose) {
        message.warning(content||'警告', duration, 'warning', onClose);
    },
    loading: function(content, duration, onClose) {
        message.loading(content||'保存中...', duration, 'loading', onClose);
    },
    config: function(options) {
        message.config(options);
    },
    destroy: function() {
        message.destroy();
    },
    show:function(type,content, duration, onClose){
        if(this.funcList.indexOf(type)==-1){
            content=type;
            type='success';
        }
        VtxMessage[type](content, duration, onClose);
    },
}