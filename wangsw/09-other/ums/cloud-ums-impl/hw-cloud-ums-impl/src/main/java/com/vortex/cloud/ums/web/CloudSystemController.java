package com.vortex.cloud.ums.web;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.vortex.cloud.ums.annotation.FunctionCode;
import com.vortex.cloud.ums.dataaccess.service.ICloudSystemService;
import com.vortex.cloud.ums.dto.CloudSystemDto;
import com.vortex.cloud.ums.enums.ResponseType;
import com.vortex.cloud.ums.model.CloudSystem;
import com.vortex.cloud.ums.util.web.BaseController;
import com.vortex.cloud.ums.util.support.ForeContext;
import com.vortex.cloud.vfs.common.mapper.JsonMapper;
import com.vortex.cloud.vfs.common.web.springmvc.SpringmvcUtils;
import com.vortex.cloud.vfs.data.dto.DataStore;
import com.vortex.cloud.vfs.data.dto.RestResultDto;
import com.vortex.cloud.vfs.data.support.SearchFilter;
import com.vortex.cloud.vfs.data.support.SearchFilter.Operator;







/**
 * 百度云云系统配置管理
 * 
 * @author lishijun
 *
 */
@SuppressWarnings("all")
  @RestController      
@RequestMapping("cloud/management/system")
public class CloudSystemController extends BaseController {
	private static final Logger logger = LoggerFactory.getLogger(CloudSystemController.class);

	private static final String FORE_DYNAMIC_SUFFIX = com.vortex.cloud.ums.support.ManagementConstant.PERMISSION_SUFFIX_SA;
	private static final String BACK_DYNAMIC_SUFFIX = com.vortex.cloud.ums.support.ManagementConstant.PERMISSION_SUFFIX_SA;

	@Resource
	private ICloudSystemService cloudSystemService;

	/**
	 * 用于表单项的验证
	 * 
	 * @param paramName
	 * @return
	 */
	@RequestMapping(value = "validate/{param}" + FORE_DYNAMIC_SUFFIX, method = RequestMethod.POST)
	public RestResultDto<Boolean> validate(@PathVariable("param") String paramName) {
		try {

			String id = SpringmvcUtils.getParameter("id"); // 更新记录时，也要校验

			// 入参非空校验
			if (StringUtils.isBlank(paramName)) {
				return RestResultDto.newSuccess(false);
			}

			String paramVal = SpringmvcUtils.getParameter(paramName);
			if (StringUtils.isBlank(paramVal)) {
				return RestResultDto.newSuccess(false);
			}

			// 保证编码唯一
			if ("systemCode".equals(paramName)) {
				return RestResultDto.newSuccess(this.checkSystemCode(id, paramVal));
			}

			return RestResultDto.newSuccess(true);
		} catch (Exception e) {
			logger.error("校验参数出错", e);
			return RestResultDto.newFalid("校验参数出错", e.getMessage());
		}
	}

	/**
	 * 保证系统编码唯一
	 * 
	 * @param id
	 *            系统记录ID
	 * @param systemCode
	 *            系统编码
	 * @return
	 */
	private boolean checkSystemCode(String id, String systemCode) {
		List<SearchFilter> filterList = new ArrayList<SearchFilter>();

		filterList.add(new SearchFilter("systemCode", SearchFilter.Operator.EQ, systemCode.trim()));

		if (StringUtils.isNotBlank(id)) {
			filterList.add(new SearchFilter("id", SearchFilter.Operator.NE, id));
		}

		List<CloudSystem> list = cloudSystemService.findListByFilter(filterList, null);

		if (CollectionUtils.isNotEmpty(list)) {
			return false;
		}

		return true;
	}

	/**
	 * 新增记录
	 * 
	 * @param dto
	 * @return
	 */
	@RequestMapping(value = "add" + BACK_DYNAMIC_SUFFIX, method = RequestMethod.POST)
	@FunctionCode(value = "CF_MANAGE_CS_ADD", type = ResponseType.Json)
	public RestResultDto<Boolean> add(HttpServletRequest request, CloudSystemDto dto) {

		try {
			dto.setSystemType(CloudSystem.SYSTEM_TYPE_CLOUD);
			cloudSystemService.saveCloudSystem(dto);
			return RestResultDto.newSuccess(true, "新增云系统成功");
		} catch (Exception e) {
			logger.error("add()", e);
			return RestResultDto.newFalid("新增云系统失败", e.getMessage());
		}
	}

	@RequestMapping(value = "loadSystemDtl" + BACK_DYNAMIC_SUFFIX, method = RequestMethod.POST)
	public RestResultDto<CloudSystemDto> loadSystemDtl(HttpServletResponse response) {
		try {
			String id = SpringmvcUtils.getParameter("id");

			JsonMapper jsonMapper = new JsonMapper();
			CloudSystemDto dto = cloudSystemService.getCloudSystemById(id);
			return RestResultDto.newSuccess(dto);
		} catch (Exception e) {
			logger.error("根据id获取系统出错", e);
			return RestResultDto.newFalid("根据id获取系统出错", e.getMessage());
		}
	}

	/**
	 * 修改
	 * 
	 * @param dto
	 * @return
	 */
	@RequestMapping(value = "update" + BACK_DYNAMIC_SUFFIX, method = RequestMethod.POST)
	@FunctionCode(value = "CF_MANAGE_CS_UPDATE", type = ResponseType.Json)
	public RestResultDto<Boolean> update(HttpServletRequest request, CloudSystemDto dto) {

		try {
			cloudSystemService.updateCloudSystem(dto);
			return RestResultDto.newSuccess(true);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("更新失败", e);
			return RestResultDto.newFalid("更新失败", e.getMessage());
		}

	}

	/**
	 * 获取系统分页数据
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "pageList" + FORE_DYNAMIC_SUFFIX, method = RequestMethod.POST)
	
	@FunctionCode(value = "CF_MANAGE_CS_LIST", type = ResponseType.Json)
	public RestResultDto<DataStore<CloudSystem>> pageList(HttpServletRequest request) {
		try {

			List<SearchFilter> sfList = new ArrayList<SearchFilter>();
			sfList.add(new SearchFilter("systemType", Operator.EQ, CloudSystem.SYSTEM_TYPE_CLOUD));

			// 分页参数
			Sort defaultSort = new Sort(Direction.ASC, "systemCode");
			Pageable pageable = ForeContext.getPageable(request, defaultSort);

			Page<CloudSystem> pageResult = cloudSystemService.findPageByFilter(pageable, sfList);

			// 返回分页
			DataStore<CloudSystem> ds = new DataStore<CloudSystem>();
			if (pageResult != null) {
				ds.setTotal(pageResult.getTotalElements());
				ds.setRows(pageResult.getContent());
			}

			return RestResultDto.newSuccess(ds);
		} catch (Exception e) {
			logger.error("获取列表分页失败", e);
			return RestResultDto.newFalid("获取列表分页失败", e.getMessage());
		}
	}
	
}
