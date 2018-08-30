package com.vortex.cloud.ums.job;

import javax.annotation.Resource;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.vortex.cloud.ums.dataaccess.service.IRedisSyncService;

/**
 * @ClassName: RedisAuthority
 * @Description: 按租户同步用户权限信息（菜单，功能码）
 * @author ZQ shan
 * @date 2017年10月9日 下午2:18:04
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
@Component
public class RedisAuthority {
	@Resource
	private IRedisSyncService redisSyncService;

	@Scheduled(cron = "0 0/5 * * * ?")
	public void init() {
		redisSyncService.syncUserAuthorityByTenant(null);
	}
}
