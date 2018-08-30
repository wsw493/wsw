package com.vortex.cloud.ums.web.rest.np;

import java.util.ArrayList;
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

import com.vortex.cloud.ums.dataaccess.service.ICloudRoleService;
import com.vortex.cloud.ums.support.ManagementConstant;
import com.vortex.cloud.ums.util.web.BaseController;
import com.vortex.cloud.vfs.common.exception.VortexException;
import com.vortex.cloud.vfs.common.mapper.JsonMapper;
import com.vortex.cloud.vfs.common.web.springmvc.SpringmvcUtils;
import com.vortex.cloud.vfs.data.dto.RestResultDto;

/**
 * 
 * @author lusm
 *
 */
@SuppressWarnings("all")
@RestController
@RequestMapping("cloud/management/rest/np/role")
public class RoleRestNpController extends BaseController {
	private static final String SYSTEM_CODE = "systemCode";
	private static final String USER_ID = "userId";
	private static final String ROLE_CODE = "roleCode";
	private static final String ROLE_ID = "roleId";
	private static final String TENANT_ID = "tenantId";
	private static final String ORG_ID = "orgId";
	private Logger logger = LoggerFactory.getLogger(RoleRestNpController.class);
	private JsonMapper jm = new JsonMapper();
	@Resource
	private ICloudRoleService cloudRoleService;

	/**
	 * 获取扮演该角色的用户ids
	 * 
	 * 
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "getUserIdsByRole", method = { RequestMethod.GET, RequestMethod.POST })
	public RestResultDto getUserIdsByRole(HttpServletRequest request) throws Exception {
		String msg = null;
		Integer result = null;
		Object data = null;

		try {
			Map<String, String> paramMap = jm.fromJson(SpringmvcUtils.getParameter(ManagementConstant.REST_PMS), Map.class);
			if (MapUtils.isEmpty(paramMap)) {
				throw new VortexException("参数不能为空");
			}

			String roleCode = paramMap.get(ROLE_CODE);
			if (StringUtils.isBlank(roleCode)) {
				throw new VortexException("请传入参数：" + ROLE_CODE);
			}
			String tenantId = paramMap.get(TENANT_ID);
			if (StringUtils.isBlank(tenantId)) {
				throw new VortexException("请传入参数：" + TENANT_ID);
			}

			String orgId = paramMap.get(ORG_ID);

			if (logger.isDebugEnabled()) {
				logger.debug("getUserIdsByRole(), roleCode=" + roleCode + ",tenantId=" + tenantId);
			}
			List<String> userIds = new ArrayList<String>();
			if (StringUtils.isBlank(orgId)) {
				userIds = cloudRoleService.getUserIdsByRole(tenantId, roleCode);
			} else {
				userIds = cloudRoleService.getUserIdsByRoleAndOrg(orgId, roleCode);
			}
			result = ManagementConstant.REST_RESULT_SUCC;
			msg = "成功获取拥有该角色的用户信息";
			data = userIds;
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
