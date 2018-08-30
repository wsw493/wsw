package com.vortex.cloud.ums.dataaccess.service.impl;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vortex.cloud.ums.dataaccess.service.ICallRestService;
import com.vortex.cloud.vfs.common.exception.ServiceException;

@Transactional
@Service("callRestService")
public class CallRestServiceImpl implements ICallRestService {
	private Logger logger = LoggerFactory.getLogger(CallRestServiceImpl.class);

	@Override
	public String callRest(String URL, Map<String, Object> parameters, String method) {
		// 获取Connection
	/*	String proxyTicket = AssertionHolder.getAssertion().getPrincipal().getProxyTicketFor(URL);
		URL = URL + "?ticket=" + proxyTicket;*/
		HttpURLConnection httpConnection = this.getConnection(URL, method);

		String param = "";

		if (MapUtils.isNotEmpty(parameters)) { // 拼接参数
			Iterator<String> it = parameters.keySet().iterator();
			int i = 1;
			while (it.hasNext()) {
				String pname = it.next();
				String pvalue = parameters.get(pname) == null ? "" : parameters.get(pname).toString();
				if (i == 1) {
					param += pname + "=" + pvalue;
				} else {
					param += "&" + pname + "=" + pvalue;
				}

				i++;
			}
		}

		this.write(httpConnection, param);
		return this.read(httpConnection); // 调用rest
	}

	private final HttpURLConnection getConnection(String urlStr, String method) {
		URL url = null;
		HttpURLConnection httpConnection = null;
		try {
			url = new URL(urlStr);
			httpConnection = (HttpURLConnection) url.openConnection();
			httpConnection.setRequestMethod(method);
			httpConnection.setDoInput(true);
			httpConnection.setDoOutput(true);
			httpConnection.setConnectTimeout(1000);
			httpConnection.connect();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("getConnection()", e);
			throw new ServiceException("连接Rest服务异常");
		}

		return httpConnection;
	}

	private final void write(HttpURLConnection httpConnection, String param) {
		BufferedWriter bw = null;
		try {
			bw = new BufferedWriter(new OutputStreamWriter(httpConnection.getOutputStream()));
			bw.write(param);
			bw.flush();
		} catch (IOException e) {
			e.printStackTrace();
			logger.error("write()", e);
			throw new ServiceException("请求Rest服务异常");
		} finally {
			if (bw != null) {
				try {
					bw.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	private String read(HttpURLConnection httpConnection) {
		StringBuffer sb = new StringBuffer("");
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(httpConnection.getInputStream(), "utf-8"));
			String line = null;
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
			logger.error("read()", e);
			throw new ServiceException("Rest服务响应异常");
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return sb.toString();
	}
}
