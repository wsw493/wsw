package com.vortex.cloud.ums.util.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * 备份删除类
 * 
 * @author dejunx
 * 
 */
@MappedSuperclass
@SuppressWarnings("serial")
public class BakOperateModel extends BakDeleteModel {


	/** 修改人 **/
	private String updateManId;
	private String updateManName;
	/** 修改日期 **/
	private Date updateTime;
	/** 保存人 **/
	private String saveManId;
	private String saveManName;
	/** 保存日期 **/
	private Date saveTime;
	/** 删除人 **/
	private String deleteManId;
	private String deleteManName;
	
	
	public BakOperateModel() {
	}

	@Column(name = "updateManId")
	public String getUpdateManId() {
		return updateManId;
	}

	public void setUpdateManId(String updateManId) {
		this.updateManId = updateManId;
	}

	@Column(name = "updateManName")
	public String getUpdateManName() {
		return updateManName;
	}

	public void setUpdateManName(String updateManName) {
		this.updateManName = updateManName;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "updateTime")
	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	@Column(name = "saveManId")
	public String getSaveManId() {
		return saveManId;
	}

	public void setSaveManId(String saveManId) {
		this.saveManId = saveManId;
	}

	@Column(name = "saveManName")
	public String getSaveManName() {
		return saveManName;
	}

	public void setSaveManName(String saveManName) {
		this.saveManName = saveManName;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "saveTime")
	public Date getSaveTime() {
		return saveTime;
	}

	public void setSaveTime(Date saveTime) {
		this.saveTime = saveTime;
	}

	@Column(name = "deleteManId")
	public String getDeleteManId() {
		return deleteManId;
	}

	public void setDeleteManId(String deleteManId) {
		this.deleteManId = deleteManId;
	}

	@Column(name = "deleteManName")
	public String getDeleteManName() {
		return deleteManName;
	}

	public void setDeleteManName(String deleteManName) {
		this.deleteManName = deleteManName;
	}


	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
