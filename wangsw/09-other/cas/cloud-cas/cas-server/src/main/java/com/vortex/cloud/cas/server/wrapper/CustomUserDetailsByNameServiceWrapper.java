package com.vortex.cloud.cas.server.wrapper;

import java.util.Map;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.util.Assert;

import com.vortex.cloud.cas.server.service.CustomUserDetailsService;

/**
 * File Name : CustomUserDetailsByNameServiceWrapper Author : luhao Create Date
 * : 2016/9/14 Description : Reviewed By : Reviewed On : Version History :
 * Modified By : Modified Date : Comments : CopyRight : COPYRIGHT(c)
 * www.XXXXX.com All Rights Reserved
 * *******************************************************************************************
 */
public class CustomUserDetailsByNameServiceWrapper<T extends Authentication>
		implements AuthenticationUserDetailsService<T>, InitializingBean {

	private CustomUserDetailsService userDetailsService = null;

	/**
	 * Constructs an empty wrapper for compatibility with Spring Security
	 * 2.0.x's method of using a setter.
	 */
	public CustomUserDetailsByNameServiceWrapper() {
		// constructor for backwards compatibility with 2.0
	}

	/**
	 * Constructs a new wrapper using the supplied
	 * {@link org.springframework.security.core.userdetails.UserDetailsService}
	 * as the service to delegate to.
	 *
	 * @param userDetailsService
	 *            the UserDetailsService to delegate to.
	 */
	public CustomUserDetailsByNameServiceWrapper(final CustomUserDetailsService userDetailsService) {
		Assert.notNull(userDetailsService, "userDetailsService cannot be null.");
		this.userDetailsService = userDetailsService;
	}

	/**
	 * Check whether all required properties have been set.
	 *
	 * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		Assert.notNull(this.userDetailsService, "UserDetailsService must be set");
	}

	/**
	 * Get the UserDetails object from the wrapped UserDetailsService
	 * implementation
	 */
	@Override
	public UserDetails loadUserDetails(T authentication) throws UsernameNotFoundException {
		UsernamePasswordAuthenticationToken user = (UsernamePasswordAuthenticationToken) authentication.getPrincipal();
		Map<String, String> details = (Map<String, String>) user.getDetails();
		return this.userDetailsService.loadUserByUsername(user.getName(), (String) user.getCredentials(),
				details.get("ip"), Integer.parseInt(details.get("inside")), details.get("appAuthServiceUrl"));
	}

	/**
	 * Set the wrapped UserDetailsService implementation
	 *
	 * @param aUserDetailsService
	 *            The wrapped UserDetailsService to set
	 */
	public void setUserDetailsService(CustomUserDetailsService aUserDetailsService) {
		this.userDetailsService = aUserDetailsService;
	}

}
