package com.vortex.cloud.ums.dataaccess.service;

import java.util.Map;

/**
 * 导入 service
 * 
 * @author SonHo
 *
 * @param <TempModel>
 */
public interface IUploadService<TempModel> {
	/**
	 * 导入
	 * 
	 * @param uploadTempModel
	 *            需要导入服务的对象的temp临时对象
	 * @param marks
	 *            时间标记
	 * @param row
	 *            导入文件所在行号
	 * @return
	 */
	public Map<String, Object> importData(TempModel tempModel, String marks, int row) throws Exception;
}
