package com.vortex.cloud.ums.web;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.collect.Lists;
import com.vortex.cloud.ums.dataaccess2.service.ICopyFunctionAcrossDatabaseService;
import com.vortex.cloud.ums.support.ManagementConstant;
import com.vortex.cloud.vfs.common.web.springmvc.SpringmvcUtils;
import com.vortex.cloud.vfs.data.dto.RestResultDto;

@RestController
@RequestMapping("cloud/management/copyresource")
public class CopyResourceController {
	private static final Logger logger = LoggerFactory.getLogger(FunctionController.class);

	@Resource
	private ICopyFunctionAcrossDatabaseService copyFunctionAcrossDatabaseService;

	/**
	 * 拷贝页面
	 * 
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "copy" + ManagementConstant.BACK_DYNAMIC_SUFFIX)
	public RestResultDto coyp(HttpServletRequest request, Model model) {
		RestResultDto restResultDto = new RestResultDto();
		Object data = null;
		String msg = "";
		Integer result = RestResultDto.RESULT_FAIL;
		String exception = "";
		String sourceSystemId = SpringmvcUtils.getParameter("sourceSystemId");
		String targetSystemId = SpringmvcUtils.getParameter("targetSystemId");
		String smenus = SpringmvcUtils.getParameter("smenus");
		List<String> smenusList = Lists.newArrayList();
		if (StringUtils.isNotBlank(smenus)) {
			smenusList = Arrays.asList(smenus.split(","));
		}
		try {
			copyFunctionAcrossDatabaseService.coyp(sourceSystemId, targetSystemId, smenusList);
			result = RestResultDto.RESULT_SUCC;
			msg = "拷贝菜单成功";
		} catch (Exception e) {
			msg = e.getMessage();
			logger.error(msg);
			e.printStackTrace();
		} finally {
			restResultDto.setResult(result);
			restResultDto.setData(data);
			restResultDto.setException(exception);
			restResultDto.setMsg(msg);
		}
		return restResultDto;
	}

}
