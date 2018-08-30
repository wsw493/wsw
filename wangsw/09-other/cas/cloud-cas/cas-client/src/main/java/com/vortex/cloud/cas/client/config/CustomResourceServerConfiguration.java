package com.vortex.cloud.cas.client.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;

import com.vortex.cloud.cas.client.provider.error.CustomOAuth2AuthenticationEntryPoint;

/**
 * File Name : ResourceServerConfiguration Author : luhao Create Date : 2016/8/8
 * Description : Reviewed By : Reviewed On : Version History : Modified By :
 * Modified Date : Comments : CopyRight : COPYRIGHT(c) www.XXXXX.com All Rights
 * Reserved
 * *******************************************************************************************
 */
@Configuration
@EnableResourceServer
public class CustomResourceServerConfiguration extends ResourceServerConfigurerAdapter {

	@Override
	public void configure(HttpSecurity http) throws Exception {
		http.headers().frameOptions().disable();
		http.csrf().disable().antMatcher("/**").authorizeRequests().antMatchers("/cas/login", "/cas/refreshToken")
				.permitAll().antMatchers("/**/np/**").permitAll().antMatchers("/cloud/gps/alarm/**").permitAll().antMatchers("/ReportServer/**").permitAll().anyRequest().authenticated();
	}

	@Override
	public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
		resources.authenticationEntryPoint(new CustomOAuth2AuthenticationEntryPoint());
	}

}
