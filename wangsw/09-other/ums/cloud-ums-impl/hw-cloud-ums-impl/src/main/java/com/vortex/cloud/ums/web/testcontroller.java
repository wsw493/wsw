package com.vortex.cloud.ums.web;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vortex.cloud.ums.dataaccess2.service.ICopyFunctionAcrossDatabaseService;

  @RestController      
@RequestMapping("test")
public class testcontroller {
	@Resource
	ICopyFunctionAcrossDatabaseService copyFunctionAcrossDatabaseService;

	@RequestMapping("test1")
	public void name() throws Exception {
		//copyFunctionAcrossDatabaseService.updateMenu();
	}

}
