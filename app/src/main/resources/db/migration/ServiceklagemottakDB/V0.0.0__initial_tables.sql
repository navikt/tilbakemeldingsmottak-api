CREATE TABLE serviceklage (
  serviceklage_id               NUMBER GENERATED ALWAYS AS IDENTITY,
  dato_opprettet                TIMESTAMP(6) NOT NULL,
  paa_vegne_av                  VARCHAR2(15),
  klagen_gjelder_id             VARCHAR2(15) NOT NULL,
  klagetype                     VARCHAR2(30),
  klagetekst                    CLOB,
  oensker_aa_kontaktes          NUMBER(1)
);