package com.vortex.cloud.ums.web.ds2;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vortex.cloud.ums.dataaccess2.service.ITenantService;
import com.vortex.cloud.ums.model.Tenant;
import com.vortex.cloud.ums.util.web.BaseController;
import com.vortex.cloud.vfs.data.dto.RestResultDto;

/**
 * 
 * @author lishijun 租户管理
 *
 */
@SuppressWarnings("all")
  @RestController      ("TenantController_ds2")
@RequestMapping("cloud/management/ds2/tenant")
public class TenantController extends BaseController {
	private static final Logger logger = LoggerFactory.getLogger(TenantController.class);
	private static final String FORE_DYNAMIC_SUFFIX = com.vortex.cloud.ums.support.ManagementConstant.PERMISSION_SUFFIX_SA;
	private static final String BACK_DYNAMIC_SUFFIX = com.vortex.cloud.ums.support.ManagementConstant.PERMISSION_SUFFIX_SA;

	@Resource(name = "tenantService2")
	private ITenantService tenantService;

	/**
	 * 列表页中点击【查看】链接加载指定tenant信息。
	 * 
	 * @param id
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "getTenantList" + BACK_DYNAMIC_SUFFIX)

	public RestResultDto<List<Tenant>> getTenantList(HttpServletRequest request, Model model) {
		try {
			List<Tenant> list = null;

			list = tenantService.findAll();
			return RestResultDto.newSuccess(list);
		} catch (Exception e) {
			logger.error("getTenantList()", e);
			return RestResultDto.newFalid("获取租户列表出错", e.getMessage());
		}

	}

}
