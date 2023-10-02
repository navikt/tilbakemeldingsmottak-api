package no.nav.tilbakemeldingsmottak.interceptors

import jakarta.servlet.http.HttpServletRequest
import org.apache.commons.lang3.StringUtils
import org.slf4j.MDC
import org.springframework.web.servlet.HandlerInterceptor

open class AbstractInterceptor : HandlerInterceptor {
    /**
     * @return Return the value in the first header that is defined in the request
     */
    fun getHeaderValuesFromRequest(
        request: HttpServletRequest,
        fallbackValue: String,
        vararg headerNames: String?
    ): String {
        for (headerName in headerNames) {
            val value = request.getHeader(headerName)
            if (!StringUtils.isBlank(value)) {
                return value
            }
        }
        return fallbackValue
    }

    fun addValueToMDC(value: String?, key: String?) {
        if (!value.isNullOrEmpty()) {
            MDC.put(key, value)
        }
    }
}
