package com.vortex.cloud.ums.web;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.vortex.cloud.ums.annotation.FunctionCode;
import com.vortex.cloud.ums.dataaccess.service.ITenantConstantService;
import com.vortex.cloud.ums.enums.ResponseType;
import com.vortex.cloud.ums.model.CloudConstant;
import com.vortex.cloud.ums.util.web.BaseController;
import com.vortex.cloud.ums.util.support.ForeContext;
import com.vortex.cloud.vfs.common.mapper.JsonMapper;
import com.vortex.cloud.vfs.common.web.springmvc.SpringmvcUtils;
import com.vortex.cloud.vfs.data.dto.DataStore;
import com.vortex.cloud.vfs.data.dto.RestResultDto;
import com.vortex.cloud.vfs.data.support.SearchFilter;
import com.vortex.cloud.vfs.data.support.SearchFilter.Operator;
import com.vortex.cloud.vfs.data.support.SearchFilters;

/**
 * 百度云 - 常量维护
 * 
 * @author lishijun
 *
 */
@SuppressWarnings("all")
@RestController
@RequestMapping("cloud/management/tenant/constant")
public class TenantConstantController extends BaseController {
	private static final Logger logger = LoggerFactory.getLogger(TenantConstantController.class);

	private static final String FORE_DYNAMIC_SUFFIX = com.vortex.cloud.ums.support.ManagementConstant.PERMISSION_SUFFIX_SA;
	private static final String BACK_DYNAMIC_SUFFIX = com.vortex.cloud.ums.support.ManagementConstant.PERMISSION_SUFFIX_SA;

	@Resource
	private ITenantConstantService tenantConstantService;

	/**
	 * 用于表单项的验证
	 * 
	 * @param paramName
	 * @return
	 */
	@RequestMapping(value = "validate/{param}" + FORE_DYNAMIC_SUFFIX, method = RequestMethod.POST)

	public RestResultDto<Boolean> validate(@PathVariable("param") String paramName) {
		try {

			// 入参非空校验
			if (StringUtils.isBlank(paramName)) {
				return RestResultDto.newSuccess(false);
			}

			String paramVal = SpringmvcUtils.getParameter(paramName);
			if (StringUtils.isBlank(paramVal)) {
				return RestResultDto.newSuccess(false);
			}

			// 此方法只校验常量名，是否与其他租户的常量名重复
			if (!("constantCode".equals(paramName))) {
				return RestResultDto.newSuccess(true);
			}

			String id = SpringmvcUtils.getParameter("id"); // 更新记录时，也要校验
			String tenantId = SpringmvcUtils.getParameter("tenantId");

			String constantCode = null;
			if ("constantCode".equals(paramName)) {
				constantCode = paramVal;
			}

			logger.info("validate(): tenantId=" + tenantId + ",id=" + id + ",constantCode=" + constantCode);

			List<SearchFilter> filterList = new ArrayList<SearchFilter>();
			SearchFilter filter = null;

			if (StringUtils.isNotBlank(tenantId)) {
				filter = new SearchFilter("tenantId", SearchFilter.Operator.EQ, tenantId);
				filterList.add(filter);
			}

			if (StringUtils.isNotBlank(id)) { // 更新记录时，查找是否存在其他记录，有字段重复
				filter = new SearchFilter("id", SearchFilter.Operator.NE, id);
				filterList.add(filter);
			}

			if (StringUtils.isNotBlank(constantCode)) {
				filter = new SearchFilter("constantCode", SearchFilter.Operator.EQ, constantCode.trim());
				filterList.add(filter);
			}

			boolean isExist = tenantConstantService.isExistConstantCode(filterList);

			return RestResultDto.newSuccess(!isExist);
		} catch (Exception e) {
			logger.error("校验出错", e);
			return RestResultDto.newFalid("校验出错", e.getMessage());
		}
	}

	/**
	 * 新增记录
	 * 
	 * @param dto
	 * @return
	 */
	@RequestMapping(value = "add" + BACK_DYNAMIC_SUFFIX, method = RequestMethod.POST)

	@FunctionCode(value = "CF_MANAGE_TENANT_CONS_ADD", type = ResponseType.Json)
	public RestResultDto<Boolean> add(HttpServletRequest request, CloudConstant entity) {

		try {
			tenantConstantService.save(entity);
			return RestResultDto.newSuccess(true, "添加成功");
		} catch (Exception e) {
			logger.error("add()", e);
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
	@FunctionCode(value = "CF_MANAGE_TENANT_CONS_LIST", type = ResponseType.Json)
	public RestResultDto<DataStore<CloudConstant>> pageList(HttpServletRequest request) throws Exception {
		try {

			String tenantId = SpringmvcUtils.getParameter("tenantId");
			String keyword = SpringmvcUtils.getParameter("keyword");

			// 分页参数 + 排序
			Pageable pageable = ForeContext.getPageable(request, null);

			// 查询条件
			List<SearchFilter> filterList = new ArrayList<SearchFilter>();

			// AND tenantId = ?
			SearchFilters searchFilters = new SearchFilters(SearchFilters.Operator.AND);
			searchFilters.add(new SearchFilter("tenantId", Operator.EQ, tenantId));

			// ( constantCode LIKE ? OR constantValue LIKE ? OR
			// constantDescription
			// LIKE ? )
			if (StringUtils.isNotBlank(keyword)) {
				SearchFilters searchFilters1 = new SearchFilters(SearchFilters.Operator.OR);

				searchFilters1.add(new SearchFilter("constantCode", Operator.LIKE, keyword));
				searchFilters1.add(new SearchFilter("constantValue", Operator.LIKE, keyword));
				searchFilters1.add(new SearchFilter("constantDescription", Operator.LIKE, keyword));

				searchFilters.add(searchFilters1);
			}

			// 获取分页结果
			Page<CloudConstant> page = tenantConstantService.findPageByFilters(pageable, searchFilters);

			// 返回分页
			DataStore<CloudConstant> ds = new DataStore<CloudConstant>();
			if (page != null) {
				ds.setTotal(page.getTotalElements());
				ds.setRows(page.getContent());
			}
			return RestResultDto.newSuccess(ds);
		} catch (Exception e) {
			return RestResultDto.newFalid("加载列表分页出错", e.getMessage());
		}
	}

	@RequestMapping(value = "loadCloudConstantDtl" + FORE_DYNAMIC_SUFFIX, method = RequestMethod.POST)
	public RestResultDto<CloudConstant> loadCloudConstantDtl(HttpServletResponse response) {
		try {

			String id = SpringmvcUtils.getParameter("id");
			JsonMapper jsonMapper = new JsonMapper();
			CloudConstant dto = tenantConstantService.findOne(id);
			return RestResultDto.newSuccess(dto);
		} catch (Exception e) {
			logger.error("根据id加载常量失败", e);
			return RestResultDto.newFalid("根据id加载常量失败", e.getMessage());
		}
	}

}
