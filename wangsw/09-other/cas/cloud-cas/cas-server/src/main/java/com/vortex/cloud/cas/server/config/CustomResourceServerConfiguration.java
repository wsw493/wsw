package com.vortex.cloud.cas.server.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;

import com.vortex.cloud.cas.server.handler.CustomLogoutHandler;
import com.vortex.cloud.cas.server.handler.CustomLogoutSuccessHandler;

/**
 * File Name             :  ResourceServerConfiguration
 * Author                :  luhao
 * Create Date           :  2016/8/8
 * Description           :
 * Reviewed By           :
 * Reviewed On           :
 * Version History       :
 * Modified By           :
 * Modified Date         :
 * Comments              :
 * CopyRight             : COPYRIGHT(c) www.XXXXX.com   All Rights Reserved
 * *******************************************************************************************
 */
@Configuration
public class CustomResourceServerConfiguration extends ResourceServerConfigurerAdapter {
    @Autowired
    private CustomLogoutSuccessHandler customLogoutSuccessHandler;

    @Autowired
    private CustomLogoutHandler customLogoutHandler;

    @Override
    public void configure(HttpSecurity http) throws Exception {
    	http.headers().frameOptions().disable();
        http.authorizeRequests().anyRequest().authenticated().and()
                .logout().addLogoutHandler(customLogoutHandler)
                .logoutSuccessHandler(customLogoutSuccessHandler);
    }

}