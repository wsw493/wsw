package com.vortex.cloud.cas.server.filter;

import org.springframework.security.web.authentication.preauth.RequestHeaderAuthenticationFilter;

import javax.servlet.http.HttpServletRequest;

/**
 * File Name             :  CustomPreAuthenticatedProcessingFilter
 * Author                :  luhao
 * Create Date           :  2016/9/18
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
public class CustomPreAuthenticatedProcessingFilter extends RequestHeaderAuthenticationFilter {
    private boolean allowPreAuthenticatedPrincipals = true;

    @Override
    protected Object getPreAuthenticatedPrincipal(HttpServletRequest request) {
        String userName = (String) (super.getPreAuthenticatedPrincipal(request));
        if (userName == null || userName.trim().equals("")) {
            return userName;
        }

        return userName;
    }
    
    public boolean isAllowPreAuthenticatedPrincipals() {
        return allowPreAuthenticatedPrincipals;
    }
}
