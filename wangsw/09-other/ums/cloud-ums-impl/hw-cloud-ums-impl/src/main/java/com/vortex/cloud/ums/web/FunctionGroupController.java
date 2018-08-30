package com.vortex.cloud.ums.web;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
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
import com.vortex.cloud.ums.dataaccess.service.ICloudFunctionGroupService;
import com.vortex.cloud.ums.dataaccess.service.ICloudFunctionService;
import com.vortex.cloud.ums.dto.CloudFunctionGroupDto;
import com.vortex.cloud.ums.enums.ResponseType;
import com.vortex.cloud.ums.model.CloudFunction;
import com.vortex.cloud.ums.model.CloudFunctionGroup;
import com.vortex.cloud.ums.tree.FunctionGroupTree;
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
@RequestMapping("cloud/management/functiongroup")
public class FunctionGroupController extends BaseController {
	private static final Logger logger = LoggerFactory.getLogger(FunctionGroupController.class);
	private static final String FORE_DYNAMIC_SUFFIX = com.vortex.cloud.ums.support.ManagementConstant.PERMISSION_SUFFIX_SA;
	private static final String BACK_DYNAMIC_SUFFIX = com.vortex.cloud.ums.support.ManagementConstant.PERMISSION_SUFFIX_SA;

	@Resource
	private ITreeService treeService;

	@Resource
	private ICloudFunctionGroupService cloudFunctionGroupService;

	@Resource
	private ICloudFunctionService cloudFunctionService;

	/**
	 * 根据ID获取数据
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "loadById" + FORE_DYNAMIC_SUFFIX, method = RequestMethod.POST)
	public RestResultDto<CloudFunctionGroupDto> loadById(String id) {
		try {
			CloudFunctionGroupDto cloudFunctionGroup = cloudFunctionGroupService.findFunctionGroupAndGroupNameById(id);
			return RestResultDto.newSuccess(cloudFunctionGroup);
		} catch (Exception e) {
			return RestResultDto.newFalid("根据id获取功能组出错", e.getMessage());
		}

	}

	@RequestMapping(value = "loadTree" + FORE_DYNAMIC_SUFFIX, method = RequestMethod.POST)
	public RestResultDto<String> loadTree(HttpServletRequest request, HttpServletResponse response) {
		try {
			String systemId = SpringmvcUtils.getParameter("systemId");
			FunctionGroupTree functionGroupTree = FunctionGroupTree.getInstance();
			Map<String, Object> paramMap = Maps.newHashMap();
			paramMap.put("systemId", systemId);
			functionGroupTree.reloadFunctionGroupTree(paramMap);
			String json = treeService.generateJsonCheckboxTree(functionGroupTree, false);

			return RestResultDto.newSuccess(json);
		} catch (Exception e) {
			return RestResultDto.newFalid("加载功能组树出错", e.getMessage());
		}
	}

	@RequestMapping(value = "pageList" + FORE_DYNAMIC_SUFFIX, method = RequestMethod.POST)
	// @FunctionCode(value = "CF_MANAGE_FG_LIST", type = ResponseType.Json)
	public RestResultDto<DataStore<CloudFunctionGroup>> pageList(HttpServletRequest request, HttpServletResponse response) {
		try {
			String parentId = SpringmvcUtils.getParameter("parentId");
			String systemId = SpringmvcUtils.getParameter("systemId");
			if (StringUtil.isNullOrEmpty(parentId)) {
				return RestResultDto.newSuccess(new DataStore<CloudFunctionGroup>());
			}
			if (StringUtil.isNullOrEmpty(systemId)) {
				throw new VortexException("系统id不能为空");
			}
			List<SearchFilter> filterList = new ArrayList<SearchFilter>();
			filterList.add(new SearchFilter("parentId", Operator.EQ, parentId));
			filterList.add(new SearchFilter("systemId", Operator.EQ, systemId));
			Sort sort = new Sort(Direction.ASC, "orderIndex");
			Pageable pageable = ForeContext.getPageable(request, sort);
			Page<CloudFunctionGroup> page = cloudFunctionGroupService.findPageByFilter(pageable, filterList);
			DataStore<CloudFunctionGroup> dataStore = null;
			if (null != page) {
				List<CloudFunctionGroup> result = page.getContent();
				dataStore = new DataStore<CloudFunctionGroup>(page.getTotalElements(), result);
			} else {
				dataStore = new DataStore<CloudFunctionGroup>();
			}
			return RestResultDto.newSuccess(dataStore);
		} catch (Exception e) {
			return RestResultDto.newFalid("加载分页列表出错", e.getMessage());
		}
	}

	/**
	 * 保存功能组信息
	 * 
	 * @param dto
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "save" + BACK_DYNAMIC_SUFFIX, method = RequestMethod.POST)
	@FunctionCode(value = "CF_MANAGE_FG_ADD", type = ResponseType.Json)
	public RestResultDto<Boolean> saveFunctionGroupInfo(HttpServletRequest request, CloudFunctionGroupDto dto) {

		try {
			cloudFunctionGroupService.save(dto);
			return RestResultDto.newSuccess(true, "保存成功");
		} catch (Exception e) {
			logger.error("CloudFunctionGroupController.save", e);
			return RestResultDto.newFalid("保存出错", e.getMessage());
		}
	}

	/**
	 * 更新功能组信息
	 * 
	 * @param dto
	 * @return
	 */
	@RequestMapping(value = "update" + BACK_DYNAMIC_SUFFIX, method = RequestMethod.POST)
	@FunctionCode(value = "CF_MANAGE_FG_UPDATE", type = ResponseType.Json)
	public RestResultDto<Boolean> updateFunctionGroupInfo(HttpServletRequest request, CloudFunctionGroupDto dto) {
		try {
			cloudFunctionGroupService.update(dto);
			return RestResultDto.newSuccess(true, "更新成功");
		} catch (Exception e) {
			logger.error("CloudFunctionGroupController.update", e);
			return RestResultDto.newFalid("更新出错", e.getMessage());
		}
	}

	@RequestMapping(value = "delete/{id}" + BACK_DYNAMIC_SUFFIX, method = RequestMethod.POST)
	@FunctionCode(value = "CF_MANAGE_FG_DEL", type = ResponseType.Json)
	public RestResultDto<Boolean> delete(HttpServletRequest request, @PathVariable("id") String id) {

		try {
			this.checkForDelete(id);
			cloudFunctionGroupService.deleteFunctionGroup(id);
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
			cloudFunctionGroupService.deletes(canBeDeletes);
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

		List<CloudFunctionGroup> sonList = cloudFunctionGroupService.findListByFilter(sfList, null);

		if (CollectionUtils.isNotEmpty(sonList)) {
			return false;
		} else {
			sfList = new ArrayList<SearchFilter>();
			sfList.add(new SearchFilter("groupId", Operator.EQ, id));

			List<CloudFunction> roleList = cloudFunctionService.findListByFilter(sfList, null);

			if (CollectionUtils.isNotEmpty(roleList)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 功能组删除校验，删除条件为： 当且仅当组为空组，即没有子组且组下没有直属功能
	 * 
	 * @param oi
	 * @param id
	 */
	private void checkForDelete(String id) throws Exception {
		List<SearchFilter> sfList = new ArrayList<SearchFilter>();
		sfList.add(new SearchFilter("parentId", Operator.EQ, id));

		List<CloudFunctionGroup> sonList = cloudFunctionGroupService.findListByFilter(sfList, null);

		if (CollectionUtils.isNotEmpty(sonList)) {
			throw new VortexException("失败：记录下存在功能组");
		} else {
			sfList = new ArrayList<SearchFilter>();
			sfList.add(new SearchFilter("groupId", Operator.EQ, id));

			List<CloudFunction> funList = cloudFunctionService.findListByFilter(sfList, null);

			if (CollectionUtils.isNotEmpty(funList)) {
				throw new VortexException("失败：记录下存在功能");
			}
		}
	}

	@RequestMapping(value = "checkForm/{param}" + FORE_DYNAMIC_SUFFIX, method = RequestMethod.POST)
	public RestResultDto<Boolean> checkForm(@PathVariable("param") String param, HttpServletRequest request) {
		try {
			String systemId = SpringmvcUtils.getParameter("systemId");

			String value = SpringmvcUtils.getParameter(param);
			String id = SpringmvcUtils.getParameter("id");

			List<SearchFilter> searchFilters = new ArrayList<SearchFilter>();
			searchFilters.add(new SearchFilter("systemId", Operator.EQ, systemId));

			if ("name".equals(param)) {
				if (StringUtil.isNullOrEmpty(value)) {
					return RestResultDto.newSuccess(false);
				}
				searchFilters.add(new SearchFilter("name", Operator.EQ, value));

				List<CloudFunctionGroup> list = cloudFunctionGroupService.findListByFilter(searchFilters, null);

				if (CollectionUtils.isEmpty(list)) {
					return RestResultDto.newSuccess(true);
				}
				if (!StringUtil.isNullOrEmpty(id) && list.size() == 1 && list.get(0).getId().equals(id)) {
					return RestResultDto.newSuccess(true);
				}
			} else if ("code".equals(param)) {
				if (StringUtil.isNullOrEmpty(value)) {
					return RestResultDto.newSuccess(false);
				}
				searchFilters.add(new SearchFilter("code", Operator.EQ, value));
				List<CloudFunctionGroup> list = cloudFunctionGroupService.findListByFilter(searchFilters, null);
				if (CollectionUtils.isEmpty(list)) {
					return RestResultDto.newSuccess(true);
				}
				if (!StringUtil.isNullOrEmpty(id) && list.size() == 1 && list.get(0).getId().equals(id)) {
					return RestResultDto.newSuccess(true);
				}
			}
			return RestResultDto.newSuccess(false);
		} catch (Exception e) {
			logger.error("校验出错", e.getMessage());
			return RestResultDto.newFalid("校验出错", e.getMessage());
		}
	}

}
