package com.vortex.cloud.ums.dto;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;

public class WorkElementUtil {
	/**
	 * 返回名称串 格式xx,xx
	 * 
	 * @return
	 */
	public static String getWorkElementNames(List<WorkElementDto> primitives) {
		StringBuffer sb = new StringBuffer();
		String names = null;
		if (CollectionUtils.isNotEmpty(primitives)) {
			for (WorkElementDto workElementDto : primitives) {
				sb.append(workElementDto.getName() + ",");
			}
			names = sb.toString();
			int index = names.lastIndexOf(",");
			if (index != -1) {
				names = names.substring(0, index);
			}
		}
		return names;

	}
}
