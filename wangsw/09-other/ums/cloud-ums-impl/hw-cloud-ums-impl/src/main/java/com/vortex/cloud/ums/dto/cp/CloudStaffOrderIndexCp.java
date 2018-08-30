package com.vortex.cloud.ums.dto.cp;

import java.util.Comparator;

import com.vortex.cloud.ums.model.CloudStaff;

public class CloudStaffOrderIndexCp implements Comparator {
	public int compare(Object object1, Object object2) {// 实现接口中的方法
		CloudStaff p1 = (CloudStaff) object1; // 强制转换
		CloudStaff p2 = (CloudStaff) object2;
		// 为空直接返回
		if (p1 == null && p2 == null) {
			return 0;
		}
		if (p1 == null || p1.getOrderIndex() == null) {
			return 1;
		}
		if (p2 == null || p2.getOrderIndex() == null) {
			return -1;
		}
		if (p1.getOrderIndex() < p2.getOrderIndex()) {
			return -1;
		}
		if (p1.getOrderIndex() > p2.getOrderIndex()) {
			return 1;
		}
		return 0;
	}
}