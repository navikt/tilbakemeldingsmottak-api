package no.nav.tilbakemeldingsmottak.consumer.email

import no.nav.tilbakemeldingsmottak.metrics.MetricLabels
import no.nav.tilbakemeldingsmottak.metrics.Metrics

interface EmailService {
    @Metrics(
        value = MetricLabels.DOK_CONSUMER,
        extraTags = [MetricLabels.PROCESS_CODE, "sendEpost"],
        percentiles = [0.5, 0.95],
        histogram = true,
        internal = true
    )
    fun sendSimpleMessage(mottaker: String, subject: String, content: String)

    @Metrics(
        value = MetricLabels.DOK_CONSUMER,
        extraTags = [MetricLabels.PROCESS_CODE, "sendEpost"],
        percentiles = [0.5, 0.95],
        histogram = true,
        internal = true
    )
    fun sendSimpleMessage(mottakere: List<String>, subject: String, content: String)

    @Metrics(
        value = MetricLabels.DOK_CONSUMER,
        extraTags = [MetricLabels.PROCESS_CODE, "sendEpost"],
        percentiles = [0.5, 0.95],
        histogram = true,
        internal = true
    )
    fun sendMessageWithAttachments(
        mottaker: String,
        subject: String,
        content: String,
        attachment: ByteArray,
        attachmentName: String
    )

    @Metrics(
        value = MetricLabels.DOK_CONSUMER,
        extraTags = [MetricLabels.PROCESS_CODE, "sendEpost"],
        percentiles = [0.5, 0.95],
        histogram = true,
        internal = true
    )
    fun sendMessageWithAttachments(
        mottakere: List<String>,
        subject: String,
        content: String,
        attachment: ByteArray,
        attachmentName: String
    )
}