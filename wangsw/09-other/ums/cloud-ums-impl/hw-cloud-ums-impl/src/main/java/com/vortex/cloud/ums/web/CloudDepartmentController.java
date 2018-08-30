package com.vortex.cloud.ums.web;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.collect.Lists;
import com.vortex.cloud.ums.annotation.FunctionCode;
import com.vortex.cloud.ums.dataaccess.service.ICloudDepartmentService;
import com.vortex.cloud.ums.dataaccess.service.ITenantService;
import com.vortex.cloud.ums.dto.CloudDepartmentDto;
import  com.vortex.cloud.vfs.data.dto.LoginReturnInfoDto;
import com.vortex.cloud.ums.enums.ResponseType;
import com.vortex.cloud.ums.model.CloudDepartment;
import com.vortex.cloud.ums.support.ManagementConstant;
import com.vortex.cloud.ums.tree.OrganizationTree;
import com.vortex.cloud.ums.util.support.Constants;
import com.vortex.cloud.ums.util.support.ForeContext;
import com.vortex.cloud.ums.util.tree.ITreeService;
import com.vortex.cloud.ums.util.web.BaseController;
import com.vortex.cloud.vfs.common.exception.VortexException;
import com.vortex.cloud.vfs.common.lang.StringUtil;
import com.vortex.cloud.vfs.common.mapper.JsonMapper;
import com.vortex.cloud.vfs.common.web.springmvc.SpringmvcUtils;
import com.vortex.cloud.vfs.data.dto.DataStore;
import com.vortex.cloud.vfs.data.dto.RestResultDto;
import com.vortex.cloud.vfs.data.support.SearchFilter;
import com.vortex.cloud.vfs.data.support.SearchFilter.Operator;

/**
 * 
 * @author lishijun 租户部门管理
 *
 */
@SuppressWarnings("all")
@RestController
@RequestMapping("cloud/management/dept")
public class CloudDepartmentController extends BaseController {
	private static final Logger logger = LoggerFactory.getLogger(CloudDepartmentController.class);

	private static final String FORE_DYNAMIC_SUFFIX = com.vortex.cloud.ums.support.ManagementConstant.PERMISSION_SUFFIX_SA;
	private static final String BACK_DYNAMIC_SUFFIX = com.vortex.cloud.ums.support.ManagementConstant.PERMISSION_SUFFIX_SA;

	@Resource
	private ITenantService tenantService;
	@Resource
	private ITreeService treeService;
	@Resource
	private ICloudDepartmentService cloudDepartmentService;

	/**
	 * 添加时的表单校验。
	 * 
	 * @param paramName
	 *            被校验的表单域name
	 * @return
	 */
	@RequestMapping(value = "checkForAdd/{paramName}" + FORE_DYNAMIC_SUFFIX, method = RequestMethod.POST)
	public RestResultDto<Boolean> checkForAdd(@PathVariable("paramName") String paramName, HttpServletRequest request) {
		try {

			String paramVal = SpringmvcUtils.getParameter(paramName);
			if (StringUtil.isNullOrEmpty(paramName) || StringUtil.isNullOrEmpty(paramVal)) {
				return RestResultDto.newSuccess(true);
			}

			if (!"depCode".equals(paramName)) {
				return RestResultDto.newSuccess(true);
			}

			String tenantId = super.getLoginInfo(request).getTenantId();
			if ("depCode".equals(paramName)) {
				if (cloudDepartmentService.isCodeExisted(tenantId, paramVal)) {
					return RestResultDto.newSuccess(false);
				} else {
					return RestResultDto.newSuccess(true);
				}
			}

			return RestResultDto.newSuccess(true);
		} catch (Exception e) {
			return RestResultDto.newFalid("校验参数出错", e.getMessage());
		}
	}

	/**
	 * 新增
	 * 
	 * @param request
	 * @param deptDto
	 * @return
	 */
	@RequestMapping(value = "addDtl" + BACK_DYNAMIC_SUFFIX, method = RequestMethod.POST)
	@FunctionCode(value = "CF_MANAGE_TENANT_DEPT_ADD", type = ResponseType.Json)
	public RestResultDto<Boolean> addDtl(HttpServletRequest request, CloudDepartmentDto deptDto) {
		try {
			deptDto.setTenantId(super.getLoginInfo(request).getTenantId());
			cloudDepartmentService.save(deptDto);
			return RestResultDto.newSuccess(true);
		} catch (Exception e) {
			logger.error("CloudDepartmentController.addDtl", e);
			return RestResultDto.newFalid("添加失败", e.getMessage());
		}

	}

	/**
	 * 分页查询
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "pageList" + FORE_DYNAMIC_SUFFIX, method = RequestMethod.POST)
	public RestResultDto<DataStore<CloudDepartmentDto>> pageList(HttpServletRequest request, HttpServletResponse response) {
		try {

			// 查询条件
			String tenantId = super.getLoginInfo(request).getTenantId();
			String depName = SpringmvcUtils.getParameter("depName").trim();

			logger.info("CloudDepartmentController.pageList(tenantId=" + tenantId + ", depName=" + depName + ")");

			List<SearchFilter> filterList = Lists.newArrayList();
			if (!StringUtils.isBlank(tenantId)) {
				filterList.add(new SearchFilter("dept.tenantId", Operator.EQ, tenantId));
			}

			if (!StringUtils.isBlank(depName)) {
				filterList.add(new SearchFilter("dept.depName", Operator.LIKE, depName));
			}

			// 得到分页
			Pageable pageable = ForeContext.getPageable(request, null);
			Page<CloudDepartment> pageResult = cloudDepartmentService.findPageByFilter(pageable, filterList);

			DataStore<CloudDepartmentDto> ds = new DataStore<>();
			if (pageResult != null) {
				ds.setTotal(pageResult.getTotalElements());

				List<CloudDepartment> deptList = pageResult.getContent();

				List<CloudDepartmentDto> dtoList = new ArrayList<>();
				CloudDepartmentDto dto = null;
				for (CloudDepartment dept : deptList) {
					dto = new CloudDepartmentDto();
					BeanUtils.copyProperties(dept, dto);
					dtoList.add(dto);
				}

				ds.setRows(dtoList);
			}
			return RestResultDto.newSuccess(ds);
		} catch (Exception e) {
			logger.error("获取列表分页失败", e);
			return RestResultDto.newFalid("获取列表分页失败", e.getMessage());
		}
	}

	/**
	 * 更新时表单域的校验
	 * 
	 * @param paramName
	 * @return
	 */
	@RequestMapping(value = "checkForUpdate/{paramName}" + FORE_DYNAMIC_SUFFIX, method = RequestMethod.POST)
	public RestResultDto<Boolean> checkForUpdate(@PathVariable("paramName") String paramName, HttpServletRequest request) {
		try {

			String tenantId = super.getLoginInfo(request).getTenantId();
			String id = SpringmvcUtils.getParameter("id");
			if (StringUtils.isBlank(tenantId) || StringUtils.isBlank(id)) {
				logger.error("checkForUpdate(), 校验参数不足");
				throw new VortexException("校验参数不足");
			}

			String paramVal = SpringmvcUtils.getParameter(paramName);
			if (StringUtils.isBlank(paramName) || StringUtils.isBlank(paramVal)) {
				return RestResultDto.newSuccess(false);
			}

			CloudDepartmentDto deptDto = cloudDepartmentService.getById(id);
			if ("depCode".equals(paramName)) {
				return RestResultDto.newSuccess(cloudDepartmentService.validateCodeOnUpdate(tenantId, id, paramVal));
			}

			return RestResultDto.newSuccess(true);
		} catch (Exception e) {
			return RestResultDto.newFalid("校验参数失败", e.getMessage());
		}
	}

	/**
	 * 更新记录。
	 * 
	 * @param deptDto
	 * @return
	 */
	@RequestMapping(value = "updateDtl" + BACK_DYNAMIC_SUFFIX, method = RequestMethod.POST)
	@FunctionCode(value = "CF_MANAGE_TENANT_DEPT_UPDATE", type = ResponseType.Json)
	public RestResultDto<Boolean> updateDtl(HttpServletRequest request, CloudDepartmentDto deptDto) {

		try {
			cloudDepartmentService.update(deptDto);
			return RestResultDto.newSuccess(true, "修改成功");
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("CloudDepartmentController.updateDtl()", e);
			return RestResultDto.newFalid("修改失败", e.getMessage());

		}

	}

	/**
	 * 删除单位
	 * 
	 * @param request
	 * @param deptDto
	 * @return
	 */
	@RequestMapping(value = "deleteDept" + Constants.BACK_DYNAMIC_SUFFIX, method = RequestMethod.POST)
	@FunctionCode(value = "CF_MANAGE_TENANT_DEPT_DELETE", type = ResponseType.Json)
	public RestResultDto<Boolean> deleteDept(HttpServletRequest request) {

		try {
			String departmentId = SpringmvcUtils.getParameter("departmentId");
			cloudDepartmentService.deleteDepartment(departmentId);
			return RestResultDto.newSuccess(true, "删除成功");
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("CloudDepartmentController.updateDtl()", e);
			return RestResultDto.newFalid("删除失败", e.getMessage());
		}

	}

	/**
	 * @Title: requestData @Description: (请求页面数据) @return void @throws
	 */
	@RequestMapping(value = "requestData" + FORE_DYNAMIC_SUFFIX, method = { RequestMethod.GET, RequestMethod.POST })
	public RestResultDto<CloudDepartmentDto> requestData(Model model, HttpServletResponse response) throws Exception {
		try {

			String id = SpringmvcUtils.getParameter("id");
			JsonMapper jsonMapper = new JsonMapper();
			CloudDepartmentDto deptDto = new CloudDepartmentDto();
			PropertyUtils.copyProperties(deptDto, cloudDepartmentService.getById(id));
			return RestResultDto.newSuccess(deptDto);
		} catch (Exception e) {
			logger.error("根据id获取部门信息失败", e);
			return RestResultDto.newFalid("根据id获取部门信息失败", e.getMessage());
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
			logger.error("加载部门树失败", e);
			return RestResultDto.newFalid("加载部门树失败", e.getMessage());
		}
	}
}
