import React from 'react';
import {notification} from 'antd';
export const VtxNotification={
    info:function(config){
        config=this.renderConfig(config,'信息通知');
        notification.info(config);
    },
    success: function(config) {
        config=this.renderConfig(config,'成功通知');
        notification.success(config);
    },
    error: function(config) {
        config=this.renderConfig(config,'失败通知');
        notification.error(config);
    },

    // Departed usage, please use warning()
    warn: function(config) {
        config=this.renderConfig(config,'警告通知');
        notification.warn(config);
    },
    warning: function(config) {
        config=this.renderConfig(config,'警告通知');
        notification.warning(config);
    },
    close: function(key) {
        notification.close(key);
    },
    config:function(options){
    	notification.config(options);//全局配置一次即可生效
    },
    open:function(config){
    	notification.open(config);
    },
    destroy: function() {
        notification.destroy();
    },
    renderConfig:function(c,t){
        return{
            message: 'message' in c?c.message : t,
            description:'description' in c?c.description : '通知提醒',
            ...c
        } 
    }
}