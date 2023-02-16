package no.nav.tilbakemeldingsmottak.serviceklage;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class Answer {

    String answer;
    Button button;
    String emit;
    String next;
    List<Question> questions;
}
