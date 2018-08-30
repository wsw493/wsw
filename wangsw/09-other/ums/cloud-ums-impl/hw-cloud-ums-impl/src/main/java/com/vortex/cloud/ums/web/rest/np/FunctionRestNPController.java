package com.vortex.cloud.ums.web.rest.np;

import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.vortex.cloud.ums.dataaccess.service.ICloudFunctionService;
import com.vortex.cloud.ums.support.ManagementConstant;
import com.vortex.cloud.ums.tree.SystemFunctionGroupTree;
import com.vortex.cloud.ums.util.tree.ITreeService;
import com.vortex.cloud.vfs.common.exception.VortexException;
import com.vortex.cloud.vfs.common.mapper.JsonMapper;
import com.vortex.cloud.vfs.common.web.springmvc.SpringmvcUtils;
import com.vortex.cloud.vfs.data.dto.RestResultDto;

/**
 * 功能rest
 * 
 * @author lusm
 *
 */
@SuppressWarnings("all")
@RestController
@RequestMapping("cloud/management/rest/np/function")
public class FunctionRestNPController {
	private static final Object SYSTEM_CODE = "systemCode";
	private static final Object USER_ID = "userId";
	private static final Object FUNCTION_IDS = "functionIds";
	private JsonMapper jm = new JsonMapper();
	private Logger logger = LoggerFactory.getLogger(FunctionRestNPController.class);
	@Resource
	private ICloudFunctionService cloudFunctionService;

	@Resource
	private ITreeService treeService;

	/**
	 * 获取用户在systemCode下的功能(systemCode可选)
	 * 
	 * 
	 * 
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings({ "unchecked", "finally" })
	@RequestMapping(value = "getFunctionsByUsreIdAndSystem" + ManagementConstant.PERMISSION_SUFFIX_READ, method = { RequestMethod.GET, RequestMethod.POST })
	public RestResultDto getFunctionsByUsreIdAndSystem(HttpServletRequest request) throws Exception {
		String msg = null;
		Integer result = ManagementConstant.REST_RESULT_SUCC;
		Object data = null;
		JsonMapper mapper = new JsonMapper();

		try {
			Map<String, Object> paramMap = jm.fromJson(SpringmvcUtils.getParameter(ManagementConstant.REST_PMS), Map.class);
			if (MapUtils.isEmpty(paramMap)) {
				throw new VortexException("参数不能为空");
			}

			String userId = (String) paramMap.get(USER_ID);
			String systemCode = (String) paramMap.get(SYSTEM_CODE);

			data = cloudFunctionService.getFunctionsByUsreIdAndSystem(userId, systemCode);
		} catch (Exception e) {
			e.printStackTrace();
			result = ManagementConstant.REST_RESULT_FAIL;
			msg = e.getMessage();
			data = null;

			logger.error(msg, e);
		} finally {
			RestResultDto rst = new RestResultDto();
			rst.setResult(result);
			rst.setMsg(msg);
			rst.setData(data);
			return rst;
		}
	}

	/**
	 * 
	 * 根据功能IDS获取其完整URL
	 * 
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings({ "unchecked", "finally" })
	@RequestMapping(value = "getFunctionsByIds" + ManagementConstant.PERMISSION_SUFFIX_READ, method = { RequestMethod.GET, RequestMethod.POST })
	public RestResultDto getFunctionsByIds(HttpServletRequest request) throws Exception {
		String msg = null;
		Integer result = ManagementConstant.REST_RESULT_SUCC;
		Object data = null;
		JsonMapper mapper = new JsonMapper();

		try {
			Map<String, Object> paramMap = jm.fromJson(SpringmvcUtils.getParameter(ManagementConstant.REST_PMS), Map.class);
			if (MapUtils.isEmpty(paramMap)) {
				throw new VortexException("参数不能为空");
			}

			String functionIds = (String) paramMap.get(FUNCTION_IDS);
			data = cloudFunctionService.getFunctionsByIds(functionIds);

		} catch (Exception e) {
			e.printStackTrace();
			result = ManagementConstant.REST_RESULT_FAIL;
			msg = e.getMessage();
			data = null;

			logger.error(msg, e);
		} finally {
			RestResultDto rst = new RestResultDto();
			rst.setResult(result);
			rst.setMsg(msg);
			rst.setData(data);
			return rst;
		}
	}

	@SuppressWarnings({ "unchecked", "finally" })
	@RequestMapping(value = "loadSystemFunctionTree" + ManagementConstant.PERMISSION_SUFFIX_READ, method = { RequestMethod.GET, RequestMethod.POST })
	public RestResultDto loadSystemFunctionTree(HttpServletRequest request) throws Exception {
		String msg = null;
		Integer result = ManagementConstant.REST_RESULT_SUCC;
		Object data = null;
		String exception = null;
		RestResultDto restResultDto = new RestResultDto();

		try {

			Map<String, Object> paramMap = jm.fromJson(SpringmvcUtils.getParameter(ManagementConstant.REST_PMS), Map.class);
			if (MapUtils.isEmpty(paramMap)) {
				throw new VortexException("参数不能为空");
			}

			String userId = (String) paramMap.get(USER_ID);

			SystemFunctionGroupTree functionGroupTree = SystemFunctionGroupTree.getInstance();
			functionGroupTree.reloadSystemFunctionTree(userId);
			String jsonStr = treeService.generateJsonCheckboxTree(functionGroupTree, false);

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
}
