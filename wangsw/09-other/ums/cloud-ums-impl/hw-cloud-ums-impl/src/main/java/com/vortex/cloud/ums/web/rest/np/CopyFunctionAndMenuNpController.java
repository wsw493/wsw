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

import com.vortex.cloud.ums.dataaccess.service.ICopyFunctionAndMenuService;
import com.vortex.cloud.ums.support.ManagementConstant;
import com.vortex.cloud.vfs.common.exception.VortexException;
import com.vortex.cloud.vfs.common.mapper.JsonMapper;
import com.vortex.cloud.vfs.common.web.springmvc.SpringmvcUtils;
import com.vortex.cloud.vfs.data.dto.RestResultDto;

@SuppressWarnings("all")
@RestController
@RequestMapping("cloud/management/rest/np/copy")
public class CopyFunctionAndMenuNpController {
	private Logger logger = LoggerFactory.getLogger(MenuRestNpController.class);
	private static final String SOURCE_BS_CODE = "sourceBsCode"; // 资源业务系统code
	private static final String TARGET_BS_CODE = "targetBsCode"; // 目标业务系统code

	@Resource
	private ICopyFunctionAndMenuService copyFunctionAndMenuService;

	/**
	 * 服务一个业务系统的功能和菜单到另一个业务系统
	 * 
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings({ "unchecked", "finally" })
	@RequestMapping(value = "copyFunctionAndMenu", method = { RequestMethod.GET, RequestMethod.POST })
	public RestResultDto copyMenu(HttpServletRequest request) throws Exception {
		String msg = "";
		Integer result = ManagementConstant.REST_RESULT_SUCC;
		Object data = null;
		String exception = "";
		RestResultDto rst = new RestResultDto();
		try {
			Map<String, String> paramMap = new JsonMapper().fromJson(SpringmvcUtils.getParameter(ManagementConstant.REST_PMS), Map.class);
			if (MapUtils.isEmpty(paramMap)) {
				throw new VortexException("参数不能为空");
			}

			String sourceBsCode = paramMap.get(SOURCE_BS_CODE);
			String targetBsCode = paramMap.get(TARGET_BS_CODE);

			copyFunctionAndMenuService.copyFunctionAndMenu(sourceBsCode, targetBsCode);
			msg = "复制功能菜单成功";
		} catch (Exception e) {
			e.printStackTrace();
			result = ManagementConstant.REST_RESULT_FAIL;
			msg = "复制功能菜单失败";
			data = null;
			exception = e.getMessage();
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
