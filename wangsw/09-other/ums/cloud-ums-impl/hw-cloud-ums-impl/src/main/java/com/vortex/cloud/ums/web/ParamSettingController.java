/*
 * Copyright (C), 2005-2014, 苏州伏泰信息科技有限公司
 * FileName: ParameterSettingController.java
 * Author:   hb
 * Date:     2014年8月15日 下午5:10:11
 * Description: //模块目的、功能描述
 * History: //修改记录
 * <author>      <time>           <desc>
 *  胡斌             2014年8月15日               加入模版
 */
package com.vortex.cloud.ums.web;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vortex.cloud.ums.annotation.FunctionCode;
import com.vortex.cloud.ums.dataaccess.service.IParamSettingService;
import com.vortex.cloud.ums.enums.ResponseType;
import com.vortex.cloud.ums.model.PramSetting;
import com.vortex.cloud.ums.util.CommonUtils;
import com.vortex.cloud.ums.util.web.BaseController;
import com.vortex.cloud.ums.util.support.ForeContext;
import com.vortex.cloud.vfs.common.web.springmvc.SpringmvcUtils;
import com.vortex.cloud.vfs.data.dto.DataStore;
import com.vortex.cloud.vfs.data.dto.RestResultDto;
import com.vortex.cloud.vfs.data.support.SearchFilter;

/**
 * 参数设置
 * 
 * @author hb
 * @version 2014年8月15日 下午5:51:46
 * @since jdk1.6 标准品 1.0
 */
@RestController
@RequestMapping("cloud/management/paramSetting")
public class ParamSettingController extends BaseController {
	private static final Logger logger = LoggerFactory.getLogger(ParamSettingController.class);

	private static final String FORE_DYNAMIC_SUFFIX = com.vortex.cloud.ums.support.ManagementConstant.PERMISSION_SUFFIX_SA;
	private static final String BACK_DYNAMIC_SUFFIX = com.vortex.cloud.ums.support.ManagementConstant.PERMISSION_SUFFIX_SA;

	@Resource
	private IParamSettingService paramSettingService;

	@RequestMapping(value = "save" + BACK_DYNAMIC_SUFFIX, method = RequestMethod.POST)

	@FunctionCode(value = "CF_MANAGE_PARAM_ADD", type = ResponseType.Json)
	public RestResultDto<Boolean> save(HttpServletRequest request, PramSetting newparameterSetting) {
		try {
			paramSettingService.save(newparameterSetting);
			return RestResultDto.newSuccess(true, "保存成功");
		} catch (Exception e) {
			logger.error("ParamSettingController.save", e);
			return RestResultDto.newFalid("保存失败", e.getMessage());
		}
	}

	@RequestMapping(value = "pageList" + FORE_DYNAMIC_SUFFIX, method = RequestMethod.POST)

	public RestResultDto<DataStore<PramSetting>> pageList(HttpServletRequest request, HttpServletResponse response) {
		try {
			// 搜索条件
			List<SearchFilter> filterList = CommonUtils.buildFromHttpRequest(request);

			// 分页排序条件
			Pageable pageable = ForeContext.getPageable(request, new Sort(Direction.ASC, "orderIndex"));

			// 获取分页
			Page<PramSetting> page = paramSettingService.findPageByFilter(pageable, filterList);

			DataStore<PramSetting> ds = null;
			if (page != null) {
				ds = new DataStore<PramSetting>(page.getTotalElements(), page.getContent());
			} else {
				ds = new DataStore<PramSetting>();
			}
			return RestResultDto.newSuccess(ds);
		} catch (Exception e) {
			logger.error("ParamSettingController.save", e);
			return RestResultDto.newFalid("查询失败", e.getMessage());
		}
	}

	@RequestMapping(value = "load" + FORE_DYNAMIC_SUFFIX, method = RequestMethod.POST)
	public RestResultDto<PramSetting> load(HttpServletRequest request, HttpServletResponse response) {
		try {
			String id = SpringmvcUtils.getParameter("id");
			return RestResultDto.newSuccess(paramSettingService.findOne(id));
		} catch (Exception e) {
			logger.error("ParamSettingController.load", e);
			return RestResultDto.newFalid("查询失败", e.getMessage());
		}
	}

	@RequestMapping(value = "delete" + BACK_DYNAMIC_SUFFIX, method = RequestMethod.POST)

	@FunctionCode(value = "CF_MANAGE_PARAM_DEL", type = ResponseType.Json)
	public RestResultDto<Boolean> delete(HttpServletRequest request) {
		try {
			String id = SpringmvcUtils.getParameter("id");
			paramSettingService.delete(id);
			return RestResultDto.newSuccess(true, "删除成功");
		} catch (Exception e) {
			logger.error("ParamSettingController.delete", e);
			return RestResultDto.newFalid("删除失败", e.getMessage());
		}
	}

	@RequestMapping(value = "deletes" + BACK_DYNAMIC_SUFFIX, method = RequestMethod.POST)

	@FunctionCode(value = "CF_MANAGE_PARAM_BAT_DEL", type = ResponseType.Json)
	public RestResultDto<Boolean> deletes(HttpServletRequest request, @RequestParam("ids") String id) {
		try {
			paramSettingService.delete(id.split(","));
			return RestResultDto.newSuccess(true, "删除成功");
		} catch (Exception e) {
			logger.error("ParamSettingController.deletes", e);
			return RestResultDto.newFalid("删除失败", e.getMessage());
		}
	}
}