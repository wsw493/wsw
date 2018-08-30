package com.vortex.cloud.ums.dataaccess.service.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;
import com.vortex.cloud.ums.dataaccess.dao.ICloudSystemDao;
import com.vortex.cloud.ums.dataaccess.dao.ICloudUserDao;
import com.vortex.cloud.ums.dataaccess.service.ICloudLoginLogService;
import com.vortex.cloud.ums.dataaccess.service.ILoginService;
import  com.vortex.cloud.vfs.data.dto.LoginReturnInfoDto;
import com.vortex.cloud.ums.enums.LoginErrEnum;
import com.vortex.cloud.ums.model.CloudUser;
import com.vortex.cloud.vfs.common.exception.VortexException;


@Transactional
@Service("loginService")
public class LoginServiceImpl implements ILoginService {
	private static final Logger logger = LoggerFactory.getLogger(LoginServiceImpl.class);

	@Resource
	private ICloudUserDao cloudUserDao;

	@Resource
	private ICloudSystemDao cloudSystemDao;

	@Resource
	private ICloudLoginLogService cloudLoginLogService;

	@Override
	public LoginReturnInfoDto login(String tenantCode, String systemCode, String userName, String password,
			String mobilePushMsgId, String ip) {
		CloudUser cloudUser = null;
		// 注意：超级管理员不属于任何租户，因此无须校验入参tenantCode
		// 用户名和密码必须，租户code和系统code必须有一个
		if (StringUtils.isEmpty(userName) || StringUtils.isEmpty(password)) {
			logger.error("用户登录时，传入的参数不足！");
			throw new VortexException("用户登录时，传入的参数不足！");
		}

		List<LoginReturnInfoDto> list = cloudUserDao.getLoginInfo(tenantCode, systemCode, userName);
		if (CollectionUtils.isEmpty(list)) {
			logger.error(LoginErrEnum.LOGIN_ERR_NOT_FOUND.getValue());
			throw new VortexException(LoginErrEnum.LOGIN_ERR_NOT_FOUND.getKey());
		} else if (list.size() > 1) {
			logger.error(LoginErrEnum.LOGIN_ERR_FOUND_MUTI.getValue());
			throw new VortexException(LoginErrEnum.LOGIN_ERR_FOUND_MUTI.getKey());
		}

		LoginReturnInfoDto result = list.get(0);

		if (!password.equals(result.getPassword())) {
			logger.error(LoginErrEnum.LOGIN_ERR_PASSWORD.getValue());
			throw new VortexException(LoginErrEnum.LOGIN_ERR_PASSWORD.getKey());
		}

		result.setPassword(null);

		// 设置系统可用列表
		result.setSystemList(cloudSystemDao.getSystemList(result.getUserId()));
		// 更新手机推送id
		if (StringUtils.isNotBlank(mobilePushMsgId)) {
			cloudUser = cloudUserDao.findOne(result.getUserId());
			cloudUser.setMobilePushMsgId(mobilePushMsgId);
			cloudUserDao.update(cloudUser);
		}
		try {
			cloudLoginLogService.saveCloudLoginLog(result.getUserName(), result.getName(), ip);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	@Override
	public LoginReturnInfoDto login(String tenantCode, String systemCode, String userName, String password, String ip) {
		return this.login(tenantCode, systemCode, userName, password, null, ip);
	}

	@Override
	public Map<String, String> login(String account, String password, String ip) throws Exception {
		Map<String, String> resultMap = Maps.newHashMap();
		List<LoginReturnInfoDto> list = cloudUserDao.getLoginInfo(null, null, account);
		if (CollectionUtils.isEmpty(list)) {
			logger.error(LoginErrEnum.LOGIN_ERR_NOT_FOUND.getValue());
			throw new VortexException(LoginErrEnum.LOGIN_ERR_NOT_FOUND.getKey());
		} else if (list.size() > 1) {
			logger.error(LoginErrEnum.LOGIN_ERR_FOUND_MUTI.getValue());
			throw new VortexException(LoginErrEnum.LOGIN_ERR_FOUND_MUTI.getKey());
		}

		LoginReturnInfoDto result = list.get(0);

		if (!password.equals(result.getPassword())) {
			logger.error(LoginErrEnum.LOGIN_ERR_PASSWORD.getValue());
			throw new VortexException(LoginErrEnum.LOGIN_ERR_PASSWORD.getKey());
		}

		resultMap.put("id", result.getUserId());
		resultMap.put("name", result.getName());
		resultMap.put("code", result.getUserName());
		resultMap.put("account", result.getUserName());
		resultMap.put("password", result.getPassword());

		cloudLoginLogService.saveCloudLoginLog(result.getUserName(), result.getName(), ip);

		return resultMap;
	}
}
