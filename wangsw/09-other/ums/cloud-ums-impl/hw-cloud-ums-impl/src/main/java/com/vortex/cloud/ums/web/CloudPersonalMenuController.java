package com.vortex.cloud.ums.web;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.vortex.cloud.ums.annotation.FunctionCode;
import com.vortex.cloud.ums.dataaccess.service.ICloudPersonalMenuService;
import com.vortex.cloud.ums.dto.CloudPersonalMenuDisplayDto;
import  com.vortex.cloud.vfs.data.dto.LoginReturnInfoDto;
import com.vortex.cloud.ums.enums.ResponseType;
import com.vortex.cloud.ums.util.web.BaseController;
import com.vortex.cloud.vfs.common.web.springmvc.SpringmvcUtils;
import com.vortex.cloud.vfs.data.dto.RestResultDto;

@SuppressWarnings("all")
  @RestController      
@RequestMapping("cloud/management/personal/menu")
public class CloudPersonalMenuController extends BaseController {
	
	private static final Logger logger = LoggerFactory.getLogger(CloudPersonalMenuController.class);

	private static final String FORE_DYNAMIC_SUFFIX = com.vortex.cloud.ums.support.ManagementConstant.PERMISSION_SUFFIX_SA;
	private static final String BACK_DYNAMIC_SUFFIX = com.vortex.cloud.ums.support.ManagementConstant.PERMISSION_SUFFIX_SA;

	@Resource
	private ICloudPersonalMenuService cloudPersonalMenuService;
	
	/**
	 * 新增
	 * @param dto
	 * @return
	 */
	@RequestMapping(value = "add" + BACK_DYNAMIC_SUFFIX, method = RequestMethod.POST)
	@FunctionCode(value = "CF_MANAGE_PERSONAL_MENU_ADD", type = ResponseType.Json)
	public RestResultDto<Boolean> add(HttpServletRequest request, String menuId, Integer orderIndex) {
		try {
			LoginReturnInfoDto loginInfo = super.getLoginInfo(request);
			String userId = loginInfo.getUserId();
			cloudPersonalMenuService.addSinglePersonalMenu(userId, menuId, orderIndex);
			return RestResultDto.newSuccess(true, "新增用户自定义菜单成功");
		} catch (Exception e) {
			logger.error("add()", e);
			return RestResultDto.newFalid("新增云系统失败", e.getMessage());
		}
	}
	
	/**
	 * 删除
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "delete" + BACK_DYNAMIC_SUFFIX, method = RequestMethod.POST)
	@FunctionCode(value = "CF_MANAGE_PERSONAL_MENU_DEL", type = ResponseType.Json)
	public RestResultDto<Boolean> delete(HttpServletRequest request) {
		try {
			String id = SpringmvcUtils.getParameter("id");
			cloudPersonalMenuService.delete(id);
			return RestResultDto.newSuccess(true, "删除成功");
		} catch (Exception e) {
			logger.error("delete()", e);
			return RestResultDto.newFalid("删除失败", e.getMessage());
		}
		
	}
	
	/**
	 * 加载用户自定义的菜单
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "getPersonalMenu" + FORE_DYNAMIC_SUFFIX, method = RequestMethod.POST)
	public RestResultDto<List<CloudPersonalMenuDisplayDto>> getPersonalMenu(HttpServletRequest request, HttpServletResponse response) {
		try {
			LoginReturnInfoDto infoDto = super.getLoginInfo(request);
			String userId = infoDto.getUserId();
			List<CloudPersonalMenuDisplayDto> dtoList = cloudPersonalMenuService.getPersonalMenu(userId);
			return RestResultDto.newSuccess(dtoList);
		} catch (Exception e) {
			logger.error("getPersonalMenu()", e);
			return RestResultDto.newFalid("加载失败", e.getMessage());
		}
	}
	
	
}
