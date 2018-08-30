package com.vortex.cloud.ums.dataaccess.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.converters.DateConverter;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.vortex.cloud.ums.dataaccess.service.ICloudOrganizationService;
import com.vortex.cloud.ums.dataaccess.service.IDeflectService;
import com.vortex.cloud.ums.dataaccess.service.ITenantDivisionService;
import com.vortex.cloud.ums.dataaccess.service.IUploadResultInfoService;
import com.vortex.cloud.ums.dataaccess.service.IWorkElementService;
import com.vortex.cloud.ums.dataaccess.service.IWorkElementTempService;
import com.vortex.cloud.ums.dataaccess.service.IWorkElementTypeService;
import com.vortex.cloud.ums.dto.WorkElementDto;
import com.vortex.cloud.ums.enums.SharpTypeEnum;
import com.vortex.cloud.ums.model.WorkElement;
import com.vortex.cloud.ums.model.WorkElementType;
import com.vortex.cloud.ums.model.upload.UploadResultInfo;
import com.vortex.cloud.ums.model.upload.WorkElementTemp;
import com.vortex.cloud.ums.util.CommonUtils;
import com.vortex.cloud.ums.util.GeoUtil;
import com.vortex.cloud.vfs.data.support.SearchFilter;


@Service("workElementTempService")
public class WorkElementTempServiceImpl implements IWorkElementTempService {

	@Resource
	private IWorkElementService workElementService;
	@Resource
	private IWorkElementTypeService workElementTypeService;
	@Resource
	private ICloudOrganizationService cloudOrganizationService;
	@Resource
	private IUploadResultInfoService uploadResultInfoService;
	@Resource
	private IDeflectService deflectService;

	@Resource
	private ITenantDivisionService tenantDivisionService;

	// 图元编码最大长度
	private static final int WORK_ELEMENT_CODE_MAX_LENGTH = 20;
	// 图元名称最大长度
	private static final int WORK_ELEMENT_NAME_MAX_LENGTH = 20;

	@Override
	public Map<String, Object> importData(WorkElementTemp temp, String marks, int row) throws Exception {
		String tenantId = CommonUtils.getTenantId();
		Map<String, Object> mapResult = Maps.newHashMap();
		boolean succFlag = true;

		StringBuffer message = new StringBuffer();
		WorkElementDto dto = new WorkElementDto();
		WorkElementType workElementType;
		dto.setTenantId(tenantId);

		// 验证编码
		if (StringUtils.isEmpty(temp.getCode())) {
			message.append("编码不能为空；");
			succFlag = false;
		} else if (temp.getCode().length() > WORK_ELEMENT_CODE_MAX_LENGTH) {
			message.append("编码长度不能超过" + WORK_ELEMENT_CODE_MAX_LENGTH + "；");
			succFlag = false;
		} else {
			List<SearchFilter> searchFilters = Lists.newArrayList();
			List<WorkElement> lists = null;
			searchFilters.add(new SearchFilter("code", SearchFilter.Operator.EQ, temp.getCode()));
			lists = workElementService.findListByFilter(CommonUtils.bindTenantId(searchFilters), null);
			if (CollectionUtils.isNotEmpty(lists)) {
				message.append("编码已存在  ");
				succFlag = false;
			} else {
				dto.setCode(temp.getCode());
			}
		}
		// 验证名称
		if (StringUtils.isEmpty(temp.getName())) {
			message.append("名称不能为空；");
			succFlag = false;
		} else if (temp.getName().length() > WORK_ELEMENT_NAME_MAX_LENGTH) {
			message.append("名称长度不能超过" + WORK_ELEMENT_NAME_MAX_LENGTH + "；");
			succFlag = false;
		} else {
			List<SearchFilter> searchFilters = Lists.newArrayList();
			List<WorkElement> lists = null;
			searchFilters.add(new SearchFilter("name", SearchFilter.Operator.EQ, temp.getName()));
			lists = workElementService.findListByFilter(CommonUtils.bindTenantId(searchFilters), null);
			if (CollectionUtils.isNotEmpty(lists)) {
				message.append("名称已存在；");
				succFlag = false;
			} else {
				dto.setName(temp.getName());
			}
		}
		// 验证图元类型
		if (StringUtils.isEmpty(temp.getWorkElementTypeName())) {
			message.append("图元类型不能为空；");
			succFlag = false;
		} else {
			List<SearchFilter> searchFilters = Lists.newArrayList();
			List<WorkElementType> lists = null;
			searchFilters.add(new SearchFilter("name", SearchFilter.Operator.EQ, temp.getWorkElementTypeName()));
			lists = workElementTypeService.findListByFilter(CommonUtils.bindTenantId(searchFilters), null);
			if (CollectionUtils.isEmpty(lists)) {
				message.append("图元类型" + temp.getWorkElementTypeName() + "不存在；");
				succFlag = false;
			} else {
				workElementType = lists.get(0);
				dto.setWorkElementTypeId(workElementType.getId());
				dto.setShape(workElementType.getShape());
			}
		}

		// 验证经纬度序列
		if (StringUtils.isEmpty(temp.getParams())) {
			message.append("经纬度序列不能为空；");
			succFlag = false;
		} else {
			try {
				String params = temp.getParams();
				dto.setParams(params);

				String paramsDone = null;
				if (StringUtils.equals(dto.getShape(), SharpTypeEnum.CIRCLE.getKey())) {
					String center = temp.getParams().substring(0, temp.getParams().lastIndexOf(","));
					String radius = temp.getParams().substring(temp.getParams().lastIndexOf(",") + 1);
					paramsDone = deflectService.deflectToBD(center);
					paramsDone += "," + radius;
					// 半径
					Double radiusValue = Double.valueOf(radius);
					dto.setRadius(radiusValue == null ? 0d : roundPrec2(radiusValue));
					dto.setParamsDone(paramsDone);
					// 面积
					Double area = GeoUtil.getCircleArea(dto.getRadius());
					dto.setArea(area == null ? 0d : roundPrec2(area));
				} else {
					if (StringUtils.equals(dto.getShape(), SharpTypeEnum.LINE.getKey())) {

						// 计算长度
						Double length = GeoUtil.getLength(params);
						dto.setLength(length == null ? 0d : roundPrec2(length));

					} else if (StringUtils.equals(dto.getShape(), SharpTypeEnum.POLYGON.getKey()) || StringUtils.equals(dto.getShape(), SharpTypeEnum.RECTANGLE.getKey())) {
						// 计算面积
						Double area = GeoUtil.getPolygonArea(temp.getParams());
						dto.setArea(area == null ? 0d : roundPrec2(area));

					}
					paramsDone = deflectService.deflectToBD(params);
					dto.setParamsDone(paramsDone);
				}
			} catch (Exception e) {
				message.append("经纬度序列偏转发生异常；");
				succFlag = false;
			}
		}

		// 验证部门名称
		if (StringUtils.isEmpty(temp.getDepartmentName())) {
			message.append("所属机构不能为空；");
			succFlag = false;
		} else {
			List<String> nameList = new ArrayList<>();
			nameList.add(temp.getDepartmentName());
			Map<String, String> nameIdMap = cloudOrganizationService.getDepartmentsOrOrgIdsByName(nameList, tenantId);
			if (MapUtils.isEmpty(nameIdMap)) {
				message.append("所属机构不存在；");
				succFlag = false;
			} else {
				dto.setDepartmentId(nameIdMap.get(temp.getDepartmentName()));
			}
		}

		// 验证行政区划
		if (StringUtils.isEmpty(temp.getDivisionName())) {
			message.append("所属行政区划不能为空；");
			succFlag = false;
		} else {
			List<String> nameList = new ArrayList<>();
			nameList.add(temp.getDivisionName());
			Map<String, String> nameIdMap = tenantDivisionService.getDivisionIdsByNames(nameList, tenantId);
			if (MapUtils.isEmpty(nameIdMap)) {
				message.append("所属行政区划不存在;");
				succFlag = false;
			} else {
				dto.setDivisionId(nameIdMap.get(temp.getDivisionName()));
			}
		}

		if (succFlag) {
			message.insert(0, "第" + row + "行：成功！");
			workElementService.saveWorkElement(dto);
		} else {
			message.insert(0, "第" + row + "行：");
		}

		temp.setUploadTime(new Date());
		temp.setTenantId(tenantId);
		temp.setSuccessful(succFlag);
		temp.setMarks(marks);
		temp.setMessage(message.toString());
		temp.setRowNum(row);

		UploadResultInfo uploadResultInfo = new UploadResultInfo();
		ConvertUtils.register(new DateConverter(null), java.util.Date.class);
		BeanUtils.copyProperties(uploadResultInfo, temp);
		uploadResultInfoService.save(uploadResultInfo);

		mapResult.put("succFlag", succFlag);

		return mapResult;
	}

	/**
	 * 保留两位
	 */
	private Double roundPrec2(Double d) {
		return new BigDecimal(d).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
	}

}
