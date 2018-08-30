package com.vortex.cloud.ums.web;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.vortex.cloud.ums.annotation.FunctionCode;
import com.vortex.cloud.ums.dataaccess.service.ITenantParamSettingService;
import com.vortex.cloud.ums.enums.ResponseType;
import com.vortex.cloud.ums.model.TenantPramSetting;
import com.vortex.cloud.ums.support.ManagementConstant;
import com.vortex.cloud.ums.util.CommonUtils;
import com.vortex.cloud.ums.util.support.ForeContext;
import com.vortex.cloud.ums.util.tree.ITreeService;
import com.vortex.cloud.ums.util.web.BaseController;
import com.vortex.cloud.vfs.common.web.springmvc.SpringmvcUtils;
import com.vortex.cloud.vfs.data.dto.DataStore;
import com.vortex.cloud.vfs.data.dto.LoginReturnInfoDto;
import com.vortex.cloud.vfs.data.dto.RestResultDto;
import com.vortex.cloud.vfs.data.support.SearchFilter;

/**
 * @author LiShijun
 * @date 2016年3月24日 下午4:25:35
 * @Description 参数设置 History <author> <time> <desc>
 */
@RestController
@RequestMapping("cloud/management/tenant/paramSetting")
public class TenantParamSettingController extends BaseController {
	private static final Logger logger = LoggerFactory.getLogger(TenantParamSettingController.class);

	private static final String FORE_DYNAMIC_SUFFIX = com.vortex.cloud.ums.support.ManagementConstant.PERMISSION_SUFFIX_SA;
	private static final String BACK_DYNAMIC_SUFFIX = com.vortex.cloud.ums.support.ManagementConstant.PERMISSION_SUFFIX_SA;

	@Resource
	private ITenantParamSettingService tenantParamSettingService;

	@Resource
	private ITreeService treeService;

	// 时间转化问题
	@InitBinder
	public void initBinder(WebDataBinder binder) {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		dateFormat.setLenient(true);
		binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
	}

	@RequestMapping(value = "save" + BACK_DYNAMIC_SUFFIX, method = RequestMethod.POST)
	@FunctionCode(value = "CF_MANAGE_TENANT_PARAM_ADD", type = ResponseType.Json)
	public RestResultDto<Boolean> save(HttpServletRequest request, TenantPramSetting newparameterSetting) {

		try {
			LoginReturnInfoDto loginInfo = super.getLoginInfo(request);
			newparameterSetting.setTenantId(loginInfo.getTenantId());
			tenantParamSettingService.save(newparameterSetting);
			return RestResultDto.newSuccess(true, "添加成功");
		} catch (Exception e) {
			logger.error("save()", e);
			return RestResultDto.newFalid("添加失败", e.getMessage());
		}
	}

	@RequestMapping(value = "pageList" + FORE_DYNAMIC_SUFFIX, method = RequestMethod.POST)
	public RestResultDto<DataStore<TenantPramSetting>> pageList(HttpServletRequest request, HttpServletResponse response) {
		try {

			List<SearchFilter> searchFilter = CommonUtils.buildFromHttpRequest(request);

			LoginReturnInfoDto loginInfo = super.getLoginInfo(request);
			String tenantId = loginInfo.getTenantId();
			SearchFilter filter1 = new SearchFilter("tenantId", SearchFilter.Operator.EQ, tenantId);
			searchFilter.add(filter1);
			// 分页、排序
			Pageable pageable = ForeContext.getPageable(request, new Sort(Direction.ASC, "orderIndex"));

			Page<TenantPramSetting> pageResult = tenantParamSettingService.findPageByFilter(pageable, searchFilter);

			DataStore<TenantPramSetting> ds = null;
			if (pageResult != null) {
				ds = new DataStore<TenantPramSetting>(pageResult.getTotalElements(), pageResult.getContent());
			} else {
				ds = new DataStore<TenantPramSetting>();
			}
			return RestResultDto.newSuccess(ds);
		} catch (Exception e) {
			logger.error("加载分页列表出错", e);
			return RestResultDto.newFalid("加载分页列表出错", e.getMessage());
		}
	}

	@RequestMapping(value = "load" + FORE_DYNAMIC_SUFFIX, method = RequestMethod.POST)
	public RestResultDto<TenantPramSetting> load(HttpServletRequest request, HttpServletResponse response) {
		try {
			String id = SpringmvcUtils.getParameter("id");
			return RestResultDto.newSuccess(tenantParamSettingService.findOne(id));
		} catch (Exception e) {
			logger.error("根据id获取租户参数失败", e);
			return RestResultDto.newFalid("根据id获取租户参数失败", e.getMessage());
		}
	}

	@RequestMapping(value = "delete" + BACK_DYNAMIC_SUFFIX, method = RequestMethod.POST)
	@FunctionCode(value = "CF_MANAGE_TENANT_PARAM_DEL", type = ResponseType.Json)
	public RestResultDto<Boolean> delete(HttpServletRequest request) {
		String id = SpringmvcUtils.getParameter("id");

		try {
			tenantParamSettingService.delete(id);
			return RestResultDto.newSuccess(true, "删除成功");
		} catch (Exception e) {
			logger.error("删除失败", e);
			return RestResultDto.newFalid("删除失败", e.getMessage());
		}

	}

	@RequestMapping(value = "deletes" + BACK_DYNAMIC_SUFFIX, method = RequestMethod.POST)
	@FunctionCode(value = "CF_MANAGE_TENANT_PARAM_BAT_DEL", type = ResponseType.Json)
	public RestResultDto<Boolean> deletes(HttpServletRequest request, @RequestParam("ids") String id) {

		try {
			tenantParamSettingService.delete(id.split(","));
			return RestResultDto.newSuccess(true, "删除成功");
		} catch (Exception e) {
			logger.error("删除失败", e);
			return RestResultDto.newFalid("删除失败", e.getMessage());
		}
	}

	/**
	 * 获取单个参数类型下的参数列表
	 * 
	 * @param request
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "loadSingleParam" + FORE_DYNAMIC_SUFFIX, method = RequestMethod.POST)

	public RestResultDto<List<Map<String, Object>>> loadSingleParam(HttpServletRequest request) {
		try {

			LoginReturnInfoDto loginInfo = super.getLoginInfo(request);
			String tenantId = loginInfo.getTenantId();
			String typeCode = SpringmvcUtils.getParameter("typeCode");

			return RestResultDto.newSuccess(this.getParamList(tenantId, typeCode));
		} catch (Exception e) {
			logger.error("获取单个参数类型下的参数列表出错", e);
			return RestResultDto.newFalid("获取单个参数类型下的参数列表出错", e.getMessage());
		}
	}

	/**
	 * 获取多个参数类型下的参数列表
	 * 
	 * @param request
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "loadMultiParamList" + FORE_DYNAMIC_SUFFIX, method = RequestMethod.POST)
	public RestResultDto<Map<String, List<Map<String, Object>>>> loadMultiParamList(HttpServletRequest request, @RequestBody List<String> typeCodes) {
		try {

			LoginReturnInfoDto loginInfo = super.getLoginInfo(request);
			String tenantId = loginInfo.getTenantId();
			Map<String, List<Map<String, Object>>> params = tenantParamSettingService.loadMultiParamList(tenantId, typeCodes);
			return RestResultDto.newSuccess(params);
		} catch (Exception e) {
			logger.error("获取多个参数类型下的参数列表出错", e);
			return RestResultDto.newFalid("获取多个参数类型下的参数列表出错", e.getMessage());
		}
	}

	/**
	 * 根据参数类型获取到参数列表，用于页面下拉框展示
	 * 
	 * @param tenantId
	 * @param parameterType
	 * @return
	 */
	private List<Map<String, Object>> getParamList(String tenantId, String parameterType) {
		List<Map<String, Object>> selectOptionList = Lists.newArrayList();

		// 获取parameter setting列表
		List<TenantPramSetting> list = this.getByParamTypeCode(tenantId, parameterType);

		if (CollectionUtils.isNotEmpty(list)) {
			Map<String, Object> mapValue = null;
			for (TenantPramSetting entity : list) {
				mapValue = Maps.newHashMap();
				mapValue.put("value", entity.getParmCode());
				mapValue.put("text", entity.getParmName());
				selectOptionList.add(mapValue);
			}
		}

		return selectOptionList;
	}

	/**
	 * 获取指定的参数类型的参数列表
	 * 
	 * @param tenantId
	 * @param parameterType
	 * @return
	 */
	private List<TenantPramSetting> getByParamTypeCode(String tenantId, String parameterType) {
		// 封装获取配置文件参数常量
		if (StringUtils.isBlank(tenantId) || StringUtils.isBlank(parameterType)) {// 若无该配置属性，返回
			return null;
		}

		// 从文件中获取参数类型code
		parameterType = ManagementConstant.getPropertyValue(parameterType);
		if (StringUtils.isBlank(parameterType)) {// 若无该配置属性，返回
			return null;
		}

		// 获取parameter setting列表
		return tenantParamSettingService.findListByParamTypeCode(tenantId, parameterType);
	}

	/**
	 * 获取单个参数类型下的参数列表
	 * 
	 * @param request
	 * 
	 * @param request
	 * @param response
	 * @return Map：key - parmCode, value - parmName
	 */
	@RequestMapping(value = "loadParamMap" + FORE_DYNAMIC_SUFFIX, method = RequestMethod.POST)
	public RestResultDto<Map<String, String>> loadParamMap(HttpServletRequest request) {
		try {

			LoginReturnInfoDto loginInfo = super.getLoginInfo(request);
			String tenantId = loginInfo.getTenantId();
			String typeCode = SpringmvcUtils.getParameter("typeCode");

			return RestResultDto.newSuccess(this.getParamMap(tenantId, typeCode));
		} catch (Exception e) {
			logger.error("根据参数类型获取到参数列表出错", e);
			return RestResultDto.newFalid("根据参数类型获取到参数列表出错", e.getMessage());
		}
	}

	/**
	 * 根据参数类型获取到参数列表，返回数据格式为Map
	 * 
	 * @param tenantId
	 * @param parameterType
	 * @return Map：key - parmCode, value - parmName
	 */
	private Map<String, String> getParamMap(String tenantId, String parameterType) {
		Map<String, String> paramMap = new HashMap<String, String>();

		// 获取parameter setting列表
		List<TenantPramSetting> list = this.getByParamTypeCode(tenantId, parameterType);

		if (CollectionUtils.isNotEmpty(list)) {
			for (TenantPramSetting param : list) {
				paramMap.put(param.getParmCode(), param.getParmName());
			}
		}

		return paramMap;
	}
}