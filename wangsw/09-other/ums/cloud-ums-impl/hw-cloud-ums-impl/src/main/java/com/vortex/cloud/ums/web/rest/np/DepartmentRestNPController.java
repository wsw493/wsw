package com.vortex.cloud.ums.web.rest.np;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.vortex.cloud.ums.dataaccess.service.ICloudDepartmentService;
import com.vortex.cloud.ums.support.ManagementConstant;
import com.vortex.cloud.ums.tree.DepartmentTree;
import com.vortex.cloud.ums.util.tree.ITreeService;
import com.vortex.cloud.vfs.common.exception.VortexException;
import com.vortex.cloud.vfs.common.mapper.JsonMapper;
import com.vortex.cloud.vfs.common.web.springmvc.SpringmvcUtils;
import com.vortex.cloud.vfs.data.dto.RestResultDto;

/**
 * 部门rest服务
 * 
 * @author lusm
 *
 */
@SuppressWarnings("all")
@RestController
@RequestMapping("cloud/management/rest/np/department")
public class DepartmentRestNPController {
	private static final String DEPARTMENT_IDS = "ids";
	private static final String DEPARTMENT_ID = "id";
	private static final Object TENANT_ID = "tenantId";
	private static final String SYNCTIME = "syncTime";
	private static final String PAGESIZE = "pageSize";
	private static final String PAGENUMBER = "pageNumber";

	private JsonMapper jm = new JsonMapper();
	private Logger logger = LoggerFactory.getLogger(DepartmentRestNPController.class);
	@Resource
	private ICloudDepartmentService cloudDepartmentService;
	@Resource
	private ITreeService treeService;

	/**
	 * 根据ids获取对应的部门列表
	 * 
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings({ "unchecked", "finally" })
	@RequestMapping(value = "getdepartmentbyids" + ManagementConstant.PERMISSION_SUFFIX_READ, method = { RequestMethod.GET, RequestMethod.POST })
	public RestResultDto getDepartmentByIds(HttpServletRequest request) throws Exception {
		String msg = null;
		Integer result = ManagementConstant.REST_RESULT_SUCC;
		Object data = null;
		JsonMapper mapper = new JsonMapper();

		try {
			Map<String, Object> paramMap = jm.fromJson(SpringmvcUtils.getParameter(ManagementConstant.REST_PMS), Map.class);
			if (MapUtils.isEmpty(paramMap)) {
				throw new VortexException("参数不能为空");
			}

			List<String> ids = (List<String>) paramMap.get(DEPARTMENT_IDS);
			data = cloudDepartmentService.findDepartmentByIds(ids);
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
	 * 根据id获取对应的部门
	 * 
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings({ "unchecked", "finally" })
	@RequestMapping(value = "getdepartmentbyid" + ManagementConstant.PERMISSION_SUFFIX_READ, method = { RequestMethod.GET, RequestMethod.POST })
	public RestResultDto getDepartmentById(HttpServletRequest request) throws Exception {
		String msg = null;
		Integer result = ManagementConstant.REST_RESULT_SUCC;
		Object data = null;
		JsonMapper mapper = new JsonMapper();

		try {
			Map<String, String> paramMap = jm.fromJson(SpringmvcUtils.getParameter(ManagementConstant.REST_PMS), Map.class);
			if (MapUtils.isEmpty(paramMap)) {
				throw new VortexException("参数不能为空");
			}

			String id = paramMap.get(DEPARTMENT_ID);
			data = cloudDepartmentService.findOne(id);
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
	 * 用于树的加载、刷新
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "loadTree" + ManagementConstant.PERMISSION_SUFFIX_READ, method = { RequestMethod.GET, RequestMethod.POST })
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
			data = getTreeString(tenantId);
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
	 * 获取树的信息
	 * 
	 * @param tenantId
	 *            租户id
	 * @return
	 */
	private String getTreeString(String tenantId) {

		/*
		 * OrganizationTree organizationTree = OrganizationTree.getInstance();
		 * organizationTree.reloadDeptOrgTree(tenantId, null); String jsonStr =
		 * treeService.generateJsonCheckboxTree(organizationTree, false); return
		 * jsonStr;
		 */
		DepartmentTree departmentTree = DepartmentTree.getInstance();
		departmentTree.reloadDeptTree(tenantId, null);
		String jsonStr = treeService.generateJsonCheckboxTree(departmentTree, false);
		return jsonStr;
	}

	/**
	 * 根据租户id查找部门信息
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "finddepartmentbytenantid" + ManagementConstant.PERMISSION_SUFFIX_READ, method = { RequestMethod.GET, RequestMethod.POST })
	public RestResultDto findDepartmentByTenantId(HttpServletRequest request) throws Exception {
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
			data = cloudDepartmentService.findDeptOrgList(tenantId, null, null);
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
	 * 根据租户id同步部门信息
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "syncDeptByPage" + ManagementConstant.PERMISSION_SUFFIX_READ, method = { RequestMethod.GET, RequestMethod.POST })
	public RestResultDto syncDeptByPage(HttpServletRequest request) throws Exception {
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
				throw new VortexException("tenantId不能为空");
			}
			String strSyncTime = String.valueOf(paramMap.get(SYNCTIME));
			Long syncTime = Long.valueOf(strSyncTime);

			if (syncTime == null) {
				throw new VortexException("参数syncTime不能为空");
			}

			Integer pageSize = (Integer) paramMap.get(PAGESIZE);
			if (pageSize == null) {
				throw new VortexException("参数pageSize不能为空");
			}
			Integer pageNumber = (Integer) paramMap.get(PAGENUMBER);
			if (pageNumber == null) {
				throw new VortexException("参数pageNumber不能为空");
			}
			data = cloudDepartmentService.syncDeptByPage(tenantId, syncTime, pageSize, pageNumber);

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

	@RequestMapping(value = "findChildren", method = { RequestMethod.GET, RequestMethod.POST })
	public RestResultDto findChildren(HttpServletRequest request) {
		String msg = null;
		Integer result = ManagementConstant.REST_RESULT_SUCC;
		Object data = null;
		String exception = null;
		RestResultDto restResultDto = new RestResultDto();

		try {
			JsonMapper jm = new JsonMapper();
			Map<String, String> paramMap = jm.fromJson(SpringmvcUtils.getParameter(ManagementConstant.REST_PMS), Map.class);
			String tenantId = paramMap.get("tenantId");
			String id = paramMap.get("id");
			data = this.cloudDepartmentService.findChildren(tenantId, id);
			msg = "查询成功";
		} catch (Exception e) {
			e.printStackTrace();
			result = ManagementConstant.REST_RESULT_FAIL;
			msg = "查询失败！";
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
}
