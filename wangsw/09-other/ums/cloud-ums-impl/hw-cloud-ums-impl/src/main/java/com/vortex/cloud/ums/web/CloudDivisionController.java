package com.vortex.cloud.ums.web;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.vortex.cloud.ums.dataaccess.service.ICloudDivisionService;
import com.vortex.cloud.ums.dto.CloudDivisionDto;
import com.vortex.cloud.ums.enums.ResponseType;
import com.vortex.cloud.ums.model.CloudDivision;
import com.vortex.cloud.ums.util.web.BaseController;
import com.vortex.cloud.vfs.common.exception.VortexException;
import com.vortex.cloud.vfs.common.lang.StringUtil;
import com.vortex.cloud.vfs.common.mapper.JsonMapper;
import com.vortex.cloud.vfs.common.web.springmvc.SpringmvcUtils;
import com.vortex.cloud.vfs.data.dto.DataStore;
import com.vortex.cloud.vfs.data.dto.RestResultDto;
import com.vortex.cloud.vfs.data.support.SearchFilter;
import com.vortex.cloud.vfs.data.support.SearchFilter.Operator;

/**
 * 云行政区域管理
 * 
 * @author LiShijun
 *
 */
@SuppressWarnings("all")
@RestController
@RequestMapping("cloud/management/xzqh")
public class CloudDivisionController extends BaseController {
	private Logger logger = LoggerFactory.getLogger(CloudDivisionController.class);

	private static final String FORE_DYNAMIC_SUFFIX = com.vortex.cloud.ums.support.ManagementConstant.PERMISSION_SUFFIX_SA;
	private static final String BACK_DYNAMIC_SUFFIX = com.vortex.cloud.ums.support.ManagementConstant.PERMISSION_SUFFIX_SA;

	@Resource
	private ICloudDivisionService cloudDivisionService;

	// 时间转化问题
	@InitBinder
	public void initBinder(WebDataBinder binder) {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		dateFormat.setLenient(true);
		binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
	}

	@RequestMapping(value = "loadTree" + FORE_DYNAMIC_SUFFIX, method = RequestMethod.POST)
	public RestResultDto<List<Map<String, Object>>> loadTree(HttpServletRequest request, HttpServletResponse response) {
		try {

			String id = SpringmvcUtils.getParameter("id");

			List<SearchFilter> filterList = new ArrayList<SearchFilter>();

			if (!StringUtil.isNullOrEmpty(id)) {
				filterList.add(new SearchFilter("parentId", Operator.EQ, id));
			} else {
				filterList.add(new SearchFilter("parentId", Operator.EQ, "-1"));
			}

			filterList.add(new SearchFilter("enabled", Operator.EQ, 1));// 有效

			Sort sort = new Sort(Direction.ASC, "orderIndex", "commonCode");

			List<CloudDivision> divisionList = cloudDivisionService.findListByFilter(filterList, sort);

			List<Map<String, Object>> results = new ArrayList<Map<String, Object>>();
			Map<String, Object> map = null;
			List<CloudDivision> childList = null;
			for (CloudDivision division : divisionList) {
				map = new HashMap<String, Object>();

				map.put("nodeType", "xzqh");
				map.put("id", division.getId());
				map.put("name", division.getName());
				map.put("lvl", division.getLevel());

				// 设置属性：ztree.treeNode.isParent
				childList = cloudDivisionService.getAllChildren(division);
				if (CollectionUtils.isNotEmpty(childList)) {
					map.put("isParent", true);
				} else {
					map.put("isParent", false);
				}

				results.add(map);
			}

			return RestResultDto.newSuccess(results);
		} catch (Exception e) {
			return RestResultDto.newFalid("加载树出错", e.getMessage());
		}
	}

	/**
	 * 获取行政区划根节点
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "getRootDivisionId" + FORE_DYNAMIC_SUFFIX, method = RequestMethod.POST)
	public RestResultDto<String> getRootDivisionId(HttpServletRequest request, HttpServletResponse response) {
		try {
			List<SearchFilter> filterList = new ArrayList<SearchFilter>();
			filterList.add(new SearchFilter("parentId", Operator.EQ, "-1")); // 获取到根节点“中国”
			filterList.add(new SearchFilter("enabled", Operator.EQ, 1));// 有效

			List<CloudDivision> list = cloudDivisionService.findListByFilter(filterList, null);

			if (CollectionUtils.isEmpty(list)) {
				throw new VortexException("行政区划根节点不存在，parentId=-1");
			}
			return RestResultDto.newSuccess(list.get(0).getId());
		} catch (Exception e) {
			return RestResultDto.newFalid("获取行政区划根节点出错", e.getMessage());
		}

	}

	/**
	 * 设置默认经纬度
	 * 
	 * @param dto
	 */
	private void setDefaultLatLon(CloudDivisionDto dto) {
		if (dto.getLatitude() != null || dto.getLongitude() != null) {
			return;
		}

		Double defLatitude = null;
		Double defLongitude = null;

		CloudDivisionDto parent = cloudDivisionService.getById(dto.getParentId());
		if (parent != null) {
			defLatitude = parent.getLatitude();
			defLongitude = parent.getLongitude();
		}

		dto.setDefLatitude(defLatitude);
		dto.setDefLongitude(defLongitude);
	}

	@RequestMapping(value = "checkForm/{param}" + FORE_DYNAMIC_SUFFIX, method = RequestMethod.POST)

	public RestResultDto<Boolean> checkForm(@PathVariable("param") String paramName) {
		try {

			String id = SpringmvcUtils.getParameter("id");
			String paramVal = SpringmvcUtils.getParameter(paramName);

			if (StringUtil.isNullOrEmpty(paramVal)) {
				return RestResultDto.newSuccess(false);
			}

			// 此方法目前只检验commonCode，其他字段认为始终通过
			if (!"commonCode".equals(paramName)) {
				return RestResultDto.newSuccess(true);
			}

			List<SearchFilter> filterList = new ArrayList<SearchFilter>();
			filterList.add(new SearchFilter("commonCode", Operator.EQ, paramVal));

			List<CloudDivision> list = cloudDivisionService.findListByFilter(filterList, null);

			if (CollectionUtils.isEmpty(list)) {
				return RestResultDto.newSuccess(true);
			}

			// update记录时，且值没变化，验证通过
			if (!StringUtil.isNullOrEmpty(id) && list.size() == 1 && list.get(0).getId().equals(id)) {
				return RestResultDto.newSuccess(true);
			}

			return RestResultDto.newSuccess(false);
		} catch (Exception e) {
			logger.error("校验参数出错", e.getMessage());
			return RestResultDto.newFalid("校验参数出错", e.getMessage());
		}
	}

	@RequestMapping(value = "add" + BACK_DYNAMIC_SUFFIX, method = RequestMethod.POST)
	@FunctionCode(value = "CF_MANAGE_XZQH_ADD", type = ResponseType.Json)
	public RestResultDto<Boolean> add(HttpServletRequest request, CloudDivisionDto newDivision) {
		try {
			String commonCode = newDivision.getCommonCode();
			if (StringUtil.isNullOrEmpty(commonCode)) {
				throw new VortexException("数字代码不能为空！");
			}
			List<SearchFilter> filterList = new ArrayList<SearchFilter>();
			filterList.add(new SearchFilter("commonCode", Operator.EQ, commonCode));
			List<CloudDivision> xzqhs = null;
			xzqhs = cloudDivisionService.findListByFilter(filterList, null);

			if (CollectionUtils.isNotEmpty(xzqhs)) {
				throw new VortexException("添加失败！数字代码已存在！");
			}

			cloudDivisionService.save(newDivision);

			return RestResultDto.newSuccess(true, "添加成功");
		} catch (Exception e) {
			return RestResultDto.newFalid("添加失败", e.getMessage());
		}
	}

	@RequestMapping(value = "pageList" + FORE_DYNAMIC_SUFFIX, method = RequestMethod.POST)
	public RestResultDto<DataStore<CloudDivisionDto>> pageList(HttpServletRequest request, HttpServletResponse response) {
		try {

			String parentId = SpringmvcUtils.getParameter("parentId");
			if (StringUtil.isNullOrEmpty(parentId)) {
				return RestResultDto.newSuccess(new DataStore<CloudDivisionDto>());
			}

			List<SearchFilter> filterList = new ArrayList<SearchFilter>();
			filterList.add(new SearchFilter("enabled", Operator.EQ, 1));
			filterList.add(new SearchFilter("parentId", Operator.EQ, parentId));
			Sort sort = new Sort(Direction.ASC, "orderIndex", "commonCode");

			List<CloudDivision> list = cloudDivisionService.findListByFilter(filterList, sort);

			List<CloudDivisionDto> dtoList = new ArrayList<CloudDivisionDto>();
			CloudDivisionDto dto = null;
			for (CloudDivision division : list) {
				dto = new CloudDivisionDto();
				BeanUtils.copyProperties(division, dto);
				dtoList.add(dto);
			}

			return RestResultDto.newSuccess(new DataStore<CloudDivisionDto>(dtoList.size(), dtoList));
		} catch (Exception e) {
			logger.error("加载分页失败", e);
			return RestResultDto.newFalid("加载分页失败", e.getMessage());
		}
	}

	@RequestMapping(value = "loadDivisionDtl" + FORE_DYNAMIC_SUFFIX, method = RequestMethod.POST)
	public RestResultDto<CloudDivisionDto> loadDivisionDtl(HttpServletResponse response) {
		try {

			String id = SpringmvcUtils.getParameter("id");
			JsonMapper jsonMapper = new JsonMapper();
			CloudDivisionDto division = cloudDivisionService.getById(id);
			this.setDefaultLatLon(division);
			return RestResultDto.newSuccess(division);
		} catch (Exception e) {
			return RestResultDto.newFalid("获取行政区划出错", e.getMessage());
		}
	}

	@RequestMapping(value = "update" + BACK_DYNAMIC_SUFFIX, method = RequestMethod.POST)
	@FunctionCode(value = "CF_MANAGE_XZQH_UPDATE", type = ResponseType.Json)
	public RestResultDto<Boolean> update(HttpServletRequest request, CloudDivisionDto dto) {
		try {
			cloudDivisionService.update(dto);
			return RestResultDto.newSuccess(true, "更新成功");
		} catch (Exception e) {
			logger.debug("更新失败", e);
			return RestResultDto.newFalid("更新失败", e.getMessage());
		}
	}

	@RequestMapping(value = "delete" + BACK_DYNAMIC_SUFFIX, method = RequestMethod.POST)
	@FunctionCode(value = "CF_MANAGE_XZQH_DEL", type = ResponseType.Json)
	public RestResultDto<Boolean> delete(HttpServletRequest request, @RequestBody String[] ids) {

		try {
			long deleted = cloudDivisionService.deleteByIdArr(ids, false); // 不需级联删除
			return RestResultDto.newSuccess(true, "共" + ids.length + "条,删除成功" + deleted + "条," + "删除失败" + (ids.length - deleted) + "条");
		} catch (Exception e) {
			logger.error("delete", e);

			return RestResultDto.newFalid("删除失败", e.getMessage());
		}

	}

	@RequestMapping(value = "cascadeDeleteChildren" + BACK_DYNAMIC_SUFFIX, method = RequestMethod.POST)
	public RestResultDto<Boolean> cascadeDeleteChildren(@RequestBody String[] ids) {

		try {
			long deleted = cloudDivisionService.deleteByIdArr(ids, true); // 需级联删除

			return RestResultDto.newSuccess(true, "共" + ids.length + "条,删除成功" + deleted + "条," + "删除失败" + (ids.length - deleted) + "条");
		} catch (Exception e) {
			logger.error("cascadeDeleteChildren", e);

			return RestResultDto.newFalid("级联删除失败", e.getMessage());
		}

	}

	@RequestMapping(value = "batchUpdate" + BACK_DYNAMIC_SUFFIX, method = RequestMethod.POST, consumes = "application/json")
	@FunctionCode(value = "CF_MANAGE_XZQH_BAT_UPDATE", type = ResponseType.Json)
	public RestResultDto<Boolean> batchUpdate(HttpServletRequest request, @RequestBody CloudDivisionDto.BatchUpdateList dtoList) {
		try {

			if (CollectionUtils.isEmpty(dtoList)) {
				throw new VortexException("请勾选要保存的行政区划！");
			}

			for (CloudDivisionDto.BatchUpdate dto : dtoList) {
				CloudDivision division = cloudDivisionService.findOne(dto.getId());
				if (division == null) {
					continue;
				}

				BeanUtils.copyProperties(dto, division);
				cloudDivisionService.update(division);

			}

			return RestResultDto.newSuccess(true, "批量更新成功");
		} catch (Exception e) {
			return RestResultDto.newFalid("批量更新行政区划时出错", e.getMessage());
		}
	}
}
