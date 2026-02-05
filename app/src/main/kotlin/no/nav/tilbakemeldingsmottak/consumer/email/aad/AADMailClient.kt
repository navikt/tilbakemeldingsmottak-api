package no.nav.tilbakemeldingsmottak.consumer.email.aad

import com.microsoft.graph.models.Message


interface AADMailClient {
    fun sendMailViaClient(message: Message)

    fun mailRecover(message: Message, e: Exception)
}
