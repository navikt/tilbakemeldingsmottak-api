package no.nav.tilbakemeldingsmottak.rest.datavarehus.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.tilbakemeldingsmottak.model.DatavarehusServiceklage;
import no.nav.tilbakemeldingsmottak.repository.ServiceklageRepository;
import no.nav.tilbakemeldingsmottak.rest.datavarehus.service.support.DatavarehusServiceklageMapper;
import no.nav.tilbakemeldingsmottak.serviceklage.Serviceklage;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor

public class DatavarehusService {

    private final ServiceklageRepository serviceklageRepository;
    private final DatavarehusServiceklageMapper datavarehusServiceklageMapper = new DatavarehusServiceklageMapper();


    public List<DatavarehusServiceklage> hentServiceklageData(OffsetDateTime datoFra) {

        // Henter ut 1 dag tilbake i tid hvis det ikke eksplisitt sendes inn en dato
        if (datoFra == null) {
            datoFra = OffsetDateTime.now().minusDays(1);
        }

        List<Serviceklage> serviceklageData = serviceklageRepository.findAllByOpprettetDatoAfter(datoFra.toLocalDateTime());
        log.info("Hentet {} serviceklager fra databasen fra dato {}", serviceklageData.size(), datoFra);
        return datavarehusServiceklageMapper.map(serviceklageData);
    }

}
