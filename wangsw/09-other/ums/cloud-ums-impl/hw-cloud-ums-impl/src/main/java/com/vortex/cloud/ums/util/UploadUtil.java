package com.vortex.cloud.ums.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;
import com.vortex.cloud.ums.config.SpringContextHolder;
import com.vortex.cloud.ums.dataaccess.service.ICentralCacheRedisService;
import com.vortex.cloud.ums.dataaccess.service.IUploadService;
import com.vortex.cloud.ums.dataaccess.service.impl.CentralCacheRedisServiceImpl;
import com.vortex.cloud.ums.dto.excelutil.ExcelRowDTO;
import com.vortex.cloud.ums.dto.excelutil.ExcelSheetDTO;
import com.vortex.cloud.ums.model.upload.UploadTempModel;
import com.vortex.cloud.ums.support.ManagementConstant;
import com.vortex.cloud.ums.util.utils.FileUtil;
import com.vortex.cloud.ums.util.utils.zip.ZipCompress;
import com.vortex.cloud.vfs.common.exception.VortexException;
import com.vortex.cloud.vfs.common.lang.StringUtil;
import com.vortex.cloud.vfs.common.mapper.JsonMapper;
import com.vortex.cloud.vfs.common.web.Servlets;
import com.vortex.cloud.vfs.data.dto.RestResultDto;

/**
 * 导入帮助类
 * 
 * @author SonHo
 *
 */
public class UploadUtil {

	private static final Logger logger = LoggerFactory.getLogger(UploadUtil.class);
	public static final Integer EXCEL_DATA_START_ROW_INDEX = 1;
	private static ICentralCacheRedisService centralCacheRedisService = SpringContextHolder.getBean(CentralCacheRedisServiceImpl.CLASSNAME);;
	/**
	 * 上传文件(压缩包)，导入数据
	 * 
	 * @param request
	 * @param response
	 * @param uploadModelClass
	 * @param uploadService
	 * @throws Exception
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void uploadImportData(HttpServletRequest request, HttpServletResponse response, Class uploadModelClass, IUploadService uploadService) throws Exception {
		RestResultDto<String> oi = new RestResultDto();
		oi.setResult(RestResultDto.RESULT_SUCC);
		response.setContentType("text/html;charset=UTF-8");
		String[] params = new String[] { "alais" };
		Map<String, Object[]> values = Maps.newHashMap();
		String marks = new Date().getTime() + StringUtil.EMPTY;
		List<Map<String, Object>> result = FileOperateUtil.upload(request, params, values);
		Map<String, Object> mapReturn = Maps.newHashMap();

		try {
			int succCount = 0;
			int failCount = 0;
			// 将excel文件读入数据库临时表中
			for (Map<String, Object> fileMap : result) {
				for (Iterator<Map.Entry<String, Object>> it = fileMap.entrySet().iterator(); it.hasNext();) {
					Map.Entry<String, Object> entry = it.next();
					String path = (String) entry.getValue();
					String fileNameT = path + entry.getKey();
					if (fileNameT.toUpperCase().indexOf(".ZIP") != -1) {
						// 解压文件
						String zipPath = ZipCompress.readByApacheZipFile(fileNameT, null);
						// 查找xls文件
						List<String> xlsList = FileUtil.findFileBySuffix(zipPath, ".XLS");

						// 遍历xls文件
						for (String xlsFileName : xlsList) {
							xlsFileName = xlsFileName.replace('\\', '/');
							String fileName = xlsFileName.substring(xlsFileName.lastIndexOf("/") + 1);
							// 将Excel表中的数据解析存储
							List<ExcelSheetDTO> sheetList = LjflFileUtil.prase(xlsFileName);// 存储每一个excel工作簿集合
							List<ExcelRowDTO> rowList = null;// 存储每一个sheet页的行数集合
							// 遍历解析后的工作页
							for (ExcelSheetDTO sheetDTO : sheetList) {
								// 遍历解析后的行
								rowList = sheetDTO.getRowList();
								int rowIndex = EXCEL_DATA_START_ROW_INDEX;
								for (ExcelRowDTO rowDTO : rowList) {
									// 封装temp对象
									UploadTempModel entityTemp = ((UploadTempModel) uploadModelClass.newInstance()).storeCell(rowDTO);
									entityTemp.setFileName(fileName);
									Map<String, Object> importFlagMap = uploadService.importData(entityTemp, marks, rowIndex);
									if (importFlagMap.get("succFlag") != null && (Boolean) importFlagMap.get("succFlag")) {
										succCount++;
									} else {
										failCount++;
									}

									rowIndex++;
								}
							}

						}
					} else {
						oi.setMsg("文件必需是zip格式！");
						oi.setResult(RestResultDto.RESULT_FAIL);
					}
				}
			}
			mapReturn.put("message", "成功" + succCount + "条, " + "失败" + failCount + "条");
			if (succCount > 0 || failCount > 0) {

				oi.setMsg((String) mapReturn.get("message"));
				request.getSession().setAttribute("uploadMarks", marks);

				// 由于分布式，所以存到redis中，然后从redis获取
				String userId = request.getHeader("UserId");
				centralCacheRedisService.putObject(ManagementConstant.MARK_KEY_PREFIX + userId, marks);
			} else {
				// 随意导入一个zip 的时候，也提示信息，不然会出现NULL的提示
				oi.setMsg(StringUtils.isNotBlank(oi.getMsg()) ? oi.getMsg() : (String) mapReturn.get("message"));
			}
		} catch (Exception e) {
			oi.setMsg("Excel文件上传失败，请检查文件是否符合格式！");
			oi.setResult(RestResultDto.RESULT_FAIL);
			e.printStackTrace();
		}

		JsonMapper jsonMapper = new JsonMapper();
		String json = jsonMapper.toJson(oi);
		Servlets.writeDefaultJson(response, json);

	}

	/**
	 * @Description: 下载导入模版 @return String @throws
	 */
	public static void downloadTemplate(HttpServletRequest request, HttpServletResponse response, String fileName) throws Exception {
		response.setContentType("text/html;charset=utf-8");
		request.setCharacterEncoding("UTF-8");
		BufferedInputStream bis = null;
		BufferedOutputStream bos = null;

		String uploadDir = request.getSession().getServletContext().getRealPath("/") + "uploadDir" + File.separator + "management" + File.separator;
		File file = new File(uploadDir);
		if (!file.exists()) {
			file.mkdir();
		}
		if (!StringUtil.isNullOrEmpty(fileName)) {
			String downLoadPath = uploadDir + fileName;
			try {
				long fileLength = new File(downLoadPath).length();
				response.setContentType("application/x-msdownload;");
				response.setHeader("Content-disposition", "attachment; filename=" + new String(fileName.getBytes("gbk"), "ISO8859-1"));
				response.setHeader("Content-Length", String.valueOf(fileLength));
				response.setHeader("Content-Transfer-Encoding", "binary");
				response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");

				response.setHeader("Pragma", "public");
				bis = new BufferedInputStream(new FileInputStream(downLoadPath));
				bos = new BufferedOutputStream(response.getOutputStream());
				byte[] buff = new byte[2048];
				int bytesRead;
				while (-1 != (bytesRead = bis.read(buff, 0, buff.length))) {
					bos.write(buff, 0, bytesRead);
				}
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				throw new VortexException(e.getMessage());
			} finally {
				if (bis != null)
					bis.close();
				if (bos != null)
					bos.close();
			}
		}
	}
}
