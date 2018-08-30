package com.vortex.cloud.ums.web;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
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
import org.springframework.web.bind.annotation.RestController;

import com.google.common.collect.Maps;
import com.vortex.cloud.ums.annotation.FunctionCode;
import com.vortex.cloud.ums.dataaccess.service.ICloudFunctionRoleService;
import com.vortex.cloud.ums.dataaccess.service.ICloudFunctionService;
import com.vortex.cloud.ums.dataaccess.service.ICloudSystemService;
import com.vortex.cloud.ums.dto.CloudFunctionDto;
import com.vortex.cloud.ums.enums.ResponseType;
import com.vortex.cloud.ums.model.CloudFunction;
import com.vortex.cloud.ums.model.CloudFunctionRole;
import com.vortex.cloud.ums.model.CloudSystem;
import com.vortex.cloud.ums.tree.FunctionGroupTree;
import com.vortex.cloud.ums.util.support.ForeContext;
import com.vortex.cloud.ums.util.tree.ITreeService;
import com.vortex.cloud.ums.util.web.BaseController;
import com.vortex.cloud.vfs.common.lang.StringUtil;
import com.vortex.cloud.vfs.common.web.springmvc.SpringmvcUtils;
import com.vortex.cloud.vfs.data.dto.DataStore;
import com.vortex.cloud.vfs.data.dto.RestResultDto;
import com.vortex.cloud.vfs.data.support.SearchFilter;
import com.vortex.cloud.vfs.data.support.SearchFilter.Operator;

/**
 * @author LiShijun
 * @date 2016年6月7日 上午10:05:25
 * @Description 功能维护 History <author> <time> <desc>
 */
@RestController
@RequestMapping("cloud/management/function")
public class FunctionController extends BaseController {
	private static final Logger logger = LoggerFactory.getLogger(FunctionController.class);
	private static final String FORE_DYNAMIC_SUFFIX = com.vortex.cloud.ums.support.ManagementConstant.PERMISSION_SUFFIX_SA;
	private static final String BACK_DYNAMIC_SUFFIX = com.vortex.cloud.ums.support.ManagementConstant.PERMISSION_SUFFIX_SA;

	@Resource
	private ITreeService treeService;

	@Resource
	private ICloudFunctionService cloudFunctionService;

	@Resource
	private ICloudFunctionRoleService cloudFunctionRoleService;

	@Resource
	private ICloudSystemService cloudSystemService;

	/**
	 * 根据ID获取数据
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "loadById" + FORE_DYNAMIC_SUFFIX, method = RequestMethod.POST)
	public RestResultDto<CloudFunctionDto> loadById(String id) {
		try {
			CloudFunctionDto dto = cloudFunctionService.getFunctionInfoById(id);
			return RestResultDto.newSuccess(dto);
		} catch (Exception e) {
			logger.error("根据id加载功能出错", e);
			return RestResultDto.newFalid("根据id加载功能出错", e.getMessage());
		}
	}

	/**
	 * 根据当前节点id，返回下面的功能列表
	 * 
	 * @param request
	 * @param response
	 * @return
	 * 
	 */
	@RequestMapping(value = "pageList" + FORE_DYNAMIC_SUFFIX, method = RequestMethod.POST)
	// @FunctionCode(value = "CF_MANAGE_FUN_LIST", type = ResponseType.Json)
	public RestResultDto<DataStore<CloudFunction>> pageList(HttpServletRequest request, HttpServletResponse response) {
		try {
			String systemId = SpringmvcUtils.getParameter("systemId");

			String parentId = SpringmvcUtils.getParameter("functionGroupId");

			String name = SpringmvcUtils.getParameter("name");
			if (StringUtil.isNullOrEmpty(parentId)) {
				return RestResultDto.newSuccess(new DataStore<CloudFunction>());
			}

			List<SearchFilter> filterList = new ArrayList<SearchFilter>();

			filterList.add(new SearchFilter("systemId", Operator.EQ, systemId));

			filterList.add(new SearchFilter("groupId", Operator.EQ, parentId));
			if (StringUtils.isNotEmpty(name)) {
				filterList.add(new SearchFilter("name", Operator.LIKE, name));
			}

			Sort sort = new Sort(Direction.ASC, "orderIndex");
			Pageable pageable = ForeContext.getPageable(request, sort);
			Page<CloudFunction> page = cloudFunctionService.findPageByFilter(pageable, filterList);
			DataStore<CloudFunction> dataStore = null;
			if (null != page) {
				List<CloudFunction> result = page.getContent();
				dataStore = new DataStore<CloudFunction>(page.getTotalElements(), result);
			} else {
				dataStore = new DataStore<CloudFunction>();
			}
			return RestResultDto.newSuccess(dataStore);
		} catch (Exception e) {
			logger.error("加载列表分页出错", e);
			return RestResultDto.newFalid("加载列表分页出错", e.getMessage());
		}
	}

	/**
	 * 保存功能组信息
	 * 
	 * @param dto
	 * @return
	 */
	@RequestMapping(value = "save" + BACK_DYNAMIC_SUFFIX, method = RequestMethod.POST)
	@FunctionCode(value = "CF_MANAGE_FUN_ADD", type = ResponseType.Json)
	public RestResultDto<Boolean> savefunctionInfo(HttpServletRequest request, CloudFunctionDto dto) {
		try {
			cloudFunctionService.save(dto);
			return RestResultDto.newSuccess(true, "保存成功！");
		} catch (Exception e) {
			logger.error("FunctionController.savefunctionInfo", e);
			return RestResultDto.newFalid("保存失败", e.getMessage());
		}
	}

	/**
	 * 更新功能组信息
	 * 
	 * @param dto
	 * @return
	 */
	@RequestMapping(value = "update" + BACK_DYNAMIC_SUFFIX, method = RequestMethod.POST)
	@FunctionCode(value = "CF_MANAGE_FUN_UPDATE", type = ResponseType.Json)
	public RestResultDto<Boolean> updatefunctionInfo(HttpServletRequest request, CloudFunctionDto dto) {
		try {
			cloudFunctionService.update(dto);
			return RestResultDto.newSuccess(true, "更新成功！");
		} catch (Exception e) {
			logger.error("FunctionController.updatefunctionInfo", e);
			return RestResultDto.newFalid("更新失败", e.getMessage());
		}

	}

	/**
	 * 删除1~N条记录
	 * 
	 * @param ids
	 * @return
	 */
	@RequestMapping(value = "deletes" + BACK_DYNAMIC_SUFFIX, method = RequestMethod.POST)
	@FunctionCode(value = "CF_MANAGE_FUN_DEL", type = ResponseType.Json)
	public RestResultDto<Boolean> deletes(HttpServletRequest request, @RequestBody String[] ids) {
		try {
			List<String> deleteList = new ArrayList<String>();
			List<String> remainList = new ArrayList<String>();
			this.splitForDeletes(ids, deleteList, remainList);
			cloudFunctionService.deletes(deleteList);
			return RestResultDto.newSuccess(true, "成功：删除" + deleteList.size() + "条，" + remainList.size() + "条记录被使用不允许删除");
		} catch (Exception e) {
			logger.error("FunctionController.deletes", e);
			return RestResultDto.newFalid("删除失败", e.getMessage());
		}

	}

	/**
	 * 将列表分为可以删除的，不可以删除的。
	 * 
	 * @param ids
	 * @param deleteList
	 *            可以删除的记录
	 * @param remainList
	 *            不可以删除的记录
	 */
	private void splitForDeletes(String[] ids, List<String> deleteList, List<String> remainList) {
		if (ArrayUtils.isEmpty(ids)) {
			return;
		}

		boolean isAllowDel = false;
		for (String id : ids) {
			isAllowDel = this.checkForDelete(id);
			if (isAllowDel) {
				deleteList.add(id);
			} else {
				remainList.add(id);
			}
		}
	}

	/**
	 * 功能是否允许删除 判断是否与角色关联
	 * 
	 * @param id
	 */
	private boolean checkForDelete(String id) {
		List<SearchFilter> sfList = new ArrayList<SearchFilter>();
		sfList.add(new SearchFilter("functionId", Operator.EQ, id));

		List<CloudFunctionRole> list = cloudFunctionRoleService.findListByFilter(sfList, null);
		if (CollectionUtils.isNotEmpty(list)) {
			return false;
		}

		return true;
	}

	@RequestMapping(value = "loadTree" + FORE_DYNAMIC_SUFFIX, method = RequestMethod.POST)
	public RestResultDto<String> loadTree(HttpServletRequest request, HttpServletResponse response) {
		try {
			String systemId = SpringmvcUtils.getParameter("systemId");

			Map<String, Object> paramMap = Maps.newHashMap();
			paramMap.put("systemId", systemId);

			FunctionGroupTree functionGroupTree = FunctionGroupTree.getInstance();
			functionGroupTree.reloadFunctionTree(paramMap);
			String json = treeService.generateJsonCheckboxTree(functionGroupTree, false);
			return RestResultDto.newSuccess(json);
		} catch (Exception e) {
			logger.error("FunctionController.loadTree", e);
			return RestResultDto.newFalid("加载树失败", e.getMessage());
		}
	}

	/**
	 * 验证功能code的唯一性
	 * 
	 * @param request
	 * 
	 * @param code
	 * @return 返回是否成功
	 */
	@RequestMapping(value = "checkForm/{param}" + FORE_DYNAMIC_SUFFIX, method = RequestMethod.POST)

	public RestResultDto<Boolean> checkForm(@PathVariable("param") String param, HttpServletRequest request) {
		try {
			String systemId = SpringmvcUtils.getParameter("systemId");
			boolean success = false;
			String value = SpringmvcUtils.getParameter(param);
			String id = SpringmvcUtils.getParameter("id");
			if (param.equals("code")) {
				success = !cloudFunctionService.isCodeExistsForSystem(systemId, id, value);// 存在返回false
			}
			return RestResultDto.newSuccess(success);
		} catch (Exception e) {
			logger.error("FunctionController.checkForm", e);
			return RestResultDto.newFalid("验证code唯一性失败", e.getMessage());
		}
	}

	/**
	 * 获取功能可指向的目标系统列表
	 * 
	 * @return
	 */
	@RequestMapping(value = "loadGoalSystem" + FORE_DYNAMIC_SUFFIX, method = RequestMethod.POST)
	public RestResultDto<List<CloudSystem>> loadGoalSystem() {
		try {
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(new Order(Direction.ASC, "systemType"));
			orderList.add(new Order(Direction.ASC, "systemName"));

			Sort sort = new Sort(orderList);

			List<CloudSystem> dtoList = cloudSystemService.findListByFilter(null, sort);
			return RestResultDto.newSuccess(dtoList);
		} catch (Exception e) {
			logger.error("FunctionController.loadGoalSystem", e);
			return RestResultDto.newFalid("获取目标系统失败", e.getMessage());
		}

	}
}
