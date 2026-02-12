package no.nav.tilbakemeldingsmottak.consumer.email.aad

import com.microsoft.graph.models.Message
import io.github.resilience4j.retry.annotation.Retry
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

@Component
@Profile("local | itest")
class AADMailClientImplLocal : AADMailClient {

    private val log = LoggerFactory.getLogger(javaClass)

    @Value("\${retry-config.sendMail.max-attempts}")
    private val mailMaxAttempts = 0

    @Retry(
        name = "sendMail",
        fallbackMethod = "mailRecover",
    )
    override fun sendMailViaClient(message: Message) {
        log.info("Sender ikke epost i local/test env")
    }

    override fun mailRecover(message: Message, e: Exception) {
        log.error("Klarte ikke å sende epost etter {} forsøk", mailMaxAttempts, e)
        throw e
    }


}
