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

import com.vortex.cloud.ums.dataaccess.service.IManagementRestService;
import com.vortex.cloud.ums.model.CloudRole;
import com.vortex.cloud.ums.support.ManagementConstant;
import com.vortex.cloud.ums.util.web.BaseController;
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
@RequestMapping("cloud/management/rest/role")
public class RoleRestController extends BaseController {
	private static final Object SYSTEM_CODE = "systemCode";
	private static final Object USER_ID = "userId";
	private static final Object ROLE_CODE = "roleCode";
	private static final String ROLE_ID = "roleId";
	private Logger logger = LoggerFactory.getLogger(RoleRestController.class);
	private JsonMapper jm = new JsonMapper();
	@Resource
	private IManagementRestService managementRestService;

	/**
	 * 根据角色id，得到角色信息
	 * 
	 * 
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "getrolebyid" + ManagementConstant.PERMISSION_SUFFIX_READ, method = { RequestMethod.GET, RequestMethod.POST })
	public RestResultDto getRoleById(HttpServletRequest request) throws Exception {
		String msg = null;
		Integer result = null;
		Object data = null;

		try {
			Map<String, String> paramMap = jm.fromJson(SpringmvcUtils.getParameter(ManagementConstant.REST_PMS), Map.class);
			if (MapUtils.isEmpty(paramMap)) {
				throw new VortexException("参数不能为空");
			}

			String roleId = paramMap.get(ROLE_ID);
			if (StringUtils.isBlank(roleId)) {
				throw new VortexException("请传入参数：" + ROLE_ID);
			}

			if (logger.isDebugEnabled()) {
				logger.debug("getRoleById(), roleId=" + roleId);
			}
			CloudRole cloudRole = managementRestService.getRoleById(roleId);
			result = ManagementConstant.REST_RESULT_SUCC;
			msg = "成功获取角色信息";
			data = cloudRole;
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
	 * 根据用户id，得到用户所拥有的角色列表
	 * 
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "getrolesbyuserid" + ManagementConstant.PERMISSION_SUFFIX_READ, method = { RequestMethod.GET, RequestMethod.POST })
	public RestResultDto getRolesByUserId(HttpServletRequest request) throws Exception {
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
				logger.debug("getRolesByUserId(), userId=" + userId);
			}
			List<CloudRole> cloudRoles = managementRestService.getRolesByUserId(userId);
			result = ManagementConstant.REST_RESULT_SUCC;
			msg = "成功获取用户信息";
			data = cloudRoles;
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
