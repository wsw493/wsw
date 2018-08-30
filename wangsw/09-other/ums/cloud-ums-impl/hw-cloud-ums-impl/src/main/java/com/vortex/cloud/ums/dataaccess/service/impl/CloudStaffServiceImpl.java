/*   
 * Copyright (C), 2005-2014, 苏州伏泰信息科技有限公司
 */
package com.vortex.cloud.ums.dataaccess.service.impl;

import java.util.ArrayList;
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
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.vortex.cloud.ums.dataaccess.dao.ICloudStaffDao;
import com.vortex.cloud.ums.dataaccess.dao.ICloudUserDao;
import com.vortex.cloud.ums.dataaccess.service.ICloudOrganizationService;
import com.vortex.cloud.ums.dataaccess.service.ICloudStaffService;
import com.vortex.cloud.ums.dataaccess.service.ITenantDivisionService;
import com.vortex.cloud.ums.dto.CloudStaffDto;
import com.vortex.cloud.ums.dto.CloudStaffPageDto;
import com.vortex.cloud.ums.dto.CloudStaffSearchDto;
import com.vortex.cloud.ums.dto.StaffDto;
import com.vortex.cloud.ums.dto.rest.CloudStaffRestDto;
import com.vortex.cloud.ums.enums.KafkaTopicEnum;
import com.vortex.cloud.ums.enums.PermissionScopeEnum;
import com.vortex.cloud.ums.enums.SyncFlagEnum;
import com.vortex.cloud.ums.model.CloudStaff;
import com.vortex.cloud.ums.model.CloudUser;
import com.vortex.cloud.ums.model.TenantDivision;
import com.vortex.cloud.ums.mq.produce.KafkaProducer;
import com.vortex.cloud.ums.util.utils.pinyin4jUtil;
import com.vortex.cloud.vfs.common.exception.ServiceException;
import com.vortex.cloud.vfs.common.lang.StringUtil;
import com.vortex.cloud.vfs.data.hibernate.repository.HibernateRepository;
import com.vortex.cloud.vfs.data.hibernate.service.SimplePagingAndSortingService;
import com.vortex.cloud.vfs.data.support.SearchFilter;
import com.vortex.cloud.vfs.data.support.SearchFilter.Operator;
import com.vortex.cloud.vfs.data.support.SearchFilters;

/**
 * @author LiShijun
 * @date 2016年4月5日 下午1:21:45
 * @Description 人员 History <author> <time> <desc>
 */
@Transactional
@Service("cloudStaffService")
public class CloudStaffServiceImpl extends SimplePagingAndSortingService<CloudStaff, String> implements ICloudStaffService {
	Logger logger = LoggerFactory.getLogger(CloudStaffServiceImpl.class);
	@Resource
	private ICloudStaffDao cloudStaffDao;

	@Resource
	private ICloudUserDao cloudUserDao;
	@Resource
	private ICloudOrganizationService cloudOrganizationService;
	@Resource
	private ITenantDivisionService tenantDivisionService;

	@Override
	public HibernateRepository<CloudStaff, String> getDaoImpl() {
		return cloudStaffDao;
	}

	@Override
	public Page<CloudStaffDto> findPageBySearchDto(Pageable pageable, CloudStaffSearchDto searchDto) {
		// 如果没有传orgId或者部门id，那么就直接返回null
		if (StringUtils.isEmpty(searchDto.getDepartmentId()) && StringUtils.isEmpty(searchDto.getOrgId())) {
			return null;
		}
		return cloudStaffDao.findPageBySearchDto(pageable, searchDto);
	}

	@Override
	public boolean isCodeExisted(String tenantId, String code) {
		if (StringUtils.isBlank(code)) {
			return false;
		}

		boolean result = false;

		List<SearchFilter> filterList = new ArrayList<>();
		filterList.add(new SearchFilter("tenantId", Operator.EQ, tenantId));
		filterList.add(new SearchFilter("code", Operator.EQ, code));
		List<CloudStaff> list = this.findListByFilter(filterList, null);

		if (CollectionUtils.isNotEmpty(list)) {
			result = true;
		}

		return result;
	}

	@Override
	public CloudStaff save(CloudStaffDto dto) throws Exception {
		this.validateOnSave(dto);

		CloudStaff entity = new CloudStaff();
		BeanUtils.copyProperties(dto, entity);

		// 设置姓名首字母
		String pinyin = pinyin4jUtil.getPinYinNoToneAndSpace(entity.getName());
		entity.setNameInitial(pinyin.trim());

		entity = cloudStaffDao.save(entity);

		KafkaProducer.getInstance().produce(KafkaTopicEnum.UMS_STAFF_SYNC.getKey(), SyncFlagEnum.ADD.getKey(), entity);
		return entity;
	}

	public void validateOnSave(CloudStaffDto dto) {

		this.validateForm(dto);

		// 逻辑业务校验
		if (this.isCodeExisted(dto.getTenantId(), dto.getCode())) {
			throw new ServiceException("编号已存在！");
		}
	}

	private void validateForm(CloudStaffDto dto) {
		if (StringUtils.isBlank(dto.getTenantId())) {
			throw new ServiceException("租户ID为空");
		}

		if (StringUtils.isBlank(dto.getDepartmentId())) {
			throw new ServiceException("单位ID为空");
		}

		if (StringUtils.isBlank(dto.getCode())) {
			throw new ServiceException("编码为空");
		}

		if (StringUtils.isBlank(dto.getName())) {
			throw new ServiceException("名称为空");
		}
	}

	@Override
	public CloudStaffDto getById(String id) {
		if (StringUtils.isEmpty(id)) {
			throw new ServiceException("id不能为空");
		}
		CloudStaffDto staff = cloudStaffDao.getById(id);
		if (staff == null) {
			throw new ServiceException("不存在id为" + id + "的人员");
		}
		if (StringUtil.isNullOrEmpty(staff.getWillCheckDivisionIds())) {
			return staff;
		}
		List<String> divisionIds = Lists.newArrayList();
		String[] division_arr = StringUtil.splitComma(staff.getWillCheckDivisionIds());
		for (String divisionId : division_arr) {
			if (StringUtil.isNullOrEmpty(divisionId)) {
				continue;
			}
			if (divisionIds.contains(divisionId)) {
				continue;
			}
			divisionIds.add(divisionId);
		}
		if (CollectionUtils.isEmpty(divisionIds)) {
			return staff;
		}
		Map<String, String> map_division = Maps.newHashMap();
		List<TenantDivision> divisionList = tenantDivisionService.findAllByIds(divisionIds.toArray(new String[divisionIds.size()]));
		if (CollectionUtils.isEmpty(divisionList)) {
			return staff;
		}
		for (TenantDivision division : divisionList) {
			map_division.put(division.getId(), division.getName());
		}
		List<String> divisionNames = Lists.newArrayList();
		for (String divisionId : division_arr) {
			if (StringUtil.isNullOrEmpty(divisionId)) {
				continue;
			}
			if (map_division.containsKey(divisionId)) {
				divisionNames.add(map_division.get(divisionId));
			}
		}
		staff.setWillCheckDivisionNames(StringUtils.join(divisionNames, ","));
		return staff;
	}

	@Override
	public void update(CloudStaffDto dto) throws Exception {
		// 入参数校验
		this.validateOnUpdate(dto);

		CloudStaff old = cloudStaffDao.findOne(dto.getId());

		BeanUtils.copyProperties(dto, old, "beenDeleted", "deletedTime", "id", "createTime", "lastChangeTime");
		// 不外包，外包公司置为空
		if (null == old.getOutSourcing() || !old.getOutSourcing()) {
			old.setOutSourcingComp("");
		}
		// 设置姓名首字母
		String pinyin = pinyin4jUtil.getPinYinNoToneAndSpace(old.getName());
		old.setNameInitial(pinyin.trim());

		cloudStaffDao.update(old);
		KafkaProducer.getInstance().produce(KafkaTopicEnum.UMS_STAFF_SYNC.getKey(), SyncFlagEnum.UPDATE.getKey(), old);
	}

	private void validateOnUpdate(CloudStaffDto dto) {

		this.validateForm(dto);

		if (StringUtils.isBlank(dto.getId())) {
			throw new ServiceException("ID为空");
		}

		// 逻辑业务校验
		if (!this.validateCodeOnUpdate(dto.getTenantId(), dto.getId(), dto.getCode())) {
			throw new ServiceException("编号已存在！");
		}
	}

	@Override
	public boolean validateCodeOnUpdate(String tenantId, String id, String newCode) {
		CloudStaff oldOrg = cloudStaffDao.findOne(id);
		String oldCode = oldOrg.getCode();

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
	public Map<String, String> getStaffNamesByIds(List<String> ids) {
		List<CloudStaff> staffs = cloudStaffDao.findAllByIds(ids.toArray(new String[ids.size()]));
		Map<String, String> nameMap = Maps.newHashMap();
		if (CollectionUtils.isNotEmpty(staffs)) {
			for (CloudStaff cloudStaff : staffs) {
				nameMap.put(cloudStaff.getId(), cloudStaff.getName());
			}
		}
		return nameMap;
	}

	@Override
	public Map<String, Object> getStaffsByIds(List<String> ids) {
		List<CloudStaff> staffs = cloudStaffDao.findAllByIds(ids.toArray(new String[ids.size()]));
		Map<String, Object> map = Maps.newHashMap();
		if (CollectionUtils.isNotEmpty(staffs)) {
			for (CloudStaff cloudStaff : staffs) {
				map.put(cloudStaff.getId(), cloudStaff);
			}
		}
		return map;
	}

	@Override
	public Map<String, Object> getStaffsByUserIds(List<String> ids) {
		List<CloudStaffDto> staffs = cloudStaffDao.getStaffsByUserIds(ids);
		Map<String, Object> map = Maps.newHashMap();
		if (CollectionUtils.isNotEmpty(staffs)) {
			for (CloudStaffDto cloudStaff : staffs) {
				map.put(cloudStaff.getUserId(), cloudStaff);
			}
		}
		return map;
	}

	@Override
	public Map<String, String> getStaffIdsByNames(List<String> names, String tenantId) {
		Map<String, String> idMap = Maps.newHashMap();
		List<CloudStaff> cloudStaffs = cloudStaffDao.getStaffIdsByNames(names, tenantId);
		for (CloudStaff cloudStaff : cloudStaffs) {
			idMap.put(cloudStaff.getName(), cloudStaff.getId());
		}
		return idMap;
	}

	@Override
	public void deleteStaffAndUser(String id) throws Exception {
		if (StringUtils.isEmpty(id)) {
			logger.error("id不能为空");
			throw new ServiceException("id不能为空");

		}
		CloudStaff cloudStaff = cloudStaffDao.findOne(id);
		if (null == cloudStaff) {
			logger.error("不存在id为" + id + "的数据");
			throw new ServiceException("不存在id为" + id + "的数据");
		}
		// 能被删除就删除
		if (canBeDeleted(id)) {
			cloudStaffDao.delete(cloudStaff);
			KafkaProducer.getInstance().produce(KafkaTopicEnum.UMS_STAFF_SYNC.getKey(), SyncFlagEnum.DELETE.getKey(), cloudStaff);
		}
		CloudUser cloudUser = cloudUserDao.getUserByStaffId(id);
		// 该人员开通了user就连同staff一起删除
		if (null != cloudUser) {
			cloudUserDao.delete(cloudUser);
			KafkaProducer.getInstance().produce(KafkaTopicEnum.UMS_USER_SYNC.getKey(), SyncFlagEnum.DELETE.getKey(), cloudUser);
		}
	}

	/**
	 * 校验是否能被删除
	 * 
	 * @param id
	 * @return
	 */
	@Override
	public boolean canBeDeleted(String id) {
		return true;
	}

	@Override
	public List<CloudStaffDto> transferModelToDto(List<CloudStaff> list) {
		List<String> ids = Lists.newArrayList();
		if (CollectionUtils.isEmpty(list)) {
			return Lists.newArrayList();
		}
		// staffIds
		for (CloudStaff staff : list) {
			ids.add(staff.getId());
		}
		// 根据staffIds获取用户名和staffId的map , {staffId:userName}
		Map<String, String> idNameMap = cloudUserDao.findUserNamesByStaffIds(ids);
		List<CloudStaffDto> dtos = Lists.newArrayList();
		// 将staff转为dto
		for (CloudStaff cloudStaff : list) {
			CloudStaffDto cloudStaffDto = new CloudStaffDto();
			BeanUtils.copyProperties(cloudStaff, cloudStaffDto);

			// 设置用户名
			cloudStaffDto.setUserName(idNameMap.get(cloudStaff.getId()));
			dtos.add(cloudStaffDto);
		}
		return dtos;
	}

	@Override
	public void deletesStaffAndUser(List<String> deleteList) throws Exception {
		if (CollectionUtils.isEmpty(deleteList)) {
			return;
		}
		// 获取要删除的staff
		List<CloudStaff> staffs = cloudStaffDao.findAllByIds(deleteList.toArray(new String[deleteList.size()]));
		// 获取要删除的staff对应的user
		List<CloudUser> users = cloudUserDao.getUsersByStaffIds(deleteList);
		// 删除user
		cloudUserDao.delete(users);
		// 删除staff
		cloudStaffDao.delete(staffs);
		for (CloudStaff cloudStaff : staffs) {
			KafkaProducer.getInstance().produce(KafkaTopicEnum.UMS_STAFF_SYNC.getKey(), SyncFlagEnum.DELETE.getKey(), cloudStaff);
		}
		for (CloudUser cloudUser : users) {
			KafkaProducer.getInstance().produce(KafkaTopicEnum.UMS_USER_SYNC.getKey(), SyncFlagEnum.DELETE.getKey(), cloudUser);
		}

	}

	@Override
	public boolean isSocialSecurityNoExist(String staffId, String socialSecurityNo) {
		if (StringUtils.isBlank(socialSecurityNo)) {
			return false;
		}

		boolean result = false;

		List<SearchFilter> filterList = new ArrayList<>();
		filterList.add(new SearchFilter("socialSecurityNo", Operator.EQ, socialSecurityNo));
		if (StringUtils.isNotBlank(staffId)) {
			filterList.add(new SearchFilter("id", Operator.NE, staffId));
		}
		List<CloudStaff> list = this.findListByFilter(filterList, null);

		if (CollectionUtils.isNotEmpty(list)) {
			result = true;
		}
		return result;

	}

	@Override
	public boolean isCredentialNumExist(String staffId, String credentialNum) {
		if (StringUtils.isBlank(credentialNum)) {
			return false;
		}

		boolean result = false;

		List<SearchFilter> filterList = new ArrayList<>();
		filterList.add(new SearchFilter("credentialNum", Operator.EQ, credentialNum));
		if (StringUtils.isNotBlank(staffId)) {
			filterList.add(new SearchFilter("id", Operator.NE, staffId));
		}
		List<CloudStaff> list = this.findListByFilter(filterList, null);

		if (CollectionUtils.isNotEmpty(list)) {
			result = true;
		}
		return result;

	}

	@Override
	public boolean isPhoneExists(String id, String phone) {
		if (StringUtils.isBlank(phone)) {
			return false;
		}

		boolean result = false;

		List<SearchFilter> filterList = new ArrayList<>();
		filterList.add(new SearchFilter("phone", Operator.EQ, phone));
		if (StringUtils.isNotBlank(id)) {
			filterList.add(new SearchFilter("id", Operator.NE, id));
		}
		List<CloudStaff> list = this.findListByFilter(filterList, null);

		if (CollectionUtils.isNotEmpty(list)) {
			result = true;
		}
		return result;
	}

	@Override
	public List<CloudStaffDto> loadStaffsByFilter(Map<String, Object> paramMap) {
		return cloudStaffDao.loadStaffsByFilter(paramMap);

	}

	@Override
	public Page<CloudStaffDto> findPageWithPermissionBySearchDto(Pageable pageable, CloudStaffSearchDto searchDto) {
		// 如果没有传orgId或者部门id，那么就直接返回null
		if (StringUtils.isEmpty(searchDto.getDepartmentId()) && StringUtils.isEmpty(searchDto.getOrgId())) {
			return null;
		}

		String userId = searchDto.getUserId();

		// 获取该用户有权限的org和department，(自定义中全选才认为有权限)
		List<String> companyIds = cloudOrganizationService.getCompanyIdsWithPermission(userId, searchDto.getTenantId());

		// 该用户下没有权限，直接返回
		if (CollectionUtils.isEmpty(companyIds)) {
			return null;
		}

		searchDto.setCompanyIds(companyIds);
		return cloudStaffDao.findPageBySearchDto(pageable, searchDto);
	}

	@Override
	public void setNameInitial() {
		SearchFilters searchFilters = new SearchFilters();
		/*
		 * searchFilters.add(new SearchFilter("nameInitial", Operator.EQ, ""));
		 * searchFilters.add(new SearchFilter("nameInitial", Operator.NULL, null));
		 */
		List<CloudStaff> staffs = cloudStaffDao.findListByFilters(searchFilters, null);

		if (CollectionUtils.isNotEmpty(staffs)) {
			for (CloudStaff cloudStaff : staffs) {
				cloudStaff.setNameInitial(pinyin4jUtil.getPinYinNoToneAndSpace(cloudStaff.getName()).toLowerCase());
			}
		}
		cloudStaffDao.update(staffs);

	}

	@Override
	public List<CloudStaffPageDto> syncStaffByPage(String tenantId, long syncTime, Integer pageSize, Integer pageNumber) {
		return cloudStaffDao.syncStaffByPage(tenantId, syncTime, pageSize, pageNumber);
	}

	@Override
	public List<CloudStaffPageDto> findAllStaffByPage(String tenantId, Integer isDeleted) {
		return cloudStaffDao.findAllStaffByPage(tenantId, isDeleted);
	}

	@Override
	public List<CloudStaffDto> findListBySearchDto(Sort defSort, CloudStaffSearchDto searchDto) {
		return cloudStaffDao.findListBySearchDto(defSort, searchDto);
	}

	@Override
	public Page<CloudStaffDto> syncStaffsByPage(Pageable pageable, Map<String, Object> paramMap) {
		return cloudStaffDao.syncStaffsByPage(pageable, paramMap);
	}

	@Override
	public List<CloudStaffDto> getStaffInfoByUserIds(List<String> ids) {
		return cloudStaffDao.getStaffInfoByUserIds(ids);
	}

	@Override
	public List<Object> getWillManStaffUser(String tenantId, String name, String willCheckDivisionId, Integer num) {
		if (StringUtil.isNullOrEmpty(tenantId)) {
			return Lists.newArrayList();
		}

		List<CloudStaffDto> list = cloudStaffDao.getWillManStaffUser(tenantId, name, willCheckDivisionId);
		if (CollectionUtils.isEmpty(list)) {
			return Lists.newArrayList();
		}
		List<Object> returnValue = Lists.newArrayList();
		List<CloudStaffDto> staffList = Lists.newArrayList();
		if (null != num && 0 != num) {// 随机抽取
			Map<Integer, String> map_temp = Maps.newHashMap();
			if (list.size() <= num) {
				staffList.addAll(list);
			} else {
				while (map_temp.size() < num) {
					int random = (int) (Math.random() * list.size());
					if (!map_temp.containsKey(random)) {
						map_temp.put(random, "");
						staffList.add(list.get(random));
					}
				}
			}
		} else {
			staffList.addAll(list);
		}
		List<String> divisionIds = Lists.newArrayList();
		for (CloudStaffDto staff : staffList) {
			if (StringUtil.isNullOrEmpty(staff.getWillCheckDivisionIds())) {
				continue;
			}
			String[] division_arr = StringUtil.splitComma(staff.getWillCheckDivisionIds());
			for (String divisionId : division_arr) {
				if (StringUtil.isNullOrEmpty(divisionId)) {
					continue;
				}
				if (divisionIds.contains(divisionId)) {
					continue;
				}
				divisionIds.add(divisionId);
			}
		}
		Map<String, String> map_division = Maps.newHashMap();
		if (CollectionUtils.isNotEmpty(divisionIds)) {
			List<TenantDivision> divisionList = tenantDivisionService.findAllByIds(divisionIds.toArray(new String[divisionIds.size()]));
			for (TenantDivision division : divisionList) {
				map_division.put(division.getId(), division.getName());
			}
		}
		Map<String, Object> mapValue = null;
		List<String> divisionNames = null;
		for (CloudStaffDto staff : staffList) {
			if (!StringUtil.isNullOrEmpty(staff.getWillCheckDivisionIds())) {
				divisionNames = Lists.newArrayList();
				String[] division_arr = StringUtil.splitComma(staff.getWillCheckDivisionIds());
				for (String divisionId : division_arr) {
					if (StringUtil.isNullOrEmpty(divisionId)) {
						continue;
					}
					if (map_division.containsKey(divisionId)) {
						divisionNames.add(map_division.get(divisionId));
					}
				}
				if (CollectionUtils.isNotEmpty(divisionNames)) {
					staff.setWillCheckDivisionNames(StringUtils.join(divisionNames, ","));
				}
			}

			mapValue = Maps.newHashMap();
			// userId
			mapValue.put("userId", staff.getUserId());
			// 用户名
			mapValue.put("userName", staff.getUserName());
			// staffId
			mapValue.put("staffId", staff.getId());
			// 姓名
			mapValue.put("staffName", staff.getName());
			// 手机号
			mapValue.put("phone", staff.getPhone());
			// 意愿检查区域
			mapValue.put("willCheckDivisionIds", staff.getWillCheckDivisionIds());
			mapValue.put("willCheckDivisionNames", staff.getWillCheckDivisionNames());
			// 工作单位
			mapValue.put("willWorkUnit", staff.getWillWorkUnit());
			// 地址
			mapValue.put("address", staff.getAddress());
			// 邮箱
			mapValue.put("email", staff.getEmail());
			returnValue.add(mapValue);
		}
		return returnValue;
	}

	@Override
	public CloudStaffRestDto getStaffByCodeAndTenantCode(String code, String tenantCode) {
		return cloudStaffDao.getStaffByCodeAndTenantCode(code, tenantCode);
	}

	@Override
	public Page<CloudStaffDto> findPageListBySearchDto(Pageable pageable, CloudStaffSearchDto searchDto) {
		return cloudStaffDao.findPageBySearchDto(pageable, searchDto);
	}

	@Override
	public List<StaffDto> listStaff(Map<String, String> conditions) throws Exception {
		if (MapUtils.isEmpty(conditions)) {
			return null;
		}

		String name = conditions.get("name");
		String phone = conditions.get("phone");
		String userId = conditions.get("userId");
		String companyId = conditions.get("companyId");
		String containManager = conditions.get("containManager");

		if (StringUtils.isNotEmpty(companyId)) { // 如果部门id不为空，直接查询该部门下的人员
			return this.cloudStaffDao.listStaff(name, phone, Lists.newArrayList(companyId), null, containManager);
		} else if (StringUtils.isNotEmpty(userId)) { // 如果部门id为空，则根据人员权限查询
			CloudUser user = this.cloudUserDao.findOne(userId);
			if (user == null) {
				return null;
			}

			// 如果人员权限字段为空或者全部，则查询租户下全部；否则查询人员权限机构列表下全部
			if (StringUtils.isEmpty(user.getPermissionScope()) || PermissionScopeEnum.ALL.getKey().equals(user.getPermissionScope())) {
				CloudStaff staff = this.cloudStaffDao.findOne(user.getStaffId());
				if (staff != null) {
					return this.cloudStaffDao.listStaff(name, phone, null, staff.getTenantId(), containManager);
				}
			} else {
				List<String> orgIds = this.cloudOrganizationService.getCompanyIdsWithPermission(userId, null);
				if (CollectionUtils.isNotEmpty(orgIds)) {
					return this.cloudStaffDao.listStaff(name, phone, orgIds, null, containManager);
				}
			}
		}

		return null;
	}
}
