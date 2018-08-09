package com.personal.cloud.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.ApplicationContext;

/**
 * @date 2018-08-08
 * @author wsw
 * Created by wsw on 2018/8/3.
 */
@EnableDiscoveryClient
@SpringBootApplication(scanBasePackages = "com.personal")
public class RedisServerApplication {

    private static Logger logger = LoggerFactory.getLogger(RedisServerApplication.class);

    public static void main(String[] args) {

        ApplicationContext ctx =  SpringApplication.run(RedisServerApplication.class,args);
        String[] activeProfiles = ctx.getEnvironment().getActiveProfiles();
        for (String profile : activeProfiles) {
            logger.info("Spring Boot 使用profile为:{}", profile);
        }
    }

}
