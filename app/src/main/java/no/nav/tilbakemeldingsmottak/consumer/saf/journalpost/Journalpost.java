package no.nav.tilbakemeldingsmottak.consumer.saf.journalpost;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Value
@Builder
public class Journalpost {
    @Builder.Default
    private final List<DokumentInfo> dokumenter = new ArrayList<>();
    private final Bruker bruker;
    private final String tema;
    private final String kanalnavn;
    private final LocalDateTime datoOpprettet;

    @Value
    @Builder
    public static class DokumentInfo {
        private final String dokumentInfoId;

        @Builder.Default
        private final List<Dokumentvariant> dokumentvarianter = new ArrayList<>();
    }

    @Value
    @Builder
    public static class Dokumentvariant {
        private final Variantformat variantformat;
        private final boolean saksbehandlerHarTilgang;
    }

    @Value
    @Builder
    public static class Bruker {
        private final String id;
    }

}
