package com.vortex.cloud.ums.web.rest.np;

import java.net.URLDecoder;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.vortex.cloud.ums.dataaccess.service.ICloudUserService;
import com.vortex.cloud.ums.dataaccess.service.ILoginService;
import com.vortex.cloud.ums.dto.CloudStaffSearchDto;
import com.vortex.cloud.ums.dto.CloudUserDto;
import com.vortex.cloud.ums.dto.UserDto;
import com.vortex.cloud.ums.model.CloudUser;
import com.vortex.cloud.ums.support.ManagementConstant;
import com.vortex.cloud.ums.tree.UserOrganizationTreeWithPermission;
import com.vortex.cloud.ums.util.support.Constants;
import com.vortex.cloud.ums.util.tree.ITreeService;
import com.vortex.cloud.vfs.common.digest.MD5;
import com.vortex.cloud.vfs.common.exception.VortexException;
import com.vortex.cloud.vfs.common.mapper.JsonMapper;
import com.vortex.cloud.vfs.common.web.springmvc.SpringmvcUtils;
import com.vortex.cloud.vfs.data.dto.DataStore;
import com.vortex.cloud.vfs.data.dto.RestResultDto;

@SuppressWarnings("all")
@RequestMapping("cloud/management/rest/np/user")
@RestController
public class UserRestNpController {
	private Logger logger = LoggerFactory.getLogger(UserRestNpController.class);
	private JsonMapper jm = new JsonMapper();
	private static final String TEANANT_CODE = "tenantCode";
	private static final String SYSTEM_CODE = "systemCode";
	private static final String PASSWORD = "password";
	private static final Object USER_NAME = "userName";
	private static final Object IM_TOKEN = "imToken";
	private static final String IP = "ip";
	private static final String PAGE = "page";
	private static final String ROWS = "rows";
	private static final String NAME = "name";
	private static final Object TENANT_ID = "tenantId";
	private static final Object USER_ID = "userId";
	private static final Object STAFF_ID = "staffId";
	private static final Object FILENAME = "fileName";
	private static final String OLD_PASSWORD = "oldPassword";
	private static final String NEW_PASSWORD = "newPassword";
	// 手机端推送id
	private static final String MOBILE_PUSH_MSG_ID = "regId";
	private static final String RONG_LIAN_ACCOUNT = "rongLianAccount";
	private static final Object IS_CONTROL_PERMISSION = "isControlPermission";
	@Resource
	private ICloudUserService cloudUserService;
	@Resource
	private ILoginService loginService;
	@Resource
	private ITreeService treeService;

	/**
	 * 根据人员用户名，得到人员基本信息
	 * 
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "getuserbyusername" + ManagementConstant.PERMISSION_SUFFIX_READ, method = { RequestMethod.GET, RequestMethod.POST })
	public RestResultDto getUserByUserName(HttpServletRequest request) throws Exception {
		String msg = null;
		Integer result = null;
		Object data = null;
		String exception = null;
		RestResultDto restResultDto = new RestResultDto();

		try {
			Map<String, String> paramMap = jm.fromJson(SpringmvcUtils.getParameter(ManagementConstant.REST_PMS), Map.class);
			if (MapUtils.isEmpty(paramMap)) {
				throw new VortexException("参数不能为空");
			}

			String userName = paramMap.get(USER_NAME);
			String tenantCode = paramMap.get(TEANANT_CODE);
			if (StringUtils.isBlank(userName)) {
				throw new VortexException("请传入参数：" + USER_NAME);
			}

			if (StringUtils.isBlank(tenantCode)) {
				throw new VortexException("请传入参数：" + TEANANT_CODE);
			}

			// 查询人员信息
			data = cloudUserService.getUserByUserNameAndTenantCode(userName, tenantCode);

			if (data != null) {
				msg = "成功获取用户信息";
				result = ManagementConstant.REST_RESULT_SUCC;
			} else {
				msg = "未获取到用户信息";
				result = ManagementConstant.REST_RESULT_FAIL;
			}
		} catch (Exception e) {
			e.printStackTrace();
			result = ManagementConstant.REST_RESULT_FAIL;
			msg = "获取用户信息出错";
			data = null;
			exception = e.getMessage();
			logger.error(msg, e);
		} finally {
			restResultDto.setData(data);
			restResultDto.setMsg(msg);
			restResultDto.setException(exception);
			restResultDto.setResult(result);
		}

		return restResultDto;
	}

	/**
	 * 登录并且更新推送id
	 * 
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "login" + ManagementConstant.PERMISSION_SUFFIX_READ, method = { RequestMethod.GET, RequestMethod.POST })
	public RestResultDto loginAndSavePushID(HttpServletRequest request) throws Exception {
		String msg = null;
		Integer result = null;
		Object data = null;
		String exception = null;
		RestResultDto restResultDto = new RestResultDto();
		try {

			Map<String, String> paramMap = jm.fromJson(SpringmvcUtils.getParameter(ManagementConstant.REST_PMS), Map.class);
			if (MapUtils.isEmpty(paramMap)) {
				throw new VortexException("参数不能为空");
			}
			// 登录
			data = loginService.login(paramMap.get(TEANANT_CODE), paramMap.get(SYSTEM_CODE), paramMap.get(USER_NAME), MD5.getMD5(paramMap.get(PASSWORD)), paramMap.get(MOBILE_PUSH_MSG_ID),
					paramMap.get(IP));

			if (data != null) {
				msg = "成功获取用户信息";
				result = ManagementConstant.REST_RESULT_SUCC;
			} else {
				msg = "未获取到用户信息";
				result = ManagementConstant.REST_RESULT_FAIL;
			}
		} catch (Exception e) {
			e.printStackTrace();
			result = ManagementConstant.REST_RESULT_FAIL;
			msg = "获取用户信息出错";
			data = null;
			exception = e.getMessage();
			logger.error(msg, e);
		} finally {
			restResultDto.setData(data);
			restResultDto.setMsg(msg);
			restResultDto.setException(exception);
			restResultDto.setResult(result);
		}

		return restResultDto;
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "loginbystaff" + ManagementConstant.PERMISSION_SUFFIX_READ, method = { RequestMethod.GET, RequestMethod.POST })
	public RestResultDto loginByStaff(HttpServletRequest request) throws Exception {
		String msg = null;
		Integer result = null;
		Object data = null;
		String exception = null;
		RestResultDto restResultDto = new RestResultDto();
		try {

			Map<String, String> paramMap = jm.fromJson(SpringmvcUtils.getParameter(ManagementConstant.REST_PMS), Map.class);
			if (MapUtils.isEmpty(paramMap)) {
				throw new VortexException("参数不能为空");
			}

			CloudUser user = cloudUserService.getUserByStaffId(paramMap.get(STAFF_ID));
			if (user != null) {
				// 登录
				data = loginService.login(paramMap.get(TEANANT_CODE), paramMap.get(SYSTEM_CODE), user.getUserName(), user.getPassword(), paramMap.get(MOBILE_PUSH_MSG_ID), paramMap.get(IP));

				if (data != null) {
					msg = "成功获取用户信息";
					result = ManagementConstant.REST_RESULT_SUCC;
				} else {
					msg = "未获取到用户信息";
					result = ManagementConstant.REST_RESULT_FAIL;
				}
			} else {
				msg = "未获取到用户信息";
				result = ManagementConstant.REST_RESULT_FAIL;
			}

		} catch (Exception e) {
			e.printStackTrace();
			result = ManagementConstant.REST_RESULT_FAIL;
			msg = "获取用户信息出错";
			data = null;
			exception = e.getMessage();
			logger.error(msg, e);
		} finally {
			restResultDto.setData(data);
			restResultDto.setMsg(msg);
			restResultDto.setException(exception);
			restResultDto.setResult(result);
		}

		return restResultDto;
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "updateImToken" + ManagementConstant.PERMISSION_SUFFIX_READ, method = { RequestMethod.GET, RequestMethod.POST })
	public RestResultDto updateImToken(HttpServletRequest request) throws Exception {
		String msg = null;
		Integer result = null;
		String exception = null;
		RestResultDto restResultDto = new RestResultDto();
		try {
			Map<String, String> paramMap = jm.fromJson(request.getParameter(ManagementConstant.REST_PMS), Map.class);
			if (MapUtils.isEmpty(paramMap)) {
				throw new VortexException("参数不能为空");
			}
			URLDecoder decode = new URLDecoder();

			String token = decode.decode(paramMap.get(IM_TOKEN), "utf-8");

			cloudUserService.updateImToken(paramMap.get(USER_ID), paramMap.get(USER_NAME), token);
			result = ManagementConstant.REST_RESULT_SUCC;
			msg = "更新用户融云token信息成功";
		} catch (Exception e) {
			e.printStackTrace();
			result = ManagementConstant.REST_RESULT_FAIL;
			msg = "更新用户融云token信息出错";
			exception = e.getMessage();
			logger.error(msg, e);
		} finally {
			restResultDto.setData(null);
			restResultDto.setMsg(msg);
			restResultDto.setException(exception);
			restResultDto.setResult(result);
		}

		return restResultDto;
	}

	/**
	 * 上传用户头像,返回头像的全路径
	 * 
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "uploadUserPhoto" + ManagementConstant.PERMISSION_SUFFIX_EDIT, method = { RequestMethod.GET, RequestMethod.POST })
	public RestResultDto uploadUserPhoto(HttpServletRequest request) throws Exception {
		String msg = null;
		Integer result = null;
		Object data = null;
		String exception = null;
		RestResultDto restResultDto = new RestResultDto();
		try {
			Map<String, String> paramMap = jm.fromJson(SpringmvcUtils.getParameter(ManagementConstant.REST_PMS), Map.class);
			if (MapUtils.isEmpty(paramMap)) {
				throw new VortexException("参数不能为空");
			}
			String userId = paramMap.get(USER_ID);
			String fileName = paramMap.get(FILENAME);
			String imgStr = paramMap.get("imgStr");
			data = cloudUserService.uploadPhoto(userId, fileName, imgStr);

			if (data != null) {
				msg = "成功上传头像";
				result = ManagementConstant.REST_RESULT_SUCC;
			} else {
				msg = "上传头像失败";
				result = ManagementConstant.REST_RESULT_FAIL;
			}
		} catch (Exception e) {
			e.printStackTrace();
			result = ManagementConstant.REST_RESULT_FAIL;
			msg = "上传头像失败";
			data = null;
			exception = e.getMessage();
			logger.error(msg, e);
		} finally {
			restResultDto.setData(data);
			restResultDto.setMsg(msg);
			restResultDto.setException(exception);
			restResultDto.setResult(result);
		}

		return restResultDto;
	}

	/**
	 * 使用request获取参数。上传用户头像,返回头像的id
	 * 
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "uploadUserPhoto2" + ManagementConstant.PERMISSION_SUFFIX_EDIT, method = { RequestMethod.GET, RequestMethod.POST })
	public RestResultDto uploadUserPhoto2(HttpServletRequest request) throws Exception {
		String msg = null;
		Integer result = null;
		Object data = null;
		String exception = null;
		RestResultDto restResultDto = new RestResultDto();
		try {
			Map<String, String> paramMap = jm.fromJson(request.getParameter(ManagementConstant.REST_PMS), Map.class);
			if (MapUtils.isEmpty(paramMap)) {
				throw new VortexException("参数不能为空");
			}
			String userId = paramMap.get(USER_ID);
			String fileName = paramMap.get(FILENAME);
			String imgStr = paramMap.get("imgStr");
			data = cloudUserService.uploadPhoto(userId, fileName, imgStr);

			if (data != null) {
				msg = "成功上传头像";
				result = ManagementConstant.REST_RESULT_SUCC;
			} else {
				msg = "上传头像失败";
				result = ManagementConstant.REST_RESULT_FAIL;
			}
		} catch (Exception e) {
			e.printStackTrace();
			result = ManagementConstant.REST_RESULT_FAIL;
			msg = "上传头像失败";
			data = null;
			exception = e.getMessage();
			logger.error(msg, e);
		} finally {
			restResultDto.setData(data);
			restResultDto.setMsg(msg);
			restResultDto.setException(exception);
			restResultDto.setResult(result);
		}

		return restResultDto;
	}

	/**
	 * 修改密码 description: 传入的参数为json
	 * 
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "changepasswordbyjson" + Constants.BACK_DYNAMIC_SUFFIX, method = { RequestMethod.GET, RequestMethod.POST })
	public RestResultDto changepasswordbyjson(HttpServletRequest request) throws Exception {
		String msg = null;
		Integer result = null;
		Object data = null;
		RestResultDto restResultDto = new RestResultDto();

		try {
			@SuppressWarnings("unchecked")
			Map<String, String> paramMap = jm.fromJson(SpringmvcUtils.getParameter(ManagementConstant.REST_PMS), Map.class);
			if (MapUtils.isEmpty(paramMap)) {
				throw new VortexException("参数不能为空");
			}
			String oldPwd = paramMap.get(OLD_PASSWORD);
			String newPwd = paramMap.get(NEW_PASSWORD);
			String userId = paramMap.get(USER_ID);

			cloudUserService.changePassword(userId, oldPwd, newPwd);
			msg = "修改成功！";
			result = ManagementConstant.REST_RESULT_SUCC;
		} catch (Exception e) {
			e.printStackTrace();
			result = ManagementConstant.REST_RESULT_FAIL;
			msg = "修改失败:" + e.getMessage();
			data = null;
			logger.error(msg, e);
		} finally {
			restResultDto.setData(data);
			restResultDto.setMsg(msg);
			restResultDto.setResult(result);
		}

		return restResultDto;
	}

	/**
	 * 更新容联账号
	 * 
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "updateRongLianAccount" + Constants.BACK_DYNAMIC_SUFFIX, method = { RequestMethod.GET, RequestMethod.POST })
	public RestResultDto updateRongLianAccount(HttpServletRequest request) throws Exception {
		String msg = null;
		Integer result = null;
		Object data = null;
		RestResultDto restResultDto = new RestResultDto();

		try {
			@SuppressWarnings("unchecked")
			Map<String, String> paramMap = jm.fromJson(SpringmvcUtils.getParameter(ManagementConstant.REST_PMS), Map.class);
			if (MapUtils.isEmpty(paramMap)) {
				throw new VortexException("参数不能为空");
			}
			String rongLianAccount = paramMap.get(RONG_LIAN_ACCOUNT);
			String userId = paramMap.get(USER_ID);

			cloudUserService.updateRongLianAccount(userId, rongLianAccount);
			msg = "修改成功！";
			result = ManagementConstant.REST_RESULT_SUCC;
		} catch (Exception e) {
			e.printStackTrace();
			result = ManagementConstant.REST_RESULT_FAIL;
			msg = "修改失败:" + e.getMessage();
			data = null;
			logger.error(msg, e);
		} finally {
			restResultDto.setData(data);
			restResultDto.setMsg(msg);
			restResultDto.setResult(result);
		}

		return restResultDto;
	}

	/**
	 * 加载机构用户树，机构+部门+人员(根据人员权限)
	 * 
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "loadUserTreeWithPermission" + ManagementConstant.PERMISSION_SUFFIX_READ, method = { RequestMethod.GET, RequestMethod.POST })
	public RestResultDto loadUserTreeWithPermission(HttpServletRequest request) throws Exception {
		String msg = null;
		Integer result = ManagementConstant.REST_RESULT_SUCC;
		Object data = null;
		JsonMapper mapper = new JsonMapper();
		RestResultDto rst = new RestResultDto();
		try {
			Map<String, String> paramMap = jm.fromJson(SpringmvcUtils.getParameter(ManagementConstant.REST_PMS), Map.class);
			if (MapUtils.isEmpty(paramMap)) {
				throw new VortexException("参数不能为空");
			}
			String userId = paramMap.get(USER_ID);
			if (StringUtils.isBlank(userId)) {
				logger.error("用户id不能为空");
				throw new VortexException("用户id不能为空");

			}
			UserOrganizationTreeWithPermission userOrganizationTree = UserOrganizationTreeWithPermission.getInstance();
			paramMap.put("userId", userId);
			paramMap.put("isControlPermission", paramMap.get(IS_CONTROL_PERMISSION));
			userOrganizationTree.reloadDeptOrgStaffTree(paramMap);
			data = treeService.generateJsonCheckboxTree(userOrganizationTree, false);
		} catch (Exception e) {
			e.printStackTrace();
			result = ManagementConstant.REST_RESULT_FAIL;
			msg = e.getMessage();
			data = null;
			logger.error(msg, e);
		} finally {
			rst.setResult(result);
			rst.setMsg(msg);
			rst.setData(data);
		}
		return rst;
	}

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
		RestResultDto restResultDto = new RestResultDto();
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
			// CloudUser cloudUser = managementRestService.getUserById(userId);
			UserDto cloudUser = cloudUserService.getUserById(userId);
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
			restResultDto.setData(data);
			restResultDto.setMsg(msg);
			restResultDto.setResult(result);
		}
		return restResultDto;
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
		RestResultDto restResultDto = new RestResultDto();

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
			restResultDto.setData(data);
			restResultDto.setMsg(msg);
			restResultDto.setResult(result);
		}

		return restResultDto;
	}

	/**
	 * 校验电话号码
	 * 
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "checkUserName", method = { RequestMethod.GET, RequestMethod.POST })
	public RestResultDto checkUserName(HttpServletRequest request) throws Exception {

		String msg = null;
		Integer result = ManagementConstant.REST_RESULT_SUCC;
		Object data = true;
		RestResultDto restResultDto = new RestResultDto();
		try {

			Map<String, String> paramMap = jm.fromJson(SpringmvcUtils.getParameter(ManagementConstant.REST_PMS), Map.class);
			if (MapUtils.isEmpty(paramMap)) {
				throw new VortexException("参数不能为空");
			}
			String userName = paramMap.get("userName");
			data = !cloudUserService.isNameExisted(userName, null);
		} catch (Exception e) {
			e.printStackTrace();
			result = ManagementConstant.REST_RESULT_FAIL;
			msg = e.getMessage();
			data = false;
			logger.error(msg, e);
		} finally {
			restResultDto.setData(data);
			restResultDto.setMsg(msg);
			restResultDto.setResult(result);
		}

		return restResultDto;
	}

	@RequestMapping(value = "getDeptInfo", method = { RequestMethod.GET, RequestMethod.POST })
	public RestResultDto getDeptInfo(HttpServletRequest request) {
		String msg = null;
		Integer result = ManagementConstant.REST_RESULT_SUCC;
		Object data = null;
		String exception = null;
		RestResultDto restResultDto = new RestResultDto();

		try {
			Map<String, String> paramMap = new JsonMapper().fromJson(SpringmvcUtils.getParameter(ManagementConstant.REST_PMS), Map.class);
			String userId = paramMap.get("userId");
			data = cloudUserService.getDeptInfo(userId);
		} catch (Exception e) {
			e.printStackTrace();
			result = ManagementConstant.REST_RESULT_FAIL;
			msg = "获取部门信息失败！";
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

	/**
	 * 根据人员账号id列表，得到人员登录账号列表
	 * 
	 * @param request
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "getUserNamesByIds", method = { RequestMethod.GET, RequestMethod.POST })
	public RestResultDto getOrgsByNames(HttpServletRequest request) {
		String msg = null;
		Integer result = ManagementConstant.REST_RESULT_SUCC;
		Object data = null;
		String exception = null;
		RestResultDto restResultDto = new RestResultDto();

		try {
			JsonMapper jm = new JsonMapper();
			Map<String, Object> paramMap = jm.fromJson(SpringmvcUtils.getParameter(ManagementConstant.REST_PMS), Map.class);
			List<String> ids = (List<String>) paramMap.get("ids");
			data = this.cloudUserService.getUserNamesByIds(ids);
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

	/**
	 * 查询用户分页 <br>
	 * page: 页， 从0开始 <br>
	 * rows: 行，默认10 <br>
	 * name:姓名（模糊匹配） <br>
	 * code：名称（模糊匹配） tenantId:租户ID （可选） orgId: departmentId:departmentId
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "getUserPageList", method = { RequestMethod.GET, RequestMethod.POST })
	public RestResultDto getUserPageList() throws Exception {
		String msg = null;
		Integer result = ManagementConstant.REST_RESULT_SUCC;
		Object data = null;
		try {

			Pageable pageable = null;

			Map<String, Object> paramMap = jm.fromJson(SpringmvcUtils.getParameter(ManagementConstant.REST_PMS), Map.class);

			Integer page = (Integer) (MapUtils.isNotEmpty(paramMap) && paramMap.get(PAGE) != null ? paramMap.get(PAGE) : 0);
			Integer rows = (Integer) (MapUtils.isNotEmpty(paramMap) && paramMap.get(ROWS) != null ? paramMap.get(ROWS) : 10);
			pageable = new PageRequest(page, rows, Direction.ASC, "orderIndex");
			CloudStaffSearchDto searchDto = new CloudStaffSearchDto();
			// 参数不为空的时候取出所有的参数
			if (MapUtils.isNotEmpty(paramMap)) {

				String name = (String) paramMap.get(NAME);
				String tenantId = (String) paramMap.get(TENANT_ID);
				String code = (String) paramMap.get("code");
				String orgId = (String) paramMap.get("orgId");
				String departmentId = (String) paramMap.get("departmentId");
				if (StringUtils.isNotBlank(name)) {
					searchDto.setName(name);
				}
				if (StringUtils.isNotBlank(tenantId)) {
					searchDto.setTenantId(tenantId);
				}
				if (StringUtils.isNotBlank(code)) {
					searchDto.setCode(code);
				}
				if (StringUtils.isNotBlank(orgId)) {
					searchDto.setOrgId(orgId);
				}
				if (StringUtils.isNotBlank(departmentId)) {
					searchDto.setDepartmentId(departmentId);
				}
			}

			Page<CloudUserDto> cloudstaffs = cloudUserService.findPageListBySearchDto(pageable, searchDto);

			DataStore<CloudUserDto> dataStore = new DataStore<>();
			if (CollectionUtils.isNotEmpty(cloudstaffs.getContent())) {
				dataStore.setRows(cloudstaffs.getContent());
				dataStore.setTotal(cloudstaffs.getTotalElements());
			}

			data = dataStore;
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
