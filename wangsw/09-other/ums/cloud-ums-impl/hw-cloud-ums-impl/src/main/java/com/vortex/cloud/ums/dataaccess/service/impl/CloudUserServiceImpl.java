/*   
 * Copyright (C), 2005-2014, 苏州伏泰信息科技有限公司
 */
package com.vortex.cloud.ums.dataaccess.service.impl;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.vortex.cloud.ums.dataaccess.dao.ICloudDepartmentDao;
import com.vortex.cloud.ums.dataaccess.dao.ICloudOrganizationDao;
import com.vortex.cloud.ums.dataaccess.dao.ICloudStaffDao;
import com.vortex.cloud.ums.dataaccess.dao.ICloudUserDao;
import com.vortex.cloud.ums.dataaccess.dao.ITenantDivisionDao;
import com.vortex.cloud.ums.dataaccess.service.ICloudUserRoleService;
import com.vortex.cloud.ums.dataaccess.service.ICloudUserService;
import com.vortex.cloud.ums.dataaccess.service.IFileService;
import com.vortex.cloud.ums.dto.CloudStaffDto;
import com.vortex.cloud.ums.dto.CloudStaffSearchDto;
import com.vortex.cloud.ums.dto.CloudUserDto;
import com.vortex.cloud.ums.dto.FileInfo;
import com.vortex.cloud.ums.dto.IdNameDto;
import com.vortex.cloud.ums.dto.UserDeptDto;
import com.vortex.cloud.ums.dto.UserDto;
import com.vortex.cloud.ums.dto.rest.CloudUserRestDto;
import com.vortex.cloud.ums.enums.KafkaTopicEnum;
import com.vortex.cloud.ums.enums.SyncFlagEnum;
import com.vortex.cloud.ums.model.CloudDepartment;
import com.vortex.cloud.ums.model.CloudOrganization;
import com.vortex.cloud.ums.model.CloudStaff;
import com.vortex.cloud.ums.model.CloudUser;
import com.vortex.cloud.ums.model.TenantDivision;
import com.vortex.cloud.ums.mq.produce.KafkaProducer;
import com.vortex.cloud.ums.support.ManagementConstant;
import com.vortex.cloud.vfs.common.digest.MD5;
import com.vortex.cloud.vfs.common.exception.ServiceException;
import com.vortex.cloud.vfs.common.exception.VortexException;
import com.vortex.cloud.vfs.common.mapper.JsonMapper;
import com.vortex.cloud.vfs.common.web.springmvc.SpringmvcUtils;
import com.vortex.cloud.vfs.data.dto.RestResultDto;
import com.vortex.cloud.vfs.data.hibernate.repository.HibernateRepository;
import com.vortex.cloud.vfs.data.hibernate.service.SimplePagingAndSortingService;
import com.vortex.cloud.vfs.data.support.SearchFilter;
import com.vortex.cloud.vfs.data.support.SearchFilter.Operator;

/**
 * @author LiShijun
 * @date 2016年4月6日 上午11:50:41
 * @Description 用户管理 History <author> <time> <desc>
 */
@Service("cloudUserService")
@Transactional
public class CloudUserServiceImpl extends SimplePagingAndSortingService<CloudUser, String> implements ICloudUserService {
	private Logger logger = LoggerFactory.getLogger(CloudUserServiceImpl.class);

	@Resource
	private ICloudUserDao cloudUserDao;

	@Resource
	private ICloudStaffDao cloudStaffDao;

	@Resource
	private ICloudUserRoleService cloudUserRoleService;

	@Resource
	private IFileService fileService;

	@Resource
	private ICloudUserService cloudUserService;

	@Resource
	private ICloudOrganizationDao cloudOrganizationDao;

	@Resource
	private ICloudDepartmentDao cloudDepartmentDao;

	@Resource
	private ITenantDivisionDao tenantDivisionDao;

	@Override
	public HibernateRepository<CloudUser, String> getDaoImpl() {
		return cloudUserDao;
	}

	@Override
	public boolean isNameExisted(String name) {
		if (StringUtils.isEmpty(name)) {
			logger.error("isNameExisted(), name is emtpy");
			return false;
		}

		boolean result = false;

		// 在用户名中是否重复
		List<SearchFilter> filterList = new ArrayList<>();
		filterList.add(new SearchFilter("userName", Operator.EQ, name));
		List<CloudUser> list = this.findListByFilter(filterList, null);

		// 在手机号中是否重复
		List<SearchFilter> filterList2 = new ArrayList<>();
		filterList2.add(new SearchFilter("phone", Operator.EQ, name));
		List<CloudStaff> staffs = cloudStaffDao.findListByFilter(filterList2, null);

		if (CollectionUtils.isNotEmpty(list) || CollectionUtils.isNotEmpty(staffs)) {
			result = true;
		}

		return result;
	}

	@Override
	public CloudUser save(CloudUserDto dto) {
		this.validateOnSave(dto);

		CloudUser entity = new CloudUser();
		BeanUtils.copyProperties(dto, entity);

		// 对密码进行MD5算法加密
		entity.setPassword(MD5.getMD5(entity.getPassword()));
		entity = cloudUserDao.save(entity);

		dto.setId(entity.getId());
		try {
			KafkaProducer.getInstance().produce(KafkaTopicEnum.UMS_USER_SYNC.getKey(), SyncFlagEnum.ADD.getKey(), entity);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return entity;
	}

	public void validateOnSave(CloudUserDto dto) {

		this.validateForm(dto);

		// 逻辑业务校验
		if (this.isNameExisted(dto.getUserName())) {
			throw new ServiceException("用户名称已存在！");
		}
	}

	private void validateForm(CloudUserDto dto) {
		if (StringUtils.isBlank(dto.getStaffId())) {
			throw new ServiceException("人员ID为空");
		}

		if (StringUtils.isBlank(dto.getUserName())) {
			throw new ServiceException("用户名为空");
		}

		if (StringUtils.isBlank(dto.getPassword())) {
			throw new ServiceException("密码为空");
		}
	}

	@Override
	public CloudUserDto getById(String id) {
		CloudUser entity = cloudUserDao.findOne(id);

		CloudUserDto dto = new CloudUserDto();
		BeanUtils.copyProperties(entity, dto);
		CloudStaffDto staffDto = cloudStaffDao.getById(dto.getStaffId());
		if (staffDto != null) {
			dto.setStaffDto(staffDto);
		}

		return dto;
	}

	@Override
	public void update(CloudUserDto dto) {
		// 入参数校验
		this.validateOnUpdate(dto);

		CloudUser old = cloudUserDao.findOne(dto.getId());

		BeanUtils.copyProperties(dto, old);

		cloudUserDao.update(old);

		try {
			KafkaProducer.getInstance().produce(KafkaTopicEnum.UMS_USER_SYNC.getKey(), SyncFlagEnum.UPDATE.getKey(), old);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void validateOnUpdate(CloudUserDto dto) {

		this.validateForm(dto);

		if (StringUtils.isBlank(dto.getId())) {
			throw new ServiceException("ID为空");
		}

		// 逻辑业务校验
		if (!this.validateNameOnUpdate(dto.getId(), dto.getUserName())) {
			throw new ServiceException("名称已存在！");
		}
	}

	@Override
	public boolean validateNameOnUpdate(String id, String newName) {
		List<SearchFilter> searchFilters = Lists.newArrayList();
		if (StringUtils.isNotBlank(id)) {
			searchFilters.add(new SearchFilter("id", Operator.NE, id));
		}
		if (StringUtils.isNotBlank(newName)) {
			searchFilters.add(new SearchFilter("userName", Operator.EQ, newName));
		}
		List<CloudUser> users = cloudUserDao.findListByFilter(searchFilters, null);

		// 在手机号中是否重复
		List<SearchFilter> filterList2 = new ArrayList<>();
		filterList2.add(new SearchFilter("phone", Operator.EQ, newName));
		List<CloudStaff> staffs = cloudStaffDao.findListByFilter(filterList2, null);

		// 与用户名重复或者是与人员中的电话号码重复
		if (CollectionUtils.isNotEmpty(users) || CollectionUtils.isNotEmpty(staffs)) {
			return false;
		} else {
			return true;
		}

	}

	@Override
	public void changePassword(String userId, String oldPwd, String newPwd) throws Exception {
		if (StringUtils.isEmpty(userId) || StringUtils.isEmpty(oldPwd) || StringUtils.isEmpty(newPwd)) {
			logger.error("修改密码时传入的参数不全！");
			throw new VortexException("修改密码时传入的参数不全！");
		}

		CloudUser user = cloudUserDao.findOne(userId);
		if (user == null) {
			logger.error("根据用户id[" + userId + "]未找到用户信息！");
			throw new VortexException("根据用户id[" + userId + "]未找到用户信息！");
		}

		if (!user.getPassword().equals(MD5.getMD5(oldPwd))) {
			logger.error("旧密码不正确！");
			throw new VortexException("旧密码不正确！");
		}

		user.setPassword(MD5.getMD5(newPwd));

		cloudUserDao.update(user);
		try {
			KafkaProducer.getInstance().produce(KafkaTopicEnum.UMS_USER_SYNC.getKey(), SyncFlagEnum.UPDATE.getKey(), user);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public CloudUserRestDto getUserByUserNameAndTenantCode(String userName, String tenantCode) throws Exception {
		if (StringUtils.isEmpty(userName) || StringUtils.isEmpty(tenantCode)) {
			logger.error("请传入用户名和租户code！");
			throw new VortexException("请传入用户名和租户code！");
		}

		CloudUserRestDto rst = cloudUserDao.getUserByUserNameAndTenantCode(userName, tenantCode);
		if (rst != null && StringUtils.isNotEmpty(rst.getId())) {
			rst.setFunctionList(cloudUserDao.getFunctionsByUserId(rst.getId()));
		}

		return rst;
	}

	@Override
	public String uploadPhoto(String userId, String fileName, String imgStr) throws Exception {
		if (StringUtils.isEmpty(userId)) {
			logger.error("请传入用户id");
			throw new VortexException("请传入用户id");
		}
		if (StringUtils.isEmpty(imgStr)) {
			logger.error("上传文件不能为空");
			throw new VortexException("上传文件不能为空");
		}
		// 上传文件
		FileInfo file = fileService.upload(imgStr, fileName);
		// 保存photoId
		CloudUser cloudUser = cloudUserDao.findOne(userId);
		if (null == cloudUser) {
			logger.error("不存在id为" + userId + "的用户");
			throw new VortexException("不存在id为" + userId + "的用户");
		}
		cloudUser.setPhotoId(file.getId());
		cloudUserDao.update(cloudUser);
		try {
			KafkaProducer.getInstance().produce(KafkaTopicEnum.UMS_USER_SYNC.getKey(), SyncFlagEnum.UPDATE.getKey(), cloudUser);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return file.getId();
	}

	@Override
	public List<CloudUserDto> getUsersByCondiction(Map<String, String> paramMap) {
		if (MapUtils.isEmpty(paramMap)) {
			logger.error("参数不能为空");
			throw new VortexException("参数不能为空");
		}
		List<CloudUserDto> users = cloudUserDao.getUsersByCondiction(paramMap);
		return users;
	}

	@Override
	public List<CloudUserDto> findListByCompanyIds(List<String> companyIds) {
		if (CollectionUtils.isEmpty(companyIds)) {
			return null;
		}
		return cloudUserDao.findListByCompanyIds(companyIds);
	}

	@Override
	public void updateRongLianAccount(String userId, String rongLianAccount) throws Exception {
		if (StringUtils.isEmpty(userId)) {
			logger.error("用户ID不能为空");
			throw new VortexException("用户ID不能为空");
		}
		CloudUser cloudUser = cloudUserDao.findOne(userId);
		if (null == cloudUser) {
			logger.error("id为" + userId + "的人员不存在");
			throw new VortexException("id为" + userId + "的人员不存在");
		}
		cloudUser.setRongLianAccount(rongLianAccount);

		cloudUserDao.update(cloudUser);
		try {
			KafkaProducer.getInstance().produce(KafkaTopicEnum.UMS_USER_SYNC.getKey(), SyncFlagEnum.UPDATE.getKey(), cloudUser);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void updateImToken(String userId, String userName, String imToken) {
		if (StringUtils.isEmpty(userId) && StringUtils.isEmpty(userName)) {
			logger.error("更新融云token时，传入的参数不足！");
			throw new VortexException("更新融云token时，传入的参数不足！");
		}
		CloudUser user = cloudUserDao.getUserByIdAndName(userId, userName);

		if (user == null) {
			logger.error("未找到用户信息！");
			throw new VortexException("未找到用户信息！");
		}

		user.setImToken(imToken);
		cloudUserDao.update(user);
		try {
			KafkaProducer.getInstance().produce(KafkaTopicEnum.UMS_USER_SYNC.getKey(), SyncFlagEnum.UPDATE.getKey(), user);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public CloudUser getUserByStaffId(String staffId) {
		return cloudUserDao.getUserByStaffId(staffId);
	}

	@Override
	public UserDto getUserById(String userId) {
		CloudUser entity = cloudUserDao.findOne(userId);

		UserDto dto = new UserDto();
		BeanUtils.copyProperties(entity, dto);

		CloudStaffDto staffDto = cloudStaffDao.getById(dto.getStaffId());

		if (staffDto == null) {
			throw new VortexException("未能根据人员ID获取到人员记录");
		}
		dto.setStaffName(staffDto.getName());
		return dto;
	}

	@Override
	public void resetPassword(String userId) {
		CloudUser entity = cloudUserDao.findOne(userId);
		entity.setPassword(MD5.getMD5("123456"));
		cloudUserDao.save(entity);

	}

	@Override
	public boolean isNameExisted(String name, String staffId) {
		if (StringUtils.isEmpty(name)) {
			logger.error("isNameExisted(), name is emtpy");
			return false;
		}

		boolean result = false;

		// 在用户名中是否重复
		List<SearchFilter> filterList = new ArrayList<>();
		filterList.add(new SearchFilter("userName", Operator.EQ, name));
		List<CloudUser> list = this.findListByFilter(filterList, null);

		// 在手机号中是否重复
		List<SearchFilter> filterList2 = new ArrayList<>();
		filterList2.add(new SearchFilter("phone", Operator.EQ, name));
		if (StringUtils.isNotBlank(staffId)) {
			filterList2.add(new SearchFilter("id", Operator.NE, staffId));
		}
		List<CloudStaff> staffs = cloudStaffDao.findListByFilter(filterList2, null);

		if (CollectionUtils.isNotEmpty(list) || CollectionUtils.isNotEmpty(staffs)) {
			result = true;
		}

		return result;
	}

	/**
	 * 重置密码
	 * 
	 * @param dto
	 * @return
	 */
	@RequestMapping(value = "resetPassword", method = RequestMethod.POST)
	public RestResultDto add(HttpServletRequest request) {
		String msg = null;
		Integer result = ManagementConstant.REST_RESULT_SUCC;
		Object data = null;
		String exception = null;
		RestResultDto restResultDto = new RestResultDto();

		try {
			Map<String, String> paramMap = new JsonMapper().fromJson(SpringmvcUtils.getParameter(ManagementConstant.REST_PMS), Map.class);
			String userId = paramMap.get("userId");
			cloudUserService.resetPassword(userId);
			msg = "重置密码成功,新密码为123456,请联系用户尽快修改密码";
		} catch (Exception e) {
			e.printStackTrace();
			result = ManagementConstant.REST_RESULT_FAIL;
			msg = "重置失败！";
			exception = e.getMessage();
			data = null;
			logger.error(msg, e);
		} finally {
			restResultDto.setResult(result);
			restResultDto.setMsg(msg);
			restResultDto.setData(data);
			restResultDto.setException(exception);
		}

		return restResultDto;
	}

	@Override
	public UserDeptDto getDeptInfo(String userId) {
		CloudStaff staff = this.cloudStaffDao.getStaffByUserId(userId);
		if (StringUtils.isEmpty(staff.getDepartmentId()) && StringUtils.isEmpty(staff.getOrgId())) {
			logger.error("此人不属于任何部门机构");
			throw new VortexException("此人不属于任何部门机构");
		}

		CloudOrganization org = null;
		if (StringUtils.isNotEmpty(staff.getOrgId())) {
			org = cloudOrganizationDao.findOne(staff.getOrgId());
		}

		CloudDepartment dpt = null;
		if (org == null) {
			dpt = cloudDepartmentDao.findOne(staff.getDepartmentId());
		}

		TenantDivision td = null;

		UserDeptDto rst = new UserDeptDto();
		if (org != null) {
			String pname = null;
			CloudOrganization porg = cloudOrganizationDao.findOne(org.getParentId());
			if (porg != null) {
				pname = porg.getOrgName();
			} else {
				CloudDepartment pdpt = cloudDepartmentDao.findOne(staff.getDepartmentId());
				if (pdpt != null) {
					pname = pdpt.getDepName();
				}
			}

			if (StringUtils.isNotEmpty(org.getDivisionId())) {
				td = tenantDivisionDao.findOne(org.getDivisionId());
			}

			rst.setId(org.getId());
			rst.setTenantId(org.getTenantId()); // 租户id
			rst.setHead(org.getHead()); // 负责人
			rst.setHeadMobile(org.getHeadMobile()); // 负责人电话
			rst.setDescription(org.getDescription()); // 描述
			rst.setLngLats(org.getLngLats()); // 经纬度
			rst.setAddress(org.getAddress()); // 地址
			rst.setEmail(org.getEmail()); // 邮箱
			rst.setName(org.getOrgName()); // 名称
			rst.setCode(org.getOrgCode()); // 编码
			rst.setParentId(org.getParentId()); // 父节点id
			rst.setParentName(pname); // 父节点名称
			rst.setFlag("2"); // 1- dept,2-org
			rst.setDivisionId(org.getDivisionId()); // 行政区划id
			rst.setDivisionName(td == null ? null : td.getName()); // 行政区划名称
			rst.setOrderIndex(org.getOrderIndex()); // 排序号
		} else if (dpt != null) {
			if (StringUtils.isNotEmpty(dpt.getDivisionId())) {
				td = tenantDivisionDao.findOne(dpt.getDivisionId());
			}

			rst.setId(dpt.getId());
			rst.setTenantId(dpt.getTenantId()); // 租户id
			rst.setHead(dpt.getHead()); // 负责人
			rst.setHeadMobile(dpt.getHeadMobile()); // 负责人电话
			rst.setDescription(dpt.getDescription()); // 描述
			rst.setLngLats(dpt.getLngLats()); // 经纬度
			rst.setAddress(dpt.getAddress()); // 地址
			rst.setEmail(dpt.getEmail()); // 邮箱
			rst.setName(dpt.getDepName()); // 名称
			rst.setCode(dpt.getDepCode()); // 编码
			rst.setParentId(null); // 父节点id
			rst.setParentName(null); // 父节点名称
			rst.setFlag("1"); // 1- dept,2-org
			rst.setDivisionId(dpt.getDivisionId()); // 行政区划id
			rst.setDivisionName(td == null ? null : td.getName()); // 行政区划名称
			rst.setOrderIndex(dpt.getOrderIndex()); // 排序号
		}

		return rst;
	}

	@Override
	public LinkedHashMap<String, String> getUserNamesByIds(List<String> ids) throws Exception {
		List<IdNameDto> list = this.cloudUserDao.getUserNamesByIds(ids);
		if (CollectionUtils.isEmpty(list)) {
			return null;
		}

		// 转化成键值对
		LinkedHashMap<String, String> rst = Maps.newLinkedHashMap();
		for (int i = 0; i < list.size(); i++) {
			rst.put(list.get(i).getId(), list.get(i).getName());
		}

		return rst;
	}

	@Override
	public Page<CloudUserDto> findPageListBySearchDto(Pageable pageable, CloudStaffSearchDto searchDto) {
		return	cloudUserDao.findPageListBySearchDto(pageable,searchDto);
	}
}
