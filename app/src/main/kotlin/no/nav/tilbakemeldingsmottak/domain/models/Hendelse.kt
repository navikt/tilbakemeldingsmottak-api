package no.nav.tilbakemeldingsmottak.domain.models

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "hendelse")
data class Hendelse(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) @Column(name = "id") val id: Long? = null,
    @Column(name = "journalpostid") val journalpostId: String? = null,
    @Column(name = "oppgaveid") val oppgaveId: String? = null,
    @Column(name = "tidspunkt", columnDefinition = "TIMESTAMP WITH TIME ZONE") val tidspunkt: LocalDateTime,
    @Column(name = "hendelsetype") val hendelsetype: String,
)
