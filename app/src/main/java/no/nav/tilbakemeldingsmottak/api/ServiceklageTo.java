package no.nav.tilbakemeldingsmottak.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ServiceklageTo {

    private Long serviceklageId;
    private LocalDateTime datoOpprettet;
    private String klagetype;
    private String klagetekst;
    private Boolean oenskerAaKontaktes;
    private Boolean erBehandlet;

}
