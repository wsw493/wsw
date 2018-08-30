package com.vortex.cloud.ums.web.rest.np;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.collect.Lists;
import com.vortex.cloud.ums.dataaccess.service.IParamTypeService;
import com.vortex.cloud.ums.dataaccess.service.ITenantParamSettingService;
import com.vortex.cloud.ums.model.PramType;
import com.vortex.cloud.ums.support.ManagementConstant;
import com.vortex.cloud.vfs.common.exception.VortexException;
import com.vortex.cloud.vfs.common.mapper.JsonMapper;
import com.vortex.cloud.vfs.common.web.springmvc.SpringmvcUtils;
import com.vortex.cloud.vfs.data.dto.RestResultDto;
import com.vortex.cloud.vfs.data.support.SearchFilter;
import com.vortex.cloud.vfs.data.support.SearchFilter.Operator;

@RestController
@RequestMapping("cloud/management/rest/np/param/type")
public class ParamTypeRestNpController {
	@Resource
	private ITenantParamSettingService tenantParamSettingService;
	@Resource
	private IParamTypeService paramTypeService;
	private static final Logger logger = LoggerFactory.getLogger(ParamTypeRestNpController.class);

	private static final String REQ_PARAM_TYPE_CODE_LIST = "paramTypeCodeList"; // 多个参数类型

	/**
	 * 通过参数类型Code，获取参数类型列表
	 * 
	 * @param request
	 * @return
	 * 
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "getTypeListByParamTypeCode", method = { RequestMethod.GET, RequestMethod.POST })
	public RestResultDto getByParamTypeCode(HttpServletRequest request) {
		String msg = null;
		Integer result = ManagementConstant.REST_RESULT_SUCC;
		Object data = null;
		String exception = null;
		RestResultDto restResultDto = new RestResultDto();

		try {
			JsonMapper jm = new JsonMapper();
			Map<String, Object> paramMap = jm.fromJson(SpringmvcUtils.getParameter(ManagementConstant.REST_PMS), Map.class);
			if (MapUtils.isEmpty(paramMap)) {
				throw new VortexException("参数不能为空");
			}

			List<String> paramTypeCodeList = (List<String>) paramMap.get(REQ_PARAM_TYPE_CODE_LIST);

			if (CollectionUtils.isEmpty(paramTypeCodeList)) {
				throw new VortexException("请传入参数：" + REQ_PARAM_TYPE_CODE_LIST);
			}
			List<SearchFilter> searchFilters = Lists.newArrayList();
			searchFilters.add(new SearchFilter("typeCode", Operator.IN, paramTypeCodeList.toArray()));
			List<PramType> list = paramTypeService.findListByFilter(searchFilters, new Sort(Direction.ASC, "orderIndex"));

			msg = "成功获取到参数类型列表";
			data = list;
		} catch (Exception e) {
			e.printStackTrace();
			result = ManagementConstant.REST_RESULT_FAIL;
			msg = "获取参数类型列表失败！";
			exception = e.getMessage();
			data = null;
			logger.error(msg, e);
		} finally {
			restResultDto.setResult(result);
			restResultDto.setMsg(msg);
			restResultDto.setData(data);
			restResultDto.setException(exception);
		}

		return restResultDto;
	}

}
