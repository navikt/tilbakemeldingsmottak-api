package no.nav.tilbakemeldingsmottak.consumer.email.aad

import com.microsoft.graph.models.Message
import com.microsoft.graph.serviceclient.GraphServiceClient
import com.microsoft.graph.users.item.sendmail.SendMailPostRequestBody
import io.github.resilience4j.retry.annotation.Retry
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

@Component
@Profile("nais")
class AADMailClientImpl(private val aadProperties: AADProperties, private val graphClient: GraphServiceClient) :
    AADMailClient {

    private val log = LoggerFactory.getLogger(javaClass)

    @Value("\${retry-config.sendMail.max-attempts}")
    private val mailMaxAttempts: Int = 0

    @Retry(
        name = "sendMail",
        fallbackMethod = "mailRecover",
    )
    override fun sendMailViaClient(message: Message) {
        log.debug("Skal sende melding: ${message.subject}, ${message.body?.content}")

        val sendMailPostRequestBody = SendMailPostRequestBody()
        sendMailPostRequestBody.message = message
        sendMailPostRequestBody.saveToSentItems = false

        graphClient.users().byUserId(aadProperties.email)
            .sendMail()
            .post(sendMailPostRequestBody)

        log.info("Epost sendt")
    }

    override fun mailRecover(message: Message, e: Exception) {
        log.error("Klarte ikke å sende epost etter {} forsøk", mailMaxAttempts, e)
        throw e
    }

}
