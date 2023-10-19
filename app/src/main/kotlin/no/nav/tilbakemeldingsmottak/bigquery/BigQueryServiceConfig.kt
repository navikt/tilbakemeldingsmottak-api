package no.nav.tilbakemeldingsmottak.bigquery

import com.google.cloud.bigquery.BigQuery
import com.google.cloud.bigquery.BigQueryOptions
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class BigQueryServiceConfig {
    @Value("\${gcp_team_project_id}")
    private val projectId: String? = null

    @Bean
    fun bigQueryService(): BigQuery {
        return BigQueryOptions.newBuilder().setProjectId(projectId).build().service
    }
}
