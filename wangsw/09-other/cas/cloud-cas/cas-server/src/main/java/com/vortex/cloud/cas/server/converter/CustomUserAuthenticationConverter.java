package com.vortex.cloud.cas.server.converter;

import com.vortex.cloud.cas.server.userdetails.CustomUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.provider.token.DefaultUserAuthenticationConverter;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * File Name             :  CustomAccessTokenConverter
 * Author                :  luhao
 * Create Date           :  2016/8/28
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
@Component
public class CustomUserAuthenticationConverter extends DefaultUserAuthenticationConverter {

    @Override
    public Map<String, ?> convertUserAuthentication(Authentication authentication) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put(USERNAME, authentication.getName());
        if (authentication.getAuthorities() != null && !authentication.getAuthorities().isEmpty()) {
            response.put(AUTHORITIES, AuthorityUtils.authorityListToSet(authentication.getAuthorities()));
        }
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        response.put("id", customUserDetails.getUserCode());
        return response;
    }

}
