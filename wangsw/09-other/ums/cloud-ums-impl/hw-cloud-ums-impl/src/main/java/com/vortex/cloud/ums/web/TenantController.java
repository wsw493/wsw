package com.vortex.cloud.ums.web;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.collect.Lists;
import com.vortex.cloud.ums.annotation.FunctionCode;
import com.vortex.cloud.ums.dataaccess.service.ITenantService;
import  com.vortex.cloud.vfs.data.dto.LoginReturnInfoDto;
import com.vortex.cloud.ums.dto.TenantDto;
import com.vortex.cloud.ums.dto.TenantUrlDto;
import com.vortex.cloud.ums.enums.ResponseType;
import com.vortex.cloud.ums.model.Tenant;
import com.vortex.cloud.ums.util.web.BaseController;
import com.vortex.cloud.ums.util.support.ForeContext;
import com.vortex.cloud.vfs.common.lang.StringUtil;
import com.vortex.cloud.vfs.common.mapper.JsonMapper;
import com.vortex.cloud.vfs.common.web.springmvc.SpringmvcUtils;
import com.vortex.cloud.vfs.data.dto.DataStore;
import com.vortex.cloud.vfs.data.dto.RestResultDto;
import com.vortex.cloud.vfs.data.support.SearchFilter;
import com.vortex.cloud.vfs.data.support.SearchFilter.Operator;








/**
 * 
 * @author lishijun 租户管理
 *
 */
@SuppressWarnings("all")
  @RestController      
@RequestMapping("cloud/management/tenant")
public class TenantController extends BaseController {
	private static final Logger logger = LoggerFactory.getLogger(TenantController.class);
	private static final String FORE_DYNAMIC_SUFFIX = com.vortex.cloud.ums.support.ManagementConstant.PERMISSION_SUFFIX_SA;
	private static final String BACK_DYNAMIC_SUFFIX = com.vortex.cloud.ums.support.ManagementConstant.PERMISSION_SUFFIX_SA;

	@Resource
	private ITenantService tenantService;

	@RequestMapping(value = "pageList" + FORE_DYNAMIC_SUFFIX, method = RequestMethod.POST)
	public RestResultDto<DataStore<TenantDto>> pageList(HttpServletRequest request, HttpServletResponse response) {
		try {
			// 查询条件
			String tenantName = SpringmvcUtils.getParameter("tenantName");
			String enabled = SpringmvcUtils.getParameter("enabled");

			logger.info("TenderController.pageList(tenantName=" + tenantName + ", enabled=" + enabled + ")");

			List<SearchFilter> filterList = Lists.newArrayList();
			if (!StringUtils.isBlank(tenantName)) {
				filterList.add(new SearchFilter("tenant.tenantName", Operator.LIKE, tenantName));
			}

			if (!StringUtils.isBlank(enabled)) {
				filterList.add(new SearchFilter("tenant.enabled", Operator.EQ, enabled));
			}

			Sort defaultSort = new Sort(Direction.DESC, "createTime");
			Pageable pageable = ForeContext.getPageable(request, defaultSort);

			// 得到分页
			Page<Tenant> pageResult = tenantService.findPageByFilter(pageable, filterList);

			DataStore<TenantDto> ds = new DataStore<TenantDto>();
			if (pageResult != null) {
				ds.setTotal(pageResult.getTotalElements());

				List<Tenant> tenants = pageResult.getContent();
				List<TenantDto> dtoList = new ArrayList<TenantDto>();
				TenantDto dto = null;
				for (Tenant tenant : tenants) {
					dto = new TenantDto();
					BeanUtils.copyProperties(tenant, dto);
					dtoList.add(dto);
				}
				ds.setRows(dtoList);
			}
			return RestResultDto.newSuccess(ds);
		} catch (Exception e) {
			return RestResultDto.newFalid("获取分页列表出错", e.getMessage());
		}

	}

	/**
	 * 用于表单项的验证
	 * 
	 * @param paramName
	 * @return
	 */
	@RequestMapping(value = "checkForm/{param}" + FORE_DYNAMIC_SUFFIX, method = RequestMethod.POST)
	public RestResultDto<Boolean> checkForm(@PathVariable("param") String paramName) {
		// 入参非空校验
		if (StringUtil.isNullOrEmpty(paramName)) {
			return RestResultDto.newSuccess(false);
		}

		String paramVal = SpringmvcUtils.getParameter(paramName);
		if (StringUtil.isNullOrEmpty(paramVal)) {
			return RestResultDto.newSuccess(false);
		}

		if (!("tenantCode".equals(paramName)) && !("domain".equals(paramName))) {
			return RestResultDto.newSuccess(true);
		}

		// 是否与其他租户信息（租户名、域名）重复
		SearchFilter filter = null;
		List<SearchFilter> searchFilters = Lists.newArrayList();

		String tenantId = SpringmvcUtils.getParameter("id");
		if (!StringUtil.isNullOrEmpty(tenantId)) {
			filter = new SearchFilter("id", SearchFilter.Operator.NE, tenantId.trim());
			searchFilters.add(filter);
		}

		filter = new SearchFilter(paramName, SearchFilter.Operator.EQ, paramVal);
		searchFilters.add(filter);

		List<Tenant> list = tenantService.findListByFilter(searchFilters, null);

		if (CollectionUtils.isNotEmpty(list)) {
			return RestResultDto.newSuccess(false);
		}
		return RestResultDto.newSuccess(true);
	}

	/**
	 * 添加Tenant
	 * 
	 * @param newTenant
	 * @return
	 */
	@RequestMapping(value = "add" + BACK_DYNAMIC_SUFFIX, method = RequestMethod.POST)
	@FunctionCode(value = "CF_MANAGE_TENANT_ADD", type = ResponseType.Json)
	public RestResultDto<Boolean> add(HttpServletRequest request, TenantDto tenantDto) {

		try {
			tenantService.saveTenant(tenantDto);
			return RestResultDto.newSuccess(true);
		} catch (Exception e) {
			logger.error("TenantController.add", e);
			return RestResultDto.newFalid("添加失败", e.getMessage());
		}

	}

	/**
	 * 列表页中点击【启用】链接
	 * 
	 * @param id
	 * @param request
	 * @param redirectAttributes
	 * @return
	 */
	@RequestMapping(value = "enable" + BACK_DYNAMIC_SUFFIX, method = RequestMethod.POST)
	@FunctionCode(value = "CF_MANAGE_TENANT_ENABLE", type = ResponseType.Json)
	public RestResultDto<Boolean> enable(HttpServletRequest request, @RequestParam("ids") String id) {
		String[] ids = id.split(",");

		try {
			tenantService.enableTenant(ids);
			return RestResultDto.newSuccess(true, "启用成功");
		} catch (Exception e) {
			logger.error("enable()：启用失败", e);
			return RestResultDto.newFalid("启用失败", e.getMessage());
		}

	}

	/**
	 * 列表页中点击【禁用】链接
	 * 
	 * @param id
	 * @param redirectAttributes
	 * @return
	 */
	@RequestMapping(value = "disable" + BACK_DYNAMIC_SUFFIX, method = RequestMethod.POST)
	@FunctionCode(value = "CF_MANAGE_TENANT_DISABLE", type = ResponseType.Json)
	public RestResultDto<Boolean> disable(HttpServletRequest request, @RequestParam("ids") String id) {
		String[] ids = id.split(",");

		try {
			tenantService.disableTenant(ids);
			return RestResultDto.newSuccess(true, "禁用成功");
		} catch (Exception e) {
			logger.error("disable()", e);
			return RestResultDto.newFalid("禁用失败", e.getMessage());
		}

	}

	@RequestMapping(value = "loadTenantDtl" + BACK_DYNAMIC_SUFFIX, method = RequestMethod.POST)
	public RestResultDto<TenantDto> loadTenantDtl(HttpServletResponse response) {
		try {
			String id = SpringmvcUtils.getParameter("id");
			JsonMapper jsonMapper = new JsonMapper();
			TenantDto dto = tenantService.loadTenant(id);
			return RestResultDto.newSuccess(dto);
		} catch (Exception e) {
			logger.error("根据id获取租户详情失败", e);
			return RestResultDto.newFalid("根据id获取租户详情失败", e.getMessage());
		}

	}

	/**
	 * 修改页面的提交更新。
	 * 
	 * @param tenant
	 * @return
	 */
	@RequestMapping(value = "update" + BACK_DYNAMIC_SUFFIX, method = RequestMethod.POST)
	@FunctionCode(value = "CF_MANAGE_TENANT_UPDATE", type = ResponseType.Json)
	public RestResultDto<Boolean> update(HttpServletRequest request, @ModelAttribute("tenant") TenantDto tenantDto) {

		try {
			tenantService.updateTenant(tenantDto);
			return RestResultDto.newSuccess(true, "更新成功");
		} catch (Exception e) {
			logger.error("update()", e);
			return RestResultDto.newFalid("更新失败", e.getMessage());
		}

	}

	/**
	 * 列表页中点击【查看】链接加载指定tenant信息。
	 * 
	 * @param id
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "getTenantList" + BACK_DYNAMIC_SUFFIX)
	public RestResultDto<List<Tenant>> getTenantList(HttpServletRequest request, Model model) {
		List<Tenant> list = null;
		try {
			list = tenantService.findAll();
			return RestResultDto.newSuccess(list);
		} catch (Exception e) {
			logger.error("getTenantList()", e);
			return RestResultDto.newFalid("获取列表失败", e.getMessage());
		}

	}
	/**
	 * 获取租户url
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "getTenantUrl" + FORE_DYNAMIC_SUFFIX, method = RequestMethod.POST)
	public RestResultDto<TenantUrlDto> getTenantUrl(HttpServletRequest request) {
		try {
			LoginReturnInfoDto loginInfo = super.getLoginInfo(request);
			String tenantId = loginInfo.getTenantId();
			TenantUrlDto tenantUrl = tenantService.getTenantUrl(tenantId);
			return RestResultDto.newSuccess(tenantUrl);
		} catch (Exception e) {
			logger.error("getTenantUrl()", e);
			return RestResultDto.newFalid("获取列表失败", e.getMessage());
		}
		
	}

}
