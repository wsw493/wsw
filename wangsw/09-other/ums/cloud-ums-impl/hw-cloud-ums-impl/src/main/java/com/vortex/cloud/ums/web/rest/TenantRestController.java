package com.vortex.cloud.ums.web.rest;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.vortex.cloud.ums.dataaccess.service.ITenantService;
import com.vortex.cloud.ums.dto.TenantDto;
import com.vortex.cloud.ums.dto.rest.TenantNameByIdSearchDto;
import com.vortex.cloud.ums.support.ManagementConstant;
import com.vortex.cloud.vfs.common.exception.VortexException;
import com.vortex.cloud.vfs.common.mapper.JsonMapper;
import com.vortex.cloud.vfs.common.web.springmvc.SpringmvcUtils;
import com.vortex.cloud.vfs.data.dto.RestResultDto;

/**
 * @author LiShijun
 * @date 2016年3月29日 下午3:50:13
 * @Description 租户维护对外提供的RESTful web service History <author> <time> <desc>
 */
@SuppressWarnings("all")
@RestController
@RequestMapping("cloud/management/rest/tenant")
public class TenantRestController {
	private Logger logger = LoggerFactory.getLogger(TenantRestController.class);
	private JsonMapper jm = new JsonMapper();

	private static final String REQ_PARAM_TENANT_CODE = "tenantCode";

	@Resource
	private ITenantService tenantService;

	/**
	 * 根据租户code获取租户
	 * 
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "getTenantByCode", method = { RequestMethod.GET, RequestMethod.POST })
	public RestResultDto getTenantByCode(HttpServletRequest request) {
		Object data = null;
		String msg = null;
		Integer result = ManagementConstant.REST_RESULT_SUCC;
		String exception = null;
		RestResultDto rst = new RestResultDto();

		try {
			Map<String, String> paramMap = jm.fromJson(SpringmvcUtils.getParameter(ManagementConstant.REST_PMS), Map.class);
			if (MapUtils.isEmpty(paramMap)) {
				throw new VortexException("参数不能为空");
			}

			String tenantCode = paramMap.get(REQ_PARAM_TENANT_CODE);
			if (StringUtils.isBlank(tenantCode)) {
				throw new VortexException("请传入参数：" + REQ_PARAM_TENANT_CODE);
			}

			if (logger.isDebugEnabled()) {
				logger.debug("getTenantByCode(), tenantId=" + tenantCode);
			}

			TenantDto dto = tenantService.getTenantByCode(tenantCode);

			result = RestResultDto.RESULT_SUCC;
			msg = "成功获取到租户";
			data = dto;
		} catch (Exception e) {
			e.printStackTrace();
			result = RestResultDto.RESULT_FAIL;
			msg = e.getMessage();
			data = null;

			logger.error(msg, e);
		} finally {
			rst.setResult(result);
			rst.setMsg(msg);
			rst.setData(data);
			rst.setException(exception);
		}
		return rst;
	}

	/**
	 * 根据指定租户id列表，获取id相应的租户name
	 * 
	 * @param request
	 * @return
	 * 
	 */
	@RequestMapping(value = "getTenantNameById", method = { RequestMethod.GET, RequestMethod.POST })
	public RestResultDto getTenantNameById(HttpServletRequest request) {
		Object data = null;
		String msg = null;
		Integer result = ManagementConstant.REST_RESULT_SUCC;
		String exception = null;
		RestResultDto rst = new RestResultDto();

		try {
			TenantNameByIdSearchDto searchDto = jm.fromJson(SpringmvcUtils.getParameter(ManagementConstant.REST_PMS), TenantNameByIdSearchDto.class);
			if (searchDto == null) {
				throw new VortexException("参数不能为空");
			}

			List<String> idList = searchDto.getIdList();
			if (logger.isDebugEnabled()) {
				logger.debug("getTenantNameById(), idList=" + idList);
			}

			data = tenantService.findTenantNameById(idList);
			result = ManagementConstant.REST_RESULT_SUCC;
			msg = "成功获取租户名称";
		} catch (Exception e) {
			e.printStackTrace();
			result = ManagementConstant.REST_RESULT_FAIL;
			msg = "获取租户名称失败，" + e.getMessage();
			data = null;
			exception = e.toString();

			logger.error(msg, e);
		} finally {
			rst.setResult(result);
			rst.setMsg(msg);
			rst.setData(data);
			rst.setException(exception);
		}

		return rst;
	}
}
