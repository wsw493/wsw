package com.vortex.cloud.ums.util;

import javax.servlet.http.HttpServletRequest;
/**
 * ip工具类
 * @author ll
 *
 */
public class IpUtil {
	
	 public static String getIpAddr(HttpServletRequest request) {
		 String ip = request.getHeader("x-forwarded-for");
		 if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			 ip = request.getHeader("Proxy-Client-IP");
		 }
		 if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			 ip = request.getHeader("WL-Proxy-Client-IP");
		 }
		 if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			 ip = request.getRemoteAddr();
		 }
		 
		 return ip;
	 }
		

}
