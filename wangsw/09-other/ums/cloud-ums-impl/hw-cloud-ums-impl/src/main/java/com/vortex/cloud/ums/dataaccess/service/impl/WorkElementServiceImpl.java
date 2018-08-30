package com.vortex.cloud.ums.dataaccess.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.vortex.cloud.ums.dataaccess.dao.IWorkElementDao;
import com.vortex.cloud.ums.dataaccess.service.ICloudDepartmentService;
import com.vortex.cloud.ums.dataaccess.service.ICloudOrganizationService;
import com.vortex.cloud.ums.dataaccess.service.ITenantDivisionService;
import com.vortex.cloud.ums.dataaccess.service.IWorkElementService;
import com.vortex.cloud.ums.dataaccess.service.IWorkElementTypeService;
import com.vortex.cloud.ums.dto.CloudDepartmentDto;
import com.vortex.cloud.ums.dto.CloudOrganizationDto;
import com.vortex.cloud.ums.dto.WorkElementDto;
import com.vortex.cloud.ums.dto.WorkElementPageDto;
import com.vortex.cloud.ums.model.WorkElement;
import com.vortex.cloud.ums.model.WorkElementType;
import com.vortex.cloud.ums.util.PropertyUtils;
import com.vortex.cloud.ums.util.utils.ConnectHttpService;
import com.vortex.cloud.vfs.common.exception.ServiceException;
import com.vortex.cloud.vfs.common.exception.VortexException;
import com.vortex.cloud.vfs.common.mapper.JsonMapper;
import com.vortex.cloud.vfs.data.dto.RestResultDto;
import com.vortex.cloud.vfs.data.hibernate.repository.HibernateRepository;
import com.vortex.cloud.vfs.data.hibernate.service.SimplePagingAndSortingService;
import com.vortex.cloud.vfs.data.support.SearchFilter;
import com.vortex.cloud.vfs.data.support.SearchFilter.Operator;
import com.vortex.cloud.vfs.data.support.SearchFilters;

@Transactional
@Service("workElementService")
public class WorkElementServiceImpl extends SimplePagingAndSortingService<WorkElement, String> implements IWorkElementService {
	private static final Logger logger = LoggerFactory.getLogger(WorkElementServiceImpl.class);

	@Resource
	private IWorkElementDao workElementDao;
	@Resource
	private ICloudDepartmentService restDepartmentService;
	@Resource
	private IWorkElementTypeService workElementTypeService;
	@Resource
	private ICloudDepartmentService cloudDepartmentService;
	@Resource
	private ICloudOrganizationService cloudOrganizationService;

	@Resource
	private ITenantDivisionService tenantDivisionService;

	private static final String URL_LBS = PropertyUtils.getPropertyValue("URL_LBS");
	private static final String REST_FUL = "/vortexapi/rest/lbs/coordconvert/v1";

	@Override
	public HibernateRepository<WorkElement, String> getDaoImpl() {
		return workElementDao;
	}

	private void checkData(WorkElementDto dto) {
		if (null == dto) {
			throw new ServiceException("dto不能为空");
		}
		if (StringUtils.isEmpty(dto.getCode())) {
			throw new ServiceException("图元code不能为空");
		}
		if (StringUtils.isEmpty(dto.getName())) {
			throw new ServiceException("图元名称不能为空");
		}
		if (StringUtils.isEmpty(dto.getShape())) {
			throw new ServiceException("图元形状不能为空");
		}
		if (StringUtils.isEmpty(dto.getParamsDone())) {
			throw new ServiceException("偏转后经纬度不能为空");
		}

		/* 偏转后经纬度（百度）转原始经纬度--start */
		Map<String, Object> map = Maps.newHashMap();
		map.put("location", dto.getParamsDone());
		map.put("from", "bd09");
		map.put("to", "wgs84");

		// 请求数据(获取偏转前 经纬度)
		String result = ConnectHttpService.callHttp(URL_LBS + REST_FUL, ConnectHttpService.METHOD_GET, map);
		JsonMapper jm = new JsonMapper();
		Map<String, Object> resultData = jm.fromJson(result, HashMap.class);

		if (MapUtils.isNotEmpty(resultData) && resultData.get("status") == RestResultDto.RESULT_SUCC) {
			List<Map<String, Object>> locations = (List<Map<String, Object>>) resultData.get("locations");

			String params = "";
			for (Map<String, Object> location : locations) {
				params += location.get("longitudeDone") + "," + location.get("latitudeDone") + ";";
			}
			params = params.substring(0, params.lastIndexOf(";"));

			dto.setParams(params);
		}
		/* 偏转后经纬度（百度）转原始经纬度--end */
	}

	@Override
	public WorkElementDto getWorkElementById(String id) throws Exception {
		if (StringUtils.isEmpty(id)) {
			logger.error("id不能为空");
			throw new ServiceException("id不能为空");
		}
		WorkElement workElement = workElementDao.findOne(id);

		if (null == workElement) {
			logger.error("不存在id为" + id + "的数据");
			throw new ServiceException("不存在id为" + id + "的数据");
		}

		WorkElementDto dto = new WorkElementDto();

		// 查询机构名称
		if (StringUtils.isNotEmpty(workElement.getDepartmentId())) {
			CloudDepartmentDto cloudDepartmentDto = cloudDepartmentService.getById(workElement.getDepartmentId());
			if (null != cloudDepartmentDto) {
				dto.setDepartmentName(cloudDepartmentDto.getDepName());
			} else {
				CloudOrganizationDto cloudOrganizationDto = cloudOrganizationService.getById(workElement.getDepartmentId());
				if (null != cloudOrganizationDto) {
					dto.setDepartmentName(cloudOrganizationDto.getOrgName());
				}
			}
		}

		// 设置行政区划名称
		if (StringUtils.isNotBlank(workElement.getDivisionId())) {
			Map<String, String> idNameMap = tenantDivisionService.getDivisionNamesByIds(Arrays.asList(new String[] { workElement.getDivisionId() }));
			if (MapUtils.isNotEmpty(idNameMap)) {
				dto.setDivisionName(idNameMap.get(workElement.getDivisionId()));
			}

		}

		WorkElementType workElementType = workElementTypeService.findOne(workElement.getWorkElementTypeId());
		if (null != workElementType) {
			dto.setWorkElementTypeCode(workElementType.getCode());
			dto.setWorkElementTypeName(workElementType.getName());
		}

		BeanUtils.copyProperties(workElement, dto);
		return dto;
	}

	@Override
	public WorkElementDto saveWorkElement(WorkElementDto dto) throws Exception {
		checkData(dto);
		WorkElement entity = new WorkElement();
		BeanUtils.copyProperties(dto, entity);
		workElementDao.save(entity);
		BeanUtils.copyProperties(entity, dto);
		return dto;
	}

	@Override
	public WorkElementDto updateWorkElement(WorkElementDto dto) throws Exception {
		checkData(dto);
		if (StringUtils.isEmpty(dto.getId())) {
			logger.error("id不能为空");
			throw new ServiceException("id不能为空");
		}
		WorkElement entity = workElementDao.findOne(dto.getId());
		BeanUtils.copyProperties(dto, entity, "id", "status", "createTime", "lastChangeTime", "tenantId");
		workElementDao.update(entity);
		BeanUtils.copyProperties(entity, dto);
		return dto;
	}

	@Override
	public void deleteWorkElement(String id) throws Exception {
		if (StringUtils.isEmpty(id)) {
			logger.error("id不能为空");
			throw new ServiceException("id不能为空");
		}
		WorkElement workElement = this.getWorkElementById(id);
		if (null == workElement) {
			logger.error("不存在id为" + id + "的数据");
			throw new ServiceException("不存在id为" + id + "的数据");
		}
		workElementDao.delete(id);
	}

	@Override
	public void deleteWorkElements(List<String> ids) throws Exception {
		if (CollectionUtils.isNotEmpty(ids)) {
			for (String id : ids) {
				workElementDao.delete(id);
			}
		}
	}

	@Override
	public boolean isParamNameExists(String id, String paramName, String value, String tenantId) throws Exception {
		List<SearchFilter> searchFilter = Lists.newArrayList();
		searchFilter.add(new SearchFilter(paramName, SearchFilter.Operator.EQ, value));
		searchFilter.add(new SearchFilter("tenantId", SearchFilter.Operator.EQ, tenantId));
		List<WorkElement> list = this.findListByFilter(searchFilter, null);

		if (CollectionUtils.isEmpty(list)) {
			return false;
		}
		if (StringUtils.isNotEmpty(id) && list.size() == 1 && list.get(0).getId().equals(id)) {
			return false;
		}
		return true;
	}

	@SuppressWarnings("null")
	@Override
	public List<WorkElement> getWorkElementsByType(String[] shapeTypes, String tenantId) throws Exception {
		if (shapeTypes == null && shapeTypes.length <= 0) {
			logger.error("shapeTypes不能为空！");
			throw new VortexException("shapeTypes不能为空");
		}
		if (StringUtils.isEmpty(tenantId)) {
			logger.error("tenantId不能为空！");
			throw new VortexException("tenantId不能为空");
		}
		SearchFilters searchFilters = new SearchFilters();
		searchFilters.setOperator(SearchFilters.Operator.AND);
		searchFilters.add(new SearchFilter("tenantId", Operator.EQ, tenantId));
		SearchFilters searchFilters2 = new SearchFilters();
		searchFilters2.setOperator(SearchFilters.Operator.OR);
		for (int i = 0; i < shapeTypes.length; i++) {
			searchFilters2.add(new SearchFilter("shapeType", Operator.EQ, shapeTypes[i]));
		}
		searchFilters.add(searchFilters2);
		List<WorkElement> list = workElementDao.findListByFilters(searchFilters, null);
		return list;
	}

	@Override
	public List<WorkElementDto> transferModelToDto(List<WorkElement> list) {
		List<WorkElementDto> dtos = new ArrayList<WorkElementDto>();
		List<String> divisionList = Lists.newArrayList();// 所有的图元的行政区划list
		List<String> companyIds = Lists.newArrayList();// 所属公司ids
		List<String> typeIds = Lists.newArrayList();// 图元类型ids
		for (WorkElement workElement : list) {

			WorkElementDto dto = new WorkElementDto();
			// 所属公司ids
			companyIds.add(workElement.getDepartmentId());
			// 图元类型ids
			typeIds.add(workElement.getWorkElementTypeId());
			// 行政区划ids
			divisionList.add(workElement.getDivisionId());

			BeanUtils.copyProperties(workElement, dto);

			dtos.add(dto);
		}

		// 设置图元类型name和code
		if (CollectionUtils.isNotEmpty(typeIds)) {
			List<WorkElementType> types = workElementTypeService.findAllByIds(typeIds.toArray(new String[typeIds.size()]));
			Map<String, WorkElementType> idTypeMap = Maps.newHashMap();
			if (CollectionUtils.isNotEmpty(types)) {
				for (WorkElementType workElementType : types) {
					idTypeMap.put(workElementType.getId(), workElementType);
				}
			}
			if (MapUtils.isNotEmpty(idTypeMap)) {
				for (WorkElementDto workElementDto : dtos) {
					workElementDto.setWorkElementTypeName(idTypeMap.get(workElementDto.getWorkElementTypeId()).getName());
					workElementDto.setWorkElementTypeCode(idTypeMap.get(workElementDto.getWorkElementTypeId()).getCode());
				}
			}
		}
		// 设置公司名称
		if (CollectionUtils.isNotEmpty(companyIds)) {
			Map<String, String> compIdNameMap = cloudOrganizationService.getDepartmentsOrOrgNamesByIds(companyIds.toArray(new String[companyIds.size()]));
			if (MapUtils.isNotEmpty(compIdNameMap)) {
				for (WorkElementDto workElementDto : dtos) {
					workElementDto.setDepartmentName(compIdNameMap.get(workElementDto.getDepartmentId()));
				}
			}
		}
		// 设置行政区划名称
		if (CollectionUtils.isNotEmpty(divisionList)) {
			Map<String, String> idNameMap = tenantDivisionService.getDivisionNamesByIds(divisionList);
			if (MapUtils.isNotEmpty(idNameMap)) {
				for (WorkElementDto workElementDto : dtos) {
					workElementDto.setDivisionName(idNameMap.get(workElementDto.getDivisionId()));
				}
			}
		}
		return dtos;
	}

	@Override
	public List<WorkElementPageDto> syncWeByPage(String tenantId, long syncTime, Integer pageSize, Integer pageNumber) {
		return workElementDao.syncWeByPage(tenantId, syncTime, pageSize, pageNumber);
	}

	@Override
	public Page<WorkElement> syncWorkElementsByPage(Pageable pageable, Map<String, Object> paramMap) {
		return workElementDao.syncWorkElementsByPage(pageable, paramMap);
	}

}
