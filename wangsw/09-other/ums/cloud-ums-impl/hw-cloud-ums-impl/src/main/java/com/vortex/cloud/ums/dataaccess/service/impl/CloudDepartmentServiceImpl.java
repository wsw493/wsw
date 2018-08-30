package com.vortex.cloud.ums.dataaccess.service.impl;

import java.util.ArrayList;
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
import com.vortex.cloud.ums.dataaccess.dao.ITenantDivisionDao;
import com.vortex.cloud.ums.dataaccess.service.ICloudDepartmentService;
import com.vortex.cloud.ums.dataaccess.service.ITenantParamSettingService;
import com.vortex.cloud.ums.dto.CloudDepartmentDto;
import com.vortex.cloud.ums.dto.CloudDeptOrgDto;
import com.vortex.cloud.ums.dto.IdNameDto;
import com.vortex.cloud.ums.dto.TenantDeptOrgDto;
import com.vortex.cloud.ums.dto.TenantDeptOrgPageDto;
import com.vortex.cloud.ums.dto.TreeDto;
import com.vortex.cloud.ums.enums.KafkaTopicEnum;
import com.vortex.cloud.ums.enums.SyncFlagEnum;
import com.vortex.cloud.ums.model.CloudDepartment;
import com.vortex.cloud.ums.model.CloudOrganization;
import com.vortex.cloud.ums.model.TenantDivision;
import com.vortex.cloud.ums.model.TenantPramSetting;
import com.vortex.cloud.ums.mq.produce.KafkaProducer;
import com.vortex.cloud.ums.support.ManagementConstant;
import com.vortex.cloud.vfs.common.exception.ServiceException;
import com.vortex.cloud.vfs.common.exception.VortexException;
import com.vortex.cloud.vfs.data.hibernate.repository.HibernateRepository;
import com.vortex.cloud.vfs.data.hibernate.service.SimplePagingAndSortingService;
import com.vortex.cloud.vfs.data.support.SearchFilter;
import com.vortex.cloud.vfs.data.support.SearchFilter.Operator;

@Transactional
@Service("cloudDepartmentService")
public class CloudDepartmentServiceImpl extends SimplePagingAndSortingService<CloudDepartment, String> implements ICloudDepartmentService {

	private static final Logger logger = LoggerFactory.getLogger(CloudDepartmentServiceImpl.class);

	@Resource
	private ICloudDepartmentDao cloudDepartmentDao;

	@Resource
	private ICloudOrganizationDao cloudOrganizationDao;
	@Resource
	private ITenantParamSettingService tenantParamSettingService;

	@Resource
	private ITenantDivisionDao tenantDivisionDao;

	@Override
	public HibernateRepository<CloudDepartment, String> getDaoImpl() {
		return cloudDepartmentDao;
	}

	@Override
	public CloudDepartment save(CloudDepartmentDto dto) {
		// 入参数校验
		this.validateOnSave(dto);

		Double longitude = dto.getLongitude();
		Double latitude = dto.getLatitude();
		if (longitude != null && latitude != null) {
			dto.setLngLats(longitude + "," + latitude);
		}

		CloudDepartment department = new CloudDepartment();
		BeanUtils.copyProperties(dto, department);
		department = cloudDepartmentDao.save(department);
		try {
			KafkaProducer.getInstance().produce(KafkaTopicEnum.UMS_DEPARTMENT_SYNC.getKey(), SyncFlagEnum.ADD.getKey(), department);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return department;
	}

	private void validateOnSave(CloudDepartmentDto dto) {

		this.validateForm(dto);

		// 逻辑业务校验
		if (this.isCodeExisted(null, dto.getDepCode())) {
			throw new ServiceException("编号已存在！");
		}
	}

	private void validateForm(CloudDepartmentDto newDto) {
		if (StringUtils.isBlank(newDto.getTenantId())) {
			throw new ServiceException("租户ID为空");
		}

		if (StringUtils.isBlank(newDto.getDepCode())) {
			throw new ServiceException("编码为空");
		}

		if (StringUtils.isBlank(newDto.getDepName())) {
			throw new ServiceException("名称为空");
		}

		if (StringUtils.isBlank(newDto.getDepType())) {
			throw new ServiceException("类型为空");
		}
	}

	@Override
	public boolean isCodeExisted(String tenantId, String code) {
		if (StringUtils.isBlank(code)) {
			return false;
		}

		boolean result = false;

		List<SearchFilter> filterList = new ArrayList<SearchFilter>();
		filterList.add(new SearchFilter("tenantId", Operator.EQ, tenantId));
		filterList.add(new SearchFilter("depCode", Operator.EQ, code));
		List<CloudDepartment> list = this.findListByFilter(filterList, null);

		if (CollectionUtils.isNotEmpty(list)) {
			result = true;
		}

		return result;
	}

	@Override
	public CloudDepartmentDto getById(String id) {
		CloudDepartment department = cloudDepartmentDao.findOne(id);

		if (null == department) {
			return null;
		}

		CloudDepartmentDto dto = new CloudDepartmentDto();
		BeanUtils.copyProperties(department, dto);
		this.setDeptTypeText(dto);
		this.setLngLat(dto);

		// 处理行政区划名称
		if (StringUtils.isNotEmpty(dto.getDivisionId())) {
			TenantDivision td = tenantDivisionDao.findOne(dto.getDivisionId());
			if (td != null) {
				dto.setDivisionName(td.getName());
			}
		}

		return dto;
	}

	/**
	 * 设置部门类型的描述文本
	 * 
	 * @param dto
	 */
	private void setDeptTypeText(CloudDepartmentDto dto) {

		TenantPramSetting tenantPramSetting = tenantParamSettingService.findOneByParamCode(dto.getTenantId(), ManagementConstant.getPropertyValue("DEPARTMENT_TYPE"), dto.getDepType());
		if (null == tenantPramSetting) {
			logger.error("在参数表中未找到相关的参数值,请配置");
			throw new ServiceException("在参数表中未找到相关的参数值,请配置");
		}
		dto.setDepTypeText(tenantPramSetting.getParmName());
	}

	/**
	 * 分离出经纬度
	 * 
	 * @param dto
	 */
	private void setLngLat(CloudDepartmentDto dto) {
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
	public void update(CloudDepartmentDto newDto) {

		// 入参数校验
		this.validateOnUpdate(newDto);

		Double longitude = newDto.getLongitude();
		Double latitude = newDto.getLatitude();
		if (longitude != null && latitude != null) {
			newDto.setLngLats(longitude + "," + latitude);
		}

		CloudDepartment oldDept = cloudDepartmentDao.findOne(newDto.getId());
		oldDept.setAddress(newDto.getAddress());
		oldDept.setDepCode(newDto.getDepCode());
		oldDept.setDepName(newDto.getDepName());
		oldDept.setDepType(newDto.getDepType());
		oldDept.setDescription(newDto.getDescription());
		oldDept.setEmail(newDto.getEmail());
		oldDept.setHead(newDto.getHead());
		oldDept.setHeadMobile(newDto.getHeadMobile());
		oldDept.setLngLats(newDto.getLngLats());
		oldDept.setOrderIndex(newDto.getOrderIndex());
		oldDept.setDivisionId(newDto.getDivisionId());
		cloudDepartmentDao.update(oldDept);
		try {
			KafkaProducer.getInstance().produce(KafkaTopicEnum.UMS_DEPARTMENT_SYNC.getKey(), SyncFlagEnum.UPDATE.getKey(), oldDept);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void validateOnUpdate(CloudDepartmentDto newDto) {

		this.validateForm(newDto);

		// 逻辑业务校验
		if (!this.validateCodeOnUpdate(null, newDto.getId(), newDto.getDepCode())) {
			throw new ServiceException("编号已存在！");
		}
	}

	@Override
	public boolean validateCodeOnUpdate(String tenantId, String id, String newDepCode) {
		boolean flag = true;
		List<SearchFilter> searchFilters = Lists.newArrayList();
		searchFilters.add(new SearchFilter("tenantId", Operator.EQ, tenantId));
		searchFilters.add(new SearchFilter("id", Operator.NE, id));
		searchFilters.add(new SearchFilter("depCode", Operator.EQ, newDepCode));
		List<CloudDepartment> list = cloudDepartmentDao.findListByFilter(searchFilters, null);
		if (CollectionUtils.isNotEmpty(list)) {
			flag = !(list.size() > 0);
		}
		return flag;
	}

	@Override
	public List<TenantDeptOrgDto> findDeptOrgList(String tenantId, String deptId, List<Integer> beenDeletedFlags) {
		return cloudDepartmentDao.findDeptOrgList(tenantId, deptId, beenDeletedFlags);
	}

	@Override
	public CloudDepartment getDepartmentByCode(String departmentCode, String tenantId) {
		CloudDepartment department = cloudDepartmentDao.getDepartmentByCode(departmentCode, tenantId);
		if (department == null) {
			logger.error("getDepartmentByCode(),未能根据租户code和部门code获取到部门记录");
			throw new ServiceException("未能根据租户code和部门code获取到部门记录");
		}

		return department;
	}

	@Override
	public List<Map<String, Object>> findDepartmentByIds(List<String> idsList) {
		List<CloudDepartment> list = cloudDepartmentDao.findAllByIds(idsList.toArray(new String[0]));
		List<Map<String, Object>> maps = Lists.newArrayList();
		Map<String, Object> dataMap;
		if (CollectionUtils.isNotEmpty(list)) {
			for (CloudDepartment cloudDepartment : list) {
				dataMap = Maps.newHashMap();
				dataMap.put("departmentId", cloudDepartment.getId());
				dataMap.put("departmentName", cloudDepartment.getDepName());
				maps.add(dataMap);
			}
		}
		return maps;
	}

	@Override
	public List<TenantDeptOrgDto> findDeptList(String tenantId, String deptId) {
		return cloudDepartmentDao.findDeptList(tenantId, deptId);
	}

	@Override
	public void deleteDepartment(String departmentId) {
		if (StringUtils.isEmpty(departmentId)) {
			logger.error("删除部门时未传入部门id！");
			throw new VortexException("删除部门时未传入部门id！");
		}

		CloudDepartment dept = cloudDepartmentDao.findOne(departmentId);
		if (dept == null) {
			logger.error("根据id[" + departmentId + "]未找到部门信息！");
			throw new VortexException("根据id[" + departmentId + "]未找到部门信息！");
		}

		if (cloudDepartmentDao.hasStaff(departmentId)) {
			logger.error("该部门下存在有效的人员，无法删除！");
			throw new VortexException("该部门下存在有效的人员，无法删除！");
		}

		if (cloudDepartmentDao.hasOrg(departmentId)) {
			logger.error("该部门下存在有效的机构，无法删除！");
			throw new VortexException("该部门下存在有效的机构，无法删除！");
		}

		cloudDepartmentDao.delete(departmentId);
		try {
			KafkaProducer.getInstance().produce(KafkaTopicEnum.UMS_DEPARTMENT_SYNC.getKey(), SyncFlagEnum.DELETE.getKey(), dept);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public List<TenantDeptOrgDto> findDeptOrgListByCompandyId(String tenantId, String companyId, List<Integer> beenDeletedFlags) {

		List<TenantDeptOrgDto> tenantDeptOrgDtos = null;
		if (StringUtils.isBlank(companyId)) // 部门为空
		{
			tenantDeptOrgDtos = this.findDeptOrgList(tenantId, companyId, beenDeletedFlags);

		} else {
			CloudOrganization cloudOrganization = cloudOrganizationDao.findOne(companyId);

			if (cloudOrganization != null) { // 传过来的是orgid
				String departId = cloudOrganization.getDepartmentId();
				String nodeCode = cloudOrganization.getNodeCode();
				String id = cloudOrganization.getId();
				tenantDeptOrgDtos = cloudOrganizationDao.findOrganizationChild(departId, nodeCode, id, beenDeletedFlags);

			} else {// 传过来是departid
				tenantDeptOrgDtos = this.findDeptOrgList(tenantId, companyId, beenDeletedFlags);
			}
		}

		return tenantDeptOrgDtos;
	}

	@Override
	public List<TenantDeptOrgPageDto> syncDeptByPage(String tenantId, long syncTime, Integer pageSize, Integer pageNumber) {
		return cloudDepartmentDao.syncDeptByPage(tenantId, syncTime, pageSize, pageNumber);
	}

	@Override
	public List<IdNameDto> findChildren(String tenantId, String id) throws Exception {
		return this.cloudDepartmentDao.findChildren(tenantId, id);
	}

	@Override
	public List<TreeDto> listDetpByParentId(String tenantId, String parentId) throws Exception {
		if ((StringUtils.isEmpty(parentId) || parentId.equals("-1")) && StringUtils.isEmpty(tenantId)) {
			logger.error("查询子机构列表时，传入的参数为空");
			throw new VortexException("查询子机构列表时，传入的参数为空");
		}

		if (StringUtils.isEmpty(parentId) || parentId.equals("-1")) {
			return this.cloudDepartmentDao.listByTenantId(tenantId); // 如果父节点是空或者-1，直接返回租户下所有结构的列表
		} else {
			CloudDepartment dept = this.cloudDepartmentDao.findOne(parentId);
			if (dept != null) {
				return this.cloudOrganizationDao.listOrgByDeptId(parentId); // 如果id是部门表id，则直接返回该部门下的所有机构
			} else {
				return this.cloudOrganizationDao.listOrgByParentId(parentId); // 如果id是机构表id，则返回此机构下的所有子机构
			}
		}
	}

	@Override
	public List<CloudDeptOrgDto> listByIds(List<String> ids) throws Exception {
		if (CollectionUtils.isEmpty(ids)) {
			return null;
		}

		// 先匹配org表，如果数量一致，直接返回
		List<CloudDeptOrgDto> rst = this.cloudOrganizationDao.listByIds(ids);
		if (CollectionUtils.isNotEmpty(rst) && rst.size() == ids.size()) {
			return rst;
		}

		// 再匹配dept表
		List<CloudDeptOrgDto> deptList = this.cloudDepartmentDao.listByIds(ids);
		if (CollectionUtils.isEmpty(rst)) {
			return deptList;
		} else {
			rst.addAll(deptList);
			return rst;
		}
	}
}
