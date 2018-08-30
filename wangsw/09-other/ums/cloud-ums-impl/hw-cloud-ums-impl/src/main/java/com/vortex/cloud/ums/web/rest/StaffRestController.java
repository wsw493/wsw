package com.vortex.cloud.ums.web.rest;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.collect.Maps;
import com.vortex.cloud.ums.dataaccess.service.ICloudStaffService;
import com.vortex.cloud.ums.dataaccess.service.IManagementRestService;
import com.vortex.cloud.ums.dto.CloudStaffDto;
import com.vortex.cloud.ums.dto.CloudStaffSearchDto;
import  com.vortex.cloud.vfs.data.dto.LoginReturnInfoDto;
import com.vortex.cloud.ums.model.CloudStaff;
import com.vortex.cloud.ums.support.ManagementConstant;
import com.vortex.cloud.ums.tree.StaffOrganizationTree;
import com.vortex.cloud.ums.tree.StaffOrganizationTreeWithPermission;
import com.vortex.cloud.ums.util.web.BaseController;
import com.vortex.cloud.ums.util.tree.ITreeService;
import com.vortex.cloud.vfs.common.exception.VortexException;
import com.vortex.cloud.vfs.common.mapper.JsonMapper;
import com.vortex.cloud.vfs.common.web.springmvc.SpringmvcUtils;
import com.vortex.cloud.vfs.data.dto.RestResultDto;

/**
 * 
 * @author lsm
 * @date 2016年4月15日
 */
@SuppressWarnings("all")
@RestController
@RequestMapping("cloud/management/rest/staff")
public class StaffRestController extends BaseController {

	private static final Object STAFF_ID = "staffId";
	private static final Object STAFF_CODE = "staffCode";
	private static final Object USER_ID = "userId";
	private static final Object DEPARTMENT_ID = "departmentId";

	private static final Object REGISTR_TYPE = "registerType"; // 标明人员是否已经注册成为用户
	private static final Object TENANT_ID = "tenantId";
	private static final Object STAFF_NAME = "staffName";
	private static final Object IDS = "ids";
	private static final Object ID = "id";
	private static final Object STAFF_NAMES = "staffNames";
	private static final String COMPANY_ID = "companyId";
	private static final Object DEPT_IDS = "deptIds";
	private static final Object CONTAINS_TENANT = "containsTenant";

	private Logger logger = LoggerFactory.getLogger(ParamRestController.class);
	private JsonMapper jm = new JsonMapper();
	@Resource
	private ITreeService treeService;
	@Resource
	private IManagementRestService managementRestService;
	@Resource
	private ICloudStaffService cloudStaffService;

	/**
	 * 根据人员id，得到人员基本信息
	 * 
	 * 
	 * 
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "getstaffbyid" + ManagementConstant.PERMISSION_SUFFIX_READ, method = { RequestMethod.GET, RequestMethod.POST })
	public RestResultDto getStaffById(HttpServletRequest request) throws Exception {
		String msg = null;
		Integer result = null;
		Object data = null;

		try {
			Map<String, String> paramMap = jm.fromJson(SpringmvcUtils.getParameter(ManagementConstant.REST_PMS), Map.class);
			if (MapUtils.isEmpty(paramMap)) {
				throw new VortexException("参数不能为空");
			}

			String staffId = paramMap.get(STAFF_ID);
			if (StringUtils.isBlank(staffId)) {
				throw new VortexException("请传入参数：" + STAFF_ID);
			}

			if (logger.isDebugEnabled()) {
				logger.debug("getStaffById(), staffId=" + staffId);
			}
			CloudStaff cloudStaff = managementRestService.getStaffById(staffId);
			result = ManagementConstant.REST_RESULT_SUCC;
			msg = "成功获取用户信息";
			data = cloudStaff;
		} catch (Exception e) {
			e.printStackTrace();
			result = ManagementConstant.REST_RESULT_FAIL;
			msg = e.getMessage();
			data = null;
			logger.error(msg, e);
		} finally {
			RestResultDto restResultDto = new RestResultDto();
			restResultDto.setData(data);
			restResultDto.setMsg(msg);
			restResultDto.setResult(result);
			return restResultDto;
		}
	}

	/**
	 * 根据人员code和租户code，得到人员基本信息
	 * 
	 * 
	 * 
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "getstaffbycode" + ManagementConstant.PERMISSION_SUFFIX_READ, method = { RequestMethod.GET, RequestMethod.POST })
	public RestResultDto getStaffByCode(HttpServletRequest request) throws Exception {
		String msg = null;
		Integer result = null;
		Object data = null;

		try {
			LoginReturnInfoDto loginInfo = super.getLoginInfo(request);
			String tenantId = loginInfo.getTenantId();

			Map<String, String> paramMap = jm.fromJson(SpringmvcUtils.getParameter(ManagementConstant.REST_PMS), Map.class);
			if (MapUtils.isEmpty(paramMap)) {
				throw new VortexException("参数不能为空");
			}
			String staffCode = paramMap.get(STAFF_CODE);
			if (StringUtils.isBlank(staffCode)) {
				throw new VortexException("请传入参数：" + STAFF_CODE);
			}

			logger.debug("getStaffById(), staffCode=" + staffCode + ",tenantId=" + tenantId);
			CloudStaff cloudStaff = managementRestService.getStaffByCode(staffCode, tenantId);
			result = ManagementConstant.REST_RESULT_SUCC;
			msg = "成功获取用户信息";
			data = cloudStaff;
		} catch (Exception e) {
			e.printStackTrace();
			result = ManagementConstant.REST_RESULT_FAIL;
			msg = e.getMessage();
			data = null;
			logger.error(msg, e);
		} finally {
			RestResultDto restResultDto = new RestResultDto();
			restResultDto.setData(data);
			restResultDto.setMsg(msg);
			restResultDto.setResult(result);
			return restResultDto;
		}
	}

	/**
	 * 根据用户id，查询人员基本信息
	 * 
	 * 
	 * 
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "getstaffbyuserid" + ManagementConstant.PERMISSION_SUFFIX_READ, method = { RequestMethod.GET, RequestMethod.POST })
	public RestResultDto getStaffByUserId(HttpServletRequest request) throws Exception {
		String msg = null;
		Integer result = null;
		Object data = null;

		try {
			Map<String, String> paramMap = jm.fromJson(SpringmvcUtils.getParameter(ManagementConstant.REST_PMS), Map.class);
			if (MapUtils.isEmpty(paramMap)) {
				throw new VortexException("参数不能为空");
			}

			String userId = paramMap.get(USER_ID);
			if (StringUtils.isBlank(userId)) {
				throw new VortexException("请传入参数：" + USER_ID);
			}

			if (logger.isDebugEnabled()) {
				logger.debug("getStaffByUserId(), userId=" + userId);
			}
			CloudStaff cloudStaff = managementRestService.getStaffByUserId(userId);
			result = ManagementConstant.REST_RESULT_SUCC;
			msg = "成功获取用户信息";
			data = cloudStaff;
		} catch (Exception e) {
			e.printStackTrace();
			result = ManagementConstant.REST_RESULT_FAIL;
			msg = e.getMessage();
			data = null;
			logger.error(msg, e);
		} finally {
			RestResultDto restResultDto = new RestResultDto();
			restResultDto.setData(data);
			restResultDto.setMsg(msg);
			restResultDto.setResult(result);
			return restResultDto;
		}
	}

	/**
	 * 根据部门id，得到只属于此部门的所有人员id（因人员信息字段过多，暂不考虑直接返回人员所有信息）
	 * 
	 * 
	 * 
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "getstaffsbydepartmentid" + ManagementConstant.PERMISSION_SUFFIX_READ, method = { RequestMethod.GET, RequestMethod.POST })
	public RestResultDto getStaffsByDepartmentId(HttpServletRequest request) throws Exception {
		String msg = null;
		Integer result = null;
		Object data = null;

		try {
			Map<String, String> paramMap = jm.fromJson(SpringmvcUtils.getParameter(ManagementConstant.REST_PMS), Map.class);
			if (MapUtils.isEmpty(paramMap)) {
				throw new VortexException("参数不能为空");
			}

			String departmentId = paramMap.get(DEPARTMENT_ID);
			if (StringUtils.isBlank(departmentId)) {
				throw new VortexException("请传入参数：" + DEPARTMENT_ID);
			}

			if (logger.isDebugEnabled()) {
				logger.debug("getStaffsByDepartmentId(), departmentId=" + departmentId);
			}
			List<String> cloudStaffs = managementRestService.getStaffsByDepartmentId(departmentId);
			result = ManagementConstant.REST_RESULT_SUCC;
			msg = "成功获取用户信息";
			data = cloudStaffs;
		} catch (Exception e) {
			e.printStackTrace();
			result = ManagementConstant.REST_RESULT_FAIL;
			msg = e.getMessage();
			data = null;
			logger.error(msg, e);
		} finally {
			RestResultDto restResultDto = new RestResultDto();
			restResultDto.setData(data);
			restResultDto.setMsg(msg);
			restResultDto.setResult(result);
			return restResultDto;
		}
	}

	/**
	 * 根据人员部门id，得到部门及其所有子部门的人员
	 * 
	 * 
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "getallstaffsbydepartmentid" + ManagementConstant.PERMISSION_SUFFIX_READ, method = { RequestMethod.GET, RequestMethod.POST })
	public RestResultDto getAllStaffsByDepartmentId(HttpServletRequest request) throws Exception {
		String msg = null;
		Integer result = null;
		Object data = null;

		try {
			Map<String, String> paramMap = jm.fromJson(SpringmvcUtils.getParameter(ManagementConstant.REST_PMS), Map.class);
			if (MapUtils.isEmpty(paramMap)) {
				throw new VortexException("参数不能为空");
			}

			String departmentId = paramMap.get(DEPARTMENT_ID);
			if (StringUtils.isBlank(departmentId)) {
				throw new VortexException("请传入参数：" + DEPARTMENT_ID);
			}

			if (logger.isDebugEnabled()) {
				logger.debug("getAllStaffsByDepartmentId(), departmentId=" + departmentId);
			}
			List<String> cloudStaffs = managementRestService.getAllStaffsByDepartmentId(departmentId);
			result = ManagementConstant.REST_RESULT_SUCC;
			msg = "成功获取用户信息";
			data = cloudStaffs;
		} catch (Exception e) {
			e.printStackTrace();
			result = ManagementConstant.REST_RESULT_FAIL;
			msg = e.getMessage();
			data = null;
			logger.error(msg, e);
		} finally {
			RestResultDto restResultDto = new RestResultDto();
			restResultDto.setData(data);
			restResultDto.setMsg(msg);
			restResultDto.setResult(result);
			return restResultDto;
		}
	}

	/**
	 * 获取租户下的人员列表，单个记录的信息为：id, name, job, tel, email
	 * 
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "getListByRegisterType", method = { RequestMethod.GET, RequestMethod.POST })
	public RestResultDto getTenantStaffListByType(HttpServletRequest request) throws Exception {
		String msg = null;
		Integer result = null;
		Object data = null;

		try {
			LoginReturnInfoDto loginInfo = super.getLoginInfo(request);
			String tenantId = loginInfo.getTenantId();

			Map<String, Object> paramMap = jm.fromJson(SpringmvcUtils.getParameter(ManagementConstant.REST_PMS), Map.class);
			if (MapUtils.isEmpty(paramMap)) {
				throw new VortexException("参数不能为空");
			}

			String registerType = (String) paramMap.get(REGISTR_TYPE);
			List<String> deptIds = (List<String>) paramMap.get(DEPT_IDS);
			String containsTenant = (String) paramMap.get(CONTAINS_TENANT);

			if (StringUtils.isBlank(registerType)) {
				throw new VortexException("请传入参数：" + REGISTR_TYPE);
			}

			if (logger.isDebugEnabled()) {
				logger.debug("getStaffListOfTenant(), registerType=" + registerType);
			}
			CloudStaffSearchDto searchDto = new CloudStaffSearchDto();
			searchDto.setRegisterType(registerType);
			searchDto.setDeptIds(deptIds);
			searchDto.setContainsTenant(containsTenant);
			searchDto.setTenantId(tenantId);

			data = managementRestService.getStaffListByUserRegisterType(searchDto);
			msg = "成功获取用户信息";
			result = ManagementConstant.REST_RESULT_SUCC;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(msg, e);

			data = null;
			msg = e.getMessage();
			result = ManagementConstant.REST_RESULT_FAIL;
		} finally {
			RestResultDto restResultDto = new RestResultDto();
			restResultDto.setData(data);
			restResultDto.setMsg(msg);
			restResultDto.setResult(result);
			return restResultDto;
		}
	}

	/**
	 * 加载机构人员树，机构+部门+人员(加载一个租户下的)
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "loadStaffTree" + ManagementConstant.PERMISSION_SUFFIX_READ, method = { RequestMethod.GET, RequestMethod.POST })
	public RestResultDto loadTree(HttpServletRequest request) throws Exception {
		String msg = null;
		Integer result = ManagementConstant.REST_RESULT_SUCC;
		Object data = null;
		JsonMapper mapper = new JsonMapper();
		try {
			Map<String, String> paramMap = jm.fromJson(SpringmvcUtils.getParameter(ManagementConstant.REST_PMS), Map.class);
			if (MapUtils.isEmpty(paramMap)) {
				throw new VortexException("参数不能为空");
			}
			String tenantId = paramMap.get(TENANT_ID);
			StaffOrganizationTree staffOrganizationTree = StaffOrganizationTree.getInstance();
			staffOrganizationTree.reloadDeptOrgStaffTree(tenantId, null);
			data = treeService.generateJsonCheckboxTree(staffOrganizationTree, false);
		} catch (Exception e) {
			e.printStackTrace();
			result = ManagementConstant.REST_RESULT_FAIL;
			msg = e.getMessage();
			data = null;

			logger.error(msg, e);
		} finally {
			RestResultDto rst = new RestResultDto();
			rst.setResult(result);
			rst.setMsg(msg);
			rst.setData(data);
			return rst;
		}
	}

	/**
	 * 加载机构人员树，机构+部门+人员(根据人员权限)
	 * 
	 * @param isControlPermission
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "loadStaffTreeWithPermission" + ManagementConstant.PERMISSION_SUFFIX_READ, method = { RequestMethod.GET, RequestMethod.POST })
	public RestResultDto loadUserTreeWithPermission(HttpServletRequest request) throws Exception {
		String msg = null;
		Integer result = ManagementConstant.REST_RESULT_SUCC;
		Object data = null;
		JsonMapper mapper = new JsonMapper();
		LoginReturnInfoDto loginInfo = super.getLoginInfo(request);
		try {
			Map<String, String> paramMap = jm.fromJson(SpringmvcUtils.getParameter(ManagementConstant.REST_PMS), Map.class);

			String userId = loginInfo.getUserId();
			String tenantId = loginInfo.getTenantId();
			StaffOrganizationTreeWithPermission staffOrganizationTreeWithPermission = StaffOrganizationTreeWithPermission.getInstance();
			paramMap.put("tenantId", tenantId);
			paramMap.put("userId", userId);
			staffOrganizationTreeWithPermission.reloadDeptOrgStaffTree(paramMap);
			data = treeService.generateJsonCheckboxTree(staffOrganizationTreeWithPermission, false);

		} catch (Exception e) {
			e.printStackTrace();
			result = ManagementConstant.REST_RESULT_FAIL;
			msg = e.getMessage();
			data = null;

			logger.error(msg, e);
		} finally {
			RestResultDto rst = new RestResultDto();
			rst.setResult(result);
			rst.setMsg(msg);
			rst.setData(data);
			return rst;
		}
	}

	/**
	 * 加载机构人员树，机构+部门+人员(加载一个租户下的)(并且通过条件过滤人员)
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "loadTreeByFilter" + ManagementConstant.PERMISSION_SUFFIX_READ, method = { RequestMethod.GET, RequestMethod.POST })
	public RestResultDto loadTreeByFilter(HttpServletRequest request) throws Exception {
		String msg = null;
		Integer result = ManagementConstant.REST_RESULT_SUCC;
		Object data = null;
		JsonMapper mapper = new JsonMapper();
		try {
			Map<String, Object> paramMap = jm.fromJson(SpringmvcUtils.getParameter(ManagementConstant.REST_PMS), Map.class);
			if (MapUtils.isEmpty(paramMap)) {
				throw new VortexException("参数不能为空");
			}
			String tenantId = (String) paramMap.get(TENANT_ID);
			String companyId = (String) paramMap.get(COMPANY_ID);
			StaffOrganizationTree staffOrganizationTree = StaffOrganizationTree.getInstance();
			staffOrganizationTree.reloadDeptOrgStaffTreeByFilter(paramMap);
			data = treeService.generateJsonCheckboxTree(staffOrganizationTree, false);
		} catch (Exception e) {
			e.printStackTrace();
			result = ManagementConstant.REST_RESULT_FAIL;
			msg = e.getMessage();
			data = null;
			logger.error(msg, e);
		} finally {
			RestResultDto rst = new RestResultDto();
			rst.setResult(result);
			rst.setMsg(msg);
			rst.setData(data);
			return rst;
		}
	}

	/**
	 * 根据人员名称查询人员信息
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "findStaffByStaffName" + ManagementConstant.PERMISSION_SUFFIX_READ, method = { RequestMethod.GET, RequestMethod.POST })
	public RestResultDto findStaffByStaffName(HttpServletRequest request) throws Exception {
		String msg = null;
		Integer result = ManagementConstant.REST_RESULT_SUCC;
		Object data = null;
		JsonMapper mapper = new JsonMapper();
		try {
			Map<String, String> paramMap = jm.fromJson(SpringmvcUtils.getParameter(ManagementConstant.REST_PMS), Map.class);
			if (MapUtils.isEmpty(paramMap)) {
				throw new VortexException("参数不能为空");
			}
			String staffName = paramMap.get(STAFF_NAME);
			if (StringUtils.isEmpty(staffName)) {
				throw new VortexException("人员名不能为空");
			}
			String tenantId = paramMap.get(TENANT_ID);
			if (StringUtils.isEmpty(tenantId)) {
				throw new VortexException("租户id不能为空");
			}

			CloudStaffDto cloudStaffDto = managementRestService.getCloudStaffDtoByStaffName(staffName, tenantId);
			data = cloudStaffDto;
		} catch (Exception e) {
			e.printStackTrace();
			result = ManagementConstant.REST_RESULT_FAIL;
			msg = e.getMessage();
			data = null;
			logger.error(msg, e);
		} finally {
			RestResultDto rst = new RestResultDto();
			rst.setResult(result);
			rst.setMsg(msg);
			rst.setData(data);
			return rst;
		}
	}

	/**
	 * 根据人员名称查询人员信息
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "getStaffNamesByIds" + ManagementConstant.PERMISSION_SUFFIX_READ, method = { RequestMethod.GET, RequestMethod.POST })
	public RestResultDto getStaffNamesByIds() throws Exception {
		String msg = null;
		Integer result = ManagementConstant.REST_RESULT_SUCC;
		Object data = null;
		JsonMapper mapper = new JsonMapper();
		try {
			Map<String, Object> paramMap = jm.fromJson(SpringmvcUtils.getParameter(ManagementConstant.REST_PMS), Map.class);
			if (MapUtils.isEmpty(paramMap)) {
				throw new VortexException("参数不能为空");
			}
			List<String> ids = (List<String>) paramMap.get(IDS);
			Map<String, String> nameMap = null;
			if (CollectionUtils.isNotEmpty(ids)) {
				nameMap = cloudStaffService.getStaffNamesByIds(ids);
			} else {// 如果ids为空，那么返回一个空的map
				nameMap = Maps.newHashMap();
			}

			data = nameMap;
		} catch (Exception e) {
			e.printStackTrace();
			result = ManagementConstant.REST_RESULT_FAIL;
			msg = e.getMessage();
			data = null;
			logger.error(msg, e);
		} finally {
			RestResultDto rst = new RestResultDto();
			rst.setResult(result);
			rst.setMsg(msg);
			rst.setData(data);
			return rst;
		}
	}

	/**
	 * 根据人员名称查询人员信息
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "getStaffIdsByNames" + ManagementConstant.PERMISSION_SUFFIX_READ, method = { RequestMethod.GET, RequestMethod.POST })
	public RestResultDto getStaffIdsByNames() throws Exception {
		String msg = null;
		Integer result = ManagementConstant.REST_RESULT_SUCC;
		Object data = null;
		JsonMapper mapper = new JsonMapper();
		try {
			Map<String, Object> paramMap = jm.fromJson(SpringmvcUtils.getParameter(ManagementConstant.REST_PMS), Map.class);
			if (MapUtils.isEmpty(paramMap)) {
				throw new VortexException("参数不能为空");
			}
			String tenantId = (String) paramMap.get(TENANT_ID);
			if (StringUtils.isEmpty(tenantId)) {
				throw new VortexException("租户id不能为空");
			}
			List<String> names = (List<String>) paramMap.get(STAFF_NAMES);
			Map<String, String> nameMap = null;
			if (CollectionUtils.isNotEmpty(names)) {
				nameMap = cloudStaffService.getStaffIdsByNames(names, tenantId);
			} else {// 如果names为空，那么返回一个空的map
				nameMap = Maps.newHashMap();
			}

			data = nameMap;
		} catch (Exception e) {
			e.printStackTrace();
			result = ManagementConstant.REST_RESULT_FAIL;
			msg = e.getMessage();
			data = null;
			logger.error(msg, e);
		} finally {
			RestResultDto rst = new RestResultDto();
			rst.setResult(result);
			rst.setMsg(msg);
			rst.setData(data);
			return rst;
		}
	}

	/**
	 * 新增
	 * 
	 * @param dto
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "addStaff" + ManagementConstant.PERMISSION_SUFFIX_READ, method = RequestMethod.POST)
	public RestResultDto addStaff(HttpServletRequest request) throws Exception {
		String msg = null;
		Integer result = ManagementConstant.REST_RESULT_SUCC;
		Object data = null;
		JsonMapper mapper = new JsonMapper();
		try {
			CloudStaffDto dto = jm.fromJson(SpringmvcUtils.getParameter(ManagementConstant.REST_PMS), CloudStaffDto.class);
			if (dto == null) {
				throw new VortexException("参数不能为空");
			}
			LoginReturnInfoDto loginInfo = super.getLoginInfo(request);
			String tenantId = loginInfo.getTenantId();
			dto.setTenantId(tenantId);
			CloudStaff staff = cloudStaffService.save(dto);
			data = staff;
		} catch (Exception e) {
			e.printStackTrace();
			result = ManagementConstant.REST_RESULT_FAIL;
			msg = e.getMessage();
			data = null;
			logger.error(msg, e);
		} finally {
			RestResultDto rst = new RestResultDto();
			rst.setResult(result);
			rst.setMsg(msg);
			rst.setData(data);
			return rst;
		}
	}

	/**
	 * 更新
	 * 
	 * @param dto
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "updateStaff" + ManagementConstant.PERMISSION_SUFFIX_READ, method = RequestMethod.POST)
	public RestResultDto updateStaff(HttpServletRequest request) throws Exception {
		String msg = null;
		Integer result = ManagementConstant.REST_RESULT_SUCC;
		Object data = null;
		JsonMapper mapper = new JsonMapper();
		try {
			CloudStaffDto dto = jm.fromJson(SpringmvcUtils.getParameter(ManagementConstant.REST_PMS), CloudStaffDto.class);
			if (dto == null) {
				throw new VortexException("参数不能为空");
			}
			LoginReturnInfoDto loginInfo = super.getLoginInfo(request);
			String tenantId = loginInfo.getTenantId();
			dto.setTenantId(tenantId);
			cloudStaffService.update(dto);
			data = dto;
		} catch (Exception e) {
			e.printStackTrace();
			result = ManagementConstant.REST_RESULT_FAIL;
			msg = e.getMessage();
			data = null;
			logger.error(msg, e);
		} finally {
			RestResultDto rst = new RestResultDto();
			rst.setResult(result);
			rst.setMsg(msg);
			rst.setData(data);
			return rst;
		}
	}

	/**
	 * 删除
	 * 
	 * @param id
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "delete" + ManagementConstant.PERMISSION_SUFFIX_READ, method = RequestMethod.POST)
	public RestResultDto delete() throws Exception {
		String msg = null;
		Integer result = ManagementConstant.REST_RESULT_SUCC;
		Object data = null;
		JsonMapper mapper = new JsonMapper();
		try {
			Map<String, Object> paramMap = jm.fromJson(SpringmvcUtils.getParameter(ManagementConstant.REST_PMS), Map.class);
			if (MapUtils.isEmpty(paramMap)) {
				throw new VortexException("参数不能为空");
			}
			String id = (String) paramMap.get(ID);
			cloudStaffService.deleteStaffAndUser(id);
		} catch (Exception e) {
			e.printStackTrace();
			result = ManagementConstant.REST_RESULT_FAIL;
			msg = e.getMessage();
			data = null;
			logger.error(msg, e);
		} finally {
			RestResultDto rst = new RestResultDto();
			rst.setResult(result);
			rst.setMsg(msg);
			rst.setData(data);
			return rst;
		}
	}

}
