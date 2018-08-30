package com.vortex.cloud.ums.web.rest.np;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.vortex.cloud.ums.dataaccess.service.ICloudStaffService;
import com.vortex.cloud.ums.dataaccess.service.IManagementRestService;
import com.vortex.cloud.ums.dto.CloudStaffDto;
import com.vortex.cloud.ums.dto.CloudStaffSearchDto;
import com.vortex.cloud.ums.dto.StaffDto;
import com.vortex.cloud.ums.model.CloudStaff;
import com.vortex.cloud.ums.support.ManagementConstant;
import com.vortex.cloud.ums.tree.StaffOrganizationTree;
import com.vortex.cloud.ums.tree.StaffOrganizationTreeWithPermission;
import com.vortex.cloud.ums.util.tree.ITreeService;
import com.vortex.cloud.ums.web.rest.ParamRestController;
import com.vortex.cloud.vfs.common.exception.VortexException;
import com.vortex.cloud.vfs.common.mapper.JsonMapper;
import com.vortex.cloud.vfs.common.web.springmvc.SpringmvcUtils;
import com.vortex.cloud.vfs.data.dto.DataStore;
import com.vortex.cloud.vfs.data.dto.RestResultDto;
import com.vortex.cloud.vfs.data.support.SearchFilter;
import com.vortex.cloud.vfs.data.support.SearchFilter.Operator;
import com.vortex.cloud.vfs.data.support.SearchFilters;

@SuppressWarnings("all")
@RequestMapping("cloud/management/rest/np/staff")
@RestController
public class StaffRestNpController {
	private static final Object STAFF_ID = "staffId";
	private static final Object TENANT_ID = "tenantId";
	private static final Object USER_ID = "userId";
	private static final String SYNCTIME = "syncTime";
	private static final String PAGESIZE = "pageSize";
	private static final String PAGENUMBER = "pageNumber";
	private static final Object STAFF_NAME = "staffName";
	private static final Object IDS = "ids";
	private static final Object STAFF_NAMES = "staffNames";
	private static final String COMPANY_ID = "companyId";
	private static final String CONTAIN_MANAGER = "containManager";// 是否包括系统管理员
	private static final String PAGE = "page";
	private static final String ROWS = "rows";
	private static final String NAME = "name";
	private static final String PARTY_POST_IDS = "partyPostIds";
	private static final String IS_DELETED = "isDeleted";
	private static final Object STAFF_CODE = "staffCode";
	private static final String TEANANT_CODE = "tenantCode";
	private Logger logger = LoggerFactory.getLogger(ParamRestController.class);
	private JsonMapper jm = new JsonMapper();
	@Resource
	private ICloudStaffService cloudStaffService;
	@Resource
	private IManagementRestService managementRestService;
	@Resource
	private ITreeService treeService;

	/**
	 * 加载机构人员树，机构+部门+人员(加载一个租户下的)(并且通过条件过滤人员)
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "loadTreeByFilter" + ManagementConstant.PERMISSION_SUFFIX_READ, method = { RequestMethod.GET, RequestMethod.POST })
	public RestResultDto loadTreeByFilter(HttpServletRequest request) throws Exception {
		String msg = null;
		Integer result = ManagementConstant.REST_RESULT_SUCC;
		Object data = null;
		JsonMapper mapper = new JsonMapper();
		try {
			Map<String, Object> paramMap = jm.fromJson(SpringmvcUtils.getParameter(ManagementConstant.REST_PMS), Map.class);
			if (MapUtils.isEmpty(paramMap)) {
				throw new VortexException("参数不能为空");
			}
			String tenantId = (String) paramMap.get(TENANT_ID);
			String companyId = (String) paramMap.get(COMPANY_ID);
			StaffOrganizationTree staffOrganizationTree = StaffOrganizationTree.getInstance();
			staffOrganizationTree.reloadDeptOrgStaffTreeByFilter(paramMap);
			data = treeService.generateJsonCheckboxTree(staffOrganizationTree, false);
		} catch (Exception e) {
			e.printStackTrace();
			result = ManagementConstant.REST_RESULT_FAIL;
			msg = e.getMessage();
			data = null;
			logger.error(msg, e);
		} finally {
			RestResultDto rst = new RestResultDto();
			rst.setResult(result);
			rst.setMsg(msg);
			rst.setData(data);
			return rst;
		}
	}

	/**
	 * 获取租户下人员列表，参数tenantId
	 * 
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "loadListByFilter" + ManagementConstant.PERMISSION_SUFFIX_READ, method = { RequestMethod.GET, RequestMethod.POST })
	public RestResultDto loadListByFilter(HttpServletRequest request) throws Exception {
		String msg = null;
		Integer result = ManagementConstant.REST_RESULT_SUCC;
		Object data = null;
		JsonMapper mapper = new JsonMapper();
		try {
			Map<String, Object> paramMap = jm.fromJson(SpringmvcUtils.getParameter(ManagementConstant.REST_PMS), Map.class);
			if (MapUtils.isEmpty(paramMap)) {
				throw new VortexException("参数不能为空");
			}
			String tenantId = (String) paramMap.get(TENANT_ID);
			String name = (String) paramMap.get("name"); // like
			String phone = (String) paramMap.get("phone"); // like
			if (StringUtils.isEmpty(tenantId)) {
				throw new VortexException("tenantId不能为空");
			}
			SearchFilters filters = new SearchFilters(SearchFilters.Operator.AND);
			filters.add(new SearchFilter("tenantId", Operator.EQ, tenantId));
			if (StringUtils.isNotEmpty(name)) {
				filters.add(new SearchFilters(new SearchFilter("name", Operator.LIKE, name)));
			}
			if (StringUtils.isNotEmpty(phone)) {
				filters.add(new SearchFilters(new SearchFilter("phone", Operator.LIKE, phone)));
			}
			String companyId = (String) paramMap.get(COMPANY_ID);
			if (StringUtils.isNotEmpty(companyId)) {
				SearchFilters searchFilters = new SearchFilters();
				searchFilters.setOperator(SearchFilters.Operator.OR);
				searchFilters.add(new SearchFilter("departmentId", Operator.EQ, companyId));
				searchFilters.add(new SearchFilter("orgId", Operator.EQ, companyId));
				filters.add(searchFilters);
			}
			Boolean containManager = (Boolean) paramMap.get(CONTAIN_MANAGER);
			// 不包含管理员
			if (containManager != null && !containManager) {
				SearchFilters searchFilters = new SearchFilters();
				searchFilters.setOperator(SearchFilters.Operator.OR);
				searchFilters.add(new SearchFilter("departmentId", Operator.NE, null));
				searchFilters.add(new SearchFilter("orgId", Operator.NE, null));
				filters.add(searchFilters);
			}

			data = cloudStaffService.findListByFilters(filters, null);
			msg = "查询成功！";
		} catch (Exception e) {
			e.printStackTrace();
			result = ManagementConstant.REST_RESULT_FAIL;
			msg = e.getMessage();
			data = null;
			logger.error(msg, e);
		} finally {
			RestResultDto rst = new RestResultDto();
			rst.setResult(result);
			rst.setMsg(msg);
			rst.setData(data);
			return rst;
		}
	}

	/**
	 * 根据参数来获取人员信息，并且带上用户信息
	 * 
	 * @param tenantId
	 *            租户id
	 * @param companyId
	 *            公司id
	 * @param containManager
	 *            是否包含管理员
	 * 
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "loadStaffsByFilter" + ManagementConstant.PERMISSION_SUFFIX_READ, method = { RequestMethod.GET, RequestMethod.POST })
	public RestResultDto loadStaffsByFilter(HttpServletRequest request) throws Exception {
		String msg = null;
		Integer result = ManagementConstant.REST_RESULT_SUCC;
		Object data = null;
		JsonMapper mapper = new JsonMapper();
		try {
			Map<String, Object> paramMap = jm.fromJson(SpringmvcUtils.getParameter(ManagementConstant.REST_PMS), Map.class);
			if (MapUtils.isEmpty(paramMap)) {
				throw new VortexException("参数不能为空");
			}
			String tenantId = (String) paramMap.get(TENANT_ID);
			if (StringUtils.isEmpty(tenantId)) {
				throw new VortexException("tenantId不能为空");
			}
			String companyId = (String) paramMap.get(COMPANY_ID);

			Boolean containManager = (Boolean) paramMap.get(CONTAIN_MANAGER);
			List<String> partyPostIds = (List<String>) paramMap.get(PARTY_POST_IDS);

			data = cloudStaffService.loadStaffsByFilter(paramMap);
			msg = "查询成功！";
		} catch (Exception e) {
			e.printStackTrace();
			result = ManagementConstant.REST_RESULT_FAIL;
			msg = e.getMessage();
			data = null;
			logger.error(msg, e);
		} finally {
			RestResultDto rst = new RestResultDto();
			rst.setResult(result);
			rst.setMsg(msg);
			rst.setData(data);
			return rst;
		}
	}

	/**
	 * 同步人员信息
	 * 
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "syncStaffByPage" + ManagementConstant.PERMISSION_SUFFIX_READ, method = { RequestMethod.GET, RequestMethod.POST })
	public RestResultDto syncStaffByPage(HttpServletRequest request) throws Exception {
		String msg = null;
		Integer result = ManagementConstant.REST_RESULT_SUCC;
		Object data = null;
		JsonMapper mapper = new JsonMapper();
		try {
			Map<String, Object> paramMap = jm.fromJson(SpringmvcUtils.getParameter(ManagementConstant.REST_PMS), Map.class);
			if (MapUtils.isEmpty(paramMap)) {
				throw new VortexException("参数不能为空");
			}
			String tenantId = (String) paramMap.get(TENANT_ID);
			if (StringUtils.isEmpty(tenantId)) {
				throw new VortexException("tenantId不能为空");
			}
			String strSyncTime = String.valueOf(paramMap.get(SYNCTIME));
			Long syncTime = Long.valueOf(strSyncTime);

			if (syncTime == null) {
				throw new VortexException("参数syncTime不能为空");
			}

			Integer pageSize = (Integer) paramMap.get(PAGESIZE);
			if (pageSize == null) {
				throw new VortexException("参数pageSize不能为空");
			}
			Integer pageNumber = (Integer) paramMap.get(PAGENUMBER);
			if (pageNumber == null) {
				throw new VortexException("参数pageNumber不能为空");
			}
			data = cloudStaffService.syncStaffByPage(tenantId, syncTime, pageSize, pageNumber);
			msg = "查询成功！";
		} catch (Exception e) {
			e.printStackTrace();
			result = ManagementConstant.REST_RESULT_FAIL;
			msg = e.getMessage();
			data = null;
			logger.error(msg, e);
		} finally {
			RestResultDto rst = new RestResultDto();
			rst.setResult(result);
			rst.setMsg(msg);
			rst.setData(data);
			return rst;
		}
	}

	/**
	 * 查所有
	 * 
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "findAllStaffByPage" + ManagementConstant.PERMISSION_SUFFIX_READ, method = { RequestMethod.GET, RequestMethod.POST })
	public RestResultDto findAllStaffByPage(HttpServletRequest request) throws Exception {
		String msg = null;
		Integer result = ManagementConstant.REST_RESULT_SUCC;
		Object data = null;
		JsonMapper mapper = new JsonMapper();
		try {
			Map<String, Object> paramMap = jm.fromJson(SpringmvcUtils.getParameter(ManagementConstant.REST_PMS), Map.class);
			if (MapUtils.isEmpty(paramMap)) {
				throw new VortexException("参数不能为空");
			}
			String tenantId = (String) paramMap.get(TENANT_ID);
			if (StringUtils.isEmpty(tenantId)) {
				throw new VortexException("tenantId不能为空");
			}
			Integer isDeleted = (Integer) paramMap.get(IS_DELETED);
			data = cloudStaffService.findAllStaffByPage(tenantId, isDeleted);
			msg = "查询成功！";
		} catch (Exception e) {
			e.printStackTrace();
			result = ManagementConstant.REST_RESULT_FAIL;
			msg = e.getMessage();
			data = null;
			logger.error(msg, e);
		} finally {
			RestResultDto rst = new RestResultDto();
			rst.setResult(result);
			rst.setMsg(msg);
			rst.setData(data);
			return rst;
		}
	}

	/**
	 * 根据人员id，得到人员基本信息
	 * 
	 * 
	 * 
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "getstaffbyid" + ManagementConstant.PERMISSION_SUFFIX_READ, method = { RequestMethod.GET, RequestMethod.POST })
	public RestResultDto getStaffById(HttpServletRequest request) throws Exception {
		String msg = null;
		Integer result = null;
		Object data = null;

		try {
			Map<String, String> paramMap = jm.fromJson(SpringmvcUtils.getParameter(ManagementConstant.REST_PMS), Map.class);
			if (MapUtils.isEmpty(paramMap)) {
				throw new VortexException("参数不能为空");
			}

			String staffId = paramMap.get(STAFF_ID);
			if (StringUtils.isBlank(staffId)) {
				throw new VortexException("请传入参数：" + STAFF_ID);
			}

			if (logger.isDebugEnabled()) {
				logger.debug("getStaffById(), staffId=" + staffId);
			}
			CloudStaffDto cloudStaffDto = cloudStaffService.getById(staffId);
			result = ManagementConstant.REST_RESULT_SUCC;
			msg = "成功获取用户信息";
			data = cloudStaffDto;
		} catch (Exception e) {
			e.printStackTrace();
			result = ManagementConstant.REST_RESULT_FAIL;
			msg = e.getMessage();
			data = null;
			logger.error(msg, e);
		} finally {
			RestResultDto restResultDto = new RestResultDto();
			restResultDto.setData(data);
			restResultDto.setMsg(msg);
			restResultDto.setResult(result);
			return restResultDto;
		}
	}

	/**
	 * 根据人员id，得到人员基本信息
	 * 
	 * 
	 * 
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "getstaff" + ManagementConstant.PERMISSION_SUFFIX_READ, method = { RequestMethod.GET, RequestMethod.POST })
	public RestResultDto getStaff(HttpServletRequest request) throws Exception {
		String msg = null;
		Integer result = null;
		Object data = null;

		try {
			Map<String, String> paramMap = jm.fromJson(SpringmvcUtils.getParameter(ManagementConstant.REST_PMS), Map.class);
			if (MapUtils.isEmpty(paramMap)) {
				throw new VortexException("参数不能为空");
			}

			String staffId = paramMap.get(STAFF_ID);
			if (StringUtils.isBlank(staffId)) {
				throw new VortexException("请传入参数：" + STAFF_ID);
			}

			if (logger.isDebugEnabled()) {
				logger.debug("getStaffById(), staffId=" + staffId);
			}
			CloudStaffDto cloudStaffDto = cloudStaffService.getById(staffId);
			result = ManagementConstant.REST_RESULT_SUCC;
			msg = "成功获取用户信息";
			data = cloudStaffDto;
		} catch (Exception e) {
			e.printStackTrace();
			result = ManagementConstant.REST_RESULT_FAIL;
			msg = e.getMessage();
			data = null;
			logger.error(msg, e);
		} finally {
			RestResultDto restResultDto = new RestResultDto();
			restResultDto.setData(data);
			restResultDto.setMsg(msg);
			restResultDto.setResult(result);
			return restResultDto;
		}
	}

	/**
	 * 根据人员id查询人员信息
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "getStaffNamesByIds" + ManagementConstant.PERMISSION_SUFFIX_READ, method = { RequestMethod.GET, RequestMethod.POST })
	public RestResultDto getStaffNamesByIds() throws Exception {
		String msg = null;
		Integer result = ManagementConstant.REST_RESULT_SUCC;
		Object data = null;
		try {
			Map<String, Object> paramMap = jm.fromJson(SpringmvcUtils.getParameter(ManagementConstant.REST_PMS), Map.class);
			if (MapUtils.isEmpty(paramMap)) {
				throw new VortexException("参数不能为空");
			}
			List<String> ids = (List<String>) paramMap.get(IDS);
			Map<String, String> nameMap = null;
			if (CollectionUtils.isNotEmpty(ids)) {
				nameMap = cloudStaffService.getStaffNamesByIds(ids);
			} else {// 如果ids为空，那么返回一个空的map
				nameMap = Maps.newHashMap();
			}

			data = nameMap;
		} catch (Exception e) {
			e.printStackTrace();
			result = ManagementConstant.REST_RESULT_FAIL;
			msg = e.getMessage();
			data = null;
			logger.error(msg, e);
		} finally {
			RestResultDto rst = new RestResultDto();
			rst.setResult(result);
			rst.setMsg(msg);
			rst.setData(data);
			return rst;
		}
	}

	/**
	 * 根据ids获取人员信息
	 * 
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "getStaffsByIds" + ManagementConstant.PERMISSION_SUFFIX_READ, method = { RequestMethod.GET, RequestMethod.POST })
	public RestResultDto getStaffsByIds() throws Exception {
		String msg = null;
		Integer result = ManagementConstant.REST_RESULT_SUCC;
		Object data = null;
		try {
			Map<String, Object> paramMap = jm.fromJson(SpringmvcUtils.getParameter(ManagementConstant.REST_PMS), Map.class);
			if (MapUtils.isEmpty(paramMap)) {
				throw new VortexException("参数不能为空");
			}
			List<String> ids = (List<String>) paramMap.get(IDS);
			Map<String, Object> nameMap = null;
			if (CollectionUtils.isNotEmpty(ids)) {
				nameMap = cloudStaffService.getStaffsByIds(ids);
			} else {// 如果ids为空，那么返回一个空的map
				nameMap = Maps.newHashMap();
			}

			data = nameMap;
		} catch (Exception e) {
			e.printStackTrace();
			result = ManagementConstant.REST_RESULT_FAIL;
			msg = e.getMessage();
			data = null;
			logger.error(msg, e);
		} finally {
			RestResultDto rst = new RestResultDto();
			rst.setResult(result);
			rst.setMsg(msg);
			rst.setData(data);
			return rst;
		}
	}

	/**
	 * 根据用户ids获取人员信息
	 * 
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "getStaffsByUserIds" + ManagementConstant.PERMISSION_SUFFIX_READ, method = { RequestMethod.GET, RequestMethod.POST })
	public RestResultDto getStaffsByUserIds() throws Exception {
		String msg = null;
		Integer result = ManagementConstant.REST_RESULT_SUCC;
		Object data = null;
		try {
			Map<String, Object> paramMap = jm.fromJson(SpringmvcUtils.getParameter(ManagementConstant.REST_PMS), Map.class);
			if (MapUtils.isEmpty(paramMap)) {
				throw new VortexException("参数不能为空");
			}
			List<String> ids = (List<String>) paramMap.get(IDS);
			Map<String, Object> nameMap = null;
			if (CollectionUtils.isNotEmpty(ids)) {
				nameMap = cloudStaffService.getStaffsByUserIds(ids);
			} else {// 如果ids为空，那么返回一个空的map
				nameMap = Maps.newHashMap();
			}

			data = nameMap;
		} catch (Exception e) {
			e.printStackTrace();
			result = ManagementConstant.REST_RESULT_FAIL;
			msg = e.getMessage();
			data = null;
			logger.error(msg, e);
		} finally {
			RestResultDto rst = new RestResultDto();
			rst.setResult(result);
			rst.setMsg(msg);
			rst.setData(data);
			return rst;
		}
	}

	/**
	 * 根据人员名称查询人员信息
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "getStaffIdsByNames" + ManagementConstant.PERMISSION_SUFFIX_READ, method = { RequestMethod.GET, RequestMethod.POST })
	public RestResultDto getStaffIdsByNames() throws Exception {
		String msg = null;
		Integer result = ManagementConstant.REST_RESULT_SUCC;
		Object data = null;
		JsonMapper mapper = new JsonMapper();
		try {
			Map<String, Object> paramMap = jm.fromJson(SpringmvcUtils.getParameter(ManagementConstant.REST_PMS), Map.class);
			if (MapUtils.isEmpty(paramMap)) {
				throw new VortexException("参数不能为空");
			}
			String tenantId = (String) paramMap.get(TENANT_ID);
			if (StringUtils.isEmpty(tenantId)) {
				throw new VortexException("租户id不能为空");
			}
			List<String> names = (List<String>) paramMap.get(STAFF_NAMES);
			Map<String, String> nameMap = null;
			if (CollectionUtils.isNotEmpty(names)) {
				nameMap = cloudStaffService.getStaffIdsByNames(names, tenantId);
			} else {// 如果names为空，那么返回一个空的map
				nameMap = Maps.newHashMap();
			}

			data = nameMap;
		} catch (Exception e) {
			e.printStackTrace();
			result = ManagementConstant.REST_RESULT_FAIL;
			msg = e.getMessage();
			data = null;
			logger.error(msg, e);
		} finally {
			RestResultDto rst = new RestResultDto();
			rst.setResult(result);
			rst.setMsg(msg);
			rst.setData(data);
			return rst;
		}
	}

	/**
	 * 查询人员分页 <br>
	 * page: 页， 从0开始 <br>
	 * rows: 行，默认10 <br>
	 * name:姓名（模糊匹配） <br>
	 * tenantId:租户ID （可选）
	 * 
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "getStaffPage" + ManagementConstant.PERMISSION_SUFFIX_READ, method = { RequestMethod.GET, RequestMethod.POST })
	public RestResultDto getStaffPage() throws Exception {
		String msg = null;
		Integer result = ManagementConstant.REST_RESULT_SUCC;
		Object data = null;
		JsonMapper mapper = new JsonMapper();
		try {

			Pageable pageable = null;
			List<SearchFilter> searchFilters = Lists.newArrayList();

			Map<String, Object> paramMap = jm.fromJson(SpringmvcUtils.getParameter(ManagementConstant.REST_PMS), Map.class);

			Integer page = (Integer) (MapUtils.isNotEmpty(paramMap) && paramMap.get(PAGE) != null ? paramMap.get(PAGE) : 0);
			Integer rows = (Integer) (MapUtils.isNotEmpty(paramMap) && paramMap.get(ROWS) != null ? paramMap.get(ROWS) : 10);
			pageable = new PageRequest(page, rows, Direction.ASC, "orderIndex");

			// 参数不为空的时候取出所有的参数
			if (MapUtils.isNotEmpty(paramMap)) {

				String name = (String) paramMap.get(NAME);
				String tenantId = (String) paramMap.get(TENANT_ID);
				if (StringUtils.isNotBlank(name)) {
					searchFilters.add(new SearchFilter("name", Operator.LIKE, name));
				}
				if (StringUtils.isNotBlank(tenantId)) {
					searchFilters.add(new SearchFilter("tenantId", Operator.EQ, tenantId));
				}
			}

			Page<CloudStaff> cloudstaffs = cloudStaffService.findPageByFilter(pageable, searchFilters);

			DataStore<CloudStaff> dataStore = new DataStore<>();
			if (CollectionUtils.isNotEmpty(cloudstaffs.getContent())) {
				dataStore.setRows(cloudstaffs.getContent());
				dataStore.setTotal(cloudstaffs.getTotalElements());
			}

			data = dataStore;
		} catch (Exception e) {
			e.printStackTrace();
			result = ManagementConstant.REST_RESULT_FAIL;
			msg = e.getMessage();
			data = null;
			logger.error(msg, e);
		} finally {
			RestResultDto rst = new RestResultDto();
			rst.setResult(result);
			rst.setMsg(msg);
			rst.setData(data);
			return rst;
		}
	}

	/**
	 * 设置名字首字母
	 * 
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "setNameInitial" + ManagementConstant.PERMISSION_SUFFIX_READ, method = { RequestMethod.GET, RequestMethod.POST })
	public RestResultDto setNameInitial() throws Exception {
		String msg = null;
		Integer result = ManagementConstant.REST_RESULT_SUCC;
		Object data = null;
		JsonMapper mapper = new JsonMapper();
		try {

			cloudStaffService.setNameInitial();
			msg = "设置所有人员首字母成功";
			data = true;
		} catch (Exception e) {
			e.printStackTrace();
			result = ManagementConstant.REST_RESULT_FAIL;
			msg = e.getMessage();
			data = null;
			logger.error(msg, e);
		} finally {
			RestResultDto rst = new RestResultDto();
			rst.setResult(result);
			rst.setMsg(msg);
			rst.setData(data);
			return rst;
		}
	}

	/*
	 * 加载机构人员树，机构+部门+人员(根据人员权限)
	 * 
	 * @param isControlPermission
	 * 
	 * @param request
	 * 
	 * @param response
	 * 
	 * @return
	 */
	@RequestMapping(value = "loadStaffTreeWithPermission" + ManagementConstant.PERMISSION_SUFFIX_READ, method = { RequestMethod.GET, RequestMethod.POST })
	public RestResultDto loadUserTreeWithPermission(HttpServletRequest request) throws Exception {
		String msg = null;
		Integer result = ManagementConstant.REST_RESULT_SUCC;
		Object data = null;
		JsonMapper mapper = new JsonMapper();
		try {
			Map<String, String> paramMap = jm.fromJson(SpringmvcUtils.getParameter(ManagementConstant.REST_PMS), Map.class);
			if (MapUtils.isEmpty(paramMap)) {
				throw new VortexException("参数不能为空");
			}
			String userId = paramMap.get(USER_ID);
			if (StringUtils.isBlank(userId)) {
				throw new VortexException("userId不能为空");
			}
			StaffOrganizationTreeWithPermission staffOrganizationTreeWithPermission = StaffOrganizationTreeWithPermission.getInstance();
			staffOrganizationTreeWithPermission.reloadDeptOrgStaffTree(paramMap);
			data = treeService.generateJsonCheckboxTree(staffOrganizationTreeWithPermission, false);

		} catch (Exception e) {
			e.printStackTrace();
			result = ManagementConstant.REST_RESULT_FAIL;
			msg = e.getMessage();
			data = null;

			logger.error(msg, e);
		} finally {
			RestResultDto rst = new RestResultDto();
			rst.setResult(result);
			rst.setMsg(msg);
			rst.setData(data);
			return rst;
		}
	}

	/**
	 * 同步人员(分页)
	 * 
	 * @param tenantId
	 * @param lastSyncTime
	 *            传0 或者不传 则查全部
	 * @param pageSize
	 * @param pageNo
	 *            从1开始
	 * 
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "syncStaffsByPage" + ManagementConstant.PERMISSION_SUFFIX_READ, method = { RequestMethod.GET, RequestMethod.POST })
	public RestResultDto syncStaffsByPage(HttpServletRequest request) throws Exception {
		String msg = null;
		Integer result = ManagementConstant.REST_RESULT_SUCC;
		Object data = null;
		JsonMapper mapper = new JsonMapper();
		try {
			Map<String, Object> paramMap = jm.fromJson(SpringmvcUtils.getParameter(ManagementConstant.REST_PMS), Map.class);
			if (MapUtils.isEmpty(paramMap)) {
				throw new VortexException("参数不能为空");
			}
			String tenantId = (String) paramMap.get(TENANT_ID);
			if (StringUtils.isEmpty(tenantId)) {
				throw new VortexException("tenantId不能为空");
			}
			Integer pageSize = (Integer) paramMap.get(PAGESIZE);
			if (pageSize == null) {
				throw new VortexException("参数pageSize不能为空");
			}
			Integer pageNumber = (Integer) paramMap.get("pageNo");
			if (pageNumber == null) {
				throw new VortexException("参数pageNo不能为空");
			}
			// 页码请求从1开始，数据库从0开始
			pageNumber = pageNumber - 1;
			Pageable pageable = new PageRequest(pageNumber, pageSize, Direction.DESC, "createTime");
			Map<String, Object> mapValue = Maps.newHashMap();
			Page<CloudStaffDto> pageList = cloudStaffService.syncStaffsByPage(pageable, paramMap);
			mapValue.put("total", pageList.getTotalElements());
			mapValue.put("rows", pageList.getContent());
			data = mapValue;
			msg = "查询成功！";
		} catch (Exception e) {
			e.printStackTrace();
			result = ManagementConstant.REST_RESULT_FAIL;
			msg = e.getMessage();
			data = null;
			logger.error(msg, e);
		} finally {
			RestResultDto rst = new RestResultDto();
			rst.setResult(result);
			rst.setMsg(msg);
			rst.setData(data);
			return rst;
		}
	}

	/**
	 * @Title: getStaffInfoByUserIds @Description: 根据userIds获取人员基本信息 @return
	 *         RestResultDto @throws
	 */
	@RequestMapping(value = "getStaffInfoByUserIds" + ManagementConstant.PERMISSION_SUFFIX_READ, method = { RequestMethod.GET, RequestMethod.POST })
	public RestResultDto getStaffInfoByUserIds(HttpServletRequest request) throws Exception {
		String msg = null;
		Integer result = ManagementConstant.REST_RESULT_SUCC;
		Object data = null;
		JsonMapper mapper = new JsonMapper();
		try {
			Map<String, Object> paramMap = jm.fromJson(SpringmvcUtils.getParameter(ManagementConstant.REST_PMS), Map.class);
			if (MapUtils.isEmpty(paramMap)) {
				throw new VortexException("参数不能为空");
			}
			List<String> ids = (List<String>) paramMap.get(IDS);
			data = cloudStaffService.getStaffInfoByUserIds(ids);
			msg = "查询成功！";
		} catch (Exception e) {
			e.printStackTrace();
			result = ManagementConstant.REST_RESULT_FAIL;
			msg = e.getMessage();
			data = null;
			logger.error(msg, e);
		} finally {
			RestResultDto rst = new RestResultDto();
			rst.setResult(result);
			rst.setMsg(msg);
			rst.setData(data);
			return rst;
		}
	}

	/**
	 * @Title: getWillManStaffUser @Description: 获取是意愿者，并有用户的人员 @return
	 *         RestResultDto @throws
	 */
	@RequestMapping(value = "getWillManStaffUser" + ManagementConstant.PERMISSION_SUFFIX_READ, method = { RequestMethod.GET, RequestMethod.POST })
	public RestResultDto getWillManStaffUser(HttpServletRequest request) throws Exception {
		String msg = null;
		Integer result = ManagementConstant.REST_RESULT_SUCC;
		Object data = null;
		JsonMapper mapper = new JsonMapper();
		try {
			Map<String, Object> paramMap = jm.fromJson(SpringmvcUtils.getParameter(ManagementConstant.REST_PMS), Map.class);
			if (MapUtils.isEmpty(paramMap)) {
				throw new VortexException("参数不能为空");
			}
			String tenantId = (String) paramMap.get(TENANT_ID);
			if (StringUtils.isEmpty(tenantId)) {
				throw new VortexException("tenantId不能为空");
			}
			String name = (String) paramMap.get("name");
			String willCheckDivisionId = (String) paramMap.get("willCheckDivisionId");
			Integer num = (Integer) paramMap.get("num");
			data = cloudStaffService.getWillManStaffUser(tenantId, name, willCheckDivisionId, num);
			msg = "查询成功！";
		} catch (Exception e) {
			e.printStackTrace();
			result = ManagementConstant.REST_RESULT_FAIL;
			msg = e.getMessage();
			data = null;
			logger.error(msg, e);
		} finally {
			RestResultDto rst = new RestResultDto();
			rst.setResult(result);
			rst.setMsg(msg);
			rst.setData(data);
			return rst;
		}
	}

	/**
	 * 根据人员code获取人员
	 * 
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "getStaffByCode", method = { RequestMethod.GET, RequestMethod.POST })
	public RestResultDto getStaffByCode(HttpServletRequest request) throws Exception {
		String msg = null;
		Integer result = null;
		Object data = null;
		String exception = null;
		RestResultDto restResultDto = new RestResultDto();

		try {
			Map<String, String> paramMap = jm.fromJson(SpringmvcUtils.getParameter(ManagementConstant.REST_PMS), Map.class);
			if (MapUtils.isEmpty(paramMap)) {
				throw new VortexException("参数不能为空");
			}

			String code = paramMap.get(STAFF_CODE);
			String tenantCode = paramMap.get(TEANANT_CODE);
			if (StringUtils.isBlank(code)) {
				throw new VortexException("请传入参数：" + STAFF_CODE);
			}

			if (StringUtils.isBlank(tenantCode)) {
				throw new VortexException("请传入参数：" + TEANANT_CODE);
			}

			// 查询人员信息
			data = cloudStaffService.getStaffByCodeAndTenantCode(code, tenantCode);

			if (data != null) {
				msg = "成功获取用户信息";
				result = ManagementConstant.REST_RESULT_SUCC;
			} else {
				msg = "未获取到用户信息";
				result = ManagementConstant.REST_RESULT_FAIL;
			}
		} catch (Exception e) {
			e.printStackTrace();
			result = ManagementConstant.REST_RESULT_FAIL;
			msg = "获取用户信息出错";
			data = null;
			exception = e.getMessage();
			logger.error(msg, e);
		} finally {
			restResultDto.setData(data);
			restResultDto.setMsg(msg);
			restResultDto.setException(exception);
			restResultDto.setResult(result);
		}

		return restResultDto;
	}

	/**
	 * 查询人员分页 <br>
	 * page: 页， 从0开始 <br>
	 * rows: 行，默认10 <br>
	 * name:姓名（模糊匹配） <br>
	 * tenantId:租户ID （可选）
	 * 
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "getStaffPageList", method = { RequestMethod.GET, RequestMethod.POST })
	public RestResultDto getStaffPageList() throws Exception {
		String msg = null;
		Integer result = ManagementConstant.REST_RESULT_SUCC;
		Object data = null;
		JsonMapper mapper = new JsonMapper();
		try {

			Pageable pageable = null;
			List<SearchFilter> searchFilters = Lists.newArrayList();

			Map<String, Object> paramMap = jm.fromJson(SpringmvcUtils.getParameter(ManagementConstant.REST_PMS), Map.class);

			Integer page = (Integer) (MapUtils.isNotEmpty(paramMap) && paramMap.get(PAGE) != null ? paramMap.get(PAGE) : 0);
			Integer rows = (Integer) (MapUtils.isNotEmpty(paramMap) && paramMap.get(ROWS) != null ? paramMap.get(ROWS) : 10);
			pageable = new PageRequest(page, rows, Direction.ASC, "orderIndex");
			CloudStaffSearchDto searchDto = new CloudStaffSearchDto();
			// 参数不为空的时候取出所有的参数
			if (MapUtils.isNotEmpty(paramMap)) {

				String name = (String) paramMap.get(NAME);
				String tenantId = (String) paramMap.get(TENANT_ID);
				if (StringUtils.isNotBlank(name)) {
					searchDto.setName(name);
				}
				if (StringUtils.isNotBlank(tenantId)) {
					searchDto.setTenantId(tenantId);
				}
			}

			Page<CloudStaffDto> cloudstaffs = cloudStaffService.findPageListBySearchDto(pageable, searchDto);

			DataStore<CloudStaffDto> dataStore = new DataStore<>();
			if (CollectionUtils.isNotEmpty(cloudstaffs.getContent())) {
				dataStore.setRows(cloudstaffs.getContent());
				dataStore.setTotal(cloudstaffs.getTotalElements());
			}

			data = dataStore;
		} catch (Exception e) {
			e.printStackTrace();
			result = ManagementConstant.REST_RESULT_FAIL;
			msg = e.getMessage();
			data = null;
			logger.error(msg, e);
		} finally {
			RestResultDto rst = new RestResultDto();
			rst.setResult(result);
			rst.setMsg(msg);
			rst.setData(data);
			return rst;
		}
	}

	@RequestMapping(value = "listStaff", method = { RequestMethod.GET, RequestMethod.POST })
	public RestResultDto<List<StaffDto>> listStaff(HttpServletRequest request) {
		try {
			Map<String, String> paramMap = jm.fromJson(SpringmvcUtils.getParameter(ManagementConstant.REST_PMS), Map.class);
			return RestResultDto.newSuccess(this.cloudStaffService.listStaff(paramMap), "查询成功");
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("TenantDeptOrgRestNpController.listStaff()", e);
			return RestResultDto.newFalid("查询失败", e.getMessage());
		}
	}
}
