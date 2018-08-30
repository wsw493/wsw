package com.vortex.cloud.ums.dataaccess.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
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
import com.vortex.cloud.ums.dataaccess.dao.IWorkElementTypeDao;
import com.vortex.cloud.ums.dataaccess.service.ICloudDepartmentService;
import com.vortex.cloud.ums.dataaccess.service.ICloudOrganizationService;
import com.vortex.cloud.ums.dataaccess.service.IWorkElementTypeService;
import com.vortex.cloud.ums.dto.CloudDepartmentDto;
import com.vortex.cloud.ums.dto.CloudOrganizationDto;
import com.vortex.cloud.ums.dto.WorkElementTypeDto;
import com.vortex.cloud.ums.dto.WorkElementTypeSearchDto;
import com.vortex.cloud.ums.model.WorkElement;
import com.vortex.cloud.ums.model.WorkElementType;
import com.vortex.cloud.ums.util.CommonUtils;
import com.vortex.cloud.vfs.common.exception.ServiceException;
import com.vortex.cloud.vfs.common.exception.VortexException;
import com.vortex.cloud.vfs.data.hibernate.repository.HibernateRepository;
import com.vortex.cloud.vfs.data.hibernate.service.SimplePagingAndSortingService;
import com.vortex.cloud.vfs.data.support.SearchFilter;
import com.vortex.cloud.vfs.data.support.SearchFilter.Operator;
import com.vortex.cloud.vfs.data.support.SearchFilters;

@Transactional
@Service("workElementTypeService")
public class WorkElementTypeServiceImpl extends SimplePagingAndSortingService<WorkElementType, String> implements IWorkElementTypeService {

	private static final Logger logger = LoggerFactory.getLogger(WorkElementTypeServiceImpl.class);
	@Resource
	private IWorkElementTypeDao workElementTypeDao;
	@Resource
	private ICloudDepartmentService cloudDepartmentService;
	@Resource
	private ICloudOrganizationService cloudOrganizationService;
	@Resource
	private IWorkElementDao workElementDao;

	@Override
	public HibernateRepository<WorkElementType, String> getDaoImpl() {
		return workElementTypeDao;
	}

	/**
	 * 检测name和code
	 * 
	 * @param workElementType
	 */
	private void checkNameAndCode(WorkElementType workElementType) {
		if (StringUtils.isEmpty(workElementType.getCode())) {
			logger.error("图元类型编码不能为空");
			throw new ServiceException("图元类型编码不能为空");
		}
		if (StringUtils.isEmpty(workElementType.getName())) {
			logger.error("图元类型名称不能为空");
			throw new ServiceException("图元类型名称不能为空");
		}

	}

	@Override
	public void saveWorkElementType(WorkElementTypeDto dto) throws Exception {

		checkNameAndCode(dto);
		boolean exists = isCodeExists(null, dto.getCode(), dto.getTenantId());
		if (exists) {
			logger.error("图元类型code已经存在");
			throw new ServiceException("图元类型code已经存在");
		}
		WorkElementType workElementType = new WorkElementType();
		BeanUtils.copyProperties(dto, workElementType);
		workElementTypeDao.save(workElementType);

	}

	@Override
	public void updateWorkElementType(WorkElementTypeDto dto) throws Exception {
		if (StringUtils.isEmpty(dto.getId())) {
			logger.error("图元类型id不能为空");
			throw new ServiceException("图元类型id不能为空");
		}
		WorkElementType workElementType = this.findOne(dto.getId());
		if (workElementType == null) {
			logger.error("不存在id为" + dto.getId() + "数据");
			throw new ServiceException("不存在id为" + dto.getId() + "数据");
		}
		checkNameAndCode(dto);
		boolean exists = isCodeExists(dto.getId(), dto.getCode(), dto.getTenantId());
		if (exists) {
			logger.error("图元类型code已经存在");
			throw new ServiceException("图元类型code已经存在");
		}

		// 如果图元类型被使用了，那么他的形状不可以修改
		boolean isUsed = isUsed(dto.getId());
		if (isUsed && !StringUtils.equals(dto.getShape(), workElementType.getShape())) {
			logger.error("图元类型已经被使用，不能修改其形状");
			throw new ServiceException("图元类型已经被使用，不能修改其外形");
		}

		workElementType.setCode(dto.getCode());
		workElementType.setDepartmentId(dto.getDepartmentId());
		workElementType.setInfo(dto.getInfo());
		workElementType.setName(dto.getName());
		workElementType.setShape(dto.getShape());
		workElementTypeDao.save(workElementType);

	}

	/**
	 * 该图元类型是否被别的图元使用
	 * 
	 * @param id
	 *            图元id
	 * @return
	 */
	private boolean isUsed(String id) {
		List<SearchFilter> filterList = Lists.newArrayList();
		filterList.add(new SearchFilter("workElementTypeId", Operator.EQ, id));

		SearchFilters searchFilters = new SearchFilters(filterList, SearchFilters.Operator.AND);
		List<WorkElement> list = workElementDao.findListByFilters(searchFilters, null);

		return CollectionUtils.isNotEmpty(list);
	}

	@Override
	public boolean isCodeExists(String id, String value, String tenantId) throws Exception {
		boolean exists = false;
		String newCode = value;
		if (StringUtils.isEmpty(id)) // 新增
		{
			exists = workElementTypeDao.isCodeExists(newCode, tenantId);

		} else {// 更新
			WorkElementTypeDto dto = this.findWorkElementTypeDtoById(id);
			if (dto == null) {
				logger.error("根据id【" + id + "】未找到图元类型！");
				throw new VortexException("根据id【" + id + "】未找到图元类型！");
			}
			if (!newCode.equals(dto.getCode())) { // id不为空，看新旧code是否相同，如果相同，不用判断，如果不相同，需要判断
				exists = workElementTypeDao.isCodeExists(newCode, tenantId);
			}
		}
		return exists;

	}

	@Override
	public boolean isParamExists(String id, String param, String paramValue, String tenantId) throws Exception {
		List<SearchFilter> searchFilter = Lists.newArrayList();
		searchFilter.add(new SearchFilter("workElementType." + param, SearchFilter.Operator.EQ, paramValue));
		List<WorkElementType> list = this.findListByFilter(CommonUtils.bindTenantId(searchFilter), null);

		if (CollectionUtils.isEmpty(list)) {
			return false;
		}
		if (StringUtils.isNotEmpty(id) && list.size() == 1 && list.get(0).getId().equals(id)) {
			return false;
		}
		return true;
	}

	@Override
	public WorkElementTypeDto findWorkElementTypeDtoById(String id) throws Exception {
		if (StringUtils.isEmpty(id)) {
			logger.error("主键id不能为空");
			throw new ServiceException("主键id不能为空");
		}
		WorkElementType workElementType = workElementTypeDao.findOne(id);
		if (workElementType == null) {
			logger.error("不存在id为" + id + "的数据");
			throw new ServiceException("不存在id为" + id + "的数据");
		}

		WorkElementTypeDto workElementTypeDto = new WorkElementTypeDto();

		if (StringUtils.isNotBlank(workElementType.getDepartmentId())) {
			try {
				CloudDepartmentDto cloudDepartmentDto = cloudDepartmentService.getById(workElementType.getDepartmentId());
				if (null != cloudDepartmentDto) {
					workElementTypeDto.setDepartmentName(cloudDepartmentDto.getDepName());
				} else {
					CloudOrganizationDto cloudOrganizationDto = cloudOrganizationService.getById(workElementType.getDepartmentId());
					if (null != cloudOrganizationDto) {
						workElementTypeDto.setDepartmentName(cloudOrganizationDto.getOrgName());
					}
				}
			} catch (Exception e) {
				logger.error("根据部门id查找部门信息失败");
				throw new ServiceException("根据部门id查找部门信息失败");
			}

		}

		BeanUtils.copyProperties(workElementType, workElementTypeDto);
		return workElementTypeDto;
	}

	@Override
	public boolean canBeDelete(String id) {
		List<SearchFilter> filterList = Lists.newArrayList();
		filterList.add(new SearchFilter("workElementTypeId", Operator.EQ, id));

		SearchFilters searchFilters = new SearchFilters(filterList, SearchFilters.Operator.AND);
		List<WorkElement> list = workElementDao.findListByFilters(searchFilters, null);

		return CollectionUtils.isEmpty(list);
	}

	@Override
	public void deleteWorkElementType(String id) throws Exception {
		if (StringUtils.isEmpty(id)) {
			logger.error("id不能为空");
			throw new ServiceException("id不能为空");
		}
		WorkElementTypeDto dto = this.findWorkElementTypeDtoById(id);
		if (null == dto) {
			logger.error("不存在id为" + id + "的数据");
			throw new ServiceException("不存在id为" + id + "的数据");
		}
		if (canBeDelete(id)) {
			workElementTypeDao.delete(id);
		}

	}

	@Override
	public void deleteWorkElementTypes(List<String> ids) throws Exception {
		if (CollectionUtils.isNotEmpty(ids)) {
			for (String id : ids) {
				workElementTypeDao.delete(id);
			}
		}
	}

	@Override
	public List<WorkElementType> findListByCondition(WorkElementTypeSearchDto dto) {
		String tenantId = dto.getTenantId();
		String shapeTypeString = dto.getShapeTypes();
		if (StringUtils.isEmpty(tenantId)) {
			logger.error("tenantId不能为空");
			throw new ServiceException("tenantId不能为空");
		}
		SearchFilters searchFilters = new SearchFilters();
		searchFilters.setOperator(SearchFilters.Operator.AND);
		searchFilters.add(new SearchFilter("tenantId", Operator.EQ, tenantId));
		if (StringUtils.isNotEmpty(shapeTypeString)) { // 类型不为空
			String[] shapeTypes = shapeTypeString.split(",");
			SearchFilters searchFilters2 = new SearchFilters();
			searchFilters2.setOperator(SearchFilters.Operator.OR);
			for (int i = 0; i < shapeTypes.length; i++) {
				searchFilters2.add(new SearchFilter("shape", Operator.EQ, shapeTypes[i]));
			}
			searchFilters.add(searchFilters2);
		}
		List<WorkElementType> workElementTypes = this.findListByFilters(searchFilters, null);
		return workElementTypes;
	}

	/**
	 * 手持端接口处理查询返回数据
	 * 
	 * @param workElementTypes
	 * @return
	 */
	@Override
	public List<Map<String, Object>> processData(List<WorkElementType> workElementTypes) {
		List<Map<String, Object>> data = new ArrayList<>();
		for (WorkElementType workElementType : workElementTypes) {
			Map<String, Object> map = new HashMap<>();
			map.put("id", workElementType.getId());
			map.put("code", workElementType.getCode());
			map.put("name", workElementType.getName());
			map.put("shape", workElementType.getShape());
			map.put("departmentId", workElementType.getDepartmentId());
			map.put("orderIndex", workElementType.getOrderIndex());
			data.add(map);
		}
		return data;
	}

	@Override
	public Map<String, String> getWorkElementTypeNamesByIds(List<String> typeIds) {
		Map<String, String> idNameMap = Maps.newHashMap();
		if (CollectionUtils.isEmpty(typeIds)) {
			return null;
		}
		List<WorkElementType> workElementTypes = workElementTypeDao.findAllByIds(typeIds.toArray(new String[typeIds.size()]));

		if (CollectionUtils.isEmpty(workElementTypes)) {
			return idNameMap;
		}

		for (WorkElementType workElementType : workElementTypes) {
			idNameMap.put(workElementType.getId(), workElementType.getName());
		}
		return idNameMap;

	}

	@Override
	public Page<WorkElementType> findPageByPermission(Pageable pageable, List<SearchFilter> searchFilters, String userId, String tenantId) {

		// 获取该用户有权限的org和department，(自定义中全选才认为有权限)
		List<String> companyIds = cloudOrganizationService.getCompanyIdsWithPermission(userId, tenantId);

		SearchFilters andSearchFilters = new SearchFilters(searchFilters, SearchFilters.Operator.AND);

		SearchFilters orSearchFilters = new SearchFilters();
		orSearchFilters.setOperator(SearchFilters.Operator.OR);
		if (CollectionUtils.isNotEmpty(companyIds)) {
			orSearchFilters.add(new SearchFilter("departmentId", Operator.IN, companyIds.toArray()));
		}
		orSearchFilters.add(new SearchFilter("departmentId", Operator.NULL, null));
		orSearchFilters.add(new SearchFilter("departmentId", Operator.EQ, ""));

		andSearchFilters.add(orSearchFilters);
		Page<WorkElementType> page = workElementTypeDao.findPageByFilters(pageable, andSearchFilters);
		return page;
	}
}
