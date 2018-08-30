package com.vortex.cloud.ums.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.DecimalFormat;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import com.google.common.collect.Lists;
import com.vortex.cloud.ums.dto.excelutil.ExcelCellDTO;
import com.vortex.cloud.ums.dto.excelutil.ExcelRowDTO;
import com.vortex.cloud.ums.dto.excelutil.ExcelSheetDTO;
import com.vortex.cloud.vfs.common.lang.StringUtil;


public class LjflFileUtil {
	private static Logger log = LoggerFactory.getLogger(LjflFileUtil.class);
	// 导出的excel后缀
	public static final String EXCEL_SUFFIX_2007 = ".xlsx";
	public static final String EXCEL_SUFFIX_2003 = ".xls";
	public static final Integer EXCEL_MAXROWNUM_2007 = 65536;
	public static final Integer EXCEL_MAXROWNUM_2003 = 1048576;
	public static final Integer EXCEL_DATA_START_ROW_INDEX = 1;

	/**
	 * 获取cell的值
	 * 
	 * @param Cell
	 * @return String
	 */
	public static String getCellValue(Cell cell) {
		String returnValue = "";
		if (null != cell) {
			switch (cell.getCellType()) {
			case Cell.CELL_TYPE_NUMERIC:// 数字
				Double doubleValue = cell.getNumericCellValue();
				// 可以将科学计数法和含有“.0”的小数转化为整数
				DecimalFormat df = new DecimalFormat("#.#########");
				returnValue = df.format(doubleValue);
				break;
			case Cell.CELL_TYPE_BOOLEAN: // 布尔型
				Boolean booleanValue = cell.getBooleanCellValue();
				returnValue = booleanValue.toString().trim();
				break;
			case Cell.CELL_TYPE_STRING: // 字符串
				returnValue = cell.getStringCellValue().trim();
				break;
			case Cell.CELL_TYPE_BLANK: // 空值
				returnValue = "";
				break;
			}
		}
		return returnValue;
	}

	/**
	 * @Title: prase
	 * @Description: (将excel中的数据解析储存到List)
	 * @return List<ExcelSheetDTO>
	 * @throws
	 */
	@SuppressWarnings("deprecation")
	public static List<ExcelSheetDTO> prase(String xlsFileName) throws Exception {
		List<ExcelSheetDTO> sheetList = Lists.newArrayList();// 存储每一个excel工作簿集合
		if (StringUtil.isNullOrEmpty(xlsFileName)) {
			return sheetList;
		}
		FileInputStream fIn = null;
		Workbook wb = null;
		Sheet sheet = null;
		Cell cell = null;
		Row row = null;
		ExcelSheetDTO sheetDTO = null;
		ExcelRowDTO rowDTO = null;
		ExcelCellDTO cellDTO = null;
		List<ExcelRowDTO> rowList = null;// 存储每一个sheet页的行数集合
		List<ExcelCellDTO> cellList = null;// 存储每一行的列数集合
		if (!StringUtil.isNullOrEmpty(xlsFileName)) {
			try {
				if (xlsFileName.endsWith(EXCEL_SUFFIX_2003)) {
					// 解析2003版本
					fIn = new FileInputStream(xlsFileName);
					wb = new HSSFWorkbook(fIn);

				} else if (xlsFileName.endsWith(EXCEL_SUFFIX_2007)) {
					// 解析2007版本
					wb = new XSSFWorkbook(xlsFileName);
				}
				if (wb.getNumberOfSheets() > 0) {
					// 遍历工作簿
					for (int numSheet = 0; numSheet < wb.getNumberOfSheets(); numSheet++) {
						sheet = wb.getSheetAt(numSheet);
						if (sheet == null) {
							throw new Exception("Excel文件没有工作簿");
						}
						if (sheet.getLastRowNum() < EXCEL_DATA_START_ROW_INDEX) {// Excel中第一行为字段名称，第二行为数据，index从0开始。getLastRowNum<1表示第二行数据行没有数据
							continue;
						}
						// 遍历行Row
						rowList = Lists.newArrayList();
						for (int rowNum = EXCEL_DATA_START_ROW_INDEX; rowNum <= sheet.getLastRowNum(); rowNum++) {
							// 对遍历的每一行数据
							row = sheet.getRow(rowNum);
							if (row == null) {
								throw new Exception("工作簿中缺少行");
							}
							if (row.getLastCellNum() <= 1) {
								continue;
							}
							// 遍历列Cell
							cellList = Lists.newArrayList();
							for (int cellNum = 1; cellNum < row.getLastCellNum(); cellNum++) {
								cell = row.getCell(cellNum);
								if (cell == null) {
									continue;
								} else {
									// 封装单元格的内容
									cellDTO = new ExcelCellDTO();
									cellDTO.setCellIndex(cellNum);
									cellDTO.setValue(LjflFileUtil.getCellValue(cell));
									cellList.add(cellDTO);
								}
							}

							// 封装行对象
							rowDTO = new ExcelRowDTO();
							rowDTO.setRowIndex(rowNum);
							rowDTO.setCellList(cellList);
							rowList.add(rowDTO);
						}
						// 封装页对象
						sheetDTO = new ExcelSheetDTO();
						sheetDTO.setSheetIndex(numSheet);
						sheetDTO.setRowList(rowList);
						sheetList.add(sheetDTO);
					}
				}
			} catch (Exception e) {
				log.error(null, e);
				e.printStackTrace();
			}
		}
		return sheetList;
	}

	public static <T> void exportExcel(HttpServletRequest request, HttpServletResponse response, String title, String columnFields, String columnNames, List<T> children)
			throws Exception {
		exportExcel(request, response, title, columnFields, columnNames, children, EXCEL_SUFFIX_2007);
	}

	/**
	 * @Title: exportExcel
	 * @Description: (导出excel )
	 * @return void
	 * @throws
	 */
	public static <T> void exportExcel(HttpServletRequest request, HttpServletResponse response, String title, String columnFields, String columnNames, List<T> children,
			String suffix) throws Exception {
		if (StringUtils.isNotEmpty(suffix)) {
			Workbook workbook = null;
			if (StringUtils.isNotEmpty(columnFields)) {
				if (!CollectionUtils.isEmpty(children)) {
					String[] columnField = StringUtil.splitComma(columnFields);
					String[] columnName = StringUtil.splitComma(columnNames);
					String fileName = title + suffix;
					String uploadDir = request.getSession().getServletContext().getRealPath("/") + File.separator + "uploadDir/";
					File file = new File(uploadDir);
					if (!file.exists()) {
						file.mkdir();
					}
					// Excel2003版最大行数是65536行。Excel2007开始的版本最大行数是1048576行。
					// Excel2003的最大列数是256列，2007以上版本是16384列。
					int maxRowNum = 0;// excel每个sheet页所含有的最大行数
					if (EXCEL_SUFFIX_2007.equals(suffix)) {
						workbook = new HSSFWorkbook(); // 产生工作簿对象 (2003版本)
						maxRowNum = EXCEL_MAXROWNUM_2007;
					} else if (EXCEL_SUFFIX_2003.equals(suffix)) {
						workbook = new XSSFWorkbook(); // 产生工作簿对象 (2007版本)
						maxRowNum = EXCEL_MAXROWNUM_2003;
					} else {
						throw new Exception("请导出正确格式的excel文档！");
					}
					Sheet sheet = workbook.createSheet(); // 产生工作表对象
					// 准备sheet页的表头
					sheet = prepareSheet(sheet, columnField, columnName);

					// 当数据量过大，超过excel最大行数，新建sheet页
					Sheet newSheet = null;
					if (maxRowNum < children.size()) {
						newSheet = workbook.createSheet(); // 产生工作表对象
						newSheet = prepareSheet(newSheet, columnField, columnName);
					}
					int i = 1;
					Row row_i = null;
					Object obj = null;
					for (T child : children) {
						if (maxRowNum >= i) {
							row_i = sheet.createRow(i);
							// 写入数据的序号
							row_i.createCell(0).setCellValue(i);
							for (int j = 1; j <= columnField.length; j++) {
								// 根据列名通过反射获取属性值
								obj = ObjectUtil.getFieldValueByName(columnField[j - 1], child);
								row_i.createCell(j).setCellValue(obj.toString());
							}
						} else {
							// 如果数据超过最大行数，将余下的数据存储到新建的sheet页
							row_i = newSheet.createRow(i - maxRowNum);
							// 写入数据的序号
							row_i.createCell(0).setCellValue(i - maxRowNum);
							for (int j = 1; j <= columnField.length; j++) {
								// 根据列名通过反射获取属性值
								obj = ObjectUtil.getFieldValueByName(columnField[j - 1], child);
								row_i.createCell(j).setCellValue(obj.toString());
							}
						}
						i++;
					}
					FileOutputStream stream = new FileOutputStream(uploadDir + fileName);
					workbook.write(stream);
					stream.flush();
					stream.close();
					String contentType = "application/vnd.ms-excel";
					FileOperateUtil.download(request, response, fileName, contentType);
				}
			}
		}
	}

	/**
	 * @Title: prepareSheet
	 * @Description: (准备sheet页的表的第一行)
	 * @return Sheet
	 * @throws
	 */
	private static Sheet prepareSheet(Sheet sheet, String[] columnField, String[] columnName) {
		Row row = sheet.createRow(0);// 产生一行
		Cell cell = row.createCell(0);// 产生第一个单元格
		cell.setCellValue("序号");// 往第一个单元格写入信息
		for (int i = 1; i <= columnField.length; i++) {
			row.createCell(i).setCellValue(columnName[i - 1]);
		}
		return sheet;
	}
}
