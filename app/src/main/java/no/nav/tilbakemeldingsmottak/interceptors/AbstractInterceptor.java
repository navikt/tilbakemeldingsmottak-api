package no.nav.tilbakemeldingsmottak.interceptors;

import static org.apache.commons.lang3.StringUtils.isBlank;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;

@Slf4j
public class AbstractInterceptor extends HandlerInterceptorAdapter {


	public AbstractInterceptor() {}

	/**
	 * @return Return the value in the first header that is defined in the request
	 */
	String getHeaderValuesFromRequest(HttpServletRequest request, String fallbackValue, String... headerNames) {
		for (String headerName : headerNames) {
			String value = request.getHeader(headerName);
			if (!isBlank(value)) {
				return value;
			}
		}
		return fallbackValue;
	}

	boolean addValueToMDC(String value, String key) {
		if (value != null && !value.isEmpty()) {
			MDC.put(key, value);
			return true;
		}
		return false;
	}
}
