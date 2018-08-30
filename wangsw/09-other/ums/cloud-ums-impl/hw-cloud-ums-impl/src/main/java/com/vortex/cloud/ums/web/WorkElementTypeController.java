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
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
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
import com.vortex.cloud.ums.dataaccess.service.IWorkElementTypeService;
import com.vortex.cloud.ums.dto.CloudDepartmentDto;
import com.vortex.cloud.ums.dto.CloudOrganizationDto;
import  com.vortex.cloud.vfs.data.dto.LoginReturnInfoDto;
import com.vortex.cloud.ums.dto.WorkElementTypeDto;
import com.vortex.cloud.ums.model.WorkElementType;
import com.vortex.cloud.ums.support.ManagementConstant;
import com.vortex.cloud.ums.tree.OrganizationTree;
import com.vortex.cloud.ums.util.CommonUtils;
import com.vortex.cloud.ums.util.web.BaseController;
import com.vortex.cloud.ums.util.support.ForeContext;
import com.vortex.cloud.ums.util.tree.ITreeService;
import com.vortex.cloud.vfs.common.exception.VortexException;
import com.vortex.cloud.vfs.common.mapper.JsonMapper;
import com.vortex.cloud.vfs.common.web.MediaTypes;
import com.vortex.cloud.vfs.common.web.springmvc.SpringmvcUtils;
import com.vortex.cloud.vfs.data.dto.DataStore;
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
@RequestMapping("cloud/management/workElementType")
public class WorkElementTypeController extends BaseController {
	private Logger logger = LoggerFactory.getLogger(WorkElementTypeController.class);
	/**
	 * 图元类型服务
	 */
	@Resource
	private IWorkElementTypeService workElementTypeService;
	@Resource
	private ICloudDepartmentService cloudDepartmentService;
	@Resource
	private ICloudOrganizationService cloudOrganizationService;
	@Resource
	private ITreeService treeService;

	/**
	 * 返回图元类型列表
	 * 
	 * @param request
	 * @param response
	 * @return
	 * 
	 */

	@RequestMapping(value = "pageList" + ManagementConstant.PERMISSION_SUFFIX_READ, method = RequestMethod.POST)
	public RestResultDto<DataStore<WorkElementTypeDto>> pageList(HttpServletRequest request, HttpServletResponse response) {
		try {

			LoginReturnInfoDto loginInfo = super.getLoginInfo(request);
			String tenantId = loginInfo.getTenantId();

			List<SearchFilter> searchFilters = CommonUtils.buildFromHttpRequest(request);
			searchFilters.add(new SearchFilter("tenantId", Operator.EQ, tenantId));

			Sort sort = new Sort(Direction.DESC, "lastChangeTime");
			Pageable pageable = ForeContext.getPageable(request, sort);
			Page<WorkElementType> page = workElementTypeService.findPageByFilter(pageable, searchFilters);
			DataStore<WorkElementTypeDto> dataStore = null;
			if (null != page) {
				List<WorkElementTypeDto> result = null;
				// 将数据转移到dto
				result = tranferIntoDto(page.getContent());

				dataStore = new DataStore<>(page.getTotalElements(), result);
			} else {
				dataStore = new DataStore<>();
			}
			return RestResultDto.newSuccess(dataStore);

		} catch (Exception e) {
			logger.error("查询分页列表失败", e);
			return RestResultDto.newFalid("查询分页列表失败", e.getMessage());
		}
	}

	/**
	 * 返回图元类型列表
	 * 
	 * @param request
	 * @param response
	 * @return
	 * 
	 */

	@RequestMapping(value = "pageListByPermission" + ManagementConstant.PERMISSION_SUFFIX_READ, method = RequestMethod.POST)
	public RestResultDto<DataStore<WorkElementTypeDto>> pageListByPermission(HttpServletRequest request, HttpServletResponse response) {
		try {

			LoginReturnInfoDto loginInfo = super.getLoginInfo(request);
			String tenantId = loginInfo.getTenantId();
			String userId = loginInfo.getUserId();
			List<SearchFilter> searchFilters = CommonUtils.buildFromHttpRequest(request);
			searchFilters.add(new SearchFilter("tenantId", Operator.EQ, tenantId));

			Sort sort = new Sort(Direction.DESC, "lastChangeTime");
			Pageable pageable = ForeContext.getPageable(request, sort);
			Page<WorkElementType> page = workElementTypeService.findPageByPermission(pageable, searchFilters, userId, tenantId);
			DataStore<WorkElementTypeDto> dataStore = null;
			if (null != page) {
				List<WorkElementTypeDto> result = null;
				// 将数据转移到dto
				result = tranferIntoDto(page.getContent());

				dataStore = new DataStore<>(page.getTotalElements(), result);
			} else {
				dataStore = new DataStore<>();
			}
			return RestResultDto.newSuccess(dataStore);

		} catch (Exception e) {
			logger.error("查询分页列表失败", e);
			return RestResultDto.newFalid("查询分页列表失败", e.getMessage());
		}
	}

	/**
	 * 根据id查找记录
	 * 
	 * @param id
	 * @return
	 */

	@RequestMapping(value = "findbyid/{id}" + ManagementConstant.PERMISSION_SUFFIX_READ, method = { RequestMethod.POST, RequestMethod.GET })
	public RestResultDto<WorkElementTypeDto> findById(@PathVariable String id) {
		WorkElementTypeDto dto = null;
		try {
			dto = workElementTypeService.findWorkElementTypeDtoById(id);
			return RestResultDto.newSuccess(dto);
		} catch (Exception e) {
			logger.error("WorkElementTypeController.findById", e);
			return RestResultDto.newFalid("根据id查询图元类型失败", e.getMessage());
		}
	}

	/**
	 * 保存数据
	 * 
	 * @param request
	 * @param dto
	 * @return
	 */

	@RequestMapping(value = "save" + ManagementConstant.PERMISSION_SUFFIX_EDIT, method = RequestMethod.POST)
	public RestResultDto<Boolean> saveWorkElementType(HttpServletRequest request, WorkElementTypeDto dto) {
		try {

			LoginReturnInfoDto loginInfo = super.getLoginInfo(request);
			String tenantId = loginInfo.getTenantId();
			dto.setTenantId(tenantId);

			// 查询数据库是否存在数据（防止保存按钮，多次点击，产生重复数据）
			List<SearchFilter> searchFilter = Lists.newArrayList();
			searchFilter.add(new SearchFilter("workElementType.name", SearchFilter.Operator.EQ, dto.getName()));
			List<WorkElementType> list = workElementTypeService.findListByFilter(CommonUtils.bindTenantId(searchFilter), null);

			if (CollectionUtils.isEmpty(list)) {
				workElementTypeService.saveWorkElementType(dto);
				return RestResultDto.newSuccess(true, "保存成功");
			} else {
				logger.error("WorkElementTypeController.saveWorkElementType", "保存失败：已存在此图元类型</br>可能原因：保存按钮，多次点击！");
				throw new VortexException("保存失败：已存在此图元类型</br>可能原因：保存按钮，多次点击！");
			}

		} catch (Exception e) {
			logger.error("保存失败", e);
			return RestResultDto.newFalid("保存失败", e.getMessage());
		}
	}

	/**
	 * 保存数据
	 * 
	 * @param request
	 * @param dto
	 * @return
	 */

	@RequestMapping(value = "update" + ManagementConstant.PERMISSION_SUFFIX_EDIT, method = RequestMethod.POST)
	public RestResultDto<Boolean> updateWorkElementType(HttpServletRequest request, WorkElementTypeDto dto) {
		try {
			workElementTypeService.updateWorkElementType(dto);
			return RestResultDto.newSuccess(true, "更新成功");
		} catch (Exception e) {
			logger.error("WorkElementTypeController.updateWorkElementType", e);
			return RestResultDto.newFalid("更新失败", e.getMessage());
		}
	}

	/**
	 * 将数据转移到dto,根据部门id查询部门名称，并将其放入dto
	 * 
	 * @param content
	 * @return
	 */
	private List<WorkElementTypeDto> tranferIntoDto(List<WorkElementType> content) {
		List<WorkElementTypeDto> result = Lists.newArrayList();
		WorkElementTypeDto workElementTypeDto;
		if (CollectionUtils.isNotEmpty(content)) {
			for (WorkElementType workElementType : content) {
				workElementTypeDto = new WorkElementTypeDto();

				// 查询机构名称
				if (StringUtils.isNotEmpty(workElementType.getDepartmentId())) {
					CloudDepartmentDto cloudDepartmentDto = cloudDepartmentService.getById(workElementType.getDepartmentId());
					if (null != cloudDepartmentDto) {
						workElementTypeDto.setDepartmentName(cloudDepartmentDto.getDepName());
					} else {
						CloudOrganizationDto cloudOrganizationDto = cloudOrganizationService.getById(workElementType.getDepartmentId());
						if (null != cloudOrganizationDto) {
							workElementTypeDto.setDepartmentName(cloudOrganizationDto.getOrgName());
						}
					}
				}

				BeanUtils.copyProperties(workElementType, workElementTypeDto);
				result.add(workElementTypeDto);
			}
		}
		return result;
	}

	/**
	 * 获取图元类型列表
	 * 
	 * @return
	 */

	@RequestMapping(value = "loadWorkElementType" + ManagementConstant.PERMISSION_SUFFIX_READ, method = RequestMethod.POST)
	public RestResultDto<List<WorkElementType>> loadWorkElementType(HttpServletRequest request) {
		try {

			String typeCode = SpringmvcUtils.getParameter("typeCode");
			LoginReturnInfoDto loginInfo = super.getLoginInfo(request);
			String tenantId = loginInfo.getTenantId();
			List<SearchFilter> searchFilters = new ArrayList<>();
			searchFilters.add(new SearchFilter("tenantId", Operator.EQ, tenantId));
			if (StringUtils.isNotEmpty(typeCode)) {
				searchFilters.add(new SearchFilter("code", Operator.LIKE, typeCode));
			}
			List<WorkElementType> list = workElementTypeService.findListByFilter(searchFilters, null);

			return RestResultDto.newSuccess(list);
		} catch (Exception e) {
			return RestResultDto.newFalid("回去图元类型列表出错", e.getMessage());
		}

	}

	/**
	 * 获取图元类型列表
	 * 
	 * @return
	 */

	@RequestMapping(value = "loadWorkElementTypeWithPermission" + ManagementConstant.PERMISSION_SUFFIX_READ, method = RequestMethod.POST)
	public RestResultDto<List<WorkElementType>> loadWorkElementTypeWithPermission(HttpServletRequest request) {
		try {
			String typeCode = SpringmvcUtils.getParameter("typeCode");
			LoginReturnInfoDto loginInfo = super.getLoginInfo(request);
			String tenantId = loginInfo.getTenantId();
			String userId = loginInfo.getUserId();
			List<SearchFilter> searchFilters = new ArrayList<>();
			searchFilters.add(new SearchFilter("tenantId", Operator.EQ, tenantId));
			if (StringUtils.isNotEmpty(typeCode)) {
				searchFilters.add(new SearchFilter("code", Operator.LIKE, typeCode));
			}

			SearchFilters andSearchFilter = this.companyPermissionFilter(tenantId, userId);
			List<WorkElementType> list = workElementTypeService.findListByFilters(andSearchFilter.addSearchFilter(searchFilters), null);

			return RestResultDto.newSuccess(list);
		} catch (Exception e) {
			return RestResultDto.newFalid("回去图元类型列表出错", e.getMessage());
		}

	}

	/**
	 * 验证功能code的唯一性
	 *
	 * @param param
	 * @param request
	 * @return 返回是否成功
	 */

	@RequestMapping(value = "checkForm/{param}" + ManagementConstant.PERMISSION_SUFFIX_READ, method = RequestMethod.POST)
	public RestResultDto<Boolean> checkForm(@PathVariable("param") String param, HttpServletRequest request) {
		try {

			String value = SpringmvcUtils.getParameter(param);
			String id = SpringmvcUtils.getParameter("id");

			if (StringUtils.isEmpty(value)) {
				return RestResultDto.newSuccess(false);
			}

			List<SearchFilter> searchFilter = Lists.newArrayList();
			searchFilter.add(new SearchFilter("workElementType." + param, SearchFilter.Operator.EQ, value));
			List<WorkElementType> list = workElementTypeService.findListByFilter(CommonUtils.bindTenantId(searchFilter), null);

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

	/**
	 * 删除1~N条记录
	 * 
	 * @param ids
	 * @return
	 */

	@RequestMapping(value = "deletes" + ManagementConstant.PERMISSION_SUFFIX_EDIT, method = RequestMethod.POST)
	public RestResultDto<Boolean> deletes(HttpServletRequest request, @RequestBody String[] ids) {
		try {

			List<String> deleteList = new ArrayList<>();
			List<String> remainList = new ArrayList<>();
			this.splitForDeletes(ids, deleteList, remainList);

			workElementTypeService.deleteWorkElementTypes(deleteList);
			return RestResultDto.newSuccess(true, "共" + ids.length + "条,删除成功" + deleteList.size() + "条," + "删除失败" + (remainList.size()) + "条");

		} catch (Exception e) {
			logger.error("批量删除失败", e);
			return RestResultDto.newFalid("批量删除失败", e.getMessage());
		}
	}

	/**
	 * 部门树
	 * 
	 * @return
	 */

	@RequestMapping(value = "loadDepartTree" + ManagementConstant.PERMISSION_SUFFIX_READ, method = { RequestMethod.GET, RequestMethod.POST })
	public RestResultDto<String> loadDepartTree(HttpServletRequest request) {
		try {

			LoginReturnInfoDto loginInfo = super.getLoginInfo(request);
			String tenantId = loginInfo.getTenantId();

			OrganizationTree tree = OrganizationTree.getInstance();
			tree.reloadDeptOrgTree(tenantId, "");
			String jsonStr = treeService.generateJsonCheckboxTree(tree, false);

			return RestResultDto.newSuccess(jsonStr);
		} catch (Exception e) {
			logger.error("加载树出错", e);
			return RestResultDto.newFalid("加载树出错", e.getMessage());
		}
	}

	/**
	 * 删除记录
	 * 
	 * @param id
	 * @return
	 */

	@RequestMapping(value = "delete/{id}" + ManagementConstant.PERMISSION_SUFFIX_EDIT, method = RequestMethod.POST)
	public RestResultDto<Boolean> delete(HttpServletRequest request, @PathVariable String id) {

		try {
			workElementTypeService.canBeDelete(id);
			workElementTypeService.deleteWorkElementType(id);

			return RestResultDto.newSuccess(true, "删除成功");

		} catch (Exception e) {
			logger.error("delete()", e);

			return RestResultDto.newFalid("删除失败", e.getMessage());
		}

	}

	/**
	 * 将数据分为可删除和不可删除的部分
	 * 
	 * @param ids
	 * @param deleteList
	 *            可删除列表
	 * @param remainList
	 *            不可删除列表
	 */
	private void splitForDeletes(String[] ids, List<String> deleteList, List<String> remainList) {
		if (ids != null && ids.length > 0) {
			for (int i = 0; i < ids.length; i++) {
				if (workElementTypeService.canBeDelete(ids[i])) {
					deleteList.add(ids[i]);
				} else {
					remainList.add(ids[i]);
				}
			}
		}
	}

	/**
	 * 图元类型自动提示
	 * 
	 * @param type
	 * @param q
	 * @param limit
	 * @param request
	 * @return
	 */

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
			String code = SpringmvcUtils.getParameter("code");
			if (StringUtils.isNotEmpty(code)) {
				filters.add(new SearchFilter("code", Operator.LIKE, code));
			}
			List<WorkElementType> list = workElementTypeService.findListByFilter(filters, null);
			List<Object> listJsonValue = Lists.newArrayList();
			Map<String, Object> mapValue = null;
			if (CollectionUtils.isNotEmpty(list)) {
				for (WorkElementType entity : list) {
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
			logger.error("查询自动提示信息出错", e);
			return RestResultDto.newFalid("查询自动提示信息出错", e.getMessage());
		}
	}

	/**
	 * 图元类型自动提示
	 * 
	 * @param type
	 * @param q
	 * @param limit
	 * @param request
	 * @return
	 */

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
			String code = SpringmvcUtils.getParameter("code");
			if (StringUtils.isNotEmpty(code)) {
				filters.add(new SearchFilter("code", Operator.LIKE, code));
			}
			SearchFilters andSearchFilters = companyPermissionFilter(tenantId, userId);

			List<WorkElementType> list = workElementTypeService.findListByFilters(andSearchFilters.addSearchFilter(filters), null);
			List<Object> listJsonValue = Lists.newArrayList();
			Map<String, Object> mapValue = null;
			if (CollectionUtils.isNotEmpty(list)) {
				for (WorkElementType entity : list) {
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
			logger.error("查询自动提示信息出错", e);
			return RestResultDto.newFalid("查询自动提示信息出错", e.getMessage());
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
