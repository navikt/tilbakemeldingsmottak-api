package no.nav.tilbakemeldingsmottak.rest.datavarehus.service;

import lombok.RequiredArgsConstructor;
import no.nav.tilbakemeldingsmottak.repository.ServiceklageRepository;
import no.nav.tilbakemeldingsmottak.serviceklage.Serviceklage;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor

public class DatavarehusService {

    private final ServiceklageRepository serviceklageRepository;

    public List<Serviceklage> hentServiceklageData(LocalDateTime datoFra) {
        return serviceklageRepository.findAllByOpprettetDatoAfter(datoFra);
    }

}
