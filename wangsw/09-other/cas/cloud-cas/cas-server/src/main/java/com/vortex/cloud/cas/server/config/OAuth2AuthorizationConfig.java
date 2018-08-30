package com.vortex.cloud.cas.server.config;

import com.google.common.collect.Lists;
import com.vortex.cloud.cas.server.provider.CustomDaoAuthenticationProvider;
import com.vortex.cloud.cas.server.provider.request.CustomOAuth2RequestFactory;
import com.vortex.cloud.cas.server.service.CustomClientDetailsService;
import com.vortex.cloud.cas.server.service.CustomUserDetailsService;
import com.vortex.cloud.cas.server.store.OwnTokenStore;
import com.vortex.cloud.cas.server.wrapper.CustomUserDetailsByNameServiceWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationProvider;

import java.util.List;

/**
 * File Name             :  OAuth2AuthorizationConfig
 * Author                :  luhao
 * Create Date           :  2016/7/29
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
public class OAuth2AuthorizationConfig extends AuthorizationServerConfigurerAdapter {
    @Autowired
    private CustomUserDetailsService userDetailsService;
    @Autowired
    private CustomClientDetailsService clientDetailsService;
    @Autowired
    private OwnTokenStore tokenStore;

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer configurer) throws Exception {
        CustomDaoAuthenticationProvider customDaoAuthenticationProvider = new CustomDaoAuthenticationProvider();
        customDaoAuthenticationProvider.setUserDetailsService(userDetailsService);
        customDaoAuthenticationProvider.setPasswordEncoder(NoOpPasswordEncoder.getInstance());

        List<AuthenticationProvider> providers = Lists.newArrayList();
        providers.add(customDaoAuthenticationProvider);
        ProviderManager providerManager = new ProviderManager(providers);
        providerManager.setEraseCredentialsAfterAuthentication(false);

        configurer.authenticationManager(providerManager);
        configurer.userDetailsService(userDetailsService);
        configurer.requestFactory(getOAuth2RequestFactory());
        configurer.tokenStore(tokenStore);
        configurer.tokenServices(getServices());

    }

    @Override
    public void configure(AuthorizationServerSecurityConfigurer oauthServer) throws Exception {
        oauthServer.checkTokenAccess("isAuthenticated()");
    }

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.withClientDetails(clientDetailsService);
    }

    private CustomOAuth2RequestFactory getOAuth2RequestFactory() {
        CustomOAuth2RequestFactory customOAuth2RequestFactory = new CustomOAuth2RequestFactory
                (clientDetailsService);
        return customOAuth2RequestFactory;
    }

    @Bean
    public DefaultTokenServices getServices() {
        List<AuthenticationProvider> providers = Lists.newArrayList();
        providers.add(getPreAuthenticatedAuthenticationProvider());
        ProviderManager providerManager = new ProviderManager(providers);

        DefaultTokenServices defaultTokenServices = new DefaultTokenServices();
        defaultTokenServices.setTokenStore(tokenStore);
        defaultTokenServices.setSupportRefreshToken(true);
        //token有效期为8小时
        defaultTokenServices.setAccessTokenValiditySeconds(3600*10);
        defaultTokenServices.setAuthenticationManager(providerManager);

        return defaultTokenServices;
    }

    public PreAuthenticatedAuthenticationProvider getPreAuthenticatedAuthenticationProvider() {
        PreAuthenticatedAuthenticationProvider preAuthenticatedAuthenticationProvider = new
                PreAuthenticatedAuthenticationProvider();
        preAuthenticatedAuthenticationProvider.setThrowExceptionWhenTokenRejected(true);
        preAuthenticatedAuthenticationProvider.setPreAuthenticatedUserDetailsService
                (getAuthenticationUserDetailsService());

        return preAuthenticatedAuthenticationProvider;
    }

    public AuthenticationUserDetailsService getAuthenticationUserDetailsService() {
        return new
                CustomUserDetailsByNameServiceWrapper<>(userDetailsService);
    }

}
