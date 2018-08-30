package com.vortex.cloud.ums.web;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.vortex.cloud.ums.annotation.FunctionCode;
import com.vortex.cloud.ums.dataaccess.service.ICloudDepartmentService;
import com.vortex.cloud.ums.dataaccess.service.ICloudOrganizationService;
import com.vortex.cloud.ums.dataaccess.service.ICloudStaffService;
import com.vortex.cloud.ums.dataaccess.service.ICloudStaffTempService;
import com.vortex.cloud.ums.dto.CloudManagementUserReqParamDto;
import com.vortex.cloud.ums.dto.CloudStaffDto;
import com.vortex.cloud.ums.dto.CloudStaffSearchDto;
import com.vortex.cloud.ums.enums.CompanyTypeEnum;
import com.vortex.cloud.ums.enums.ResponseType;
import com.vortex.cloud.ums.model.CloudDepartment;
import com.vortex.cloud.ums.model.upload.CloudStaffTemp;
import com.vortex.cloud.ums.util.CommonUtils;
import com.vortex.cloud.ums.util.FileOperateUtil;
import com.vortex.cloud.ums.util.UploadUtil;
import com.vortex.cloud.ums.util.support.ForeContext;
import com.vortex.cloud.ums.util.tree.ITreeService;
import com.vortex.cloud.ums.util.web.BaseController;
import com.vortex.cloud.vfs.common.exception.VortexException;
import com.vortex.cloud.vfs.common.lang.StringUtil;
import com.vortex.cloud.vfs.common.mapper.JsonMapper;
import com.vortex.cloud.vfs.common.web.springmvc.SpringmvcUtils;
import com.vortex.cloud.vfs.data.dto.DataStore;
import com.vortex.cloud.vfs.data.dto.LoginReturnInfoDto;
import com.vortex.cloud.vfs.data.dto.RestResultDto;
import com.vortex.cloud.vfs.data.support.SearchFilter;
import com.vortex.cloud.vfs.data.support.SearchFilter.Operator;
import com.vortex.cloud.vfs.data.support.SearchFilters;

/**
 * management用户管理需要区分对待租户管理员和业务系统管理员。 1、业务系统管理员访问时，实现人员管理、用户管理、角色配置。
 * 除携带原有的公共参数（租户code、业务系统code、用户Id）以外，另外新增部门code用于过滤租户的部门组织机构树。
 * 用户开通时，默认关联访问时指定的业务系统。 2、租户管理员访问时，实现人员管理、用户管理、用户关联业务系统。 访问时只需携带原有的公共参数。
 * 注意去除角色配置功能，用户关联时的业务系统为租户下的所有业务系统列表。
 * 
 * @author lishijun
 */
@SuppressWarnings("all")
@RestController
@RequestMapping("cloud/management/staff/system")
public class CloudStaffSystemController extends BaseController {
	private static final Logger logger = LoggerFactory.getLogger(CloudStaffSystemController.class);

	private static final String FORE_DYNAMIC_SUFFIX = com.vortex.cloud.ums.support.ManagementConstant.PERMISSION_SUFFIX_SA;
	private static final String BACK_DYNAMIC_SUFFIX = com.vortex.cloud.ums.support.ManagementConstant.PERMISSION_SUFFIX_SA;

	@Resource
	private ICloudDepartmentService cloudDepartmentService;
	@Resource
	private ICloudStaffTempService cloudStaffTempService;
	@Resource
	private ITreeService treeService;

	@Resource
	private ICloudOrganizationService cloudOrganizationService;

	@Resource
	private ICloudStaffService cloudStaffService;

	// 时间转化问题
	@InitBinder
	public void initBinder(WebDataBinder binder) {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		dateFormat.setLenient(true);
		binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
	}

	/**
	 * @param reqParam
	 */
	protected void checkReqParams(CloudManagementUserReqParamDto reqParam) {
		String systemCode = reqParam.getSystemCode();
		if (StringUtils.isBlank(systemCode)) {
			String msg = "请求参数中缺少业务系统Code";
			logger.error("checkReqParams()," + msg);
			throw new VortexException(msg);
		}

		// 允许没有部门Code
		// String departmentCode = reqParam.getDepartmentCode();
		// if (StringUtils.isBlank(departmentCode)) {
		// String msg = "请求参数中缺少部门Code";
		// logger.error("checkReqParams()," + msg);
		// throw new VortexException(msg);
		// }
	}

	/**
	 * 将code转成id
	 * 
	 * @param reqParam
	 */
	private void translateCodeToId(CloudManagementUserReqParamDto reqParam) {
		String departmentCode = reqParam.getDepartmentCode();
		if (StringUtils.isBlank(departmentCode)) {
			return;
		}

		// 根据部门code获取部门Id
		CloudDepartment department = cloudDepartmentService.getDepartmentByCode(departmentCode, reqParam.getTenantId());
		if (department == null) {
			logger.error("translateCodeToId(),未能根据租户code和部门code获取到部门记录");
			throw new VortexException("未能根据租户code和部门code获取到部门记录");
		}
		reqParam.setDepartmentId(department.getId());
	}

	/**
	 * 添加时的表单校验。
	 * 
	 * @param paramName
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "checkForAdd/{paramName}" + FORE_DYNAMIC_SUFFIX, method = RequestMethod.POST)

	public RestResultDto<Boolean> checkForAdd(@PathVariable("paramName") String paramName, HttpServletRequest request) {
		try {

			LoginReturnInfoDto loginInfo = super.getLoginInfo(request);
			String tenantId = loginInfo.getTenantId();
			String paramVal = SpringmvcUtils.getParameter(paramName);
			if (StringUtils.isBlank(tenantId) || StringUtils.isBlank(paramName) || StringUtils.isBlank(paramVal)) {
				return RestResultDto.newSuccess(false);
			}

			if ("code".equals(paramName)) {
				if (cloudStaffService.isCodeExisted(tenantId, paramVal)) {
					return RestResultDto.newSuccess(false);
				}
			}

			return RestResultDto.newSuccess(true);
		} catch (Exception e) {
			logger.error("校验出错", e);
			return RestResultDto.newFalid("校验出错", e.getMessage());
		}
	}

	/**
	 * 新增
	 * 
	 * @param dto
	 * @return
	 */
	@RequestMapping(value = "add" + BACK_DYNAMIC_SUFFIX, method = RequestMethod.POST)
	@FunctionCode(value = "CF_MANAGE_SYSTEM_STAFF_ADD", type = ResponseType.Json)
	public RestResultDto<Boolean> add(HttpServletRequest request, CloudStaffDto dto) {
		try {
			dto.setTenantId(super.getLoginInfo(request).getTenantId());
			cloudStaffService.save(dto);
			return RestResultDto.newSuccess(true, "添加成功");
		} catch (Exception e) {
			logger.error("addDtl", e);
			return RestResultDto.newFalid("添加失败", e.getMessage());
		}

	}

	/**
	 * 删除
	 * 
	 * @param dto
	 * @return
	 */
	@RequestMapping(value = "delete/{id}" + BACK_DYNAMIC_SUFFIX, method = RequestMethod.POST)
	@FunctionCode(value = "CF_MANAGE_SYSTEM_STAFF_DELETE", type = ResponseType.Json)
	public RestResultDto<Boolean> delete(HttpServletRequest request, @PathVariable String id) {
		try {
			cloudStaffService.deleteStaffAndUser(id);
			return RestResultDto.newSuccess(true, "删除成功");
		} catch (Exception e) {
			logger.error("delete", e);
			return RestResultDto.newFalid("删除失败", e.getMessage());
		}

	}

	@RequestMapping(value = "pageList" + FORE_DYNAMIC_SUFFIX, method = RequestMethod.POST)
	// @FunctionCode(value = "CF_MANAGE_SYSTEM_STAFF_LIST", type =
	// ResponseType.Json)
	public RestResultDto<DataStore<CloudStaffDto>> pageList(HttpServletRequest request, HttpServletResponse response) {
		try {

			CloudStaffSearchDto searchDto = this.getSearchCondition(request);

			// 得到分页
			Sort defaultSort = sortMethod(request);
			Pageable pageable = ForeContext.getPageable(request, defaultSort);
			Page<CloudStaffDto> pageResult = cloudStaffService.findPageBySearchDto(pageable, searchDto);

			DataStore<CloudStaffDto> ds = new DataStore<>();
			if (pageResult != null) {
				ds.setTotal(pageResult.getTotalElements());
				ds.setRows(pageResult.getContent());
			}

			return RestResultDto.newSuccess(ds);
		} catch (Exception e) {
			logger.error("查询分页出错", e);
			return RestResultDto.newFalid("查询分页出错", e.getMessage());
		}
	}

	@RequestMapping(value = "pageListWithPermission" + FORE_DYNAMIC_SUFFIX, method = RequestMethod.POST)
	// @FunctionCode(value = "CF_MANAGE_SYSTEM_STAFF_LIST", type =
	// ResponseType.Json)
	public RestResultDto<DataStore<CloudStaffDto>> pageListWithPermission(HttpServletRequest request, HttpServletResponse response) {
		try {
			CloudStaffSearchDto searchDto = this.getSearchCondition(request);

			// 得到分页
			Sort defaultSort = sortMethod(request);
			Pageable pageable = ForeContext.getPageable(request, defaultSort);
			Page<CloudStaffDto> pageResult = cloudStaffService.findPageWithPermissionBySearchDto(pageable, searchDto);

			DataStore<CloudStaffDto> ds = new DataStore<>();
			if (pageResult != null) {
				ds.setTotal(pageResult.getTotalElements());
				ds.setRows(pageResult.getContent());
			}

			return RestResultDto.newSuccess(ds);
		} catch (Exception e) {
			logger.error("查询分页出错", e);
			return RestResultDto.newFalid("查询分页出错", e.getMessage());
		}
	}

	private CloudStaffSearchDto getSearchCondition(HttpServletRequest request) throws Exception {
		// 查询条件
		LoginReturnInfoDto loginInfo = super.getLoginInfo(request);
		String tenantId = loginInfo.getTenantId();
		String userId = loginInfo.getUserId();
		String departmentId = SpringmvcUtils.getParameter("departmentId");
		String selectedId = SpringmvcUtils.getParameter("selectedId");
		String selectedType = SpringmvcUtils.getParameter("selectedType");

		String code = SpringmvcUtils.getParameter("code");
		String name = SpringmvcUtils.getParameter("name");
		String socialSecurityNo = SpringmvcUtils.getParameter("socialSecurityNo");
		String credentialNum = SpringmvcUtils.getParameter("credentialNum");
		String gender = SpringmvcUtils.getParameter("gender");

		String ageGroupStart = SpringmvcUtils.getParameter("ageGroupStart");
		String ageGroupEnd = SpringmvcUtils.getParameter("ageGroupEnd");
		String workYearLimitStart = SpringmvcUtils.getParameter("workYearLimitStart");
		String workYearLimitEnd = SpringmvcUtils.getParameter("workYearLimitEnd");
		String educationId = SpringmvcUtils.getParameter("educationId");
		String partyPostId = SpringmvcUtils.getParameter("partyPostId");
		String partyPostIds = SpringmvcUtils.getParameter("partyPostIds");
		String ckRange = SpringmvcUtils.getParameter("ckRange");
		String phone = SpringmvcUtils.getParameter("phone");
		String isLeave = SpringmvcUtils.getParameter("isLeave");
		logger.info("pageList(" + "tenantId=" + tenantId + ", departmentId=" + departmentId + ", selectedId=" + selectedId + ", selectedType=" + selectedType + ", code=" + code + ", name=" + name + ", socialSecurityNo=" + socialSecurityNo
				+ ", credentialNum=" + credentialNum + ")");

		CloudStaffSearchDto searchDto = new CloudStaffSearchDto();
		searchDto.setTenantId(tenantId);
		searchDto.setDepartmentId(departmentId);

		String orgId = null;
		if (CompanyTypeEnum.ORG.getKey().equals(selectedType)) {
			orgId = selectedId;
		}
		searchDto.setOrgId(orgId);
		searchDto.setUserId(userId);
		searchDto.setGender(gender);
		searchDto.setCode(code);
		searchDto.setName(name);
		searchDto.setSocialSecurityNo(socialSecurityNo);
		searchDto.setCredentialNum(credentialNum);
		searchDto.setAgeGroupEnd(ageGroupEnd);
		searchDto.setAgeGroupStart(ageGroupStart);
		searchDto.setWorkYearLimitStart(workYearLimitStart);
		searchDto.setWorkYearLimitEnd(workYearLimitEnd);
		searchDto.setEducationId(educationId);
		searchDto.setPartyPostId(partyPostId);
		if (StringUtils.isNotBlank(partyPostIds)) {
			searchDto.setPartyPostIds(Arrays.asList(partyPostIds.split(",")));
		}
		searchDto.setCkRange(ckRange);
		searchDto.setIsLeave(isLeave);
		searchDto.setPhone(phone);
		return searchDto;
	}

	@RequestMapping(value = "loadStaffDtl" + FORE_DYNAMIC_SUFFIX, method = RequestMethod.POST)
	public RestResultDto<CloudStaffDto> loadStaffDtl(HttpServletResponse response) {
		try {
			String id = SpringmvcUtils.getParameter("id");
			JsonMapper jsonMapper = new JsonMapper();
			CloudStaffDto dto = cloudStaffService.getById(id);
			return RestResultDto.newSuccess(dto);
		} catch (Exception e) {
			logger.error("加载人员出错", e);
			return RestResultDto.newFalid("加载人员出错", e.getMessage());
		}

	}

	/**
	 * 修改时的表单校验
	 * 
	 * @param paramName
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "checkForUpdate/{paramName}" + FORE_DYNAMIC_SUFFIX, method = RequestMethod.POST)

	public RestResultDto<Boolean> checkForUpdate(@PathVariable("paramName") String paramName, HttpServletRequest request) {
		try {

			LoginReturnInfoDto loginInfo = super.getLoginInfo(request);
			String tenantId = loginInfo.getTenantId();
			String id = SpringmvcUtils.getParameter("id");
			if (StringUtils.isBlank(id)) {
				logger.error("checkForUpdate(), ID is null or empty");
				return RestResultDto.newSuccess(false);
			}

			String paramVal = SpringmvcUtils.getParameter(paramName);
			if (StringUtils.isBlank(paramName) || StringUtils.isBlank(paramVal)) {
				return RestResultDto.newSuccess(false);
			}

			if ("code".equals(paramName)) {
				return RestResultDto.newSuccess(cloudStaffService.validateCodeOnUpdate(tenantId, id, paramVal));
			}

			return RestResultDto.newSuccess(true);
		} catch (Exception e) {
			logger.error("校验出错", e);
			return RestResultDto.newFalid("校验出错", e.getMessage());
		}
	}

	/**
	 * 删除
	 * 
	 * @param dto
	 * @return
	 */
	@RequestMapping(value = "deletes" + BACK_DYNAMIC_SUFFIX, method = RequestMethod.POST)
	@FunctionCode(value = "CF_MANAGE_STAFF_DELETE", type = ResponseType.Json)

	public RestResultDto<Boolean> deletes(HttpServletRequest request, @RequestBody String[] ids) {
		try {
			List<String> deleteList = new ArrayList<>();
			List<String> remainList = new ArrayList<>();
			this.splitForDeletes(ids, deleteList, remainList);

			cloudStaffService.deletesStaffAndUser(deleteList);
			return RestResultDto.newSuccess(true, "本次删除操作执行结果：删除成功" + deleteList.size() + "条," + "删除失败" + (remainList.size()) + "条");
		} catch (Exception e) {
			logger.error("deletes()", e);
			return RestResultDto.newFalid("删除失败", e.getMessage());
		}

	}

	/**
	 * 将列表分为可以删除的，不可以删除的。
	 * 
	 * @param ids
	 * @param deleteList
	 *            可以删除的记录
	 * @param remainList
	 *            不可以删除的记录
	 */
	private void splitForDeletes(String[] ids, List<String> deleteList, List<String> remainList) {
		if (ArrayUtils.isEmpty(ids)) {
			return;
		}

		boolean isAllowDel = false;
		for (String id : ids) {
			isAllowDel = cloudStaffService.canBeDeleted(id);
			if (isAllowDel) {
				deleteList.add(id);
			} else {
				remainList.add(id);
			}
		}
	}

	/**
	 * 下载导入模版
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping(value = "downloadTemplate")
	public void downloadTemplate(HttpServletRequest request, HttpServletResponse response) throws Exception {

		UploadUtil.downloadTemplate(request, response, "人员导入模版.zip");
	}

	/**
	 * 下载Excel表
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping(value = "download")
	public void download(HttpServletRequest request, HttpServletResponse response) throws Exception {

		response.setContentType("text/html;charset=utf-8");
		request.setCharacterEncoding("UTF-8");
		LoginReturnInfoDto loginInfo = super.getLoginInfo(request);
		String tenantId = loginInfo.getTenantId();
		String userId = loginInfo.getUserId();

		// 查询配置信息
		String title = "人员信息表";
		// 导出数据
		List<CloudStaffDto> children = Lists.newArrayList();
		// 查询条件
		SearchFilters andSearchFilters = new SearchFilters(SearchFilters.Operator.AND);
		List<SearchFilter> searchFilter = CommonUtils.buildFromHttpRequest(request);
		searchFilter.add(new SearchFilter("tenantId", Operator.EQ, tenantId));
		String columnFields = SpringmvcUtils.getParameter("columnFields");
		String columnNames = SpringmvcUtils.getParameter("columnNames");
		/*
		 * columnFields = columnFields.substring(0, columnFields.lastIndexOf(","));
		 * columnNames = columnNames.substring(0, columnNames.lastIndexOf(","));
		 */
		String downloadAll = SpringmvcUtils.getParameter("downloadAll");
		String downloadIds = SpringmvcUtils.getParameter("downloadIds");

		// 当前有权限的公司
		List<String> companyIds = cloudOrganizationService.getCompanyIdsWithPermission(userId, tenantId);
		// 没有权限直接返回
		if (CollectionUtils.isEmpty(companyIds)) {
			FileOperateUtil.exportExcel(request, response, title, columnFields, columnNames, children);
		}

		List<SearchFilter> companyFilter = Lists.newArrayList();
		companyFilter.add(new SearchFilter("departmentId", Operator.IN, companyIds.toArray()));
		companyFilter.add(new SearchFilter("orgId", Operator.IN, companyIds.toArray()));
		SearchFilters orSearchFilters = new SearchFilters(companyFilter, SearchFilters.Operator.OR);
		andSearchFilters.add(orSearchFilters);
		// 排序
		Sort defSort = sortMethod(request);

		boolean isDownloadAll = (StringUtil.isNullOrEmpty(downloadAll) ? false : Boolean.valueOf(downloadAll));

		List<CloudStaffDto> staffs = null;
		if (isDownloadAll) {
			/*
			 * if (!StringUtil.isNullOrEmpty(downloadIds)) { searchFilter.add(new
			 * SearchFilter("id", Operator.IN, StringUtil.splitComma(downloadIds))); }
			 * searchFilter = CommonUtils.bindTenantId(searchFilter);
			 * andSearchFilters.addSearchFilter(searchFilter);
			 * 
			 * list = cloudStaffService.findListByFilters(andSearchFilters, defSort);
			 */
			CloudStaffSearchDto searchDto = new CloudStaffSearchDto();
			searchDto.setTenantId(tenantId);
			if (!StringUtil.isNullOrEmpty(downloadIds)) {
				searchDto.setIds(Arrays.asList(StringUtil.splitComma(downloadIds)));
			}

			staffs = cloudStaffService.findListBySearchDto(defSort, searchDto);

		} else {

			CloudStaffSearchDto searchDto = this.getSearchCondition(request);

			Pageable pageable = ForeContext.getPageable(request, defSort);
			Page<CloudStaffDto> pageResult = cloudStaffService.findPageBySearchDto(pageable, searchDto);

			/*
			 * Pageable pageable = ForeContext.getPageable(request, defSort);
			 * Page<CloudStaff> pageResult = cloudStaffService.findPageByFilters(pageable,
			 * andSearchFilters.addSearchFilter(searchFilter));
			 */
			if (null != pageResult) {
				children = pageResult.getContent();
			}
		}
		if (CollectionUtils.isNotEmpty(staffs)) {
			children.addAll(staffs);

		}
		FileOperateUtil.exportExcel(request, response, title, columnFields, columnNames, children);
	}

	/**
	 * 上传文件(压缩包)
	 * 
	 * @author
	 * @date
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "uploadImportData")
	public void uploadImportData(HttpServletRequest request, HttpServletResponse response) throws Exception {

		UploadUtil.uploadImportData(request, response, CloudStaffTemp.class, cloudStaffTempService);
	}

	/**
	 * 验证手机号唯一
	 * 
	 * @param paramName
	 * @return
	 */
	@RequestMapping(value = "validatePhone/{param}" + FORE_DYNAMIC_SUFFIX, method = RequestMethod.POST)

	public RestResultDto<Boolean> validatePhone(@PathVariable("param") String paramName) {
		try {

			// 入参非空校验
			if (StringUtil.isNullOrEmpty(paramName)) {
				return RestResultDto.newSuccess(false);
			}
			Map<String, Object> filter = Maps.newHashMap();
			String paramVal = SpringmvcUtils.getParameter(paramName);
			String id = SpringmvcUtils.getParameter("id"); // 更新记录时，也要校验
			String phone = null;

			if (StringUtil.isNullOrEmpty(paramVal)) {
				return RestResultDto.newSuccess(false);
			}
			if (!("phone".equals(paramName))) {
				return RestResultDto.newSuccess(true);
			} else {
				phone = paramVal;
			}

			logger.info("validate():id=" + id + "phone=" + phone);

			// 已经存在就返回false，不存在返回true
			boolean isExist = cloudStaffService.isPhoneExists(id, phone);
			return RestResultDto.newSuccess(!isExist);
		} catch (Exception e) {
			logger.error("校验出错", e);
			return RestResultDto.newFalid("校验出错", e.getMessage());
		}
	}

	/**
	 * @Title: sortMethod @Description: (排序) @return Sort @throws
	 */
	private Sort sortMethod(HttpServletRequest request) {
		// 得到分页
		List<Order> orders = Lists.newArrayList();
		Sort frontSort = ForeContext.getSort(request);
		orders.add(new Order(Direction.ASC, "orderIndex"));
		orders.add(new Order(Direction.DESC, "createTime"));

		Sort defaultSort = new Sort(orders);
		if (frontSort != null) {
			defaultSort = frontSort.and(defaultSort);
		}

		return defaultSort;
	}
}
