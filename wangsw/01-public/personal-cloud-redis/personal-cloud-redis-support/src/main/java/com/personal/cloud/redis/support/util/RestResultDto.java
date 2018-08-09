package com.personal.cloud.redis.support.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

/**
 * @date 2018-08-08
 * @author wsw
 * Created by wsw on 2018/8/8.
 */
@SuppressWarnings("serial")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RestResultDto<T> implements Serializable {
    /**
     * 调用rest后返回结果为成功
     */
    public static final Integer RESULT_SUCC = 0;
    /**
     * 调用rest后返回结果为失败
     */
    public static final Integer RESULT_FAIL = 1;

    /**
     * 返回调用结果，0-成功，1-失败
     */
    private Integer result = RESULT_SUCC;
    /**
     * 返回前台的信息
     */
    private String msg = StringUtils.EMPTY;

    /**
     * 异常信息， 放置e.getMessage()
     */
    private String exception = StringUtils.EMPTY;

    /**
     * 返回的结果数据
     */
    private T data;

    public static <T> RestResultDto<T> newFalid(String exception) {
        return newResult(RESULT_FAIL, StringUtils.EMPTY, null, exception);
    }

    public static <T> RestResultDto<T> newFalid(String msg,String exception) {
        return newResult(RESULT_FAIL, msg, null, exception);
    }

    public static <T> RestResultDto<T> newSuccess() {
        return newResult(RESULT_SUCC, StringUtils.EMPTY, null, StringUtils.EMPTY);
    }

    public static <T> RestResultDto<T> newSuccess(T data) {
        return newResult(RESULT_SUCC, StringUtils.EMPTY, data, StringUtils.EMPTY);
    }

    public static <T> RestResultDto<T> newSuccess(T data, String msg) {
        return newResult(RESULT_SUCC, msg, data, StringUtils.EMPTY);
    }

    private static <T> RestResultDto<T>  newResult(Integer result, String msg, T data, String exception) {
        return new RestResultDto<T>(result, msg, data, exception);
    }

    public RestResultDto() {
    }

    public RestResultDto(Integer result, String msg, T data, String exception) {
        this.result = result;
        this.msg = msg;
        this.data = data;
        this.exception = exception;
    }

    public RestResultDto(Integer result, String msg, T data) {
        this.result = result;
        this.msg = msg;
        this.data = data;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getException() {
        return exception;
    }

    public void setException(String exception) {
        this.exception = exception;
    }

    public Integer getResult() {
        return result;
    }

    public void setResult(Integer result) {
        this.result = result;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

}
