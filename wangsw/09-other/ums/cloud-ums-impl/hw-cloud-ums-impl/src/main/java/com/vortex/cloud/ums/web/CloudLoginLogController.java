package com.vortex.cloud.ums.web;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.vortex.cloud.ums.dataaccess.service.ICloudLoginLogService;
import com.vortex.cloud.ums.model.CloudLoginLog;
import com.vortex.cloud.ums.util.web.BaseController;
import com.vortex.cloud.ums.util.support.ForeContext;
import com.vortex.cloud.vfs.common.web.springmvc.SpringmvcUtils;
import com.vortex.cloud.vfs.data.dto.DataStore;
import com.vortex.cloud.vfs.data.dto.RestResultDto;
import com.vortex.cloud.vfs.data.support.SearchFilter;
import com.vortex.cloud.vfs.data.support.SearchFilter.Operator;






  @RestController      
@RequestMapping("cloud/management/log")
public class CloudLoginLogController extends BaseController {
	private static final Logger logger = LoggerFactory.getLogger(CloudLoginLogController.class);

	private static final String FORE_DYNAMIC_SUFFIX = com.vortex.cloud.ums.support.ManagementConstant.PERMISSION_SUFFIX_SA;

	@Resource
	private ICloudLoginLogService cloudLoginLogService;

	/**
	 * 登录日志列表
	 * 
	 * @param request
	 * @param response
	 * @return
	 * 
	 */
	@RequestMapping(value = "pageList" + FORE_DYNAMIC_SUFFIX, method = RequestMethod.POST)

	public RestResultDto<DataStore<CloudLoginLog>> pageList(HttpServletRequest request, HttpServletResponse response) {
		try {

			String ip = SpringmvcUtils.getParameter("ip");// 筛选ip
			String userName = SpringmvcUtils.getParameter("userName");
			String createTimeStart = SpringmvcUtils.getParameter("createTimeStart");
			String createTimeEnd = SpringmvcUtils.getParameter("createTimeEnd");
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			List<SearchFilter> filterList = new ArrayList<SearchFilter>();
			if (StringUtils.isNotEmpty(userName)) {
				filterList.add(new SearchFilter("userName", Operator.LIKE, userName));
			}
			if (StringUtils.isNotEmpty(ip)) {
				filterList.add(new SearchFilter("ip", Operator.EQ, ip));
			}
			try {
				if (StringUtils.isNotBlank(createTimeStart)) {
					filterList.add(new SearchFilter("createTime", Operator.GTE, sdf.parse(createTimeStart)));
				}

				if (StringUtils.isNotBlank(createTimeEnd)) {
					filterList.add(new SearchFilter("createTime", Operator.LTE, sdf.parse(createTimeEnd)));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			Sort sort = new Sort(Direction.DESC, "createTime");
			Pageable pageable = ForeContext.getPageable(request, sort);
			Page<CloudLoginLog> page = cloudLoginLogService.findPageByFilter(pageable, filterList);
			DataStore<CloudLoginLog> dataStore = null;
			if (null != page) {
				List<CloudLoginLog> result = page.getContent();
				dataStore = new DataStore<CloudLoginLog>(page.getTotalElements(), result);
			} else {
				dataStore = new DataStore<CloudLoginLog>();
			}
			return RestResultDto.newSuccess(dataStore);
		} catch (Exception e) {
			logger.error("加载分页失败", e);
			return RestResultDto.newFalid("加载分页失败", e.getMessage());
		}
	}

}
