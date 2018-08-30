package com.vortex.cloud.ums.web;

import java.util.ArrayList;
import java.util.List;

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
import com.vortex.cloud.ums.dataaccess.service.ICloudMenuService;
import com.vortex.cloud.ums.dto.CloudMenuDto;
import com.vortex.cloud.ums.dto.CloudMenuSearchDto;
import com.vortex.cloud.ums.enums.ResponseType;
import com.vortex.cloud.ums.model.CloudMenu;
import com.vortex.cloud.ums.tree.CloudMenuTree;
import com.vortex.cloud.ums.util.support.ForeContext;
import com.vortex.cloud.ums.util.tree.ITreeService;
import com.vortex.cloud.ums.util.web.BaseController;
import com.vortex.cloud.vfs.common.web.springmvc.SpringmvcUtils;
import com.vortex.cloud.vfs.data.dto.DataStore;
import com.vortex.cloud.vfs.data.dto.RestResultDto;
import com.vortex.cloud.vfs.data.support.SearchFilter;
import com.vortex.cloud.vfs.data.support.SearchFilter.Operator;

/**
 * 
 * @author LiShijun
 * @date 2016年5月23日 上午8:55:35
 * @Description 菜单管理 History <author> <time> <desc>
 */
@SuppressWarnings("all")
@RestController
@RequestMapping("cloud/management/menu")
public class MenuController extends BaseController {

	private static final Logger logger = LoggerFactory.getLogger(MenuController.class);

	private static final String FORE_DYNAMIC_SUFFIX = com.vortex.cloud.ums.support.ManagementConstant.PERMISSION_SUFFIX_SA;
	private static final String BACK_DYNAMIC_SUFFIX = com.vortex.cloud.ums.support.ManagementConstant.PERMISSION_SUFFIX_SA;

	@Resource
	private ITreeService treeService;

	@Resource
	private ICloudMenuService cloudMenuService;

	/**
	 * 添加时的表单校验。
	 * 
	 * @param paramName
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "checkForm/{paramName}" + FORE_DYNAMIC_SUFFIX, method = RequestMethod.POST)

	public RestResultDto<Boolean> checkForm(@PathVariable("paramName") String paramName, HttpServletRequest request) {
		try {
			String systemId = SpringmvcUtils.getParameter("systemId");
			String id = SpringmvcUtils.getParameter("id");
			String paramVal = SpringmvcUtils.getParameter(paramName);

			if (StringUtils.isBlank(systemId) || StringUtils.isBlank(paramName) || StringUtils.isBlank(paramVal)) {
				return RestResultDto.newSuccess(false);
			}

			if ("code".equals(paramName)) {
				boolean codeExist = cloudMenuService.isCodeExistForSystem(systemId, id, paramVal);
				return RestResultDto.newSuccess(!codeExist);
			}

			return RestResultDto.newSuccess(true);
		} catch (Exception e) {
			logger.error("MenuController.checkForm", e);
			return RestResultDto.newFalid("查询失败", e.getMessage());
		}
	}

	/**
	 * 用于树的加载、刷新
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "loadMenuTree" + FORE_DYNAMIC_SUFFIX, method = RequestMethod.POST)
	public RestResultDto<String> loadMenuTree(HttpServletRequest request, HttpServletResponse response) {
		try {
			String systemId = SpringmvcUtils.getParameter("systemId");

			CloudMenuTree tree = CloudMenuTree.getInstance();
			tree.reloadMenuTree(systemId);
			String json = treeService.generateJsonCheckboxTree(tree, false);
			return RestResultDto.newSuccess(json);
		} catch (Exception e) {
			logger.error("MenuController.loadMenuTree", e);
			return RestResultDto.newFalid("加载失败", e.getMessage());
		}
	}

	/**
	 * 新增
	 * 
	 * @param dto
	 * @return
	 */
	@RequestMapping(value = "add" + BACK_DYNAMIC_SUFFIX, method = RequestMethod.POST)
	@FunctionCode(value = "CF_MANAGE_MENU_ADD", type = ResponseType.Json)
	public RestResultDto<Boolean> add(HttpServletRequest request, CloudMenuDto dto) {
		try {
			cloudMenuService.saveBusinessSystem(dto);
			return RestResultDto.newSuccess(true, "保存成功");
		} catch (Exception e) {
			logger.error("MenuController.add", e);
			return RestResultDto.newFalid("保存失败", e.getMessage());
		}
	}

	@RequestMapping(value = "pageList" + FORE_DYNAMIC_SUFFIX, method = RequestMethod.POST)
	// @FunctionCode(value = "CF_MANAGE_MENU_LIST", type = ResponseType.Json)
	public RestResultDto<DataStore<CloudMenuDto>> pageList(HttpServletRequest request, CloudMenuSearchDto searchDto) {
		try {
			DataStore<CloudMenuDto> ds = new DataStore<CloudMenuDto>();
			if (StringUtils.isBlank(searchDto.getParentId())) {
				logger.error("pageList()", "没有选择父级菜单");
				return RestResultDto.newSuccess(ds);
			}
			Sort defaultSort = new Sort(Direction.DESC, "orderIndex");
			Pageable pageable = ForeContext.getPageable(request, defaultSort);

			// 得到分页
			Page<CloudMenuDto> pageResult = cloudMenuService.findPage(pageable, searchDto);

			if (pageResult != null) {
				ds.setTotal(pageResult.getTotalElements());
				ds.setRows(pageResult.getContent());
			}
			return RestResultDto.newSuccess(ds);
		} catch (Exception e) {
			logger.error("MenuController.pageList", e);
			return RestResultDto.newFalid("查询失败", e.getMessage());
		}
	}

	/**
	 * 去查看页面
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "getmenu" + FORE_DYNAMIC_SUFFIX, method = RequestMethod.POST)
	public RestResultDto<CloudMenuDto> getMenuById(String id) {
		try {
			CloudMenuDto menu = cloudMenuService.getById(id);
			return RestResultDto.newSuccess(menu);
		} catch (Exception e) {
			logger.error("MenuController.getMenuById", e);
			return RestResultDto.newFalid("查看失败", e.getMessage());
		}
	}

	/**
	 * 删除1~N条记录
	 * 
	 * @param ids
	 * @return
	 */
	@RequestMapping(value = "deletes" + BACK_DYNAMIC_SUFFIX, method = RequestMethod.POST)

	@FunctionCode(value = "CF_MANAGE_MENU_DEL", type = ResponseType.Json)
	public RestResultDto<Boolean> deletes(HttpServletRequest request, @RequestBody String[] ids) {
		try {
			List<String> deleteList = new ArrayList<String>();
			List<String> remainList = new ArrayList<String>();
			this.splitForDeletes(ids, deleteList, remainList);
			cloudMenuService.deletes(deleteList);
			return RestResultDto.newSuccess(true, "成功：删除" + deleteList.size() + "条，" + remainList.size() + "条记录因存在子记录不允许删除");

		} catch (Exception e) {
			logger.error("MenuController.getMenuById", e);
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
	 * 是否允许删除 判断是否存在子列表
	 * 
	 * @param id
	 */
	private boolean checkForDelete(String id) {
		// 是否存在子记录
		List<SearchFilter> sfList = new ArrayList<SearchFilter>();
		sfList.add(new SearchFilter("parentId", Operator.EQ, id));

		List<CloudMenu> sonList = cloudMenuService.findListByFilter(sfList, null);
		if (CollectionUtils.isNotEmpty(sonList)) {
			return false;
		}

		return true;
	}
}
