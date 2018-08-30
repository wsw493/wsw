package com.vortex.cloud.ums.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

  @RestController      
@RequestMapping("/cloud/management/filter")
public class PermissionFilterController {
	private static final String ERR_PAGE = "permissionErr";

	@RequestMapping(value = "err")
	public String goToQuery(HttpServletRequest request, HttpServletResponse response) {
		System.out.println(request.getAttribute("errMsg"));
		return ERR_PAGE;
	}
}
