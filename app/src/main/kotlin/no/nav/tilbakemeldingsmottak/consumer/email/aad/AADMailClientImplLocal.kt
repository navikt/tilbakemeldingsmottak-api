package no.nav.tilbakemeldingsmottak.consumer.email.aad

import com.microsoft.graph.models.Message
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

@Component
@Profile("local | itest")
class AADMailClientImplLocal : AADMailClient {

    private val log = LoggerFactory.getLogger(javaClass)

    @Value("\${retry-config.send-mail.max-attempts}")
    private val mailMaxAttempts = 0
    override fun sendMailViaClient(message: Message) {
        log.debug("Skal sende melding: ${message.subject}, ${message.body?.content}")
        log.info("Sender ikke epost i local/test env")
    }

    override fun mailRecover(e: Exception, message: Message) {
        log.error("Klarte ikke å sende epost etter {} forsøk", mailMaxAttempts, e)
        throw e
    }


}
