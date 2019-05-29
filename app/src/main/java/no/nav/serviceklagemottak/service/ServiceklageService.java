package no.nav.serviceklagemottak.service;

import no.nav.serviceklagemottak.api.OpprettServiceklageRequest;
import no.nav.serviceklagemottak.domain.Serviceklage;
import no.nav.serviceklagemottak.repository.ServiceklageRepository;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

@Service
public class ServiceklageService {

    private ServiceklageRepository serviceklageRepository;

    @Inject
    public ServiceklageService(ServiceklageRepository serviceklageRepository) {
        this.serviceklageRepository = serviceklageRepository;
    }

    public long opprettServiceklage(OpprettServiceklageRequest request) {
        Serviceklage serviceklage = Serviceklage.builder()
                .email(request.getEmail())
                .klagetekst(request.getKlagetekst())
                .build();

        serviceklageRepository.save(serviceklage);

        return serviceklage.getId();
    }
}
