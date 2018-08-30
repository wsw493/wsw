package com.vortex.cloud.ums.web;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.vortex.cloud.ums.annotation.FunctionCode;
import com.vortex.cloud.ums.dataaccess.service.ITenantDivisionService;
import com.vortex.cloud.ums.dataaccess.service.ITenantService;
import com.vortex.cloud.ums.dto.TenantDivisionDto;
import com.vortex.cloud.ums.enums.ResponseType;
import com.vortex.cloud.ums.model.TenantDivision;
import com.vortex.cloud.ums.tree.TenantDivisionTree;
import com.vortex.cloud.ums.util.tree.ITreeService;
import com.vortex.cloud.ums.util.web.BaseController;
import com.vortex.cloud.vfs.common.exception.VortexException;
import com.vortex.cloud.vfs.common.lang.StringUtil;
import com.vortex.cloud.vfs.common.mapper.JsonMapper;
import com.vortex.cloud.vfs.common.web.springmvc.SpringmvcUtils;
import com.vortex.cloud.vfs.data.dto.DataStore;
import com.vortex.cloud.vfs.data.dto.LoginReturnInfoDto;
import com.vortex.cloud.vfs.data.dto.RestResultDto;
import com.vortex.cloud.vfs.data.support.SearchFilter;
import com.vortex.cloud.vfs.data.support.SearchFilter.Operator;

/**
 * 租户的行政区域管理 维护租户的行政区域树
 * 
 * @author LiShijun
 *
 */
@SuppressWarnings("all")
@RestController
@RequestMapping("cloud/management/tenant/xzqh")
public class TenantDivisionController extends BaseController {
	private Logger logger = LoggerFactory.getLogger(TenantDivisionController.class);

	private static final String FORE_DYNAMIC_SUFFIX = com.vortex.cloud.ums.support.ManagementConstant.PERMISSION_SUFFIX_SA;
	private static final String BACK_DYNAMIC_SUFFIX = com.vortex.cloud.ums.support.ManagementConstant.PERMISSION_SUFFIX_SA;

	@Resource
	private ITenantDivisionService tenantDivisionService;

	@Resource
	private ITreeService treeService;

	@Resource
	private ITenantService tenantService;

	// 时间转化问题
	@InitBinder
	public void initBinder(WebDataBinder binder) {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		dateFormat.setLenient(true);
		binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
	}

	@RequestMapping(value = "loadTree" + FORE_DYNAMIC_SUFFIX, method = RequestMethod.POST)
	public RestResultDto<String> loadTree(HttpServletRequest request, HttpServletResponse response) throws IOException {
		try {
			LoginReturnInfoDto loginInfo = super.getLoginInfo(request);
			String tenantId = loginInfo.getTenantId();

			TenantDivisionTree tree = TenantDivisionTree.getInstance();

			tree.reloadTenantDivisionTree(tenantId, null);
			String jsonStr = treeService.generateJsonCheckboxTree(tree, false);
			return RestResultDto.newSuccess(jsonStr);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			e.printStackTrace();
			return RestResultDto.newFalid("加载树出错", e.getMessage());
		}

	}

	/**
	 * 设置默认经纬度
	 * 
	 * @param dto
	 */
	private void setDefaultLatLon(TenantDivisionDto dto) {
		if (dto.getLatitude() != null || dto.getLongitude() != null) {
			return;
		}

		Double defLatitude = null;
		Double defLongitude = null;

		TenantDivisionDto parent = tenantDivisionService.getById(dto.getParentId());
		if (parent != null) {
			defLatitude = parent.getLatitude();
			defLongitude = parent.getLongitude();
		}

		dto.setDefLatitude(defLatitude);
		;
		dto.setDefLongitude(defLongitude);
	}

	@RequestMapping(value = "checkForm/{param}" + FORE_DYNAMIC_SUFFIX, method = RequestMethod.POST)
	public RestResultDto<Boolean> checkForm(HttpServletRequest request, @PathVariable("param") String paramName) {
		try {

			// String tenantId = SpringmvcUtils.getParameter("tenantId");
			LoginReturnInfoDto loginInfo = super.getLoginInfo(request);
			String tenantId = loginInfo.getTenantId();

			String id = SpringmvcUtils.getParameter("id");
			String paramVal = SpringmvcUtils.getParameter(paramName);

			if (StringUtil.isNullOrEmpty(paramVal)) {

				return RestResultDto.newSuccess(false);
			}

			if (!"commonCode".equals(paramName)) {
				return RestResultDto.newSuccess(true);
			}

			List<SearchFilter> filterList = new ArrayList<>();
			filterList.add(new SearchFilter("tenantId", Operator.EQ, tenantId));
			filterList.add(new SearchFilter("commonCode", Operator.EQ, paramVal));
			List<TenantDivision> list = tenantDivisionService.findListByFilter(filterList, null);

			if (CollectionUtils.isEmpty(list)) {
				return RestResultDto.newSuccess(true);
			}

			// update记录时，且值没变化，验证通过
			if (!StringUtil.isNullOrEmpty(id) && list.size() == 1 && list.get(0).getId().equals(id)) {
				return RestResultDto.newSuccess(true);
			}

			return RestResultDto.newSuccess(false);
		} catch (Exception e) {
			logger.error("校验参数出错", e);
			return RestResultDto.newFalid("校验参数出错", e.getMessage());
		}
	}

	@RequestMapping(value = "add" + BACK_DYNAMIC_SUFFIX, method = RequestMethod.POST)
	@FunctionCode(value = "CF_MANAGE_TENANT_XZQH_ADD", type = ResponseType.Json)
	public RestResultDto<Boolean> add(HttpServletRequest request, TenantDivisionDto newDivision) {
		try {

			String commonCode = newDivision.getCommonCode();
			if (StringUtil.isNullOrEmpty(commonCode)) {
				throw new VortexException("数字代码不能为空！");
			}
			newDivision.setTenantId(getLoginInfo(request).getTenantId());
			List<SearchFilter> filterList = new ArrayList<>();
			filterList.add(new SearchFilter("tenantId", Operator.EQ, newDivision.getTenantId()));
			filterList.add(new SearchFilter("commonCode", Operator.EQ, commonCode));
			List<TenantDivision> xzqhs = tenantDivisionService.findListByFilter(filterList, null);

			if (CollectionUtils.isNotEmpty(xzqhs)) {
				throw new VortexException("添加失败！数字代码已存在！");
			}

			newDivision.setIsRoot(TenantDivision.ROOT_NOT);

			tenantDivisionService.save(newDivision);

			return RestResultDto.newSuccess(true, "添加成功");
		} catch (Exception e) {
			logger.error("添加失败", e);
			return RestResultDto.newFalid("添加失败", e.getMessage());
		}
	}

	@RequestMapping(value = "pageList" + FORE_DYNAMIC_SUFFIX, method = RequestMethod.POST)
	public RestResultDto<DataStore<TenantDivisionDto>> pageList(HttpServletRequest request, HttpServletResponse response) {
		try {

			String parentId = SpringmvcUtils.getParameter("parentId");
			if (StringUtil.isNullOrEmpty(parentId)) {
				return RestResultDto.newSuccess(new DataStore<>());
			}

			List<SearchFilter> filterList = new ArrayList<>();
			filterList.add(new SearchFilter("enabled", Operator.EQ, TenantDivision.ENABLED_YES));
			filterList.add(new SearchFilter("parentId", Operator.EQ, parentId));

			Sort sort = new Sort(Direction.ASC, "orderIndex", "commonCode");

			List<TenantDivision> list = tenantDivisionService.findListByFilter(filterList, sort);
			TenantDivision tenantDivision = tenantDivisionService.findOne(parentId);
			if (tenantDivision != null && TenantDivision.ROOT_YES.equals(tenantDivision.getIsRoot())) {
				list.add(tenantDivision);
			}
			List<TenantDivisionDto> dtoList = new ArrayList<>();
			if (CollectionUtils.isNotEmpty(list)) {
				TenantDivisionDto dto = null;
				for (TenantDivision divisio : list) {
					dto = new TenantDivisionDto();
					BeanUtils.copyProperties(divisio, dto);
					dtoList.add(dto);
				}
			}

			return RestResultDto.newSuccess(new DataStore<>(dtoList.size(), dtoList));
		} catch (Exception e) {
			logger.error("加载列表分页出错", e);
			return RestResultDto.newFalid("加载列表分页出错", e.getMessage());
		}
	}

	@RequestMapping(value = "loadTenantDivisionDtl" + FORE_DYNAMIC_SUFFIX, method = RequestMethod.POST)
	public RestResultDto<TenantDivisionDto> loadTenantDivisionDtl(HttpServletResponse response) {
		try {

			String id = SpringmvcUtils.getParameter("id");
			JsonMapper jsonMapper = new JsonMapper();
			TenantDivisionDto division = tenantDivisionService.getById(id);
			this.setDefaultLatLon(division);
			return RestResultDto.newSuccess(division);
		} catch (Exception e) {
			logger.error("根据id获取租户行政区划出错", e);
			return RestResultDto.newFalid("根据id获取租户行政区划出错", e.getMessage());
		}
	}

	@RequestMapping(value = "update" + BACK_DYNAMIC_SUFFIX, method = RequestMethod.POST)
	@FunctionCode(value = "CF_MANAGE_TENANT_XZQH_UPDATE", type = ResponseType.Json)
	public RestResultDto<Boolean> update(HttpServletRequest request, TenantDivisionDto dto) {

		try {
			tenantDivisionService.update(dto);
			return RestResultDto.newSuccess(true, "更新成功");
		} catch (Exception e) {
			logger.error("更新失败", e);
			return RestResultDto.newFalid("更新失败", e.getMessage());
		}

	}

	@RequestMapping(value = "delete" + BACK_DYNAMIC_SUFFIX, method = RequestMethod.POST)

	@FunctionCode(value = "CF_MANAGE_TENANT_XZQH_DEL", type = ResponseType.Json)
	public RestResultDto<Boolean> delete(HttpServletRequest request, @RequestBody String[] ids) {

		try {
			long deleted = tenantDivisionService.deleteByIdArr(ids, false); // 不需级联删除

			return RestResultDto.newSuccess(true, "成功删除" + deleted + "条记录！");
		} catch (Exception e) {
			logger.error("delete()", e);

			return RestResultDto.newFalid("删除失败", e.getMessage());
		}

	}

	@RequestMapping(value = "cascadeDeleteChildren" + BACK_DYNAMIC_SUFFIX, method = RequestMethod.POST)

	public RestResultDto<Boolean> cascadeDeleteChildren(@RequestBody String[] ids) {

		try {
			long deleted = tenantDivisionService.deleteByIdArr(ids, true); // 需级联删除

			return RestResultDto.newSuccess(true, "成功级联删除" + deleted + "条记录！");
		} catch (Exception e) {
			logger.error("cascadeDeleteChildren()", e);

			return RestResultDto.newFalid("级联删除失败", e.getMessage());
		}

	}

	@RequestMapping(value = "batchUpdate" + BACK_DYNAMIC_SUFFIX, method = RequestMethod.POST, consumes = "application/json")

	@FunctionCode(value = "CF_MANAGE_TENANT_XZQH_BAT_UPDATE", type = ResponseType.Json)
	public RestResultDto<Boolean> batchUpdate(HttpServletRequest request, @RequestBody TenantDivisionDto.BatchUpdateList dtoList) {
		try {

			if (CollectionUtils.isEmpty(dtoList)) {
				throw new VortexException("请勾选要保存的行政区划！");
			}

			for (TenantDivisionDto.BatchUpdate dto : dtoList) {

				TenantDivision division = tenantDivisionService.findOne(dto.getId());
				if (division == null) {
					continue;
				}

				BeanUtils.copyProperties(dto, division);
				tenantDivisionService.update(division);

			}
			return RestResultDto.newSuccess(true, "批量更新行政区划成功");
		} catch (Exception e) {
			logger.error("批量更新行政区划时出错", e);
			return RestResultDto.newFalid("批量更新行政区划时出错", e.getMessage());
		}
	}
}
