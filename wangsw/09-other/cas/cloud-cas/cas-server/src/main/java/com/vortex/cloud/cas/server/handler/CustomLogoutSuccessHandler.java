package com.vortex.cloud.cas.server.handler;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.web.authentication.AbstractAuthenticationTargetUrlRequestHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.vortex.cloud.vfs.common.mapper.JsonMapper;
import com.vortex.cloud.vfs.data.dto.RestResultDto;

/**
 * Spring Security logout handler
 */
@Component
public class CustomLogoutSuccessHandler
        extends AbstractAuthenticationTargetUrlRequestHandler
        implements LogoutSuccessHandler {
    private static final String BEARER_AUTHENTICATION = "Bearer ";
    private static final String HEADER_AUTHORIZATION = "authorization";

    @Autowired
    private DefaultTokenServices defaultTokenServices;

    @Override
    public void onLogoutSuccess(HttpServletRequest request,
                                HttpServletResponse response,
                                Authentication authentication)
            throws IOException, ServletException {

        String token = request.getHeader(HEADER_AUTHORIZATION);

        if (token != null && StringUtils.startsWithIgnoreCase(token, BEARER_AUTHENTICATION)) {
            String accessToken = token.split(" ")[1];
            defaultTokenServices.revokeToken(accessToken);
        }

        response.setStatus(HttpServletResponse.SC_OK);

        RestResultDto<?> result = RestResultDto.newSuccess("logout success");
        response.setContentType("application/json");
		JsonMapper jsonMapper=new JsonMapper();
        response.getWriter().print(jsonMapper.toJson(result));
        
    }

}
