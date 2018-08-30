package com.vortex.cloud.ums.model.upload;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.List;

import javax.persistence.Transient;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.util.StringUtils;

import com.vortex.cloud.ums.dto.excelutil.ExcelCellDTO;
import com.vortex.cloud.ums.dto.excelutil.ExcelRowDTO;

/**
 * 智慧厕所 导入model
 * 
 * @author SonHo
 *
 */
@SuppressWarnings("serial")
public class WorkElementTemp extends UploadTempModel {
	/** 编号 **/
	private String code;
	/** 名称 **/
	private String name;
	/** 经纬度序列 **/
	private String params;
	/** 所属机构 **/
	private String departmentName;
	/** 图元类型 ***/
	private String workElementTypeName;
	/** 行政区划名称 */
	private String divisionName;

	@Transient
	@Override
	public UploadTempModel storeCell(ExcelRowDTO rowDTO) throws Exception {
		List<ExcelCellDTO> cellList = rowDTO.getCellList();
		if (CollectionUtils.isNotEmpty(cellList)) {
			// 遍历单元格cell的结果，封装成Temp对象
			for (ExcelCellDTO cellDTO : cellList) {
				// 保存数据到Temp
				this.setByIndex(cellDTO.getCellIndex(), cellDTO.getValue());
			}
		}
		return this;
	}

	@Transient
	@Override
	public void setByIndex(int index, String content) throws Exception {
		// 和Excel中列字段顺序对应
		String[] indexFeildName = new String[] { "serialNum", "code", "name", "workElementTypeName", "params", "departmentName","divisionName" };
		if (index < indexFeildName.length) {
			PropertyDescriptor pd = new PropertyDescriptor(indexFeildName[index], this.getClass());
			// 获得set方法
			Method method = pd.getWriteMethod();
			method.setAccessible(true);
			method.invoke(this, StringUtils.trimWhitespace(content));
		}
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getParams() {
		return params;
	}

	public void setParams(String params) {
		this.params = params;
	}

	public String getDepartmentName() {
		return departmentName;
	}

	public void setDepartmentName(String departmentName) {
		this.departmentName = departmentName;
	}

	public String getWorkElementTypeName() {
		return workElementTypeName;
	}

	public void setWorkElementTypeName(String workElementTypeName) {
		this.workElementTypeName = workElementTypeName;
	}

	public String getDivisionName() {
		return divisionName;
	}

	public void setDivisionName(String divisionName) {
		this.divisionName = divisionName;
	}

}
