package no.nav.serviceklagemottak.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author Bjørnar Hunshamar, Visma Consulting.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "serviceklage")
public class Serviceklage {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "serviceklage_seq")
    @GenericGenerator(name = "serviceklage_seq", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
            parameters = {@Parameter(name = "sequence_name", value = "serviceklage_seq")})
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "klagetekst", nullable = false)
    private String klagetekst;

    @Column(name = "tilbakemelding")
    private String tilbakemelding;

}
