package com.vortex.cloud.ums.job;

import java.util.Date;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vortex.cloud.ums.dataaccess.service.IRedisSyncService;

//@Component
public class RedisFunctionInit {
	@Resource
	private IRedisSyncService redisSyncService;// = SpringContextHolder.getBean("redisSyncService");

	private static final Logger logger = LoggerFactory.getLogger(RedisFunctionInit.class);

	// @Scheduled(cron = "0 0/5 * * * ?")
	public void init() {
		logger.info((new Date()) + "同步人员功能号开始。。。。。。");
		redisSyncService.syncUserFunction();
		logger.info((new Date()) + "同步人员功能号结束。。。。。。");
	}
}
