CREATE TABLE serviceklage (
  serviceklage_id               INTEGER,
  journalpost_id                VARCHAR(10),
  oppgave_id                    VARCHAR(10),
  dato_opprettet                TIMESTAMP(6) NOT NULL,
  paa_vegne_av                  VARCHAR(15),
  klagen_gjelder_id             VARCHAR(15) NOT NULL,
  klagetype                     VARCHAR(30),
  klagetekst                    TEXT,
  oensker_aa_kontaktes          BOOLEAN,
  er_serviceklage               VARCHAR(50),
  gjelder                       VARCHAR(2000),
  kanal                         VARCHAR(40),
  paaklaget_enhet               VARCHAR(4),
  behandlende_enhet             VARCHAR(4),
  ytelse_tjeneste               VARCHAR(30),
  tema                          VARCHAR(40),
  utfall                        VARCHAR(200),
  svarmetode                    VARCHAR(30),
  PRIMARY KEY (serviceklage_id)
);
CREATE SEQUENCE serviceklage_serviceklage_id_seq;