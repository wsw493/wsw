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
public class BakDeleteModel extends BaseModel {
	/**
	 * 已删除
	 */
	public final static Integer DELETED = 1;
	/**
	 * 未删除
	 */
	public final static Integer NO_DELETED = 0;

	/** 备份删除  0:未删除，1：已删除**/
	private Integer beenDeleted = NO_DELETED;
	/** 删除时间 **/
	private Date deletedTime;

	public BakDeleteModel() {
	}

	@Column(name = "beenDeleted")
	public Integer getBeenDeleted() {
		return beenDeleted;
	}

	public void setBeenDeleted(Integer beenDeleted) {
		this.beenDeleted = beenDeleted;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "deletedTime")
	public Date getDeletedTime() {
		return deletedTime;
	}

	public void setDeletedTime(Date deletedTime) {
		this.deletedTime = deletedTime;
	}
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
