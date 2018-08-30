package com.vortex.cloud.ums.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jxl.Workbook;
import jxl.format.Alignment;
import jxl.format.VerticalAlignment;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * 
 * @author flyoung
 * @date 2013-1-15 下午12:05:57
 */
@SuppressWarnings("all")
public class FileOperateUtil {
	private static final String REALNAME = "realName";
	private static final String STORENAME = "storeName";
	private static final String SIZE = "size";
	private static final String SUFFIX = "suffix";
	private static final String CONTENTTYPE = "contentType";
	private static final String CREATETIME = "createTime";
	private static final String UPLOADDIR = "uploadDir/";

	/**
	 * 将上传的文件进行重命名
	 * 
	 * @author flyoung
	 * @date 2012-3-29 下午3:39:53
	 * @param name
	 * @return
	 */
	private static String rename(String name) {

		Long now = Long.parseLong(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
		Long random = (long) (Math.random() * now);
		String fileName = now + "" + random;

		if (name.indexOf(".") != -1) {
			fileName += name.substring(name.lastIndexOf("."));
		}

		return fileName;
	}

	/**
	 * 压缩后的文件名
	 * 
	 * @author flyoung
	 * @date 2012-3-29 下午6:21:32
	 * @param name
	 * @return
	 */
	private static String zipName(String name) {
		String prefix = "";
		if (name.indexOf(".") != -1) {
			prefix = name.substring(0, name.lastIndexOf("."));
		} else {
			prefix = name;
		}
		return prefix + ".zip";
	}

	/**
	 * 上传文件
	 * 
	 * @author flyoung
	 * @date 2013-1-15 下午12:25:47
	 * @param request
	 * @param params
	 * @param values
	 * @return
	 * @throws Exception
	 */
	public static List<Map<String, Object>> upload(HttpServletRequest request, String[] params,
			Map<String, Object[]> values) throws Exception {

		List<Map<String, Object>> result = Lists.newArrayList();

		MultipartHttpServletRequest mRequest = (MultipartHttpServletRequest) request;
		Map<String, MultipartFile> fileMap = mRequest.getFileMap();

		String uploadDir = request.getSession().getServletContext().getRealPath("/")
				+ File.separator + FileOperateUtil.UPLOADDIR;
		File file = new File(uploadDir);

		if (!file.exists()) {
			file.mkdir();
		}

		String fileName = null;
		int i = 0;
		for (Iterator<Map.Entry<String, MultipartFile>> it = fileMap.entrySet().iterator(); it
				.hasNext(); i++) {

			Map.Entry<String, MultipartFile> entry = it.next();
			MultipartFile mFile = entry.getValue();

			fileName = mFile.getOriginalFilename();
			String storeName = rename(fileName);
			FileUtils.copyInputStreamToFile(mFile.getInputStream(), new File(uploadDir, storeName));
			// 返回文件名 存放目录
			Map<String, Object> map = Maps.newHashMap();
			map.put(storeName, uploadDir);

			result.add(map);
		}
		return result;
	}

	/**
	 * 下载
	 * 
	 * @author flyoung
	 * @date 2013-1-15 下午12:25:39
	 * @param request
	 * @param response
	 * @param storeName
	 * @param contentType
	 * @param realName
	 * @throws Exception
	 */
	public static void download(HttpServletRequest request, HttpServletResponse response,
			String storeName, String contentType, String realName) throws Exception {
		response.setContentType("text/html;charset=UTF-8");
		request.setCharacterEncoding("UTF-8");
		BufferedInputStream bis = null;
		BufferedOutputStream bos = null;

		String ctxPath = request.getSession().getServletContext().getRealPath("/") + File.separator
				+ FileOperateUtil.UPLOADDIR;
		String downLoadPath = ctxPath + storeName;

		long fileLength = new File(downLoadPath).length();

		String fileName = "";
		if (request.getHeader("User-Agent").toLowerCase().indexOf("firefox") > 0) {
			fileName = new String(realName.getBytes("utf-8"), "ISO8859-1");// firefox浏览器
		} else {
			fileName = URLEncoder.encode(realName, "UTF-8");// 其他浏览器包括IE浏览器和google浏览器
		}

		// response.setContentType(contentType);
		response.setHeader("Content-disposition", "attachment; filename=" + fileName);
		response.setHeader("Content-Length", String.valueOf(fileLength));

		bis = new BufferedInputStream(new FileInputStream(downLoadPath));
		bos = new BufferedOutputStream(response.getOutputStream());
		byte[] buff = new byte[2048];
		int bytesRead;
		while (-1 != (bytesRead = bis.read(buff, 0, buff.length))) {
			bos.write(buff, 0, bytesRead);
		}
		bis.close();
		bos.close();
	}

	public static void download(HttpServletRequest request, HttpServletResponse response,
			String storeName, String contentType) throws Exception {
		download(request, response, storeName, contentType, storeName);
	}

	public static <T> void exportExcel(HttpServletRequest request, HttpServletResponse response,
			String title, String columnFields, String columnNames, List<T> children)
			throws Exception {
		if (StringUtils.isNotEmpty(columnFields)) {
			if (children != null && children.size() > 0) {
				String[] columnField = columnFields.split(",");
				String[] columnName = columnNames.split(",");
				String fileName = title + ".xls";
				String uploadDir = request.getSession().getServletContext().getRealPath("/")
						+ File.separator + "uploadDir/";
				File file = new File(uploadDir);
				if (!file.exists()) {
					file.mkdir();
				}
				WritableWorkbook book = Workbook.createWorkbook(new File(uploadDir, fileName));// 创建可写工作簿
				WritableSheet sheet = book.createSheet(title, 0);// 创建可写工作表
				sheet.addCell(new Label(0, 0, "序号"));
				for (int i = 1; i <= columnField.length; i++) {
					sheet.addCell(new Label(i, 0, columnName[i - 1]));
				}

				int i = 1;
				for (T child : children) {
					sheet.addCell(new Label(0, i, Integer.toString(i)));
					for (int j = 1; j <= columnField.length; j++) {
						Object obj = ObjectUtil.getFieldValueByName(columnField[j - 1], child);
						if (obj instanceof Float){
							jxl.write.NumberFormat nf = new jxl.write.NumberFormat("0.00");
					        WritableCellFormat wcfF = new WritableCellFormat(nf);
					        sheet.addCell(new jxl.write.Number(j, i, obj == null ? 0 : Double.valueOf(obj.toString()),wcfF));
						} else {
							sheet.addCell(new Label(j, i, obj == null ? "" : obj.toString()));
						}
						
					}
					i++;
				}
				book.write();
				book.close();
				// String contentType = "application/octet-stream";
				String contentType = "application/vnd.ms-excel";
				FileOperateUtil.download(request, response, fileName, contentType);
			}
		}
	}

	public static void exportExcelFromMap(HttpServletRequest request, HttpServletResponse response,
			String title, String columnFields, String columnNames, List<Map<String,String>> children)
			throws Exception {
		if (StringUtils.isNotEmpty(columnFields)) {
			if (children != null && children.size() > 0) {
				String[] columnField = columnFields.split(",");
				String[] columnName = columnNames.split(",");
				String fileName = title + ".xls";
				String uploadDir = request.getSession().getServletContext().getRealPath("/")
						+ File.separator + "uploadDir/";
				File file = new File(uploadDir);
				if (!file.exists()) {
					file.mkdir();
				}
				WritableWorkbook book = Workbook.createWorkbook(new File(uploadDir, fileName));// 创建可写工作簿
				WritableSheet sheet = book.createSheet(title, 0);// 创建可写工作表
				sheet.addCell(new Label(0, 0, "序号"));
				for (int i = 1; i <= columnField.length; i++) {
					sheet.addCell(new Label(i, 0, columnName[i - 1]));
				}

				int i = 1;
				for (Map<String,String> child : children) {
					sheet.addCell(new Label(0, i, Integer.toString(i)));
					for (int j = 1; j <= columnField.length; j++) {
//						Object obj = ObjectUtil.getFieldValueByName(columnField[j - 1], child);
						String obj = child.get(columnField[j - 1]);
//						if (obj instanceof Float){
//							jxl.write.NumberFormat nf = new jxl.write.NumberFormat("0.00");
//					        WritableCellFormat wcfF = new WritableCellFormat(nf);
//					        sheet.addCell(new jxl.write.Number(j, i, obj == null ? 0 : Double.valueOf(obj.toString()),wcfF));
//						} else {
							sheet.addCell(new Label(j, i, obj == null ? "" : obj.toString()));
//						}
						
					}
					i++;
				}
				book.write();
				book.close();
				// String contentType = "application/octet-stream";
				String contentType = "application/vnd.ms-excel";
				FileOperateUtil.download(request, response, fileName, contentType);
			}
		}
	}

	/**
	 * 加样式的导出
	 * 
	 * @author hb
	 * @version 2014-8-23 下午14:05:57
	 * 
	 * @param request
	 * @param response
	 * @param title sheet标题
	 * @param colFieldsArr 列对应的字段
	 * @param colNamesArr 列对应的中文名称(即显示在excel中的)
	 * @param dataList 填充数据
	 * @throws Exception
	 */
	public static <T> void exportExcel(HttpServletRequest request, HttpServletResponse response,
			String title, String[] colFieldsArr, String[] colNamesArr, int[] colWidthArr,
			List<T> dataList) throws Exception {
		if (ArrayUtils.isEmpty(colFieldsArr) || ArrayUtils.isEmpty(colNamesArr)) {
			return;
		}
		if (CollectionUtils.isNotEmpty(dataList)) {
			String fileName = title + ".xls";
			String uploadDir = request.getSession().getServletContext().getRealPath("/")
					+ File.separator + "uploadDir/";
			File file = new File(uploadDir);
			if (!file.exists()) {
				file.mkdir();
			}
			int proportionBase = 3;// 比例基数
			if (ArrayUtils.isEmpty(colWidthArr)) {
				colWidthArr = new int[dataList.size()];
				for (int i = 0; i < dataList.size(); i++) {
					colWidthArr[i] = 3;
				}
			}
			// 创建可写工作簿
			WritableWorkbook workBook = Workbook.createWorkbook(new File(uploadDir, fileName));
			// 设置标题的格式
			WritableFont font = new WritableFont(WritableFont.TIMES, 18, WritableFont.BOLD);
			WritableCellFormat titleFormat = new WritableCellFormat(font);
			titleFormat.setAlignment(Alignment.CENTRE);
			titleFormat.setVerticalAlignment(VerticalAlignment.CENTRE);
			// 创建可写工作表
			WritableSheet sheet = workBook.createSheet(title, 0);
			sheet.setName(title);
			sheet.mergeCells(0, 0, colNamesArr.length - 1, 0);
			sheet.setRowView(0, 600);
			sheet.addCell(new Label(0, 0, title, titleFormat));
			// 设置列名的格式
			WritableFont colFont = new WritableFont(WritableFont.TIMES, 10, WritableFont.BOLD);
			WritableCellFormat colFormat = new WritableCellFormat(colFont);
			colFormat.setAlignment(Alignment.CENTRE);
			colFormat.setVerticalAlignment(VerticalAlignment.CENTRE);
			// 设置第二行的行高
			sheet.setRowView(1, 400);
			// 添加列名
			for (int i = 0; i < colNamesArr.length; i++) {
				sheet.addCell(new Label(i, 1, colNamesArr[i], colFormat));
				sheet.setColumnView(i, colWidthArr[i] * proportionBase);
			}
			String labelValue = null;// 每个单元格对应的值
			Object obj = null;
			int serialNumber = 1;// 序号
			// 给每行赋值
			for (int r = 2; r <= dataList.size() + 1; r++) {// 从第三行开始输出真实数据
				for (int c = 0; c < colNamesArr.length; c++) {
					if (c == 0) {
						labelValue = String.valueOf(serialNumber);
					} else {
						obj = ObjectUtil.getFieldValueByName(colFieldsArr[c], dataList.get(r - 2));
						labelValue = obj == null ? "" : obj.toString();
					}
					sheet.addCell(new Label(c, r, labelValue));
				}
				serialNumber++;
			}
			workBook.write();
			workBook.close();
			// String contentType = "application/octet-stream";
			String contentType = "application/vnd.ms-excel";
			download(request, response, fileName, contentType);
		}
	}
}