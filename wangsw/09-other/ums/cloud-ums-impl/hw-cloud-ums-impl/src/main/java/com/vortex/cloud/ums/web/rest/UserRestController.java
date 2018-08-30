package com.vortex.cloud.ums.web.rest;

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

import com.vortex.cloud.ums.dataaccess.service.ICloudUserService;
import com.vortex.cloud.ums.dataaccess.service.IManagementRestService;
import com.vortex.cloud.ums.dto.CloudUserDto;
import  com.vortex.cloud.vfs.data.dto.LoginReturnInfoDto;
import com.vortex.cloud.ums.model.CloudUser;
import com.vortex.cloud.ums.support.ManagementConstant;
import com.vortex.cloud.ums.tree.UserOrganizationTree;
import com.vortex.cloud.ums.tree.UserOrganizationTreeWithPermission;
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
@RequestMapping("cloud/management/rest/user")
public class UserRestController extends BaseController {
	private static final Object STAFF_ID = "staffId";
	private static final Object USER_ID = "userId";
	private static final Object USERNAME = "userName";
	private static final String OLD_PASSWORD = "oldPassword";
	private static final String NEW_PASSWORD = "newPassword";
	private static final Object TENANT_ID = "tenantId";
	private static final Object IS_CONTROL_PERMISSION = "isControlPermission";
	private Logger logger = LoggerFactory.getLogger(UserRestController.class);
	private JsonMapper jm = new JsonMapper();
	@Resource
	private IManagementRestService managementRestService;
	@Resource
	private ICloudUserService cloudUserService;
	@Resource
	private ITreeService treeService;

	/**
	 * 根据人员id，得到人员基本信息
	 * 
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "getuserbyid" + ManagementConstant.PERMISSION_SUFFIX_READ, method = { RequestMethod.GET, RequestMethod.POST })
	public RestResultDto getUserById(HttpServletRequest request) throws Exception {
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
				logger.debug("getUserById(), userId=" + userId);
			}
			CloudUser cloudUser = managementRestService.getUserById(userId);
			result = ManagementConstant.REST_RESULT_SUCC;
			msg = "成功获取用户信息";
			data = cloudUser;
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
	 * 根据人员id，得到人员基本信息
	 * 
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "getUserAndStaffById" , method = { RequestMethod.GET, RequestMethod.POST })
	public RestResultDto getUserAndStaffById(HttpServletRequest request) throws Exception {
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
				logger.debug("getUserById(), userId=" + userId);
			}
			CloudUserDto cloudUser = cloudUserService.getById(userId);
			result = ManagementConstant.REST_RESULT_SUCC;
			msg = "成功获取用户信息";
			data = cloudUser;
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
	 * 根据人员staffid，得到人员基本信息
	 * 
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "getuserbystaffid" + ManagementConstant.PERMISSION_SUFFIX_READ, method = { RequestMethod.GET, RequestMethod.POST })
	public RestResultDto getUserByStaffId(HttpServletRequest request) throws Exception {
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
				logger.debug("getUserByStaffId(), staffId=" + staffId);
			}
			CloudUser cloudUser = managementRestService.getUserByStaffId(staffId);
			result = ManagementConstant.REST_RESULT_SUCC;
			if (cloudUser != null) {
				msg = "成功获取用户信息";
			} else {
				msg = "未获取到用户信息";
			}

			data = cloudUser;
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
	 * 根据人员用户名，得到人员基本信息
	 * 
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "getuserbyusername" + ManagementConstant.PERMISSION_SUFFIX_READ, method = { RequestMethod.GET, RequestMethod.POST })
	public RestResultDto getUserByUserName(HttpServletRequest request) throws Exception {
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

			String userName = paramMap.get(USERNAME);
			if (StringUtils.isBlank(userName)) {
				throw new VortexException("请传入参数：" + USERNAME);
			}

			logger.debug("getUserByStaffId(), userName=" + userName + "tenantId=" + tenantId);
			CloudUser cloudUser = managementRestService.getUserByUserName(tenantId, userName);
			result = ManagementConstant.REST_RESULT_SUCC;
			msg = "成功获取用户信息";
			data = cloudUser;
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
	 * 修改密码 description: 传入的参数拼接在url后面
	 * 
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "changepassword" + ManagementConstant.PERMISSION_SUFFIX_READ, method = { RequestMethod.GET, RequestMethod.POST })
	public RestResultDto changePassword(HttpServletRequest request) throws Exception {
		String msg = null;
		Integer result = null;
		Object data = null;

		try {
			LoginReturnInfoDto loginInfo = super.getLoginInfo(request);
			String userId = loginInfo.getUserId();

			String oldPwd = request.getParameter(OLD_PASSWORD);
			String newPwd = request.getParameter(NEW_PASSWORD);

			cloudUserService.changePassword(userId, oldPwd, newPwd);
			msg = "修改成功！";
			result = ManagementConstant.REST_RESULT_SUCC;
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
	 * 修改密码 description: 传入的参数为json
	 * 
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "changepasswordbyjson" + ManagementConstant.PERMISSION_SUFFIX_READ, method = { RequestMethod.GET, RequestMethod.POST })
	public RestResultDto changepasswordbyjson(HttpServletRequest request) throws Exception {
		String msg = null;
		Integer result = null;
		Object data = null;

		try {
			LoginReturnInfoDto loginInfo = super.getLoginInfo(request);
			String userId = loginInfo.getUserId();

			Map<String, String> paramMap = jm.fromJson(SpringmvcUtils.getParameter(ManagementConstant.REST_PMS), Map.class);
			if (MapUtils.isEmpty(paramMap)) {
				throw new VortexException("参数不能为空");
			}
			String oldPwd = paramMap.get(OLD_PASSWORD);
			String newPwd = paramMap.get(NEW_PASSWORD);

			cloudUserService.changePassword(userId, oldPwd, newPwd);
			msg = "修改成功！";
			result = ManagementConstant.REST_RESULT_SUCC;
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
	 * 根据条件获取用户列表
	 * 
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "getusersbycondiction" + ManagementConstant.PERMISSION_SUFFIX_READ, method = { RequestMethod.GET, RequestMethod.POST })
	public RestResultDto getUsersByCondiction(HttpServletRequest request) throws Exception {

		String msg = null;
		Integer result = null;
		Object data = null;

		try {

			Map<String, String> paramMap = jm.fromJson(SpringmvcUtils.getParameter(ManagementConstant.REST_PMS), Map.class);
			if (MapUtils.isEmpty(paramMap)) {
				throw new VortexException("参数不能为空");
			}
			String tenantId = paramMap.get(TENANT_ID);
			if (StringUtils.isEmpty(tenantId)) {
				throw new VortexException("tenantId不能为空");
			}
			List<CloudUserDto> users = cloudUserService.getUsersByCondiction(paramMap);
			data = users;
			msg = "获取租户下人员列表成功！";
			result = ManagementConstant.REST_RESULT_SUCC;
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
	 * 加载机构用户树，机构+部门+人员(加载一个租户下的)
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "loadUserTree" + ManagementConstant.PERMISSION_SUFFIX_READ, method = { RequestMethod.GET, RequestMethod.POST })
	public RestResultDto loadTree(HttpServletRequest request) throws Exception {
		String msg = null;
		Integer result = ManagementConstant.REST_RESULT_SUCC;
		Object data = null;
		JsonMapper mapper = new JsonMapper();
		LoginReturnInfoDto loginInfo = super.getLoginInfo(request);
		try {
			Map<String, String> paramMap = jm.fromJson(SpringmvcUtils.getParameter(ManagementConstant.REST_PMS), Map.class);
			if (MapUtils.isEmpty(paramMap)) {
				throw new VortexException("参数不能为空");
			}
			String tenantId = paramMap.get(TENANT_ID);
			UserOrganizationTree userOrganizationTree = UserOrganizationTree.getInstance();
			userOrganizationTree.reloadDeptOrgUserTree(tenantId, null);
			data = treeService.generateJsonCheckboxTree(userOrganizationTree, false);
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
	 * 加载机构用户树，机构+部门+人员(加载一个租户下的)
	 * 
	 * @param isControlPermission
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "loadUserTreeWithPermission" + ManagementConstant.PERMISSION_SUFFIX_READ, method = { RequestMethod.GET, RequestMethod.POST })
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
			UserOrganizationTreeWithPermission userOrganizationTree = UserOrganizationTreeWithPermission.getInstance();
			paramMap.put("tenantId", tenantId);
			paramMap.put("userId", userId);
			userOrganizationTree.reloadDeptOrgStaffTree(paramMap);
			data = treeService.generateJsonCheckboxTree(userOrganizationTree, false);

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
