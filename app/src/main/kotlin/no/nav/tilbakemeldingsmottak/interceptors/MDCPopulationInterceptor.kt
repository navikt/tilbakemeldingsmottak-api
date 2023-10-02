package no.nav.tilbakemeldingsmottak.interceptors

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import no.nav.tilbakemeldingsmottak.config.MDCConstants
import org.slf4j.MDC
import org.springframework.web.servlet.ModelAndView
import java.util.*

class MDCPopulationInterceptor : AbstractInterceptor() {
    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        val callId = getHeaderValuesFromRequest(
            request, UUID.randomUUID().toString(),
            "nav-callid", "nav-call-id", "x_callId", "callid"
        )
        addValueToMDC(callId, MDCConstants.MDC_CALL_ID)
        val consumerId = getHeaderValuesFromRequest(
            request, "tilbakemeldingsmottak",
            "nav-consumerid", "nav-consumer-id", "x_consumerid", "consumerid"
        )
        addValueToMDC(consumerId, MDCConstants.MDC_CONSUMER_ID)
        return true
    }

    override fun postHandle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any,
        modelAndView: ModelAndView?
    ) {
        MDC.clear()
    }
}
