package com.vortex.cloud.ums.util.model;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

/**
 * 平台节点持久类
 * 
 * @author dejunx
 * 
 */
@MappedSuperclass
@SuppressWarnings("all")
public class PersistentNode extends Persistent implements SerializableObject {
	private String code;
	private String name;

	public PersistentNode() {
	}

	@Column(name = "code")
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	@Column(name = "name")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
