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
import com.vortex.cloud.ums.dataaccess.service.IParamGroupService;
import com.vortex.cloud.ums.enums.ResponseType;
import com.vortex.cloud.ums.model.PramGroup;
import com.vortex.cloud.ums.util.web.BaseController;
import com.vortex.cloud.vfs.data.dto.RestResultDto;

  @RestController      
@RequestMapping("cloud/management/paramGroup")
public class ParamGroupDetailController extends BaseController {
	private Logger logger = LoggerFactory.getLogger(ParamGroupDetailController.class);
	
//	private static final String FORE_DYNAMIC_SUFFIX = com.vortex.cloud.ums.dataaccess.service.ManagementConstant.PERMISSION_SUFFIX_SA;
	private static final String BACK_DYNAMIC_SUFFIX = com.vortex.cloud.ums.support.ManagementConstant.PERMISSION_SUFFIX_SA;

	@Resource
	private IParamGroupService paramGroupService;
	
	/**
	 * 使用@ModelAttribute, 实现Struts2
	 * Preparable二次部分绑定的效果,先根据form的id从数据库查出Task对象,再把Form提交的内容绑定到该对象上。
	 * 因为仅update()方法的form中有id属性，因此本方法在该方法中执行.
	 */
	@ModelAttribute("parameterTypeGroup")
	public PramGroup get(@RequestParam(value = "id", required = false) String id) {
		if(StringUtils.isBlank(id)) {
			return null;
		}
		
		return paramGroupService.findOne(id);
	}
	
	@RequestMapping(value = "update" + BACK_DYNAMIC_SUFFIX, method = RequestMethod.POST)
	
	@FunctionCode(value = "CF_MANAGE_PARAM_G_UPDATE", type = ResponseType.Json)
	public RestResultDto<Boolean> update(HttpServletRequest request, @ModelAttribute("parameterTypeGroup") PramGroup parameterTypeGroup) {
		try {
			paramGroupService.update(parameterTypeGroup);
			return RestResultDto.newSuccess(true, "更新成功");
		} catch (Exception e) {
			logger.error("ParamGroupController.save", e);
			return RestResultDto.newFalid("更新失败", e.getMessage());
		}
	}
}
