package com.vortex.cloud.ums.web.ds2;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vortex.cloud.ums.tree.ds2.CloudMenuTree;
import com.vortex.cloud.ums.util.web.BaseController;
import com.vortex.cloud.ums.util.tree.ITreeService;
import com.vortex.cloud.vfs.common.web.springmvc.SpringmvcUtils;
import com.vortex.cloud.vfs.data.dto.RestResultDto;

@SuppressWarnings("all")
@RestController("MenuController_ds2")
@RequestMapping("cloud/management/ds2/menu")
public class MenuController extends BaseController {

	private static final Logger logger = LoggerFactory.getLogger(MenuController.class);

	private static final String FORE_DYNAMIC_SUFFIX = com.vortex.cloud.ums.support.ManagementConstant.PERMISSION_SUFFIX_READ;
	private static final String BACK_DYNAMIC_SUFFIX = com.vortex.cloud.ums.support.ManagementConstant.PERMISSION_SUFFIX_SA;

	@Resource
	private ITreeService treeService;

	/**
	 * 用于树的加载、刷新
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "loadMenuTree" + BACK_DYNAMIC_SUFFIX)
	public RestResultDto<String> loadMenuTree(HttpServletRequest request, HttpServletResponse response) {
		try {

			String systemId = SpringmvcUtils.getParameter("systemId");

			CloudMenuTree tree = CloudMenuTree.getInstance();
			tree.reloadMenuTree(systemId);
			String json = treeService.generateJsonCheckboxTree(tree, false);
			return RestResultDto.newSuccess(json);
		} catch (Exception e) {
			logger.error("获取菜单树失败", e);
			return RestResultDto.newFalid("获取菜单树失败", e.getMessage());
		}
	}

}
