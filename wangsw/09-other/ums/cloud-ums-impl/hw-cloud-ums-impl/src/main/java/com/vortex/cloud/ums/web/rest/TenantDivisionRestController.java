package com.vortex.cloud.ums.web.rest;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.collect.Maps;
import com.vortex.cloud.ums.dataaccess.service.ITenantDivisionService;
import  com.vortex.cloud.vfs.data.dto.LoginReturnInfoDto;
import com.vortex.cloud.ums.dto.TenantDivisionDto;
import com.vortex.cloud.ums.model.TenantDivision;
import com.vortex.cloud.ums.support.ManagementConstant;
import com.vortex.cloud.ums.tree.TenantDivisionTree;
import com.vortex.cloud.ums.util.web.BaseController;
import com.vortex.cloud.ums.util.tree.ITreeService;
import com.vortex.cloud.vfs.common.exception.VortexException;
import com.vortex.cloud.vfs.common.mapper.JsonMapper;
import com.vortex.cloud.vfs.common.web.springmvc.SpringmvcUtils;
import com.vortex.cloud.vfs.data.dto.RestResultDto;

/**
 * @author LiShijun
 * @date 2016年3月29日 下午3:50:13
 * @Description 租户行政区划对外提供的RESTful web service History <author> <time> <desc>
 */
@SuppressWarnings("all")
@RestController
@RequestMapping("cloud/management/rest/tenant/division")
public class TenantDivisionRestController extends BaseController {
	private Logger logger = LoggerFactory.getLogger(TenantDivisionRestController.class);
	private JsonMapper jm = new JsonMapper();
	private static final Object IDS = "ids";
	private static final String REQ_PARAM_DIVISION_ID = "divisionId";
	private static final Object LEVEL = "level";
	private static final String CONTAINS_ROOT = "containsRoot";

	@Resource
	private ITreeService treeService;

	@Resource
	private ITenantDivisionService tenantDivisionService;

	/**
	 * 获取行政区划树
	 * 
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "loadDivisionTree", method = { RequestMethod.GET, RequestMethod.POST })
	public RestResultDto loadDivisionTree(HttpServletRequest request) {
		String msg = null;
		Integer result = ManagementConstant.REST_RESULT_SUCC;
		Object data = null;
		String exception = null;
		RestResultDto restResultDto = new RestResultDto();

		try {
			LoginReturnInfoDto loginInfo = super.getLoginInfo(request);
			String tenantId = loginInfo.getTenantId();

			Map<String, String> paramMap = jm.fromJson(SpringmvcUtils.getParameter(ManagementConstant.REST_PMS), Map.class);
			/*
			 * if (MapUtils.isEmpty(paramMap)) { throw new
			 * VortexException("参数不能为空"); }
			 */
			String divisionId = null;
			if (MapUtils.isNotEmpty(paramMap)) {
				divisionId = paramMap.get(REQ_PARAM_DIVISION_ID);
			}
			// 注意：divisionId不是必须的请求参数，如果没有传入，则取租户下整颗树

			if (logger.isDebugEnabled()) {
				logger.debug("loadDivisionTree(), tenantId=" + tenantId + ",divisionId=" + divisionId);
			}

			TenantDivisionTree tree = TenantDivisionTree.getInstance();
			tree.reloadTenantDivisionTree(tenantId, divisionId);
			String jsonStr = treeService.generateJsonCheckboxTree(tree, false);

			result = RestResultDto.RESULT_SUCC;
			msg = "成功构造树";
			data = jsonStr;
		} catch (Exception e) {
			e.printStackTrace();
			result = RestResultDto.RESULT_FAIL;
			msg = "获取树失败:" + e.getMessage();
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

	/**
	 * 获取行政区划列表
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "getDivisionList", method = { RequestMethod.GET, RequestMethod.POST })
	public RestResultDto getDivisionList(HttpServletRequest request) {
		String msg = null;
		Integer result = RestResultDto.RESULT_SUCC;
		Object data = null;
		String exception = null;
		RestResultDto restResultDto = new RestResultDto();
		try {
			LoginReturnInfoDto loginInfo = super.getLoginInfo(request);
			String tenantId = loginInfo.getTenantId();
			TenantDivisionDto tenantDivision = new TenantDivisionDto();
			String divisionId = "";
			Integer level;
			String containsRoot;// 是否包含root节点 ，1包含，0不包含
			Map<String, Object> paramMap = jm.fromJson(SpringmvcUtils.getParameter(ManagementConstant.REST_PMS), Map.class);
			if (MapUtils.isNotEmpty(paramMap)) {
				level = (Integer) paramMap.get(LEVEL);
				tenantDivision.setLevel(level);
				divisionId = (String) (paramMap.get(REQ_PARAM_DIVISION_ID));
				tenantDivision.setParentId(divisionId);
				containsRoot = (String) (paramMap.get(CONTAINS_ROOT));
				tenantDivision.setContainsRoot(containsRoot);
			}
			tenantDivision.setTenantId(tenantId);

			// 注意：divisionId不是必须的请求参数，如果没有传入，则取租户下整颗树

			if (logger.isDebugEnabled()) {
				logger.debug("loadDivisionTree(), tenantId=" + tenantId + ",divisionId=" + divisionId);
			}

			List<TenantDivision> list = tenantDivisionService.findTenantDivisionList(tenantDivision);

			result = RestResultDto.RESULT_SUCC;
			msg = "成功获取行政区划列表";
			data = list;
		} catch (Exception e) {
			e.printStackTrace();
			result = RestResultDto.RESULT_FAIL;
			msg = e.getMessage();
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

	/**
	 * 根据id获取行政区划名称
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "getDivisionNamesByIds", method = { RequestMethod.GET, RequestMethod.POST })
	public RestResultDto getDivisionNamesByIds(HttpServletRequest request) {
		String msg = null;
		Integer result = RestResultDto.RESULT_SUCC;
		Object data = null;
		String exception = null;
		RestResultDto restResultDto = new RestResultDto();
		try {
			LoginReturnInfoDto loginInfo = super.getLoginInfo(request);
			String tenantId = loginInfo.getTenantId();

			Map<String, Object> paramMap = jm.fromJson(SpringmvcUtils.getParameter(ManagementConstant.REST_PMS), Map.class);
			if (MapUtils.isEmpty(paramMap)) {
				throw new VortexException("参数不能为空");
			}

			List<String> ids = (List<String>) paramMap.get(IDS);

			Map<String, String> nameMap = null;
			if (CollectionUtils.isEmpty(ids)) { // 数组为空就返回空map
				nameMap = Maps.newHashMap();
			} else {
				nameMap = tenantDivisionService.getDivisionNamesByIds(ids);
			}
			result = RestResultDto.RESULT_SUCC;
			msg = "成功获取行政区划名称";
			data = nameMap;
		} catch (Exception e) {
			e.printStackTrace();
			result = RestResultDto.RESULT_FAIL;
			msg = "获取行政区划名称失败";
			data = null;
			exception = e.getMessage();
			logger.error(msg, e);
		} finally {
			restResultDto.setResult(result);
			restResultDto.setMsg(msg);
			restResultDto.setData(data);
			restResultDto.setException(exception);
		}
		return restResultDto;
	}

	/**
	 * 根据ids获取行政区划names
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "getDivisionIdsByNames", method = { RequestMethod.GET, RequestMethod.POST })
	public RestResultDto getDivisionIdsByNames(HttpServletRequest request) {
		String msg = null;
		Integer result = RestResultDto.RESULT_SUCC;
		Object data = null;
		String exception = null;
		RestResultDto restResultDto = new RestResultDto();
		try {
			// LoginReturnInfoDto loginInfo = super.getLoginInfo(request);
			// String tenantId = loginInfo.getTenantId();
			Map<String, Object> paramMap = jm.fromJson(SpringmvcUtils.getParameter(ManagementConstant.REST_PMS), Map.class);
			if (MapUtils.isEmpty(paramMap)) {
				throw new VortexException("参数不能为空");
			}
			String tenantId = (String) paramMap.get("tenantId");
			if (StringUtils.isEmpty(tenantId)) {
				logger.error("请传入参数：" + "tenantId");
				throw new VortexException("请传入参数：" + "tenantId");
			}
			List<String> names = (List<String>) paramMap.get("names");
			Map<String, String> nameMap = null;
			if (CollectionUtils.isEmpty(names)) { // 数组为空就返回空map
				nameMap = Maps.newHashMap();
			} else {
				nameMap = tenantDivisionService.getDivisionIdsByNames(names, tenantId);
			}
			result = RestResultDto.RESULT_SUCC;
			msg = "成功获取行政区划id";
			data = nameMap;
		} catch (Exception e) {
			e.printStackTrace();
			result = RestResultDto.RESULT_FAIL;
			msg = "获取行政区划id失败";
			data = null;
			exception = e.getMessage();
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
