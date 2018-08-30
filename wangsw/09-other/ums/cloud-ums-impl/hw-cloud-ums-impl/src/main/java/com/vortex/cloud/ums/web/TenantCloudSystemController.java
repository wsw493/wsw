package com.vortex.cloud.ums.web;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.vortex.cloud.ums.annotation.FunctionCode;
import com.vortex.cloud.ums.dataaccess.service.ITenantCloudSystemService;
import com.vortex.cloud.ums.dto.CloudSysSearchDto;
import com.vortex.cloud.ums.dto.TenantSystemRelationDto;
import com.vortex.cloud.ums.enums.ResponseType;
import com.vortex.cloud.ums.util.support.ForeContext;
import com.vortex.cloud.ums.util.web.BaseController;
import com.vortex.cloud.vfs.common.web.springmvc.SpringmvcUtils;
import com.vortex.cloud.vfs.data.dto.DataStore;
import com.vortex.cloud.vfs.data.dto.RestResultDto;

/**
 * 配置管理租户云系统
 * 
 * @author lishijun
 *
 */
@SuppressWarnings("all")
@RestController
@RequestMapping("cloud/management/tenant/system/cloud")
public class TenantCloudSystemController extends BaseController {
	private static final Logger logger = LoggerFactory.getLogger(TenantCloudSystemController.class);

	private static final String FORE_DYNAMIC_SUFFIX = com.vortex.cloud.ums.support.ManagementConstant.PERMISSION_SUFFIX_SA;
	private static final String BACK_DYNAMIC_SUFFIX = com.vortex.cloud.ums.support.ManagementConstant.PERMISSION_SUFFIX_SA;

	private static final String CLOUD_SYSTEM_LIST_TITLE = "云系统列表";

	@Resource
	private ITenantCloudSystemService tenantCloudSystemService;

	@RequestMapping(value = "pageList" + FORE_DYNAMIC_SUFFIX, method = RequestMethod.POST)
	@FunctionCode(value = "CF_MANAGE_TENANT_CS_LIST", type = ResponseType.Json)
	public DataStore<TenantSystemRelationDto> pageList(HttpServletRequest request) {
		String tenantId = SpringmvcUtils.getParameter("tenantId");
		String systemName = SpringmvcUtils.getParameter("systemName");

		// 分页参数
		Sort defaultSort = new Sort(Direction.ASC, "systemCode");
		Pageable pageable = ForeContext.getPageable(request, defaultSort);

		CloudSysSearchDto searchDto = new CloudSysSearchDto();
		searchDto.setTenantId(tenantId);
		if (StringUtils.isNotBlank(systemName)) {
			searchDto.setSystemName(systemName);
		}

		// 分页
		long totalNum = 0; // 总记录数
		List<TenantSystemRelationDto> dtoList = null;

		Page<TenantSystemRelationDto> pageResult = tenantCloudSystemService.getPageOfCloudSys(pageable, searchDto);
		if (pageResult != null) {
			totalNum = pageResult.getTotalElements();
			dtoList = pageResult.getContent();
		}

		// 返回分页
		DataStore<TenantSystemRelationDto> ds = new DataStore<TenantSystemRelationDto>();
		ds.setTotal(totalNum);
		ds.setRows(dtoList);
		return ds;
	}

	/**
	 * 为租户开通某个系统
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "enableCloudSys" + BACK_DYNAMIC_SUFFIX, method = RequestMethod.POST)

	@FunctionCode(value = "CF_MANAGE_TENANT_CS_ENABLE", type = ResponseType.Json)
	public RestResultDto<Boolean> enableCloudSys(HttpServletRequest request) {

		String tenantId = SpringmvcUtils.getParameter("tenantId");
		String cloudSystemId = SpringmvcUtils.getParameter("cloudSystemId");

		try {
			tenantCloudSystemService.enableCloudSystem(tenantId, cloudSystemId);
			return RestResultDto.newSuccess(true, "启用成功");
		} catch (Exception e) {
			logger.error("操作失败", e);
			return RestResultDto.newFalid("操作失败", e.getMessage());
		}

	}

	/**
	 * 禁用租户的某个系统
	 * 
	 * @param tenantNodeSystemId
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "disableCloudSys" + BACK_DYNAMIC_SUFFIX, method = RequestMethod.POST)

	@FunctionCode(value = "CF_MANAGE_TENANT_CS_DISABLE", type = ResponseType.Json)
	public RestResultDto<Boolean> disableCloudSys(HttpServletRequest request) {
		String id = SpringmvcUtils.getParameter("id");

		try {
			tenantCloudSystemService.disableCloudSystem(id);
			return RestResultDto.newSuccess(true, "已禁用！");
		} catch (Exception e) {
			logger.error("操作失败", e);
			return RestResultDto.newFalid("操作失败", e.getMessage());
		}
	}
}
