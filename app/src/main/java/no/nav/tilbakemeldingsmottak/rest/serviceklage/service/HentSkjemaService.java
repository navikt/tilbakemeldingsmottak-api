package no.nav.tilbakemeldingsmottak.rest.serviceklage.service;

import static no.nav.tilbakemeldingsmottak.rest.serviceklage.domain.ServiceklageConstants.FREMMET_DATO;
import static no.nav.tilbakemeldingsmottak.rest.serviceklage.domain.ServiceklageConstants.INNSENDER;
import static no.nav.tilbakemeldingsmottak.rest.serviceklage.domain.ServiceklageConstants.KANAL;
import static no.nav.tilbakemeldingsmottak.rest.serviceklage.domain.ServiceklageConstants.SVARMETODE;
import static no.nav.tilbakemeldingsmottak.rest.serviceklage.domain.ServiceklageConstants.SVAR_IKKE_NOEDVENDIG;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import lombok.extern.slf4j.Slf4j;
import no.nav.tilbakemeldingsmottak.exceptions.SkjemaSerializationException;
import no.nav.tilbakemeldingsmottak.repository.ServiceklageRepository;
import no.nav.tilbakemeldingsmottak.rest.serviceklage.domain.DefaultAnswers;
import no.nav.tilbakemeldingsmottak.rest.serviceklage.domain.HentSkjemaResponse;
import no.nav.tilbakemeldingsmottak.rest.serviceklage.domain.Serviceklage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j

public class HentSkjemaService {

    private final ServiceklageRepository serviceklageRepository;
    private final String classpathSkjema;
    private ObjectMapper mapper = new ObjectMapper(new YAMLFactory());

    private static final String MESSAGE = "Feltet ble fylt ut under registrering av serviceklage";
    private static final String SCHEMA_PATH = "classpath:schema/schema.yaml";
    private static final Charset CHARSET = Charset.forName("utf-8");

    public HentSkjemaService(@Value(SCHEMA_PATH) Resource schema, ServiceklageRepository serviceklageRepository) throws IOException {
        mapper.findAndRegisterModules();
        this.classpathSkjema = StreamUtils.copyToString(schema.getInputStream(), CHARSET);
        this.serviceklageRepository = serviceklageRepository;
    }

    public HentSkjemaResponse hentSkjema(String journalpostId) {
        HentSkjemaResponse response = readSkjema();

        Serviceklage serviceklage = serviceklageRepository.findByJournalpostId(journalpostId);
        if (serviceklage != null) {
            response.setDefaultAnswers(DefaultAnswers.builder()
                    .message(MESSAGE)
                    .answers(mapDefaultAnswers(serviceklage))
                    .build());
        }

        return response;
    }

    private HentSkjemaResponse readSkjema() {
        try {
            return mapper.readValue(classpathSkjema, HentSkjemaResponse.class);
        } catch (Exception e) {
            throw new SkjemaSerializationException("Feil under serialisering av skjema");
        }
    }

    private Map<String, String> mapDefaultAnswers(Serviceklage serviceklage) {
        Map<String, String> defaultAnswers = new HashMap<>();
        if (serviceklage.getOpprettetDato() != null) {
            defaultAnswers.put(FREMMET_DATO, serviceklage.getFremmetDato().toString());
        }
        if (serviceklage.getInnsender() != null) {
            defaultAnswers.put(INNSENDER, serviceklage.getInnsender());
        }
        if (serviceklage.getKanal() != null) {
            defaultAnswers.put(KANAL, serviceklage.getKanal());
        }
        if (serviceklage.getSvarmetode() != null) {
            defaultAnswers.put(SVARMETODE, serviceklage.getSvarmetode());
        }
        if (serviceklage.getSvarmetodeUtdypning() != null) {
            defaultAnswers.put(SVAR_IKKE_NOEDVENDIG, serviceklage.getSvarmetodeUtdypning());
        }
        return defaultAnswers;
    }

}
