package com.vortex.cloud.ums.job;

import com.vortex.cloud.ums.config.SpringContextHolder;
import com.vortex.cloud.ums.dataaccess.service.IRedisSyncService;

/**
 * @ClassName: RedisBaseInfo
 * @Description: 按租户同步基本信息
 * @author ZQ shan
 * @date 2017年10月9日 下午2:18:18
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
public class RedisBaseInfo {
	public static void startConsumer() throws Exception {
		IRedisSyncService redisSyncService = SpringContextHolder.getBean("redisSyncService");
		// 同步系统完整菜单
		redisSyncService.syncSystemMenuByTenant(null);
		// 同步系统完整机构部门
		redisSyncService.syncDeptOrgByTenant(null);
		// 同步系统完整人员
		redisSyncService.syncStaffByTenant(null);
		// 同步用户权限信息
		redisSyncService.syncUserAuthorityByTenant(null);
	}

}
