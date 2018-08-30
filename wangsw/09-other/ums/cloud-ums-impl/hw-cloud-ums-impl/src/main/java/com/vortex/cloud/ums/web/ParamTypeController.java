package com.vortex.cloud.ums.web;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.vortex.cloud.ums.annotation.FunctionCode;
import com.vortex.cloud.ums.dataaccess.service.IParamSettingService;
import com.vortex.cloud.ums.dataaccess.service.IParamTypeService;
import com.vortex.cloud.ums.enums.ResponseType;
import com.vortex.cloud.ums.model.PramSetting;
import com.vortex.cloud.ums.model.PramType;
import com.vortex.cloud.ums.util.CommonUtils;
import com.vortex.cloud.ums.util.web.BaseController;
import com.vortex.cloud.ums.util.support.ForeContext;
import com.vortex.cloud.vfs.common.lang.StringUtil;
import com.vortex.cloud.vfs.common.mapper.JsonMapper;
import com.vortex.cloud.vfs.common.web.springmvc.SpringmvcUtils;
import com.vortex.cloud.vfs.data.dto.DataStore;
import com.vortex.cloud.vfs.data.dto.RestResultDto;
import com.vortex.cloud.vfs.data.support.SearchFilter;
import com.vortex.cloud.vfs.data.support.SearchFilter.Operator;








@SuppressWarnings("all")
  @RestController      
@RequestMapping("cloud/management/paramType")
public class ParamTypeController extends BaseController {
	private Logger logger = LoggerFactory.getLogger(ParamTypeController.class);
	
	private static final String FORE_DYNAMIC_SUFFIX = com.vortex.cloud.ums.support.ManagementConstant.PERMISSION_SUFFIX_SA;
	private static final String BACK_DYNAMIC_SUFFIX = com.vortex.cloud.ums.support.ManagementConstant.PERMISSION_SUFFIX_SA;

	@Resource
	private IParamTypeService paramTypeService;
	
	@Resource
	private IParamSettingService paramSettingService;


	@RequestMapping(value = "save" + BACK_DYNAMIC_SUFFIX, method = RequestMethod.POST)
	
	@FunctionCode(value = "CF_MANAGE_PARAM_TYPE_ADD", type = ResponseType.Json)
	public RestResultDto<Boolean> save(HttpServletRequest request, PramType newparameterType) {
		try {
			paramTypeService.save(newparameterType);
			return RestResultDto.newSuccess(true, "保存成功");
		} catch (Exception e) {
			logger.error("ParamTypeController.save", e);
			return RestResultDto.newFalid("保存失败", e.getMessage());
		}
	}

	@RequestMapping(value = "checkForm/{param}" + FORE_DYNAMIC_SUFFIX, method = RequestMethod.POST)
	public RestResultDto<Boolean> checkForm(@PathVariable("param") String paramName) {
		String paramVal = SpringmvcUtils.getParameter(paramName);
		String id = SpringmvcUtils.getParameter("id");
		if (StringUtil.isNullOrEmpty(paramVal)) {
			return RestResultDto.newSuccess(false);
		}
		
		if ("typeCode".equals(paramName)) {
			
			List<SearchFilter> filterList = new ArrayList<SearchFilter>();
			SearchFilter filter = new SearchFilter("parameterType.typeCode", SearchFilter.Operator.EQ, paramVal);
			filterList.add(filter);
			
			List<PramType> list = paramTypeService.findListByFilter(filterList, null);
			if(CollectionUtils.isEmpty(list)) {
				return RestResultDto.newSuccess(true);
			}

			// 更新时，排除自身
			if (!StringUtil.isNullOrEmpty(id) 
					&& list.size() == 1 && list.get(0).getId().equals(id)) {
				return RestResultDto.newSuccess(true);
			}
		}else if ("typeName".equals(paramName)) {
			List<SearchFilter> searchFilter = new ArrayList<SearchFilter>();
			SearchFilter filter = new SearchFilter("parameterType.typeName", SearchFilter.Operator.EQ, paramVal);
			searchFilter.add(filter);
			
			List<PramType> list = paramTypeService.findListByFilter(searchFilter, null);
			
			if(CollectionUtils.isEmpty(list)) {
				return RestResultDto.newSuccess(true);
			}
			
			// 更新时，排除自身
			if (!StringUtil.isNullOrEmpty(id) 
					&& list.size() == 1 && list.get(0).getId().equals(id)) {
				return RestResultDto.newSuccess(true);
			}
		}
		return RestResultDto.newSuccess(false);
	}

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
			logger.error("ParamTypeController.pageList", e);
			return RestResultDto.newFalid("查询失败", e.getMessage());
		}
	}

	@RequestMapping(value = "loadParamTypeDtl" + FORE_DYNAMIC_SUFFIX, method = RequestMethod.POST)
	public RestResultDto<String> loadParamTypeDtl(HttpServletResponse response){
		try {
			String id = SpringmvcUtils.getParameter("id");
			JsonMapper jsonMapper = new JsonMapper();
			PramType dto = paramTypeService.findOne(id);
			return RestResultDto.newSuccess(jsonMapper.toJson(dto));
		} catch (Exception e) {
			logger.error("ParamTypeController.loadParamTypeDtl", e);
			return RestResultDto.newFalid("查询失败", e.getMessage());
		}
	}


	@RequestMapping(value = "delete" + BACK_DYNAMIC_SUFFIX, method = RequestMethod.POST)
	@FunctionCode(value = "CF_MANAGE_PARAM_TYPE_DEL", type = ResponseType.Json)
	public RestResultDto<Boolean> delete(HttpServletRequest request) {
		try {
			String id = SpringmvcUtils.getParameter("id");
			List<SearchFilter> filterList = new ArrayList<SearchFilter>();
			filterList.add(new SearchFilter("typeId", Operator.EQ, id));
			List<PramSetting> list = paramSettingService.findListByFilter(filterList, null);
			if (CollectionUtils.isNotEmpty(list)) {
				return RestResultDto.newFalid("删除失败，该类型下存在值");
			} else {
				paramTypeService.delete(id);
				return RestResultDto.newSuccess(true, "删除成功");
			}
			
			
		} catch (Exception e) {
			logger.error("ParamTypeController.delete", e);
			return RestResultDto.newFalid("删除失败", e.getMessage());
		}
		
	}
}
