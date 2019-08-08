CREATE TABLE serviceklage (
  serviceklage_id               NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  journalpost_id                NUMBER(11),
  dato_opprettet                TIMESTAMP(6) NOT NULL,
  paa_vegne_av                  VARCHAR2(15),
  klagen_gjelder_id             VARCHAR2(15) NOT NULL,
  klagetype                     VARCHAR2(30),
  klagetekst                    CLOB,
  oensker_aa_kontaktes          NUMBER(1),
  er_serviceklage               VARCHAR2(50),
  gjelder                       VARCHAR2(2000),
  paaklaget_enhet               VARCHAR2(4),
  behandlende_enhet             VARCHAR2(4),
  ytelse_tjeneste               VARCHAR2(30),
  tema                          VARCHAR2(30),
  utfall                        VARCHAR2(100),
  svarmetode                    VARCHAR2(100)
);