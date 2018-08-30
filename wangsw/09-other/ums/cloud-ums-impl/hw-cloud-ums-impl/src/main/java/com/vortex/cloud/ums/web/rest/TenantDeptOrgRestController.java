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
import com.vortex.cloud.ums.dataaccess.service.ICloudDepartmentService;
import com.vortex.cloud.ums.dataaccess.service.ICloudOrganizationService;
import com.vortex.cloud.ums.dataaccess.service.IManagementRestService;
import  com.vortex.cloud.vfs.data.dto.LoginReturnInfoDto;
import com.vortex.cloud.ums.dto.TenantDeptOrgDto;
import com.vortex.cloud.ums.model.CloudDepartment;
import com.vortex.cloud.ums.model.CloudOrganization;
import com.vortex.cloud.ums.support.ManagementConstant;
import com.vortex.cloud.ums.tree.OrganizationTree;
import com.vortex.cloud.ums.tree.OrganizationTreeWithPermission;
import com.vortex.cloud.ums.util.web.BaseController;
import com.vortex.cloud.ums.util.tree.ITreeService;
import com.vortex.cloud.vfs.common.exception.VortexException;
import com.vortex.cloud.vfs.common.mapper.JsonMapper;
import com.vortex.cloud.vfs.common.web.springmvc.SpringmvcUtils;
import com.vortex.cloud.vfs.data.dto.RestResultDto;

/**
 * @author LiShijun
 * @date 2016年3月29日 下午3:50:13
 * @Description 租户单位组织机构维护对外提供的RESTful web service History <author> <time>
 *              <desc>
 */
@SuppressWarnings("all")
@RestController
@RequestMapping("cloud/management/rest/tenant/dept")
public class TenantDeptOrgRestController extends BaseController {
	private Logger logger = LoggerFactory.getLogger(TenantDeptOrgRestController.class);
	private JsonMapper jm = new JsonMapper();
	private static final String DEPARTMENT_CODE = "departmentCode";
	private static final String DEPARTMENT_ID = "departmentId";
	private static final String TENANT_ID = "tenantId";
	private static final String ORG_CODE = "orgCode";
	private static final String ORG_ID = "orgId";
	private static final String REQ_PARAM_DEPT_ID = "deptId";
	private static final String ID = "id"; // 机构或者是部门的id
	private static final String IDS = "ids"; // 机构或者是部门的id
	private static final String NAMES = "names";
	private static final Object IS_CONTROL_PERMISSION = "isControlPermission";

	@Resource
	private ITreeService treeService;

	@Resource
	private ICloudDepartmentService cloudDepartmentService;
	@Resource
	private IManagementRestService managementRestService;
	@Resource
	private ICloudOrganizationService cloudOrganizationService;

	/**
	 * 获取单位机构树
	 * 
	 * @param request
	 * @return
	 * @throws Exception
	 */

	@RequestMapping(value = "loadOrgTree", method = { RequestMethod.GET, RequestMethod.POST })
	public RestResultDto loadOrgTree(HttpServletRequest request) throws Exception {
		String msg = null;
		Integer result = null;
		Object data = null;
		String exception = "";
		RestResultDto restResultDto = new RestResultDto();

		try {
			LoginReturnInfoDto loginInfo = super.getLoginInfo(request);
			String tenantId = loginInfo.getTenantId();

			Map<String, String> paramMap = jm.fromJson(SpringmvcUtils.getParameter(ManagementConstant.REST_PMS), Map.class);
			// if (MapUtils.isEmpty(paramMap)) {
			// throw new VortexException("参数不能为空");
			// }

			String deptId = MapUtils.isEmpty(paramMap) ? null : paramMap.get(REQ_PARAM_DEPT_ID);
			// 注意：deptId不是必须的请求参数，如果没有传入，则取租户下所有单位的机构树

			if (logger.isDebugEnabled()) {
				logger.debug("loadOrgTree(), tenantId=" + tenantId + ",deptId=" + deptId);
			}

			OrganizationTree tree = OrganizationTree.getInstance();
			tree.reloadDeptOrgTree(tenantId, deptId);
			String jsonStr = treeService.generateJsonCheckboxTree(tree, false);

			result = RestResultDto.RESULT_SUCC;
			msg = "成功构造树";
			data = jsonStr;
		} catch (Exception e) {
			e.printStackTrace();
			result = RestResultDto.RESULT_FAIL;
			msg = e.getMessage();
			data = null;
			exception = e.getMessage();
			logger.error(msg, e);
		} finally {
			restResultDto.setResult(result);
			restResultDto.setMsg(msg);
			restResultDto.setData(data);
			restResultDto.setException(exception);

		}
		return restResultDto;
	}

	/**
	 * 获取单位机构树(根据当前人的权限范围来获取) <br>
	 * 不传控制权限就不做权限控制，查询该租户下的单位机构树
	 * 
	 * @param request
	 * @return
	 * @throws Exception
	 */

	@RequestMapping(value = "loadOrgTreeByPermission", method = { RequestMethod.GET, RequestMethod.POST })
	public RestResultDto loadOrgTreeByPermission(HttpServletRequest request) throws Exception {
		String msg = null;
		Integer result = null;
		Object data = null;
		String exception = "";
		RestResultDto restResultDto = new RestResultDto();

		try {
			LoginReturnInfoDto loginInfo = super.getLoginInfo(request);
			String tenantId = loginInfo.getTenantId();
			String userId = loginInfo.getUserId();

			Map<String, String> paramMap = jm.fromJson(SpringmvcUtils.getParameter(ManagementConstant.REST_PMS), Map.class);

			if (logger.isDebugEnabled()) {
				logger.debug("loadOrgTree(), tenantId=" + tenantId);
			}
			Map<String, String> param = Maps.newHashMap();
			param.put("userId", userId);
			param.put("tenantId", tenantId);
			if (MapUtils.isNotEmpty(paramMap)) {
				param.put("isControlPermission", paramMap.get(IS_CONTROL_PERMISSION));
			}

			OrganizationTreeWithPermission tree = OrganizationTreeWithPermission.getInstance();
			tree.reloadDeptOrgTree(param);
			String jsonStr = treeService.generateJsonCheckboxTree(tree, false);

			result = RestResultDto.RESULT_SUCC;
			msg = "成功构造树";
			data = jsonStr;
		} catch (Exception e) {
			e.printStackTrace();
			result = RestResultDto.RESULT_FAIL;
			msg = e.getMessage();
			data = null;
			exception = e.getMessage();
			logger.error(msg, e);
		} finally {
			restResultDto.setResult(result);
			restResultDto.setMsg(msg);
			restResultDto.setData(data);
			restResultDto.setException(exception);

		}
		return restResultDto;
	}

	/**
	 * 获取单位机构列表
	 * 
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "findOrgList", method = { RequestMethod.GET, RequestMethod.POST })
	public RestResultDto findOrgList(HttpServletRequest request) throws Exception {
		String msg = null;
		Integer result = null;
		Object data = null;
		String exception = "";
		RestResultDto restResultDto = new RestResultDto();

		try {
			LoginReturnInfoDto loginInfo = super.getLoginInfo(request);
			String tenantId = loginInfo.getTenantId();

			Map<String, String> paramMap = jm.fromJson(SpringmvcUtils.getParameter(ManagementConstant.REST_PMS), Map.class);
			// if (MapUtils.isEmpty(paramMap)) {
			// throw new VortexException("参数不能为空");
			// }

			String deptId = MapUtils.isEmpty(paramMap) ? null : paramMap.get(REQ_PARAM_DEPT_ID);
			// 注意：deptId不是必须的请求参数，如果没有传入，则取租户下所有单位的机构树

			if (logger.isDebugEnabled()) {
				logger.debug("findOrgList(), tenantId=" + tenantId + ",deptId=" + deptId);
			}

			List<TenantDeptOrgDto> list = cloudDepartmentService.findDeptOrgList(tenantId, deptId, null);

			result = RestResultDto.RESULT_SUCC;
			msg = "成功获取列表";
			data = list;
		} catch (Exception e) {
			e.printStackTrace();
			result = RestResultDto.RESULT_FAIL;
			msg = e.getMessage();
			data = null;
			exception = e.getMessage();
			logger.error(msg, e);
		} finally {
			restResultDto.setResult(result);
			restResultDto.setMsg(msg);
			restResultDto.setData(data);
			restResultDto.setException(exception);
		}
		return restResultDto;
	}

	/**
	 * 根据机构code和租户code，得到机构信息
	 * 
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "getorganizationbycode" + ManagementConstant.PERMISSION_SUFFIX_READ, method = { RequestMethod.GET, RequestMethod.POST })
	public RestResultDto getOrganizationByCode(HttpServletRequest request) throws Exception {
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

			String orgCode = paramMap.get(ORG_CODE);
			if (StringUtils.isBlank(orgCode)) {
				throw new VortexException("请传入参数：" + ORG_CODE);
			}

			logger.debug("getOrganizationByCode(), orgCode=" + orgCode + "tenantId=" + tenantId);
			CloudOrganization cloudOrganization = managementRestService.getOrganizationByCode(orgCode, tenantId);
			result = ManagementConstant.REST_RESULT_SUCC;
			msg = "成功获取机构信息";
			data = cloudOrganization;
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
	 * 根据角色id，得到角色信息
	 * 
	 * 
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "getorganizationbyid" + ManagementConstant.PERMISSION_SUFFIX_READ, method = { RequestMethod.GET, RequestMethod.POST })
	public RestResultDto getOrganizationById(HttpServletRequest request) throws Exception {
		String msg = null;
		Integer result = null;
		Object data = null;

		try {
			Map<String, String> paramMap = jm.fromJson(SpringmvcUtils.getParameter(ManagementConstant.REST_PMS), Map.class);
			if (MapUtils.isEmpty(paramMap)) {
				throw new VortexException("参数不能为空");
			}

			String orgId = paramMap.get(ORG_ID);
			if (StringUtils.isBlank(orgId)) {
				throw new VortexException("请传入参数：" + ORG_ID);
			}

			if (logger.isDebugEnabled()) {
				logger.debug("getOrganizationById(), orgId=" + orgId);
			}
			CloudOrganization cloudOrganization = managementRestService.getOrganizationById(orgId);
			result = ManagementConstant.REST_RESULT_SUCC;
			msg = "成功获取机构信息";
			data = cloudOrganization;
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
	 * 根据部门code和租户id，得到部门信息
	 * 
	 * 
	 * 
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "getdepartmentbycode" + ManagementConstant.PERMISSION_SUFFIX_READ, method = { RequestMethod.GET, RequestMethod.POST })
	public RestResultDto getDepartmentByCode(HttpServletRequest request) throws Exception {
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

			String departmentCode = paramMap.get(DEPARTMENT_CODE);
			if (StringUtils.isBlank(departmentCode)) {
				throw new VortexException("请传入参数：" + DEPARTMENT_CODE);
			}

			if (logger.isDebugEnabled()) {
				logger.debug("getDepartmentByCode(), tenantId=" + tenantId + ",departmentCode=" + departmentCode);
			}
			CloudDepartment cloudDepartment = managementRestService.getDepartmentByCode(departmentCode, tenantId);
			result = ManagementConstant.REST_RESULT_SUCC;
			msg = "成功获取部门信息";
			data = cloudDepartment;
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
	 * 根据部门id，得到部门信息
	 * 
	 * 
	 * 
	 * 
	 * 
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "getdepartmentbyid" + ManagementConstant.PERMISSION_SUFFIX_READ, method = { RequestMethod.GET, RequestMethod.POST })
	public RestResultDto getDepartmentById(HttpServletRequest request) throws Exception {
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
				logger.debug("getDepartmentById(), departmentId=" + departmentId);
			}
			CloudDepartment cloudDepartment = managementRestService.getDepartmentById(departmentId);
			result = ManagementConstant.REST_RESULT_SUCC;
			msg = "成功获取部门信息";
			data = cloudDepartment;
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
	 * 根据部门id，得到部门信息
	 * 
	 * 
	 * 
	 * 
	 * 
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "getDepartOrOrgById" + ManagementConstant.PERMISSION_SUFFIX_READ, method = { RequestMethod.GET, RequestMethod.POST })
	public RestResultDto getDepartOrOrgById(HttpServletRequest request) throws Exception {
		String msg = null;
		Integer result = null;
		Object data = null;

		try {
			Map<String, String> paramMap = jm.fromJson(SpringmvcUtils.getParameter(ManagementConstant.REST_PMS), Map.class);
			if (MapUtils.isEmpty(paramMap)) {
				throw new VortexException("参数不能为空");
			}

			String id = paramMap.get(ID);
			if (StringUtils.isBlank(id)) {
				throw new VortexException("请传入参数：" + ID);
			}

			if (logger.isDebugEnabled()) {
				logger.debug("getDepartOrOrgById(), id=" + id);
			}
			Map<String, Object> map = cloudOrganizationService.getDepartmentOrOrgNameById(id);
			result = ManagementConstant.REST_RESULT_SUCC;
			msg = "成功获取部门信息";
			data = map;
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
	 * 根据部门ids，得到部门名称
	 * 
	 * 结果是id：name的map集合
	 * 
	 * 
	 * 
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "getDepartsOrOrgNamesByIds" + ManagementConstant.PERMISSION_SUFFIX_READ, method = { RequestMethod.GET, RequestMethod.POST })
	public RestResultDto getDepartsOrOrgsByIds(HttpServletRequest request) throws Exception {
		String msg = null;
		Integer result = null;
		Object data = null;

		try {
			Map<String, Object> paramMap = jm.fromJson(SpringmvcUtils.getParameter(ManagementConstant.REST_PMS), Map.class);
			if (MapUtils.isEmpty(paramMap)) {
				throw new VortexException("参数不能为空");
			}

			List<String> idList = (List<String>) paramMap.get(IDS);
			String[] ids = idList.toArray(new String[idList.size()]);
			// List<String> idList = Arrays.asList(ids);
			if (CollectionUtils.isEmpty(idList)) {
				throw new VortexException("请传入参数：" + IDS);
			}

			Map<String, String> map = cloudOrganizationService.getDepartmentsOrOrgNamesByIds(ids);
			result = ManagementConstant.REST_RESULT_SUCC;
			msg = "成功获取部门信息";
			data = map;
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
	 * 查找租户下的部门和机构,以List<map<String ,String >>返回（[{text:xx,id:xx},]）
	 * 
	 * 
	 * 
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "loadDepartments" + ManagementConstant.PERMISSION_SUFFIX_READ, method = { RequestMethod.GET, RequestMethod.POST })
	public RestResultDto loadDepartments(HttpServletRequest request) throws Exception {
		String msg = null;
		Integer result = null;
		Object data = null;

		try {
			Map<String, Object> paramMap = jm.fromJson(SpringmvcUtils.getParameter(ManagementConstant.REST_PMS), Map.class);
			if (MapUtils.isEmpty(paramMap)) {
				throw new VortexException("参数不能为空");
			}

			String tenantId = (String) paramMap.get(TENANT_ID);
			if (StringUtils.isEmpty(tenantId)) {
				logger.error("请传入参数：" + TENANT_ID);
				throw new VortexException("请传入参数：" + TENANT_ID);
			}
			List<Map<String, String>> selectMapList = cloudOrganizationService.getDepartmentsOrOrgByCondiction(paramMap);

			result = ManagementConstant.REST_RESULT_SUCC;
			msg = "成功获取部门信息";
			data = selectMapList;
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
	 * 根据名称查找部门id，结果是name：ID的map集合
	 * 
	 * 
	 * 
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "loadDepartOrOrgIDsByNames" + ManagementConstant.PERMISSION_SUFFIX_READ, method = { RequestMethod.GET, RequestMethod.POST })
	public RestResultDto loadDepartOrOrgIDsByNames(HttpServletRequest request) throws Exception {
		String msg = null;
		Integer result = null;
		Object data = null;

		try {
			Map<String, Object> paramMap = jm.fromJson(SpringmvcUtils.getParameter(ManagementConstant.REST_PMS), Map.class);
			if (MapUtils.isEmpty(paramMap)) {
				throw new VortexException("参数不能为空");
			}

			String tenantId = (String) paramMap.get(TENANT_ID);
			if (StringUtils.isEmpty(tenantId)) {
				logger.error("请传入参数：" + TENANT_ID);
				throw new VortexException("请传入参数：" + TENANT_ID);
			}
			List<String> names = (List<String>) paramMap.get(NAMES);

			Map<String, String> nameIdMap = cloudOrganizationService.getDepartmentsOrOrgIdsByName(names, tenantId);

			result = ManagementConstant.REST_RESULT_SUCC;
			msg = "成功获取部门公司id信息";
			data = nameIdMap;
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
	 * 根据人员权限获取部门列表
	 * 
	 * @param isControlPermission
	 *            是否带权限
	 * 
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "loadDepartmentsWithPermission" + ManagementConstant.PERMISSION_SUFFIX_READ, method = { RequestMethod.GET, RequestMethod.POST })
	public RestResultDto loadDepartmentsWithPermission(HttpServletRequest request) throws Exception {
		String msg = null;
		Integer result = null;
		Object data = null;

		try {
			Map<String, Object> paramMap = jm.fromJson(SpringmvcUtils.getParameter(ManagementConstant.REST_PMS), Map.class);
			LoginReturnInfoDto loginInfo = super.getLoginInfo(request);
			String userId = loginInfo.getUserId();
			String tenantId = loginInfo.getTenantId();
			paramMap.put("userId", userId);
			paramMap.put("tenantId", tenantId);

			List<TenantDeptOrgDto> companys = cloudOrganizationService.loadDepartmentsWithPermission(paramMap);

			result = ManagementConstant.REST_RESULT_SUCC;
			msg = "成功获取部门信息";
			data = companys;
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
}
