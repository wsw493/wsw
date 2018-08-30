package com.vortex.cloud.ums.dto.excelutil;

import java.util.List;

/**
* @ClassName: ExcelRowDTO
* @Description: (封装excel的row对象)
* @author njj
* @date 2016年5月17日 下午3:12:43
* @see [相关类/方法]（可选）
* @since [产品/模块版本] （可选）
*/
public class ExcelRowDTO {
	private int rowIndex;//行下标，第几行
	private List<ExcelCellDTO> cellList;//当前行所含有的列数
	
	public int getRowIndex() {
		return rowIndex;
	}
	public void setRowIndex(int rowIndex) {
		this.rowIndex = rowIndex;
	}
	public List<ExcelCellDTO> getCellList() {
		return cellList;
	}
	public void setCellList(List<ExcelCellDTO> cellList) {
		this.cellList = cellList;
	}
}
