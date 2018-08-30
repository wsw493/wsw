package com.vortex.cloud.ums.web.rest.np;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vortex.cloud.ums.dataaccess.service.IWorkElementTypeService;
import com.vortex.cloud.ums.model.WorkElementType;
import com.vortex.cloud.ums.support.ManagementConstant;
import com.vortex.cloud.vfs.common.exception.VortexException;
import com.vortex.cloud.vfs.common.mapper.JsonMapper;
import com.vortex.cloud.vfs.common.web.MediaTypes;
import com.vortex.cloud.vfs.common.web.springmvc.SpringmvcUtils;
import com.vortex.cloud.vfs.data.dto.RestResultDto;
import com.vortex.cloud.vfs.data.support.SearchFilter;
import com.vortex.cloud.vfs.data.support.SearchFilter.Operator;

/**
 * 图元类型 rest cont
 * 
 * @author SonHo
 *
 */
@RestController
@RequestMapping("cloud/management/rest/np/workElementType")
public class WorkElementTypeRestNpController {
	private static final Logger logger = LoggerFactory.getLogger(WorkElementTypeRestNpController.class);
	private JsonMapper jm = new JsonMapper();

	@Resource
	private IWorkElementTypeService workElementTypeService;

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "queryList" + ManagementConstant.BACK_DYNAMIC_SUFFIX, produces = MediaTypes.JSON_UTF_8)
	public RestResultDto queryList(HttpServletRequest request) {
		RestResultDto restResultDto = new RestResultDto();
		try {
			Map<String, Object> paramMap = jm.fromJson(SpringmvcUtils.getParameter(ManagementConstant.REST_PMS), Map.class);
			if (MapUtils.isEmpty(paramMap)) {
				throw new VortexException("参数不能为空");
			}
			String tenantId = (String) paramMap.get("tenantId");
			if (StringUtils.isEmpty(tenantId)) {
				throw new VortexException("tenantId不能为空");
			}
			String code = (String) paramMap.get("code");
			List<SearchFilter> filters = new ArrayList<SearchFilter>();
			filters.add(new SearchFilter("tenantId", Operator.EQ, tenantId));
			if (StringUtils.isNotEmpty(code)) {
				filters.add(new SearchFilter("code", Operator.LIKE, code));
			}
			Sort sort = new Sort(Direction.ASC, "orderIndex");
			List<WorkElementType> workElementTypes = workElementTypeService.findListByFilter(filters, sort);
			restResultDto.setData(workElementTypes);
			restResultDto.setResult(RestResultDto.RESULT_SUCC);
			restResultDto.setMsg("查询成功");
		} catch (Exception e) {
			restResultDto.setResult(RestResultDto.RESULT_FAIL);
			restResultDto.setMsg("查询失败");
			restResultDto.setException(e.getMessage());
			logger.error("WorkElementTypeRestNpController.queryList", e);
		}
		return restResultDto;
	}

	@RequestMapping(value = "save" + ManagementConstant.BACK_DYNAMIC_SUFFIX, produces = MediaTypes.JSON_UTF_8)
	public RestResultDto save(HttpServletRequest request) {
		RestResultDto restResultDto = new RestResultDto();
		try {
			String json = SpringmvcUtils.getParameter(ManagementConstant.REST_PMS);
			if (StringUtils.isEmpty(json)) {
				throw new VortexException("参数不能为空");
			}
			WorkElementType model = jm.fromJson(json, WorkElementType.class);

			if (StringUtils.isEmpty(model.getTenantId())) {
				throw new VortexException("租户Id不能为空");
			}
			if (StringUtils.isEmpty(model.getCode())) {
				throw new VortexException("编码不能为空");
			} else {
				if (workElementTypeService.isParamExists(null, "code", model.getCode(), model.getTenantId())) {
					throw new VortexException("编码已存在");
				}
			}
			if (StringUtils.isEmpty(model.getName())) {
				throw new VortexException("名称不能为空");
			} else {
				if (workElementTypeService.isParamExists(null, "name", model.getName(), model.getTenantId())) {
					throw new VortexException("名称已存在");
				}
			}
			if (StringUtils.isEmpty(model.getShape())) {
				throw new VortexException("形状不能为空");
			}
			if (null == model.getOrderIndex()) {
				throw new VortexException("排序号不能为空");
			}
			restResultDto.setData(workElementTypeService.save(model));
			restResultDto.setResult(RestResultDto.RESULT_SUCC);
			restResultDto.setMsg("查询成功");
		} catch (Exception e) {
			restResultDto.setResult(RestResultDto.RESULT_FAIL);
			restResultDto.setMsg("查询失败");
			restResultDto.setException(e.getMessage());
			logger.error("WorkElementTypeRestNpController.save", e);
		}
		return restResultDto;
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "findByIds" + ManagementConstant.BACK_DYNAMIC_SUFFIX, produces = MediaTypes.JSON_UTF_8)
	public RestResultDto findByIds(HttpServletRequest request) {
		RestResultDto restResultDto = new RestResultDto();
		try {
			Map<String, Object> paramMap = jm.fromJson(SpringmvcUtils.getParameter(ManagementConstant.REST_PMS), Map.class);
			if (MapUtils.isEmpty(paramMap)) {
				throw new VortexException("参数不能为空");
			}
			String tenantId = (String) paramMap.get("tenantId");
			if (StringUtils.isEmpty(tenantId)) {
				throw new VortexException("租户Id不能为空");
			}
			String ids = (String) paramMap.get("ids");
			if (StringUtils.isEmpty(ids)) {
				throw new VortexException("ids不能为空");
			}
			List<SearchFilter> filters = new ArrayList<SearchFilter>();
			filters.add(new SearchFilter("tenantId", Operator.EQ, tenantId));
			filters.add(new SearchFilter("id", Operator.IN, ids.split(",")));
			Sort sort = new Sort(Direction.ASC, "orderIndex");
			List<WorkElementType> workElementTypes = workElementTypeService.findListByFilter(filters, sort);
			restResultDto.setData(workElementTypes);
			restResultDto.setResult(RestResultDto.RESULT_SUCC);
			restResultDto.setMsg("查询成功");
		} catch (Exception e) {
			restResultDto.setResult(RestResultDto.RESULT_FAIL);
			restResultDto.setMsg("查询失败");
			restResultDto.setException(e.getMessage());
			logger.error("WorkElementTypeRestNpController.findByIds", e);
		}
		return restResultDto;
	}
}
