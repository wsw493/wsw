package com.vortex.cloud.ums.web.ds2;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.vortex.cloud.ums.dataaccess2.service.ICloudSystemService;
import com.vortex.cloud.ums.model.CloudSystem;
import com.vortex.cloud.ums.util.web.BaseController;
import com.vortex.cloud.vfs.common.web.springmvc.SpringmvcUtils;
import com.vortex.cloud.vfs.data.dto.RestResultDto;

/**
 * 
 * @author
 *
 */
@SuppressWarnings("all")
@RestController("BussinessSystemController_ds2")
@RequestMapping("cloud/management/tenant/ds2/business")
public class BussinessSystemController extends BaseController {
	private static final Logger logger = LoggerFactory.getLogger(BussinessSystemController.class);

	private static final String FORE_DYNAMIC_SUFFIX = com.vortex.cloud.ums.support.ManagementConstant.PERMISSION_SUFFIX_SA;
	private static final String BACK_DYNAMIC_SUFFIX = com.vortex.cloud.ums.support.ManagementConstant.PERMISSION_SUFFIX_SA;

	@Resource(name = "cloudSystemService2")
	private ICloudSystemService cloudSystemService;

	/**
	 * 根据tenantId获取系统列表
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "getSystemList" + BACK_DYNAMIC_SUFFIX, method = RequestMethod.POST)

	public RestResultDto<List<CloudSystem>> getSystemList(HttpServletRequest request) {
		try {
			String tenantId = SpringmvcUtils.getParameter("tenantId");
			List<CloudSystem> list = null;

			list = cloudSystemService.getCloudSystems(tenantId);
			return RestResultDto.newSuccess(list);
		} catch (Exception e) {
			logger.error("getSystemList", e);
			return RestResultDto.newFalid("获取系统列表出错", e.getMessage());
		}

	}

}
