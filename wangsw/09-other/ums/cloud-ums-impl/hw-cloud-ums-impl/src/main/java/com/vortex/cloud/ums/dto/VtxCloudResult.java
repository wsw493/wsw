package com.vortex.cloud.ums.dto;

public class VtxCloudResult {
	public static final String REST_RESULT = "result"; // 调用rest服务后返回的map中的访问结果
	public static final String REST_ERR_MSG = "errMsg"; // 调用rest服务后返回的map中的访问结果
	public static final String REST_DATA = "data"; // 调用rest服务后返回的map中的访问结果
	public static final Integer REST_RESULT_SUCC = 0; // 调用rest后返回结果为成功
	public static final Integer REST_RESULT_FAIL = 1; // 调用rest后返回结果为失败
	private Integer result; // 返回调用结果，0-成功，1-失败
	private String errMsg; // 错误信息
	private Object data; // 返回的结果数据

	public Integer getResult() {
		return result;
	}

	public void setResult(Integer result) {
		this.result = result;
	}

	public String getErrMsg() {
		return errMsg;
	}

	public void setErrMsg(String errMsg) {
		this.errMsg = errMsg;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

}
