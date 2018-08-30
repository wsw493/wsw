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
import org.springframework.web.bind.annotation.RestController;

import com.vortex.cloud.ums.annotation.FunctionCode;
import com.vortex.cloud.ums.dataaccess.service.IParamTypeService;
import com.vortex.cloud.ums.enums.ResponseType;
import com.vortex.cloud.ums.model.PramType;
import com.vortex.cloud.ums.util.CommonUtils;
import com.vortex.cloud.ums.util.web.BaseController;
import com.vortex.cloud.ums.util.support.ForeContext;
import com.vortex.cloud.vfs.common.mapper.JsonMapper;
import com.vortex.cloud.vfs.common.web.springmvc.SpringmvcUtils;
import com.vortex.cloud.vfs.data.dto.DataStore;
import com.vortex.cloud.vfs.data.dto.RestResultDto;
import com.vortex.cloud.vfs.data.support.SearchFilter;

@SuppressWarnings("all")
@RestController
@RequestMapping("cloud/management/tenant/paramType")
public class TenantParamTypeController extends BaseController {
	private Logger logger = LoggerFactory.getLogger(TenantParamTypeController.class);

	private static final String FORE_DYNAMIC_SUFFIX = com.vortex.cloud.ums.support.ManagementConstant.PERMISSION_SUFFIX_SA;
	private static final String BACK_DYNAMIC_SUFFIX = com.vortex.cloud.ums.support.ManagementConstant.PERMISSION_SUFFIX_SA;

	private static final String LIST_TITLE = "参数类型";
	private static final String LIST_VIEW_TITLE = "查看参数类型";

	@Resource
	private IParamTypeService paramTypeService;

	@RequestMapping(value = "pageList" + FORE_DYNAMIC_SUFFIX, method = RequestMethod.POST)
	public RestResultDto<DataStore<PramType>> pageList(HttpServletRequest request, HttpServletResponse response) {
		try {

			// 过滤map
			List<SearchFilter> searchFilter = CommonUtils.buildFromHttpRequest(request);

			Sort defaultSort = new Sort(Direction.ASC, "orderIndex");
			Pageable pageable = ForeContext.getPageable(request, defaultSort);
			Page<PramType> page = paramTypeService.findPageByFilter(pageable, searchFilter);

			DataStore<PramType> ds = null;
			if (page == null) {
				ds = new DataStore<PramType>();
			} else {
				ds = new DataStore<PramType>(page.getTotalElements(), page.getContent());
			}
			return RestResultDto.newSuccess(ds);
		} catch (Exception e) {
			logger.error("获取列表分页出错", e);
			return RestResultDto.newFalid("获取列表分页出错", e.getMessage());
		}
	}

	@RequestMapping(value = "loadParamTypeDtl" + FORE_DYNAMIC_SUFFIX, method = RequestMethod.POST)
	public RestResultDto<PramType> loadParamTypeDtl(HttpServletResponse response) {
		try {

			String id = SpringmvcUtils.getParameter("id");
			JsonMapper jsonMapper = new JsonMapper();
			PramType dto = paramTypeService.findOne(id);
			return RestResultDto.newSuccess(dto);
		} catch (Exception e) {
			logger.error("根据id获取参数类型成功", e);
			return RestResultDto.newFalid("根据id获取参数类型成功", e.getMessage());
		}

	}
}
