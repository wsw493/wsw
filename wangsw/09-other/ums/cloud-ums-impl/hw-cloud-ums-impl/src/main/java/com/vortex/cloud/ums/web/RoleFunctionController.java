package com.vortex.cloud.ums.web;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.vortex.cloud.ums.annotation.FunctionCode;
import com.vortex.cloud.ums.dataaccess.service.ICloudFunctionRoleService;
import com.vortex.cloud.ums.dto.CloudFunctionRoleDto;
import com.vortex.cloud.ums.enums.ResponseType;
import com.vortex.cloud.ums.model.CloudFunctionGroup;
import com.vortex.cloud.ums.util.web.BaseController;
import com.vortex.cloud.ums.util.orm.Page;
import com.vortex.cloud.ums.util.support.ForeContext;
import com.vortex.cloud.vfs.common.exception.VortexException;
import com.vortex.cloud.vfs.common.web.springmvc.SpringmvcUtils;
import com.vortex.cloud.vfs.data.dto.DataStore;
import com.vortex.cloud.vfs.data.dto.RestResultDto;

@RestController
@RequestMapping("cloud/management/functionrole")
public class RoleFunctionController extends BaseController {
	private static final Logger logger = LoggerFactory.getLogger(FunctionGroupController.class);

	private static final String FORE_DYNAMIC_SUFFIX = com.vortex.cloud.ums.support.ManagementConstant.PERMISSION_SUFFIX_SA;
	private static final String BACK_DYNAMIC_SUFFIX = com.vortex.cloud.ums.support.ManagementConstant.PERMISSION_SUFFIX_SA;

	@Resource
	private ICloudFunctionRoleService cloudFunctionRoleService;

	/**
	 * 保存功能角色信息
	 * 
	 * @param dto
	 * @return
	 */
	@RequestMapping(value = "save/{roleId}" + BACK_DYNAMIC_SUFFIX, method = RequestMethod.POST)
	@FunctionCode(value = "CF_MANAGE_ROLE_FUN_ADD", type = ResponseType.Json)
	public RestResultDto<Boolean> saveFunctionGroupInfo(HttpServletRequest request, CloudFunctionGroup dto, @PathVariable String roleId) {
		try {

			String functionIds = SpringmvcUtils.getParameter("functionIds");
			String systemId = SpringmvcUtils.getParameter("systemId");

			if (StringUtils.isBlank(functionIds)) {
				throw new VortexException("未指定功能！");
			}
			String[] functionIdArr = functionIds.split(",");
			cloudFunctionRoleService.saveFunctionsForRole(roleId, systemId, functionIdArr);

			return RestResultDto.newSuccess(true, "添加成功！");
		} catch (Exception e) {
			logger.error("添加失败", e);
			return RestResultDto.newFalid("添加失败", e.getMessage());
		}
	}

	/**
	 * 删除功能角色信息
	 * 
	 * @param dto
	 * @return
	 */
	@RequestMapping(value = "delete/{id}" + BACK_DYNAMIC_SUFFIX, method = RequestMethod.POST)
	@FunctionCode(value = "CF_MANAGE_ROLE_FUN_DEL", type = ResponseType.Json)
	public RestResultDto<Boolean> deleteFunctionGroupInfo(HttpServletRequest request, @PathVariable String id) {
		try {
			cloudFunctionRoleService.deleteFunctionRole(id);
			return RestResultDto.newSuccess(true, "删除成功！");
		} catch (Exception e) {
			logger.error("delete()", e);
			return RestResultDto.newFalid("删除失败", e.getMessage());
		}
	}

	/**
	 * 根据当前节点id，返回下面的功能列表
	 * 
	 * @param request
	 * @param response
	 * @return
	 * 
	 */
	@RequestMapping(value = "pageList" + FORE_DYNAMIC_SUFFIX, method = RequestMethod.POST)
	@FunctionCode(value = "CF_MANAGE_ROLE_FUN_LIST", type = ResponseType.Json)
	public RestResultDto<DataStore<CloudFunctionRoleDto>> pageList(HttpServletRequest request, HttpServletResponse response) {
		try {
			String roleId = SpringmvcUtils.getParameter("roleId");
			String systemId = SpringmvcUtils.getParameter("systemId");

			if (StringUtils.isBlank(roleId) || StringUtils.isBlank(systemId)) {
				logger.error("pageList(), 入参为空");
				throw new VortexException("pageList(), 入参为空");
			}

			Pageable pageable = ForeContext.getPageable(request, null);
			Page<CloudFunctionRoleDto> page = null;

			page = cloudFunctionRoleService.getPageBySystem(roleId, systemId, pageable);

			DataStore<CloudFunctionRoleDto> dataStore = null;
			if (null != page) {
				List<CloudFunctionRoleDto> result = page.getResult();
				dataStore = new DataStore<CloudFunctionRoleDto>(page.getTotalRecords(), result);
			} else {
				dataStore = new DataStore<CloudFunctionRoleDto>();
			}
			return RestResultDto.newSuccess(dataStore);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			e.printStackTrace();
			return RestResultDto.newFalid("加载分页失败", e.getMessage());
		}
	}

	/**
	 * 根据当前节点id，返回下面的功能列表
	 * 
	 * @param request
	 * @param response
	 * @return
	 * 
	 */
	@RequestMapping(value = "dataList" + FORE_DYNAMIC_SUFFIX, method = RequestMethod.POST)
	public RestResultDto<List<CloudFunctionRoleDto>> dataList(HttpServletRequest request, HttpServletResponse response) {
		try {

			String roleId = SpringmvcUtils.getParameter("roleId");
			String systemId = SpringmvcUtils.getParameter("systemId");

			if (StringUtils.isBlank(roleId) || StringUtils.isBlank(systemId)) {
				logger.error("dataList(), 入参为空");
				throw new VortexException("dataList(), 入参为空");
			}

			return RestResultDto.newSuccess(cloudFunctionRoleService.getListBySystem(roleId, systemId));
		} catch (Exception e) {
			logger.error("加载列表出错", e);
			return RestResultDto.newFalid("加载列表出错", e.getMessage());
		}
	}

}
