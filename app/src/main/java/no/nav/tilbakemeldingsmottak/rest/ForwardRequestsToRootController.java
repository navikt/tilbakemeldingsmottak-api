package no.nav.tilbakemeldingsmottak.rest;

import no.nav.security.oidc.api.Unprotected;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author Ugur Alpay Cenar, Visma Consulting.
 */
@Controller
@Unprotected
public class ForwardRequestsToRootController {

    @RequestMapping(value = {"serviceklage/**"})
    public String forwardToRoot() {
        return "forward:/";
    }

}
