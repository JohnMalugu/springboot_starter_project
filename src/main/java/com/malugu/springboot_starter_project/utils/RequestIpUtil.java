package com.malugu.springboot_starter_project.utils;

import jakarta.servlet.http.HttpServletRequest;

public class RequestIpUtil {

	public static String getClientIpAddress(HttpServletRequest request) {
		String ipAddress = request.getHeader("X-Forwarded-For");

		if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
			ipAddress = request.getHeader("Proxy-Client-IP");
		}
		if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
			ipAddress = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
			ipAddress = request.getRemoteAddr();
		}

		// X-Forwarded-For can sometimes return a comma-separated list of IPs
		// In such cases, the first IP in the list is usually the client's original IP.
		if (ipAddress != null && ipAddress.contains(",")) {
			ipAddress = ipAddress.split(",")[0].trim();
		}

		return ipAddress;
	}
}
