package com.vortex.cloud.ums.web.rest.np;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JavaType;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.vortex.cloud.ums.dataaccess.service.AreaOrLineAlarm;
import com.vortex.cloud.ums.dataaccess.service.ICloudOrganizationService;
import com.vortex.cloud.ums.dataaccess.service.ICloudUserService;
import com.vortex.cloud.ums.dataaccess.service.ITenantDivisionService;
import com.vortex.cloud.ums.dataaccess.service.IWorkElementService;
import com.vortex.cloud.ums.dataaccess.service.IWorkElementTypeService;
import com.vortex.cloud.ums.dataaccess.service.impl.AreaOrLineAlarmImpl;
import com.vortex.cloud.ums.dto.WorkElementDto;
import com.vortex.cloud.ums.enums.SharpTypeEnum;
import com.vortex.cloud.ums.model.WorkElement;
import com.vortex.cloud.ums.model.WorkElementType;
import com.vortex.cloud.ums.model.gps.Position;
import com.vortex.cloud.ums.support.ManagementConstant;
import com.vortex.cloud.ums.tree.OrganizationTree;
import com.vortex.cloud.ums.tree.WorkElementTree;
import com.vortex.cloud.ums.util.support.Constants;
import com.vortex.cloud.ums.util.tree.TreeService;
import com.vortex.cloud.vfs.common.exception.VortexException;
import com.vortex.cloud.vfs.common.mapper.JsonMapper;
import com.vortex.cloud.vfs.common.web.MediaTypes;
import com.vortex.cloud.vfs.common.web.springmvc.SpringmvcUtils;
import  com.vortex.cloud.vfs.data.dto.LoginReturnInfoDto;
import com.vortex.cloud.vfs.data.dto.RestResultDto;
import com.vortex.cloud.vfs.data.support.SearchFilter;
import com.vortex.cloud.vfs.data.support.SearchFilter.Operator;










/**
 * 图元 rest controller
 * 
 * @author SonHo
 *
 */
  @RestController      
@RequestMapping("cloud/management/rest/np/workElement")
public class WorkElementRestNpController {
	private static final Logger logger = LoggerFactory.getLogger(WorkElementRestNpController.class);
	private JsonMapper jm = new JsonMapper();
	public static int LINEOFFSET = 20;
	private static final Object TENANT_ID = "tenantId";
	private static final String SYNCTIME = "syncTime";
	private static final String PAGESIZE = "pageSize";
	private static final String PAGENUMBER = "pageNumber";

	@Resource
	private IWorkElementService workElementService;
	@Resource
	private ICloudOrganizationService cloudOrganizationService;
	@Resource
	private IWorkElementTypeService workElementTypeService;
	@Resource
	private ICloudUserService cloudUserService;

	@Resource
	private ITenantDivisionService tenantDivisionService;

	@Resource
	private TreeService treeService;

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "queryList" + Constants.BACK_DYNAMIC_SUFFIX, produces = MediaTypes.JSON_UTF_8)
	public RestResultDto queryList(HttpServletRequest request) {
		RestResultDto restResultDto = new RestResultDto();
		try {
			Map<String, Object> paramMap = jm.fromJson(SpringmvcUtils.getParameter(ManagementConstant.REST_PMS), Map.class);
			if (MapUtils.isEmpty(paramMap)) {
				throw new VortexException("参数不能为空");
			}
			String tenantId = (String) paramMap.get("tenantId");
			if (StringUtils.isEmpty(tenantId)) {
				throw new VortexException("tenantId不能为空");
			}
			String typeCode = (String) paramMap.get("typeCode");
			List<SearchFilter> filters = new ArrayList<SearchFilter>();
			filters.add(new SearchFilter("tenantId", Operator.EQ, tenantId));
			List<SearchFilter> typeFilters = new ArrayList<SearchFilter>();
			typeFilters.add(new SearchFilter("tenantId", Operator.EQ, tenantId));
			if (StringUtils.isNotEmpty(typeCode)) {
				typeFilters.add(new SearchFilter("code", Operator.LIKE, typeCode));
			}
			String shape = (String) paramMap.get("shape");
			if (StringUtils.isNotEmpty(shape)) {
				typeFilters.add(new SearchFilter("shape", Operator.EQ, shape));
			}

			String typeCodes = (String) paramMap.get("typeCodes");
			if (StringUtils.isNotEmpty(typeCodes)) {
				typeFilters.add(new SearchFilter("code", Operator.IN, typeCodes.split(",")));
			}
			String typeIds = (String) paramMap.get("typeIds");
			if (StringUtils.isNotEmpty(typeIds)) {
				typeFilters.add(new SearchFilter("id", Operator.IN, typeIds.split(",")));
			}
			List<WorkElementType> types = workElementTypeService.findListByFilter(typeFilters, null);
			if (CollectionUtils.isNotEmpty(types)) {
				List<String> idList = new ArrayList<String>();
				for (WorkElementType workElementType : types) {
					idList.add(workElementType.getId());
				}
				String[] ids = idList.toArray(new String[idList.size()]);
				filters.add(new SearchFilter("workElementTypeId", Operator.IN, ids));
			} else {
				restResultDto.setData(new ArrayList<WorkElement>());
				restResultDto.setResult(RestResultDto.RESULT_SUCC);
				restResultDto.setMsg("查询成功");
				return restResultDto;
			}
			String userId = (String) paramMap.get("userId");
			if (StringUtils.isNotEmpty(userId)) {
				filters.add(new SearchFilter("userId", Operator.EQ, userId));
			}
			List<WorkElement> list = workElementService.findListByFilter(filters, null);

			restResultDto.setData(workElementService.transferModelToDto(list));
			restResultDto.setResult(RestResultDto.RESULT_SUCC);
			restResultDto.setMsg("查询成功");
		} catch (Exception e) {
			restResultDto.setResult(RestResultDto.RESULT_FAIL);
			restResultDto.setMsg("查询失败");
			restResultDto.setException(e.getMessage());
			logger.error("WorkElementTypeRestNpController.queryList", e);
		}
		return restResultDto;
	}
	/**
	 * 同步图元
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "syncWeByPage" + ManagementConstant.PERMISSION_SUFFIX_READ, method = { RequestMethod.GET, RequestMethod.POST })
	public RestResultDto syncWeByPage(HttpServletRequest request) throws Exception {
		String msg = null;
		Integer result = ManagementConstant.REST_RESULT_SUCC;
		Object data = null;
		JsonMapper mapper = new JsonMapper();
		try {
			Map<String, Object> paramMap = jm.fromJson(SpringmvcUtils.getParameter(ManagementConstant.REST_PMS), Map.class);
			if (MapUtils.isEmpty(paramMap)) {
				throw new VortexException("参数不能为空");
			}
			String tenantId = (String)paramMap.get(TENANT_ID);
			if (StringUtils.isEmpty(tenantId)) {
				throw new VortexException("tenantId不能为空");
			}
			String strSyncTime = String.valueOf(paramMap.get(SYNCTIME));
			Long syncTime =Long.valueOf(strSyncTime);
			
			if (syncTime == null) {
				throw new VortexException("参数syncTime不能为空");
			}
			
			Integer pageSize = (Integer)paramMap.get(PAGESIZE);
			if (pageSize == null) {
				throw new VortexException("参数pageSize不能为空");
			}
			Integer pageNumber = (Integer)paramMap.get(PAGENUMBER);
			if (pageNumber == null) {
				throw new VortexException("参数pageNumber不能为空");
			}
			data = workElementService.syncWeByPage(tenantId, syncTime, pageSize, pageNumber);
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
	 * 通过图元类型id以及图元相关数据保存图元
	 * 
	 * @param request
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "save" + Constants.BACK_DYNAMIC_SUFFIX, produces = MediaTypes.JSON_UTF_8)
	public RestResultDto save(HttpServletRequest request) {
		RestResultDto restResultDto = new RestResultDto();
		WorkElementDto dto = new WorkElementDto();
		try {
			Map<String, Object> paramMap = jm.fromJson(SpringmvcUtils.getParameter(ManagementConstant.REST_PMS), Map.class);
			if (MapUtils.isEmpty(paramMap)) {
				throw new VortexException("参数不能为空");
			}
			String tenantId = (String) paramMap.get("tenantId");
			if (StringUtils.isEmpty(tenantId)) {
				throw new VortexException("tenantId不能为空");
			} else {
				dto.setTenantId(tenantId);
			}

			String shape = (String) paramMap.get("shape");
			if (StringUtils.isEmpty(shape)) {// shape非空判断
				throw new VortexException("shape不能为空");
			} else {
				if (StringUtils.isEmpty(SharpTypeEnum.getValueByKey(shape))) {// shape合法性判断
					throw new VortexException("shape不存在");
				} else {
					String workElementTypeId = (String) paramMap.get("workElementTypeId");

					if (StringUtils.isEmpty(workElementTypeId)) {//
						throw new VortexException("workElementTypeId图元类型id不能为空");
					} else {
						WorkElementType type = workElementTypeService.findOne(workElementTypeId);
						if (null == type) {
							throw new VortexException("根据workElementTypeId未能找到对应的图元");
						} else {
							dto.setWorkElementTypeId(type.getId());
							if (!StringUtils.equals(type.getShape(), shape)) {
								throw new VortexException("图元类型编码，名称 所查到的shape与传入的shape不符");
							} else {
								dto.setShape(shape);
								if (StringUtils.equals(SharpTypeEnum.CIRCLE.getKey(), shape)) {
									Double radius = (Double) paramMap.get("radius");
									if (radius == null) {
										throw new VortexException("半径radius不能为空");
									} else {
										dto.setRadius(radius);
									}
								} else if (StringUtils.equals(SharpTypeEnum.LINE.getKey(), shape)) {
									Double length = (Double) paramMap.get("length");
									if (length == null) {
										throw new VortexException("长度length不能为空");
									} else {
										dto.setLength(length);
									}
								} else if (StringUtils.equals(SharpTypeEnum.RECTANGLE.getKey(), shape) || StringUtils.equals(SharpTypeEnum.POLYGON.getKey(), shape)) {
									Double area = (Double) paramMap.get("area");
									if (area == null) {
										throw new VortexException("面积area不能为空");
									} else {
										dto.setArea(area);
									}
								}
							}
						}
					}

				}
			}
			String code = (String) paramMap.get("code");
			if (StringUtils.isEmpty(code)) {
				throw new VortexException("code不能为空");
			} else {
				if (workElementService.isParamNameExists(null, "code", code, tenantId)) {
					throw new VortexException("code已存在");
				} else {
					dto.setCode(code);
				}
			}

			String name = (String) paramMap.get("name");
			if (StringUtils.isEmpty(name)) {
				throw new VortexException("name不能为空");
			} else {
				if (workElementService.isParamNameExists(null, "name", name, tenantId)) {
					throw new VortexException("name已存在");
				} else {
					dto.setName(name);
				}
			}

			String params = (String) paramMap.get("params");
			if (StringUtils.isEmpty(params)) {
				throw new VortexException("params经纬度序列不能为空");
			} else {
				dto.setParams(params);
			}
			String paramsDone = (String) paramMap.get("paramsDone");
			if (StringUtils.isEmpty(paramsDone)) {
				throw new VortexException("paramsDone偏转后经纬度序列不能为空");
			} else {
				dto.setParamsDone(paramsDone);
			}
			String color = (String) paramMap.get("color");
			if (StringUtils.isNotEmpty(color)) {
				dto.setColor(color);
			}
			String departmentId = (String) paramMap.get("departmentId");
			if (StringUtils.isNotEmpty(departmentId)) {
				if (cloudOrganizationService.getDepartmentOrOrgById(departmentId, null) == null) {
					throw new VortexException("部门或者机构id不存在");
				} else {
					dto.setDepartmentId(departmentId);
				}
			}
			String divisionId = (String) paramMap.get("divisionId");
			if (StringUtils.isNotEmpty(divisionId)) {
				if (tenantDivisionService.getById(divisionId) == null) {
					throw new VortexException("行政区划不存在");
				} else {
					dto.setDivisionId(divisionId);
				}
			}
			String description = (String) paramMap.get("description");
			if (StringUtils.isNotEmpty(description)) {
				dto.setDescription(description);
			}

			String userId = (String) paramMap.get("userId");
			if (StringUtils.isNotEmpty(userId)) {
				if (cloudUserService.findOne(userId) == null) {
					throw new VortexException("用户userId不存在");
				} else {
					dto.setUserId(userId);
				}
			}
			restResultDto.setData(workElementService.saveWorkElement(dto));
			restResultDto.setResult(RestResultDto.RESULT_SUCC);
			restResultDto.setMsg("保存成功");
		} catch (Exception e) {
			restResultDto.setResult(RestResultDto.RESULT_FAIL);
			restResultDto.setMsg("保存失败");
			restResultDto.setException(e.getMessage());
			logger.error("WorkElementTypeRestNpController.save", e);
		}
		return restResultDto;
	}

	/**
	 * 通过图元相关信息以及图元类型相关信息保存图元，若图元类型相关信息不存在，则同步保存图元类型
	 * 
	 * @param request
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "batchSave" + Constants.BACK_DYNAMIC_SUFFIX, produces = MediaTypes.JSON_UTF_8)
	public RestResultDto batchSave(HttpServletRequest request) {
		RestResultDto restResultDto = new RestResultDto();
		WorkElementDto dto = new WorkElementDto();
		try {
			Map<String, Object> paramMap = jm.fromJson(SpringmvcUtils.getParameter(ManagementConstant.REST_PMS), Map.class);
			if (MapUtils.isEmpty(paramMap)) {
				throw new VortexException("参数不能为空");
			}
			String tenantId = (String) paramMap.get("tenantId");
			if (StringUtils.isEmpty(tenantId)) {
				throw new VortexException("tenantId不能为空");
			} else {
				dto.setTenantId(tenantId);
			}

			String shape = (String) paramMap.get("shape");
			if (StringUtils.isEmpty(shape)) {// shape非空判断
				throw new VortexException("shape不能为空");
			} else {
				if (StringUtils.isEmpty(SharpTypeEnum.getValueByKey(shape))) {// shape合法性判断
					throw new VortexException("shape不存在");
				} else {
					// String workElementTypeId = (String)
					// paramMap.get("workElementTypeId");
					String typeCode = (String) paramMap.get("typeCode");
					String typeName = (String) paramMap.get("typeName");
					String typeShape = (String) paramMap.get("typeShape");

					if (StringUtils.isEmpty(typeCode) || StringUtils.isEmpty(typeName) || StringUtils.isEmpty(typeShape)) {//
						throw new VortexException("typeCode图元类型编码，typeName图元类型名称,typeShape图元类型外形不能为空");
					} else {
						List<SearchFilter> typeFilters = new ArrayList<SearchFilter>();
						typeFilters.add(new SearchFilter("tenantId", Operator.EQ, tenantId));
						typeFilters.add(new SearchFilter("code", Operator.EQ, typeCode));
						typeFilters.add(new SearchFilter("name", Operator.EQ, typeName));
						List<WorkElementType> list = workElementTypeService.findListByFilter(typeFilters, null);
						WorkElementType type = null;
						if (CollectionUtils.isEmpty(list)) {// 图元类型数据不存在，则新增图元类型
							WorkElementType temp = new WorkElementType();
							temp.setTenantId(tenantId);
							temp.setCode(typeCode);
							temp.setName(typeName);
							temp.setShape(typeShape);
							temp.setInfo((String) paramMap.get("typeInfo"));
							temp.setDepartmentId((String) paramMap.get("typeDepartmentId"));
							temp.setOrderIndex((Integer) paramMap.get("typeOrderIndex"));
							type = workElementTypeService.save(temp);
						} else {
							type = list.get(0);
						}
						dto.setWorkElementTypeId(type.getId());
						if (!StringUtils.equals(type.getShape(), shape)) {
							throw new VortexException("图元类型编码，名称 所查到的shape与传入的shape不符");
						} else {
							dto.setShape(shape);
							if (StringUtils.equals(SharpTypeEnum.CIRCLE.getKey(), shape)) {
								Double radius = (Double) paramMap.get("radius");
								if (radius == null) {
									throw new VortexException("半径radius不能为空");
								} else {
									dto.setRadius(radius);
								}
							} else if (StringUtils.equals(SharpTypeEnum.LINE.getKey(), shape)) {
								Double length = (Double) paramMap.get("length");
								if (length == null) {
									throw new VortexException("长度length不能为空");
								} else {
									dto.setLength(length);
								}
							} else if (StringUtils.equals(SharpTypeEnum.RECTANGLE.getKey(), shape) || StringUtils.equals(SharpTypeEnum.POLYGON.getKey(), shape)) {
								Double area = (Double) paramMap.get("area");
								if (area == null) {
									throw new VortexException("面积area不能为空");
								} else {
									dto.setArea(area);
								}
							}
						}
					}

				}
			}
			String code = (String) paramMap.get("code");
			if (StringUtils.isEmpty(code)) {
				throw new VortexException("code不能为空");
			} else {
				if (workElementService.isParamNameExists(null, "code", code, tenantId)) {
					throw new VortexException("code已存在");
				} else {
					dto.setCode(code);
				}
			}

			String name = (String) paramMap.get("name");
			if (StringUtils.isEmpty(name)) {
				throw new VortexException("name不能为空");
			} else {
				if (workElementService.isParamNameExists(null, "name", name, tenantId)) {
					throw new VortexException("name已存在");
				} else {
					dto.setName(name);
				}
			}

			String params = (String) paramMap.get("params");
			if (StringUtils.isEmpty(params)) {
				throw new VortexException("params经纬度序列不能为空");
			} else {
				dto.setParams(params);
			}
			String paramsDone = (String) paramMap.get("paramsDone");
			if (StringUtils.isEmpty(paramsDone)) {
				throw new VortexException("paramsDone偏转后经纬度序列不能为空");
			} else {
				dto.setParamsDone(paramsDone);
			}
			String color = (String) paramMap.get("color");
			if (StringUtils.isNotEmpty(color)) {
				dto.setColor(color);
			}
			String departmentId = (String) paramMap.get("departmentId");
			if (StringUtils.isNotEmpty(departmentId)) {
				if (cloudOrganizationService.getDepartmentOrOrgById(departmentId, null) == null) {
					throw new VortexException("部门或者机构id不存在");
				} else {
					dto.setDepartmentId(departmentId);
				}
			}
			String divisionId = (String) paramMap.get("divisionId");
			if (StringUtils.isNotEmpty(divisionId)) {
				if (tenantDivisionService.getById(divisionId) == null) {
					throw new VortexException("行政区划不存在");
				} else {
					dto.setDivisionId(divisionId);
				}
			}
			String description = (String) paramMap.get("description");
			if (StringUtils.isNotEmpty(description)) {
				dto.setDescription(description);
			}

			String userId = (String) paramMap.get("userId");
			if (StringUtils.isNotEmpty(userId)) {
				if (cloudUserService.findOne(userId) == null) {
					throw new VortexException("用户userId不存在");
				} else {
					dto.setUserId(userId);
				}
			}
			restResultDto.setData(workElementService.saveWorkElement(dto));
			restResultDto.setResult(RestResultDto.RESULT_SUCC);
			restResultDto.setMsg("保存成功");
		} catch (Exception e) {
			restResultDto.setResult(RestResultDto.RESULT_FAIL);
			restResultDto.setMsg("保存失败");
			restResultDto.setException(e.getMessage());
			logger.error("WorkElementTypeRestNpController.save", e);
		}
		return restResultDto;
	}

	/**
	 * 修改图元
	 * 
	 * @param request
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "updateSave" + Constants.BACK_DYNAMIC_SUFFIX, produces = MediaTypes.JSON_UTF_8)
	public RestResultDto updateSave(HttpServletRequest request) {
		RestResultDto restResultDto = new RestResultDto();
		try {
			Map<String, Object> paramMap = jm.fromJson(SpringmvcUtils.getParameter(ManagementConstant.REST_PMS), Map.class);
			if (MapUtils.isEmpty(paramMap)) {
				throw new VortexException("参数不能为空");
			}

			// 修改的时候将原先的图元设置为无效，然后重新新增一个图元。
			String id = (String) paramMap.get("id");
			if (StringUtils.isEmpty(id)) {
				throw new VortexException("图元id不能为空");
			}

			WorkElementDto dto = workElementService.getWorkElementById(id);

			String tenantId = (String) paramMap.get("tenantId");
			if (StringUtils.isEmpty(tenantId)) {
				throw new VortexException("tenantId不能为空");
			} else {
				dto.setTenantId(tenantId);
			}

			String shape = (String) paramMap.get("shape");
			if (StringUtils.isEmpty(shape)) {// shape非空判断
				throw new VortexException("shape不能为空");
			} else {
				if (StringUtils.isEmpty(SharpTypeEnum.getValueByKey(shape))) {// shape合法性判断
					throw new VortexException("shape不存在");
				} else {
					String workElementTypeId = (String) paramMap.get("workElementTypeId");
					if (StringUtils.isEmpty(workElementTypeId)) {// workElementTypeId非空判断
						throw new VortexException("workElementTypeId不能为空");
					} else {
						WorkElementType workelemElementType = workElementTypeService.findOne(workElementTypeId);
						if (null == workelemElementType) {// workElementTypeId合法性判断
							throw new VortexException("图元类型workElementTypeId不存在");
						} else {
							dto.setWorkElementTypeId(workElementTypeId);
							if (!StringUtils.equals(workelemElementType.getShape(), shape)) {
								throw new VortexException("图元类型workElementTypeId 所查到的shape与传入的shape不符");
							} else {
								dto.setShape(shape);
								if (StringUtils.equals(SharpTypeEnum.CIRCLE.getKey(), shape)) {
									Double radius = (Double) paramMap.get("radius");
									if (radius == null) {
										throw new VortexException("半径radius不能为空");
									} else {
										dto.setRadius(radius);
									}
								} else if (StringUtils.equals(SharpTypeEnum.LINE.getKey(), shape)) {
									Double length = (Double) paramMap.get("length");
									if (length == null) {
										throw new VortexException("长度length不能为空");
									} else {
										dto.setLength(length);
									}
								} else if (StringUtils.equals(SharpTypeEnum.RECTANGLE.getKey(), shape) || StringUtils.equals(SharpTypeEnum.POLYGON.getKey(), shape)) {
									Double area = (Double) paramMap.get("area");
									if (area == null) {
										throw new VortexException("面积area不能为空");
									} else {
										dto.setArea(area);
									}
								}
							}
						}
					}

				}
			}
			String code = (String) paramMap.get("code");
			if (StringUtils.isEmpty(code)) {
				throw new VortexException("code不能为空");
			} else {
				if (workElementService.isParamNameExists(dto.getId(), "code", code, tenantId)) {
					throw new VortexException("code已存在");
				} else {
					dto.setCode(code);
				}
			}

			String name = (String) paramMap.get("name");
			if (StringUtils.isEmpty(name)) {
				throw new VortexException("name不能为空");
			} else {
				if (workElementService.isParamNameExists(dto.getId(), "name", name, tenantId)) {
					throw new VortexException("name已存在");
				} else {
					dto.setName(name);
				}
			}

			String params = (String) paramMap.get("params");
			if (StringUtils.isEmpty(params)) {
				throw new VortexException("params经纬度序列不能为空");
			} else {
				dto.setParams(params);
			}
			String paramsDone = (String) paramMap.get("paramsDone");
			if (StringUtils.isEmpty(paramsDone)) {
				throw new VortexException("paramsDone偏转后经纬度序列不能为空");
			} else {
				dto.setParamsDone(paramsDone);
			}
			String color = (String) paramMap.get("color");
			if (StringUtils.isNotEmpty(color)) {
				dto.setColor(color);
			}
			String departmentId = (String) paramMap.get("departmentId");
			if (StringUtils.isNotEmpty(departmentId)) {
				if (cloudOrganizationService.getDepartmentOrOrgById(departmentId, null) == null) {
					throw new VortexException("部门或者机构id不存在");
				} else {
					dto.setDepartmentId(departmentId);
				}
			}
			String divisionId = (String) paramMap.get("divisionId");
			if (StringUtils.isNotEmpty(divisionId)) {
				if (tenantDivisionService.getById(divisionId) == null) {
					throw new VortexException("行政区划不存在");
				} else {
					dto.setDivisionId(divisionId);
				}
			}

			String description = (String) paramMap.get("description");
			if (StringUtils.isNotEmpty(description)) {
				dto.setDescription(description);
			}

			restResultDto.setData(workElementService.updateWorkElement(dto));
			restResultDto.setResult(RestResultDto.RESULT_SUCC);
			restResultDto.setMsg("修改成功");
		} catch (Exception e) {
			restResultDto.setResult(RestResultDto.RESULT_FAIL);
			restResultDto.setMsg("修改失败");
			restResultDto.setException(e.getMessage());
			logger.error("WorkElementTypeRestNpController.updateSave", e);
		}
		return restResultDto;
	}

	/**
	 * 根据图元ids获取图元信息
	 * 
	 * @param request
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "findByIds" + Constants.BACK_DYNAMIC_SUFFIX, produces = MediaTypes.JSON_UTF_8)
	public RestResultDto findByIds(HttpServletRequest request) {
		RestResultDto restResultDto = new RestResultDto();
		try {
			Map<String, Object> paramMap = jm.fromJson(SpringmvcUtils.getParameter(ManagementConstant.REST_PMS), Map.class);
			if (MapUtils.isEmpty(paramMap)) {
				throw new VortexException("参数不能为空");
			}
			String tenantId = (String) paramMap.get("tenantId");
			if (StringUtils.isEmpty(tenantId)) {
				throw new VortexException("tenantId不能为空");
			}
			String workElementIds = (String) paramMap.get("workElementIds");
			List<SearchFilter> filters = new ArrayList<SearchFilter>();
			if (StringUtils.isNotEmpty(workElementIds)) {
				filters.add(new SearchFilter("id", Operator.IN, workElementIds.split(",")));
			}
			List<WorkElement> list = workElementService.findListByFilter(filters, null);

			restResultDto.setData(list);
			restResultDto.setResult(RestResultDto.RESULT_SUCC);
			restResultDto.setMsg("查询成功");
		} catch (Exception e) {
			restResultDto.setResult(RestResultDto.RESULT_FAIL);
			restResultDto.setMsg("查询失败");
			restResultDto.setException(e.getMessage());
			logger.error("WorkElementTypeRestNpController.findByIds", e);
		}
		return restResultDto;
	}

	/**
	 * 判断一个点是否在图元内
	 * 
	 * @param request
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "isInWorkElements" + Constants.BACK_DYNAMIC_SUFFIX, produces = MediaTypes.JSON_UTF_8)
	public RestResultDto isInWorkElements(HttpServletRequest request) {
		RestResultDto restResultDto = new RestResultDto();
		try {
			Map<String, Object> paramMap = jm.fromJson(SpringmvcUtils.getParameter(ManagementConstant.REST_PMS), Map.class);
			if (MapUtils.isEmpty(paramMap)) {
				throw new VortexException("参数不能为空");
			}
			String tenantId = (String) paramMap.get("tenantId");
			if (StringUtils.isEmpty(tenantId)) {
				throw new VortexException("tenantId不能为空");
			}
			String workElementIds = (String) paramMap.get("workElementIds");
			if (StringUtils.isEmpty(workElementIds)) {
				throw new VortexException("workElementIds不能为空");
			}
			String longitudeDone = (String) paramMap.get("longitudeDone");
			if (StringUtils.isEmpty(longitudeDone)) {
				throw new VortexException("longitudeDone偏转后经度不能为空");
			}
			String latitudeDone = (String) paramMap.get("latitudeDone");
			if (StringUtils.isEmpty(latitudeDone)) {
				throw new VortexException("latitudeDone偏转后纬度不能为空");
			}

			List<SearchFilter> filters = new ArrayList<SearchFilter>();
			if (StringUtils.isNotEmpty(tenantId)) {
				filters.add(new SearchFilter("tenantId", Operator.EQ, tenantId));
			}
			if (StringUtils.isNotEmpty(workElementIds)) {
				filters.add(new SearchFilter("id", Operator.IN, workElementIds.split(",")));
			}
			List<WorkElement> list = workElementService.findListByFilter(filters, null);
			if (CollectionUtils.isEmpty(list)) {
				throw new VortexException("根据workElementIds未能找到相应的图元");
			}
			AreaOrLineAlarm areaOrLineAlarm = new AreaOrLineAlarmImpl();
			boolean succ = false;
			String workElementId = "";
			String workElementName = "";
			for (WorkElement element : list) {
				String shape = element.getShape();
				Position position = new Position();
				position.setLatitudeDone(Double.parseDouble(latitudeDone));
				position.setLongitudeDone(Double.parseDouble(longitudeDone));
				double notInMeter;
				try {
					notInMeter = areaOrLineAlarm.overMeter(position, element);
					if (((SharpTypeEnum.POLYGON.getKey().equalsIgnoreCase(shape) || SharpTypeEnum.CIRCLE.getKey().equalsIgnoreCase(shape) || SharpTypeEnum.RECTANGLE.getKey().equalsIgnoreCase(shape)) && notInMeter <= 0) || (SharpTypeEnum.LINE.getKey().equalsIgnoreCase(shape) && notInMeter <= LINEOFFSET)) {
						succ = true;
						workElementId = element.getId();
						workElementName = element.getName();
					}
				} catch (Exception e) {
					e.printStackTrace();
					logger.error("计算点在区段内报错：" + e.toString());
				}

			}
			Map<String, Object> dataMap = new HashMap<String, Object>();
			dataMap.put("isInWorkElements", succ);
			dataMap.put("workElementId", workElementId);
			dataMap.put("workElementName", workElementName);
			restResultDto.setData(dataMap);
			restResultDto.setResult(RestResultDto.RESULT_SUCC);
			restResultDto.setMsg("查询成功");
		} catch (Exception e) {
			restResultDto.setResult(RestResultDto.RESULT_FAIL);
			restResultDto.setMsg("查询失败");
			restResultDto.setException(e.getMessage());
			logger.error("WorkElementTypeRestNpController.isInWorkElements", e);
		}
		return restResultDto;
	}

	/**
	 * 部门+图元 树(远程调用)
	 * 
	 * @return
	 */
	@RequestMapping(value = "loadWorkElementTree" + Constants.BACK_DYNAMIC_SUFFIX, produces = MediaTypes.JSON_UTF_8)
	public RestResultDto loadWorkElementTree() {
		RestResultDto restResultDto = new RestResultDto();

		try {
			String params = SpringmvcUtils.getParameter(ManagementConstant.REST_PMS);
			Assert.isTrue(StringUtils.isNotEmpty(params), "参数不能为空");

			JsonMapper jsonMapper = new JsonMapper();
			JavaType javaType = jsonMapper.contructMapType(Map.class, String.class, String.class);
			Map<String, String> paramMap = jsonMapper.fromJson(params, javaType);

			if (MapUtils.isEmpty(paramMap)) {
				throw new VortexException("参数不能为空");
			}

			String roadName = paramMap.get("roadName");

			String tenantId = paramMap.get("tenantId");
			if (StringUtils.isEmpty(tenantId)) {
				throw new VortexException("tenantId不能为空");
			}
			LoginReturnInfoDto info = new LoginReturnInfoDto();
			info.setTenantId(tenantId);

			String typeCode = paramMap.get("typeCode");
			List<String> typeIdList = new ArrayList<String>();
			if (StringUtils.isNotEmpty(typeCode)) {
				List<SearchFilter> typeFilters = new ArrayList<SearchFilter>();
				typeFilters.add(new SearchFilter("code", Operator.LIKE, typeCode));
				typeFilters.add(new SearchFilter("tenantId", Operator.EQ, info.getTenantId()));
				List<WorkElementType> types = workElementTypeService.findListByFilter(typeFilters, null);

				if (CollectionUtils.isNotEmpty(types)) {
					for (WorkElementType workElementType : types) {
						typeIdList.add(workElementType.getId());
					}
				}
			}

			List<SearchFilter> searchFilter = Lists.newArrayList();
			searchFilter.add(new SearchFilter("tenantId", Operator.EQ, info.getTenantId()));
			if (CollectionUtils.isNotEmpty(typeIdList)) {
				searchFilter.add(new SearchFilter("workElementTypeId", Operator.IN, typeIdList.toArray(new String[typeIdList.size()])));
			}
			if (StringUtils.isNotEmpty(roadName)) {
				searchFilter.add(new SearchFilter("name", Operator.LIKE, roadName));
			}

			OrganizationTree tree = OrganizationTree.getInstance();
			tree.reloadOrganizationElementTree(info, searchFilter);
			String jsonStr = treeService.generateJsonCheckboxTree(tree, false);

			JsonMapper outJsonMapper = new JsonMapper();
			JavaType outJavaType = outJsonMapper.contructMapType(HashMap.class, String.class, Object.class);
			Object data = jsonMapper.fromJson(jsonStr, outJavaType);

			restResultDto.setData(data);
			restResultDto.setResult(RestResultDto.RESULT_SUCC);
			restResultDto.setMsg("成功获取机构+图元树");

		} catch (Exception e) {
			restResultDto.setResult(RestResultDto.RESULT_FAIL);
			restResultDto.setMsg("获取机构+图元树 失败");
			restResultDto.setException(e.getMessage());
			logger.error("SyncRestController.syncCategoryList", e);
		}

		return restResultDto;
	}

	/**
	 * 加载以图元类型为枝干的图元树
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "loadWorkElementTypeTree" + Constants.BACK_DYNAMIC_SUFFIX, produces = MediaTypes.JSON_UTF_8)
	public RestResultDto loadWorkElementTypeTree(HttpServletRequest request) {
		RestResultDto restResultDto = new RestResultDto();
		try {
			JavaType javaType = jm.contructMapType(Map.class, String.class, Object.class);
			Map<String, Object> paramMap = jm.fromJson(SpringmvcUtils.getParameter(ManagementConstant.REST_PMS), javaType);
			if (MapUtils.isEmpty(paramMap)) {
				throw new VortexException("参数不能为空");
			}
			String tenantId = (String) paramMap.get("tenantId");
			if (StringUtils.isEmpty(tenantId)) {
				throw new VortexException("tenantId不能为空");
			}
			WorkElementTree workElementTree = WorkElementTree.getInstance();
			workElementTree.reloadWorkElementTree(paramMap);
			String treeStr = treeService.generateJsonCheckboxTree(workElementTree, false);
			restResultDto.setData(jm.fromJson(treeStr, javaType));
			restResultDto.setResult(RestResultDto.RESULT_SUCC);
			restResultDto.setMsg("查询成功");
		} catch (Exception e) {
			restResultDto.setResult(RestResultDto.RESULT_FAIL);
			restResultDto.setMsg("查询失败");
			restResultDto.setException(e.getMessage());
			logger.error("WorkElementTypeRestNpController.loadWorkElementTypeTree", e);
		}
		return restResultDto;
	}

	@RequestMapping(value = "delete" + Constants.BACK_DYNAMIC_SUFFIX, produces = MediaTypes.JSON_UTF_8)
	public RestResultDto delete(HttpServletRequest request) {
		RestResultDto restResultDto = new RestResultDto();
		try {
			Map<String, Object> paramMap = jm.fromJson(SpringmvcUtils.getParameter(ManagementConstant.REST_PMS), Map.class);
			if (MapUtils.isEmpty(paramMap)) {
				throw new VortexException("参数不能为空");
			}
			String workElementIds = (String) paramMap.get("workElementIds");
			if (StringUtils.isEmpty(workElementIds)) {
				throw new VortexException("workElementIds不能为空");
			}
			workElementService.deleteWorkElements(Lists.newArrayList(workElementIds.split(",")));
			restResultDto.setResult(RestResultDto.RESULT_SUCC);
			restResultDto.setMsg("删除成功");
		} catch (Exception e) {
			restResultDto.setResult(RestResultDto.RESULT_FAIL);
			restResultDto.setMsg("删除失败");
			restResultDto.setException(e.getMessage());
			logger.error("WorkElementTypeRestNpController.delete", e);
		}
		return restResultDto;
	}
	/**
	 * 获取图元列表
	 * 
	 * @param request
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "findList" + Constants.BACK_DYNAMIC_SUFFIX, produces = MediaTypes.JSON_UTF_8)
	public RestResultDto findList(HttpServletRequest request) {
		RestResultDto restResultDto = new RestResultDto();
		try {
			Map<String, Object> paramMap = jm.fromJson(SpringmvcUtils.getParameter(ManagementConstant.REST_PMS), Map.class);
			if (MapUtils.isEmpty(paramMap)) {
				throw new VortexException("参数不能为空");
			}
			String tenantId = (String) paramMap.get("tenantId");
			if (StringUtils.isEmpty(tenantId)) {
				throw new VortexException("tenantId不能为空");
			}
			String divisionId = (String) paramMap.get("divisionId");
			List<SearchFilter> filters = new ArrayList<>();
			if (StringUtils.isNotEmpty(divisionId)) {
				filters.add(new SearchFilter("divisionId", Operator.EQ, divisionId));
			}
			filters.add(new SearchFilter("tenantId", Operator.EQ, tenantId));
			List<WorkElement> list = workElementService.findListByFilter(filters, null);

			restResultDto.setData(list);
			restResultDto.setResult(RestResultDto.RESULT_SUCC);
			restResultDto.setMsg("查询成功");
		} catch (Exception e) {
			restResultDto.setResult(RestResultDto.RESULT_FAIL);
			restResultDto.setMsg("查询失败");
			restResultDto.setException(e.getMessage());
			logger.error("WorkElementTypeRestNpController.findList", e);
		}
		return restResultDto;
	}
	
	/**
	 * 同步图元(分页)
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
	@SuppressWarnings({ "unchecked", "finally" })
	@RequestMapping(value = "syncWorkElementsByPage" + Constants.BACK_DYNAMIC_SUFFIX, produces = MediaTypes.JSON_UTF_8)
	public RestResultDto syncWorkElementsByPage(HttpServletRequest request) {
		String msg = null;
		Integer result = ManagementConstant.REST_RESULT_SUCC;
		Object data = null;
		JsonMapper mapper = new JsonMapper();
		Map<String, Object> mapValue = Maps.newHashMap();
		mapValue.put("total", 0);
		mapValue.put("rows", 0);
		try {
			Map<String, Object> paramMap = jm.fromJson(SpringmvcUtils.getParameter(ManagementConstant.REST_PMS),
					Map.class);
			if (MapUtils.isEmpty(paramMap)) {
				throw new VortexException("参数不能为空");
			}
			String tenantId = (String) paramMap.get(TENANT_ID);
			if (StringUtils.isEmpty(tenantId)) {
				throw new VortexException("tenantId不能为空");
			}
			String typeCode = (String) paramMap.get("typeCode");
			List<SearchFilter> typeFilters = new ArrayList<>();
			typeFilters.add(new SearchFilter("tenantId", Operator.EQ, tenantId));
			if (StringUtils.isNotEmpty(typeCode)) {
				typeFilters.add(new SearchFilter("code", Operator.LIKE, typeCode));
			}
			String shape = (String) paramMap.get("shape");
			if (StringUtils.isNotEmpty(shape)) {
				typeFilters.add(new SearchFilter("shape", Operator.EQ, shape));
			}

			String typeCodes = (String) paramMap.get("typeCodes");
			if (StringUtils.isNotEmpty(typeCodes)) {
				typeFilters.add(new SearchFilter("code", Operator.IN, typeCodes.split(",")));
			}
			String typeIds = (String) paramMap.get("typeIds");
			if (StringUtils.isNotEmpty(typeIds)) {
				typeFilters.add(new SearchFilter("id", Operator.IN, typeIds.split(",")));
			}
			List<WorkElementType> types = workElementTypeService.findListByFilter(typeFilters, null);
			if (CollectionUtils.isEmpty(types)) {// 该租户下无指定类型
				msg = "该租户下无相关图元类型";
				data = mapValue;
				RestResultDto rst = new RestResultDto();
				rst.setResult(result);
				rst.setMsg(msg);
				rst.setData(data);
				return rst;
			}
			List<String> idList = new ArrayList<>();
			for (WorkElementType workElementType : types) {
				idList.add(workElementType.getId());
			}
			paramMap.put("workElementTypeIds", idList);
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

			Page<WorkElement> pageList = workElementService.syncWorkElementsByPage(pageable, paramMap);
			mapValue.put("total", pageList.getTotalElements());
			mapValue.put("rows", workElementService.transferModelToDto(pageList.getContent()));
			data = mapValue;
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
}
