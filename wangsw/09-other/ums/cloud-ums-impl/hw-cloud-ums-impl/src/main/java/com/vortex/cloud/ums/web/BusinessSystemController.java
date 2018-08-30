package com.vortex.cloud.ums.web;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.vortex.cloud.ums.annotation.FunctionCode;
import com.vortex.cloud.ums.dataaccess.service.ICloudSystemService;
import com.vortex.cloud.ums.dataaccess.service.ITenantBusinessService;
import com.vortex.cloud.ums.dto.CloudSystemDto;
import com.vortex.cloud.ums.dto.SystemSearchDto;
import com.vortex.cloud.ums.enums.ResponseType;
import com.vortex.cloud.ums.model.CloudSystem;
import com.vortex.cloud.ums.util.support.ForeContext;
import com.vortex.cloud.ums.util.web.BaseController;
import com.vortex.cloud.vfs.common.lang.StringUtil;
import com.vortex.cloud.vfs.common.mapper.JsonMapper;
import com.vortex.cloud.vfs.common.web.springmvc.SpringmvcUtils;
import com.vortex.cloud.vfs.data.dto.DataStore;
import com.vortex.cloud.vfs.data.dto.RestResultDto;
import com.vortex.cloud.vfs.data.support.SearchFilter;

/**
 * 百度云租户业务系统注册
 * 
 * @author lishijun
 *
 */
@SuppressWarnings("all")
@RestController
@RequestMapping("cloud/management/tenant/business")
public class BusinessSystemController extends BaseController {
	private static final Logger logger = LoggerFactory.getLogger(BusinessSystemController.class);
	private static final String FORE_DYNAMIC_SUFFIX = com.vortex.cloud.ums.support.ManagementConstant.PERMISSION_SUFFIX_SA;
	private static final String BACK_DYNAMIC_SUFFIX = com.vortex.cloud.ums.support.ManagementConstant.PERMISSION_SUFFIX_SA;

	@Resource
	private ITenantBusinessService tenantBusinessService;

	/*
	 * @Resource private ICloudBusinessSystemService cloudSystemService;
	 */
	@Resource
	private ICloudSystemService cloudSystemService;

	/**
	 * 用于表单项的验证
	 * 
	 * @param paramName
	 * @return
	 */
	@RequestMapping(value = "validate/{param}" + FORE_DYNAMIC_SUFFIX, method = RequestMethod.POST)
	public RestResultDto<Boolean> validate(@PathVariable("param") String paramName) {
		// 入参非空校验
		if (StringUtil.isNullOrEmpty(paramName)) {
			return RestResultDto.newSuccess(false);
		}

		String paramVal = SpringmvcUtils.getParameter(paramName);
		if (StringUtil.isNullOrEmpty(paramVal)) {
			return RestResultDto.newSuccess(false);
		}

		// 是否与其他租户信息（租户名、域名）重复
		if (!("systemCode".equals(paramName))) {
			return RestResultDto.newSuccess(true);
		}

		String id = SpringmvcUtils.getParameter("id"); // 更新记录时，也要校验
		String tenantId = SpringmvcUtils.getParameter("tenantId");

		String systemCode = null;
		if ("systemCode".equals(paramName)) {
			systemCode = paramVal;
		}

		logger.info("validate(): tenantId=" + tenantId + ",id=" + id + ",systemCode=" + systemCode);

		List<SearchFilter> filterList = new ArrayList<SearchFilter>();
		SearchFilter filter = null;

		filter = new SearchFilter("tenantId", SearchFilter.Operator.EQ, tenantId);
		filterList.add(filter);

		filter = new SearchFilter("systemCode", SearchFilter.Operator.EQ, systemCode.trim());
		filterList.add(filter);

		if (!StringUtil.isNullOrEmpty(id)) {
			filter = new SearchFilter("id", SearchFilter.Operator.NE, id);
			filterList.add(filter);
		}

		boolean isExist = tenantBusinessService.isExistSystem(filterList);

		return RestResultDto.newSuccess(!isExist);
	}

	/**
	 * 新增记录
	 * 
	 * @param dto
	 * @return
	 */
	@RequestMapping(value = "add" + BACK_DYNAMIC_SUFFIX, method = RequestMethod.POST)
	@FunctionCode(value = "CF_MANAGE_TENANT_BS_ADD", type = ResponseType.Json)
	public RestResultDto<Boolean> add(HttpServletRequest request, CloudSystemDto dto) {

		try {
			dto.setTenantId(super.getLoginInfo(request).getTenantId());
			dto.setSystemType(CloudSystem.SYSTEM_TYPE_BUSINESS);
			cloudSystemService.saveCloudSystem(dto);
			return RestResultDto.newSuccess(true, "添加成功");
		} catch (Exception e) {
			logger.error("BusinessSystemController.add", e);
			return RestResultDto.newFalid("添加失败", e.getMessage());
		}

	}

	/**
	 * 为指定tenant获取系统分页数据
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "pageList" + FORE_DYNAMIC_SUFFIX, method = RequestMethod.POST)
	public RestResultDto<DataStore<CloudSystemDto>> pageList(HttpServletRequest request) {
		try {
			String tenantId = super.getLoginInfo(request).getTenantId();
			String systemName = SpringmvcUtils.getParameter("systemName");

			// 分页参数
			Pageable pageable = ForeContext.getPageable(request, null);

			SystemSearchDto searchDto = new SystemSearchDto();
			searchDto.setSystemName(systemName);
			searchDto.setTenantId(tenantId);
			searchDto.setSystemType(CloudSystem.SYSTEM_TYPE_BUSINESS);

			// 分页
			long totalNum = 0; // 总记录数
			List<CloudSystemDto> dtoList = null;

			Page<CloudSystemDto> pageResult = cloudSystemService.getPageOfBusinessSys(pageable, searchDto);
			if (pageResult != null) {
				totalNum = pageResult.getTotalElements();
				dtoList = pageResult.getContent();
			}

			// 返回分页
			DataStore<CloudSystemDto> ds = new DataStore<CloudSystemDto>();
			ds.setTotal(totalNum);
			ds.setRows(dtoList);
			return RestResultDto.newSuccess(ds);
		} catch (Exception e) {
			logger.error("加载列表分页出错", e);
			return RestResultDto.newFalid("加载列表分页出错", e.getMessage());
		}

	}

	@RequestMapping(value = "loadBusinessSystemDtl" + FORE_DYNAMIC_SUFFIX, method = RequestMethod.POST)
	public RestResultDto<CloudSystemDto> loadBusinessSystemDtl(HttpServletResponse response) {
		try {
			String id = SpringmvcUtils.getParameter("id");
			JsonMapper jsonMapper = new JsonMapper();
			CloudSystemDto dto = cloudSystemService.getCloudSystemById(id);
			return RestResultDto.newSuccess(dto);
		} catch (Exception e) {
			logger.error("根据id加载系统出错", e);
			return RestResultDto.newFalid("根据id加载系统出错", e.getMessage());
		}

	}

	/**
	 * 修改公司信息。
	 * 
	 * @param dto
	 * @return
	 */
	@RequestMapping(value = "update" + BACK_DYNAMIC_SUFFIX, method = RequestMethod.POST)
	@FunctionCode(value = "CF_MANAGE_TENANT_BS_UPDATE", type = ResponseType.Json)
	public RestResultDto<Boolean> update(HttpServletRequest request, CloudSystemDto dto) {

		try {
			cloudSystemService.updateCloudSystem(dto);
			return RestResultDto.newSuccess(true, "修改成功");
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("修改失败", e);
			return RestResultDto.newFalid("修改失败", e.getMessage());
		}

	}

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
