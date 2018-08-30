package com.vortex.cloud.ums.web;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.vortex.cloud.ums.dataaccess.service.IParamGroupService;
import com.vortex.cloud.ums.tree.ParamGroupTree;
import com.vortex.cloud.ums.util.web.BaseController;
import com.vortex.cloud.ums.util.tree.ITreeService;
import com.vortex.cloud.vfs.data.dto.RestResultDto;

/**
 * @author LiShijun
 * @date 2016年3月24日 下午3:16:33
 * @Description 租户对参数模版做个性化维护
 * 
 *              History <author> <time> <desc>
 */
@SuppressWarnings("all")
@RestController
@RequestMapping("cloud/management/tenant/paramGroup")
public class TenantParamGroupController extends BaseController {
	private Logger logger = LoggerFactory.getLogger(TenantParamGroupController.class);

	private static final String FORE_DYNAMIC_SUFFIX = com.vortex.cloud.ums.support.ManagementConstant.PERMISSION_SUFFIX_SA;
	private static final String BACK_DYNAMIC_SUFFIX = com.vortex.cloud.ums.support.ManagementConstant.PERMISSION_SUFFIX_SA;

	@Resource
	private IParamGroupService paramGroupService;

	@Resource
	private ITreeService treeService;

	@RequestMapping(value = "loadTree" + FORE_DYNAMIC_SUFFIX, method = RequestMethod.POST)
	public RestResultDto<String> loadTree(HttpServletRequest request, HttpServletResponse response) {
		try {
			ParamGroupTree parameterTypeTree = ParamGroupTree.getInstance();
			parameterTypeTree.reloadParameterTypeTree(null);
			String jsonStr = treeService.generateJsonCheckboxTree(parameterTypeTree, false);
			return RestResultDto.newSuccess(jsonStr);
		} catch (Exception e) {
			logger.error("加载树出错", e);
			return RestResultDto.newFalid("加载树出错", e.getMessage());
		}
	}
}
