package com.vortex.cloud.ums.web;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.collect.Lists;
import com.vortex.cloud.ums.dataaccess.service.ICloudMenuService;
import com.vortex.cloud.ums.dataaccess.service.ICloudStaffService;
import com.vortex.cloud.ums.dataaccess.service.ICloudSystemService;
import com.vortex.cloud.ums.dataaccess.service.ITenantService;
import com.vortex.cloud.ums.dto.CloudMenuDto;
import com.vortex.cloud.ums.dto.CloudSystemDto;
import com.vortex.cloud.ums.dto.TenantDto;
import com.vortex.cloud.ums.model.CloudStaff;
import com.vortex.cloud.ums.model.CloudSystem;
import com.vortex.cloud.ums.util.web.BaseController;
import com.vortex.cloud.vfs.data.dto.RestResultDto;
@SuppressWarnings("all")
@RestController      
@RequestMapping("cloud/management/inital")
public class InitalController extends BaseController {
	private static final Logger logger = LoggerFactory.getLogger(InitalController.class);
	private static final String FORE_DYNAMIC_SUFFIX = com.vortex.cloud.ums.support.ManagementConstant.PERMISSION_SUFFIX_SA;
	private static final String BACK_DYNAMIC_SUFFIX = com.vortex.cloud.ums.support.ManagementConstant.PERMISSION_SUFFIX_SA;
	
	@Resource
	private ITenantService tenantService;
	
	@Resource
	private ITenantService iTenantService;
	
	@Resource
	private ICloudSystemService iCloudSystemService;
	
	@Resource
	private ICloudStaffService iCloudStaffService;
	
	@Resource
	private ICloudMenuService iCloudMenuService;
	
	
	@RequestMapping(value = "addTenant" + FORE_DYNAMIC_SUFFIX, method = RequestMethod.POST)
	public RestResultDto<Boolean> addTenant(HttpServletRequest request, HttpServletResponse response) {
		try {
			List<TenantDto> tenantDtoList = Lists.newArrayList();
			TenantDto tenantDto = null;
			for (int i = 0; i < 1000; i++) {
				tenantDto = new TenantDto();
				tenantDto.setTenantName("性能测试租户" + i);
				tenantDto.setTenantCode("test" + i);
				tenantDto.setUserName("test" + i);
				tenantDto.setPassword("123456");
				tenantDto.setDivisionId("fa3f59adea7111e59c70b82a72d4c631");
				
				tenantService.saveTenant(tenantDto);
			}
			
			
			return RestResultDto.newSuccess(true);
		} catch (Exception e) {
			return RestResultDto.newFalid("新增租户出错", e.getMessage());
		}

	}
	
	@RequestMapping(value = "addTenantSystem" + FORE_DYNAMIC_SUFFIX, method = RequestMethod.POST)
	public RestResultDto<Boolean> addTenantSystem(HttpServletRequest request, HttpServletResponse response) {
		try {
			//获取所有租户
			Object tenant = iTenantService.getAllTenant();
			List<Map<String, String>> tenantList = (List<Map<String, String>>)tenant;
			for (Map<String, String> ddd : tenantList) {
				for (int i = 0; i < 10; i++) {
					CloudSystemDto cs = new CloudSystemDto();
					cs.setSystemCode("yw" + ddd.get("tenantCode") + i);
					cs.setSystemName("业务系统：" + ddd.get("tenantName") + i);
					cs.setWebsite("http://baidu.com");
					cs.setTenantId(ddd.get("tenantId"));
					cs.setMapType("BMAP");
					cs.setSystemType(2);
					cs.setUserName("yw" + ddd.get("tenantCode") + i);
					cs.setPassword("123456");
					iCloudSystemService.saveCloudSystem(cs);
				}
			}
			return RestResultDto.newSuccess(true);
		} catch (Exception e) {
			return RestResultDto.newFalid("新增租户业务系统出错", e.getMessage());
		}

	}
	@RequestMapping(value = "addTenantStaff" + FORE_DYNAMIC_SUFFIX, method = RequestMethod.POST)
	public RestResultDto<Boolean> addTenantStaff(HttpServletRequest request, HttpServletResponse response) {
		try {
			//获取所有租户
			Object tenant = iTenantService.getAllTenant();
			List<Map<String, String>> tenantList = (List<Map<String, String>>)tenant;
			List<CloudStaff> cloudStaffList = new ArrayList<CloudStaff>();
			for (Map<String, String> ddd : tenantList) {
				for (int i = 0; i < 5000; i++) {
					CloudStaff dto = new CloudStaff();
					dto.setCode("13s32taff"  + ddd.get("tenantId") + i);
					dto.setTenantId(ddd.get("tenantId"));
					dto.setDepartmentId("12");
					dto.setName("staff"  + ddd.get("tenantName") + i);
					cloudStaffList.add(dto);
				}
				iCloudStaffService.save(cloudStaffList);
				cloudStaffList.clear();
			}
			return RestResultDto.newSuccess(true);
		} catch (Exception e) {
			return RestResultDto.newFalid("新增租户业务系统出错", e.getMessage());
		}

	}
	@RequestMapping(value = "addTenantMenu" + FORE_DYNAMIC_SUFFIX, method = RequestMethod.POST)
	public RestResultDto<Boolean> addTenantMenu(HttpServletRequest request, HttpServletResponse response) {
		try {
			//获取所有系统
			List<CloudSystem>  systemList = iCloudSystemService.findAll();
			for (CloudSystem cs : systemList) {
				for (int i = 0; i < 20; i++ ) {
					CloudMenuDto dto = new CloudMenuDto();
					dto.setCode("menu" + cs.getTenantId() + i);
					dto.setName("123123");
					dto.setOrderIndex(1);
					dto.setParentId("-1");
					dto.setIsHidden(0);
					dto.setIsControlled(0);
					dto.setSystemId(cs.getId());
					iCloudMenuService.saveBusinessSystem(dto);
				}
			}
			
			return RestResultDto.newSuccess(true);
		} catch (Exception e) {
			return RestResultDto.newFalid("新增租户业务系统出错", e.getMessage());
		}

	}
	@RequestMapping(value = "addTenantFunctionGroup" + FORE_DYNAMIC_SUFFIX, method = RequestMethod.POST)
	public RestResultDto<Boolean> addTenantFunctionGroup(HttpServletRequest request, HttpServletResponse response) {
		try {
			
			
			return RestResultDto.newSuccess(true);
		} catch (Exception e) {
			return RestResultDto.newFalid("新增租户业务系统出错", e.getMessage());
		}

	}
	
	

}
