package no.nav.tilbakemeldingsmottak.service;

import lombok.extern.slf4j.Slf4j;
import no.nav.tilbakemeldingsmottak.api.OpprettServiceklageRequest;
import no.nav.tilbakemeldingsmottak.domain.Serviceklage;
import no.nav.tilbakemeldingsmottak.repository.ServiceklageRepository;
import no.nav.tilbakemeldingsmottak.service.epost.EmailServiceImpl;
import no.nav.tilbakemeldingsmottak.service.mappers.OpprettServiceklageRequestMapper;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.mail.MessagingException;

@Service
@Slf4j
public class ServiceklageService {

    private ServiceklageRepository serviceklageRepository;
    private EmailServiceImpl emailService;
    private final OpprettServiceklageRequestMapper opprettServiceklageRequestMapper;

    @Inject
    public ServiceklageService(ServiceklageRepository serviceklageRepository, EmailServiceImpl emailService, OpprettServiceklageRequestMapper opprettServiceklageRequestMapper) {
        this.serviceklageRepository = serviceklageRepository;
        this.emailService = emailService;
        this.opprettServiceklageRequestMapper = opprettServiceklageRequestMapper;
    }

    public long opprettServiceklage(OpprettServiceklageRequest request) throws MessagingException {
        Serviceklage serviceklage = opprettServiceklageRequestMapper.map(request);

        serviceklageRepository.save(serviceklage);
        log.info("Serviceklage med serviceklageId={} persistert", serviceklage.getServiceklageId());
        emailService.sendMail(request, serviceklage.getServiceklageId());

        return serviceklage.getServiceklageId();
    }


}
