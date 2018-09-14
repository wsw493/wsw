/**
 * @function 获取签名参数方法
 * @param: method   请求的方法GET/POST
 * @param: params   请求参数 Object
 * @param: encType  ALL,PART 默认ALL
 * @param: crypto   MD5,SHA 默认MD5
 * @param: token    请求TOKEN-权限控制
 * @return 参数拼接的字符串
 */
export function sign({method, params, encType, crypto, token}){
    let v_timestamp  = new Date().valueOf();
    let v_nonce = nonceDO();
    let v_enc_type = encType?encType:'ALL';
    let v_crypto = crypto?crypto:'MD5';

    let v_sign = signDO(
        v_enc_type=='ALL'?
        {
            ...params,
            v_timestamp,
            v_crypto: v_crypto,
            v_enc_type: v_enc_type
        }:{
            v_timestamp
        },v_nonce,v_crypto
    );
    return 'v_timestamp=' + v_timestamp + '&v_nonce=' + v_nonce + '&v_sign=' + v_sign + '&v_crypto=' + v_crypto + '&v_enc_type=' + v_enc_type
}
/**
 * @function 生成6位的随机数
 */
function nonceDO(){
    let random = '';
    for(let i = 0;i<6;i++){
        random += Math.floor(Math.random()*10);
    }
    return random;
}
/**
 * @function 生成签名算法
 * @return 返回通过MD5或者SHA加密的密钥
 */
function signDO(params,v_nonce,v_crypto){
    let keys = [];
    let obj = {};
    let str = '';
    for(let key in params){
        keys.push(key);
    }
    keys = keys.sort();
    for(let i=0;i<keys.length;i++){
        if(params[keys[i]] || params[keys[i]]===0){
            str+=(keys[i]+params[keys[i]]);
        }
    }
    if(v_crypto=='MD5'){
        return hex_md5(v_nonce + str).toUpperCase();
    }else {
        let shaObj = new jsSHA(v_nonce + str, "ASCII");
        return shaObj.getHash('SHA-1', 'HEX').toUpperCase();
    }
}