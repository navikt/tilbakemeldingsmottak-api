package no.nav.tilbakemeldingsmottak.consumer.saf.graphql

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

data class GraphQLRequest @JsonCreator constructor(
    @JsonProperty("operationName") val operationName: String,
    @JsonProperty("variables") val variables: Map<String, Any>
)
