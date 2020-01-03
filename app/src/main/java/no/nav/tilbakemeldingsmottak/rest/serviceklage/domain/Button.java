package no.nav.tilbakemeldingsmottak.rest.serviceklage.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class Button {
    String text;
    String info;
}
