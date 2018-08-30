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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.vortex.cloud.ums.annotation.FunctionCode;
import com.vortex.cloud.ums.dataaccess.service.IParamGroupService;
import com.vortex.cloud.ums.dataaccess.service.IParamTypeService;
import com.vortex.cloud.ums.enums.ResponseType;
import com.vortex.cloud.ums.model.PramGroup;
import com.vortex.cloud.ums.model.PramType;
import com.vortex.cloud.ums.tree.ParamGroupTree;
import com.vortex.cloud.ums.util.web.BaseController;
import com.vortex.cloud.ums.util.support.ForeContext;
import com.vortex.cloud.ums.util.tree.ITreeService;
import com.vortex.cloud.vfs.common.lang.StringUtil;
import com.vortex.cloud.vfs.common.web.springmvc.SpringmvcUtils;
import com.vortex.cloud.vfs.data.dto.DataStore;
import com.vortex.cloud.vfs.data.dto.RestResultDto;
import com.vortex.cloud.vfs.data.support.SearchFilter;
import com.vortex.cloud.vfs.data.support.SearchFilter.Operator;








@SuppressWarnings("all")
  @RestController      
@RequestMapping("cloud/management/paramGroup")
public class ParamGroupController extends BaseController {
	private Logger logger = LoggerFactory.getLogger(ParamGroupController.class);

	private static final String FORE_DYNAMIC_SUFFIX = com.vortex.cloud.ums.support.ManagementConstant.PERMISSION_SUFFIX_SA;
	private static final String BACK_DYNAMIC_SUFFIX = com.vortex.cloud.ums.support.ManagementConstant.PERMISSION_SUFFIX_SA;

	@Resource
	private IParamGroupService paramGroupService;

	@Resource
	private IParamTypeService paramTypeService;

	@Resource
	private ITreeService treeService;

	@RequestMapping(value = "pageList" + FORE_DYNAMIC_SUFFIX, method = RequestMethod.POST)
	public RestResultDto<DataStore<PramGroup>> pageList(HttpServletRequest request, HttpServletResponse response) {
		try {
			String parentId = SpringmvcUtils.getParameter("parentId");
			String name = SpringmvcUtils.getParameter("name");
			String code = SpringmvcUtils.getParameter("code");
			if (StringUtil.isNullOrEmpty(parentId)) {
				return RestResultDto.newSuccess(new DataStore<PramGroup>());
			}
			List<SearchFilter> filterList = new ArrayList<SearchFilter>();
			filterList.add(new SearchFilter("parentId", Operator.EQ, parentId));
			if (org.apache.commons.lang3.StringUtils.isNotBlank(code)) {
				filterList.add(new SearchFilter("groupCode", Operator.LIKE, code));
			}
			if (org.apache.commons.lang3.StringUtils.isNotBlank(name)) {
				filterList.add(new SearchFilter("groupName", Operator.LIKE, name));
			}
			Sort sort = new Sort(Direction.ASC, "orderIndex");
			Pageable pageable = ForeContext.getPageable(request, sort);
			Page<PramGroup> page = paramGroupService.findPageByFilter(pageable, filterList);
			DataStore<PramGroup> dataStore = null;
			if (null != page) {
				List<PramGroup> result = page.getContent();
				dataStore = new DataStore<PramGroup>(page.getTotalElements(), result);
			} else {
				dataStore = new DataStore<PramGroup>();
			}
			return RestResultDto.newSuccess(dataStore);
		} catch (Exception e) {
			return RestResultDto.newFalid("加载分页列表出错", e.getMessage());
		}
	}

	@RequestMapping(value = "loadTree" + FORE_DYNAMIC_SUFFIX, method = RequestMethod.POST)
	public RestResultDto<String> loadTree(HttpServletRequest request, HttpServletResponse response) {
		try {

			ParamGroupTree parameterTypeTree = ParamGroupTree.getInstance();
			parameterTypeTree.reloadParameterTypeTree(null);
			String jsonStr = treeService.generateJsonCheckboxTree(parameterTypeTree, false);

			return RestResultDto.newSuccess(jsonStr);
		} catch (Exception e) {
			return RestResultDto.newFalid("加载参数组树失败", e.getMessage());
		}
	}

	@RequestMapping(value = "save" + BACK_DYNAMIC_SUFFIX, method = RequestMethod.POST)
	@FunctionCode(value = "CF_MANAGE_PARAM_G_ADD", type = ResponseType.Json)
	public RestResultDto<Boolean> save(HttpServletRequest request, PramGroup newParameterTypeGroup,
			RedirectAttributes redirectAttributes) {
		try {
			paramGroupService.save(newParameterTypeGroup);
			return RestResultDto.newSuccess(true, "保存成功");
		} catch (Exception e) {
			logger.error("ParamGroupController.save", e);
			return RestResultDto.newFalid("保存失败", e.getMessage());
		}

	}

	@RequestMapping(value = "load" + FORE_DYNAMIC_SUFFIX, method = RequestMethod.POST)
	public RestResultDto<PramGroup> load(HttpServletRequest request, HttpServletResponse response) {
		try {
			String id = SpringmvcUtils.getParameter("id");
			return RestResultDto.newSuccess(paramGroupService.findOne(id));
		} catch (Exception e) {
			logger.error("ParamGroupController.save", e);
			return RestResultDto.newFalid("查询失败", e.getMessage());
		}

	}

	@RequestMapping(value = "delete" + BACK_DYNAMIC_SUFFIX, method = RequestMethod.POST)
	@FunctionCode(value = "CF_MANAGE_PARAM_G_DEL", type = ResponseType.Json)
	public RestResultDto<Boolean> delete(HttpServletRequest request) {
		try {

			String id = SpringmvcUtils.getParameter("id");
			List<SearchFilter> filterList = new ArrayList<SearchFilter>();
			filterList.add(new SearchFilter("parentId", Operator.EQ, id));
			List<PramGroup> list = paramGroupService.findListByFilter(filterList, null);
			if (CollectionUtils.isNotEmpty(list)) {
				return RestResultDto.newFalid("删除失败,该节点不是叶子节点");
			} else {
				filterList = new ArrayList<SearchFilter>();
				filterList.add(new SearchFilter("parameterType.groupId", Operator.EQ, id));

				List<PramType> parameterTypelist = paramTypeService.findListByFilter(filterList, null);
				if (CollectionUtils.isNotEmpty(parameterTypelist)) {
					return RestResultDto.newFalid("删除失败,该节点下存在参数类型");
				} else {
					paramGroupService.delete(id);
					return RestResultDto.newSuccess(true, "删除成功");
				}
			}

		} catch (Exception e) {
			logger.error("ParamGroupController.delete", e);
			return RestResultDto.newFalid("删除失败", e.getMessage());
		}
	}

	@RequestMapping(value = "checkForm/{param}" + FORE_DYNAMIC_SUFFIX, method = RequestMethod.POST)
	public RestResultDto<Boolean> checkForm(@PathVariable("param") String paramName) {
		String paramVal = SpringmvcUtils.getParameter(paramName);
		String id = SpringmvcUtils.getParameter("id");
		if (StringUtil.isNullOrEmpty(paramVal)) {
			return RestResultDto.newSuccess(false);
		}

		if ("groupCode".equals(paramName)) {

			List<SearchFilter> filterList = new ArrayList<SearchFilter>();
			SearchFilter filter = new SearchFilter("groupCode", SearchFilter.Operator.EQ, paramVal);
			if (StringUtils.isNotBlank(id)) {
				filterList.add(new SearchFilter("id", SearchFilter.Operator.NE, id));
			}
			filterList.add(filter);

			List<PramGroup> list = paramGroupService.findListByFilter(filterList, null);
			return RestResultDto.newSuccess(CollectionUtils.isEmpty(list));
		}
		return RestResultDto.newSuccess(false);
	}

}
