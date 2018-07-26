package com.personal.cloud.eureka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;
import org.springframework.context.ApplicationContext;

/**
 * @author wsw
 * Created by wsw on 2018/5/23.
 */
@EnableEurekaServer
@SpringBootApplication(scanBasePackages = "com.personal")
public class EurekaServerApplication {

    private static Logger logger = LoggerFactory.getLogger(EurekaServerApplication.class);

    public static void main(String[] args) {
        ApplicationContext ctx =  SpringApplication.run(EurekaServerApplication.class,args);
        String[] activeProfiles = ctx.getEnvironment().getActiveProfiles();
        for (String profile : activeProfiles) {
            logger.info("Spring Boot 使用profile为:{}", profile);
        }
    }
}
