package no.nav.serviceklagemottak.service;

import lombok.extern.slf4j.Slf4j;
import no.nav.serviceklagemottak.api.OpprettServiceklageRequest;
import no.nav.serviceklagemottak.domain.Serviceklage;
import no.nav.serviceklagemottak.repository.ServiceklageRepository;
import no.nav.serviceklagemottak.service.epost.EmailServiceImpl;
import no.nav.serviceklagemottak.service.mappers.OpprettServiceklageRequestMapper;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

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

    public long opprettServiceklage(OpprettServiceklageRequest request) {
        Serviceklage serviceklage = opprettServiceklageRequestMapper.map(request);

        serviceklageRepository.save(serviceklage);
        log.info("Serviceklage med serviceklageId={} persistert", serviceklage.getServiceklageId());
        emailService.sendMail("bjornar.hunshamar@nav.no", "Serviceklage", "Serviceklage er mottatt");

        return serviceklage.getServiceklageId();
    }


}
