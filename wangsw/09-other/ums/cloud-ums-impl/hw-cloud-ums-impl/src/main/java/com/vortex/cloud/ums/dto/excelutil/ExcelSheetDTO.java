package com.vortex.cloud.ums.dto.excelutil;

import java.util.List;

/**
* @ClassName: ExcelSheetDTO
* @Description: (封装excel的sheet对象)
* @author njj
* @date 2016年5月17日 下午3:11:40
* @see [相关类/方法]（可选）
* @since [产品/模块版本] （可选）
*/
public class ExcelSheetDTO {
	
	private int sheetIndex;//sheet页下标，第几页
	private List<ExcelRowDTO> rowList;//当前页下含有的行数
	
	public int getSheetIndex() {
		return sheetIndex;
	}
	public void setSheetIndex(int sheetIndex) {
		this.sheetIndex = sheetIndex;
	}
	public List<ExcelRowDTO> getRowList() {
		return rowList;
	}
	public void setRowList(List<ExcelRowDTO> rowList) {
		this.rowList = rowList;
	}
}
