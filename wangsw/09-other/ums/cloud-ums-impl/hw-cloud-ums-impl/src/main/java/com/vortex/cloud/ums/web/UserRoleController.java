package com.vortex.cloud.ums.web;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.vortex.cloud.ums.annotation.FunctionCode;
import com.vortex.cloud.ums.dataaccess.service.ICloudRoleService;
import com.vortex.cloud.ums.dataaccess.service.ICloudUserRoleService;
import com.vortex.cloud.ums.dataaccess.service.ICloudUserService;
import com.vortex.cloud.ums.dto.CloudUserDto;
import com.vortex.cloud.ums.dto.CloudUserRoleDto;
import com.vortex.cloud.ums.dto.CloudUserRoleSearchDto;
import com.vortex.cloud.ums.enums.ResponseType;
import com.vortex.cloud.ums.model.CloudRole;
import com.vortex.cloud.ums.util.web.BaseController;
import com.vortex.cloud.ums.util.support.ForeContext;
import com.vortex.cloud.ums.util.tree.ITreeService;
import com.vortex.cloud.vfs.data.dto.DataStore;
import com.vortex.cloud.vfs.data.dto.RestResultDto;




/**
 * @author lishijun 用户角色配置管理
 *
 */
@SuppressWarnings("all")
  @RestController      
@RequestMapping("cloud/management/user/{userId}/role")
public class UserRoleController extends BaseController {
	private static final Logger logger = LoggerFactory.getLogger(UserRoleController.class);

	private static final String FORE_DYNAMIC_SUFFIX = com.vortex.cloud.ums.support.ManagementConstant.PERMISSION_SUFFIX_SA;
	private static final String BACK_DYNAMIC_SUFFIX = com.vortex.cloud.ums.support.ManagementConstant.PERMISSION_SUFFIX_SA;

	@Resource
	private ICloudRoleService cloudRoleService;

	@Resource
	private ICloudUserRoleService cloudUserRoleService;

	@Resource
	private ICloudUserService cloudUserService;

	@Resource
	private ITreeService treeService;

	@ModelAttribute
	public void initModel(@PathVariable("userId") String userId, Model model) {
		model.addAttribute("user", cloudUserService.getById(userId));
	}

	@RequestMapping(value = "add/{roleIds}" + BACK_DYNAMIC_SUFFIX, method = RequestMethod.POST)
	
	@FunctionCode(value = "CF_MANAGE_USER_ROLE_ADD", type = ResponseType.Json)
	public RestResultDto<Boolean> add(HttpServletRequest request, @ModelAttribute("user") CloudUserDto user, @PathVariable("roleIds") String roleIds) {
		try {
			if (StringUtils.isBlank(roleIds)) {
				return RestResultDto.newSuccess(false, "未指定角色");
			}
			String[] roleIdArr = roleIds.split(",");
			cloudUserRoleService.addRoles(user.getId(), roleIdArr);
			return RestResultDto.newSuccess(true, "保存成功");
		} catch (Exception e) {
			logger.error("UserRoleController.add", e);
			return RestResultDto.newFalid("保存失败", e.getMessage());
		}
		
	}

	@RequestMapping(value = "pageList" + FORE_DYNAMIC_SUFFIX, method = RequestMethod.POST)
	
	public RestResultDto<DataStore<CloudUserRoleDto>> pageList(HttpServletRequest request, @ModelAttribute("user") CloudUserDto user) {
		try {
			CloudUserRoleSearchDto searchDto = new CloudUserRoleSearchDto();
			searchDto.setUserId(user.getId());
			// 得到分页
			Pageable pageable = ForeContext.getPageable(request, null);
			Page<CloudUserRoleDto> pageResult = cloudUserRoleService.findPageBySearchDto(pageable, searchDto);

			DataStore<CloudUserRoleDto> ds = new DataStore<>();
			if (pageResult != null) {
				ds.setTotal(pageResult.getTotalElements());
				ds.setRows(pageResult.getContent());
			}
			return RestResultDto.newSuccess(ds);
		} catch (Exception e) {
			logger.error("UserRoleController.pageList", e);
			return RestResultDto.newFalid("查询失败", e.getMessage());
		}
	}

	@RequestMapping(value = "delete/{id}" + BACK_DYNAMIC_SUFFIX, method = RequestMethod.POST)
	@FunctionCode(value = "CF_MANAGE_USER_ROLE_DEL", type = ResponseType.Json)
	public RestResultDto<Boolean> delete(HttpServletRequest request, @ModelAttribute("user") CloudUserDto user, @PathVariable("id") String id) {
		try {
			cloudUserRoleService.delete(id);
			return RestResultDto.newSuccess(true, "删除成功");
		} catch (Exception e) {
			logger.error("UserRoleController.pageList", e);
			return RestResultDto.newFalid("删除失败", e.getMessage());
		}
	}

	/**
	 * 根据当前节点id，返回下面的功能列表
	 * @param request
	 * @param response
	 * @return
	 * 
	 */
	@RequestMapping(value = "dataList" + BACK_DYNAMIC_SUFFIX, method = RequestMethod.POST)
	public RestResultDto<List<CloudRole>> dataList(HttpServletRequest request, HttpServletResponse response, @PathVariable("userId") String userId) {
		try {
			List<CloudRole> cloudRoleList = cloudUserRoleService.getRolesByUserId(userId);
			return RestResultDto.newSuccess(cloudRoleList);
		} catch (Exception e) {
			logger.error("UserRoleController.pageList", e);
			return RestResultDto.newFalid("查询失败", e.getMessage());
		}
	}
}
