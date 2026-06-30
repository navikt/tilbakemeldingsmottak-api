package no.nav.tilbakemeldingsmottak

import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class TestSecurityController {
    @GetMapping("/test/security/claim")
    @PreAuthorize("@claimChecker.hasAccess(authentication, {'serviceklage-klassifisering'})")
    fun claimProtected(): String = "ok"
}
