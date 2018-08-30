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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.vortex.cloud.ums.annotation.FunctionCode;
import com.vortex.cloud.ums.dataaccess.service.ICloudRoleGroupService;
import com.vortex.cloud.ums.dataaccess.service.ICloudRoleService;
import com.vortex.cloud.ums.dto.CloudRoleGroupDto;
import com.vortex.cloud.ums.enums.ResponseType;
import com.vortex.cloud.ums.model.CloudRole;
import com.vortex.cloud.ums.model.CloudRoleGroup;
import com.vortex.cloud.ums.tree.RoleGroupTree;
import com.vortex.cloud.ums.util.support.ForeContext;
import com.vortex.cloud.ums.util.tree.ITreeService;
import com.vortex.cloud.ums.util.web.BaseController;
import com.vortex.cloud.vfs.common.exception.VortexException;
import com.vortex.cloud.vfs.common.lang.StringUtil;
import com.vortex.cloud.vfs.common.web.springmvc.SpringmvcUtils;
import com.vortex.cloud.vfs.data.dto.DataStore;
import com.vortex.cloud.vfs.data.dto.RestResultDto;
import com.vortex.cloud.vfs.data.support.SearchFilter;
import com.vortex.cloud.vfs.data.support.SearchFilter.Operator;

@RestController
@RequestMapping("cloud/management/rolegroup")
public class RoleGroupController extends BaseController {
	private static final Logger logger = LoggerFactory.getLogger(RoleGroupController.class);
	private static final String FORE_DYNAMIC_SUFFIX = com.vortex.cloud.ums.support.ManagementConstant.PERMISSION_SUFFIX_SA;
	private static final String BACK_DYNAMIC_SUFFIX = com.vortex.cloud.ums.support.ManagementConstant.PERMISSION_SUFFIX_SA;

	/*
	 * private static final String CLOUD_ROLE_GROUP_LIST_URL =
	 * "cloud/management/rolegroup/rolegroupList"; private static final String
	 * CLOUD_ROLE_GROUP_ADD_URL = "cloud/management/rolegroup/rolegroupAddForm";
	 * private static final String CLOUD_ROLE_GROUP_UPDATE_URL =
	 * "cloud/management/rolegroup/rolegroupUpdateForm"; private static final String
	 * CLOUD_ROLE_GROUP_VIEW_URL = "cloud/management/rolegroup/rolegroupViewForm";
	 */
	@Resource
	private ITreeService treeService;

	@Resource
	private ICloudRoleGroupService cloudRoleGroupService;

	@Resource
	private ICloudRoleService cloudRoleService;

	/**
	 * 列表页面
	 * 
	 * @param model
	 * @param request
	 * @return
	 */
	/*
	 * @RequestMapping(value = "gotoQuery" + FORE_DYNAMIC_SUFFIX, method =
	 * RequestMethod.GET)
	 * 
	 * @FunctionCode(value = "CF_MANAGE_RG_LIST", type = ResponseType.Page) public
	 * String gotoQuery(HttpServletRequest request, Model model) { String systemId =
	 * super.getSystemId(request); model.addAttribute("systemId", systemId);
	 * 
	 * return CLOUD_ROLE_GROUP_LIST_URL; }
	 * 
	 *//**
		 * 增加页面
		 * 
		 * @param model
		 * @param request
		 * @return
		 */
	/*
	 * @RequestMapping(value = "gotoAdd" + FORE_DYNAMIC_SUFFIX, method =
	 * RequestMethod.GET)
	 * 
	 * @FunctionCode(value = "CF_MANAGE_RG_ADD", type = ResponseType.Page) public
	 * String gotoAdd(HttpServletRequest request, Model model) { String parentId =
	 * SpringmvcUtils.getParameter("parentId"); String systemId =
	 * SpringmvcUtils.getParameter("systemId"); model.addAttribute("parentId",
	 * parentId); model.addAttribute("systemId", systemId); return
	 * CLOUD_ROLE_GROUP_ADD_URL; }
	 * 
	 *//**
		 * 查看页面
		 * 
		 * @param model
		 * @return
		 */

	/*
	 * @RequestMapping(value = "gotoView" + FORE_DYNAMIC_SUFFIX, method =
	 * RequestMethod.GET)
	 * 
	 * @FunctionCode(value = "CF_MANAGE_RG_VIEW", type = ResponseType.Page) public
	 * String gotoView(HttpServletRequest request, Model model) { String id =
	 * SpringmvcUtils.getParameter("id"); model.addAttribute("id", id);
	 * 
	 * return CLOUD_ROLE_GROUP_VIEW_URL; }
	 * 
	 *//**
		 * 更新页面
		 * 
		 * @param model
		 * @return
		 *//*
			 * @RequestMapping(value = "gotoUpdate" + FORE_DYNAMIC_SUFFIX, method =
			 * RequestMethod.GET)
			 * 
			 * @FunctionCode(value = "CF_MANAGE_RG_UPDATE", type = ResponseType.Page) public
			 * String gotoUpdate(HttpServletRequest request, Model model) { String id =
			 * SpringmvcUtils.getParameter("id"); model.addAttribute("id", id);
			 * 
			 * return CLOUD_ROLE_GROUP_UPDATE_URL; }
			 */
	@RequestMapping(value = "loadTree" + FORE_DYNAMIC_SUFFIX, method = RequestMethod.POST)
	public RestResultDto<String> loadTree(HttpServletRequest request, HttpServletResponse response) {
		try {
			String systemId = SpringmvcUtils.getParameter("systemId");
			RoleGroupTree roleGroupTree = RoleGroupTree.getInstance();
			Map<String, Object> paramMap = Maps.newHashMap();
			paramMap.put("systemId", systemId);
			roleGroupTree.reloadRoleGroupTree(paramMap);
			String jsonStr = treeService.generateJsonCheckboxTree(roleGroupTree, false);
			return RestResultDto.newSuccess(jsonStr, "加载角色组树成功");
		} catch (Exception e) {
			logger.error("加载角色组树失败", e);
			return RestResultDto.newFalid("加载角色组树失败", e.getMessage());
		}
	}

	@RequestMapping(value = "pageList" + FORE_DYNAMIC_SUFFIX, method = RequestMethod.POST)
	// @FunctionCode(value = "CF_MANAGE_RG_LIST", type = ResponseType.Json)
	public RestResultDto<DataStore<CloudRoleGroup>> pageList(HttpServletRequest request, HttpServletResponse response) {
		try {
			DataStore<CloudRoleGroup> dataStore = null;
			String parentId = SpringmvcUtils.getParameter("parentId");
			String systemId = SpringmvcUtils.getParameter("systemId");

			if (StringUtil.isNullOrEmpty(parentId)) {
				dataStore = new DataStore<CloudRoleGroup>();
			}
			if (StringUtils.isEmpty(systemId)) {
				throw new VortexException("businessSystemId不能为空");
			}

			List<SearchFilter> filterList = new ArrayList<SearchFilter>();
			filterList.add(new SearchFilter("parentId", Operator.EQ, parentId));
			filterList.add(new SearchFilter("systemId", Operator.EQ, systemId));
			Sort sort = new Sort(Direction.ASC, "orderIndex");
			Pageable pageable = ForeContext.getPageable(request, sort);
			Page<CloudRoleGroup> page = cloudRoleGroupService.findPageByFilter(pageable, filterList);
			if (null != page) {
				List<CloudRoleGroup> result = page.getContent();
				dataStore = new DataStore<CloudRoleGroup>(page.getTotalElements(), result);
			} else {
				dataStore = new DataStore<CloudRoleGroup>();
			}
			return RestResultDto.newSuccess(dataStore, "获取分页列表成功");
		} catch (Exception e) {
			logger.error("获取分页列表失败", e);
			return RestResultDto.newFalid("获取分页列表失败", e.getMessage());
		}

	}

	/**
	 * 保存角色组信息
	 * 
	 * @param dto
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "save" + BACK_DYNAMIC_SUFFIX, method = RequestMethod.POST)
	@FunctionCode(value = "CF_MANAGE_RG_ADD", type = ResponseType.Json)
	public RestResultDto<Boolean> saveRoleGroupInfo(HttpServletRequest request, CloudRoleGroupDto dto) {

		try {
			cloudRoleGroupService.saveRoleGroup(dto);
			return RestResultDto.newSuccess(true, "保存成功");
		} catch (Exception e) {
			logger.error("CloudRoleGroupController.save", e);
			return RestResultDto.newFalid("保存失败", e.getMessage());
		}
	}

	/**
	 * 根据ID获取数据
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "loadById" + FORE_DYNAMIC_SUFFIX, method = RequestMethod.POST)
	public RestResultDto<CloudRoleGroupDto> loadById(String id) {
		try {
			CloudRoleGroupDto cloudRoleGroupDto = cloudRoleGroupService.findRoleGroupAndGroupNameById(id);
			return RestResultDto.newSuccess(cloudRoleGroupDto, "根据ID获取数据成功");
		} catch (Exception e) {
			logger.error("CloudRoleGroupController.loadById", e);
			return RestResultDto.newFalid("根据ID获取数据失败", e.getMessage());
		}
	}

	/**
	 * 更新角色组信息
	 * 
	 * @param dto
	 * @return
	 */
	@RequestMapping(value = "update" + BACK_DYNAMIC_SUFFIX, method = RequestMethod.POST)
	@FunctionCode(value = "CF_MANAGE_RG_UPDATE", type = ResponseType.Json)
	public RestResultDto<Boolean> updateRoleGroupInfo(HttpServletRequest request, CloudRoleGroupDto dto) {

		try {
			cloudRoleGroupService.updateRoleGroup(dto);
			return RestResultDto.newSuccess(true, "更新成功");
		} catch (Exception e) {
			logger.error("CloudRoleGroupController.update", e);
			return RestResultDto.newFalid("更新失败", e.getMessage());
		}
	}

	/**
	 * 删除记录
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "delete/{id}" + BACK_DYNAMIC_SUFFIX, method = RequestMethod.POST)
	@FunctionCode(value = "CF_MANAGE_RG_DEL", type = ResponseType.Json)
	public RestResultDto<Boolean> delete(HttpServletRequest request, @PathVariable("id") String id) {

		try {

			this.checkForDelete(id);
			cloudRoleGroupService.deleteRoleGroup(id);
			return RestResultDto.newSuccess(true, "删除成功");
		} catch (Exception e) {

			logger.error("删除失败" + e.getMessage());
			return RestResultDto.newFalid("删除失败", e.getMessage());
		}
	}

	/**
	 * 删除记录
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "deletes" + BACK_DYNAMIC_SUFFIX, method = RequestMethod.POST)
	@FunctionCode(value = "CF_MANAGE_RG_DEL", type = ResponseType.Json)
	public RestResultDto<Boolean> deletes(HttpServletRequest request, @RequestBody List<String> ids) {
		List<String> canBeDeletes = Lists.newArrayList();
		try {
			if (CollectionUtils.isEmpty(ids)) {
				return RestResultDto.newSuccess(true, "共" + ids.size() + "条,删除成功" + canBeDeletes.size() + "条," + "删除失败" + (ids.size() - canBeDeletes.size()) + "条");
			}

			for (String id : ids) {
				if (this.canBeDelete(id)) {
					canBeDeletes.add(id);
				}
			}
			cloudRoleGroupService.deletes(canBeDeletes);
			return RestResultDto.newSuccess(true, "共" + ids.size() + "条,删除成功" + canBeDeletes.size() + "条," + "删除失败" + (ids.size() - canBeDeletes.size()) + "条");
		} catch (Exception e) {

			logger.error("删除失败" + e.getMessage());
			return RestResultDto.newFalid("删除失败", e.getMessage());
		}
	}

	/**
	 * 判断能否被删除
	 * 
	 * @param id
	 * @return
	 */
	private boolean canBeDelete(String id) {
		List<SearchFilter> sfList = new ArrayList<SearchFilter>();
		sfList.add(new SearchFilter("parentId", Operator.EQ, id));

		List<CloudRoleGroup> sonList = cloudRoleGroupService.findListByFilter(sfList, null);

		if (CollectionUtils.isNotEmpty(sonList)) {
			return false;
		} else {
			sfList = new ArrayList<SearchFilter>();
			sfList.add(new SearchFilter("groupId", Operator.EQ, id));

			List<CloudRole> roleList = cloudRoleService.findListByFilter(sfList, null);

			if (CollectionUtils.isNotEmpty(roleList)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 角色组删除校验，删除条件为： 当且仅当组为空组，即没有子组且组下没有直属角色
	 * 
	 * @param oi
	 * @param id
	 */
	private void checkForDelete(String id) throws Exception {
		List<SearchFilter> sfList = new ArrayList<SearchFilter>();
		sfList.add(new SearchFilter("parentId", Operator.EQ, id));

		List<CloudRoleGroup> sonList = cloudRoleGroupService.findListByFilter(sfList, null);

		if (CollectionUtils.isNotEmpty(sonList)) {
			throw new VortexException("失败：记录下存在角色组");
		} else {
			sfList = new ArrayList<SearchFilter>();
			sfList.add(new SearchFilter("groupId", Operator.EQ, id));

			List<CloudRole> roleList = cloudRoleService.findListByFilter(sfList, null);

			if (CollectionUtils.isNotEmpty(roleList)) {
				throw new VortexException("失败：记录下存在角色");
			}
		}
	}

	@RequestMapping(value = "checkForm/{param}" + FORE_DYNAMIC_SUFFIX, method = RequestMethod.POST)
	public RestResultDto<Boolean> checkForm(@PathVariable("param") String param, HttpServletRequest request) {
		try {
			boolean flag = false;
			String value = SpringmvcUtils.getParameter(param);
			String id = SpringmvcUtils.getParameter("id");
			String systemId = SpringmvcUtils.getParameter("systemId");
			List<SearchFilter> searchFilters = new ArrayList<SearchFilter>();
			searchFilters.add(new SearchFilter("systemId", Operator.EQ, systemId));

			if ("name".equals(param)) {
				if (StringUtil.isNullOrEmpty(value)) {
					flag = false;
				}
				searchFilters.add(new SearchFilter("name", Operator.EQ, value));

				List<CloudRoleGroup> list = cloudRoleGroupService.findListByFilter(searchFilters, null);

				if (CollectionUtils.isEmpty(list)) {
					flag = true;
				}
				if (!StringUtil.isNullOrEmpty(id) && list.size() == 1 && list.get(0).getId().equals(id)) {
					flag = true;
				}
			} else if ("code".equals(param)) {
				if (StringUtil.isNullOrEmpty(value)) {
					flag = false;
				}
				searchFilters.add(new SearchFilter("code", Operator.EQ, value));
				List<CloudRoleGroup> list = cloudRoleGroupService.findListByFilter(searchFilters, null);
				if (CollectionUtils.isEmpty(list)) {
					flag = true;
				}
				if (!StringUtil.isNullOrEmpty(id) && list.size() == 1 && list.get(0).getId().equals(id)) {
					flag = true;
				}
			}
			return RestResultDto.newSuccess(flag);
		} catch (Exception e) {
			logger.error("校验参数出错", e);
			return RestResultDto.newFalid("校验参数出错", e.getMessage());
		}
	}

}
