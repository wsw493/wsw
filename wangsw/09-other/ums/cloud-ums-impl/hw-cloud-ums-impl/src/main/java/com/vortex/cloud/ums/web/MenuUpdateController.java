package com.vortex.cloud.ums.web;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vortex.cloud.ums.annotation.FunctionCode;
import com.vortex.cloud.ums.dataaccess.service.ICloudMenuService;
import com.vortex.cloud.ums.dto.CloudMenuDto;
import com.vortex.cloud.ums.enums.ResponseType;
import com.vortex.cloud.ums.util.web.BaseController;
import com.vortex.cloud.vfs.data.dto.RestResultDto;

/**
 * 使用@ModelAttribute, 实现Struts2 Preparable二次部分绑定的效果。
 * 先根据form的id从数据库查出记录, 再把Form提交的内容绑定到该对象上。
 */
  @RestController      
@RequestMapping("cloud/management/menu")
public class MenuUpdateController extends BaseController {
	private Logger logger = LoggerFactory.getLogger(MenuUpdateController.class);
	
//	private static final String FORE_DYNAMIC_SUFFIX = com.vortex.cloud.ums.dataaccess.service.ManagementConstant.PERMISSION_SUFFIX_SA;
	private static final String BACK_DYNAMIC_SUFFIX = com.vortex.cloud.ums.support.ManagementConstant.PERMISSION_SUFFIX_SA;
	
	@Resource
	private ICloudMenuService cloudMenuService;
	
	@ModelAttribute("menu")
	public CloudMenuDto getById(@RequestParam(value = "id", required = true) String id) {
		if(StringUtils.isBlank(id)) {
			return null;
		}
		
		return cloudMenuService.getById(id);
	}
	
	@RequestMapping(value = "update" + BACK_DYNAMIC_SUFFIX, method = RequestMethod.POST)
	
	@FunctionCode(value = "CF_MANAGE_MENU_UPDATE", type = ResponseType.Json)
	public RestResultDto<Boolean> update(HttpServletRequest request, @ModelAttribute("menu") CloudMenuDto menu) {
		try {
			cloudMenuService.updateForBusinessSystem(menu);
			return RestResultDto.newSuccess(true, "更新成功");
		} catch (Exception e) {
			logger.error("MenuUpdateController.update", e);
			return RestResultDto.newFalid("更新失败", e.getMessage());
		}
	}
}
