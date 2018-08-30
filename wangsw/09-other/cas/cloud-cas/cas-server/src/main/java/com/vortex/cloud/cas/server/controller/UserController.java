package com.vortex.cloud.cas.server.controller;

import java.security.Principal;
import java.util.Map;

import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.collect.Maps;
import com.vortex.cloud.cas.server.userdetails.CustomUserDetails;
import com.vortex.cloud.vfs.data.dto.RestResultDto;

/**
 * File Name             :  UserController
 * Author                :  luhao
 * Create Date           :  2016/8/23
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
@RestController
@RequestMapping
public class UserController {

    @RequestMapping(value = "/user", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public RestResultDto<?> getUser(Principal principal) {
        Map<String, String> userMap = Maps.newHashMap();
        CustomUserDetails customUserDetails = null;
        if (principal instanceof PreAuthenticatedAuthenticationToken) {
            PreAuthenticatedAuthenticationToken preAuthenticatedAuthenticationToken =
                    (PreAuthenticatedAuthenticationToken) principal;
            customUserDetails = (CustomUserDetails) preAuthenticatedAuthenticationToken.getPrincipal();
        } else {
            OAuth2Authentication oAuth2Authentication = (OAuth2Authentication) principal;
            customUserDetails = (CustomUserDetails) oAuth2Authentication.getPrincipal();
        }

        userMap.put("userCode", customUserDetails.getUserCode());
        userMap.put("userName", customUserDetails.getUsername());
        return RestResultDto.newSuccess(userMap);
    }

    @RequestMapping(value = "/c/logout", method = {RequestMethod.GET, RequestMethod.POST})
    public RestResultDto<?> logout() {
        return RestResultDto.newSuccess("logout success");
    }

}
