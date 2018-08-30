package com.vortex.cloud.ums.util.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vortex.cloud.vfs.common.mapper.JsonMapper;

public class ConnectHttpService {
	private static final int CONNECT_TIMEOUT = 10000; // 超时毫秒数
	public static final String REST_PMS = "parameters"; // 参数key
	public static final String METHOD_POST = "POST";
	public static final String METHOD_GET = "GET";
	public static final String UTF8 = "utf-8";

	private static Logger logger = LoggerFactory.getLogger(ConnectHttpService.class);

	/**
	 * 有cas权限的http调用，并且参数为"param1=xx&param2=xx"的形式
	 * 
	 * @param url
	 * @param method
	 * @param params
	 * @return
	 */
	/*
	 * public static String callHttpByPermission(String url, String method,
	 * Map<String, Object> params) { String pms = convertMap(params); return
	 * callHttpByPermition(url, method, pms); }
	 */

	/**
	 * 有cas权限的http调用，并且参数为"parameters={..}"的形式
	 * 
	 * @param url
	 * @param method
	 * @param params
	 * @return
	 */
	/*
	 * public static String callHttpByPermissionAndParameters(String url, String
	 * method, Map<String, Object> params) { String pms =
	 * convertMapToParameters(params); return callHttpByPermition(url, method,
	 * pms); }
	 */

	/**
	 * 有cas权限的http调用
	 * 
	 * @param url
	 * @param method
	 * @param pms
	 * @return
	 */
	/*
	 * private static String callHttpByPermition(String url, String method,
	 * String pms) { if (METHOD_GET.equals(method)) { if
	 * (StringUtils.isNotEmpty(pms)) { if (url.indexOf("?") != -1) { url += "&"
	 * + pms; } else { url += "?" + pms; } }
	 * 
	 * pms = null; }
	 * 
	 * // 拼接票据 String proxyTicket =
	 * AssertionHolder.getAssertion().getPrincipal().getProxyTicketFor(url);
	 * 
	 * String rst = null; if (METHOD_POST.equals(method)) { if
	 * (StringUtils.isEmpty(pms)) { pms = "ticket=" + proxyTicket; } else { pms
	 * += "&ticket=" + proxyTicket; } rst = sendPost(url, pms); } else if
	 * (METHOD_GET.equals(method)) { if (url.indexOf("?") != -1) { url +=
	 * "&ticket=" + proxyTicket; } else { url += "?ticket=" + proxyTicket; } rst
	 * = sendGet(url, pms); }
	 * 
	 * return rst; }
	 */
	/**
	 * 不控制权限的http调用，并且参数为"param1=xx&param2=xx"的形式
	 * 
	 * @param url
	 * @param method
	 * @param params
	 * @return
	 */
	public static String callHttp(String url, String method, Map<String, Object> params) {
		String pms = convertMap(params);
		String rst = null;
		if (METHOD_POST.equals(method)) {
			rst = sendPost(url, pms);
		} else if (METHOD_GET.equals(method)) {
			rst = sendGet(url, pms);
		}

		return rst;
	}

	/**
	 * 不控制权限的http调用，并且参数为"parameters={..}"的形式
	 * 
	 * @param url
	 * @param method
	 * @param params
	 * @return
	 */
	public static String callHttpByParameters(String url, String method, Map<String, Object> params) {
		String pms = convertMapToParameters(params);
		String rst = null;
		if (METHOD_POST.equals(method)) {
			rst = sendPost(url, pms);
		} else if (METHOD_GET.equals(method)) {
			rst = sendGet(url, pms);
		}

		return rst;
	}

	/**
	 * 不控制权限的http调用，参数直接传入拼接好的String，形如"param1=xx&param2=xx"
	 * 
	 * @param url
	 * @param method
	 * @param params
	 * @return
	 */
	public static String callHttpByStringParameters(String url, String method, String params) {
		String rst = null;
		try {
			params = encodeParam(params, UTF8);
			if (METHOD_POST.equals(method)) {
				rst = sendPost(url, params);
			} else if (METHOD_GET.equals(method)) {
				rst = sendGet(url, params);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rst;
	}

	/**
	 * 将url中?后面的参数编码为相应的格式
	 * 
	 * @param uri
	 * @return
	 */
	private static String encodeParam(String params, String enc) {
		if (StringUtils.isEmpty(params)) {
			return params;
		}

		String[] pms = params.split("&");
		String rst = "";
		String[] temp = null;
		try {
			for (int i = 0; i < pms.length; i++) {
				temp = pms[i].split("=");
				if (temp.length < 2) {
					if (i == 0) {
						rst += pms[i];
					} else {
						rst += "&" + pms[i];
					}
				} else if (temp.length == 2) {
					if (i == 0) {
						rst += temp[0] + "=" + URLEncoder.encode(temp[1], enc);
					} else {
						rst += "&" + temp[0] + "=" + URLEncoder.encode(temp[1], enc);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return rst;
	}

	/**
	 * 将Map<String, Object>转换为形如
	 * 
	 * @param params
	 * @return
	 */
	private static String convertMap(Map<String, Object> params) {
		String rst = "";
		JsonMapper jm = new JsonMapper();
		try {
			if (MapUtils.isNotEmpty(params)) { // 拼接参数
				Iterator<String> it = params.keySet().iterator();
				int i = 1;
				while (it.hasNext()) {
					String pname = it.next();

					Object value = params.get(pname);
					String pvalue = "";
					if (value == null) {
						pvalue = "";
					} else if (value instanceof String) {
						pvalue = value.toString();
					} else {
						pvalue = jm.toJson(params.get(pname));
					}

					if (i == 1) {
						rst += pname + "=" + URLEncoder.encode(pvalue, UTF8);
					} else {
						rst += "&" + pname + "=" + URLEncoder.encode(pvalue, UTF8);
					}
					i++;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return rst;
	}

	private static String convertMapToParameters(Map<String, Object> params) {
		if (MapUtils.isEmpty(params)) {
			return "";
		} else {
			try {
				return REST_PMS + "=" + URLEncoder.encode(new JsonMapper().toJson(params), UTF8);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}

		return "";
	}

	/**
	 * 向指定 URL 发送POST方法的请求
	 * 
	 * @param url
	 *            发送请求的 URL
	 * @param param
	 *            请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
	 * @return 所代表远程资源的响应结果
	 */
	private static final String sendPost(String url, String param) {
		BufferedWriter out = null;
		// PrintWriter out = null;
		BufferedReader in = null;
		String result = "";
		try {
			URL realUrl = new URL(url);
			// 打开和URL之间的连接
			URLConnection conn = realUrl.openConnection();
			// 设置通用的请求属性
			conn.setRequestProperty("accept", "*/*");
			conn.setRequestProperty("connection", "Keep-Alive");
			conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
			conn.setRequestProperty("Accept", "application/json");
			conn.setRequestProperty("Accept-Charset", UTF8);
			conn.setRequestProperty("contentType", UTF8);

			conn.setReadTimeout(CONNECT_TIMEOUT);
			// 发送POST请求必须设置如下两行
			conn.setDoOutput(true);
			conn.setDoInput(true);
			// 获取URLConnection对象对应的输出流
			out = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream(), UTF8));
			// 发送请求参数
			out.write(param);

			// flush输出流的缓冲
			out.flush();
			// 定义BufferedReader输入流来读取URL的响应
			in = new BufferedReader(new InputStreamReader(conn.getInputStream(), UTF8));
			String line;
			while ((line = in.readLine()) != null) {
				result += line;
			}
		} catch (Exception e) {
			System.out.println("发送 POST 请求出现异常！" + e);
			logger.error("发送 POST 请求出现异常！" + e);
			e.printStackTrace();
		}
		// 使用finally块来关闭输出流、输入流
		finally {
			try {
				if (out != null) {
					out.close();
				}
				if (in != null) {
					in.close();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return result.trim();
	}

	/**
	 * 向指定URL发送GET方法的请求
	 *
	 * @param url
	 *            发送请求的URL
	 * @param param
	 *            请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
	 * @return URL 所代表远程资源的响应结果
	 */
	private static final String sendGet(String url, String param) {
		String result = "";
		BufferedReader in = null;
		try {
			URL realUrl = new URL(StringUtils.isEmpty(param) ? url : url + "?" + param);
			// 打开和URL之间的连接
			URLConnection connection = realUrl.openConnection();
			// 设置通用的请求属性
			connection.setRequestProperty("accept", "*/*");
			connection.setRequestProperty("connection", "Keep-Alive");
			connection.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
			connection.setReadTimeout(CONNECT_TIMEOUT);
			// 建立实际的连接
			connection.connect();

			// 定义 BufferedReader输入流来读取URL的响应
			in = new BufferedReader(new InputStreamReader(connection.getInputStream(), UTF8));
			String line;
			while ((line = in.readLine()) != null) {
				result += line;
			}
		} catch (Exception e) {
			logger.error("发送GET请求出现异常！" + e);
			System.out.println("发送GET请求出现异常！" + e);
			e.printStackTrace();
		}
		// 使用finally块来关闭输入流
		finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return result.trim();
	}
}
