package com.vortex.cloud.ums.dataaccess.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.vortex.cloud.ums.dataaccess.dao.ICloudDepartmentDao;
import com.vortex.cloud.ums.dataaccess.dao.ICloudOrganizationDao;
import com.vortex.cloud.ums.dataaccess.dao.ICloudStaffDao;
import com.vortex.cloud.ums.dataaccess.dao.ICloudUserDao;
import com.vortex.cloud.ums.dataaccess.dao.ITenantDivisionDao;
import com.vortex.cloud.ums.dataaccess.service.ICloudDepartmentService;
import com.vortex.cloud.ums.dataaccess.service.ICloudOrganizationService;
import com.vortex.cloud.ums.dataaccess.service.ICloudUserService;
import com.vortex.cloud.ums.dto.CloudOrganizationDto;
import com.vortex.cloud.ums.dto.CloudUserDto;
import com.vortex.cloud.ums.dto.TenantDeptOrgDto;
import com.vortex.cloud.ums.enums.CompanyTypeEnum;
import com.vortex.cloud.ums.enums.KafkaTopicEnum;
import com.vortex.cloud.ums.enums.PermissionScopeEnum;
import com.vortex.cloud.ums.enums.SyncFlagEnum;
import com.vortex.cloud.ums.model.CloudDepartment;
import com.vortex.cloud.ums.model.CloudOrganization;
import com.vortex.cloud.ums.model.CloudStaff;
import com.vortex.cloud.ums.model.CloudUser;
import com.vortex.cloud.ums.model.TenantDivision;
import com.vortex.cloud.ums.mq.produce.KafkaProducer;
import com.vortex.cloud.ums.tree.DepartmentTree;
import com.vortex.cloud.vfs.common.exception.ServiceException;
import com.vortex.cloud.vfs.common.exception.VortexException;
import com.vortex.cloud.vfs.data.hibernate.repository.HibernateRepository;
import com.vortex.cloud.vfs.data.hibernate.service.SimplePagingAndSortingService;
import com.vortex.cloud.vfs.data.support.SearchFilter;
import com.vortex.cloud.vfs.data.support.SearchFilter.Operator;

@Transactional
@Service("cloudOrganizationService")
public class CloudOrganizationServiceImpl extends SimplePagingAndSortingService<CloudOrganization, String> implements ICloudOrganizationService {

	private static final Logger logger = LoggerFactory.getLogger(CloudOrganizationServiceImpl.class);

	@Resource
	private ICloudOrganizationDao cloudOrganizationDao;

	@Resource
	private ICloudDepartmentService cloudDepartmentService;
	@Resource
	private ICloudDepartmentDao cloudDepartmentDao;

	@Resource
	private ICloudUserService cloudUserService;
	@Resource
	private ICloudUserDao cloudUserDao;
	@Resource
	private ICloudStaffDao cloudStaffDao;

	@Resource
	private ITenantDivisionDao tenantDivisionDao;

	@Override
	public HibernateRepository<CloudOrganization, String> getDaoImpl() {
		return cloudOrganizationDao;
	}

	@Override
	public boolean isCodeExisted(String tenantId, String code) {
		if (StringUtils.isBlank(tenantId) || StringUtils.isBlank(code)) {
			throw new ServiceException("isCodeExisted(), 入参为空");
		}

		boolean result = false;

		List<SearchFilter> filterList = new ArrayList<>();
		filterList.add(new SearchFilter("tenantId", Operator.EQ, tenantId));
		filterList.add(new SearchFilter("orgCode", Operator.EQ, code));
		List<CloudOrganization> list = this.findListByFilter(filterList, null);

		if (CollectionUtils.isNotEmpty(list)) {
			result = true;
		}

		return result;
	}

	@Override
	public CloudOrganization save(CloudOrganizationDto dto) {
		this.validateOnSave(dto);

		Double longitude = dto.getLongitude();
		Double latitude = dto.getLatitude();
		if (longitude != null && latitude != null) {
			dto.setLngLats(longitude + "," + latitude);
		}

		CloudOrganization org = new CloudOrganization();
		BeanUtils.copyProperties(dto, org);
		org = cloudOrganizationDao.save(org);
		try {
			KafkaProducer.getInstance().produce(KafkaTopicEnum.UMS_ORGAN_SYNC.getKey(), SyncFlagEnum.ADD.getKey(), org);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return org;
	}

	public void validateOnSave(CloudOrganizationDto dto) {

		this.validateForm(dto);

		// 逻辑业务校验
		if (this.isCodeExisted(dto.getTenantId(), dto.getOrgCode())) {
			throw new ServiceException("编号已存在！");
		}
	}

	private void validateForm(CloudOrganizationDto dto) {
		if (StringUtils.isBlank(dto.getTenantId())) {
			throw new ServiceException("租户ID为空");
		}

		if (StringUtils.isBlank(dto.getDepartmentId())) {
			throw new ServiceException("单位ID为空");
		}

		if (StringUtils.isBlank(dto.getParentId())) {
			throw new ServiceException("上级机构ID为空");
		}

		if (StringUtils.isBlank(dto.getOrgCode())) {
			throw new ServiceException("编码为空");
		}

		if (StringUtils.isBlank(dto.getOrgName())) {
			throw new ServiceException("名称为空");
		}
	}

	@Override
	public CloudOrganizationDto getById(String id) {
		CloudOrganization org = cloudOrganizationDao.findOne(id);
		if (org == null) {
			return null;
		}

		CloudOrganizationDto dto = new CloudOrganizationDto();
		BeanUtils.copyProperties(org, dto);

		/*
		 * 设置parent name。注意：单位和机构分开存在于2张表中。 如果是单位下的第一级机构，则parent为单位 否则，为上一级机构
		 */
		String parentId = dto.getParentId();
		String parentName = null;
		if (parentId.equals(dto.getDepartmentId())) { // 第一级机构
			CloudDepartment department = cloudDepartmentService.findOne(parentId);
			parentName = department.getDepName();
		} else {
			CloudOrganization organization = this.findOne(parentId);
			parentName = organization.getOrgName();
		}
		dto.setParentName(parentName);

		this.setLngLat(dto);

		// 处理行政区划
		if (StringUtils.isNotEmpty(dto.getDivisionId())) {
			TenantDivision td = tenantDivisionDao.findOne(dto.getDivisionId());
			if (td != null) {
				dto.setDivisionName(td.getName());
			}
		}

		return dto;
	}

	/**
	 * 分离出经纬度
	 * 
	 * @param dto
	 */
	private void setLngLat(CloudOrganizationDto dto) {
		String lngLats = dto.getLngLats();
		if (StringUtils.isNotBlank(lngLats)) {

			String[] lngLatArr = lngLats.split(",");
			if (ArrayUtils.isEmpty(lngLatArr) || lngLatArr.length != 2) {
				logger.error("getById(),经纬度为非法字符串，不能解析");
				throw new ServiceException("经纬度为非法字符串，不能解析");
			}
			String lng = lngLatArr[0];
			String lat = lngLatArr[1];
			if (StringUtils.isNotBlank(lng) && StringUtils.isNotBlank(lat)) {
				dto.setLongitude(Double.parseDouble(lng));
				dto.setLatitude(Double.parseDouble(lat));
			}
		}
	}

	@Override
	public void update(CloudOrganizationDto dto) {
		// 入参数校验
		this.validateOnUpdate(dto);

		Double longitude = dto.getLongitude();
		Double latitude = dto.getLatitude();
		if (longitude != null && latitude != null) {
			dto.setLngLats(longitude + "," + latitude);
		}

		CloudOrganization old = cloudOrganizationDao.findOne(dto.getId());
		old.setAddress(dto.getAddress());
		old.setDescription(dto.getDescription());
		old.setEmail(dto.getEmail());
		old.setHead(dto.getHead());
		old.setHeadMobile(dto.getHeadMobile());
		old.setLngLats(dto.getLngLats());
		old.setOrgCode(dto.getOrgCode());
		old.setOrgName(dto.getOrgName());
		old.setOrderIndex(dto.getOrderIndex());
		old.setDivisionId(dto.getDivisionId());
		cloudOrganizationDao.update(old);
		try {
			KafkaProducer.getInstance().produce(KafkaTopicEnum.UMS_ORGAN_SYNC.getKey(), SyncFlagEnum.UPDATE.getKey(), old);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void validateOnUpdate(CloudOrganizationDto dto) {

		this.validateForm(dto);

		// 逻辑业务校验
		if (!this.validateCodeOnUpdate(dto.getTenantId(), dto.getId(), dto.getOrgCode())) {
			throw new ServiceException("编号已存在！");
		}
	}

	@Override
	public boolean validateCodeOnUpdate(String tenantId, String id, String newCode) {
		CloudOrganization oldOrg = cloudOrganizationDao.findOne(id);
		String oldCode = oldOrg.getOrgCode();

		if (newCode.equals(oldCode)) // 没有修改
		{
			return true;
		} else {
			boolean isExisted = this.isCodeExisted(tenantId, newCode);
			if (isExisted) {
				return false;
			} else {
				return true;
			}
		}
	}

	@Override
	public Map<String, Object> getDepartmentOrOrgNameById(String id) {
		CloudDepartment dto = cloudDepartmentDao.findOne(id);
		CloudOrganization organization = cloudOrganizationDao.findOne(id);
		Map<String, Object> nameMap = Maps.newHashMap();
		if (dto != null) {
			nameMap.put(dto.getId(), dto.getDepName());
		} else if (organization != null) {
			nameMap.put(organization.getId(), organization.getOrgName());
		}
		return nameMap;

	}

	@Override
	public Map<String, String> getDepartmentsOrOrgNamesByIds(String[] ids) {
		if (ArrayUtils.isEmpty(ids)) {
			return null;
		}
		List<TenantDeptOrgDto> tenantDeptOrgDtos = cloudOrganizationDao.getDepartmentsOrOrgByIds(ids, null);
		Map<String, String> nameMap = Maps.newHashMap();
		if (CollectionUtils.isNotEmpty(tenantDeptOrgDtos)) {
			for (TenantDeptOrgDto tenantDeptOrgDto : tenantDeptOrgDtos) {
				nameMap.put(tenantDeptOrgDto.getId(), tenantDeptOrgDto.getName());
			}
		}
		return nameMap;
	}

	@Override
	public List<Map<String, String>> getDepartmentsOrOrgByCondiction(Map<String, Object> paramMap) {
		List<SearchFilter> searchFilters = Lists.newArrayList();
		searchFilters.add(new SearchFilter("tenantId", Operator.EQ, paramMap.get("tenantId")));

		// 公司类型 对应CompanyTypeEnum中的key
		String companyType = (String) paramMap.get("companyType");

		List<CloudDepartment> cloudDepartments = null;
		List<CloudOrganization> organizations = null;

		if (StringUtils.isBlank(companyType)) {// 不过滤类型
			cloudDepartments = cloudDepartmentDao.findListByFilter(searchFilters, null);
			organizations = cloudOrganizationDao.findListByFilter(searchFilters, null);
		} else {
			if (companyType.equals(CompanyTypeEnum.DEPART.getKey())) { // 过滤department类型
				cloudDepartments = cloudDepartmentDao.findListByFilter(searchFilters, null);
			} else if (companyType.equals(CompanyTypeEnum.ORG.getKey())) {// 过滤department类型
				organizations = cloudOrganizationDao.findListByFilter(searchFilters, null);
			}
		}

		List<Map<String, String>> list = Lists.newArrayList();// 所有列表

		List<Map<String, String>> departmentList = Lists.newArrayList(); // department列表

		List<Map<String, String>> orgList = Lists.newArrayList();// org 列表

		if (CollectionUtils.isNotEmpty(cloudDepartments)) {
			for (CloudDepartment cloudDepartment : cloudDepartments) {
				Map<String, String> Map = Maps.newHashMap();
				Map.put("id", cloudDepartment.getId());
				Map.put("text", cloudDepartment.getDepName());
				Map.put("parentId", "-1");
				Map.put("companyType", CompanyTypeEnum.DEPART.getKey());// 公司类型是department
				departmentList.add(Map);
			}
		}
		if (CollectionUtils.isNotEmpty(organizations)) {
			for (CloudOrganization cloudOrganization : organizations) {
				Map<String, String> Map = Maps.newHashMap();
				Map.put("id", cloudOrganization.getId());
				Map.put("text", cloudOrganization.getOrgName());
				Map.put("parentId", cloudOrganization.getParentId());
				Map.put("companyType", CompanyTypeEnum.ORG.getKey()); // 公司类型是org
				orgList.add(Map);
			}
		}
		list.addAll(departmentList);
		list.addAll(orgList);

		return list;
	}

	@Override
	public void deleteOrg(String orgId) {
		if (StringUtils.isEmpty(orgId)) {
			logger.error("删除机构时，传入的id为空!");
			throw new VortexException("删除机构时，传入的id为空!");
		}

		CloudOrganization org = cloudOrganizationDao.findOne(orgId);
		if (org == null) {
			logger.error("根据id[" + orgId + "]未找到机构信息！");
			throw new VortexException("根据id[" + orgId + "]未找到机构信息！");
		}

		if (cloudOrganizationDao.hasStaff(orgId)) {
			logger.error("该机构下存在有效的人员，无法删除！");
			throw new VortexException("该机构下存在有效的人员，无法删除！");
		}

		if (cloudOrganizationDao.hasChild(orgId)) {
			logger.error("该机构下存在有效的子机构，无法删除！");
			throw new VortexException("该机构下存在有效的子机构，无法删除！");
		}

		cloudOrganizationDao.delete(orgId);

		try {
			KafkaProducer.getInstance().produce(KafkaTopicEnum.UMS_ORGAN_SYNC.getKey(), SyncFlagEnum.DELETE.getKey(), org);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public Map<String, String> getDepartmentsOrOrgIdsByName(List<String> names, String tenantId) {
		List<SearchFilter> searchFilters1 = Lists.newArrayList();
		List<SearchFilter> searchFilters2 = Lists.newArrayList();
		searchFilters1.add(new SearchFilter("tenantId", Operator.EQ, tenantId));
		searchFilters2.add(new SearchFilter("tenantId", Operator.EQ, tenantId));
		if (CollectionUtils.isNotEmpty(names)) {
			searchFilters1.add(new SearchFilter("depName", Operator.IN, names.toArray()));
			searchFilters2.add(new SearchFilter("orgName", Operator.IN, names.toArray()));
		}
		List<CloudDepartment> cloudDepartments = cloudDepartmentDao.findListByFilter(searchFilters1, null);
		List<CloudOrganization> organizations = cloudOrganizationDao.findListByFilter(searchFilters2, null);
		Map<String, String> nameIdMap = Maps.newHashMap();
		if (CollectionUtils.isNotEmpty(cloudDepartments)) {
			for (CloudDepartment cloudDepartment : cloudDepartments) {
				nameIdMap.put(cloudDepartment.getDepName(), cloudDepartment.getId());
			}
		}
		if (CollectionUtils.isNotEmpty(organizations)) {
			for (CloudOrganization cloudOrganization : organizations) {
				nameIdMap.put(cloudOrganization.getOrgName(), cloudOrganization.getId());
			}
		}
		return nameIdMap;
	}

	@Override
	public Map<String, Object> getDepartmentsOrOrgByName(List<String> names, String tenantId) {
		List<SearchFilter> searchFilters1 = Lists.newArrayList();
		List<SearchFilter> searchFilters2 = Lists.newArrayList();
		searchFilters1.add(new SearchFilter("tenantId", Operator.EQ, tenantId));
		searchFilters2.add(new SearchFilter("tenantId", Operator.EQ, tenantId));
		if (CollectionUtils.isNotEmpty(names)) {
			searchFilters1.add(new SearchFilter("depName", Operator.IN, names.toArray()));
			searchFilters2.add(new SearchFilter("orgName", Operator.IN, names.toArray()));
		}
		List<CloudDepartment> cloudDepartments = cloudDepartmentDao.findListByFilter(searchFilters1, null);
		List<CloudOrganization> organizations = cloudOrganizationDao.findListByFilter(searchFilters2, null);
		Map<String, Object> nameIdMap = Maps.newHashMap();
		if (CollectionUtils.isNotEmpty(cloudDepartments)) {
			for (CloudDepartment cloudDepartment : cloudDepartments) {
				nameIdMap.put(cloudDepartment.getDepName(), cloudDepartment);
			}
		}
		if (CollectionUtils.isNotEmpty(organizations)) {
			for (CloudOrganization cloudOrganization : organizations) {
				nameIdMap.put(cloudOrganization.getOrgName(), cloudOrganization);
			}
		}
		return nameIdMap;
	}

	@Override
	public TenantDeptOrgDto getDepartmentOrOrgById(String rootDeptId, List<Integer> beenDeletedFlags) {
		TenantDeptOrgDto tenantDeptOrgDto = new TenantDeptOrgDto();
		if (StringUtils.isEmpty(rootDeptId)) {
			return null;
		}
		// CloudDepartment dto = cloudDepartmentDao.findOne(rootDeptId);
		// CloudOrganization organization = cloudOrganizationDao.findOne(rootDeptId);
		CloudDepartment dto = cloudDepartmentDao.findById(rootDeptId, beenDeletedFlags);
		CloudOrganization organization = cloudOrganizationDao.findById(rootDeptId, beenDeletedFlags);
		if (dto == null && organization == null) {
			logger.error("根据id[" + rootDeptId + "]未找到机构信息！");
			throw new VortexException("根据id[" + rootDeptId + "]未找到机构信息！");
		}
		if (organization != null) {
			tenantDeptOrgDto.setId(organization.getId());
			tenantDeptOrgDto.setName(organization.getOrgName());
			tenantDeptOrgDto.setCompanyType(CompanyTypeEnum.ORG.getKey());
			tenantDeptOrgDto.setTenantId(organization.getTenantId());
			tenantDeptOrgDto.setCode(organization.getOrgCode());
			tenantDeptOrgDto.setDepartmentId(organization.getDepartmentId());
			tenantDeptOrgDto.setLngLats(organization.getLngLats());
			tenantDeptOrgDto.setDescription(organization.getDescription());
			tenantDeptOrgDto.setHead(organization.getHead());
			tenantDeptOrgDto.setHeadMobile(organization.getHeadMobile());
			tenantDeptOrgDto.setAddress(organization.getAddress());
			tenantDeptOrgDto.setEmail(organization.getEmail());
			tenantDeptOrgDto.setType("3");
			tenantDeptOrgDto.setParentId(organization.getParentId());
		} else {
			tenantDeptOrgDto.setId(dto.getId());
			tenantDeptOrgDto.setName(dto.getDepName());
			tenantDeptOrgDto.setCompanyType(CompanyTypeEnum.DEPART.getKey());
			tenantDeptOrgDto.setTenantId(dto.getTenantId());
			tenantDeptOrgDto.setCode(dto.getDepCode());
			tenantDeptOrgDto.setDepartmentId(dto.getId());
			tenantDeptOrgDto.setLngLats(dto.getLngLats());
			tenantDeptOrgDto.setDescription(dto.getDescription());
			tenantDeptOrgDto.setHead(dto.getHead());
			tenantDeptOrgDto.setHeadMobile(dto.getHeadMobile());
			tenantDeptOrgDto.setAddress(dto.getAddress());
			tenantDeptOrgDto.setEmail(dto.getEmail());
			tenantDeptOrgDto.setType(dto.getDepType());
			tenantDeptOrgDto.setParentId(DepartmentTree.ROOT_NODE_ID);
		}

		return tenantDeptOrgDto;
	}

	@Override
	public List<TenantDeptOrgDto> getDepartmentsOrOrgByIds(String[] ids) {
		if (ArrayUtils.isEmpty(ids)) {
			return Lists.newArrayList();
		}
		return cloudOrganizationDao.getDepartmentsOrOrgByIds(ids, null);
	}

	/**
	 * 获取用户
	 * 
	 * @param userId
	 * @return
	 */
	private CloudUserDto findByUserId(String userId) {
		return cloudUserService.getById(userId);
	}

	private List<TenantDeptOrgDto> findDeptOrgList(String tenantId, String deptId, List<Integer> beenDeletedFlags) {
		ICloudDepartmentService service = cloudDepartmentService;

		return service.findDeptOrgList(tenantId, deptId, beenDeletedFlags);
	}

	/**
	 * 根据orgId或者是departmentid 查询所有子节点
	 * 
	 * @param tenantId
	 * @param companyId
	 * @return
	 */
	private List<TenantDeptOrgDto> findDeptOrgListByCompandyId(String tenantId, String companyId, List<Integer> beenDeletedFlags) {
		ICloudDepartmentService service = cloudDepartmentService;

		return service.findDeptOrgListByCompandyId(tenantId, companyId, beenDeletedFlags);
	}

	private TenantDeptOrgDto getCompany(String companyId, List<Integer> beenDeletedFlags) {

		return this.getDepartmentOrOrgById(companyId, beenDeletedFlags);
	}

	@Override
	public List<String> getCompanyIdsWithPermission(String userId, String tenantId) {
		Map<String, Object> paramMap = Maps.newHashMap();
		List<String> companyIds = Lists.newArrayList();
		paramMap.put("isControlPermission", "1");
		paramMap.put("tenantId", tenantId);
		paramMap.put("userId", userId);
		// 获取该用户有权限的org和department，(自定义中全选才认为有权限)
		List<TenantDeptOrgDto> tenantDeptOrgDtos = this.loadDepartmentsWithPermission(paramMap);

		// 该用户下没有权限，直接返回
		if (CollectionUtils.isEmpty(tenantDeptOrgDtos)) {
			return null;
		}

		for (TenantDeptOrgDto tenantDeptOrgDto : tenantDeptOrgDtos) {
			companyIds.add(tenantDeptOrgDto.getId());
		}
		return companyIds;
	}

	@Override
	public List<TenantDeptOrgDto> loadDepartmentsWithPermission(Map<String, Object> param) {

		String userId = (String) param.get("userId");
		String tenantId = (String) param.get("tenantId");
		String isControlPermission = (String) param.get("isControlPermission");
		List<Integer> beenDeletedFlags = (List<Integer>) param.get("beenDeletedFlags");
		// 获取用户
		CloudUser user = cloudUserService.findOne(userId);

		String scope = user.getCustomScope();
		// 人员信息
		CloudStaff staffDto = cloudStaffDao.findOne(user.getStaffId());
		String orgId = null;
		String departmentId = null;
		String companyId = null;
		if (staffDto != null) {
			orgId = staffDto.getOrgId();
			departmentId = staffDto.getDepartmentId();
			companyId = StringUtils.isNotBlank(orgId) ? orgId : departmentId;
			tenantId = staffDto.getTenantId();
		}
		// 自定义范围
		String[] customScope = StringUtils.isNotEmpty(scope) ? scope.split(",") : null;
		// 没有设置范围，直接返回
		List<TenantDeptOrgDto> nodes = new ArrayList<>();

		// 为了兼容以前的，没有设置权限就给他全部的权限
		// 不控制权限或者拥有的是全部权限，显示该租户下的
		if (StringUtils.isEmpty(user.getPermissionScope()) || PermissionScopeEnum.ALL.getKey().equals(user.getPermissionScope()) || StringUtils.isBlank(isControlPermission) || "0".equals(isControlPermission)) {

			List<TenantDeptOrgDto> list = this.findDeptOrgList(tenantId, null, beenDeletedFlags);
			if (CollectionUtils.isNotEmpty(list)) {
				nodes.addAll(list);
			}

		} else if (PermissionScopeEnum.NONE.getKey().equals(user.getPermissionScope())) {

		} else if (PermissionScopeEnum.CUSTOM.getKey().equals(user.getPermissionScope())) {
			if (ArrayUtils.isNotEmpty(customScope)) {

				List<TenantDeptOrgDto> list = cloudOrganizationDao.getDepartmentsOrOrgByIds(customScope, beenDeletedFlags);
				if (CollectionUtils.isNotEmpty(list)) {
					nodes.addAll(list);
				}
			}
		} else if (PermissionScopeEnum.SELF.getKey().equals(user.getPermissionScope())) {
			// 本级

			// 获取自己

			// 如果该人员部署于任何depart，name就返回空的树
			if (StringUtils.isNotBlank(companyId)) {

				// 返回自己属于的节点，挂在root节点下
				TenantDeptOrgDto tenantDeptOrgDto = getCompany(companyId, beenDeletedFlags);
				if (tenantDeptOrgDto != null) {
					nodes.add(tenantDeptOrgDto);
				}
			}
		} else if (PermissionScopeEnum.SELF_AND_DOWN.getKey().equals(user.getPermissionScope())) {
			// 本级及以下

			try {
				// 本身
				TenantDeptOrgDto self = getCompany(companyId, beenDeletedFlags);
				// 如果该人员部署于任何depart，name就返回空的树
				List<TenantDeptOrgDto> list = Lists.newArrayList();
				if (StringUtils.isNotBlank(companyId)) {
					list = this.findDeptOrgListByCompandyId(tenantId, companyId, beenDeletedFlags);
				}
				list.add(self);
				if (CollectionUtils.isNotEmpty(list)) {
					nodes.addAll(list);
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return nodes;
	}

	@Override
	public List<TenantDeptOrgDto> loadAllDepartmentsWithPermission(Map<String, Object> paramMap) {
		String userId = (String) paramMap.get("userId");
		String tenantId = (String) paramMap.get("tenantId");
		String isControlPermission = (String) paramMap.get("isControlPermission");
		// 获取用户
		CloudUser user = cloudUserService.findOne(userId);

		String scope = user.getCustomScope();
		// 人员信息
		CloudStaff staffDto = cloudStaffDao.findOne(user.getStaffId());
		String orgId = null;
		String departmentId = null;
		String companyId = null;
		if (staffDto != null) {
			orgId = staffDto.getOrgId();
			departmentId = staffDto.getDepartmentId();
			companyId = StringUtils.isNotBlank(orgId) ? orgId : departmentId;
		}
		// 自定义范围
		String[] customScope = StringUtils.isNotEmpty(scope) ? scope.split(",") : null;
		// 没有设置范围，直接返回
		List<TenantDeptOrgDto> nodes = new ArrayList<>();

		// 为了兼容以前的，没有设置权限就给他全部的权限
		// 不控制权限或者拥有的是全部权限，显示该租户下的
		if (StringUtils.isEmpty(user.getPermissionScope()) || PermissionScopeEnum.ALL.getKey().equals(user.getPermissionScope()) || StringUtils.isBlank(isControlPermission) || "0".equals(isControlPermission)) {

			List<TenantDeptOrgDto> list = this.findDeptOrgList(tenantId, null, null);
			if (CollectionUtils.isNotEmpty(list)) {
				nodes.addAll(list);
			}

		} else if (PermissionScopeEnum.NONE.getKey().equals(user.getPermissionScope())) {

		} else if (PermissionScopeEnum.CUSTOM.getKey().equals(user.getPermissionScope())) {
			if (ArrayUtils.isNotEmpty(customScope)) {

				List<TenantDeptOrgDto> selected = this.getDepartmentsOrOrgByIds(customScope);

				// 租户下所有的depart和org
				Map<String, TenantDeptOrgDto> allMap = Maps.newHashMap();
				List<TenantDeptOrgDto> all = this.findDeptOrgList(tenantId, null, null);
				// 利用map,去除重复
				if (CollectionUtils.isNotEmpty(all)) {
					for (TenantDeptOrgDto tenantDeptOrgDto : all) {
						allMap.put(tenantDeptOrgDto.getId(), tenantDeptOrgDto);
					}
				}

				// 用来构造树的数据，利用map去除重复
				Map<String, TenantDeptOrgDto> treeMap = Maps.newHashMap();
				for (TenantDeptOrgDto child : selected) {
					getParents(child, allMap, treeMap);
				}
				// 因为父节点中可能存在选中的节点，最后添加全选的节点，才不会被父节点中半选覆盖
				for (TenantDeptOrgDto child : selected) {
					// 添加自己
					treeMap.put(child.getId(), child);
				}
				// 树的机构数据
				List<TenantDeptOrgDto> treeList = new ArrayList<>(treeMap.values());
				if (CollectionUtils.isNotEmpty(treeList)) {
					nodes.addAll(treeList);
				}
			}
		} else if (PermissionScopeEnum.SELF.getKey().equals(user.getPermissionScope())) {
			// 本级

			// 获取自己

			// 如果该人员部署于任何depart，name就返回空的树
			if (StringUtils.isNotBlank(companyId)) {

				// 返回自己属于的节点，挂在root节点下
				TenantDeptOrgDto tenantDeptOrgDto = getCompany(companyId, null);
				if (tenantDeptOrgDto != null) {
					nodes.add(tenantDeptOrgDto);
				}
			}
		} else if (PermissionScopeEnum.SELF_AND_DOWN.getKey().equals(user.getPermissionScope())) {
			// 本级及以下

			try {
				// 本身
				TenantDeptOrgDto self = getCompany(companyId, null);
				// 如果该人员部署于任何depart，name就返回空的树
				List<TenantDeptOrgDto> list = Lists.newArrayList();
				if (StringUtils.isNotBlank(companyId)) {
					list = this.findDeptOrgListByCompandyId(tenantId, companyId, null);
				}
				list.add(self);
				if (CollectionUtils.isNotEmpty(list)) {
					nodes.addAll(list);
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return nodes;
	}

	/**
	 * 获取当前子节点的父节点，将他存到map中
	 * 
	 * @param child
	 * @param allMap
	 * @param treeMap
	 * @return
	 */
	private void getParents(TenantDeptOrgDto child, Map<String, TenantDeptOrgDto> allMap, Map<String, TenantDeptOrgDto> treeMap) {
		// 获取父节点
		TenantDeptOrgDto parent = allMap.get(child.getParentId());
		// 父节点认为是半选中状态
		parent.setFullChecked(false);
		// 将父节点放到treeMap中
		treeMap.put(parent.getId(), parent);

		// 如果父节点==-1 ，就返回，结束递归
		if (parent.getParentId().equals(DepartmentTree.ROOT_NODE_ID)) {
			return;
		}

		// 递归
		getParents(parent, allMap, treeMap);

	}

	@Override
	public Map<String, Object> getOrgIdsByPermission(String userId) {
		if (StringUtils.isEmpty(userId)) {
			return null;
		}

		String permissionScope = null;

		CloudUser user = this.cloudUserDao.findOne(userId);
		if (user == null) {
			return null;
		}
		if (StringUtils.isEmpty(user.getPermissionScope())) { // 权限字段为空的默认全部
			permissionScope = PermissionScopeEnum.ALL.getKey();
		} else {
			permissionScope = user.getPermissionScope();
		}

		Map<String, Object> rst = new HashMap<String, Object>();
		rst.put("permissionScope", permissionScope);

		// 全部或者无权限，直接返回空列表，因为全部的话返回所有ids太大，直接可以根据租户去另行做业务；否则返回具体部门id列表
		if (PermissionScopeEnum.ALL.getKey().equals(permissionScope) || PermissionScopeEnum.NONE.getKey().equals(permissionScope)) {
			rst.put("orgIds", null);
		} else {
			rst.put("orgIds", this.getCompanyIdsWithPermission(userId, null));
		}

		return rst;
	}
}
