import {message} from 'antd';
export function formValidation(data){
    // {checkType:'array', checkVal: ''}
    // {checkType:'phone', checkVal: ''}
    // {checkType:'number', checkVal: ''}
    if(data instanceof(Array)){
        for(let elem of data){
            if(!formValidation(elem)){
                return false
            }
        }
        return true;
    }
    else{
        // if(data.checkVal===null || data.checkVal===undefined)
        //     return false;
        switch(data.checkType){
            case 'number': return /^\d+$/.test(data.checkVal)  || data.checkVal == null || data.checkVal=='';
            case 'phone': return /^1\d{10}$/.test(data.checkVal) || data.checkVal=='';
            case 'required': return /\S+/.test(data.checkVal);
            case 'empty': return !/\S+/.test(data.checkVal);
            case 'requiredArray': return (data.checkVal instanceof(Array) && data.checkVal.length>0);
            case 'codeValidator': return ( /^[\w\W]{1,255}$/.test(data.checkVal));
            case 'nameValidator': return ( /^[\w\W]{1,255}$/.test(data.checkVal));
            case 'email': return ( /^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\.[a-zA-Z0-9_-]+)+$/.test(data.checkVal) || data.checkVal=='');
            case 'idCard': return (/^[1-9]\d{5}[1-9]\d{3}((0\d)|(1[0-2]))(([0|1|2]\d)|3[0-1])\d{3}(\d|X){1}$/.test(data.checkVal)); /*带X的身份证*/
            case 'officePhone': (/0\d{2}-\d{8}|0\d{2}-\d{7}|0\d{3}-\d{7}|0\d{3}-\d{8}/.test(data.checkVal));/*电话验证*/
            default: return true;
        }
    }
}

export function getBaicPostData(postdata={}){
    return {
        authParam:{
            tenantId:'3a97dba37aae489883dd0ef82f96d373'
        },
        param:{
            ...postdata
        },
        reqMethod:{
            isJsonP:0
        }
    }
}

export function downLoadFile(reqURL,paramName,paramVal){
    var formDom=$("<form>");//定义一个form表单  
    formDom.attr("style","display:none");  
    formDom.attr("target","");  
    formDom.attr("method","post");  
    formDom.attr("action",reqURL);  
    var input1=$("<input>");  
    input1.attr("type","hidden");  
    input1.attr("name",paramName);  
    input1.attr("value",paramVal);  
    $("body").append(formDom);//将表单放置在web中  
    formDom.append(input1);  
    formDom.submit();//表单提交 
    formDom.remove();
}
export function getMyDate(str){  
    var oDate = new Date(str),  
    oYear = oDate.getFullYear(),  
    oMonth = oDate.getMonth()+1,  
    oDay = oDate.getDate(),  
    oHour = oDate.getHours(),  
    oMin = oDate.getMinutes(),  
    oSen = oDate.getSeconds(),  
    oTime = oYear +'-'+ getzf(oMonth) +'-'+ getzf(oDay) +' '+ getzf(oHour) +':'+ getzf(oMin) +':'+getzf(oSen);//最后拼接时间  
    return oTime;  
};  
//补0操作  
function getzf(num){  
    if(parseInt(num) < 10){  
        num = '0'+num;  
    }  
    return num;  
}

//处理table的数据格式的方法
export function handleTableData(data){
    if(typeof(data) == 'undefined'){
        data = [];
    }
    return data.map((item)=>{
        return {
            ...item,
            key: item.id
        }
    });
}
//table侧边树，数据处理
export function handleTreeData(data){
    if(typeof(data) == 'undefined'){
        data = [];
    }
    return data.map((item) => {
        if (item.length == 0) {
            return {
                key: item.id,
                name: item.name,
                attr: item,
                isLeaf: item.leaf,
                nodeType: item.nodeType
            }
        }else {
            return {
                key: item.id,
                name: item.name,
                attr: item,
                nodeType: item.nodeType,
                isLeaf: item.leaf,
                children: handleTreeData(item.children)
            }
        }
        
    });
}

//处理返回数据
export function handleRestResultData(data, callback){
    if(data && data.result == 0){
        if(typeof callback == 'function'){
            callback();
        }
    }else {
        if(data && data.exception){
            message.error(data.exception);
        }
    }
}

//筛选需要处理的数据更新form的数据
/*
    columns: 需要传到后端的字段
    editItem: 对象所有数据
*/
export function handleEditItem(columns, editItem){
    var newItem = {};
    columns.map((item)=>{
        newItem[item] = editItem[item];
    });
    return newItem;
}

//比较两数据值
export function compareValue(val1, val2){
    return val1 == val2;
}

export const splitOverride = (data)=>{
    var arrObj = [];
    if (data != "") {
      var args1 = data.split(";");
      for(var i=0;i<args1.length;i++){
        var args2 = args1[i].split(",");
        arrObj.push([args2[0],args2[1]]);
      } 
    }
    return arrObj;
}
export function getArgs(key) {
    var t = ''
    var args = window.location.hash.split('?');
    args = args[1].split('&');
    args.map((item)=>{
        if(item.indexOf(key)>-1){
            t = item.split('=')[1];
        }
    });
    return t;
}