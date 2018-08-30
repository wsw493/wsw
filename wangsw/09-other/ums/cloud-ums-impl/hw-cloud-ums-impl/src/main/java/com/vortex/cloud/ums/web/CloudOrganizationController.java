package com.vortex.cloud.ums.web;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.vortex.cloud.ums.annotation.FunctionCode;
import com.vortex.cloud.ums.dataaccess.service.ICloudDepartmentService;
import com.vortex.cloud.ums.dataaccess.service.ICloudOrganizationService;
import com.vortex.cloud.ums.dataaccess.service.ITenantService;
import com.vortex.cloud.ums.dto.CloudDepartmentDto;
import com.vortex.cloud.ums.dto.CloudOrganizationDto;
import  com.vortex.cloud.vfs.data.dto.LoginReturnInfoDto;
import com.vortex.cloud.ums.dto.TenantDto;
import com.vortex.cloud.ums.enums.ResponseType;
import com.vortex.cloud.ums.model.CloudOrganization;
import com.vortex.cloud.ums.support.ManagementConstant;
import com.vortex.cloud.ums.tree.OrganizationTree;
import com.vortex.cloud.ums.tree.OrganizationTreeWithPermission;
import com.vortex.cloud.ums.util.web.BaseController;
import com.vortex.cloud.ums.util.support.ForeContext;
import com.vortex.cloud.ums.util.tree.ITreeService;
import com.vortex.cloud.vfs.common.exception.VortexException;
import com.vortex.cloud.vfs.common.mapper.JsonMapper;
import com.vortex.cloud.vfs.common.web.springmvc.SpringmvcUtils;
import com.vortex.cloud.vfs.data.dto.DataStore;
import com.vortex.cloud.vfs.data.dto.RestResultDto;
import com.vortex.cloud.vfs.data.support.SearchFilter;
import com.vortex.cloud.vfs.data.support.SearchFilter.Operator;

/**
 * 
 * @author lishijun 租户部门下级组织结构树管理
 *
 */
@SuppressWarnings("all")
@RestController
@RequestMapping("cloud/management/org")
public class CloudOrganizationController extends BaseController {
	private static final Logger logger = LoggerFactory.getLogger(CloudOrganizationController.class);

	private static final String FORE_DYNAMIC_SUFFIX = com.vortex.cloud.ums.support.ManagementConstant.PERMISSION_SUFFIX_SA;
	private static final String BACK_DYNAMIC_SUFFIX = com.vortex.cloud.ums.support.ManagementConstant.PERMISSION_SUFFIX_SA;

	@Resource
	private ITreeService treeService;

	@Resource
	private ITenantService tenantService;

	@Resource
	private ICloudDepartmentService cloudDepartmentService;

	@Resource
	private ICloudOrganizationService cloudOrganizationService;

	/**
	 * 用于树的加载、刷新
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "loadTree" + FORE_DYNAMIC_SUFFIX, method = { RequestMethod.POST, RequestMethod.GET })
	public RestResultDto<String> loadTree(HttpServletRequest request, HttpServletResponse response) {
		try {

			String departmentId = SpringmvcUtils.getParameter("departmentId"); // 单位ID
			List<SearchFilter> filterList = new ArrayList<>();
			filterList.add(new SearchFilter("org.departmentId", Operator.EQ, departmentId));

			OrganizationTree organizationTree = OrganizationTree.getInstance();
			organizationTree.reloadOrganizationTree(departmentId, filterList);
			String jsonStr = treeService.generateJsonCheckboxTree(organizationTree, false);
			return RestResultDto.newSuccess(jsonStr);
		} catch (Exception e) {
			logger.error("加载树出错", e);
			return RestResultDto.newFalid("加载树出错", e.getMessage());
		}
	}

	/**
	 * 获取单位机构树(根据当前人的权限范围来获取) <br>
	 * 不传控制权限就不做权限控制，查询该租户下的单位机构树
	 * 
	 * @param request
	 * @return
	 * @throws Exception
	 */

	@RequestMapping(value = "loadOrgTreeByPermission" + FORE_DYNAMIC_SUFFIX, method = { RequestMethod.GET, RequestMethod.POST })
	public RestResultDto<String> loadOrgTreeByPermission(HttpServletRequest request) throws Exception {
		String jsonStr = null;
		try {
			Map<String, String> param = Maps.newHashMap();
			LoginReturnInfoDto loginInfo = super.getLoginInfo(request);
			String tenantId = loginInfo.getTenantId();
			String userId = loginInfo.getUserId();

			String isControlPermission = SpringmvcUtils.getParameter("isControlPermission");

			if (logger.isDebugEnabled()) {
				logger.debug("loadOrgTree(), tenantId=" + tenantId);
			}
			param.put("userId", userId);
			param.put("tenantId", tenantId);
			param.put("isControlPermission", isControlPermission);

			OrganizationTreeWithPermission tree = OrganizationTreeWithPermission.getInstance();
			tree.reloadDeptOrgTree(param);
			jsonStr = treeService.generateJsonCheckboxTree(tree, false);
			return RestResultDto.newSuccess(jsonStr);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("加载树出错", e);
			return RestResultDto.newFalid("加载树出错", e.getMessage());
		}
	}

	/**
	 * 用于机构树的加载、刷新
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "loadDeptOrgTree" + FORE_DYNAMIC_SUFFIX, method = RequestMethod.POST)
	public RestResultDto<String> loadDeptOrgTree(HttpServletRequest request, HttpServletResponse response) {
		try {

			LoginReturnInfoDto loginInfo = super.getLoginInfo(request);
			String tenantId = loginInfo.getTenantId();
			String departmentId = SpringmvcUtils.getParameter("departmentId");

			OrganizationTree tree = OrganizationTree.getInstance();
			tree.reloadDeptOrgTree(tenantId, departmentId);
			String jsonStr = treeService.generateJsonCheckboxTree(tree, false);

			return RestResultDto.newSuccess(jsonStr);
		} catch (Exception e) {
			logger.error("加载树出错", e);
			return RestResultDto.newFalid("加载树出错", e.getMessage());
		}
	}

	/**
	 * 添加时的表单校验。
	 * 
	 * @param paramName
	 * @return
	 */
	@RequestMapping(value = "checkForAdd/{paramName}" + FORE_DYNAMIC_SUFFIX, method = RequestMethod.POST)

	public RestResultDto<Boolean> checkForAdd(@PathVariable("paramName") String paramName, HttpServletRequest request) {
		try {

			LoginReturnInfoDto loginInfo = super.getLoginInfo(request);
			String tenantId = loginInfo.getTenantId();
			String paramVal = SpringmvcUtils.getParameter(paramName);
			if (StringUtils.isBlank(paramName) || StringUtils.isBlank(paramVal)) {
				return RestResultDto.newSuccess(false);
			}

			if ("orgCode".equals(paramName)) {
				if (cloudOrganizationService.isCodeExisted(tenantId, paramVal)) {
					return RestResultDto.newSuccess(false);
				} else {
					return RestResultDto.newSuccess(true);
				}
			}

			return RestResultDto.newSuccess(true);
		} catch (Exception e) {
			logger.error("校验出错", e);
			return RestResultDto.newFalid("校验出错", e.getMessage());
		}
	}

	/**
	 * 新增
	 * 
	 * @param dto
	 * @return
	 */
	@RequestMapping(value = "addDtl" + BACK_DYNAMIC_SUFFIX, method = RequestMethod.POST)
	@FunctionCode(value = "CF_MANAGE_TENANT_ORG_ADD", type = ResponseType.Json)
	public RestResultDto<Boolean> addDtl(HttpServletRequest request, CloudOrganizationDto dto) {
		try {
			LoginReturnInfoDto loginInfo = super.getLoginInfo(request);
			String tenantId = loginInfo.getTenantId();
			dto.setTenantId(tenantId);
			cloudOrganizationService.save(dto);
			return RestResultDto.newSuccess(true, "添加成功");
		} catch (Exception e) {
			logger.error("CloudOrganizationController.addDtl", e);
			return RestResultDto.newFalid("添加失败", e.getMessage());
		}

	}

	@RequestMapping(value = "pageList" + FORE_DYNAMIC_SUFFIX, method = RequestMethod.POST)
	public RestResultDto<DataStore<CloudOrganizationDto>> pageList(HttpServletRequest request, HttpServletResponse response) {
		try {
			// 查询条件
			String parentId = SpringmvcUtils.getParameter("parentId");
			String orgName = SpringmvcUtils.getParameter("orgName");

			logger.info("CloudOrganizationController.pageList(parentId=" + parentId + ", orgName=" + orgName + ")");

			if (StringUtils.isBlank(parentId)) {
				throw new VortexException("请选择父部门！");
			}

			List<SearchFilter> filterList = Lists.newArrayList();
			filterList.add(new SearchFilter("org.parentId", Operator.EQ, parentId));

			if (!StringUtils.isBlank(orgName)) {
				filterList.add(new SearchFilter("org.orgName", Operator.LIKE, orgName));
			}

			// 得到分页
			Pageable pageable = ForeContext.getPageable(request, null);
			Page<CloudOrganization> pageResult = cloudOrganizationService.findPageByFilter(pageable, filterList);

			DataStore<CloudOrganizationDto> ds = new DataStore<>();
			if (pageResult != null) {
				ds.setTotal(pageResult.getTotalElements());

				List<CloudOrganization> orgList = pageResult.getContent();

				List<CloudOrganizationDto> dtoList = new ArrayList<>();
				CloudOrganizationDto dto = null;
				for (CloudOrganization org : orgList) {
					dto = new CloudOrganizationDto();
					BeanUtils.copyProperties(org, dto);
					dtoList.add(dto);
				}

				ds.setRows(dtoList);
			}

			return RestResultDto.newSuccess(ds);
		} catch (Exception e) {
			return RestResultDto.newFalid("获取列表分页失败", e.getMessage());
		}
	}

	@RequestMapping(value = "loadCloudOrgDtl" + FORE_DYNAMIC_SUFFIX, method = RequestMethod.POST)
	public RestResultDto<CloudOrganizationDto> loadCloudOrgDtl(HttpServletResponse response) {
		try {
			JsonMapper jsonMapper = new JsonMapper();
			String id = SpringmvcUtils.getParameter("id");
			CloudOrganizationDto dto = cloudOrganizationService.getById(id);
			return RestResultDto.newSuccess(dto);
		} catch (Exception e) {
			return RestResultDto.newFalid("根据id加载机构失败", e.getMessage());
		}
	}

	/**
	 * 设置默认经纬度
	 * 
	 * @param dto
	 */
	private void setDefaultLatLon(CloudOrganizationDto dto) {
		if (dto.getLatitude() != null || dto.getLongitude() != null) {
			return;
		}

		Double latitude = null;
		Double longitude = null;

		CloudOrganizationDto parentOrg = cloudOrganizationService.getById(dto.getParentId());
		if (parentOrg != null) {
			latitude = parentOrg.getLatitude();
			longitude = parentOrg.getLongitude();
		}

		if (latitude == null) {
			CloudDepartmentDto department = cloudDepartmentService.getById(dto.getDepartmentId());
			if (department != null) {
				latitude = department.getLatitude();
				longitude = department.getLongitude();
			}
		}

		if (latitude == null) {
			TenantDto tenant = tenantService.loadTenant(dto.getTenantId());
			if (tenant != null) {
				latitude = tenant.getLatitudeDone();
				longitude = tenant.getLongitudeDone();
			}
		}

		dto.setLatitude(latitude);
		dto.setLongitude(longitude);
	}

	/**
	 * 修改时的表单校验
	 * 
	 * @param paramName
	 * @return
	 */
	@RequestMapping(value = "checkForUpdate/{paramName}" + FORE_DYNAMIC_SUFFIX, method = RequestMethod.POST)
	public RestResultDto<Boolean> checkForUpdate(@PathVariable("paramName") String paramName) {
		try {

			String tenantId = SpringmvcUtils.getParameter("tenantId");
			String id = SpringmvcUtils.getParameter("id");
			if (StringUtils.isBlank(tenantId) || StringUtils.isBlank(id)) {
				logger.error("checkForUpdate(), 校验参数不足");
				throw new VortexException("校验参数不足");
			}

			String paramVal = SpringmvcUtils.getParameter(paramName);
			if (StringUtils.isBlank(paramName) || StringUtils.isBlank(paramVal)) {
				return RestResultDto.newSuccess(false);
			}

			if ("orgCode".equals(paramName)) {
				return RestResultDto.newSuccess(cloudOrganizationService.validateCodeOnUpdate(tenantId, id, paramVal));
			}

			return RestResultDto.newSuccess(true);
		} catch (Exception e) {
			return RestResultDto.newFalid("校验参数出错", e.getMessage());
		}
	}

	/**
	 * 修改项目组信息
	 * 
	 * @param dto
	 * @return
	 */
	@RequestMapping(value = "updateDtl" + BACK_DYNAMIC_SUFFIX, method = RequestMethod.POST)
	@FunctionCode(value = "CF_MANAGE_TENANT_ORG_UPDATE", type = ResponseType.Json)
	public RestResultDto<Boolean> updateDtl(HttpServletRequest request, CloudOrganizationDto dto) {

		try {
			cloudOrganizationService.update(dto);
			return RestResultDto.newSuccess(true, "修改成功");
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("CloudOrganizationController.updateDtl()", e);
			return RestResultDto.newFalid("修改失败", e.getMessage());
		}

	}

	/**
	 * 删除机构
	 * 
	 * @param request
	 * @param orgId
	 * @return
	 */
	@RequestMapping(value = "deleteOrg" + ManagementConstant.BACK_DYNAMIC_SUFFIX, method = RequestMethod.POST)
	@FunctionCode(value = "CF_MANAGE_TENANT_ORG_DELETE", type = ResponseType.Json)
	public RestResultDto<Boolean> deleteDept(HttpServletRequest request, String orgId) {

		try {
			cloudOrganizationService.deleteOrg(orgId);
			return RestResultDto.newSuccess(true, "删除成功");
		} catch (Exception e) {
			e.printStackTrace();

			logger.error("CloudOrganizationController.deleteDept()", e);
			return RestResultDto.newFalid("删除失败", e.getMessage());
		}

	}
}
