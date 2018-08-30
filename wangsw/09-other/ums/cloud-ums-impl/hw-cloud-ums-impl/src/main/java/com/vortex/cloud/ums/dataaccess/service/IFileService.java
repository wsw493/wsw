package com.vortex.cloud.ums.dataaccess.service;

import com.vortex.cloud.ums.dto.FileInfo;

/**
 * 提供文件服务，创建文件目录,上传文件
 * 
 * @author lusm
 *
 */
public interface IFileService {
	/**
	 * 上传文件，返回图片信息
	 * 
	 * @param 文件string
	 * @return
	 * @throws Exception
	 */
	FileInfo upload(String imgStr, String fileName) throws Exception;

}
