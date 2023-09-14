package no.nav.tilbakemeldingsmottak.interceptors;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.MDC;
import org.springframework.web.servlet.HandlerInterceptor;

import static org.apache.commons.lang3.StringUtils.isBlank;

public class AbstractInterceptor implements HandlerInterceptor {

    public AbstractInterceptor() {
    }

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

    void addValueToMDC(String value, String key) {
        if (value != null && !value.isEmpty()) {
            MDC.put(key, value);
        }
    }
}
