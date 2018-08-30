package com.vortex.cloud.ums.web;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.vortex.cloud.ums.annotation.FunctionCode;
import com.vortex.cloud.ums.dataaccess.service.ICloudStaffService;
import com.vortex.cloud.ums.dataaccess.service.ICloudUserService;
import com.vortex.cloud.ums.dataaccess.service.ITenantService;
import com.vortex.cloud.ums.dto.CloudUserDto;
import com.vortex.cloud.ums.enums.PermissionScopeEnum;
import com.vortex.cloud.ums.enums.ResponseType;
import com.vortex.cloud.ums.util.web.BaseController;
import com.vortex.cloud.vfs.common.mapper.JsonMapper;
import com.vortex.cloud.vfs.common.web.springmvc.SpringmvcUtils;
import com.vortex.cloud.vfs.data.dto.RestResultDto;

/**
 * 
 * @author LiShijun
 * @date 2016年5月27日 下午3:25:17
 * @Description 维护用户 History <author> <time> <desc>
 */
@SuppressWarnings("all")
@RestController
@RequestMapping("cloud/management/user")
public class UserController extends BaseController {
	private static final Logger logger = LoggerFactory.getLogger(UserController.class);

	private static final String FORE_DYNAMIC_SUFFIX = com.vortex.cloud.ums.support.ManagementConstant.PERMISSION_SUFFIX_SA;
	private static final String BACK_DYNAMIC_SUFFIX = com.vortex.cloud.ums.support.ManagementConstant.PERMISSION_SUFFIX_SA;

	@Resource
	private ICloudStaffService cloudStaffService;

	@Resource
	private ICloudUserService cloudUserService;

	@Resource
	private ITenantService tenantService;

	// 时间转化问题
	@InitBinder
	public void initBinder(WebDataBinder binder) {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		dateFormat.setLenient(true);
		binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
	}

	@RequestMapping(value = "loadCloudUserDtl" + FORE_DYNAMIC_SUFFIX, method = RequestMethod.POST)
	public RestResultDto<String> loadCloudUserDtl(HttpServletResponse response) {
		try {
			String id = SpringmvcUtils.getParameter("id");
			JsonMapper jsonMapper = new JsonMapper();
			CloudUserDto dto = cloudUserService.getById(id);
			return RestResultDto.newSuccess(jsonMapper.toJson(dto));
		} catch (Exception e) {
			logger.error("UserController.loadCloudUserDtl", e);
			return RestResultDto.newFalid("加载失败", e.getMessage());
		}
	}

	/**
	 * 新增
	 * 
	 * @param dto
	 * @return
	 */
	@RequestMapping(value = "add" + BACK_DYNAMIC_SUFFIX, method = RequestMethod.POST)
	@FunctionCode(value = "CF_MANAGE_USER_ADD", type = ResponseType.Json)
	public RestResultDto<Boolean> add(HttpServletRequest request, CloudUserDto dto) {
		try {
			cloudUserService.save(dto);
			return RestResultDto.newSuccess(true, "保存成功！");
		} catch (Exception e) {
			logger.error("UserController.add", e);
			return RestResultDto.newFalid("保存失败", e.getMessage());
		}

	}

	/**
	 * 新增
	 * 
	 * @param dto
	 * @return
	 */
	@RequestMapping(value = "resetPassword" + BACK_DYNAMIC_SUFFIX, method = RequestMethod.POST)
	@FunctionCode(value = "CF_MANAGE_USER_RESET_PWD", type = ResponseType.Json)
	public RestResultDto<Boolean> resetPassword(HttpServletRequest request) {
		try {
			String userId = SpringmvcUtils.getParameter("userId");
			cloudUserService.resetPassword(userId);
			return RestResultDto.newSuccess(true, "重置密码成功,新密码为123456,请联系用户尽快修改密码");
		} catch (Exception e) {
			logger.error("重置密码失败", e);
			return RestResultDto.newFalid("重置密码失败", e.getMessage());
		}

	}

	@RequestMapping(value = "loadPermissionScope" + FORE_DYNAMIC_SUFFIX, method = RequestMethod.POST)
	public RestResultDto<List<Map<String, String>>> loadPermissionScope(HttpServletRequest request, CloudUserDto dto) {
		try {
			List<Map<String, String>> result = Lists.newArrayList();
			for (PermissionScopeEnum permissionScopeEnum : PermissionScopeEnum.values()) {
				Map<String, String> map = Maps.newHashMap();
				map.put("key", permissionScopeEnum.getKey());
				map.put("text", permissionScopeEnum.getValue());
				result.add(map);
			}
			return RestResultDto.newSuccess(result);
		} catch (Exception e) {
			logger.error("UserController.loadPermissionScope", e);
			return RestResultDto.newFalid("获取权限范围失败", e.getMessage());
		}
	}

	/**
	 * 添加时的表单校验。
	 * 
	 * @param paramName
	 * @return
	 */
	@RequestMapping(value = "checkForAdd/{paramName}" + FORE_DYNAMIC_SUFFIX, method = RequestMethod.POST)
	public RestResultDto<Boolean> checkForAdd(@PathVariable("paramName") String paramName) {
		String paramVal = SpringmvcUtils.getParameter(paramName);
		if (StringUtils.isBlank(paramName) || StringUtils.isBlank(paramVal)) {
			return RestResultDto.newSuccess(false);
		}

		if (!StringUtils.equals("userName", paramName)) {
			return RestResultDto.newSuccess(true);
		}

		if (cloudUserService.isNameExisted(paramVal)) {
			return RestResultDto.newSuccess(false);
		} else {
			return RestResultDto.newSuccess(true);
		}
	}

	/**
	 * 修改时的表单校验
	 * 
	 * @param paramName
	 * @return
	 */
	@RequestMapping(value = "checkForUpdate/{paramName}" + FORE_DYNAMIC_SUFFIX, method = RequestMethod.POST)
	public RestResultDto<Boolean> checkForUpdate(@PathVariable("paramName") String paramName) {
		String id = SpringmvcUtils.getParameter("id");
		if (StringUtils.isBlank(id)) {
			logger.error("checkForUpdate(), ID is null or empty");
			return RestResultDto.newSuccess(false);
		}

		String paramVal = SpringmvcUtils.getParameter(paramName);
		if (StringUtils.isBlank(paramName) || StringUtils.isBlank(paramVal)) {
			return RestResultDto.newSuccess(false);
		}

		if (!StringUtils.equals("userName", paramName)) {
			return RestResultDto.newSuccess(true);
		}

		return RestResultDto.newSuccess(cloudUserService.validateNameOnUpdate(id, paramVal));
	}
}
