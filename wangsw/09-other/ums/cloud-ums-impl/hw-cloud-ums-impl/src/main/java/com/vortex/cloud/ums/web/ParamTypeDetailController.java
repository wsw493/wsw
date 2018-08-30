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
import com.vortex.cloud.ums.dataaccess.service.IParamTypeService;
import com.vortex.cloud.ums.enums.ResponseType;
import com.vortex.cloud.ums.model.PramType;
import com.vortex.cloud.ums.util.web.BaseController;
import com.vortex.cloud.vfs.data.dto.RestResultDto;

  @RestController      
@RequestMapping("cloud/management/paramType")
public class ParamTypeDetailController extends BaseController {
	private Logger logger = LoggerFactory.getLogger(ParamTypeDetailController.class);
	
//	private static final String FORE_DYNAMIC_SUFFIX = com.vortex.cloud.ums.dataaccess.service.ManagementConstant.PERMISSION_SUFFIX_SA;
	private static final String BACK_DYNAMIC_SUFFIX = com.vortex.cloud.ums.support.ManagementConstant.PERMISSION_SUFFIX_SA;

	@Resource
	private IParamTypeService paramTypeService;
	
	/**
	 * 使用@ModelAttribute, 实现Struts2
	 * Preparable二次部分绑定的效果,先根据form的id从数据库查出Task对象,再把Form提交的内容绑定到该对象上。
	 * 因为仅update()方法的form中有id属性，因此本方法在该方法中执行.
	 */
	@ModelAttribute("parameterType")
	public PramType get(@RequestParam(value = "id", required = false) String id) {
		if(StringUtils.isBlank(id)) {
			return null;
		}
		
		return paramTypeService.findOne(id);
	}

	@RequestMapping(value = "update" + BACK_DYNAMIC_SUFFIX, method = RequestMethod.POST)
	
	@FunctionCode(value = "CF_MANAGE_PARAM_TYPE_UPDATE", type = ResponseType.Json)
	public RestResultDto<Boolean> update(HttpServletRequest request, @ModelAttribute("parameterType") PramType parameterType) {
		try {
			paramTypeService.update(parameterType);
			return RestResultDto.newSuccess(true, "更新成功");
		} catch (Exception e) {
			logger.error("ParamSettingDetailController.update", e);
			return RestResultDto.newFalid("更新失败", e.getMessage());
		}
	}
}
