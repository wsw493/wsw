package com.vortex.cloud.ums.model;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.vortex.cloud.vfs.data.model.BakDeleteModel;



/**
 * management系统日志表：用来记录该系统每个人员做了哪些操作，
 * 
 * @author lusm
 *
 */
@Entity
@Table(name = "cloud_manage_log")
public class CloudLog extends BakDeleteModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6224229411318190403L;
	/**
	 * 该操作用户id
	 */
	private String userId;
	/**
	 * 操作开始时间
	 */
	private Date startTime;
	/**
	 * 操作结束时间
	 */
	private Date endTime;

	/**
	 * 别调用的方法
	 */
	private String calledMethod;

	/**
	 * 是否有权限访问 1有权限 0 没有权限
	 */
	private Integer hasPermission;

	public Integer getHasPermission() {
		return hasPermission;
	}

	public void setHasPermission(Integer hasPermission) {
		this.hasPermission = hasPermission;
	}

	public String getUserId() {
		return userId;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public String getCalledMethod() {
		return calledMethod;
	}

	public void setCalledMethod(String calledMethod) {
		this.calledMethod = calledMethod;
	}

}
