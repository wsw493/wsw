package com.vortex.cloud.ums.dataaccess.service.impl;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;
import com.vortex.cloud.ums.dataaccess.service.IFileService;
import com.vortex.cloud.ums.dto.FileInfo;
import com.vortex.cloud.ums.dto.VtxCloudResult;
import com.vortex.cloud.ums.util.PropertyUtils;
import com.vortex.cloud.ums.util.utils.ConnectHttpService;
import com.vortex.cloud.vfs.common.exception.VortexException;
import com.vortex.cloud.vfs.common.mapper.JsonMapper;

/**
 * 提供文件服务，创建文件目录
 * 
 * @author lusm
 *
 */
@SuppressWarnings("all")
@Transactional
@Service("fileService")
public class FileServiceImpl implements IFileService {
	private Logger logger = LoggerFactory.getLogger(FileServiceImpl.class);
	

	private final String fileServer = PropertyUtils.getPropertyValue("file.server");

	private JsonMapper jsonMapper = new JsonMapper();

	/**
	 * 
	 * @return
	 * @throws Exception
	 */
	@Override
	public FileInfo upload(String imgStr, String fileName) throws Exception {

		Map<String, Object> file = Maps.newHashMap();

		FileInfo fileInfo = new FileInfo();

		file.put("base64Str", imgStr);
		file.put("fileName", fileName);

		String result = ConnectHttpService.callHttpByParameters(fileServer + "/vortex/rest/cloud/np/file/uploadFileWithBase64", ConnectHttpService.METHOD_POST, file);

		VtxCloudResult vtxCloudResult = jsonMapper.fromJson(result, VtxCloudResult.class);
		if (vtxCloudResult.getResult().equals(VtxCloudResult.REST_RESULT_FAIL)) {
			logger.error("调用服务上传文件失败");
			throw new VortexException("调用服务上传文件失败");
		}
		fileInfo.setFileName(fileName);
		fileInfo.setFileUrl(((Map<String, String>) vtxCloudResult.getData()).get("fileUrl"));
		fileInfo.setFullPath(((Map<String, String>) vtxCloudResult.getData()).get("downloadPath"));
		fileInfo.setId(((Map<String, String>) vtxCloudResult.getData()).get("id"));
		return fileInfo;

	}
}
