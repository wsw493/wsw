package com.vortex.cloud.ums.dataaccess.service.impl;

import java.util.Arrays;
import java.util.Date;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.converters.DateConverter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;
import com.vortex.cloud.ums.dataaccess.service.ICloudOrganizationService;
import com.vortex.cloud.ums.dataaccess.service.ICloudStaffService;
import com.vortex.cloud.ums.dataaccess.service.ICloudStaffTempService;
import com.vortex.cloud.ums.dataaccess.service.ITenantParamSettingService;
import com.vortex.cloud.ums.dataaccess.service.IUploadResultInfoService;
import com.vortex.cloud.ums.model.CloudDepartment;
import com.vortex.cloud.ums.model.CloudOrganization;
import com.vortex.cloud.ums.model.CloudStaff;
import com.vortex.cloud.ums.model.TenantPramSetting;
import com.vortex.cloud.ums.model.upload.CloudStaffTemp;
import com.vortex.cloud.ums.model.upload.UploadResultInfo;
import com.vortex.cloud.ums.util.CommonUtils;
import com.vortex.cloud.ums.util.PropertyUtils;
import com.vortex.cloud.ums.util.utils.pinyin4jUtil;
import com.vortex.cloud.vfs.common.lang.DateUtil;
import com.vortex.cloud.vfs.common.lang.StringUtil;

@Transactional
@Service("cloudStaffTempService")
public class CloudStaffTempServiceImpl implements ICloudStaffTempService {

	@Resource
	private ICloudOrganizationService cloudOrganizationService;
	@Resource
	private IUploadResultInfoService uploadResultInfoService;
	@Resource
	private ICloudStaffService cloudStaffService;
	@Resource
	private ITenantParamSettingService tenantParamSettingService;

	// 人员编码最大长度
	private static final int STAFF_CODE_MAX_LENGTH = 64;
	// 人员姓名最大长度
	private static final int STAFF_NAME_MAX_LENGTH = 20;
	// 人员身份证最大长度
	private static final int STAFF_CREDENTIAL_NUM_MAX_LENGTH = 32;
	// 人员社保号最大长度
	private static final int STAFF_SOCIAL_SECURITY_NO_MAX_LENGTH = 255;

	@Override
	public Map<String, Object> importData(CloudStaffTemp temp, String marks, int row) throws Exception {
		String tenantId = CommonUtils.getTenantId();
		Map<String, Object> mapResult = Maps.newHashMap();
		boolean succFlag = true;

		StringBuffer message = new StringBuffer();
		CloudStaff model = new CloudStaff();
		model.setTenantId(tenantId);

		// 验证机构
		if (StringUtil.isNullOrEmpty(temp.getOrgName())) {
			message.append("所属机构不能为空；");
			succFlag = false;
		} else {
			Map<String, Object> nameObjMap = cloudOrganizationService.getDepartmentsOrOrgByName(Arrays.asList(new String[] { temp.getOrgName() }), tenantId);
			if (null != nameObjMap.get(temp.getOrgName())) {
				// 在department下
				if (nameObjMap.get(temp.getOrgName()) instanceof CloudDepartment) {
					CloudDepartment cloudDepartment = (CloudDepartment) nameObjMap.get(temp.getOrgName());
					model.setDepartmentId(cloudDepartment.getId());
				} else {
					// 在org下
					CloudOrganization cloudOrganization = (CloudOrganization) nameObjMap.get(temp.getOrgName());
					model.setDepartmentId(cloudOrganization.getDepartmentId());
					model.setOrgId(cloudOrganization.getId());
				}
				model.setOrgName(temp.getOrgName());
			} else {
				message.append("所属机构不存在；");
				succFlag = false;
			}
		}
		// 验证编码
		if (StringUtil.isNullOrEmpty(temp.getCode())) {
			message.append("编码不能为空；");
			succFlag = false;
		} else if (temp.getCode().length() > STAFF_CODE_MAX_LENGTH) {
			message.append("编码长度不能超过" + STAFF_CODE_MAX_LENGTH + "；");
			succFlag = false;
		} else {
			if (cloudStaffService.isCodeExisted(tenantId, temp.getCode())) {
				message.append("编码已存在；");
				succFlag = false;
			} else {
				model.setCode(temp.getCode());
			}
		}
		// 验证名称
		if (StringUtil.isNullOrEmpty(temp.getName())) {
			message.append("姓名不能为空；");
			succFlag = false;
		} else if (temp.getName().length() > STAFF_NAME_MAX_LENGTH) {
			message.append("姓名长度不能超过" + STAFF_NAME_MAX_LENGTH + "；");
			succFlag = false;
		} else {
			// 设置姓名全拼
			String pinyin = pinyin4jUtil.getPinYinNoToneAndSpace(temp.getName());
			model.setNameInitial(pinyin);
			model.setName(temp.getName());
		}
		// 验证性别
		if (StringUtil.isNullOrEmpty(temp.getGender())) {
			message.append("性别不能为空；");
			succFlag = false;
		} else {
			model.setGender(temp.getGender());
		}
		// 验证生日
		if (StringUtils.isNotEmpty(temp.getBirthday())) {

			try {

				// 如果时间先parse再format后和之前的不相等了，我们认为他的时间格式是不对的
				if (!validateDate(temp.getBirthday())) {
					message.append("生日格式不对；");
					succFlag = false;
				} else if (DateUtil.isAfter(new Date(), DateUtil.parse(temp.getBirthday(), DateUtil.DATE_FORMAT))) {
					message.append("生日不能在今天之后；");
					succFlag = false;
				} else {
					model.setBirthday(temp.getBirthday());
				}
			} catch (Exception e) {
				message.append("生日格式必须为yyyy-MM-dd；");
				succFlag = false;
			}
		}
		// 验证身份证
		if (StringUtils.isNotEmpty(temp.getCredentialNum())) {
			if (temp.getName().length() > STAFF_CREDENTIAL_NUM_MAX_LENGTH) {
				message.append("身份证号长度不能超过" + STAFF_NAME_MAX_LENGTH + "；");
				succFlag = false;
			} else {
				if (cloudStaffService.isCredentialNumExist(temp.getId(), temp.getCredentialNum())) {
					message.append("身份证号已经存在；");
					succFlag = false;
				} else {
					model.setCredentialNum(temp.getCredentialNum());
				}
			}
		}
		// 验证民族
		if (StringUtils.isNotEmpty(temp.getNationName())) {
			TenantPramSetting tenantPramSetting = tenantParamSettingService.findOneByParamName(tenantId, PropertyUtils.getPropertyValue("STAFF_NATION"), temp.getNationName());
			model.setNationName(temp.getNationName());
			model.setNationId(tenantPramSetting != null ? tenantPramSetting.getParmCode() : "");
		}
		// 验证婚姻状况
		if (StringUtils.isNotEmpty(temp.getMaritalStatusName())) {
			TenantPramSetting tenantPramSetting = tenantParamSettingService.findOneByParamName(tenantId, PropertyUtils.getPropertyValue("STAFF_MARITAL_STATUS"), temp.getMaritalStatusName());
			model.setMaritalStatusName(temp.getMaritalStatusName());
			model.setMaritalStatusId(tenantPramSetting != null ? tenantPramSetting.getParmCode() : "");
		}
		// 验证政治面貌
		if (StringUtils.isNotEmpty(temp.getPoliticalStatusName())) {
			TenantPramSetting tenantPramSetting = tenantParamSettingService.findOneByParamName(tenantId, PropertyUtils.getPropertyValue("STAFF_POLITICAL_STATUS"), temp.getPoliticalStatusName());
			model.setPoliticalStatusName(temp.getPoliticalStatusName());
			model.setPoliticalStatusId(tenantPramSetting != null ? tenantPramSetting.getParmCode() : "");
		}
		// 验证参加工作时间
		if (StringUtils.isNotEmpty(temp.getJoinWorkTime())) {
			try {
				if (!validateDate(temp.getJoinWorkTime())) {
					message.append("参加工作时间日期格式不对；");
					succFlag = false;
				} else if (StringUtils.isNotBlank(model.getBirthday())
						&& DateUtil.isBefore(DateUtil.parse(model.getBirthday(), DateUtil.DATE_FORMAT), DateUtil.parse(temp.getJoinWorkTime(), DateUtil.DATE_FORMAT))) {
					message.append("参加工作时间不能在生日之前 ");
					succFlag = false;
				} else {
					model.setJoinWorkTime(temp.getJoinWorkTime());
				}
			} catch (Exception e) {
				message.append("参加工作时间格式必须为yyyy-MM-dd；");
				succFlag = false;
			}
		}
		// 验证 工作年限
		if (StringUtils.isNotEmpty(temp.getWorkYearLimit())) {
			model.setWorkYearLimit(temp.getWorkYearLimit());
		}
		// 验证是否离职退休
		if (StringUtils.isNotEmpty(temp.getIsLeave())) {

			if ("是".equals(temp.getIsLeave())) {
				model.setIsLeave(CloudStaff.STATUS_LEAVE_YES);
			} else if ("否".equals(temp.getIsLeave())) {
				model.setIsLeave(CloudStaff.STATUS_LEAVE_NO);
			}

		}
		// 验证离职日期
		if (StringUtils.isNotEmpty(temp.getLeaveTime())) {
			try {
				// 在职状态
				if (CloudStaff.STATUS_LEAVE_NO.equals(model.getIsLeave())) {
					message.append("该人员依然在职,不需要离职退休时间");
					succFlag = false;
				} else if (!validateDate(temp.getLeaveTime())) {
					message.append("退休时间日期格式不对；");
					succFlag = false;
				} else if (StringUtils.isNotBlank(model.getJoinWorkTime())
						&& DateUtil.isBefore(DateUtil.parse(model.getJoinWorkTime(), DateUtil.DATE_FORMAT), DateUtil.parse(temp.getLeaveTime(), DateUtil.DATE_FORMAT))) {
					message.append("退休时间不能在参加工作时间之前；");
					succFlag = false;
				} else {
					model.setLeaveTime(temp.getLeaveTime());
				}
			} catch (Exception e) {
				message.append("退休时间格式必须为yyyy-MM-dd；");
				succFlag = false;
			}
		}
		// 验证用工类型
		if (StringUtils.isNotEmpty(temp.getWorkTypeName())) {
			/*
			 * if (StringUtil.equals(WorkTypeEnum.PATROL.getValue(),
			 * temp.getWorkTypeName())) {
			 * model.setWorkTypeCode(WorkTypeEnum.PATROL.getKey()); } else if
			 * (StringUtil.equals(WorkTypeEnum.CLEAN.getValue(),
			 * temp.getWorkTypeName())) {
			 * model.setWorkTypeCode(WorkTypeEnum.CLEAN.getKey()); } else {
			 * message.append("用工类型只能为巡检或保洁；"); succFlag = false; }
			 */
			TenantPramSetting tenantPramSetting = tenantParamSettingService.findOneByParamName(tenantId, PropertyUtils.getPropertyValue("STAFF_WORK_TYPE"), temp.getWorkTypeName());
			model.setWorkTypeCode(tenantPramSetting != null ? tenantPramSetting.getParmCode() : "");

		}
		// 验证orderindex
		if (StringUtils.isNotEmpty(temp.getOrderIndex())) {
			model.setOrderIndex(Integer.parseInt(temp.getOrderIndex()));
		}
		// 验证描述
		if (StringUtils.isNotEmpty(temp.getDescription())) {
			model.setDescription(temp.getDescription());
		}
		// 验证出生地
		if (StringUtils.isNotEmpty(temp.getBirthPlace())) {
			model.setBirthPlace(temp.getBirthPlace());
		}
		// 验证现籍
		if (StringUtils.isNotEmpty(temp.getPresentPlace())) {
			model.setPresentPlace(temp.getPresentPlace());
		}
		// 验证居住地
		if (StringUtils.isNotEmpty(temp.getLivePlace())) {
			model.setLivePlace(temp.getLivePlace());
		}
		/*********** 联系方式 ***********/
		// 验证手机号

		if (StringUtil.isNullOrEmpty(temp.getPhone())) {
			message.append("手机号码不能为空；");
			succFlag = false;
		} else {
			if (cloudStaffService.isPhoneExists(null, temp.getPhone())) {
				message.append("手机号已经存在；");
				succFlag = false;
			} else {

				model.setPhone(temp.getPhone());
			}
		}
		// 验证办公室电话
		if (StringUtils.isNotEmpty(temp.getOfficeTel())) {
			model.setOfficeTel(temp.getOfficeTel());
		}
		// 验证邮箱
		if (StringUtils.isNotEmpty(temp.getEmail())) {
			model.setEmail(temp.getEmail());
		}
		// 验证内部邮件
		if (StringUtils.isNotEmpty(temp.getInnerEmail())) {
			model.setInnerEmail(temp.getInnerEmail());
		}
		/*********** 教育背景 ***********/
		// 验证毕业学校
		if (StringUtils.isNotEmpty(temp.getGraduate())) {
			model.setGraduate(temp.getGraduate());
		}
		// 验证学历
		if (StringUtils.isNotEmpty(temp.getEducationName())) {
			TenantPramSetting tenantPramSetting = tenantParamSettingService.findOneByParamName(tenantId, PropertyUtils.getPropertyValue("STAFF_EDUCATION"), temp.getEducationName());
			model.setEducationName(temp.getEducationName());
			model.setEducationId(tenantPramSetting != null ? tenantPramSetting.getParmCode() : "");
		}
		/*********** 用工情况 ***********/
		// 验证人员编制性质
		if (StringUtils.isNotEmpty(temp.getAuthorizeName())) {
			TenantPramSetting tenantPramSetting = tenantParamSettingService.findOneByParamName(tenantId, PropertyUtils.getPropertyValue("STAFF_AUTHORIZE"), temp.getAuthorizeName());
			model.setAuthorizeName(temp.getAuthorizeName());
			model.setAuthorizeId(tenantPramSetting != null ? tenantPramSetting.getParmCode() : "");
		}
		// 验证职位
		if (StringUtils.isNotEmpty(temp.getPostName())) {
			TenantPramSetting tenantPramSetting = tenantParamSettingService.findOneByParamName(tenantId, PropertyUtils.getPropertyValue("STAFF_POSITION"), temp.getPostName());
			model.setPostName(temp.getPostName());
			model.setPostId(tenantPramSetting != null ? tenantPramSetting.getParmCode() : "");
		}
		// 验证职务
		if (StringUtils.isNotEmpty(temp.getPartyPostName())) {
			TenantPramSetting tenantPramSetting = tenantParamSettingService.findOneByParamName(tenantId, PropertyUtils.getPropertyValue("STAFF_POST"), temp.getPartyPostName());
			model.setPartyPostName(temp.getPartyPostName());
			model.setPartyPostId(tenantPramSetting != null ? tenantPramSetting.getParmCode() : "");
		}
		// 验证进入本单位时间
		if (StringUtils.isNotEmpty(temp.getEntryHereTime())) {
			try {
				if (!validateDate(temp.getEntryHereTime())) {
					message.append("进入本单位时间格式不对；");
					succFlag = false;
				}
				model.setEntryHereTime(temp.getEntryHereTime());
			} catch (Exception e) {
				message.append("进入本单位时间格式必须为yyyy-MM-dd；");
				succFlag = false;
			}
		}
		// 验证ID卡
		if (StringUtils.isNotEmpty(temp.getIdCard())) {
			model.setIdCard(temp.getIdCard());
		}
		// 验证社保卡号
		if (StringUtils.isNotEmpty(temp.getSocialSecurityNo())) {
			if (temp.getSocialSecurityNo().length() > STAFF_SOCIAL_SECURITY_NO_MAX_LENGTH) {
				message.append("社保卡号长度不能超过" + STAFF_SOCIAL_SECURITY_NO_MAX_LENGTH + "；");
				succFlag = false;
			} else if (cloudStaffService.isSocialSecurityNoExist(temp.getId(), temp.getSocialSecurityNo())) {
				message.append("社保卡号已经存在；");
				succFlag = false;
			} else {
				model.setSocialSecurityNo(temp.getSocialSecurityNo());
			}
		}
		// 验证社保缴纳情况
		if (StringUtils.isNotEmpty(temp.getSocialSecuritycase())) {
			TenantPramSetting tenantPramSetting = tenantParamSettingService.findOneByParamName(tenantId, PropertyUtils.getPropertyValue("STAFF_SOCIAL_SECURITY_CASE"), temp.getSocialSecuritycase());
			model.setSocialSecuritycase(tenantPramSetting != null ? tenantPramSetting.getParmCode() : "");
		}
		// 外包情况
		if (StringUtils.isNotEmpty(temp.getOutSourcing()) && "是".equals(temp.getOutSourcing())) {
			model.setOutSourcing(true);
			model.setOutSourcingComp(temp.getOutSourcingComp());
		}

		if (succFlag) {
			message.insert(0, "第" + row + "行：成功！");
			cloudStaffService.save(model);
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
	 * 校验日期的格式对不对
	 * 
	 * @param dateFormatted
	 * @return
	 * @throws Exception
	 */
	private boolean validateDate(String dateFormatted) throws Exception {
		String birthdayFormatted = DateUtil.format(DateUtil.parse(dateFormatted, DateUtil.DATE_FORMAT), DateUtil.DATE_FORMAT);

		return dateFormatted.equals(birthdayFormatted);
	}

}
