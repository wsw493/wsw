package com.vortex.cloud.ums.annotation;

import java.io.IOException;
import java.lang.reflect.Method;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.vortex.cloud.ums.dataaccess.service.ICloudUserService;
import com.vortex.cloud.ums.dataaccess.service.IRedisValidateService;
import com.vortex.cloud.vfs.common.web.springmvc.SpringmvcUtils;
import com.vortex.cloud.vfs.data.dto.RestResultDto;

@Service("aopService")
@Aspect
public class AopService {
	private Logger logger = LoggerFactory.getLogger(AopService.class);
	private static final String PAGE403 = "cloud/management/err403";
	@Resource
	private IRedisValidateService redisValidateService;

	@Resource
	private ICloudUserService cloudUserService;

	// @Resource
	// private ICloudLogService cloudLogService;

	@Before("execution(* com.vortex.cloud.ums.web..*.*(..))")
	public void beforeRequest(JoinPoint joinpoint) throws IOException {

	}

	@Around("execution(* com.vortex.cloud.ums.web..*.*(..))")
	public Object aroundRequest(ProceedingJoinPoint joinpoint) throws Throwable {
		// 记录日志
		// CloudLog log = new CloudLog();
		// log.setStartTime(new Date(System.currentTimeMillis()));
		FunctionCode functionCode = this.getFunctionCode(joinpoint);
		Object rst = null;
		String userId = this.getUserId(joinpoint);
		boolean hasPermission = (functionCode == null || this.hasPermission(userId, functionCode.value()));
		// 如果没有找到功能号标注，或者功能验证通过，则执行方法；否则直接返回错误
		if (hasPermission) {
			rst = joinpoint.proceed();
		} else {
			// 返回结果
			rst = getReturnValue(joinpoint);
		}
		// log.setUserId(userId);
		// log.setHasPermission(hasPermission ? 1 : 0);
		// log.setCalledMethod(joinpoint.getTarget().toString() +
		// joinpoint.getSignature().getName());
		// log.setEndTime(new Date(System.currentTimeMillis()));
		//
		// cloudLogService.save(log);
		return rst;
	}

	private boolean hasPermission(String userId, String codeValue) {
		return this.redisValidateService.hasFunction(userId, codeValue);
	}

	/**
	 * 获取session
	 * 
	 * @param joinpoint
	 * @return
	 */
	private String getUserId(JoinPoint joinpoint) {
		// 可以考虑此种方式获取request对象，而不是从方法参数中获取
		HttpServletRequest request = SpringmvcUtils.getRequest();
		String userId = request.getHeader("UserId");
		return userId;
	}

	/**
	 * 获取返回值
	 * 
	 * @param joinpoint
	 * @return
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	private Object getReturnValue(JoinPoint joinpoint) throws InstantiationException, IllegalAccessException {

		FunctionCode code = getFunctionCode(joinpoint);
		Object returnObject = null;
		if (null != code) {
			switch (code.type()) {
			case Page: // 页面返回重定向URl
				returnObject = PAGE403;
				break;
			case Json:// 普通的返回对象
				returnObject = new RestResultDto<String>(10003, "没有权限访问此功能(" + code.value() + ")", null, null);
				logger.error("没有权限访问此功能(" + code.value() + ")");
				break;
			case RestJson:// 普通的返回Rest对象
				returnObject = new RestResultDto<String>(10003, "没有权限访问此功能(" + code.value() + ")", null, null);
				logger.error("没有权限访问此功能(" + code.value() + ")");
				break;
			default:
				returnObject = new RestResultDto<String>(10003, "没有权限访问此功能(" + code.value() + ")", null, null);
				logger.error("没有权限访问此功能(" + code.value() + ")");
				break;
			}
		}
		return returnObject;
	}

	private FunctionCode getFunctionCode(JoinPoint joinpoint) {
		MethodSignature methodSignature = (MethodSignature) joinpoint.getSignature();
		Method method = methodSignature.getMethod();
		FunctionCode functionCode = null;
		// 查看注解是否存在
		if (method.isAnnotationPresent(FunctionCode.class)) {
			functionCode = method.getAnnotation(FunctionCode.class);
		}
		return functionCode;
	}
}
