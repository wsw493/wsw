package com.vortex.cloud.ums.web;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
import org.springframework.data.domain.Sort.Order;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.vortex.cloud.ums.dataaccess.service.ICloudDepartmentService;
import com.vortex.cloud.ums.dataaccess.service.ICloudOrganizationService;
import com.vortex.cloud.ums.dataaccess.service.IWorkElementService;
import com.vortex.cloud.ums.dataaccess.service.IWorkElementTempService;
import com.vortex.cloud.ums.dataaccess.service.IWorkElementTypeService;
import com.vortex.cloud.ums.dto.WorkElementDto;
import com.vortex.cloud.ums.model.WorkElement;
import com.vortex.cloud.ums.model.WorkElementType;
import com.vortex.cloud.ums.model.upload.WorkElementTemp;
import com.vortex.cloud.ums.support.ManagementConstant;
import com.vortex.cloud.ums.tree.DepartmentTree;
import com.vortex.cloud.ums.tree.OrganizationTree;
import com.vortex.cloud.ums.tree.OrganizationTreeWithPermission;
import com.vortex.cloud.ums.util.CommonUtils;
import com.vortex.cloud.ums.util.FileOperateUtil;
import com.vortex.cloud.ums.util.UploadUtil;
import com.vortex.cloud.ums.util.support.ForeContext;
import com.vortex.cloud.ums.util.tree.TreeService;
import com.vortex.cloud.ums.util.web.BaseController;
import com.vortex.cloud.vfs.common.lang.StringUtil;
import com.vortex.cloud.vfs.common.mapper.JsonMapper;
import com.vortex.cloud.vfs.common.web.MediaTypes;
import com.vortex.cloud.vfs.common.web.springmvc.SpringmvcUtils;
import com.vortex.cloud.vfs.data.dto.DataStore;
import  com.vortex.cloud.vfs.data.dto.LoginReturnInfoDto;
import com.vortex.cloud.vfs.data.dto.RestResultDto;
import com.vortex.cloud.vfs.data.support.SearchFilter;
import com.vortex.cloud.vfs.data.support.SearchFilter.Operator;
import com.vortex.cloud.vfs.data.support.SearchFilters;

/**
 * 图元类型controller
 * 
 * @author lsm
 *
 */
@RestController
@RequestMapping("cloud/management/workElement")
public class WorkElementController extends BaseController {
	private static final Logger logger = LoggerFactory.getLogger(WorkElementController.class);

	@Resource
	private IWorkElementService workElementService;
	@Resource
	private IWorkElementTypeService workElementTypeService;
	@Resource
	private ICloudDepartmentService cloudDepartmentService;
	@Resource
	private ICloudOrganizationService cloudOrganizationService;
	@Resource
	private IWorkElementTempService workElementTempService;
	@Resource
	private TreeService treeService;

	@RequestMapping(value = "pageList" + ManagementConstant.PERMISSION_SUFFIX_READ, method = RequestMethod.POST)
	public DataStore<WorkElementDto> pageList(HttpServletRequest request, HttpServletResponse response) {
		DataStore<WorkElementDto> dataStore = new DataStore<>();
		try {
			LoginReturnInfoDto loginInfo = super.getLoginInfo(request);
			String tenantId = loginInfo.getTenantId();

			// 列表页面url默认带上图元类型编码typeCode的模糊查询，先做一次查询，转换为workElementTypeId
			String typeCode = SpringmvcUtils.getParameter("typeCode");
			List<String> typeIdList = new ArrayList<>();
			if (StringUtils.isNotEmpty(typeCode)) {
				List<SearchFilter> typeFilters = new ArrayList<>();
				typeFilters.add(new SearchFilter("code", Operator.LIKE, typeCode));
				typeFilters.add(new SearchFilter("tenantId", Operator.EQ, tenantId));
				List<WorkElementType> types = workElementTypeService.findListByFilter(typeFilters, null);
				if (CollectionUtils.isNotEmpty(types)) {
					for (WorkElementType workElementType : types) {
						typeIdList.add(workElementType.getId());
					}
				}
			}

			List<SearchFilter> filterList = CommonUtils.buildFromHttpRequest(request);
			filterList.add(new SearchFilter("tenantId", Operator.EQ, tenantId));
			if (CollectionUtils.isNotEmpty(typeIdList)) {
				filterList.add(new SearchFilter("workElementTypeId", Operator.IN, typeIdList.toArray(new String[typeIdList.size()])));
			}

			Sort sort = sortMethod();
			Pageable pageable = ForeContext.getPageable(request, sort);
			Page<WorkElement> page = workElementService.findPageByFilter(pageable, filterList);

			if (null != page) {
				List<WorkElementDto> result = null;
				// 将数据转移到dto
				result = workElementService.transferModelToDto(page.getContent());
				dataStore = new DataStore<>(page.getTotalElements(), result);
			}
		} catch (Exception e) {
			logger.error(this.getClass().getSimpleName() + ".pageList", e);
		}

		return dataStore;
	}

	@RequestMapping(value = "pageListByPermission" + ManagementConstant.PERMISSION_SUFFIX_READ, method = RequestMethod.POST)

	public DataStore<WorkElementDto> pageListByPermission(HttpServletRequest request, HttpServletResponse response) {
		DataStore<WorkElementDto> dataStore = new DataStore<>();
		try {
			LoginReturnInfoDto loginInfo = super.getLoginInfo(request);
			String tenantId = loginInfo.getTenantId();
			String userId = loginInfo.getUserId();
			// 列表页面url默认带上图元类型编码typeCode的模糊查询，先做一次查询，转换为workElementTypeId
			String typeCode = SpringmvcUtils.getParameter("typeCode");
			List<String> typeIdList = new ArrayList<>();
			if (StringUtils.isNotEmpty(typeCode)) {
				List<SearchFilter> typeFilters = new ArrayList<>();
				typeFilters.add(new SearchFilter("code", Operator.LIKE, typeCode));
				typeFilters.add(new SearchFilter("tenantId", Operator.EQ, tenantId));
				List<WorkElementType> types = workElementTypeService.findListByFilter(typeFilters, null);
				if (CollectionUtils.isNotEmpty(types)) {
					for (WorkElementType workElementType : types) {
						typeIdList.add(workElementType.getId());
					}
				}
			}

			List<SearchFilter> filterList = CommonUtils.buildFromHttpRequest(request);
			filterList.add(new SearchFilter("tenantId", Operator.EQ, tenantId));
			if (CollectionUtils.isNotEmpty(typeIdList)) {
				filterList.add(new SearchFilter("workElementTypeId", Operator.IN, typeIdList.toArray(new String[typeIdList.size()])));
			}

			Sort sort = sortMethod();
			Pageable pageable = ForeContext.getPageable(request, sort);

			SearchFilters andSearchFilters = companyPermissionFilter(tenantId, userId);

			Page<WorkElement> page = workElementService.findPageByFilters(pageable, andSearchFilters.addSearchFilter(filterList));

			if (null != page) {
				List<WorkElementDto> result = null;
				// 将数据转移到dto
				result = workElementService.transferModelToDto(page.getContent());
				dataStore = new DataStore<>(page.getTotalElements(), result);
			}
		} catch (Exception e) {
			logger.error(this.getClass().getSimpleName() + ".pageList", e);
		}

		return dataStore;
	}

	@RequestMapping(value = "findbyid/{id}" + ManagementConstant.PERMISSION_SUFFIX_READ, method = { RequestMethod.POST, RequestMethod.GET })

	public WorkElementDto findById(@PathVariable String id) {
		WorkElementDto dto = null;
		try {
			dto = workElementService.getWorkElementById(id);
		} catch (Exception e) {
			logger.error(this.getClass().getSimpleName() + ".findbyid", e);

		}
		return dto;
	}

	@RequestMapping(value = "save" + ManagementConstant.PERMISSION_SUFFIX_EDIT, method = RequestMethod.POST)
	public RestResultDto<Boolean> saveWorkElement(HttpServletRequest request, WorkElementDto dto) {
		try {

			LoginReturnInfoDto loginInfo = super.getLoginInfo(request);
			String tenantId = loginInfo.getTenantId();
			dto.setTenantId(tenantId);

			// 查询数据库是否存在数据（防止保存按钮，多次点击，产生重复数据）
			List<SearchFilter> searchFilter = Lists.newArrayList();
			searchFilter.add(new SearchFilter("workElement.name", SearchFilter.Operator.EQ, dto.getName()));
			List<WorkElement> list = workElementService.findListByFilter(CommonUtils.bindTenantId(searchFilter), null);

			if (CollectionUtils.isEmpty(list)) {
				workElementService.saveWorkElement(dto);
			}

			return RestResultDto.newSuccess(true, "保存成功");
		} catch (Exception e) {
			logger.error("保存失败", e);
			return RestResultDto.newFalid("保存失败", e.getMessage());
		}
	}

	@RequestMapping(value = "update" + ManagementConstant.PERMISSION_SUFFIX_EDIT, method = RequestMethod.POST)
	public RestResultDto<Boolean> updateWorkElement(HttpServletRequest request, WorkElementDto dto) {
		try {
			LoginReturnInfoDto loginInfo = super.getLoginInfo(request);
			String tenantId = loginInfo.getTenantId();
			dto.setTenantId(tenantId);

			workElementService.updateWorkElement(dto);
			return RestResultDto.newSuccess(true, "更新成功");
		} catch (Exception e) {
			logger.error(this.getClass().getSimpleName() + ".update", e);
			return RestResultDto.newFalid("更新失败", e.getMessage());
		}
	}

	@RequestMapping(value = "checkForm/{param}" + ManagementConstant.PERMISSION_SUFFIX_READ, method = RequestMethod.POST)

	public RestResultDto<Boolean> checkForm(@PathVariable("param") String param, HttpServletRequest request) {
		try {

			String value = SpringmvcUtils.getParameter(param);
			String id = SpringmvcUtils.getParameter("id");

			if (StringUtils.isEmpty(value)) {
				return RestResultDto.newSuccess(false);
			}

			List<SearchFilter> searchFilter = Lists.newArrayList();
			searchFilter.add(new SearchFilter("workElement." + param, SearchFilter.Operator.EQ, value));
			List<WorkElement> list = workElementService.findListByFilter(CommonUtils.bindTenantId(searchFilter), null);

			if (CollectionUtils.isEmpty(list)) {
				return RestResultDto.newSuccess(true);
			}

			if (StringUtils.isNotEmpty(id) && list.size() == 1 && list.get(0).getId().equals(id)) {
				return RestResultDto.newSuccess(true);
			}

			return RestResultDto.newSuccess(false);
		} catch (Exception e) {
			return RestResultDto.newFalid("校验参数出错", e.getMessage());
		}
	}

	@RequestMapping(value = "deletes" + ManagementConstant.PERMISSION_SUFFIX_EDIT, method = RequestMethod.POST)

	public RestResultDto<Boolean> deletes(HttpServletRequest request, @RequestBody String[] ids) {
		try {

			List<String> deleteList = Lists.newArrayList();
			if (ids != null && ids.length > 0) {
				deleteList = Lists.newArrayList(ids);
			}

			workElementService.deleteWorkElements(deleteList);
			return RestResultDto.newSuccess(true, "共" + ids.length + "条,删除成功" + deleteList.size() + "条," + "删除失败" + (ids.length - deleteList.size()) + "条");
		} catch (Exception e) {
			logger.error(this.getClass().getSimpleName() + ".deletes", e);

			return RestResultDto.newFalid("删除失败", e.getMessage());
		}

	}

	@RequestMapping(value = "delete/{id}" + ManagementConstant.PERMISSION_SUFFIX_EDIT, method = RequestMethod.POST)

	public RestResultDto<Boolean> delete(HttpServletRequest request, @PathVariable String id) {

		try {
			workElementService.deleteWorkElement(id);
			return RestResultDto.newSuccess(true, "删除成功");
		} catch (Exception e) {
			logger.error(this.getClass().getSimpleName() + ".delete", e);

			return RestResultDto.newFalid("删除失败", e.getMessage());
		}

	}

	@RequestMapping(value = "getWorkElementsByType" + ManagementConstant.PERMISSION_SUFFIX_READ, method = RequestMethod.POST)

	public RestResultDto<List<WorkElement>> getWorkElementsByType(HttpServletRequest request) {
		try {
			List<WorkElement> list = null;
			String shapeTypes = SpringmvcUtils.getParameter("shapeTypes");
			String[] typesList = null;
			if (StringUtils.isNotBlank(shapeTypes)) {
				typesList = shapeTypes.split(",");
			}
			LoginReturnInfoDto loginInfo = super.getLoginInfo(request);
			String tenantId = loginInfo.getTenantId();

			list = workElementService.getWorkElementsByType(typesList, tenantId);
			return RestResultDto.newSuccess(list);
		} catch (Exception e) {
			logger.error(this.getClass().getSimpleName() + ".getWorkElementsByType", e);
			return RestResultDto.newFalid("根据类型回去图元列表失败", e.getMessage());
		}
	}

	@RequestMapping(value = "loadDepartTree" + ManagementConstant.PERMISSION_SUFFIX_READ, method = { RequestMethod.GET, RequestMethod.POST })

	public RestResultDto<String> loadDepartTree(HttpServletRequest request) {
		try {

			LoginReturnInfoDto loginInfo = super.getLoginInfo(request);
			String tenantId = loginInfo.getTenantId();
			DepartmentTree departmentTree = DepartmentTree.getInstance();
			departmentTree.reloadDeptTree(tenantId, null);
			String jsonStr = treeService.generateJsonCheckboxTree(departmentTree, false);
			return RestResultDto.newSuccess(jsonStr);
		} catch (Exception e) {
			logger.error("加载树出错", e);
			return RestResultDto.newFalid("加载树出错", e.getMessage());
		}
	}

	@RequestMapping(value = "loadCompanySectionTree" + ManagementConstant.PERMISSION_SUFFIX_READ, method = RequestMethod.POST)
	public RestResultDto<String> loadCompanySectionTree(HttpServletRequest request, HttpServletResponse response) {
		try {
			LoginReturnInfoDto info = this.getLoginInfo(request);

			// 列表页面url默认带上图元类型编码typeCode的模糊查询，先做一次查询，转换为workElementTypeId
			String typeCode = SpringmvcUtils.getParameter("typeCode");
			List<String> typeIdList = new ArrayList<>();
			if (StringUtils.isNotEmpty(typeCode)) {
				List<SearchFilter> typeFilters = new ArrayList<>();
				typeFilters.add(new SearchFilter("code", Operator.LIKE, typeCode));
				typeFilters.add(new SearchFilter("tenantId", Operator.EQ, info.getTenantId()));
				List<WorkElementType> types = workElementTypeService.findListByFilter(typeFilters, null);
				if (CollectionUtils.isNotEmpty(types)) {
					for (WorkElementType workElementType : types) {
						typeIdList.add(workElementType.getId());
					}
				}
			}

			List<SearchFilter> searchFilter = CommonUtils.buildFromHttpRequest(request);
			searchFilter.add(new SearchFilter("tenantId", Operator.EQ, info.getTenantId()));
			if (CollectionUtils.isNotEmpty(typeIdList)) {
				searchFilter.add(new SearchFilter("workElementTypeId", Operator.IN, typeIdList.toArray(new String[typeIdList.size()])));
			}

			response.setContentType("application/json;charset=UTF-8");

			OrganizationTree tree = OrganizationTree.getInstance();
			tree.reloadOrganizationElementTree(info, searchFilter);
			String jsonStr = treeService.generateJsonCheckboxTree(tree, false);

			return RestResultDto.newSuccess(jsonStr);
		} catch (Exception e) {
			logger.error(this.getClass().getSimpleName() + ".loadCompanySectionTree", e);
			return RestResultDto.newFalid("加载树出错", e.getMessage());
		}
	}

	@RequestMapping(value = "loadCompanySectionTreeByPermission" + ManagementConstant.PERMISSION_SUFFIX_READ, method = RequestMethod.POST)
	public RestResultDto<String> loadCompanySectionTreeByPermission(HttpServletRequest request, HttpServletResponse response) {
		try {
			LoginReturnInfoDto info = this.getLoginInfo(request);
			List<SearchFilter> searchFilter = CommonUtils.buildFromHttpRequest(request);
			// 列表页面url默认带上图元类型编码typeCode的模糊查询，先做一次查询，转换为workElementTypeId
			String typeCode = SpringmvcUtils.getParameter("typeCode");
			List<String> typeIdList = new ArrayList<>();
			if (StringUtils.isNotEmpty(typeCode)) {
				List<SearchFilter> typeFilters = new ArrayList<>();
				typeFilters.add(new SearchFilter("code", Operator.LIKE, typeCode));
				typeFilters.add(new SearchFilter("tenantId", Operator.EQ, info.getTenantId()));
				List<WorkElementType> types = workElementTypeService.findListByFilter(typeFilters, null);
				if (CollectionUtils.isNotEmpty(types)) {
					for (WorkElementType workElementType : types) {
						typeIdList.add(workElementType.getId());
					}
				}
				searchFilter.add(new SearchFilter("workElementTypeId", Operator.IN, typeIdList.toArray(new String[typeIdList.size()])));
			}
			
			searchFilter.add(new SearchFilter("tenantId", Operator.EQ, info.getTenantId()));

			response.setContentType("application/json;charset=UTF-8");

			OrganizationTreeWithPermission tree = OrganizationTreeWithPermission.getInstance();
			tree.reloadOrganizationElementTree(info, searchFilter);
			String jsonStr = treeService.generateJsonCheckboxTree(tree, false);

			return RestResultDto.newSuccess(jsonStr);

		} catch (Exception e) {
			logger.error(this.getClass().getSimpleName() + ".loadCompanySectionTree", e);
			return RestResultDto.newFalid("加载树出错", e.getMessage());
		}
	}

	@RequestMapping(value = "uploadImportData")
	public void uploadImportData(HttpServletRequest request, HttpServletResponse response) {
		try {
			UploadUtil.uploadImportData(request, response, WorkElementTemp.class, workElementTempService);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("导入图元出错", e);
		}

	}

	@RequestMapping(value = "downloadTemplate")
	public void downloadTemplate(HttpServletRequest request, HttpServletResponse response) {
		try {
			UploadUtil.downloadTemplate(request, response, "图元导入模版.zip");
		} catch (Exception e) {
			logger.error("下载模板失败", e);
		}

	}

	@RequestMapping(value = "download")
	public void download(HttpServletRequest request, HttpServletResponse response) {

		try {
			response.setContentType("text/html;charset=utf-8");
			request.setCharacterEncoding("UTF-8");
			LoginReturnInfoDto loginReturnInfoDto = super.getLoginInfo(request);
			String userId = loginReturnInfoDto.getUserId();
			String tenantId = loginReturnInfoDto.getTenantId();
			// 查询配置信息
			String title = "图元信息表";
			// 查询条件
			List<SearchFilter> searchFilter = CommonUtils.buildFromHttpRequest(request);
			SearchFilters andSearchFilters = companyPermissionFilter(tenantId, userId);

			// 列表页面url默认带上图元类型编码typeCode的模糊查询，先做一次查询，转换为workElementTypeId
			String typeCode = SpringmvcUtils.getParameter("typeCode");
			List<String> typeIdList = new ArrayList<>();
			if (StringUtils.isNotEmpty(typeCode)) {
				List<SearchFilter> typeFilters = new ArrayList<>();
				typeFilters.add(new SearchFilter("code", Operator.LIKE, typeCode));
				typeFilters.add(new SearchFilter("tenantId", Operator.EQ, CommonUtils.getTenantId()));
				List<WorkElementType> types = workElementTypeService.findListByFilter(typeFilters, null);
				if (CollectionUtils.isNotEmpty(types)) {
					for (WorkElementType workElementType : types) {
						typeIdList.add(workElementType.getId());
					}
				}
			}
			if (CollectionUtils.isNotEmpty(typeIdList)) {
				searchFilter.add(new SearchFilter("workElementTypeId", Operator.IN, typeIdList.toArray(new String[typeIdList.size()])));
			}

			String columnFields = SpringmvcUtils.getParameter("columnFields");
			String columnNames = SpringmvcUtils.getParameter("columnNames");
			// 不明白当初为什么加这个玩意，去了最后一列，我暂时删除了。 by lsm
			// columnFields = columnFields.substring(0,
			// columnFields.lastIndexOf(","));
			// columnNames = columnNames.substring(0,
			// columnNames.lastIndexOf(","));
			String downloadAll = SpringmvcUtils.getParameter("downloadAll");
			String downloadIds = SpringmvcUtils.getParameter("downloadIds");

			// 排序
			Sort defSort = sortMethod();

			List<WorkElementDto> children = Lists.newArrayList();
			boolean isDownloadAll = (StringUtil.isNullOrEmpty(downloadAll) ? false : Boolean.valueOf(downloadAll));
			List<WorkElement> list = null;
			if (isDownloadAll) {
				if (!StringUtil.isNullOrEmpty(downloadIds)) {
					searchFilter.add(new SearchFilter("id", Operator.IN, StringUtil.splitComma(downloadIds)));
				}
				CommonUtils.bindTenantId(searchFilter);

				list = workElementService.findListByFilters(andSearchFilters.addSearchFilter(searchFilter), defSort);
			} else {
				Pageable pageable = ForeContext.getPageable(request, defSort);
				CommonUtils.bindTenantId(searchFilter);
				Page<WorkElement> pageResult = workElementService.findPageByFilters(pageable, andSearchFilters.addSearchFilter(searchFilter));
				if (null != pageResult) {
					list = pageResult.getContent();
				}
			}
			if (CollectionUtils.isNotEmpty(list)) {
				children.addAll(workElementService.transferModelToDto(list));

			}
			FileOperateUtil.exportExcel(request, response, title, columnFields, columnNames, children);
		} catch (Exception e) {
			logger.error(this.getClass().getSimpleName() + ".download", e);
		}
	}

	private Sort sortMethod() {
		String sort = SpringmvcUtils.getParameter("sort");
		String order = SpringmvcUtils.getParameter("order");
		Sort defSort = new Sort(Direction.ASC, "code");
		Order order2 = new Order(Direction.DESC, "createTime");
		List<Order> orders = CommonUtils.getCommonSort(sort, order, "workElement");
		orders.add(order2);
		if (CollectionUtils.isNotEmpty(orders)) {
			defSort = new Sort(orders);
		}
		return defSort;
	}

	@RequestMapping(value = "autoComplete/{type}" + ManagementConstant.BACK_DYNAMIC_SUFFIX, produces = MediaTypes.JSON_UTF_8)
	public RestResultDto<Map<String, String>> autoComplete(@PathVariable String type, @RequestParam("q") String q, @RequestParam("limit") String limit, HttpServletRequest request) {
		try {

			LoginReturnInfoDto loginInfo = super.getLoginInfo(request);
			String tenantId = loginInfo.getTenantId();
			JsonMapper jsonMapper = new JsonMapper();
			Map<String, String> returnMap = Maps.newHashMap();
			List<SearchFilter> filters = Lists.newArrayList();
			filters.add(new SearchFilter(type, Operator.LIKE, q));
			filters.add(new SearchFilter("tenantId", Operator.EQ, tenantId));

			// 列表页面url默认带上图元类型编码typeCode的模糊查询，先做一次查询，转换为workElementTypeId
			String typeCode = SpringmvcUtils.getParameter("typeCode");
			List<String> typeIdList = new ArrayList<>();
			if (StringUtils.isNotEmpty(typeCode)) {
				List<SearchFilter> typeFilters = new ArrayList<>();
				typeFilters.add(new SearchFilter("code", Operator.LIKE, typeCode));
				typeFilters.add(new SearchFilter("tenantId", Operator.EQ, tenantId));
				List<WorkElementType> types = workElementTypeService.findListByFilter(typeFilters, null);
				if (CollectionUtils.isNotEmpty(types)) {
					for (WorkElementType workElementType : types) {
						typeIdList.add(workElementType.getId());
					}
				}
			}
			if (CollectionUtils.isNotEmpty(typeIdList)) {
				filters.add(new SearchFilter("workElementTypeId", Operator.IN, typeIdList.toArray(new String[typeIdList.size()])));
			}

			List<WorkElement> list = workElementService.findListByFilter(filters, null);
			List<Object> listJsonValue = Lists.newArrayList();
			Map<String, Object> mapValue = null;
			if (CollectionUtils.isNotEmpty(list)) {
				for (WorkElement entity : list) {
					mapValue = Maps.newHashMap();
					mapValue.put("id", entity.getId());
					if (StringUtils.equals(type, "name")) {
						mapValue.put("value", entity.getName());
					}
					listJsonValue.add(mapValue);
				}
			}
			returnMap.put("value", jsonMapper.toJson(listJsonValue));
			return RestResultDto.newSuccess(returnMap);
		} catch (Exception e) {
			logger.error("自动提示出错", e);
			return RestResultDto.newFalid("自动提示出错", e.getMessage());
		}
	}

	@RequestMapping(value = "autoCompleteWithPermission/{type}" + ManagementConstant.BACK_DYNAMIC_SUFFIX, produces = MediaTypes.JSON_UTF_8)

	public RestResultDto<Map<String, String>> autoCompleteWithPermission(@PathVariable String type, @RequestParam("q") String q, @RequestParam("limit") String limit, HttpServletRequest request) {
		try {

			LoginReturnInfoDto loginInfo = super.getLoginInfo(request);
			String tenantId = loginInfo.getTenantId();
			String userId = loginInfo.getUserId();
			JsonMapper jsonMapper = new JsonMapper();
			Map<String, String> returnMap = Maps.newHashMap();
			List<SearchFilter> filters = Lists.newArrayList();
			filters.add(new SearchFilter(type, Operator.LIKE, q));
			filters.add(new SearchFilter("tenantId", Operator.EQ, tenantId));

			// 列表页面url默认带上图元类型编码typeCode的模糊查询，先做一次查询，转换为workElementTypeId
			String typeCode = SpringmvcUtils.getParameter("typeCode");
			List<String> typeIdList = new ArrayList<>();
			if (StringUtils.isNotEmpty(typeCode)) {
				List<SearchFilter> typeFilters = new ArrayList<>();
				typeFilters.add(new SearchFilter("code", Operator.LIKE, typeCode));
				typeFilters.add(new SearchFilter("tenantId", Operator.EQ, tenantId));
				List<WorkElementType> types = workElementTypeService.findListByFilter(typeFilters, null);
				if (CollectionUtils.isNotEmpty(types)) {
					for (WorkElementType workElementType : types) {
						typeIdList.add(workElementType.getId());
					}
				}
			}
			if (CollectionUtils.isNotEmpty(typeIdList)) {
				filters.add(new SearchFilter("workElementTypeId", Operator.IN, typeIdList.toArray(new String[typeIdList.size()])));
			}
			SearchFilters andSearchFilters = companyPermissionFilter(tenantId, userId);

			List<WorkElement> list = workElementService.findListByFilters(andSearchFilters.addSearchFilter(filters), null);
			List<Object> listJsonValue = Lists.newArrayList();
			Map<String, Object> mapValue = null;
			if (CollectionUtils.isNotEmpty(list)) {
				for (WorkElement entity : list) {
					mapValue = Maps.newHashMap();
					mapValue.put("id", entity.getId());
					if (StringUtils.equals(type, "name")) {
						mapValue.put("value", entity.getName());
					}
					listJsonValue.add(mapValue);
				}
			}
			returnMap.put("value", jsonMapper.toJson(listJsonValue));
			return RestResultDto.newSuccess(returnMap);
		} catch (Exception e) {
			logger.error("自动提示出错", e);
			return RestResultDto.newFalid("自动提示出错", e.getMessage());
		}
	}

	/**
	 * 获取权限过滤的filter
	 * 
	 * @param tenantId
	 * @param userId
	 * @return
	 */
	private SearchFilters companyPermissionFilter(String tenantId, String userId) {
		SearchFilters andSearchFilters = new SearchFilters(SearchFilters.Operator.AND);

		// 获取该用户的有权限的机构列表
		List<String> companyIds = getPermissionCompanys(userId, tenantId);

		SearchFilters orSearchFilters = new SearchFilters();
		orSearchFilters.setOperator(SearchFilters.Operator.OR);
		if (CollectionUtils.isNotEmpty(companyIds)) {
			orSearchFilters.add(new SearchFilter("departmentId", Operator.IN, companyIds.toArray()));
		}
		orSearchFilters.add(new SearchFilter("departmentId", Operator.NULL, null));
		orSearchFilters.add(new SearchFilter("departmentId", Operator.EQ, ""));

		andSearchFilters.add(orSearchFilters);
		return andSearchFilters;
	}

	/**
	 * 获取该用户的有权限的机构列表
	 * 
	 * @param userId
	 * @return
	 */
	private List<String> getPermissionCompanys(String userId, String tenantId) {

		// 获取该用户有权限的org和department，（自定义中只认为全选中的是有权限的）
		List<String> companyIds = cloudOrganizationService.getCompanyIdsWithPermission(userId, tenantId);

		return companyIds;
	}
}
