package com.vortex.cloud.ums.web;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.vortex.cloud.ums.dataaccess.service.ICloudRoleService;
import com.vortex.cloud.ums.model.CloudRole;
import com.vortex.cloud.ums.util.web.BaseController;
import com.vortex.cloud.ums.util.support.ForeContext;
import com.vortex.cloud.vfs.common.web.springmvc.SpringmvcUtils;
import com.vortex.cloud.vfs.data.dto.DataStore;
import com.vortex.cloud.vfs.data.dto.RestResultDto;
import com.vortex.cloud.vfs.data.support.SearchFilter;
import com.vortex.cloud.vfs.data.support.SearchFilter.Operator;






/**
 * 云系统角色管理：超级管理员为租户管理员角色、业务系统管理员角色分配相应默认功能
 * 
 * @author LiShijun
 *
 */
  @RestController      
@RequestMapping("cloud/management/role/cloud")
public class CloudSystemRoleController extends BaseController {
	private static final Logger logger = LoggerFactory.getLogger(CloudSystemRoleController.class);
	private static final String FORE_DYNAMIC_SUFFIX = com.vortex.cloud.ums.support.ManagementConstant.PERMISSION_SUFFIX_SA;
//	private static final String BACK_DYNAMIC_SUFFIX = com.vortex.cloud.ums.support.ManagementConstant.PERMISSION_SUFFIX_SA;

	@Resource
	private ICloudRoleService cloudRoleService;
	
	/**
	 * 获取云系统预定义角色列表
	 * 
	 * @param request
	 * @param response
	 * @return
	 * 
	 */
	@RequestMapping(value = "pageList" + FORE_DYNAMIC_SUFFIX, method = RequestMethod.POST)
	
	public RestResultDto<DataStore<CloudRole>> pageList(HttpServletRequest request, HttpServletResponse response) {
		try {
			String systemId = SpringmvcUtils.getParameter("systemId");

			List<SearchFilter> filterList = new ArrayList<SearchFilter>();

			filterList.add(new SearchFilter("systemId", Operator.EQ, systemId));
			filterList.add(new SearchFilter("roleType", Operator.EQ, CloudRole.ROLE_TYPE_PRESET));

			Sort sort = new Sort(Direction.ASC, "orderIndex");
			Pageable pageable = ForeContext.getPageable(request, sort);
			
			Page<CloudRole> page = cloudRoleService.findPageByFilter(pageable, filterList);
			
			DataStore<CloudRole> ds = new DataStore<CloudRole>();
			if (null != page) {
				ds.setRows(page.getContent());
				ds.setTotal(page.getTotalElements());
			}
			return RestResultDto.newSuccess(ds);
		} catch (Exception e) {
			logger.error("CloudSystemRoleController.pageList", e);
			return RestResultDto.newFalid("查询失败", e.getMessage());
		}
	}
}
