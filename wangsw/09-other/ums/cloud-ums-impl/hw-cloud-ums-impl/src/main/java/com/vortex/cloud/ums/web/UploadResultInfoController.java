package com.vortex.cloud.ums.web;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.vortex.cloud.ums.dataaccess.service.ICentralCacheRedisService;
import com.vortex.cloud.ums.dataaccess.service.IUploadResultInfoService;
import com.vortex.cloud.ums.dataaccess.service.impl.CentralCacheRedisServiceImpl;
import com.vortex.cloud.ums.model.upload.UploadResultInfo;
import com.vortex.cloud.ums.support.ManagementConstant;
import com.vortex.cloud.ums.util.CommonUtils;
import com.vortex.cloud.ums.util.support.ForeContext;
import com.vortex.cloud.ums.util.web.BaseController;
import com.vortex.cloud.vfs.data.dto.DataStore;
import com.vortex.cloud.vfs.data.dto.RestResultDto;
import com.vortex.cloud.vfs.data.support.SearchFilter;
import com.vortex.cloud.vfs.data.support.SearchFilter.Operator;

/**
 * Excel上传信息controller
 * 
 * @author SonHo
 *
 */
@SuppressWarnings("all")
@RestController
@RequestMapping("cloud/management/uploadResultInfo")
public class UploadResultInfoController extends BaseController {

	private static final Logger logger = LoggerFactory.getLogger(UploadResultInfoController.class);

	private static final String TITLE_LIST = "上传信息列表";
	private static final String URL_LIST = "cloud/management/uploadResultInfo/uploadResultInfoList";

	@Resource
	private IUploadResultInfoService uploadResultInfoService;

	@Resource(name = CentralCacheRedisServiceImpl.CLASSNAME)
	private ICentralCacheRedisService centralCacheRedisService;

	@RequestMapping(value = "queryList" + ManagementConstant.BACK_DYNAMIC_SUFFIX, method = RequestMethod.POST)
	public RestResultDto<DataStore<UploadResultInfo>> pageList(HttpServletRequest request) {
		try {

			DataStore<UploadResultInfo> ds = new DataStore<>();
			Order defaultOrder = new Order(Direction.ASC, "rowNum");
			Sort defaultSort = ForeContext.getSort(request, defaultOrder);
			Pageable pageable = ForeContext.getPageable(request, defaultSort);

			List<SearchFilter> searchFilter = CommonUtils.buildFromHttpRequest(request);
			// 从session取得查询标志
			// String marks = (String)
			// request.getSession().getAttribute("uploadMarks");

			// 由于分布式，所以存到redis中，然后从redis获取
			String marks = centralCacheRedisService.getObject(
					ManagementConstant.MARK_KEY_PREFIX + super.getLoginInfo(request).getUserId(), String.class);
			searchFilter.add(new SearchFilter("marks", Operator.EQ, marks));
			ds = uploadResultInfoService.queryDataStorePage(pageable, searchFilter);
			return RestResultDto.newSuccess(ds);
		} catch (Exception e) {
			logger.error("获取列表分页出错", e);
			return RestResultDto.newFalid("获取列表分页出错", e.getMessage());
		}
	}

}
