package no.nav.tilbakemeldingsmottak.rest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author Ugur Alpay Cenar, Visma Consulting.
 */
@Controller
public class ForwardRequestsToRootController {

    @RequestMapping(value = {"serviceklage/**"})
    public String forwardToRoot() {
        return "forward:/";
    }

}
