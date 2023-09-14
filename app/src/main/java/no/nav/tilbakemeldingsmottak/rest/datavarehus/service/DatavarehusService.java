package no.nav.tilbakemeldingsmottak.rest.datavarehus.service;

import lombok.RequiredArgsConstructor;
import no.nav.tilbakemeldingsmottak.domain.Serviceklage;
import no.nav.tilbakemeldingsmottak.model.DatavarehusServiceklage;
import no.nav.tilbakemeldingsmottak.repository.ServiceklageRepository;
import no.nav.tilbakemeldingsmottak.rest.datavarehus.service.support.DatavarehusServiceklageMapper;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

@Service
@RequiredArgsConstructor

public class DatavarehusService {

    private static final Logger log = getLogger(DatavarehusService.class);

    private final ServiceklageRepository serviceklageRepository;
    private final DatavarehusServiceklageMapper datavarehusServiceklageMapper = new DatavarehusServiceklageMapper();


    public List<DatavarehusServiceklage> hentServiceklageData(OffsetDateTime datoFra) {

        // Henter ut 1 dag tilbake i tid hvis det ikke eksplisitt sendes inn en dato
        if (datoFra == null) {
            datoFra = OffsetDateTime.now().minusDays(1);
        }

        List<Serviceklage> serviceklageData = serviceklageRepository.findAllByOpprettetDatoAfterOrAvsluttetDatoAfter(datoFra.toLocalDateTime(), datoFra.toLocalDateTime());
        log.info("Hentet {} serviceklager fra databasen fra dato {}", serviceklageData.size(), datoFra);
        return datavarehusServiceklageMapper.map(serviceklageData);
    }

}
