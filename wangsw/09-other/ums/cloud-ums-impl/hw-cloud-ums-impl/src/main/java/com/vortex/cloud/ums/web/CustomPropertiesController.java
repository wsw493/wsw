package com.vortex.cloud.ums.web;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.collect.Maps;
import com.vortex.cloud.ums.util.PropertyUtils;
import com.vortex.cloud.ums.util.support.Constants;
import com.vortex.cloud.vfs.common.web.springmvc.SpringmvcUtils;
import com.vortex.cloud.vfs.data.dto.RestResultDto;



  @RestController      
@RequestMapping("cloud/management/web/customproperties")
public class CustomPropertiesController {
	private static final Logger logger = LoggerFactory.getLogger(CustomPropertiesController.class);
	public static final String CUSTOM_FILE_NAME = PropertyUtils.PROPERTIES_CUSTOM;

	@Value("${spring.datasource.url}")
	private String jdbcUrl1;
	@Value("${spring.datasource2.url}")
	private String jdbcUrl2;

	/**
	 * 
	 * @param propertyName
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "getCustomValue" + Constants.BACK_DYNAMIC_SUFFIX)
	public RestResultDto<Map<String, String>> getCustomValue(HttpServletRequest request) {
		try {
			Map<String, String> rstMap = Maps.newHashMap();
			String propertyName = SpringmvcUtils.getParameter("propertyName");
			
			if(propertyName.equals("jdbc.url2")){
				rstMap.put("url", jdbcUrl2);
			}else if(propertyName.equals("jdbc.url")){
				rstMap.put("url", jdbcUrl1);
			}
		
		
			return RestResultDto.newSuccess(rstMap);
		} catch (Exception e) {
			logger.error("CustomPropertiesController.getCustomValue", e);
			return RestResultDto.newFalid("查询失败", e.getMessage());
		}
	}
}