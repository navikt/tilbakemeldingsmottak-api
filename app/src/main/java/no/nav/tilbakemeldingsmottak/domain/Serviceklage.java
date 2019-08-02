package no.nav.tilbakemeldingsmottak.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

/**
 * @author Bjørnar Hunshamar, Visma Consulting.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "serviceklage")
public class Serviceklage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "serviceklage_id", nullable = false)
    private Long serviceklageId;

    @Column(name = "dato_opprettet", nullable = false, updatable = false)
    private LocalDateTime datoOpprettet;

    @Column(name = "paa_vegne_av")
    private String paaVegneAv;

    @Column(name = "klagen_gjelder_id", nullable = false)
    private String klagenGjelderId;

    @Column(name = "klagetype")
    private String klagetype;

    @Column(name = "klagetekst")
    private String klagetekst;

    @Column(name = "oensker_aa_kontaktes")
    private Boolean oenskerAaKontaktes;

    @Column(name = "er_serviceklage")
    private String erServiceklage;

    @Column(name = "gjelder")
    private String gjelder;

    @Column(name = "paaklaget_enhet")
    private String paaklagetEnhet;

    @Column(name = "behandlende_enhet")
    private String behandlendeEnhet;

    @Column(name = "ytelse_tjeneste")
    private String ytelseTjeneste;

    @Column(name = "tema")
    private String tema;

    @Column(name = "utfall")
    private String utfall;

    @Column(name = "svarmetode")
    private String svarmetode;
}
