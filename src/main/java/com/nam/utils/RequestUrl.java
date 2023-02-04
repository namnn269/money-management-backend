package com.nam.utils;

import javax.servlet.http.HttpServletRequest;

public class RequestUrl {
	public static String getUrlFromRequest(HttpServletRequest request) {
		return "http://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
	}
}
