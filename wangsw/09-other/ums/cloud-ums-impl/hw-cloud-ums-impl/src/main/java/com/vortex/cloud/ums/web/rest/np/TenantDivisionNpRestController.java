package com.vortex.cloud.ums.web.rest.np;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.collect.Maps;
import com.vortex.cloud.ums.dataaccess.service.ITenantDivisionService;
import com.vortex.cloud.ums.dto.TenantDivisionDto;
import com.vortex.cloud.ums.model.TenantDivision;
import com.vortex.cloud.ums.support.ManagementConstant;
import com.vortex.cloud.ums.tree.TenantDivisionTree;
import com.vortex.cloud.ums.util.tree.ITreeService;
import com.vortex.cloud.ums.util.web.BaseController;
import com.vortex.cloud.vfs.common.exception.VortexException;
import com.vortex.cloud.vfs.common.mapper.JsonMapper;
import com.vortex.cloud.vfs.common.web.springmvc.SpringmvcUtils;
import com.vortex.cloud.vfs.data.dto.RestResultDto;

/**
 * 这是没有cas权限控制的api，提供用来被定时器调用
 * 
 * @author lsm
 * @date 2016年3月29日 下午3:50:13
 * @Description 租户行政区划对外提供的RESTful web service History <author> <time> <desc>
 */
@SuppressWarnings("all")
@RestController
@RequestMapping("cloud/management/rest/np/tenant/division")
public class TenantDivisionNpRestController extends BaseController {
	private Logger logger = LoggerFactory.getLogger(TenantDivisionNpRestController.class);
	private JsonMapper jm = new JsonMapper();
	private static final String REQ_PARAM_DIVISION_ID = "divisionId";
	private static final Object IDS = "ids";
	private static final String TENANT_ID = "tenantId";
	private static final Object LEVEL = "level";

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
		Integer result = RestResultDto.RESULT_FAIL;
		Object data = null;
		String exception = null;
		RestResultDto restResultDto = new RestResultDto();

		try {

			Map<String, String> paramMap = jm.fromJson(SpringmvcUtils.getParameter(ManagementConstant.REST_PMS), Map.class);
			/*
			 * if (MapUtils.isEmpty(paramMap)) { throw new VortexException("参数不能为空"); }
			 */
			String tenantId = paramMap.get(TENANT_ID);
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
		Integer result = RestResultDto.RESULT_FAIL;
		Object data = null;
		String exception = null;
		RestResultDto restResultDto = new RestResultDto();
		TenantDivisionDto tenantDivision = new TenantDivisionDto();
		try {

			Map<String, Object> paramMap = jm.fromJson(SpringmvcUtils.getParameter(ManagementConstant.REST_PMS), Map.class);
			// if (MapUtils.isEmpty(paramMap)) {
			// throw new VortexException("参数不能为空");
			// }
			String tenantId = (String) paramMap.get(TENANT_ID);
			Integer level = (Integer) paramMap.get(LEVEL);
			String divisionId = (String) (MapUtils.isEmpty(paramMap) ? null : paramMap.get(REQ_PARAM_DIVISION_ID));
			tenantDivision.setTenantId(tenantId);
			tenantDivision.setLevel(level);
			tenantDivision.setParentId(divisionId);

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
	 * 获取行政区划，并且包含该租户所在的行政区划的节点（isRoot=1 and isRoot=0）
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "getDivisionListWithRoot", method = { RequestMethod.GET, RequestMethod.POST })
	public RestResultDto getDivisionListWithRoot(HttpServletRequest request) {
		String msg = null;
		Integer result = RestResultDto.RESULT_FAIL;
		Object data = null;
		String exception = null;
		RestResultDto restResultDto = new RestResultDto();
		TenantDivision tenantDivision = new TenantDivision();
		try {

			Map<String, Object> paramMap = jm.fromJson(SpringmvcUtils.getParameter(ManagementConstant.REST_PMS), Map.class);
			// if (MapUtils.isEmpty(paramMap)) {
			// throw new VortexException("参数不能为空");
			// }
			String tenantId = (String) paramMap.get(TENANT_ID);
			Integer level = (Integer) paramMap.get(LEVEL);
			String divisionId = (String) (MapUtils.isEmpty(paramMap) ? null : paramMap.get(REQ_PARAM_DIVISION_ID));
			tenantDivision.setTenantId(tenantId);
			tenantDivision.setLevel(level);
			tenantDivision.setParentId(divisionId);

			// 注意：divisionId不是必须的请求参数，如果没有传入，则取租户下整颗树

			if (logger.isDebugEnabled()) {
				logger.debug("loadDivisionTree(), tenantId=" + tenantId + ",divisionId=" + divisionId);
			}

			List<TenantDivision> list = tenantDivisionService.findTenantDivisionListWithRoot(tenantDivision);

			result = RestResultDto.RESULT_SUCC;
			msg = "成功获取行政区划列表";
			data = list;
		} catch (Exception e) {
			e.printStackTrace();
			result = RestResultDto.RESULT_FAIL;
			msg = e.getMessage();
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
	 * 通过ids获取行政区划的名字
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "getDivisionNamesByIds", method = { RequestMethod.GET, RequestMethod.POST })
	public RestResultDto getDivisionNamesByIds(HttpServletRequest request) {
		String msg = null;
		Integer result = RestResultDto.RESULT_FAIL;
		Object data = null;
		String exception = null;
		RestResultDto restResultDto = new RestResultDto();
		try {

			Map<String, Object> paramMap = jm.fromJson(SpringmvcUtils.getParameter(ManagementConstant.REST_PMS), Map.class);
			if (MapUtils.isEmpty(paramMap)) {
				throw new VortexException("参数不能为空");
			}
			String tenantId = (String) paramMap.get(TENANT_ID);
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
	 * 根据id获取行政区划
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "getById", method = { RequestMethod.GET, RequestMethod.POST })
	public RestResultDto getById(HttpServletRequest request) {
		String msg = null;
		Integer result = RestResultDto.RESULT_FAIL;
		Object data = null;
		String exception = null;
		RestResultDto restResultDto = new RestResultDto();
		try {

			Map<String, String> paramMap = jm.fromJson(SpringmvcUtils.getParameter(ManagementConstant.REST_PMS), Map.class);
			String id = paramMap.get("id");

			data = tenantDivisionService.findOne(id);

			result = RestResultDto.RESULT_SUCC;
		} catch (Exception e) {
			e.printStackTrace();
			result = RestResultDto.RESULT_FAIL;
			msg = e.getMessage();
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
	 * 根据id获取行政区划
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "getChildren", method = { RequestMethod.GET, RequestMethod.POST })
	public RestResultDto getChildren(HttpServletRequest request) {
		String msg = null;
		Integer result = RestResultDto.RESULT_FAIL;
		Object data = null;
		String exception = null;
		RestResultDto restResultDto = new RestResultDto();
		try {

			Map<String, String> paramMap = jm.fromJson(SpringmvcUtils.getParameter(ManagementConstant.REST_PMS), Map.class);
			String id = paramMap.get("id");

			data = tenantDivisionService.getChildren(id);

			result = RestResultDto.RESULT_SUCC;
		} catch (Exception e) {
			e.printStackTrace();
			result = RestResultDto.RESULT_FAIL;
			msg = e.getMessage();
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
	 * 获得租户下某个级别的行政区划列表
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "getByLevel", method = { RequestMethod.GET, RequestMethod.POST })
	public RestResultDto getByLevel(HttpServletRequest request) {
		String msg = null;
		Integer result = RestResultDto.RESULT_FAIL;
		Object data = null;
		String exception = null;
		RestResultDto restResultDto = new RestResultDto();
		try {

			Map<String, String> paramMap = jm.fromJson(SpringmvcUtils.getParameter(ManagementConstant.REST_PMS), Map.class);
			String tenantId = paramMap.get("tenantId");
			Integer level = new Integer(paramMap.get("level"));

			data = tenantDivisionService.getByLevel(tenantId, level);

			result = RestResultDto.RESULT_SUCC;
		} catch (Exception e) {
			e.printStackTrace();
			result = RestResultDto.RESULT_FAIL;
			msg = e.getMessage();
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
	 * 导入的时候根据区划名称查询区划id
	 * 
	 * @param request
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "getDivisionsByNames", method = { RequestMethod.GET, RequestMethod.POST })
	public RestResultDto getDivisionsByNames(HttpServletRequest request) {
		String msg = null;
		Integer result = ManagementConstant.REST_RESULT_SUCC;
		Object data = null;
		String exception = null;
		RestResultDto restResultDto = new RestResultDto();

		try {
			JsonMapper jm = new JsonMapper();
			Map<String, Object> paramMap = jm.fromJson(SpringmvcUtils.getParameter(ManagementConstant.REST_PMS), Map.class);
			String tenantId = (String) paramMap.get("tenantId");
			List<String> names = (List<String>) paramMap.get("names");
			data = this.tenantDivisionService.getDivisionsByNames(tenantId, names);
			msg = "查询成功";
		} catch (Exception e) {
			e.printStackTrace();
			result = ManagementConstant.REST_RESULT_FAIL;
			msg = "查询失败！";
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
