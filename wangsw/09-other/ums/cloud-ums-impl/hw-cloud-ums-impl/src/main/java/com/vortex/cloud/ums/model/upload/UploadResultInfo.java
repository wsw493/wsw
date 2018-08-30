package com.vortex.cloud.ums.model.upload;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.vortex.cloud.vfs.data.model.BakDeleteModel;



/**
 * 上传信息表，文件上传信息，错误信息等
 * 
 * @author SonHo
 *
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "cloud_jcss_upload_result_info")
public class UploadResultInfo extends BakDeleteModel {
	/**
	 * 租户id
	 */
	private String tenantId;

	/**
	 * 系统id
	 */
	private String businessSystemId;

	/**
	 * 是否添加成功 0失败 1成功
	 */
	private boolean successful;

	/**
	 * 出错的message
	 */
	private String message;

	/**
	 * 标志位 一次上传识别位一致 用于查找报告
	 * 
	 */
	private String marks;

	/**
	 * 上传的文件名
	 */
	private String fileName;

	/**
	 * 该文件对应的行号
	 */
	private Integer rowNum;

	/**
	 * 序号 有效数据的第0列
	 */
	private Integer serialNum;

	/**
	 * 上传时间
	 */
	private Date uploadTime;

	public boolean isSuccessful() {
		return successful;
	}

	public void setSuccessful(boolean successful) {
		this.successful = successful;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getMarks() {
		return marks;
	}

	public void setMarks(String marks) {
		this.marks = marks;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public Integer getRowNum() {
		return rowNum;
	}

	public void setRowNum(Integer rowNum) {
		this.rowNum = rowNum;
	}

	public Integer getSerialNum() {
		return serialNum;
	}

	public void setSerialNum(Integer serialNum) {
		this.serialNum = serialNum;
	}

	@Column(name = "tenantId", length = 32, nullable = false)
	public String getTenantId() {
		return tenantId;
	}

	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}

	@Column(name = "businessSystemId", length = 32, nullable = true)
	public String getBusinessSystemId() {
		return businessSystemId;
	}

	public void setBusinessSystemId(String businessSystemId) {
		this.businessSystemId = businessSystemId;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getUploadTime() {
		return uploadTime;
	}

	public void setUploadTime(Date uploadTime) {
		this.uploadTime = uploadTime;
	}
}
