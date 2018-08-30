package com.vortex.cloud.ums.web;

import java.util.ArrayList;
import java.util.HashMap;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.vortex.cloud.ums.annotation.FunctionCode;
import com.vortex.cloud.ums.dataaccess.service.ICloudFunctionRoleService;
import com.vortex.cloud.ums.dataaccess.service.ICloudRoleService;
import com.vortex.cloud.ums.dataaccess.service.ICloudUserRoleService;
import com.vortex.cloud.ums.dto.CloudRoleDto;
import com.vortex.cloud.ums.enums.ResponseType;
import com.vortex.cloud.ums.model.CloudFunctionRole;
import com.vortex.cloud.ums.model.CloudRole;
import com.vortex.cloud.ums.model.CloudUserRole;
import com.vortex.cloud.ums.tree.RoleGroupTree;
import com.vortex.cloud.ums.tree.SystemRoleGroupTree;
import com.vortex.cloud.ums.util.support.ForeContext;
import com.vortex.cloud.ums.util.tree.ITreeService;
import com.vortex.cloud.ums.util.web.BaseController;
import com.vortex.cloud.vfs.common.lang.StringUtil;
import com.vortex.cloud.vfs.common.web.springmvc.SpringmvcUtils;
import com.vortex.cloud.vfs.data.dto.DataStore;
import com.vortex.cloud.vfs.data.dto.LoginReturnInfoDto;
import com.vortex.cloud.vfs.data.dto.RestResultDto;
import com.vortex.cloud.vfs.data.support.SearchFilter;
import com.vortex.cloud.vfs.data.support.SearchFilter.Operator;

/**
 * 业务系统角色管理
 * 
 * @author LiShijun
 *
 */
@RestController
@RequestMapping("cloud/management/role")
public class RoleController extends BaseController {
	private static final Logger logger = LoggerFactory.getLogger(RoleController.class);
	private static final String FORE_DYNAMIC_SUFFIX = com.vortex.cloud.ums.support.ManagementConstant.PERMISSION_SUFFIX_SA;
	private static final String BACK_DYNAMIC_SUFFIX = com.vortex.cloud.ums.support.ManagementConstant.PERMISSION_SUFFIX_SA;

	@Resource
	private ITreeService treeService;

	@Resource
	private ICloudRoleService cloudRoleService;

	@Resource
	private ICloudFunctionRoleService cloudFunctionRoleService;

	@Resource
	private ICloudUserRoleService cloudUserRoleService;

	/**
	 * 根据ID获取数据
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "loadById" + FORE_DYNAMIC_SUFFIX, method = RequestMethod.POST)

	public RestResultDto<CloudRoleDto> loadById(String id) {
		try {

			return RestResultDto.newSuccess(cloudRoleService.getRoleInfoById(id));
		} catch (Exception e) {
			logger.error("根据id加载角色失败", e);
			return RestResultDto.newFalid("根据id加载角色失败", e.getMessage());
		}
	}

	/**
	 * 根据当前节点id，返回下面的角色列表
	 * 
	 * @param request
	 * @param response
	 * @return
	 * 
	 */
	@RequestMapping(value = "pageList" + FORE_DYNAMIC_SUFFIX, method = RequestMethod.POST)

	// @FunctionCode(value = "CF_MANAGE_ROLE_LIST", type = ResponseType.Json)
	public RestResultDto<DataStore<CloudRole>> pageList(HttpServletRequest request, HttpServletResponse response) {
		try {

			String parentId = SpringmvcUtils.getParameter("roleGroupId");
			String systemId = SpringmvcUtils.getParameter("systemId");
			String name = SpringmvcUtils.getParameter("name");
			if (StringUtil.isNullOrEmpty(parentId)) {
				return RestResultDto.newSuccess(new DataStore<CloudRole>());
			}
			List<SearchFilter> filterList = new ArrayList<SearchFilter>();
			if (StringUtils.isNotEmpty(name)) {
				filterList.add(new SearchFilter("name", Operator.LIKE, name));
			}
			filterList.add(new SearchFilter("groupId", Operator.EQ, parentId));
			filterList.add(new SearchFilter("systemId", Operator.EQ, systemId));
			Sort sort = new Sort(Direction.ASC, "orderIndex");
			Pageable pageable = ForeContext.getPageable(request, sort);
			Page<CloudRole> page = cloudRoleService.findPageByFilter(pageable, filterList);
			DataStore<CloudRole> dataStore = null;
			if (null != page) {
				List<CloudRole> result = page.getContent();
				dataStore = new DataStore<CloudRole>(page.getTotalElements(), result);
			} else {
				dataStore = new DataStore<CloudRole>();
			}
			return RestResultDto.newSuccess(dataStore);
		} catch (Exception e) {
			logger.error("加载分页失败", e);
			return RestResultDto.newFalid("加载分页失败", e.getMessage());
		}

	}

	/**
	 * 保存角色组信息
	 * 
	 * @param dto
	 * @return
	 */
	@RequestMapping(value = "save" + BACK_DYNAMIC_SUFFIX, method = RequestMethod.POST)

	@FunctionCode(value = "CF_MANAGE_ROLE_ADD", type = ResponseType.Json)
	public RestResultDto<Boolean> saveroleInfo(HttpServletRequest request, CloudRoleDto dto) {
		try {
			cloudRoleService.saveRole(dto);
			return RestResultDto.newSuccess(true, "保存成功");
		} catch (Exception e) {
			logger.error("CloudRoleController.save", e);
			return RestResultDto.newFalid("保存失败", e.getMessage());
		}
	}

	/**
	 * 更新角色组信息
	 * 
	 * @param dto
	 * @return
	 */
	@RequestMapping(value = "update" + BACK_DYNAMIC_SUFFIX, method = RequestMethod.POST)
	@FunctionCode(value = "CF_MANAGE_ROLE_UPDATE", type = ResponseType.Json)
	public RestResultDto<Boolean> updateroleInfo(HttpServletRequest request, CloudRoleDto dto) {
		try {
			cloudRoleService.updateRole(dto);
			return RestResultDto.newSuccess(true, "更新成功");
		} catch (Exception e) {
			logger.error("CloudRoleController.update", e);
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
	@FunctionCode(value = "CF_MANAGE_ROLE_DEL", type = ResponseType.Json)
	public RestResultDto<Boolean> deletes(HttpServletRequest request, @RequestBody String[] ids) {
		try {
			List<String> deleteList = new ArrayList<String>();
			List<String> remainList = new ArrayList<String>();
			this.splitForDeletes(ids, deleteList, remainList);

			cloudRoleService.deletes(deleteList);
			return RestResultDto.newSuccess(true, "本次删除操作执行结果：删除" + deleteList.size() + "条，" + remainList.size() + "条记录被使用不允许删除");
		} catch (Exception e) {
			logger.error("deletes()", e);
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
	 * 角色是否允许删除 判断是否与功能关联 判断是否与用户关联
	 * 
	 * @param id
	 */
	private boolean checkForDelete(String id) {
		// 是否关联功能
		List<SearchFilter> sfList = new ArrayList<SearchFilter>();
		sfList.add(new SearchFilter("roleId", Operator.EQ, id));

		List<CloudFunctionRole> funRoleList = cloudFunctionRoleService.findListByFilter(sfList, null);
		if (CollectionUtils.isNotEmpty(funRoleList)) {
			return false;
		}

		// 是否关联用户
		sfList = new ArrayList<SearchFilter>();
		sfList.add(new SearchFilter("roleId", Operator.EQ, id));
		List<CloudUserRole> userRoleList = cloudUserRoleService.findListByFilter(sfList, null);
		if (CollectionUtils.isNotEmpty(userRoleList)) {
			return false;
		}

		return true;
	}

	/**
	 * 验证角色code的唯一性
	 * 
	 * @param request
	 * 
	 * @param code
	 * @return 返回是否成功
	 */
	@RequestMapping(value = "checkForm/{param}" + FORE_DYNAMIC_SUFFIX, method = RequestMethod.POST)

	public RestResultDto<Boolean> checkForm(@PathVariable("param") String param, HttpServletRequest request) {
		try {
			boolean success = false;
			String value = SpringmvcUtils.getParameter(param);
			String id = SpringmvcUtils.getParameter("id");
			String systemId = SpringmvcUtils.getParameter("systemId");
			if (param.equals("code")) {
				success = !cloudRoleService.isRoleCodeExists(id, value, systemId);// 存在返回false
			}
			return RestResultDto.newSuccess(success);
		} catch (Exception e) {
			logger.error("验证code唯一性失败" + e.getMessage());
			e.printStackTrace();
			return RestResultDto.newFalid("验证code唯一性失败", e.getMessage());
		}

	}

	/**
	 * 加载角色树
	 * 
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "loadRoleTree" + FORE_DYNAMIC_SUFFIX, method = RequestMethod.POST)
	public RestResultDto<String> loadRoleTree(HttpServletRequest request, HttpServletResponse response) {
		try {

			String systemId = SpringmvcUtils.getParameter("systemId");

			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("systemId", systemId);

			RoleGroupTree roleGroupTree = RoleGroupTree.getInstance();
			roleGroupTree.reloadRoleTree(paramMap);
			String jsonStr = treeService.generateJsonCheckboxTree(roleGroupTree, false);
			return RestResultDto.newSuccess(jsonStr);
		} catch (Exception e) {
			logger.error("加载角色树失败", e);
			return RestResultDto.newFalid("加载角色树失败", e.getMessage());
		}
	}

	/**
	 * 加载系统-角色组-角色树
	 * 
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "loadSystemRoleTree" + FORE_DYNAMIC_SUFFIX, method = RequestMethod.POST)
	public RestResultDto<String> loadSystemRoleTree(HttpServletRequest request, HttpServletResponse response) {
		try {
			LoginReturnInfoDto infoDto = super.getLoginInfo(request);

			SystemRoleGroupTree systemRoleGroupTree = SystemRoleGroupTree.getInstance();
			systemRoleGroupTree.reloadSystemRoleTree(infoDto.getTenantId());
			String jsonStr = treeService.generateJsonCheckboxTree(systemRoleGroupTree, false);
			return RestResultDto.newSuccess(jsonStr);
		} catch (Exception e) {
			logger.error("加载角色树失败", e);
			return RestResultDto.newFalid("加载角色树失败", e.getMessage());
		}
	}
}
