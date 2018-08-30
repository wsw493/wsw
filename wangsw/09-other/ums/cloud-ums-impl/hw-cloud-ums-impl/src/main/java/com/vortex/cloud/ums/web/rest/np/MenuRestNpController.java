package com.vortex.cloud.ums.web.rest.np;

import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.vortex.cloud.ums.dataaccess.service.ICloudMenuService;
import com.vortex.cloud.ums.dataaccess.service.IRedisValidateService;
import com.vortex.cloud.ums.support.ManagementConstant;
import com.vortex.cloud.vfs.common.exception.VortexException;
import com.vortex.cloud.vfs.common.mapper.JsonMapper;
import com.vortex.cloud.vfs.common.web.springmvc.SpringmvcUtils;
import com.vortex.cloud.vfs.data.dto.RestResultDto;

@SuppressWarnings("all")
@RestController
@RequestMapping("cloud/management/rest/np/menu")
public class MenuRestNpController {
	private static final String USER_ID = "userId";
	private static final String SYSTEM_CODE = "systemCode";
	private JsonMapper jm = new JsonMapper();
	private Logger logger = LoggerFactory.getLogger(MenuRestNpController.class);
	@Resource
	private IRedisValidateService redisValidateService;
	@Resource
	private ICloudMenuService cloudMenuService;

	/**
	 * 获取菜单json
	 * 
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings({ "unchecked", "finally" })
	@RequestMapping(value = "getmenujson" + ManagementConstant.PERMISSION_SUFFIX_READ, method = { RequestMethod.GET, RequestMethod.POST })
	public RestResultDto getBsMenu(HttpServletRequest request) throws Exception {
		String msg = "";
		Integer result = ManagementConstant.REST_RESULT_SUCC;
		Object data = null;
		String exception = "";
		RestResultDto rst = new RestResultDto();
		try {
			Map<String, String> paramMap = jm.fromJson(SpringmvcUtils.getParameter(ManagementConstant.REST_PMS), Map.class);
			if (MapUtils.isEmpty(paramMap)) {
				throw new VortexException("参数不能为空");
			}

			String userId = paramMap.get(USER_ID);
			String systemCode = paramMap.get(SYSTEM_CODE);

			data = redisValidateService.getBsMenuJson(userId, systemCode);
			msg = "获取菜单成功";
		} catch (Exception e) {
			e.printStackTrace();
			result = ManagementConstant.REST_RESULT_FAIL;
			msg = "获取菜单失败";
			data = null;
			exception = e.getMessage();
			logger.error(msg, e);
		} finally {
			rst.setResult(result);
			rst.setMsg(msg);
			rst.setData(data);
			rst.setException(exception);
		}

		return rst;
	}

	/**
	 * 获取菜单json
	 * 
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings({ "unchecked", "finally" })
	@RequestMapping(value = "getmenujsonbyurl" + ManagementConstant.PERMISSION_SUFFIX_READ, method = { RequestMethod.GET, RequestMethod.POST })
	public RestResultDto getBsMenuByUrl(HttpServletRequest request) throws Exception {
		String msg = null;
		Integer result = ManagementConstant.REST_RESULT_SUCC;
		Object data = null;

		try {
			String userId = SpringmvcUtils.getParameter(USER_ID);
			String systemCode = SpringmvcUtils.getParameter(SYSTEM_CODE);

			data = redisValidateService.getBsMenuJson(userId, systemCode);
			// data = new
			// JsonMapper().toJson(cloudMenuService.getMenuTree(systemCode,
			// userId));
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
