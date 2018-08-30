package com.vortex.cloud.ums.util.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.annotations.GenericGenerator;

/**
 * 基类
 * 
 * @author dejunx
 * 
 */
@MappedSuperclass
@SuppressWarnings("serial")
public abstract class BaseModel implements SerializableObject {

	/** 主键 uuid **/
	private String id;
	/** 状态 **/
	private Integer status = 0;
	/** 记录创建时间 **/
	private Date createTime;
	/** 记录最后修改时间 **/
	private Date lastChangeTime;

	public BaseModel() {
	}

	@Id
	@GeneratedValue(generator = "idGenerator")
	@GenericGenerator(name = "idGenerator", strategy = "assigned")
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Column(name = "status")
	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "createTime")
	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "lastChangeTime")
	public Date getLastChangeTime() {
		return lastChangeTime;
	}

	public void setLastChangeTime(Date lastChangeTime) {
		this.lastChangeTime = lastChangeTime;
	}
	
	@Override
	public int hashCode(){
		if (this.getId() == null){
			return super.hashCode();
		}
		return 31 + this.getId().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getId() == null) {
			return false;
		}
		if (obj.getClass().equals(this.getClass())) {
			BaseModel another = (BaseModel) obj;
			return getId().equals(another.getId());
		}
		return false;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
