package com.vortex.cloud.ums.util.model;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

import com.vortex.cloud.vfs.data.model.BaseModel;

/**
 * 平台持久类
 * 
 * @author dejunx
 * 
 */
@MappedSuperclass
@SuppressWarnings("all")
public class Persistent extends BaseModel implements SerializableObject {

	private String appId;

	public Persistent() {
	}

	@Column(name = "appId")
	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	@Override
	public int hashCode() {
		if (super.getId() != null) {
			return super.getId().hashCode();
		}

		return 0;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		Persistent per = null;
		try {
			per = (Persistent) obj;
		} catch (ClassCastException cce) {
			return false;
		}

		if ((per.getId() == null) || (getId() == null)) {
			return false;
		}

		return per.getId().equals(getId());
	}
}
