package com.vortex.cloud.cas.client.provider.error;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.oauth2.provider.error.DefaultWebResponseExceptionTranslator;
import org.springframework.security.oauth2.provider.error.OAuth2AuthenticationEntryPoint;
import org.springframework.security.oauth2.provider.error.WebResponseExceptionTranslator;

import com.alibaba.fastjson.JSON;
import com.vortex.cloud.vfs.data.dto.RestResultDto;

public class CustomOAuth2AuthenticationEntryPoint extends OAuth2AuthenticationEntryPoint {

	private WebResponseExceptionTranslator exceptionTranslator = new DefaultWebResponseExceptionTranslator();

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException authException) throws IOException, ServletException {
		try {
			ResponseEntity<OAuth2Exception> entity = exceptionTranslator.translate(authException);
			String errorCode = entity.getBody().getOAuth2ErrorCode();
			String message=entity.getBody().getMessage();
			if (entity.getBody() instanceof InvalidTokenException) {
				response.getWriter().write(JSON.toJSONString(new RestResultDto<>(10002, errorCode+":"+message, null, null)));
				return;
			}
			response.getWriter().write(JSON.toJSONString(new RestResultDto<>(10001, errorCode+":"+message, null, null)));
		} catch (Exception e) {
			response.getWriter()
					.write(JSON.toJSONString(new RestResultDto<>(10001, "exception translate error", null, null)));
		}

	}
}
