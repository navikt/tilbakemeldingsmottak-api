package no.nav.tilbakemeldingsmottak.consumer.email

import com.microsoft.graph.serviceclient.GraphServiceClient
import no.nav.tilbakemeldingsmottak.consumer.email.aad.AADMailClient
import no.nav.tilbakemeldingsmottak.consumer.email.aad.AADMailClientImpl
import no.nav.tilbakemeldingsmottak.consumer.email.aad.AADMailConfiguration
import no.nav.tilbakemeldingsmottak.consumer.email.aad.AADProperties
import no.nav.tilbakemeldingsmottak.consumer.email.aad.AzureEmailService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.runner.ApplicationContextRunner
import java.util.function.Supplier

class EmailServiceConfigTest {
    private val contextRunner = ApplicationContextRunner()
        .withInitializer { it.environment.setActiveProfiles("nais") }
        .withUserConfiguration(
            LoggingEmailService::class.java,
            AzureEmailService::class.java,
            AADMailConfiguration::class.java,
            AADMailClientImpl::class.java
        )

    @Test
    fun `disabled email uses logging without Graph or mail credentials`() {
        contextRunner.withPropertyValues("email.enabled=false").run { context ->
            assertNull(context.startupFailure)
            assertEquals(1, context.getBeansOfType(EmailService::class.java).size)
            assertTrue(context.getBean(EmailService::class.java) is LoggingEmailService)
            assertTrue(context.getBeansOfType(GraphServiceClient::class.java).isEmpty())
            assertTrue(context.getBeansOfType(AADMailClient::class.java).isEmpty())
        }
    }

    @Test
    fun `email defaults to logging when not configured`() {
        contextRunner.run { context ->
            assertNull(context.startupFailure)
            assertEquals(1, context.getBeansOfType(EmailService::class.java).size)
            assertTrue(context.getBean(EmailService::class.java) is LoggingEmailService)
            assertTrue(context.getBeansOfType(GraphServiceClient::class.java).isEmpty())
            assertTrue(context.getBeansOfType(AADMailClient::class.java).isEmpty())
        }
    }

    @Test
    fun `enabled email uses Azure and creates Graph client`() {
        contextRunner
            .withPropertyValues(
                "email.enabled=true",
                "email_from_address=sender@example.test",
                "retry-config.sendMail.max-attempts=1"
            )
            .withBean(AADProperties::class.java, Supplier {
                AADProperties().apply {
                    tenant = "00000000-0000-0000-0000-000000000001"
                    clientId = "00000000-0000-0000-0000-000000000002"
                    clientSecret = "test-secret"
                    email = "sender@example.test"
                }
            })
            .run { context ->
                assertNull(context.startupFailure)
                assertEquals(1, context.getBeansOfType(EmailService::class.java).size)
                assertTrue(context.getBean(EmailService::class.java) is AzureEmailService)
                assertEquals(1, context.getBeansOfType(GraphServiceClient::class.java).size)
                assertTrue(context.getBean(AADMailClient::class.java) is AADMailClientImpl)
            }
    }
}
