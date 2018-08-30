package com.vortex.cloud.cas.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;

/**
 * File Name             :  CasServerApplication
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
@EnableEurekaClient
@SpringBootApplication
@EnableAuthorizationServer
@EnableResourceServer
@ComponentScan("com.vortex")
public class CasServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(CasServerApplication.class, args);
    }
}
