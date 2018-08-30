package com.vortex.cloud.ums.dto.excelutil;

/**
* @ClassName: ExcelCellDTO
* @Description: (封装excel的cell对象)
* @author njj
* @date 2016年5月17日 下午3:13:57
* @see [相关类/方法]（可选）
* @since [产品/模块版本] （可选）
*/
public class ExcelCellDTO {
	private int cellIndex; //单元格列下标
	private String value; //单元格的值
	private String type;// 数据类型
	public int getCellIndex() {
		return cellIndex;
	}
	public void setCellIndex(int cellIndex) {
		this.cellIndex = cellIndex;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
}
