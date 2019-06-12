package no.nav.serviceklagemottak.service;

import lombok.extern.slf4j.Slf4j;
import no.nav.serviceklagemottak.api.OpprettServiceklageRequest;
import no.nav.serviceklagemottak.api.RegistrerTilbakemeldingRequest;
import no.nav.serviceklagemottak.domain.Serviceklage;
import no.nav.serviceklagemottak.exceptions.ServiceklageIkkeFunnetException;
import no.nav.serviceklagemottak.repository.ServiceklageRepository;
import no.nav.serviceklagemottak.service.epost.EmailServiceImpl;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

@Service
@Slf4j
public class ServiceklageService {

    private ServiceklageRepository serviceklageRepository;
    private EmailServiceImpl emailService;

    @Inject
    public ServiceklageService(ServiceklageRepository serviceklageRepository, EmailServiceImpl emailService) {
        this.serviceklageRepository = serviceklageRepository;
        this.emailService = emailService;
    }

    public long opprettServiceklage(OpprettServiceklageRequest request) {
        Serviceklage serviceklage = Serviceklage.builder()
                .navn(request.getInnmelder().getNavn())
                .klagetekst(request.getKlagetekst())
                .build();

        serviceklageRepository.save(serviceklage);
        log.info("Serviceklage med id=%d lagret", serviceklage.getId());
        emailService.sendMail("leif.ljastad@trygdeetaten.no", "Serviceklage", "Serviceklage er mottatt");
        log.info("Mail sendt");

        return serviceklage.getId();
    }

    public void registrerTilbakemelding(long id, RegistrerTilbakemeldingRequest request) {
        Serviceklage serviceklage = serviceklageRepository.findById(id)
                .orElseThrow(() -> new ServiceklageIkkeFunnetException("Kunne ikke finne serviceklage med id=" + id));

        serviceklage.setTilbakemelding(request.getTilbakemelding());

        serviceklageRepository.save(serviceklage);
        log.info("Tilbkaemelding registrert");
    }
}
