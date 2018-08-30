package com.vortex.cloud.ums.web.rest;

import java.util.ArrayList;
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

import com.vortex.cloud.ums.dataaccess.service.ITenantConstantService;
import  com.vortex.cloud.vfs.data.dto.LoginReturnInfoDto;
import com.vortex.cloud.ums.model.CloudConstant;
import com.vortex.cloud.ums.support.ManagementConstant;
import com.vortex.cloud.ums.util.web.BaseController;
import com.vortex.cloud.vfs.common.exception.VortexException;
import com.vortex.cloud.vfs.common.mapper.JsonMapper;
import com.vortex.cloud.vfs.common.web.springmvc.SpringmvcUtils;
import com.vortex.cloud.vfs.data.dto.RestResultDto;
import com.vortex.cloud.vfs.data.support.SearchFilter;
import com.vortex.cloud.vfs.data.support.SearchFilter.Operator;

/**
 * @author LiShijun
 * @date 2016年3月29日 下午3:50:13
 * @Description 租户常量维护对外提供的RESTful web service History <author> <time> <desc>
 */
@SuppressWarnings("all")
@RestController
@RequestMapping("cloud/management/rest/tenant/constant")
public class TenantConstantRestController extends BaseController {
	private Logger logger = LoggerFactory.getLogger(TenantConstantRestController.class);

	private JsonMapper jm = new JsonMapper();

	private static final String REQ_PARAM_CONSTANT_CODE = "constantCode";
	private static final String REQ_PARAM_CONSTANT_VAL = "constantValue";
	private static final String REQ_PARAM_CONSTANT_DESC = "constantDescription";

	@Resource
	private ITenantConstantService tenantConstantService;

	/**
	 * 根据常量名获取相应的常量记录
	 * 
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "getByName" + ManagementConstant.PERMISSION_SUFFIX_READ, method = { RequestMethod.GET, RequestMethod.POST })
	public RestResultDto getByName(HttpServletRequest request) throws Exception {
		String msg = null;
		Integer result = ManagementConstant.REST_RESULT_SUCC;
		Object data = null;
		String exception = null;
		RestResultDto restResultDto = new RestResultDto();

		try {
			LoginReturnInfoDto loginInfo = super.getLoginInfo(request);
			String tenantId = loginInfo.getTenantId();

			Map<String, String> paramMap = jm.fromJson(SpringmvcUtils.getParameter(ManagementConstant.REST_PMS), Map.class);
			if (MapUtils.isEmpty(paramMap)) {
				throw new VortexException("参数不能为空");
			}

			String constantCode = paramMap.get(REQ_PARAM_CONSTANT_CODE);
			if (StringUtils.isBlank(constantCode)) {
				throw new VortexException("请传入参数：" + REQ_PARAM_CONSTANT_CODE);
			}

			if (logger.isDebugEnabled()) {
				logger.debug("getByName(), tenantId=" + tenantId + ",constantCode=" + constantCode);
			}

			List<SearchFilter> filterList = new ArrayList<SearchFilter>();
			filterList.add(new SearchFilter("tenantId", Operator.EQ, tenantId));
			filterList.add(new SearchFilter("constantCode", Operator.EQ, constantCode));

			List<CloudConstant> list = tenantConstantService.findListByFilter(filterList, null);
			if (CollectionUtils.isEmpty(list)) {
				result = RestResultDto.RESULT_FAIL;
				msg = "没有记录";
				data = null;
			} else {
				result = RestResultDto.RESULT_SUCC;
				msg = "成功获取到记录";
				data = list.get(0);
			}
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
			return restResultDto;
		}
	}

	/**
	 * 更新指定租户下的指定常量记录 如果存在，则更新记录； 如果不存在，则创建记录；
	 * 
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "update" + ManagementConstant.PERMISSION_SUFFIX_EDIT, method = { RequestMethod.GET, RequestMethod.POST })
	public RestResultDto update(HttpServletRequest request) throws Exception {
		String msg = null;
		Integer result = RestResultDto.RESULT_SUCC;
		Object data = null;
		String exception = null;
		RestResultDto restResultDto = new RestResultDto();

		try {
			LoginReturnInfoDto loginInfo = super.getLoginInfo(request);
			String tenantId = loginInfo.getTenantId();

			Map<String, String> paramMap = jm.fromJson(SpringmvcUtils.getParameter(ManagementConstant.REST_PMS), Map.class);
			if (MapUtils.isEmpty(paramMap)) {
				throw new VortexException("参数不能为空");
			}

			String constantCode = paramMap.get(REQ_PARAM_CONSTANT_CODE);
			if (StringUtils.isBlank(constantCode)) {
				throw new VortexException("请传入参数：" + REQ_PARAM_CONSTANT_CODE);
			}

			String constantValue = paramMap.get(REQ_PARAM_CONSTANT_VAL);
			if (StringUtils.isBlank(constantValue)) {
				throw new VortexException("请传入参数：" + REQ_PARAM_CONSTANT_VAL);
			}

			String constantDescription = paramMap.get(REQ_PARAM_CONSTANT_DESC);
			if (StringUtils.isBlank(constantValue)) {
				throw new VortexException("请传入参数：" + REQ_PARAM_CONSTANT_DESC);
			}

			if (logger.isDebugEnabled()) {
				StringBuffer sb = new StringBuffer("update(): ");
				sb.append("tenantId=" + tenantId + ",");
				sb.append("constantCode=" + constantCode + ",");
				sb.append("constantValue=" + constantValue + ",");
				sb.append("constantDescription=" + constantDescription);

				logger.debug(sb.toString());
			}

			List<SearchFilter> filterList = new ArrayList<SearchFilter>();
			filterList.add(new SearchFilter("tenantId", Operator.EQ, tenantId));
			filterList.add(new SearchFilter("constantCode", Operator.EQ, constantCode));

			List<CloudConstant> list = tenantConstantService.findListByFilter(filterList, null);
			if (CollectionUtils.isEmpty(list)) {
				CloudConstant constant = new CloudConstant();
				constant.setTenantId(tenantId);
				constant.setConstantCode(constantCode);
				constant.setConstantValue(constantValue);
				constant.setConstantDescription(constantDescription);

				tenantConstantService.save(constant);

				result = RestResultDto.RESULT_SUCC;
				msg = "成功增加记录";
				data = constant;
			} else {
				CloudConstant constant = list.get(0);
				constant.setConstantValue(constantValue);
				constant.setConstantDescription(constantDescription);
				tenantConstantService.update(constant);

				result = RestResultDto.RESULT_SUCC;
				msg = "成功更新记录";
				data = constant;
			}
		} catch (Exception e) {
			e.printStackTrace();
			result = RestResultDto.RESULT_FAIL;
			msg = e.getMessage();
			data = null;
			exception = e.getMessage();
			logger.error(msg, e);
		} finally {
			restResultDto.setResult(result);
			restResultDto.setMsg(msg);
			restResultDto.setData(data);
			restResultDto.setException(exception);
			return restResultDto;
		}
	}
}
