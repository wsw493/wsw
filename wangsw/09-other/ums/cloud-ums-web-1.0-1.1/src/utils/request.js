import fetch from 'dva/fetch';
import download from '../../resources/js/download';
import Promise from 'promise-polyfill';
import {message} from 'antd';
import {sign} from './signWay';
import {getArgs} from './toolFunctions';

function parseJSON(response) {
  return response.json();
}

function checkStatus(response) {
  if (response.status >= 200 && response.status < 300) {
    return response;
  }

  const error = new Error(response.statusText);
  error.response = response;
  throw error;
}

function getAccessToken () {
    var data = localStorage.getItem("ACCESS-TOKEN");
    var access_token = "";
    if(data){
        data = JSON.parse(data);
        access_token = data.access_token;
    }
    return access_token;
}

function getRefreshToken () {
    var data = localStorage.getItem("ACCESS-TOKEN");
    var refresh_token = "";
    if(data){
        data = JSON.parse(data);
        refresh_token = data.refresh_token;
    }
    return refresh_token;
}

function judgeToken (){
    var start = localStorage.getItem("ACCESS-TOKEN-GETTIME");
    var now = new Date().getTime();
    const time = now - start;
    if(time >= 15*60*1000 && time< 30*60*1000){
        refreshToken();
    }
    if(time >= 30*60*1000){
        if(top != window){
            window.top.postMessage('logout','*');
        }
    }
}

//刷新token方法 url:ip + port
export function refreshToken(url, param) {
    url = ManagementConstant.URL_GATEWAY;
    $.ajax({
        type:"POST",
        url:url +"/cas/refreshToken?refresh_token="+getRefreshToken(),
        dataType: 'json',
        async:false,
        headers: {
            'Content-Type': 'application/json;charset=UTF-8',
            'Authorization': 'Bearer ' + getAccessToken()
        },
        complete:function (data){
            if(data.responseJSON && data.responseJSON.rc==0){
                localStorage.setItem("ACCESS-TOKEN-GETTIME",new Date().getTime());
                localStorage.setItem("ACCESS-TOKEN", data.getResponseHeader("ACCESS-TOKEN"));

                window.top.postMessage("ACCESS-TOKEN"+localStorage.getItem("ACCESS-TOKEN")+
                    "ACCESS-TOKEN-GETTIME"+localStorage.getItem("ACCESS-TOKEN-GETTIME"),'*');
            }
        }
    })
}

/**
 * Requests a URL, returning a promise.
 *
 * @param  {string} url       The URL we want to request
 * @param  {object} [options] The options we want to pass to "fetch"
 * @return {object}           An object containing either "data" or "err"
 */
export function request(url, options) {
    let testUrlHead = ManagementConstant.URL_GATEWAY;
    let callUrl = ''
    if(options.body && options.body.tenantId){
        callUrl = (options.defaultUrl ? options.defaultUrl : testUrlHead) + url+'?userId='+getArgs('userId');
    }else if(options.body && options.body.userId){
        callUrl = (options.defaultUrl ? options.defaultUrl : testUrlHead) + url+'?tenantId='+getArgs('tenantId');
    }else {
        callUrl = (options.defaultUrl ? options.defaultUrl : testUrlHead) + url+'?tenantId='+getArgs('tenantId')+'&userId='+getArgs('userId');
    }
    let ajaxPropmise = new Promise((resolve,reject)=>{
        $.ajax({
            type:options.method ? options.method:'POST',
            url: callUrl,
            data:options.body ? options.body : null,
            dataType:options.dataType  ? options.dataType :  'json',
            contentType: options.contentType ? options.contentType : ContentType.String,
            headers: options.headers ? options.headers : {
                Accept: 'application/json,text/plain,*/*'
            },
            success:function (data) {
                resolve(data);
            },
            error:function(XMLHttpRequest, textStatus, errorThrown){
                reject(XMLHttpRequest);
            }
        });
    });
    return ajaxPropmise.then((data) => {
        if(data.result == '10001' || data.result == "10002"){
            message.error(data.msg);
            if(top != window){
                window.top.postMessage('logout','*');
            }
        }
        if(data.result == '10003'){
            message.error(data.msg);
            if(top != window){
                window.top.postMessage('noauthorize','*');
            }
        }
        // if(!(data.result==0 || data.result==1)){
        //     if(typeof data.total != 'undefined'){
        //         data = {data:data,result:0,msg:'',exception:''};
        //     }else {
        //         data = {data:JSON.stringify(data),result:0,msg:'',exception:''};
        //     }
        // }
        if(data.result==0 && typeof(data.data)=='undefined'){
            data.data = {};
        }
        return data;
    }).catch((data)=>{
        if(data.status == '401'){
            message.error("没有权限");
            if(top != window){
                window.top.postMessage('logout','*');
            }
        }
        return null;
    });
}

export function requestFile(url, options) {
    let testUrlHead = ManagementConstant.URL_GATEWAY;
    let callUrl = testUrlHead + url+'?tenantId='+getArgs('tenantId')+'&userId='+getArgs('userId');
    let ajaxPropmise = new Promise((resolve,reject)=>{
        $.ajax({
            type:options.method,
            url: callUrl,
            data:options.body || null,
            dataType:'text',
            processData: false,
            contentType: false,
            headers: options.headers ? options.headers : {
                'Authorization': 'Bearer ' + token
            },
            success:function (data) {
                resolve(data);
            },
            error:function(XMLHttpRequest, textStatus, errorThrown){
                reject(textStatus);
            }
        });
    });
    return ajaxPropmise.then(data => data).catch(err=>{
        return null;
    });
}

export function downloadFile(url, options) {
    $('body').append('<form id="downloadForm" style="display:none" target=""></form>');
    
    var param = options.body;
    param['Authorization'] = 'Bearer ' + token;
    param['access_token'] = token;
    for(var key in param){
        $("#downloadForm").append('<input type="hidden" name="'+key+'" value="'+param[key]+'" />')
    }
    let testUrlHead = ManagementConstant.URL_GATEWAY;
    $("#downloadForm").attr("action",testUrlHead + url);
    $("#downloadForm").submit();
    $("#downloadForm").remove();
}

//登录方法 url:ip + port,param: 用户名，密码
export function toLogin(param1) {
    if (!window.Promise) {  
        window.Promise = Promise;  
    }
    var param = {
        username: param1.userName!=''?param1.userName:"lizicheng",
        password: "e10adc3949ba59abbe56e057f20f883e",
        ip: localIP
    };
    var url ='http://192.168.1.248:18082' || ManagementConstant.URL_GATEWAY;
    fetch(url + "/cas/login",{
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(param)
    }).then((response)=>{
        if(response.headers && response.headers.get('ACCESS-TOKEN')){
            localStorage.setItem("ACCESS-TOKEN-GETTIME",new Date().getTime());
            //将ACCESS-TOKEN存到localStorage中
            localStorage.setItem("ACCESS-TOKEN",response.headers.get('ACCESS-TOKEN'));
        }
        return response.json();
    }).then((json)=>{
        console.log(json);
    }).catch((error)=>{
        console.log(error);
    });
}
