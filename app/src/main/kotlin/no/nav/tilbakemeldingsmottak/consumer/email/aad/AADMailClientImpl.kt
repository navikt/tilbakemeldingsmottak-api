package no.nav.tilbakemeldingsmottak.consumer.email.aad

import com.microsoft.graph.models.Message
import com.microsoft.graph.models.UserSendMailParameterSet
import com.microsoft.graph.requests.GraphServiceClient
import no.nav.tilbakemeldingsmottak.metrics.MetricLabels
import no.nav.tilbakemeldingsmottak.metrics.Metrics
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

@Component
@Profile("nais")
class AADMailClientImpl(private val aadProperties: AADProperties, private val graphClient: GraphServiceClient<*>) :
    AADMailClient {

    private val log = LoggerFactory.getLogger(javaClass)

    @Value("\${retry-config.send-mail.max-attempts}")
    private val mailMaxAttempts: Int = 0

    /*
        @Metrics(
            value = MetricLabels.DOK_CONSUMER,
            extraTags = [MetricLabels.PROCESS_CODE, "sendEpost"],
            percentiles = [0.5, 0.95],
            histogram = true
        )
    */
    override fun sendMailViaClient(message: Message) {
        log.debug("Skal sende melding: ${message.subject}, ${message.body?.content}")

        val sendMailParameterSet = UserSendMailParameterSet
            .newBuilder()
            .withMessage(message)
            .withSaveToSentItems(false)
            .build()

        graphClient.users(aadProperties.email)
            .sendMail(sendMailParameterSet)
            .buildRequest()
            .post()

        log.info("Epost sendt")
    }

    override fun mailRecover(e: Exception, message: Message) {
        log.error("Klarte ikke å sende epost etter {} forsøk", mailMaxAttempts, e)
        throw e
    }

}
