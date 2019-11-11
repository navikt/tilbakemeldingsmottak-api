package no.nav.tilbakemeldingsmottak.interceptors;

import lombok.extern.slf4j.Slf4j;
import no.nav.tilbakemeldingsmottak.config.MDCConstants;
import org.slf4j.MDC;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

@Slf4j
public class MDCPopulationInterceptor extends AbstractInterceptor {

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
		String callId = getHeaderValuesFromRequest(request, UUID.randomUUID().toString(),
				"nav-callid", "nav-call-id", "x_callId", "callid");
		addValueToMDC(callId, MDCConstants.MDC_CALL_ID);

		String consumerId = getHeaderValuesFromRequest(request, "tilbakemeldingsmottak",
				"nav-consumerid", "nav-consumer-id", "x_consumerid", "consumerid");
		addValueToMDC(consumerId, MDCConstants.MDC_CONSUMER_ID);

		return true;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
		MDC.clear();
	}
}
